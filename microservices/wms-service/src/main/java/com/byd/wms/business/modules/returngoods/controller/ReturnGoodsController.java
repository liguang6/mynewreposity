package com.byd.wms.business.modules.returngoods.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsCloudPlatformRemote;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;
import com.byd.wms.business.modules.returngoods.service.WmsReturnGoodsService;

@RestController
@RequestMapping("returngoods")
public class ReturnGoodsController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private WmsReturnGoodsService wmsReturnGoodsService;
	@Autowired
	private SCMDeliveryService scmDeliveryService;
	@Autowired
	WmsSapRemote wmsSapRemote;
    @Autowired
    WmsCTxtService wmsCTxtService;
    @Autowired
    private WmsCloudPlatformRemote wmsCloudPlatformRemote;
    @Autowired
    private UserUtils userUtils;

	@RequestMapping("/getBusinessNameListByWerks")
    public R getBusinessNameListByWerks(@RequestParam Map<String, Object> params){
		List<String> result = wmsReturnGoodsService.getBusinessNameListByWerks(params);		
		return R.ok().put("result", result);
	}
	
	/**
	 * PDA 外购124退货 扫描条码获取相关信息
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getReturnInfoByBarcode")
	public R getReturnInfoByBarcode(@RequestBody Map<String, Object> params) {
		List<Map<String, Object>> result = wmsReturnGoodsService.getReturnInfoByBarcode(params);
		return R.ok().put("result", result);
	}
	
	@CrossOrigin
	@RequestMapping("/getSapPoInfo")
	public R getSapPoInfo(@RequestBody Map<String, Object> params) {
		if("".equals(params.get("PO_NO"))) {
			return R.error("生产订单不能为空！");
		}
		List<Map<String, Object>> result = wmsReturnGoodsService.getSapPoInfo(params);
		return R.ok().put("result", result);
	}
	
	@CrossOrigin
	@RequestMapping("/getSapPoBarcodeInfo")
	public R getSapPoBarcodeInfo(@RequestBody Map<String, Object> params) {
		if("".equals(params.get("PO_NO"))) {
			return R.error("生产订单不能为空！");
		}
		if("".equals(params.get("Barcode"))) {
			return R.error("条码不能为空！");
		}
		List<Map<String, Object>> result = wmsReturnGoodsService.getSapPoBarcodeInfo(params);
		return R.ok().put("result", result);
	}
	
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping("/getReceiveRoomOutData")
    public R getReceiveRoomOutData(@RequestParam Map<String, Object> params) {
		logger.info("-->getReceiveRoomOutData BUSINESS_CODE = "+ params.get("BUSINESS_CODE"));
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if("25".equals(params.get("BUSINESS_CODE"))) {				//BUSINESS_CODE:25 外购退货
			params.put("BUSINESSCODE", "25");
			String MATNR = params.get("MATNR").toString();
			params.put("MATNR", "('" + MATNR + "')");
			result = wmsReturnGoodsService.getReceiveRoomOutData(params);			
		}else if("26".equals(params.get("BUSINESS_CODE"))) {		//BUSINESS_CODE:26 调拨退货
			params.put("BUSINESS_NAME", "303调拨单退货");
			params.put("BUSINESSCODE", "26");
			String MATNR = params.get("MATNR").toString();
			params.put("MATNR", "('" + MATNR + "')");
			result = wmsReturnGoodsService.getReceiveRoomOutData(params);
			
		}else if("27".equals(params.get("BUSINESS_CODE"))) {		//BUSINESS_CODE:27 STO退货  84077942  84077970
			Map<String, Object> sto_result = new HashMap<String, Object>();
			String deliveryNO = params.get("deliveryNO").toString();
			try {
				sto_result = wmsSapRemote.getSapBapiDeliveryGetlist(deliveryNO);
			}catch(Exception e){
				return R.error("调用SAP接口查询失败！");
			}
			if(null == (Map<String,Object>)sto_result.get("header")) {
				return R.error("SAP交货单"+deliveryNO+"数据查询失败。");
			}
			
			if(!params.get("WERKS").equals(((Map<String,Object>)sto_result.get("header")).get("WERKS"))) {
				return R.error("SAP交货单"+deliveryNO+"不是工厂"+params.get("WERKS")+"的交货单。");
			}
			if(!"T".equals(((Map<String,Object>)sto_result.get("header")).get("VBTYP"))) {
				return R.error("SAP交货单"+deliveryNO+" VBTYP:"+((Map<String,Object>)sto_result.get("header")).get("VBTYP")+",不是正确的退货交货单(T)。");
			}
			if(!"A".equals(((Map<String,Object>)sto_result.get("header")).get("WBSTK"))) {
				return R.error("SAP交货单"+deliveryNO+" WBSTK:"+((Map<String,Object>)sto_result.get("header")).get("WBSTK")+",不是正确的退货交货单(A)。");
			}
			
			List<Map<String,Object>> sto_itemList = (List<Map<String, Object>>)sto_result.get("item");
			//判断STO 交货单号是否已经创建退货单，不允许重复创建
			String VBELN = removeZeroStr(sto_itemList.get(0).get("VBELN").toString());
			if(wmsReturnGoodsService.getWmsOutReturnSapOutCount(VBELN)>0) {
				return R.error("SAP交货单"+deliveryNO+"已经创建退货单，不允许重复创建。");
			}
			
			//如果【料号+库存类型+供应商代码】存在相同的数据，提示数据有误，需合并数据后重新创建
			String stocheck = "";
			for(int j=0;j<sto_itemList.size();j++) {
				
				if(stocheck.indexOf(sto_itemList.get(j).get("WERKS").toString() + 
					sto_itemList.get(j).get("MATNR").toString() + 
					sto_itemList.get(j).get("CHARG").toString())>=0) {
					return R.error("SAP交货单"+deliveryNO+"有可合并的行项目，请先合并后再创建退货单。");
				}
				stocheck += sto_itemList.get(j).get("WERKS").toString() + 
				sto_itemList.get(j).get("MATNR").toString() + 
				sto_itemList.get(j).get("CHARG").toString();
			}
			
			//根据SAP交货单返回的料号 及供应商代码 作为条件 查询【收货单表WMS_IN_RECEIPT】
			String LIFNR = "VBYD" + sto_itemList.get(0).get("WERKS").toString();
			params.put("LIFNR", LIFNR);
			String MATNR = params.get("MATNR").toString();
			if("".equals(MATNR)) {
				MATNR = "(";
				for(int j=0;j<sto_itemList.size();j++) {
					MATNR += "'" + sto_itemList.get(j).get("MATNR").toString() + "',";
				}
				MATNR = MATNR.substring(0, MATNR.length()-1) + ")";
			}else {
				MATNR = "('" + MATNR + "')";
			}
			logger.info("---->mat = " + MATNR);
			params.put("MATNR", MATNR);
			params.put("BUSINESSCODE", "27");
			List<Map<String, Object>> result2 = new ArrayList<Map<String, Object>>();
			result2 = wmsReturnGoodsService.getReceiveRoomOutData(params);
						
			//在确认SAP交货单是该工厂下有效交货单的情况下，取SAP交货单的“料号+库存类型+供应商代码”+用户选择的仓库号到【WMS收货单】表中匹配符合条件的数据。
			String stoStr = "";
			int reCount = 0;
			for(int i=0;i<sto_itemList.size();i++) {
				reCount++;
				//供应商：VBYDC161 物料：10084958-00 批次：B0001 数量：500 <span style='color:blue'>已勾选退货数量： </span>
				//<span id='sp1' class='sp1' style='color:blue'>0<span>
				stoStr = (i+1) +" 供应商VBYD" + sto_itemList.get(i).get("WERKS") + " 物料" + sto_itemList.get(i).get("MATNR") +
						" 批次" + sto_itemList.get(i).get("CHARG") + " SAP交货单数量" + sto_itemList.get(i).get("LFIMG") + 
						" <span style='color:blue'>已选数量： </span><span id='sp"+reCount+"' class='sp"+reCount+"' style='color:blue'>0<span>";
				logger.info("---->stoStr "+i+" = " + stoStr);
				for(int j=0;j<result2.size();j++) {
					String CHARG = "";	//STO原批次
					if (sto_itemList.get(i).get("CHARG")!= null) {
						CHARG = sto_itemList.get(i).get("CHARG").toString();
					}
					if(result2.get(j).get("LIFNR").toString().equals("VBYD" + sto_itemList.get(i).get("WERKS").toString()) && 
					   result2.get(j).get("MATNR").toString().equals(sto_itemList.get(i).get("MATNR").toString())
					  ) {
						Map<String, Object> reMap = new HashMap<String, Object>();
						if("".equals(CHARG)) {
							reMap = result2.get(j);
							reMap.put("stoStr", stoStr);
							reMap.put("g_id", reCount);
							reMap.put("stonum", sto_itemList.get(i).get("LFIMG"));
							reMap.put("SAP_OUT_NO", sto_itemList.get(i).get("VBELN"));
							reMap.put("SAP_OUT_ITEM_NO", sto_itemList.get(i).get("POSNR"));
							result.add(reMap);
						}else {
							if(result2.get(j).get("BATCH") == null) {
								return R.error("收料房库存数据有误，请检查数据。");
							}
							if(CHARG.equals(result2.get(j).get("BATCH").toString())) {
								reMap = result2.get(j);
								reMap.put("stoStr", stoStr);
								reMap.put("g_id", reCount);
								reMap.put("stonum", sto_itemList.get(i).get("LFIMG"));
								reMap.put("SAP_OUT_NO", sto_itemList.get(i).get("VBELN"));
								reMap.put("SAP_OUT_ITEM_NO", sto_itemList.get(i).get("POSNR"));
								result.add(reMap);
							}
						}
					}
				}
				
			}
			
		}
		for(int i=0;i<result.size();i++) {
			if(result.get(i).get("WMS_SAP_MAT_DOC") != null) {
				result.get(i).put("REF_SAP_MATDOC_NO",result.get(i).get("WMS_SAP_MAT_DOC").toString().substring(0, result.get(i).get("WMS_SAP_MAT_DOC").toString().indexOf(":")));
				result.get(i).put("REF_SAP_MATDOC_ITEM_NO",result.get(i).get("WMS_SAP_MAT_DOC").toString().substring(result.get(i).get("WMS_SAP_MAT_DOC").toString().indexOf(":")+1, result.get(i).get("WMS_SAP_MAT_DOC").toString().indexOf(";")));
			}else {
				result.get(i).put("REF_SAP_MATDOC_NO","");
				result.get(i).put("REF_SAP_MATDOC_ITEM_NO","");
				result.get(i).put("REF_SAP_MATDOC_YEAR","");
			}
		}
		
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getWareHouseOutPickupData")
	public R getWareHouseOutPickupData(@RequestParam Map<String, Object> params) {
		logger.info("-->getWareHouseOutPickupData");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = wmsReturnGoodsService.getWareHouseOutPickupData(params);	
		
		return R.ok().put("result", result);
	}
	
	@CrossOrigin
	@RequestMapping("/createReceiveRoomOutReturn")
	public R createReceiveRoomOutReturn(@RequestParam Map<String, Object> params) {
		String outNo = wmsReturnGoodsService.createReceiveRoomOutReturn(params);
		if("-1".equals(outNo)) {
			return R.error("退货单号生成失败！");
		} else if("-2".equals(outNo)) {
			return R.error("退货单单据编号规则未配置，退货单号生成失败!");
		}else {
			return R.ok().put("outNo",outNo);
		}
	}
	@CrossOrigin
	@RequestMapping("/createReceiveRoomOutReturnPda")
	public R createReceiveRoomOutReturnPda(@RequestBody Map<String, Object> params) {
		String outNo = wmsReturnGoodsService.createReceiveRoomOutReturn(params);
		if("-1".equals(outNo)) {
			return R.error("退货单号生成失败！");
		} else if("-2".equals(outNo)) {
			return R.error("退货单单据编号规则未配置，退货单号生成失败!");
		}else {
			return R.ok().put("outNo",outNo);
		}
	}
	
	@RequestMapping("/getWareHouseOutReturnData")
	public R getWareHouseOutReturnData(@RequestParam Map<String, Object> params) {
		logger.info("-->getWareHouseOutReturnData");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = wmsReturnGoodsService.getWareHouseOutReturnData(params);	
		if(result.size() == 0) {
			return R.error("无此退货单信息！");
		}
		if("X".equals(result.get(0).get("DEL"))) {
			return R.error("退货单号："+params.get("RETURN_NO")+"已删除！");
		}
		if("03".equals(result.get(0).get("RETURN_STATUS"))) {
			return R.error("退货单号："+params.get("RETURN_NO")+"已退货！");
		}
		/**
    	 * 根据工厂代码、业务类型获取SAP交货单收料(V)("23")抬头文本与行文本
    	 */
    	Map<String,String>txtMap=new HashMap<String,String>();
    	//SysUserEntity currentUser = ShiroUtils.getUserEntity();
    	txtMap.put("WERKS", params.get("WERKS").toString());
    	txtMap.put("BUSINESS_NAME",result.get(0).get("BUSINESS_NAME").toString());
    	txtMap.put("JZ_DATE", params.get("JZ_DATE").toString());
    	txtMap.put("RETURN_NO", params.get("RETURN_NO").toString());
    	txtMap.put("VENDOR_MANAGER", (result.get(0).get("VENDOR_MANAGER") == null)?"":result.get(0).get("VENDOR_MANAGER").toString());
    	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
    	for(Map<String,Object> _m:result) {
    		_m.put("HEADER_TXT", txt.get("txtrule"));
    		_m.put("ITEM_TEXT", txt.get("txtruleitem"));
    	}
    	
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getReceiveRoomOutReturnData")
	public R getReceiveRoomOutReturnData(@RequestParam Map<String, Object> params) {
		logger.info("-->getReceiveRoomOutReturnData");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = wmsReturnGoodsService.getReceiveRoomOutReturnData(params);
		//5.3.3.4如单据不存在，则系统返回消息：无此退货单信息。
		if(result.size() == 0) {
			return R.error("无此退货单信息！");
		}
		//5.3.3.3如单据“删除标示”=X，则系统返回消息：该退货单号：***************已删除。
		if("X".equals(result.get(0).get("DEL"))) {
			return R.error("退货单号："+params.get("RETURN_NO")+"已删除！");
		}
		//5.3.3.2如单据状态为“已完成”，则系统返回消息：退货单号：***************已退货。
		/*if("03".equals(result.get(0).get("RETURN_STATUS"))) {
			return R.error("退货单号："+params.get("RETURN_NO")+"已退货！");
		}*/
		for(int i=0;i<result.size();i++) {
			if("03".equals(result.get(i).get("ITEM_STATUS"))) {
				return R.error("退货单号："+params.get("RETURN_NO")+"已退货！");
			}
		}
		
		/**
    	 * 根据工厂代码、业务类型获取SAP交货单收料(V)("23")抬头文本与行文本
    	 */
    	Map<String,String>txtMap=new HashMap<String,String>();
    	txtMap.put("WERKS", params.get("WERKS").toString());
    	txtMap.put("BUSINESS_NAME",result.get(0).get("BUSINESS_NAME").toString());
    	txtMap.put("JZ_DATE", params.get("JZ_DATE").toString());
    	txtMap.put("RETURN_NO", params.get("RETURN_NO").toString());
    	txtMap.put("VENDOR_MANAGER", (result.get(0).get("VENDOR_MANAGER") == null)?"":result.get(0).get("VENDOR_MANAGER").toString());
    	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
    	for(Map<String,Object> _m:result) {
    		_m.put("HEADER_TXT", txt.get("txtrule"));
    		_m.put("ITEM_TEXT", txt.get("txtruleitem"));
    	}
		
		return R.ok().put("result", result);
	}
	
	@CrossOrigin
	@RequestMapping("/confirmReceiveRoomOutReturnPda")
	public R confirmReceiveRoomOutReturnPda(@RequestBody Map<String, Object> params) {
		return this.confirmReceiveRoomOutReturn(params);
	}
	
	@CrossOrigin
	@RequestMapping("/confirmReceiveRoomOutReturn")
	public R confirmReceiveRoomOutReturn(@RequestParam Map<String, Object> params) {
		try {
			logger.info("-->confirmReceiveRoomOutReturn");
			String result = wmsReturnGoodsService.confirmReceiveRoomOutReturn(params);
			
			List<Map<String,Object>> tpoParamsList = new ArrayList<Map<String,Object>>();
			JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
			Map<String,Object> currentUser = userUtils.getUser();
			JSONArray jaa =new JSONArray();
            JSONObject jsonObject=null;
			for(int i=0;i<jarr.size();i++){
				JSONObject itemData=  jarr.getJSONObject(i);
				//5.3.8.2针对WMS业务类型为SCM送货单类的退货单，还需同时调取SCM接口更新SCM送货单的可交货数量（可交货数量增加）。
				if("01".equals(itemData.getString("BUSINESS_TYPE"))) {
					Map<String, Object> tpo = new HashMap <String, Object>();
					tpo.put("PO_NO", itemData.getString("PO_NO"));
					tpo.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
					tpo.put("DELIVERYAMOUNT", itemData.getString("QTY2"));
					tpoParamsList.add(tpo);
				}else if("20".equals(itemData.getString("BUSINESS_TYPE"))) {
					//YK190813 20 云平台送货单
					jsonObject =  new JSONObject();
	            	jsonObject.put("deliveryNo",itemData.getString("ASNNO"));
	            	jsonObject.put("posnr",itemData.getString("ASNITM"));
    	            jsonObject.put("flag","1");
    	            jsonObject.put("qty",0-Double.valueOf(itemData.getString("QTY2")));
    	            jsonObject.put("user",currentUser.get("USERNAME"));
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
	    			return R.error("调用云平台接口回写数据失败！");
	    		}
            }
			
			return R.ok().put("result", result);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error(e.getMessage()+" 系统异常，请联系管理员！");
		}

	}
	
	@RequestMapping("/confirmWareHouseOutReturn")
	public R confirmWareHouseOutReturn(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmWareHouseOutReturn");
		String result = "";
		try {
			result = wmsReturnGoodsService.confirmWareHouseOutReturn(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error(e.getMessage()+" 系统异常，请联系管理员！");
		}
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		//10.3.9.7	针对122类退货和产成品退车间类退货页面，确认还需同步更新【WMS收货单表】的库房冲销/退货数量字段（数量增加）。
		//在更新【WMS收货单表】时，针对WMS业务类型为SCM送货单类的，还需同时调取SCM接口更新SCM送货单的可交货数量（可交货数量增加）。
		List<Map<String,Object>> tpoParamsList = new ArrayList<Map<String,Object>>();
		if("01".equals(jarr.getJSONObject(0).get("BUSINESS_NAME"))) {
			for(int i=0;i<jarr.size();i++){
				JSONObject itemData=  jarr.getJSONObject(i);
				Map<String, Object> tpo = new HashMap <String, Object>();
				tpo.put("PO_NO", itemData.getString("PO_NO"));
				tpo.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
				tpo.put("DELIVERYAMOUNT", Float.valueOf(itemData.getString("RETURN_QTY")));
				tpoParamsList.add(tpo);
			}
		}
		if(tpoParamsList.size()>0)scmDeliveryService.updateTPO(tpoParamsList);
		Map<String,Object> currentUser = userUtils.getUser();
		JSONArray jaa =new JSONArray();
        JSONObject jsonObject=null;
		if("20".equals(jarr.getJSONObject(0).get("BUSINESS_TYPE"))) {
			for(int i=0;i<jarr.size();i++){
				JSONObject itemData=  jarr.getJSONObject(i);
				//YK190813 20 云平台送货单
				jsonObject =  new JSONObject();
	        	jsonObject.put("deliveryNo",itemData.getString("ASNNO"));
	        	jsonObject.put("posnr",itemData.getString("ASNITM"));
	            jsonObject.put("flag","2");
	            jsonObject.put("qty",0-Double.valueOf(itemData.getString("RETURN_QTY")));
	            jsonObject.put("user",currentUser.get("USERNAME"));
	            jsonObject.put("date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	            jaa.add(jsonObject);
			}
		}
		if(jaa.size()>0) {
        	Map<String,Object> param =new HashMap<String,Object>();
        	param.put("param", jaa.toString());
        	Map<String,Object>res=wmsCloudPlatformRemote.sendDeliveryData(param);
        	if("500".equals(res.get("code").toString())) {
    			return R.error("调用云平台接口回写数据失败！");
    		}
        }
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getWareHouseOutData37")
	public R getWareHouseOutData37(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData37");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String IO_NO = params.get("IO_NO").toString();
		String WERKS = params.get("WERKS").toString();
		Map<String,Object> sapResult = wmsSapRemote.getSapBapiKaufOrder(IO_NO);
		logger.info("-->sapResult="+sapResult.toString());
		//{KTEXT=1102 TCC测试, BUKRS=C190, PHAS3=, KOSTL=, SAKNR=, LOEKZ=, WERKS=C190, AUFNR=001500006664, PHAS1=X}
		//{MESSAGE=ORDER_NOT_FOUND, CODE=-1}
		String bucks = wmsReturnGoodsService.getPlantBucks(WERKS);
		
		if(sapResult.get("CODE") != null) {
			return R.error("SAP接口返回失败："+sapResult.get("MESSAGE"));
		}
		//PHAS1=X且LOEKZ≠X为有效的可操作订单，否则系统报错：“订单*********不允许货物移动” 
		if(!("X".equals(sapResult.get("PHAS1")) || (!"X".equals(sapResult.get("LOEKZ"))))) {
			return R.error("订单不允许货物移动：PHAS1=X且LOEKZ≠X为有效的可操作订单");
		}
		if(!bucks.equals(sapResult.get("BUKRS"))) {
			return R.error("该订单号不属于"+WERKS+",请确认工厂及订单号是否正确!");
		}
		result = wmsReturnGoodsService.getWareHouseOutData37(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getWareHouseOutData33")
	public R getWareHouseOutData33(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData33");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String COSTCENTER = params.get("COSTCENTER").toString();
		String WERKS = params.get("WERKS").toString();
		Map<String,Object> sapResult = wmsSapRemote.getSapBapiCostcenterDetail(COSTCENTER);
		logger.info("-->sapResult="+sapResult.toString());
		//校验输入的成本中心是否存在，是否在有效期内（VALID_TO-有效截止日期），否，报错‘***成本中心不存在/不在有效期内’。
		//效验成本中心所属公司代码（COMP_CODE是否与页面操作工厂对应的公司代码一致），否报错：‘***成本中心不属于****公司代码’。
		//{VALID_TO=9999-12-31, CODE=, DESCRIPT=总经理办公室, COSTCENTER=C1600100, VALID_FROM=2013-01-01, 
		//COMP_CODE=C160, NAME=总经理办公室}
		String bucks = wmsReturnGoodsService.getPlantBucks(WERKS);
		if(!bucks.equals(sapResult.get("COMP_CODE"))) {
			return R.error("成本中心不属于"+WERKS+"或没有此成本中心号!");
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		try {
			Date et=sdf.parse(sapResult.get("VALID_TO").toString());
			Date st=sdf.parse(DateUtils.format(new Date(),"yyyy-MM-dd"));
			logger.info(et.toString() + "|" + st.toString());
			if (et.before(st)){ 
				return R.error("成本中心"+COSTCENTER+"有效期到" + sapResult.get("VALID_TO").toString());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		result = wmsReturnGoodsService.getWareHouseOutData33(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/getWareHouseOutData34")
	public R getWareHouseOutData34(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData34");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String WBS_NO = params.get("WBS").toString();
		String WERKS = params.get("WERKS").toString();
		Map<String,Object> sapResult = wmsSapRemote.getSapBapiSapBapiWbs(WBS_NO);
		logger.info("-->sapResult="+sapResult.toString());
		if(sapResult.get("CODE").toString().equals("0")) {
			if(!WERKS.equals(sapResult.get("COMP_CODE").toString())) {
				return R.error("WBS元素号不属于"+WERKS);
			}
		}else {
			return R.error("SAP接口查询失败："+sapResult.get("MESSAGE").toString());
		}
		result = wmsReturnGoodsService.getWareHouseOutData34(params);		
		return R.ok().put("result", result);
	}
	
	//31 研发/内部订单成品退货(261) 32 CO订单成品退货(102)
	@RequestMapping("/getWareHouseOutData31")
	public R getWareHouseOutData31(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData31");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String WERKS = params.get("WERKS").toString();
		String ionos = params.get("IO_NO").toString();
		//8.3.2.1	调用SAP接口函数：ZGPP_KAUF_ORDER_READ(getSapBapiKaufOrder)：
		//获取SAP系统中ORDER_NAME-订单描述，并校验内部订单返回的“PLANT-工厂” 是否与页面操作的“工厂”一致，否，提示“该订单号不属于***工厂，请确认工厂及订单号是否正确”。
		//判断内部订单状态， PHAS1=X且LOEKZ≠X为有效的可操作订单，否则系统报错：“订单*********不允许货物移动”。
		Map<String,Object> sapResult = wmsSapRemote.getSapBapiKaufOrder(ionos);
		System.out.println("SAP RE CODE = " + sapResult.get("CODE"));
		if(sapResult.get("CODE").toString().equals("0")) {
			if(!WERKS.equals(sapResult.get("BUKRS").toString())) {
				return R.error("该订单号不属于"+WERKS);
			}
		}else {
			return R.error("SAP接口查询失败："+sapResult.get("MESSAGE").toString());
		}
		String MATNR = params.get("MATNR").toString();
		MATNR = MATNR.replaceAll(",", "','");
		params.put("MATNR", "('" + MATNR + "')");
		result = wmsReturnGoodsService.getWareHouseOutData31(params);
		return R.ok().put("result", result);
	}
	
	/**
	 * 查询 【库房退货:生产订单半成品退货】数据
	 * @param params
	 * @return
	 */
	@RequestMapping("/getWareHouseOutData30")
	public R getWareHouseOutData30(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData30");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String WERKS = params.get("WERKS").toString();
		String ponos = params.get("PO_NO").toString();
		//先判断工厂是否启用核销业务（【WMS_C_PLANT---工厂配置表】”HX_FLAG=X”即为启用核销业务）
		//5.3.2.1启用则继续判断该生产订单是否做过核销账务,如剩余核销数量>0（关联查询【生产订单抬头核销表--- WMS_HX_MO_HEAD】，
		//则不允许做继续做生产订单退货业务，系统返回消息：该生产订单存在V类业务数据。
		List<Map<String, Object>> plantInfo = new ArrayList<Map<String, Object>>();
		plantInfo = wmsReturnGoodsService.getPlantInfoByWerks(params);
		logger.info("-->HX_FLAG = " + plantInfo.get(0).get("HX_FLAG"));
		
		List<String> po_list=Arrays.asList(ponos.split(","));
		for(int i=0;i<po_list.size();i++) {
			logger.info("-->PO:" + po_list.get(i));
			if("X".equals(plantInfo.get(0).get("HX_FLAG"))) {
				//启用核销
				int hx_qty = wmsReturnGoodsService.getMoHeadHxQty(po_list.get(i));
				if(hx_qty > 0) {
					return R.error("生产订单："+po_list.get(i)+"存在V类业务数据！");
				}
			}
			
			//5.3.2.2到【生产订单抬头表--- WMS_SAP_MO_HEAD】中查询确认生产订单是否存在，
			//如不存在，报错提示：生产订单********不存在，请检查生产订单或手工同步生产订单。
			Map<String, Object> par = new HashMap <String, Object>();
			par.put("AUFNR", po_list.get(i));
			par.put("WERKS", WERKS);
			int mo_qty = wmsReturnGoodsService.getSapMoCount(par);
			if(mo_qty == 0) {
				return R.error("生产订单："+po_list.get(i)+"不存在，请检查生产订单或手工同步生产订单！");
			}else {
				String mo_status = wmsReturnGoodsService.getSapMoStatus(par);
				logger.info("-->mo_status:" + mo_status);
				//订单状态需 包含REL 不含DLFL 不含LKD  REL MSPT PRC SETC SSAP OPGN NEWQ
				if(!((mo_status.indexOf("REL") >= 0) && (mo_status.indexOf("DLFL") < 0) && (mo_status.indexOf("LKD") < 0))) {
					return R.error("生产订单："+po_list.get(i)+"未释放，已关闭或已删除！");
				}
			}
		}
		result = wmsReturnGoodsService.getWareHouseOutData30(params);	
		return R.ok().put("result", result);
	}
	
	/**
	 * 查询 【库房退货:采购订单退货161】数据
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getWareHouseOutData29")
	public R getWareHouseOutData29(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData29");
		//首先到WMS【采购订单同步表】中查询确认采购订单是否存在，如不存在，报错提示：采购订单**********不存在，请检查采购订单或手工同步采购订单。
		List<Map<String, Object>> poSapDataList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> poDataList = new ArrayList<Map<String, Object>>();
		poSapDataList = wmsReturnGoodsService.getSapPoData(params);
		if(poSapDataList.size() == 0) {
			return R.error("采购订单："+params.get("PONO")+"不存在或当前工厂下没有数据，请检查采购订单或手工同步采购订单！");
		}else {
			//如用户输入的采购订单为退货采购订单（SAP【EKPO】RETPO=X），系统返回消息：该采购订单为退货采购订单，不支持122退货。
			/*if(!"X".equals(poDataList.get(0).get("RETPO"))) {
				return R.error("采购订单："+params.get("PONO")+"不支持161退货！");
			}*/
			//BUG #1270 TCC 190807  19部有把退货和正常收货的 下到了同一个采购订单 只需要读取 retpo=x的数据
			//有几行是X就查出几行来，全部都不是X就报错
			for(int i=0;i<poSapDataList.size();i++) {
				if("X".equals(poSapDataList.get(i).get("RETPO"))) {
					poDataList.add(poSapDataList.get(i));
				}
			}
			
			//如果输入的单号是STO采购订单，报错提示：STO采购订单，请选择STO退货功能 （通过采购订单抬头的采购凭证类型判断，如果是BYDS则报错）。
			logger.info("-->BSTYP : " + poSapDataList.get(0).get("BSTYP"));
			if("BYDS".equals(poSapDataList.get(0).get("BSTYP"))) {
				return R.error("采购订单："+params.get("PONO")+"是STO采购订单，请选择STO退货功能！");
			}
			//判断采购订单是否是有效订单
			//2.3.2.4如采购订单未批准，点击“创建退货单”时，系统返回消息：采购凭证 ********** 未批准！采购订单抬头---WMS_SAP_PO_HEAD】表的“FRGRL=空”代表采购订单已审批，“FRGRL=X”代表采购订单未审批。
			if("X".equals(poSapDataList.get(0).get("FRGRL"))) {
				return R.error("采购订单："+params.get("PONO")+"未批准！");
			}
			//2.3.2.5如采购订单行项目均被删除，系统返回消息：采购订单**********没有项目。			【SQL中校验】
			//2.3.2.6如采购订单行项目均标记为“交货已完成”，系统返回消息：采购订单**********没有项目。	【SQL中校验】
		}
		String mat = "";
		for(int i=0;i<poDataList.size();i++) {
			String mat_str = "(S.MATNR = '"+poDataList.get(0).get("MATNR")+"' AND S.SOBKZ = '"+poDataList.get(0).get("SOBKZ")+"')";
			mat += mat_str + " OR ";
		}
		mat = "(" + mat.substring(0,mat.length()-4) + ")";
		logger.info("-->mat : " + mat);
		
		params.put("mat", mat);
		params.put("LIFNR", poDataList.get(0).get("LIFNR"));
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = wmsReturnGoodsService.getWareHouseOutData29(params);	
	
		return R.ok().put("result", result);
	}
	
	/**
	 * 查询 【库房退货:采购订单退货122】数据
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getWareHouseOutData28")
	public R getWareHouseOutData28(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData28");
		
		List<Map<String, Object>> poDataList = new ArrayList<Map<String, Object>>();
		poDataList = wmsReturnGoodsService.getSapPoData(params);
		if(poDataList.size() == 0) {
			return R.error("采购订单："+params.get("PONO")+"不存在或当前工厂下没有数据，请检查采购订单或手工同步采购订单！");
		}
		//如用户输入的采购订单为退货采购订单（SAP【EKPO】RETPO=X），系统返回消息：该采购订单为退货采购订单，不支持122退货。
		if("X".equals(poDataList.get(0).get("RETPO"))) {
			return R.error("采购订单："+params.get("PONO")+"为退货采购订单，不支持122退货！");
		}
		
		//系统先判断工厂是否启用核销业务（【WMS_C_PLANT---工厂配置表】”HX_FLAG=X”即为启用核销业务），
		//如启用则继续判断原采购订单是否做过核销账务,如剩余核销数量>0（关联查询【PO核销信息表--- WMS_HX_PO】，则不允许做继续做122退货业务，
		//系统返回消息：该采购订单存在V类业务数据。
		List<Map<String, Object>> plantInfo = new ArrayList<Map<String, Object>>();
		plantInfo = wmsReturnGoodsService.getPlantInfoByWerks(params);
		logger.info("-->HX_FLAG = " + plantInfo.get(0).get("HX_FLAG"));
		if("X".equals(plantInfo.get(0).get("HX_FLAG"))) {
			int hxqty = 0;
			hxqty = wmsReturnGoodsService.getPoHxQty(params.get("PONO").toString());
			if(hxqty > 0) {
				return R.error("该采购订单存在V类业务数据!");
			}
		}
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = wmsReturnGoodsService.getWareHouseOutData28(params);
		return R.ok().put("result", result);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getWareHouseOutData27")
	public R getWareHouseOutData27(@RequestParam Map<String, Object> params) {
		logger.info("---->getWareHouseOutData27");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		//4.3.2.1关联【退货单抬头表】和【退货单行项目表】查询该“SAP交货单”是否已被创建过退货单（包含创建、下架和完成）且未被标记删除，
		//如存在未删除状态的退货单，则报错：该SAP退货交货单已存在WMS退货单。
		int returnCount = 0;
		returnCount = wmsReturnGoodsService.getWmsReturnItemCountBySapOutNo(params.get("SAP_OUT_NO").toString());
		if(returnCount > 0) {
			return R.error("该SAP退货交货单已存在WMS退货单!");
		}
		//4.3.2.2调取SAP接口查询是否存在有效的STO退货交货单（查询SAP【LIKP】表，VBTYP= T类型的(即SD的凭证类别=订单的退货类型)，
		//且在【VBUK】表，WBSTK=A的交货单即为有效的允许过账的退货交货单）。
		//如查询确认交货单的VBTYP≠T，则系统报错：SAP交货单**********不是正确的退货交货单。
		Map<String,Object> sapReturnMap = wmsSapRemote.getSapBapiDeliveryGetlist(params.get("SAP_OUT_NO").toString());
		logger.info("-->sapReturnMap = " + sapReturnMap.toString());
		//List<Map<String, Object>> outDataGroup = new ArrayList<Map<String, Object>>();
		
		if(null == sapReturnMap.get("CODE")) {
			//4.3.2.4如查询SAP交货单不存在或交货单已删除，则页面返回消息：SAP交货单********在工厂XXXX下不存在。
			List<Map<String, Object>> sapItems = new ArrayList<Map<String, Object>>();
			sapItems = (List<Map<String, Object>>)sapReturnMap.get("item");
			if(0 == sapItems.size()) {
				return R.error("SAP交货单"+params.get("SAP_OUT_NO")+"在工厂"+params.get("WERKS")+"下不存在!");
			}
			
			if(!"T".equals(((Map<String, Object>)sapReturnMap.get("header")).get("VBTYP"))) {
				return R.error("SAP交货单"+params.get("SAP_OUT_NO")+"不是正确的退货交货单!");
			}
			//4.3.2.5如查询SAP交货单已交货完成（即接口查询SAP【VBUK】表，WBSTK=C），则页面返回消息：交货已完成。
			if("C".equals(((Map<String, Object>)sapReturnMap.get("header")).get("WBSTK"))){
				return R.error("SAP交货单"+params.get("SAP_OUT_NO")+"交货已完成!");
			}
			//outDataGroup
			params.put("outDataGroup", sapItems);
		}else {
			return R.error("SAP接口查询失败！MESSAGE：" + sapReturnMap.get("MESSAGE"));
		}
		
		result = wmsReturnGoodsService.getWareHouseOutData27(params);
		return R.ok().put("result", result);
	}
	
	@RequestMapping("/createWareHouseOutReturn")
	public R createWareHouseOutReturn(@RequestParam Map<String, Object> params) {
		String outNo = wmsReturnGoodsService.createWareHouseOutReturn(params);
		if("-1".equals(outNo)) {
			return R.error("退货单号生成失败！");
		}else {
			return R.ok().put("outNo",outNo);
		}
	}
	
	
	/**
	 * PDA 161 122 库房退货 确认
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/pdaWareHouseOutReturnConfirm")
	public R pdaWareHouseOutReturnConfirm(@RequestBody Map<String, Object> params) {
		String msg = "";
		try {
			msg = wmsReturnGoodsService.pdaWareHouseOutReturnConfirm(params);
		}catch (Exception e) {
			msg = e.getMessage();
			return R.error(msg);
		}
		return R.ok().put("msg",msg);
	}
	
	@RequestMapping("/wareHouseOutPickup")
	public R wareHouseOutPickup(@RequestParam Map<String, Object> params) {
		String outNo = wmsReturnGoodsService.wareHouseOutPickup(params);
		if("-1".equals(outNo)) {
			return R.error("拣配失败！");
		}else if("-2".equals(outNo)) {
			return R.error("下架数量超出冻结数量！");
		}else {
			return R.ok().put("outNo",outNo);
		}
	}
	
	@RequestMapping("/wareHouseOutPickupCancel")
	public R wareHouseOutPickupCancel(@RequestParam Map<String, Object> params) {
		String ret = wmsReturnGoodsService.wareHouseOutPickupCancel(params);
		if("-1".equals(ret)) {
			return R.error("取消下架失败！");
		}else {
			return R.ok();
		}
	}
	
	public static String removeZeroStr(String str) {
		if(str == null) return "";
		for(int i=0;i<str.length();i++) {
			if(!"0".equals(str.substring(i,i+1))) {
				str = str.substring(i);
				break;
			}
		}
		return str;
	}
}
