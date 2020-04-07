package com.byd.wms.business.modules.account.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.account.dao.WmsAccountReceiptHxPoDao;
import com.byd.wms.business.modules.account.dao.WmsAccountReceiptHxToDao;
import com.byd.wms.business.modules.account.dao.WmsReturnGoodsVoucherCancelDao;
import com.byd.wms.business.modules.account.dao.WmsReturnGoodsVoucherReversalDao;
import com.byd.wms.business.modules.account.service.WmsReturnGoodsVoucherReversalService;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.service.WmsCVendorService;
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;

@Service("wmsReturnGoodsVoucherReversalService")
public class WmsReturnGoodsVoucherReversalServiceImpl implements WmsReturnGoodsVoucherReversalService {
	@Autowired
	private WmsReturnGoodsVoucherReversalDao wmsReturnGoodsVoucherReversalDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private SCMDeliveryService scmDeliveryService;
	@Autowired
	private WmsReturnGoodsVoucherCancelDao wmsReturnGoodsVoucherCancelDao;
	@Autowired
	private WmsAccountReceiptHxPoDao wmsAccountReceiptHxPoDao;
	@Autowired
	private WmsAccountReceiptHxToDao wmsAccountReceiptHxToDao;
	@Autowired
	private WmsCVendorService vendorService;
	@Autowired
	WmsCWhService wmsCWhService;
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	

	@Override
	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params) {
		return wmsReturnGoodsVoucherReversalDao.getWmsDocHeadInfo(params);
	}

	@Override
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params) {
		return wmsReturnGoodsVoucherReversalDao.getSapDocHeadInfo(params);
	}

	@Override
	public List<Map<String, Object>> getVoucherReversalData(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String confirmVoucherReversal(Map<String, Object> params) {
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());

		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			Map<String,Object> parMap = new HashMap<String,Object>();
			parMap.put("ID", itemData.get("ID"));
			parMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			wmsReturnGoodsVoucherReversalDao.updateWmsDocItemQtyCancel(parMap);
			
			parMap.put("RETURN_NO", itemData.get("RETURN_NO"));
			parMap.put("RETURN_ITEM_NO", itemData.get("RETURN_ITEM_NO"));
			parMap.put("WERKS", itemData.get("WERKS"));
			parMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
			parMap.put("MATNR", itemData.get("MATNR"));
			parMap.put("MAKTX", itemData.get("MAKTX"));
			parMap.put("BATCH", itemData.get("BATCH"));
			parMap.put("LGORT", itemData.get("LGORT"));
			parMap.put("SOBKZ", itemData.get("SOBKZ"));
			parMap.put("LIFNR", itemData.get("LIFNR"));
			parMap.put("LIKTX", itemData.get("LIKTX"));
			parMap.put("UNIT", itemData.get("UNIT"));
			parMap.put("CREATOR", params.get("USERNAME").toString());
			parMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
			
			if("04".equals(params.get("BUSINESS_CLASS").toString())) {		//收料房
				List<Map<String, Object>> re_list = wmsReturnGoodsVoucherReversalDao.getReceiptInfoFromReturnItem(parMap);
				if(re_list.size()>0) {
					parMap.put("RECEIPT_NO", re_list.get(0).get("RECEIPT_NO").toString());
					parMap.put("RECEIPT_ITEM_NO", re_list.get(0).get("RECEIPT_ITEM_NO").toString());
					wmsReturnGoodsVoucherReversalDao.updateInReceiptQty(parMap);
				}
				
				List<Map<String, Object>> re_list2 = wmsReturnGoodsVoucherReversalDao.queryInRhStock(parMap);
				if(re_list2.size()>0) {
					wmsReturnGoodsVoucherReversalDao.updateInRhStock(parMap);
				}else {
					wmsReturnGoodsVoucherReversalDao.insertInRhStock(parMap);
				}
				
				String BUSINESS_TYPE = itemData.get("BUSINESS_TYPE").toString();
				if("03".equals(BUSINESS_TYPE)) {
					wmsReturnGoodsVoucherReversalDao.updatePoHx(parMap);
				}
				
				if("09".equals(BUSINESS_TYPE)) {
					wmsReturnGoodsVoucherReversalDao.updateToHx(parMap);
				}
				
			}else if("05".equals(itemData.get("BUSINESS_CLASS"))) {		//库房
				wmsReturnGoodsVoucherReversalDao.updateCoreStockFreeze(parMap);
				
				if("28".equals(itemData.get("BUSINESS_NAME").toString())) {
					wmsReturnGoodsVoucherReversalDao.updateInReceiptWhReturnQty(parMap);
				}
				if("30".equals(itemData.get("BUSINESS_NAME").toString())||
						"33".equals(itemData.get("BUSINESS_NAME").toString())||
						"34".equals(itemData.get("BUSINESS_NAME").toString())||
						"35".equals(itemData.get("BUSINESS_NAME").toString())) {
					wmsReturnGoodsVoucherReversalDao.updateInBoundCancelQty(parMap);
				}
			}else if("06".equals(itemData.get("BUSINESS_CLASS"))) {		//车间
				//2.8.6.1	更新原WMS凭证行项目的“冲销/取消数量（QTY_CANCEL）”（冲销/取消数量增加）。
				parMap.put("ID", itemData.get("ID"));
				parMap.put("QTY_WMS", itemData.get("QTY_WMS"));
				wmsReturnGoodsVoucherCancelDao.updateWmsDocItemQtyCancel(parMap);
				//2.8.6.2	更新【WMS库存表】，根据“工厂+仓库号+料号+WMS批次+库位+储位+供应商代码+特殊库存标识”更新该行数据的“数量（非限制）字段（数量减少）。
				parMap.put("WERKS", itemData.get("WERKS"));
				parMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
				parMap.put("MATNR", itemData.get("MATNR"));
				parMap.put("MAKTX", itemData.get("MAKTX"));
				parMap.put("BATCH", itemData.get("BATCH"));
				parMap.put("SOBKZ", itemData.get("SOBKZ"));
				parMap.put("LIFNR", itemData.get("LIFNR"));
				parMap.put("LGORT", itemData.get("LGORT"));
				parMap.put("BIN_CODE", itemData.get("BIN_CODE"));
				wmsReturnGoodsVoucherCancelDao.updateCoreStockStockQty(parMap);
				//1.8.6.3	针对“生产订单退料（冲预留）35”类凭证取消，需更新【生产订单组件表--- WMS_SAP_MO_COMPONENT】的“投料数量（TL_QTY）”字段值（投料数量增加）。
				if("35".equals(itemData.getString("BUSINESS_NAME"))) {
					parMap.put("MO_NO", itemData.get("MO_NO"));
					parMap.put("MO_ITEM_NO", itemData.get("MO_ITEM_NO"));
					wmsReturnGoodsVoucherCancelDao.updateSapMoComponentTLQty(parMap);
				}
			}
		}
		
		//1.8.4.4针对WMS业务类型为SCM送货单类的凭证，取消时还需同时调取SCM接口更新SCM送货单的可交货数量（可交货数量减少）。
		List<Map<String,Object>> tpoParamsList = new ArrayList<Map<String,Object>>();
		//需获取原业务的BUSINESS_TYPE
		String BUSINESS_TYPE = "";
		Map<String,Object> parMap = new HashMap<String,Object>();
		parMap.put("RETURN_NO", jarr.getJSONObject(0).getString("RETURN_NO"));
		parMap.put("RETURN_ITEM_NO", jarr.getJSONObject(0).getString("RETURN_ITEM_NO"));
		List<Map<String,Object>> b_types = wmsReturnGoodsVoucherReversalDao.getCancelBussinessType(parMap);
		if(b_types.size()>0) {
			BUSINESS_TYPE = b_types.get(0).get("BUSINESS_TYPE").toString();
		}
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			if("01".equals(BUSINESS_TYPE)) {
				Map<String, Object> tpo = new HashMap <String, Object>();
				tpo.put("PO_NO", itemData.getString("PO_NO"));
				tpo.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO"));
				tpo.put("DELIVERYAMOUNT", 0-Float.valueOf(itemData.getString("QTY_WMS")));
				tpoParamsList.add(tpo);
			}
			if(tpoParamsList.size()>0)scmDeliveryService.updateTPO(tpoParamsList);
		}
		
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
			//item.put("BUSINESS_CODE", itemData.getString("BUSINESS_CODE"));
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
			item.put("QTY_CANCEL", itemData.getString("QTY_WMS"));
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
			item.put("SAP_MOVE_TYPE", itemData.getString("SAP_MOVE_TYPE"));		
			item.put("WMS_SAP_MAT_DOC", itemData.getString("WMS_SAP_MAT_DOC"));		
			
			itemList.add(item);
		}
		String WMS_NO=commonService.saveWMSDoc(head,itemList);		
		String SAP_NO="";
		
		String sap_flag = jarr.getJSONObject(0).getString("SAP_FLAG");
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
			
			List<Map<String,Object>> matDocItemList = new ArrayList<Map<String,Object>>();
			for(int i=0;i<jarr.size();i++){
				JSONObject item =  jarr.getJSONObject(i);
				Map<String, Object> item_params = new HashMap<String,Object>();
				item_params.put("WMS_NO", item.get("WMS_NO"));
				item_params.put("WMS_ITEM_NO", item.get("WMS_ITEM_NO"));
				String WMS_SAP_MAT_DOC = "";
				float LFIMG = 0;													//任务过帐数量
				float postQty = 0;													//已过帐数量
				float reQty = Float.valueOf(item.get("QTY_WMS").toString());		//冲销数量
				List<Map<String,Object>> jobResult = wmsReturnGoodsVoucherReversalDao.getReversalJobInfo(item_params);
				if(jobResult != null) {
					WMS_SAP_MAT_DOC = jobResult.get(0).get("WMS_SAP_MAT_DOC").toString();
					LFIMG = Float.valueOf(jobResult.get(0).get("LFIMG").toString());
					postQty = Float.valueOf(jobResult.get(0).get("postQty").toString());
				}
				
				if("".equals(WMS_SAP_MAT_DOC)) {
					//未产生过帐凭证的情况下 判断【冲销数量】是否等于【任务过帐数量】 等于则删除任务 小于则修改过帐数量
					if(reQty == LFIMG) {
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty < LFIMG) {
						item_params.put("LFIMG", LFIMG - reQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}else {
						//大于则报错
						throw new RuntimeException("冲销数量不能大于过帐数量");
					}
				}else {
					//已过帐SAP情况：
					//判断【冲销数量】是否等于【任务过帐数量】-【已过帐数量】  		等于：修改任务过帐数量=0|大于：修改任务过帐数量=0，新增冲销数量=postQty-（LFIMG-reQty）
					if(reQty == (LFIMG - postQty)) {
						//删除过帐任务
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty > (LFIMG - postQty)) {
						//删除过帐任务   | 冲销数量=postQty-（LFIMG-reQty）
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
						Map<String, Object> temp = new HashMap<String,Object>();
						temp = itemList.get(i);
						temp.put("QTY_SAP", LFIMG - postQty);
						matDocItemList.add(temp);
					}else if(reQty < (LFIMG - postQty)) {
						//修改过帐任务数量 = reQty - postQty
						item_params.put("LFIMG", reQty - postQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}
				}
			}
			wmsReturnGoodsVoucherReversalDao.delSapJobHead(sap_params);	//全部行项目删除时删除任务头表
			if(matDocItemList.size() > 0) {
				sap_params.put("WERKS", itemData.get("WERKS"));
				sap_params.put("WMS_NO", WMS_NO);
				sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
				sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
				sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
				sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
				sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
				sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
				sap_params.put("matDocItemList", matDocItemList);
				SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
			}
			/*if(itemData.get("WMS_SAP_MAT_DOC") == null) {
				wmsReturnGoodsVoucherReversalDao.delSapJobItem(sap_params);
				wmsReturnGoodsVoucherReversalDao.delSapJobHead(sap_params);
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
			}*/
		}
		return "WMS_NO:" + WMS_NO + ";" + SAP_NO;
	}

	@Override
	@Transactional
	public String confirmVoucherReversalInHandover(Map<String, Object> params) {
		List<Map<String, Object>> labelList=new ArrayList<Map<String,Object>>();
		
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		String pda_flag=params.get("PDA_FLAG")==null?"":params.get("PDA_FLAG").toString();
		for(int i=0;i<jarr.size();i++){
			
			Map<String, Object> whtempMap=new HashMap<String, Object>();
			whtempMap.put("WERKS", params.get("WERKS"));
			whtempMap.put("WH_NUMBER", params.get("WH_NUMBER"));
			whtempMap.put("BARCODE_FLAG", "X");
	    	List<WmsCWhEntity> retCWh=wmsCWhService.selectByMap(whtempMap);
	    	//启用条码的工厂，该功能不允许冲销进仓凭证，只能使用PDA扫描标签(条码)做冲销
	    	if(retCWh!=null&&retCWh.size()>0&&pda_flag==""){//存在则 启用了条码
	    		throw new RuntimeException("启用条码的工厂,不允许冲销进仓凭证，只能使用PDA扫描标签(条码)做冲销！");
	    	}
			
			JSONObject itemData=  jarr.getJSONObject(i);
			// 1 库存数据【WMS_CORE_STOCK】更新   20190507放到自动或者手动上架的任务里面去了 
			/*Map<String,Object> coreStockMap = new HashMap<String,Object>();
			coreStockMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			coreStockMap.put("WERKS", itemData.get("WERKS"));
			coreStockMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
			coreStockMap.put("MATNR", itemData.get("MATNR"));
			coreStockMap.put("BATCH", itemData.get("BATCH"));
			coreStockMap.put("LIFNR", itemData.get("LIFNR"));
			coreStockMap.put("LGORT", itemData.get("LGORT"));
			coreStockMap.put("SOBKZ", itemData.get("SOBKZ"));
			coreStockMap.put("BIN_CODE", itemData.get("BIN_CODE"));
			wmsReturnGoodsVoucherCancelDao.updateCoreStockCancelQty(coreStockMap);*/
			
			//根据是否手动/自动上架 来更改库存、上架任务表、库存条码表
			Map<String,Object> inboundqueryMap = new HashMap<String,Object>();
			inboundqueryMap.put("INBOUND_NO", itemData.get("INBOUND_NO"));
			inboundqueryMap.put("INBOUND_ITEM_NO", itemData.get("INBOUND_ITEM_NO"));
			List<Map<String,Object>> inboundList=wmsReturnGoodsVoucherCancelDao.getInboundInfoByInboundNo(inboundqueryMap);
			if(inboundList!=null&&inboundList.size()>0){
				/*Map<String, Object> whtempMap_=new HashMap<String, Object>();
				whtempMap_.put("WERKS", params.get("WERKS"));
				whtempMap_.put("WH_NUMBER", params.get("WH_NUMBER"));
				whtempMap_.put("BARCODE_FLAG", params.get("X"));
		    	List<WmsCWhEntity> retCWh_=wmsCWhService.selectByMap(whtempMap_);//是否启用条码
*/				
				String auto_putaway_flag=inboundList.get(0).get("AUTO_PUTAWAY_FLAG")==null?"":inboundList.get(0).get("AUTO_PUTAWAY_FLAG").toString();
				if("X".equals(auto_putaway_flag)){//自动上架
					 //库存数据【WMS_CORE_STOCK】更新
						Map<String,Object> coreStockMap = new HashMap<String,Object>();
						coreStockMap.put("QTY_WMS", itemData.get("QTY_WMS"));
						coreStockMap.put("WERKS", itemData.get("WERKS"));
						coreStockMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
						coreStockMap.put("MATNR", itemData.get("MATNR"));
						coreStockMap.put("BATCH", itemData.get("BATCH"));
						coreStockMap.put("LIFNR", itemData.get("LIFNR"));
						coreStockMap.put("LGORT", itemData.get("LGORT"));
						coreStockMap.put("SOBKZ", itemData.get("SOBKZ"));
						coreStockMap.put("BIN_CODE", itemData.get("BIN_CODE"));
						//先判断 库存的非限制 数量 冲销数量少于库存数量，不允许冲销
						List<Map<String,Object>> retCoreStockList=wmsReturnGoodsVoucherCancelDao.getCoreStockByCond(coreStockMap);
						List<Map<String,Object>> retCoreStockInfoList=wmsReturnGoodsVoucherCancelDao.getCoreStockInfoByCond(coreStockMap);
						if(retCoreStockList!=null&&retCoreStockList.size()>0){
							String stock_qty_str=retCoreStockList.get(0).get("STOCK_QTY").toString();
							//获取库存表的基本单位
							String meins=retCoreStockInfoList.get(0).get("MEINS")==null?"":retCoreStockInfoList.get(0).get("MEINS").toString();
							//采购单位
							String unit=itemData.get("UNIT")==null?"":itemData.get("UNIT").toString();
							
							BigDecimal qty_wms_d=new BigDecimal(itemData.get("QTY_WMS").toString());//待冲销数量
							
							if(!unit.equals(meins)){
								BigDecimal UMREZ = new BigDecimal(itemData.get("UMREZ")==null?"1":itemData.get("UMREZ").toString());//转换分子
								BigDecimal UMREN = new BigDecimal(itemData.get("UMREN")==null?"1":itemData.get("UMREN").toString());//转换分母
								qty_wms_d=(qty_wms_d.multiply(UMREN)).divide(UMREZ,3,BigDecimal.ROUND_DOWN).setScale(3,BigDecimal.ROUND_DOWN);
							}
							
							if(new BigDecimal(stock_qty_str).subtract(qty_wms_d).compareTo(BigDecimal.ZERO)<0){
								throw new RuntimeException("物料号:"+itemData.get("MATNR")+" 批次:"+itemData.get("BATCH")+"的冲销数量不能小于库存数量!");
							}
						}
						wmsReturnGoodsVoucherCancelDao.updateCoreStockCancelQty(coreStockMap);
				}
				if("0".equals(auto_putaway_flag)){//手动上架
					//获取上架任务状态
					List<Map<String,Object>> retWhTasklist=wmsReturnGoodsVoucherCancelDao.getwmsWhTaskByInboundNo(inboundqueryMap);
					if(retWhTasklist!=null&&retWhTasklist.size()>0){
						String wt_status=retWhTasklist.get(0).get("WT_STATUS")==null?"":retWhTasklist.get(0).get("WT_STATUS").toString();
						String task_num=retWhTasklist.get(0).get("TASK_NUM")==null?"":retWhTasklist.get(0).get("TASK_NUM").toString();
						String wh_number=retWhTasklist.get(0).get("WH_NUMBER")==null?"":retWhTasklist.get(0).get("WH_NUMBER").toString();
						String to_bin_code=retWhTasklist.get(0).get("TO_BIN_CODE")==null?"":retWhTasklist.get(0).get("TO_BIN_CODE").toString();
						if("00".equals(wt_status)){//未清
							//已经产生了上架任务，还没有上架。 不允许冲销凭证
							throw new RuntimeException("已经产生了上架任务，还没有上架。,不允许冲销凭证!");
						}
						if("02".equals(wt_status)){//已确认
							//库存数据【WMS_CORE_STOCK】更新
							Map<String,Object> coreStockMap = new HashMap<String,Object>();
							coreStockMap.put("QTY_WMS", itemData.get("QTY_WMS"));
							coreStockMap.put("WERKS", itemData.get("WERKS"));
							coreStockMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
							coreStockMap.put("MATNR", itemData.get("MATNR"));
							coreStockMap.put("BATCH", itemData.get("BATCH"));
							coreStockMap.put("LIFNR", itemData.get("LIFNR"));
							coreStockMap.put("LGORT", itemData.get("LGORT"));
							coreStockMap.put("SOBKZ", itemData.get("SOBKZ"));
							coreStockMap.put("BIN_CODE", to_bin_code);//仓库任务表的目的储位
							
							//先判断 库存的非限制 数量 冲销数量少于库存数量，不允许冲销
							List<Map<String,Object>> retCoreStockList=wmsReturnGoodsVoucherCancelDao.getCoreStockByCond(coreStockMap);
							List<Map<String,Object>> retCoreStockInfoList=wmsReturnGoodsVoucherCancelDao.getCoreStockInfoByCond(coreStockMap);
							if(retCoreStockList!=null&&retCoreStockList.size()>0){
								String stock_qty_str=retCoreStockList.get(0).get("STOCK_QTY").toString();
								BigDecimal qty_wms_d=new BigDecimal(itemData.get("QTY_WMS").toString());//待冲销数量
								//获取库存表的基本单位
								String meins=retCoreStockInfoList.get(0).get("MEINS")==null?"":retCoreStockInfoList.get(0).get("MEINS").toString();
								//采购单位
								String unit=itemData.get("UNIT")==null?"":itemData.get("UNIT").toString();
								
								if(!unit.equals(meins)){
									BigDecimal UMREZ = new BigDecimal(itemData.get("UMREZ")==null?"1":itemData.get("UMREZ").toString());//转换分子
									BigDecimal UMREN = new BigDecimal(itemData.get("UMREN")==null?"1":itemData.get("UMREN").toString());//转换分母
									qty_wms_d=(qty_wms_d.multiply(UMREN)).divide(UMREZ,3,BigDecimal.ROUND_DOWN).setScale(3,BigDecimal.ROUND_DOWN);
								}
								if(new BigDecimal(stock_qty_str).subtract(qty_wms_d).compareTo(BigDecimal.ZERO)<0){
									throw new RuntimeException("物料号:"+itemData.get("MATNR")+" 批次:"+itemData.get("BATCH")+"的冲销数量不能小于库存数量!");
								}
							}
							
							wmsReturnGoodsVoucherCancelDao.updateCoreStockCancelQty(coreStockMap);
						}
					}
				}
			}
			// 2 原WMS凭证数据【WMS_CORE_WMSDOC_ITEM】更新
			Map<String,Object> docMap = new HashMap<String,Object>();
			docMap.put("ID", itemData.get("ID"));
			docMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			wmsReturnGoodsVoucherCancelDao.updateWmsDocItemQtyCancel(docMap);
			// 3 进仓单数据【WMS_IN_INBOUND_ITEM】更新
			Map<String,Object> inboundItemMap = new HashMap<String,Object>();
			inboundItemMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			inboundItemMap.put("INBOUND_NO", itemData.get("INBOUND_NO"));
			inboundItemMap.put("INBOUND_ITEM_NO", itemData.get("INBOUND_ITEM_NO"));
			if(!"".equals(inboundItemMap.get("INBOUND_NO"))&&!"".equals(inboundItemMap.get("INBOUND_ITEM_NO"))){
				if(inboundItemMap.get("INBOUND_NO")!=null&&inboundItemMap.get("INBOUND_ITEM_NO")!=null){
				wmsReturnGoodsVoucherCancelDao.updateInBoundItemRealQty(inboundItemMap);
				}
			}
			
			if("02".equals(params.get("BUSINESS_CLASS").toString())){//收货进仓交接产生的凭证取消 才做以下逻辑
				Map<String,Object> receiptMap = new HashMap<String,Object>();
				
				receiptMap.put("QTY_WMS", itemData.get("QTY_WMS"));
				receiptMap.put("RETURN_NO", itemData.get("RETURN_NO"));
				receiptMap.put("RETURN_ITEM_NO", itemData.get("RETURN_ITEM_NO"));
				receiptMap.put("WERKS", itemData.get("WERKS"));
				receiptMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
				//20190722 update 更新收料房都用F_LGORT 因为F_LGORT是收货单的库位
				/*if("10".equals(itemData.get("BUSINESS_NAME"))&&"06".equals(itemData.get("BUSINESS_TYPE"))){
					receiptMap.put("LGORT", itemData.get("F_LGORT"));
				}else{
					receiptMap.put("LGORT", itemData.get("LGORT"));
				}*/
				
				receiptMap.put("LGORT", itemData.get("F_LGORT"));
				receiptMap.put("MATNR", itemData.get("MATNR"));
				receiptMap.put("MAKTX", itemData.get("MAKTX"));
				receiptMap.put("BATCH", itemData.get("BATCH"));
				receiptMap.put("SOBKZ", itemData.get("SOBKZ"));
				receiptMap.put("LIFNR", itemData.get("LIFNR"));
				//查询供应商名称
				Map<String,Object> vendorMap=new HashMap<String,Object>();
				vendorMap.put("WERKS", itemData.get("WERKS"));
				vendorMap.put("LIFNR", itemData.get("LIFNR"));
				vendorMap.put("DEL_FLAG", "0");
				
				List<WmsCVendor> retVendorList=vendorService.selectByMap(vendorMap);
				if(retVendorList!=null&&retVendorList.size()>0){
					receiptMap.put("LIKTX", retVendorList.get(0).getName1());
				}else{
					receiptMap.put("LIKTX", "");
				}
				receiptMap.put("UNIT", itemData.get("UNIT"));
				receiptMap.put("CREATOR", params.get("USERNAME").toString());
				receiptMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
				receiptMap.put("RECEIPT_NO", itemData.get("RECEIPT_NO"));
				receiptMap.put("RECEIPT_ITEM_NO", itemData.get("RECEIPT_ITEM_NO"));
				receiptMap.put("EDITOR", params.get("USERNAME").toString());
				receiptMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				//收货单数据【wms_in_receipt】更新   判断是否是试装 储位  更新试装进仓数量，试装产生的进仓单中，retQcList是多天记录，此处有bug
				List<Map<String, Object>> retQcList=wmsReturnGoodsVoucherCancelDao.queryInbondHeadQcResult(receiptMap);
				
				if(retQcList.size()>0){
					receiptMap.put("QC_RESULT", retQcList.get(0).get("QC_RESULT"));
					//判断是否是试装储位
					/*Map<String, Object> whbinMap = new HashMap <String, Object>();
					whbinMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
					whbinMap.put("BIN_CODE", retQcList.get(0).get("BIN_CODE"));
					whbinMap.put("DEL", "0");
					List<WmsCoreWhBinEntity> whBinList=coreWhbinService.selectByMap(whbinMap);
					if(whBinList!=null&&whBinList.size()>0){//存在试装储位
						if("04".equals(whBinList.get(0).getBinType())){//试装储位类型
							receiptMap.put("BIN_CODE", "SZ");
						}
					}else{
						receiptMap.put("BIN_CODE", itemData.get("BIN_CODE"));
					}*/
					//不用判断了，事务记录表保存的SZ储位就是试装
					receiptMap.put("BIN_CODE", itemData.get("BIN_CODE"));
					wmsReturnGoodsVoucherCancelDao.updateInReceiptInfo(receiptMap);
				}
				
				
				
				//收料房数据【WMS_IN_RH_STOCK】更新  没有对应的数据，就插入一条数据
				/*List<Map<String, Object>> ret_list= wmsReturnGoodsVoucherCancelDao.queryInRhStock(receiptMap);
				if(ret_list.size()>0) {
					wmsReturnGoodsVoucherCancelDao.updateInRhStock(receiptMap);
				}else {
					wmsReturnGoodsVoucherCancelDao.insertInRhStock(receiptMap);
				}*/
				List<Map<String, Object>> inRhList=new ArrayList<Map<String, Object>>();
				inRhList.add(receiptMap);
				wmsReturnGoodsVoucherCancelDao.updateOrInsertRhStock(inRhList);
				
				//查询该凭证号对应的收货单表 wms_in_receipt中的test_flag，如果为02 label_status=02 否则03
				List<Map<String, Object>> retReciptTFlist=wmsReturnGoodsVoucherCancelDao.getReceiptInfoByreceiptNo(receiptMap);
				String label_status="";
				if(retReciptTFlist!=null&&retReciptTFlist.size()>0){
					if("02".equals(retReciptTFlist.get(0).get("TEST_FLAG"))){
						label_status="02";
					}else{
						label_status="03";
					}
				}
				//该凭证号对应的标签都改为这个状态
				if(itemData.get("LABEL_NO")!=null){
	    			String label_no_str=itemData.get("LABEL_NO").toString();
					String[] label_no_arry=label_no_str.split(",");
					for(int n=0;n<label_no_arry.length;n++){
						Map<String, Object> labelMapTemp=new HashMap<String, Object>();
						labelMapTemp.put("LABEL_NO", label_no_arry[n]);
						labelMapTemp.put("LABEL_STATUS", label_status);
						labelList.add(labelMapTemp);
					}
	    		}
				
			}else{//自制进仓产生的标签状态都改为 03
				if(itemData.get("LABEL_NO")!=null){
	    			String label_no_str=itemData.get("LABEL_NO").toString();
					String[] label_no_arry=label_no_str.split(",");
					for(int n=0;n<label_no_arry.length;n++){
						Map<String, Object> labelMapTemp=new HashMap<String, Object>();
						labelMapTemp.put("LABEL_NO", label_no_arry[n]);
						labelMapTemp.put("LABEL_STATUS", "03");
						labelList.add(labelMapTemp);
					}
	    		}
			}
			
			
			// 4 核销信息数据修改
			Map<String, Object> HXTempMap=new HashMap<String, Object>();
			HXTempMap.put("QTY_WMS", itemData.get("QTY_WMS"));
			HXTempMap.put("WERKS", itemData.get("WERKS"));
			HXTempMap.put("WH_NUMBER", itemData.get("WH_NUMBER"));
			HXTempMap.put("PO_NO", itemData.get("PO_NO"));
			HXTempMap.put("PO_ITEM_NO", itemData.get("PO_ITEM_NO"));
			HXTempMap.put("MO_NO", itemData.get("MO_NO"));
			HXTempMap.put("MO_ITEM_NO", itemData.get("MO_ITEM_NO"));
			HXTempMap.put("RSNUM", itemData.get("RSNUM"));
			HXTempMap.put("RSPOS", itemData.get("RSPOS"));
				if("02".equals(params.get("BUSINESS_CLASS").toString())){//收货进仓单凭证 取消 核销信息修改
							if("10".equals(itemData.get("BUSINESS_NAME"))&&"03".equals(itemData.get("BUSINESS_TYPE"))&&"SS105".equals(itemData.get("WMS_MOVE_TYPE"))){
								//核销表【WMS_HX_PO】 修改
								wmsReturnGoodsVoucherCancelDao.updateHXPO(HXTempMap);
							}else if("10".equals(itemData.get("BUSINESS_NAME"))&&"07".equals(itemData.get("BUSINESS_TYPE"))&&"SS105T".equals(itemData.get("WMS_MOVE_TYPE"))){
								//核销表【WMS_HX_DN】 修改
							}else if("10".equals(itemData.get("BUSINESS_NAME"))&&"09".equals(itemData.get("BUSINESS_TYPE"))&&"SS305DB".equals(itemData.get("WMS_MOVE_TYPE"))){
								//核销表【WMS_HX_TO】 修改
							}
						}
				if("03".equals(params.get("BUSINESS_CLASS").toString())){//创建进仓单凭证 取消 核销信息修改
							if("61".equals(itemData.get("BUSINESS_NAME"))&&"11".equals(itemData.get("BUSINESS_TYPE"))&&"SS101M".equals(itemData.get("WMS_MOVE_TYPE"))){
								//核销表【WMS_HX_MO_ITEM】 修改
								wmsReturnGoodsVoucherCancelDao.updateHXMOITEM(HXTempMap);
							}else if("63".equals(itemData.get("BUSINESS_NAME"))&&"11".equals(itemData.get("BUSINESS_TYPE"))&&"SS531".equals(itemData.get("WMS_MOVE_TYPE"))){
								//核销表【WMS_HX_MO_COMPONENT】 修改
								wmsReturnGoodsVoucherCancelDao.updateHXMOCOMPONENT(HXTempMap);
							}
						}
		}
		//更改标签状态
		if(labelList.size()>0){
			wmsInInboundDao.updateLabelStatusByLabelNoByList(labelList);
		}
		
		//启用条码的工厂，根据条码号，删除wms_core_stock_label对应的库存关联信息
		Map<String, Object> whtempMap_=new HashMap<String, Object>();
		whtempMap_.put("WERKS", params.get("WERKS"));
		whtempMap_.put("WH_NUMBER", params.get("WH_NUMBER"));
    	List<WmsCWhEntity> retCWh_=wmsCWhService.selectByMap(whtempMap_);
    	if(retCWh_.size()>0){
		if("X".equals(retCWh_.get(0).getBarcodeFlag())){//启用了条码
			wmsReturnGoodsVoucherCancelDao.delStockLabelByList(labelList);
		}
		}
		
		// 5 准备 wms 事务记录和 sap 过账记录
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
			item.put("QTY_CANCEL", itemData.getString("QTY_WMS"));
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
			item.put("REF_SAP_MATDOC_YEAR", itemData.getString("REF_SAP_MATDOC_YEAR"));	
			item.put("REF_SAP_MATDOC_NO", itemData.getString("REF_SAP_MATDOC_NO"));	
			item.put("REF_SAP_MATDOC_ITEM_NO", itemData.getString("REF_SAP_MATDOC_ITEM_NO"));	

			item.put("COST_CENTER", itemData.getString("COST_CENTER"));
			item.put("LIFNR", itemData.getString("LIFNR"));
			item.put("LIKTX", itemData.getString("LIKTX"));
			
			itemList.add(item);
		}
		String WMS_NO=commonService.saveWMSDoc(head,itemList);		
		String SAP_NO="";
		
		String sap_flag = jarr.getJSONObject(0).getString("SAP_FLAG");
		//00 补过账的 
		if("01".equals(sap_flag)||"00".equals(sap_flag)) {
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
			
			List<Map<String,Object>> matDocItemList = new ArrayList<Map<String,Object>>();
			for(int i=0;i<jarr.size();i++){
				JSONObject item =  jarr.getJSONObject(i);
				Map<String, Object> item_params = new HashMap<String,Object>();
				item_params.put("WMS_NO", item.get("WMS_NO"));
				item_params.put("WMS_ITEM_NO", item.get("WMS_ITEM_NO"));
				String WMS_SAP_MAT_DOC = "";
				float LFIMG = 0;													//任务过帐数量
				float postQty = 0;													//已过帐数量
				float reQty = Float.valueOf(item.get("QTY_WMS").toString());		//冲销数量
				List<Map<String,Object>> jobResult = wmsReturnGoodsVoucherReversalDao.getReversalJobInfo(item_params);
				if(jobResult != null) {
					WMS_SAP_MAT_DOC = jobResult.get(0).get("WMS_SAP_MAT_DOC").toString();
					LFIMG = Float.valueOf(jobResult.get(0).get("LFIMG").toString());
					postQty = Float.valueOf(jobResult.get(0).get("postQty").toString());
				}
				
				if("".equals(WMS_SAP_MAT_DOC)) {
					//未产生过帐凭证的情况下 判断【冲销数量】是否等于【任务过帐数量】 等于则删除任务 小于则修改过帐数量
					if(reQty == LFIMG) {
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty < LFIMG) {
						item_params.put("LFIMG", LFIMG - reQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}else {
						//大于则报错
						throw new RuntimeException("冲销数量不能大于过帐数量");
					}
				}else {
					//已过帐SAP情况：
					//判断【冲销数量】是否等于【任务过帐数量】-【已过帐数量】  		等于：修改任务过帐数量=0|大于：修改任务过帐数量=0，新增冲销数量=postQty-（LFIMG-reQty）
					if(reQty == (LFIMG - postQty)) {
						//删除过帐任务
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty > (LFIMG - postQty)) {
						//删除过帐任务   | 冲销数量=postQty-（LFIMG-reQty）
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
						Map<String, Object> temp = new HashMap<String,Object>();
						temp = itemList.get(i);
						temp.put("QTY_SAP", LFIMG - postQty);
						matDocItemList.add(temp);
					}else if(reQty < (LFIMG - postQty)) {
						//修改过帐任务数量 = reQty - postQty
						item_params.put("LFIMG", reQty - postQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}
				}
			}
			wmsReturnGoodsVoucherReversalDao.delSapJobHead(sap_params);	//全部行项目删除时删除任务头表
			if(matDocItemList.size() > 0) {
				sap_params.put("WERKS", itemData.get("WERKS"));
				sap_params.put("WMS_NO", WMS_NO);
				sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
				sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
				sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
				sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
				sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
				sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
				sap_params.put("matDocItemList", matDocItemList);
				SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
			}
			//
			/*JSONObject itemData=  jarr.getJSONObject(0);
			Map<String, Object> sap_params = new HashMap<String,Object>();
			sap_params.put("WMS_NO", itemData.get("WMS_NO"));
			if(itemData.get("WMS_SAP_MAT_DOC") == null) {
				wmsReturnGoodsVoucherReversalDao.delSapJobItem(sap_params);
				wmsReturnGoodsVoucherReversalDao.delSapJobHead(sap_params);
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
			}*/
		}
		return "WMS_NO:" + WMS_NO + ";" + SAP_NO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String confirmVoucherReversalInReceipt(Map<String, Object> params) {
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		JSONObject.parseArray(params.get("ARRLIST").toString(), Map.class).forEach(m->{
    		m=(Map<String,Object>)m;
    		itemList.add(m);
    	});
		String WMS_NO="";//取消后生成的新凭证
		String SAP_NO="";//SAP物料凭证取消 返回取消的物料凭证号		
		Map itemData= itemList.get(0);//第一个行项目		
		String sap_flag = (String) itemData.get("SAP_FLAG");//初始SPA_FLAG		
		String BUSINESS_NAME=(String) itemData.get("BUSINESS_NAME");
		String BUSINESS_TYPE = (String) itemData.get("BUSINESS_TYPE");
		
		String WMS_MOVE_TYPE = commonService.getRevokeMoveType(itemData.get("WMS_MOVE_TYPE").toString());
		String SAP_MOVE_TYPE = commonService.getRevokeSapMoveType(itemData.get("SAP_MOVE_TYPE").toString());
		String REVERSAL_FLAG = "X";
		String CANCEL_FLAG = "X";
		Map<String,Object> head=new HashMap<String,Object>();  //WMS凭证记录抬头
		head.put("WERKS", params.get("WERKS"));
		head.put("PZ_DATE", params.get("PZ_DATE"));
		head.put("JZ_DATE", params.get("JZ_DATE"));
		head.put("HEADER_TXT", params.get("HEADER_TXT"));
		head.put("TYPE", "01");//冲销凭证
		head.put("BUSINESS_CLASS", params.get("BUSINESS_CLASS"));
		head.put("CREATOR",params.get("USERNAME")+"："+params.get("FULL_NAME"));
		head.put("CREATE_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		
		itemList.forEach(item->{
			item.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			item.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));	
			item.put("REF_WMS_NO", params.get("VO_NO"));		
			item.put("REF_WMS_ITEM_NO", item.get("WMS_ITEM_NO"));	
			item.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
			item.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			item.put("REVERSAL_FLAG", REVERSAL_FLAG);
			item.put("CANCEL_FLAG", CANCEL_FLAG);
			item.put("QTY_SAP", item.get("QTY_WMS"));
			item.put("QTY_CANCEL", item.get("QTY_WMS"));
			item.put("RH_QTY", item.get("QTY_WMS"));
			String SAP_FLAG = item.get("SAP_FLAG")==null?"00":item.get("SAP_FLAG").toString();
			if("02".equals(SAP_FLAG))SAP_FLAG="01";
			item.put("SAP_FLAG", SAP_FLAG);		
		});
		/**
		 * 产生新的WMS凭证号
		 */
		WMS_NO=commonService.saveWMSDoc(head,itemList);		
		
		/**
		 * 原WMS凭证数据【WMS_CORE_WMSDOC_ITEM】更新：更新原WMS凭证行项目的“冲销/取消数量（QTY_CANCEL）”（冲销数量增加）。
		 */
		wmsReturnGoodsVoucherCancelDao.updateWmsDocItemQtyCancel_Batch(itemList);
		
		/**
		 * 收货单数据【wms_in_receipt】更新：更新 收料房冲销/退货数量 104+124数量（RETURN_QTY），用冲销数量累加，
		 * 如果可进仓数量（INABLE_QTY）大于0，需要用冲销数量递减。
		 */
		wmsReturnGoodsVoucherReversalDao.updateInReceipt_Batch(itemList);
		
		/**
		 * 送检单数据行项目【WMS_QC_INSPECTION_ITEM】更新：更新送检数量（INSPECTION_QTY），用冲销数量递减。
		 */
		wmsReturnGoodsVoucherReversalDao.updateQCItem_Batch(itemList);
	
		/**
		 * 	质检结果数据【wms_qc_result】更新：更新数量（RESULT_QTY），用冲销数量递减。
		 */
		wmsReturnGoodsVoucherReversalDao.updateQCResult_Batch(itemList);
		
		/**
		 * 收料房数据【WMS_IN_RH_STOCK】更新：减少对应供应商代码，批次，料号 的数量，如果等于0，就硬删这条数据。
		 */
		wmsReturnGoodsVoucherCancelDao.updateDeleteInRhStock_Batch(itemList);
		
		/**
		 *  标签数据【wms_core_label】更新：del=X，用收货单号和行号查询。
		 */
		wmsReturnGoodsVoucherCancelDao.deleteLable_Batch(itemList);
		
		/**
		 * 核销信息数据修改：核销实收产生的凭证需要把取消数量累加到对应核销信息表的 【实物收料房收料冲销，取消】字段。
		 * 核销实收凭证判断逻辑：
		 * BUSINESS_NAME=07和BUSINESS_TYPE=03和WMS_MOVE_TYPE=SS103 对应核销表【WMS_HX_PO】
		 * 或者BUSINESS_NAME=08和BUSINESS_TYPE=07和WMS_MOVE_TYPE=SS103T 对应核销表【WMS_HX_DN】
		 * 或者 BUSINESS_NAME=09和BUSINESS_TYPE=09和WMS_MOVE_TYPE=SS303DB 对应核销表【WMS_HX_TO】
		 */
		if("07".equals(BUSINESS_NAME)&&"03".equals(BUSINESS_TYPE)&&"SS103".equals(itemData.get("WMS_MOVE_TYPE"))) {
				wmsReturnGoodsVoucherCancelDao.updateHXPO_Batch(itemList);
		}
		if("08".equals(BUSINESS_NAME)&&"07".equals(BUSINESS_TYPE)&&"SS103T".equals(itemData.get("WMS_MOVE_TYPE"))) {
			wmsReturnGoodsVoucherCancelDao.updateHXDN_Batch(itemList);
		}
		if("09".equals(BUSINESS_NAME)&&"09".equals(BUSINESS_TYPE)&&"SS303DB".equals(itemData.get("WMS_MOVE_TYPE"))) {
			wmsReturnGoodsVoucherCancelDao.updateHXTO_Batch(itemList);
		}
		
		if("01".equals(sap_flag)) {
			//实时过账
			Map<String, Object> sap_params = new HashMap<String,Object>();	
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
			Map<String, Object> sap_params = new HashMap<String,Object>();
			sap_params.put("WMS_NO", itemData.get("WMS_NO"));
			
			List<Map<String,Object>> matDocItemList = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> item : itemList) {
				Map<String, Object> item_params = new HashMap<String,Object>();
				item_params.put("WMS_NO", item.get("WMS_NO"));
				item_params.put("WMS_ITEM_NO", item.get("WMS_ITEM_NO"));
				String WMS_SAP_MAT_DOC = "";
				float LFIMG = 0;													//任务过帐数量
				float postQty = 0;													//已过帐数量
				float reQty = Float.valueOf(item.get("QTY_WMS").toString());		//冲销数量
				List<Map<String,Object>> jobResult = wmsReturnGoodsVoucherReversalDao.getReversalJobInfo(item_params);
				if(jobResult != null) {
					WMS_SAP_MAT_DOC = jobResult.get(0).get("WMS_SAP_MAT_DOC").toString();
					LFIMG = Float.valueOf(jobResult.get(0).get("LFIMG").toString());
					postQty = Float.valueOf(jobResult.get(0).get("postQty").toString());
				}
				
				if("".equals(WMS_SAP_MAT_DOC)) {
					//未产生过帐凭证的情况下 判断【冲销数量】是否等于【任务过帐数量】 等于则删除任务 小于则修改过帐数量
					if(reQty == LFIMG) {
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty < LFIMG) {
						item_params.put("LFIMG", LFIMG - reQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}else {
						//大于则报错
						throw new RuntimeException("冲销数量不能大于过帐数量");
					}
				}else {
					//已过帐SAP情况：
					//判断【冲销数量】是否等于【任务过帐数量】-【已过帐数量】  		等于：修改任务过帐数量=0|大于：修改任务过帐数量=0，新增冲销数量=postQty-（LFIMG-reQty）
					if(reQty == (LFIMG - postQty)) {
						//删除过帐任务
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty > (LFIMG - postQty)) {
						//删除过帐任务   | 冲销数量=postQty-（LFIMG-reQty）
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
						Map<String, Object> temp = new HashMap<String,Object>();
						temp = item;
						temp.put("QTY_SAP", LFIMG - postQty);
						matDocItemList.add(temp);
					}else if(reQty < (LFIMG - postQty)) {
						//修改过帐任务数量 = reQty - postQty
						item_params.put("LFIMG", reQty - postQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}
				}
			}
			wmsReturnGoodsVoucherReversalDao.delSapJobHead(sap_params);	//全部行项目删除时删除任务头表
			if(matDocItemList.size() > 0) {
				sap_params.put("WERKS", itemData.get("WERKS"));
				sap_params.put("WMS_NO", WMS_NO);
				sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
				sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
				sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
				sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
				sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
				sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
				sap_params.put("matDocItemList", matDocItemList);
				SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
			}
			
/*			Map<String, Object> sap_params = new HashMap<String,Object>();
			sap_params.put("WMS_NO", itemData.get("WMS_NO"));
			if(itemData.get("WMS_SAP_MAT_DOC") == null) {
				wmsReturnGoodsVoucherCancelDao.delSapJobItem(sap_params);
				wmsReturnGoodsVoucherCancelDao.delSapJobHead(sap_params);
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
			}*/
		}
		return "WMS_NO:" + WMS_NO + ";" + SAP_NO;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String confirmVoucherReversalInReceiptV(Map<String, Object> params) {
		StringBuffer errorMsg = new StringBuffer();
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		JSONObject.parseArray(params.get("ARRLIST").toString(), Map.class).forEach(m->{
    		m=(Map<String,Object>)m;
    		m.put("QTY_SAP", m.get("QTY_WMS"));
    		itemList.add(m);
    	});
		String WMS_NO="";//取消后生成的新凭证
		String SAP_NO="";//SAP物料凭证取消 返回取消的物料凭证号		
		Map itemData= itemList.get(0);//第一个行项目		
		String sap_flag = (String) itemData.get("SAP_FLAG");//初始SPA_FLAG		
		String BUSINESS_NAME=(String) itemData.get("BUSINESS_NAME");
		
		String WMS_MOVE_TYPE = commonService.getRevokeMoveType(itemData.get("WMS_MOVE_TYPE").toString());
		String SAP_MOVE_TYPE = commonService.getRevokeSapMoveType(itemData.get("SAP_MOVE_TYPE").toString());
		String REVERSAL_FLAG = "X";
		String CANCEL_FLAG = "X";
		
		/**
		 * 校验剩余核销数量是否大于等于待取消凭证物料数量
		 */
		if("22".equals(BUSINESS_NAME)) {
			//22 SAP采购订单收料(V)
			List<Map<String, Object>> checkHxPoInfoList = wmsAccountReceiptHxPoDao.checkHxPoInfo(itemList);
			if(checkHxPoInfoList.size()>0) {
				errorMsg.append("采购订单：");
				for (Map<String, Object> map : checkHxPoInfoList) {
					errorMsg.append(map.get("EBELN")+"-"+map.get("EBELP"));
				}
				errorMsg.append("剩余核销数量不足，凭证行项目不能取消！");
			}
		}
		if("24".equals(BUSINESS_NAME)) {
			//24 303调拨收料(V)
			List<Map<String, Object>> checkHxPoInfoList = wmsAccountReceiptHxToDao.checkHxToInfo(itemList);
			if(checkHxPoInfoList.size()>0) {
				errorMsg.append("303调拨单：");
				for (Map<String, Object> map : checkHxPoInfoList) {
					errorMsg.append(map.get("SAP_MATDOC_NO")+"-"+map.get("SAP_MATDOC_ITEM_NO"));
				}
				errorMsg.append("剩余核销数量不足，凭证行项目不能取消！");
			}
		}
		//60 生产订单收货过账(V)
		
		//62 副产品收货过账(V)
		/**
		 * 校验虚拟库存是否大于等于待取消凭证物料数量
		 */
		List<Map<String, Object>> checkMatVirtualStockList = commonService.checkMatVirtualStock(itemList);
		if(checkMatVirtualStockList.size()>0) {
			errorMsg.append("物料：");
			for (Map<String, Object> map : checkMatVirtualStockList) {
				errorMsg.append(map.get("MATNR")+"-"+map.get("BATCH")+"-"+map.get("BIN_CODE"));
			}
			errorMsg.append("虚拟库存不足，凭证行项目不能取消！");
		}
		
		Map<String,Object> head=new HashMap<String,Object>();  //WMS凭证记录抬头
		head.put("WERKS", params.get("WERKS"));
		head.put("PZ_DATE", params.get("PZ_DATE"));
		head.put("JZ_DATE", params.get("JZ_DATE"));
		head.put("HEADER_TXT", params.get("HEADER_TXT"));
		head.put("TYPE", "01");//冲销凭证
		head.put("BUSINESS_CLASS", params.get("BUSINESS_CLASS"));
		head.put("CREATOR",params.get("USERNAME")+"："+params.get("FULL_NAME"));
		head.put("CREATE_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		
		itemList.forEach(item->{
			item.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			item.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));	
			item.put("REF_WMS_NO", params.get("VO_NO"));		
			item.put("REF_WMS_ITEM_NO", item.get("WMS_ITEM_NO"));	
			item.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
			item.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			item.put("REVERSAL_FLAG", REVERSAL_FLAG);
			item.put("CANCEL_FLAG", CANCEL_FLAG);
			item.put("QTY_SAP", item.get("QTY_WMS"));
			item.put("QTY_CANCEL", item.get("QTY_WMS"));
			item.put("RH_QTY", item.get("QTY_WMS"));
			String SAP_FLAG = item.get("SAP_FLAG")==null?"00":item.get("SAP_FLAG").toString();
			if("02".equals(SAP_FLAG))SAP_FLAG="01";
			item.put("SAP_FLAG", SAP_FLAG);	
			item.put("VIRTUAL_QTY", 0-Double.valueOf(item.get("QTY_WMS").toString()));
		});
		
		/**
		 * 更新虚拟库存
		 */
		commonService.modifyWmsStock(itemList);
		
		/**
		 * 产生新的WMS凭证号
		 */
		WMS_NO=commonService.saveWMSDoc(head,itemList);		
		
		/**
		 * 原WMS凭证数据【WMS_CORE_WMSDOC_ITEM】更新：更新原WMS凭证行项目的“冲销/取消数量（QTY_CANCEL）”（冲销数量增加）。
		 */
		wmsReturnGoodsVoucherCancelDao.updateWmsDocItemQtyCancel_Batch(itemList);
		
		/**
		 * 核销信息数据修改：核销取消产生的凭证需要把取消数量累加到对应核销信息表的 【实物收料房收料冲销，取消】字段。
		 */
		if("22".equals(BUSINESS_NAME)) {
				wmsReturnGoodsVoucherCancelDao.updateHXPO_Cancel(itemList);
		}
		if("24".equals(BUSINESS_NAME)) {
			wmsReturnGoodsVoucherCancelDao.updateHXTO_Cancel(itemList);
		}
		
		if("01".equals(sap_flag)) {
			//实时过账
			Map<String, Object> sap_params = new HashMap<String,Object>();	
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
			Map<String, Object> sap_params = new HashMap<String,Object>();
			sap_params.put("WMS_NO", itemData.get("WMS_NO"));
			
			List<Map<String,Object>> matDocItemList = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> item : itemList) {
				Map<String, Object> item_params = new HashMap<String,Object>();
				item_params.put("WMS_NO", item.get("WMS_NO"));
				item_params.put("WMS_ITEM_NO", item.get("WMS_ITEM_NO"));
				String WMS_SAP_MAT_DOC = "";
				float LFIMG = 0;													//任务过帐数量
				float postQty = 0;													//已过帐数量
				float reQty = Float.valueOf(item.get("QTY_WMS").toString());		//冲销数量
				List<Map<String,Object>> jobResult = wmsReturnGoodsVoucherReversalDao.getReversalJobInfo(item_params);
				if(jobResult != null) {
					WMS_SAP_MAT_DOC = jobResult.get(0).get("WMS_SAP_MAT_DOC").toString();
					LFIMG = Float.valueOf(jobResult.get(0).get("LFIMG").toString());
					postQty = Float.valueOf(jobResult.get(0).get("postQty").toString());
				}
				
				if("".equals(WMS_SAP_MAT_DOC)) {
					//未产生过帐凭证的情况下 判断【冲销数量】是否等于【任务过帐数量】 等于则删除任务 小于则修改过帐数量
					if(reQty == LFIMG) {
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty < LFIMG) {
						item_params.put("LFIMG", LFIMG - reQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}else {
						//大于则报错
						throw new RuntimeException("冲销数量不能大于过帐数量");
					}
				}else {
					//已过帐SAP情况：
					//判断【冲销数量】是否等于【任务过帐数量】-【已过帐数量】  		等于：修改任务过帐数量=0|大于：修改任务过帐数量=0，新增冲销数量=postQty-（LFIMG-reQty）
					if(reQty == (LFIMG - postQty)) {
						//删除过帐任务
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
					}else if(reQty > (LFIMG - postQty)) {
						//删除过帐任务   | 冲销数量=postQty-（LFIMG-reQty）
						wmsReturnGoodsVoucherReversalDao.delSapJobItem(item_params);
						Map<String, Object> temp = new HashMap<String,Object>();
						temp = item;
						temp.put("QTY_SAP", LFIMG - postQty);
						matDocItemList.add(temp);
					}else if(reQty < (LFIMG - postQty)) {
						//修改过帐任务数量 = reQty - postQty
						item_params.put("LFIMG", reQty - postQty);
						wmsReturnGoodsVoucherReversalDao.updateJobItem(item_params);
					}
				}
			}
			wmsReturnGoodsVoucherReversalDao.delSapJobHead(sap_params);	//全部行项目删除时删除任务头表
			if(matDocItemList.size() > 0) {
				sap_params.put("WERKS", itemData.get("WERKS"));
				sap_params.put("WMS_NO", WMS_NO);
				sap_params.put("JZ_DATE",  params.get("JZ_DATE"));
				sap_params.put("PZ_DATE",  params.get("PZ_DATE"));
				sap_params.put("HEADER_TXT",  params.get("HEADER_TXT"));
				sap_params.put("REF_WMS_NO", itemData.get("WMS_NO"));
				sap_params.put("MAT_DOC", itemData.get("SAP_MATDOC_NO"));
				sap_params.put("DOC_YEAR", params.get("PZ_DATE").toString().substring(0,4));
				sap_params.put("matDocItemList", matDocItemList);
				SAP_NO = "SAP_NO:" + commonService.sapGoodsMvtReversal(sap_params);
			}
		}
		return "WMS_NO:" + WMS_NO + ";" + SAP_NO;
	}

	@Override
	public List<Map<String, Object>> getVoucherReversalDataByLable(Map<String, Object> params) {
		
		return wmsReturnGoodsVoucherReversalDao.getVoucherReversalDataByLable(params);
	}
	
	
	

}
