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
import com.byd.wms.business.modules.config.entity.WmsCMatUsingEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCMatUsingService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 物料上线配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2018-08-15 13:57:57
 */
@RestController
@RequestMapping("config/matusing")
public class WmsCMatUsingController {
    @Autowired
    private WmsCMatUsingService wmsCMatUsingService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatUsingService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCMatUsingEntity wmsCMatUsing = wmsCMatUsingService.selectById(id);

        return R.ok().put("wmsCMatUsing", wmsCMatUsing);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatUsingEntity wmsCMatUsing){
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",wmsCMatUsing.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂信息未维护！");
	    }
	    List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS", wmsCMatUsing.getWerks()).
                eq("MATNR", wmsCMatUsing.getMatnr()));
    	if(materialList.size()==0) {
    		return R.error(wmsCMatUsing.getWerks()+"未维护该SAP物料!");
    	}
    	List<WmsCMatUsingEntity> urgentList=wmsCMatUsingService.selectList(new EntityWrapper<WmsCMatUsingEntity>().eq("WERKS", wmsCMatUsing.getWerks()).
                eq("MATNR", wmsCMatUsing.getMatnr()));
    	if(urgentList.size()>0) {
    		return R.error(wmsCMatUsing.getWerks()+"已维护该紧急物料号!");
    	}
    	
    	wmsCMatUsingService.insert(wmsCMatUsing);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatUsingEntity wmsCMatUsing){
    	
    	wmsCMatUsingService.updateById(wmsCMatUsing);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCMatUsingEntity wmsCMatUsing){
    	
		wmsCMatUsingService.updateById(wmsCMatUsing);
		return R.ok();
    }
    @RequestMapping("/upload")
	public R upload(@RequestBody List<WmsCMatUsingEntity> entityList) throws IOException{
    	List<WmsCMatUsingEntity> addList=new ArrayList<WmsCMatUsingEntity>();
    	List<WmsCMatUsingEntity> updateList=new ArrayList<WmsCMatUsingEntity>();

    	StringBuffer matnr=new StringBuffer();
    	for(WmsCMatUsingEntity entity : entityList) {
    		// 封装成 【工厂代码-物料号,工厂代码-物料号】
    		matnr.append(entity.getWerks().concat("-").concat(entity.getMatnr()).concat(","));
    	}
    	String [] param=matnr.toString().split(",");
    	List<Map<String,Object>> list=wmsCMatUsingService.validate(Arrays.asList(param));
    	for(WmsCMatUsingEntity entity : entityList) {
    		String useFlag=entity.getUseFlag().equals("是") ? "X" : "0";
    		entity.setUseFlag(useFlag);
    		String concatStr=entity.getWerks()+"-"+entity.getMatnr();
    		boolean updateFlag=false;
    		for(Map<String,Object> map : list) {
    			String uniqueStr=map.get("UNIQUE_STR").toString();
    			if(concatStr.equals(uniqueStr)) {
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
    		wmsCMatUsingService.insertBatch(addList);
    	}
    	if(updateList.size()>0) {
    		wmsCMatUsingService.updateBatchById(updateList);
    	}
		return R.ok();
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<Map<String, Object>> entityList) throws IOException{
		String werks="";
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		if(!CollectionUtils.isEmpty(entityList)){
			for(Map<String, Object> map:entityList){
				if(map.get("werks")!=null && !map.get("werks").toString().equals("")) {
					if(!werks.equals(map.get("werks").toString())) {
						List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
								new EntityWrapper<WmsSapPlant>().eq("WERKS", map.get("werks").toString()).eq("DEL","0"));
					    if(plantList.size()==0) {
					    	msg="工厂代码未维护;";
					    }else {
					    	werks=map.get("werks").toString();
					    }
					}
				} else {
					msg="工厂代码不能为空;";
				}
				map.put("matnr", map.get("matnr"));
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
				map.put("msg", msg);
				msg="";
			}
		}
		return R.ok().put("data", entityList);
	}  
}
