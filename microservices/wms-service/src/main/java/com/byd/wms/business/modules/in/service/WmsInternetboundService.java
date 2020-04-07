package com.byd.wms.business.modules.in.service;

import java.util.List;
import java.util.Map;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年9月21日 上午10:39:40 
 * 类说明 
 */
public interface WmsInternetboundService {
	List<Map<String,Object>> trunedResult303(Map<String,Object> result);
	String saveInInternetbound(Map<String, Object> params);
	List<Map<String,Object>> queryList101_531(Map<String,Object> cond);
	Map<String,Object> queryListCO(Map<String,Object> cond);
	Map<String,Object> queryListCostCenter(Map<String,Object> cond);
	Map<String,Object> queryListWBS(Map<String,Object> cond);
	Map<String,Object> queryListYf(Map<String,Object> cond);
	List<Map<String,Object>> queryListA101(Map<String,Object> param);
	List<Map<String,Object>> queryListA531(Map<String,Object> param);
	List<Map<String,Object>> queryListPO101(Map<String,Object> param);
	
	void validMo262(Map<String, Object> param);
	
	Map<String, Object> getwhManager_n(Map<String, Object> param);
	
	
}
