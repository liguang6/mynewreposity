package com.byd.wms.business.modules.report.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.report.dao.MaterialStoreDao;
import com.byd.wms.business.modules.report.service.MaterialStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/11/25
 */

@Service("materialStoreService")
public class MaterialStoreServiceImpl implements MaterialStoreService {

    @Autowired
    public MaterialStoreDao materialStoreDao;

    @Override
    public PageUtils getMaterialList(Map<String, Object> params){

        System.out.println("===============================");
        String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
        String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
        int start = 0;int end = 0;
        int count=materialStoreDao.selectMaterialInfoCount(params);
        if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }else {
            end=count;
        }
        params.put("START", start);params.put("END", end);
        List<Map<String,Object>> list=materialStoreDao.selectMaterialList(params);

        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(list);
        page.setTotal(count);
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);

    }
}
