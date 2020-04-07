package com.byd.wms.webservice.ws.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.byd.wms.webservice.common.remote.WmsCallMaterialsServiceRemote;
import com.byd.wms.webservice.ws.service.WmsCallMaterialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/20
 * @description：
 */
@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.ws.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.ws.service.WmsCallMaterialsService" // 接口地址
)
public class WmsCallMaterialsServiceImpl implements WmsCallMaterialsService {

    @Autowired
    private WmsCallMaterialsServiceRemote wmsCallMaterialsServiceRemote;

    @Override
    public String callMaterials(String params) {
        String rst;
        try {
            rst=wmsCallMaterialsServiceRemote.callMaterials(params);
        }catch (Exception e){
            Map<String, Object> remap = new HashMap<>();
            remap.put("MSGTY","E");
            remap.put("MSGTX",e.getMessage());
            rst= JSONObject.toJSONString(remap);
        }
        return rst;
    }
}
