package com.byd.web.sys.service;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysDataPermissionObjectEntity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

/**
 * @Description  数据权限对象管理
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysDataPermissionObjectRemote {

    /**
     * 分页查询
     */
    @RequestMapping(value = "/admin-service/sys/dataPermissionObject/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
    @RequestMapping(value = "/admin-service/sys/dataPermissionObject/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value="id") Long id);

    /**
     * 保存
     * @param storage
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/dataPermissionObject/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody SysDataPermissionObjectEntity storage);

    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/dataPermissionObject/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody SysDataPermissionObjectEntity wh);

    /**
     * 软删BY ID
     */
    @RequestMapping(value = "/admin-service/sys/dataPermissionObject/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value="id") Long id);


}
