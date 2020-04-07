package com.byd.wms.business.modules.in.controller;
/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月18日 下午2:27:10 
 * 类说明 
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.common.service.WmsCloudPlatformRemote;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.service.WmsInHandoverService;



/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年9月29日 上午9:42:43 
 * 类说明  进仓交接
 */
@RestController
@RequestMapping("in/wmsinhandoverbound")
public class WmsInHandoverController {
	@Autowired
	private WmsInHandoverService wmsInHandoverService;
	@Autowired
    private WmsCloudPlatformRemote wmsCloudPlatformRemote;
	@Autowired
    WmsCWhService wmsCWhService;
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	
	@Autowired
    private RedisUtils redisUtils;
	
	@CrossOrigin
	@RequestMapping("/list")
	public R list(@RequestBody Map<String, Object> params){
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	params.put("WERKS", params.get("WERKS"));
    	params.put("WH_NUMBER", params.get("WH_NUMBER"));
    	params.put("INBOUND_NO", params.get("INBOUND_NO"));
    	params.put("WH_MANAGER", params.get("WH_MANAGER"));
    	params.put("LIFNR", params.get("LIFNR"));
    	params.put("MATNR", params.get("MATNR"));
    	params.put("BATCH", params.get("BATCH"));
    	
    	Map<String,Object> retMap=new HashMap<String,Object>();
    	if(params.get("INBOUND_NO")!=null&&(!"".equals(params.get("INBOUND_NO").toString()))){
    		List<Map<String, Object>> resultHeadList =wmsInHandoverService.getInBoundHead(params);
    		if(resultHeadList.size()==0){
    			retMap.put("retMsg", "单号  "+params.get("INBOUND_NO")+" 查不到对应数据，请检查是否输入有误!");
    		}
    	}
    	
    	try {
			result=wmsInHandoverService.getInBoundItem(params);
			//20190709 改为从创建外购进仓单的时候就获取了311的发货库位了
			/*BUSINESS_NAME=01和BUSINESS_TYPE=01，BUSINESS_CLASS=01或者BUSINESS_NAME=02和BUSINESS_TYPE=02，BUSINESS_CLASS=01
			在WMS_CORE_WMSDOC_ITEM中满足以上条件的 查询参考凭证和参考凭证行项目  20190703 已经取消该逻辑，在创建进仓单的时候就已经保存了参考凭证和行项目*/
			/*for(int i=0;i<result.size();i++){
				
				if("10".equals(result.get(i).get("BUSINESS_NAME"))&&"06".equals(result.get(i).get("BUSINESS_TYPE"))){
					
					//从送货单获取源库位,并传给过账接口
					Map<String, Object> receiptMap = new HashMap <String, Object>();
					if(result.get(i).get("RECEIPT_NO")!=null&&result.get(i).get("RECEIPT_ITEM_NO")!=null){
						receiptMap.put("RECEIPT_NO", result.get(i).get("RECEIPT_NO"));
						receiptMap.put("RECEIPT_ITEM_NO", result.get(i).get("RECEIPT_ITEM_NO"));
						List<Map<String, Object>> retReceiptList=wmsInInboundService.getReceiptInfoByReceiveNo(receiptMap);
						if(retReceiptList!=null&&retReceiptList.size()>0){
							result.get(i).put("LGORT_311", retReceiptList.get(0).get("LGORT"));
							//用户把收货单的 工厂，仓库号 库位  保存为事务记录表的源工厂 源仓库号 源库位
							result.get(i).put("WERKS_311", retReceiptList.get(0).get("WERKS"));
							result.get(i).put("WH_NUMBER_311", retReceiptList.get(0).get("WH_NUMBER"));
						}
					}
					
					
					
				}
				
				
			}*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
    	
    	Map<String, Object> tempMap=new HashMap<String, Object>();
    	tempMap.put("WERKS", params.get("WERKS"));
    	tempMap.put("WH_NUMBER", params.get("WH_NUMBER"));
    	List<WmsCWhEntity> retCWh=wmsCWhService.selectByMap(tempMap);
    	String barcode_flag="";
    	if(retCWh!=null&&retCWh.size()>0){
    		barcode_flag=retCWh.get(0).getBarcodeFlag();
    	}
    	
        return R.ok().put("result", result).put("retMsg", retMap).put("BARCODE_FLAG", barcode_flag);
    }
	/**
	 * 进仓交接
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@CrossOrigin
	@RequestMapping("/save")
	public R handover(@RequestBody Map<String, Object> params)  {
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
			//retMap=wmsInHandoverService.handover(params);
		
			//更新头表状态
			//wmsInHandoverService.updateHeadStatus(params);
			
			//如果是303,Z23出库产生的标签，就保存进仓单 20190813 add start
			List<Map<String, Object>> retlabelist=wmsInHandoverService.getLabelInfoBy303Z23(params);
			if(retlabelist!=null&&retlabelist.size()>0){
				for(Map<String, Object> temp:retlabelist){
					temp.put("WH_NUMBER", params.get("WH_NUMBER"));//将WH_NUMBER重新赋值为前台传入的值
				}
				//保存进仓单
				Map<String, Object> retParams=getInternetbountBatch(retlabelist);
				
				params.put("retmapList", retParams.get("retmapList"));
				params.put("inbounditemlist", retParams.get("inbounditemlist"));
				
			}
			//20190813 add end
			
			//加锁
			boolean keyFlag = true;
			List<String> keylist = new ArrayList<String>();
	    	String uid = UUID.randomUUID().toString();
	    	StringBuffer inberror = new StringBuffer();
			JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
			for(int i=0;i<jarr.size();i++){
				JSONObject jsonData=  jarr.getJSONObject(i);
				String inboundNo = jsonData.getString("INBOUND_NO");
				if(keylist.contains(inboundNo))
					continue;
				
	    		if (redisUtils.tryLock(inboundNo, uid))
	    		{
	    			keylist.add(inboundNo);
	    		} else {
	    			keyFlag = false;
	    			inberror.append(inboundNo);
	    			break;
	    		}
				
			}
			
			if (keyFlag) {
				params.put("UID", uid);
				params.put("keylist", keylist);
				retMap=wmsInHandoverService.handover_new(params);
			}
			
			for(String keys : keylist) {
				redisUtils.unlock(keys, uid); //解锁
			}
			
			if (!keyFlag) 
				return R.error(inberror.toString()+",正在执行中，请稍后再试！");
			
			//更新头表状态 已经放到handover_new 执行
			//wmsInHandoverService.updateHeadStatus_new(params);
			
			//BUSINESS_TYPE为20-云平台送货单，需更新云平台信息
	  		/**
    		 * 过账后回写云平台数据
    		 */
    		JSONArray jaa =new JSONArray();
            JSONObject jsonObject=null;
//			JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
			for(int i=0;i<jarr.size();i++){
				JSONObject jsonData=  jarr.getJSONObject(i);
				String BUSINESS_TYPE = jsonData.getString("BUSINESS_TYPE");
				if("20".equals(BUSINESS_TYPE)) {
					jsonObject =  new JSONObject();
			       	jsonObject.put("deliveryNo",jsonData.getString("ASNNO"));
    	            jsonObject.put("posnr",jsonData.getString("ASNITM"));
    	            jsonObject.put("flag","2");//进仓标识为2-105过账
    	            jsonObject.put("qty",jsonData.getString("MAY_IN_QTY"));
    	            jsonObject.put("user",params.get("USERNAME").toString());
    	            jsonObject.put("date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	            
    	            jaa.add(jsonObject);
				}
				
			}
			String cloudMsg = "";
			if(jaa.size()>0) {
	            Map<String,Object> param =new HashMap<String,Object>();
	            param.put("param", jaa.toString());
	            if(!"500".equals(retMap.get("code").toString())) {
	            	Map<String,Object>res=wmsCloudPlatformRemote.sendDeliveryData(param);
		    		if("500".equals(res.get("code").toString())) {
		    			cloudMsg = "<br>调用云平台接口回写数据失败!";
		    		}
	            }
			}
			
			return R.ok().put("msg", retMap.get("msg")+cloudMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}	
		
    	
    }
	
	@CrossOrigin
	@RequestMapping("/labellist")
	public R labelList(@RequestBody Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			result=wmsInHandoverService.getLabelList(params);//查询单个标签
			if(result!=null&&result.size()>0){
				
				Map<String, Object> tempMap=new HashMap<String, Object>();
		    	tempMap.put("WERKS", params.get("WERKS"));
		    	tempMap.put("WH_NUMBER", params.get("WH_NUMBER"));
		    	List<WmsCWhEntity> retCWh=wmsCWhService.selectByMap(tempMap);//判断是否启用条码
		    	String barcode_flag="";//是否启用条码管理  X 启用 默认0
		    	if(retCWh!=null&&retCWh.size()>0){
		    		barcode_flag=retCWh.get(0).getBarcodeFlag();
		    	}
		    	
				for(Map<String, Object> res:result){
					String labelStatus="";//标签状态
					String labelWerks="";//标签工厂
					if(res.get("LABEL_STATUS")!=null){
						labelStatus=res.get("LABEL_STATUS").toString();
						if("07".equals(labelStatus)){//
							throw new RuntimeException("已进仓 状态不允许进仓！");
						}else if("20".equals(labelStatus)){
							throw new RuntimeException("已关闭 状态不允许进仓！");
						}
						//
						if("X".equals(barcode_flag)){//启用条码
							if("02".equals(labelStatus)||"03".equals(labelStatus)){
								
							}else{
								throw new RuntimeException("不是已收料或者待进仓不允许进仓！");
							}
						}else if("0".equals(barcode_flag)){//不启用条码
							if("00".equals(labelStatus)||"01".equals(labelStatus)||"03".equals(labelStatus)){
								
							}else{
								throw new RuntimeException("不是创建或者已收料不允许进仓！");
							}
						}

						/*if(!("02".equals(labelStatus)) && !("03".equals(labelStatus))) {
							throw new RuntimeException("状态不允许进仓！");
						}*/

					}
					
				}
				
				/*BUSINESS_NAME=01和BUSINESS_TYPE=01，BUSINESS_CLASS=01或者BUSINESS_NAME=02和BUSINESS_TYPE=02，BUSINESS_CLASS=01
				在WMS_CORE_WMSDOC_ITEM中满足以上条件的 查询参考凭证和参考凭证行项目*/
				for(int i=0;i<result.size();i++){
					if(result.get(i).get("BUSINESS_NAME")!=null&&result.get(i).get("BUSINESS_TYPE")!=null){
						if(("01".equals(result.get(i).get("BUSINESS_NAME"))&&"01".equals(result.get(i).get("BUSINESS_TYPE")))||
						   ("01".equals(result.get(i).get("BUSINESS_NAME"))&&"02".equals(result.get(i).get("BUSINESS_TYPE")))){
						Map<String,Object> matDocMap=new HashMap<String,Object>();
						matDocMap.put("BUSINESS_NAME", result.get(i).get("BUSINESS_NAME"));//查询凭证表的条件
						matDocMap.put("BUSINESS_TYPE", result.get(i).get("BUSINESS_TYPE"));
						matDocMap.put("RECEIPT_NO", result.get(i).get("RECEIPT_NO"));
						matDocMap.put("RECEIPT_ITEM_NO", result.get(i).get("RECEIPT_ITEM_NO"));
						//查询参考凭证
						List<Map<String, Object>> retMatDocList=wmsInHandoverService.getWMSDOCMatDoc(matDocMap);
						if(retMatDocList!=null&&retMatDocList.size()>0){
							if(retMatDocList.get(0).get("WMS_SAP_MAT_DOC")!=null){
								String sap_mat_doc=retMatDocList.get(0).get("WMS_SAP_MAT_DOC").toString();
								String[] sapMatDocArrayMain=sap_mat_doc.split(";");//4100071036:0001;
								for(int m=0;m<sapMatDocArrayMain.length;m++){
									for(int n=0;n<sapMatDocArrayMain[m].length();n++){
										String[] sapMatDocArrayItem=sapMatDocArrayMain[m].split(":");
										String REF_SAP_MATDOC_NO=sapMatDocArrayItem[0];
										String REF_SAP_MATDOC_ITEM_NO=sapMatDocArrayItem[1];
										result.get(i).put("REF_SAP_MATDOC_NO", REF_SAP_MATDOC_NO);
										result.get(i).put("REF_SAP_MATDOC_ITEM_NO", REF_SAP_MATDOC_ITEM_NO);
									}
								}
							}
							
							//查询凭证年份
							
							if(retMatDocList.get(0).get("WMS_NO")!=null){//取出wms凭证号
								Map<String,Object> matDocYearMap=new HashMap<String,Object>();
								matDocYearMap.put("WMS_NO", retMatDocList.get(0).get("WMS_NO"));
								List<Map<String, Object>> docYearList=wmsInHandoverService.getSAPDOCDocDate(matDocYearMap);
								if(docYearList!=null&&docYearList.size()>0){
									result.get(i).put("REF_SAP_MATDOC_YEAR", docYearList.get(0).get("DOC_YEAR"));
								}
							}
						}
						}
					}
				}
				
			}else{
				throw new RuntimeException("标签号 "+params.get("LABEL_NO")+" 不存在！");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
        return R.ok().put("result", result);
	}
	
	@CrossOrigin
	@RequestMapping("/ValidlabelQyt")
	public R ValidlabelQyt(@RequestBody Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try{
			wmsInHandoverService.validLabelQty(params);
		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok().put("result", result);
	}
	
	@CrossOrigin
	@RequestMapping("/ValidlabelHandover")
	public R ValidlabelHandover(@RequestBody Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try{
			wmsInHandoverService.labelHandoverValid(params);
		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok().put("result", result);
	}
	
	@CrossOrigin
	@RequestMapping("/getLabelInfo")
	public R getLabelInfo(@RequestBody Map<String, Object> params) {
		List<Map<String,Object>> label = wmsInHandoverService.getLabelInfo(params);
		return R.ok().put("data", label);
	}
	
	@CrossOrigin
	@RequestMapping("/inPoCptConsumelist")
	public R inPoCptConsumelist(@RequestBody Map<String, Object> params){
		List<Map<String, Object>> cptList = new ArrayList<Map<String, Object>>();
		cptList.add(params);
		List<Map<String, Object>> result=wmsInHandoverService.getInpoCptConsume(cptList);
		return R.ok().put("result", result);
		
	}
	/**
	 * 303,Z23出库产生的标签，扫描进仓需要保存到进仓单
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/saveinbound303")
	public R saveinbound303(@RequestBody Map<String, Object> params){
		//如果是303,Z23出库产生的标签，就保存进仓单
		List<Map<String, Object>> retlabelist=wmsInHandoverService.getLabelInfoBy303Z23(params);
		if(retlabelist!=null&&retlabelist.size()>0){
			for(Map<String, Object> temp:retlabelist){
				temp.put("WH_NUMBER", params.get("WH_NUMBER"));//将WH_NUMBER重新赋值为前台传入的值
			}
			//保存进仓单
			Map<String, Object> retParams=getInternetbountBatch(retlabelist);
			wmsInHandoverService.saveinbound303(retParams);
		}
		return R.ok();
	}
	
	private Map<String, Object> getInternetbountBatch(List<Map<String, Object>> paramslist)  {
		Map<String, Object> params=new HashMap<String, Object>();
		//进仓单行项目清单
    	List<Map<String, Object>> inbounditemlist=new ArrayList<Map<String, Object>>();
		UserUtils us = new UserUtils();
		String username =  us.getTokenUsername();
		for(int i=0;i<paramslist.size();i++){

    		Map<String, Object> tempItemmap=new HashMap();
			tempItemmap.put("DEL", "0");
			tempItemmap.put("WERKS", paramslist.get(i).get("WERKS"));
			tempItemmap.put("WH_NUMBER", paramslist.get(i).get("WH_NUMBER"));
			tempItemmap.put("LGORT", paramslist.get(i).get("LGORT"));//前台的值
			tempItemmap.put("MATNR", paramslist.get(i).get("MATNR"));
			tempItemmap.put("MAKTX", paramslist.get(i).get("MAKTX"));
			tempItemmap.put("BIN_CODE", paramslist.get(i).get("BIN_CODE"));
			tempItemmap.put("UNIT", paramslist.get(i).get("UNIT"));
			tempItemmap.put("IN_QTY", paramslist.get(i).get("IN_QTY"));//前台的值
			tempItemmap.put("FULL_BOX_QTY", paramslist.get(i).get("FULL_BOX_QTY"));
			tempItemmap.put("BOX_COUNT", "");
			tempItemmap.put("WH_MANAGER", "");
			tempItemmap.put("LIFNR", paramslist.get(i).get("LIFNR"));
			tempItemmap.put("LIKTX", paramslist.get(i).get("LIKTX"));
			
			
			tempItemmap.put("SAP_MATDOC_NO", paramslist.get(i).get("SAP_MATDOC_NO"));
			tempItemmap.put("SAP_MATDOC_ITEM_NO", paramslist.get(i).get("SAP_MATDOC_ITEM_NO"));
			
			tempItemmap.put("F_WERKS", paramslist.get(i).get("MOVE_PLANT"));
			tempItemmap.put("F_WH_NUMBER", paramslist.get(i).get("F_WH_NUMBER"));
			tempItemmap.put("F_LGORT", paramslist.get(i).get("MOVE_STLOC"));
			tempItemmap.put("F_BATCH", paramslist.get(i).get("MOVE_BATCH"));
			
			tempItemmap.put("WMS_MOVE_TYPE", paramslist.get(i).get("WMS_MOVE_TYPE"));
			
			tempItemmap.put("CREATOR", username);
			tempItemmap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			tempItemmap.put("LABEL_NO", paramslist.get(i).get("LABEL_NO"));
			
			tempItemmap.put("DANGER_FLAG", "0");
			tempItemmap.put("RECEIPT_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
			tempItemmap.put("PRODUCT_DATE", paramslist.get(i).get("PRODUCT_DATE")==null?"":paramslist.get(i).get("PRODUCT_DATE"));
			
			//
			Map<String, Object> businessmap = new HashMap<>();
			if("303".equals(paramslist.get(i).get("WMS_MOVE_TYPE"))){//对应进仓交接是 305的21
				businessmap.put("BUSINESS_NAME", "21");
			}
			if("Z23".equals(paramslist.get(i).get("WMS_MOVE_TYPE"))){//对应进仓交接是 Z25 的79
				businessmap.put("BUSINESS_NAME", "79");
			}
			String SOBKZ=paramslist.get(i).get("SOBKZ")==null?"Z":paramslist.get(i).get("SOBKZ").toString();//库存类型
			businessmap.put("SOBKZ", SOBKZ);
			List<Map<String, Object>> businesslist=wmsInInboundDao.getBusinessInfo(businessmap);
			
			if(businesslist.size()>0){
				
			  tempItemmap.put("BUSINESS_NAME", businesslist.get(0).get("BUSINESS_NAME").toString());
				
			}
			inbounditemlist.add(tempItemmap);
		}
		//产生批次
		
		List<Map<String,Object>> retmapList = wmsMatBatchService.getBatch(inbounditemlist);
		for(Map<String,Object> retmap:retmapList){
			if(retmap.get("MSG")!=null&&!"success".equals(retmap.get("MSG"))) {
					throw new RuntimeException((String) retmap.get("MSG"));
				}
			}
				
		params.put("retmapList", retmapList);
		params.put("inbounditemlist", inbounditemlist);
		return params;
	}
	
}
