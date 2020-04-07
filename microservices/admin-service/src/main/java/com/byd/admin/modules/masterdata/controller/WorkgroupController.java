package com.byd.admin.modules.masterdata.controller;

import java.math.BigDecimal;
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

import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.admin.modules.masterdata.service.WorkgroupNoService;
import com.byd.admin.modules.masterdata.service.WorkgroupService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;



/**
 * 车间班组、小班组-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@Controller
@RequestMapping("/masterdata/workgroup")
public class WorkgroupController {
    @Autowired
    private WorkgroupService workgroupService;
    @Autowired
    private WorkgroupNoService workgroupNoService;
    
    @Autowired
    private UserUtils userUtils;
    @Autowired
	private DeptService sysDeptService;
    
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
    	PageUtils page = workgroupService.queryPage(params,Ldepts,isPlanNode,isMonitoryPoint);
        return R.ok().put("page", page);
    }
    
    @RequestMapping("/workGrouplistByPage")
    @ResponseBody
    public R workGrouplistByPage(@RequestParam Map<String, Object> params){
    	
    	PageUtils page = sysDeptService.getWorkshopWorkgroupListByPage(params);
        return R.ok().put("page", page);
    }
    
    
    @RequestMapping("/getWorkTeamListByPage")
    @ResponseBody
    public R getWorkTeamListByPage(@RequestParam Map<String, Object> params){
    	
    	PageUtils page = sysDeptService.getWorkTeamListByPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @ResponseBody
    public R info(@PathVariable("id") Long id){
    	DeptEntity dept = workgroupService.selectById(id);

        return R.ok().put("dept", dept);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @ResponseBody
    public R save(@RequestBody DeptEntity dept){
        ValidatorUtils.validateEntity(dept);
    
        
        Map<String,Object> currentUser = userUtils.getUser();
     
        
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @ResponseBody
    public R update(@RequestBody DeptEntity dept){
        ValidatorUtils.validateEntity(dept);
        Map<String,Object> currentUser = userUtils.getUser();
    
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody Long[] ids){
    	workgroupService.deleteBatchIds(Arrays.asList(ids));

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
    	workgroupService.deleteById(id);
    	return R.ok();
    }
    
    @RequestMapping("/editorProcess")
    @ResponseBody
    public R editorProcess(@RequestParam Long processId) throws IllegalAccessException{
    	DeptEntity dept =  workgroupService.selectById(processId);
    	if(dept==null){
    		throw new IllegalAccessException("工序不存在");
    	}
    	return R.ok().put("dept", dept);
    }
    
    /**
     * 通过线别查询
     */
    @RequestMapping("/listByLineName")
    @ResponseBody
    public R info(@RequestParam Map<String, Object> params){
        List<DeptEntity> settingProcessList = workgroupService.selectByMap(params);

        return R.ok().put("settingProcessList", settingProcessList);
    }
    // 模糊查找工序
    @RequestMapping("/getProcessList")
    @ResponseBody
    public R getProcessList(@RequestParam Map<String,String> map){
    	List<Map<String,Object>> list = workgroupService.getProcessList(map);
        return R.ok().put("data", list);
    }
    
    @RequestMapping("/getStandardWorkGroupList")
    @ResponseBody
    public R getStandardWorkGroupList(@RequestParam Map<String,String> map){
    	List<Map<String,Object>> list = workgroupNoService.getStandardWorkgroupList(map);
        return R.ok().put("result", list);
    }
    
    @RequestMapping("/getWorkShopByCode")
    @ResponseBody
    public R getWorkShopByCode(@RequestParam Map<String,String> map){
    	List<Map<String,Object>> list = workgroupNoService.getWorkShopByCode(map);
        return R.ok().put("result", list);
    }
    
    @RequestMapping("/getWorkGroupByCode")
    @ResponseBody
    public R getWorkGroupByCode(@RequestParam Map<String,String> map){
    	List<Map<String,Object>> list = workgroupNoService.getWorkGroupByCode(map);
        return R.ok().put("result", list);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/saveDept")
    @ResponseBody
    public R saveDept(@RequestParam Map<String,String> map){
    	try{
    	if("WORKGROUP".equals(map.get("DEPT_TYPE"))){//查询在这个 工厂，车间，班组是否存在
    		Map<String,Object> condMap=new HashMap<String,Object>();
    		condMap.put("WORKGROUP", map.get("WORK_GROUP_NO"));
    		condMap.put("WORKSHOP", map.get("WORKSHOP"));
    		condMap.put("WERKS", map.get("WERKS"));
    		
    		List<Map<String,Object>> retGroupList=workgroupNoService.getWorkshopWorkgroupByCode(condMap);
    		
    		if(retGroupList!=null&&retGroupList.size()>0){
    			throw new RuntimeException("工厂 "+map.get("WERKS")+" 车间 "+map.get("WORKSHOP")+" 班组 "+map.get("WORK_GROUP_NO")+" 已经存在，不能重复添加!");
    		}
    	}
    	
    	if("TEAM".equals(map.get("DEPT_TYPE"))){//查询在这个 工厂，车间，班组,小班组 是否存在
    		Map<String,Object> condMap=new HashMap<String,Object>();
    		condMap.put("WORKTEAM", map.get("WORK_GROUP_NO"));//小班组
    		condMap.put("WORKGROUP", map.get("WORKGROUP"));//班组
    		condMap.put("WORKSHOP", map.get("WORKSHOP"));
    		condMap.put("WERKS", map.get("WERKS"));
    		
    		List<Map<String,Object>> retGroupList=workgroupNoService.getWorkshopWorkgroupWorkTeamByCode(condMap);
    		
    		if(retGroupList!=null&&retGroupList.size()>0){
    			throw new RuntimeException("工厂 "+map.get("WERKS")+" 车间 "+map.get("WORKSHOP")+" 班组 "+map.get("WORKGROUP")+" 小班组 "+map.get("WORK_GROUP_NO")+" 已经存在，不能重复添加!");
    		}
    	}
    	
    	DeptEntity dept=new DeptEntity();
    	dept.setParentId(map.get("PARENT_ID")==null?0:Long.parseLong(map.get("PARENT_ID").toString()));
    	dept.setCode(map.get("WORK_GROUP_NO")==null?"":map.get("WORK_GROUP_NO").toString());
    	dept.setName(map.get("WORK_GROUP_NAME")==null?"":map.get("WORK_GROUP_NAME").toString());
    	dept.setTreeLeaf("1");
    	
    	String tree_level_str=map.get("TREE_LEVEL")==null?"":map.get("TREE_LEVEL").toString();
    	
    		if(!"".equals(tree_level_str)){
    			BigDecimal tree_level_d=new BigDecimal(tree_level_str);
    			tree_level_d=tree_level_d.add(new BigDecimal(1));
    			dept.setTreeLevel(tree_level_d.toString());
    		}
    	
    	dept.setTreeNames(map.get("TREE_NAMES")==null?"":map.get("TREE_NAMES").toString().concat(" ").concat(map.get("WORK_GROUP_NAME").toString()));
    	dept.setDeptType(map.get("DEPT_TYPE"));
    	dept.setDeptKind("1");
    	dept.setStatus("0");
    	
    	//String currentUser = userUtils.getTokenUsername();
    	//Long userid=userUtils.getUserId();
    	Map<String,Object> currentUser = userUtils.getUser();
    	dept.setCreateBy(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	dept.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	dept.setUpdateBy(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	dept.setUpdateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
    	ValidatorUtils.validateEntity(dept);
    
    	sysDeptService.insert(dept);
    	
    	//新增成功后，要把父id的tree_leaf改为0
    	DeptEntity dept_parent=new DeptEntity();
    	dept_parent.setDeptId(map.get("PARENT_ID")==null?0:Long.parseLong(map.get("PARENT_ID").toString()));
    	dept_parent.setTreeLeaf("0");
    	sysDeptService.updateById(dept_parent);
	    } catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
        
        return R.ok();
    }
    
    /**
     * 保存
     */
    @RequestMapping("/updateDept")
    @ResponseBody
    public R updateDept(@RequestParam Map<String,String> map){
    	DeptEntity dept=new DeptEntity();
    	dept.setDeptId(Long.parseLong(map.get("DEPT_ID").toString()));
    	dept.setId(Long.parseLong(map.get("DEPT_ID").toString()));
    	dept.setParentId(map.get("PARENT_ID")==null?0:Long.parseLong(map.get("PARENT_ID").toString()));
    	dept.setCode(map.get("WORK_GROUP_NO")==null?"":map.get("WORK_GROUP_NO").toString());
    	dept.setName(map.get("WORK_GROUP_NAME")==null?"":map.get("WORK_GROUP_NAME").toString());
    	dept.setTreeLeaf("1");
    	String tree_level_str=map.get("TREE_LEVEL")==null?"":map.get("TREE_LEVEL").toString();
    	
		if(!"".equals(tree_level_str)){
			BigDecimal tree_level_d=new BigDecimal(tree_level_str);
			tree_level_d=tree_level_d.add(new BigDecimal(1));
			dept.setTreeLevel(tree_level_d.toString());
		}
		
    	if(map.get("TREE_NAMES")!=null){
    	 
    		String[] tress_names_array=map.get("TREE_NAMES").toString().split(" ");
    		String tress_names_update="";
    		for(int m=0;m<tress_names_array.length;m++){
    			if("".equals(tress_names_update)){
    				tress_names_update=tress_names_array[m];
    			}else{
    				tress_names_update=tress_names_update.concat(" ").concat(tress_names_array[m].toString());
    			}
    			
    		}
    		dept.setTreeNames(tress_names_update.concat(" ").concat(map.get("WORK_GROUP_NAME")==null?"":map.get("WORK_GROUP_NAME").toString()));
    	
    	 
    	}
    	
    	
    	dept.setDeptType(map.get("DEPT_TYPE"));
    	dept.setDeptKind("1");
    	dept.setStatus("0");
    	
    	//String currentUser = userUtils.getTokenUsername();
    	Map<String,Object> currentUser = userUtils.getUser();
    	Long userid=userUtils.getUserId();
    	dept.setUpdateBy(currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	dept.setUpdateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
    	ValidatorUtils.validateEntity(dept);
    
    	sysDeptService.updateById(dept);
    	
    	//新增成功后，要把父id的tree_leaf改为0
    	DeptEntity dept_parent=new DeptEntity();
    	dept_parent.setDeptId(map.get("PARENT_ID")==null?0:Long.parseLong(map.get("PARENT_ID").toString()));
    	dept_parent.setTreeLeaf("0");
    	sysDeptService.updateById(dept_parent);
        
        return R.ok();
    }
    
    
    @RequestMapping("/getWorkGroupById")
    @ResponseBody
    public R getWorkGroupById(@RequestParam Map<String,String> map){
    	Map<String,Object> workgroupRes = workgroupNoService.getWorkGroupById(map);
        return R.ok().put("result", workgroupRes);
    }
    
    /**
     * 删除单条记录
     * @param id
     * @return
     */
    @RequestMapping("/delete_dept")
    @ResponseBody
    public R delete_dept(@RequestParam Map<String,String> map){
    	sysDeptService.deleteById(Long.parseLong(map.get("DEPT_ID").toString()));
    	return R.ok();
    }
    
    @RequestMapping("/getWorkTeamById")
    @ResponseBody
    public R getWorkTeamById(@RequestParam Map<String,String> map){
    	Map<String,Object> workteamRes = workgroupNoService.getWorkTeamById(map);
        return R.ok().put("result", workteamRes);
    }
    
    @RequestMapping("/getDeptWorkTeamById")
    @ResponseBody
    public R getDeptWorkTeamById(@RequestParam Map<String,String> map){
    	Map<String,Object> workgroupRes = workgroupNoService.getDeptWorkTeamById(map);
        return R.ok().put("result", workgroupRes);
    }
}
