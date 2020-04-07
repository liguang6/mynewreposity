package com.byd.wms.business.modules.pda.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.out.service.ScannerOutService;
import com.byd.wms.business.modules.pda.service.PdaByWhydService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date 2019/5/6
 * @Description pda储位移动
 **/

@RestController
@RequestMapping("/pda")
public class PdaByWhydController {


    @Autowired
    private PdaByWhydService pdaByWhydService;



    /**
     * 查询条码信息
     * @param
     * @return
     */
    @RequestMapping("/queryScannerByPda")
    @CrossOrigin
    public List queryByBarcode(@RequestBody Map<String,Object> data){
        System.err.println("queryByBarcode===>>>"+data.toString());
        List<Map<String,Object>> list = pdaByWhydService.queryByBarcode(data);
        System.err.println(list.toString());
        return list;
    }

    /**
     * 校验储位
     * @param
     * @return
     */
    @RequestMapping("/validateBinCode")
    @CrossOrigin
    public List<Map<String,Object>> validateBinCode(@RequestBody Map<String,Object> data){
        System.err.println("validateBinCode===>>>"+data.toString());
        List<Map<String,Object>> lmap = pdaByWhydService.validateBinCode(data);
        System.err.println(lmap.toString());
        return lmap;
    }

    /**
     * 提交
     * @param data
     * @return
     */
    @RequestMapping("/commitByHwyd")
    @CrossOrigin
    public R commitByHwyd(@RequestBody Map<String,Object> data) {
        //System.err.println("commitByHwyd===>>>"+data.toString());
        ArrayList al = (ArrayList) data.get("params");
        HashMap hm = (HashMap) al.get(0);
        //校验是否有这个源储位
        String yBinCode = (String) hm.get("yBin_Code");
        String mbBinCode = (String) hm.get("mbBin_Code");
        int boxQty = (int) hm.get("BOX_QTY");
        //System.err.println(yBinCode);
        //System.err.println(mbBinCode);
        List<Map<String, Object>> listByBinCode = pdaByWhydService.queryBin(hm);
        //System.err.println(":listByBinCode   "+listByBinCode.toString());
        if(listByBinCode.size()==0){
            return R.error("工厂:"+hm.get("WERKS")+"下不存在储位:"+hm.get("BIN_CODE")+"");
        }
        //
        boolean isOk= false;
        List<Map<String, Object>> listByStockQty = pdaByWhydService.queryStockQty(hm);
        if(listByStockQty!=null && listByStockQty.size()>0){
            int stockQty = new BigDecimal(String.valueOf(listByStockQty.get(0).get("STOCK_QTY"))).intValue() ;
            //System.err.println("stockQty   "+stockQty);
            //System.err.println("boxQty   "+boxQty);
            if(stockQty > boxQty){
                //需要拆分
                //System.err.println("走拆分");
                hm.put("STOCK_QTY",stockQty-boxQty);
                hm.put("mbBin_Code",mbBinCode);
                hm.put("qty",boxQty);
                isOk= pdaByWhydService.commitByHwyd2(hm);
            }else {
                //不需要拆分
                //System.err.println("不走拆分");
                hm.put("STOCK_QTY",boxQty);
                hm.put("mbBin_Code",mbBinCode);
                hm.put("qty",boxQty);
                isOk=pdaByWhydService.commitByHwyd(hm);
            }

        }else {
            return R.error("操作失败,库存表数量找不到！");
        }
        if(!isOk) {
            return R.error("操作失败");
        }
        //System.err.println(R.ok());
        return R.ok();
    }



}
