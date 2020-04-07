package com.byd.wms.business.modules.returnpda.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.*;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;
import com.byd.wms.business.modules.returngoods.dao.WorkshopReturnDao;
import com.byd.wms.business.modules.returnpda.dao.WmsReMaterialPdaDao;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialPdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialWarehouseService;

import java.util.*;

/**
 * @author liguang6
 * @date 2019/12/12 11:47
 * @title
 */
@Service
public class WmsReMaterilPdaServiceImpl implements WmsReMaterialPdaService {
    @Autowired
    private WmsReMaterialPdaDao wmsReMaterialPdaDao;
    @Autowired
    private WmsCTxtService wmsCTxtService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private CommonService commonService;
    @Autowired
    private WorkshopReturnDao workshopReturnDao;
    @Autowired
    WmsCDocNoService wmscDocNoService;
    @Autowired
    WmsReMaterialPdaService wmsReMaterialPdaService;
    @Autowired
    WmsReMaterialWarehouseService wmsReMaterialWareHouseService;


    @Override
    public R validateScanCache(Map<String,Object> params) {
        return wmsReMaterialWareHouseService.validatePoReceiptLable(params);
    }
    /**
     * 将扫秒信息插入缓存表
     * @param params
     * @return page
     */
    @Override
    public void insertScanCache(Map<String,Object> params) {
        //插入缓存表
         wmsReMaterialPdaDao.insertScanCache(params);
    }
    /**
     * 将条码插入条码明细表
     * @param params
     * @return page
     */
    @Override
    public void insertBarCodeLog(Map<String,Object> params) {
        List<Map<String,Object>> labelList = wmsReMaterialPdaService.getAllScanInfo(params);
        String labelArray[] = new String[labelList.size()];
        for (int i =0 ;i<labelList.size();i++){
            labelArray[i] = labelList.get(i).get("LABEL_NO").toString();
        }
        params.put("LABEL_NO",labelArray);
        wmsReMaterialPdaDao.insertBarCodeLog(params);
    }
    /**
     * 条码号用数组保存到对应WMS凭证明细表。
     * @param params
     * @return page
     */
    @Override
    public void insertWmsPZ(Map<String,Object> params) {
        List<Map<String,Object>> labelList = (List<Map<String,Object>>)params.get("list");
        List<Map<String,Object>> labelList1 = wmsReMaterialPdaService.getAllScanInfo(params);
        String labelArray[] = new String[labelList1.size()];
        for (int i =0 ;i<labelList1.size();i++){
            labelArray[i] = labelList1.get(i).get("LABEL_NO").toString();
        }
        params.put("LABEL_NO",labelArray);
        params.put("HEADER_TXT",labelList.get(0).get("HEADER_TXT").toString());
        params.put("OPERATION_ITEM",params.get("TIME_SLOT"));
        params.put("MAT_DOC",params.get("SAP_NO"));
        wmsReMaterialPdaDao.insertWmsPZ(params);

    }
    /**
     * 更新标签表数据（库存类型，库位，状态=08，更新人，更新时间）
     * @param params
     * @return page
     */
    @Override
    public void updataCoreLabel(Map<String,Object> params) {
        List<Map<String,Object>> labelList = wmsReMaterialPdaService.getAllScanInfo(params);
        params.put("list1",labelList);
        wmsReMaterialPdaDao.updataCoreLabel(params);
    }
    /**
     * 保存数据
     * @param params
     * @return void
     */
    @Override
    public void saveData(Map<String,Object> params){
        List<Map<String,Object>> dataList = new ArrayList<>();
        dataList = (List<Map<String, Object>>)JSON.parse(params.get("params").toString());
        params.put("dataList",dataList);
        wmsReMaterialPdaDao.saveData(params);
    }
    /**
     * 分组获取数据表
     * @param params
     * @return page
     */
    @Override
    public PageUtils getPorecCache(Map<String, Object> params) {
        //获取采购订单收货缓存信息
        params= ParamsFilterUtils.paramFilter(params);
        String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
        String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
        int start = 0;int end = 0;
        int count=1;
        if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }else {
            end=count;
        }
        List<Map<String,Object>> result = new ArrayList<>();
        Page page=new Query<Map<String,Object>>(params).getPage();
        result = wmsReMaterialPdaDao.getPorecCache(params);
        page.setRecords(result);
        page.setTotal(count);
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }
    /**
     * 获取条码明细
     * @param JSON.Map rowdatas
     * @return page
     */
    @Override
    public PageUtils getScanInfo(Map<String,Object> params){
        List<Map<String,Object>> result = new ArrayList<>();
        List<Map<String,Object>> a = new ArrayList<>();
        Page page = new Query<Map<String,Object>>(params).getPage();
        Map<String,Object> map = new TreeMap<>();
        map = (Map)JSON.parse(params.get("params").toString());
        map.putAll(params);
        int i = 0;
          a = wmsReMaterialPdaDao.getScanInfo(map);
        for(Map<String,Object> map1:a){
           i+=1;
           map1.put("ROW",i);
           result.add(map1);
       }
        page.setRecords(result);
        page.setTotal(result.size());
        return new PageUtils(page);
    }
    @Override
    public List<Map<String,Object>> getAllScanInfo(Map<String,Object> params){
        List<Map<String,Object>> result = wmsReMaterialPdaDao.getAllScanInfo(params);
        return result;
    }
    /**
     * 删除条码明细
     * @param params
     * @return void
     */
    @Override
    public int removeScanInfo(Map<String,Object> params){
             return wmsReMaterialPdaDao.removeScanInfo(params);
    }

     @Override
    public Map<String, Object> getWmsDocByLabelNo(Map<String,Object> params){
         //通过标签号带出信息
         Map<String,Object> result = new TreeMap<>();
         Map<String,Object> a = new TreeMap<>();
         Map<String,Object> b = new TreeMap<>();
         Map<String,Object> c = new TreeMap<>();
         Map<String,Object> d = new TreeMap<>();
         //获取退货库位（LGORT），接收库位（F_LGORT），库存类型（SOBKZ）
         // 供应商代码（LIFNR）, //测试标签数据CSBQ0000001544
         a = wmsReMaterialPdaDao.getWmsDocByLabelNo(params);
         //获取库存类型，供应商代码，装箱数量
         b = wmsReMaterialPdaDao.getWmsQTYByLabel(params);
         //获取批次
         c = wmsReMaterialPdaDao.getWmsBatchByLabel(params);
         Map<String,String>txtMap=new HashMap<String,String>();
         if(a !=null && !a.isEmpty()){
             a.put("BATCH",c.get("BATCH"));
             a.put("BOX_QTY",b.get("BOX_QTY"));//获取装箱数量
             a.put("UNIT",b.get("UNIT"));
             a.put("MAKTX",b.get("MAKTX"));
             result = a;
         }else {
             b.put("BATCH",c.get("BATCH"));
             b.put("MOVE_STLOC","");
             b.put("LGORT","");
             result = b;
         }
         //行文本：根据行文本配置规则自动生成，允许修改。
         txtMap.put("WERKS", params.get("WERKS").toString());
         txtMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME").toString());
         txtMap.put("FULL_NAME", params.get("USER_NAME").toString());
         Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
             if(!"success".equals(txt.get("msg").toString())) {
                 throw new RuntimeException(txt.get("msg").toString());
             }else {
                 result.put("HEADER_TXT", txt.get("txtruleitem"));
             }
          return result;  }

    /**生成退货订单号
     * 确认过账
     * @param JSON.list<Map> rowdatas
     * @return String WMS_NO,SAP_NO
     */
    @Override
    @Transactional
    public Map<String, Object> confirmWorkshopReturn(Map<String,Object> params) {
        String RETURN_NO = "";
        Map<String,Object> result = new TreeMap<>();
        Map<String, Object> currentUser = userUtils.getUser();
        JSONArray jarr = JSON.parseArray(params.get("params").toString());
        //【退货单抬头表】
        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("WERKS", params.get("WERKS").toString());
        params2.put("WMS_DOC_TYPE", "06");
        RETURN_NO = wmscDocNoService.getDocNo(params2).get("docno").toString();
        if (null == RETURN_NO) {
            throw new RuntimeException("没有生成退料单号！");
        }
        String HEADER_TXT = "";
        Map<String, Object> headMap = new HashMap<String, Object>();
        headMap.put("RETURN_NO", RETURN_NO);
        headMap.put("RETURN_TYPE", "06");        //ModBy:YK190621 改为存BUSINESS_CLASS
        headMap.put("RETURN_STATUS", "00");
        headMap.put("WERKS", params.get("WERKS").toString());
        headMap.put("LIFNR", "");
        headMap.put("LIKTX", "");
        headMap.put("HEADER_TXT", HEADER_TXT);
        headMap.put("IS_AUTO", "");
        headMap.put("DEL", "0");
        headMap.put("CREATOR", params.get("CREATOR"));
        headMap.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        headMap.put("EDITOR", "");
        headMap.put("EDIT_DATE", "");
        workshopReturnDao.insertWmsOutReturnHead(headMap);
        //【退货单行项目表】
        int RETURN_ITEM_NO = 1;
        for (int j = 0; j < jarr.size(); j++) {
            JSONObject outData = jarr.getJSONObject(j);
            Map<String, Object> itemMap = new HashMap<String, Object>();
            Map<String, Object> itemDetailMap = new HashMap<String, Object>();
            itemMap.put("RETURN_NO", RETURN_NO);
            itemMap.put("RETURN_ITEM_NO", RETURN_ITEM_NO);
            itemMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME").toString());
            itemMap.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE").toString());
            itemMap.put("WERKS", params.get("WERKS").toString());
            itemMap.put("MOVE_STLOC", outData.getString("MOVE_STLOC").toString());
            itemMap.put("LGORT", outData.getString("LGORT").toString());
            itemMap.put("MATNR", outData.getString("MATNR"));
            itemMap.put("MAKTX", outData.getString("MAKTX"));
            itemMap.put("UNIT", outData.getString("MEINS"));
            itemMap.put("TOTAL_RETURN_QTY", Float.valueOf(outData.getString("TOTALL_QTY").trim()));
            itemMap.put("RETURN_PEOPLE", currentUser.get("USERNAME"));
            itemMap.put("ITEM_STATUS", "00");
            itemMap.put("MEMO", (outData.getString("MEMO") == null) ? "" : outData.getString("MEMO"));
            itemMap.put("RECEIPT_NO", (outData.getString("RECEIPT_NO") == null) ? "" : outData.getString("RECEIPT_NO"));
            itemMap.put("RECEIPT_ITEM_NO", (outData.getString("RECEIPT_ITEM_NO") == null) ? "" : outData.getString("RECEIPT_ITEM_NO"));
            itemMap.put("LIFNR", (outData.getString("LIFNR") == null) ? "" : outData.getString("LIFNR"));
            itemMap.put("LIKTX", (outData.getString("LIKTX") == null) ? "" : outData.getString("LIKTX"));
            String PO_NO = "";
            String PO_ITEM_NO = "";
            if ("69".equals(params.get("BUSINESS_NAME").toString())) {
                PO_NO = (outData.getString("RSNUM") == null) ? "" : outData.getString("RSNUM");
                PO_ITEM_NO = (outData.getString("RSPOS") == null) ? "" : outData.getString("RSPOS");
            } else {
                PO_NO = (outData.getString("PO_NO") == null) ? "" : outData.getString("PO_NO");
                PO_ITEM_NO = (outData.getString("PO_ITEM_NO") == null) ? "" : outData.getString("PO_ITEM_NO");
            }
            itemMap.put("PO_NO", PO_NO);
            itemMap.put("PO_ITEM_NO", PO_ITEM_NO);
            itemMap.put("SAP_OUT_NO", (outData.getString("SAP_OUT_NO") == null) ? "" : outData.getString("SAP_OUT_NO"));
            itemMap.put("SAP_OUT_ITEM_NO", (outData.getString("SAP_OUT_ITEM_NO") == null) ? "" : outData.getString("SAP_OUT_ITEM_NO"));
            itemMap.put("RSNUM", (outData.getString("RSNUM") == null) ? "" : outData.getString("RSNUM"));
            itemMap.put("RSPOS", (outData.getString("RSPOS") == null) ? "" : outData.getString("RSPOS"));
            itemMap.put("SAP_MATDOC_NO", (outData.getString("SAP_MATDOC_NO") == null) ? "" : outData.getString("SAP_MATDOC_NO"));
            itemMap.put("SAP_MATDOC_ITEM_NO", (outData.getString("SAP_MATDOC_ITEM_NO") == null) ? "" : outData.getString("SAP_MATDOC_ITEM_NO"));
            itemMap.put("INBOUND_NO", (outData.getString("INBOUND_NO") == null) ? "" : outData.getString("INBOUND_NO"));
            itemMap.put("INBOUND_ITEM_NO", (outData.getString("INBOUND_ITEM_NO") == null) ? "" : outData.getString("INBOUND_ITEM_NO"));
            itemMap.put("SQE", (outData.getString("SQE") == null) ? "" : outData.getString("SQE"));
            itemMap.put("PICK_FLAG", "0");
            itemMap.put("DEL", "0");
            itemMap.put("CREATOR", params.get("CREATOR"));
            itemMap.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            itemMap.put("EDITOR", "");
            itemMap.put("EDIT_DATE", "");


            itemDetailMap.put("RETURN_NO", RETURN_NO);
            itemDetailMap.put("RETURN_ITEM_NO", RETURN_ITEM_NO);
            itemDetailMap.put("MATNR", outData.getString("MATNR"));
            itemDetailMap.put("F_BATCH", (outData.getString("F_BATCH") == null) ? "" : outData.getString("F_BATCH"));
            itemDetailMap.put("BATCH", (outData.getString("BATCH") == null) ? "" : outData.getString("BATCH"));
            itemDetailMap.put("RETURN_QTY", Float.valueOf(outData.getString("TOTALL_QTY").trim()));
            itemDetailMap.put("LIFNR", (outData.getString("LIFNR") == null) ? "" : outData.getString("LIFNR"));
            itemDetailMap.put("SOBKZ", outData.getString("SOBKZ"));
            itemDetailMap.put("BIN_CODE", (outData.getString("BIN_CODE") == null) ? "" : outData.getString("BIN_CODE"));
            itemDetailMap.put("BIN_CODE_XJ", (outData.getString("BIN_CODE_XJ") == null) ? "" : outData.getString("BIN_CODE_XJ"));
            itemDetailMap.put("XJ_QTY", (outData.getString("XJ_QTY") == null) ? "" : outData.getString("XJ_QTY"));
            itemDetailMap.put("REAL_QTY", (outData.getString("REAL_QTY") == null) ? "" : outData.getString("REAL_QTY"));
            itemDetailMap.put("ITEM_TEXT", (outData.getString("ITEM_TEXT") == null) ? "" : outData.getString("ITEM_TEXT"));
            itemDetailMap.put("RETURN_PEOPLE", currentUser.get("USERNAME"));
            itemDetailMap.put("MEMO", (outData.getString("MEMO") == null) ? "" : outData.getString("MEMO"));
            itemDetailMap.put("DEL", "0");
            itemDetailMap.put("CREATOR", params.get("CREATOR"));
            itemDetailMap.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            itemDetailMap.put("EDITOR", "");
            itemDetailMap.put("EDIT_DATE", "");
            itemDetailMap.put("LABEL_NO", "");

            workshopReturnDao.insertWmsOutReturnItem(itemMap);
            workshopReturnDao.insertWmsOutReturnItemDetail(itemDetailMap);
            RETURN_ITEM_NO++;
            params.put("RETURN_NO",RETURN_NO);
        }
        result = wmsReMaterialPdaService.confirmWorkshopReturned(params);
        result.put("CREAT_DATE",jarr.getJSONObject(0).getString("CREAT_DATE"));
        return result;

    }
    @Override
    public Map<String, Object> confirmWorkshopReturned(Map<String,Object> params){
        Map<String,Object> result = new TreeMap<>();
        String WMS_NO = "";//wms凭证
        String SAP_NO = "";//sap凭证
        Map<String,Object> currentUser = userUtils.getUser();
        JSONArray jarr = JSON.parseArray(params.get("params").toString());
        String SAP_MOVE_TYPE = (String)params.get("SAP_MOVE_TYPE");
        String WMS_MOVE_TYPE = (jarr.getJSONObject(0).getString("WMS_MOVE_TYPE")!=null)?jarr.getJSONObject(0).getString("WMS_MOVE_TYPE"):"";
        /**
         * 保存WMS凭证记录抬头和明细
         * @param head PZ_DATE：凭证日期  JZ_DATE：记账日期  HEADER_TXT：头文本 TYPE：凭证类型  SAP_MOVE_TYPE：SAP移动类型
         * @param matList
         * @return String WMS_NO
         */
        Map<String, Object> head = new TreeMap<>();
        List<Map<String, Object>> matList = new ArrayList<>();
        head.put("WERKS", params.get("WERKS"));
        head.put("PZ_DATE", params.get("docDate"));
        head.put("JZ_DATE", params.get("debitDate"));
        head.put("HEADER_TXT", jarr.getJSONObject(0).getString("HEADER_TXT"));
        head.put("TOTALL_QTY", jarr.getJSONObject(0).getString("TOTALL_QTY"));
        head.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
        head.put("TYPE", "00");
        head.put("CREATOR", params.get("CREATOR"));
        head.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        for(int i=0;i<jarr.size();i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            JSONObject itemData = jarr.getJSONObject(i);
            List<Map<String, Object>> moveTypeList = new ArrayList<>();
            String REVERSAL_FLAG = "0";
            String CANCEL_FLAG = "0";
            item.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE"));
            item.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
            item.put("BUSINESS_CLASS", params.get("BUSINESS_CLASS"));
            item.put("SOBKZ", itemData.getString("SOBKZ"));
            item.put("MATNR", itemData.getString("MATNR"));
            item.put("LGORT", itemData.getString("LGORT"));
            item.put("QTY_WMS", itemData.getString("TOTALL_QTY"));
            item.put("QTY_SAP",itemData.getString("TOTALL_QTY"));
            item.put("LIFNR", itemData.getString("LIFNR"));
            item.put("WERKS", params.get("WERKS"));
            item.put("PZ_DATE", params.get("docDate"));
            item.put("JZ_DATE", params.get("debitDate"));
            item.put("HEADER_TXT", jarr.getJSONObject(0).getString("HEADER_TXT"));
            item.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
            item.put("TYPE", "00");
            item.put("BIN_CODE", params.get("BIN_CODE"));
            item.put("CREATOR", params.get("CREATOR"));
            item.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));

            try {
                moveTypeList = workshopReturnDao.getSapWmsMoveType(item);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            if (moveTypeList.size() == 0) {
                throw new RuntimeException("没有配置正确的移动类型！");
            } else {
                item.put("WMS_MOVE_TYPE", moveTypeList.get(0).get("WMS_MOVE_TYPE"));
                item.put("SAP_MOVE_TYPE", moveTypeList.get(0).get("SAP_MOVE_TYPE"));
            }
            WMS_MOVE_TYPE = moveTypeList.get(0).get("WMS_MOVE_TYPE").toString();
            SAP_MOVE_TYPE = moveTypeList.get(0).get("SAP_MOVE_TYPE").toString();

            item.put("ITEM_TEXT", itemData.getString("ITEM_TEXT"));
            item.put("UNIT", itemData.getString("UNIT"));
            item.put("MAKTX", itemData.getString("MAKTX"));
            item.put("CUSTOMER","");
            item.put("MOVE_PLANT","");
            item.put("MOVE_BATCH","");
            matList.add(item);


        }if(!WMS_MOVE_TYPE.equals("")) {
            WMS_NO = commonService.saveWMSDoc(head,matList);
        }
        /**
         * SAP过账通用方法供异步线程调用
         * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
         * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
         * SAP过账失败回滚
         * @param params
         * 过账内容键值对： WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期 HEADER_TXT：抬头文本 ,
         * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型,BUSINESS_CLASS:业务分类
         * 过账行项目List:matList
         * @return String SAP_NO
         */
        if(!SAP_MOVE_TYPE.equals("")) {	//需过帐SAP
            params.put("WMS_NO", WMS_NO);
            params.putAll(head);
            params.put("matList", matList);
            try {
                SAP_NO = commonService.doSapPost(params);
            }catch(Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        result.put("WMS_NO",WMS_NO);
        result.put("SAP_NO",SAP_NO);
        return result;
    }
}
