package com.byd.web.wms.config.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.config.service.WmsCShippingListRemote;

/**
 * 物料物流参数配置表 自制产品入库参数
 *
 * @author cscc tangj
 * @email 
 * @date 2018-09-28 10:30:07
 */
@RestController
@RequestMapping("config/shipping")
public class WmsCShippingListController {
    @Autowired
    private WmsCShippingListRemote wmsCShippingListRemote;
    
    /**
     * 列表	
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        return wmsCShippingListRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}/{itemNo}")
    public R info(@PathVariable("id") String id,@PathVariable("itemNo") String itemNo){
        return wmsCShippingListRemote.info(id,itemNo);
    }
    /**
	 * 
	 * 保存
	 */
    @RequestMapping("/save")
    public R save(@RequestParam HashMap<String, Object> params) {
    	return wmsCShippingListRemote.save(params);
    }
    /**
   	 * 
   	 * 修改
   	 */
   	@RequestMapping("/update")
   	public R update(@RequestParam Map<String, Object> params) {
		return wmsCShippingListRemote.update(params);
   		
   	}
}
