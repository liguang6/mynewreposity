/**
 * Copyright 2018 cc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.web.sys.controller;

import com.byd.utils.Constant;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.web.sys.service.SysLoginRemote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录相关
 * 
 * @author cc
 * @email 
 * @date 2016年11月10日 下午1:15:31
 */
@RestController
public class SysLoginController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SysLoginRemote sysLoginRemote;
	@Autowired 
	protected HttpSession session; 
	@Autowired
	private RedisUtils redisUtils;
	
	/**
     * 登录
     */
     @CrossOrigin
     @RequestMapping(value = "login", method = RequestMethod.POST)
    public R login(@RequestParam  Map<String,Object> params) {
    	 params.put("language", "ZH");
    	 R r=  sysLoginRemote.login(params);
    	 if(r.get("code")!=null && r.get("code").toString().equals("0")) {
    		 session.setAttribute("username", params.get("username")); 
    		 session.setAttribute("USERNAME", params.get("username")); 
    		 session.setAttribute(Constant.SESSION_LANGUAGE_KEY, params.get("language")); 
    	 }
    	 return r;
	}
    
    
	/**
	 * 语言设置
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/setlanguage")
	public R setlanguage(HttpServletRequest request, HttpServletResponse response) {
		//根据session判段语言
		String sessionLanguage = (String)request.getSession().getAttribute(Constant.SESSION_LANGUAGE_KEY);
		//根据本地浏览器判段语言
		String localLanguage = (String)request.getParameter("language");
		System.out.println("sessionLanguage="+sessionLanguage);
		String language = (localLanguage==null || localLanguage.isEmpty()) ? sessionLanguage : localLanguage;
		request.getSession().setAttribute(Constant.SESSION_LANGUAGE_KEY, language);  
		request.setAttribute("language", language); 
		return R.ok();
	}
	
	/**
	 * 退出
	 */
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", session.getAttribute("username"));
		String username = session.getAttribute("username").toString();
		try {
			/* 删除redis里用户信息
			redisUtils.delete(RedisKeys.getUserKey(username));
			redisUtils.delete(RedisKeys.getUserAuthKey(username));
			redisUtils.delete(RedisKeys.getUserMenuKey(username));
			redisUtils.delete(RedisKeys.getUserTokenKey(username));
			redisUtils.delete(RedisKeys.getUserWerksKey(username)); */
			session.setAttribute("username", null);
			session.setAttribute("USERNAME", null);
			session.invalidate();
			response.sendRedirect(request.getContextPath() + "/login.html");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
     * 登录校验
     */
     @CrossOrigin
     @RequestMapping(value = "checkLogin", method = RequestMethod.POST)
    public R checkLogin(@RequestParam  Map<String,Object> params) {
    	 R r=  sysLoginRemote.checkLogin(params);
    	 return r;
	}
}
