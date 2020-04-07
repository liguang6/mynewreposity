package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCMatManagerTypeEntity;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 仓库人料关系模式配置
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
public interface WmsCMatManagerTypeDao extends BaseMapper<WmsCMatManagerTypeEntity> {
	
	public List<Map<String,Object>> getListByPage(Map<String,Object> param);
	
	public int getListCount(Map<String,Object> param);
	
	public int merge(List<WmsCMatManagerTypeEntity> list);
	
	public List<Map<String,Object>>getLgortSelect(Map<String,Object> param);
}
