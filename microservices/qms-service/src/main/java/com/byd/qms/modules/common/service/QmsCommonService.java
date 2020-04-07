/**
 * Copyright 2018 cscc
 */
package com.byd.qms.modules.common.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

/**
 * QMS通用Service接口
 * 
 * @author tangj
 * @email 
 * @date 2019年07月24日 15:49:01
 */
public interface QmsCommonService {

	public List<Map<String,Object>> getBusTypeCodeList(String busTypeCode);

	public List<Map<String, Object>> getTestNodes(String testType, String TEST_CLASS);
	
	public List<Map<String,Object>> getOrderNoList(String orderNo);

	public List<Map<String, Object>> getBusList(Map<String, Object> condMap);

	public List<Map<String, Object>> getTestTools(Map<String, Object> condMap);

	public List<Map<String, Object>> getQmsTestRecords(Map<String, Object> condMap);
}
