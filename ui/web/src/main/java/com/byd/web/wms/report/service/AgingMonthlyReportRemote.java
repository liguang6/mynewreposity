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
* @version 2019年11月28日 
* 
*/

@FeignClient(name = "WMS-SERVICE")
public interface AgingMonthlyReportRemote {


    /** 
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/report/agingMonthlyReport/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryAgingMonthlyPage(@RequestParam Map<String, Object> params);
}
