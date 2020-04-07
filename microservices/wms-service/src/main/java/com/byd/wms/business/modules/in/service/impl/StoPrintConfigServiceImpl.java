package com.byd.wms.business.modules.in.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.in.dao.StoPrintConfigDao;
import com.byd.wms.business.modules.in.service.StoPrintConfigService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service("stoPrintConfigService")
public class StoPrintConfigServiceImpl implements StoPrintConfigService {
    @Autowired
    private StoPrintConfigDao stoPrintConfigDao;
    @Override
    public PageUtils queryList(Map<String, Object> params) {
        params= ParamsFilterUtils.paramFilter(params);
        String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
        String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
        int start = 0;int end = 0;
        int count=1;
        if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }else {
            end=count;
        }
        params.put("START", start);params.put("END", end);
        List<Map<String,Object>> list=stoPrintConfigDao.queryList(params);
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(list);
        page.setTotal(count);
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }
    @Override
    @Transient
   public void edit(Map<String,Object> params){
        Map maps = (Map)JSON.parse(params.get("params").toString());
          stoPrintConfigDao.edit(maps);

    }
    @Override
    @Transient
    public void add(Map<String,Object> params){
        Map map =(Map)JSON.parse(params.get("params").toString());
        stoPrintConfigDao.add(map);
    }
    @Override
    @Transient
    public void del(Map<String,Object> params){
        Map map =(Map)JSON.parse(params.get("params").toString());
        stoPrintConfigDao.del(map);
    }
    }


