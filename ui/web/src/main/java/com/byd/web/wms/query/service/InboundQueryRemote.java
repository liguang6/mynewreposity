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
 * @version 创建时间：2019年4月11日 下午5:06:09 
 * 类说明 
 */
@FeignClient(name = "WMS-SERVICE")
public interface InboundQueryRemote {
	@RequestMapping(value = "/wms-service/query/inboundQuery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R list(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/query/inboundQuery/detail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R detail(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/query/inboundQuery/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R delete(@RequestParam(value = "params") Map<String, Object> params);
}
