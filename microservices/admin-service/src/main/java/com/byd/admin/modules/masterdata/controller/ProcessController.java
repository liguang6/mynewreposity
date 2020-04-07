package com.byd.admin.modules.masterdata.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.admin.modules.masterdata.entity.DictEntity;
import com.byd.admin.modules.masterdata.entity.ProcessEntity;
import com.byd.admin.modules.masterdata.service.DictService;
import com.byd.admin.modules.masterdata.service.ProcessService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;



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
    private ProcessService settingProcessService;
    
    @Autowired
    private DictService sysDictService;
    
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params){
    	String deptIds = params.get("deptIds")==null?null:params.get("deptIds").toString();
    	String isPlanNode = params.get("isPlanNode")==null?null:params.get("isPlanNode").toString();
    	String isMonitoryPoint = params.get("isMonitoryPoint")==null?null:params.get("isMonitoryPoint").toString();
    	Long[] Ldepts = null;
    	if(!StringUtils.isEmpty(deptIds)){
    		String[] deptStrlist = deptIds.split(",");
    		Ldepts = new Long[deptStrlist.length];
    		for(int i=0;i<deptStrlist.length;i++){
    			Ldepts[i] = Long.valueOf(deptStrlist[i]);
    		}
    	}
    	PageUtils page = settingProcessService.queryPage(params,Ldepts,isPlanNode,isMonitoryPoint);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @ResponseBody
    public R info(@PathVariable("id") Long id){
        ProcessEntity settingProcess = settingProcessService.selectById(id);

        return R.ok().put("settingProcess", settingProcess);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @ResponseBody
    public R save(@RequestBody ProcessEntity settingProcess){
    	try{
    	Map<String, Object> condMap=new HashMap<String, Object>();
    	condMap.put("PROCESS_CODE", settingProcess.getProcessCode());
        condMap.put("WERKS", settingProcess.getWerks());
        condMap.put("DEL", "0");
    	List<ProcessEntity> retList=settingProcessService.selectByMap(condMap);
    	if(retList!=null&&retList.size()>0){
    		throw new RuntimeException("工序代码 "+settingProcess.getProcessCode()+" 在工厂 "+settingProcess.getWerks()+" 中已经存在，不能再次添加！");
    	}
    	
        ValidatorUtils.validateEntity(settingProcess);
        // 替换空格、制表符、换页符等空白字符
        settingProcess.setProcessName(settingProcess.getProcessName().replaceAll("\\s*", ""));
        settingProcess.setMonitoryPointFlag("on".equals(settingProcess.getMonitoryPointFlag())?"X":"0");
        
        if(!StringUtils.isEmpty(settingProcess.getSectionCode())){
            DictEntity sectionDict = sysDictService.selectOne(new EntityWrapper<DictEntity>().eq("type", "SECTION").eq("code", settingProcess.getSectionCode()));
            settingProcess.setSectionCode(sectionDict.getCode());
            settingProcess.setSectionName(sectionDict.getValue());
        }
        
        //计划节点不是必填
        if(!StringUtils.isEmpty(settingProcess.getPlanNodeCode())){
        	DictEntity planNodeDict = sysDictService.selectOne(new EntityWrapper<DictEntity>().eq("type", "PLAN_NODE").eq("code", settingProcess.getPlanNodeCode()));
            settingProcess.setPlanNodeName(planNodeDict.getValue());
            settingProcess.setPlanNodeCode(planNodeDict.getCode());
        }
        
        Map<String,Object> currentUser = userUtils.getUser();
        
        settingProcess.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
        settingProcess.setEditDate(DateUtils.format(new Date()));
        
        int count=settingProcessService.checkRepeat(settingProcess);
        if(count>0) {
        	return R.error(settingProcess.getProcessCode()+"在工厂"+settingProcess.getWerks()+"下已存在，同工厂下不允许存在工序代码相同的工序！");
        }
        settingProcess.setDel("0");
        settingProcessService.insert(settingProcess);
    	} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
        
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @ResponseBody
    public R update(@RequestBody ProcessEntity settingProcess){
        ValidatorUtils.validateEntity(settingProcess);
        settingProcess.setProcessName(settingProcess.getProcessName().trim());
        settingProcess.setMonitoryPointFlag("on".equals(settingProcess.getMonitoryPointFlag())?"X":"0");
        settingProcess.setDel("0");
        
        if(!StringUtils.isEmpty(settingProcess.getSectionCode())){
	        DictEntity sectionDict = sysDictService.selectOne(new EntityWrapper<DictEntity>().eq("type", "SECTION").eq("code", settingProcess.getSectionCode()));
	        settingProcess.setSectionCode(sectionDict.getCode());
	        settingProcess.setSectionName(sectionDict.getValue());
        }
        
        if(!StringUtils.isEmpty(settingProcess.getPlanNodeCode())){
        	DictEntity planNodeDict = sysDictService.selectOne(new EntityWrapper<DictEntity>().eq("type", "PLAN_NODE").eq("code", settingProcess.getPlanNodeCode()));
            settingProcess.setPlanNodeName(planNodeDict.getValue());
            settingProcess.setPlanNodeCode(planNodeDict.getCode());//设置Code.
        } 
        
        Map<String,Object> currentUser = userUtils.getUser();
        settingProcess.setEditor(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
        settingProcess.setEditDate(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        
        settingProcessService.updateAllColumnById(settingProcess);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody Long[] ids){
        settingProcessService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 删除单条记录
     * @param id
     * @return
     */
    @RequestMapping("/delete_single")
    @ResponseBody
    public R deleteSingle(Long id){
    	ProcessEntity settingProcess = settingProcessService.selectById(id);
    	settingProcess.setDel("X");
    	settingProcessService.updateById(settingProcess);
    	return R.ok();
    }
    
    @RequestMapping("/editorProcess")
    @ResponseBody
    public R editorProcess(@RequestParam Long processId) throws IllegalAccessException{
    	ProcessEntity entity =  settingProcessService.selectById(processId);
    	if(entity==null){
    		throw new IllegalAccessException("工序不存在");
    	}
    	return R.ok().put("process", entity);
    }
    
    /**
     * 通过线别查询
     */
    @RequestMapping("/listByLineName")
    @ResponseBody
    public R info(@RequestParam Map<String, Object> params){
        List<ProcessEntity> settingProcessList = settingProcessService.selectByMap(params);

        return R.ok().put("settingProcessList", settingProcessList);
    }
    // 模糊查找工序
    @RequestMapping("/getProcessList")
    @ResponseBody
    public R getProcessList(@RequestParam Map<String,String> map){
    	List<Map<String,Object>> list = settingProcessService.getProcessList(map);
        return R.ok().put("data", list);
    }
}
