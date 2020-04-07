package com.byd.web.account.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * PDA一步联动收货
 * @author ren.wei3
 *
 */

@FeignClient(name = "WMS-SERVICE")
public interface PostAccountPadRemote {

	@RequestMapping(value = "/wms-service/acPda/task/getwhTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getwhTask(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/wms-service/acPda/task/posttingAc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R posttingAc(@RequestBody Map<String, Object> params);

}
