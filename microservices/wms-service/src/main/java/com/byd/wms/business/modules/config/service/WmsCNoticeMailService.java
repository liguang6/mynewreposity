package com.byd.wms.business.modules.config.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
public interface WmsCNoticeMailService {
	// 分页查询
		PageUtils queryPage(Map<String, Object> params);

    void saveNoticeMail(Map<String, Object> params);

	Map<String, Object> selectById(Long id);

	void updateById(Map<String, Object> params);

    void delById(Long id);
}
