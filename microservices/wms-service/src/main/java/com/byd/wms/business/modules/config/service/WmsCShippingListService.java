package com.byd.wms.business.modules.config.service;


import com.byd.utils.PageUtils;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * 物料物流参数配置表 自制产品入库参数
 *
 * @author cscc
 * @email 
 * @date 2018-09-28 10:30:07
 */
public interface WmsCShippingListService {

	PageUtils queryPage(Map<String, Object> params);
	
	void saveNoticeMail(Map<String, Object> params);

	Map<String, Object> selectById(String id,String itemNo);

	void updateById(Map<String, Object> params);
}

