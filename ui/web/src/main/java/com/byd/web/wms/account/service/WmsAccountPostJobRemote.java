package com.byd.web.wms.account.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.R;

/**
 * 过账失败任务处理，包含失败任务再次过账，失败任务删除等功能
 * @author (changsha) thw
 * @date 2019-5-16
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsAccountPostJobRemote {
	@RequestMapping(value = "/wms-service/account/wmsAccountPostJob/listPostJob", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R listPostJob(@RequestBody Map<String, Object> params);
	
	
    /**
     * 失败任务过账
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/account/wmsAccountPostJob/post", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R post(@RequestBody Map<String, Object> params);
    
    /**
     * 失败任务关闭（删除过账任务）
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/account/wmsAccountPostJob/closePostJob", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R closePostJob(@RequestBody Map<String, Object> params);
}
