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

package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.SysDictDao;
import com.byd.wms.business.modules.config.entity.SysDictEntity;
import com.byd.wms.business.modules.config.service.SysDictService;


@Service("sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDictEntity> implements SysDictService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	Page<SysDictEntity> page = null;
        String name = (String)params.get("name");
        String type = (String)params.get("type");
        page = new Query<SysDictEntity>(params).getPage();
        if(page.getLimit()==-1) {
        	List<SysDictEntity> records = this.selectList(new EntityWrapper<SysDictEntity>()
                    .like(StringUtils.isNotBlank(name),"name", name)
                    .like(StringUtils.isNotBlank(type),"type", type));
			page.setRecords(records);
			page.setTotal(records.size());
			page.setSize(records.size());
        }else {
            page = this.selectPage(
            		page,
                    new EntityWrapper<SysDictEntity>()
                        .like(StringUtils.isNotBlank(name),"name", name)
                        .like(StringUtils.isNotBlank(type),"type", type)
            );
        }

        return new PageUtils(page);
    }

}
