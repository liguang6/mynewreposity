package com.byd.web.wms.query.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 查询SAP凭证记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-21 10:12:08
 */

@FeignClient(name = "WMS-SERVICE")
public interface SapDocQueryRemote {
   
    /**
     * 查询SAP凭证记录
     */
    @RequestMapping(value = "/wms-service/query/sapDocQuery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
}
