package com.byd.wms.business.modules.config.controller;

import java.io.IOException;
import java.util.HashMap;
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
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年3月4日 下午3:28:15 
 * 类说明 
 */
@RestController
@RequestMapping("/config/CWh")
public class WmsCWhController {
	@Autowired
	WmsSapPlantService wmsSapPlantService;
	@Autowired
	WmsCWhService wmsCWhService;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmsCWhService.queryPage(params);	
		return R.ok().put("page", page);
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	WmsCWhEntity cwh = wmsCWhService.selectById(id);
    	
    	Map<String, Object> sapMap=new HashMap<String, Object>();
    	sapMap.put("WERKS", cwh.getWerks());
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectByMap(sapMap);
		if(plantList!=null&&plantList.size()>0){
			cwh.setWerksName(plantList.get(0).getWerksName());
		}
        return R.ok().put("cplant", cwh);
    }
	
	@RequestMapping("/deletes")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsCWhEntity cWhTemp=new WmsCWhEntity();
			cWhTemp.setId(Long.parseLong(id[i]));
			cWhTemp.setDelFlag("X");
			wmsCWhService.updateById(cWhTemp);
		}
		return R.ok();
	}
	
	@RequestMapping("/queryPlantName")
	public R queryPlantName(@RequestParam Map<String, Object> params){
		params.put("del", "0");
		String werksName="";
		List<WmsSapPlant> plantList=wmsSapPlantService.selectByMap(params);
		if(plantList!=null&&plantList.size()>0){
			werksName=plantList.get(0).getWerksName();
		}
		return R.ok().put("werksName", werksName);
	}
	
	@RequestMapping("/save")
	public R add(@RequestBody WmsCWhEntity entity){
		
		wmsCWhService.insert(entity);
		return R.ok();
	}
	
	@RequestMapping("/queryEntity")
	public R queryEntity(@RequestParam Map<String, Object> params){
		params.put("DEL_FLAG", "0");
		String onlyOne="";
		String onlyid="";
		List<WmsCWhEntity> entityList=wmsCWhService.selectByMap(params);
		if(entityList!=null&&entityList.size()>0){
			onlyOne="true";
			onlyid=entityList.get(0).getId().toString();
		}else{
			onlyOne="false";
		}
		return R.ok().put("onlyOne", onlyOne).put("onlyId", onlyid);
	}
	
	@RequestMapping("/update")
	public R update(@RequestBody WmsCWhEntity entity){
		wmsCWhService.updateById(entity);
		return R.ok();
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<WmsCWhEntity> entityList) throws IOException{
		
		if(!CollectionUtils.isEmpty(entityList)){
			
			for(WmsCWhEntity entity:entityList){
				String msg="";
				
				if(entity.getWerks()==null || (entity.getWerks()!=null && "".equals(entity.getWerks()))){
					msg+="工厂代码不能为空";
				}else {
					Map<String, Object> params=new HashMap<String, Object>();
					params.put("WERKS", entity.getWerks());
					params.put("DEL_FLAG", "0");
					
					List<WmsCWhEntity> cplantList=wmsCWhService.selectByMap(params);
					if(cplantList!=null&&cplantList.size()>0){
						msg+="该工厂代码已经存在";
					}
				}
				
				if(entity.getWerksName()==null || (entity.getWerksName()!=null && "".equals(entity.getWerksName()))){
					msg+="工厂名称不能为空";
				}
				entity.setMsg(msg);
				
				
			}
		}
		return R.ok().put("data", entityList);
	}
	
	@RequestMapping("/upload")
	public R upload(@RequestBody List<WmsCWhEntity> entityList) throws IOException{
		 for(int i=0;i<entityList.size();i++){
			 WmsCWhEntity wmscPlant=new WmsCWhEntity();
			 wmscPlant.setWerks(entityList.get(i).getWerks());
			 wmscPlant.setWerksName(entityList.get(i).getWerksName());
			 wmscPlant.setWhNumber(entityList.get(i).getWhNumber());
			 //wmscPlant.setLgort(entityList.get(i).getLgort());
			 
			 wmscPlant.setEditor(entityList.get(i).getEditor());
			 wmscPlant.setEditorDate(entityList.get(i).getEditorDate());
			 
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
			 if(entityList.get(i).getPackageFlag()!=null){
				 if("是".equals(entityList.get(i).getPackageFlag())){
					 wmscPlant.setPackageFlag("X");
				 }else if("否".equals(entityList.get(i).getPackageFlag())){
					 wmscPlant.setPackageFlag("0");
				 }
			 }
			 /*if(entityList.get(i).getPdaPickFlag()!=null){
				 if("是".equals(entityList.get(i).getPdaPickFlag())){
					 wmscPlant.setPdaPickFlag("X");
				 }else if("否".equals(entityList.get(i).getPdaPickFlag())){
					 wmscPlant.setPdaPickFlag("0");
				 }
			 }*/
			 if(entityList.get(i).getBarcodeFlag()!=null){
				 if("是".equals(entityList.get(i).getBarcodeFlag())){
					 wmscPlant.setBarcodeFlag("X");
				 }else if("否".equals(entityList.get(i).getBarcodeFlag())){
					 wmscPlant.setBarcodeFlag("0");
				 }
			 }
			 if(entityList.get(i).getResbdFlag()!=null){
				 if("是".equals(entityList.get(i).getResbdFlag())){
					 wmscPlant.setResbdFlag("X");
				 }else if("否".equals(entityList.get(i).getResbdFlag())){
					 wmscPlant.setResbdFlag("0");
				 }
			 }
			 if(entityList.get(i).getCmmsFlag()!=null){
				 if("是".equals(entityList.get(i).getCmmsFlag())){
					 wmscPlant.setCmmsFlag("X");
				 }else if("否".equals(entityList.get(i).getCmmsFlag())){
					 wmscPlant.setCmmsFlag("0");
				 }
			 }
			 
			 wmscPlant.setDelFlag("0");
			 wmsCWhService.insert(wmscPlant);
		 }
		 
		 return R.ok();
	 }
}
