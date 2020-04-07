package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsCQcMatEntity;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.WmsCVendorService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.byd.wms.business.modules.config.service.WmsSapVendorService;

@RestController
@RequestMapping("/config/CVendor")
public class WmsCVendorController {
	@Autowired
	WmsCVendorService wmscVendorService;
	@Autowired
	WmsCPlantService wmscPlantService;
	@Autowired
	WmsSapPlantService wmsSapPlantService;
	@Autowired
	WmsSapVendorService wmsSapVendorService;
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmscVendorService.queryPage(params);	
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/save")
	public R add(@RequestBody WmsCVendor entity){
		List<WmsSapVendor> list=wmsSapVendorService.selectList(new EntityWrapper<WmsSapVendor>().eq("LIFNR",entity.getLifnr()));
		if(list.size()==0) {
			return R.error("供应商代码"+entity.getLifnr()+"不存在");
		}
		wmscVendorService.insert(entity);
		return R.ok();
	}
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam("id") Long id){
    	WmsCVendor cvendor = wmscVendorService.selectById(id);
        return R.ok().put("cvendor", cvendor);
    }
    
	@RequestMapping("/update")
	public R update(@RequestBody WmsCVendor entity){
		
		wmscVendorService.updateById(entity);
		return R.ok();
	}
	@RequestMapping("/del")
	public R delete(Long id){
		if(id == null){
			return R.error("参数错误");
		}
		WmsCVendor entity = new WmsCVendor();
		entity.setId(id);
		entity.setDelFlag("X");
		wmscVendorService.updateById(entity);
		return R.ok();
	}
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		List<WmsCVendor> list=new ArrayList<WmsCVendor>();
		for(int i=0;i<id.length;i++){
			WmsCVendor entity = new WmsCVendor();
			entity.setId(Long.parseLong(id[i]));
			entity.setDelFlag("X");
			list.add(entity);
		}
		boolean isUpdate=wmscVendorService.updateBatchById(list);
		if(isUpdate)
		    return R.ok();
		else 
			return R.error();
	}
	@RequestMapping("/listEntity")
	public R queryEntity(@RequestParam Map<String, Object> params){
		params.put("del_flag", "0");
		String onlyOne="";
		String onlyid="";
		List<WmsCVendor> entityList=wmscVendorService.selectByMap(params);
		if(entityList!=null&&entityList.size()>0){
			onlyOne="true";
			onlyid=entityList.get(0).getId().toString();
		}else{
			onlyOne="false";
		}
		return R.ok().put("onlyOne", onlyOne).put("onlyId", onlyid);
	}
	
	@RequestMapping("/listPlantName")
	public R queryPlantName(@RequestParam Map<String, Object> params){
		params.put("del", "0");
		String werksName="";
		List<WmsSapPlant> plantList=wmsSapPlantService.selectByMap(params);
		if(plantList!=null&&plantList.size()>0){
			werksName=plantList.get(0).getWerksName();
		}
		return R.ok().put("werksName", werksName);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<WmsCVendor> entityList) throws IOException{
		String werks="";
		String werksName="";
		if(!CollectionUtils.isEmpty(entityList)){
			
			for(WmsCVendor entity:entityList){
				String msg="";
				if(!werks.equals(entity.getWerks())) {
					werks=entity.getWerks();
					Map<String, Object> params=new HashMap<String, Object>();
					params.put("werks", entity.getWerks());
					List<WmsSapPlant> plantList=wmsSapPlantService.selectByMap(params);
					if(plantList!=null&&plantList.size()>0){
						werksName=plantList.get(0).getWerksName();
					}
				}
				if(StringUtils.isBlank(entity.getWerks()==null ? "" : entity.getWerks())){
					msg+="工厂代码不能为空";
				}
				entity.setWerksName(werksName);
				if(entity.getLifnr()!=null && !entity.getLifnr().equals("")){
					Map<String, Object> params=new HashMap<String, Object>();
					params.put("LIFNR", entity.getLifnr());
					List<WmsSapVendor> vendorList=wmsSapVendorService.selectByMap(params);
					if(vendorList!=null&&vendorList.size()>0){
						entity.setName1(vendorList.get(0).getName1());
					}else {
						msg+="供应商不存在！";
					}
				}else {
					msg+="供应商不能为空！";
				}
				entity.setMsg(msg);				
			}
		}
		return R.ok().put("data", entityList);
	}
	
	@RequestMapping("/upload")
	public R upload(@RequestBody List<WmsCVendor> entityList) throws IOException{
		
		List<WmsCVendor> addList=new ArrayList<WmsCVendor>();
    	List<WmsCVendor> updateList=new ArrayList<WmsCVendor>();

    	StringBuffer matnr=new StringBuffer();
    	for(WmsCVendor entity : entityList) {
    		// 封装成 【工厂代码-供应商,工厂代码-供应商】
    		matnr.append(entity.getWerks().concat("-").concat(entity.getLifnr()).concat(","));
    	}
    	String [] param=matnr.toString().split(",");
    	List<Map<String, Object>> list=wmscVendorService.validate(Arrays.asList(param));
    	for(WmsCVendor entity : entityList) {
    		String concatStr=entity.getWerks()+"-"+entity.getLifnr();
    		boolean updateFlag=false;
    		for(Map<String,Object> map : list) {
    			String werksLifnr=map.get("WERKS_LIFNR").toString();
    			if(concatStr.equals(werksLifnr)) {
    				entity.setId(Long.valueOf(map.get("ID").toString()));
    				updateList.add(entity);
    				updateFlag=true;
    				break;
    			}
    		}
    		if(!updateFlag) {
    			addList.add(entity);
    		}
    	}
    	if(addList.size()>0) {
    		wmscVendorService.insertBatch(addList);
    	}
    	if(updateList.size()>0) {
    		wmscVendorService.updateBatchById(updateList);
    	}
	    return R.ok();
	 }
	
	@RequestMapping("/listVendortName")
	public R queryVendorName(@RequestParam Map<String, Object> params){
		
		String vendorName="";
		List<WmsSapVendor> vendorList=wmsSapVendorService.selectByMap(params);
		if(vendorList!=null&&vendorList.size()>0){
			vendorName=vendorList.get(0).getName1();
		}
		return R.ok().put("vendorName", vendorName);
	}

	@RequestMapping("/querySAPVendorName")
	public R querySAPVendorName(@RequestParam Map<String, Object> params){

		PageUtils page =wmsSapVendorService.queryPage(params);

		return R.ok().put("page", page);
	}

}
