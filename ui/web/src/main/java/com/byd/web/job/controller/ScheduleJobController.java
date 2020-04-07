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

package com.byd.web.job.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.job.entity.ScheduleJobEntity;
import com.byd.web.job.service.ScheduleJobRemote;

/**
 * 定时任务
 *
 * @author Mark 
 * @since 1.2.0 2016-11-28
 */
@RestController
@RequestMapping("/sys/schedule")
public class ScheduleJobController {
	@Autowired
	private ScheduleJobRemote scheduleJobRemote;
	
	/**
	 * 定时任务列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return scheduleJobRemote.list(params);
	}
	
	/**
	 * 定时任务信息
	 */
	@RequestMapping("/info/{jobId}")
	public R info(@PathVariable("jobId") Long jobId){
		return scheduleJobRemote.info(jobId);
	}
	
	/**
	 * 保存定时任务
	 */
	@RequestMapping("/save")
	public R save(@RequestBody ScheduleJobEntity scheduleJob){
		return scheduleJobRemote.save(scheduleJob);
	}
	
	/**
	 * 修改定时任务
	 */
	@RequestMapping("/update")
	public R update(@RequestBody ScheduleJobEntity scheduleJob){
		return scheduleJobRemote.update(scheduleJob);
	}
	
	/**
	 * 删除定时任务
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] jobIds){
		return scheduleJobRemote.delete(jobIds);
	}
	
	/**
	 * 立即执行任务
	 */
	@RequestMapping("/run")
	public R run(@RequestBody Long[] jobIds){
		return scheduleJobRemote.run(jobIds);
	}
	
	/**
	 * 暂停定时任务
	 */
	@RequestMapping("/pause")
	public R pause(@RequestBody Long[] jobIds){
		return scheduleJobRemote.pause(jobIds);
	}
	
	/**
	 * 恢复定时任务
	 */
	@RequestMapping("/resume")
	public R resume(@RequestBody Long[] jobIds){
		return scheduleJobRemote.resume(jobIds);
	}

}
