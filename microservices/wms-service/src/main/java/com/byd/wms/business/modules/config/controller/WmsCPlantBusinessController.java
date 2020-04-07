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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCPlantBusinessEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsCPlantBusinessService;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 工厂业务类型配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2018-09-29 14:57:55
 */
@RestController
@RequestMapping("config/plantbusiness")
public class WmsCPlantBusinessController {
    @Autowired
    private WmsCPlantBusinessService wmsCPlantBusinessService;
    @Autowired
    private WmsSapPlantService wmsSapPlantService;
    @Autowired
    private WmsSapPlantLgortService wmsSapPlanLgorttService;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCPlantBusinessService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	Map<String,Object> business= wmsCPlantBusinessService.getById(id);
        return R.ok().put("wmsCPlantBusiness", business);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCPlantBusinessEntity wmsCPlantBusiness){
    	
	    List<WmsCPlantBusinessEntity> businessList=wmsCPlantBusinessService.selectList(
				new EntityWrapper<WmsCPlantBusinessEntity>().eq("WERKS",wmsCPlantBusiness.getWerks())
				.eq("BUSINESS_CODE",wmsCPlantBusiness.getBusinessCode()));
	    if(businessList.size()>0) {
	    	return R.error(wmsCPlantBusiness.getWerks()+":"+wmsCPlantBusiness.getBusinessCode()+"记录已维护！");
	    }
	    if(wmsCPlantBusiness.getLgort()!=null && !wmsCPlantBusiness.getLgort().equals("")) {
	    	List<WmsSapPlantLgortEntity> lgortList=wmsSapPlanLgorttService.selectList(
					new EntityWrapper<WmsSapPlantLgortEntity>().eq("WERKS",wmsCPlantBusiness.getWerks())
					.eq("LGORT",wmsCPlantBusiness.getLgort()).eq("DEL","0"));
		    if(lgortList.size()==0) {
		    	return R.error("中转库位未维护！");
		    }
	    }
    	long sortNo=wmsCPlantBusinessService.getMaxSortNo(wmsCPlantBusiness.getWerks());
    	wmsCPlantBusiness.setSortNo(sortNo+1);
    	
        wmsCPlantBusinessService.insert(wmsCPlantBusiness);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCPlantBusinessEntity wmsCPlantBusiness){
    	
        wmsCPlantBusinessService.updateById(wmsCPlantBusiness);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCPlantBusinessEntity entity){
        wmsCPlantBusinessService.delete(new EntityWrapper<WmsCPlantBusinessEntity>().eq("ID",entity.getId()));
        return R.ok();
    }
	
	@RequestMapping("/getWmsBusinessCodeList")
	public R getWmsBusinessCodeList(@RequestBody Map<String, Object> params) {
		List<Map<String,Object>> list= wmsCPlantBusinessService.getWmsBusinessCodeList(params);
		return R.ok().put("businessCodeList", list);
	}
	/**
     * 复制保存
     * @param entitys
     * @return
     */
    @RequestMapping("/saveCopyData")
    public R saveCopyData(@RequestBody List<Map<String, Object>> list){
    	for(Map<String, Object> map : list) {
    		if(map.get("lgort")!=null && !map.get("lgort").toString().equals("")) {
		    	List<WmsSapPlantLgortEntity> lgortList=wmsSapPlanLgorttService.selectList(
						new EntityWrapper<WmsSapPlantLgortEntity>().eq("WERKS",map.get("werks"))
						.eq("LGORT",map.get("lgort")).eq("DEL","0"));
			    if(lgortList.size()==0) {
			    	return R.error(map.get("werks")+"中转库位【"+map.get("lgort")+"】未维护！");
			    }
		    }
    	}
		wmsCPlantBusinessService.saveCopyData(list);
    	return R.ok();
    }
}
