package com.byd.web.returngoods.controller;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.returngoods.service.WmsReMaterialPdaRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author liguang6
 * @date 2019/12/11 17:36
 * @title 平面库余料退库
 */
@RestController
@RequestMapping("/returngoods/reMaterial")

public class WmsReMaterialPdaController {
    @Autowired
    private  WmsReMaterialPdaRemote wmsReMaterialPdaRemote;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    protected HttpSession session;


    //初始化数据
    public Map<String,Object> initParams(Map<String,Object> params){
        params.put("WH_NUMBER", session.getAttribute("warehouse"));
        params.put("WERKS", session.getAttribute("werks"));
        params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
        params.put("MENU_KEY", "PDA_RM_312");
        params.put("BUSINESS_CODE","RT_311");
        params.put("BUSINESS_NAME","40");
        params.put("BUSINESS_TYPE","00");
        params.put("BUSINESS_CLASS","06");
        params.put("SAP_MOVE_TYPE","311");
        return params;
    }
    @RequestMapping("/initialize")
    public R initialize(@RequestParam Map<String, Object> params){
       initParams(params);
        return wmsReMaterialPdaRemote.initialize(params);
    }

    @RequestMapping("/scan")
    public R getWmsDocByLabelNo(@RequestParam Map<String, Object> params){
        initParams(params);
        return wmsReMaterialPdaRemote.getWmsDocByLabelNo(params);
    }
     @RequestMapping("/scanInfo")
     public R getScanInfo(@RequestParam Map<String,Object> params){
         initParams(params);
        return wmsReMaterialPdaRemote.getScanInfo(params);
        }
        @RequestMapping("/remove")
    public R removeScanInfo(@RequestParam Map<String,Object> params){
            initParams(params);
        return wmsReMaterialPdaRemote.removeScanInfo(params);
        }
        @RequestMapping("/confirmWorkshopReturn")
    public R confirmWorkshopReturn(@RequestParam Map<String,Object> params){
            initParams(params);
        return wmsReMaterialPdaRemote.confirmWorkshopReturn(params);
        }
    @RequestMapping("/valiateLabel")
    public R validateLabel(@RequestParam Map<String,Object> params){
        initParams(params);
        return wmsReMaterialPdaRemote.validateLabel(params);
    }
    @RequestMapping("/saveData")
    public R saveData(@RequestParam Map<String,Object> params){
        initParams(params);
        return wmsReMaterialPdaRemote.saveData(params);
    }
}
