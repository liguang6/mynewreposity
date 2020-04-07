package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;

import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.wms.business.modules.config.dao.WmsCVendorDao;
import com.byd.wms.business.modules.config.dao.WmsSapVendorDao;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.config.service.WmsCVendorService;
import com.byd.wms.business.modules.config.service.WmsSapVendorService;

@Service
public class WmsSapVendorServiceImpl extends ServiceImpl<WmsSapVendorDao, WmsSapVendor> implements WmsSapVendorService{

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String lifnr = params.get("lifnr") == null?null:String.valueOf(params.get("lifnr"));
        String name1 = params.get("name1") == null?null:String.valueOf(params.get("name1"));

        if(StringUtils.isBlank(lifnr)){
            lifnr = params.get("LIFNR") == null?null:String.valueOf(params.get("LIFNR"));
        }
        if(StringUtils.isBlank(name1)){
            name1 = params.get("NAME1") == null?null:String.valueOf(params.get("NAME1"));
        }

        Page<WmsSapVendor> page = this.selectPage(new Query<WmsSapVendor>(params).getPage(),
                new EntityWrapper<WmsSapVendor>().like("LIFNR", lifnr).like("NAME1", name1)
        );
        return new PageUtils(page);
    }

}
