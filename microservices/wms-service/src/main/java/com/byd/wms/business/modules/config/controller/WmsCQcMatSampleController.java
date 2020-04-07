package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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
import com.byd.wms.business.modules.config.entity.WmsCQcMatEntity;
import com.byd.wms.business.modules.config.entity.WmsCQcMatSampleEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.config.service.WmsCQcMatSampleService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.byd.wms.business.modules.config.service.WmsSapVendorService;





/**
 * 物料质检抽样配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@RestController
@RequestMapping("config/wmscqcmatsample")
public class WmsCQcMatSampleController {
    @Autowired
    private WmsCQcMatSampleService wmsCQcMatSampleService;
    @Autowired
	private WmsSapVendorService wmsSapVendorService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCQcMatSampleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCQcMatSampleEntity wmsCQcMatSample = wmsCQcMatSampleService.selectById(id);

        return R.ok().put("data", wmsCQcMatSample);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcMatSampleEntity wmsCQcMatSample){
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",wmsCQcMatSample.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂信息未维护！");
	    }
	    List<WmsCQcMatSampleEntity> qcMatList=wmsCQcMatSampleService.selectList(
				new EntityWrapper<WmsCQcMatSampleEntity>().eq("WERKS",wmsCQcMatSample.getWerks())
				.eq("MATNR",wmsCQcMatSample.getMatnr())
				.eq("LIFNR",wmsCQcMatSample.getLifnr()).eq("DEL","0"));
	    if(qcMatList.size()>0) {
	    	return R.error(wmsCQcMatSample.getWerks()+wmsCQcMatSample.getMatnr()+wmsCQcMatSample.getLifnr()+"已维护！");
	    }
        wmsCQcMatSampleService.insert(wmsCQcMatSample);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcMatSampleEntity wmsCQcMatSample){
        
        wmsCQcMatSampleService.updateAllColumnById(wmsCQcMatSample);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsCQcMatSampleService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    
    @RequestMapping("/preview")
    public R preview(@RequestBody List<Map<String,Object>> entityList) throws IOException{
    	
		String werks="";
		String vendor = new String();
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		if(!CollectionUtils.isEmpty(entityList)){
			for(Map<String,Object> map:entityList){
				
				List<WmsSapPlant> plantList = null;
				
				if(map.get("werks")!=null && !map.get("werks").toString().equals("")) {
					// 同工厂代码值校验一次
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
				if(map.get("matnr")!=null && !map.get("matnr").toString().equals("")) {
					if(matnrSb.indexOf(map.get("matnr").toString().concat(";"))>=0) {
						msg+="物料号存在重复数据;";
					}else {
						matnrSb.append(map.get("matnr").toString().concat(";"));
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
				List<WmsSapVendor> vendorList = null;
				if(map.get("lifnr")!=null && !map.get("lifnr").toString().equals("")) {
					if(!vendor.equals(map.get("lifnr").toString())) {
					    vendorList = wmsSapVendorService.selectList(
								new EntityWrapper<WmsSapVendor>().eq("LIFNR",map.get("lifnr").toString()));
					    if(vendorList.size()==0) {
					    	msg+="供应商代码:"+map.get("lifnr").toString()+"在系统中未维护！";
					    }else {
					    	vendor=map.get("lifnr").toString();
							map.put("lifnr", vendorList.get(0).getLifnr());
							map.put("liktx", vendorList.get(0).getName1());
					    }
					}
				} else {
					msg="供应商代码不能为空;";
				}
				map.put("msg", msg);
				msg="";
			}
		}
		return R.ok().put("data", entityList);
    }
    
    @RequestMapping("/upload")
    public R upload(@RequestBody List<WmsCQcMatSampleEntity> entitys){
    	try {
    		wmsCQcMatSampleService.merge(entitys);
    		return R.ok();
    	}catch(Exception e) {
    		return R.error("导入出错");
    	}
    	
//    	return R.ok();
//    	List<WmsCQcMatSampleEntity> addList=new ArrayList<WmsCQcMatSampleEntity>();
//    	List<WmsCQcMatSampleEntity> updateList=new ArrayList<WmsCQcMatSampleEntity>();
//
//    	StringBuffer matnr=new StringBuffer();
//    	for(WmsCQcMatSampleEntity entity : entitys) {
//    		// 封装成 【工厂代码-供应商-物料号,工厂代码-供应商-物料号】
//    		matnr.append(entity.getWerks().concat("-").concat(entity.getLifnr()).concat("-").concat(entity.getMatnr()).concat(","));
//    	}
//    	String [] param=matnr.toString().split(",");
//    	List<Map<String, Object>> list=wmsCQcMatSampleService.validate(Arrays.asList(param));
//    	for(WmsCQcMatSampleEntity entity : entitys) {
//    		String concatStr=entity.getWerks()+"-"+entity.getLifnr()+"-"+entity.getMatnr();
//    		boolean updateFlag=false;
//    		for(Map<String,Object> map : list) {
//    			String werksMatnr=map.get("WERKS_MATNR").toString();
//    			if(concatStr.equals(werksMatnr)) {
//    				entity.setId(Long.valueOf(map.get("ID").toString()));
//    				updateList.add(entity);
//    				updateFlag=true;
//    				break;
//    			}
//    		}
//    		if(!updateFlag) {
//    			addList.add(entity);
//    		}
//    	}
//    	if(addList.size()>0) {
//    		wmsCQcMatSampleService.insertBatch(addList);
//    	}
//    	if(updateList.size()>0) {
//    		wmsCQcMatSampleService.updateBatchById(updateList);
//    	}
  	}

}
