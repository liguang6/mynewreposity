package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCMatFixedStorageEntity;

/**
 * @author ren.wei3
 *
 */
public interface WmsCMatFixedStorageService extends IService<WmsCMatFixedStorageEntity> {

	PageUtils queryPage(Map<String, Object> params);
	
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	public List<WmsCMatFixedStorageEntity> findEmptyBin(Map<String, Object> params);
	
	public List<Map<String, Object>> findAlreadyBin(Map<String, Object> params);
}
