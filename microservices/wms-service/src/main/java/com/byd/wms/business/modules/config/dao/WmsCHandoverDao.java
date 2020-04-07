package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCHandoverEntity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 交接人员配置表
 * 
 * @author cscc tangj
 * @email 
 * @date 2019-04-24 13:57:57
 */
public interface WmsCHandoverDao  extends BaseMapper<WmsCHandoverEntity> {
	
    public List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	public int getListCount(Map<String,Object> params);
	
	List<Map<String, Object>> getCHandoverList(Map<String, Object> map);
}
