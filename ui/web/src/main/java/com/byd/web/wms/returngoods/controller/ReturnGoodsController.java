package com.byd.web.wms.returngoods.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.returngoods.service.ReturnGoodsRemote;

@RestController
@RequestMapping("returngoods")
public class ReturnGoodsController {
	@Autowired
	ReturnGoodsRemote returnGoodsRemote;
	
	@RequestMapping("/getBusinessNameListByWerks")
    public R getBusinessNameListByWerks(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getBusinessNameListByWerks(paramMap);
	}
	
	@RequestMapping("/getReceiveRoomOutData")
    public R getReceiveRoomOutData(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getReceiveRoomOutData(paramMap);
	}
    
	@RequestMapping("/getWareHouseOutPickupData")
	public R getWareHouseOutPickupData(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutPickupData(paramMap);
	}
    
	@RequestMapping("/createReceiveRoomOutReturn")
	public R createReceiveRoomOutReturn(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.createReceiveRoomOutReturn(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutReturnData")
	public R getWareHouseOutReturnData(@RequestParam Map<String, Object> paramMap) {
		paramMap.put("FULL_NAME", "");
		return returnGoodsRemote.getWareHouseOutReturnData(paramMap);
	}
	
	@RequestMapping("/getReceiveRoomOutReturnData")
	public R getReceiveRoomOutReturnData(@RequestParam Map<String, Object> paramMap) {
		paramMap.put("FULL_NAME", "");
		return returnGoodsRemote.getReceiveRoomOutReturnData(paramMap);
	}
	
	@RequestMapping("/confirmReceiveRoomOutReturn")
	public R confirmReceiveRoomOutReturn(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.confirmReceiveRoomOutReturn(paramMap);
	}
	
	@RequestMapping("/confirmWareHouseOutReturn")
	public R confirmWareHouseOutReturn(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.confirmWareHouseOutReturn(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData37")
	public R getWareHouseOutData37(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData37(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData33")
	public R getWareHouseOutData33(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData33(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData34")
	public R getWareHouseOutData34(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData34(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData30")
	public R getWareHouseOutData30(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData30(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData31")
	public R getWareHouseOutData31(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData31(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData29")
	public R getWareHouseOutData29(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData29(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData28")
	public R getWareHouseOutData28(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData28(paramMap);
	}
	
	@RequestMapping("/getWareHouseOutData27")
	public R getWareHouseOutData27(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.getWareHouseOutData27(paramMap);
	}
	
	@RequestMapping("/createWareHouseOutReturn")
	public R createWareHouseOutReturn(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.createWareHouseOutReturn(paramMap);
	}
	
	@RequestMapping("/wareHouseOutPickup")
	public R wareHouseOutPickup(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.wareHouseOutPickup(paramMap);
	}
	
	@RequestMapping("/wareHouseOutPickupCancel")
	public R wareHouseOutPickupCancel(@RequestParam Map<String, Object> paramMap) {
		return returnGoodsRemote.wareHouseOutPickupCancel(paramMap);
	}
}
