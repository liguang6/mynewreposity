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

package com.byd.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysRoleEntity;
import com.byd.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 角色管理
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:33:33
 */
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {
    List<Map> queryAlls(Map<String, Object> params);

    List<Map<String,Object>> queryRoleList(Map<String, Object> params);

    int queryRoleCount(Map<String, Object> params);
    
    void deleteRoleAuth(Long[] roleIds);
}
