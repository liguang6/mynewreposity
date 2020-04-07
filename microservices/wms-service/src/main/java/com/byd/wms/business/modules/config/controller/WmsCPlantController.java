package com.byd.wms.business.modules.config.controller;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/config/CPlant")
public class WmsCPlantController {
	@Autowired
	WmsCPlantService wmscPlantService;
	@Autowired
	WmsSapPlantService wmsSapPlantService;

	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmscPlantService.queryPage(params);	
		return R.ok().put("page", page);
	}
	@RequestMapping("/get")
	public R getWmsCPlantById(Long id){
		WmsCPlant data = wmscPlantService.selectById(id);
		return R.ok().put("data", data);
	}
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	WmsCPlant cplant = wmscPlantService.selectById(id);
        return R.ok().put("cplant", cplant);
    }
	@RequestMapping("/update")
	public R update(@RequestBody WmsCPlant entity){
		
		wmscPlantService.updateById(entity);
		return R.ok();
	}
	@RequestMapping("/del")
	public R delete(@RequestBody WmsCPlant entity){
		wmscPlantService.updateById(entity);
		return R.ok();
	}
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsCPlant entity = new WmsCPlant();
			entity.setId(Long.parseLong(id[i]));
			entity.setDelFlag("X");
			wmscPlantService.updateById(entity);
		}
		return R.ok();
	}
	@RequestMapping("/save")
	public R add(@RequestBody WmsCPlant entity){
		
		wmscPlantService.insert(entity);
		return R.ok();
	}
	@RequestMapping("/listEntity")
	public R queryEntity(@RequestParam Map<String, Object> params){
		params.put("del_flag", "0");
		String onlyOne="";
		String onlyid="";
		List<WmsCPlant> entityList=wmscPlantService.selectByMap(params);
		if(entityList!=null&&entityList.size()>0){
			onlyOne="true";
			onlyid=entityList.get(0).getId().toString();
		}else{
			onlyOne="false";
		}
		return R.ok().put("onlyOne", onlyOne).put("onlyId", onlyid);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<WmsCPlant> entityList) throws IOException{
		
		if(!CollectionUtils.isEmpty(entityList)){
			
			for(WmsCPlant entity:entityList){
				String msg="";
				
				if(entity.getWerks()==null || (entity.getWerks()!=null && "".equals(entity.getWerks()))){
					msg+="工厂代码不能为空";
				}else {
					Map<String, Object> params=new HashMap<String, Object>();
					params.put("werks", entity.getWerks());
					params.put("del_flag", "0");
					
					List<WmsCPlant> cplantList=wmscPlantService.selectByMap(params);
					if(cplantList!=null&&cplantList.size()>0){
						msg+="该工厂代码已经存在";
					}
				}
				
/*				if(entity.getWerksName()==null || (entity.getWerksName()!=null && "".equals(entity.getWerksName()))){
					msg+="工厂名称不能为空";
				}*/
				entity.setMsg(msg);
				
				entityList.add(entity);
				
			}
		}
		return R.ok().put("data", entityList);
	}
	@RequestMapping("/import")
	public R upload(@RequestBody List<WmsCPlant> entityList) throws IOException{
		 for(int i=0;i<entityList.size();i++){
			 WmsCPlant wmscPlant=new WmsCPlant();
			 wmscPlant.setWerks(entityList.get(i).getWerks());
			// wmscPlant.setWerksName(entityList.get(i).getWerksName());
			 if(entityList.get(i).getWmsFlag()!=null){
				 if("是".equals(entityList.get(i).getWmsFlag())){
					 wmscPlant.setWmsFlag("X");
				 }else if("否".equals(entityList.get(i).getWmsFlag())){
					 wmscPlant.setWmsFlag("0");
				 }
			 }
			 if(entityList.get(i).getVendorFlag()!=null){
				 if("是".equals(entityList.get(i).getVendorFlag())){
					 wmscPlant.setVendorFlag("X");
				 }else if("否".equals(entityList.get(i).getVendorFlag())){
					 wmscPlant.setVendorFlag("0");
				 }
			 }
			 if(entityList.get(i).getIgFlag()!=null){
				 if("是".equals(entityList.get(i).getIgFlag())){
					 wmscPlant.setIgFlag("X");
				 }else if("否".equals(entityList.get(i).getIgFlag())){
					 wmscPlant.setIgFlag("0");
				 }
			 }
			 if(entityList.get(i).getHxFlag()!=null){
				 if("是".equals(entityList.get(i).getHxFlag())){
					 wmscPlant.setHxFlag("X");
				 }else if("否".equals(entityList.get(i).getHxFlag())){
					 wmscPlant.setHxFlag("0");
				 }
			 }
			 
			 wmscPlant.setDelFlag("0");
			 wmscPlantService.insert(wmscPlant);
		 }
		 
		 return R.ok();
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
}
