package com.byd.web.sys.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * @Description  数据权限管理
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysColumnConfigurationRemote {
	
    @RequestMapping(value = "/admin-service/sys/columnConfiguration/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);
    
    @RequestMapping(value = "/admin-service/sys/columnConfiguration/usergridlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R usergridlist(@RequestParam(value="params") Map<String, Object> params);
    
    @RequestMapping(value = "/admin-service/sys/columnConfiguration/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestParam(value="params") Map<String, Object> params);
    
    @RequestMapping(value = "/admin-service/sys/columnConfiguration/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestParam(value="params") Map<String, Object> params) ;
    
    @RequestMapping(value = "/admin-service/sys/columnConfiguration/userSave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R userSave(@RequestParam(value="params") Map<String, Object> params) ;

    @RequestMapping(value = "/admin-service/sys/columnConfiguration/trans", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R trans(@RequestParam(value="params") Map<String, Object> params) ;
}

