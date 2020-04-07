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
import com.byd.web.sys.masterdata.entity.BusTypeEntity;


/**
 * 车型表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-05 15:56:12
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface BusTypeRemote {

    /**
     * 列表
     */
    @RequestMapping(value = "/admin-service/masterdata/bustype/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);


    /**
     * 信息
     */
    @RequestMapping(value = "/admin-service/masterdata/bustype/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);

    /**
     * 保存
     */
    @RequestMapping(value = "/admin-service/masterdata/bustype/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody BusTypeEntity settingBusType);

    /**
     * 修改
     */
    @RequestMapping(value = "/admin-service/masterdata/bustype/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody BusTypeEntity settingBusType);

    /**
     * 删除
     */
    @RequestMapping(value = "/admin-service/masterdata/bustype/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);

}
