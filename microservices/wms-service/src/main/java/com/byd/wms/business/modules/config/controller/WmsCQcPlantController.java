package com.byd.wms.business.modules.config.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UsefulMethods;
import com.byd.wms.business.modules.config.entity.WmsCQcPlantEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCQcPlantService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

/**
 * 工厂质检配置表
 *
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@RestController
@RequestMapping("config/wmscqcplant")
public class WmsCQcPlantController {
    @Autowired
    private WmsCQcPlantService wmsCQcPlantService;
    
    @Autowired
    private WmsSapPlantService wmsSapPlantService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCQcPlantService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCQcPlantEntity wmsCQcPlant = wmsCQcPlantService.selectById(id);

        return R.ok().put("data", wmsCQcPlant);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcPlantEntity wmsCQcPlant){
 
	    List<WmsCQcPlantEntity> qcPlantList=wmsCQcPlantService.selectList(
				new EntityWrapper<WmsCQcPlantEntity>().eq("WERKS",wmsCQcPlant.getWerks())
				.eq("BUSINESS_NAME",wmsCQcPlant.getBusinessName()).eq("DEL","0"));
	    if(qcPlantList.size()>0) {
	    	return R.error(wmsCQcPlant.getWerks()+"该业务类型的质检配置已维护！");
	    }
        wmsCQcPlantService.insert(wmsCQcPlant);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcPlantEntity wmsCQcPlant){
       
        wmsCQcPlantService.updateAllColumnById(wmsCQcPlant);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
    	wmsCQcPlantService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 导入预览 excel 2 java beans
     * @param excel
     * @return
     * @throws IOException
     */
    @RequestMapping("/preview")
    public R preview(@RequestBody List<WmsCQcPlantEntity> entityList) throws IOException{
    	
    	
    	//校验
    	for(WmsCQcPlantEntity entity:entityList){
    		//检查开始时间和结束时间
			try {
				DateUtils.stringToDate(entity.getStartDate(),DateUtils.DATE_PATTERN);
				DateUtils.stringToDate(entity.getEndDate(),DateUtils.DATE_PATTERN);
			} catch (Exception e) {
				entity.getMsgs().add("时间格式错误（例：2018-01-01）");
				e.printStackTrace();
			}
			//检查工厂代码是否存在,且唯一
			int count =wmsSapPlantService.selectCount(new EntityWrapper<WmsSapPlant>().eq("werks", entity.getWerks()));
    		if(count !=1){
    			entity.getMsgs().add("工厂代码不存在");
    		}
    		//校验是否存在重复的工厂代码
    		try {
				List<Integer> repeatColumes = UsefulMethods.checkCloumRepeat("werks", entity.getWerks(), entityList.indexOf(entity), entityList, WmsCQcPlantEntity.class);
			    if(!CollectionUtils.isEmpty(repeatColumes)){
			    	entity.getMsgs().add("工厂代码与第 "+UsefulMethods.toNumbers(repeatColumes)+" 行重复");
			    }
    		}catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	return R.ok().put("data", entityList);
    }
    
    /**
     * 保存导入信息
     * @param entityLists
     * @return
     */
    @RequestMapping("/imports")
    public R imports(@RequestBody List<WmsCQcPlantEntity> entityList){
    	wmsCQcPlantService.insertBatch(entityList);
    	return R.ok();
    }

}
