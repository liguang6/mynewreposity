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
import com.byd.web.sys.masterdata.entity.DeviceTypeEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午11:06:02 
 * 类说明 
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface DeviceTypeRemote {
	/**
     * 列表
     */
    @RequestMapping(value = "/admin-service/masterdata/deviceType/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
    
    /**
     * 信息
     */
    @RequestMapping(value = "/admin-service/masterdata/deviceType/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
    /**
     * 保存
     */
    @RequestMapping(value = "/admin-service/masterdata/deviceType/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody DeviceTypeEntity device);
    
    /**
     * 修改
     */
    @RequestMapping(value = "/admin-service/masterdata/deviceType/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody DeviceTypeEntity device);
    
    /**
     * 删除
     */
    @RequestMapping(value = "/admin-service/masterdata/deviceType/deleteById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteById(@RequestParam(value="id") long id);
}
