package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.formbean.WmsCMatStorageFormbean;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 物料存储配置表 仓库系统上线前配置
 * 
 * @author tangj
 * @email 
 * @date 2018-08-10 16:09:55
 */
public interface WmsCMatStorageDao extends BaseMapper<WmsCMatStorageEntity> {
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	List<WmsCMatStorageFormbean> getCMatStorageList(Map<String,Object> param);
	
	int getCMatStorageCount(Map<String,Object> param);
}
