package com.byd.admin.modules.masterdata.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.masterdata.service.BusTypeService;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.admin.modules.masterdata.service.MatMapService;
import com.byd.admin.modules.masterdata.service.ProcessService;
import com.byd.admin.modules.masterdata.service.TestRulesService;
import com.byd.admin.modules.masterdata.service.TestStandardService;
import com.byd.admin.modules.masterdata.service.TestToolService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;
import com.byd.utils.StringUtils;
import com.byd.utils.UserUtils;


/**
 * 主数据对外提供服务类
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
@RestController
@RequestMapping("/masterdataService")
public class MasterDataService{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DeptService sysDeptService;//组织结构-工厂、车间、线别、班组、小班组等
	@Autowired
	private BusTypeService busTypeService;//车型
	@Autowired
	private ProcessService processService;//标准工序
	@Autowired
	private TestStandardService testStandardService; //品质检验标准
	@Autowired
	private TestRulesService testRulesService; //品质抽检规则
	@Autowired
	private TestToolService testToolService; //品质抽检规则
	
	@Autowired
	private MatMapService matMapService; //物料/零部件图纸
	
	@Autowired
    private RedisUtils redisUtils;
	@Autowired
	private UserUtils userUtils;

	
	/**
	 * 获取所有工厂清单
	 * @param params CODE 工厂代码
	 * @return
	 */
    @RequestMapping("/getWerksList")
    public R getWerksList(@RequestParam Map<String, Object> params){
    	params.put("DEPT_TYPE", "WERKS");
    	List<Map<String,Object>> deptList = sysDeptService.getDeptChildNodes(params);
    	
    	return R.ok().put("data", deptList);
    }
	
	/**
	 * 根据工厂代码获取工厂下所有车间
	 * @param params CODE 工厂代码
	 * @return
	 */
    @RequestMapping("/getWerksWorkshopList")
    public R getWerksWorkshopList(@RequestParam Map<String, Object> params){
    	String CODE = params.get("WERKS")==null?params.get("CODE")==null?"":params.get("CODE").toString():params.get("WERKS").toString();
    	if(StringUtils.isEmpty(CODE)) {
    		return R.error("工厂代码不能为空！");
    	}
    	params.put("CODE", CODE);
    	params.put("DEPT_TYPE", "WORKSHOP");
    	List<Map<String,Object>> deptList = sysDeptService.getDeptChildNodes(params);
    	
    	return R.ok().put("data", deptList);
    }
	
    
	/**
	 * 根据工厂代码获取用户权限车间
	 * @param params CODE/WERKS 工厂代码 
	 * @return
	 */
    @RequestMapping("/getUserWorkshopByWerks")
    public R getUserWorkshopByWerks(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> rtn = new ArrayList<>();
    	
    	String allCode = "";
    	
       	String CODE = params.get("WERKS")==null?params.get("CODE")==null?"":params.get("CODE").toString():params.get("WERKS").toString();
    	if(StringUtils.isEmpty(CODE)) {
    		return R.error("工厂代码不能为空！");
    	}
    	String MENU_KEY = params.get("MENU_KEY")==null?"":params.get("MENU_KEY").toString();
    	if(StringUtils.isEmpty(MENU_KEY)) {
    		return R.error("菜单KEY不能为空！");
    	}
    	String ALL_OPTION = params.get("ALL_OPTION")==null?"":params.get("ALL_OPTION").toString();
    	//获取工厂下所有车间信息
    	params.put("CODE", CODE);
    	params.put("DEPT_TYPE", "WORKSHOP");
    	List<Map<String,Object>> deptList = sysDeptService.getDeptChildNodes(params);
    	
    	//获取用户授权的所有车间信息
		String username = userUtils.getTokenUsername();
		List<Map<String,Object>> userMesWorkshopList = redisUtils.getList(RedisKeys.getUserMesWorkshopKey(username));
		
		//过滤用户工厂下有权限的车间清单
		for (Map<String, Object> map : userMesWorkshopList) {
			if(map.get(MENU_KEY)!=null && StringUtils.isNotEmpty(map.get(MENU_KEY).toString())) {
				//找到用户车间权限
				String userWorkshopStr = map.get(MENU_KEY).toString();
				for (String workshop : userWorkshopStr.split(",")) {
					if(workshop.equals("*")) {
						for (Map<String, Object> allWorkshop : deptList) {
							if(allWorkshop.get("CODE")!=null) {
								allWorkshop.put("code", allWorkshop.get("CODE").toString());
								allWorkshop.put("name", allWorkshop.get("NAME").toString());
								allCode += allWorkshop.get("CODE").toString()+",";
								rtn.add(allWorkshop);
							}
						}
					} else if(workshop.contains("*")) {
						if("*".equals(workshop)) {
							workshop = "";
						}else {
							workshop = workshop.toUpperCase().split("\\*")[0];
						}
						for (Map<String, Object> allWorkshop : deptList) {
							if(allWorkshop.get("CODE")!=null) {
								allWorkshop.put("code", allWorkshop.get("CODE").toString());
								allWorkshop.put("name", allWorkshop.get("NAME").toString());
								allCode += allWorkshop.get("CODE").toString()+",";
								rtn.add(allWorkshop);
							}
						}
					}else {
						for (Map<String, Object> allWorkshop : deptList) {
							if( ( allWorkshop.get("CODE")!=null&&allWorkshop.get("CODE").toString().equals(workshop) )
								|| (allWorkshop.get("NAME")!=null&&allWorkshop.get("NAME").toString().equals(workshop) )
									) {
								allWorkshop.put("code", allWorkshop.get("CODE").toString());
								allWorkshop.put("name", allWorkshop.get("NAME").toString());
								allCode += allWorkshop.get("CODE").toString()+",";
								rtn.add(allWorkshop);
								break;
							}
						}
					}
					
				}
				break;
			}
		}
		if(StringUtils.isNotEmpty(ALL_OPTION) && "X".equals(ALL_OPTION)) {
			//构建全部值
			Map<String,Object> map = new HashMap<String, Object>();
			if (StringUtils.isNotEmpty(allCode)) {
				map.put("code", allCode);
				map.put("CODE", allCode);
				map.put("NAME", "全部");
				map.put("name", "全部");
			}else {
				map.put("code", allCode);
				map.put("CODE", allCode);
				map.put("NAME", "无权限");
				map.put("name", "无权限");
			}
			
			List<Map<String,Object>> rtn_all = new ArrayList<>();
			rtn_all.add(map);
			rtn_all.addAll(rtn);
			
			return R.ok().put("data", rtn_all);
		}
		if(rtn.size()<=0) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("code", allCode);
			map.put("CODE", allCode);
			map.put("NAME", "无权限");
			map.put("name", "无权限");
			rtn.add(map);
		}
    	return R.ok().put("data", rtn);
    }
    
	/**
	 * 根据工厂代码、车间获取用户权限线别
	 * @param params WERKS 工厂代码  WORKSHOP 车间
	 * @return
	 */
    @RequestMapping("/getUserLine")
    public R getUserLine(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> rtn = new ArrayList<>();
    	
    	String allCode = "";
    	
       	String WERKS = params.get("WERKS")==null?"":params.get("WERKS").toString();
    	if(StringUtils.isEmpty(WERKS)) {
    		return R.error("工厂代码不能为空！");
    	}
      	String WORKSHOP = params.get("WORKSHOP")==null?"":params.get("WORKSHOP").toString();
    	if(StringUtils.isEmpty(WORKSHOP)) {
    		return R.error("车间不能为空！");
    	}
    	String MENU_KEY = params.get("MENU_KEY")==null?"":params.get("MENU_KEY").toString();
    	if(StringUtils.isEmpty(MENU_KEY)) {
    		return R.error("菜单KEY不能为空！");
    	}
    	String ALL_OPTION = params.get("ALL_OPTION")==null?"":params.get("ALL_OPTION").toString();
    	//获取工厂、车间下所有线别
    	params.put("WERKS", WERKS);
    	params.put("WORKSHOP", WORKSHOP);
    	List<Map<String,Object>> deptList = sysDeptService.getWorkshopLineList(params);
    	
    	//获取用户授权的所有线别信息
		String username = userUtils.getTokenUsername();
		List<Map<String,Object>> userMesLineList = redisUtils.getList(RedisKeys.getUserMesLineKey(username));
		
		//过滤用户工厂、车间下有权限的线别清单
		for (Map<String, Object> map : userMesLineList) {
			if(map.get(MENU_KEY)!=null && StringUtils.isNotEmpty(map.get(MENU_KEY).toString())) {
				//找到用户线别权限
				String userLineStr = map.get(MENU_KEY).toString();
				for (String line : userLineStr.split(",")) {
					if(line.equals("*")) {
						for (Map<String, Object> allLine : deptList) {
							if(allLine.get("CODE")!=null) {
								allLine.put("code", allLine.get("CODE").toString());
								allLine.put("name", allLine.get("NAME").toString());
								allCode += allLine.get("CODE").toString()+",";
								rtn.add(allLine);
							}
						}
					} else if(line.contains("*")) {
						if("*".equals(line)) {
							line = "";
						}else {
							line = line.toUpperCase().split("\\*")[0];
						}
						for (Map<String, Object> allLine : deptList) {
							if(allLine.get("CODE")!=null) {
								allLine.put("code", allLine.get("CODE").toString());
								allLine.put("name", allLine.get("NAME").toString());
								allCode += allLine.get("CODE").toString()+",";
								rtn.add(allLine);
							}
						}
					}else {
						for (Map<String, Object> allLine : deptList) {
							if( ( allLine.get("CODE")!=null&&allLine.get("CODE").toString().equals(line) )
								|| (allLine.get("NAME")!=null&&allLine.get("NAME").toString().equals(line) )
									) {
								allLine.put("code", allLine.get("CODE").toString());
								allLine.put("name", allLine.get("NAME").toString());
								allCode += allLine.get("CODE").toString()+",";
								rtn.add(allLine);
								break;
							}
						}
					}
					
				}
				break;
			}
		}
    	
		if(StringUtils.isNotEmpty(ALL_OPTION) && "X".equals(ALL_OPTION)) {
			//构建全部值
			Map<String,Object> map = new HashMap<String, Object>();
			if (StringUtils.isNotEmpty(allCode)) {
				map.put("code", allCode);
				map.put("CODE", allCode);
				map.put("NAME", "全部");
				map.put("name", "全部");
			}else {
				map.put("code", allCode);
				map.put("CODE", allCode);
				map.put("NAME", "无权限");
				map.put("name", "无权限");
			}
			List<Map<String,Object>> rtn_all = new ArrayList<>();
			rtn_all.add(map);
			rtn_all.addAll(rtn);
			
			return R.ok().put("data", rtn_all);
		}
		if(rtn.size()<=0) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("code", allCode);
			map.put("CODE", allCode);
			map.put("NAME", "无权限");
			map.put("name", "无权限");
			rtn.add(map);
		}
    	return R.ok().put("data", rtn);
    }
    
    
	/**
	 * 根据工厂代码、车间获取车间下所有线别信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getWorkshopLineList")
    public R getWorkshopLineList(@RequestParam Map<String, Object> params){
    	String WERKS = params.get("WERKS")==null?(params.get("werks")==null?"":params.get("werks").toString()):params.get("WERKS").toString();
    	if(StringUtils.isEmpty(WERKS)) {
    		return R.error("工厂不能为空！");
    	}
       	String WORKSHOP = params.get("WORKSHOP")==null?(params.get("workshop")==null?"":params.get("workshop").toString()):params.get("WORKSHOP").toString();
    	if(StringUtils.isEmpty(WORKSHOP)) {
    		return R.error("车间不能为空！");
    	}
    	params.put("WERKS", WERKS);
    	params.put("WORKSHOP", WORKSHOP);
    	List<Map<String,Object>> deptList = sysDeptService.getWorkshopLineList(params);
    	
    	return R.ok().put("data", deptList);
    }
    
	/**
	 * 根据工厂代码、车间获取车间下所有班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getWorkshopWorkgroupList")
    public R getWorkshopWorkgroupList(@RequestParam Map<String, Object> params){
    	String WERKS = params.get("WERKS")==null?(params.get("werks")==null?"":params.get("werks").toString()):params.get("WERKS").toString();
    	if(StringUtils.isEmpty(WERKS)) {
    		return R.error("工厂不能为空！");
    	}
       	String WORKSHOP = params.get("WORKSHOP")==null?(params.get("workshop")==null?"":params.get("workshop").toString()):params.get("WORKSHOP").toString();
    	if(StringUtils.isEmpty(WORKSHOP)) {
    		return R.error("车间不能为空！");
    	}
    	params.put("WERKS", WERKS);
    	params.put("WORKSHOP", WORKSHOP);
    	List<Map<String,Object>> deptList = sysDeptService.getWorkshopWorkgroupList(params);
    	
    	return R.ok().put("data", deptList);
    }
    
	/**
	 * 根据工厂代码、车间、班组获取所有小班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getTeamList")
    public R getTeamList(@RequestParam Map<String, Object> params){
    	String WERKS = params.get("WERKS")==null?(params.get("werks")==null?"":params.get("werks").toString()):params.get("WERKS").toString();
    	if(StringUtils.isEmpty(WERKS)) {
    		return R.error("工厂不能为空！");
    	}
       	String WORKSHOP = params.get("WORKSHOP")==null?(params.get("workshop")==null?"":params.get("workshop").toString()):params.get("WORKSHOP").toString();
    	if(StringUtils.isEmpty(WORKSHOP)) {
    		return R.error("车间不能为空！");
    	}
     	String WORKGROUP = params.get("WORKGROUP")==null?(params.get("workgroup")==null?"":params.get("workgroup").toString()):params.get("WORKGROUP").toString();
    	if(StringUtils.isEmpty(WORKGROUP)) {
    		return R.error("班组不能为空！");
    	}
    	params.put("WERKS", WERKS);
    	params.put("WORKSHOP", WORKSHOP);
    	params.put("WORKGROUP", WORKGROUP);
    	List<Map<String,Object>> deptList = sysDeptService.getTeamList(params);
    	
    	return R.ok().put("data", deptList);
    }
    
	/**
	 * 获取所有车型清单
	 * @param params VEHICLE_TYPE 车辆类型
	 * @return
	 */
    @RequestMapping("/getBusTypeList")
    public R getBusTypeList(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> busTypeList = busTypeService.getBusTypeList(params);
    	
    	return R.ok().put("data", busTypeList);
    }

	/**
	 * 获取工厂、车间下所有标准工序信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getWorkshopProcessList")
    public R getWorkshopProcessList(@RequestParam Map<String, Object> params){
    	String WERKS = params.get("WERKS")==null?(params.get("werks")==null?"":params.get("werks").toString()):params.get("WERKS").toString();
    	if(StringUtils.isEmpty(WERKS)) {
    		return R.error("工厂不能为空！");
    	}
       	String WORKSHOP = params.get("WORKSHOP")==null?(params.get("workshop")==null?"":params.get("workshop").toString()):params.get("WORKSHOP").toString();
    	if(StringUtils.isEmpty(WORKSHOP)) {
    		return R.error("车间不能为空！");
    	}
    	List<Map<String,Object>> processList = processService.getWorkshopProcessList(params);
    	
    	return R.ok().put("data", processList);
    }
    
	/**
	 * 获取品质检验标准清单
	 * @param STANDARD_TYPE：检验标准类型 如外观标准，STANDARD_NAME：标准描述  pageSize 限制匹配记录条数
	 * @return
	 */
    @RequestMapping("/getQmsTestStandard")
    public R getQmsTestStandard(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> qmsTestStandardList = testStandardService.getQmsTestStandard(params);
    	
    	return R.ok().put("data", qmsTestStandardList);
    }
    
	/**
	 * 获取品质抽样规则
	 * @param WERKS：工厂代码 WORKSHOP：车间代码/名称  ORDER_AREA_CODE：订单区域代码
	 * @return
	 */
    @RequestMapping("/getQmsTestRules")
    public R getQmsTestRules(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> qmsTestRulesList = testRulesService.getQmsTestRules(params);
    	
    	return R.ok().put("data", qmsTestRulesList);
    }
    
	/**
	 * 获取品质检验工具
	 * @param params WERKS：工厂代码  TEST_TOOL_NO： 检具编码  TEST_TOOL_NAME：检具名称 pageSize:模糊匹配最大显示条数
	 * @return
	 */
    @RequestMapping("/getQmsTestToolList")
    public R getQmsTestToolList(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> qmsTestToolList = testToolService.getQmsTestToolList(params);
    	
    	return R.ok().put("data", qmsTestToolList);
    }
    
    /**
     * 图纸库检索
     */
    @RequestMapping("/pmdMapQuery")
    @ResponseBody
    public R pmdMapQuery(@RequestParam Map<String, Object> params){
    	PageUtils page = matMapService.queryPmdMapPage(params);
        return R.ok().put("page", page);
    }
    @RequestMapping("/getPmdMapList")
    @ResponseBody
    public R getPmdMapList(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list = matMapService.getPmdMapList(params);
        return R.ok().put("data", list);
    }
    
    
	/**
	 * 根据图号获取图纸临时文件地址
	 * @param params material_no/MATERIAL_NO：图号
	 * @return
	 */
    @RequestMapping("/getMaterialNoMapFile")
    public R getMaterialNoMapFile(@RequestParam Map<String, Object> params){
     	String material_no = params.get("material_no")==null?(params.get("MATERIAL_NO")==null?"":params.get("MATERIAL_NO").toString()):params.get("material_no").toString();
    	if(StringUtils.isEmpty(material_no)) {
    		return R.error("获取失败！请检查图纸编号是否正确！");
    	}
    	if(material_no.indexOf("-") <=0) {
    		return R.error("获取失败！请检查图纸编号是否正确！");
    	}
    	String map_url = null;
		map_url = matMapService.getMatMapUrl(params);
    	
    	return R.ok().put("data", map_url);
    }
    /**
	 * 根据工厂代码、车间获取所有小班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping("/getTeamListByWorkshop")
    public R getTeamListByWorkshop(@RequestParam Map<String, Object> params){
    	String WERKS = params.get("WERKS")==null?(params.get("werks")==null?"":params.get("werks").toString()):params.get("WERKS").toString();
    	if(StringUtils.isEmpty(WERKS)) {
    		return R.error("工厂不能为空！");
    	}
       	String WORKSHOP = params.get("WORKSHOP")==null?(params.get("workshop")==null?"":params.get("workshop").toString()):params.get("WORKSHOP").toString();
    	if(StringUtils.isEmpty(WORKSHOP)) {
    		return R.error("车间不能为空！");
    	}
     	
    	params.put("WERKS", WERKS);
    	params.put("WORKSHOP", WORKSHOP);
    	List<Map<String,Object>> deptList = sysDeptService.getTeamListByWorkshop(params);
    	
    	return R.ok().put("data", deptList);
	}
	// 模糊查找工序
    @RequestMapping("/getProcessList")
    public R getProcessList(@RequestParam Map<String,String> map){
    	List<Map<String,Object>> list = processService.getProcessList(map);
        return R.ok().put("data", list);
    }
}
