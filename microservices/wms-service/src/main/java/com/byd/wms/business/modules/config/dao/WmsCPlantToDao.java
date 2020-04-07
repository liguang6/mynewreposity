package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface WmsCPlantToDao {

	int getCPlantToCount(Map<String, Object> params);

	List<Map<String, Object>> getCPlantToList(Map<String, Object> params);

	void saveUpdate(Map<String, Object> params);

	void delete(@Param(value="ids")String ids);
	
	Map<String, Object> getCPlantTo(Map<String, Object> params);

}
