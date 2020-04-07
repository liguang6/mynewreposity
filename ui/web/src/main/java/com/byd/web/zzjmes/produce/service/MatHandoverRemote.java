package com.byd.web.zzjmes.produce.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 车间内交接/车间供货
 * @author cscc tangj
 * @email 
 * @date 2019-09-23 16:16:08
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface MatHandoverRemote {
	
	@RequestMapping(value = "/zzjmes-service/matHandover/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestParam Map<String, Object> paramMap);
    
	@RequestMapping(value = "/zzjmes-service/matHandover/querySupplyPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R querySupplyPage(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/matHandover/getSupplyDetailList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSupplyDetailList(@RequestParam Map<String, Object> paramMap);
    /**
   	 * @return
   	 */
    @RequestMapping(value = "/zzjmes-service/matHandover/getMatInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
 	public R getMatInfo(@RequestParam Map<String,Object> params);
    
    /**
   	 * @return
   	 */
    @RequestMapping(value = "/zzjmes-service/matHandover/getSupplyMatInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
 	public R getSupplyMatInfo(@RequestParam Map<String,Object> params);
    
    /**
     * 交接保存
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/matHandover/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String,Object> params);
    
    /**
     * 车间供货保存
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/matHandover/saveSupply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveSupply(@RequestBody Map<String,Object> params);
    
    @RequestMapping(value = "/zzjmes-service/matHandover/getList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getList(@RequestParam Map<String, Object> paramMap);
    
    @RequestMapping(value = "/zzjmes-service/matHandover/getHandoverRuleList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getHandoverRuleList(@RequestParam Map<String, Object> paramMap);
	
}