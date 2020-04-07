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

package com.byd.web.sys.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysLogEntity;

/**
 * 系统日志
 * 
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysLogRemote {
	/**
	 * 列表
	 */
	@RequestMapping(value = "/admin-service/sys/log/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam(value="params") Map<String, Object> params);
	
	@RequestMapping(value = "/admin-service/sys/log/insert", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void insert(@RequestParam(value="syslog") SysLogEntity syslog);
	
}
