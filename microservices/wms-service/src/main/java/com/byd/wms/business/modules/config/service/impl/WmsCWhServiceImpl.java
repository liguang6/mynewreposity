package com.byd.wms.business.modules.config.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCWhDao;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.service.WmsCWhService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年3月4日 下午2:24:25 
 * 类说明 
 */

@Service("wmsCWhService")
public class WmsCWhServiceImpl extends ServiceImpl<WmsCWhDao, WmsCWhEntity> implements WmsCWhService {
	@Autowired
	private WmsCWhDao wmsCWhdao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> list=wmsCWhdao.getWmsCWhList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsCWhdao.getWmsCWhCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
	
	@Override
	public Map<String,Object> getWmsCWh(Map<String,Object> param) {
		return wmsCWhdao.getWmsCWh(param);
	}
}
