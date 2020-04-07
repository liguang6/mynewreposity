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
import com.byd.wms.business.modules.config.entity.WmsCMatLtSampleEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCMatLtSampleService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 物料物流参数配置表 自制产品入库参数
 *
 * @author cscc tangj
 * @email 
 * @date 2018-09-28 10:30:07
 */
@RestController
@RequestMapping("config/sample")
public class WmsCMatLtSampleController {
    @Autowired
    private WmsCMatLtSampleService wmsCMatLtSampleService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCMatLtSampleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCMatLtSampleEntity sample = wmsCMatLtSampleService.selectById(id);

        return R.ok().put("sample", sample);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCMatLtSampleEntity wmsCMatLtSample){
    	
	    List<WmsSapMaterialEntity> materialList=wmsSapMaterialService.selectList(new EntityWrapper<WmsSapMaterialEntity>().eq("WERKS", wmsCMatLtSample.getWerks()).
                eq("MATNR", wmsCMatLtSample.getMatnr()));
    	if(materialList.size()==0) {
    		return R.error(wmsCMatLtSample.getWerks()+"未维护该SAP物料!");
    	}
    	List<WmsCMatLtSampleEntity> urgentList=wmsCMatLtSampleService.selectList(new EntityWrapper<WmsCMatLtSampleEntity>().eq("WERKS", wmsCMatLtSample.getWerks()).
                eq("MATNR", wmsCMatLtSample.getMatnr()).eq("DEL", "0"));
    	if(urgentList.size()>0) {
    		return R.error(wmsCMatLtSample.getWerks()+":"+wmsCMatLtSample.getMatnr()+"已维护!");
    	}
    	
        wmsCMatLtSampleService.insert(wmsCMatLtSample);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCMatLtSampleEntity wmsCMatLtSample){
    	
        wmsCMatLtSampleService.updateAllColumnById(wmsCMatLtSample);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsCMatLtSampleService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCMatLtSampleEntity wmsCMatLtSample){
    	
		wmsCMatLtSampleService.updateById(wmsCMatLtSample);
		return R.ok();
    }
    @RequestMapping("/upload")
	public R upload(@RequestBody List<Map<String,Object>> entityList) throws IOException{
    	wmsCMatLtSampleService.merge(entityList);
		return R.ok();
	}
	
	@RequestMapping("/preview")
	public R previewExcel(@RequestBody List<Map<String,Object>> entityList) throws IOException{
		
		StringBuffer matnrSb=new StringBuffer();
		String msg="";
		if(!CollectionUtils.isEmpty(entityList)){
			for(Map<String,Object> map:entityList){
				
				if(map.get("werks")==null ||( map.get("werks")!=null && map.get("werks").toString().equals(""))) {
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
				map.put("msg", msg);
				msg="";
			}
		}
		return R.ok().put("data", entityList);
	}  
}
