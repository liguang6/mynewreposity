package com.byd.web.wms.out.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 需求拣配
 * @author ren.wei3
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsOutResersalPickingServiceRemote {

	/**
	 * 查询
	 */
	@RequestMapping(value = "/wms-service/out/resersalPicking/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam(value = "params") Map<String, Object> params);

	/**
	 * 更新
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/wms-service/out/resersalPicking/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestParam Map<String, Object> params);
}