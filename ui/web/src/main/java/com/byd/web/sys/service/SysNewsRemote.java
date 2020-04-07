package com.byd.web.sys.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "ADMIN-SERVICE")
public interface SysNewsRemote {
	/**
	 *
	 * 列表
	 */
	@RequestMapping(value = "/admin-service/sys/news/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;

	 
	@RequestMapping(value = "/admin-service/sys/news/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R query(@RequestParam Map<String, Object> params) ;

	 
	@RequestMapping(value = "/admin-service/sys/news/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("id") Long id) ;
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/admin-service/sys/news/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestParam Map<String, Object> params) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/admin-service/sys/news/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestParam Map<String, Object> params) ;
	
	/**
    * 单条记录删除
    * @param id
    */
   @RequestMapping(value = "/admin-service/sys/news/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R delById(@RequestParam(value="id") Long id) ;
   
	/**
    * 根据账号、工厂等条件获取有效系统公告信息
    * @param id
    */
   @RequestMapping(value = "/admin-service/sys/news/getUserNews", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R getUserNews(@RequestParam Map<String, Object> params) ;
	
}
