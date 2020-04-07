package com.byd.wms.business.modules.config.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCControlFlagService;

/**
 * 
 * 控制标识配置
 *
 */
@RestController
@RequestMapping("config/controlflag")
public class WmsCControlFlagController {
	@Autowired
	private WmsCControlFlagService wmsCControlFlagService;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = wmsCControlFlagService.queryPage(params);
		return R.ok().put("page", page);
	}
		
	@RequestMapping("/info")
	public R info(@RequestParam Long id) {
		WmsCControlFlagEntity wmsCControlFlag = wmsCControlFlagService.selectById(id);
		return R.ok().put("wmsCControlFlag", wmsCControlFlag);
	}
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsCControlFlagEntity wmsCControlFlag) {
		 List<WmsCControlFlagEntity> list=wmsCControlFlagService.selectList(
	    		 new EntityWrapper<WmsCControlFlagEntity>()
	    		 .eq("WH_NUMBER", wmsCControlFlag.getWarehouseCode()).eq("CONTROL_FLAG_TYPE", wmsCControlFlag.getControlFlagType())
	             .eq("CONTROL_FLAG", wmsCControlFlag.getControlFlag()).eq("DEL","0"));
	    if(list.size()>0) {
	    	return R.error("控制标识已维护！");
	    }
		wmsCControlFlagService.insert(wmsCControlFlag);
		return R.ok();
	}
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsCControlFlagEntity wmsCControlFlag) {
		wmsCControlFlagService.updateById(wmsCControlFlag);
		return R.ok();
	}
	
	/**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCControlFlagEntity wmsCControlFlag) {
    	
    	wmsCControlFlagService.updateById(wmsCControlFlag);
		return R.ok();
    }
	
}
