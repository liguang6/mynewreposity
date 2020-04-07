package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsVoucherReversalDao {
	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params);
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params);	
	public int updateWmsDocItemQtyCancel(Map<String, Object> params);
	public List<Map<String, Object>> getReceiptInfoFromReturnItem(Map<String, Object> params);
	public int updateInReceiptQty(Map<String, Object> params);
	public List<Map<String, Object>> queryInRhStock(Map<String, Object> params);
	public int updateInRhStock(Map<String, Object> params);
	public int insertInRhStock(Map<String, Object> params);
	public int updatePoHx(Map<String, Object> params);
	public int updateToHx(Map<String, Object> params);
	public int delSapJobItem(Map<String, Object> params);
	public int delSapJobHead(Map<String, Object> params);
	public int updateJobItem(Map<String, Object> params);
	public int updateCoreStockFreeze(Map<String, Object> params);
	public List<Map<String, Object>> getCancelBussinessType(Map<String, Object> params);
	public int updateInReceiptWhReturnQty(Map<String, Object> params);
	public int updateInBoundCancelQty(Map<String, Object> params);
	public void updateInReceipt_Batch(List<Map<String, Object>> itemList);
	public void updateQCItem_Batch(List<Map<String, Object>> itemList);
	public void updateQCResult_Batch(List<Map<String, Object>> itemList);
	public List<Map<String, Object>> getVoucherReversalDataByLable(Map<String, Object> params);
	public List<Map<String, Object>> getReversalJobInfo(Map<String, Object> params);
}
