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
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.admin.modules.sys.dao.SysTextCookieDao;
import com.byd.admin.modules.sys.entity.SysColumnConfigurationEntity;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysTextCookieEntity;
import com.byd.admin.modules.sys.redis.SysConfigRedis;
import com.byd.admin.modules.sys.service.SysTextCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("SysTextCookieService")
public class SysTextcookieServiceImpl extends ServiceImpl<SysTextCookieDao, SysTextCookieEntity> implements SysTextCookieService {
	@Autowired
	private SysTextCookieDao sysTextCookieDao;

	@Override
	public String queryPage(Map<String, Object> params) {
		String inputValue=sysTextCookieDao.selectinputValue(params);
        return inputValue;
	}

	@Override
	public void saveInputVal(Map<String, Object> params) {
		Map<String,Object> queryparam = new HashMap<String,Object>();
		SysTextCookieEntity sysTextCookieEntity=new SysTextCookieEntity();
		Long userId=(Long)params.get("userId");
		String inputId=params.get("inputId").toString();
		String  inuptValue=params.get("inuptValue").toString();
		sysTextCookieEntity.setInputValue(inuptValue);
		sysTextCookieEntity.setInputId(inputId);
		sysTextCookieEntity.setUserId(userId);
		queryparam.put("user_Id",userId);
		queryparam.put("input_Id", inputId);
		List<SysTextCookieEntity> locallist = super.selectByMap(queryparam);
		if(locallist.isEmpty()) {
			super.insert(sysTextCookieEntity);
		}else {
			super.update(sysTextCookieEntity, new EntityWrapper<SysTextCookieEntity>()
					.eq("input_Id", inputId)
					.eq("user_Id", userId));
		};
		
	}

	
}
