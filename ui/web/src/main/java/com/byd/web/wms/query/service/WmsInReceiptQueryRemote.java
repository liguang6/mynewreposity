package com.byd.web.wms.query.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 查询收料房的物料操作记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-15 10:12:08
 */

@FeignClient(name = "WMS-SERVICE")
public interface WmsInReceiptQueryRemote {
   
    /**
     * 查询收货单记录
     */
    @RequestMapping(value = "/wms-service/query/receiptQuery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
    /**
     * 打印数据
     */
    @RequestMapping(value = "/wms-service/query/receiptQuery/getLabelData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getLabelData(@RequestParam Map<String, Object> params);
    
    /**
     * 
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/query/stockQuery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R listStock(@RequestParam Map<String, Object> params);
    
}
