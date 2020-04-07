package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsCMatDangerEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
/**
 * 仓库存储类型配置 各工厂仓库存储类型设置
 * 
 * @author tangj
 * @email 
 * @date 2018年08月06日 
 */
public interface WmsCoreWhAreaDao extends BaseMapper<WmsCoreWhAreaEntity> {
	/**
	 * 导入校验，区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
}
