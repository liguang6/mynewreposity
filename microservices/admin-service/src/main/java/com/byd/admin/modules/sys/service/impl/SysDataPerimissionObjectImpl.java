package com.byd.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.sys.dao.SysDataPermissionDao;
import com.byd.admin.modules.sys.dao.SysDataPermissionObjectDao;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.entity.SysDataPermissionObjectEntity;
import com.byd.admin.modules.sys.service.SysDataPermissionObjectService;
import com.byd.admin.modules.sys.service.SysDataPermissionService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysDataPerimissionObjectImpl extends ServiceImpl<SysDataPermissionObjectDao, SysDataPermissionObjectEntity> implements SysDataPermissionObjectService {

    @Autowired
    SysDataPermissionObjectDao sysDataPermissionObjectDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
        String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
        int start = 0;int end = 6000;
        if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }
        params.put("start", start);params.put("end", end);
        List<Map<String,Object>> records = sysDataPermissionObjectDao.queryDataPermissionObjectList(params);
        if(records.size()==0){
            pageNo = "1";
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
            params.put("start", start);params.put("end", end);
            records = sysDataPermissionObjectDao.queryDataPermissionObjectList(params);
        }
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(records);
        page.setTotal(sysDataPermissionObjectDao.getSysDataPermissionObjectCount(params));
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));


        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(SysDataPermissionEntity entity) {
        //删除角色
        this.delete(entity);
    }

    @Override
    public List<Map<String,Object>> selectSysDataPermissionById(Long id) {
        Map map = new HashMap();
        map.put("id",id);
        List<Map<String,Object>> idkey =  sysDataPermissionObjectDao.selectSysDataPermissionObjectById(map);
        return idkey;
    }

}
