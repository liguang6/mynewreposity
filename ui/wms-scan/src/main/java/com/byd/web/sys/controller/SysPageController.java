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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.service.SysLoginRemote;

/**
 * 系统页面视图
 * 
 * @author cscc
 * @email 
 * @date 2016年11月24日 下午11:05:27
 */
@Controller
public class SysPageController {
	
	@Autowired
	private SysLoginRemote sysLoginRemote;
	
	@Autowired
    private UserUtils userUtils;
	
/*	@RequestMapping("{modules}/{module}/{url}.html")
	public String module(@PathVariable("modules") String modules,@PathVariable("module") String module, @PathVariable("url") String url){
		return modules+"/" + module + "/" + url;
	}*/
	
	@RequestMapping("sys/{sysModule}/{url}.html")
	public String sys(@PathVariable("sysModule") String sysModule,@PathVariable("url") String url){
		return "sys/" + sysModule +"/"+url;
	}
	
	@RequestMapping("sys/{url}.html")
	public String sys(@PathVariable("url") String url){
		return "sys/" + url;
	}
	
	@RequestMapping("job/{url}.html")
	public String job(@PathVariable("url") String url){
		return "job/" + url;
	}
	
	@RequestMapping("{wmsModule}/{url}.html")
	public String scan(@PathVariable("wmsModule") String wmsModule, @PathVariable("url") String url){
		return wmsModule + "/" + url;
	}

	@RequestMapping(value = {"/", "index.html"})
	public String index(){
		return "index";
	}

	@RequestMapping("index2.html")
	public String index2(){
		return "index2";
	}

	@RequestMapping("login.html")
	public String login(){
		return "login";
	}

	@RequestMapping("main.html")
	public String main(){
		return "main";
	}

	@RequestMapping("404.html")
	public String notFound(){
		return "404";
	}

    @RequestMapping("SelectWarehouse.html")
 	public R SelectWarehouse(){
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("username", userUtils.getUser().get("USERNAME"));
    	return sysLoginRemote.getPdaUserAuthWh(params);
 	}
}
