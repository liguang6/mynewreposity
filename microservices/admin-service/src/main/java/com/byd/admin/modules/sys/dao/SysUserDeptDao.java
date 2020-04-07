package com.byd.admin.modules.sys.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysUserDeptEntity;

public interface SysUserDeptDao extends BaseMapper<SysUserDeptEntity>{

	List<Long> queryDeptIdList(Long userId);

}
