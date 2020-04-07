package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.masterdata.entity.BusTypeEntity;
import com.byd.utils.PageUtils;

/**
 * 车型表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-05 15:56:12
 */
public interface BusTypeService extends IService<BusTypeEntity> {

    PageUtils queryPage(Map<String, Object> params);
    List<BusTypeEntity> queryAll(Map<String, Object> params);
    
    List<Map<String,Object>> getBusTypeList(Map<String, Object> params);
}

