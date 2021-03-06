package com.byd.web.wms.report.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
* @author chen.yafei1
* @version 2019年12月3日 
* 
*/

@FeignClient(name = "WMS-SERVICE")
public interface StoPutAwayReportRemote {


    /** 
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/report/stoPutAwayReport/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryStoPutAwayPage(@RequestParam Map<String, Object> params);
}
