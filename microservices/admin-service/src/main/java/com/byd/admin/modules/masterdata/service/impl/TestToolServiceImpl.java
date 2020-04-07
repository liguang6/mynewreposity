package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.admin.modules.masterdata.dao.TestToolDao;
import com.byd.admin.modules.masterdata.service.TestToolService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
/***
 * 品质检具
 * @author thw
 * @date 2019-09-03 16:12:06
 */
@Service("testToolService")
public class TestToolServiceImpl implements TestToolService{
	@Autowired
	private TestToolDao testToolDao;

	/**
	 * 获取品质检验工具
	 * @param params WERKS：工厂代码  TEST_TOOL_NO： 检具编码  TEST_TOOL_NAME：检具名称
	 * @return
	 */
	@Override
	public List<Map<String,Object>> getQmsTestToolList(Map<String,Object> params){
		return testToolDao.getQmsTestToolList(params);
	}

	@Override
	public PageUtils getQmsTestToolListByPage(Map<String, Object> params) {
		
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=testToolDao.getQmsTestToolListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=testToolDao.getQmsTestToolListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public int getQmsTestToolListCount(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testToolDao.getQmsTestToolListCount(params);
	}

	@Override
	public int insertQmsTestTool(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testToolDao.insertQmsTestTool(params);
	}

	@Override
	public Map<String, Object> selectById(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testToolDao.selectById(params);
	}

	@Override
	public int updateQmsTestTool(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testToolDao.updateQmsTestTool(params);
	}

	@Override
	public int delQmsTestTool(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testToolDao.delQmsTestTool(params);
	}
	
}
