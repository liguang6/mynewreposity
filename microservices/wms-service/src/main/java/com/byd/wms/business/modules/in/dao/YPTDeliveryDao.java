package com.byd.wms.business.modules.in.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.in.entity.YPTDeliveryEntity;

import java.util.List;
import java.util.Map;

public interface YPTDeliveryDao extends BaseMapper<YPTDeliveryEntity>{


	int queryHeadByYPTCount(Map<String,Object> param);
	public List<Map<String, Object>> queryHeadByYPT(Map<String, Object> params);

	int queryItemByYPTCount(Map<String,Object> param);
	public List<Map<String, Object>> queryItemByYPT(Map<String, Object> params);

	int getLabelCount(Map<String,Object> param);
	List<Map<String,Object>> getLabelList(Map<String,Object> param);



}
