package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsCMatFixedStorageEntity;
import com.byd.wms.business.modules.config.formbean.WmsCMatFixedStorageFormbean;

/**
 * @author ren.wei3
 *
 */
public interface WmsCMatFixedStorageDao extends BaseMapper<WmsCMatFixedStorageEntity>{

	public List<Map<String, Object>> queryFixedStorage(Map<String,Object> param);
	
	public int getListCount(Map<String,Object> param);
	
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	public List<WmsCMatFixedStorageEntity> findEmptyBin(Map<String,Object> param);
	
	public List<Map<String, Object>> findAlreadyBin(Map<String,Object> param);
}
