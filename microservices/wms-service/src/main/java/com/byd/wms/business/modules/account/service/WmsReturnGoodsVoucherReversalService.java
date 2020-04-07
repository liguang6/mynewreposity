package com.byd.wms.business.modules.account.service;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsVoucherReversalService {
	
	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params); 
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params);
	/**
	 * 查询凭证信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getVoucherReversalData(Map<String, Object> params); 
	
	/**
	 * 确认冲销 【退货】
	 * @param params
	 * @return
	 */
	public String confirmVoucherReversal(Map<String, Object> params); 
	/**
	 * 进仓冲销 
	 * @param params
	 * @return
	 */
	public String confirmVoucherReversalInHandover(Map<String, Object> params);
	/**
	 * 收货冲销
	 * @param params
	 * @return
	 */
	public String confirmVoucherReversalInReceipt(Map<String, Object> params);
	
	/**
	 * 收货V冲销
	 * @param params
	 * @return
	 */
	public String confirmVoucherReversalInReceiptV(Map<String, Object> params);
	
	/**
	 * 根据标签号查询凭证信息和条码信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getVoucherReversalDataByLable(Map<String, Object> params);
}
