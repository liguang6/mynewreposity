package com.byd.wms.business.modules.config.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年3月4日 下午2:22:57 
 * 类说明 
 */
public interface WmsCWhService extends IService<WmsCWhEntity> {
	PageUtils queryPage(Map<String,Object> params);
	
	Map<String,Object> getWmsCWh(Map<String,Object> param);
}
