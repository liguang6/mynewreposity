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
import com.byd.web.sys.masterdata.entity.ProcessEntity;
import com.byd.web.sys.masterdata.service.ProcessRemote;



/**
 * 标准工序表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@Controller
@RequestMapping("/masterdata/process")
public class ProcessController {
    @Autowired
    private ProcessRemote processRemote;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params, String deptIds, String isPlanNode, String isMonitoryPoint){
    	params.put("deptIds", deptIds);
    	params.put("isPlanNode", isPlanNode);
    	params.put("isMonitoryPoint", isMonitoryPoint);
    	
    	return processRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @ResponseBody
    public R info(@PathVariable("id") Long id){
       return processRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @ResponseBody
    public R save(@RequestBody ProcessEntity settingProcess){
    	return processRemote.save(settingProcess);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @ResponseBody
    public R update(@RequestBody ProcessEntity settingProcess){
    	return processRemote.update(settingProcess);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody Long[] ids){
        return processRemote.delete(ids);
    }
    
    /**
     * 删除单条记录
     * @param id
     * @return
     */
    @RequestMapping("/delete_single")
    @ResponseBody
    public R deleteSingle(@RequestParam Long id){
    	return processRemote.deleteSingle(id);
    }
    
    @RequestMapping("/editorProcess")
    public String editorProcess(@RequestParam Long processId,Model model) throws IllegalAccessException{
    	R r = processRemote.editorProcess(processId);
    	Map<String,Object> map = (Map)r.get("process");
    	
    	model.addAttribute("entity", map);
    	return "/sys/masterdata/process_editor";
    	
    }
    
    /**
     * 通过线别查询
     */
    @RequestMapping("/listByLineName")
    @ResponseBody
    public R info(@RequestParam Map<String, Object> params){
    	return processRemote.info(params);
    }
    // 模糊查找工序
    @RequestMapping("/getProcessList")
    @ResponseBody
    public R getProcessList(@RequestParam Map<String,String> map){
    	return processRemote.getProcessList(map);
    }
}
