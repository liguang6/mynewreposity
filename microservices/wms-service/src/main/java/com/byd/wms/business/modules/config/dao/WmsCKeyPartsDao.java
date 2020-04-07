package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCKeyPartsEntity;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 工厂关键零部件配置表
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
public interface WmsCKeyPartsDao extends BaseMapper<WmsCKeyPartsEntity> {
	
	public int merge(List<WmsCKeyPartsEntity> list);
}
