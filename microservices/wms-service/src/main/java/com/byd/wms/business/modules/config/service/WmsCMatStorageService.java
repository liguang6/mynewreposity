package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;

import java.util.List;
import java.util.Map;

/**
 * 物料存储配置表 仓库系统上线前配置
 *
 * @author tangj
 * @email 
 * @date 2018-08-10 16:09:55
 */
public interface WmsCMatStorageService extends IService<WmsCMatStorageEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    /**
	 * 导入区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
	PageUtils queryPagenew(Map<String, Object> params);
}

