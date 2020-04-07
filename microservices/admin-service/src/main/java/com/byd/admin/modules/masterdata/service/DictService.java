package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.masterdata.entity.DictEntity;
import com.byd.utils.PageUtils;

/**
 * 数据字典
 *
 * @author Mark 
 * @since 3.1.0 2018-01-27
 */
public interface DictService extends IService<DictEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    public List<Map<String,Object>> getDictlistByType(Map<String, Object> params);

    //获取字典表工厂OrderNum用于部件MES生成编号
    public int queryMasterDictWerksOrderNum(Map<String, Object> params);
}

