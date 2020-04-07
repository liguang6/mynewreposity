package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.dao.WorkgroupDao;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.admin.modules.masterdata.service.WorkgroupService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

@Service("workgroupService")
public class WorkgroupServiceImpl extends ServiceImpl<WorkgroupDao, DeptEntity> implements WorkgroupService {
	@Autowired
	WorkgroupDao workgroupDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params,Long[] deptIds,String isPlanNode,String isMonitoryPoint) {
        String lineName = params.get("lineName")==null?null:String.valueOf(params.get("lineName"));
    	String factoryName = params.get("factoryName")==null?null:String.valueOf(params.get("factoryName"));
        String workshopName = params.get("workshopName")==null?null:String.valueOf(params.get("workshopName"));
        String processCode = params.get("processCode")==null?null:String.valueOf(params.get("processCode"));
        String processName = params.get("processName")==null?null:String.valueOf(params.get("processName"));
        Page<DeptEntity> page = this.selectPage(
                new Query<DeptEntity>(params).getPage(),
                new EntityWrapper<DeptEntity>()
                .in(deptIds!=null,"dept_id", deptIds)
                .eq(!StringUtils.isEmpty(isMonitoryPoint),"monitory_point_flag", isMonitoryPoint)
                .isNotNull(!StringUtils.isEmpty(isPlanNode),"plan_node_id")
                .like(!StringUtils.isEmpty(lineName),"line_name", lineName).or().like(!StringUtils.isEmpty(lineName),"line", lineName)
                .like(!StringUtils.isEmpty(factoryName), "werks_name",factoryName).like(!StringUtils.isEmpty(factoryName),"werks", factoryName)
                .like(!StringUtils.isEmpty(workshopName), "workshop_name",workshopName).like(!StringUtils.isEmpty(workshopName),"workshop", workshopName)
                .like(!StringUtils.isEmpty(processCode), "process_code",processCode)
                .like(!StringUtils.isEmpty(processName), "process_name",processName)
        );

        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> getProcessList(Map<String,String> map) {
		return workgroupDao.getProcessList(map);
	}

}
