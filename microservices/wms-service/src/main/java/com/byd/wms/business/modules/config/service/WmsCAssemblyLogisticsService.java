package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCAssemblyLogisticsEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年7月26日 下午2:46:19 
 * 类说明 
 */
public interface WmsCAssemblyLogisticsService extends IService<WmsCAssemblyLogisticsEntity>{
	PageUtils queryPage(Map<String, Object> params);
	public int batchSave(List<WmsCAssemblyLogisticsEntity> list);
}
