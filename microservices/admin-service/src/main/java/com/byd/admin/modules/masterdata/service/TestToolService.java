package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/**
 * 品质检具
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface TestToolService{
	
	/**
	 * 获取品质检验工具
	 * @param params WERKS：工厂代码  TEST_TOOL_NO： 检具编码  TEST_TOOL_NAME：检具名称
	 * @return
	 */
	List<Map<String,Object>> getQmsTestToolList(Map<String,Object> params);
	
	public PageUtils getQmsTestToolListByPage(Map<String, Object> params);
	public int getQmsTestToolListCount(Map<String, Object> params);
	public int insertQmsTestTool(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateQmsTestTool(Map<String, Object> params);
	public int delQmsTestTool(Map<String, Object> params);
	
}
