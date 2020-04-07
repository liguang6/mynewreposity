package com.byd.admin.modules.sys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.sys.dao.SysUserDeptDao;
import com.byd.admin.modules.sys.entity.SysUserDeptEntity;
import com.byd.admin.modules.sys.service.SysUserDeptService;

@Service
public class SysUserDeptServiceImpl extends ServiceImpl<SysUserDeptDao, SysUserDeptEntity>  implements SysUserDeptService{

	@Override
	public List<Long> queryDeptIdList(Long userId) {
		List<Long> deptIds=baseMapper.queryDeptIdList(userId);
		return deptIds;
	}

}
