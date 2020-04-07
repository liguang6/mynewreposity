package com.byd.wms.business.modules.out.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;

public interface WmsOutRequirementItemDao extends BaseMapper<WmsOutRequirementItemEntity>{

	   
	/**
	 * 更新需求行数量和状态
	 * @param params
	 * @return
	 */
	int updateOutboundItemQtyANDStatus(List<Map<String,Object>> params);
	
	/**
	 * 获取可打配送标签数据
	 * @param params
	 */
	List<Map<String, Object>> queryShippingLabel(Map<String, Object> params);

	Map<String, Object> queryStatusByWhTask(Map map);

	int updateGbStatusByRequirementHead(List<Map> list);

	int updateGbStatusByRequirementItem(List<Map> list);

	void updateQxStatusByRequirementHead(List<Map> list);

	void updateQxStatusByRequirementItem(List<Map> list);

	List<Map> queryStatusByRequirementItem(Map map);
	
	/**
	 * 获取可打配送标签数据CS
	 * @param params
	 */
	List<Map<String, Object>> queryShippingLabelcs(Map<String, Object> params);
	
	/**
	 * 获取需求已下架数据，打关键零部件标签用
	 * @param params
	 */
	List<Map<String, Object>> queryKeyPartsLabel(Map<String, Object> params);

	/**
	 * 状态未关闭且未删除，实发数量为0的行项目需求数量
	 */
	Integer queryRequirementByRealQty(Map map);
	
	/**
	 * 查询需求明细
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryRequirementItem(Map<String, Object> params);
}
