package com.byd.web.wms.query.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 查询发料包装
 * @author qjm
 * @email
 * @date 2019-06-05
 */


@FeignClient(name = "WMS-SERVICE")
public interface WmsDocPackingQueryRemote {
   
    /**
     * 查询发料包装记录
     */
    @RequestMapping(value = "/wms-service/query/docPackingQuery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
}
