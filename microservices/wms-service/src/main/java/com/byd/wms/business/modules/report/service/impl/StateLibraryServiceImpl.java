package com.byd.wms.business.modules.report.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.report.service.StateLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/5
 */
@Service("stateLibraryService")
public class StateLibraryServiceImpl implements StateLibraryService {

    @Override
    public PageUtils getInventoryList(Map<String, Object> params) {

        //测试数据，待接口开发数据源
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> a = new HashMap<>();
        Map<String, Object> b = new HashMap<>();
        b.put("WH_NUMBER", "2仓");
        a.put("WERKS", "1厂");
        a.put("WH_NUMBER", "1仓");
        b.put("WERKS", "2厂");
        list.add(a);
        list.add(b);

        Page page = new Query<Map<String, Object>>(params).getPage();
        page.setRecords(list);
        page.setTotal(10);
        page.setSize(Integer.valueOf(15));
        page.setCurrent(Integer.valueOf(1));
        return new PageUtils(page);
    }
}
