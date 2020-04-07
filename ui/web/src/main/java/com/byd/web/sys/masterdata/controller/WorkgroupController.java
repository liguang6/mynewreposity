package com.byd.web.sys.masterdata.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.byd.utils.R;
import com.byd.web.sys.masterdata.entity.SysDeptEntity;
import com.byd.web.sys.masterdata.service.WorkgroupRemote;



/**
 * 标准工序表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@Controller
@RequestMapping("/masterdata/workgroup")
public class WorkgroupController {
    @Autowired
    private WorkgroupRemote workgroupRemote;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params, String deptIds, String isPlanNode, String isMonitoryPoint){
    	params.put("deptIds", deptIds);
    	params.put("isPlanNode", isPlanNode);
    	params.put("isMonitoryPoint", isMonitoryPoint);
    	
    	return workgroupRemote.list(params);
    }
    
    /**
     * 列表
     */
    @RequestMapping("/workGrouplistByPage")
    @ResponseBody
    public R workGrouplistByPage(@RequestParam Map<String,String> map){
    	
    	return workgroupRemote.workGrouplistByPage(map);
    }
    
    /*
     * 小班组列表
     */
    @RequestMapping("/getWorkTeamListByPage")
    @ResponseBody
    public R getWorkTeamListByPage(@RequestParam Map<String,String> map){
    	
    	return workgroupRemote.getWorkTeamListByPage(map);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @ResponseBody
    public R info(@PathVariable("id") Long id){
       return workgroupRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @ResponseBody
    public R save(@RequestBody SysDeptEntity dept){
    	return workgroupRemote.save(dept);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @ResponseBody
    public R update(@RequestBody SysDeptEntity dept){
    	return workgroupRemote.update(dept);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody Long[] ids){
        return workgroupRemote.delete(ids);
    }
    
    /**
     * 删除单条记录
     * @param id
     * @return
     */
    @RequestMapping("/delete_single")
    @ResponseBody
    public R deleteSingle(@RequestParam Long id){
    	return workgroupRemote.deleteSingle(id);
    }
    
    @RequestMapping("/editorProcess")
    public String editorProcess(@RequestParam Long id,Model model) throws IllegalAccessException{
    	R r = workgroupRemote.editWorkgroup(id);
    	Map<String,Object> map = (Map)r.get("dept");
    	
    	model.addAttribute("entity", map);
    	return "/masterdata/org/workgroup_editor";
    	
    }
    
    /**
     * 通过线别查询
     */
    @RequestMapping("/listByLineName")
    @ResponseBody
    public R info(@RequestParam Map<String, Object> params){
    	return workgroupRemote.info(params);
    }
    
    @RequestMapping("/getWorkgroupList")
    @ResponseBody
    public R getProcessList(@RequestParam Map<String,String> map){
    	return workgroupRemote.getWorkgroupList(map);
    }
    
    @RequestMapping("/getTeamList")
    @ResponseBody
    public R getTeamList(@RequestParam Map<String,String> map){
    	return workgroupRemote.getTeamList(map);
    }
    
    @RequestMapping("/getStandardWorkGroupList")
    @ResponseBody
    public R getStandardWorkGroupList(@RequestParam Map<String,String> map){
    	return workgroupRemote.getStandardWorkGroupList(map);
    }
    
    @RequestMapping("/getWorkShopByCode")
    @ResponseBody
    public R getWorkShopByCode(@RequestParam Map<String,String> map){
    	return workgroupRemote.getWorkShopByCode(map);
    }
    
    @RequestMapping("/saveDept")
    @ResponseBody
    public R saveDept(@RequestParam Map<String, String> map){
    	return workgroupRemote.saveDept(map);
    }
    
    
    @RequestMapping("/updateDept")
    @ResponseBody
    public R updateDept(@RequestParam Map<String, String> map){
    	return workgroupRemote.updateDept(map);
    }
    
    @RequestMapping("/getWorkGroupById")
    @ResponseBody
    public R getWorkGroupById(@RequestParam Map<String,String> map){
    	return workgroupRemote.getWorkGroupById(map);
    }
    
    
    @RequestMapping("/deleteDept")
    @ResponseBody
    public R deleteDept(@RequestParam Map<String,String> map){
    	return workgroupRemote.delete_dept(map);
    }
    
    @RequestMapping("/getWorkGroupByCode")
    @ResponseBody
    public R getWorkGroupByCode(@RequestParam Map<String,String> map){
    	return workgroupRemote.getWorkGroupByCode(map);
    }
    
    @RequestMapping("/getWorkTeamById")
    @ResponseBody
    public R getWorkTeamById(@RequestParam Map<String,String> map){
    	return workgroupRemote.getWorkTeamById(map);
    }
    
    @RequestMapping("/getDeptWorkTeamById")
    @ResponseBody
    public R getDeptWorkTeamById(@RequestParam Map<String,String> map){
    	return workgroupRemote.getDeptWorkTeamById(map);
    }
    
}
