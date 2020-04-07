package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.masterdata.entity.BusTypeEntity;

/**
 * 车型表-基础数据
 * 
 * @author cscc
 * @email 
 * @date 2018-06-05 15:56:12
 */
public interface BusTypeDao extends BaseMapper<BusTypeEntity> {
	List<Map<String,Object>> getBusTypeList(Map<String, Object> params);
}
