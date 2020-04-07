package com.byd.web.sys.service;

import com.byd.utils.R;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Description  数据权限管理
 * @Author qiu.jiaming1
 * @Date 2019/2/26
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysTableConfigurationRemote {

    /**
     * 分页查询
     */
    @RequestMapping(value = "/admin-service/sys/tableConfiguration/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);

}

