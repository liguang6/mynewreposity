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

package com.byd.admin.modules.sys.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.byd.admin.modules.sys.entity.SysLogEntity;
import com.byd.admin.modules.sys.service.SysLogService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

/**
 * 系统日志
 * 
 * @author cscc
 * @email 
 * @date 2017-03-08 10:40:56
 */
@Controller
@RequestMapping("/sys/log")
public class SysLogController {
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysLogService.queryPage(params);

		return R.ok().put("page", page);
	}
	
	
	@RequestMapping("/insert")
	public void insert(@RequestParam SysLogEntity syslog){
		sysLogService.insert(syslog);
	}
	
	@RequestMapping("/addLog")
	public void addLog(@RequestParam Map<String, Object> syslog){
		SysLogEntity sysLogEntity = new SysLogEntity();
		sysLogEntity.setOperation(syslog.get("OPERATION")==null?null:syslog.get("OPERATION").toString());
		sysLogEntity.setMethod(syslog.get("METHOD")==null?null:syslog.get("METHOD").toString());
		sysLogEntity.setParams(syslog.get("PARAMS")==null?null:syslog.get("PARAMS").toString());
		sysLogEntity.setIp(syslog.get("IP")==null?null:syslog.get("IP").toString());
		sysLogEntity.setUsername(syslog.get("USERNAME")==null?null:syslog.get("USERNAME").toString());
		sysLogEntity.setTime(syslog.get("TIME")==null?null:Long.parseLong(syslog.get("TIME").toString()));
		sysLogEntity.setCreateDate(syslog.get("CREATE_DATE")==null?null:syslog.get("CREATE_DATE").toString());
		
		sysLogService.insert(sysLogEntity);
	}
	
}
