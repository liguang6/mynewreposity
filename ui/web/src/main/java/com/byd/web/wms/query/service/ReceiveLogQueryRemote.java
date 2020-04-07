package com.byd.web.wms.query.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: ren.wei3@byd.com
 * @version 创建时间：2019年11月21日 
 * S收货日志查询
 */

@FeignClient(name = "WMS-SERVICE")
public interface ReceiveLogQueryRemote {

	@RequestMapping(value = "/wms-service/query/receivelogs/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R list(@RequestParam(value = "params") Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/query/receivelogs/listitem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R listitem(@RequestParam(value = "params") Map<String, Object> params);
}
