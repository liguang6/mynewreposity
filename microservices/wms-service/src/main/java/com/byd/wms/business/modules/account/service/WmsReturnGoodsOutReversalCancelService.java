package com.byd.wms.business.modules.account.service;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsOutReversalCancelService {
	
	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params); 
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params);
	/**
	 * 查询凭证信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getVoucherReversalData(Map<String, Object> params); 


	/**
	 * 出库冲销凭证
	 */
	public String confirmOutReversal(Map<String, Object> params);

	/**
	 * 出库取消凭证
	 */
	public String confirmOutCancel(Map<String, Object> params);

}
