package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsVoucherCancelDao {

	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params);
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params);	
	public int updateWmsDocItemQtyCancel(Map<String, Object> params);
	public int updateWhBinUStorageUnit(Map<String, Object> params);
	public List<Map<String, Object>> getReceiptInfoFromReturnItem(Map<String, Object> params);
	public int updateInReceiptQty(Map<String, Object> params);
	public List<Map<String, Object>> queryInRhStock(Map<String, Object> params);
	public int updateInRhStock(Map<String, Object> params);
	public int insertInRhStock(Map<String, Object> params);
	public int updatePoHx(Map<String, Object> params);
	public int updateToHx(Map<String, Object> params);
	public int delSapJobItem(Map<String, Object> params);
	public int delSapJobHead(Map<String, Object> params);
	public int updateCoreStockStockQty(Map<String, Object> params);
	public int updateCoreStockFreeze(Map<String, Object> params);
	public int updateSapMoComponentTLQty(Map<String, Object> params);
	public int updateLabelsStatus(Map<String, Object> params);
	public List<Map<String, Object>> getCancelBussinessType(Map<String, Object> params);
	public int updateInReceiptWhReturnQty(Map<String, Object> params);
	public int updateInBoundCancelQty(Map<String, Object> params);
	
	public int updateCoreStockCancelQty(Map<String, Object> params);
	public int updateInBoundItemRealQty(Map<String, Object> params);
	public int updateInReceiptInfo(Map<String, Object> params);
	public List<Map<String, Object>> queryInbondHeadQcResult(Map<String, Object> params);
	public int updateHXPO(Map<String, Object> params);
	public int updateHXMOITEM(Map<String, Object> params);
	public int updateHXMOCOMPONENT(Map<String, Object> params);
	public void updateWmsDocItemQtyCancel_Batch(List<Map<String, Object>> itemList);
	public void deleteInReceipt_Batch(List<Map<String, Object>> itemList);
	public void deleteLable_Batch(List<Map<String, Object>> itemList);
	public void deleteQCItem_Batch(List<Map<String, Object>> itemList);
	public void deleteQCHead(Map<String, Object> rmap);
	public void deleteQCResult_Batch(List<Map<String, Object>> itemList);
	public void updateDeleteInRhStock_Batch(List<Map<String, Object>> itemList);
	public void updateHXPO_Batch(List<Map<String, Object>> itemList);
	public void updateHXDN_Batch(List<Map<String, Object>> itemList);
	public void updateHXTO_Batch(List<Map<String, Object>> itemList);
	public int updateOrInsertRhStock(List<Map<String, Object>> Listparams);
	
	/**
	 * 冲销、取消更新核销信息
	 * @param itemList
	 */
	public void updateHXPO_Cancel(List<Map<String, Object>> itemList);
	
	public void updateHXTO_Cancel(List<Map<String, Object>> itemList);
	
	public List<Map<String, Object>> getInboundInfoByInboundNo(Map<String, Object> rmap);
	public List<Map<String, Object>> getwmsWhTaskByInboundNo(Map<String, Object> rmap);
	
	public List<Map<String, Object>> getReceiptInfoByreceiptNo(Map<String, Object> map);
	
	public void delStockLabelByList(List<Map<String, Object>> labelList);
	
	public List<Map<String, Object>> getCoreStockByCond(Map<String, Object> map);
	
	public List<Map<String, Object>> getCoreStockInfoByCond(Map<String, Object> map);
	
}
