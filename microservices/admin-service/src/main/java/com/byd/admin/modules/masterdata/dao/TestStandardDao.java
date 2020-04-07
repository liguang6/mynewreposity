package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

/** 
 * 品质检验标准
 * @author thw: 
 * @version 创建时间：2019年9月7日 上午10:32:01 
 * 类说明 
 */
public interface TestStandardDao{
	
	List<Map<String,Object>> getQmsTestStandard(Map<String,Object> params);
	
	public List<Map<String, Object>> getQmsTestStandardListByPage(Map<String, Object> params);
	public int getQmsTestStandardListCount(Map<String, Object> params);
	public int insertQmsTestStandard(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateQmsTestStandard(Map<String, Object> params);
	public int delQmsTestStandard(Map<String, Object> params);
	
}
