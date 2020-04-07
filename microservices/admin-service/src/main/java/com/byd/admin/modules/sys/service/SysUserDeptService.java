package com.byd.admin.modules.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysUserDeptEntity;

public interface SysUserDeptService extends IService<SysUserDeptEntity>{
	/**
	 * 根据用户ID，获取部门ID列表
	 */
	List<Long> queryDeptIdList(Long userId);
}
