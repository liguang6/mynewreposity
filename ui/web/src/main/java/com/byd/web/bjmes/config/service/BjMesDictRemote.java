package com.byd.web.bjmes.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.R;

@FeignClient(name = "BJMES-SERVICE")
public interface BjMesDictRemote {
	

	@RequestMapping(value = "/bjmes-service/config/bjMesDict/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestBody Map<String,Object> params);

	@RequestMapping(value = "/bjmes-service/config/bjMesDict/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    /**
     * 保存
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesDict/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	
	/**
     * 更新
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesDict/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody Map<String, Object> params);
	
	
	@RequestMapping(value = "/bjmes-service/config/bjMesDict/delById/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesDict/getList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getList(@RequestBody Map<String,Object> params);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesDict/getDictList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String,Object>> getDictList(@RequestBody Map<String,Object> params);
	
}
