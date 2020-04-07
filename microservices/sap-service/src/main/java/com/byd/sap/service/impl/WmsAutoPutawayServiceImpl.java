package com.byd.sap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.byd.sap.bapi.BAPI_PO_CREATE1;
import com.byd.sap.bapi.ZGMM_VL_001_00;
import com.byd.sap.bapi.ZGMM_VL_002_00;
import com.byd.sap.bapi.bean.BAPIDLVREFTOSTO;
import com.byd.sap.bapi.bean.BAPIMEPOHEADER;
import com.byd.sap.bapi.bean.BAPIMEPOITEM;
import com.byd.sap.bapi.bean.BapiPOCreate1Bean;
import com.byd.sap.bapi.bean.POSCHEDULE;
import com.byd.sap.bapi.bean.ZGMMVL00100Bean;
import com.byd.sap.service.IWmsAutoPutawayService;

/**
 * STO一步联动收货
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:09:01 
 */
@Service("wmsAutoPutawayService")
public class WmsAutoPutawayServiceImpl implements IWmsAutoPutawayService{

	@Autowired
	private BAPI_PO_CREATE1 bapiPoCreate1;
	
	@Autowired
	private ZGMM_VL_001_00 zgmmvl00100;
	
	@Autowired
	private ZGMM_VL_002_00 zgmmvl00200;
	
	/**
	 * 创建PO
	 */
	@Override
	public Map<String,Object> createStoPO(Map<String,Object> params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String werks = params.get("WERKS") == null ? "":params.get("WERKS").toString();
		BapiPOCreate1Bean beans = new BapiPOCreate1Bean();
		
		BAPIMEPOHEADER head = new BAPIMEPOHEADER();
		head.setDocType(params.get("docType").toString());
		head.setDocDate(params.get("docDate").toString());
		head.setPurchOrg(params.get("purchOrg").toString());
		head.setPurGroup(params.get("purGroup").toString());
		head.setVendor(params.get("vendor").toString());
		head.setCompCode(params.get("compCode").toString());
		beans.setHeader(head);
		
		List<BAPIMEPOITEM> items = new ArrayList<BAPIMEPOITEM>();
		List<POSCHEDULE> poscheduleList = new ArrayList<POSCHEDULE>();
		List<Map<String,String>> itemList = (List<Map<String,String>>) JSONArray.parse(params.get("ITEMLIST").toString());
		for (Map<String,String> item : itemList) {
			BAPIMEPOITEM poitem = new BAPIMEPOITEM();
			poitem.setPoItem(item.get("ITEM_NO"));
			poitem.setPlant(werks);
			poitem.setStgeLoc("00ZJ");
			//poitem.setStgeLoc(item.get("LGORT"));
			poitem.setMaterial(item.get("MATNR"));
			BigDecimal Qty = item.get("RECEIVE_QTY") == null ? BigDecimal.ZERO:new BigDecimal(String.valueOf(item.get("RECEIVE_QTY"))); 
			poitem.setQuantity(Qty);
			poitem.setPoUnit(item.get("UNIT"));
			items.add(poitem);
			
			POSCHEDULE poschedule = new POSCHEDULE();
			poschedule.setPoItem(item.get("ITEM_NO"));
			poschedule.setDeliveryDate(params.get("docDate").toString());
			poschedule.setQuantity(Qty);
			poschedule.setDeliveryDate(item.get("DELIVERY_DATE"));
			poscheduleList.add(poschedule);
		}
		
		beans.setItem(items);
		beans.setPoschedule(poscheduleList);
		returnMap = bapiPoCreate1.execute(werks, beans);
		return returnMap;
	}
	
	/**
	 * 创建交货单
	 */
	@Override
	public Map<String,Object> createStoDN(Map<String,Object> params) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String werks = params.get("WERKS") == null ? "":params.get("WERKS").toString();
		ZGMMVL00100Bean beans = new ZGMMVL00100Bean();
		List<BAPIDLVREFTOSTO> items = new ArrayList<BAPIDLVREFTOSTO>();
		List<Map<String,String>> itemList = (List<Map<String,String>>) JSONArray.parse(params.get("ITEMLIST").toString());
		for (Map<String,String> item : itemList) {
			BAPIDLVREFTOSTO itembean = new BAPIDLVREFTOSTO();
			itembean.setRefDoc(item.get("EBELN"));
			itembean.setRefItem(item.get("EBELP"));
			BigDecimal Qty = item.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(String.valueOf(item.get("QTY"))); 
			itembean.setDlvQty(Qty);
			itembean.setSalesUnit(item.get("UNIT"));
			items.add(itembean);
		}
		beans.setDueDate(params.get("DUE_DATE").toString());
		beans.setItem(items);
		returnMap = zgmmvl00100.execute(werks, beans);
		return returnMap;
	}
	
	@Override
	public Map<String,Object> postDN(String werks,String vbeln) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = zgmmvl00200.execute(werks, vbeln);
		return returnMap;
	}
}
