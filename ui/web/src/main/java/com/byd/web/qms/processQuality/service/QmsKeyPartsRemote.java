package com.byd.web.qms.processQuality.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.byd.utils.R;

@FeignClient(name = "QMS-SERVICE")
public interface QmsKeyPartsRemote {
	/**
     * 关键零部件追踪分页查询抬头数据
     */
	@RequestMapping(value = "/qms-service/processQuality/keyParts/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryPage(@RequestBody Map<String, Object> params);
	
	/**
     * 关键零部件追踪查询明细数据
     */
	@RequestMapping(value = "/qms-service/processQuality/keyParts/queryDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryDetail(@RequestBody Map<String, Object> params);
	
	/**
     * 查询订单配置
     */
	@RequestMapping(value = "/qms-service/processQuality/keyParts/queryOrderConfigList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryOrderConfigList(@RequestBody Map<String, Object> params);
	
	/**
     * 查询车间
     */
	@RequestMapping(value = "/qms-service/processQuality/keyParts/queryWorkshopList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryWorkshopList(@RequestBody Map<String, Object> params);
	
	/**
     * 查询线别
     */
	@RequestMapping(value = "/qms-service/processQuality/keyParts/queryLineNameList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryLineNameList(@RequestBody Map<String, Object> params);
	
	/**
     * 关键零部件追溯分页查询抬头数据
     */
	@RequestMapping(value = "/qms-service/processQuality/keyParts/queryTraceBackPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryTraceBackPage(@RequestBody Map<String, Object> params);
	
}
