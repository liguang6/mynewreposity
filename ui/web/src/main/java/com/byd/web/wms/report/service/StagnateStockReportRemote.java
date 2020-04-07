package com.byd.web.wms.report.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
* @author 作者 : chen.yafei1
* @version 创建时间：2019年11月27日 下午3:25:39 
* 
*/

@FeignClient(name = "WMS-SERVICE")
public interface StagnateStockReportRemote {


    /** 
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/report/stagnateStockReport/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R querystagenateStockPage(@RequestParam Map<String, Object> params);
}
