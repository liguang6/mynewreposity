package com.byd.web.sys.service;

import com.byd.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

/**
 * 登录相关
 * 
 * @author cscc
 * @email 
 * @date 2016年11月10日 下午1:15:31
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysLoginRemote {
	/**
     * WEB登录
     */
	@RequestMapping(value = "/admin-service/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R login(@RequestParam(value="params")  Map<String,Object> params);
	
	/**
	 * WEB退出
	 */
	@RequestMapping(value = "/admin-service/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R logout(@RequestParam(value="params") Map<String,Object> params);
	
	@RequestMapping(value = "/admin-service/checkLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R checkLogin(@RequestParam(value="params")  Map<String,Object> params);
	
	/**
     * 
     */
	@RequestMapping(value = "/admin-service/getPdaUserAuthWh", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPdaUserAuthWh(@RequestParam(value="params")  Map<String,Object> params);
	
	
}
