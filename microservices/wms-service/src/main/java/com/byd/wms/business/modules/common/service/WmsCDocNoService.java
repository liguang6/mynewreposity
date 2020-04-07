package com.byd.wms.business.modules.common.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.common.entity.WmsCDocNo;

public interface WmsCDocNoService extends IService<WmsCDocNo>{
	PageUtils queryPage(Map<String,Object> params);
	/**
	 * params 传入参数 WERKS 工厂代码   WMS_DOC_TYPE WMS单据类型
	 */
	Map<String, Object> getDocNo(Map<String,Object> params);
	
	String getDocNo(String werks,String docType);
	
	List<Map<String, Object>> getDictByMap(Map<String, Object> params);
	/**
	 * 批量获取
	 * @param params
	 * @return
	 */
	Map<String, Object> getDocNoBatch(Map<String,Object> params);
	
}
