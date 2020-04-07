package com.byd.wms.webservice.cloud.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.UserUtils;
import com.byd.wms.webservice.cloud.dao.CloudWebServiceDao;
import com.byd.wms.webservice.cloud.service.CloudWebService;
import com.byd.wms.webservice.common.Constant.WebserviceInfo;
import com.byd.wms.webservice.common.annotation.WebServicePath;
import com.byd.wms.webservice.common.util.WebServiceClientUtil;

/**
 * @ClassName 云平台webService接口调用
 * @Author qiu.jiaming1
 * @Date $time$ $date$
 * @Description //TODO $end$
 **/

@Service
public class CloudWebServiceImpl implements CloudWebService {
    @Autowired
    private CloudWebServiceDao cloudWebServiceDao;

    @Autowired
    private UserUtils userUtils;

    @Override
    @Transactional
    @WebServicePath(path=WebserviceInfo.WEBCLOUD_PATH,
    	methodName = WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDETAILDATA)
    public HashMap deliveryWmsService(HashMap hashMap) {
        String str = (String) hashMap.get("webServiceData");

        //System.err.println("data=========>>>>>>>>>>>>"+str);
        //JSONObject js = JSONObject.parseObject(str);
        JSONObject js = JSONObject.parseObject(str);
        String status = (String) js.get("STATUS");
        //返回结果
        HashMap hm = new HashMap();
        if(status==null || status.equals("E")){
            hm.put("STATUS",(String) js.get("STATUS"));
            hm.put("MSG",(String) js.get("MSG"));
            return hm;
        }
        /**---------------------开始解析--------------------------------*/
        js = (JSONObject) js.get("DATA");
    //try{

        /**---------------------1.插入送货单抬头--------------------------------*/
        HashMap hmByHead = new HashMap();
        hmByHead.put("DELIVERY_NO",js.getString("DELIVERY_NO"));
        hmByHead.put("DELIVERY_TYPE",js.getString("DELIVERY_TYPE"));
        hmByHead.put("STATUS_RMK",js.getString("STATUS_RMK"));
        hmByHead.put("FACT_NO",js.getString("FACT_NO"));
        hmByHead.put("LGORT_NO",js.getString("LGORT_NO"));
        hmByHead.put("BOOKING_DATE",js.getString("BOOKING_DATE"));
        hmByHead.put("BOOKING_TIME",js.getString("BOOKING_TIME"));
        hmByHead.put("PROVIDER_CODE",js.getString("PROVIDER_CODE"));
        hmByHead.put("PROVIDER_NAME",js.getString("PROVIDER_NAME"));
        hmByHead.put("WHCD_NO",js.getString("WHCD_NO"));
        hmByHead.put("PROVIDER_NAME",js.getString("PROVIDER_NAME"));
        hmByHead.put("REMARK",js.getString("MEMO"));
        hmByHead.put("CTURID",userUtils.getUser().get("USERNAME").toString());
        hmByHead.put("CTDT",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        //hmByHead.put("LAST_UPDATE",js.getString("LAST_UPDATE"));
        //hmByHead.put("UPURID",js.getString("UPURID"));
        cloudWebServiceDao.insertWmsCloudDelivery(hmByHead);
        /**---------------------2.插入送货单明细--------------------------------*/
        JSONArray jsoaItem = JSON.parseArray(js.get("DELIVERY_ITEM").toString());
        List lmByDeliveryItem = new ArrayList();
        for (int j = 0;j<jsoaItem.size();j++){
            JSONObject json = (JSONObject) jsoaItem.get(j);
            HashMap hmByItem = new HashMap();
            hmByItem.put("DELIVERY_NO",js.getString("DELIVERY_NO"));
            hmByItem.put("ITEM_NO",json.getString("ITEM_NO"));
            hmByItem.put("MAT_NO",json.getString("MAT_NO"));

            hmByItem.put("MAT_DESC",json.getString("MAT_DESC"));
            hmByItem.put("PO_NO",json.getString("EBELN"));
            hmByItem.put("PO_ITEM",json.getString("EBELP"));

            hmByItem.put("FACT_NO",js.getString("FACT_NO"));
            hmByItem.put("LGORT_NO",js.getString("LGORT_NO"));
            hmByItem.put("PROVIDER_CODE",js.getString("PROVIDER_CODE"));
            hmByItem.put("PROVIDER_NAME",js.getString("PROVIDER_NAME"));
            hmByItem.put("CTURID",userUtils.getUser().get("USERNAME").toString());
            hmByItem.put("CTDT",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            //hmByItem.put("UPURID",js.getString("UPURID"));
            //hmByItem.put("UPDT",js.getString("UPDT"));
            hmByItem.put("TESTMATERIAL_QTY",json.getString("TESTMATERIAL_QTY"));
            hmByItem.put("ITEM_QTY",json.getString("ITEM_QTY"));
            hmByItem.put("UNIT_NO",json.getString("UNIT_NO"));
            hmByItem.put("DEL","0");
            lmByDeliveryItem.add(hmByItem);

        }
        cloudWebServiceDao.insertWmsCloudDeliveryItem(lmByDeliveryItem);
        /**---------------------3.插入到条码层级关系并更新最内层包装明细信息----------------------------*/
        JSONArray jsoa = JSON.parseArray(js.get("PACKING_DATA").toString());
        System.err.println(jsoa.toString());
        for(int i = 0;i<jsoa.size();i++){
            JSONObject json = (JSONObject) jsoa.get(i);
            String barcodeNo = json.getString("BARCODE_NO");
            String typeNo = json.getString("TYPE_NO");
            //调用插入dao先把顶层父条码插入
            HashMap hmap = new HashMap();
            hmap.put("BARCODE_NO",barcodeNo);
            hmap.put("TYPE_NO",typeNo);
            hmap.put("PARENT_BARCODE_NO","");
            hmap.put("CTURID",userUtils.getUser().get("USERNAME").toString());
            hmap.put("CTDT",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            cloudWebServiceDao.insertWmsCloudPacking(hmap);
            JSONArray jsonArray = (JSONArray) json.get("SUBLIST_DATA");
            if(jsonArray!=null && jsonArray.size()>0){
                query(barcodeNo,jsonArray);
            }

        }
        JSONArray barDetail = (JSONArray) js.get("PACKING_DETAIL_DATA");
        for (int i = 0;i<barDetail.size();i++){
            HashMap detailMap = new HashMap();
            JSONObject jss = (JSONObject) barDetail.get(i);
            detailMap.put("BARCODE_NO",jss.getString("BARCODE_NO"));
            detailMap.put("TYPE_NO",jss.getString("TYPE_NO"));
            JSONObject detailJson = (JSONObject) jss.get("OPI_DATA");
            //
            detailMap.put("LGORT_NO",js.getString("FACT_NO"));
            detailMap.put("FACT_NO",js.getString("FACT_NO"));
            detailMap.put("DELIVERY_NO",detailJson.getString("DELIVERY_NO"));
            detailMap.put("DELIVERY_ITEM",detailJson.getString("DELIVERY_ITEM"));
            detailMap.put("MAT_NO",detailJson.getString("MAT_NO"));
            detailMap.put("MAT_DESC",detailJson.getString("MAT_DESC"));
            detailMap.put("VERSION_NO",detailJson.getString("VERSION_NO"));
            detailMap.put("PRODUCTION_QTY",detailJson.getString("PRODUCTION_QTY"));
            detailMap.put("UNIT_NO",detailJson.getString("UNIT_NO"));
            detailMap.put("BUNIT",detailJson.getString("BUNIT"));
            detailMap.put("BUNIT_QTY",detailJson.getString("BUNIT_QTY"));
            detailMap.put("BATCH_NO",detailJson.getString("BATCH_NO"));
            detailMap.put("BYD_BATCH",detailJson.getString("BYD_BATCH"));
            detailMap.put("PO_NO",detailJson.getString("PO_NO"));
            detailMap.put("PO_TYPE",detailJson.getString("PO_TYPE"));
            detailMap.put("PO_ITEM",detailJson.getString("PO_ITEM"));
            detailMap.put("TESTMATERIAL_FLAG",detailJson.getString("TESTMATERIAL_FLAG"));
            detailMap.put("SPEC_QTY",detailJson.getString("SPEC_QTY"));
            detailMap.put("CONTAINERCODE",detailJson.getString("CONTAINERCODE"));
            detailMap.put("END_FLAG",detailJson.getString("END_FLAG"));
            detailMap.put("PROD_DATE",detailJson.getString("PROD_DATE"));
            detailMap.put("EXPIRY_DATE",detailJson.getString("EXPIRY_DATE"));
            detailMap.put("CREATE_DATE",detailJson.getString("CREATE_DATE"));
            detailMap.put("MEMO",detailJson.getString("MEMO"));
            detailMap.put("DELIVERY_ORDER_NO",detailJson.getString("DELIVERY_ORDER_NO"));
            //detailMap.put("LAST_UPDATE",detailJson.getString("LAST_UPDATE"));
            //更新最里层子条码的明细信息
            cloudWebServiceDao.updateWmsCloudPacking(detailMap);
        }
    //}catch (Exception e){
        //hm.put("STATUS","E");
        //hm.put("MSG",e.getMessage());
    //}
        hm.put("STATUS",(String) js.get("STATUS"));
        return hm;
    }


    //{"deliveryNo":"N190400000000015","posnr":"00001","flag":"2","qty":10,"user":"1","date":"201904291733"}


    public void query(final String barno, JSONArray jsonArray){
        ExecutorService service = Executors.newCachedThreadPool();
        HashMap hm = new HashMap();
        try {
            for(int i = 0;i<jsonArray.size();i++){
                JSONObject jso = (JSONObject) jsonArray.get(i);
                final String nBarno = jso.getString("BARCODE_NO");
                String nType = jso.getString("TYPE_NO");
                hm.put("BARCODE_NO",nBarno);
                hm.put("TYPE_NO",nType);
                hm.put("PARENT_BARCODE_NO",barno);
                hm.put("CTURID",userUtils.getUser().get("USERNAME").toString());
                hm.put("CTDT",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                //调用dao层插入
                cloudWebServiceDao.insertWmsCloudPacking(hm);

                JSONArray nJsonArray = (JSONArray) jso.get("SUBLIST_DATA");
                if(nJsonArray!=null && nJsonArray.size()>0){
                    query(nBarno,nJsonArray);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            service.shutdown();
        }


    }

    @Override
    @WebServicePath(path=WebserviceInfo.WEBCLOUD_PATH,
            methodName = WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDATA)
    public HashMap sendDeliveryData(HashMap hm){
        //System.err.println(hm.toString());
        Object objects[] = new Object[1];
        objects[0] = hm.get("webServiceData");
        //System.err.println("data=========>>>>>>>>>>>>"+objects[0].toString());
        JSONObject js = JSONObject.parseObject(objects[0].toString());
        String status = (String) js.get("STATUS");
        //返回结果
        HashMap hmm = new HashMap();
        if(status==null || status.equals("E")){
            hmm.put("STATUS",(String) js.get("STATUS"));
            hmm.put("MSG",(String) js.get("MSG"));
            return hmm;
        }
        hmm.put("STATUS",(String) js.get("STATUS"));
        hmm.put("DATA",(JSONArray) js.get("DATA"));
        return hmm;
    }

    @Override
    @WebServicePath(path=WebserviceInfo.WEBCLOUD_PATH,methodName = WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDATA_BYBARCODE)
	public HashMap getDeliveryDataByBarcode(HashMap hm) {
    	Object objects[] = new Object[1];
        objects[0] = hm.get("webServiceData");
        JSONObject js = JSONObject.parseObject(objects[0].toString());
        String status = (String) js.get("STATUS");
        //返回结果
        HashMap hmm = new HashMap();
        if(status==null || status.equals("E")){
            hmm.put("STATUS",(String) js.get("STATUS"));
            hmm.put("MSG",(String) js.get("MSG"));
            return hmm;
        }
        hmm.put("STATUS",(String) js.get("STATUS"));
        hmm.put("DATA",((JSONArray) js.get("DATA")).get(0));
        return hmm;
    }



    @Override
    @WebServicePath(path=WebserviceInfo.WEBCLOUD_PATH,
    methodName = WebserviceInfo.WEBCLOUD_METHOD_DELIVERYDETAILDATA)
	public HashMap getDeliveryData(HashMap hm) {
    	Object objects[] = new Object[1];
        objects[0] = hm.get("webServiceData");
        JSONObject js = JSONObject.parseObject(objects[0].toString());
        String status = (String) js.get("STATUS");
        //返回结果
        HashMap hmm = new HashMap();
        if(status==null || status.equals("E")){
            hmm.put("STATUS",(String) js.get("STATUS"));
            hmm.put("MSG",(String) js.get("MSG"));
            return hmm;
        }
        hmm.put("STATUS",(String) js.get("STATUS"));
        hmm.put("DATA",(JSONObject) js.get("DATA"));
        return hmm;
	}


/*	public static void main(String[] args) {
        WebServiceClientUtil webServiceClientUtil = new WebServiceClientUtil();
        Object[] objects= new Object[0];
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deliveryNo","N190500000000010");
        JSONArray jaa =new JSONArray();
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("deliveryNo","N190500000000010");
        jsonObject.put("posnr","00001");
        jsonObject.put("flag","1");
        jsonObject.put("qty","27");
        jsonObject.put("user","admin");
        jsonObject.put("date","2019-05-24 14:55:53");
        jaa.add(jsonObject);
        try {
            objects = webServiceClientUtil.invoke("http://10.43.57.114:8080/byd_mdm/services/pc/deliveryEwmService?wsdl",
                    "getDeliveryData",jaa.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.err.println("*****"+objects[0].toString());
        JSONObject js = JSONObject.parseObject(objects[0].toString());
        js = (JSONObject) js.get("DATA");
        System.err.println(js.toString());
        //js = (JSONObject) js.get("PACKING_DATA");
        //JSONArray jsonArray = JSON.parseArray(js.get("PACKING_DATA").toString());
        String str = "";


    }*/

}
