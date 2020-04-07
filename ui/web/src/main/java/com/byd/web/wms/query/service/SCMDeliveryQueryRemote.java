package com.byd.web.wms.query.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年3月6日 上午10:10:39 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface SCMDeliveryQueryRemote {
	
    @RequestMapping(value = "/wms-service/query/scmquery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R list(@RequestParam(value = "params") Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/query/scmquery/listitem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R listitem(@RequestParam(value = "params") Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/query/scmquery/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R update(@RequestParam(value = "params") Map<String, Object> params);
}
