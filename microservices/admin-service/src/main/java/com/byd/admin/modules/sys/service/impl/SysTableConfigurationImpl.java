package com.byd.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.sys.dao.SysDataPermissionDao;
import com.byd.admin.modules.sys.dao.SysTableConfigurationDao;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.entity.SysRoleEntity;
import com.byd.admin.modules.sys.entity.SysTableConfigurationEntity;
import com.byd.admin.modules.sys.service.SysDataPermissionService;
import com.byd.admin.modules.sys.service.SysTableConfigurationService;
import com.byd.utils.Constant;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class SysTableConfigurationImpl extends ServiceImpl<SysTableConfigurationDao, SysMenuEntity> implements SysTableConfigurationService {

    @Autowired
    SysTableConfigurationDao SysTableConfigurationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String menuName = (String)params.get("menuName");
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
        String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
        int start = 0;int end = 6000;
        if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }
        params.put("start", start);
        params.put("end", end);
        List<Map<String,Object>> records = SysTableConfigurationDao.queryGridByName(params);
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo)); 
        page.setRecords(records);
        page.setTotal(SysTableConfigurationDao.getGridPageCount(params));
 		return new PageUtils(page);
    }


}
