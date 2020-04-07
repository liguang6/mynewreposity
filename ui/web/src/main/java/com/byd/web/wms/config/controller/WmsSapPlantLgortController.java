package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsSapPlantLgortEntity;
import com.byd.web.wms.config.service.WmsSapPlantLgortRemote;

@RestController
@RequestMapping("/config/sapPlantLgort")
public class WmsSapPlantLgortController {
	
	@Autowired
	WmsSapPlantLgortRemote lgortRemote;
	@Autowired
	UserUtils userUtils;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String,Object> params){
		return lgortRemote.list(params);
	}
	
	@RequestMapping("/save")
	public R save(@RequestBody WmsSapPlantLgortEntity entity){
    	
		entity.setDel("0");
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		lgortRemote.save(entity);
		return R.ok();
	}
	@RequestMapping("/update")
	public R update(@RequestBody WmsSapPlantLgortEntity entity){
    	
		entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		lgortRemote.update(entity);
		return R.ok();
	}
	@RequestMapping("/get")
	public R getOne(@RequestParam Long id){
		return lgortRemote.getOne(id);
	}
	
	@RequestMapping("/del")
	public R delFlag(@RequestParam Long id){
		
		return lgortRemote.delFlag(id);
	}
	
	@RequestMapping("/preview")
	public R preview(@RequestBody MultipartFile excel) throws IOException{
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<WmsSapPlantLgortEntity> listEntity = new ArrayList<WmsSapPlantLgortEntity>();
		for(String[] row:sheet){
			WmsSapPlantLgortEntity entity = new WmsSapPlantLgortEntity();
			entity.setWerks(row[0]);
			entity.setLgort(row[1]);
			entity.setLgortName(row[2]);
			entity.setSobkz(row[3]);
			entity.setBadFlag(row[4]);
			listEntity.add(entity);
		}
		return lgortRemote.preview(listEntity);
	}
	
	@RequestMapping("/import")
	public R importWmsSapPlantLgortEntity(@RequestBody List<WmsSapPlantLgortEntity> listEntity){
		for(WmsSapPlantLgortEntity entity:listEntity){
			entity.setDel("0");
			entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
		}
		return lgortRemote.importWmsSapPlantLgortEntity(listEntity);
	}

}
