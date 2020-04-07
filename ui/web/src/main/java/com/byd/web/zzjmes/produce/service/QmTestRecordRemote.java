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
 * 品质检验
 * @author tangj
 * @email 
 * @date 2019-09-16 16:16:08
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface QmTestRecordRemote {
	
	@RequestMapping(value = "/zzjmes-service/qmTestRecord/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestParam Map<String, Object> paramMap);
    
	@RequestMapping(value = "/zzjmes-service/qmTestRecord/getPmdInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
 	public List<Map<String,Object>> getPmdInfo(@RequestParam Map<String,Object> params);
    
    @RequestMapping(value = "/zzjmes-service/qmTestRecord/getOrderList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  	public List<Map<String,Object>> getOrderList(@RequestParam Map<String,Object> params);

	@RequestMapping(value = "/zzjmes-service/qmTestRecord/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestParam Map<String,Object> params) ;
	
	@RequestMapping(value = "/zzjmes-service/qmTestRecord/del", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R del(@RequestParam Map<String,Object> params) ;

    @RequestMapping(value = "/zzjmes-service/qmTestRecord/getTestRecordList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  	public R getTestRecordList(@RequestParam Map<String,Object> params);
	
	@RequestMapping(value = "/zzjmes-service/qmTestRecord/getHeadList", method = RequestMethod.POST,produces = MediaType.ALL_VALUE)
	public List<Map<String,Object>> getHeadList(@RequestParam Map<String,Object> params) ;
}