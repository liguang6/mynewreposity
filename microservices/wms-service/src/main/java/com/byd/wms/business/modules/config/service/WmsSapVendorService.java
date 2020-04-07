package com.byd.wms.business.modules.config.service;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;

import java.util.Map;

public interface WmsSapVendorService extends IService<WmsSapVendor>{

    PageUtils queryPage(Map<String,Object> params);

}
