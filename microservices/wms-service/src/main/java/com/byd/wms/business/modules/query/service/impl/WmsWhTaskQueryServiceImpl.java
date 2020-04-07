package com.byd.wms.business.modules.query.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.query.dao.WmsInReceiptQueryDao;
import com.byd.wms.business.modules.query.dao.WmsWhTaskQueryDao;
import com.byd.wms.business.modules.query.service.WmsInReceiptQueryService;
import com.byd.wms.business.modules.query.service.WmsWhTaskQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;


@Service("wmsWhTaskQueryService")
public class WmsWhTaskQueryServiceImpl implements WmsWhTaskQueryService {
	@Autowired
    private WmsWhTaskQueryDao wmsWhTaskQueryDao;

	@Override
	public PageUtils queryTaskPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsWhTaskQueryDao.getTaskCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsWhTaskQueryDao.getTaskList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
}
