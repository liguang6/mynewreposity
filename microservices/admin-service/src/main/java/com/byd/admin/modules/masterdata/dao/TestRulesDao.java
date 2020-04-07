package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

/** 
 * 品质抽检规则
 * @author thw: 
 * @version 创建时间：2019年9月7日 上午10:32:01 
 * 类说明 
 */
public interface TestRulesDao{
	
	List<Map<String,Object>> getQmsTestRules(Map<String,Object> params);
	
	public List<Map<String, Object>> getQmsTestRulesListByPage(Map<String, Object> params);
	public int getQmsTestRulesListCount(Map<String, Object> params);
	public int insertQmsTestRules(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateQmsTestRules(Map<String, Object> params);
	public int delQmsTestRules(Map<String, Object> params);
	
}
