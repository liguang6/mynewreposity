package com.byd.wms.business.modules.config.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsCAssemblyLogisticsEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年7月26日 下午2:42:05 
 * 类说明 
 */
public interface WmsCAssemblyLogisticsDao extends BaseMapper<WmsCAssemblyLogisticsEntity>{
	public int merge(List<WmsCAssemblyLogisticsEntity> list);
}
