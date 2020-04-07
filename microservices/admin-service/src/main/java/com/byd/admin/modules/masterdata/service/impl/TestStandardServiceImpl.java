package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.admin.modules.masterdata.dao.TestStandardDao;
import com.byd.admin.modules.masterdata.service.TestStandardService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
/***
 * 品质检验标准
 * @author thw
 * @date 2019-09-03 16:12:06
 */
@Service("testStandardService")
public class TestStandardServiceImpl implements TestStandardService{
	@Autowired
	private TestStandardDao testStandardDao;

	@Override
	public List<Map<String,Object>> getQmsTestStandard(Map<String,Object> params){
		return testStandardDao.getQmsTestStandard(params);
	}

	@Override
	public PageUtils getQmsTestStandardListByPage(
			Map<String, Object> params) {String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
			String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
			int start = 0;int end = 0;
			int count=testStandardDao.getQmsTestStandardListCount(params);
			if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
				start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
				end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
			}else {
				end=count;
			}
			params.put("START", start);params.put("END", end);
			List<Map<String,Object>> list=testStandardDao.getQmsTestStandardListByPage(params);
			Page page=new Query<Map<String,Object>>(params).getPage();
			page.setRecords(list);
			page.setTotal(count);
			page.setSize(Integer.valueOf(pageSize));
			page.setCurrent(Integer.valueOf(pageNo));
	        return new PageUtils(page);}

	@Override
	public int insertQmsTestStandard(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testStandardDao.insertQmsTestStandard(params);
	}

	@Override
	public Map<String, Object> selectById(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testStandardDao.selectById(params);
	}

	@Override
	public int updateQmsTestStandard(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testStandardDao.updateQmsTestStandard(params);
	}

	@Override
	public int delQmsTestStandard(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return testStandardDao.delQmsTestStandard(params);
	}
	
}
