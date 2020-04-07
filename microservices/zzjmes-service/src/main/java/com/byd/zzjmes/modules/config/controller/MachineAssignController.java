package com.byd.zzjmes.modules.config.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.config.service.MachineAssignService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月11日 下午2:51:23 
 * 类说明 
 */
@RestController
@RequestMapping("machineAssign")
public class MachineAssignController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MachineAssignService machineAssignService;
    @Autowired
    private UserUtils userUtils;
    
    @RequestMapping("/getMachineAssignList")
    public R getMachineAssignList(@RequestParam Map<String, Object> params){
		return R.ok().put("page", machineAssignService.getMachineAssignList(params));
	}
    
    @RequestMapping("/insertMachineAssign")
	public R insertMachineAssign(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("del", "0");
    	params.put("creator", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
    	params.put("create_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		int result =machineAssignService.insertMachineAssign(params);
		return R.ok().put("result", result);
	}
    
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	Map<String,Object> param=new HashMap<String,Object>();
    	param.put("ID", id);
    	Map<String,Object> machineAssign = machineAssignService.selectById(param);

        return R.ok().put("machineAssign", machineAssign);
    }
    
    @RequestMapping("/updateMachineAssign")
	public R updateMachineAssign(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("USERNAME").toString());
    	params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		int result =machineAssignService.updateMachineAssign(params);
		return R.ok().put("result", result);
	}
    
    @RequestMapping("/delMachineAssign")
	public R delMachineAssign(@RequestParam Map<String, Object> params){
    try{
    	Map<String,Object> cond=new HashMap<String,Object>();
    	cond.put("ID", params.get("id"));
    	Map<String,Object> machineAssign = machineAssignService.selectById(cond);
    	List<Map<String,Object>> retMachinePlan=machineAssignService.selectMachineByNo(machineAssign);//通过machionecode查询是否存在
    	if(retMachinePlan!=null&&retMachinePlan.size()>0){
    		throw new RuntimeException(machineAssign.get("machinecode")+" 已有生产计划，不允许删除！");
    	}
    } catch (Exception e) {
		//e.printStackTrace();
		return R.error(e.getMessage());
	}
    	
		int result =machineAssignService.delMachineAssign(params);
		return R.ok().put("result", result);
	}
    
}
