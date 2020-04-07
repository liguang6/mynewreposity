package com.byd.admin.modules.masterdata.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.admin.modules.masterdata.entity.ProcessFlowEntity;
import com.byd.admin.modules.masterdata.service.ProcessFlowService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.utils.UserUtils;



/**
 * 工艺流程表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@Controller
@RequestMapping("/masterdata/processflow")
public class ProcessFlowController {
    @Autowired
    private ProcessFlowService settingProcessFlowService;
    
    @Autowired
    private UserUtils userUtils;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = settingProcessFlowService.queryPage(params);
        return R.ok().put("page", page);
    }
    
    
    @RequestMapping("/listUnique")
    @ResponseBody
    public R listUnique(@RequestParam Map<String, Object> params,String deptIds,Long busTypeId,String vehicleType,String busTypeCode){
    	Page<ProcessFlowEntity> page = new Query<ProcessFlowEntity>(params).getPage();
    	Long[] Ldepts = null;
    	if(!StringUtils.isEmpty(deptIds)){
    		String[] deptStrlist = deptIds.split(",");
    		Ldepts = new Long[deptStrlist.length];
    		for(int i=0;i<deptStrlist.length;i++){
    			Ldepts[i] = Long.valueOf(deptStrlist[i]);
    		}
    	}
    	Page<ProcessFlowEntity> rsu =  settingProcessFlowService.selectWithPage(page, Ldepts, busTypeCode, busTypeId, vehicleType);
    	PageUtils pageUtils = new PageUtils(rsu);
    	return R.ok().put("page", pageUtils);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{deptId}/{busTypeCode}/{vehicleType}/")
    @ResponseBody
    public R info(@PathVariable("deptId") Long deptId,@PathVariable("busTypeCode")String busTypeCode,@PathVariable("vehicleType")String vehicleType){
        //车型工序信息的工序
        List<ProcessFlowEntity> oneSettingProcessFlow = settingProcessFlowService.selectList(new EntityWrapper<ProcessFlowEntity>()
        		.eq("dept_id", deptId)
        		.eq("bus_type_code", busTypeCode)
        		.eq("vehicle_type", vehicleType));
        return R.ok().put("processFlow", oneSettingProcessFlow);
    }

    
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    @ResponseBody
    public R save(@RequestBody List<ProcessFlowEntity> settingProcessFlows){
    	if(CollectionUtils.isEmpty(settingProcessFlows)){
    		throw new IllegalArgumentException("新增车型工序,入参不能为空");
    	}
    	ProcessFlowEntity query = settingProcessFlows.get(0);
    	List<ProcessFlowEntity> list=settingProcessFlowService.selectList(
    			new EntityWrapper<ProcessFlowEntity>()
    			.eq("dept_id", query.getDeptId())
    			.eq("workshop_name", query.getWorkshopName())
    			.eq("line_name", query.getLineName())
    			.eq("bus_type_code", query.getBusTypeCode())
    			.eq("vehicle_type", query.getVehicleType()));
    	if(list.size()>0) {
    		return R.error("该线别已存在该车型的工序配置");
    	}
    	
    	Map<String,Object> currentUser = userUtils.getUser();
    	
    	for(ProcessFlowEntity entity:settingProcessFlows){
    		if(entity.getProcessCode()!=null && !entity.getProcessCode().equals("")) {
	    		entity.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
	    		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
    		}else {
    			return R.error("第"+(entity.getSortNo()+1)+"行工序代码不能为空");
    		}
    	}
        settingProcessFlowService.insertBatch(settingProcessFlows);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @ResponseBody
    public R update(@RequestBody List<ProcessFlowEntity> settingProcessFlows){
        //1.删除原有记录  2.新增记录
        if(CollectionUtils.isEmpty(settingProcessFlows)){
        	throw new IllegalArgumentException("车型工序修改: 入参不能为空");
        }
        ProcessFlowEntity query = settingProcessFlows.get(0);
        settingProcessFlowService.delete(
    			new EntityWrapper<ProcessFlowEntity>()
    			.eq("dept_id", query.getDeptId())
    			.eq("workshop_name", query.getWorkshopName())
    			.eq("line_name", query.getLineName())
    			.eq("bus_type_code", query.getBusTypeCode())
    			.eq("vehicle_type", query.getVehicleType()));
        Map<String,Object> currentUser = userUtils.getUser();
        for(ProcessFlowEntity entity:settingProcessFlows){
    		if(entity.getProcessCode()!=null && !entity.getProcessCode().equals("")) {
	    		entity.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
	    		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
    		}else {
    			return R.error("第"+(entity.getSortNo()+1)+"行工序代码不能为空");
    		}
    	}
        settingProcessFlowService.insertBatch(settingProcessFlows);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody Long[] ids){
        settingProcessFlowService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    
    /**
     * 删除单个
     * @param id
     * @return
     */
    @RequestMapping("/delete_single")
    @ResponseBody
    public R delete(Long deptId,String busTypeCode,String vehicleType){
    	//生产线 & 车型 & 车辆类型 相同的是同一个车型工序
    	settingProcessFlowService.delete(
    			new EntityWrapper<ProcessFlowEntity>()
    			.eq("dept_id", deptId)
    			.eq("bus_type_code", busTypeCode)
    			.eq("vehicle_type", vehicleType));
    	return R.ok();
    }
    
    @RequestMapping("/toProcessInfo")
    public String toProcessInfo(Long deptId,String busTypeCode,String vehicleType,Model model){
    	model.addAttribute("deptId", deptId);
    	model.addAttribute("busTypeCode", busTypeCode);
    	model.addAttribute("vehicleType", vehicleType);
    	return "/modules/setting/settingprocessflow_info";
    }
    
    @RequestMapping("/toProcessEditor")
    public String toProcessEditor(Long deptId,String busTypeCode,String vehicleType,Model model){
    	model.addAttribute("deptId", deptId);
    	model.addAttribute("busTypeCode", busTypeCode);
    	model.addAttribute("vehicleType", vehicleType);
    	return "/modules/setting/settingprocessflow_editor_new";
    }
    
    /**
     * 通过线别车辆类型等查询
     */
    @RequestMapping("/listByLineNamebusTypeCode")
    @ResponseBody
    public R info(@RequestParam Map<String, Object> params){
        List<ProcessFlowEntity> settingProcessFlowList = settingProcessFlowService.selectByMap(params);

        return R.ok().put("settingProcessFlowList", settingProcessFlowList);
    }

}
