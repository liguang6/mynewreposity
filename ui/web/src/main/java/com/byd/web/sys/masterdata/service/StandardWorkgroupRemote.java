package com.byd.web.sys.masterdata.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月20日 上午9:54:35 
 * 类说明 
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface StandardWorkgroupRemote {
	@RequestMapping(value = "/admin-service/masterdata/standardWorkgroup/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 public R list(@RequestParam Map<String, Object> params);
	
		/**
	     * 信息
	     */
	 @RequestMapping(value = "/admin-service/masterdata/standardWorkgroup/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 public R info(@PathVariable("id") Long id);
	 
	 /**
	     * 保存
	     */
	 @RequestMapping(value = "/admin-service/masterdata/standardWorkgroup/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 public R save(@RequestParam Map<String, Object> params);
	 
	 /**
	     * 修改
	     */
	 @RequestMapping(value = "/admin-service/masterdata/standardWorkgroup/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 public R update(@RequestParam Map<String, Object> params);
	 
	 /**
	     * 删除
	     */
	 @RequestMapping(value = "/admin-service/masterdata/standardWorkgroup/deleteById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 public R deleteById(@RequestParam(value="id") long id);

	 @RequestMapping(value = "/admin-service/masterdata/standardWorkgroup/getStandardWorkgroupList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getStandardWorkgroupList(@RequestParam(value="map") Map<String,Object> map);
}
