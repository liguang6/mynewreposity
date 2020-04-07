package com.byd.wms.webservice.labelmaster.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.wms.webservice.common.util.WebServiceAccessLock;
import com.byd.wms.webservice.labelmaster.dao.LabelWebServiceDao;
import com.byd.wms.webservice.labelmaster.service.LabelWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.labelmaster.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.labelmaster.service.LabelWebService" // 接口地址
)
public class LabelWebServiceImpl implements LabelWebService{

    @Autowired
    private LabelWebServiceDao labelWebServiceDao;


    @Override
    public String getLabelMaster(String mapInfo){
        JSONObject res = new JSONObject();
        WebServiceAccessLock.operatelock("PDMMapService", WebServiceAccessLock.COUNT_TYPE_ADD);
        int ac_count=WebServiceAccessLock.currentCount;
        System.out.println("PDMMapService当前访问数:" + ac_count);
        if(ac_count >=WebServiceAccessLock.MAX_COUNT) {
            res.put("success", false);
            res.put("msg", "接口访问次数过多，请稍后重试！");
            return res.toString();
        }
        JSONArray jsa=null;
        try {
            System.out.println("PDMMAP:"+mapInfo);
            jsa=JSONArray.parseArray(mapInfo);
        }catch(Exception e) {
            res.put("success", false);
            res.put("msg", "参数不是有效JSON数据！");
            return res.toString();
        }
        List<Map<String,Object>> mapList=new ArrayList<Map<String,Object>>();
        for (int i = 0; i < jsa.size(); i++) {
            JSONObject sa = jsa.getJSONObject(i);
            if(sa.get("WERKS") == null ||"".equals(sa.get("WERKS").toString().trim())){
                res.put("msg", "工厂字段(WERKS)必填！");
                return res.toString();
            }
            if(sa.get("SYS_FLAG") == null || "".equals(sa.get("SYS_FLAG").toString().trim())){
                res.put("msg", "系统标识字段(SYS_FLAG)必填！");
                return res.toString();
            }
            int flag = labelWebServiceDao.getSysFlag((String) sa.get("SYS_FLAG"),(String) sa.get("WERKS"));
            if(flag == 0){
                res.put("msg", "传输失败！该系统标识 "+sa.get("SYS_FLAG")+" 在WMS系统中不存在");
                return res.toString();
            }else if(flag > 0){
                Map<String,Object> m = new HashMap<String,Object>(16);
                //工厂 必填
                m.put("WERKS", sa.get("WERKS"));
                //系统标识 必填
                m.put("SYS_FLAG", sa.get("SYS_FLAG"));
                //条码编号 选填
                m.put("LABEL_NO", sa.get("LABEL_NO"));
                //BYD批次 选填
                m.put("BATCH", sa.get("BATCH"));
                //查询WMS_WEBSERVICE_LOG日志表，根据系统标识和工厂获取上次调用日期
                String log_date = labelWebServiceDao.getLogDate((String) sa.get("SYS_FLAG"),(String) sa.get("WERKS"));
                m.put("LOG_DATE",log_date);
                mapList.add(m);
            }

        }

        Map<String,Object> condMap= new HashMap<String,Object>(16);
        Map<String,Object> logMap= new HashMap<String,Object>(16);


        condMap.put("map_list", mapList);
        logMap.put("SYS_FLAG", mapList.get(0).get("SYS_FLAG"));
        logMap.put("FROM_JSON", mapList.toString());

        try {
            List<Map<String,Object>> list = labelWebServiceDao.getLabelMaster(condMap);

            //保存日志在WMS_LABELWEBSERVICE_LOG （该表不会删除数据）
            //如果传入的批次号（BATCH）和 条码号（LABEL_NO）都为空，则保存日志，反之不保存
              for(int i = 0;i<mapList.size();i++){
                if((mapList.get(i).get("BATCH") == null || "".equals(mapList.get(i).get("BATCH").toString().trim()))
                        && (mapList.get(i).get("LABEL_NO") == null||"".equals(mapList.get(i).get("LABEL_NO").toString().trim()))){
                    Map<String,Object> logMap2= new HashMap<String,Object>(16);
                    List<Map<String,Object>> mapList2=new ArrayList<Map<String,Object>>();
                    mapList2.add(mapList.get(i));
                    logMap2.put("map_list", mapList2);
                    labelWebServiceDao.saveLog2(logMap2);
                 }
            }

            //保存日志在WMS_WEBSERVICE_LOG（该表会定期删除数据）
            logMap.put("STATUS","100");
            // logMap.put("TO_JSON",res); //返回json太大，暂不保存
            labelWebServiceDao.saveLog(logMap);
            res.put("success", true);
            res.put("msg", "传输成功！");
            res.put("data",list);
            return res.toString();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("transferMapInfo: "+e.getMessage());
            res.put("success", false);
            res.put("msg", "接口异常，传输失败！");
            if(e.getMessage().contains("Duplicate entry")) {
                res.put("msg", e.getMessage());
            }
            WebServiceAccessLock.operatelock("PDMMapService", WebServiceAccessLock.COUNT_TYPE_SUB);
            logMap.put("STATUS","101");
            //logMap.put("TO_JSON",res.toString());
            //labelWebServiceDao.saveLog(logMap);
            return res.toString();
        }
    }
}
