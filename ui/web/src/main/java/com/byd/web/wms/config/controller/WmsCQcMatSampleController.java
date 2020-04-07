package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.byd.web.wms.config.entity.WmsCQcMatSampleEntity;
import com.byd.web.wms.config.service.WmsCQcMatSampleRemote;

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
    private WmsCQcMatSampleRemote wmsCQcMatSampleRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsCQcMatSampleRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsCQcMatSampleRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcMatSampleEntity wmsCQcMatSample){
    	Map<String,Object> user = userUtils.getUser();
    	wmsCQcMatSample.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcMatSample.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	wmsCQcMatSample.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	wmsCQcMatSample.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	return wmsCQcMatSampleRemote.save(wmsCQcMatSample);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcMatSampleEntity wmsCQcMatSample){
    	Map<String,Object> user = userUtils.getUser();
        ValidatorUtils.validateEntity(wmsCQcMatSample);
        wmsCQcMatSample.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCQcMatSample.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCQcMatSampleRemote.update(wmsCQcMatSample);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsCQcMatSampleRemote.delete(ids);

        return R.ok();
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
        wmsCQcMatSampleRemote.delete(ids);
        return R.ok();
    }
    
    @RequestMapping("/preview")
    public R preview(MultipartFile excel) throws IOException{
    	List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		int index=0;
		Map<String,Object> user = userUtils.getUser();
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", ++index);
				map.put("werks", row[0]);
				map.put("matnr", row[1]);
				map.put("lifnr", row[2]);
				map.put("sampling", row[3]);
				map.put("minSample", row[4]);
				map.put("maxSample", row[5]);
				map.put("unpacking", row[6]);
				map.put("minUnpacking", row[7]);
				map.put("maxUnpacking", row[8]);
				map.put("memo", row[9]);
				map.put("del", "0");
				map.put("creator", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("createDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(map);
			}
		}
    		
		return wmsCQcMatSampleRemote.preview(entityList);
    }
    
    @RequestMapping("/import")
    public R importExcel(@RequestBody List<Map<String,Object>> entitys){
    	return wmsCQcMatSampleRemote.upload(entitys);
    
  	}

}
