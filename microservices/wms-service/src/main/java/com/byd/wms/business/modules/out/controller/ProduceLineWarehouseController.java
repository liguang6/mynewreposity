package com.byd.wms.business.modules.out.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.byd.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
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
import com.byd.wms.business.modules.out.service.ProduceLineWarehouseService;



/**
 * 线边仓领料
 * @author develop07
 */
@RestController
@RequestMapping("/out/producelinewarehouse")
public class ProduceLineWarehouseController {
	
	@Autowired
	ProduceLineWarehouseService service;
	
	@Autowired
	CreateRequirementService createService;
	
	//校验订单
	@RequestMapping("/processProduceOrder")
	public R processProduceOrder(@RequestBody Map<String,Object> data){
		List<ProduceOrderVO> produceOrders = JSON.parseArray(JSON.toJSONString(data.get("params")), ProduceOrderVO.class);
		List<String> depts = JSON.parseArray(JSON.toJSONString(data.get("depts")), String.class);
		Map<String, Integer>  checkResultMap = service.validProduceOrders(produceOrders,depts);
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
	public R producerOrders(@RequestBody Map<String,Object> params){

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
		//查询合格的线边仓领料订单行项目
		List<Map<String, Object>>  sapMoComponentList = createService.queryProducerOrders(referOrderLgort, filterZeroRequireLine, orderList, werks, whNumber, false, true);
		
		String msg = "";
		for(Map<String,Object> sapMoComponent : sapMoComponentList) {
			if (null == sapMoComponent.get("MAKTX") || sapMoComponent.get("MAKTX").equals("")) {
				msg += (sapMoComponent.get("MATNR") + "物料未同步；\n");
			}
		}
		if(!msg.equals("")) {
			return R.error(msg);
		}
		System.err.println(sapMoComponentList.toString());
		return R.ok().put("data", sapMoComponentList);
	}
	
	
	
	
	@RequestMapping("/create")
	public R create(@RequestBody Map<String,Object> data){
		//创建 线边仓/库存地点调拨领料311
		List<CreateProduceOrderAO> cList = JSON.parseArray(JSON.toJSONString(data.get("params")), CreateProduceOrderAO.class);
		String userName = (String) data.get("staffNumber");
		String reqestNo = null;
		try {
			reqestNo = service.createProduceOrderOutReqSplit(cList,userName);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok().put("data", reqestNo); 
	}



	/*@RequestMapping("/upload")
	public R upload(@RequestBody List<CreateProduceOrderAO> entityList) throws IOException {
		//System.err.println("uploaduploaduploaduploadupload");
		String reqNo;
		try {
			reqNo = createRequirementService.creteScddByUpload(entityList);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok().put("data", reqNo);
	}*/

	@RequestMapping("/preview")
	public R preview(@RequestBody List<Map<String,Object>> entityList) throws IOException {
		//System.err.println("========================previewpreviewpreview==================");
		System.err.println(entityList.toString());
		String msg = "";
		if (!CollectionUtils.isEmpty(entityList)) {
			for (Map<String, Object> map : entityList) {

				/*if (map.get("AUFNR") == null || (map.get("AUFNR") != null && map.get("AUFNR").toString().equals(""))) {
					msg += "生产订单号不能为空！";
				}
				if(map.get("requireTypes").equals("41")){
					if (map.get("POSNR") == null || (map.get("POSNR") != null && map.get("POSNR").toString().equals(""))) {
						msg += "生产订单行项目号不能为空！";
					}
				}*/

				if (map.get("MATNR") == null || (map.get("MATNR") != null && map.get("MATNR").toString().equals(""))) {
					msg += "料号不能为空！";
				}
				if (map.get("REQ_QTY") == null || (map.get("REQ_QTY") != null && map.get("REQ_QTY").toString().equals(""))) {
					msg += "需求数量不能为空！";
				}
				map.put("editDate", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
				map.put("msg", msg);
			}

		}
		return R.ok().put("data", entityList);
	}

}
