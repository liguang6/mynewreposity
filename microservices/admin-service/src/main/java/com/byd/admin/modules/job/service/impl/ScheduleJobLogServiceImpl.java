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

package com.byd.admin.modules.job.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.admin.modules.job.dao.ScheduleJobLogDao;
import com.byd.admin.modules.job.entity.ScheduleJobLogEntity;
import com.byd.admin.modules.job.service.ScheduleJobLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogDao, ScheduleJobLogEntity> implements ScheduleJobLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<ScheduleJobLogEntity> page =null;
		String jobId = (String)params.get("jobId");
		String beanName = (String)params.get("beanName");
		String status = (String)params.get("status");
		
		page = new Query<ScheduleJobLogEntity>(params).getPage();
		
		Wrapper<ScheduleJobLogEntity> wrapper = new EntityWrapper<ScheduleJobLogEntity>()
				.like(StringUtils.isNotBlank(jobId),"job_id", jobId)
				.like(StringUtils.isNotBlank(beanName),"bean_name", beanName)
				.eq(StringUtils.isNotBlank(status),"status", status)
				.orderBy("log_id");
		
		if(page.getLimit()==-1) {
			List<ScheduleJobLogEntity> records = this.selectList(wrapper);
			page.setRecords(records);
			page.setTotal(records.size());
			page.setSize(records.size());
		}else {
			page = this.selectPage(page,wrapper);
		}
		
		return new PageUtils(page);
	}

}
