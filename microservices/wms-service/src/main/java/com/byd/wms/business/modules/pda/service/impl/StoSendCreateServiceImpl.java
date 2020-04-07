package com.byd.wms.business.modules.pda.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import com.byd.wms.business.modules.in.service.impl.WmsInAutoPutawayServiceImpl;
import com.byd.wms.business.modules.pda.dao.StoSendCreateDao;
import com.byd.wms.business.modules.pda.service.StoSendCreateService;

@Service("stoSendCreateService")
public class StoSendCreateServiceImpl implements StoSendCreateService {

	@Autowired
	private StoSendCreateDao stoSendCreatehDao;

	@Autowired
	private WmsCDocNoService docNoService;
	

	@Autowired
    private UserUtils userUtils;
	
	@Autowired
    private WmsInAutoPutawayServiceImpl wmsInAutoPutawayService;
	
	@Autowired
    private WmsInReceiptService wmsInReceiptService;

	@Override
	public List list(Map<String, Object> params) {
		return stoSendCreatehDao.list(params);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String create(Map<String, Object> params) {
		List<Map<String,Object>> itemList1 = (List<Map<String,Object>>) JSONArray.parse(params.get("itemList").toString());
		Map<String,Object> currentUser = userUtils.getUser();
		String toWerks = params.get("toWerks").toString();	
		String toLgort = params.get("toLgort").toString();	
		String WHCD_NO = params.get("whNo").toString();
		String requirementNo = params.get("requirementNo").toString();	
		String BOOKING_DATE = params.get("deliveryTime").toString();	
		
		String stoNo = docNoService.getDocNo(toWerks, WmsDocTypeEnum.OUT_DN.getCode());
		Map<String,Object> o = itemList1.get(0);
		String PROVIDER_CODE = o.get("LIFNR")==null?" ":o.get("LIFNR").toString();
		//String LGORT_NO = o.get("LGORT")==null?"":o.get("LGORT").toString();
		String werks = o.get("WERKS")==null?"":o.get("WERKS").toString();
		String providerCode = o.get("LIFNR")==null?"":o.get("LIFNR").toString();
		String providerName = o.get("LIKTX")==null?"":o.get("LIKTX").toString();
		
		
		Map<String, Object> param = new HashMap<>();
		param.put("WERKS", werks);
		param.put("BUSINESS_NAME", "01");
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		param.put("SEARCH_DATE", curDate);
		String testFlag = wmsInReceiptService.getTestFlag(param);//工厂质检配置
		
		Map<String, Object> head = new HashMap<>();
		head.put("DELIVERY_NO", stoNo);
		head.put("DELIVERY_TYPE", "03");
		head.put("STATUS_RMK", "1");
		head.put("FACT_NO", toWerks);
		head.put("LGORT_NO", toLgort);
		head.put("PROVIDER_CODE", providerCode);
		head.put("PROVIDER_NAME", providerName);
		head.put("WHCD_NO", WHCD_NO);
		head.put("BOOKING_DATE", BOOKING_DATE);
		head.put("CTURID", currentUser.get("USERNAME"));
		head.put("REQUIREMENT_NO", requirementNo);
		head.put("CTDT", DateUtils.format(new Date(),
				DateUtils.DATE_PATTERN));
		
		
		for (Map<String, Object> map : itemList1) {
			map.put("RECEIPT_QTY", map.get("QTY_SAP"));
			map.put("FULL_BOX_QTY", map.get("QTY"));
			map.put("PO_ITEM", map.get("PO_ITEM_NO"));
			map.put("TEST_FLAG", testFlag);
			map.put("WERKS", params.get("toWerks"));
			map.put("LGORT", toLgort);
		}
		wmsInAutoPutawayService.saveCoreLabel(new ArrayList<Map<String, Object>>(), itemList1);
		int itemNo = 1;
		List<Map<String, Object>> itemList = new ArrayList<>();
		List<Map<String, Object>> packingList = new ArrayList<>();
		for (Map<String, Object> map : itemList1) {
			Map<String, Object> item = new HashMap<>();
			String ITEM_QTY = map.get("QTY_SAP")==null?"":map.get("QTY_SAP").toString();
			String FULL_BOX_QTY = map.get("QTY")==null?"":map.get("QTY").toString();
			//String lgortNo = map.get("LGORT")==null?"":map.get("LGORT").toString();
			String lifnr = map.get("LIFNR")==null?"":map.get("LIFNR").toString();
			String liktx = map.get("LIKTX")==null?"":map.get("LIKTX").toString();
			String MAT_NO = map.get("MATNR")==null?"":map.get("MATNR").toString();
			String MAT_DESC = map.get("MAKTX")==null?"":map.get("MAKTX").toString();
			String UNIT_NO = map.get("UNIT")==null?"":map.get("UNIT").toString();
			String LABEL_NO = map.get("LABEL_NO")==null?"":map.get("LABEL_NO").toString();
			String PO_NO = map.get("PO_NO")==null?"":map.get("PO_NO").toString();
			String PO_ITEM = map.get("PO_ITEM_NO")==null?"":map.get("PO_ITEM_NO").toString();
			//String WERKS = map.get("WERKS")==null?"":map.get("WERKS").toString();
			String BATCH = map.get("BATCH")==null?"":map.get("BATCH").toString();
			String SAP_OUT_NO = map.get("SAP_OUT_NO")==null?"":map.get("SAP_OUT_NO").toString();
			String SAP_OUT_ITEM_NO = map.get("SAP_OUT_ITEM_NO")==null?"":map.get("SAP_OUT_ITEM_NO").toString();
			
			item.put("DELIVERY_NO", stoNo);
			item.put("ITEM_NO", String.valueOf(itemNo));
			item.put("ITEM_QTY", ITEM_QTY);
			item.put("FULL_BOX_QTY", FULL_BOX_QTY);
			item.put("PO_NO", PO_NO);
			item.put("PO_ITEM", PO_ITEM);
			item.put("FACT_NO", toWerks);
			item.put("LGORT_NO", toLgort);
			item.put("PROVIDER_CODE", lifnr);
			item.put("PROVIDER_NAME", liktx);
			item.put("MAT_NO", MAT_NO);
			item.put("MAT_DESC", MAT_DESC);
			item.put("UNIT_NO", UNIT_NO);
			item.put("CTURID", currentUser.get("USERNAME"));
			item.put("CTDT", DateUtils.format(new Date(),
					DateUtils.DATE_PATTERN));
			item.put("DEL", "0");	
			item.put("SAP_OUT_NO", SAP_OUT_NO);
			item.put("SAP_OUT_ITEM_NO", SAP_OUT_ITEM_NO);
			
			
			item.put("BARCODE_NO", LABEL_NO);
			item.put("BYD_BATCH", BATCH);
			
			itemList.add(item);
			packingList.add(item);
			itemNo++;			
		}
		stoSendCreatehDao.insertHead(head);
		stoSendCreatehDao.insertItem(itemList);
		stoSendCreatehDao.insertPacking(packingList);
		return stoNo;
	}

	@Override
	public List querySto(Map<String, Object> params) {		
		return stoSendCreatehDao.querySto(params);
	}		
	
	@Override
	public List queryCustomer(Map<String, Object> params) {		
		return stoSendCreatehDao.queryCustomer(params);
	}
	
	@Override
	public List queryWMSNo(Map<String, Object> params) {		
		return stoSendCreatehDao.queryWMSNo(params);
	}
	
	@Override
	public List queryContact(Map<String, Object> params) {		
		return stoSendCreatehDao.queryContact(params);
	}
	
	@Override
	public List queryLiktx(Map<String, Object> params) {		
		return stoSendCreatehDao.queryLiktx(params);
	}
	
	@Override
	public List checkAddr(Map<String, Object> params) {		
		return stoSendCreatehDao.checkAddr(params);
	}
	
	@Override
	public List checkExist(Map<String, Object> params) {		
		return stoSendCreatehDao.checkExist(params);
	}
	
	public List queryBydeliveryNo(List<Map<String, Object>> params) {
		List list = new ArrayList<>();
		for(Map<String, Object> map:params) {
			Map<String, Object> result= stoSendCreatehDao.queryBydeliveryNo(map);
			if(result!=null) {
				map.put("ZMAKTX", result.get("MAKTX"));
				map.put("REFERENCE_NO", result.get("REFERENCE_NO"));
				map.put("DOSAGE", result.get("DOSAGE"));
				map.put("STATION", result.get("POINT_OF_USE"));
				map.put("ON_LINE_TYPE", result.get(""));
				map.put("ON_LINE_MOUTH", result.get(""));
				map.put("CAR_TYPE", result.get(""));
				map.put("CONFIGURATION", result.get(""));
				map.put("PRODUCT_CODE", result.get(""));//END_MATERIAL_CODE
				list.add(map);
			}			
		}
		return list;
	}
	
}