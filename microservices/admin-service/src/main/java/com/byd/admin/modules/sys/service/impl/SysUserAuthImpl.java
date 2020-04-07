package com.byd.admin.modules.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.sys.dao.SysUserAuthDao;
import com.byd.admin.modules.sys.entity.SysUserAuthEntity;
import com.byd.admin.modules.sys.service.SysUserAuthService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

@Service
public class SysUserAuthImpl extends ServiceImpl<SysUserAuthDao, SysUserAuthEntity> implements SysUserAuthService {

	@Autowired
	SysUserAuthDao sysUserAuthDao;
	
	@Override
    public PageUtils queryPage(Map<String, Object> params) {
        List<Map<String,Object>> records = sysUserAuthDao.queryUserPermissionList(params);
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(records);
        page.setTotal(records.size());
		page.setSize(records.size());
        return new PageUtils(page);
	}
	
	@Override
    public Map<String,Object> selectSysUserPermissionById(Long id) {
        Map map = new HashMap();
        map.put("id",id);
        Map<String,Object> idkey =  sysUserAuthDao.selectSysUserPermissionById(map);
        return idkey;
    }
}
