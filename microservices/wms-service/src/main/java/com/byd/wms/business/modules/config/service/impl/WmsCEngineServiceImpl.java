package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.dao.WmsCControlFlagDao;
import com.byd.wms.business.modules.config.dao.WmsCEngineDao;
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.service.WmsCControlFlagService;
import com.byd.wms.business.modules.config.service.WmsCEngineService;

@Service("wmsCEngineService")
public class WmsCEngineServiceImpl implements WmsCEngineService {
	
	@Autowired
	private WmsCEngineDao wmsCEngineDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		List<Map<String, Object>> list = wmsCEngineDao.queryAll(params);
		Page page = new Query<Map<String, Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(list.size());
		page.setSize(list.size());
		return new PageUtils(page);
	}

	@Override
	public Map<String, Object> selectById(Map<String, Object> params) {

		return wmsCEngineDao.selectById(params);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateById(Map<String, Object> params) {
		
		wmsCEngineDao.updateItemById(params);
		wmsCEngineDao.updateById(params);
		wmsCEngineDao.updateProject(params);
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteById(Map<String, Object> params) {
		
		wmsCEngineDao.updateItemById(params);
		List<Map<String, Object>> list = wmsCEngineDao.selectByProject(params);
		if(list.size()==0)
			wmsCEngineDao.updateById(params);
	}

	@Transactional(rollbackFor=Exception.class)
	@Override
	public void insert(Map<String, Object> params) {

		wmsCEngineDao.insert(params);
		wmsCEngineDao.insertItem(params);
		
	}

}
