package com.byd.wms.business.modules.config.dao;

import com.byd.wms.business.modules.config.entity.WmsCMatManagerEntity;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 仓库人料关系配置
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
public interface WmsCMatManagerDao extends BaseMapper<WmsCMatManagerEntity> {
	
    public List<Map<String,Object>> getListByPage(Map<String,Object> param);
	
	public int getListCount(Map<String,Object> param);
	
	public int merge(List<WmsCMatManagerEntity> list);
	
	public List<Map<String,Object>> validateMat(Map<String,Object> param);
	
	public List<Map<String,Object>> validateLifnr(Map<String,Object> param);
	
	public List<Map<String,Object>> validateAuthorizeCode(Map<String,Object> param);
}
