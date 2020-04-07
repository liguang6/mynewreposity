package com.byd.wms.business.modules.kn.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.kn.service.WmsCBarcodeStorageService;

/**
 * 
 * 储位标签打印
 *
 */
@RestController
@RequestMapping("kn/barcodeStorage")
public class WmsCBarcodeStorageController {
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsCBarcodeStorageService WmsCBarcodeStorageService;
	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = WmsCBarcodeStorageService.queryPage(params);
		return R.ok().put("page", page);
	}
		
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<Map<String, Object>> entityList) throws IOException{
		String werks="";
		String vendor = new String();
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		if(!CollectionUtils.isEmpty(entityList)){
			for(Map<String, Object> map:entityList){
				List<WmsSapPlant> plantList = null;

				map.put("msg", msg);
				msg="";
			}
		}
		return R.ok().put("data", entityList);
	}  
	
	@RequestMapping("/import")
	public R importExel(@RequestBody List<Map<String, Object>> params) {
		boolean flag = WmsCBarcodeStorageService.importExel(params);
		return R.ok().put("flag", flag);
	}
}
