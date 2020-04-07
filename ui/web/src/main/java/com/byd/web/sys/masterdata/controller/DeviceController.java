package com.byd.web.sys.masterdata.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.masterdata.entity.DeviceEntity;
import com.byd.web.sys.masterdata.service.DeviceRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午11:14:35 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/device")
public class DeviceController {
	@Autowired
    private DeviceRemote deviceRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return deviceRemote.list(params);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return deviceRemote.info(id);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DeviceEntity device){
    	return deviceRemote.save(device);
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody DeviceEntity device){
        return deviceRemote.update(device);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	return deviceRemote.deleteById(id);
    }
    
    @RequestMapping("/getDeviceTypeList")
    public R getDeviceTypeList(@RequestParam(value = "code") String code){
    	return deviceRemote.getDeviceTypeList(code);
    }
    
    @RequestMapping("/getMaxMachineCode")
    @ResponseBody
    public R getMaxMachineCode(@RequestParam Map<String,String> map){
    	return deviceRemote.getMaxMachineCode(map);
    }
    
    @RequestMapping("/getDeviceTypeByCode")
    @ResponseBody
    public R getDeviceTypeByCode(@RequestParam Map<String,String> map){
    	return deviceRemote.getDeviceTypeByCode(map);
    }
    
    @RequestMapping("/getDeviceList")
    @ResponseBody
    public R getDeviceList(@RequestParam Map<String,String> map){
    	return deviceRemote.getDeviceList(map);
    }
}
