package com.byd.wms.business.modules.account.dao;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsOutReversalDao {
	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params);
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params);
	public int updateWmsDocItemQtyCancel2(Map<String, Object> params);
	public int delSapJobItem(Map<String, Object> params);
	public int delSapJobHead(Map<String, Object> params);


    void updateRequirementItemCancelQty(List<Map<String, Object>> lmap);
}
