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

package com.byd.web.job.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.job.entity.ScheduleJobEntity;

/**
 * 定时任务
 *
 * @author develop01 
 * @since 1.2.0 2019-3-22
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface ScheduleJobRemote {
	
	/**
	 * 定时任务列表
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params);
	
	/**
	 * 定时任务信息
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/info/{jobId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("jobId") Long jobId);
	
	/**
	 * 保存定时任务
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody ScheduleJobEntity scheduleJob);
	
	/**
	 * 修改定时任务
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody ScheduleJobEntity scheduleJob);
	
	/**
	 * 删除定时任务
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestBody Long[] jobIds);
	
	/**
	 * 立即执行任务
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/run", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R run(@RequestBody Long[] jobIds);
	
	/**
	 * 暂停定时任务
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/pause", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R pause(@RequestBody Long[] jobIds);
	
	/**
	 * 恢复定时任务
	 */
	@RequestMapping(value = "/admin-service/sys/schedule/resume", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R resume(@RequestBody Long[] jobIds);

}
