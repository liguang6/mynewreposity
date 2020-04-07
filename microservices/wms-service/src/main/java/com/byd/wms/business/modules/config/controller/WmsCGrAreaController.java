package com.byd.wms.business.modules.config.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCGrAreaEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCGrAreaService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收料房存放区
 * @author tangj
 * @email 
 * @date 2018年08月02号
 */
@RestController
@RequestMapping("config/area")
public class WmsCGrAreaController {
    @Autowired
    private WmsCGrAreaService wmsCGrAreaService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;

    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCGrAreaService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        WmsCGrAreaEntity area = wmsCGrAreaService.selectById(id);
        return R.ok().put("area", area);
    }
    
    @RequestMapping("/save")
    public R save(@RequestBody WmsCGrAreaEntity area){
  
        List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",area.getWerks()).eq("DEL","0"));
	    if(plantList.size()==0) {
	    	return R.error("工厂信息未维护！");
	    }
        List<WmsCGrAreaEntity> list=wmsCGrAreaService.selectList(
    			new EntityWrapper<WmsCGrAreaEntity>().eq("WERKS", area.getWerks())
    			.eq("AREA_CODE", area.getAreaCode()).eq("DEL","0"));
    	if(list.size()>0) {
    		return R.error("收料房存放区记录已维护！");
    	}else {
    		wmsCGrAreaService.insert(area);
        	return R.ok();
    	}
    }

    @RequestMapping("/update")
    public R update(@RequestBody WmsCGrAreaEntity wh){
        
        wmsCGrAreaService.updateById(wh);
        return R.ok();
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCGrAreaEntity entity){
		wmsCGrAreaService.updateById(entity);
		return R.ok();
    }
    

}


	 