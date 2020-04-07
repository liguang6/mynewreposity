package com.byd.web.out.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * PDA-下架拣配
 * @author yang.bintao
 *
 */

@FeignClient(name = "WMS-SERVICE")
public interface OutPdaRemote {	
	@RequestMapping(value = "/wms-service/outPda/xiaJiaJianPei", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R tuiJian(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/outPda/scanLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R scanLabel(@RequestParam(value = "params") Map<String, Object> params);
		
	@RequestMapping(value = "/wms-service/outPda/saveXiaJiaXinXi", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveXiaJiaXinXi(@RequestBody Map<String, Object> params);
}
