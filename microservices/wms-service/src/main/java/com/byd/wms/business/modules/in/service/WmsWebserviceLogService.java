package com.byd.wms.business.modules.in.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.in.entity.WmsWebServiceLogEntity;

import java.util.Map;


/**
 * @author rain
 * @date 2019年11月27日14:12:20
 * @description webservicelog
 */
public interface WmsWebserviceLogService extends IService<WmsWebServiceLogEntity> {

	/**
	 * 列表页面查询
	 * @param params
	 * @return
	 */
	public PageUtils queryPage(Map<String, Object> params);


	/**
	 * 通过pk查询日志信息
	 * @param pkLog
	 * @return
	 */
	WmsWebServiceLogEntity queryByPkLog(Long pkLog);


}
