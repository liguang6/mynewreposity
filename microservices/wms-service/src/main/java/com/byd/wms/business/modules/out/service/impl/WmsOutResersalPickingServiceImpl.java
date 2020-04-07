package com.byd.wms.business.modules.out.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.*;
import com.byd.wms.business.modules.out.dao.WmsOutResersalPickingDao;
import com.byd.wms.business.modules.out.service.WmsOutResersalPickingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Service
public class WmsOutResersalPickingServiceImpl implements WmsOutResersalPickingService {

    @Autowired
    private WmsOutResersalPickingDao wmsOutResersalPickingDao;

    @Autowired
    private UserUtils userUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //fenye?
        List<Map<String, Object>> list= wmsOutResersalPickingDao.selectWhTask(params);
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(list);
        page.setTotal(list.size());
        page.setSize(list.size());

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public Map<String, Object> update(Map<String, Object> params) {
        HashMap hm = new HashMap();
        List<HashMap> itemList = JSON.parseArray(params.get("ITEMLIST").toString(),HashMap.class);
        //System.err.println("itemList.size "+itemList.size());
//        System.err.println(itemList);
        List<HashMap> listWq = new ArrayList<>();
        List<HashMap> listBfQr = new ArrayList<>();
        List<HashMap> listQr = new ArrayList<>();
        List<HashMap> listBfgz = new ArrayList<>();

        List<HashMap> listLb = new ArrayList<>();

        try {
            itemList.forEach((lmap)-> {
                        lmap.put("WERKS", (String) params.get("WERKS"));
                        lmap.put("WH_NUMBER", (String) params.get("WH_NUMBER"));
                        lmap.put("requirementNo", (String) params.get("requirementNo"));
                    });
            listWq = itemList.stream().
                    filter(o -> o.get("WT_STATUS").equals("未清")).collect(Collectors.toList());
            listBfQr = itemList.stream().
                    filter(o -> o.get("WT_STATUS").equals("部分确认")).collect(Collectors.toList());
            listQr = itemList.stream().
                    filter(o -> o.get("WT_STATUS").equals("已确认")).collect(Collectors.toList());
            listBfgz = itemList.stream().
                    filter(o -> o.get("WT_STATUS").equals("部分过账")).collect(Collectors.toList());
//            System.err.println("listWq.size() "+listWq.size());
//            System.err.println("listWq "+listWq);
//            System.err.println("listBfQr.size() "+listBfQr.size());
//            System.err.println("listBfQr "+listBfQr);
//            System.err.println("listQr.size() "+listQr.size());
//            System.err.println("listQr "+listQr);
//            System.err.println("listBfgz.size()"+listBfgz.size());
//            System.err.println("listBfgz "+listBfgz);

            //校验是否负数
            if(listWq.size()>0){
                listWq.forEach(m->{
                    List<Map<String, Object>> lm = wmsOutResersalPickingDao.selectQtyByStock(m);
                    Double RSB_QTY = Double.parseDouble(String.valueOf(lm.get(0).get("RSB_QTY")==null?0:lm.get(0).get("RSB_QTY")));//RSB_QTY
                    //System.err.println("RSB_QTY "+RSB_QTY);
                    if(RSB_QTY-Double.parseDouble(String.valueOf(m.get("QUANTITY")==null?0:m.get("QUANTITY")))<0 ){
                        //System.err.println("RSB_QTY-Double.parseDouble(String.valueOf(m.get(\"QUANTITY\")))<0");
                        hm.put("MESSAGE","预留数量为负数！");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                });
            }

            //未清逻辑
            //下架推荐后没确认的数量，仓库任务的全部数量
            if(listWq.size()>0){
                //l.RSB_QTY - #{s.QUANTITY} WHERE BIN_CODE=#{s.FROM_BIN_CODE}
                //2.STOCK_QTY + #{s.QUANTITY}  WHERE BIN_CODE=#{s.FROM_BIN_CODE}
                boolean Wq1 = wmsOutResersalPickingDao.updateWqByStock(listWq);
            }
            //校验是否负数
            if(listBfQr.size()>0){
                listBfQr.forEach(m->{
                    List<Map<String, Object>> lm = wmsOutResersalPickingDao.selectQtyByStock(m);

                    Double RSB_QTY = Double.parseDouble(String.valueOf(lm.get(0).get("RSB_QTY")==null?0:lm.get(0).get("RSB_QTY")));//RSB_QTY
                    //System.err.println("RSB_QTY "+RSB_QTY);
                    Double XJ_QTY = Double.parseDouble(String.valueOf(lm.get(0).get("XJ_QTY")==null?0:lm.get(0).get("XJ_QTY")));//XJ_QTY
                    //System.err.println("XJ_QTY "+XJ_QTY);
                    if(XJ_QTY-Double.parseDouble(String.valueOf(m.get("CONFIRM_QUANTITY")==null?0:m.get("CONFIRM_QUANTITY")))<0 ){
                        //System.err.println("XJ_QTY-Double.parseDouble(String.valueOf(m.get(\"CONFIRM_QUANTITY\")))<0");
                        hm.put("MESSAGE","下架数量为负数！");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                    if(RSB_QTY-(Double.parseDouble(String.valueOf(m.get("QUANTITY")==null?0:m.get("QUANTITY")))-
                            Double.parseDouble(String.valueOf(m.get("CONFIRM_QUANTITY")==null?0:m.get("CONFIRM_QUANTITY"))))<0 ){
//                        System.err.println("RSB_QTY-(Double.parseDouble(String.valueOf(m.get(\"QUANTITY\")))-\n" +
//                                "                        Double.parseDouble(String.valueOf(m.get(\"CONFIRM_QUANTITY\"))))<0 ");
                        hm.put("MESSAGE","预留数量为负数！");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                });
            }
            //部分确认逻辑
            //下架数量XJ_QTY = CONFIRM_QUANTITY 预留数量RSB_QTY = QUANTITY-CONFIRM_QUANTITY
            if(listBfQr.size()>0){
                //l.XJ_QTY - #{s.CONFIRM_QUANTITY} WHERE BIN_CODE=#{s.FROM_BIN_CODE}
                //2.RSB_QTY- (#{s.QUANTITY}-#{s.CONFIRM_QUANTITY}) WHERE BIN_CODE=#{s.FROM_BIN_CODE}
                //3.STOCK_QTY + #{s.QUANTITY} WHERE BIN_CODE=#{s.FROM_BIN_CODE}
                boolean Bfqr1 = wmsOutResersalPickingDao.updateBfqrByStock(listBfQr);
                //更新需求行项目数量
                wmsOutResersalPickingDao.updateBfqrByReqItem(listBfQr);
            }
            //校验是否负数
            if(listQr.size()>0){
                listQr.forEach(m->{
                    List<Map<String, Object>> lm = wmsOutResersalPickingDao.selectQtyByStock(m);
                    Double XJ_QTY = Double.parseDouble(String.valueOf(lm.get(0).get("XJ_QTY")==null?0:lm.get(0).get("XJ_QTY")));//RSB_QTY
                    //System.err.println("RSB_QTY "+RSB_QTY);
                    if(XJ_QTY-Double.parseDouble(String.valueOf(m.get("CONFIRM_QUANTITY")==null?0:m.get("CONFIRM_QUANTITY")))<0 ){
                        //System.err.println("RSB_QTY-Double.parseDouble(String.valueOf(m.get(\"CONFIRM_QUANTITY\")))<0");
                        hm.put("MESSAGE","下架数量为负数！");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                });
            }
            //已确认逻辑
            //下架推荐后的确认数量
            if(listQr.size()>0){
                //l.XJ_QTY - #{s.CONFIRM_QUANTITY} WHERE BIN_CODE=#{s.FROM_BIN_CODE}
                //2.STOCK_QTY + #{s.CONFIRM_QUANTITY} WHERE BIN_CODE=#{s.FROM_BIN_CODE}
                boolean Yqr1 = wmsOutResersalPickingDao.updateQrByStock(listQr);
                //更新需求行项目数量
                wmsOutResersalPickingDao.updateBfqrByReqItem(listQr);
            }


            //校验是否负数
            if(listBfgz.size()>0){
                listBfgz.forEach(m->{
                    List<Map<String, Object>> lm = wmsOutResersalPickingDao.selectQtyByStock(m);
                    System.err.println("lm             "+lm);
                    System.err.println("111  "+Double.parseDouble(String.valueOf(m.get("CONFIRM_QUANTITY")==null?0:m.get("CONFIRM_QUANTITY"))));
                    System.err.println("222  "+Double.parseDouble(String.valueOf(m.get("REAL_QUANTITY")==null?0:m.get("REAL_QUANTITY"))));
                    Double XJ_QTY = Double.parseDouble(String.valueOf(lm.get(0).get("XJ_QTY")==null?0:lm.get(0).get("XJ_QTY")));//RSB_QTY
                    //System.err.println("RSB_QTY "+RSB_QTY);
                    if(XJ_QTY-(Double.parseDouble(String.valueOf(m.get("CONFIRM_QUANTITY")==null?0:m.get("CONFIRM_QUANTITY")))
                            -Double.parseDouble(String.valueOf(m.get("REAL_QUANTITY")==null?0:m.get("REAL_QUANTITY"))))<0 ){
                        //System.err.println("RSB_QTY-Double.parseDouble(String.valueOf(m.get(\"CONFIRM_QUANTITY\")))<0");
                        hm.put("MESSAGE","下架数量为负数！");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                    
                    Double RSB_QTY = Double.parseDouble(String.valueOf(lm.get(0).get("RSB_QTY")==null?0:lm.get(0).get("RSB_QTY")));//RSB_QTY
                    //System.err.println("RSB_QTY "+RSB_QTY);
                    if(RSB_QTY-(Double.parseDouble(String.valueOf(m.get("QUANTITY")==null?0:m.get("QUANTITY"))) 
                    		- Double.parseDouble(String.valueOf(m.get("CONFIRM_QUANTITY")==null?0:m.get("CONFIRM_QUANTITY"))))<0 ){
                        //System.err.println("RSB_QTY-Double.parseDouble(String.valueOf(m.get(\"QUANTITY\")))<0");
                        hm.put("MESSAGE","预留数量为负数！");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return;
                    }
                });
            }

            //部分过账逻辑
            if(listBfgz.size()>0){
                //
                boolean Yqr1 = wmsOutResersalPickingDao.updateBfgzByStock(listBfgz);
                //更新需求行项目数量
                wmsOutResersalPickingDao.updateBfqrByReqItem2(listBfgz);
            }



            //启动条码逻辑
            if(listBfgz.size()>0){
                listBfgz.forEach((lmap)->{
                    String LABEL_NO = (String) lmap.get("LABEL_NO");
                    if(!StringUtils.isBlank(LABEL_NO)){
                        JSONArray jsonArray = JSON.parseArray(LABEL_NO);
                        String labelNo = "";
                        for (int i=0; i<jsonArray.size();i++){
                            HashMap hashMap = new HashMap();
                            hashMap.putAll(lmap);
                            labelNo = (String) jsonArray.get(i);
                            if(!StringUtils.isBlank(labelNo)){
                                hashMap.put("LABEL_NO",labelNo);
                                listLb.add(hashMap);
                            }
                        }

                    }
                });
            }
            if(listQr.size()>0){
                listQr.forEach((lmap)->{
                    String LABEL_NO = (String) lmap.get("LABEL_NO");
                    if(!StringUtils.isBlank(LABEL_NO)){
                        JSONArray jsonArray = JSON.parseArray(LABEL_NO);
                        String labelNo = "";
                        for (int i=0; i<jsonArray.size();i++){
                            HashMap hashMap = new HashMap();
                            hashMap.putAll(lmap);
                            labelNo = (String) jsonArray.get(i);
                            if(!StringUtils.isBlank(labelNo)){
                                hashMap.put("LABEL_NO",labelNo);
                                listLb.add(hashMap);
                            }
                        }

                    }
                });
            }
            if(listBfQr.size()>0){
                listBfQr.forEach((lmap)->{
                    String LABEL_NO = (String) lmap.get("LABEL_NO");
                    if(!StringUtils.isBlank(LABEL_NO)){
                        JSONArray jsonArray = JSON.parseArray(LABEL_NO);
                        String labelNo = "";
                        for (int i=0; i<jsonArray.size();i++){
                            HashMap hashMap = new HashMap();
                            hashMap.putAll(lmap);
                            labelNo = (String) jsonArray.get(i);
                            if(!StringUtils.isBlank(labelNo)){
                                lmap.put("LABEL_NO",labelNo);
                                listLb.add(hashMap);
                            }
                        }

                    }
                });
            }
            //System.err.println("listLb.size() "+listLb.size());
            //System.err.println(listLb);
            //AAAA是待上架区 把储位更新为待上架区
            if(listLb.size()>0){
                boolean label1 = wmsOutResersalPickingDao.updateBin(listLb);
                boolean label2 = wmsOutResersalPickingDao.updateStatusByLabel(listLb);
            }
            //更新仓库任务状态已取消
            //System.err.println(itemList.toString());
            if(itemList.size()>0){
                boolean b5 = wmsOutResersalPickingDao.updateStatusByTask(itemList);
            }
            //删除已取消仓库任务记录，添加入拣配表

        }catch (Exception e){
            hm.put("MESSAGE","更新失败！");
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }finally {

        }

        return hm;
    }


}
