package com.byd.wms.business.modules.out.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsSapCustomerEntity;
import com.byd.wms.business.modules.config.service.WmsSapCustomerService;
import com.byd.wms.business.modules.out.dao.SendCreateRequirementDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.service.CreateRequirementService;

/**
 * 出库模块控制器
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/outreq")
public class OutReqCreateController {
	@Autowired
	CreateRequirementService service;

	@Autowired
	WmsSapRemote sapService;
	
	@Autowired
	WmsOutRequirementItemDao outReqItemDao;


	@Autowired
	SendCreateRequirementDao reqCreateDao;
	
	@Autowired
	WmsSapCustomerService wmsSapCustomerService;

	@RequestMapping("/queryOutItems6")
	public R queryOutItems6(@RequestBody List<Map<String, Object>> list) {
		// 查询委外订单出库项目
		List resuleList = service.queryOutItems6(list);
		if(resuleList.size()==0){
			return R.error("请注意同步物料主数据！");
		}
		return R.ok().put("data", resuleList);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/queryOutItems7")
	public R queryOutItems7(@RequestBody List<Map<String, Object>> list) {
		//System.err.println("list==============>>> "+list.toString());
		// 查询SAP交货单出库行项目
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> item : list) {
			String deliveryNO = (String) item.get("deliveryNO");
			Map<String, Object> resp = sapService
					.getSapBapiDeliveryGetlist(deliveryNO);

			Map<String, Object> header = (Map<String, Object>) resp
					.get("header");
//			System.err.println(header.toString());
			List<Map<String, Object>> itemList = (List<Map<String, Object>>) resp
					.get("item");
//			System.err.println(itemList.toString());
			for (Map<String, Object> ite : itemList) {
				ite.putAll(header);
				ite.put("REQ_QTY",ite.get("LFIMG"));
				if (item.get("lgortList") != null && !item.get("lgortList").equals(""))
					ite.put("LGORT",item.get("lgortList"));
				res.add(ite);
			}
		}

		return R.ok().put("data", res);
	}

	@RequestMapping("/outReqCreate6")
	public R outReqCreate6(@RequestBody List<Map<String, Object>> list) {
		// 委外订单出库需求创建
		String reqNo;
		try {
			reqNo = service.outReqCreate6Split(list);
		} catch (Exception e) {
			return R.error("保存失败!");
		}
		return R.ok().put("data", reqNo);
	}

	@RequestMapping("/outReqCreate7")
	public R outReqCreate7(@RequestBody List<Map<String, Object>> list) {
		// 创建SAP交货单出库需求
		String reqNo = null;
		reqNo = service.outReqCreate7Split(list);
		return R.ok().put("data", reqNo);
	}
	
	

	@RequestMapping("/validateOutItems6")
	public R validateOutItems6(@RequestBody List<Map<String, Object>> list) {
		// 校验输入的委外订单是否合格
		Map<String, Integer> map = service.validateOutItems6(list);
		String msg = "";
		for (String item : map.keySet()) {
			if (map.get(item).intValue() == 1) {
				msg += item + "SAP采购订单信息未同步，请先手动执行同步\n";
			} else if (map.get(item).intValue() == 2) {
				msg += item + "SAP采购订单未审批\n";
			} else if(map.get(item).intValue() == 3){
				msg += item + "您无操作此工厂下订单的权限\n";
			}
		}
		if (msg.length() == 0)
			return R.ok();
		return R.error().put("msg", msg);
	}

	// SAP销售订单发货
	@RequestMapping("/validateOutItems7")
	public R validateOutItems7(@RequestBody Map<String, Object> data) {
		//System.err.println(data.toString());
		String msg = "";
		List<Map<String,Object>> list = (List<Map<String, Object>>) data.get("list");
		
		for (Map<String, Object> item : list) {
			String deliveryNO = (String) item.get("deliveryNO");
			if (StringUtils.isBlank(deliveryNO)) {
				return R.error("SAP发货单号不能为空");
			}
			// 从SAP系统获取交货单信息
			Map<String, Object> resp = sapService
					.getSapBapiDeliveryGetlist(deliveryNO);
			System.err.println(resp.toString());
			System.err.println("MESSAGE::: "+resp.get("MESSAGE"));
			if (resp.get("MESSAGE") != null) {
				msg += deliveryNO + "交货单不存在\n";
			} else {
				// 交货单存在
				@SuppressWarnings("unchecked")
				Map<String, Object> header = (Map<String, Object>) resp
						.get("header");
				List<Map<String, Object>> items =(List<Map<String, Object>>) resp
						.get("item");
				if (!"J".equals((String) header.get("VBTYP"))) {
					msg += deliveryNO + "SAP交货单不是正确的交货单\n";
					continue;
				}
				//判断是否有权限
				System.err.println("header "+header.toString());
				System.err.println("header "+items.toString());
				String werks = (String) items.get(0).get("WERKS");
				//获取调用端传过来的，权限工厂
				String werksCode = (String) item.get("werks");
				//List<String> deptList = (List<String>) data.get("depts");
				System.err.println("werks==="+werks);
				System.err.println("werksCode==="+werksCode);
				if (!werks.equals(werksCode)){
					msg += String.format("你无权操作%s交货单号\n", deliveryNO);
					continue;
				}
				//boolean premFlag = false;

/*				for (String deptCode : deptList) {
					if (werks.equals(deptCode))
						premFlag = true;
				}
		
				if (!premFlag) {
					msg += String.format("你无权操作%s交货单号\n", deliveryNO);
					continue;
				}*/
				// 核销相关的校验
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("werks", werks);
				params.put("vbeln", deliveryNO);
				List<Map<String, Object>> hxDnList = reqCreateDao
						.selectHxDn(params);
				if (!CollectionUtils.isEmpty(hxDnList)) {
					msg += String.format("%s交货单存在V类业务，请先处理V类业务", deliveryNO);
					continue;
				} else {
					List<WmsOutRequirementItemEntity> entitys = outReqItemDao
							.selectList(new EntityWrapper<WmsOutRequirementItemEntity>()
									.eq("SAP_OUT_NO", deliveryNO));
					/*if (!CollectionUtils.isEmpty(entitys)) {
						msg += String.format("已存在%s交货单的需求", deliveryNO);
					}*/
				}
			}
		}
		if (msg.length() > 0) {
			return R.error(msg);
		}
		return R.ok();
	}
	
	@RequestMapping("/validateOutItem10")
	public R validate10(@RequestBody Map<String, Object> data){
		List<Map<String,Object>> list = (List<Map<String, Object>>) data.get("list");
		List<String> depts = (List<String>) data.get("depts");
		//校验外部销售发货
		Map<String, Integer> errorMap = service.validateOutItems10(list,depts);
		String errorMsg = "";
		for (String key : errorMap.keySet()) {
			switch (errorMap.get(key)) {
			case 1:
				errorMsg += (key + "采购订单不存在\n");
				break;
			case 2:
				errorMsg += (key + "订单未审批\n");
				break;
			case 3:
				errorMsg += (String.format("没有操作%s订单的权限\n", key));
				break;
			default:
				errorMsg += (key + "校验失败\n");
			}
		}
		if (errorMsg.length() > 0) {
			return R.error(errorMsg);
		}
		return R.ok();
	}
	
	
	@RequestMapping("/queryOutItem10")
	public R queryOutItem10(@RequestBody List<Map<String, Object>> list){
		//查询-需求类型：外部销售发货（251）
		return R.ok().put("data", service.queryOutItem10(list));
	}
	
	@RequestMapping("/outReqCreate10")
	public R createOutReq10(@RequestBody List<Map<String, Object>> list){
		//创建-外部销售发货（251）
		return R.ok().put("data",service.createReqOutItem10Split(list));
	}

	@RequestMapping("/validateOutItem8")
	public R validateOutItem8(@RequestBody List<Map<String, Object>> list) {
		// UB转储单校验
		Map<String, Integer> errorMap = service.validateOutItem8(list);
		String errorMsg = "";
/*		for (String key : errorMap.keySet()) {
			switch (errorMap.get(key)) {
			case 1:
				errorMsg += (key + "采购订单不存在\n");
				break;
			case 2:
				errorMsg += (key + "单据类型异常\n");
				break;
			case 3:
				errorMsg += (key + "订单未审批\n");
				break;
			case 4:
				break;
			case 5:
				errorMsg += (String.format("采购订单%s不属于指定接收工厂\n", key));
				break;
			default:
				errorMsg += (key + "校验失败\n");
			}
		}*/
		if (errorMsg.length() > 0) {
			return R.error(errorMsg);
		}
		return R.ok();
	}
	
	@RequestMapping("/queryOutItem8")
	public R queryOutItem8(@RequestBody List<Map<String, Object>> list){
		//查询UB转储业务出库数据
		return R.ok().put("data",service.queryOutItem8(list));
	}
	
	@RequestMapping("/outReqCreate8")
	public R outReqCreate8(@RequestBody List<Map<String, Object>> list){
		//创建UB转储出库需求
		String reqestNo = null;
		try {
			reqestNo = service.createReqOutItem8Split(list);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok().put("data", reqestNo);
	}
	
	
	@RequestMapping("/queryTotalStockQty")
	public R queryTotalStockQty(@RequestBody Map<String,Object> params){
		//查询库存总数
		//System.err.println("params===========>>> "+params.toString());
		//System.err.println("result ===========>>> "+service.selectTotalStockQty(params));
		return R.ok().put("data", service.selectTotalStockQty(params));
	}
	
	@RequestMapping("/createOutReq11")
	public R createOutReq11(@RequestBody List<Map<String, Object>> list){
		//创建工厂间调拨需求
		return R.ok().put("data", service.createOutReq10Split(list));
	}
	
	@RequestMapping("/mathx")
	public R validateMatrialsHX(@RequestBody List<Map<String, Object>> list){
		System.err.println("mathxmathxmathxmathxmathx 校验物料"+list.toString());
		//校验物料有效性
		//如果该工厂启用了核销业务，工厂+接收工厂+料号关联查询【WMS_HX_TO】表的HX_QTY_XF是否>0，大于0时讲剩余料号及剩余核销数量体现在报错中
		Map<String,Object> errorMap = service.checkMatrialsHx(list);
		String msg ="";
		for(String matnr:errorMap.keySet()){
			msg += errorMap.get(matnr)+"\n";
		}
		if(msg.length() > 0){
			return R.error(msg);
		}
		return R.ok();
	}
	
	/**
	 * 库存地点调拨311—类型不转移
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq13")
	public R createOutReq13(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.createOutReq13Split(list));
	}
	
	
	//校验 工厂间调拨发货（A303）
	@RequestMapping("/validate16")
	public R validate16(@RequestBody List<Map<String, Object>> list){
		Map<String,Integer> map = service.validateOutReq16(list);
		String msg = "";
		for(String key:map.keySet()){
			switch(map.get(key)){
			case 1:msg+=key+"凭证号不存在，请确认凭证号准确性\n";break;
			case 2:msg+=key+"凭证号已无可出库物料\n";break;
			default:msg+="校验失败\n";
			}
		}
		if(msg.length() > 0){
			return R.error(msg);
		}
		return R.ok();
	}
	
	//查询 工厂间调拨发货（A303）
	@RequestMapping("/query16")
	public R query16(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.queryOutReq16(list));
	}
	//创建 工厂间调拨发货（A303）
	@RequestMapping("/create16")
	public R create16(@RequestBody List<Map<String, Object>> list){
		//System.err.println("create16create16create16");
		return R.ok().put("data", service.createOutReq16Split(list));
	}
	
	//校验 - SAP交货单销售发货（A311T）
	@RequestMapping("/validate17")
	public R validate17(@RequestBody List<Map<String, Object>> list){
		Map<String,Integer> map = service.validate17(list);
		String msg = "";
		/*for(String key:map.keySet()){
			switch(map.get(key)){
			case 1:msg+=String.format("未找到%s交货单，确认数据准确性\n", key);break;
			case 2:msg+=String.format("%s交货单已无可操作数据\n", key);break;
			default:msg+="校验失败\n";
			}
		}*/
		if(msg.length() > 0){
			return R.error(msg);
		}
		return R.ok();
	}

	//查询- SAP交货单销售发货（A311T）
	@RequestMapping("/query17")
	public R query17(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.query17(list));
	}
	

	//创建 - SAP交货单销售发货（A311T）
	@RequestMapping("/create17")
	public R create17(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.create17Split(list));
	}

	/**
	 * 报废（551）
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq18")
	public R createOutReq18(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.createOutReq18Split(list));
	}
		
	@CrossOrigin
	@RequestMapping("/queryOutReqPda311")
	public R queryOutReqPda311(@RequestBody Map<String, Object> map){
		return R.ok().put("data", reqCreateDao.queryOutReqPda311(map));
	}
	@CrossOrigin
	@RequestMapping("/queryUNIT")
	public R queryUNIT(@RequestBody Map<String, Object> map){
		return R.ok().put("data", reqCreateDao.queryUNIT(map));
	}
	
	@CrossOrigin
	@RequestMapping("/createOutReqPda311")
	public R createOutReqPda311(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.createOutReqPda311Split(list));
	}


	/**
	 * STO一步联动311
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq20")
	public R createOutReq20(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.createOutReq20Split(list));
	}
	
	/**
	 * 校验 工厂间调拨301（总装）
	 * @param list
	 * @return
	 */
	@RequestMapping("/validate21")
	public R validate21(@RequestBody List<Map<String, Object>> list){
		Map<String,Integer> map = service.validateOutReq21(list);
		String msg = "";
		for(String key:map.keySet()){
			switch(map.get(key)){
			case 1:msg+=key+"配送单号不存在，请确认\n";break;
			case 2:msg+=key+"配送单号已备料\n";break;
			default:msg+="校验失败\n";
			}
		}
		if(msg.length() > 0){
			return R.error(msg);
		}
		return R.ok();
	}	
	
	/**
	 * 查询 工厂间调拨301（总装）
	 * @param list
	 * @return
	 */
	@RequestMapping("/query21")
	public R query21(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.queryOutReq21(list));
	}
	
	/**
	 * 工厂间调拨301（总装）
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq21")
	public R createOutReq21(@RequestBody List<Map<String, Object>> list){
		return R.ok().put("data", service.createOutReq21Split(list));
	}
	
	@RequestMapping("/validateCostomer")
	public R validateCostomer(@RequestParam Map<String, Object> params){
		WmsSapCustomerEntity entity = wmsSapCustomerService.selectOne(new EntityWrapper<WmsSapCustomerEntity>()
				.eq("KUNNR", params.get("COSTOMER")));
		if (entity != null)
			return R.ok();
		else
			return R.error();
	}

}
