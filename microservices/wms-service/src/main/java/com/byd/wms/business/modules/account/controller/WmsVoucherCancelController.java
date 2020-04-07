package com.byd.wms.business.modules.account.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.service.WmsReturnGoodsVoucherCancelService;
import com.byd.wms.business.modules.common.service.WmsCloudPlatformRemote;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;

@RestController
@RequestMapping("account/wmsVoucherCancel")
public class WmsVoucherCancelController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private WmsReturnGoodsVoucherCancelService wmsReturnGoodsVoucherCancelService;
	@Autowired
	private SCMDeliveryService scmDeliveryService;
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	@Autowired
	private WmsCloudPlatformRemote wmsCloudPlatformRemote;
	
	@RequestMapping("/getVoucherCancelData")
	public R getVoucherCancelData(@RequestParam Map<String, Object> params) {
		logger.info("-->getVoucherCancelData");
		String vo_type = params.get("VO_TYPE").toString();	//1:WMS凭证;2:SAP凭证
		String vo_no = params.get("VO_NO").toString();
		params.put("TODAY", DateUtils.format(new Date(),"yyyy-MM-dd"));
		//确认凭证是否存在，如不存在，报错提示：凭证**********不存在。
		List<Map<String, Object>> checkDataList = new ArrayList<Map<String, Object>>();
		if("1".equals(vo_type)) {
			checkDataList = wmsReturnGoodsVoucherCancelService.getWmsDocHeadInfo(params);
		}else {
			checkDataList = wmsReturnGoodsVoucherCancelService.getSapDocHeadInfo(params);
		}
		Map<String,Object> checkMap=checkDataList.size()==0?null:checkDataList.get(0);
		String BUSINESS_TYPE=checkMap==null?"":(String) checkMap.get("BUSINESS_TYPE");
		String BUSINESS_NAME=checkMap==null?"":(String) checkMap.get("BUSINESS_NAME");
		String BUSINESS_CLASS=checkMap==null?"":(String) checkMap.get("BUSINESS_CLASS");
		if("01".equals(BUSINESS_CLASS)) {//收货确认凭证
			/**
			 * SAP交货单收料产生的凭证不允许取消，判断条件 【WMS凭证明细表---WMS_CORE_WMSDOC_ITEM】
			 * --BUSINESS_NAME=03和BUSINESS_TYPE=06;
			 */
			if("03".equals(BUSINESS_NAME)&&"06".equals(BUSINESS_TYPE)) {
				return R.error("SAP交货单收料产生的凭证不允许取消！");
			}		
			
			/**
			 * 查询凭证号和行项目对应的收货单数据（收货单和行号）【WMS_IN_RECEIPT】字段RETURN_QTY和IN_QTY，DESTROY_QTY必须为空或者等于0，
			 * 如果不是，报错提示：该凭证不允许取消！；			
			 */
			Iterator it=checkDataList.iterator();
			while(it.hasNext()) {
				Map<String, Object> m=(Map<String, Object>) it.next();
				if(m.get("RECEIPT_CHECK_QTY")!=null&&Long.valueOf(m.get("RECEIPT_CHECK_QTY").toString())>0) {
					it.remove();
				}
			}			
		}
		
		if(0 == checkDataList.size()) {
			return R.error("凭证编号"+vo_no+"不存在或者不属于工厂“"+params.get("WERKS")+"”仓库号 “"+params.get("WH_NUMBER")+"”，或不存在可取消的行项目数据");
		}
		
		//存在采购订单号的凭证，要查询出 WMS_SAP_PO_ITEM中的UMREN UMREZ
				List<Map<String, Object>> poNOList = new ArrayList<Map<String, Object>>();
				for(Map<String,Object> checkDataMap:checkDataList){
					if(checkDataMap.get("PO_NO")!=null&&checkDataMap.get("PO_ITEM_NO")!=null){
						if(!"".equals(checkDataMap.get("PO_NO").toString())&&!"".equals(checkDataMap.get("PO_ITEM_NO").toString())){
							Map<String, Object> poMap=new HashMap<String, Object>();
							poMap.put("PO_NO", checkDataMap.get("PO_NO").toString());
							poMap.put("PO_ITEM_NO", checkDataMap.get("PO_ITEM_NO").toString());
							poNOList.add(poMap);
						}
					}
				}
				
				if(poNOList!=null&&poNOList.size()>0){
					List<Map<String, Object>> poitemlistRet=wmsInInboundDao.getPOITEMByList(poNOList);
					if(poitemlistRet!=null&&poitemlistRet.size()>0){
						for(Map<String, Object> poitemMap:poitemlistRet){
							for(Map<String, Object> checkItemMap:checkDataList){
								//比较po_no和po_item_no相等的
								if(poitemMap.get("EBELN").equals(checkItemMap.get("PO_NO"))&&
										poitemMap.get("EBELP").equals(checkItemMap.get("PO_ITEM_NO"))){
									checkItemMap.put("UMREN", poitemMap.get("UMREN"));
									checkItemMap.put("UMREZ", poitemMap.get("UMREZ"));
								}
							}
						}
					}
				}
		
		return R.ok().put("result", checkDataList);
	}
	
	@RequestMapping("/confirmVoucherCancelData")
	public R confirmVoucherCancelData(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmVoucherCancelData:BUSINESS_CLASS=" + params.get("BUSINESS_CLASS").toString());
		logger.info("::::::"+params.get("ARRLIST").toString());
		String BUSINESS_CLASS = params.get("BUSINESS_CLASS").toString();
		String result = "";
		try {
			if("04".equals(BUSINESS_CLASS) || "05".equals(BUSINESS_CLASS) || "06".equals(BUSINESS_CLASS)) {	//收料房退货 库房退货 车间退料
				result = wmsReturnGoodsVoucherCancelService.confirmVoucherCancel(params);
				//1.8.4.4针对WMS业务类型为SCM送货单类的凭证，取消时还需同时调取SCM接口更新SCM送货单的可交货数量（可交货数量减少）。
				if("04".equals(BUSINESS_CLASS)) {
					JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
					List<Map<String,Object>> tpoParamsList = new ArrayList<Map<String,Object>>();
					//需获取原业务的BUSINESS_TYPE
					String BUSINESS_TYPE = "";
					Map<String,Object> parMap = new HashMap<String,Object>();
					parMap.put("RETURN_NO", jarr.getJSONObject(0).getString("RETURN_NO"));
					parMap.put("RETURN_ITEM_NO", jarr.getJSONObject(0).getString("RETURN_ITEM_NO"));
					List<Map<String,Object>> b_types = wmsReturnGoodsVoucherCancelService.getCancelBussinessType(parMap);
					if(b_types.size()>0) {
						BUSINESS_TYPE = b_types.get(0).get("BUSINESS_TYPE").toString();
					}
					JSONArray jaa =new JSONArray();
		            JSONObject jsonObject=null;
					for(int i=0;i<jarr.size();i++){
						JSONObject jsonData=  jarr.getJSONObject(i);
						BUSINESS_TYPE = jsonData.getString("BUSINESS_TYPE");
						JSONObject itemData=  jarr.getJSONObject(i);
						if("01".equals(BUSINESS_TYPE)) {
							Map<String, Object> tpo = new HashMap <String, Object>();
							tpo.put("PO_NO", itemData.getString("PO_NO"));
							tpo.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
							tpo.put("DELIVERYAMOUNT", 0-Float.valueOf(itemData.getString("QTY_WMS")));
							tpoParamsList.add(tpo);
						}
						if("20".equals(BUSINESS_TYPE)) {
							jsonObject =  new JSONObject();
					       	jsonObject.put("deliveryNo",jsonData.getString("ASNNO"));
		    	            jsonObject.put("posnr",jsonData.getString("ASNITM"));
		    	            jsonObject.put("flag","1");//进仓标识为2-105过账
		    	            jsonObject.put("qty", Double.valueOf(jsonData.getString("QTY_WMS")));
		    	            jsonObject.put("user",params.get("USERNAME").toString());
		    	            jsonObject.put("date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		    	            jaa.add(jsonObject);
						}
					}
					if(tpoParamsList.size()>0)scmDeliveryService.updateTPO(tpoParamsList);
					if(jaa.size()>0) {
			            Map<String,Object> param =new HashMap<String,Object>();
			            param.put("param", jaa.toString());
		            	Map<String,Object>res=wmsCloudPlatformRemote.sendDeliveryData(param);
			    		if("500".equals(res.get("code").toString())) {
			    			result += "<br>调用云平台接口回写数据失败!";
			    		}
					}
				}
				if("05".equals(BUSINESS_CLASS)) {
					JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
					JSONArray jaa =new JSONArray();
		            JSONObject jsonObject=null;
					for(int i=0;i<jarr.size();i++){
						JSONObject jsonData=  jarr.getJSONObject(i);
						String BUSINESS_TYPE = jsonData.getString("BUSINESS_TYPE");
						if("20".equals(BUSINESS_TYPE)) {
							jsonObject =  new JSONObject();
					       	jsonObject.put("deliveryNo",jsonData.getString("ASNNO"));
		    	            jsonObject.put("posnr",jsonData.getString("ASNITM"));
		    	            jsonObject.put("flag","2");//进仓标识为2-105过账
		    	            jsonObject.put("qty", Double.valueOf(jsonData.getString("QTY_WMS")));
		    	            jsonObject.put("user",params.get("USERNAME").toString());
		    	            jsonObject.put("date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		    	            jaa.add(jsonObject);
						}
					}
					if(jaa.size()>0) {
			            Map<String,Object> param =new HashMap<String,Object>();
			            param.put("param", jaa.toString());
		            	Map<String,Object>res=wmsCloudPlatformRemote.sendDeliveryData(param);
			    		if("500".equals(res.get("code").toString())) {
			    			result += "<br>调用云平台接口回写数据失败!";
			    		}
					}
				}
				
			}else if("02".equals(BUSINESS_CLASS) || "03".equals(BUSINESS_CLASS)) {	//进仓交接凭证取消
				result = wmsReturnGoodsVoucherCancelService.confirmVoucherCancelInHandOver(params);
				/**
	    		 * 过账后回写云平台数据
	    		 */
	    		JSONArray jaa =new JSONArray();
	            JSONObject jsonObject=null;
				JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
				for(int i=0;i<jarr.size();i++){
					JSONObject jsonData=  jarr.getJSONObject(i);
					String BUSINESS_TYPE = jsonData.getString("BUSINESS_TYPE");
					if("20".equals(BUSINESS_TYPE)) {
						jsonObject =  new JSONObject();
				       	jsonObject.put("deliveryNo",jsonData.getString("ASNNO"));
	    	            jsonObject.put("posnr",jsonData.getString("ASNITM"));
	    	            jsonObject.put("flag","2");//进仓标识为2-105过账
	    	            jsonObject.put("qty", 0-Double.valueOf(jsonData.getString("QTY_WMS")));
	    	            jsonObject.put("user",params.get("USERNAME").toString());
	    	            jsonObject.put("date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    	            
	    	            jaa.add(jsonObject);
					}
					
				}
				if(jaa.size()>0) {
		            Map<String,Object> param =new HashMap<String,Object>();
		            param.put("param", jaa.toString());
	            	Map<String,Object>res=wmsCloudPlatformRemote.sendDeliveryData(param);
		    		if("500".equals(res.get("code").toString())) {
		    			result += "<br>调用云平台接口回写数据失败!";
		    		}
				}
				
			}else if("01".equals(BUSINESS_CLASS)) {		//收货凭证取消
				result = wmsReturnGoodsVoucherCancelService.confirmVoucherCancelInReceipt(params);
		  		
				/**
	    		 * 过账后回写云平台数据
	    		 */
	    		JSONArray jaa =new JSONArray();
	            JSONObject jsonObject=null;
				JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
				for(int i=0;i<jarr.size();i++){
					JSONObject jsonData=  jarr.getJSONObject(i);
					String BUSINESS_TYPE = jsonData.getString("BUSINESS_TYPE");
					if("20".equals(BUSINESS_TYPE)) {
						jsonObject =  new JSONObject();
				       	jsonObject.put("deliveryNo",jsonData.getString("ASNNO"));
	    	            jsonObject.put("posnr",jsonData.getString("ASNITM"));
	    	            jsonObject.put("flag","1");//进仓标识为2-103过账
	    	            jsonObject.put("qty", 0-Double.valueOf(jsonData.getString("QTY_WMS")));
	    	            jsonObject.put("user",params.get("USERNAME").toString());
	    	            jsonObject.put("date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    	            
	    	            jaa.add(jsonObject);
					}
					
				}
				if(jaa.size()>0) {
		            Map<String,Object> param =new HashMap<String,Object>();
		            param.put("param", jaa.toString());
	            	Map<String,Object>res=wmsCloudPlatformRemote.sendDeliveryData(param);
		    		if("500".equals(res.get("code").toString())) {
		    			result += "<br>调用云平台接口回写数据失败!";
		    		}
				}
			}else if("08".equals(BUSINESS_CLASS)) {
				//收货V凭证取消
				result = wmsReturnGoodsVoucherCancelService.confirmVoucherCancelInReceiptV(params);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
		
		return R.ok().put("result", result);
	}
	
	
}
