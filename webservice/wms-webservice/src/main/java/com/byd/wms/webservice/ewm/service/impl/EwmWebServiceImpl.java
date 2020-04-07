package com.byd.wms.webservice.ewm.service.impl;

import com.alibaba.fastjson.JSON;
import com.byd.wms.webservice.ewm.sap.rfc.functions.ZRFCWMS003;
import com.byd.wms.webservice.ewm.sap.rfc.functions.ZRFCWMS003_Service;
import com.byd.wms.webservice.ewm.sap.rfc.functions.ZSWMSTM;
import com.byd.wms.webservice.ewm.sap.rfc.functions.ZTWMSTM;
import com.byd.wms.webservice.ewm.service.EwmWebService;
import org.springframework.stereotype.Service;

import javax.xml.ws.Holder;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.List;

@Service
public class EwmWebServiceImpl implements EwmWebService {


    public HashMap sendLabel2Ewm(HashMap hm){
/*
        ZRFCWMS003_Service ZRFCWMS003_Service = new ZRFCWMS003_Service();
        ZRFCWMS003 zrfcWMS003 = ZRFCWMS003_Service.getZRFCWMS003();

        BindingProvider bp = (BindingProvider) zrfcWMS003;
        Map<String, Object> context = bp.getRequestContext();
        context.put(BindingProvider.USERNAME_PROPERTY, "menjing");
        context.put(BindingProvider.PASSWORD_PROPERTY, "menjing");
*/
        //权限验证
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("menjing", "menjing".toCharArray());
            }
        });
        //组装数据
        Holder<String> eMESSAGE = new Holder<>();
        ZTWMSTM ztwmstm = new ZTWMSTM();
        String param = "[" +
                "{\"whmno\":\"A150\",\"tmidn\":\"12345567\",\"TMQTY\":\"0.0\",\"PDATE\":\"0000-00-00\",\"VALTO\":\"0000-00-00\",\"ASNQTY\":\"0.0\"}," +
                "{\"whmno\":\"A150\",\"tmidn\":\"12345567\",\"TMQTY\":\"0.0\",\"PDATE\":\"0000-00-00\",\"VALTO\":\"0000-00-00\",\"ASNQTY\":\"0.0\"}" +
                "]";
        List<ZSWMSTM> item = JSON.parseArray(param, ZSWMSTM.class);
        item.forEach(e-> ztwmstm.getItem().add(e));
        Holder<ZTWMSTM> itTM = new Holder<>(ztwmstm);
        //获取SAP接口
        ZRFCWMS003_Service ZRFCWMS003_Service = new ZRFCWMS003_Service();
        ZRFCWMS003 zrfcWMS003 = ZRFCWMS003_Service.getZRFCWMS003();
        //调用SAP接口
        zrfcWMS003.zrfcWMS003(itTM, eMESSAGE);

        return null;
    }

}
