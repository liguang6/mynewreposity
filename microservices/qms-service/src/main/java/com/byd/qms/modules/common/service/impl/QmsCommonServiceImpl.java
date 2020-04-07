/**
 * Copyright 2018 cscc
 */
package com.byd.qms.modules.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.byd.qms.modules.common.dao.QmsCommonDao;
import com.byd.qms.modules.common.service.QmsCommonService;
import java.util.List;
import java.util.Map;

@Service("commonService")
public class QmsCommonServiceImpl implements QmsCommonService {
	@Autowired
	private QmsCommonDao qmsCommonDao;
	
	@Override
	public List<Map<String, Object>> getBusTypeCodeList(String busTypeCode) {
		return qmsCommonDao.getBusTypeCodeList(busTypeCode);
	}

	@Override
	public List<Map<String, Object>> getTestNodes(String testType,String TEST_CLASS) {
		return qmsCommonDao.getTestNodes(testType,TEST_CLASS);
	}

	@Override
	public List<Map<String, Object>> getOrderNoList(String orderNo) {
		return qmsCommonDao.getOrderNoList(orderNo);
	}

	@Override
	public List<Map<String, Object>> getBusList(Map<String, Object> condMap) {
		return qmsCommonDao.getBusList(condMap);
	}

	@Override
	public List<Map<String, Object>> getTestTools(Map<String, Object> condMap) {
		return qmsCommonDao.getTestTools(condMap);
	}

	@Override
	public List<Map<String, Object>> getQmsTestRecords(Map<String, Object> condMap) {
		return qmsCommonDao.getQmsTestRecords(condMap);
	}


}
