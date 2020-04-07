package com.byd.wms.business.modules.config.controller;


import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCSapUserEntity;
import com.byd.wms.business.modules.config.service.WmsCSapUserService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月29日 上午11:26:44 
 * 类说明 
 */
@RestController
@RequestMapping("/config/CSapUser")
public class WmsCSapUserController {
	@Autowired
	WmsCSapUserService wmscSapUserService;
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmscSapUserService.queryPage(params);	
		return R.ok().put("page", page);
	}
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
    	WmsCSapUserEntity csapuser = wmscSapUserService.selectById(id);
        return R.ok().put("csapuser", csapuser);
    }
	@RequestMapping("/update")
	public R update(@RequestBody WmsCSapUserEntity entity){
		//validate
		
		wmscSapUserService.updateById(entity);
		return R.ok();
	}
	
	@RequestMapping("/save")
	public R add(@RequestBody WmsCSapUserEntity entity){
		
		wmscSapUserService.insert(entity);
		return R.ok();
	}
	
	@RequestMapping("/dels")
	public R deletes(String ids){
		if(ids == null){
			return R.error("参数错误");
		}
		String[] id=ids.split(",");
		for(int i=0;i<id.length;i++){
			WmsCSapUserEntity entity = new WmsCSapUserEntity();
			entity.setId(Long.parseLong(id[i]));
			entity.setDel("1");
			wmscSapUserService.updateById(entity);
		}
		return R.ok();
	}
}
