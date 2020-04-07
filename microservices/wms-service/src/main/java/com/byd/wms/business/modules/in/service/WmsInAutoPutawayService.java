package com.byd.wms.business.modules.in.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/**
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:34:14 
 */
public interface WmsInAutoPutawayService {

	public Map<String, Object> createPO(Map<String, Object> params);
	
	public String saveStepLog(Map<String, Object> params);
	
	public Map<String, Object> createDN(Map<String, Object> params);
	
	public void updatePutawayStatus(List<Map<String,String>> params);
	
	public Map<String, Object> postDN(Map<String, Object> params);
	
	public PageUtils loglist(Map<String, Object> params);
	
	public List<Map<String,Object>> getPutawayInfo(Map<String, Object> params);
	
	public int updatePutawayLog(List<Map<String,Object>> params);
	
	public void saveSAPMatDoc(String doctype);
	
	public Map<String, Object> postDNAgain(Map<String, Object> params);
	
	/**
	 * 获取凭证已收数量
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getReceivedQty(List<Map<String, Object>> paramList);
}
