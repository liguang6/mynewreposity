package com.byd.admin.modules.sys.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.admin.modules.sys.dao.SysNewsDao;
import com.byd.admin.modules.sys.service.SysNewsService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;



/** 
 * 类说明 
 */
@Service("wmsCKanbanInfoService")
public class SysNewsServiceImpl implements SysNewsService {
	@Autowired
	SysNewsDao sysNewsDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count = sysNewsDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String, Object>> list = sysNewsDao.queryNoticeMailList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}
	@Override
	public void saveNoticeMail(Map<String, Object> params) {
		sysNewsDao.insertNoticeMail(params)
		;
	}
	@Override
	public Map<String, Object> selectById(Long id) {
		return sysNewsDao.selectById(id);
	}

	@Override
	public void updateById(Map<String, Object> params) {
		sysNewsDao.updateById(params);
	}

	@Override
	public void delById(Long id) {
		sysNewsDao.delById(id);
	}
	@Override
	public PageUtils query(Map<String, Object> params) {
		List<Map<String, Object>> list=sysNewsDao.query(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(6);
		page.setSize(Integer.valueOf(6));
		page.setCurrent(Integer.valueOf(1));
		return new PageUtils(page);
	}
	
	@Override
	public List<Map<String,Object>> getUserNews(Map<String, Object> params){
		return sysNewsDao.getUserNews(params);
	}
}
