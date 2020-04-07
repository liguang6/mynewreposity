package com.byd.web.qms.processQuality.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.qms.processQuality.service.QmsKeyPartsRemote;
/**
 * 品质 关键零部件
 * @author cscc tangj
 * @email 
 * @date 2019-07-30 09:12:08
 */
@RestController
@RequestMapping("qms/keyParts")
public class QmsKeyPartsController {
    @Autowired
    private QmsKeyPartsRemote qmsKeyPartsRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 关键零部件追踪列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return qmsKeyPartsRemote.queryPage(params);
    }
    /**
     * 关键零部件追踪明细列表
     */
    @RequestMapping("/queryDetail")
    public R queryDetail(@RequestParam Map<String, Object> params){
    	return qmsKeyPartsRemote.queryDetail(params);
    }
    /**
     * 订单配置列表
     */
    @RequestMapping("/queryOrderConfigList")
    public R queryOrderConfigList(@RequestParam Map<String, Object> params){
    	return qmsKeyPartsRemote.queryOrderConfigList(params);
    }
    /**
     * 车间列表
     */
    @RequestMapping("/queryWorkshopList")
    public R queryWorkshopList(@RequestParam Map<String, Object> params){
    	return qmsKeyPartsRemote.queryWorkshopList(params);
    }
    /**
     * 线别列表
     */
    @RequestMapping("/queryLineNameList")
    public R queryLineNameList(@RequestParam Map<String, Object> params){
    	return qmsKeyPartsRemote.queryLineNameList(params);
    }
    /**
     * 关键零部件追溯
     */
    @RequestMapping("/queryTraceBackPage")
    public R queryTraceBackPage(@RequestParam Map<String, Object> params){
    	return qmsKeyPartsRemote.queryTraceBackPage(params);
    }
}