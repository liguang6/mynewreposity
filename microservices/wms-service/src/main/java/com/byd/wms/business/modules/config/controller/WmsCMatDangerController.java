package com.byd.wms.business.modules.config.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCMatDangerEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.config.service.WmsCMatDangerService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.byd.wms.business.modules.config.service.WmsSapVendorService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 危化品物料配置表
 * @author tangj
 * @email 
 * @date 2018年08月01日 
 */
@RestController
@RequestMapping("config/matdanger")
public class WmsCMatDangerController {
	@Autowired
	private WmsSapVendorService wmsSapVendorService;
    @Autowired
    private WmsCMatDangerService wmsCMatDangerService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatDangerService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCMatDangerEntity matdanger = wmsCMatDangerService.selectById(id);
        return R.ok().put("matdanger", matdanger);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatDangerEntity matdanger){
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",matdanger.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂信息未维护！");
	    }
	    List<WmsSapVendor> vendorList = wmsSapVendorService.selectList(
				new EntityWrapper<WmsSapVendor>().eq("LIFNR",matdanger.getLifnr()));
	    if(vendorList.size()==0) {
	    	return R.error("供应商未维护！");
	    }
	    List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(
				new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS",matdanger.getWerks()).
				eq("MATNR",matdanger.getMatnr()));
	    if(materialList.size()==0) {
	    	return R.error("SAP物料信息未维护！");
	    }
        List<WmsCMatDangerEntity> list=wmsCMatDangerService.selectList(
    			new EntityWrapper<WmsCMatDangerEntity>().eq("WERKS", matdanger.getWerks())
    			.eq("MATNR", matdanger.getMatnr()).eq("LIFNR", matdanger.getMatnr()).eq("DEL","0"));
    	if(list.size()>0) {
    		return R.error("危化品物料记录已维护！");
    	}else {
    		wmsCMatDangerService.insert(matdanger);
        	return R.ok();
    	}
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatDangerEntity wh){
    	List<WmsSapVendor> vendorList = wmsSapVendorService.selectList(
				new EntityWrapper<WmsSapVendor>().eq("LIFNR",wh.getLifnr()));
	    if(vendorList.size()==0) {
	    	return R.error("供应商未维护！");
	    }
        wmsCMatDangerService.updateById(wh);
        return R.ok();
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCMatDangerEntity entity){
		wmsCMatDangerService.updateById(entity);
		return R.ok();
    }
    @RequestMapping("/upload")
	public R upload(@RequestBody List<WmsCMatDangerEntity> entityList) throws IOException{
    	List<WmsCMatDangerEntity> addList=new ArrayList<WmsCMatDangerEntity>();
    	List<WmsCMatDangerEntity> updateList=new ArrayList<WmsCMatDangerEntity>();

    	StringBuffer matnr=new StringBuffer();
    	for(WmsCMatDangerEntity entity : entityList) {
    		// 封装成 【工厂代码-供应商-物料号,工厂代码-供应商-物料号】
    		matnr.append(entity.getWerks().concat("-").concat(entity.getLifnr()).concat("-").concat(entity.getMatnr()).concat(","));
    	}
    	String [] param=matnr.toString().split(",");
    	List<Map<String,Object>> list=wmsCMatDangerService.validateMatDager(Arrays.asList(param));
    	for(WmsCMatDangerEntity entity : entityList) {
    		char dangerFlag=entity.getDangerFlag()=='是' ? 'X' : '0';
    		entity.setDangerFlag(dangerFlag);
    		char extendedEffectDate = entity.getExtendedEffectDate()=='是' ? 'X' : '0';
    		entity.setExtendedEffectDate(extendedEffectDate);
    		String concatStr=entity.getWerks()+"-"+entity.getLifnr()+"-"+entity.getMatnr();
    		boolean updateFlag=false;
    		for(Map<String,Object> map : list) {
    			String werksMatnr=map.get("WERKS_MATNR").toString();
    			if(concatStr.equals(werksMatnr)) {
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
        	wmsCMatDangerService.insertBatch(addList);
    	}
    	if(updateList.size()>0) {
    	    wmsCMatDangerService.updateBatchById(updateList);
    	}
		return R.ok();
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
				if(map.get("werks")!=null && !map.get("werks").toString().equals("")) {
					if(!werks.equals(map.get("werks").toString())) {
						plantList=wmsSapPlantService.selectList(
								new EntityWrapper<WmsSapPlant>().eq("WERKS", map.get("werks").toString()).eq("DEL","0"));
					    if(plantList.size()==0) {
					    	msg="工厂代码在系统中未维护;";
					    }else {
					    	werks=map.get("werks").toString();
					    }
					}
				} else {
					msg="工厂代码不能为空;";
				}
				List<WmsSapVendor> vendorList = null;
				if(map.get("lifnr")!=null && !map.get("lifnr").toString().equals("")) {
					if(!vendor.equals(map.get("lifnr").toString())) {
					    vendorList = wmsSapVendorService.selectList(
								new EntityWrapper<WmsSapVendor>().eq("LIFNR",map.get("lifnr").toString()));
					    if(vendorList.size()==0) {
					    	return R.error("供应商代码:"+map.get("lifnr").toString()+"在系统中未维护！");
					    }else {
					    	vendor=map.get("lifnr").toString();
					    }
					}
				} else {
					msg="供应商代码不能为空;";
				}
				map.put("lifnr", map.get("lifnr"));
				map.put("matnr", map.get("matnr"));
				if(map.get("matnr")!=null && !map.get("matnr").toString().equals("")) {
					if(matnrSb.indexOf(map.get("matnr").toString().concat(";"))>=0) {
						msg+="物料号存在重复数据;";
					}else {
						matnrSb.append(map.get("lifnr").toString().concat(";"));
						List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(
								new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS",map.get("werks")).
								eq("MATNR",map.get("matnr")));
					    if(materialList.size()==0) {
					    	msg+="SAP物料信息不存在;";
					    }else{
					    	map.put("maktx", materialList.get(0).getMaktx());
						}
					}
				} else {
					msg+="物料号不能为空;";
				}
				map.put("msg", msg);
				msg="";
			}
		}
		return R.ok().put("data", entityList);
	}  
}
