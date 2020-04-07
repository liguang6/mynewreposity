package com.byd.wms.business.modules.out.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.byd.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.out.entity.InternalOrderAO;
import com.byd.wms.business.modules.out.service.CreateRequirementService;


/**
 * 内部订单领料需求控制器
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/innerorder")
public class InnerOrderController {
	
	static Logger log = LoggerFactory.getLogger(InnerOrderController.class);
	
	@Autowired
	WmsSapRemote wmsSapRemote;
	
	@Autowired
	CreateRequirementService service;

	/**
	 * 从SAP查询内部订单
	 * @param innerOrderNo
	 * @return
	 */
	@RequestMapping("/SAPInnerOrder/{innerOrderNo}")
	public R getInnerOrderFromSAP(@PathVariable("innerOrderNo") String innerOrderNo){
		log.debug("---> get internal order from SAP");
		Map<String,Object> result =  wmsSapRemote.getSapBapiKaufOrder(innerOrderNo);
		return R.ok().put("data", result);
	}
	
	/**
	 * 创建内部订单领料需求
	 * @param internalOrderList
	 * @return
	 */
	@RequestMapping("/createInternalOrder")
	public R createIternalOrderRequirement(@RequestBody List<InternalOrderAO> internalOrderList){
		System.err.println(internalOrderList.toString());
		log.info("---> 创建内部订单领料需求");
		String requirementNo =  service.saveInternalOrderRequirementSplit(internalOrderList);
		log.info("<--- 创建内部订单"+requirementNo);
		return R.ok().put("data", requirementNo);
	}

	@RequestMapping("/previewNbdd")
	public R preview(@RequestBody List<Map<String,Object>> entityList) throws IOException {
		//System.err.println("========================previewpreviewpreview==================");
		System.err.println(entityList.toString());
		String msg = "";
		if (!CollectionUtils.isEmpty(entityList)) {
			for (Map<String, Object> map : entityList) {


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
