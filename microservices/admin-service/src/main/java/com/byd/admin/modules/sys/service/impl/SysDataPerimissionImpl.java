package com.byd.admin.modules.sys.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.sys.dao.SysDataPermissionDao;
import com.byd.admin.modules.sys.entity.SysDataPermissionEntity;
import com.byd.admin.modules.sys.service.SysDataPermissionService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class SysDataPerimissionImpl extends ServiceImpl<SysDataPermissionDao, SysDataPermissionEntity> implements SysDataPermissionService {

    @Autowired
    SysDataPermissionDao sysDataPermissionDao;
    public List<SysDataPermissionEntity> queryDataPermission(Map<Long, String> params) {
        List<SysDataPermissionEntity>lists = baseMapper.queryDataPermission(params);
        return lists;
    }

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
        List<Map<String,Object>> records = sysDataPermissionDao.queryDataPermissionList(params);
        if(records.size()==0){
            pageNo = "1";
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
            params.put("start", start);params.put("end", end);
            records = sysDataPermissionDao.queryDataPermissionList(params);
        }
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(records);
        page.setTotal(sysDataPermissionDao.getSysDataPermissionCount(params));
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
        List<Map<String,Object>> idkey =  sysDataPermissionDao.selectSysDataPermissionById(map);
        return idkey;
    }

    @Override
    public List<Map<String, Object>> getAuthFields(String menuId) {
        Map map = new HashMap();
        map.put("menuId",menuId);
        List<Map<String,Object>> idkey =  sysDataPermissionDao.getAuthFields(map);
        //System.err.println("list"+idkey.toString());
//        StringBuilder sb = new StringBuilder();
//        for (int i =0;i<idkey.size();i++){
//            sb.append(idkey.get(i).get("AUTHFIELDS")+",");
//        }
//        sb = new StringBuilder(sb.substring(0,sb.length()-1));
//        idkey.get(0).put("AUTHFIELDS",sb.toString());
        //System.err.println(idkey.get(0).toString());
        return idkey;
    }
    
    @Override
    public List<Map<String, Object>> queryDataPermissionByMenuKey(Map<String, Object> params){
    	return sysDataPermissionDao.queryDataPermissionByMenuKey(params);
    }

}
