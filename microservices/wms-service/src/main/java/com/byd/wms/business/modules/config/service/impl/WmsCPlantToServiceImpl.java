package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCPlantToDao;
import com.byd.wms.business.modules.config.service.WmsCPlantToService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;

@Service
public class WmsCPlantToServiceImpl implements WmsCPlantToService {
	@Autowired
	private WmsCPlantToDao wmsCPlantToDao;
	
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsCPlantToDao.getCPlantToCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsCPlantToDao.getCPlantToList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public void save(Map<String, Object> params) {
		wmsCPlantToDao.saveUpdate(params);		
	}

	@Override
	public void deletes(String ids) {
		wmsCPlantToDao.delete(ids);
	}

}
