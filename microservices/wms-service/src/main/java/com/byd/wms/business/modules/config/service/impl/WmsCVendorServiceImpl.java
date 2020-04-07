package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatDangerDao;
import com.byd.wms.business.modules.config.dao.WmsCVendorDao;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.service.WmsCVendorService;

@Service
public class WmsCVendorServiceImpl extends ServiceImpl<WmsCVendorDao, WmsCVendor> implements WmsCVendorService{
	@Autowired
	private WmsCVendorDao wmsCVendorDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
		String lifnr = params.get("lifnr") == null?null:String.valueOf(params.get("lifnr"));
		String name1 = params.get("name1") == null?null:String.valueOf(params.get("name1"));
		
		if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
		if(StringUtils.isBlank(lifnr)){
			lifnr = params.get("LIFNR") == null?null:String.valueOf(params.get("LIFNR"));
		}
		if(StringUtils.isBlank(name1)){
			name1 = params.get("NAME1") == null?null:String.valueOf(params.get("NAME1"));
		}
		
		Page<WmsCVendor> page = this.selectPage(new Query<WmsCVendor>(params).getPage(),
				new EntityWrapper<WmsCVendor>().like("werks", werks).like("lifnr", lifnr).like("name1", name1).eq("del_flag", "0")//已经标识为删除的数据不显示
				);
		return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCVendorDao.validate(list);
	}


	

}
