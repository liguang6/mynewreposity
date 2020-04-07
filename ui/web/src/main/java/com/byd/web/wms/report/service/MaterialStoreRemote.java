package com.byd.web.wms.report.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/11/25
 */

@FeignClient(name = "WMS-SERVICE")
public interface MaterialStoreRemote {

    @RequestMapping(value = "/wms-service/report/materialStore/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
}
