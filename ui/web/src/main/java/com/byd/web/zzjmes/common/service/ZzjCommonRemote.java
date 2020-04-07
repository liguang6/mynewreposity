package com.byd.web.zzjmes.common.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "ZZJMES-SERVICE")
public interface ZzjCommonRemote {
	
    /**
     * 计划批次
     */
    @RequestMapping(value = "/zzjmes-service/common/getPlanBatchList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPlanBatchList(@RequestParam(value = "params") Map<String,Object> params);

    @RequestMapping(value = "/zzjmes-service/common/getJTProcess", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getJTProcess(@RequestBody Map<String, Object> params);
	
    @RequestMapping(value = "/zzjmes-service/common/getMachineList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getMachineList(@RequestParam(value = "params") Map<String, Object> params);
    
    @RequestMapping(value = "/zzjmes-service/common/getAssemblyPositionList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getAssemblyPositionList(@RequestParam(value = "params") Map<String, Object> params);
    
    //异常录入
    @RequestMapping(value = "/zzjmes-service/common/productionExceptionManage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R productionExceptionManage(@RequestParam(value = "params") Map<String, Object> params);
    @RequestMapping(value = "/zzjmes-service/common/getProductionExceptionList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getProductionExceptionList(@RequestParam(value = "params") Map<String, Object> params);
}
