package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsCMatDangerEntity;
/**
 * 危化品物料配置表
 * 
 * @author tangj
 * @email 
 * @date 2018年08月01日 
 */
public interface WmsCMatDangerDao extends BaseMapper<WmsCMatDangerEntity> {
	/**
	 * 导入危化品物料验证，区分insert与update数据
	 **/
	public List<Map<String,Object>> validateMatDager(List<String> list);
}
