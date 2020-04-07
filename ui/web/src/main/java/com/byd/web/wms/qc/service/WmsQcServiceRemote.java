package com.byd.web.wms.qc.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.wms.qc.entity.WmsQcInspectionItemEntity;
import com.byd.web.wms.qc.entity.WmsQcRecordEntity;
import com.byd.web.wms.qc.entity.WmsQcResultEntity;

/**
 * 调用 WMS-SERVICE 质检模块接口服务
 * 
 * @author develop07
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsQcServiceRemote {

	// -------调用wms-service WmsQcInspectionHeadController 的接口--------
	@RequestMapping("wms-service/qc/wmsqcinspectionhead/list")
	R list(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/info/{id}")
	public R info(@PathVariable("id") Long id);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/saveinspectionbatch")
	public R saveNoInspectionBatch(@RequestBody Map<String, Object> paramsMap);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/saveOnInspect")
	public R saveOnInspect(@RequestBody Map<String, Object> paramsMap);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/saveOnInspectSingleBatch")
	public R saveOnInspectionSingleBatch(
			@RequestBody Map<String, Object> paramsMap);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/hasInspectedRejudgeSave")
	public R hasInspectedRejudgeSave(@RequestBody Map<String, Object> paramsMap);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/add_stock_rejudge_inspect")
	public R addStockRejudgeInspect(@RequestBody Map<String, Object> paramsMap);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/saveStockRejudgeNotInspect")
	public R saveStockRejudgeNotInspect(
			@RequestBody List<Map<String, Object>> items);

	@RequestMapping("wms-service/qc/wmsqcinspectionhead/saveStockRejudgeOnInspect")
	public R saveStockRejudgeOnInspect(
			@RequestBody List<Map<String, Object>> items);

	// -------WmsQcInspectionItemController----------

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/list")
	public R listInspectionItem(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/listHasInspected")
	public R listHasInspected(@RequestParam Map<String, Object> params);
	
	@RequestMapping("wms-service/qc/wmsqcinspectionitem/getInspectionItemTask")
	public R getInspectionItemTask(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/info/{id}")
	public R infoInspectionItem(@PathVariable("id") Long id);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/save")
	public R save(@RequestBody WmsQcInspectionItemEntity wmsQcInspectionItem);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/update")
	public R update(@RequestBody WmsQcInspectionItemEntity wmsQcInspectionItem);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/delete")
	public R deleteInspectionItem(@RequestBody Long[] ids);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/stokerejudgeitems")
	public R queryStockReJudgeItems(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/stock_rejudge_not_inspected")
	public R selectStockReJudgeNotInspected(
			@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/stoke_rejuge_on_inspect")
	public R selectStockRejudgeOnInspect(
			@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcinspectionitem/export_excel_stock_rejudge_not_inspect")
	public ResponseEntity<byte[]> exportExcelStockRejudgeNotInspect(
			@RequestParam Map<String, Object> params) throws Exception;

	// -------WmsQcQueryController----------

	@RequestMapping("wms-service/qcQuery/inspectionList")
	public R inspectionList(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qcQuery/inspectionItem")
	public R inspectionItem(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qcQuery/resultList")
	public R resultList(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qcQuery/recordList")
	public R recordList(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qcQuery/destroyQtyList")
	public R destroyQtyList(@RequestParam Map<String, Object> params);

	// -----WmsQcRecordController------

	@RequestMapping("wms-service/qc/wmsqcrecord/list")
	public R listQcRecord(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcrecord/info/{id}")
	public R infoQcRecord(@PathVariable("id") Long id);

	@RequestMapping("wms-service/qc/wmsqcrecord/save")
	public R save(@RequestBody WmsQcRecordEntity wmsQcRecord);

	@RequestMapping("wms-service/qc/wmsqcrecord/update")
	public R update(@RequestBody WmsQcRecordEntity wmsQcRecord);

	@RequestMapping("wms-service/qc/wmsqcrecord/delete")
	public R deleteQcRecord(@RequestBody Long[] ids);

	// ------WmsQcResultController--------
	@RequestMapping("wms-service/qc/wmsqcresult/list")
	public R listQcResult(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/qc/wmsqcresult/info/{editor}")
	public R infoQcResult(@PathVariable("editor") String editor);

	@RequestMapping("wms-service/qc/wmsqcresult/save")
	public R saveQcResult(@RequestBody WmsQcResultEntity wmsQcResult);

	@RequestMapping("wms-service/qc/wmsqcresult/update")
	public R updateQcResult(@RequestBody WmsQcResultEntity wmsQcResult);

	@RequestMapping("wms-service/qc/wmsqcresult/delete")
	public R deleteQcResult(@RequestBody String[] editors);

	@RequestMapping("wms-service/qc/wmsqcresult/rejudgeitems")
	public R reJudgeInspectItems(@RequestParam Map<String, Object> params);
}
