package com.byd.wms.business.modules.account.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.service.WmsAccountStockConvertService;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;

/**
 * 无PO收货账务处理 service
 * @author develop01
 */
@Service("wmsAccountConvertService")
public class WmsAccountStockConvertServiceImpl implements WmsAccountStockConvertService {
	@Autowired
	private CommonService commonService;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WmsSapRemote wmsSapRemote;
    
	/**
	 * 411K,412K,309,310,411E,413E等库存类型转移业务处理
	 * @author (changsha) thw
	 * @date 2019-06-28
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R save(Map<String, Object> params) {
		StringBuilder msg=new StringBuilder("操作成功，库存已调整！");
		List<Map<String,Object>> matList = (List<Map<String,Object>>) params.get("matList");
		String WMS_MOVE_TYPE = params.get("WMS_MOVE_TYPE").toString();
		String JZ_DATE = params.get("JZ_DATE").toString();
		String PZ_DATE = params.get("PZ_DATE").toString();
		
		Map<String,Object> wms_doc_head=new HashMap<String,Object>();
		wms_doc_head.put("PZ_DATE", PZ_DATE);
		wms_doc_head.put("JZ_DATE", JZ_DATE);
		wms_doc_head.put("PZ_YEAR", PZ_DATE.substring(0,4));
		String HEADER_TXT = params.get("HEADER_TXT")==null?null:(String)params.get("HEADER_TXT");
		wms_doc_head.put("HEADER_TXT", HEADER_TXT);
		wms_doc_head.put("CREATOR", params.get("CREATOR"));
		wms_doc_head.put("CREATE_DATE", params.get("CREATE_DATE"));
		wms_doc_head.put("WERKS", params.get("WERKS"));
		wms_doc_head.put("PSTNG_DATE", JZ_DATE.replaceAll("-", ""));
		wms_doc_head.put("DOC_DATE", PZ_DATE.replaceAll("-", ""));
		wms_doc_head.put("JZ_DATE", JZ_DATE.toString().replaceAll("-", ""));
		wms_doc_head.put("SAP_MOVE_TYPE", WMS_MOVE_TYPE);
		wms_doc_head.put("PZ_DATE", PZ_DATE.toString().replaceAll("-", ""));
		
		String GM_CODE = "04";
		List<Map<String,Object>> stockMatList = new ArrayList<>(); //更新库存使用的物料清单
		List<Map<String,Object>> wmsDocMatList = new ArrayList<>(); //过账使用的物料清单
		
		//封装参数
		for (Map<String, Object> map : matList) {
			Double QTY = Double.valueOf(map.get("QTY").toString());

			Map<String, Object> stockMap_M = new HashMap<>(); 
			stockMap_M.put("WERKS", map.get("WERKS"));
			stockMap_M.put("WH_NUMBER", map.get("WH_NUMBER"));
			stockMap_M.put("BATCH", map.get("BATCH"));
			stockMap_M.put("LIFNR", map.get("LIFNR"));
			stockMap_M.put("STOCK_QTY", QTY-2*QTY);
			stockMap_M.put("UNIT", map.get("UNIT"));
			stockMap_M.put("MEINS", map.get("UNIT"));
			stockMap_M.put("SO_NO", null);
			stockMap_M.put("SO_ITEM_NO", null);
			stockMap_M.put("EDITOR", params.get("EDITOR"));
			stockMap_M.put("EDIT_DATE", params.get("EDIT_DATE"));
			
			if("411_K".equals(WMS_MOVE_TYPE)) {
				//源库存扣减-K
				stockMap_M.put("MATNR", map.get("MATNR"));
				stockMap_M.put("LGORT", map.get("F_LGORT"));
				stockMap_M.put("BIN_CODE", map.get("F_BIN_CODE"));
				stockMap_M.put("SOBKZ", "K");
			}
			if("411_E".equals(WMS_MOVE_TYPE)) {
				//源库存扣减-E
				stockMap_M.put("MATNR", map.get("F_MATNR"));
				stockMap_M.put("LGORT", map.get("F_LGORT"));
				stockMap_M.put("BIN_CODE", map.get("F_BIN_CODE"));
				stockMap_M.put("SOBKZ", "E");
				stockMap_M.put("SO_NO", params.get("SO_NO"));
				stockMap_M.put("SO_ITEM_NO", pixStrZero(params.get("SO_ITEM_NO").toString(),6));
			}
			if("412_K".equals(WMS_MOVE_TYPE)) {
				//源库存扣减-Z
				stockMap_M.put("MATNR", map.get("MATNR"));
				stockMap_M.put("LGORT", map.get("F_LGORT"));
				stockMap_M.put("BIN_CODE", map.get("F_BIN_CODE"));
				stockMap_M.put("SOBKZ", "Z");
			}
			if("309".equals(WMS_MOVE_TYPE)) {
				//旧料号库存扣减-Z
				stockMap_M.put("MATNR", map.get("MATNR"));
				stockMap_M.put("LGORT", map.get("LGORT"));
				stockMap_M.put("BIN_CODE", map.get("BIN_CODE"));
				stockMap_M.put("SOBKZ", "Z");
			}
			if("310".equals(WMS_MOVE_TYPE)) {
				//旧料号库存扣减-Z
				stockMap_M.put("MATNR", params.get("MATNR_T"));
				stockMap_M.put("LGORT", params.get("LGORT"));
				stockMap_M.put("LIFNR", params.get("LIFNR"));
				stockMap_M.put("BIN_CODE", params.get("BIN_CODE"));
				stockMap_M.put("SOBKZ", "Z");
				stockMap_M.put("BATCH", params.get("BATCH"));
			}
			if("413".equals(WMS_MOVE_TYPE)) {
				//源库存扣减-Z
				stockMap_M.put("MATNR", map.get("MATNR"));
				stockMap_M.put("LGORT", map.get("LGORT"));
				stockMap_M.put("BIN_CODE", map.get("BIN_CODE"));
				stockMap_M.put("SOBKZ", "Z");
			}
			if("413_E".equals(WMS_MOVE_TYPE)) {
				//源库存扣减-源销售订单
				stockMap_M.put("MATNR", map.get("F_MATNR"));
				stockMap_M.put("LGORT", map.get("F_LGORT"));
				stockMap_M.put("BIN_CODE", map.get("F_BIN_CODE"));
				stockMap_M.put("SOBKZ", "E");
				stockMap_M.put("LIFNR", null);
				stockMap_M.put("SO_NO", params.get("SO_NO"));
				stockMap_M.put("SO_ITEM_NO", pixStrZero(params.get("SO_ITEM_NO").toString(),6));
			}
			
			stockMatList.add(stockMap_M);
			
			Map<String, Object> stockMap_A = new HashMap<>(); 
			stockMap_A.put("WERKS", map.get("WERKS"));
			stockMap_A.put("WH_NUMBER", map.get("WH_NUMBER"));
			stockMap_A.put("BATCH", map.get("BATCH"));
			stockMap_A.put("BIN_NAME", map.get("BIN_NAME"));
			stockMap_A.put("LIFNR", map.get("LIFNR"));
			if (null != map.get("LIKTX") && !"".equals(map.get("LIKTX"))) {
				stockMap_A.put("LIKTX", map.get("LIKTX"));
			} else if (null != map.get("LIFNR") && !"".equals(map.get("LIFNR"))) {
				List<Map<String, Object>> vendorlist = commonService.getVendor(map.get("LIFNR").toString());
				if (vendorlist.size() > 0)
					stockMap_A.put("LIKTX", vendorlist.get(0).get("NAME1"));
			}
			
			stockMap_A.put("UNIT", map.get("UNIT"));
			stockMap_A.put("MEINS", map.get("UNIT"));
			stockMap_A.put("STOCK_QTY", QTY);
			stockMap_A.put("SO_NO", null);
			stockMap_A.put("SO_ITEM_NO", null);
			stockMap_A.put("EDITOR", params.get("EDITOR"));
			stockMap_A.put("EDIT_DATE", params.get("EDIT_DATE"));
			
			if("411_K".equals(WMS_MOVE_TYPE)) {
				//增加自有库存-Z
				stockMap_A.put("MATNR", map.get("MATNR"));
				stockMap_A.put("MAKTX", map.get("MAKTX"));
				stockMap_A.put("LGORT", map.get("LGORT"));
				stockMap_A.put("BIN_CODE", map.get("BIN_CODE"));
				stockMap_A.put("SOBKZ", "Z");
			}
			if("411_E".equals(WMS_MOVE_TYPE)) {
				//增加自有库存-Z
				stockMap_A.put("MATNR", map.get("MATNR"));
				stockMap_A.put("MAKTX", map.get("MAKTX"));
				stockMap_A.put("LGORT", map.get("LGORT"));
				stockMap_A.put("BIN_CODE", map.get("BIN_CODE"));
				stockMap_A.put("SOBKZ", "Z");
			}
			if("412_K".equals(WMS_MOVE_TYPE)) {
				//增加寄售库存-K
				stockMap_A.put("MATNR", map.get("MATNR"));
				stockMap_A.put("MAKTX", map.get("MAKTX"));
				stockMap_A.put("LGORT", map.get("LGORT"));
				stockMap_A.put("BIN_CODE", map.get("BIN_CODE"));
				stockMap_A.put("SOBKZ", "K");
			}
			if("309".equals(WMS_MOVE_TYPE)) {
				//增加新物料库存-Z
				stockMap_A.put("MATNR", params.get("MATNR_T"));
				stockMap_A.put("LGORT", params.get("LGORT"));
				if (null != params.get("BIN_CODE") && !"".equals(params.get("BIN_CODE")))
					stockMap_A.put("BIN_CODE", params.get("BIN_CODE"));
				else 
					stockMap_A.put("BIN_CODE", "AAAA");
				
				stockMap_A.put("SOBKZ", "Z");
				
				Map<String, Object> params_a = new HashMap<String, Object>();
				params_a.put("WERKS", params.get("WERKS"));
				params_a.put("MATNR", params.get("MATNR_T"));
				Map<String,Object> matnrinfo = commonService.getMaterialInfo(params_a);
				stockMap_A.put("MAKTX", matnrinfo.get("MAKTX"));
			}
			if("310".equals(WMS_MOVE_TYPE)) {
				//增加新物料库存-Z
				stockMap_A.put("MATNR", map.get("MATNR"));
				stockMap_A.put("LGORT", map.get("LGORT"));
				stockMap_A.put("BIN_CODE", params.get("BIN_CODE"));
				stockMap_A.put("SOBKZ", "Z");
				stockMap_A.put("BATCH", params.get("BATCH"));
				stockMap_A.put("LIFNR", params.get("LIFNR"));
			}
			if("413".equals(WMS_MOVE_TYPE)) {
				//增加销售订单库存-E
				stockMap_A.put("MATNR", map.get("MATNR"));
				stockMap_A.put("MAKTX", map.get("MAKTX"));
				stockMap_A.put("LGORT", params.get("LGORT"));
				stockMap_A.put("BIN_CODE", map.get("BIN_CODE"));
				stockMap_A.put("SOBKZ", "E");
				stockMap_A.put("SO_NO", params.get("SO_NO"));
				stockMap_A.put("SO_ITEM_NO", pixStrZero(params.get("SO_ITEM_NO").toString(), 6));
				stockMap_A.put("LIFNR", null);
			}
			if("413_E".equals(WMS_MOVE_TYPE)) {
				//增加新销售订单库存-K
				stockMap_A.put("MATNR", map.get("MATNR"));
				stockMap_A.put("MAKTX", map.get("MAKTX"));
				stockMap_A.put("LGORT", map.get("LGORT"));
				stockMap_A.put("BIN_CODE", map.get("F_BIN_CODE"));
				stockMap_A.put("SOBKZ", "E");
				stockMap_A.put("SO_NO", map.get("SO_NO"));
				stockMap_A.put("SO_ITEM_NO", pixStrZero(map.get("SO_ITEM_NO").toString(), 6));
				stockMap_A.put("LIFNR", null);
			}
			stockMatList.add(stockMap_A);
			
			Map<String, Object> wmsDocMatMap = new HashMap<>();
			wmsDocMatMap.put("BUSINESS_NAME", "00"); //设置为 MIGO
			wmsDocMatMap.put("BUSINESS_TYPE", "00"); //无
			wmsDocMatMap.put("BUSINESS_CLASS", "10");  //账务处理
			wmsDocMatMap.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
			wmsDocMatMap.put("SAP_MOVE_TYPE", WMS_MOVE_TYPE);
			wmsDocMatMap.put("REVERSAL_FLAG", "X");
			wmsDocMatMap.put("CANCEL_FLAG", "X");
			wmsDocMatMap.put("WERKS", map.get("WERKS"));
			wmsDocMatMap.put("WH_NUMBER", map.get("WH_NUMBER"));
			wmsDocMatMap.put("MATNR", map.get("MATNR"));
			wmsDocMatMap.put("MAKTX", map.get("MAKTX"));
			wmsDocMatMap.put("BATCH", map.get("BATCH"));
			wmsDocMatMap.put("BIN_CODE", map.get("BIN_CODE"));
			wmsDocMatMap.put("BIN_NAME", map.get("BIN_NAME"));
			wmsDocMatMap.put("LIFNR", map.get("LIFNR"));
			wmsDocMatMap.put("LIKTX", map.get("LIKTX"));
			wmsDocMatMap.put("UNIT", map.get("UNIT"));
			wmsDocMatMap.put("QTY_WMS", QTY);
			wmsDocMatMap.put("QTY_SAP", QTY);
			wmsDocMatMap.put("ITEM_TEXT", map.get("ITEM_TEXT"));
			wmsDocMatMap.put("RECEIVER", params.get("RECEIVER"));
			wmsDocMatMap.put("CREATOR", params.get("CREATOR"));
			wmsDocMatMap.put("CREATE_DATE", params.get("CREATE_DATE"));
			if("411_K".equals(WMS_MOVE_TYPE)) {
				//增加自有库存-Z
				wmsDocMatMap.put("LGORT", map.get("F_LGORT"));
				wmsDocMatMap.put("SOBKZ", "K");
				
				wmsDocMatMap.put("MOVE_PLANT", map.get("WERKS"));
				wmsDocMatMap.put("MOVE_MAT", map.get("MATNR"));
				wmsDocMatMap.put("MOVE_STLOC", map.get("LGORT"));
				
				wmsDocMatMap.put("F_WERKS", map.get("WERKS"));
				wmsDocMatMap.put("F_WH_NUMBER", map.get("WH_NUMBER"));
				wmsDocMatMap.put("F_LGORT", map.get("LGORT"));
				wmsDocMatMap.put("F_BATCH", map.get("BATCH"));
			}
			if("411_E".equals(WMS_MOVE_TYPE)) {
				//增加自有库存-Z
				wmsDocMatMap.put("MATNR", map.get("F_MATNR"));
				wmsDocMatMap.put("MAKTX", map.get("F_MAKTX"));
				wmsDocMatMap.put("LGORT", map.get("F_LGORT"));
				wmsDocMatMap.put("SOBKZ", "E");
				wmsDocMatMap.put("SO_NO", params.get("SO_NO"));
				wmsDocMatMap.put("SO_ITEM_NO", pixStrZero(params.get("SO_ITEM_NO").toString(), 6));
				wmsDocMatMap.put("VENDOR", null);
				
				wmsDocMatMap.put("MOVE_PLANT", map.get("WERKS"));
				wmsDocMatMap.put("MOVE_MAT", map.get("MATNR"));
				wmsDocMatMap.put("MOVE_STLOC", map.get("LGORT"));
				wmsDocMatMap.put("MOVE_BATCH", map.get("BATCH"));
				
				wmsDocMatMap.put("F_WERKS", map.get("WERKS"));
				wmsDocMatMap.put("F_WH_NUMBER", map.get("WH_NUMBER"));
				wmsDocMatMap.put("F_LGORT", map.get("LGORT"));
				wmsDocMatMap.put("F_BATCH", map.get("BATCH"));
			}
			if("412_K".equals(WMS_MOVE_TYPE)) {
				wmsDocMatMap.put("LGORT", map.get("LGORT"));
				wmsDocMatMap.put("SOBKZ", "K");
				
				wmsDocMatMap.put("MOVE_PLANT", map.get("WERKS"));
				wmsDocMatMap.put("MOVE_MAT", map.get("MATNR"));
				wmsDocMatMap.put("MOVE_STLOC", map.get("F_LGORT"));
				
				wmsDocMatMap.put("F_WERKS", map.get("WERKS"));
				wmsDocMatMap.put("F_WH_NUMBER", map.get("WH_NUMBER"));
				wmsDocMatMap.put("F_LGORT", map.get("F_LGORT"));
				wmsDocMatMap.put("F_BATCH", map.get("BATCH"));
			}
			
			if("309".equals(WMS_MOVE_TYPE)) {
				wmsDocMatMap.put("LGORT", map.get("LGORT"));
				wmsDocMatMap.put("MOVE_BATCH", map.get("MOVE_BATCH")==null?null:map.get("MOVE_BATCH").toString());
				//MB1B移动类型，需要接收工厂
				wmsDocMatMap.put("MOVE_PLANT", params.get("WERKS"));
				wmsDocMatMap.put("MOVE_MAT", params.get("MATNR_T"));
				wmsDocMatMap.put("MOVE_STLOC", params.get("LGORT"));
				
				wmsDocMatMap.put("F_WERKS", map.get("WERKS"));
				wmsDocMatMap.put("F_WH_NUMBER", map.get("WH_NUMBER"));
				wmsDocMatMap.put("F_LGORT", params.get("LGORT"));
				wmsDocMatMap.put("F_BATCH", map.get("BATCH"));
			}
			if("310".equals(WMS_MOVE_TYPE)) {
				wmsDocMatMap.put("LGORT", params.get("LGORT"));
				wmsDocMatMap.put("BATCH", params.get("BATCH"));
				wmsDocMatMap.put("MOVE_BATCH", params.get("MOVE_BATCH")==null?null:params.get("MOVE_BATCH").toString());
				//MB1B移动类型，需要接收工厂
				wmsDocMatMap.put("MOVE_PLANT", params.get("WERKS"));
				wmsDocMatMap.put("MOVE_MAT", params.get("MATNR_T"));
				wmsDocMatMap.put("MOVE_STLOC", params.get("LGORT"));
				
				wmsDocMatMap.put("F_WERKS", map.get("WERKS"));
				wmsDocMatMap.put("F_WH_NUMBER", map.get("WH_NUMBER"));
				wmsDocMatMap.put("F_LGORT", params.get("LGORT"));
				wmsDocMatMap.put("F_BATCH", map.get("BATCH"));
			}
			if("413".equals(WMS_MOVE_TYPE)) {
				wmsDocMatMap.put("LGORT", map.get("LGORT"));
				wmsDocMatMap.put("SO_NO", params.get("SO_NO"));
				wmsDocMatMap.put("SO_ITEM_NO", pixStrZero(params.get("SO_ITEM_NO").toString(), 6));
				wmsDocMatMap.put("SOBKZ", "E");
				wmsDocMatMap.put("VENDOR", null);
				
				wmsDocMatMap.put("MOVE_PLANT", map.get("WERKS"));
				wmsDocMatMap.put("MOVE_MAT", map.get("MATNR"));
				wmsDocMatMap.put("MOVE_STLOC", params.get("LGORT"));
				
				wmsDocMatMap.put("F_WERKS", map.get("WERKS"));
				wmsDocMatMap.put("F_WH_NUMBER", map.get("WH_NUMBER"));
				wmsDocMatMap.put("F_LGORT", params.get("LGORT"));
				wmsDocMatMap.put("F_BATCH", map.get("BATCH"));
			}
			if("413_E".equals(WMS_MOVE_TYPE)) {
				wmsDocMatMap.put("MATNR", map.get("MATNR"));
				wmsDocMatMap.put("MAKTX", map.get("MAKTX"));
				wmsDocMatMap.put("LGORT", map.get("LGORT"));
				wmsDocMatMap.put("SOBKZ", "E");
				wmsDocMatMap.put("SO_NO", map.get("SO_NO"));
				wmsDocMatMap.put("SO_ITEM_NO", pixStrZero(map.get("SO_ITEM_NO").toString(),6));
				wmsDocMatMap.put("VENDOR", null);
				
				wmsDocMatMap.put("MOVE_PLANT", map.get("WERKS"));
				wmsDocMatMap.put("MOVE_MAT", map.get("F_MATNR"));
				wmsDocMatMap.put("MOVE_STLOC", map.get("F_LGORT"));
				wmsDocMatMap.put("MOVE_BATCH", map.get("BATCH"));
				wmsDocMatMap.put("VAL_SALES_ORD", params.get("SO_NO"));
				wmsDocMatMap.put("VAL_S_ORD_ITEM", pixStrZero(params.get("SO_ITEM_NO").toString(),6));
				
				wmsDocMatMap.put("F_WERKS", map.get("WERKS"));
				wmsDocMatMap.put("F_WH_NUMBER", map.get("WH_NUMBER"));
				wmsDocMatMap.put("F_LGORT", map.get("F_LGORT"));
				wmsDocMatMap.put("F_BATCH", map.get("BATCH"));
			}
			
			wmsDocMatList.add(wmsDocMatMap);
		}

		//1、更新库存，减少寄售库存，增加自有库存
		Map<String, Object> wmsStockMap = new HashMap<>();
		wmsStockMap.put("matList", stockMatList);
		commonService.saveWmsStock(wmsStockMap);
		//2、产生WMS凭证
		String WMS_NO = commonService.saveWMSDoc(wms_doc_head, wmsDocMatList);
		wms_doc_head.put("WMS_NO", WMS_NO);
		wms_doc_head.put("REF_WMS_NO", WMS_NO);
		msg.append("WMS凭证号："+WMS_NO+"("+WMS_MOVE_TYPE+")");
		//3、过账SAP
		wms_doc_head.put("GM_CODE", GM_CODE);
		String SAP_DOC_NO = "";
		try {
			SAP_DOC_NO = this.sapPost(wms_doc_head, wmsDocMatList);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		msg.append("；SAP凭证号："+SAP_DOC_NO);
		return R.ok(msg.toString());
	}
	
	/**
	 * SAP实时过账
	 * @param head 过账抬头
	 * @param matList 过账行项目
	 * @return
	 */
	private String sapPost(Map<String,Object> sapSynMap,List<Map<String,Object>> matList) {
		String  SAP_NO = "";
		List<Map<String,Object>> sap_success_list=new ArrayList<Map<String,Object>>();
		List<String> exception_list=new ArrayList<String>();
		String WERKS = sapSynMap.get("WERKS")==null?(matList.get(0).get("WERKS")==null?null:matList.get(0).get("WERKS").toString()):sapSynMap.get("WERKS").toString();
		String PZ_DATE = sapSynMap.get("PZ_DATE")==null?sapSynMap.get("DOC_DATE")==null?"":sapSynMap.get("DOC_DATE").toString():sapSynMap.get("PZ_DATE").toString();
		Object REF_WMS_NO = sapSynMap.get("WMS_NO")==null?(sapSynMap.get("REF_WMS_NO")==null?matList.get(0).get("WMS_NO"):sapSynMap.get("REF_WMS_NO")):sapSynMap.get("WMS_NO");
		Object REF_DOC_NO = sapSynMap.get("REF_DOC_NO")==null?(matList.get(0).get("REF_DOC_NO")==null?null:matList.get(0).get("WMS_NO")):sapSynMap.get("REF_DOC_NO");
		//处理头文本为空问题
		if(sapSynMap.get("HEADER_TXT")==null) {
			sapSynMap.put("HEADER_TXT", (String)matList.get(0).get("HEADER_TXT"));
		}
		
		String GM_CODE = null;
		for(String MOVE_TYPE: sapSynMap.get("SAP_MOVE_TYPE").toString().split("\\+")) {
			String[] stringArray = MOVE_TYPE.split("\\_");
			String SOBKZ_CONFIG = null;
			if(stringArray.length>1) {
				//特殊库存类型过账移动类型
				SOBKZ_CONFIG = stringArray[1];
			}
			MOVE_TYPE = stringArray[0];
			List<Map<String,String>> ITEMLIST = new ArrayList<Map<String,String>>();
			
			//获取行项目单位换算信息
			List<String> matUnitList = commonDao.getMatUnit(matList);
			//单位替换
			for (String string : matUnitList) {
				String[] unitArr = string.split("\\#\\*");
				int index = Integer.valueOf(unitArr[0]);
				String  UNIT_EN = unitArr[1];
				if(matList.get(index) !=null) {
					matList.get(index).put("ENTRY_UOM", UNIT_EN);
				}
				
			}
			
			for (Map<String, Object> m : matList) {
				Map<String,String> _m=new HashMap<String,String>();
				_m.put("WMS_NO", m.get("WMS_NO")==null?"":m.get("WMS_NO").toString());
				_m.put("WMS_ITEM_NO", m.get("WMS_ITEM_NO")==null?"":m.get("WMS_ITEM_NO").toString());
				if(MOVE_TYPE.equals("411") || MOVE_TYPE.equals("412") || MOVE_TYPE.equals("413") || MOVE_TYPE.equals("309") || MOVE_TYPE.equals("310")) {
					GM_CODE= "04";
				} else {
					GM_CODE = sapSynMap.get("GM_CODE")==null?(m.get("GM_CODE")==null?"":m.get("GM_CODE").toString()):sapSynMap.get("GM_CODE").toString();
				}
				
				_m.put("GM_CODE", GM_CODE);
				_m.put("MATERIAL", m.get("MATNR").toString());//物料号
				_m.put("MATNR", m.get("MATNR").toString());//物料号
				_m.put("PLANT", WERKS);//工厂代码
				_m.put("WERKS", WERKS);//工厂代码
				_m.put("F_WERKS", m.get("F_WERKS")==null?"":m.get("F_WERKS").toString());//工厂代码
				_m.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH").toString());//WMS批次
				_m.put("CHARG", m.get("CHARG")==null?"":m.get("CHARG").toString());//交货批次 交货单过账使用
				_m.put("MOVE_TYPE", MOVE_TYPE);//SAP过账移动类型

				if(SOBKZ_CONFIG!=null) {
					_m.put("SPEC_STOCK",SOBKZ_CONFIG);//特殊库存标识
				}
				_m.put("STGE_LOC", (String)m.get("LGORT"));//库位
				_m.put("LGORT", (String)m.get("LGORT"));//库位

				Double ENTRY_QNT = Double.parseDouble(m.get("RECEIPT_QTY")==null?(
						(m.get("ENTRY_QNT")==null?(m.get("QTY_SAP")==null?"0":m.get("QTY_SAP").toString())
								:m.get("ENTRY_QNT").toString())):m.get("RECEIPT_QTY").toString());
				
				Double POST_QTY = Double.parseDouble(m.get("POST_QTY")==null?"0":m.get("POST_QTY").toString());
				
				if(POST_QTY>0) {
					ENTRY_QNT = ENTRY_QNT-POST_QTY;
				}
				
				_m.put("ENTRY_QNT", String.format("%.3f", ENTRY_QNT));//收货数量

				String ENTRY_UOM = m.get("ENTRY_UOM")==null?(m.get("UNIT")==null?"PCS":m.get("UNIT").toString()):m.get("ENTRY_UOM").toString();
				_m.put("ENTRY_UOM", ENTRY_UOM);//单位
				String PO_NUMBER = m.get("PO_NO")==null?(m.get("PO_NUMBER")==null?(m.get("EBELN")==null?"":m.get("EBELN").toString()):m.get("PO_NUMBER").toString()):m.get("PO_NO").toString();
				_m.put("PO_NUMBER",PO_NUMBER);//采购订单号
				String PO_ITEM = m.get("PO_ITEM_NO")==null?(m.get("PO_ITEM")==null?"":m.get("PO_ITEM").toString()):m.get("PO_ITEM_NO").toString();
				_m.put("PO_ITEM", PO_ITEM);//采购订单行项目号

				_m.put("VENDOR", m.get("VENDOR")==null?(m.get("LIFNR")==null?"":m.get("LIFNR").toString().trim()):m.get("VENDOR").toString().trim());//供应商
				_m.put("CUSTOMER", m.get("CUSTOMER")==null?"":m.get("CUSTOMER").toString());//客户代码
				_m.put("COSTCENTER", m.get("COSTCENTER")==null?(m.get("COST_CENTER")==null?"":m.get("COST_CENTER").toString()):m.get("COSTCENTER").toString());//成本中心
				_m.put("WBS_ELEM", m.get("WBS_ELEM")==null?(m.get("WBS")==null?"":m.get("WBS").toString()):m.get("WBS_ELEM").toString());//WBS元素
				_m.put("GR_RCPT", m.get("GR_RCPT")==null?(m.get("RECEIVER")==null?"":m.get("RECEIVER").toString()):m.get("GR_RCPT").toString());//收货方/运达方
				//String ORDERID = m.get("ORDERID")==null?(m.get("MO_NO")==null?(m.get("IO_NO")==null?"":m.get("IO_NO").toString()):m.get("MO_NO").toString()):m.get("ORDERID").toString();
				String ORDERID = "";
				if(m.get("ORDERID")!=null && !m.get("ORDERID").equals("")) {
					ORDERID = m.get("ORDERID").toString();
				}else if(m.get("MO_NO")!=null && !m.get("MO_NO").equals("")) {
					ORDERID = m.get("MO_NO").toString();
				}else if(m.get("IO_NO")!=null && !m.get("IO_NO").equals("")) {
					ORDERID = m.get("IO_NO").toString();
				}
				//去除左侧0
				ORDERID = ORDERID.replaceAll("^(0+)", "");
				
				_m.put("ORDERID", ORDERID);//内部/生产订单号
				String ORDER_ITNO = m.get("MO_ITEM_NO")==null?(m.get("POSNR")==null?"":m.get("POSNR").toString()):m.get("MO_ITEM_NO").toString();
				_m.put("ORDER_ITNO", ORDER_ITNO);//生产订单行项目号
				_m.put("RESERV_NO", m.get("RESERV_NO")==null?(m.get("RSNUM")==null?"":m.get("RSNUM").toString()):m.get("RESERV_NO").toString());//预留号
				_m.put("RES_ITEM", m.get("RES_ITEM")==null?(m.get("RSPOS")==null?"":m.get("RSPOS").toString()):m.get("RES_ITEM").toString());//预留行项目号
				
				String SALES_ORD = m.get("SALES_ORD")==null?(m.get("SO_NO")==null?null:m.get("SO_NO").toString()):m.get("SALES_ORD").toString();
				String S_ORD_ITEM = m.get("S_ORD_ITEM")==null?(m.get("SO_ITEM_NO")==null?null:m.get("SO_ITEM_NO").toString()):m.get("S_ORD_ITEM").toString();
				String VAL_SALES_ORD = m.get("VAL_SALES_ORD")==null?(m.get("SO_NO")==null?null:m.get("SO_NO").toString()):m.get("VAL_SALES_ORD").toString();
				String VAL_S_ORD_ITEM = m.get("VAL_S_ORD_ITEM")==null?(m.get("SO_ITEM_NO")==null?null:m.get("SO_ITEM_NO").toString()):m.get("VAL_S_ORD_ITEM").toString();
				_m.put("SALES_ORD", SALES_ORD);
				_m.put("S_ORD_ITEM", S_ORD_ITEM);
				_m.put("VAL_SALES_ORD", VAL_SALES_ORD);
				_m.put("VAL_S_ORD_ITEM", VAL_S_ORD_ITEM);
				
				_m.put("ITEM_TEXT",(String)m.get("ITEM_TEXT") );//行文本

				String MVT_IND = null; //移动标识
				if(null != PO_NUMBER && !"".equals(PO_NUMBER)) {
					MVT_IND = "B"; //采购订单过账
				}
				if(null != ORDERID && !"".equals(ORDERID) && GM_CODE.equals("02")) {
					MVT_IND = "F"; //生产订单过账
				}
				if(MOVE_TYPE.equals("541")) {
					//分包发料
					MVT_IND = "O";
				}
				if(MOVE_TYPE.equals("531")) {
					//副产品
					MVT_IND = null;
					_m.put("ORDER_ITNO", null);//生产订单行项目号
				}
				if(null !=MVT_IND) {
					_m.put("MVT_IND", MVT_IND);//移动标识
				}

				Map<String,Object> sapMoveReas = commonDao.getSapMoveReasByMoveType(MOVE_TYPE);
				String MOVE_REAS = sapMoveReas == null?"":sapMoveReas.get("MOVE_REAS")==null?"":sapMoveReas.get("MOVE_REAS").toString();
				_m.put("MOVE_REAS", MOVE_REAS);
				
				_m.put("MOVE_BATCH", m.get("MOVE_BATCH")==null?null:m.get("MOVE_BATCH").toString());
				//MB1B移动类型，需要接收工厂
				_m.put("MOVE_PLANT", m.get("MOVE_PLANT")==null ? m.get("PLANT")!=null && GM_CODE.equals("04")?m.get("PLANT").toString():null : m.get("MOVE_PLANT").toString());
				_m.put("MOVE_MAT", m.get("MOVE_MAT")==null?null:m.get("MOVE_MAT").toString());
				//MB1B移动类型，需要接收库位
				String MOVE_STLOC = m.get("MOVE_STLOC")==null ? null : m.get("MOVE_STLOC").toString();
				_m.put("MOVE_STLOC", MOVE_STLOC);
				
				ITEMLIST.add(_m);
			}

			sapSynMap.put("ITEMLIST", ITEMLIST);

			Map<String,Object> sapDoc = new HashMap<String,Object>();
			try {
				if(ITEMLIST.size()>0) {
					//其他移动类型过账
					sapDoc =	wmsSapRemote.getSapBapiGoodsmvtCreate(sapSynMap);
				}else {
					//过账失败
					sapDoc.put("CODE", "-5");
					sapDoc.put("MESSAGE", "无过账行项目数据！");
				}

			}catch(Exception e) {
				throw new RuntimeException(e.getMessage());
			}

			if("0".equals(sapDoc.get("CODE"))) {
				sapDoc.put("WERKS", WERKS);
				sapDoc.put("MOVE_TYPE", MOVE_TYPE);
				sapDoc.put("GOODSMVT_PSTNG_DATE", PZ_DATE.replaceAll("-", ""));
				sapDoc.put("ITEMLIST", ITEMLIST);
				sapDoc.put("REF_WMS_NO", REF_WMS_NO);
				sap_success_list.add(sapDoc);

			}else {//过账失败，抛出异常，cancel掉已过账的记录
				exception_list.add(MOVE_TYPE+"过账失败："+sapDoc.get("MESSAGE")+";");
				Iterator it =sap_success_list.iterator();
				while(it.hasNext()) {
					Map<String,Object> c=(Map<String,Object>)it.next();
					Map<String,Object>rmap = wmsSapRemote.getSapBapiGoodsmvtCancel(c);
					if(!"0".equals(rmap.get("CODE"))) {
						exception_list.add("凭证冲销失败，凭证号："+c.get("MATERIALDOCUMENT")+",凭证年份："+c.get("MATDOCUMENTYEAR")+
								", 移动类型："+MOVE_TYPE);
					}
					it.remove();
				}
			}
		}
		if(exception_list.size()>0) {//有过账失败抛出异常
			throw new RuntimeException(StringUtils.join(exception_list, ";"));
		}
		if(sap_success_list.size()>0) {//过账成功，保存SAP过账记录
			String MAT_DOC_STR = "";

			for(Map<String,Object> m:sap_success_list) {
				List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
				List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
				List<Map<String,String>> updateWmsdocList = new ArrayList<Map<String,String>>();//更新WMS凭证行项目数据

				List<Map<String,String>> ITEMLIST = (List<Map<String,String>>)m.get("ITEMLIST"); //待过账物料清单
				Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(m.get("MATERIALDOCUMENT").toString(),m.get("MATDOCUMENTYEAR").toString());
				Map<String,String> head=(Map<String, String>) rm.get("GOODSMVT_HEADER");
				List<Map<String,String>> itemList =(List<Map<String, String>>) rm.get("GOODSMVT_ITEMS");
				if(head!=null) {
					head.put("REF_WMS_NO", REF_WMS_NO==null?"":REF_WMS_NO.toString());
					head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
					head.put("REF_DOC_NO", REF_DOC_NO==null?"":REF_DOC_NO.toString());
					sap_head_list.add(head);
					SAP_NO+=head.get("MAT_DOC")+"(" +m.get("MOVE_TYPE")+"); ";
				}

				for (Map<String, String> wmsDocMap : ITEMLIST) {
					String wmdDoc_MATNR = wmsDocMap.get("MATNR").toString();
					String wmdDoc_WERKS = wmsDocMap.get("PLANT").toString();
					String wmdDoc_UNIT = wmsDocMap.get("ENTRY_UOM").toString();
					Double wmdDoc_QTY_SAP = Double.valueOf(wmsDocMap.get("ENTRY_QNT").toString());

					for (Map<String, String> sapDocMap : itemList) {
						String sapDoc_MATERIAL = sapDocMap.get("MATNR").toString();
						String sapDoc_PLANT = sapDocMap.get("PLANT").toString();
						String sapDoc_ENTRY_UOM = sapDocMap.get("ENTRY_UOM").toString();
						Double sapDoc_ENTRY_QNT = Double.valueOf(sapDocMap.get("ENTRY_QNT").toString());

						if(wmdDoc_MATNR.equals(sapDoc_MATERIAL) && wmdDoc_WERKS.equals(sapDoc_PLANT) &&
								wmdDoc_UNIT.equals(sapDoc_ENTRY_UOM) &&
								wmdDoc_QTY_SAP.doubleValue() ==sapDoc_ENTRY_QNT.doubleValue()) {
							//匹配
							Map<String,String> updateWmsdoc = new HashMap<String,String>();
							updateWmsdoc.put("WMS_NO", wmsDocMap.get("WMS_NO"));
							updateWmsdoc.put("WMS_ITEM_NO", wmsDocMap.get("WMS_ITEM_NO"));
							updateWmsdoc.put("WMS_SAP_MAT_DOC", sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";");
							//updateWmsdoc.get("WMS_SAP_MAT_DOC")!=null?(updateWmsdoc.get("WMS_SAP_MAT_DOC")+sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";"):
							updateWmsdocList.add(updateWmsdoc);

							sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
							sapDocMap.put("REF_WMS_ITEM_NO", sapDocMap.get("REF_WMS_ITEM_NO")!=null?(sapDocMap.get("REF_WMS_ITEM_NO")+";"+wmsDocMap.get("WMS_ITEM_NO")):wmsDocMap.get("WMS_ITEM_NO"));
							sapDocMap.put("REF_MATDOC_ITM", wmsDocMap.get("REF_MATDOC_ITM"));

							sap_detail_list.add(sapDocMap);
							break;
						}
					}
				}

				MAT_DOC_STR += m.get("MOVE_TYPE")+":"+head.get("MAT_DOC")+";";

				/**
				 * 更新WMS凭证行项目SAP物料凭证信息
				 */
				commonDao.updateWMSDocItemSapDocInfo(updateWmsdocList);

				/**
				 * 更新WMS凭证抬头SAP物料凭证信息
				 */
				Map<String,Object> wmsDocMap = new HashMap<String,Object>();
				wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
				wmsDocMap.put("WMS_NO", REF_WMS_NO);
				commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);

				commonDao.insertSapDocHead(sap_head_list);	//SAP凭证表-抬头
				commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
			}

		}

		return SAP_NO;

	}

	protected String pixStrZero(String str,int length) {
		int pxnum = length-str.length();
		if(pxnum >0) str = String.format("%0"+(pxnum)+"d", 0) + str;    
		if(pxnum <0) str = str.substring(0, length);    
	    //System.out.println(str);
		return str;
	}
}