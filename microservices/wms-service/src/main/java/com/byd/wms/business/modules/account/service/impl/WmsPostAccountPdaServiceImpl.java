package com.byd.wms.business.modules.account.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.dao.WmsPostAccountPdaDao;
import com.byd.wms.business.modules.account.service.WmsPostAccountPdaService;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.in.dao.WmsSTOReceiptPdaDao;
import com.byd.wms.business.modules.in.service.WmsSTOReceiptPdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WmsPostAccountPdaServiceImpl implements WmsPostAccountPdaService {


    @Autowired
    private WmsPostAccountPdaDao postAccountPdaDao;
    @Autowired
    private CommonDao commonDao;

    @Override
    public PageUtils getwhTask(Map<String, Object> params) {
        String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
        String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
        int start = 0;
        int limit=0;
        int count = postAccountPdaDao.getwhTaskCount(params);
        if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            limit = Integer.valueOf(pageSize);
        }else {
            limit=count;
        }
        params.put("start", start);params.put("limit", limit);
        List<Map<String,Object>> list=postAccountPdaDao.getwhTask(params);
        if(list != null && list.size() !=0){

            BigDecimal REC_QTY = BigDecimal.ZERO;
            for (Map<String,Object> item : list) {
                Long boxs = Long.parseLong(item.get("COUNT")== null || item.get("COUNT").equals("") ?
                        "0" : item.get("COUNT").toString());
                REC_QTY = REC_QTY.add(BigDecimal.valueOf(boxs));
            }
            for (Map<String,Object> item : list) {
                item.put("REC_QTY",REC_QTY);
            }

        }

        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(list);
        page.setTotal(count);
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));

        return new PageUtils(page);
    }

    @Override
    public R posttingAc(Map<String, Object> params) {


        return R.ok();
    }



}
