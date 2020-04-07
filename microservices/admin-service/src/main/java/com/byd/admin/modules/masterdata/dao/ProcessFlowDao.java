package com.byd.admin.modules.masterdata.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.byd.admin.modules.masterdata.entity.ProcessFlowEntity;

/**
 * 工艺流程表-基础数据
 * 
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
public interface ProcessFlowDao extends BaseMapper<ProcessFlowEntity> {
	List<ProcessFlowEntity>  selectWithPage(Pagination page,@Param("deptIds")Long[] depts,@Param("busTypeCode")String busTypeCode,@Param("busTypeId")Long busTypeId,@Param("vehicleType")String vehicleType);
}
