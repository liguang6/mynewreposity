package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.ProcessFlowDao;
import com.byd.admin.modules.masterdata.entity.ProcessFlowEntity;
import com.byd.admin.modules.masterdata.service.ProcessFlowService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;


@Service("settingProcessFlowService")
public class ProcessFlowServiceImpl extends ServiceImpl<ProcessFlowDao, ProcessFlowEntity> implements ProcessFlowService {

	@Autowired
	ProcessFlowDao processFlowDao;
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String linename = String.valueOf(params.get("lineName"));
        Page<ProcessFlowEntity> page = this.selectPage(
                new Query<ProcessFlowEntity>(params).getPage(),
                new EntityWrapper<ProcessFlowEntity>().eq(StringUtils.isNotBlank(linename) ,"line_name", linename)
        );

        return new PageUtils(page);
    }
    
    public Page<ProcessFlowEntity> selectWithPage(Page<ProcessFlowEntity> page,Long[] depts,String busTypeCode,Long busTypeId,String vehicleType){
    	List<ProcessFlowEntity> list = processFlowDao.selectWithPage(page, depts, busTypeCode, busTypeId, vehicleType);
        return page.setRecords(list);
    }

}
