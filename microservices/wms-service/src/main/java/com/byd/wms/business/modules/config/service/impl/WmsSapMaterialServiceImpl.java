package com.byd.wms.business.modules.config.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsSapMaterialDao;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;

@Service("wmsSapMaterialService")
public class WmsSapMaterialServiceImpl extends ServiceImpl<WmsSapMaterialDao, WmsSapMaterialEntity> implements WmsSapMaterialService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<WmsSapMaterialEntity> page = this.selectPage(
                new Query<WmsSapMaterialEntity>(params).getPage(),
                new EntityWrapper<WmsSapMaterialEntity>()
        );

        return new PageUtils(page);
    }

}
