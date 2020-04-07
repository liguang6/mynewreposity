package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.admin.modules.masterdata.dao.StandardWorkgroupDao;
import com.byd.admin.modules.masterdata.service.StandardWorkgroupService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月25日 下午2:24:21 
 * 类说明 
 */
@Service
public class StandardWorkgroupServiceImpl implements StandardWorkgroupService{
	@Autowired
	private StandardWorkgroupDao standardWorkgroupDao;
	@Override
	public PageUtils getStandardWorkgroupListByPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=standardWorkgroupDao.getStandardWorkgroupListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=standardWorkgroupDao.getStandardWorkgroupListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public int insertStandardWorkgroup(Map<String, Object> params) {
		return standardWorkgroupDao.insertStandardWorkgroup(params);
	}
	
	@Override
	public Map<String, Object> selectById(Map<String, Object> params) {
		return standardWorkgroupDao.selectById(params);
	}

	@Override
	public int updateStandardWorkgroup(Map<String, Object> params) {
		
		return standardWorkgroupDao.updateStandardWorkgroup(params);
	}

	@Override
	public int delStandardWorkgroup(Map<String, Object> params) {
		
		return standardWorkgroupDao.delStandardWorkgroup(params);
	}

	@Override
	public List<Map<String, Object>> getStandardWorkgroupList(Map<String, Object> params) {
		
		return standardWorkgroupDao.getStandardWorkgroupList(params);
	}


}
