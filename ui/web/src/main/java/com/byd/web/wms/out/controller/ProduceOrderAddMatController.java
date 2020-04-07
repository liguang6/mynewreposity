package com.byd.web.wms.out.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.out.entity.CreateProduceOrderAO;
import com.byd.web.wms.out.entity.ProduceOrderVO;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;

/**
 * 生产订单补料261
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/produceorderaddmat")
public class ProduceOrderAddMatController {
	
	@Autowired
	WmsOutReqServiceRemote outReqServiceRemote;
	
	@RequestMapping("/create")
	public R create(@RequestBody List<CreateProduceOrderAO> cList){
		Map<String,Object> data = new HashMap<>();
		data.put("params", cList);
		data.put("staffNumber", "");
		return outReqServiceRemote.createAddMat(data);
	}
	
	
	@RequestMapping("/processProduceOrder")
	public R processProduceOrder(@RequestBody List<ProduceOrderVO> produceOrders){
		Map<String,Object> data = new HashMap<String, Object>();
/*		Map<String,Object> params = new HashMap<>();
		params.put("deptType", "3");
		List<String> depts = deptsService.queryDeptListWithPremision(params).stream().map(SysDeptEntity::getCode).collect(Collectors.toList());
		data.put("depts", depts);*/
		data.put("params", produceOrders);
		return outReqServiceRemote.processProduceOrderAddMat(data);
	}
	
	@RequestMapping("/producerOrders")
	public R queryProducerOrderInfo(@RequestBody Map<String,Object> params){
		return outReqServiceRemote.queryProducerOrderInfo(params);
	}
}
