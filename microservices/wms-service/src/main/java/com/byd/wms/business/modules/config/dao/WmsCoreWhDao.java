package com.byd.wms.business.modules.config.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
/**
 * 仓库信息维护
 * 
 * @author cscc
 * @email 
 * @date 2018年7月31日 上午9:33:01
 */
public interface WmsCoreWhDao extends BaseMapper<WmsCoreWhEntity> {
    
	public int delById(Long id);
	
	public int deleteBatch(List list);
	
	List<Map<String,Object>> getWmsCoreWhAddressList(Map<String,Object> param);
	
	int getWmsCoreWhAddressCount(Map<String,Object> param);
	
	List<Map<String,Object>> getWmsCoreWhList(Map<String,Object> param);
}
