package com.byd.wms.business.modules.out.service;

import java.util.Map;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;

public interface RequirementManageService {

	/**
	 * 查询出库需求头
	 * @param params
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public PageUtils selectRequirementHeadList(Map<String,Object> params,Integer currentPage,Integer pageSize);
	
	/**
	 * 查询出库需求明细
	 * @param params
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public PageUtils selectRequirementItemList(Map<String, Object> params, Integer currentPage,Integer pageSize);


	/**
	 * 关闭/删除出库需求
	 * @param
	 */
	public R closeRrequirement(Map<String, Object> params);
}
