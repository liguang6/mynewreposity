package com.byd.wms.business.modules.account.service;

import java.util.List;
import java.util.Map;

public interface WmsReturnGoodsVoucherCancelService {
	
	public List<Map<String, Object>> getWmsDocHeadInfo(Map<String, Object> params); 
	public List<Map<String, Object>> getSapDocHeadInfo(Map<String, Object> params);
	/**
	 * 查询凭证信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getVoucherCancelData(Map<String, Object> params); 
	
	/**
	 * 确认取消 【退货】
	 * @param params
	 * @return
	 */
	public String confirmVoucherCancel(Map<String, Object> params); 
	/**
	 * 进仓取消 
	 * @param params
	 * @return
	 */
	public String confirmVoucherCancelInHandOver(Map<String, Object> params);
	/**
	 * 收货凭证 取消
	 * @param params
	 * @return
	 */
	public String confirmVoucherCancelInReceipt(Map<String, Object> params); 
	
	/**
	 * 收货(V)凭证 取消
	 * @param params
	 * @return
	 */
	public String confirmVoucherCancelInReceiptV(Map<String, Object> params); 
	
	public List<Map<String, Object>> getCancelBussinessType(Map<String, Object> params);
}
