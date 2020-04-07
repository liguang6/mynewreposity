package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;

import java.util.Map;

/**
 * @ClassName WmsCoreSearchSequenceService
 * @Description  存储类型搜索顺序配置
 * @Author qiu.jiaming1
 * @Date 2019/2/28
 */
public interface WmsCoreSearchSequenceService extends IService<WmsCoreSearchSequenceEntity>{
    // 分页查询
	PageUtils queryPage(Map<String, Object> params);
	
}
