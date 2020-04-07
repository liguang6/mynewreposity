package com.byd.wms.business.modules.config.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCAssemblySortTypeEntity;
import com.byd.wms.business.modules.config.service.WmsCAssemblySortTypeService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年8月2日 下午4:06:05 
 * 类说明 
 */
@RestController
@RequestMapping("config/assemblySortType")
public class WmsCAssemblySortTypeController {
	@Autowired
	private WmsCAssemblySortTypeService wmsCAssemblySortTypeService;
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCAssemblySortTypeService.queryPage(params);

        return R.ok().put("page", page);
    }
    
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType){
    	
    	wmsCAssemblySortTypeService.updateById(wmsCAssemblySortType);
		return R.ok();
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	WmsCAssemblySortTypeEntity wmsCAssemblySortType = wmsCAssemblySortTypeService.selectById(id);

        return R.ok().put("wmsCAssemblySortType", wmsCAssemblySortType);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType){
	    
        List<WmsCAssemblySortTypeEntity> list=wmsCAssemblySortTypeService.selectList(
    			new EntityWrapper<WmsCAssemblySortTypeEntity>().eq("F_WERKS", wmsCAssemblySortType.getfWerks())
    			.eq("JIS_SORT_TYPE", wmsCAssemblySortType.getJisSortType())
    			.eq("SORT_NUM", wmsCAssemblySortType.getSortNum()).eq("DEL","0"));
    	if(list.size()>0) {
    		return R.error("该总装JIS排序类别配置已维护！");
    	}else {
    		wmsCAssemblySortTypeService.insert(wmsCAssemblySortType);
        	return R.ok();
    	}
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCAssemblySortTypeEntity wmsCAssemblySortType){
        
    	wmsCAssemblySortTypeService.updateAllColumnById(wmsCAssemblySortType);//全部更新
        
        return R.ok();
    }

}
