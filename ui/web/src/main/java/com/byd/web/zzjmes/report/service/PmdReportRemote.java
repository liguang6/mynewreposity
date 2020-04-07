package com.byd.web.zzjmes.report.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 自制件报表通用控制器
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface PmdReportRemote {
    
    /**
     * 订单产量达成明细报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/pmdOutputReachReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R pmdOutputReachReport(@RequestParam Map<String, Object> params);
    
    @RequestMapping(value = "/zzjmes-service/report/getPmdOutputReachList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPmdOutputReachList(@RequestParam Map<String, Object> params);
    
    /**
     * 批次产量达成明细报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/batchOutputReachReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R batchOutputReachReport(@RequestParam Map<String, Object> params);
    
    @RequestMapping(value = "/zzjmes-service/report/getBatchOutputReachList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getBatchOutputReachList(@RequestParam Map<String, Object> params);
    
    /**
     * 订单需求达成报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/pmdReqReachReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R pmdReqReachReport(@RequestParam Map<String, Object> params);
    
    /**
     * 订单批次需求达成报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/orderBatchReachReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R orderBatchReachReport(@RequestParam Map<String, Object> params);
    
    /**
     * 班组计划达成报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/workgroupReachReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R workgroupReachReport(@RequestParam Map<String, Object> params);
    
    /**
     * 组合件加工进度报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/orderAssemblyReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R orderAssemblyReport(@RequestParam Map<String, Object> params);
    
    @RequestMapping(value = "/zzjmes-service/report/getOrderAssemblyList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getOrderAssemblyList(@RequestParam Map<String, Object> params);

    /**
     * 工序工时-加工时长报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/pmdProcessTimeReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R pmdProcessTimeReport(@RequestParam Map<String, Object> params);

    /**
     * 工序工时-流转时间报表
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/report/pmdProcessTransferTimeReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R pmdProcessTransferTimeReport(@RequestParam Map<String, Object> params);
    
    
}