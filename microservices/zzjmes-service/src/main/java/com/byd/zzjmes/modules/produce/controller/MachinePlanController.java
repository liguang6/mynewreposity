package com.byd.zzjmes.modules.produce.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.zzjmes.modules.config.service.MachineAssignService;
import com.byd.zzjmes.modules.produce.service.MachinePlanService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 机台计划
 * @author cscc tangj
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("produce/machinePlan")
public class MachinePlanController {
    @Autowired
    private MachinePlanService machinePlanService;
    @Autowired
    private MachineAssignService machineAssignService;
    
    @RequestMapping("/getMachinePlanList")
    public R getMachinePlanList(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list=machinePlanService.getMachinePlanList(params);
		return new R().put("data", list);
	}
    /**查询分页**/
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
    	PageUtils page = machinePlanService.queryPage(params);
        return R.ok().put("page", page);
    }
    /*****************
               校验无误的记录直接保存，有错误信息的数据显示到前端页面，可导出供用户修改调整再次导入 
     ********************/

    @RequestMapping("/upload")
 	public R upload(@RequestBody Map<String,Object> params){
 		String zzj_plan_batch=params.get("zzj_plan_batch").toString();
 		List<Map<String,Object>> entityList = (List<Map<String,Object>>)params.get("entityList");
 		// 通过验证 可直接保存记录
 		List<Map<String,Object>> saveList=new ArrayList<Map<String,Object>>();
 	    // 验证有错误 显示到页面的记录
 	 	List<Map<String,Object>> errorList=new ArrayList<Map<String,Object>>();
 	 	// 重复行校验（工厂+车间+线别+订单+零件号+装配位置+使用工序+工艺流程+批次+计划工序+机台+计划日期 不能有相同的数据）
 	 	List<String> uniqueList=new ArrayList<String>();
 	 	// 校验机台数据正确性
 		List<String> machineList=machinePlanService.checkMachine(params);
 		for(Map<String,Object> map:entityList){
 			String msg="";
 			/*************** 空值校验 格式校验 唯一性校验 值正确性校验 start ******************/
 			if(isNull(map.get("zzj_no"))) {
 				msg="零部件号不能为空; ";
 			}
 			if(isNull(map.get("assembly_position"))) {
 				msg+="装配位置不能为空; ";
 			}
 			if(isNull(map.get("process"))) {
 				msg+="使用工序不能为空; ";
 			}
 			if(isNull(map.get("process_flow"))) {
 				msg+="工艺流程不能为空; ";
 			}else {
 				if(map.get("process_flow").toString().
 						indexOf(map.get("plan_process").toString())<0) {
 					msg+="工艺流程不包含【"+map.get("plan_process")+"】计划工序; ";
 				}
 			}
 			if(isNull(map.get("plan_process"))) {
 				msg+="计划工序不能为空; ";
 			}
 			if(isNull(map.get("plan_quantity"))) {
 				msg+="计划数量不能为空; ";
 			}else {
 				if(!isNumeric(map.get("plan_quantity").toString().trim())) {
 					msg+="计划数量必须为数字类型; ";
 				}
 			}
 			if(isNull(map.get("machine"))) {
 				msg+="机台不能为空;";
 			}else {
 				if(!isNull(map.get("plan_process"))) {
	 				String key=map.get("machine").toString().trim()+"#"+map.get("plan_process").toString().trim();
	 				if(!machineList.contains(key)) {
	 					msg+="机台未维护或机台未绑定该工序;";
	 				}
 				}
 			}
 			if(isNull(map.get("plan_date"))) {
 				msg+="生产日期 ";
 			}else {
 				if(!isLegalDate(map.get("plan_date").toString().trim())) {
 					msg+="生产日期格式必须为:YYYY-MM-DD; ";
 				}
 			}
 			/*************** 空值校验 格式校验 唯一性校验 值正确性校验  end ********************/
 		    // 唯一性校验：零件号+装配位置+使用工序+工艺流程+批次+计划工序+机台+计划日期
 			if(msg.equals("")) {
 				String uniqueStr=map.get("zzj_no").toString().trim()+"-"+map.get("assembly_position").toString().trim()+"-"
 						+map.get("process").toString().trim()+"-"+map.get("process_flow").toString().trim()+"-"
                        +zzj_plan_batch +"-"+map.get("plan_process").toString().trim()+"-"+map.get("machine").toString().trim()
                        +"-"+map.get("plan_date").toString().trim();
 				if(uniqueList.contains(uniqueStr)) {
 					msg=uniqueStr+":已重复";
 					map.put("msg", msg);
 					errorList.add(map);
 				}else {
 					saveList.add(map);
 					uniqueList.add(uniqueStr);
 				}		
 			}else {
 				map.put("msg", msg);
 				errorList.add(map);
 			}
 		}
 	    // 空值、格式、值正确性 重复性等基础校验后  再次校验数据在机台计划/下料明细是否已存在/是否超需求
 		if(saveList.size()>0) {
            Map<String,Object> checkMap=new HashMap<String,Object>();
            checkMap.putAll(params);
 			Map<String, Object> zzjPlanList = machinePlanService.getPlanByMap(params);
 			//本批次在整个订单中所属数量范围，的起始数
 			checkMap.put("start_date",zzjPlanList.get("start_date"));
			Integer batchQuantityStart = machinePlanService.getZzjPlanBatchQuantity(checkMap) + 1;
			//批次车付数
			Integer batchQuantity = Integer.parseInt(String.valueOf(zzjPlanList.get("quantity")));
		
			checkMap.put("pmd_list", saveList);
 			List<Map<String,Object>> checkPmdList=machinePlanService.getExistPmdList(checkMap);
 			//取机台下料计划 ，判断是否存在
			List<Map<String,Object>> machinePlanExistList = machinePlanService.checkMachinePlanList(checkMap);
 			//取机台下料计划数量列表 判断数量是否超出【check_flag=01】
			checkMap.put("check_flag", "01");
			List<Map<String,Object>> machinePlanQtyList = machinePlanService.checkMachinePlanList(checkMap);
			
 			Iterator<Map<String,Object>> iterator = saveList.iterator();
 	        while(iterator.hasNext()){
 	        	String msg="";
 	        	Map<String,Object> map=iterator.next();
 	        	
 				String machine_plan_str=map.get("zzj_no").toString().trim()+"#"+map.get("assembly_position").toString().trim()+"#"
 						+map.get("process").toString().trim()+"#"+map.get("process_flow").toString().trim();
				boolean is_exsist=false;
				
 				for(Map<String,Object> pmd : checkPmdList) {
 					// 判断下料明细是否存在(zzj_no+assembly_position+process+process_flow)
					String pmd_str=pmd.get("zzj_no").toString().trim()+"#"+pmd.get("assembly_position").toString().trim()+"#"
	 						+pmd.get("process").toString().trim()+"#"+pmd.get("process_flow").toString().trim();
					if(pmd_str.equals(machine_plan_str)) {
						is_exsist=true;
						String pmd_item_id=pmd.get("id").toString();
						map.put("zzj_pmd_items_id", pmd_item_id);
						map.put("zzj_name", pmd.get("zzj_name"));
						// 下料明细存在 再校验是否已存在相同的机台下料计划
						for(Map<String,Object> m : machinePlanExistList) {
							if(pmd_item_id.equals(m.get("zzj_pmd_items_id").toString()) && 
								 map.get("plan_process").toString().equals(m.get("plan_process").toString()) &&
								 map.get("plan_date").toString().equals(m.get("plan_date").toString()) &&
								 map.get("machine").toString().equals(m.get("machine").toString())) {
								msg="存在已导入的机台下料计划;";
								map.put("msg", msg);
								errorList.add(map);
								iterator.remove();
								break;
							}
						}
						// 跳出checkPmdList循环
		 	        	if(!msg.equals("")) {
		 	        		break;
		 	        	}
						// 校验是否超需求
		 	        	Double panBatchSum = 0.0d;  //批次总需求
						// 存在技改数据
						if(!isNull(pmd.get("change_from"))) {
							String ecnQuantity = String.valueOf(pmd.get("change_from"));
							String[] ecnQuantitySection = ecnQuantity.split(";");
							for (String ecnSection : ecnQuantitySection) {
								String[] ecn = ecnSection.split(":");// 1-5:4;6-50:5; 解释: 1~5 需要4 6~50需要5
								Double start = Double.parseDouble(ecn[0].split("-")[0]);
								Double end = Double.parseDouble(ecn[0].split("-")[1]);
								Double num = Double.parseDouble(ecn[1]);
								if(batchQuantityStart > end || batchQuantityStart + batchQuantity - 1 < start) continue;
								start = batchQuantityStart <= start ? start : batchQuantityStart;
								end = batchQuantityStart + batchQuantity - 1 >= end ? end : batchQuantityStart + batchQuantity - 1;
								panBatchSum += (end - start + 1) * num;
							}
						}else {
							Double carQuantity  = Double.valueOf(pmd.get("quantity")==null?"0":pmd.get("quantity").toString());
							Double batchCar = Double.parseDouble(zzjPlanList.get("quantity").toString());// 批次车数
							panBatchSum +=  carQuantity * batchCar;
						}
						//planQtyTotal:从excel汇总计划数量(相同zzj_no、process、assembly_position、process_flow、plan_process)
						List<Map<String,Object>> match_list = new ArrayList<Map<String,Object>>();
						match_list=saveList.stream().filter(m->(map.get("zzj_no").toString().trim().equals(m.get("zzj_no"))  &&
								map.get("process").toString().trim().equals(m.get("process")) 
								&& map.get("assembly_position").toString().trim().equals(m.get("assembly_position"))
								&& map.get("process_flow").toString().trim().equals(m.get("process_flow"))
								&& map.get("plan_process").toString().trim().equals(m.get("plan_process"))
								&& isNull(m.get("msg"))
								)).collect(Collectors.toList());
		
						Double planQtyTotal = match_list.stream().mapToDouble(mx -> Double.valueOf(mx.get("plan_quantity").toString())).sum();
						// existedMachineSum：相同的下料行项目和计划工序 已存在的计划数量
						Double existedMachineSum = 0d;
						List<Map<String,Object>> _sub_plan_list = new ArrayList<Map<String,Object>>();
						machinePlanQtyList.forEach(m->{
							m = (Map) m;
							if(pmd_item_id.equals(m.get("zzj_pmd_items_id").toString())
									&& map.get("plan_process").toString().equals(m.get("plan_process").toString())) {
								_sub_plan_list.add(m);
							}
						});

						if (!CollectionUtils.isEmpty(_sub_plan_list)) {
							existedMachineSum = _sub_plan_list.stream()
								.mapToDouble(mx -> Double.valueOf(mx.get("plan_quantity") == null ? "0" : mx.get("plan_quantity").toString()))
									.sum();
						}
						if (panBatchSum < planQtyTotal + existedMachineSum) {
							msg += String.format("计划数量之和%s超过了还可以导入的数量%s，请修改；", planQtyTotal,
								panBatchSum - existedMachineSum);
							map.put("msg", msg);
							errorList.add(map);
							iterator.remove();
						}
					}	
 		    	}
 				if(!is_exsist) {
					msg=machine_plan_str+"在下料明细中不存在!";
					map.put("msg", msg);
					errorList.add(map);
					iterator.remove();
				}
 	        }
 		}
 		if(saveList.size()==0) {
 			return R.ok().put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
 		}
 		try {
 			Map<String,Object> saveMap=new HashMap<String,Object>();
 			saveMap.putAll(params);
 			saveMap.remove("entityList");
 			saveMap.put("add_list", saveList);
			machinePlanService.save(saveMap);
	    	return R.ok().put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage()).put("data", errorList);
    	}
 	}
    @RequestMapping("/save")
 	public R save(@RequestBody Map<String,Object> params){
    	try {	
    		Gson gson=new Gson();
			List<Map<String,Object>> addList =gson.fromJson((String) params.get("add_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
			List<Map<String,Object>> updateList =gson.fromJson((String) params.get("modify_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
			// unionList校验是否超需求
     		List<Map<String,Object>> unionList=new ArrayList<Map<String,Object>>();
     		if(addList!=null && addList.size()>0) {
     			unionList.addAll(addList);
     		}
     		if(updateList!=null && updateList.size()>0) {
     			unionList.addAll(updateList);
     		}
     	 	// 校验机台数据正确性
     		List<String> machineList=machinePlanService.checkMachine(params);
     		Map<String,Object> checkMap=new HashMap<String,Object>();
            checkMap.putAll(params);
 			Map<String, Object> zzjPlanList = machinePlanService.getPlanByMap(params);
 			checkMap.put("start_date",zzjPlanList.get("start_date"));
 			//本批次在整个订单中所属数量范围，的起始数
			Integer batchQuantityStart = machinePlanService.getZzjPlanBatchQuantity(checkMap) + 1;
			//批次车付数	
			Integer batchQuantity = Integer.parseInt(String.valueOf(zzjPlanList.get("quantity")));
		
			checkMap.put("pmd_list", unionList);
 			List<Map<String,Object>> checkPmdList=machinePlanService.getExistPmdList(checkMap);
 			//取机台下料计划 ，判断是否存在
			List<Map<String,Object>> machinePlanExistList = machinePlanService.checkMachinePlanList(checkMap);
 			//取机台下料计划数量列表 判断数量是否超出【check_flag=01】
			checkMap.put("check_flag", "01");
			List<Map<String,Object>> machinePlanQtyList = machinePlanService.checkMachinePlanList(checkMap);
			// 新增：校验在下料明细中是否存在
 			Iterator<Map<String,Object>> add_iterator = addList.iterator();
 	        while(add_iterator.hasNext()){
 	        	Map<String,Object> map=add_iterator.next();
 				String machine_plan_str=map.get("zzj_no")+"#"+map.get("assembly_position")+"#"
 						+map.get("process")+"#"+map.get("process_flow");
				boolean is_exsist=false;
 				for(Map<String,Object> pmd : checkPmdList) {
					// 下料明细存在(is_exsist=true) 再校验是否已存在相同的机台下料计划
					String pmd_str=pmd.get("zzj_no")+"#"+pmd.get("assembly_position")+"#"
	 						+pmd.get("process")+"#"+pmd.get("process_flow");
					if(pmd_str.equals(machine_plan_str)) {
						String pmd_item_id=pmd.get("id").toString();
						map.put("zzj_pmd_items_id", pmd_item_id);
						map.put("zzj_name", pmd.get("zzj_name"));
						is_exsist=true;
						for(Map<String,Object> m : machinePlanExistList) {
							if(pmd.get("id").toString().equals(m.get("zzj_pmd_items_id").toString()) && 
						         map.get("plan_process").toString().equals(m.get("plan_process").toString()) &&
								 map.get("plan_date").toString().equals(m.get("plan_date").toString()) && 
								 map.get("machine").toString().equals(m.get("machine").toString())) {
								throw new RuntimeException("第"+Integer.parseInt(map.get("row_no").toString())+"行：存在已导入的机台下料计划!");
							}
						}
					}
				}
				if(!is_exsist) {
					throw new RuntimeException("第"+Integer.parseInt(map.get("row_no").toString())+"行:下料明细不存在!");
				}
 			}
 	        // 更新：校验在机台计划中是否已存在
 	        Iterator<Map<String,Object>> update_iterator = updateList.iterator();
 	        while(update_iterator.hasNext()){
 	        	Map<String,Object> map=update_iterator.next();
	        	for(Map<String,Object> m : machinePlanExistList) {
					if(map.get("zzj_pmd_items_id").toString().equals(m.get("zzj_pmd_items_id").toString()) && 
                        map.get("plan_process").toString().equals(m.get("plan_process").toString()) &&
						map.get("plan_date").toString().equals(m.get("plan_date").toString()) && 
						map.get("machine").toString().equals(m.get("machine").toString())) {
						throw new RuntimeException("第"+Integer.parseInt(map.get("row_no").toString())+"行：存在已导入的机台下料计划!");
					}
				} 
 	        }
 	        // 校验是否超需求
 	        Iterator<Map<String,Object>> iterator = unionList.iterator();
	        while(iterator.hasNext()){	
	        	Map<String,Object> map=iterator.next();
	        	String key=map.get("machine").toString()+"#"+map.get("plan_process").toString();
	        	if(!machineList.contains(key)) {
					throw new RuntimeException("第"+Integer.parseInt(map.get("row_no").toString())+"行："+map.get("machine")+"机台未维护或该机台未绑定该工序!");
	        	}
				// 在下料明细存在，校验是否超需求
				Double panBatchSum = 0d;  //批次总需求
				for(Map<String,Object> mat:checkPmdList) {
					if(mat.get("zzj_no").toString().equals(map.get("zzj_no").toString())&& 
							mat.get("assembly_position").toString().equals(map.get("assembly_position").toString()) && 
							mat.get("process").toString().equals(map.get("process").toString()) && 
							mat.get("process_flow").toString().equals(map.get("process_flow").toString())) {
						map.put("zzj_pmd_items_id", mat.get("id"));
						// 存在技改数据
						if(mat.get("change_from") != null && !mat.get("change_from").toString().equals("")) {
							String ecnQuantity = String.valueOf(mat.get("change_from"));
							String[] ecnQuantitySection = ecnQuantity.split(";");
							for (String ecnSection : ecnQuantitySection) {
								String[] ecn = ecnSection.split(":");// 1-5:4;6-50:5; 解释: 1~5 需要4 6~50需要5
								Double start = Double.parseDouble(ecn[0].split("-")[0]);
								Double end = Double.parseDouble(ecn[0].split("-")[1]);
								Double num = Double.parseDouble(ecn[1]);
								if(batchQuantityStart > end || batchQuantityStart + batchQuantity - 1 < start) continue;
								start = batchQuantityStart <= start ? start : batchQuantityStart;
								end = batchQuantityStart + batchQuantity - 1 >= end ? end : batchQuantityStart + batchQuantity - 1;
								panBatchSum += (end - start + 1) * num;
							}
						}else {							
							Double carQuantity  = Double.valueOf(mat.get("quantity")==null?"0":mat.get("quantity").toString());
							Double batchCar = Double.parseDouble(zzjPlanList.get("quantity").toString());// 批次车数
							panBatchSum +=  carQuantity * batchCar;
						}
					}
				}	
				//planQtyTotal:从excel汇总后计划数量(相同zzj_no、process、assembly_position、process_flow、plan_process)
				List<Map<String,Object>> match_list = new ArrayList<Map<String,Object>>();
				match_list=unionList.stream().filter(m->(map.get("zzj_no").toString().trim().equals(m.get("zzj_no"))  &&
						map.get("process").toString().trim().equals(m.get("process")) 
						&& map.get("assembly_position").toString().trim().equals(m.get("assembly_position"))
						&& map.get("process_flow").toString().trim().equals(m.get("process_flow"))
						&& map.get("plan_process").toString().trim().equals(m.get("plan_process"))
						)).collect(Collectors.toList());

				Double planQtyTotal = match_list.stream().mapToDouble(mx -> Double.valueOf(mx.get("plan_quantity").toString())).sum();
				Integer initplanQtyTotal = match_list.stream().mapToInt(mx -> Integer.valueOf(mx.get("init_plan_quantity")!=null ? mx.get("init_plan_quantity").toString() : "0")).sum();
				Double existedMachineSum = 0d;
				List<Map<String,Object>> _sub_plan_list = new ArrayList<Map<String,Object>>();
				for(Map<String,Object> m : machinePlanQtyList) {
					//汇总 同下料明细、同计划工序的计划数量
					if(m.get("zzj_pmd_items_id").toString().equals(map.get("zzj_pmd_items_id").toString())&& 
						    m.get("plan_process").toString().equals(map.get("plan_process").toString())) {
						_sub_plan_list.add(m);
					}
				}
				if (!CollectionUtils.isEmpty(_sub_plan_list)) {
					existedMachineSum = _sub_plan_list.stream()
						.mapToDouble(mx -> Double.valueOf(mx.get("plan_quantity").toString()))
							.sum();
				}
				if (panBatchSum-existedMachineSum < planQtyTotal) {
					throw new RuntimeException(String.format("第"+Integer.parseInt(map.get("row_no").toString())+"行：相同零部件号、工序的计划数量之和%s超过了还可以录入的数量%s，请修改!", planQtyTotal,
							panBatchSum - existedMachineSum));
				}	
	        }
	        
	        params.put("add_list", addList);
	        params.put("modify_list", updateList);
	    	machinePlanService.save(params);
	    	return R.ok();
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    @RequestMapping("/del")
 	public R del(@RequestBody Map<String,Object> params){
    	try {
    		Gson gson=new Gson();
			List<Map<String,Object>> delList =gson.fromJson((String) params.get("delete_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
			params.put("delete_list", delList);
			machinePlanService.del(params);
			return R.ok();
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    
    @RequestMapping("/getOutputRecords")
    public R getOutputRecords(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list=machinePlanService.getOutputRecords(params);
		return new R().put("data", list);
	}
    
    @RequestMapping("/getTemplateData")
    public R getTemplateData(@RequestParam Map<String, Object> params){
    	List<Map<String,Object>> list=machinePlanService.getTemplateData(params);
		return new R().put("data", list);
	}
    
    @RequestMapping("/getMachineInfo")
    public R getMachineInfo(@RequestParam Map<String, Object> params){
    	List<String> list=machinePlanService.checkMachine(params);
		return new R().put("data", list);
	}
    
    // 空值:true  非空：false
    public boolean isNull(Object str) {
    	boolean result=true;
    	if(str==null) {
    		return result;
    	}
    	if(str.toString().trim().equals("")) {
    		return result;
    	}
    	return false;
    }
    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    private static boolean isLegalDate(String sDate) {
        int legalLen = 10;
        if ((sDate == null) || (sDate.length() != legalLen)) {
            return false;
        }
     
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }
}