package com.byd.wms.business.modules.out.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;

public interface WmsOutRequirementHeadDao extends BaseMapper<WmsOutRequirementHeadEntity>{

	/**
	 * 获取需求清单（待交接）
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectRequirement(Map<String, Object> params);
	
	/**
	 * 查询出库需求头表
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> selectRequirementHeadList(Page page,Map<String, Object> params);
	
	/**
	 * 查询出库需求行项目
	 * @param params
	 * @return
	 */
	List<Map<String,Object>> selectRequirementItemList(Page page,Map<String, Object> params);
	
	int updateRequirementStatus(List<Map<String,Object>> params);
}
