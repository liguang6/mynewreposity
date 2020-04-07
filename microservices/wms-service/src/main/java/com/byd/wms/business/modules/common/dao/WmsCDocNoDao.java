package com.byd.wms.business.modules.common.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.common.entity.WmsCDocNo;

public interface WmsCDocNoDao extends BaseMapper<WmsCDocNo>{
	List<Map<String, Object>> getDictByMap(Map<String, Object> params);
	
	long getDocNextNo(WmsCDocNo entity);
	
	void updateDocNoById(Map<String, Object> param);
}
