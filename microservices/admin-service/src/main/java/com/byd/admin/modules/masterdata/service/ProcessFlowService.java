package com.byd.admin.modules.masterdata.service;

import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.masterdata.entity.ProcessFlowEntity;
import com.byd.utils.PageUtils;

/**
 * 工艺流程表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
public interface ProcessFlowService extends IService<ProcessFlowEntity> {

    PageUtils queryPage(Map<String, Object> params);
    public Page<ProcessFlowEntity> selectWithPage(Page<ProcessFlowEntity> page,Long[] depts,String busTypeCode,Long busTypeId,String vehicleType);
}

