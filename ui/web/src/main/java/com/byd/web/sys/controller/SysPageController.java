/**
 * Copyright 2018 cscc
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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 系统页面视图
 * 
 * @author cscc
 * @email 
 * @date 2016年11月24日 下午11:05:27
 */
@Controller
public class SysPageController {
	
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
	
	@RequestMapping("wms/{wmsModule}/{url}.html")
	public String wms(@PathVariable("wmsModule") String wmsModule, @PathVariable("url") String url){
		return "wms/" + wmsModule + "/" + url;
	}
	
	@RequestMapping("qms/{qmsModule}/{url}.html")
	public String qms(@PathVariable("qmsModule") String qmsModule, @PathVariable("url") String url){
		return "qms/" + qmsModule + "/" + url;
	}
	@RequestMapping("qms/{url}.html")
	public String qms(@PathVariable("url") String url){
		return "qms/" + url;
	}
	
	
	@RequestMapping("zzjmes/{zzjmesModule}/{url}.html")
	public String zzjmes(@PathVariable("zzjmesModule") String zzjmesModule, @PathVariable("url") String url){
		return "zzjmes/" + zzjmesModule + "/" + url;
	}
	
	@RequestMapping("bjmes/{bjmesModule}/{url}.html")
	public String bjmes(@PathVariable("bjmesModule") String bjmesModule, @PathVariable("url") String url){
		return "bjmes/" + bjmesModule + "/" + url;
	}
	
	@RequestMapping("mes/{mesModule}/{url}.html")
	public String mes(@PathVariable("mesModule") String mesModule, @PathVariable("url") String url){
		return "mes/" + mesModule + "/" + url;
	}

	@RequestMapping(value = {"/", "index.html"})
	public String index(){
		return "index";
	}

	@RequestMapping("index1.html")
	public String index1(){
		return "index1";
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

}
