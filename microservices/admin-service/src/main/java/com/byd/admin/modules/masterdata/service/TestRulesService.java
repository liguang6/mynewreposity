package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/**
 * 品质抽检规则
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface TestRulesService{
	
	/**
	 * 获取品质抽样规则 
	 * @param params WERKS：工厂代码 WORKSHOP：车间代码/名称  ORDER_AREA_CODE：订单区域代码
	 * @return
	 */
	List<Map<String,Object>> getQmsTestRules(Map<String,Object> params);
	
	public PageUtils getQmsTestRulesListByPage(Map<String, Object> params);
	
	public int insertQmsTestRules(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateQmsTestRules(Map<String, Object> params);
	public int delQmsTestRules(Map<String, Object> params);
	
}
