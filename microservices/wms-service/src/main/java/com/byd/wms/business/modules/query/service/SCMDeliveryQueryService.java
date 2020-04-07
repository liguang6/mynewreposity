package com.byd.wms.business.modules.query.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年11月20日 下午4:13:59 
 * 类说明 
 */
public interface SCMDeliveryQueryService {
	public List<Map<String, Object>> queryHeadBySCM(Map<String, Object> params);
	public List<Map<String, Object>> queryItemBySCM(Map<String, Object> params);
	public PageUtils queryPage(Map<String, Object> params);
	public PageUtils queryPageDetail(Map<String, Object> params);
	List<Map<String, Object>> getHasReceiveQty(Map<String,Object> param);
	void updateSCMState(List<Map<String, Object>> params);
	int updateHEAD(List<Map<String, Object>> params);
	int updateITEM(List<Map<String, Object>> params);
	int updateDETAIL(List<Map<String, Object>> params);
	
	int updateTPO_onWayAmount(List<Map<String, Object>> params);
	public List<Map<String, Object>> queryAllItemBySCM(List<Map<String, Object>> params);
}
