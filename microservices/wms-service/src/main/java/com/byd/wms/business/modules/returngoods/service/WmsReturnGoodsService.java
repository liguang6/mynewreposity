package com.byd.wms.business.modules.returngoods.service;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsService {
	public List<String> getBusinessNameListByWerks(Map<String, Object> params);
	public List<Map<String, Object>> getReceiveRoomOutData(Map<String, Object> params);
	public String getTestFlagByWerksBusinessCode(Map<String, Object> params);
	/**
	 * 收料房退货 创建退货单
	 */
	public String createReceiveRoomOutReturn(Map<String, Object> params);
	public int getWmsOutReturnSapOutCount(String SAP_OUT_NO);
	public List<Map<String, Object>> getReceiveRoomOutReturnData(Map<String, Object> params);
	public String confirmReceiveRoomOutReturn(Map<String, Object> params);
	public String confirmWareHouseOutReturn(Map<String, Object> params);
	
	public List<Map<String, Object>> getReceiveRoomOutReturnPrintData(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutReturnPrintData(Map<String, Object> params);
	
	public List<Map<String, Object>> getSapPoData(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData30(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData29(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData28(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData27(Map<String, Object> params);

	public List<Map<String, Object>> getWareHouseOutData31(Map<String, Object> params);
	
	public List<Map<String, Object>> getWareHouseOutData33(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData34(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData37(Map<String, Object> params);
	
	public List<Map<String, Object>> getWareHouseOutReturnData(Map<String, Object> params);
	
	public List<Map<String, Object>> getReturnInfoByBarcode(Map<String, Object> params);
	public List<Map<String, Object>> getSapPoInfo(Map<String, Object> params);
	public List<Map<String, Object>> getSapPoBarcodeInfo(Map<String, Object> params);
	
	public String getPlantBucks(String WERKS);
	public String wareHouseOutPickup(Map<String, Object> params);
	public String wareHouseOutPickupCancel(Map<String, Object> params);
	
	public String createWareHouseOutReturn(Map<String, Object> params);
	public String pdaWareHouseOutReturnConfirm(Map<String, Object> params);
	public List<Map<String, Object>> getPlantInfoByWerks(Map<String, Object> params);
	public int getPoHxQty(String PONO);
	public int getMoHeadHxQty(String AUFNR);
	public int getSapMoCount(Map<String, Object> params);
	public String getSapMoStatus(Map<String, Object> params);
	
	public List<Map<String, Object>> getWareHouseOutPickupData(Map<String, Object> params);
	
	public int getWmsReturnItemCountBySapOutNo(String SapOutNo);
}
