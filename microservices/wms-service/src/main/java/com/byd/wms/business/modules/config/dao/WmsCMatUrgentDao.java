package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCMatUrgentEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 紧急物料配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-15 13:57:57
 */
public interface WmsCMatUrgentDao extends BaseMapper<WmsCMatUrgentEntity> {
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
}
