package com.byd.web.zzjmes.produce.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 委外发货
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface OutsourcingRemote {
    
    /**
   	 * 根据工厂、车间、线别、订单编号、批次编号、零部件号、工序获取下料明细信息及上工序产量信息
   	 * @param condMap 
   	 * 	"werks":"workshop": "line": "order_no": "zzj_plan_batch": 
   		"zzj_no": "process_name" 工序编号  machine_plan_items_id
   	 * @return
   	 */
    @RequestMapping(value = "/zzjmes-service/outsourcing/getOutsourcingMatInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
 	public R getOutsourcingMatInfo(@RequestParam Map<String,Object> params);
    
    /**
     * 委外发货保存
     * @param params
     * @return
     */
    @RequestMapping(value = "/zzjmes-service/outsourcing/saveOutsourcing", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveOutsourcing(@RequestBody Map<String,Object> params);
    
}