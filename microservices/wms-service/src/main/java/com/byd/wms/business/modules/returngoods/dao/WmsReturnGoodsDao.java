package com.byd.wms.business.modules.returngoods.dao;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsDao {
	public List<String> getBusinessNameListByWerks(Map<String, Object> params);
	
	public List<Map<String, Object>> getReceiveRoomOutData(Map<String, Object> params);
	public List<Map<String, Object>> getReceiveRoomOutReturnPrintData(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutReturnPrintData(Map<String, Object> params);
	
	public String getTestFlagByWerksBusinessCode(Map<String, Object> params);
	public int insertWmsOutReturnHead(Map<String, Object> params);
	public int insertWmsOutReturnItem(Map<String, Object> params);
	public int insertWmsOutReturnItemDetail(Map<String, Object> params);
	public int getWmsOutReturnSapOutCount(String SAP_OUT_NO);
	
	public List<Map<String, Object>> getReceiveRoomOutReturnData(Map<String, Object> params);
	public int updateOutReturnHeadStatus(Map<String, Object> params);
	public int updateReceiveRoomOutReturnHeadStatus(Map<String, Object> params);
	public int updateOutReturnItemStatus(Map<String, Object> params);
	public int updateOutReturnItemDXJQty(Map<String, Object> params);
	public int updateOutReturnItemDRealQty(Map<String, Object> params);
	public int updateReceipt(Map<String, Object> params);
	public int updateRHStock(Map<String, Object> params);
	public int deleteRHZeroStock(Map<String, Object> params);
	public int getRHStockRhQty(Map<String, Object> params);
	public int updateWhBinStorageUnit(Map<String, Object> params);
	
	public int updateLabelsStatus(Map<String, Object> params);
	
	public List<Map<String, Object>> getSapPoData(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData29Group(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData29(Map<String, Object> params);
	
	public List<Map<String, Object>> getWareHouseOutData28Group(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData28(Map<String, Object> params);
	
	public List<Map<String, Object>> getWareHouseOutData27(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData31(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData33(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData34(Map<String, Object> params);
	public List<Map<String, Object>> getWareHouseOutData37(Map<String, Object> params);
	
	public List<Map<String, Object>> getReturnInfoByBarcode(Map<String, Object> params);
	
	public List<Map<String, Object>> getSapPoInfo(Map<String, Object> params);
	public List<Map<String, Object>> getSapPoBarcodeInfo(Map<String, Object> params);
	
	public String getPlantBucks(String WERKS);
	
	public List<Map<String, Object>> getWareHouseOutPickupData(Map<String, Object> params);
	public int insertOutPickupData(Map<String, Object> params);
	public int updateStockQty(Map<String, Object> params);
	public int getStockFreezeQtyByID(Map<String, Object> params);
	public int delOutPickingData(Map<String, Object> params);
	
	public int updateOutPickingHandoverQty(Map<String, Object> params);
	public int updateOutReturnItemRealReturnQty(Map<String, Object> params);
	public int updateStockXJFreezeQty(Map<String, Object> params);
	public int updateWareHouseReturnHeadStatus(Map<String, Object> params);
	public int updateWareHouseReturnItemStatus(Map<String, Object> params);
	public int updateWareHouseReturnPickingStatus(Map<String, Object> params);
	
	public int getMoHeadHxQty(String AUFNR);
	public int getSapMoCount(Map<String, Object> params);
	public String getSapMoStatus(Map<String, Object> params);
	
	public List<Map<String, Object>> getSapMoGroupForWareHouseOutData30(Map<String, Object> params);
	public List<Map<String, Object>> getInboundDataForWareHouseOutData30(Map<String, Object> params);
	
	public List<Map<String, Object>> getWareHouseOutReturnData(Map<String, Object> params);
	
	public int getWmsReturnItemCountBySapOutNo(String SapOutNo);
	public List<Map<String, Object>> getWmsKetuiQty27(Map<String, Object> params);
	
	public List<Map<String, Object>> getPlantInfoByWerks(Map<String, Object> params);
	public int getPoHxQty(String PONO);
}
