package com.byd.wms.business.modules.kn.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.kn.entity.WmsKnStorageMoveEntity;
/**
 * 移储
 *
 * @author cscc tangj
 * @email 
 * @date 2018-11-07 10:12:08
 */
public interface WmsKnStorageMoveService  extends IService<WmsKnStorageMoveEntity> {
	// 查询移储记录
    PageUtils queryPage(Map<String, Object> params);
	// 查询移除库存
	public List<Map<String,Object>> queryStock(Map<String, Object> params);
    // 移储保存
    public void save(List<Map<String, Object>> list);
    
}

