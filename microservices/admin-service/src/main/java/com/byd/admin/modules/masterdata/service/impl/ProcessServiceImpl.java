package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.ProcessDao;
import com.byd.admin.modules.masterdata.entity.ProcessEntity;
import com.byd.admin.modules.masterdata.service.ProcessService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;


@Service("settingProcessService")
public class ProcessServiceImpl extends ServiceImpl<ProcessDao, ProcessEntity> implements ProcessService {
	@Autowired
	ProcessDao settingProcessDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params,Long[] deptIds,String isPlanNode,String isMonitoryPoint) {
    	String factoryName = params.get("WERKS")==null?null:String.valueOf(params.get("WERKS"));
        String workshopName = params.get("WORKSHOP")==null?null:String.valueOf(params.get("WORKSHOP"));
        String processCode = params.get("processCode")==null?null:String.valueOf(params.get("processCode"));
        String processName = params.get("processName")==null?null:String.valueOf(params.get("processName"));
        String processType = params.get("processType")==null?null:String.valueOf(params.get("processType"));
        
        Wrapper<ProcessEntity>  wrapper =   new EntityWrapper<ProcessEntity>()
        .eq("del", "0")
        .in(deptIds!=null,"dept_id", deptIds)
        .eq(!StringUtils.isEmpty(isMonitoryPoint),"monitory_point_flag", isMonitoryPoint)
        .isNotNull(!StringUtils.isEmpty(isPlanNode),"PLAN_NODE_CODE")
        .like(!StringUtils.isEmpty(processCode), "process_code",processCode)
        .like(!StringUtils.isEmpty(processName), "process_name",processName)
        .eq(!StringUtils.isEmpty(factoryName),"werks", factoryName)
        .in(!StringUtils.isEmpty(workshopName),"workshop", workshopName)
        .eq(!StringUtils.isEmpty(processType),"process_type", processType);
        
        Page<ProcessEntity> page = this.selectPage(
                new Query<ProcessEntity>(params).getPage(),
                wrapper
        );

        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> getProcessList(Map<String,String> map) {
		return settingProcessDao.getProcessList(map);
	}

	@Override
	public int checkRepeat(ProcessEntity entity) {
		return settingProcessDao.checkRepeat(entity);
	}
	
	@Override
	public List<Map<String,Object>> getWorkshopProcessList(Map<String,Object> map){
		return settingProcessDao.getWorkshopProcessList(map);
	}

}
