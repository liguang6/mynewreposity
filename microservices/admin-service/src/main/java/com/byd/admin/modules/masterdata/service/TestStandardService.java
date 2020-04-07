package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/**
 * 品质检验标准
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface TestStandardService{
	
	List<Map<String,Object>> getQmsTestStandard(Map<String,Object> params);
	
	public PageUtils getQmsTestStandardListByPage(Map<String, Object> params);
	
	public int insertQmsTestStandard(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateQmsTestStandard(Map<String, Object> params);
	public int delQmsTestStandard(Map<String, Object> params);
	
}
