package com.byd.web.sys.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysDataPermissionEntity;

/**
 * @ClassName SysDataPermissionController
 * @Description  数据权限管理
 * @Author qiu.jiaming1
 * @Date 2019/2/26
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysDataPermissionRemote {
    /**
     * 分页查询
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value="id") Long id);

    /**
     * 单条记录查询By menuID
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/getAuthFields", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getAuthFields(@RequestParam Map<String, Object> params);

    /**
     * 保存
     * @param storage
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody SysDataPermissionEntity storage);

    /**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody SysDataPermissionEntity wh);

    /**
     * 软删BY ID
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value="id") Long id);

    /**
     * 用户权限保存
     * @param storage
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/userAuthSave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R userAuthSave(@RequestBody Map<String, Object> params);
    
    /**
     * 分页查询
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/userAuthlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R userAuthlist(@RequestParam Map<String, Object> params);
    
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/admin-service/sys/dataPermission/userAuthInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R userAuthInfo(@RequestParam(value = "id") Long id);
	
	/**
     * 修改
     * @param wh
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/dataPermission/userAuthUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R userAuthUpdate(@RequestBody Map<String, Object> params);
    
    /**
     * 删除By ID
     */
	@RequestMapping(value = "/admin-service/sys/dataPermission/userAuthDelById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R userAuthDelById(@RequestParam(value = "params") Map<String, Object> params);
}
