package com.byd.web.wms.report.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
* @author 作者 : ren.wei3@byd.com 
* @version 创建时间：2019年6月4日 下午3:25:39 
* 
*/

@FeignClient(name = "WMS-SERVICE")
public interface QueryComponentRemote {


    /**
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/report/InAndOutStockQtyByDay/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R inAndOutStockQtyByDayListStock(@RequestParam Map<String, Object> params);



    /**
     * 
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/report/stockCompare/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R listStock(@RequestParam Map<String, Object> params);
    
    /**
     * 
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/report/wmsReportBarcodeLog/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R wmsReportBarcodeLogList(@RequestParam Map<String, Object> params);
}
