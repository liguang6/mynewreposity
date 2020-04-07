package com.byd.wms.webservice.cloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.webservice.cloud.dao.QmsWebServiceDao;
import com.byd.wms.webservice.cloud.service.QmsWebService;
import com.byd.wms.webservice.ws.dao.WmsWebServiceQmsCheckDao;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName
 * @Author rain
 * @Date 2019年11月30日11:35:48
 * @Description QMS质检
 **/
@Service
public class QmsWebServiceImpl implements QmsWebService {
    @Autowired
    private QmsWebServiceDao qmsWebServiceDao;
    @Autowired
    private WmsWebServiceQmsCheckDao wmsWebServiceQmsCheckDao;
    @Autowired
    private UserUtils userUtils;

    /**
     * 发送质检信息给QMS系统
     * TODO 需要对方路径,修改路径
     * @param hm
     * @return
     */
    @Override
//    @WebServicePath(path=WebserviceInfo.WEBCLOUD_PATH, methodName = WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDATA)
    public HashMap sendQmsData(HashMap hm){
        HashMap hmm = new HashMap();
        String curTime = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
        String curUser = "WMS";
        String resultVstatus="100";//接口执行状态:[{"100","成功"},{"101","失败"}]
        try {
            System.out.println(hm.toString());
            JSONObject infoJson = JSONObject.parseObject(hm.get("quality_info").toString());
            curUser = (String) infoJson.get("current_user");
            System.out.println(infoJson.get("json"));
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
//          Client client = dcf.createClient("http://10.76.9.30:8080/qms/wmsWebqservices?WSDL");//QMS本地测试环境地址
            Client client= dcf.createClient("http://10.9.43.64:8888/qmsWeb/qms/wmsWebqservices?WSDL");//QMS测试环境地址
            Object[] objects = new Object[1];
            try {
                objects = client.invoke("insertQualityData", infoJson.get("json").toString());
            } catch (Exception e) {
                e.printStackTrace();
                hmm.put("msgty", "fail");
                hmm.put("msgtx", "sendQmsData发送质检信息接口请求发生异常!"+e.getMessage());
                hmm.put("program", "QMS");
                hmm.put("ctime", curTime);
                resultVstatus="101";
                return hmm;
            }
            /**
             *为送质检信息预留的接口
             */
//        Object objects[] = new Object[1];
//        objects[0] = hm.get("webServiceData");
            System.out.println("data=========>>>>>>>>>>>>" + objects[0].toString());
            JSONObject js = JSONObject.parseObject(objects[0].toString());
//            System.out.println(objects[0].toString());
            String msgty = (String) js.getString("MSGTY");
            //返回结果
            if (msgty == null || "fail".equals(msgty)) {
                hmm.put("msgty", (String) js.getString("MSGTY"));
                hmm.put("msgtx", (String) js.getString("MSGTX"));
                hmm.put("program", (String) js.getString("PROGRAM"));
                hmm.put("ctime", (String) js.getString("CTIME"));
                resultVstatus="101";
                return hmm;
            }
            hmm.put("msgty", (String) js.getString("MSGTY"));
            hmm.put("msgtx", (String) js.getString("MSGTX"));
            hmm.put("program", (String) js.getString("PROGRAM"));
            hmm.put("ctime", (String) js.getString("CTIME"));
            return hmm;
        }catch (Exception e){
            e.printStackTrace();
            hmm.put("msgty", "fail");
            hmm.put("msgtx", "sendQmsData发送质检信息接口发生异常!"+e.getMessage());
            hmm.put("program", "WMS");
            hmm.put("ctime", curTime);
            resultVstatus="101";
            return hmm;
        }finally {
            /**
             * 保存接口日志
             * #{d.biz_id},#{d.flow_no},#{d.from_no},#{d.to_no},
             * 			#{d.from_jsondata},#{d.to_jsondata},#{d.vstatus},
             * 			#{d.cturid},#{d.ctdt},#{d.upurid},#{d.updt}
             */
            Map<String, Object> logMap=new HashMap<>();
            logMap.put("biz_id","");
            logMap.put("flow_no","sendQmsData");
            logMap.put("from_no","WMS");
            logMap.put("to_no","QMS");
            logMap.put("from_jsondata",hm.get("quality_info").toString());
            logMap.put("to_jsondata",hmm.toString());
            logMap.put("vstatus",resultVstatus);
            logMap.put("cturid",curUser);
            logMap.put("ctdt",curTime);
            logMap.put("upurid","");
            logMap.put("updt","");
            wmsWebServiceQmsCheckDao.insertLogInfo(logMap);
        }
    }



}
