package com.byd.wms.business.modules.out.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * WMS出库需求拣配
 * @author ren.wei3
 * @date 2019-04-11
 */
public interface WmsOutPickingDao{
	
	/**
	 * 获取需求清单（待拣配）
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectRequirement(Map<String, Object> params);
	
	int selectRequirementCount(Map<String,Object> params);
	
	int updateCallMaterial(Map<String, Object> params);
	
	List<Map<String, Object>> selectOutPicking(Map<String, Object> params);
	
	void deleteOutPickingById(@Param("ID")Long id);	
	
	void updateCoreStockQty(Integer qty,String werks,String whNumber,String lgort,String matnr,String lifnr);
	
	/**
	 * 拣配清单
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectPickList(Map<String, Object> params);
}
