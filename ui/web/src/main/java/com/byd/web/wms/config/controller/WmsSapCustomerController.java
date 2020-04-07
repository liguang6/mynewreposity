package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsSapCustomerEntity;
import com.byd.web.wms.config.service.WmsSapCustomerRemote;


@RestController
@RequestMapping("/config/sapCustomer")
public class WmsSapCustomerController {
	@Autowired
	WmsSapCustomerRemote wmsSapCustomerRemote;
	@Autowired
	UserUtils userUtils;

	//查询分页
	@RequestMapping("/list")
	public R list(@RequestParam Map<String,Object> params){
		
		return wmsSapCustomerRemote.list(params);
	}
	
	
	//查询 单条记录
	@RequestMapping("/info")
	public R  getOne(@RequestParam Long id){
		return wmsSapCustomerRemote.info(id);
	}
	
	//新增或更新
	@RequestMapping("/save")
	public R addOrUpdate(@RequestBody WmsSapCustomerEntity entity){
		Map<String,Object> currentUser = userUtils.getUser();
		entity.setEditor(currentUser.get("USERNAME").toString()+"："+currentUser.get("FULL_NAME"));
		entity.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		return wmsSapCustomerRemote.addOrUpdate(entity);
	}
	
	//删除
	@RequestMapping("/delLogic")
	public R delLogic(@RequestParam Long id){
		//TODO:少字段
		return R.ok();
	}
	
	//查询预览
	@RequestMapping("/preview")
	public R preview(@RequestBody MultipartFile excel) throws IOException{
		List<String[]> sheet = ExcelReader.readExcel(excel);
		List<WmsSapCustomerEntity> entitys = new ArrayList<WmsSapCustomerEntity>();
		for(String[] row:sheet){
			WmsSapCustomerEntity entity = new WmsSapCustomerEntity();
			//转换成bean
			entity.setBukrs(row[0]);
			entity.setKunnr(row[1]);
			entity.setName1(row[2]);
			entity.setSortl(row[3]);
			entity.setVkorg(row[4]);
			entity.setVtweg(row[5]);
			entity.setSpart(row[6]);
			entity.setAddress(row[7]);
			entity.setManager(row[8]);
			
			entitys.add(entity);
		}
		return wmsSapCustomerRemote.preview(entitys);
	}
	
	//批量导入
	@RequestMapping("/import")
	public R importBatch(@RequestBody List<WmsSapCustomerEntity> entitys) {
		if (entitys != null && entitys.size() > 0) {
			return wmsSapCustomerRemote.importBatch(entitys);
		} else {
			return R.error("没有可以导入的记录");
		}
	}
	
	//导出excel文件
	@RequestMapping("/export")
	public ResponseEntity<byte[]> export2Excel(@RequestParam Map<String,Object> params) throws Exception{
		return wmsSapCustomerRemote.export2Excel(params);
	}
}
