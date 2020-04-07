package com.byd.web.wms.report.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @auther: peng.yang8
 * @data: 2019/12/5
 */
@FeignClient(name = "WMS-SERVICE")
public interface StateLibraryRemote {

    @RequestMapping(value = "/wms-service/report/stateLibrary/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
}
