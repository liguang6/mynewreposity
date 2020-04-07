package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.admin.modules.masterdata.dao.TestRulesDao;
import com.byd.admin.modules.masterdata.service.TestRulesService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
/***
 * 品质抽样规则
 * @author thw
 * @date 2019-09-03 16:12:06
 */
@Service("testRulesService")
public class TestRulesServiceImpl implements TestRulesService{
	@Autowired
	private TestRulesDao testRulesDao;

	/**
	 * 获取品质抽样规则 
	 * @param params WERKS：工厂代码 WORKSHOP：车间代码/名称  ORDER_AREA_CODE：订单区域代码
	 * @return
	 */
	@Override
	public List<Map<String,Object>> getQmsTestRules(Map<String,Object> params){
		return testRulesDao.getQmsTestRules(params);
	}

	@Override
	public PageUtils getQmsTestRulesListByPage(
			Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=testRulesDao.getQmsTestRulesListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		if(params.get("WORKSHOP")!=null ){
			String[] workshoplist=params.get("WORKSHOP").toString().split(",");
				params.put("workshoplist", workshoplist);
			
		}
		
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=testRulesDao.getQmsTestRulesListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public int insertQmsTestRules(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testRulesDao.insertQmsTestRules(params);
	}

	@Override
	public Map<String, Object> selectById(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testRulesDao.selectById(params);
	}

	@Override
	public int updateQmsTestRules(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testRulesDao.updateQmsTestRules(params);
	}

	@Override
	public int delQmsTestRules(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testRulesDao.delQmsTestRules(params);
	}
	
}
