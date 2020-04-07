package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;

public interface WmsSapPlantLgortDao extends BaseMapper<WmsSapPlantLgortEntity>{
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	public List<Map<String,Object>> selectLgortList(Map<String, Object> param);
	
}
