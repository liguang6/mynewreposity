package com.byd.wms.business.modules.account.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.ListUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.account.dao.WmsReturnGoodsOutReversalDao;
import com.byd.wms.business.modules.account.service.WmsReturnGoodsOutReversalCancelService;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.impl.WarehouseTasksServiceImpl;
import com.byd.wms.business.modules.config.dao.WmsSapPlantLgortDao;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WmsReturnGoodsOutReversalCancelServiceImpl implements WmsReturnGoodsOutReversalCancelService {
	@Autowired
	private WmsReturnGoodsOutReversalDao wmsReturnGoodsOutReversalDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WarehouseTasksServiceImpl warehouseTasksService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsSapPlantLgortDao wmsSapPlantLgortDao;


	@Override
	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params) {
		return wmsReturnGoodsOutReversalDao.getWmsDocHeadInfo(params);
	}

	@Override
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params) {
		return wmsReturnGoodsOutReversalDao.getSapDocHeadInfo(params);
	}

	@Override
	public List<Map<String, Object>> getVoucherReversalData(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	@Transactional(rollbackFor = Exception.class)
	public String confirmOutReversal(Map<String, Object> params) {
		//System.err.println(params.toString());
		//System.err.println(params.get("ARRLIST").toString());

		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		List<Map<String, Object>> queryStockParams = new ArrayList<Map<String, Object>>(); //库存查询条件
		List<Map<String,Object>> lm = new ArrayList<>();
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);

			Map<String,Object> coreStockMap = new HashMap<String,Object>();
			Map<String,Object> coreStockMap1 = new HashMap<String,Object>();
			coreStockMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			coreStockMap.put("WERKS", itemData.get("WERKS"));
			coreStockMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
			coreStockMap.put("MATNR", itemData.get("MATNR"));
			coreStockMap.put("MAKTX", itemData.get("MAKTX"));
			coreStockMap.put("BATCH", itemData.get("BATCH"));
			coreStockMap.put("LIFNR", itemData.get("LIFNR"));
			coreStockMap.put("LIKTX", itemData.get("LIKTX"));
			coreStockMap.put("LGORT", itemData.get("LGORT"));
			coreStockMap.put("SOBKZ", itemData.get("SOBKZ"));
			coreStockMap.put("BIN_CODE", "AAAA");
			coreStockMap.put("BIN_NAME", "进仓区");
			coreStockMap.put("MEINS", itemData.get("UNIT"));

			coreStockMap.put("REQUIREMENT_NO", itemData.get("REQUIREMENT_NO"));
			coreStockMap.put("REQUIREMENT_ITEM_NO", itemData.get("REQUIREMENT_ITEM_NO"));
			coreStockMap.put("EDITOR", userUtils.getUser().get("USERNAME"));
			coreStockMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			
			//准备数据更新接收方库存
			coreStockMap1.putAll(coreStockMap);
			coreStockMap1.put("WERKS", itemData.get("MOVE_PLANT") == null?itemData.get("WERKS"):itemData.get("MOVE_PLANT"));
			coreStockMap1.put("LGORT", itemData.get("MOVE_STLOC"));
			coreStockMap1.put("WH_NUMBER", "");
			if (!itemData.get("BUSINESS_NAME").equals("47"))
				coreStockMap1.put("SOBKZ", "Z");
	    	queryStockParams.add(coreStockMap1);
	    	
			//
			lm.add(coreStockMap);
			//wmsReturnGoodsOutCancelDao.updateCoreStockCancelQty2(coreStockMap);
			// 1 原WMS凭证数据【WMS_CORE_WMSDOC_ITEM】更新
			Map<String,Object> docMap = new HashMap<String,Object>();
			docMap.put("ID", itemData.get("ID"));
			docMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			wmsReturnGoodsOutReversalDao.updateWmsDocItemQtyCancel2(docMap);

		}
		//带出待上架明细的推荐储位，如果是AAAA则不调用产生上架任务
		lm = warehouseTasksService.searchBinForPutaway(lm);
		//System.err.println("带出待上架明细的推荐储位 " + lm.toString());
		//2 调用上架任务插入
		List<Map<String,Object>> whTaskList = new ArrayList();
		lm.forEach((k)->{
			//BIN_CODE_SHELF
			//System.err.println("key : " + k.toString() );
			//FROM_BIN_CODE TO_BIN_CODE
			k.put("FROM_BIN_CODE",k.get("BIN_CODE"));
			k.put("TO_BIN_CODE",k.get("BIN_CODE_SHELF"));
			if(k.get("BIN_CODE")!=null
					&& !k.get("BIN_CODE").equals("")
						&& !k.get("BIN_CODE").equals("AAAA")){
							whTaskList.add(k);
			}
		});
		//FROM_BIN_CODE TO_BIN_CODE

		whTaskList.forEach((e)->{
			if(e.get("PROCESS_TYPE")==null){
				e.put("PROCESS_TYPE","00");
			}
			if(e.get("QUANTITY")==null){
				e.put("QUANTITY",e.get("QTY_WMS"));
			}
		});
		//System.err.println(whTaskList.toString());

		if(whTaskList!=null &&whTaskList.size()>0){
			warehouseTasksService.saveWHTask(whTaskList);
		}
		
		// 3、库存地点调拨、工厂间调拨（301）业务，如果接受库位已上WMS，需要减少接受库位库存
		this.updateReceiveStock(jarr,queryStockParams);

		// 3库存数据【WMS_CORE_STOCK】更新
		lm.forEach((e)->{
			if(e.get("STOCK_QTY")==null){
				e.put("STOCK_QTY",e.get("QTY_WMS"));
			}
		});
		if(lm!=null && lm.size()>0){
			//commonDao.updateWmsStock(lm);
			List<Map<String,Object>> stockList = new ArrayList<>();
			List<String> stockKeyList = new ArrayList<>();
			for (Map<String, Object> map : lm) {
				StringBuffer sb = new StringBuffer();
				sb.append(map.get("WERKS").toString());
				sb.append(map.get("WH_NUMBER").toString());
				sb.append(map.get("LGORT").toString());
				sb.append(map.get("BIN_CODE").toString());
				sb.append(map.get("MATNR").toString());
				sb.append(map.get("BATCH").toString());
				sb.append(map.get("SOBKZ").toString());
				sb.append(map.get("LIFNR"));
				sb.append(map.get("SO_NO"));
				sb.append(map.get("SO_ITEM_NO"));
				String s = sb.toString();
				
				Double STOCK_QTY = Double.valueOf(map.get("STOCK_QTY")==null?"0":map.get("STOCK_QTY").toString());
				Double VIRTUAL_QTY = Double.valueOf(map.get("VIRTUAL_QTY")==null?"0":map.get("VIRTUAL_QTY").toString());
				Double LOCK_QTY = Double.valueOf(map.get("LOCK_QTY")==null?"0":map.get("LOCK_QTY").toString());
				Double VIRTUAL_LOCK_QTY = Double.valueOf(map.get("VIRTUAL_LOCK_QTY")==null?"0":map.get("VIRTUAL_LOCK_QTY").toString());
				Double FREEZE_QTY = Double.valueOf(map.get("FREEZE_QTY")==null?"0":map.get("FREEZE_QTY").toString());
				Double XJ_QTY = Double.valueOf(map.get("XJ_QTY")==null?"0":map.get("XJ_QTY").toString());
				
				if(stockKeyList.indexOf(s) >=0) {
					Map oldMap = stockList.get(stockKeyList.indexOf(s));
					
					Double STOCK_QTY_O = Double.valueOf(oldMap.get("STOCK_QTY")==null?"0":oldMap.get("STOCK_QTY").toString());
					Double VIRTUAL_QTY_O = Double.valueOf(oldMap.get("VIRTUAL_QTY")==null?"0":oldMap.get("VIRTUAL_QTY").toString());
					Double LOCK_QTY_O = Double.valueOf(oldMap.get("LOCK_QTY")==null?"0":oldMap.get("LOCK_QTY").toString());
					Double VIRTUAL_LOCK_QTY_O = Double.valueOf(oldMap.get("VIRTUAL_LOCK_QTY")==null?"0":oldMap.get("VIRTUAL_LOCK_QTY").toString());
					Double FREEZE_QTY_O = Double.valueOf(oldMap.get("FREEZE_QTY")==null?"0":oldMap.get("FREEZE_QTY").toString());
					Double XJ_QTY_O = Double.valueOf(oldMap.get("XJ_QTY")==null?"0":oldMap.get("XJ_QTY").toString());
					
					oldMap.put("STOCK_QTY", STOCK_QTY_O+STOCK_QTY);
					oldMap.put("VIRTUAL_QTY", VIRTUAL_QTY_O+VIRTUAL_QTY);
					oldMap.put("LOCK_QTY", LOCK_QTY_O+LOCK_QTY);
					oldMap.put("VIRTUAL_LOCK_QTY", VIRTUAL_LOCK_QTY_O+VIRTUAL_LOCK_QTY);
					oldMap.put("FREEZE_QTY", FREEZE_QTY_O+FREEZE_QTY);
					oldMap.put("XJ_QTY", XJ_QTY_O+XJ_QTY);
				}else {
					stockList.add(map);
					stockKeyList.add(s);
				}
			}
			commonDao.updateWmsStock(stockList);
		}

		//4 更新需求行项目冲销数量
		wmsReturnGoodsOutReversalDao.updateRequirementItemCancelQty(lm);

		// 5 准备 wms 事务记录和 sap 过账记录

		Map<String,Object> head = new HashMap<String,Object>();
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		head.put("WERKS", params.get("WERKS"));
		head.put("PZ_DATE", params.get("PZ_DATE"));
		head.put("JZ_DATE", params.get("JZ_DATE"));
		head.put("HEADER_TXT", params.get("HEADER_TXT"));
		head.put("TYPE", "01");
		head.put("CREATOR",params.get("USERNAME")+"："+params.get("FULL_NAME"));
		head.put("CREATE_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		for(int i=0;i<jarr.size();i++){
			Map<String,Object> item = new HashMap<String,Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			String REVERSAL_FLAG = "X";
			String CANCEL_FLAG = "X";
			item.put("BUSINESS_CLASS", params.get("BUSINESS_CLASS"));
			item.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			item.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			item.put("WMS_MOVE_TYPE", commonService.getRevokeMoveType(itemData.getString("WMS_MOVE_TYPE")));
			item.put("SAP_MOVE_TYPE", commonService.getRevokeSapMoveType(itemData.getString("SAP_MOVE_TYPE")));
			item.put("REVERSAL_FLAG", REVERSAL_FLAG);
			item.put("CANCEL_FLAG", CANCEL_FLAG);
			item.put("SOBKZ", itemData.getString("SOBKZ"));
			item.put("F_WERKS", itemData.getString("F_WERKS"));
			item.put("MATNR", itemData.getString("MATNR"));
			item.put("MAKTX", itemData.getString("MAKTX"));
			item.put("F_BATCH", itemData.getString("F_BATCH"));
			item.put("WERKS", itemData.getString("WERKS"));
			item.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			item.put("LGORT", itemData.getString("LGORT"));
			item.put("UNIT", itemData.getString("UNIT"));
			item.put("QTY_WMS", itemData.getString("QTY_WMS"));
			item.put("QTY_SAP", itemData.getString("QTY_WMS"));
			item.put("QTY_CANCEL", "");
			item.put("BATCH", itemData.getString("BATCH"));
			item.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
			item.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
			item.put("PO_NO", itemData.getString("PO_NO"));
			item.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
			item.put("IO_NO", itemData.getString("IO_NO"));
			item.put("WBS", itemData.getString("WBS"));
			item.put("MO_NO", itemData.getString("MO_NO"));
			item.put("MO_ITEM_NO", itemData.getString("MO_ITEM_NO"));
			item.put("ITEM_TEXT", itemData.getString("ITEM_TEXT"));
			item.put("RSNUM", itemData.getString("RSNUM"));
			item.put("RSPOS", itemData.getString("RSPOS"));
			item.put("SAP_OUT_NO", itemData.getString("SAP_OUT_NO"));
			item.put("SAP_OUT_ITEM_NO", itemData.getString("SAP_OUT_ITEM_NO"));
			item.put("RETURN_NO", itemData.getString("RETURN_NO"));
			item.put("RETURN_ITEM_NO", itemData.getString("RETURN_ITEM_NO"));
			item.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			item.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.put("REF_WMS_NO", params.get("VO_NO"));
			item.put("GM_CODE", itemData.getString("GM_CODE"));
			item.put("REF_WMS_ITEM_NO", itemData.getString("WMS_ITEM_NO"));
			String SAP_FLAG = itemData.getString("SAP_FLAG");
			if("02".equals(itemData.getString("SAP_FLAG")))SAP_FLAG="01";
			item.put("SAP_FLAG", SAP_FLAG);
			item.put("WMS_SAP_MAT_DOC", itemData.getString("WMS_SAP_MAT_DOC"));
			item.put("COST_CENTER", itemData.getString("COST_CENTER"));
			item.put("LIFNR", itemData.getString("LIFNR"));
			item.put("LIKTX", itemData.getString("LIKTX"));
			item.put("MOVE_PLANT", itemData.getString("MOVE_PLANT"));
			item.put("MOVE_STLOC", itemData.getString("MOVE_STLOC"));
			item.put("CUSTOMER", itemData.getString("CUSTOMER"));
			itemList.add(item);
		}
		String WMS_NO=commonService.saveWMSDoc(head,itemList);
		String SAP_NO="";

		String sap_flag = jarr.getJSONObject(0).getString("SAP_FLAG");
		//System.out.println("sap_flagsap_flagsap_flag "+sap_flag);
		if("01".equals(sap_flag)) {
			//实时过账
			Map<String, Object> sap_params = new HashMap<String,Object>();
			JSONObject itemData=  jarr.getJSONObject(0);
			sap_params.put("WERKS", itemData.get("WERKS"));
			sap_params.put("WMS_NO", WMS_NO);
			sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
			sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
			sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
			sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
			sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
			sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
			sap_params.put("matDocItemList", itemList);
			SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
		}else if("02".equals(sap_flag)) {
			//异步过账
			//如SAP凭证字段有值，则先执行WMS端任务产生WMS凭证（WMS凭证行项目表的SAP过账标识字段写入：02异步过账），同时产生SAP过账任务，过账任务标识为“未过账”。
			//返回消息样式为：操作成功。WMS凭证：**********（移动类型代码）SAP凭证：转为后台处理中……。
			//如“SAP凭证（WMS_SAP_MAT_DOC）”字段为空，则查询过账任务表，将原凭证的过账任务删除（硬删），同时产生WMS取消凭证（该取消凭证的SAP过账标识字段写入：00：无需过账）。
			JSONObject itemData=  jarr.getJSONObject(0);
			Map<String, Object> sap_params = new HashMap<String,Object>();
			sap_params.put("WMS_NO", itemData.get("WMS_NO"));
			if(itemData.get("WMS_SAP_MAT_DOC") == null) {
				wmsReturnGoodsOutReversalDao.delSapJobItem(sap_params);
				wmsReturnGoodsOutReversalDao.delSapJobHead(sap_params);
			}else {
				sap_params.put("WERKS", itemData.get("WERKS"));
				sap_params.put("WMS_NO", WMS_NO);
				sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
				sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
				sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
				sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
				sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
				sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
				sap_params.put("matDocItemList", itemList);
				SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
			}
		}
		
		return "WMS_NO:" + WMS_NO + ";" +SAP_NO;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public String confirmOutCancel(Map<String, Object> params) {

		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		List<Map<String, Object>> queryStockParams = new ArrayList<Map<String, Object>>(); //库存查询条件
		List<Map<String,Object>> lm = new ArrayList<>();
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);

			Map<String,Object> coreStockMap = new HashMap<String,Object>();
			Map<String,Object> coreStockMap1 = new HashMap<String,Object>();
			coreStockMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			coreStockMap.put("WERKS", itemData.get("WERKS"));
			coreStockMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
			coreStockMap.put("MATNR", itemData.get("MATNR"));
			coreStockMap.put("MAKTX", itemData.get("MAKTX"));
			coreStockMap.put("BATCH", itemData.get("BATCH"));
			coreStockMap.put("LIFNR", itemData.get("LIFNR"));
			coreStockMap.put("LIKTX", itemData.get("LIKTX"));
			coreStockMap.put("LGORT", itemData.get("LGORT"));
			coreStockMap.put("SOBKZ", itemData.get("SOBKZ"));
			coreStockMap.put("BIN_CODE", "AAAA");
			coreStockMap.put("BIN_NAME", "进仓区");
			coreStockMap.put("UNIT", itemData.get("UNIT"));

			//准备数据更新接收方库存
			coreStockMap1.putAll(coreStockMap);
			coreStockMap1.put("WERKS", itemData.get("MOVE_PLANT") == null?itemData.get("WERKS"):itemData.get("MOVE_PLANT"));
			coreStockMap1.put("LGORT", itemData.get("MOVE_STLOC"));
			coreStockMap1.put("WH_NUMBER", "");
			if (!itemData.get("BUSINESS_NAME").equals("47"))
				coreStockMap1.put("SOBKZ", "Z");
	    	queryStockParams.add(coreStockMap1);

			coreStockMap.put("REQUIREMENT_NO", itemData.get("REQUIREMENT_NO"));
			coreStockMap.put("REQUIREMENT_ITEM_NO", itemData.get("REQUIREMENT_ITEM_NO"));
			coreStockMap.put("EDITOR", userUtils.getUser().get("USERNAME"));
			coreStockMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			//
			lm.add(coreStockMap);
//			wmsReturnGoodsOutCancelDao.updateCoreStockCancelQty2(coreStockMap);
			// 1 原WMS凭证数据【WMS_CORE_WMSDOC_ITEM】更新
			Map<String,Object> docMap = new HashMap<String,Object>();
			docMap.put("ID", itemData.get("ID"));
			docMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			wmsReturnGoodsOutReversalDao.updateWmsDocItemQtyCancel2(docMap);

		}
		//带出待上架明细的推荐储位，如果是AAAA则不调用产生上架任务
		lm = warehouseTasksService.searchBinForPutaway(lm);
		//System.err.println("带出待上架明细的推荐储位 " + lm.toString());
		//2 调用上架任务插入
		List<Map<String,Object>> whTaskList = new ArrayList();
		lm.forEach((k)->{
			//BIN_CODE_SHELF
			//System.err.println("key : " + k.toString() );
			//FROM_BIN_CODE TO_BIN_CODE
			k.put("FROM_BIN_CODE",k.get("BIN_CODE"));
			k.put("TO_BIN_CODE",k.get("BIN_CODE_SHELF"));
			if(k.get("BIN_CODE")!=null
					&& !k.get("BIN_CODE").equals("")
					&& !k.get("BIN_CODE").equals("AAAA")){
				whTaskList.add(k);
			}
		});
		//FROM_BIN_CODE TO_BIN_CODE

		whTaskList.forEach((e)->{
			if(e.get("PROCESS_TYPE")==null){
				e.put("PROCESS_TYPE","00");
			}
			if(e.get("QUANTITY")==null){
				e.put("QUANTITY",e.get("QTY_WMS"));
			}
		});

		if(whTaskList!=null &&whTaskList.size()>0){
			warehouseTasksService.saveWHTask(whTaskList);
		}
		
		
		// 3、库存地点调拨、工厂间调拨（301）业务，如果接受库位已上WMS，需要减少接受库位库存
		this.updateReceiveStock(jarr,queryStockParams);
		

		// 4、库存数据【WMS_CORE_STOCK】更新
		lm.forEach((e)->{
			if(e.get("STOCK_QTY")==null){
				e.put("STOCK_QTY",e.get("QTY_WMS"));
			}
		});
		if(lm!=null && lm.size()>0){
			List<Map<String,Object>> stockList = new ArrayList<>();
			List<String> stockKeyList = new ArrayList<>();
			for (Map<String, Object> map : lm) {
				StringBuffer sb = new StringBuffer();
				sb.append(map.get("WERKS").toString());
				sb.append(map.get("WH_NUMBER").toString());
				sb.append(map.get("LGORT").toString());
				sb.append(map.get("BIN_CODE").toString());
				sb.append(map.get("MATNR").toString());
				sb.append(map.get("BATCH").toString());
				sb.append(map.get("SOBKZ").toString());
				sb.append(map.get("LIFNR"));
				sb.append(map.get("SO_NO"));
				sb.append(map.get("SO_ITEM_NO"));
				String s = sb.toString();
				
				Double STOCK_QTY = Double.valueOf(map.get("STOCK_QTY")==null?"0":map.get("STOCK_QTY").toString());
				Double VIRTUAL_QTY = Double.valueOf(map.get("VIRTUAL_QTY")==null?"0":map.get("VIRTUAL_QTY").toString());
				Double LOCK_QTY = Double.valueOf(map.get("LOCK_QTY")==null?"0":map.get("LOCK_QTY").toString());
				Double VIRTUAL_LOCK_QTY = Double.valueOf(map.get("VIRTUAL_LOCK_QTY")==null?"0":map.get("VIRTUAL_LOCK_QTY").toString());
				Double FREEZE_QTY = Double.valueOf(map.get("FREEZE_QTY")==null?"0":map.get("FREEZE_QTY").toString());
				Double XJ_QTY = Double.valueOf(map.get("XJ_QTY")==null?"0":map.get("XJ_QTY").toString());
				
				if(stockKeyList.indexOf(s) >=0) {
					Map oldMap = stockList.get(stockKeyList.indexOf(s));
					
					Double STOCK_QTY_O = Double.valueOf(oldMap.get("STOCK_QTY")==null?"0":oldMap.get("STOCK_QTY").toString());
					Double VIRTUAL_QTY_O = Double.valueOf(oldMap.get("VIRTUAL_QTY")==null?"0":oldMap.get("VIRTUAL_QTY").toString());
					Double LOCK_QTY_O = Double.valueOf(oldMap.get("LOCK_QTY")==null?"0":oldMap.get("LOCK_QTY").toString());
					Double VIRTUAL_LOCK_QTY_O = Double.valueOf(oldMap.get("VIRTUAL_LOCK_QTY")==null?"0":oldMap.get("VIRTUAL_LOCK_QTY").toString());
					Double FREEZE_QTY_O = Double.valueOf(oldMap.get("FREEZE_QTY")==null?"0":oldMap.get("FREEZE_QTY").toString());
					Double XJ_QTY_O = Double.valueOf(oldMap.get("XJ_QTY")==null?"0":oldMap.get("XJ_QTY").toString());
					
					oldMap.put("STOCK_QTY", STOCK_QTY_O+STOCK_QTY);
					oldMap.put("VIRTUAL_QTY", VIRTUAL_QTY_O+VIRTUAL_QTY);
					oldMap.put("LOCK_QTY", LOCK_QTY_O+LOCK_QTY);
					oldMap.put("VIRTUAL_LOCK_QTY", VIRTUAL_LOCK_QTY_O+VIRTUAL_LOCK_QTY);
					oldMap.put("FREEZE_QTY", FREEZE_QTY_O+FREEZE_QTY);
					oldMap.put("XJ_QTY", XJ_QTY_O+XJ_QTY);
				}else {
					stockList.add(map);
					stockKeyList.add(s);
				}
			}
			commonDao.updateWmsStock(stockList);
		}
		
		//5、 更新需求行项目冲销数量
		wmsReturnGoodsOutReversalDao.updateRequirementItemCancelQty(lm);

		// 6、 准备 wms 事务记录和 sap 过账记录
		Map<String,Object> head = new HashMap<String,Object>();
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		head.put("WERKS", params.get("WERKS"));
		head.put("PZ_DATE", params.get("PZ_DATE"));
		head.put("JZ_DATE", params.get("JZ_DATE"));
		head.put("HEADER_TXT", params.get("HEADER_TXT"));
		head.put("TYPE", "02");
		head.put("CREATOR",params.get("USERNAME")+"："+params.get("FULL_NAME"));
		head.put("CREATE_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		for(int i=0;i<jarr.size();i++){
			Map<String,Object> item = new HashMap<String,Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			String REVERSAL_FLAG = "X";
			String CANCEL_FLAG = "X";
			item.put("BUSINESS_CLASS", params.get("BUSINESS_CLASS"));
			item.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			item.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			item.put("WMS_MOVE_TYPE", commonService.getRevokeMoveType(itemData.getString("WMS_MOVE_TYPE")));
			item.put("SAP_MOVE_TYPE", commonService.getRevokeSapMoveType(itemData.getString("SAP_MOVE_TYPE")));
			item.put("REVERSAL_FLAG", REVERSAL_FLAG);
			item.put("CANCEL_FLAG", CANCEL_FLAG);
			item.put("SOBKZ", itemData.getString("SOBKZ"));
			item.put("F_WERKS", itemData.getString("F_WERKS"));
			item.put("MATNR", itemData.getString("MATNR"));
			item.put("MAKTX", itemData.getString("MAKTX"));
			item.put("F_BATCH", itemData.getString("F_BATCH"));
			item.put("WERKS", itemData.getString("WERKS"));
			item.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			item.put("LGORT", itemData.getString("LGORT"));
			item.put("UNIT", itemData.getString("UNIT"));

			item.put("QTY_WMS", itemData.getString("QTY_WMS"));
			item.put("QTY_SAP", itemData.getString("QTY_WMS"));
			item.put("QTY_CANCEL", "");

			item.put("BATCH", itemData.getString("BATCH"));
			item.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
			item.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
			item.put("PO_NO", itemData.getString("PO_NO"));
			item.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
			item.put("IO_NO", itemData.getString("IO_NO"));
			item.put("COST_CENTER", itemData.getString("COST_CENTER"));
			item.put("WBS", itemData.getString("WBS"));
			item.put("MO_NO", itemData.getString("MO_NO"));
			item.put("MO_ITEM_NO", itemData.getString("MO_ITEM_NO"));
			item.put("ITEM_TEXT", itemData.getString("ITEM_TEXT"));
			item.put("RSNUM", itemData.getString("RSNUM"));
			item.put("RSPOS", itemData.getString("RSPOS"));
			item.put("SAP_OUT_NO", itemData.getString("SAP_OUT_NO"));
			item.put("SAP_OUT_ITEM_NO", itemData.getString("SAP_OUT_ITEM_NO"));
			item.put("RETURN_NO", itemData.getString("RETURN_NO"));
			item.put("RETURN_ITEM_NO", itemData.getString("RETURN_ITEM_NO"));
			item.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			item.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.put("REF_WMS_NO", params.get("VO_NO"));
			item.put("GM_CODE", itemData.getString("GM_CODE"));
			item.put("REF_WMS_ITEM_NO", itemData.getString("WMS_ITEM_NO"));
			String SAP_FLAG = itemData.getString("SAP_FLAG");
			if("02".equals(itemData.getString("SAP_FLAG")))SAP_FLAG="01";
			item.put("SAP_FLAG", SAP_FLAG);
			item.put("LIFNR", itemData.getString("LIFNR"));
			item.put("LIKTX", itemData.getString("LIKTX"));
			item.put("WMS_SAP_MAT_DOC", itemData.getString("WMS_SAP_MAT_DOC"));
			item.put("MOVE_PLANT", itemData.getString("MOVE_PLANT"));
			item.put("MOVE_STLOC", itemData.getString("MOVE_STLOC"));
			item.put("CUSTOMER", itemData.getString("CUSTOMER"));
			itemList.add(item);
		}
		String WMS_NO=commonService.saveWMSDoc(head,itemList);
		String SAP_NO="";

		String sap_flag = jarr.getJSONObject(0).getString("SAP_FLAG");
		//System.out.println("sap_flagsap_flagsap_flag "+sap_flag);
		if("01".equals(sap_flag)) {
			//实时过账
			Map<String, Object> sap_params = new HashMap<String,Object>();
			JSONObject itemData=  jarr.getJSONObject(0);
			sap_params.put("WERKS", itemData.get("WERKS"));
			sap_params.put("WMS_NO", WMS_NO);
			sap_params.put("LGORT", itemData.getString("LGORT"));
			sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
			sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
			sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
			sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
			sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
			sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
			sap_params.put("matDocItemList", itemList);
			System.err.println("sap_params: "+sap_params.toString());
			SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
		}else if("02".equals(sap_flag)) {
			//异步过账
			//如SAP凭证字段有值，则先执行WMS端任务产生WMS凭证（WMS凭证行项目表的SAP过账标识字段写入：02异步过账），同时产生SAP过账任务，过账任务标识为“未过账”。
			//返回消息样式为：操作成功。WMS凭证：**********（移动类型代码）SAP凭证：转为后台处理中……。
			//如“SAP凭证（WMS_SAP_MAT_DOC）”字段为空，则查询过账任务表，将原凭证的过账任务删除（硬删），同时产生WMS取消凭证（该取消凭证的SAP过账标识字段写入：00：无需过账）。
			JSONObject itemData=  jarr.getJSONObject(0);
			Map<String, Object> sap_params = new HashMap<String,Object>();
			sap_params.put("WMS_NO", itemData.get("WMS_NO"));
			if(itemData.get("WMS_SAP_MAT_DOC") == null) {
				wmsReturnGoodsOutReversalDao.delSapJobItem(sap_params);
				wmsReturnGoodsOutReversalDao.delSapJobHead(sap_params);
			}else {
				sap_params.put("WERKS", itemData.get("WERKS"));
				sap_params.put("WMS_NO", WMS_NO);
				sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
				sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
				sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
				sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
				sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
				sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
				sap_params.put("matDocItemList", itemList);
				sap_params.put("LGORT", itemData.getString("LGORT"));
				System.err.println("sap_params: "+sap_params.toString());
				SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
			}
		}
		return "WMS_NO:" + WMS_NO + ";" +SAP_NO;
	}
	
	/**
	 * 冲销或取消时，更新接收方库存
	 * @param jarr
	 * @param queryStockParams
	 */
	public void updateReceiveStock(JSONArray jarr, List<Map<String,Object>> queryStockParams) {
		boolean uReceiveStock = false; //调拨业务，是否更新接收库位库存
		String businessname = jarr.getJSONObject(0).getString("BUSINESS_NAME");
		String SAP_MOVE_TYPE = jarr.getJSONObject(0).getString("SAP_MOVE_TYPE");
    	if ((businessname.equals("46") && SAP_MOVE_TYPE.contains("301")) || businessname.equals("47") || businessname.equals("48") || businessname.equals("77")) {
    		String rWerks = "";
        	String rLgort = "";
    		if (businessname.equals("47") || businessname.equals("48")) {
    			rWerks = jarr.getJSONObject(0).getString("WERKS");
    		} else {
    			rWerks = jarr.getJSONObject(0).getString("MOVE_PLANT");
    		}
    		
    		rLgort = jarr.getJSONObject(0).getString("MOVE_STLOC");
    		List<WmsSapPlantLgortEntity> plantlgortlist = wmsSapPlantLgortDao.selectList(new EntityWrapper<WmsSapPlantLgortEntity>()
		             .eq(StringUtils.isNotBlank(rWerks),"WERKS", rWerks)
		             .eq(StringUtils.isNotBlank(rLgort),"LGORT", rLgort)
		             );
    		
    		if (plantlgortlist.size() > 0) {
    			uReceiveStock = true;
    		}
    	}
    	
    	if (uReceiveStock) {
    		List<Map<String, Object>> uStockMatList = new ArrayList<Map<String, Object>>();
    		List<Map<String, Object>> stockListTemp = new ArrayList<Map<String,Object>>();
    		List<Map<String, Object>> bStockMatList = commonService.getWmsStock(queryStockParams);
    		for (Map<String, Object> param : queryStockParams) {
    			boolean stockflag = false;
    			BigDecimal qtywms = param.get("QTY_WMS") == null ? BigDecimal.ZERO:new BigDecimal(param.get("QTY_WMS").toString());
    			for (Map<String, Object> bstockmat:bStockMatList) {
    				//解决不同行相同物料，如果库存已经扣减过，则用缓存库存计算。
					for (Map<String, Object> stocktemp : stockListTemp) {
						if (stocktemp.get("ID").equals(bstockmat.get("ID"))) {
							bstockmat.putAll(stocktemp);
							stockListTemp.remove(stocktemp);
							break;
						}
					}
					
        			if (param.get("MATNR").equals(bstockmat.get("MATNR")) && param.get("BATCH").equals(bstockmat.get("BATCH")) 
        					&& param.get("LIFNR") ==null||param.get("LIFNR").equals("") ? true:param.get("LIFNR").equals(bstockmat.get("LIFNR")) 
        				    && param.get("LGORT").equals(bstockmat.get("LGORT"))
        					&& param.get("SOBKZ").equals(bstockmat.get("SOBKZ"))) {
        				BigDecimal qtystock = bstockmat.get("STOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(bstockmat.get("STOCK_QTY").toString());
        				Map<String, Object> uStockMat = new HashMap<String,Object>();
        				if (qtywms.compareTo(qtystock) <= 0) {
            				uStockMat.putAll(bstockmat);
            				uStockMat.put("STOCK_QTY", qtywms.negate()); 
                			uStockMat.put("XJ_QTY", "");
                			uStockMat.put("RSB_QTY", "");
            				uStockMat.put("VIRTUAL_QTY", "");
            				uStockMat.put("VIRTUAL_LOCK_QTY", "");
            				uStockMat.put("LOCK_QTY", "");
            				uStockMat.put("FREEZE_QTY", "");
            				uStockMat.put("EDITOR", userUtils.getUser().get("USERNAME"));
            				uStockMat.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            				uStockMatList.add(uStockMat);
            				
            				stockListTemp.add(bstockmat);
	        				stockflag = true;
	        	    		break;
        				} else if(qtystock.compareTo(BigDecimal.ZERO) > 0) {
        					uStockMat.putAll(bstockmat);
            				uStockMat.put("STOCK_QTY", qtystock.negate()); 
                			uStockMat.put("XJ_QTY", "");
                			uStockMat.put("RSB_QTY", "");
            				uStockMat.put("VIRTUAL_QTY", "");
            				uStockMat.put("VIRTUAL_LOCK_QTY", "");
            				uStockMat.put("LOCK_QTY", "");
            				uStockMat.put("FREEZE_QTY", "");
            				uStockMat.put("EDITOR", userUtils.getUser().get("USERNAME"));
            				uStockMat.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            				uStockMatList.add(uStockMat);
            				
            				stockListTemp.add(bstockmat);
            				qtywms = qtywms.subtract(qtystock);
        				}
        			}
    			}
    			
    			if (!stockflag) {
    				throw new IllegalArgumentException("找不到接收方库存或库存不足扣减，取消失败！");
    			}
	    	}
    		
    		if (uStockMatList.size() > 0) {
    	    	ListUtils.sort(uStockMatList, "ID", true); //先排序防死锁
    	    	commonService.modifyWmsStock(uStockMatList);
        	}
    	}
	}
}
