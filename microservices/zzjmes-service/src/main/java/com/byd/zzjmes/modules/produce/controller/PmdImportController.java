package com.byd.zzjmes.modules.produce.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.produce.service.MasterDataRemote;
import com.byd.zzjmes.modules.produce.service.PmdImportService;

/**
 * 下料明细导入
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("pmdImport")
public class PmdImportController {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MasterDataRemote masterDataRemote; 
    @Autowired
    private PmdImportService pmdImportService;
    @Autowired
    private UserUtils userUtils;
    
    /*****************
               校验无误的记录直接保存，有错误信息的数据显示到前端页面，可导出供用户修改调整再次导入 
     ********************/
    @RequestMapping("/upload")
 	public R upload(@RequestBody Map<String,Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	
 		String werks = params.get("werks").toString();
 		String werks_name = params.get("werks_name").toString();
 		String workshop = params.get("workshop").toString();
 		String workshop_name = params.get("workshop_name").toString();
 		String line = params.get("line").toString();
 		String line_name = params.get("line_name").toString();
 		String order_no = params.get("order_no").toString();
 		
 	 	Map<String,Object> savePmdParams = new HashMap<>();
 	 	savePmdParams.put("werks", werks);
 	 	savePmdParams.put("WERKS", werks);
 	 	savePmdParams.put("werks_name", werks_name);
 	 	savePmdParams.put("WERKS_NAME", werks_name);
 	 	savePmdParams.put("workshop", workshop);
 	 	savePmdParams.put("WORKSHOP", workshop);
 	 	savePmdParams.put("workshop_name", workshop_name);
 	 	savePmdParams.put("WORKSHOP_NAME", workshop_name);
 	 	savePmdParams.put("line", line);
 	 	savePmdParams.put("LINE", line);
 	 	savePmdParams.put("line_name", line_name);
 	 	savePmdParams.put("LINE_NAME", line_name);
 	 	savePmdParams.put("order_no", order_no);
 	 	savePmdParams.put("creator", user.get("USERNAME")+":"+user.get("FULL_NAME"));
 	 	savePmdParams.put("creat_date", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
 		
 		List<List> entityList = (List<List>)params.get("entityList");
 		
 		StringBuffer allUseWorkshopBf = new StringBuffer();
 		allUseWorkshopBf.append(workshop_name);
 		//循环EXCEL数据获取使用车间
 		for (List excelLine : entityList) {
 	 		String workshopStr = excelLine.get(10)==null?"":excelLine.get(10).toString().trim();
 	 		if(!(allUseWorkshopBf.indexOf(workshopStr) >=0)){
 	 			allUseWorkshopBf.append(",");
 	 			allUseWorkshopBf.append( workshopStr );
 	 		}
 	 		
 		}
 		
 		// 通过验证 可直接保存记录
 		Map<String,Object> saveMap = new HashMap<String,Object>();
 	    // 验证有错误 显示到页面的记录
 	 	List<Map<String,Object>> errorList = new ArrayList<Map<String,Object>>();
 	 	// 重复行校验（零部件号+装配位置+使用工序+工艺流程 唯一）
 	 	List<String> uniqueList = new ArrayList<String>();
 	 	
 	 	Map<String,Object> masterdataParams = new HashMap<>();
 	 	masterdataParams.put("werks", werks);
 	 	masterdataParams.put("WERKS", werks);
 	 	masterdataParams.put("werks_name", werks_name);
 	 	masterdataParams.put("WERKS_NAME", werks_name);
 	 	masterdataParams.put("workshop", allUseWorkshopBf.toString());
 	 	masterdataParams.put("WORKSHOP", allUseWorkshopBf.toString());
 	 	masterdataParams.put("workshop_name", allUseWorkshopBf.toString());
 	 	masterdataParams.put("WORKSHOP_NAME", allUseWorkshopBf.toString());
 	 	//工厂标准车间信息
 	 	R workshopR = masterDataRemote.getWerksWorkshopList(masterdataParams);
 	 	List<Map<String,Object>> workshopList = workshopR.get("data")==null?null:(List<Map<String,Object>>)workshopR.get("data");
 	 	StringBuffer workshopBf = new StringBuffer();
 	 	workshopList.forEach( map -> {
 	 		map = (Map) map;
 	 		workshopBf.append(";");
 	 		workshopBf.append(map.get("NAME"));
 	 		workshopBf.append(";");
 	 	});
 	 	
 	 	//工厂、车间下标准工序信息
 	 	R processR = masterDataRemote.getWorkshopProcessList(masterdataParams);
 	 	List<Map<String,Object>> processList = processR.get("data")==null?null:(List<Map<String,Object>>)processR.get("data");
 	 	StringBuffer processBf = new StringBuffer();
 	 	StringBuffer processCodeBf = new StringBuffer();
 	 	Map<String,String> processMap = new HashMap<>();
 	 	processList.forEach( map -> {
 	 		map = (Map) map;
 	 		processBf.append(";");
 	 		processBf.append(map.get("WORKSHOP_NAME")+"-");
 	 		processBf.append(map.get("PROCESS_NAME"));
 	 		processBf.append(";");
 	 		
 	 		processCodeBf.append(";");
 	 		processCodeBf.append(map.get("WORKSHOP_NAME")+"-");
 	 		processCodeBf.append(map.get("PROCESS_CODE"));
 	 		processCodeBf.append(";");
 	 		processMap.put(map.get("PROCESS_CODE").toString(), map.get("PROCESS_NAME").toString());
 	 	});
 	 	
 	 	//材料类型字典清单
 	 	Map<String,Object> cailiaoTypeParams = new HashMap<>();
 	 	cailiaoTypeParams.put("TYPE", "ZZJ_CAILIAO_TYPE");
 	 	List<Map<String,Object>> cailiaoTypeList = masterDataRemote.getDictlistByType(cailiaoTypeParams);
 	 	StringBuffer cailiaoTypeBf = new StringBuffer();
 	 	cailiaoTypeList.forEach( map -> {
 	 		map = (Map) map;
 	 		cailiaoTypeBf.append(";");
 	 		cailiaoTypeBf.append(map.get("VALUE"));
 	 		cailiaoTypeBf.append(";");
 	 	});
 	 	
 		//分包类型字典清单
 	 	Map<String,Object> zzjSubTypeParams = new HashMap<>();
 	 	zzjSubTypeParams.put("TYPE", "ZZJ_SUB_TYPE");
 	 	List<Map<String,Object>> zzjSubTypeList = masterDataRemote.getDictlistByType(zzjSubTypeParams);
 	 	StringBuffer zzjSubTypeBf = new StringBuffer();
 	 	zzjSubTypeList.forEach( map -> {
 	 		map = (Map) map;
 	 		zzjSubTypeBf.append(";");
 	 		zzjSubTypeBf.append(map.get("VALUE"));
 	 		zzjSubTypeBf.append(";");
 	 	});
 	 	
 		//工段字典清单
 	 	Map<String,Object> sectionParams = new HashMap<>();
 	 	sectionParams.put("TYPE", "SECTION");
 	 	List<Map<String,Object>> sectionList = masterDataRemote.getDictlistByType(sectionParams);
	 	StringBuffer sectionBf = new StringBuffer();
	 	sectionList.forEach( map -> {
 	 		map = (Map) map;
 	 		sectionBf.append(";");
 	 		sectionBf.append(map.get("VALUE"));
 	 		sectionBf.append(";");
 	 	});
 	 	
	 	// 获取已导入的下料明细清单
	 	String pmd_head_id = null;
	 	List<Map<String, Object>> pmdItems = null;
		Map<String, Object> pmdinfoMap = pmdImportService.queryPmdInfo(savePmdParams);
		if (null != pmdinfoMap.get("pmd_head_id")) {
			pmd_head_id = pmdinfoMap.get("pmd_head_id").toString();
			pmdItems = (List<Map<String, Object>>) pmdinfoMap.get("pmdItems");
		}
		List<String> savedPmdItems = new ArrayList<>();
		if (null != pmdItems) {
			for (Map<String, Object> map : pmdItems) {
				//零部件号+装配位置+使用工序+工艺流程
				StringBuffer pmdItemBf = new StringBuffer();
				pmdItemBf.append(map.get("zzj_no") + ";" + map.get("assembly_position")+ ";" + map.get("process")+ ";" + map.get("process_flow")+";");
				savedPmdItems.add(pmdItemBf.toString());
			}
		}
		if(pmd_head_id !=null) {
			params.put("pmd_head_id", pmd_head_id);
		}
		
		List<Map<String,Object>> saveList = new ArrayList<Map<String,Object>>();
		try {
	 	 	Pattern pattern = Pattern.compile("[0-9]*");
	 	 	for (List data : entityList) {
	 			Map<String, Object> infomap = new HashMap<String, Object>();
	 			StringBuffer errorMessage = new StringBuffer();
	 			//校验数据
				if(data.get(0) != null && !data.get(0).toString().equals("")){
					infomap.put("no",data.get(0).toString().trim()); 
				}else{
					infomap.put("no", "");
					errorMessage.append("必须填写序号;"); 
				}
				infomap.put("sap_mat", data.get(1) == null ? null : data.get(1).toString().trim());
				
				if (data.get(2) != null && !data.get(2).toString().equals("")) {
					infomap.put("mat_description", data.get(2).toString());
				} else {
					infomap.put("mat_description", "");
					errorMessage.append("必须填写物料描述;");
				}
				
				if (data.get(3) != null && !data.get(3).toString().equals("")) {
					infomap.put("mat_type", data.get(3).toString().trim());
				} else {
					infomap.put("mat_type", "");
					errorMessage.append("必须填写物料类型;");
				}
				if (data.get(4) != null && !data.get(4).toString().equals("")) {
					infomap.put("specification", data.get(4).toString().trim());
				} else {
					infomap.put("specification", "");
					errorMessage.append("必须填写材料/规格;");
				}
				infomap.put("unit", data.get(5) == null ? null : data.get(5).toString().trim());
				if (data.get(6) != null && !data.get(6).toString().equals("")) {
					infomap.put("loss", String.format("%.3f", Math.round(Double.valueOf(data.get(6).toString().trim()) * 1000) * 0.001d));
				} else {
					infomap.put("loss", "");
					errorMessage.append("必须填写单车损耗;");
				}
				if (data.get(7) != null && !data.get(7).toString().equals("")) {
					infomap.put("quantity", data.get(7).toString().trim());
					if (!pattern.matcher(data.get(7).toString().trim()).matches()) {
						errorMessage.append("单车用量必须为正整数;");
					}
				} else {
					infomap.put("quantity", "");
					errorMessage.append("必须填写单车用量;");
				}
				if (data.get(8) != null && !data.get(8).toString().equals("")) {
					infomap.put("weight", String.format("%.3f", Math.round(Double.valueOf(data.get(8).toString().trim()) * 1000) * 0.001d));
				}else {
					infomap.put("weight", "");
				}
				if (data.get(9) != null && !data.get(9).toString().equals("")) {
					infomap.put("total_weight", String.format("%.3f",Math.round(Double.valueOf(data.get(9).toString().trim()) * 1000) * 0.001d));
				}else {
					infomap.put("total_weight", "");
				}
				
				if (data.get(10) != null && !data.get(10).toString().equals("")) {
					// 校验填写的车间是否在工厂下存在
					infomap.put("use_workshop", data.get(10).toString().trim());
					if (workshopBf.toString().indexOf(";"+data.get(10).toString()+";") < 0) {
						errorMessage.append("使用车间：" + data.get(10).toString().trim() + "不存在;");
					}
				} else {
					infomap.put("use_workshop", "");
					errorMessage.append("必须填写使用车间;");
				}
				
				if (data.get(11) != null && !data.get(11).toString().equals("")) {
					infomap.put("process", data.get(11).toString().trim());
				    if(processCodeBf.toString().indexOf(";"+data.get(10) == null ? "" : data.get(10).toString().trim()+"-"+data.get(11).toString().trim()+";")<0){
				    	errorMessage.append("工序"+data.get(11).toString().trim()+"不存在;");
				    }else {
				        //根据工序代码或者工序名称
					    infomap.put("process_name", processMap.get(data.get(11).toString().trim()));
				    }
				
				} else {
					infomap.put("process", "");
					 infomap.put("process_name","");
					errorMessage.append("必须填写工序;");
				}
				
				if (data.get(12) != null && !data.get(12).toString().equals("")) {
					infomap.put("assembly_position", data.get(12).toString().trim());
				} else {
					infomap.put("assembly_position", "");
					errorMessage.append("必须填写装配位置;");
				}
				
				if (data.get(13) != null && !data.get(13).toString().equals("")) {
					infomap.put("zzj_no", data.get(13).toString().trim());
				} else {
					infomap.put("zzj_no", "");
					errorMessage.append("必须填写零部件编号;");
				}
				infomap.put("filling_size", data.get(14) == null ? null : data.get(14).toString().trim());
				infomap.put("accuracy_demand", data.get(15) == null ? null : data.get(15).toString().trim());
				infomap.put("surface_treatment", data.get(16) == null ? null : data.get(16).toString().trim());
				infomap.put("memo", data.get(17) == null ? null : data.get(17).toString().trim());
				infomap.put("processflow_memo", data.get(18) == null ? null : data.get(18).toString().trim());
				infomap.put("change_description", data.get(19) == null ? null : data.get(19).toString().trim());
				infomap.put("change_subject", data.get(20) == null ? null : data.get(20).toString().trim());
				if (data.get(21) != null && !data.get(21).toString().equals("")) {
					infomap.put("cailiao_type", data.get(21).toString().trim());
					if (cailiaoTypeBf.toString().indexOf(";"+data.get(21).toString()+";") < 0) {
						errorMessage.append("材料类型：" + data.get(21).toString().trim() + "不存在;");
					}
				} else {
					infomap.put("cailiao_type", "");
					errorMessage.append("必须填写材料类型;");
				}
				if (data.get(22) != null && !data.get(22).toString().equals("")) {
					infomap.put("material_no", data.get(22).toString().trim());
				} else {
					infomap.put("material_no", "");
					errorMessage.append("必须填写图号;");
				}
				if (data.get(23) != null && !data.get(23).toString().equals("")) {
					infomap.put("zzj_name", data.get(23).toString().trim());
				} else {
					infomap.put("zzj_name", "");
					errorMessage.append("必须填名称;");
				}
				
				if (data.get(24) != null && !data.get(24).toString().equals("")) {
					infomap.put("subcontracting_type", data.get(24).toString().trim());
					if (zzjSubTypeBf.toString().indexOf(";"+data.get(24).toString()+";") < 0) {
						errorMessage.append("分包类型：" + data.get(24).toString().trim() + "不存在;");
					}
				} else {
					infomap.put("subcontracting_type", "");
					errorMessage.append("必须填写分包类型;");
				}
				infomap.put("process_sequence", data.get(25) == null ? null : data.get(25).toString().trim());
				if (data.get(26) != null && !data.get(26).toString().equals("")) {
					String processFlowStr = data.get(26).toString().trim();
					String[] processFlowStrArr = processFlowStr.split("-");
					String notExitProcess = "";
					for (String processFlow : processFlowStrArr) {
						if (processBf.toString().indexOf(";"+workshop_name+"-"+processFlow+";") < 0) {
							notExitProcess += processFlow+",";
						}
					}
					if(StringUtils.isNotBlank(notExitProcess)) {
						errorMessage.append("工艺流程工序：" + notExitProcess + "在"+werks_name+"车间下不存在;");
					}
					infomap.put("process_flow", processFlowStr);
					int lastIndex = processFlowStr.lastIndexOf("-");
					String process_product = "";
					String process_guide = "";
					if(lastIndex <= 0 ) {
						process_product = processFlowStr;
						process_guide = "";
					}else {
						process_product = processFlowStr.substring(0, processFlowStr.lastIndexOf("-"));
						process_guide = processFlowStr.substring(processFlowStr.lastIndexOf("-")+1);
						//根据用户要求，当最后一道工序为 电泳或进仓时，暂不截取
						if("电泳".equals(process_guide) || "进仓".equals(process_guide)) {
							process_product = processFlowStr;
							process_guide = "";
						}
					}
					infomap.put("process_product", process_product);
					infomap.put("process_guide", process_guide);
				} else {
					infomap.put("process_flow", "");
					errorMessage.append("必须填写工艺流程;");
				}
				infomap.put("process_time", data.get(27) == null ? null : data.get(27).toString().trim());
				infomap.put("process_machine", data.get(28) == null ? null : data.get(28).toString().trim());
				if (data.get(29) != null && !data.get(29).toString().equals("")) {
					infomap.put("section", data.get(29).toString().trim());
					if (sectionBf.toString().indexOf(";"+data.get(29).toString()+";") < 0) {
						errorMessage.append("工段：" + data.get(29).toString().trim() + "不存在;");
					}
				} else {
					infomap.put("section", "");
					errorMessage.append("必须填写工段;");
				}
	 			
				infomap.put("aperture", data.get(30) == null ? null : data.get(30).toString().trim());
				infomap.put("maiban", data.get(31) == null ? null : data.get(31).toString().trim());
				infomap.put("banhou", data.get(32) == null ? null : data.get(32).toString().trim());
				infomap.put("filling_size_max", data.get(33) == null ? null : data.get(33).toString().trim());
				
				//零部件号+装配位置+使用工序+工艺流程
				String lineStr = "";
				lineStr += data.get(13) == null ? "" : data.get(13).toString().trim()+";";
				lineStr += data.get(12) == null ? "" : data.get(12).toString().trim()+";";
				lineStr += data.get(11) == null ? "" : data.get(11).toString().trim()+";";
				lineStr += data.get(26) == null ? "" : data.get(26).toString().trim()+";";
				
				//校验EXCEL数据是否存在重复行
				if(uniqueList.indexOf(lineStr) >=0) {
					//存在重复
					errorMessage.append("EXCEL中第"+(uniqueList.indexOf(lineStr)+1)+"行数据与第本行数据重复;");
					//将保存数据清单中重复的数据移到错误数据清单
					if(null !=saveMap.get(lineStr)) {
						errorList.add((Map<String, Object>) saveMap.get(lineStr));
						saveMap.remove(lineStr);
					}
					
				}else {
					uniqueList.add(lineStr);
				}
				//校验待导入数据与数据库中已导入的下料明细是否重复
				if(savedPmdItems.indexOf(lineStr) >=0) {
					//存在重复
					errorMessage.append("数据在系统已存在;");
				}
				
				if(errorMessage.toString().length()<=0) {
					//无错误，本行数据加入保存数据清单
					saveMap.put(lineStr, infomap);
				}else {
					infomap.put("errorMsg", errorMessage.toString());
					errorList.add(infomap);
				}
				
	 	 	}
	 		
	 		saveMap.forEach( (key,value) -> {
	 			saveList.add((Map<String, Object>) value);
	 		});
	 		
	 	    // 没有可保存的数据  将校验出错的记录返回
	 		if(saveList.size()==0) {
	 			return R.ok().put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
	 		}
 		
 			savePmdParams.put("pmd_list", saveList);
			pmdImportService.savePmdInfo(savePmdParams);
	    	return R.ok().put("data", errorList).put("saveCount", saveList.size()).put("errorCount", errorList.size());
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		logger.error("导入下料明细失败："+e.getMessage());
    		errorList.addAll(saveList);
    		return R.error(e.getMessage()).put("data", errorList).put("saveCount", "0").put("errorCount", errorList.size());
    	}
 	}
    
    
    @RequestMapping("/save")
 	public R save(@RequestBody Map<String,Object> params){
    	try {
	    	//machinePlanService.save(params);
	    	return R.ok();
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    // 处理空值
    public boolean isNull(Object str) {
    	boolean result=false;
    	if(str==null) {
    		return result;
    	}
    	if(str.toString().trim().equals("")) {
    		return result;
    	}
    	return true;
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