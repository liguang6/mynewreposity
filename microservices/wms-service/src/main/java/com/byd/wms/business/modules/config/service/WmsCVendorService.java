package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCVendor;

public interface WmsCVendorService extends IService<WmsCVendor>{
	
	PageUtils queryPage(Map<String,Object> params);
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);

}
