package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCoreWhAreaEntity;
import com.byd.web.wms.config.service.WmsCoreWhAreaRemote;

/**
 * 仓库存储类型配置 各工厂仓库存储类型设置
 * @author tangj
 * @email 
 * @date 2018年08月06日 
 */
@RestController
@RequestMapping("config/wharea")
public class WmsCoreWhAreaController {
    @Autowired
    private WmsCoreWhAreaRemote wmsCoreWhAreaRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCoreWhAreaRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCoreWhAreaRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreWhAreaEntity whArea){
		whArea.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		whArea.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		whArea.setDel("0");
    	return wmsCoreWhAreaRemote.save(whArea);
    	
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreWhAreaEntity whArea){
		whArea.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		whArea.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCoreWhAreaRemote.update(whArea);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCoreWhAreaEntity entity = new WmsCoreWhAreaEntity();
		entity.setId(id);
		entity.setDel("X");
		return wmsCoreWhAreaRemote.delById(entity);
    }
    @RequestMapping("/import")
	public R upload(@RequestBody List<WmsCoreWhAreaEntity> entityList) throws IOException{
    	List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
    	for(int i=0;i<entityList.size();i++){
    		Map<String,Object> tempMap=new HashMap<String,Object>();
    		tempMap.put("whNumber", entityList.get(i).getWhNumber());
    		tempMap.put("storageAreaCode", entityList.get(i).getStorageAreaCode());
    		tempMap.put("areaName", entityList.get(i).getAreaName());
    		tempMap.put("storageModel", entityList.get(i).getStorageModel());
    		tempMap.put("putRule", entityList.get(i).getPutRule());
    		tempMap.put("mixFlag", entityList.get(i).getMixFlag());
    		tempMap.put("storageCapacityFlag", entityList.get(i).getStorageCapacityFlag());
    		tempMap.put("autoPutawayFlag", entityList.get(i).getAutoPutawayFlag());
    		tempMap.put("autoReplFlag", entityList.get(i).getAutoReplFlag());
    		tempMap.put("checkWeightFlag", entityList.get(i).getCheckWeightFlag());
    		tempMap.put("floor", entityList.get(i).getFloor());
    		tempMap.put("coordinate", entityList.get(i).getCoordinate());
    		tempMap.put("status", entityList.get(i).getStatus());
    		tempMap.put("editor", userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    		tempMap.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		mapList.add(tempMap);
    	}
    	return wmsCoreWhAreaRemote.upload(mapList);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		String whNumber="";
		String storageTypeCode="";
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", ++index);
				map.put("whNumber", row[0]);
				map.put("storageAreaCode", row[1]);			
				map.put("areaName", row[2]);
				map.put("storageModel", row[3]);
				map.put("putRule", row[4]);
				map.put("mixFlag", row[5]);
				map.put("storageCapacityFlag", row[6]);
				map.put("autoPutawayFlag", row[7]);
				map.put("autoReplFlag", row[8]);
				map.put("checkWeightFlag", row[9]);
				map.put("floor", row[10]);
				map.put("coordinate", row[11]);
				map.put("status", row[12]);
				map.put("del", "0");
				map.put("editor", userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(map);
				msg="";
			}
		}
		
		return wmsCoreWhAreaRemote.previewExcel(entityList);
	}  
	// 查询All
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params){
        return wmsCoreWhAreaRemote.queryAll(params);
    }
	// 查询All
	@RequestMapping("/queryAreaName")
	public R queryAreaName(@RequestParam Map<String, Object> params){
		return wmsCoreWhAreaRemote.queryAreaName(params);
	}




}
