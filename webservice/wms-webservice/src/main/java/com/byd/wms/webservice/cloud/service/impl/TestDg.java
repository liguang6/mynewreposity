package com.byd.wms.webservice.cloud.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2019/5/9 0009.
 */
public class TestDg {

    //事务注解
    public static void test(final String barno, JSONArray jsonArray){
        ExecutorService service = Executors.newCachedThreadPool();
        HashMap hm = new HashMap();
        //String barno = jsonObject.getString("barno");
        //String type = jsonObject.getString("type");
        //JSONArray jsonArray = jsonObject.get("nbarno");
        //System.err.println(jsonArray.toString());
        try {
            for(int i = 0;i<jsonArray.size();i++){
                JSONObject jso = (JSONObject) jsonArray.get(i);
                final String nBarno = jso.getString("BARCODE_NO");
                String nType = jso.getString("TYPE_NO");
                hm.put("BARCODE_NO",nBarno);
                hm.put("TYPE_NO",nType);
                hm.put("PARENT_BARCODE_NO",barno);

                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        //调用dao层插入
                        String insertStr ="insert into table('TYPE_NO','BARCODE_NO','PARENT_BARCODE_NO') values(%s,%s,%s)";
                        insertStr = String.format(insertStr,nType,nBarno,barno);
                        System.err.println("insertStr===>>> "+insertStr.toString());

                    }
                });

                JSONArray nJsonArray = (JSONArray) jso.get("SUBLIST_DATA");
                if(nJsonArray!=null && nJsonArray.size()>0){
                    test(nBarno,nJsonArray);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            service.shutdown();
        }


    }

/*    public static void main(String[] args) {

        String str = "{\"PACKING_DATA\": [{\n" +
                "\n" +
                "\t\t\t\"BARCODE_NO\": \"00000000000011\",\n" +
                "\t\t\t\"TYPE_NO\": \"1\",\n" +
                "\t\t\t\"SUBLIST_DATA\": [{\n" +
                "\t\t\t\t\t\"BARCODE_NO\": \"00000000000021\",\n" +
                "\t\t\t\t\t\"TYPE_NO\": \"1\",\n" +
                "\t\t\t\t\t\"SUBLIST_DATA\": [{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000031\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"2\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000032\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"2\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"BARCODE_NO\": \"00000000000022\",\n" +
                "\t\t\t\t\t\"TYPE_NO\": \"1\",\n" +
                "\t\t\t\t\t\"SUBLIST_DATA\": [{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000031\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"1\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000032\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"1\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\n" +
                "\t\t\t\"BARCODE_NO\": \"00000000000012\",\n" +
                "\t\t\t\"TYPE_NO\": \"1\",\n" +
                "\t\t\t\"SUBLIST_DATA\": [{\n" +
                "\t\t\t\t\t\"BARCODE_NO\": \"00000000000021\",\n" +
                "\t\t\t\t\t\"TYPE_NO\": \"1\",\n" +
                "\t\t\t\t\t\"SUBLIST_DATA\": [{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000031\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"2\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000032\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"2\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"BARCODE_NO\": \"00000000000022\",\n" +
                "\t\t\t\t\t\"TYPE_NO\": \"1\",\n" +
                "\t\t\t\t\t\"SUBLIST_DATA\": [{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000031\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"1\"\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"BARCODE_NO\": \"00000000000032\",\n" +
                "\t\t\t\t\t\t\t\"TYPE_NO\": \"1\"\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t]\n}";
        //System.err.println(str);
        JSONObject jsonObject = JSON.parseObject(str);
        System.err.println(jsonObject.toString());
        JSONArray jsoa = (JSONArray) jsonObject.get("PACKING_DATA");
        for(int i = 0;i<jsoa.size();i++){
            JSONObject json = (JSONObject) jsoa.get(i);
            String barcodeNo = json.getString("BARCODE_NO");
            String typeNo = json.getString("TYPE_NO");
            //调用插入dao先把顶层父条码插入
            String insertStr ="insert into table('TYPE_NO','BARCODE_NO','PARENT_BARCODE_NO') values(%s,%s,%s)";
            insertStr = String.format(insertStr,typeNo,barcodeNo,"");
            System.err.println("insertStr===>>> "+insertStr.toString());
            JSONArray jsonArray = (JSONArray) json.get("SUBLIST_DATA");
            test(barcodeNo,jsonArray);
        }


        //
        JSONArray barDetail = (JSONArray) jsonObject.get("PACKING_DETAIL_DATA");
        HashMap detailMap = new HashMap();
        for (int i = 0;i<barDetail.size();i++){
            JSONObject js = (JSONObject) barDetail.get(i);
            detailMap.put("barcodeNo",js.getString("BARCODE_NO"));
            detailMap.put("typeNo",js.getString("TYPE_NO"));
            detailMap.put("opiData",js.getString("OPI_DATA"));
            //更新最里层子条码的明细信息


        }


    }*/

}