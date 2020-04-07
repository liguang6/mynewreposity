package com.byd.wms.business.modules.in.service;

import java.util.List;
import java.util.Map;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年9月29日 上午9:31:52 
 * 类说明 
 */
public interface WmsInHandoverService {
	List<Map<String,Object>> getInBoundHead(Map<String,Object> map);
	List<Map<String,Object>> getInBoundItem(Map<String,Object> map) throws Exception;
	
	public Map<String, Object> handover(Map<String, Object> map) ;
	
	public Map<String, Object> updateHeadStatus(Map<String, Object> map) ;
	
	public List<Map<String,Object>> getLabelList(Map<String,Object> map);
	
	List<Map<String,Object>> getWMSDOCMatDoc(Map<String,Object> map);
	
	List<Map<String,Object>> getSAPDOCDocDate(Map<String,Object> map);
	
	public Map<String, Object> validLabelQty(Map<String, Object> map) ;
	List<Map<String, Object>> getLabelInfo(Map<String, Object> params);
	
	List<Map<String,Object>> getInpoCptConsume(List<Map<String, Object>> cptList);
	
	public Map<String, Object> handover_new(Map<String, Object> map) ;
	
	public Map<String, Object> updateHeadStatus_new(Map<String, Object> params);
	
	public Map<String, Object> labelHandoverValid(Map<String, Object> params);
	
	public List<Map<String,Object>> getLabelInfoBy303Z23(Map<String, Object> params);
	
	public String saveinbound303(Map<String, Object> param) ;
}
