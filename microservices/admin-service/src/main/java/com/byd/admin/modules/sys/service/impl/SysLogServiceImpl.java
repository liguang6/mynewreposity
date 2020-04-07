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

package com.byd.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.admin.modules.sys.dao.SysLogDao;
import com.byd.admin.modules.sys.entity.SysLogEntity;
import com.byd.admin.modules.sys.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	Page<SysLogEntity> page = null;
    	
        String key = (String)params.get("key");
        page = new Query<SysLogEntity>(params).getPage();
        if(page.getLimit()==-1) {
        	List<SysLogEntity> records = this.selectList(new EntityWrapper<SysLogEntity>().like(StringUtils.isNotBlank(key),"username", key));
			page.setRecords(records);
			page.setTotal(records.size());
			page.setSize(records.size());
        }else {
            page = this.selectPage(
                    page,
                    new EntityWrapper<SysLogEntity>().like(StringUtils.isNotBlank(key),"username", key)
                );
        }


        return new PageUtils(page);
    }
}
