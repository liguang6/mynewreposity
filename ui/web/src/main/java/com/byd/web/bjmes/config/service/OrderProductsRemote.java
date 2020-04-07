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
public interface OrderProductsRemote {
	
	@RequestMapping(value = "/bjmes-service/config/bjMesOrderProducts/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestParam Map<String, Object> paramMap);
	
	/**
     * 保存
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesOrderProducts/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesOrderProducts/getList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getList(@RequestParam Map<String,Object> params);

	@RequestMapping(value = "/bjmes-service/config/bjMesOrderProducts/delById/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delById(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesOrderProducts/queryOrderMapPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryOrderMapPage(@RequestParam Map<String, Object> paramMap);

	/**
     * 保存
     */
	@RequestMapping(value = "/bjmes-service/config/bjMesOrderProducts/saveOrderMap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveOrderMap(@RequestParam Map<String, Object> params);

	@RequestMapping(value = "/bjmes-service/config/bjMesOrderProducts/getOrderMapList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getOrderMapList(@RequestParam Map<String,Object> params);


}
