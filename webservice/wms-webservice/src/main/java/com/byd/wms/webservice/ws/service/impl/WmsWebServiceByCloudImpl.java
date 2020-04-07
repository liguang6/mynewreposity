package com.byd.wms.webservice.ws.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.StringUtils;
import com.byd.wms.webservice.cloud.dao.CloudWebServiceDao;
import com.byd.wms.webservice.common.util.WebServiceAccessLock;
import com.byd.wms.webservice.ws.service.WmsWebServiceByCloudService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.*;

@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.ws.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.ws.service.WmsWebServiceByCloudService" // 接口地址
)
public class WmsWebServiceByCloudImpl implements WmsWebServiceByCloudService {

    @Autowired
    private CloudWebServiceDao cloudWebServiceDao;

    /**
     * 云平台获取仓库地址
     * @param name
     * @return
     */
    @Override
    public String queryWhDetail(String name) {
        JSONObject js = (JSONObject) JSON.parse(name);
        HashMap hm = new HashMap();
        hm.put("WERKS",js.getString("WERKS"));
        //hm.put("LGORT_NO",js.getString("LGORT_NO"));
        List<Map<String,Object>> lm = cloudWebServiceDao.queryWhDetail(hm);
        String lgortNo = js.getString("LGORT_NO");
        //System.err.println(lm.toString());
        boolean flag = false;
        if(!StringUtils.isBlank(lgortNo)){
            for(int k = 0;k<lm.size();k++){
                flag = false;
                String lgort = (String) lm.get(k).get("LGORT_NO");
                String lgorts[] = lgort.split(",");
                for(int o = 0;o<lgorts.length;o++){
                    if(lgorts[o].equals(lgortNo)){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    lm.remove(k);
                    k--;
                }
            }
        }
        //System.err.println("end======== " + lm.toString());
        JSONArray jsa = new JSONArray();
        for (int i = 0 ;i<lm.size();i++){
            JSONObject jsData = new JSONObject();
            jsData.put("CONTACTS",lm.get(i).get("CONTACTS")==null?"":lm.get(i).get("CONTACTS"));
            jsData.put("WH_NAME",lm.get(i).get("WH_NAME")==null?"":lm.get(i).get("WH_NAME"));
            jsData.put("WH_NUMBER",lm.get(i).get("WH_NUMBER")==null?"":lm.get(i).get("WH_NUMBER"));
            jsData.put("ADDRESS",lm.get(i).get("ADDRESS")==null?"":lm.get(i).get("ADDRESS"));
            jsData.put("TEL",lm.get(i).get("TEL")==null?"":lm.get(i).get("TEL"));
            jsa.add(jsData);
        }
        System.err.println(jsa.toString());
        JSONObject result = new JSONObject();
        if(lm.size()>0){
            result.put("DATA",jsa.toString());
            result.put("SUCCESS","S");
        }else {
            result.put("DATA","");
            result.put("MSG","查询失败,数据为空！");
            result.put("SUCCESS","E");
        }
        return result.toString();
    }
    
    public static void main(String[] args) {
    	String PDMMAP = "[{\"edit_date\":\"2019-09-18 08:18\",\"editor\":\"PLM系统\",\"map_name\":\"转向管柱及万向节总成\",\"map_no\":\"K10B-3404010\",\"map_sheet\":\"A4\",\"map_url\":\"\\\\\\\\10.23.1.23\\\\客研院plm图纸\\\\K10B\\\\K10B-3404010-转向管柱及万向节总成_001_A4_2019-9-18.pdf\",\"publish_date\":\"2019-9-18 08:17\",\"publisher\":\"周永发2971683(2971683)\",\"status\":\"B\",\"version\":\"001\"}]";
    	JSONArray jsa=null;
    	jsa=JSONArray.parseArray(PDMMAP);
    	//System.out.println(mlist.get(0).get("editor"));
    	for(int i=0;i<jsa.size();i++){
    		 // 遍历 jsonarray 数组，把每一个对象转成 json 对象
    		JSONObject sa = jsa.getJSONObject(i);
    		System.out.println(sa.get("editor"));
    	}
    }

}
