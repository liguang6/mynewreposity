package com.byd.wms.webservice.ws.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.wms.webservice.ws.dao.WmsStockQueryDao;
import com.byd.wms.webservice.ws.dao.WmsWebServiceQmsCheckDao;
import com.byd.wms.webservice.ws.service.WmsStockQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.*;

@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.ws.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.ws.service.WmsStockQueryService" // 接口地址
)
public class WmsStockQueryServiceImpl implements WmsStockQueryService {
    @Autowired
    WmsStockQueryDao wmsStockQueryDao;
    @Autowired
    WmsWebServiceQmsCheckDao wmsWebServiceQmsCheckDao;
    @Override
    public String getQueryStock(String params) {
        JSONObject res = new JSONObject();
        String curTime = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
        Map a = new HashMap();
        try {
            a = (Map) JSON.parse(params);
            if (a.get("WERKS")==null || a.get("WERKS").toString().isEmpty()) {
                res.put("data","");
                res.put("success", false);
                res.put("msg", "工厂不能为空");
            } else if (a.get("CXLX")==null || a.get("CXLX").toString().isEmpty()){
                res.put("data","");
                res.put("success", false);
                res.put("msg", "查询类型不能为空");
            }else{
                Map<String,Object>werks = new TreeMap<>();
                werks.put("WERKS",a.get("WERKS"));
                werks.put("WH_NUMBER",a.get("WH_NUMBER")==null?"":a.get("WH_NUMBER"));
                List<Map<String, Object>> list2 = wmsStockQueryDao.getWERKS(werks);
                if(list2.size() ==0){
                    res.put("data","");
                    res.put("success", false);
                    res.put("msg", "工厂或仓库号输入不正确");
                }else {
                    List<Map<String, Object>> list = wmsStockQueryDao.getQueryStock(a);
                    if (list.size() == 0) {
                        res.put("success", false);
                        res.put("msg", "查询数据为空");
                    }else {
                        res.put("success", true);
                        res.put("msg", "查询成功");
                    }
                    //处理数据
                    List<Map<String, Object>> list1 = new ArrayList<>();
                    for (Map<String, Object> map : list) {
                        map.put("CXLX", a.get("CXLX") == null ? "" : a.get("CXLX"));
                        map.put("MAKTX", map.get("MAKTX") == null ? "" : map.get("MAKTX"));
                        map.put("FREEZE_QTY", map.get("FREEZE_QTY") == null ? "" : map.get("FREEZE_QTY"));
                        map.put("LIKTX", map.get("LIKTX") == null ? "" : map.get("LIKTX"));
                        map.put("WERKS", map.get("WERKS") == null ? "" : map.get("WERKS"));
                        map.put("WH_NUMBER", map.get("WH_NUMBER") == null ? "" : map.get("WH_NUMBER"));
                        map.put("SO_ITEM_NO", map.get("SO_ITEM_NO") == null ? "" : map.get("SO_ITEM_NO"));
                        map.put("UNIT", map.get("MEINS") == null ? "" : map.get("MEINS"));
                        map.put("PRFRQ", map.get("PRFRQ") == null ? "" : map.get("PRFRQ"));
                        map.put("SO_NO", map.get("SO_NO") == null ? "" : map.get("SO_NO"));
                        map.put("BATCH", map.get("BATCH") == null ? "" : map.get("BATCH"));
                        map.put("LIFNR", map.get("LIFNR") == null ? "" : map.get("LIFNR"));
                        map.put("MATNR", map.get("MATNR") == null ? "" : map.get("MATNR"));
                        map.put("LGORT", map.get("LGORT") == null ? "" : map.get("LGORT"));
                        map.put("STOCK_QTY", map.get("STOCK_QTY") == null ? "" : map.get("STOCK_QTY"));
                        map.put("VALTO", map.get("EFFECT_DATE") == null ? "" : map.get("EFFECT_DATE"));
                        map.put("SOBKZ", map.get("SOBKZ") == null ? "" : map.get("SOBKZ"));
                        map.put("PGORDE", "");
                        map.put("DOPART", "");
                        list1.add(map);

                    }

                    res.put("data", JSON.toJSONString(list1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("msg", "输入值类型不是有效JSON类型");
        }finally {
            /**
             * 保存接口日志
             * #{d.biz_id},#{d.flow_no},#{d.from_no},#{d.to_no},
             * 			#{d.from_jsondata},#{d.to_jsondata},#{d.vstatus},
             * 			#{d.cturid},#{d.ctdt},#{d.upurid},#{d.updt}
             */
            Map<String, Object> logMap=new HashMap<>();
            logMap.put("biz_id","");
            logMap.put("flow_no","WmsStockQueryService");
            logMap.put("from_no","");
            logMap.put("to_no","WMS");
            logMap.put("from_jsondata",params);
            logMap.put("to_jsondata","");
            //接口执行状态:[{"true","成功"},{"false","失败"}]
            logMap.put("vstatus",res.get("success"));
            logMap.put("cturid",curTime);
            logMap.put("ctdt","");
            logMap.put("upurid","");
            logMap.put("updt","");
            wmsWebServiceQmsCheckDao.insertLogInfo(logMap);
            //返回信息
            return res.toString();
        }

        }

}


