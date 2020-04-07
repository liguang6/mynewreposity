package com.byd.web.returngoods.controller;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.returngoods.service.WmsReMaterialTwoPdaRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author liguang6
 * @date 2019/22/11 17:36
 * @title 平面库余料退库261
 */
@RestController
@RequestMapping("/returngoods/reMaterialTwo")

public class WmsReMaterialTwoPdaController {
    @Autowired
    private  WmsReMaterialTwoPdaRemote wmsReMaterialTwoPdaRemote;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    protected HttpSession session;


//初始化数据
  public Map<String,Object> initParams(Map<String,Object> params){
    params.put("WH_NUMBER", session.getAttribute("warehouse"));
    params.put("WERKS", session.getAttribute("werks"));
    params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
    params.put("MENU_KEY", "PDA_RM_262");
    params.put("BUSINESS_NAME","35");
    params.put("BUSINESS_TYPE","10");
    params.put("BUSINESS_CLASS","06");
    params.put("BUSINESS_CODE","RT_301");
    params.put("SAP_MOVE_TYPE","261");
    return params;
}
    @RequestMapping("/scanTwo")
    public R getWmsDocByLabelNo(@RequestParam Map<String, Object> params){
          initParams(params);
        return wmsReMaterialTwoPdaRemote.getWmsDocByLabelNo(params);
    }
     @RequestMapping("/scanInfoTwo")
     public R getScanInfo(@RequestParam Map<String,Object> params){
         initParams(params);
        return wmsReMaterialTwoPdaRemote.getScanInfo(params);
        }
        @RequestMapping("/removeTwo")
    public R removeScanInfo(@RequestParam Map<String,Object> params){
            initParams(params);
        return wmsReMaterialTwoPdaRemote.removeScanInfo(params);
        }
        @RequestMapping("/confirmWorkshopReturnTwo")
    public R confirmWorkshopReturn(@RequestParam Map<String,Object> params){
            initParams(params);
        return wmsReMaterialTwoPdaRemote.confirmWorkshopReturn(params);
        }
        @RequestMapping("/valiateLabelTwo")
    public R validateLabel(@RequestParam Map<String,Object> params){
            initParams(params);
        return wmsReMaterialTwoPdaRemote.validateLabelTwo(params);
        }
    @RequestMapping("/saveData")
    public R saveData(@RequestParam Map<String,Object> params){
        initParams(params);
        return wmsReMaterialTwoPdaRemote.saveData(params);
    }

}
