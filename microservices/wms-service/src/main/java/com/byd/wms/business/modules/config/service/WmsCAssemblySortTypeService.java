package com.byd.wms.business.modules.config.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCAssemblySortTypeEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年8月2日 下午3:38:07 
 * 类说明 
 */
public interface WmsCAssemblySortTypeService extends IService<WmsCAssemblySortTypeEntity>{
	PageUtils queryPage(Map<String, Object> params);
}
