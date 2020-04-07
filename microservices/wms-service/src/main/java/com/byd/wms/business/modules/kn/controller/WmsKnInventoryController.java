package com.byd.wms.business.modules.kn.controller;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;
import com.byd.wms.business.modules.kn.service.WmsKnInventoryService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



/**
 * 盘点记录
 *
 * @author cscc tangjin
 * @email 
 * @date 2018-10-19 10:12:08
 */
@RestController
@RequestMapping("kn/inventory")
public class WmsKnInventoryController {
    @Autowired
    private WmsKnInventoryService wmsKnInventoryService;
    @Autowired
	FreeMarkerConfigurer configurer;
	@Autowired
	HttpServletResponse response;
	@Autowired
	HttpServletRequest request;
	@Autowired
	WmsSapPlantLgortService lgortService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestBody Map<String, Object> params){
        PageUtils page = wmsKnInventoryService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 打印、导出、明细查询BY inventoryNo
     */
    @RequestMapping("/getItemByInventoryNo")
    public R getItemByInventoryNo(@RequestBody String INVENTORY_NO){
    	List<Map<String, Object>> list=wmsKnInventoryService.queryInventoryItem(INVENTORY_NO);
        return R.ok().put("list", list);
    }
    /**
     * 创建盘点表
     */
    @RequestMapping("/create")
    public R create(@RequestBody Map<String, Object> params){
    	Map<String,Object> result=wmsKnInventoryService.createInventoryTask(params);
    	if(result.get("code").toString().equals("0")) {
    		return R.ok(result.get("msg").toString());
    	}else {
    		return R.error(result.get("msg").toString());
    	}
    }
    @RequestMapping(value = "/print")
    public R print(@RequestBody Map<String,Object> params) throws IOException {

    	List<Map<String,Object>> dataList=wmsKnInventoryService.print(params);
    	
    	return R.ok();
    }
    
    /**
     * 盘点结果录入页面查询
     */
    @CrossOrigin
    @RequestMapping("/getInventoryResult")
    public R getInventoryResult(@RequestBody Map<String, Object> params){
    	Map<String,Object> map=wmsKnInventoryService.getInventoryHead(params);
    	if(map==null) {
    		return R.error("该工厂不存在该盘点任务号");
    	}else {
    		String status=map.get("STATUS").toString();
    		String type=params.get("TYPE").toString();
    		// 初盘：00【盘点任务号状态只有为"已打印"、"已初盘"才允许录入】
    		if(type.equals("00")) {
    			if(!status.equals("01") && !status.equals("02")) {
    				return R.error("盘点任务号状态为"+map.get("STATUS_DESC").toString()+",不允许录入初盘数据");
    			}
    		}
    		// 复盘：01【盘点任务号状态只有为"已初盘"、"已复盘"才允许录入】
    		if(type.equals("01")) {
    			if(!status.equals("02") && !status.equals("03")) {
    				return R.error("盘点任务号状态为"+map.get("STATUS_DESC").toString()+",不允许录入复盘数据");
    			}
    		}
    	}
    	List<Map<String, Object>> list=wmsKnInventoryService.queryInventoryItem(params.get("INVENTORY_NO").toString());
        map.put("list", list);
    	return R.ok().put("map", map);
    }
    /**
     * 盘点结果录入保存
     */
    @CrossOrigin
    @RequestMapping("/saveResult")
    public R saveResult(@RequestBody Map<String, Object> params){
		wmsKnInventoryService.batchUpdateResult(params);
    	return R.ok();
    }
    // 盘点结果导入保存
    @RequestMapping("/import")
	public R upload(@RequestBody Map<String, Object> params) throws IOException{
    	
		wmsKnInventoryService.batchUpdateImp(params);
		return R.ok();
	}
	// Excel导入盘点结果
	@RequestMapping("/previewExcel")
	public R previewExcel(@RequestBody Map<String, Object> params){
		List<Map<String,Object>> entityList = (List<Map<String,Object>>)params.get("entityList");
		String currentUser=params.get("USERNAME").toString();
		List<Map<String,Object>> saveList=new ArrayList<Map<String,Object>>();
		String currentDate=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String msg="";
		String type=params.get("TYPE").toString();
		String inventoryNo=null;
		
		for(Map<String, Object> row:entityList){
			Map<String,Object> map=new HashMap<String,Object>();
			if(row.get("INVENTORY_NO")==null && (row.get("INVENTORY_NO")!=null && row.get("INVENTORY_NO").toString().equals(""))) {
				msg="盘点任务号不能为空;";
			}else {
				if(row.get("WERKS")==null && (row.get("WERKS")!=null && row.get("WERKS").toString().equals(""))) {
					msg+="工厂代码不能为空;";
				}
				if(row.get("WH_NUMBER")==null && (row.get("WH_NUMBER")!=null && row.get("WH_NUMBER").toString().equals(""))) {
					msg+="仓库号不能为空;";
				}
				if(inventoryNo==null) {
					Map<String,Object> param=new HashMap<String,Object>();
					param.put("INVENTORY_NO", row.get("INVENTORY_NO"));
					param.put("WERKS", row.get("WERKS"));
					param.put("WH_NUMBER", row.get("WH_NUMBER"));
					Map<String,Object> head=wmsKnInventoryService.getInventoryHead(param);
				    if(head==null) {
				    	msg+="盘点任务号不存在;";
				    }
				    inventoryNo=row.get("INVENTORY_NO").toString();
				}else {
					if(!inventoryNo.equals(row.get("INVENTORY_NO").toString())) {
						return R.error("不允许导入多个盘点任务号的数据").put("data", entityList);
					}					
				}
				
			}
			if(row.get("MATNR")==null && (row.get("MATNR")!=null && row.get("MATNR").toString().equals(""))) {
				msg+="物料号不能为空;";
			}
			if(row.get("LIFNR")==null && (row.get("LIFNR")!=null && row.get("LIFNR").toString().equals(""))) {
				msg+="供应商代码不能为空;";
			}
			// 导入初盘结果数据（type：00）
			if(type.equals("00")) {
				if(row.get("INVENTORY_QTY")==null && (row.get("INVENTORY_QTY")!=null && row.get("INVENTORY_QTY").toString().equals(""))) {
					msg+="初盘数量不能为空;";
				}
			}
			// 导入复盘结果数据（type：01）
			if(type.equals("01")) {
				if(row.get("INVENTORY_QTY_REPEAT")==null && (row.get("INVENTORY_QTY_REPEAT")!=null && row.get("INVENTORY_QTY_REPEAT").toString().equals(""))) {
					msg+="复盘数量不能为空;";
				}
			}
			map.put("EDITOR", currentUser);
			map.put("EDIT_DATE", currentDate);
			map.put("MSG", msg);
			entityList.add(map);
			msg="";
		}
		return R.ok().put("data", entityList);
	}
	/**
     * 盘点结果确认页面查询
     */
    @RequestMapping("/getInventoryConfirm")
    public R getInventoryConfirm(@RequestBody Map<String, Object> params){
    	
    	List<Map<String, Object>> list=wmsKnInventoryService.getInventoryConfirmList(params);
        if(list.size()==0) {
        	R.error("未找到符合条件的数据!");
        }
    	return R.ok().put("list", list);
    }
    /**
     * 盘点结果确认保存
     */
    @RequestMapping("/saveConfirm")
    public R saveConfirm(@RequestBody Map<String, Object> params){
		wmsKnInventoryService.batchUpdateConfirm(params);
    	return R.ok();
    }
    /**
     * 获取库位
     */
    @RequestMapping("/lgortlist")
    public R lgortlist(@RequestBody Map<String, Object> params){
    	
    	List<WmsSapPlantLgortEntity> result=lgortService.selectByMap(params);
    	return R.ok().put("list", result);
    }
    /**
     * 加载区域管理员
     */
    @RequestMapping("/getWhManagerList")
    public R getWhManagerList(@RequestBody Map<String, Object> params){
    	List<Map<String,Object>> list=wmsKnInventoryService.getWhManagerList(params);
    	return R.ok().put("list", list);
    }
}