package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCAssemblyLogisticsEntity;
import com.byd.web.wms.config.entity.WmsCKeyPartsEntity;
import com.byd.web.wms.config.service.WmsCAssemblyLogisticsRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年7月26日 下午3:40:46 
 * 类说明 
 */
@RestController
@RequestMapping("config/assemblylogistics")
public class WmsCAssemblyLogisticsController {
	@Autowired
	private WmsCAssemblyLogisticsRemote wmsCAssemblyLogisticsRemote;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCAssemblyLogisticsRemote.list(params);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCAssemblyLogisticsRemote.info(id);
    }
    
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCAssemblyLogisticsEntity entity = new WmsCAssemblyLogisticsEntity();
        Map<String,Object> user = userUtils.getUser();
        entity.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setId(id);
		entity.setDel("X");
		return wmsCAssemblyLogisticsRemote.delById(entity);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics){
        Map<String,Object> user = userUtils.getUser();
        wmsCAssemblyLogistics.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCAssemblyLogistics.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        wmsCAssemblyLogistics.setDel("0");
    	return wmsCAssemblyLogisticsRemote.save(wmsCAssemblyLogistics);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCAssemblyLogisticsEntity wmsCAssemblyLogistics){
        Map<String,Object> user = userUtils.getUser();
        wmsCAssemblyLogistics.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCAssemblyLogistics.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCAssemblyLogisticsRemote.update(wmsCAssemblyLogistics);
    }
    
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/batchSave")
	@ResponseBody
	public R batchSave(@RequestParam Map<String, Object> params) throws IllegalAccessException{
        Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		return wmsCAssemblyLogisticsRemote.batchSave(params);
	}
	/**
	 * 粘贴数据校验
	 * @param excel
	 */
	@RequestMapping("/checkPasteData")
	@ResponseBody
	public R checkPasteData(@RequestParam Map<String,Object> params){
		return wmsCAssemblyLogisticsRemote.checkPasteData(params);
	}
}
