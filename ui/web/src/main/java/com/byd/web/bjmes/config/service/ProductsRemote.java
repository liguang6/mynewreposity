package com.byd.web.bjmes.config.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月15日 下午4:51:17 
 * 类说明 
 */
@FeignClient(name = "BJMES-SERVICE")
public interface ProductsRemote {
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestParam Map<String, Object> paramMap);
	
	/**
     * 保存
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
	
	/**
     * 更新
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody Map<String, Object> params);
	
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/delById/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/deleteByParentId/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteByParentId(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/infoByParentId/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R infoByParentId(@PathVariable("id") Long id);
	
	/**
     * 保存
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/saveComp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveComp(@RequestParam Map<String, Object> params);

    @RequestMapping(value = "/bjmes-service/config/bjMesProducts/getList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getList(@RequestBody Map<String,Object> params);

	/**
     * 更新检测节点
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesProducts/updateTestNode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R updateTestNode(@RequestParam Map<String, Object> params);
}
