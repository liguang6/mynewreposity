package com.byd.web.sys.masterdata.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.masterdata.entity.DeviceTypeEntity;
import com.byd.web.sys.masterdata.service.DeviceTypeRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午11:14:35 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/deviceType")
public class DeviceTypeController {
	@Autowired
    private DeviceTypeRemote deviceTypeRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return deviceTypeRemote.list(params);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return deviceTypeRemote.info(id);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DeviceTypeEntity deviceType){
    	return deviceTypeRemote.save(deviceType);
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody DeviceTypeEntity deviceType){
        return deviceTypeRemote.update(deviceType);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	return deviceTypeRemote.deleteById(id);
    }
}
