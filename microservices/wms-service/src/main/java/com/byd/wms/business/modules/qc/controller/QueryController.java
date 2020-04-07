package com.byd.wms.business.modules.qc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionHeadService;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionItemService;
import com.byd.wms.business.modules.qc.service.WmsQcRecordService;
import com.byd.wms.business.modules.qc.service.WmsQcResultService;


@RestController
@RequestMapping("/qcQuery")
public class QueryController {
	
	@Autowired
	WmsQcInspectionHeadService headService;
	
	@Autowired
	WmsQcInspectionItemService itemService;
	
	@Autowired
	WmsQcResultService resultService;
	
	@Autowired
	WmsQcRecordService recordService;
	
	@RequestMapping("/inspectionList")
	public R inspectionList(@RequestParam Map<String,Object> params){
		PageUtils page = headService.queryPage(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/inspectionItem")
	public R inspectionItem(@RequestParam Map<String,Object> params){
		List<WmsQcInspectionItemEntity> list = itemService.selectInspectionItems(params);
		return R.ok().put("list", list);
	}
	
	@RequestMapping("/resultList")
	public R resultList(@RequestParam Map<String,Object> params){
		PageUtils page = resultService.queryResultList(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/recordList")
	public R recordList(@RequestParam Map<String,Object> params){
		PageUtils page = recordService.queryRecordList(params);
		return R.ok().put("page", page);
	}
	
	@RequestMapping("/destroyQtyList")
	public R destroyQtyList(@RequestParam Map<String,Object> params){
		PageUtils page = resultService.queryDestroyQtyList(params);
		return R.ok().put("page", page);
	}

}
