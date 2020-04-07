package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

/** 
 * 品质检具库
 * @author thw: 
 * @version 创建时间：2019年9月7日 上午10:32:01 
 * 类说明 
 */
public interface TestToolDao{
	
	List<Map<String,Object>> getQmsTestToolList(Map<String,Object> params);
	
	public List<Map<String, Object>> getQmsTestToolListByPage(Map<String, Object> params);
	public int getQmsTestToolListCount(Map<String, Object> params);
	public int insertQmsTestTool(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateQmsTestTool(Map<String, Object> params);
	public int delQmsTestTool(Map<String, Object> params);
	
}
