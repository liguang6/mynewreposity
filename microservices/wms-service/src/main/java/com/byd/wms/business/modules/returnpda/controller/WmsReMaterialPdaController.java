package com.byd.wms.business.modules.returnpda.controller;

import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.returngoods.service.WorkshopReturnService;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialPdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/**
 * @author liguang6
 * @date 2019/12/12 10:13
 * @title 平面库余料退回(312)
 */
@RestController
@RequestMapping("/returnpda/wmsRematerialpda")
public class WmsReMaterialPdaController {
    @Autowired
    private WmsReMaterialPdaService wmsReMaterialPdaService;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    WorkshopReturnService workshopReturnService;
    @Autowired
    CommonService commonService;

    /**
     * 初始化请求参数
     * @param params
     * @return
     */
    public Map<String,Object> initParams(Map<String,Object> params){
        try {
            Map<String, Object> currentUser = userUtils.getUser();
            params.put("USERNAME", currentUser.get("USERNAME"));
            params.put("FULL_NAME", currentUser.get("FULL_NAME"));
            String curDate = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            params.put("CREATOR", params.get("USERNAME") + "：" + params.get("FULL_NAME"));
            params.put("CREATE_DATE", curDate);


        }catch (Exception e){
            throw new RuntimeException("初始化请求参数出错!");
        }

        return params;
    }

/**
 * 初始化页面
 * @param params
 * @return page
 */
@RequestMapping("/initialize")
public R initialize(@RequestParam Map<String, Object> params) {
    initParams(params);
    PageUtils page = wmsReMaterialPdaService.getPorecCache(params);
    return R.ok().put("page", page);

}


    /**1公共逻辑：验证，插入，删除缓存表数据
     * 2扫码：通过标签号获取退货库位（LGORT），接收库位（F_LGORT），库存类型（SOBKZ），供应商代码（LIFNR）到页面(扫描条码或者输入条码回车确定)
     * WMS凭证明细表【WMS_CORE_WMSDOC_ITEM】-LABEL_NO和SAP_MOVE_TYPE=311
     * @param params
     * @return page
     */
    @RequestMapping("/scan")
    public R getWmsDocByLabelNo(@RequestParam Map<String, Object> params) {
        initParams(params);
        //PDA扫描缓存表【WMS_PDA_SCAN_CACHE】是否有对应账号和业务类型的数据，
        Map currentUser = userUtils.getUser();
        String LABEL_NO = params.get("LABEL_NO") == null ? "" : params.get("LABEL_NO").toString().trim();
            Map<String, Object> scanCache = new TreeMap<>();
            if(LABEL_NO!="") {
                //获得扫入条码信息
                Map<String, Object> scanCacheInfo = new TreeMap<>();
                scanCacheInfo = wmsReMaterialPdaService.getWmsDocByLabelNo(params);
                //将条码信息插入缓存表
                scanCacheInfo.putAll(params);
                scanCacheInfo.put("QTY", scanCacheInfo.get("BOX_QTY"));
                wmsReMaterialPdaService.insertScanCache(scanCacheInfo);
            }
            //将缓存表信息返回给页面list
            PageUtils page = wmsReMaterialPdaService.getPorecCache(params);
            return R.ok().put("page", page);

    }


    /**
     * 判断条码重复
     * @param params
     * @return datagrid*/
    @RequestMapping("/valiateLabel")
    public R validatePoReceiptLable(@RequestParam Map<String,Object> params){
        initParams(params);
            try{
                return wmsReMaterialPdaService.validateScanCache(params);
            } catch (Exception e) {
                //e.printStackTrace();
                return R.error(e.getMessage());
            }
        }



    /**
     * 获取条码明细
     * @param params
     * @return datagrid*/

    @RequestMapping("/scanInfo")
    public R getScanInfo(@RequestParam Map<String,Object> params) {
        initParams(params);

        PageUtils page = wmsReMaterialPdaService.getScanInfo(params);

        return R.ok().put("page", page);

    }


    /**
     * 删除条码明细
     * @param JSON.List<Map> params
     * @return*/
    @RequestMapping("/removeScanInfo")
    public R removeScanInfo(@RequestParam Map<String,Object> params){
        initParams(params);
         List<Map<String,Object>> list = new ArrayList<>();
         list = (List<Map<String,Object>>)JSONArray.parse(params.get("params").toString());
         params.put("list",list);
        int a =wmsReMaterialPdaService.removeScanInfo(params);
        return R.ok().put("msg","成功删除"+a+"条信息");
    }
    /**
     * 保存数据
     * @param JSON
     * @param R
     */

    @RequestMapping("/saveData")
    public R saveData(@RequestParam Map<String,Object> params){
        initParams(params);
        try {
            wmsReMaterialPdaService.saveData(params);
        }catch (Exception e){
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.ok().put("msg","保存成功");
    }

    /**
     * 点确认触发过账逻辑
     * @param params
     * @return
     */
    @RequestMapping("/confirmWorkshopReturn")
    public R confirmWorkshopReturn(@RequestParam Map<String, Object> params) {
        initParams(params);
        Map<String,Object> result = new TreeMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        list = (List<Map<String, Object>>)JSONArray.parse(params.get("params").toString());
        params.put("list",list);
        String msg = "";

        try {
            result = wmsReMaterialPdaService.confirmWorkshopReturn(params);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
        List<Map<String,Object>> labelList = wmsReMaterialPdaService.getAllScanInfo(params);
        Date startDate = DateUtils.stringToDate(labelList.get(0).get("CREAT_DATE").toString(),"yyyy-MM-dd HH:mm:ss");
        Date endDate = new Date();
        //计算执行时间
        String returnFlag = "sec";
        result.put("TIME_SLOT",this.computeTimeSlot(startDate,endDate,returnFlag));
        result.put("msg","过账成功");
        //过账成功把对应条码保存到扫描条码记录表【WMS_REPORT_BARCODE_LOG】
        wmsReMaterialPdaService.insertBarCodeLog(params);
        //操作时间保存到WMS凭证抬头表；把条码号用数组保存到对应WMS凭证明细表。
      /*  params.putAll(result);
        wmsReMaterialPdaService.insertWmsPZ(params);*/
        //更新标签表数据（库存类型，库位，状态=08，更新人，更新时间）
        wmsReMaterialPdaService.updataCoreLabel(params);
        //删除,过账成功删除这个表对应条码数据
        wmsReMaterialPdaService.removeScanInfo(params);

         return R.ok().put("res",result);

    }

    /**
     * 计算两个时间段相差的秒数
     * @param date1
     * @param date2
     */
    public long computeTimeSlot(Date startDate, Date endDate, String returnFlag) {
        long lstartDate = startDate.getTime();
        long lendDate = endDate.getTime();
        long diff = (lstartDate < lendDate) ? (lendDate - lstartDate) : (lstartDate - lendDate);
        long day = diff / (24 * 60 * 60 * 1000);
        long hour = diff / (60 * 60 * 1000) - day * 24;
        long min = diff / (60 * 1000) - day * 24 * 60 - hour * 60;
        long sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
        long a = 0;
        System.out.println("date1 与 date2 相差 " + day + "天" + hour + "小时" + min + "分" + sec + "秒");
        switch (returnFlag) {
            case "day":
                 a = day;break;
            case "hour":
                 a =hour;break;
            case "min":
                 a =min;break;
            case "sec":
                 a =sec;break;
            default: a = 0;
                break;
        }
        return a;
    }

}








