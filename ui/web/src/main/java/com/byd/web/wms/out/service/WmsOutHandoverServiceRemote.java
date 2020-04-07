package com.byd.web.wms.out.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 需求交接
 * @author ren.wei3
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsOutHandoverServiceRemote {

	/**
     * 查询
     */
	@RequestMapping(value = "/wms-service/out/handover/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/out/handover/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R handover(@RequestParam(value = "params") Map<String, Object> params);
}
