package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.web.wms.config.entity.WmsCQcPlantEntity;
import com.byd.web.wms.config.service.WmsCQcPlantRemote;
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
    private WmsCQcPlantRemote wmsCQcPlantRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsCQcPlantRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCQcPlantRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcPlantEntity wmsCQcPlant){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCQcPlant.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcPlant.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	wmsCQcPlant.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcPlant.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	
        wmsCQcPlantRemote.save(wmsCQcPlant);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcPlantEntity wmsCQcPlant){
        ValidatorUtils.validateEntity(wmsCQcPlant);
        Map<String,Object> user = userUtils.getUser();
    	wmsCQcPlant.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcPlant.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCQcPlantRemote.update(wmsCQcPlant);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
    	return wmsCQcPlantRemote.delete(ids);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	Long[] ids = new Long[1];
    	ids[0] = id;
        return wmsCQcPlantRemote.delete(ids);
    }
    
    /**
     * 导入预览 excel 2 java beans
     * @param excel
     * @return
     * @throws IOException
     */
    @RequestMapping("/preview")
    public R preview(MultipartFile excel) throws IOException{
    	List<String[]> sheet = ExcelReader.readExcel(excel);
    	List<WmsCQcPlantEntity> entitys = new ArrayList<WmsCQcPlantEntity>();
    	for(String[] row:sheet){
    		//转换
    		WmsCQcPlantEntity entity = new WmsCQcPlantEntity();
    		entity.setMsgs(new ArrayList<String>());
    		entity.setWerks(row[0]);
    		entity.setBusinessName(row[1]);
    		entity.setTestFlag(row[2]);
    		entity.setStartDate(row[3]);
    		entity.setEndDate(row[4]);
    		entity.setMemo(row[5]);
    		entitys.add(entity);
    	}
    	
    	return wmsCQcPlantRemote.previewExcel(entitys);
    }
    
    /**
     * 保存导入信息
     * @param entityLists
     * @return
     */
    @RequestMapping("/import")
    public R imports(@RequestBody List<WmsCQcPlantEntity> entityList){
    	return wmsCQcPlantRemote.imports(entityList);
    }

}
