package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.common.staticFactory.LocaleLanguageFactory;
import com.byd.web.wms.config.service.WmsCMatFixedStorageRemote;

/**
 * @author ren.wei3
 * 物料固定储位配置
 */
@RestController
@RequestMapping("config/fixedStorage")
public class WmsCMatFixedStorageController {

	@Autowired
    private WmsCMatFixedStorageRemote wmsCMatFixedStorageRemote;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
//    	String sql = params.get(Constant.SQL_FILTER) == null ? null:(String)params.get(Constant.SQL_FILTER);
//    	System.err.println("sql>>>>>>>>"+sql);
        return wmsCMatFixedStorageRemote.list(params);
    }
    
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCMatFixedStorageRemote.info(id);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
	    params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
	    params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    //验证数据权限
	    boolean errorflag = false;
		//为区分业务数据与权限过滤KEY, 权限对象KEY加前缀 "auth_"
		List<String> authfilter = (List<String>) params.get("auth_WH_NUMBER"); 
		if (authfilter != null) {
			for (String filter : authfilter) {
				Pattern pattern = Pattern.compile(filter);
				Matcher matcher = pattern.matcher(params.get("whNumber").toString());
				if(matcher.find()){
					errorflag = true;
	            }
			}
		}
		if (!errorflag && authfilter != null) {
//	    	return R.error("您无权操作"+params.get("whNumber").toString()+"的数据!");
	    	return R.error(LocaleLanguageFactory.getValue("NOT_AUTHORIZED_DATA", params.get("whNumber")));
	    }
        return wmsCMatFixedStorageRemote.save(params);
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
        params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
	    params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    //验证数据权限
		boolean errorflag = false;
		//为区分业务数据与权限过滤KEY, 权限对象KEY加前缀 "auth_"
		List<String> authfilter = (List<String>) params.get("auth_WH_NUMBER");
		if (authfilter != null) {
			for (String filter : authfilter) {
				Pattern pattern = Pattern.compile(filter);
				Matcher matcher = pattern.matcher(params.get("whNumber").toString());
				if(matcher.find()){
					errorflag = true;
	            }
			}
		}
		if (!errorflag && authfilter != null) {
//	    	return R.error("您无权操作"+params.get("whNumber").toString()+"的数据!");
	    	return R.error(LocaleLanguageFactory.getValue("NOT_AUTHORIZED_DATA", params.get("whNumber")));
	    }
	    
        return wmsCMatFixedStorageRemote.update(params);
    }
    
    /**
     * 软删BY ID
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Map<String, Object> params){
    	String ids = "";
    	String deleteListStr=params.get("deleteList").toString();
    	List<Map> deleteList = JSONObject.parseArray(deleteListStr, Map.class);
    	for (Map<String,Object> deletemap: deleteList) {
    		//验证数据权限
    		boolean errorflag = false;
    		//为区分业务数据与权限过滤KEY, 权限对象KEY加前缀 "auth_"
    		List<String> authfilter = (List<String>) params.get("auth_WH_NUMBER"); 
    		if (authfilter != null) {
	    		for (String filter : authfilter) {
	    			Pattern pattern = Pattern.compile(filter);
	    			Matcher matcher = pattern.matcher(deletemap.get("whNumber").toString());
	    			if(matcher.find()){
	    				errorflag = true;
	                }
	    		}
    		}
    		if (!errorflag && authfilter != null) {
    			return R.error(LocaleLanguageFactory.getValue("NOT_AUTHORIZED_DATA", deletemap.get("whNumber")));
//    			return R.error("您无权操作"+deletemap.get("whNumber").toString()+"的数据!");
    		}

    	    ids=ids+deletemap.get("id")+",";
    	}
	    return wmsCMatFixedStorageRemote.delById(ids);
    }
    
    /**
     * 批量导入固定储位
     * @param excel
     * @return
     * @throws IOException
     */
    @RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", ++index);
				map.put("whNumber", row[0]);
				map.put("werks", row[1]);
				map.put("matnr", row[2]);
				map.put("maktx", row[3]);			
				map.put("storageAreaCode", row[4]);
				map.put("binCode", row[5]);
				map.put("seqno", row[6]);
				//map.put("qty", row[7]);
				map.put("stockM", row[7]);
				map.put("stockL", row[8]);
				map.put("lgort", row[9]);
				map.put("sobkz", row[10]);
				map.put("lifnr", row[11]);
				map.put("editor", userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(map);
			}
		}
		return wmsCMatFixedStorageRemote.previewExcel(entityList);
	}
    
    @RequestMapping("/import")
	public R upload(@RequestBody List<Map<String,Object>> entityList) throws IOException{ 
		return wmsCMatFixedStorageRemote.upload(entityList);
	}
}
