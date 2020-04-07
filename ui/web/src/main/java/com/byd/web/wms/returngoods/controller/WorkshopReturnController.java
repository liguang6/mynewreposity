package com.byd.web.wms.returngoods.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.returngoods.service.WorkshopReturnRemote;

@RestController
@RequestMapping("workshopReturn")
public class WorkshopReturnController {
	@Autowired
	WorkshopReturnRemote workshopReturnRemote;
	
	@RequestMapping("/getBusinessNameListByWerks")
    public R getBusinessNameListByWerks(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getBusinessNameListByWerks(paramMap);
	}
	
	@RequestMapping("/getSapInOrderInfo")
	public R getSapInOrderInfo(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getSapInOrderInfo(paramMap);
	}
	
	@RequestMapping("/getSapVendorInfo")
	public R getSapVendorInfo(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getSapVendorInfo(paramMap);
	}
	
	@RequestMapping("/getCostCenterInfo")
	public R getCostCenterInfo(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getCostCenterInfo(paramMap);
	}
	
	@RequestMapping("/getWbsInfo")
	public R getWbsInfo(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getWbsInfo(paramMap);
	}
	
	@RequestMapping("/getWorkshopReturnData")
    public R getWorkshopReturnData(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getWorkshopReturnData(paramMap);
	}
	
	@RequestMapping("/getWorkshopReturnConfirmData")
    public R getWorkshopReturnConfirmData(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getWorkshopReturnConfirmData(paramMap);
	}
	
	@RequestMapping("/getSapMoComponentDate")
    public R getSapMoComponentDate(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.getSapMoComponentDate(paramMap);
	}
	
	@RequestMapping("/createWorkshopReturn")
	public R createWorkshopReturn(@RequestParam Map<String, Object> paramMap){
		return workshopReturnRemote.createWorkshopReturn(paramMap);
	}
	
	@RequestMapping("/confirmWorkshopReturn")
	public R confirmWorkshopReturn(@RequestParam Map<String, Object> paramMap){
		paramMap.put("FULL_NAME", "");
		return workshopReturnRemote.confirmWorkshopReturn(paramMap);
	}
}
