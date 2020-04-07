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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;


@RestController
@RequestMapping("/config/sapPlantLgort")
public class WmsSapPlantLgortController {
	
	@Autowired
	WmsSapPlantLgortService lgortService;
	@Autowired
	WmsSapPlantService wmsSapPlantService;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String,Object> params){
		PageUtils page = lgortService.queryPage(params);
	
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/save")
	public R save(@RequestBody WmsSapPlantLgortEntity entity){
    	List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",entity.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂代码未维护！");
	    }
	    List<WmsSapPlantLgortEntity> list=lgortService.selectList(new EntityWrapper<WmsSapPlantLgortEntity>()
	    		.eq("WERKS",entity.getWerks()).eq("LGORT", entity.getLgort())
	    		.eq("DEL","0"));
	    if(list.size()>0) {
	    	return R.error("工厂库存地点已维护！");
	    }
		
		lgortService.insertOrUpdateAllColumn(entity);
		return R.ok();
	}
	@RequestMapping("/update")
	public R update(@RequestBody WmsSapPlantLgortEntity entity){
    	
		lgortService.updateAllColumnById(entity);
		return R.ok();
	}
	@RequestMapping("/get")
	public R getOne(Long id){
		WmsSapPlantLgortEntity entity = lgortService.selectById(id);
		return R.ok().put("data", entity);
	}
	
	@RequestMapping("/delFlag")
	public R delFlag(Long id){
		WmsSapPlantLgortEntity entity  = lgortService.selectById(id);
		if(entity != null){
			entity.setDel("X");
			lgortService.updateById(entity);
		}else{
			return R.error("记录未找到");
		}
		return R.ok();
	}
	
	@RequestMapping("/preview")
	public R preview(@RequestBody List<WmsSapPlantLgortEntity> listEntity) throws IOException{
		String werks="";
		String msg="";
		int index=1;
		if(!CollectionUtils.isEmpty(listEntity)){
			
			for(WmsSapPlantLgortEntity entity:listEntity){
				
				if(StringUtils.isBlank(entity.getWerks()==null ? "" : entity.getWerks())){
					msg+="工厂代码不能为空";
				}else {
					if(!werks.equals(entity.getWerks())) {
						werks=entity.getWerks();
						Map<String, Object> params=new HashMap<String, Object>();
						params.put("werks", entity.getWerks());
						List<WmsSapPlant> plantList=wmsSapPlantService.selectByMap(params);
						if(plantList.size()==0){
							msg+="工厂代码不存在";
						}
					}
				}
				if(StringUtils.isBlank(entity.getLgort()==null ? "" : entity.getLgort())){
					msg+="库存地点代码不能为空";
				}
				if(StringUtils.isBlank(entity.getLgortName()==null ? "" : entity.getLgortName())){
					msg+="库存地点名称不能为空";
				}
				entity.setMsg(msg);
				entity.setRowNo(index+"");
				index++;
			}
		}
		return R.ok().put("data", listEntity);
	}
	
	@RequestMapping("/importWmsSapPlantLgortEntity")
	public R importWmsSapPlantLgortEntity(@RequestBody List<WmsSapPlantLgortEntity> entityList){

		List<WmsSapPlantLgortEntity> addList=new ArrayList<WmsSapPlantLgortEntity>();
    	List<WmsSapPlantLgortEntity> updateList=new ArrayList<WmsSapPlantLgortEntity>();

    	StringBuffer s=new StringBuffer();
    	for(WmsSapPlantLgortEntity entity : entityList) {
    		// 封装成 【工厂代码-库存地点代码,工厂代码-库存地点代码】
    		s.append(entity.getWerks().concat("-").concat(entity.getLgort()).concat(","));
    	}
    	String [] param=s.toString().split(",");
    	List<Map<String, Object>> list=lgortService.validate(Arrays.asList(param));
    	for(WmsSapPlantLgortEntity entity : entityList) {
    		String concatStr=entity.getWerks()+"-"+entity.getLgort();
    		boolean updateFlag=false;
    		for(Map<String,Object> map : list) {
    			String werksLgort=map.get("WERKS_LGORT").toString();
    			if(concatStr.equals(werksLgort)) {
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
    		lgortService.insertBatch(addList);
    	}
    	if(updateList.size()>0) {
    		lgortService.updateBatchById(updateList);
    	}
	    return R.ok();
	}

}
