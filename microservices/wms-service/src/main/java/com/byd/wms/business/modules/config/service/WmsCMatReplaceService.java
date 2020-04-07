package com.byd.wms.business.modules.config.service;

import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCMatReplaceEntity;

public interface WmsCMatReplaceService extends IService<WmsCMatReplaceEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    
}