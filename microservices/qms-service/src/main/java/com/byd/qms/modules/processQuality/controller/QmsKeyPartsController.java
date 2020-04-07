package com.byd.qms.modules.processQuality.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.qms.modules.processQuality.service.QmsKeyPartsService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

/**
 * 品质 关键零部件
 * @author cscc tangj
 * @email 
 * @date 2019-07-30 10:12:08
 */
@RestController
@RequestMapping("processQuality/keyParts")
public class QmsKeyPartsController {
    @Autowired
    private QmsKeyPartsService qmsKeyPartsService; 
    
    /**
     * 抬头列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestBody Map<String, Object> params){
        PageUtils page = null;
       // testType：【01：大巴 ;02：专用车】
        String testType=params.get("testType")!=null ? params.get("testType").toString() : "01";
        if(testType.equals("01")) {
        	page= qmsKeyPartsService.queryPage(params);
        }
        if(testType.equals("02")) {
        	page= qmsKeyPartsService.queryPage02(params);
        }
        return R.ok().put("page", page);
    }
    /**
     * 明细列表
     */
    @RequestMapping("/queryDetail")
    public R queryDetail(@RequestBody Map<String, Object> params){
        PageUtils page = null;
        List<Map<String,Object>> list =null;
        // testType：【01：大巴 ;02：专用车】
         String testType=params.get("testType")!=null ? params.get("testType").toString() : "01";
         if(testType.equals("01")) {
        	 list = qmsKeyPartsService.getList(params);
         }
         if(testType.equals("02")) {
        	 list = qmsKeyPartsService.getList02(params);
         }
         page = new PageUtils(list, list.size(), list.size(), 1);
         return R.ok().put("page", page);
    }
    /**
     * 订单配置列表
     */
    @RequestMapping("/queryOrderConfigList")
    public R queryOrderConfigList(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list =null;
        // testType：【01：大巴 ;02：专用车】
         String testType=params.get("testType")!=null ? params.get("testType").toString() : "01";
         if(testType.equals("01")) {
        	 list = qmsKeyPartsService.getOrderConfigList(params);
         }
         if(testType.equals("02")) {
        	 list = qmsKeyPartsService.getOrderConfigList02(params);
         }
         return R.ok().put("data", list);
    }
    /**
     * 车间
     */
    @RequestMapping("/queryWorkshopList")
    public R queryWorkshopList(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = qmsKeyPartsService.getWorkshopList(params);
        return R.ok().put("data", list);
    }
    /**
     * 线别
     */
    @RequestMapping("/queryLineNameList")
    public R queryLineNameList(@RequestBody Map<String, Object> params){
        List<Map<String,Object>> list = qmsKeyPartsService.getLineNameList(params);
        return R.ok().put("data", list);
    }
    
    /**
     * 关键零部件追溯查询
     */
    @RequestMapping("/queryTraceBackPage")
    public R queryTraceBackPage(@RequestBody Map<String, Object> params){
        PageUtils page = null;
       // testType：【01：大巴 ;02：专用车】
        String testType=params.get("testType")!=null ? params.get("testType").toString() : "01";
        if(testType.equals("01")) {
        	page= qmsKeyPartsService.getBmsTraceBackList(params);
        }
        if(testType.equals("02")) {
        	page= qmsKeyPartsService.getVmesTraceBackList(params);
        }
        return R.ok().put("page", page);
    }
}