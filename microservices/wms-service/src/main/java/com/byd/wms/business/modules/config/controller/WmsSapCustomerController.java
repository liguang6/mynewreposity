package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.ExcelReader;
import com.byd.utils.ExcelWriter;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsSapCustomerEntity;
import com.byd.wms.business.modules.config.service.WmsSapCustomerService;


@RestController
@RequestMapping("/config/sapCustomer")
public class WmsSapCustomerController {
	@Autowired
	WmsSapCustomerService wmsSapCustomerService;

	//查询分页
	@RequestMapping("/list")
	public R list(@RequestParam Map<String,Object> params){
		String name1 = params.get("name1")==null?null:String.valueOf(params.get("name1"));
		if(StringUtils.isBlank(name1)){
			name1 = params.get("NAME1")==null?null:String.valueOf(params.get("NAME1"));
		}
		String kunnr = params.get("kunnr")==null?null:String.valueOf(params.get("kunnr"));
		if(StringUtils.isBlank(kunnr)){
			 kunnr = params.get("KUNNER")==null?null:String.valueOf(params.get("KUNNER"));
		}
		Page<WmsSapCustomerEntity> page =  wmsSapCustomerService.selectPage(new Query<WmsSapCustomerEntity>(params).getPage()
				,new EntityWrapper<WmsSapCustomerEntity>().like(StringUtils.isNotBlank(name1),"name1", name1)
				.like(StringUtils.isNotBlank(kunnr),"kunnr", kunnr)
				);
		return R.ok().put("page",new PageUtils(page));
	}
	
	
	//查询 单条记录
	@RequestMapping("/info")
	public R  getOne(Long id){
		WmsSapCustomerEntity entity = wmsSapCustomerService.selectById(id);
		return R.ok().put("data", entity);
	}
	
	//新增或更新
	@RequestMapping("/save")
	public R addOrUpdate(@RequestBody WmsSapCustomerEntity entity){
		wmsSapCustomerService.insertOrUpdate(entity);
		return R.ok();
	}
	
	//删除
	@RequestMapping("/delLogic")
	public R delLogic(Long id){
		//TODO:少字段
		return R.ok();
	}
	
	//查询预览
	@RequestMapping("/preview")
	public R preview(@RequestBody List<WmsSapCustomerEntity> entitys) throws IOException{
		
		for(WmsSapCustomerEntity entity:entitys){
			entity.setMsgList(new ArrayList<String>());
			//检查
			if(StringUtils.isBlank(entity.getBukrs())){
				//简单检查，后续添加其他检查条件
				entity.getMsgList().add("公司代码不能为空!");
			}
		}
		return R.ok().put("data", entitys);
	}
	
	//批量导入
	@RequestMapping("/import")
	public R importBatch(@RequestBody List<WmsSapCustomerEntity> entitys) {
		if (!CollectionUtils.isEmpty(entitys)) {
			wmsSapCustomerService.insertBatch(entitys);
		} else {
			return R.error("没有可以导入的记录");
		}
		return R.ok();
	}
	
	//导出excel文件
	@RequestMapping("/export")
	public ResponseEntity<byte[]> export2Excel(@RequestParam Map<String,Object> params) throws Exception{
		List<WmsSapCustomerEntity> entitys = wmsSapCustomerService.selectByMap(params);
		Map<String,String> columeMap = new LinkedHashMap<>();
		columeMap.put("bukrs", "公司简称");
		columeMap.put("kunnr", "客户代码");
		columeMap.put("name1", "客户名称");
		columeMap.put("sortl", "客户简称");
		columeMap.put("vkorg", "销售渠道");
		columeMap.put("vtweg", "分销渠道");
		columeMap.put("spart", "部门");
		columeMap.put("address", "地址");
		columeMap.put("manager", "客户管理者");
		columeMap.put("editor", "编辑人");
		columeMap.put("editDate", "编辑日期");
		
		return ExcelWriter.generateBytesResponse(entitys, columeMap);
	}
}
