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
import com.byd.web.wms.config.entity.WmsCAssemblyLogisticsEntity;
import com.byd.web.wms.config.entity.WmsCAssemblySortTypeEntity;
import com.byd.web.wms.config.service.WmsCAssemblyLogisticsRemote;
import com.byd.web.wms.config.service.WmsCAssemblySortTypeRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年8月2日 下午4:24:39 
 * 类说明 
 */
@RestController
@RequestMapping("config/assemblySortType")
public class WmsCAssemblySortTypeController {
	@Autowired
	private WmsCAssemblySortTypeRemote wmsCAssemblySortTypeRemote;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCAssemblySortTypeRemote.list(params);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCAssemblySortTypeRemote.info(id);
    }
    
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCAssemblySortTypeEntity entity = new WmsCAssemblySortTypeEntity();
        Map<String,Object> user = userUtils.getUser();
        entity.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setId(id);
		entity.setDel("X");
		return wmsCAssemblySortTypeRemote.delById(entity);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType){
        Map<String,Object> user = userUtils.getUser();
        wmsCAssemblySortType.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCAssemblySortType.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCAssemblySortType.setDel("0");
    	return wmsCAssemblySortTypeRemote.save(wmsCAssemblySortType);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType){
        Map<String,Object> user = userUtils.getUser();
        wmsCAssemblySortType.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCAssemblySortType.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCAssemblySortTypeRemote.update(wmsCAssemblySortType);
    }
}
