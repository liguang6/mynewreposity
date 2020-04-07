package com.byd.web.sys.masterdata.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.sys.masterdata.entity.DeviceEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午11:06:02 
 * 类说明 
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface DeviceRemote {
	/**
     * 列表
     */
    @RequestMapping(value = "/admin-service/masterdata/device/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
    
    /**
     * 信息
     */
    @RequestMapping(value = "/admin-service/masterdata/device/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
    /**
     * 保存
     */
    @RequestMapping(value = "/admin-service/masterdata/device/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody DeviceEntity device);
    
    /**
     * 修改
     */
    @RequestMapping(value = "/admin-service/masterdata/device/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody DeviceEntity device);
    
    /**
     * 删除
     */
    @RequestMapping(value = "/admin-service/masterdata/device/deleteById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteById(@RequestParam(value="id") long id);
    
    @RequestMapping(value = "/admin-service/masterdata/device/getDeviceTypeList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getDeviceTypeList(@RequestParam(value = "code") String code);
    
    @RequestMapping(value = "/admin-service/masterdata/device/getMaxMachineCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getMaxMachineCode(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/device/getDeviceTypeByCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getDeviceTypeByCode(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/device/getDeviceList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getDeviceList(@RequestParam(value="map") Map<String,String> map);
    
}
