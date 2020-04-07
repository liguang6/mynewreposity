package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCGrAreaEntity;
import com.byd.web.wms.config.service.WmsCGrAreaRemote;

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
    private WmsCGrAreaRemote wmsCGrAreaRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCGrAreaRemote.list(params);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCGrAreaRemote.info(id);
    }
    
    @RequestMapping("/save")
    public R save(@RequestBody WmsCGrAreaEntity area){
    	area.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	area.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	area.setDel("0");
        return wmsCGrAreaRemote.save(area);
    }

    @RequestMapping("/update")
    public R update(@RequestBody WmsCGrAreaEntity area){
    	area.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
    	area.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCGrAreaRemote.update(area);
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCGrAreaEntity entity = new WmsCGrAreaEntity();
    	entity.setEditor(userUtils.getUser().get("USERNAME").toString()+"："+userUtils.getUser().get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setId(id);
		entity.setDel("X");
    	return wmsCGrAreaRemote.delById(entity);
    }
    

}


	 