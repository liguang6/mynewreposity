package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCMatLtSampleEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 物料物流参数配置表 自制产品入库参数
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-09-28 10:30:07
 */
public interface WmsCMatLtSampleDao extends BaseMapper<WmsCMatLtSampleEntity> {
	/**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	int merge(List<Map<String,Object>> list);
}
