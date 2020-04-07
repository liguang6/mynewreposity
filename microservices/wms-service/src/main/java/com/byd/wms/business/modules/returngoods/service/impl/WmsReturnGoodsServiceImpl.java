package com.byd.wms.business.modules.returngoods.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.account.entity.WmsHxDnEntity;
import com.byd.wms.business.modules.account.entity.WmsHxPoEntity;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxDnService;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxPoService;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.returngoods.dao.WmsReturnGoodsDao;
import com.byd.wms.business.modules.returngoods.service.WmsReturnGoodsService;

@Service("wmsOutService")
public class WmsReturnGoodsServiceImpl implements WmsReturnGoodsService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private WmsReturnGoodsDao wmsReturnGoodsDao;
	@Autowired
	WmsCDocNoService wmscDocNoService;
	@Autowired
	private CommonService commonService;
    @Autowired
    WmsCTxtService wmsCTxtService;
	@Autowired
	private WmsAccountReceiptHxPoService wmsAccountReceiptHxPoService;
	@Autowired
	private WmsAccountReceiptHxDnService wmsAccountReceiptHxDnService;
    @Autowired
    private UserUtils userUtils;
	@Autowired
	WmsSapRemote wmsSapRemote;
	
	@Override
	public List<String> getBusinessNameListByWerks(Map<String, Object> params) {
		return wmsReturnGoodsDao.getBusinessNameListByWerks(params);
	}
	
	@Override
	public List<Map<String, Object>> getReceiveRoomOutData(Map<String, Object> params){
		return wmsReturnGoodsDao.getReceiveRoomOutData(params);
	}
	
	@Override
	public int getWmsOutReturnSapOutCount(String SAP_OUT_NO) {
		return wmsReturnGoodsDao.getWmsOutReturnSapOutCount(SAP_OUT_NO);
	}
	
	@Override
	public List<Map<String, Object>> getReturnInfoByBarcode(Map<String, Object> params){
		return wmsReturnGoodsDao.getReturnInfoByBarcode(params);
	}
	
	@Override
	public List<Map<String, Object>> getSapPoInfo(Map<String, Object> params){
		return wmsReturnGoodsDao.getSapPoInfo(params);
	}
	
	@Override
	public List<Map<String, Object>> getSapPoBarcodeInfo(Map<String, Object> params){
		return wmsReturnGoodsDao.getSapPoBarcodeInfo(params);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createReceiveRoomOutReturn(Map<String, Object> params) {
		String outNo = "";
		//String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		//同一供应商的物料才允许创建在同一张退货单上，当选择不同供应商的物料退料时，系统需要根据供应商做拆单处理（即生成多张退货单）。
		List<String> lifnr_list = new ArrayList<String>();
		for(int i=0;i<jarr.size();i++){
			JSONObject outData=  jarr.getJSONObject(i);
			if(i==0) {
				lifnr_list.add(outData.getString("LIFNR") + "|" + outData.getString("LIKTX"));
			}else {
				if(!lifnr_list.contains(outData.getString("LIFNR") + "|" + outData.getString("LIKTX"))) {
					lifnr_list.add(outData.getString("LIFNR") + "|" + outData.getString("LIKTX"));
				}
			}
		}
		
		for(int i=0;i<lifnr_list.size();i++) {
			logger.info("---->lifnr_list " + i + " :" +
					lifnr_list.get(i).substring(0, lifnr_list.get(i).indexOf("|")) + "--" +
					lifnr_list.get(i).substring(lifnr_list.get(i).indexOf("|")+1,lifnr_list.get(i).length())
					);
			//【退货单抬头表】
			Map<String, Object> params2 = new HashMap <String, Object>();
			params2.put("WERKS", params.get("WERKS").toString());
			params2.put("WMS_DOC_TYPE", "06");
			
			Map<String,Object> rtnNoMap = wmscDocNoService.getDocNo(params2);
			if(rtnNoMap.get("MSG") !=null && !"success".equals(rtnNoMap.get("MSG"))) {
				return "-2";
			}
			String RETURN_NO = rtnNoMap.get("docno").toString();
			if(null == RETURN_NO) {
				return "-1";
			}
			outNo += RETURN_NO + ";";
			String HEADER_TXT = "";
			Map<String,Object> currentUser = userUtils.getUser();
		   	params.put("USERNAME", currentUser.get("USERNAME"));
			
			Map<String, Object> headMap = new HashMap <String, Object>();
			headMap.put("RETURN_NO", RETURN_NO);
			headMap.put("RETURN_TYPE", "04");	//ModBy:YK190621 改为存BUSINESS_CLASS
			headMap.put("RETURN_STATUS", "00");
			headMap.put("F_WERKS", "");
			headMap.put("WERKS", params.get("WERKS").toString());
			headMap.put("F_WH_NUMBER", "");
			headMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
			headMap.put("LIFNR", lifnr_list.get(i).substring(0, lifnr_list.get(i).indexOf("|")));
			headMap.put("LIKTX", lifnr_list.get(i).substring(lifnr_list.get(i).indexOf("|")+1,lifnr_list.get(i).length()));
			headMap.put("HEADER_TXT", HEADER_TXT);
			headMap.put("IS_AUTO", "");
			headMap.put("DEL", "0");
			headMap.put("CREATOR", params.get("USERNAME").toString());
			headMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
			headMap.put("EDITOR", "");
			headMap.put("EDIT_DATE", "");
			wmsReturnGoodsDao.insertWmsOutReturnHead(headMap);
			
			//【退货单行项目表】
			int RETURN_ITEM_NO = 1;
			for(int j=0;j<jarr.size();j++){
				JSONObject outData=  jarr.getJSONObject(j);
				
				if(lifnr_list.get(i).equals(outData.getString("LIFNR") + "|" + outData.getString("LIKTX"))) {										
					Map<String, Object> itemMap = new HashMap <String, Object>();
					Map<String, Object> itemDetailMap = new HashMap <String, Object>();
					itemMap.put("RETURN_NO",RETURN_NO);
					itemMap.put("RETURN_ITEM_NO",RETURN_ITEM_NO);					
					itemMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME").toString());
					itemMap.put("BUSINESS_TYPE",outData.getString("BUSINESS_TYPE"));					
					itemMap.put("WERKS",params.get("WERKS").toString());
					itemMap.put("WH_NUMBER",params.get("WH_NUMBER").toString());
					itemMap.put("F_LGORT","");
					itemMap.put("MATNR",outData.getString("MATNR"));
					itemMap.put("MAKTX",outData.getString("MAKTX"));
					itemMap.put("UNIT",outData.getString("UNIT"));
					itemMap.put("TOTAL_RETURN_QTY",outData.getString("QTY3"));
					itemMap.put("RETURN_PEOPLE",params.get("USERNAME").toString());
					itemMap.put("RETURN_REASON_DESC",outData.getString("REASON"));
					itemMap.put("ITEM_STATUS","00");
					itemMap.put("MEMO","");
					itemMap.put("RECEIPT_NO",outData.getString("RECEIPT_NO"));
					itemMap.put("RECEIPT_ITEM_NO",outData.getString("RECEIPT_ITEM_NO"));
					itemMap.put("LIFNR",outData.getString("LIFNR"));
					itemMap.put("LIKTX",outData.getString("LIKTX"));
					itemMap.put("PO_NO",outData.getString("PO_NO"));
					itemMap.put("PO_ITEM_NO",outData.getString("PO_ITEM_NO"));
					itemMap.put("SAP_OUT_NO",removeZeroStr(outData.getString("SAP_OUT_NO")));
					itemMap.put("SAP_OUT_ITEM_NO",outData.getString("SAP_OUT_ITEM_NO"));
					itemMap.put("MO_NO","");
					itemMap.put("MO_ITEM_NO","");
					itemMap.put("RSNUM","");
					itemMap.put("RSPOS","");
					itemMap.put("COST_CENTER","");
					itemMap.put("IO_NO","");
					itemMap.put("WBS","");
					itemMap.put("SAP_MATDOC_NO","");
					itemMap.put("SAP_MATDOC_ITEM_NO","");
					itemMap.put("INBOUND_NO","");
					itemMap.put("INBOUND_ITEM_NO","");
					itemMap.put("SQE","");
					itemMap.put("PICK_FLAG","0");
					itemMap.put("DEL","0");
					itemMap.put("CREATOR",params.get("USERNAME").toString());
					itemMap.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
					itemMap.put("EDITOR","");
					itemMap.put("EDIT_DATE","");
					itemMap.put("LABEL_NO",outData.getString("LABEL_NOS"));
					itemMap.put("REF_SAP_MATDOC_NO",outData.getString("REF_SAP_MATDOC_NO"));
					itemMap.put("REF_SAP_MATDOC_ITEM_NO",outData.getString("REF_SAP_MATDOC_ITEM_NO"));
					itemMap.put("REF_SAP_MATDOC_YEAR",outData.getString("REF_SAP_MATDOC_YEAR"));
					
					itemDetailMap.put("RETURN_NO",RETURN_NO);
					itemDetailMap.put("RETURN_ITEM_NO",RETURN_ITEM_NO);	
					itemDetailMap.put("MATNR",outData.getString("MATNR"));
					itemDetailMap.put("LGORT",outData.getString("LGORT"));
					itemDetailMap.put("F_BATCH",outData.getString("F_BATCH"));
					itemDetailMap.put("BATCH",outData.getString("BATCH"));
					itemDetailMap.put("RETURN_QTY",outData.getString("QTY3"));
					itemDetailMap.put("LIFNR",outData.getString("LIFNR"));
					itemDetailMap.put("SOBKZ",outData.getString("SOBKZ"));
					itemDetailMap.put("BIN_CODE","");
					itemDetailMap.put("BIN_CODE_XJ","");
					itemDetailMap.put("XJ_QTY","");
					itemDetailMap.put("REAL_QTY","");
					itemDetailMap.put("ITEM_TEXT","");
					itemDetailMap.put("RETURN_PEOPLE",params.get("USERNAME").toString());
					itemDetailMap.put("MEMO","");
					itemDetailMap.put("DEL","0");
					itemDetailMap.put("CREATOR",params.get("USERNAME").toString());
					itemDetailMap.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
					itemDetailMap.put("EDITOR","");
					itemDetailMap.put("EDIT_DATE","");
					itemDetailMap.put("LABEL_NO",outData.getString("LABEL_NOS"));
					
					wmsReturnGoodsDao.insertWmsOutReturnItem(itemMap);
					wmsReturnGoodsDao.insertWmsOutReturnItemDetail(itemDetailMap);
					RETURN_ITEM_NO++;
				}
			}
		}
		return outNo;
	}
	
	@Override
	@Transactional
	public String pdaWareHouseOutReturnConfirm(Map<String, Object> params) {
		String outNo = "";
		String pickNo = "";
		String confirmNo = "";
		outNo = this.createWareHouseOutReturn(params);
		
		params.put("OUT_NO", outNo);
		params.put("RETURN_NO", outNo);
		List<Map<String, Object>> picklist = this.getWareHouseOutPickupData(params);
		String jsonStr = JSONArray.toJSONString(picklist);
		System.out.println(jsonStr);		
		params.put("ARRLIST", jsonStr);
		
		pickNo = this.wareHouseOutPickup(params);
		if("-1".equals(pickNo)) {
			throw new RuntimeException("拣配单号生成失败！");
		}else if("-2".equals(pickNo)) {
			throw new RuntimeException("下架数量超出冻结数量！");
		}
		
		picklist = this.getWareHouseOutReturnData(params);
		jsonStr = JSONArray.toJSONString(picklist);
		params.put("ARRLIST", jsonStr);
		confirmNo = this.confirmWareHouseOutReturn(params);
		return outNo + pickNo + confirmNo;
	}
	
	@Override
	public String createWareHouseOutReturn(Map<String, Object> params) {
		String outNo = "";
		//String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		Map<String,Object> currentUser = userUtils.getUser();
		//【退货单抬头表】
		Map<String, Object> params2 = new HashMap <String, Object>();
		params2.put("WERKS", params.get("WERKS").toString());
		params2.put("WMS_DOC_TYPE", "06");

		String RETURN_NO = wmscDocNoService.getDocNo(params2).get("docno").toString();
		if(null == RETURN_NO) {
			return "-1";
		}
		outNo = RETURN_NO;
		String HEADER_TXT = "";	//TODO
		
		Map<String, Object> headMap = new HashMap <String, Object>();
		headMap.put("RETURN_NO", RETURN_NO);
		headMap.put("RETURN_TYPE", "05");		//ModBy:YK190621 改为存BUSINESS_CLASS
		headMap.put("RETURN_STATUS", "00");
		headMap.put("F_WERKS", "");
		headMap.put("WERKS", params.get("WERKS").toString());
		headMap.put("F_WH_NUMBER", "");
		headMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
		headMap.put("LIFNR", jarr.getJSONObject(0).getString("LIFNR"));
		headMap.put("LIKTX", jarr.getJSONObject(0).getString("LIKTX"));
		headMap.put("HEADER_TXT", HEADER_TXT);
		headMap.put("IS_AUTO", "");
		headMap.put("DEL", "0");
		headMap.put("CREATOR", currentUser.get("USERNAME"));
		headMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
		headMap.put("EDITOR", "");
		headMap.put("EDIT_DATE", "");
		
		wmsReturnGoodsDao.insertWmsOutReturnHead(headMap);
		
		//【退货单行项目表】
		int RETURN_ITEM_NO = 1;
		for(int j=0;j<jarr.size();j++){
			JSONObject outData=  jarr.getJSONObject(j);
			Map<String, Object> itemMap = new HashMap <String, Object>();
			Map<String, Object> itemDetailMap = new HashMap <String, Object>();
			itemMap.put("RETURN_NO",RETURN_NO);
			itemMap.put("RETURN_ITEM_NO",RETURN_ITEM_NO);					
			itemMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME").toString());
			itemMap.put("BUSINESS_TYPE",(outData.getString("BUSINESS_TYPE") != null)?outData.getString("BUSINESS_TYPE"):params.get("BUSINESS_TYPE").toString());					
			itemMap.put("WERKS",params.get("WERKS").toString());
			itemMap.put("WH_NUMBER",params.get("WH_NUMBER").toString());
			itemMap.put("F_LGORT",outData.getString("LGORT"));
			itemMap.put("MATNR",outData.getString("MATNR"));
			itemMap.put("MAKTX",outData.getString("MAKTX"));
			System.out.println("-->UNIT : " + ((outData.getString("MEINS") != null)?outData.getString("MEINS").toString():outData.getString("UNIT").toString()));
			itemMap.put("UNIT",(outData.getString("MEINS")!= null)?outData.getString("MEINS"):outData.getString("UNIT"));
			itemMap.put("TOTAL_RETURN_QTY",outData.getString("QTY1"));
			itemMap.put("RETURN_PEOPLE",currentUser.get("USERNAME"));
			itemMap.put("RETURN_REASON_DESC","");
			itemMap.put("ITEM_STATUS","00");
			itemMap.put("MEMO",outData.getString("MEMO"));
			itemMap.put("RECEIPT_NO","");
			itemMap.put("RECEIPT_ITEM_NO","");
			itemMap.put("LIFNR",outData.getString("LIFNR"));
			itemMap.put("LIKTX",outData.getString("LIKTX"));
			itemMap.put("PO_NO",outData.getString("PONO"));
			itemMap.put("PO_ITEM_NO",outData.getString("EBELP"));
			itemMap.put("RECEIPT_NO",outData.getString("RECEIPT_NO"));
			itemMap.put("RECEIPT_ITEM_NO",outData.getString("RECEIPT_ITEM_NO"));
			itemMap.put("SAP_OUT_NO",(outData.getString("SAP_OUT_NO")!= null)?outData.getString("SAP_OUT_NO"):"");		//SAP_OUT_NO
			itemMap.put("SAP_OUT_ITEM_NO",(outData.getString("POSNR")!= null)?outData.getString("POSNR"):"");
			itemMap.put("MO_NO",(outData.getString("MO_NO")!= null)?outData.getString("MO_NO"):"");
			itemMap.put("MO_ITEM_NO",(outData.getString("MO_ITEM_NO")!= null)?outData.getString("MO_ITEM_NO"):"");
			itemMap.put("RSNUM",(outData.getString("RSNUM")!= null)?outData.getString("RSNUM"):"");
			itemMap.put("RSPOS",(outData.getString("RSPOS")!= null)?outData.getString("RSPOS"):"");
			itemMap.put("COST_CENTER",(outData.getString("COST_CENTER")!= null)?outData.getString("COST_CENTER"):"");
			itemMap.put("IO_NO",(outData.getString("IO_NO")!= null)?outData.getString("IO_NO"):"");
			itemMap.put("WBS",(outData.getString("WBS")!= null)?outData.getString("WBS"):"");
			itemMap.put("SAP_MATDOC_NO",(outData.getString("SAP_MATDOC_NO")!= null)?outData.getString("SAP_MATDOC_NO"):"");
			itemMap.put("SAP_MATDOC_ITEM_NO",(outData.getString("SAP_MATDOC_ITEM_NO")!= null)?outData.getString("SAP_MATDOC_ITEM_NO"):"");
			itemMap.put("INBOUND_NO",(outData.getString("INBOUND_NO")!= null)?outData.getString("INBOUND_NO"):"");
			itemMap.put("INBOUND_ITEM_NO",(outData.getString("INBOUND_ITEM_NO")!= null)?outData.getString("INBOUND_ITEM_NO"):"");
			itemMap.put("SQE",(outData.getString("SQE")!= null)?outData.getString("SQE"):"");
			itemMap.put("PICK_FLAG","X");
			itemMap.put("DEL","0");
			itemMap.put("CREATOR",currentUser.get("USERNAME"));
			itemMap.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
			itemMap.put("EDITOR","");
			itemMap.put("EDIT_DATE","");
			
			itemDetailMap.put("RETURN_NO",RETURN_NO);
			itemDetailMap.put("RETURN_ITEM_NO",RETURN_ITEM_NO);	
			itemDetailMap.put("MATNR",outData.getString("MATNR"));
			itemDetailMap.put("LGORT",outData.getString("LGORT"));
			itemDetailMap.put("F_BATCH",outData.getString("F_BATCH"));
			itemDetailMap.put("BATCH",outData.getString("BATCH"));
			itemDetailMap.put("RETURN_QTY",outData.getString("QTY1"));
			itemDetailMap.put("LIFNR",outData.getString("LIFNR"));
			itemDetailMap.put("SOBKZ",outData.getString("SOBKZ"));
			itemDetailMap.put("BIN_CODE","");
			itemDetailMap.put("BIN_CODE_XJ","");
			itemDetailMap.put("XJ_QTY","");
			itemDetailMap.put("REAL_QTY","");
			itemDetailMap.put("ITEM_TEXT","");
			itemDetailMap.put("RETURN_PEOPLE",currentUser.get("USERNAME"));
			itemDetailMap.put("MEMO","");
			itemDetailMap.put("DEL","0");
			itemDetailMap.put("CREATOR",currentUser.get("USERNAME"));
			itemDetailMap.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
			itemDetailMap.put("EDITOR","");
			itemDetailMap.put("EDIT_DATE","");

			wmsReturnGoodsDao.insertWmsOutReturnItem(itemMap);
			wmsReturnGoodsDao.insertWmsOutReturnItemDetail(itemDetailMap);
			RETURN_ITEM_NO++;
		}
		return outNo;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String wareHouseOutPickup(Map<String, Object> params) {
		String pickupNo = "";
		Map<String, Object> params2 = new HashMap <String, Object>();
		params2.put("WERKS", params.get("WERKS").toString());
		params2.put("WMS_DOC_TYPE", "11");
		pickupNo = wmscDocNoService.getDocNo(params2).get("docno").toString();
		if(null == pickupNo) {
			return "-1";
		}
		
		//下架数据写入【WMS拣配下架表--- WMS_OUT_PICKING】
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		//int PICK_ITEM_NO = 1;
		for(int j=0;j<jarr.size();j++){
			JSONObject trData=  jarr.getJSONObject(j);
			/*Map<String, Object> params_pickup = new HashMap <String, Object>();
			params_pickup.put("PICK_NO", pickupNo);
			params_pickup.put("PICK_ITEM_NO", PICK_ITEM_NO);
			params_pickup.put("REF_BUSINESS_NO", trData.get("RETURN_NO"));
			params_pickup.put("REF_BUSINESS_ITEM_NO", trData.get("RETURN_ITEM_NO"));
			//params_pickup.put("BUSINESS_CODE", pickupNo);
			params_pickup.put("BUSINESS_NAME", trData.get("BUSINESS_NAME"));
			params_pickup.put("BUSINESS_TYPE", trData.get("BUSINESS_TYPE"));
			params_pickup.put("WERKS", params.get("WERKS").toString());
			params_pickup.put("WH_NUMBER", params.get("WH_NUMBER").toString());
			params_pickup.put("REQ_ITEM_STATUS", "01");
			params_pickup.put("ITEM_TEXT", "");
			params_pickup.put("DEL", "");
			params_pickup.put("MATNR", trData.get("MATNR"));
			params_pickup.put("MAKTX", trData.get("MAKTX"));
			params_pickup.put("UNIT", trData.get("UNIT"));
			params_pickup.put("LIFNR", trData.get("LIFNR"));
			params_pickup.put("BIN_CODE", trData.get("BIN_CODE"));
			params_pickup.put("LGORT", trData.get("LGORT"));
			params_pickup.put("BIN_CODE_XJ", "BBBB");
			params_pickup.put("QTY", trData.get("QTY1"));
			params_pickup.put("QTY_LOCK", "");
			params_pickup.put("BATCH", trData.get("BATCH"));
			params_pickup.put("HANDOVER_QTY", "");
			params_pickup.put("HANDOVER_QTY_LOCK", "");
			params_pickup.put("LGORT_RECEIVE", "");
			params_pickup.put("SOBKZ", trData.get("SOBKZ"));
			params_pickup.put("HX_FLAG", "0");
			params_pickup.put("RECEIVER", "");
			params_pickup.put("HANDOVER_DATE", "");
			params_pickup.put("CREATOR", currentUser.get("USERNAME"));
			params_pickup.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
			params_pickup.put("EDITOR", "");
			params_pickup.put("EDIT_DATE", "");
			//获取【实时冻结数量】
			params_pickup.put("S_ID", trData.get("S_ID").toString());
			int realFreezeQty = wmsReturnGoodsDao.getStockFreezeQtyByID(params_pickup);
			if(Float.valueOf(trData.get("QTY1").toString()) > Float.valueOf(realFreezeQty)) {
				return "-2";
			}*/
			//YK190613  取消OutPickup表
			//wmsReturnGoodsDao.insertOutPickupData(params_pickup);		
			//PICK_ITEM_NO++;
			//将拣配下架数量写入【退货单行项目明细表--- WMS_OUT_RETURN_ITEM_D】，
			Map<String, Object> paramsUpdate = new HashMap <String, Object>();
			paramsUpdate.put("DID", trData.get("D_ID"));
			paramsUpdate.put("XJ_QTY", trData.get("QTY1"));
			paramsUpdate.put("QTY", trData.get("QTY1"));
			paramsUpdate.put("BIN_CODE", trData.get("BIN_CODE"));
			paramsUpdate.put("BIN_CODE_XJ", trData.get("BIN_CODE_XJ"));
			int re = wmsReturnGoodsDao.updateOutReturnItemDXJQty(paramsUpdate);
			System.out.println("-->re = " + re + "|" + trData.get("QTY1").toString());
			//并更新【WMS库存表---WMS_CORE_STOCK】、【退货单抬头表--- WMS_OUT_RETURN_HEAD】、【退货单行项目表--- WMS_OUT_RETURN_ITEM】。
			paramsUpdate.put("RETURN_NO", trData.get("RETURN_NO"));
			wmsReturnGoodsDao.updateOutReturnHeadStatus(paramsUpdate);
			
			//9.3.5.2更新【WMS库存表---WMS_CORE_STOCK】库存表的“冻结数量（FREEZE_QTY）”（冻结数量减少）、
			//“下架数量（XJ_QTY）”（下架数量增加）和下架储位（BBBB）。			
			paramsUpdate.put("QTY", trData.get("QTY1"));	
			paramsUpdate.put("S_ID", trData.get("S_ID"));
			paramsUpdate.put("XJ_BIN_CODE", trData.get("BIN_CODE_XJ"));
			wmsReturnGoodsDao.updateStockQty(paramsUpdate);
			//【退货单行项目表--- WMS_OUT_RETURN_ITEM】表的行项目状态（ITEM_STATUS），状态由“创建”状态更新为“下架”状态。
			paramsUpdate.put("IID", trData.get("I_ID"));
			paramsUpdate.put("ITEM_STATUS", "02");
			wmsReturnGoodsDao.updateOutReturnItemStatus(paramsUpdate);
		}
		
		return pickupNo;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String wareHouseOutPickupCancel(Map<String, Object> params) {
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		//9.3.6.1	更新【退货单行项目明细表--- WMS_OUT_RETURN_ITEM_D】，将取消下架的行项目数据的下架数量和下架储位清空。
		//9.3.6.2	更新【退货单行项目表--- WMS_OUT_RETURN_ITEM】，行项目表状态由“01：下架”更新为“00：创建”。
		//9.3.6.3	更新【WMS库存表---WMS_CORE_STOCK】的“冻结数量（FREEZE_QTY）”（冻结数量增加）、“下架数量（XJ_QTY）”（下架数量减少），清空下架储位（下架储位由BBBB更新为空）。
		//9.3.6.4	取消下架时只能整条记录取消，不允许修改下架数量。

		for(int j=0;j<jarr.size();j++){
			JSONObject trData=  jarr.getJSONObject(j);
			//9.3.6.1更新【WMS拣配下架表--- WMS_OUT_PICKING】，将取消下架的行项目数据打上删除标记（即：DEL字段写入X）。
			Map<String, Object> params2 = new HashMap <String, Object>();
			params2.put("REF_BUSINESS_NO", trData.get("RETURN_NO"));
			params2.put("REF_BUSINESS_ITEM_NO", trData.get("RETURN_ITEM_NO"));
			wmsReturnGoodsDao.delOutPickingData(params2);
			
			//9.3.6.2更新【退货单行项目表--- WMS_OUT_RETURN_ITEM】，行项目表状态由“01：下架”更新为“00：创建”。
			Map<String, Object> params3 = new HashMap <String, Object>();
			params3.put("ITEM_STATUS", "00");
			params3.put("IID", trData.get("I_ID"));
			wmsReturnGoodsDao.updateOutReturnItemStatus(params3);
			params3.put("DID", trData.get("D_ID"));
			params3.put("XJ_QTY", "0");
			params3.put("BIN_CODE_XJ", "");
			params3.put("RETURN_NO", trData.get("RETURN_NO"));
			wmsReturnGoodsDao.updateOutReturnItemDXJQty(params3);
			wmsReturnGoodsDao.updateOutReturnHeadStatus(params3);
			//9.3.6.3更新【WMS库存表---WMS_CORE_STOCK】的“冻结数量（FREEZE_QTY）”（冻结数量增加）、“下架数量（XJ_QTY）”（下架数量减少），
			//清空下架储位（下架储位由BBBB更新为空）。
			Map<String, Object> params4 = new HashMap <String, Object>();
			params4.put("S_ID", trData.get("S_ID"));
			params4.put("QTY", 0-Float.valueOf(trData.get("QTY1").toString()));
			params4.put("XJ_BIN_CODE", "");
			wmsReturnGoodsDao.updateStockQty(params4);
		}
		return "0";
	}
	
	@Override
	public String getTestFlagByWerksBusinessCode(Map<String, Object> params) {
		return wmsReturnGoodsDao.getTestFlagByWerksBusinessCode(params);
	}
	
	@Override
	public List<Map<String, Object>> getReceiveRoomOutReturnData(Map<String, Object> params){
		return wmsReturnGoodsDao.getReceiveRoomOutReturnData(params);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public String confirmReceiveRoomOutReturn(Map<String, Object> params) {
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());

		//5.3.8.1退货单行项目更新为”已完成“状态，并同步更新【收货单表】中的可退数量和退货数量（退货数量增加，可退数量减少）,
		//更新收料房库[WMS_IN_RH_STOCK]存数量（收料房库存数量减少，当收料房库存更新为0时，删除该行数据），
		//同步更新【退货单抬头表WMS_OUT_RETURN_HEAD】和【退货单行项目表WMS_OUT_RETURN_ITEM】的状态和实际退货数量。
		Map<String, Object> paramsMap = new HashMap <String, Object>();
		//paramsMap.put("HID", jarr.getJSONObject(0).getString("HID"));
		paramsMap.put("RETURN_NO", jarr.getJSONObject(0).getString("RETURN_NO"));
		paramsMap.put("RETURN_STATUS", "03");
		wmsReturnGoodsDao.updateReceiveRoomOutReturnHeadStatus(paramsMap);	
		wmsReturnGoodsDao.updateWareHouseReturnItemStatus(paramsMap);
		boolean barcodeFlag = false;			

		//List<Map<String,Object>> tpoParamsList = new ArrayList<Map<String,Object>>();
		if("X".equals(jarr.getJSONObject(0).get("BARCODE_FLAG"))) {
			barcodeFlag = true;
		}
		
		for(int i=0;i<jarr.size();i++){
			Map<String, Object> itemParamsMap = new HashMap <String, Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			itemParamsMap.put("IID", itemData.getString("IID"));
			itemParamsMap.put("DID", itemData.getString("DID"));
			itemParamsMap.put("ITEM_STATUS", "03");
			itemParamsMap.put("REAL_QTY", itemData.getString("QTY2"));
			//wmsReturnGoodsDao.updateOutReturnItemStatus(itemParamsMap);
			wmsReturnGoodsDao.updateOutReturnItemDRealQty(itemParamsMap);
			

			//ModBy:YK 190422
			//5.3.8.2	针对工厂+仓库号在【仓库配置表-WMS_C_WH】中是否启用条码管理-BARCODE_FLAG =X（启用条码） 时 ，
			//需要用“工厂+仓库号+料号+源批次+WMS批次+特殊库存类型+供应商代码+收货单号+收货单行项目号” 去【条码表-WMS_CORE_LABEL】中
			//找到对应的状态为可退货且DEL≠X的条码号LABEL_NO 更新为 收料房退货状态05，并产生条码操作记录，退货的条码更新至退货单行项目还是退货单明细表？
			if(barcodeFlag) {
				Map<String,Object> parMap = new HashMap<String,Object>();
				List<String> LABELS = new ArrayList<String>();
				if(itemData.getString("LABEL_NO") != null) {
					for (String label: itemData.getString("LABEL_NO").split(",")){
						LABELS.add(label);
					}
				}
				String LABEL_STATUS = "05";
				if("04".equals(itemData.getString("BUSINESS_NAME"))) {
					LABEL_STATUS = "05";
				}else if("05".equals(itemData.getString("BUSINESS_NAME"))) {
					LABEL_STATUS = "06";
				}
				if(LABELS.size() > 0) {	
					parMap.put("LABELS", LABELS);
					parMap.put("LABEL_STATUS", LABEL_STATUS);
					wmsReturnGoodsDao.updateLabelsStatus(parMap);
				}
			}
			
			
			itemParamsMap.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
			itemParamsMap.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
			//更新 WMS收货单WMS_IN_RECEIPT
			wmsReturnGoodsDao.updateReceipt(itemParamsMap);
			//更新WMS库存表WMS_IN_RH_STOCK
			itemParamsMap.put("WERKS", itemData.getString("WERKS"));
			itemParamsMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			itemParamsMap.put("MATNR", itemData.getString("MATNR"));
			itemParamsMap.put("BATCH", itemData.getString("BATCH"));
			itemParamsMap.put("LGORT", itemData.getString("LGORT"));
			itemParamsMap.put("SOBKZ", itemData.getString("SOBKZ"));
			itemParamsMap.put("LIFNR", itemData.getString("LIFNR"));
			itemParamsMap.put("F_BATCH", itemData.getString("F_BATCH"));
			//确认数量不能大于库存数量
			int rh_qty = wmsReturnGoodsDao.getRHStockRhQty(itemParamsMap);
			if( Float.valueOf(itemData.getString("QTY2").toString()).intValue() > rh_qty) {
				throw new RuntimeException("料号" +itemData.getString("MATNR").toString()+"数量不能大于库存数量！");
			}
			//当收料房库存更新为0 时，需要硬删收料房库存。
			wmsReturnGoodsDao.deleteRHZeroStock(itemParamsMap);
			wmsReturnGoodsDao.updateRHStock(itemParamsMap);
			
			if("03".equals(itemData.getString("BUSINESS_TYPE"))) {
				//5.3.8.3针对退货业务对应收货业务类型为PO（A）的，退货成功后，还需同步更新相应的核销信息表【WMS_HX_PO---PO核销信息】
				//的实退124数量（实退124数量增加）及剩余核销数量（剩余核销数量增加）。
				WmsHxPoEntity wmsHxPoEntity = new WmsHxPoEntity();
				wmsHxPoEntity.setEbeln(itemData.getString("PO_NO"));
				wmsHxPoEntity.setEbelp(itemData.getString("PO_ITEM_NO"));
				wmsHxPoEntity.setSs124(Double.valueOf(itemData.getString("QTY2")));
				wmsHxPoEntity.setHxQty(Double.valueOf(itemData.getString("QTY2")));
				wmsAccountReceiptHxPoService.updateByPoNo(wmsHxPoEntity);
			}else if("07".equals(itemData.getString("BUSINESS_TYPE"))) {
				//针对退货业务对应收货业务类型为STO交货单（A）的  ，退货成功后，还需同步更新相应的核销信息表【WMS_HX_DN--- SAP交货单核销信息】
				//的实退124数量（实退124数量增加）及剩余核销数量（剩余核销数量增加）。
				WmsHxDnEntity wmsHxDnEntity = new WmsHxDnEntity();
				wmsHxDnEntity.setVbeln(itemData.getString("SAP_OUT_NO"));
				wmsHxDnEntity.setPosnr(itemData.getString("SAP_OUT_ITEM_NO"));
				wmsHxDnEntity.setSs124t(Double.valueOf(itemData.getString("QTY2")));
				wmsHxDnEntity.setHxQtyXs(Double.valueOf(itemData.getString("QTY2")));				
				wmsAccountReceiptHxDnService.updateByDnNo(wmsHxDnEntity);
			}else if("09".equals(itemData.getString("BUSINESS_TYPE"))) {
				//5.3.8.5针对退货业务类型对应的收货业务类型为303调拨单（A）的，退货成功后，还需同步更新相应的核销信息表【WMS_HX_TO--- 303调拨单核销信息】
				//的实收304DB数量及剩余核销数量(实收304DB数量增加，剩余核销数量增加)。
				
			}

			//5.3.8.2针对WMS业务类型为SCM送货单类的退货单，还需同时调取SCM接口更新SCM送货单的可交货数量（可交货数量增加）。
			//不同数据源不能放在一个事务内处理
			/*if("01".equals(itemData.getString("BUSINESS_TYPE"))) {
				Map<String, Object> tpo = new HashMap <String, Object>();
				tpo.put("PO_NO", itemData.getString("PO_NO"));
				tpo.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
				tpo.put("DELIVERYAMOUNT", itemData.getString("QTY2"));
				tpoParamsList.add(tpo);
			}*/
		}
		
		//if(tpoParamsList.size()>0)scmDeliveryService.updateTPO(tpoParamsList);
		
		/**
		 * 5.3.8.6系统根据相应的业务类型产生WMS凭证、SAP凭证，WMS凭证存储到【WMS凭证表_抬头】和【WMS凭证表_明细】表，
		 * SAP凭证存储到【SAP凭证表-抬头】和【SAP凭证表-明细】中，存储凭证类型为“标准凭证”， 【WMS凭证表_明细】中的“SAP过账标识”
		 * 根据退货业务对应的WMS业务类型判断该次业务是否需要过账SAP及是同步过账还是异步过账，
		 * 如需过账，写入“01过账”，如无需过账写入“00无需过账”，如是异步过账则写入“02 异步过账”。
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
    	txtMap.put("WERKS", params.get("WERKS").toString());
    	txtMap.put("BUSINESS_NAME",jarr.getJSONObject(0).get("BUSINESS_NAME").toString());
    	txtMap.put("JZ_DATE", params.get("JZ_DATE").toString());
    	txtMap.put("RETURN_NO", params.get("RETURN_NO").toString());
    	txtMap.put("VENDOR_MANAGER", (jarr.getJSONObject(0).get("VENDOR_MANAGER") == null)?"":jarr.getJSONObject(0).get("VENDOR_MANAGER").toString());
    	//txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);	
		

		//系统关联查询【退货单行项目表】、【工厂业务类型配置表】和【WMS业务类型表】确认该【退货单行项目】所对应的WMS业务类型是否需要过账SAP
		String SAP_MOVE_TYPE = (jarr.getJSONObject(0).getString("SAP_MOVE_TYPE") == null)?"":jarr.getJSONObject(0).getString("SAP_MOVE_TYPE");
		logger.info("-->SAP_MOVE_TYPE = " + SAP_MOVE_TYPE);
		
		Map<String,Object> head = new HashMap<String,Object>();
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		head.put("WERKS", params.get("WERKS"));
		head.put("PZ_DATE", params.get("PZ_DATE"));
		head.put("JZ_DATE", params.get("JZ_DATE"));
		head.put("HEADER_TXT", txt.get("txtrule"));
		head.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		head.put("TYPE", "00");
		Map<String,Object> currentUser = userUtils.getUser();
		head.put("CREATOR",currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		head.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
		//head.put("BUSINESS_CODE", params.get("PZ_DATE"));
		for(int i=0;i<jarr.size();i++){
			Map<String,Object> item = new HashMap<String,Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			//针对外购退货和调拨退货产生的凭证，【WMS凭证表_明细】中的“是否可冲销标识（REVERSAL_FLAG）”写入X，即：该类凭证不允许冲销；
			//针对STO退货，【WMS凭证表_明细】中的“是否可冲销标识（REVERSAL_FLAG）”和“是否可取消标识（CANCEL_FLAG）”均写入X，即：该类凭证不允许冲销和取消。
			String REVERSAL_FLAG = "0";
			String CANCEL_FLAG = "0";
			if("25".equals(itemData.getString("BUSINESS_NAME"))) {
				//REVERSAL_FLAG = "X";
			}else if("26".equals(itemData.getString("BUSINESS_NAME"))) {
				//REVERSAL_FLAG = "X";
			}else if("27".equals(itemData.getString("BUSINESS_NAME"))) {
				REVERSAL_FLAG = "X";
				CANCEL_FLAG = "X";
			}
			item.put("BUSINESS_CODE", itemData.getString("BUSINESS_CODE"));
			item.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			item.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			item.put("BUSINESS_CLASS", itemData.getString("BUSINESS_CLASS"));
			item.put("WMS_MOVE_TYPE", itemData.getString("WMS_MOVE_TYPE"));
			item.put("SAP_MOVE_TYPE", itemData.getString("SAP_MOVE_TYPE"));
			item.put("REVERSAL_FLAG", REVERSAL_FLAG);
			item.put("CANCEL_FLAG", CANCEL_FLAG);
			item.put("ITEM_TEXT", txt.get("txtruleitem"));
			item.put("SOBKZ", itemData.getString("SOBKZ"));
			item.put("F_WERKS", itemData.getString("F_WERKS"));
			item.put("MATNR", itemData.getString("MATNR"));
			item.put("MAKTX", itemData.getString("MAKTX"));
			item.put("F_BATCH", itemData.getString("F_BATCH"));
			item.put("WERKS", itemData.getString("WERKS"));
			item.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			item.put("LGORT", itemData.getString("LGORT"));
			item.put("UNIT", itemData.getString("UNIT"));
			item.put("QTY_WMS", itemData.getString("QTY2"));
			item.put("QTY_SAP", itemData.getString("QTY2"));
			//MODBY:YK 190520 BUG503 收料房退货-外购退货：过账成功后供应商信息需存入【WMS凭证表-明细】中
			item.put("LIFNR", itemData.getString("LIFNR"));
			item.put("LIKTX", itemData.getString("LIKTX"));
			item.put("ASNNO", itemData.getString("ASNNO"));
			item.put("ASNITM", itemData.getString("ASNITM"));
			item.put("BATCH", itemData.getString("BATCH"));
			item.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
			item.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
			item.put("PO_NO", itemData.getString("PO_NO"));
			item.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
			item.put("IO_NO", itemData.getString("IO_NO"));
			item.put("WBS", itemData.getString("WBS"));
			item.put("MO_NO", itemData.getString("MO_NO"));
			item.put("MO_ITEM_NO", itemData.getString("MO_ITEM_NO"));
			item.put("RSNUM", itemData.getString("RSNUM"));
			item.put("RSPOS", itemData.getString("RSPOS"));
			item.put("LABEL_NO", itemData.getString("LABEL_NO"));
			item.put("SAP_OUT_NO", itemData.getString("SAP_OUT_NO"));
			item.put("SAP_OUT_ITEM_NO", itemData.getString("SAP_OUT_ITEM_NO"));
			item.put("RETURN_NO", itemData.getString("RETURN_NO"));
			item.put("RETURN_ITEM_NO", itemData.getString("RETURN_ITEM_NO"));
			item.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
			item.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));	
			item.put("REF_SAP_MATDOC_NO", itemData.getString("REF_SAP_MATDOC_NO"));
			item.put("REF_SAP_MATDOC_ITEM_NO", itemData.getString("REF_SAP_MATDOC_ITEM_NO"));
			item.put("REF_SAP_MATDOC_YEAR", itemData.getString("REF_SAP_MATDOC_YEAR"));		
			item.put("XCHPF", itemData.getString("XCHPF"));		
			itemList.add(item);
		}
		
		String WMS_NO = "";
		List<Map<String,Object>> itemListSap = new ArrayList<Map<String,Object>>();
		if("27".equals(itemList.get(0).get("BUSINESS_NAME").toString())) {
			//BUG1002:由于STO退货交货单每个行项目只能过账一次，所以需要汇总累加同交货单行项目的数量，过账到SAP，还需要判断料号在SAP是否启用了批次
			//（用工厂代码和料号查询WMS_SAP_MATERIAL字段XCHPF=X，启用了批次），如果启用了批次，还需判断这个料号对应的同交货单和行项目的数据批次必须一致，
			//并传输这个批次到SAP过账。
			String poStr = "";
			Map<String, Object> sto_result = new HashMap<String, Object>();
			
			String deliveryNO = itemList.get(0).get("SAP_OUT_NO").toString();
			try {
				sto_result = wmsSapRemote.getSapBapiDeliveryGetlist(deliveryNO);
			}catch(Exception e){
				throw new RuntimeException("调用SAP接口查询失败！");
			}
			for(int i=0;i<itemList.size();i++){
				if(poStr.equals(itemList.get(i).get("SAP_OUT_NO").toString() + itemList.get(i).get("SAP_OUT_ITEM_NO").toString())) {
					float qtySap = 0;
					qtySap = Float.valueOf(itemListSap.get((itemListSap.size())-1).get("QTY_SAP").toString()) + Float.valueOf(itemList.get(i).get("QTY_SAP").toString());
					itemListSap.get((itemListSap.size())-1).put("QTY_SAP", qtySap);
					itemListSap.get((itemListSap.size())-1).put("QTY_WMS", qtySap);
				}else {
					itemListSap.add(itemList.get(i));
				}
				poStr = itemList.get(i).get("SAP_OUT_NO").toString() + itemList.get(i).get("SAP_OUT_ITEM_NO").toString();
			}
			
			List<Map<String,Object>> sto_itemList = (List<Map<String, Object>>)sto_result.get("item");
			for(int i=0;i<sto_itemList.size();i++){
				//logger.info("-->POSNR : " + sto_itemList.get(i).get("POSNR").toString());
				boolean check = false;
				for(int j=0;j<itemListSap.size();j++){
					String stostr = sto_itemList.get(i).get("VBELN").toString() + sto_itemList.get(i).get("POSNR").toString();
					if (stostr.equals(itemListSap.get(j).get("SAP_OUT_NO").toString() + itemListSap.get(j).get("SAP_OUT_ITEM_NO").toString())) {
						check = true;
						//针对STO类型，SAP过帐时取SAP 接口返回 的库位信息
						itemListSap.get(j).put("SAP_LGORT", sto_itemList.get(i).get("LGORT").toString());
					}
				}
				if(!check) {
					logger.info("-->VGBEL POSNR checked: " + sto_itemList.get(i).get("VGBEL").toString() + "|" + sto_itemList.get(i).get("POSNR").toString());
					Map<String,Object> item = new HashMap<String,Object>();
					item.put("BUSINESS_CODE", itemListSap.get(0).get("BUSINESS_CODE"));
					item.put("BUSINESS_NAME", itemListSap.get(0).get("BUSINESS_NAME"));
					item.put("BUSINESS_TYPE", itemListSap.get(0).get("BUSINESS_TYPE"));
					item.put("BUSINESS_CLASS", itemListSap.get(0).get("BUSINESS_CLASS"));
					item.put("WMS_MOVE_TYPE", itemListSap.get(0).get("WMS_MOVE_TYPE"));
					item.put("SAP_MOVE_TYPE", itemListSap.get(0).get("SAP_MOVE_TYPE"));
					item.put("REVERSAL_FLAG", "X");
					item.put("CANCEL_FLAG", "X");
					item.put("ITEM_TEXT", "");
					item.put("SOBKZ", itemListSap.get(0).get("SOBKZ"));
					item.put("F_WERKS", itemListSap.get(0).get("F_WERKS"));
					item.put("MATNR", sto_itemList.get(i).get("MATNR").toString());
					item.put("MAKTX", sto_itemList.get(i).get("ARKTX").toString());
					item.put("F_BATCH", "");
					item.put("WERKS", itemListSap.get(0).get("WERKS"));
					item.put("WH_NUMBER", itemListSap.get(0).get("WH_NUMBER"));
					item.put("LGORT", itemListSap.get(0).get("LGORT"));
					item.put("SAP_LGORT", sto_itemList.get(i).get("LGORT").toString());
					item.put("UNIT", sto_itemList.get(i).get("VRKME").toString());
					item.put("QTY_WMS", "0");
					item.put("QTY_SAP", "0");		//BUG#1240 保存凭证提示数据类型不正确
					item.put("LIFNR", itemListSap.get(0).get("LIFNR"));
					item.put("LIKTX", itemListSap.get(0).get("LIKTX"));
					item.put("BATCH", sto_itemList.get(i).get("CHARG").toString());
					item.put("RECEIPT_NO", itemListSap.get(0).get("RECEIPT_NO"));
					item.put("RECEIPT_ITEM_NO", itemListSap.get(0).get("RECEIPT_ITEM_NO"));
					item.put("PO_NO", itemListSap.get(0).get("PO_NO"));
					item.put("PO_ITEM_NO", "");
					item.put("IO_NO", itemListSap.get(0).get("IO_NO"));
					item.put("WBS", itemListSap.get(0).get("WBS"));
					item.put("MO_NO", itemListSap.get(0).get("MO_NO"));
					item.put("MO_ITEM_NO", "");
					item.put("RSNUM", itemListSap.get(0).get("RSNUM"));
					item.put("RSPOS", "");
					item.put("LABEL_NO", itemListSap.get(0).get("LABEL_NO"));
					item.put("SAP_OUT_NO", sto_itemList.get(i).get("VBELN").toString());
					item.put("SAP_OUT_ITEM_NO", sto_itemList.get(i).get("POSNR").toString());
					item.put("RETURN_NO", itemListSap.get(0).get("RETURN_NO"));
					item.put("RETURN_ITEM_NO", "");
					item.put("CREATOR", currentUser.get("USERNAME"));
					item.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));	
					item.put("REF_SAP_MATDOC_NO", itemListSap.get(0).get("REF_SAP_MATDOC_NO"));
					item.put("REF_SAP_MATDOC_ITEM_NO", "");
					item.put("REF_SAP_MATDOC_YEAR", itemListSap.get(0).get("REF_SAP_MATDOC_YEAR"));		
					item.put("XCHPF", itemListSap.get(0).get("XCHPF"));		
					itemListSap.add(item);						
				}
			}
			
			WMS_NO=commonService.saveWMSDoc(head,itemListSap);
		}else {
			WMS_NO=commonService.saveWMSDoc(head,itemList);
		}
		
		//String WMS_NO=commonService.saveWMSDoc(head,itemList);
		
		//SAP过帐
		String SAP_NO = "";
		if(!SAP_MOVE_TYPE.equals("")) {	//需过帐SAP
			//库存调拨 sapGoodsmvtCreateMB04
			//STO 按采购订单收货过账 sapGoodsmvtCreateMB01
			//外购退货：交货单过账 sapDeliveryUpdate

			params.put("BUSINESS_NAME", jarr.getJSONObject(0).getString("BUSINESS_NAME"));
			params.put("BUSINESS_TYPE", jarr.getJSONObject(0).getString("BUSINESS_TYPE"));
			params.put("BUSINESS_CLASS", jarr.getJSONObject(0).getString("BUSINESS_CLASS"));
			params.put("WMS_NO", WMS_NO);
			
			if("27".equals(itemList.get(0).get("BUSINESS_NAME").toString())) {
				for(int i=0;i<itemListSap.size();i++){
					//itemListSap.get(i).put("LGORT", itemListSap.get(i).get("SAP_LGORT"));
				}
				params.put("matList", itemListSap);
			}else {
				params.put("matList", itemList);
			}
			
			try {
				SAP_NO= "<br/>SAP凭证号：" + commonService.doSapPost(params);
			}catch (Exception e) {
				//SAP_NO=e.getMessage();
				throw new RuntimeException(e.getMessage());
			}
		}
		return "WMS凭证号：" + WMS_NO + SAP_NO;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public String confirmWareHouseOutReturn(Map<String, Object> params){
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		Map<String,Object> currentUser = userUtils.getUser();
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			Map<String,Object> par = new HashMap<String,Object>();
			//10.3.9.3更新【拣配下架表】的状态和交接下架数量（HANDOVER_QTY），状态更新为“已交接”。
			par.put("QTY1", itemData.get("QTY1"));
			par.put("P_ID", itemData.get("P_ID"));
			//wmsReturnGoodsDao.updateOutPickingHandoverQty(par);
			//10.3.9.2更新【退货单抬头表】和【退货单行项目表】，抬头表状态更新为“01：完成”，行项目表状态更新为“02：完成”，
			//并在“实际退货数量（REAL_RETURN_QTY）”字段写入实际退货确认数量。
			par.put("IID", itemData.get("IID"));
			wmsReturnGoodsDao.updateOutReturnItemRealReturnQty(par);
			//10.3.9.4更新【WMS库存表】的冻结数量和下架数量，下架数量减少（退货数量），冻结数量增加（退货数量-下架数量）。
			par.put("WERKS", itemData.get("WERKS"));
			par.put("MATNR", itemData.get("MATNR"));
			par.put("SOBKZ", itemData.get("SOBKZ"));
			par.put("BATCH", itemData.get("BATCH"));
			par.put("RETURN_QTY", itemData.get("RETURN_QTY"));
			wmsReturnGoodsDao.updateStockXJFreezeQty(par);
			
			par.put("REAL_QTY", itemData.get("RETURN_QTY"));
			par.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
			par.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
			//针对122类退货和产成品退车间类退货页面，确认还需同步更新【WMS收货单表】的库房冲销/退货数量字段（数量增加）。
			if("28".equals(itemData.getString("BUSINESS_NAME"))){
				wmsReturnGoodsDao.updateReceipt(par);
			}
			
			//ModBy:YK190426
			//10.3.9.6	如退出库存的储位对应的存储区【仓库储位-WMS_CORE_WH_BIN】【仓库存储区-WMS_CORE_WH_AREA】需要进行库容检查的
			//STORAGE_CAPACITY_FLAG=X，需要将退出库存的存储单元更新存储区的占用存储单元-U_STORAGE_UNIT。
			//退出库存的存储单元=退出库存数量÷【WMS_C_MAT_STORAGE】-QTY 非整数时向上取整 , 占用存储单元-U_STORAGE_UNIT减少。
			if("X".equals(itemData.get("STORAGE_CAPACITY_FLAG").toString())) {
				int u_num = 0;
				if(!"0".equals(itemData.get("STORAGE_QTY").toString())) {
					u_num = (int) Math.ceil(Integer.valueOf(itemData.get("QTY1").toString()) / Integer.valueOf(itemData.get("STORAGE_QTY").toString()));
					Map<String, Object> par2 = new HashMap<String,Object>();
					par2.put("U_QTY", u_num);
					par2.put("WH_NUMBER", itemData.get("WH_NUMBER"));
					par2.put("STORAGE_AREA_CODE", itemData.get("STORAGE_AREA_CODE"));
					par2.put("BIN_CODE", itemData.get("BIN_CODE"));
					wmsReturnGoodsDao.updateWhBinStorageUnit(par2);
				}
			}
		}
		
		
		//状态统一更新
		Map<String,Object> par = new HashMap<String,Object>();
		par.put("RETURN_NO", jarr.getJSONObject(0).get("RETURN_NO"));
		wmsReturnGoodsDao.updateWareHouseReturnHeadStatus(par);
		wmsReturnGoodsDao.updateWareHouseReturnItemStatus(par);
		wmsReturnGoodsDao.updateWareHouseReturnPickingStatus(par);
		
		Map<String,String>txtMap=new HashMap<String,String>();
    	txtMap.put("WERKS", params.get("WERKS").toString());
    	txtMap.put("BUSINESS_NAME",jarr.getJSONObject(0).get("BUSINESS_NAME").toString());
    	txtMap.put("JZ_DATE", params.get("JZ_DATE").toString());
    	txtMap.put("RETURN_NO", params.get("RETURN_NO").toString());
    	txtMap.put("VENDOR_MANAGER", (jarr.getJSONObject(0).get("VENDOR_MANAGER") == null)?"":jarr.getJSONObject(0).get("VENDOR_MANAGER").toString());
    	txtMap.put("FULL_NAME", (jarr.getJSONObject(0).get("FULL_NAME") == null)?"":jarr.getJSONObject(0).get("FULL_NAME").toString());
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
    	
		//系统关联查询【退货单行项目表】、【工厂业务类型配置表】和【WMS业务类型表】确认该【退货单行项目】所对应的WMS业务类型是否需要过账SAP
		String SAP_MOVE_TYPE = jarr.getJSONObject(0).getString("SAP_MOVE_TYPE");
		logger.info("-->SAP_MOVE_TYPE = " + SAP_MOVE_TYPE);
				
		Map<String,Object> head = new HashMap<String,Object>();
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		head.put("WERKS", params.get("WERKS"));
		head.put("PZ_DATE", params.get("PZ_DATE"));
		head.put("JZ_DATE", params.get("JZ_DATE"));
		head.put("HEADER_TXT", txt.get("txtrule"));
		head.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		head.put("TYPE", "00");
		head.put("CREATOR",currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
		head.put("CREATE_DATE",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
		//head.put("BUSINESS_CODE", params.get("PZ_DATE"));
		
		for(int i=0;i<jarr.size();i++){
			Map<String,Object> item = new HashMap<String,Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			//针对STO退货业务产生的WMS凭证，【WMS凭证表_明细】中的“是否可冲销标识（REVERSAL_FLAG）”和“是否可取消标识（CANCEL_FLAG）”均写入X，即：该类凭证不允许冲销和取消。
			String REVERSAL_FLAG = "0";
			String CANCEL_FLAG = "0";
			/*if("27".equals(itemData.getString("BUSINESS_NAME"))) {
				REVERSAL_FLAG = "X";
				CANCEL_FLAG = "X";
			}*/
			item.put("BUSINESS_CODE", itemData.getString("BUSINESS_CODE"));
			item.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			item.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			item.put("BUSINESS_CLASS", itemData.getString("BUSINESS_CLASS"));
			item.put("WMS_MOVE_TYPE", itemData.getString("WMS_MOVE_TYPE"));
			item.put("REVERSAL_FLAG", REVERSAL_FLAG);
			item.put("CANCEL_FLAG", CANCEL_FLAG);
//			item.put("ITEM_TEXT", txt.get("txtruleitem"));
			//改为取页面传过来的， 解决用户在页面修改默认行文本无效问题
			item.put("ITEM_TEXT", itemData.getString("ITEM_TEXT"));
			item.put("SOBKZ", itemData.getString("SOBKZ"));
			item.put("F_WERKS", itemData.getString("F_WERKS"));
			item.put("MATNR", itemData.getString("MATNR"));
			item.put("MAKTX", itemData.getString("MAKTX"));
			item.put("F_BATCH", itemData.getString("F_BATCH"));
			item.put("WERKS", itemData.getString("WERKS"));
			item.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			item.put("LGORT", itemData.getString("LGORT"));
			item.put("UNIT", itemData.getString("UNIT"));
			item.put("QTY_WMS", itemData.getString("QTY1"));
			item.put("QTY_SAP", itemData.getString("QTY1"));
			item.put("LIFNR", itemData.getString("LIFNR"));
			item.put("BIN_CODE", itemData.getString("BIN_CODE"));
			item.put("BATCH", itemData.getString("BATCH"));
			item.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
			item.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
			item.put("PO_NO", itemData.getString("PO_NO"));
			item.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
			item.put("IO_NO", itemData.getString("IO_NO"));
			item.put("WBS", itemData.getString("WBS"));
			item.put("MO_NO", itemData.getString("MO_NO"));
			item.put("MO_ITEM_NO", itemData.getString("MO_ITEM_NO"));
			item.put("RSNUM", itemData.getString("RSNUM"));
			item.put("RSPOS", itemData.getString("RSPOS"));
			item.put("ASNNO", itemData.getString("ASNNO"));
			item.put("ASNITM", itemData.getString("ASNITM"));
			item.put("SAP_OUT_NO", itemData.getString("SAP_OUT_NO"));
			item.put("SAP_OUT_ITEM_NO", itemData.getString("SAP_OUT_ITEM_NO"));
			item.put("RETURN_NO", itemData.getString("RETURN_NO"));
			item.put("RETURN_ITEM_NO", itemData.getString("RETURN_ITEM_NO"));
			item.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
			item.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));			
			itemList.add(item);
		}		

		String WMS_NO = "";
		List<Map<String,Object>> itemListSap = new ArrayList<Map<String,Object>>();
		if("27".equals(itemList.get(0).get("BUSINESS_NAME").toString())) {
			//BUG1002:由于STO退货交货单每个行项目只能过账一次，所以需要汇总累加同交货单行项目的数量，过账到SAP，还需要判断料号在SAP是否启用了批次
			//（用工厂代码和料号查询WMS_SAP_MATERIAL字段XCHPF=X，启用了批次），如果启用了批次，还需判断这个料号对应的同交货单和行项目的数据批次必须一致，
			//并传输这个批次到SAP过账。
			String poStr = "";
			Map<String, Object> sto_result = new HashMap<String, Object>();
			
			String deliveryNO = itemList.get(0).get("SAP_OUT_NO").toString();
			try {
				sto_result = wmsSapRemote.getSapBapiDeliveryGetlist(deliveryNO);
			}catch(Exception e){
				throw new RuntimeException("调用SAP接口查询失败！");
			}
			for(int i=0;i<itemList.size();i++){
				if(poStr.equals(itemList.get(i).get("SAP_OUT_NO").toString() + itemList.get(i).get("SAP_OUT_ITEM_NO").toString())) {
					float qtySap = 0;
					qtySap = Float.valueOf(itemListSap.get((itemListSap.size())-1).get("QTY_SAP").toString()) + Float.valueOf(itemList.get(i).get("QTY_SAP").toString());
					itemListSap.get((itemListSap.size())-1).put("QTY_SAP", qtySap);
					itemListSap.get((itemListSap.size())-1).put("QTY_WMS", qtySap);
				}else {
					itemListSap.add(itemList.get(i));
				}
				poStr = itemList.get(i).get("SAP_OUT_NO").toString() + itemList.get(i).get("SAP_OUT_ITEM_NO").toString();
			}
			
			List<Map<String,Object>> sto_itemList = (List<Map<String, Object>>)sto_result.get("item");
			for(int i=0;i<sto_itemList.size();i++){
				//logger.info("-->POSNR : " + sto_itemList.get(i).get("POSNR").toString());
				boolean check = false;
				for(int j=0;j<itemListSap.size();j++){
					String stostr = sto_itemList.get(i).get("VBELN").toString() + sto_itemList.get(i).get("POSNR").toString();
					if (stostr.equals(itemListSap.get(j).get("SAP_OUT_NO").toString() + itemListSap.get(j).get("SAP_OUT_ITEM_NO").toString())) {
						check = true;
						//针对STO类型，SAP过帐时取SAP 接口返回 的库位信息
						itemListSap.get(j).put("SAP_LGORT", sto_itemList.get(i).get("LGORT").toString());
						itemListSap.get(j).put("MOVE_STLOC", "00ZJ");
					}
				}
				if(!check) {
					logger.info("-->VGBEL POSNR checked: " + sto_itemList.get(i).get("VGBEL").toString() + "|" + sto_itemList.get(i).get("POSNR").toString());
					Map<String,Object> item = new HashMap<String,Object>();
					item.put("BUSINESS_CODE", itemListSap.get(0).get("BUSINESS_CODE"));
					item.put("BUSINESS_NAME", itemListSap.get(0).get("BUSINESS_NAME"));
					item.put("BUSINESS_TYPE", itemListSap.get(0).get("BUSINESS_TYPE"));
					item.put("BUSINESS_CLASS", itemListSap.get(0).get("BUSINESS_CLASS"));
					item.put("WMS_MOVE_TYPE", itemListSap.get(0).get("WMS_MOVE_TYPE"));
					item.put("SAP_MOVE_TYPE", itemListSap.get(0).get("SAP_MOVE_TYPE"));
					item.put("REVERSAL_FLAG", "X");
					item.put("CANCEL_FLAG", "X");
					item.put("ITEM_TEXT", "");
					item.put("SOBKZ", itemListSap.get(0).get("SOBKZ"));
					item.put("F_WERKS", itemListSap.get(0).get("F_WERKS"));
					item.put("MATNR", sto_itemList.get(i).get("MATNR").toString());
					item.put("MAKTX", sto_itemList.get(i).get("ARKTX").toString());
					item.put("F_BATCH", "");
					item.put("WERKS", itemListSap.get(0).get("WERKS"));
					item.put("WH_NUMBER", itemListSap.get(0).get("WH_NUMBER"));
					item.put("LGORT", itemListSap.get(0).get("LGORT"));
					item.put("SAP_LGORT", sto_itemList.get(i).get("LGORT").toString());
					item.put("UNIT", sto_itemList.get(i).get("VRKME").toString());
					item.put("QTY_WMS", "0");
					item.put("QTY_SAP", "0");
					item.put("LIFNR", itemListSap.get(0).get("LIFNR"));
					item.put("LIKTX", itemListSap.get(0).get("LIKTX"));
					item.put("BATCH", sto_itemList.get(i).get("CHARG").toString());
					item.put("RECEIPT_NO", itemListSap.get(0).get("RECEIPT_NO"));
					item.put("RECEIPT_ITEM_NO", itemListSap.get(0).get("RECEIPT_ITEM_NO"));
					item.put("PO_NO", itemListSap.get(0).get("PO_NO"));
					item.put("PO_ITEM_NO", "");
					item.put("IO_NO", itemListSap.get(0).get("IO_NO"));
					item.put("WBS", itemListSap.get(0).get("WBS"));
					item.put("MO_NO", itemListSap.get(0).get("MO_NO"));
					item.put("MO_ITEM_NO", "");
					item.put("RSNUM", itemListSap.get(0).get("RSNUM"));
					item.put("RSPOS", "");
					item.put("LABEL_NO", itemListSap.get(0).get("LABEL_NO"));
					item.put("SAP_OUT_NO", sto_itemList.get(i).get("VBELN").toString());
					item.put("SAP_OUT_ITEM_NO", sto_itemList.get(i).get("POSNR").toString());
					item.put("RETURN_NO", itemListSap.get(0).get("RETURN_NO"));
					item.put("RETURN_ITEM_NO", "");
					item.put("CREATOR", currentUser.get("USERNAME"));
					item.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));	
					item.put("REF_SAP_MATDOC_NO", itemListSap.get(0).get("REF_SAP_MATDOC_NO"));
					item.put("REF_SAP_MATDOC_ITEM_NO", "");
					item.put("REF_SAP_MATDOC_YEAR", itemListSap.get(0).get("REF_SAP_MATDOC_YEAR"));		
					item.put("XCHPF", itemListSap.get(0).get("XCHPF"));	
					item.put("CUSTOMER", "");	
					itemListSap.add(item);						
				}
			}
			
			WMS_NO=commonService.saveWMSDoc(head,itemListSap);
		}else {
			WMS_NO=commonService.saveWMSDoc(head,itemList);
		}
		
		//String WMS_NO=commonService.saveWMSDoc(head,itemList);		
		
		String SAP_NO = "";
		if(!SAP_MOVE_TYPE.equals("")) {	//需过帐SAP
						
			params.put("BUSINESS_NAME", jarr.getJSONObject(0).getString("BUSINESS_NAME"));
			params.put("BUSINESS_TYPE", jarr.getJSONObject(0).getString("BUSINESS_TYPE"));
			params.put("BUSINESS_CLASS", jarr.getJSONObject(0).getString("BUSINESS_CLASS"));
			params.put("WMS_NO", WMS_NO);
			if("27".equals(itemList.get(0).get("BUSINESS_NAME").toString())) {
				for(int i=0;i<itemListSap.size();i++){
					if(itemListSap.get(i).get("SAP_LGORT") != null) {
						//itemListSap.get(i).put("LGORT", itemListSap.get(i).get("SAP_LGORT"));
					}
				}
				params.put("matList", itemListSap);
			}else {
				params.put("matList", itemList);
			}
			//SAP_NO= ";SAP_NO=" + commonService.doSapPost(params);
			try {
				SAP_NO= "<br/>SAP凭证号：" + commonService.doSapPost(params);
			}catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return "WMS_NO:" + WMS_NO + SAP_NO;
	}
	
	@Override
	public List<Map<String, Object>> getReceiveRoomOutReturnPrintData(Map<String, Object> params){		
		return wmsReturnGoodsDao.getReceiveRoomOutReturnPrintData(params);
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutReturnPrintData(Map<String, Object> params){
		return wmsReturnGoodsDao.getWareHouseOutReturnPrintData(params);
	}

	@Override
	public List<Map<String, Object>> getSapPoData(Map<String, Object> params){
		return wmsReturnGoodsDao.getSapPoData(params);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getWareHouseOutData27(Map<String, Object> params){
		logger.info("---->getWareHouseOutData27");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> outDataGroup = new ArrayList<Map<String, Object>>();	
		outDataGroup = (List<Map<String, Object>>) params.get("outDataGroup");
		int trid = 0;
		String mat = "";
		for(int i=0;i<outDataGroup.size();i++) {
			logger.info("-->MATNR:" + outDataGroup.get(i).get("MATNR"));
			mat = outDataGroup.get(i).get("MATNR").toString();
			float totalQty = 0;
			//统计同一料号不同行项目的情况 累计
			for(int j=0;j<outDataGroup.size();j++) {
				if(mat.equals(outDataGroup.get(j).get("MATNR").toString())) {
					totalQty += Float.valueOf(outDataGroup.get(j).get("LFIMG").toString());
				}
			}
			outDataGroup.get(i).put("totalQty", totalQty);
			
			//查询可退数量
			params.put("MATNR", mat);
			params.put("LIFNR", "VBYD" + outDataGroup.get(i).get("WERKS").toString());
			String KETUI = "0";
			List<Map<String, Object>> KetuiList = wmsReturnGoodsDao.getWmsKetuiQty27(params);
			if(KetuiList.size()>0) {
				logger.info("-->已创单数量 = " + KetuiList.get(0).get("QTY1"));
				logger.info("-->总冻结数量 = " + KetuiList.get(0).get("TOTALFREEZE"));
				//对比冻结数量和交货单数量，当冻结数量≤交货单数量时，可退数量=冻结数量-“00创建”状态的RETURN_QTY字段之和
				//（00创建状态退货数量取值逻辑：根据“工厂+料号+WMS批次+供应商代码”匹配退货数量，且删除标识≠X）；当冻结数量>交货单数量时，可退数量=交货单数量。
				if(Float.valueOf(KetuiList.get(0).get("TOTALFREEZE").toString()) <= totalQty) {
					KETUI = String.valueOf(Float.valueOf(KetuiList.get(0).get("TOTALFREEZE").toString()) -
					Float.valueOf(KetuiList.get(0).get("QTY1").toString()));					
				}else {
					KETUI = String.valueOf(totalQty);
				}
			}
			//VBELN SAP交货单 ；VGBEL：VGPOS采购订单  
			//VBELN-交货单号（10）POSNR-交货行项目（6）
			//VGBEL-参考单据的单据编号(10)   采购订单号   VGPOS-参考项目的项目号() 采购订单行项目号
			String VGBEL = outDataGroup.get(i).get("VGBEL").toString();
			String VGPOS = outDataGroup.get(i).get("VGPOS").toString();
			String VBELN = outDataGroup.get(i).get("VBELN").toString();//交货单号
			String POSNR = outDataGroup.get(i).get("POSNR").toString();//交货行项目（6）
			params.put("VBELN", VBELN);
			params.put("POSNR", POSNR);
			params.put("VGBEL", VGBEL);
			params.put("VGPOS", VGPOS);
			params.put("KETUI", KETUI);
			params.put("GROUP", "行项目:" + outDataGroup.get(i).get("POSNR") + "料号:" + outDataGroup.get(i).get("MATNR") + 
					"交货数量:" + outDataGroup.get(i).get("LFIMG") + "可退数量:" + KETUI);
			logger.info("-->行项目:" + outDataGroup.get(i).get("POSNR") + "料号:" + outDataGroup.get(i).get("MATNR") + 
					"交货数量:" + outDataGroup.get(i).get("LFIMG") + "可退数量:" + KETUI);
			
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			data = wmsReturnGoodsDao.getWareHouseOutData27(params);
			for(int j=0;j<data.size();j++) {
				data.get(j).put("COUNT",data.size());data.get(j).put("INDEX",(j+1));
				data.get(j).put("TRID",trid);trid++;
				result.add(data.get(j));
			}
		}
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutData28(Map<String, Object> params){
		logger.info("---->getWareHouseOutData28");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> outDataGroup = new ArrayList<Map<String, Object>>();	
		outDataGroup = wmsReturnGoodsDao.getWareHouseOutData28Group(params);
		int trid = 0;
		float kt_qty = 0;
		for(int i=0;i<outDataGroup.size();i++) {
			float FREEZE_QTY = Float.valueOf(outDataGroup.get(i).get("FREEZE_QTY").toString());
			if(!outDataGroup.get(i).get("S_UNIT").toString().equals(outDataGroup.get(i).get("MEINS").toString())) {
				FREEZE_QTY = FREEZE_QTY / (Float.valueOf(outDataGroup.get(i).get("UMREN").toString())/Float.valueOf(outDataGroup.get(i).get("UMREZ").toString()));
			}
			if(FREEZE_QTY <= Float.valueOf(outDataGroup.get(i).get("MENGE").toString())) {
				//outDataGroup.get(i).put("KETUI", Float.valueOf(outDataGroup.get(i).get("FREEZE_QTY").toString()) - Float.valueOf(outDataGroup.get(i).get("RETURN_QTY").toString()));				
				kt_qty = Float.valueOf(outDataGroup.get(i).get("FREEZE_QTY").toString()) - Float.valueOf(outDataGroup.get(i).get("RETURN_QTY").toString());
			}else {
				//outDataGroup.get(i).put("KETUI", Float.valueOf(outDataGroup.get(i).get("MENGE").toString()) - Float.valueOf(outDataGroup.get(i).get("QTY1").toString()));	
				kt_qty = Float.valueOf(outDataGroup.get(i).get("MENGE").toString()) - Float.valueOf(outDataGroup.get(i).get("QTY1").toString());
			}
			if(!outDataGroup.get(i).get("S_UNIT").toString().equals(outDataGroup.get(i).get("MEINS").toString())) {
				kt_qty = kt_qty / (Float.valueOf(outDataGroup.get(i).get("UMREN").toString())/Float.valueOf(outDataGroup.get(i).get("UMREZ").toString()));
				kt_qty = (float)(Math.round(kt_qty*1000))/1000;				
			}
			logger.info("-->行项目:" + outDataGroup.get(i).get("EBELP") + "料号:" + outDataGroup.get(i).get("MATNR") + 
					"订单数量:" + outDataGroup.get(i).get("MENGE") + "已创单数量:" + outDataGroup.get(i).get("QTY1") + 
					"可退数量:" + outDataGroup.get(i).get("KETUI") + "BATCHS:" + outDataGroup.get(i).get("BATCHS") + 
					"TOTAL_IN_QTY:" + outDataGroup.get(i).get("TOTAL_IN_QTY"));
			outDataGroup.get(i).put("KETUI",String.valueOf(kt_qty) + " " + outDataGroup.get(i).get("MEINS"));
			params.put("MATNR", outDataGroup.get(i).get("MATNR"));
			params.put("SOBKZ", outDataGroup.get(i).get("SOBKZ"));
			params.put("LIFNR", outDataGroup.get(i).get("LIFNR"));
			params.put("EBELP", outDataGroup.get(i).get("EBELP"));
			String BATCHS = outDataGroup.get(i).get("BATCHS").toString();
			BATCHS.replaceAll("'", "','");
			params.put("BATCHS", "('"+BATCHS+"')");
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			data = wmsReturnGoodsDao.getWareHouseOutData28(params);
			for(int j=0;j<data.size();j++) {
				data.get(j).put("KETUI",outDataGroup.get(i).get("KETUI"));
				data.get(j).put("COUNT",data.size());data.get(j).put("INDEX",(j+1));
				data.get(j).put("group", "行项目:" + outDataGroup.get(i).get("EBELP") + "料号:" + outDataGroup.get(i).get("MATNR") + 
					"订单数量:" + outDataGroup.get(i).get("MENGE") + "已创单数量:" + outDataGroup.get(i).get("QTY1") + 
					"可退数量:" + outDataGroup.get(i).get("KETUI"));
				data.get(j).put("TRID",trid);trid++;
				
				float qty1 = 0;
				float qtyf = 0;
				if(!data.get(j).get("UNIT").toString().equals(data.get(j).get("MEINS").toString())) {
					if(!(data.get(j).get("UMREN") == null || data.get(j).get("UMREZ") == null)) {
						qty1 = Float.valueOf(data.get(j).get("QTY1").toString()) / (Float.valueOf(data.get(j).get("UMREN").toString())/Float.valueOf(data.get(j).get("UMREZ").toString()));
						qty1 = (float)(Math.round(qty1*1000))/1000;
						data.get(j).put("QTY1",qty1);
						qtyf = Float.valueOf(data.get(j).get("FREEZE_QTY").toString()) / (Float.valueOf(data.get(j).get("UMREN").toString())/Float.valueOf(data.get(j).get("UMREZ").toString()));
						qtyf = (float)(Math.round(qtyf*1000))/1000;
						data.get(j).put("FREEZE_QTY",qtyf);
					}
				}
				
				result.add(data.get(j));				
			}
		}
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutData33(Map<String, Object> params){
		logger.info("---->getWareHouseOutData33");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> b_data = new ArrayList<Map<String, Object>>();
		b_data = wmsReturnGoodsDao.getWareHouseOutData33(params);
		for(int i=0;i<b_data.size();i++) {
			if(Float.valueOf(b_data.get(i).get("FREEZE_QTY").toString()) <= 
					Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
					Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
					Float.valueOf(b_data.get(i).get("CD_QTY").toString())
					) {
				b_data.get(i).put("QTY1", b_data.get(i).get("FREEZE_QTY"));
				b_data.get(i).put("KETUI", b_data.get(i).get("FREEZE_QTY"));
			}else {
				b_data.get(i).put("QTY1", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
				b_data.get(i).put("KETUI", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
			}
			if(Float.valueOf(b_data.get(i).get("IN_QTY").toString()) - 
				Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) - 
				Float.valueOf(b_data.get(i).get("CD_QTY").toString()) >0 ) {
					result.add(b_data.get(i));
			}
		}
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutData37(Map<String, Object> params){
		logger.info("---->getWareHouseOutData37");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> b_data = new ArrayList<Map<String, Object>>();
		b_data = wmsReturnGoodsDao.getWareHouseOutData37(params);
		for(int i=0;i<b_data.size();i++) {
			if(Float.valueOf(b_data.get(i).get("FREEZE_QTY").toString()) <= 
					Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
					Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
					Float.valueOf(b_data.get(i).get("CD_QTY").toString())
					) {
				b_data.get(i).put("QTY1", b_data.get(i).get("FREEZE_QTY"));
				b_data.get(i).put("KETUI", b_data.get(i).get("FREEZE_QTY"));
			}else {
				b_data.get(i).put("QTY1", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
				b_data.get(i).put("KETUI", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
			}
			if(Float.valueOf(b_data.get(i).get("IN_QTY").toString()) - 
				Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) - 
				Float.valueOf(b_data.get(i).get("CD_QTY").toString()) >0 ) {
					result.add(b_data.get(i));
			}
		}
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutData31(Map<String, Object> params){
		logger.info("---->getWareHouseOutData31");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		//8.3.2.2	系统根据用户输入的“工厂+仓库号+内部/CO订单+料号”关联查询【进仓单表】、
		//【WMS库存表--- WMS_CORE_STOCK】判断该内部/CO订单是否有对应的“进仓数量”和“冻结数量”，
		//（进仓数量-冲销/退货数量-已创单数量）>0且冻结数量>0即为存在符合条件的退货数据，如存在多条符合条件的数据则显示多条（批次字段：按照先进先出原则排序）。
		//8.3.2.3	关于库位：如用户在查询条件中输入库位则组合库位查询冻结库存，如未输入库位，则查询该工厂下所有库位是否存在符合条件的冻结库存。
		List<Map<String, Object>> b_data = new ArrayList<Map<String, Object>>();
		b_data = wmsReturnGoodsDao.getWareHouseOutData31(params);
		for(int i=0;i<b_data.size();i++) {
			if(Float.valueOf(b_data.get(i).get("FREEZE_QTY").toString()) <= 
					Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
					Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
					Float.valueOf(b_data.get(i).get("CD_QTY").toString())
					) {
				b_data.get(i).put("QTY1", b_data.get(i).get("FREEZE_QTY"));
				b_data.get(i).put("KETUI", b_data.get(i).get("FREEZE_QTY"));
			}else {
				b_data.get(i).put("QTY1", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
				b_data.get(i).put("KETUI", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
			}
			if(Float.valueOf(b_data.get(i).get("IN_QTY").toString()) - 
				Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) - 
				Float.valueOf(b_data.get(i).get("CD_QTY").toString()) >0 ) {
					result.add(b_data.get(i));
			}
		}
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutData34(Map<String, Object> params){
		logger.info("---->getWareHouseOutData34");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> b_data = new ArrayList<Map<String, Object>>();
		b_data = wmsReturnGoodsDao.getWareHouseOutData34(params);
		for(int i=0;i<b_data.size();i++) {
			if(Float.valueOf(b_data.get(i).get("FREEZE_QTY").toString()) <= 
					Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
					Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
					Float.valueOf(b_data.get(i).get("CD_QTY").toString())
					) {
				b_data.get(i).put("QTY1", b_data.get(i).get("FREEZE_QTY"));
				b_data.get(i).put("KETUI", b_data.get(i).get("FREEZE_QTY"));
			}else {
				b_data.get(i).put("QTY1", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
				b_data.get(i).put("KETUI", Float.valueOf(b_data.get(i).get("IN_QTY").toString()) -
						Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) -
						Float.valueOf(b_data.get(i).get("CD_QTY").toString()));
			}
			if(Float.valueOf(b_data.get(i).get("IN_QTY").toString()) - 
				Float.valueOf(b_data.get(i).get("QTY_CANCEL").toString()) - 
				Float.valueOf(b_data.get(i).get("CD_QTY").toString()) >0 ) {
					result.add(b_data.get(i));
			}
		}
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutData30(Map<String, Object> params){
		logger.info("---->getWareHouseOutData30");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String WERKS = params.get("WERKS").toString();
		String ponos = params.get("PO_NO").toString();
		List<String> po_list=Arrays.asList(ponos.split(","));
		for(int i=0;i<po_list.size();i++) {
			//改成直接查WMS_SAP_MO_HEAD【20181030】
			Map<String,Object> cdmap=new HashMap<String,Object>();
			cdmap.put("WERKS", WERKS);
			cdmap.put("AUFNR", po_list.get(i));
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			data = wmsReturnGoodsDao.getSapMoGroupForWareHouseOutData30(cdmap);
			
			for(int j=0;j<data.size();j++) {
				Map<String,Object> cdmap2=new HashMap<String,Object>();
				cdmap2.put("WERKS", WERKS);
				cdmap2.put("WH_NUMBER", params.get("WH_NUMBER").toString());
				cdmap2.put("MATNR", data.get(j).get("MATNR"));
				cdmap2.put("MO_NO", data.get(j).get("AUFNR"));
				cdmap2.put("MO_ITEM_NO", data.get(j).get("POSNR"));
				List<Map<String, Object>> b_data = new ArrayList<Map<String, Object>>();
				b_data = wmsReturnGoodsDao.getInboundDataForWareHouseOutData30(cdmap2);
				//（进仓数量-冲销/退货数量-已创单数量）>0 为存在符合条件的退货数据
				logger.info("size:" + b_data.size());
				for(int m=0;m<b_data.size();m++) {
					//5.3.3.4退货数量：必填字段，允许修改。当冻结数量≤（批次进仓数量-冲销/退货数量）时，退货数量默认等于冻结数量；
					//当冻结数量>（批次进仓数量-冲销/退货数量）时，退货数量默认=（批次进仓数量-冲销/退货数量）。
					
					//BUG 1055  190715 可退货数量：当冻结数量≤（批次进仓数量-冲销/退货数量）时，可退货数量默认=冻结数量-已创单未删除数量；
					//当冻结数量>（批次进仓数量-冲销/退货数量）时，
					//可退货数量默认=（批次进仓数量-冲销/退货数量-已创单未删除数量）
					//退货数量默认等于可退货数量，可修改小于可退货数量。
					b_data.get(m).put("IN_QTY", Float.valueOf(b_data.get(m).get("IN_QTY").toString())-Float.valueOf(b_data.get(m).get("QTY_CANCEL").toString()));
					
					if(Float.valueOf(b_data.get(m).get("FREEZE_QTY").toString()) <= 
							Float.valueOf(b_data.get(m).get("IN_QTY").toString()) -
							Float.valueOf(b_data.get(m).get("QTY_CANCEL").toString()) -
							Float.valueOf(b_data.get(m).get("CD_QTY").toString())
							) {
						b_data.get(m).put("QTY1", Float.valueOf(b_data.get(m).get("FREEZE_QTY").toString()) - Float.valueOf(b_data.get(m).get("CD_QTY").toString()));
						b_data.get(m).put("KETUI", Float.valueOf(b_data.get(m).get("FREEZE_QTY").toString()) - Float.valueOf(b_data.get(m).get("CD_QTY").toString()));
					}else {
						b_data.get(m).put("QTY1", Float.valueOf(b_data.get(m).get("IN_QTY").toString()) -
								//Float.valueOf(b_data.get(m).get("QTY_CANCEL").toString()) -
								Float.valueOf(b_data.get(m).get("CD_QTY").toString()));
						b_data.get(m).put("KETUI", Float.valueOf(b_data.get(m).get("IN_QTY").toString()) -
								//Float.valueOf(b_data.get(m).get("QTY_CANCEL").toString()) -
								Float.valueOf(b_data.get(m).get("CD_QTY").toString()));
					}
					
					if(Float.valueOf(b_data.get(m).get("IN_QTY").toString()) - 
					Float.valueOf(b_data.get(m).get("QTY_CANCEL").toString()) - 
					Float.valueOf(b_data.get(m).get("CD_QTY").toString()) >0 ) {
						result.add(b_data.get(m));
					}
				}
			}
			
		}
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutData29(Map<String, Object> params){
		logger.info("---->getWareHouseOutData29");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();		
		
		List<Map<String, Object>> outDataGroup = new ArrayList<Map<String, Object>>();	
		outDataGroup = wmsReturnGoodsDao.getWareHouseOutData29Group(params);
		int trid = 0;
		for(int i=0;i<outDataGroup.size();i++) {
			//计算可退数量
			//可退数量：当冻结数量≤采购订单数量时，可退数量=冻结数量-“00创建”状态的RETURN_QTY字段之和
			//[这里需要扣除00创建状态的退货数量的原因是冻结库存还未下架但可能被已创建状态的退货单占用了。] 
			//（00创建状态退货数量取值逻辑：根据“工厂+料号+特殊库存类型+WMS批次+供应商代码”匹配退货数量）；
			//当冻结数量>采购订单数量时，可退数量=采购订单数量-已创单数量。
			float kt_qty = 0;
			if(Float.valueOf(outDataGroup.get(i).get("FREEZE_QTY").toString()) <= Float.valueOf(outDataGroup.get(i).get("MENGE").toString())) {
				//outDataGroup.get(i).put("KETUI", Float.valueOf(outDataGroup.get(i).get("FREEZE_QTY").toString()) - Float.valueOf(outDataGroup.get(i).get("RETURN_QTY").toString()));				
				kt_qty = Float.valueOf(outDataGroup.get(i).get("FREEZE_QTY").toString()) - Float.valueOf(outDataGroup.get(i).get("RETURN_QTY").toString());
			}else {
				//outDataGroup.get(i).put("KETUI", Float.valueOf(outDataGroup.get(i).get("MENGE").toString()) - Float.valueOf(outDataGroup.get(i).get("QTY1").toString()));	
				kt_qty = Float.valueOf(outDataGroup.get(i).get("MENGE").toString()) - Float.valueOf(outDataGroup.get(i).get("QTY1").toString());
			}
			
			logger.info("-->行项目:" + outDataGroup.get(i).get("EBELP") + "料号:" + outDataGroup.get(i).get("MATNR") + 
					"订单数量:" + outDataGroup.get(i).get("MENGE") + "已创单数量:" + outDataGroup.get(i).get("QTY1") + 
					"可退数量:" + kt_qty + "|" + outDataGroup.get(i).get("S_UNIT") + "|" + outDataGroup.get(i).get("MEINS") + "|" + outDataGroup.get(i).get("LEINS")+ "|" + outDataGroup.get(i).get("UMREN")+ "|" + outDataGroup.get(i).get("UMREZ"));
			if(!outDataGroup.get(i).get("S_UNIT").toString().equals(outDataGroup.get(i).get("MEINS").toString())) {
				kt_qty = kt_qty / (Float.valueOf(outDataGroup.get(i).get("UMREN").toString())/Float.valueOf(outDataGroup.get(i).get("UMREZ").toString()));
				kt_qty = (float)(Math.round(kt_qty*1000))/1000;				
			}
			outDataGroup.get(i).put("KETUI",String.valueOf(kt_qty) + " " + outDataGroup.get(i).get("MEINS"));
			params.put("MATNR", outDataGroup.get(i).get("MATNR"));
			params.put("MATNR", outDataGroup.get(i).get("MATNR"));
			params.put("SOBKZ", outDataGroup.get(i).get("SOBKZ"));
			params.put("EBELP", outDataGroup.get(i).get("EBELP"));
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			data = wmsReturnGoodsDao.getWareHouseOutData29(params);
			for(int j=0;j<data.size();j++) {
				data.get(j).put("KETUI",outDataGroup.get(i).get("KETUI"));
				data.get(j).put("COUNT",data.size());data.get(j).put("INDEX",(j+1));
				data.get(j).put("group", "行项目:" + outDataGroup.get(i).get("EBELP") + "料号:" + outDataGroup.get(i).get("MATNR") + 
					"订单数量:" + outDataGroup.get(i).get("MENGE") + "已创单数量:" + outDataGroup.get(i).get("QTY1") + 
					"可退数量:" + outDataGroup.get(i).get("KETUI"));
				data.get(j).put("TRID",trid);trid++;
				float qty1 = 0;
				float qtyf = 0;
				if(!data.get(j).get("UNIT").toString().equals(data.get(j).get("MEINS").toString())) {
					if(!(data.get(j).get("UMREN") == null || data.get(j).get("UMREZ") == null)) {
						qty1 = Float.valueOf(data.get(j).get("QTY1").toString()) / (Float.valueOf(data.get(j).get("UMREN").toString())/Float.valueOf(data.get(j).get("UMREZ").toString()));
						qty1 = (float)(Math.round(qty1*1000))/1000;
						data.get(j).put("QTY1",qty1);
						qtyf = Float.valueOf(data.get(j).get("FREEZE_QTY").toString()) / (Float.valueOf(data.get(j).get("UMREN").toString())/Float.valueOf(data.get(j).get("UMREZ").toString()));
						qtyf = (float)(Math.round(qtyf*1000))/1000;
						data.get(j).put("FREEZE_QTY",qtyf);
					}
				}
				
				result.add(data.get(j));
			}
		}
		
		//result = wmsReturnGoodsDao.getWareHouseOutData29(params);
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutReturnData(Map<String, Object> params){
		//return wmsReturnGoodsDao.getWareHouseOutReturnData(params);	
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = wmsReturnGoodsDao.getWareHouseOutReturnData(params);
		//BUG#1159退货拣配 采购单位和基本单位 不同的，需要换算成基本单位，对库存数据做拣配
		//处理第二单位问题
		for(int i=0;i<result.size();i++) {
			if(!result.get(i).get("UNIT").toString().equals(result.get(i).get("LMEIN").toString())) {
				float return_qty = 0;
				return_qty = Float.valueOf(result.get(i).get("RETURN_QTY").toString())/
					(Float.valueOf(result.get(i).get("UMREZ").toString())/Float.valueOf(result.get(i).get("UMREN").toString()));
				return_qty = (float)(Math.round(return_qty*1000))/1000;
				result.get(i).put("RETURN_QTY", return_qty);
				result.get(i).put("QTY1", return_qty);
				result.get(i).put("UNIT", result.get(i).get("LMEIN").toString());
			}
		}
		return result;
	}
	
	@Override
	public int getPoHxQty(String PONO) {
		return wmsReturnGoodsDao.getPoHxQty(PONO);
	}
	
	@Override
	public List<Map<String, Object>> getWareHouseOutPickupData(Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		result = wmsReturnGoodsDao.getWareHouseOutPickupData(params);
		//BUG#1159退货拣配 采购单位和基本单位 不同的，需要换算成基本单位，对库存数据做拣配
		//处理第二单位问题
		for(int i=0;i<result.size();i++) {
			if(!result.get(i).get("UNIT").toString().equals(result.get(i).get("LMEIN").toString())) {
				float return_qty = 0;
				return_qty = Float.valueOf(result.get(i).get("RETURN_QTY").toString())/
					(Float.valueOf(result.get(i).get("UMREZ").toString())/Float.valueOf(result.get(i).get("UMREN").toString()));
				return_qty = (float)(Math.round(return_qty*1000))/1000;
				result.get(i).put("RETURN_QTY", return_qty);
				result.get(i).put("QTY1", return_qty);
				result.get(i).put("UNIT", result.get(i).get("LMEIN").toString());
			}
		}
		return result;
	}
	
	@Override
	public int getWmsReturnItemCountBySapOutNo(String SapOutNo) {
		return wmsReturnGoodsDao.getWmsReturnItemCountBySapOutNo(SapOutNo);
	}
	
	@Override
	public int getMoHeadHxQty(String AUFNR) {
		return wmsReturnGoodsDao.getMoHeadHxQty(AUFNR);
	}
	
	@Override
	public int getSapMoCount(Map<String, Object> params) {
		return wmsReturnGoodsDao.getSapMoCount(params);
	}
	
	@Override 
	public String getSapMoStatus(Map<String, Object> params) {
		return wmsReturnGoodsDao.getSapMoStatus(params);
	}
	
	@Override
	public List<Map<String, Object>> getPlantInfoByWerks(Map<String, Object> params){
		return wmsReturnGoodsDao.getPlantInfoByWerks(params);
	}
	
	@Override
	public String getPlantBucks(String WERKS) {
		return wmsReturnGoodsDao.getPlantBucks(WERKS);
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
