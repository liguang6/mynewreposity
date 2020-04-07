package com.byd.wms.business.modules.config.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCNoticeMailDao;
import com.byd.wms.business.modules.config.service.WmsCNoticeMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("WmsCNoticeMailService")
public class WmsCNoticeMailServiceImpl  implements WmsCNoticeMailService {
	@Autowired
	private WmsCNoticeMailDao WmsCNoticeMailDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=WmsCNoticeMailDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String, Object>> list=WmsCNoticeMailDao.queryNoticeMailList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

	@Override
	public void saveNoticeMail(Map<String, Object> params) {
		WmsCNoticeMailDao.insertNoticeMail(params)
		;
	}

	@Override
	public Map<String, Object> selectById(Long id) {
		return WmsCNoticeMailDao.selectById(id);
	}

	@Override
	public void updateById(Map<String, Object> params) {
		WmsCNoticeMailDao.updateById(params);
	}

	@Override
	public void delById(Long id) {
		WmsCNoticeMailDao.delById(id);
	}

}
