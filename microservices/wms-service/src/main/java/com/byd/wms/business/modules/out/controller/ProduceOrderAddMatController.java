package com.byd.wms.business.modules.out.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.byd.utils.R;
import com.byd.wms.business.modules.out.entity.CreateProduceOrderAO;
import com.byd.wms.business.modules.out.entity.ProduceOrderVO;
import com.byd.wms.business.modules.out.service.CreateRequirementService;
import com.byd.wms.business.modules.out.service.ProduceOrderAddMat;


/**
 * 生产订单补料261
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/produceorderaddmat")
public class ProduceOrderAddMatController {
	
	@Autowired
	ProduceOrderAddMat service;
	
	@Autowired
	CreateRequirementService createRequirementService;
	
	
	
	@RequestMapping("/create")
	public R create(@RequestBody Map<String,Object> data){
		List<CreateProduceOrderAO> cList = JSON.parseArray(JSON.toJSONString(data.get("params")), CreateProduceOrderAO.class);
		String staffNumber = (String) data.get("staffNumber");
		//创建生产订单补料261
		String reqestNo = null;
		try {
			reqestNo = service.createOutReqSplit(cList,staffNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok().put("data", reqestNo); 
	}
	
	
	@RequestMapping("/processProduceOrder")
	public R processProduceOrder(@RequestBody Map<String,Object> data){
		//校验生产订单
		List<ProduceOrderVO> produceOrders = JSON.parseArray(JSON.toJSONString(data.get("params")), ProduceOrderVO.class);
		List<String> depts = JSON.parseArray(JSON.toJSONString(data.get("depts")), String.class);
		//System.err.println("deptsdeptsdeptsdeptsdepts "+data.toString());
		//System.err.println("deptsdeptsdeptsdeptsdepts "+data.get("depts"));
		Map<String,Integer> checkResultMap= service.validProduceOrders(produceOrders,depts);
		String msg = "";
		for (String orderNo : checkResultMap.keySet()) {
			switch (checkResultMap.get(orderNo)) {
			case 1:
				msg += (orderNo + "订单信息不存在/未同步，请确认输入是否正确或手动同步生产订单\n");
				break;
			case 2:
				msg += (orderNo + "生产订单不存在\n");
				break;
			case 3:
				msg += (orderNo + "您没有此订单所属工厂的权限\n");
				break;
			case 4:
				msg += (orderNo + "生产订单未发布或已关闭，不允许发料\n");
				break;
			case 5:
				msg += (orderNo + "订单未发布，不允许发料\n");
				break;
			default:
				msg += (orderNo + "校验失败\n");
			}
		}
		if (msg.length() > 0) {
			return R.error(msg);
		}		
		return R.ok();
	}
	@RequestMapping("/producerOrders")
	public R queryProducerOrderInfo(@RequestBody Map<String,Object> params){
		//查询生产订单
		
		String referOrderLgort = (String)params.get("referOrderLgort");
		String filterZeroRequireLine = (String)params.get("filterZeroRequireLine");
		String werks = (String)params.get("werks");
		String whNumber = (String)params.get("whNumber");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> queryItems = (List<Map<String, Object>>) params.get("items");
		List<ProduceOrderVO> orderList = new ArrayList<ProduceOrderVO>();
		for(Map<String,Object> item:queryItems){
			if(item.get("orderNo") == null)
				return R.error("订单号不能为空");
			
			String orderNo = (String)item.get("orderNo");
			
			String mat = (String)item.get("mat");
			String location = (String)item.get("location");
			String station = (String)item.get("station");
			
			Double qty = null;
			Double requireSuitQty = null;
			try {
				if(StringUtils.isNoneBlank((String)item.get("qty"))){
					qty = Double.parseDouble((String)item.get("qty"));
				}
				if(StringUtils.isNoneBlank((String)item.get("requireSuitQty"))){
					requireSuitQty = Double.parseDouble((String)item.get("requireSuitQty")); 
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return R.error("数字转换错误");
			}
			
			ProduceOrderVO order = new ProduceOrderVO();
			order.setLocation(location);
			order.setMat(mat);
			order.setOrderNo(orderNo);
			order.setQty(qty);
			order.setRequireSuitQty(requireSuitQty);
			order.setStation(station);
			orderList.add(order);
		}
		List<Map<String,Object>> sapMoComponentList = createRequirementService.queryProducerOrders(referOrderLgort,filterZeroRequireLine,orderList,werks,whNumber,true,false);
		
		String msg = "";
		for(Map<String,Object> sapMoComponent : sapMoComponentList) {
			if (null == sapMoComponent.get("MAKTX") || sapMoComponent.get("MAKTX").equals("")) {
				msg += (sapMoComponent.get("MATNR") + "物料未同步；\n");
			}
		}
		if(!msg.equals("")) {
			return R.error(msg);
		}
		
		return R.ok().put("data", sapMoComponentList);
	}
}
