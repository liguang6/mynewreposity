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

package com.byd.zzjmes.common.remote;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 系统日志接口
 * 
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysLogRemote {
	@RequestMapping(value = "/admin-service/sys/log/addLog", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void addLog(@RequestParam(value="syslog") Map<String, Object> syslog);
	
}
