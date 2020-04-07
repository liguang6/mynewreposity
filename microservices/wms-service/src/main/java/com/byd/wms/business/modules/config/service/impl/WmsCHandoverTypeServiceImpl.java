package com.byd.wms.business.modules.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCHandoverTypeDao;
import com.byd.wms.business.modules.config.entity.WmsCHandoverTypeEntity;
import com.byd.wms.business.modules.config.service.WmsCHandoverTypeService;

@Service("wmsCHandoverTypeService")
public class WmsCHandoverTypeServiceImpl extends ServiceImpl<WmsCHandoverTypeDao, WmsCHandoverTypeEntity> implements WmsCHandoverTypeService {
	@Autowired
	private WmsCHandoverTypeDao wmsCHandoverTypeDao;
	@Override
    public PageUtils queryPage(Map<String, Object> params) {
        
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsCHandoverTypeDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsCHandoverTypeDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }
	@Override
	public List<Map<String, Object>> getList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmsCHandoverTypeDao.getList(map);
	}
	@Override
	public int merge(List<Map<String, Object>> list) {
		return wmsCHandoverTypeDao.merge(list);
	}
}
