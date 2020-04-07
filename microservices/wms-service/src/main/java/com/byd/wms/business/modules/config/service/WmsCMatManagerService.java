package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCKeyPartsEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerEntity;

import java.util.List;
import java.util.Map;

/**
 * 仓库人料关系配置
 *
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
public interface WmsCMatManagerService extends IService<WmsCMatManagerEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    public List<Map<String,Object>> validateMat(Map<String,Object> param);
	
	public List<Map<String,Object>> validateLifnr(Map<String,Object> param);
	
	public List<String> validateAuthorizeCode(Map<String,Object> param);
	
	public int batchSave(List<WmsCMatManagerEntity> list);
}

