package com.byd.web.wms.out.service;

import java.util.List;
import java.util.Map;

import com.byd.web.wms.config.entity.WmsCoreWhBinEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.R;
import com.byd.web.wms.out.entity.CostCenterAO;
import com.byd.web.wms.out.entity.CreateProduceOrderAO;
import com.byd.web.wms.out.entity.InternalOrderAO;
import com.byd.web.wms.out.entity.ProduceOrderVO;
import com.byd.web.wms.out.entity.WbsElementAO;

/**
 * 出库需求创建模块服务调用
 * 
 * @author develop07
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsOutReqServiceRemote {
	// InnerOrderController
	@RequestMapping("wms-service/out/innerorder/SAPInnerOrder/{innerOrderNo}")
	public R getInnerOrderFromSAP(
			@PathVariable("innerOrderNo") String innerOrderNo);

	@RequestMapping("wms-service/out/innerorder/createInternalOrder")
	public R createIternalOrderRequirement(
			List<InternalOrderAO> internalOrderList);

	// OutReqCreateController

	@RequestMapping("wms-service/out/outreq/queryOutItems6")
	public R queryOutItems6(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/queryOutItems7")
	public R queryOutItems7(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/outReqCreate6")
	public R outReqCreate6(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/outReqCreate7")
	public R outReqCreate7(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/validateOutItems6")
	public R validateOutItems6(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/validateOutItems7")
	public R validateOutItems7(@RequestBody Map<String, Object> data);

	@RequestMapping("wms-service/out/outreq/validateOutItem10")
	public R validate10(@RequestBody Map<String, Object> list);

	@RequestMapping("wms-service/out/outreq/queryOutItem10")
	public R queryOutItem10(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/outReqCreate10")
	public R createOutReq10(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/validateOutItem8")
	public R validateOutItem8(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/queryOutItem8")
	public R queryOutItem8(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/outReqCreate8")
	public R outReqCreate8(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/queryTotalStockQty")
	public R queryTotalStockQty(@RequestBody Map<String, Object> params);

	@RequestMapping("wms-service/out/outreq/createOutReq11")
	public R createOutReq11(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/mathx")
	public R validateMatrialsHX(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/createOutReq13")
	public R createOutReq13(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/validate16")
	public R validate16(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/query16")
	public R query16(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/create16")
	public R create16(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/validate17")
	public R validate17(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/query17")
	public R query17(@RequestBody List<Map<String, Object>> list);

	@RequestMapping("wms-service/out/outreq/create17")
	public R create17(@RequestBody List<Map<String, Object>> list);

	// ProduceLineWarehouseController

	@RequestMapping("wms-service/out/producelinewarehouse/processProduceOrder")
	public R processProduceOrder(@RequestBody Map<String,Object> data);

	@RequestMapping("wms-service/out/producelinewarehouse/producerOrders")
	public R producerOrders(@RequestBody Map<String, Object> params);

	@RequestMapping("wms-service/out/producelinewarehouse/create")
	public R create(@RequestBody Map<String,Object> data);

	// ProduceOrderAddMatController

	@RequestMapping("wms-service/out/produceorderaddmat/create")
	public R createAddMat(@RequestBody Map<String,Object> data);

	@RequestMapping("wms-service/out/produceorderaddmat/processProduceOrder")
	public R processProduceOrderAddMat(
			@RequestBody Map<String,Object> data);

	@RequestMapping("wms-service/out/produceorderaddmat/producerOrders")
	public R queryProducerOrderInfo(@RequestBody Map<String, Object> params);

	// ProduceOrderController

	@RequestMapping("wms-service/out/createRequirement/getPlantBusinessTypes")
	public R getPlantBusinessTypes(@RequestParam Map<String, Object> params);

	@RequestMapping("wms-service/out/createRequirement/processProduceOrder")
	public R processProduceOrderO(
			@RequestBody List<ProduceOrderVO> produceOrders);

	@RequestMapping("wms-service/out/createRequirement/producerOrders")
	public R queryProducerOrderInfoO(@RequestBody Map<String, Object> params);

	@RequestMapping("wms-service/out/createRequirement/sapCostcenter/{Costcenter}")
	public Map<String, Object> getSapCostcenterDetail(
			@PathVariable("Costcenter") String Costcenter);

	@RequestMapping("wms-service/out/createRequirement/mat")
	public R getMat(@RequestParam("werks")String werks,@RequestParam("mat") String mat);

	@RequestMapping("wms-service/out/createRequirement/costcenterReq")
	public R createCostcenterRequirement(@RequestBody List<CostCenterAO> items);

	@RequestMapping("wms-service/out/createRequirement/lgortAndStock")
	public R getLgortStockByMaterial(@RequestParam("werks")String werks,@RequestParam("whNumber") String whNumber,@RequestParam("matnr") String matnr);

	@RequestMapping("wms-service/out/createRequirement/costcenterBatchLoadPreview")
	public R batchLoadPreview(MultipartFile excel);

	@RequestMapping("wms-service/out/createRequirement/create")
	public R createProduceOrderReq(@RequestBody List<CreateProduceOrderAO> cList);

	/**
	 * 查询非上线物料
	 * 
	 * @param werks
	 * @return
	 */
	@RequestMapping("wms-service/out/createRequirement/matuseing/{werks}")
	public R matUsing(@PathVariable("werks") String werks);

	@RequestMapping("wms-service/out/createRequirement/sapPoItemHead/{werks}/{poNo}/")
	public R sapPoItemHead(@PathVariable("werks") String werks,
			@PathVariable("poNo") String poNo);

	// WbsElementController

	@RequestMapping("wms-service/out/wbsElement/wbs_element/{wbsElementNo}")
	public R queryWBSElement(@PathVariable("wbsElementNo") String wbsElementNo);

	@RequestMapping("wms-service/out/wbsElement/save")
	public R saveWBSElement(@RequestBody List<WbsElementAO> wbsList);

	@RequestMapping("wms-service/out/wbsElement/pdf")
	public void pdfPreview(@RequestParam("requirementNo")String requirementNo,@RequestParam("printSize") String printSize);

	// ScannerOutController
	
	@RequestMapping("wms-service/out/scanner/queryScanner")
	public R queryScannerOut(@RequestBody Map<String, Object> params);

	@RequestMapping("wms-service/out/scanner/validReqNo")
	public R validReqNo(@RequestBody Map<String, Object> params);

	@RequestMapping("wms-service/out/scanner/queryBusinessName")
	public R queryBusinessName(@RequestBody Map<String, Object> params);
	
	@RequestMapping("wms-service/out/scanner/obtained")
	public R obtained(@RequestBody List<Map<String,Object>> params);

	@RequestMapping("wms-service/out/scanner/cancelObtained")
	public R cancelObtained(@RequestBody List<Map<String,Object>> params);
	
	@RequestMapping("wms-service/out/scanner/handoverComfirm")
	public R handoverComfirm(@RequestBody List<Map<String,Object>> params);

	@RequestMapping("wms-service/out/outreq/createOutReq18")
	public R createOutReq18(@RequestBody List<Map<String, Object>> list);
	
	@RequestMapping("wms-service/out/outreq/queryOutReqPda311")
	public R queryOutReqPda311(@RequestBody Map<String, Object> map);
	
	@RequestMapping("wms-service/out/outreq/queryUNIT")
	public R queryUNIT(@RequestBody Map<String, Object> map);
	
	@RequestMapping("wms-service/out/outreq/createOutReqPda311")
	public R createOutReqPda311(@RequestBody List<Map<String, Object>> list);
	

	@RequestMapping("wms-service/out/outreq/createOutReq20")
	public R createOutReq20(@RequestBody List<Map<String, Object>> list);
	
	@RequestMapping("wms-service/out/outreq/validate21")
	public R validate21(@RequestBody List<Map<String, Object>> list);
	
	@RequestMapping("wms-service/out/outreq/query21")
	public R query21(@RequestBody List<Map<String, Object>> list);
	
	@RequestMapping("wms-service/out/outreq/createOutReq21")
	public R createOutReq21(@RequestBody List<Map<String, Object>> list);


	@RequestMapping("wms-service/out/scanner/queryBarcodeCache")
	List<Map<String, Object>> queryBarcodeCache(Map<String, Object> data);
	
	@RequestMapping("wms-service/out/requirement/list")
	R queryRequirementList(@RequestBody Map<String, Object> params);
	
	@RequestMapping("wms-service/out/requirement/items")
	R queryRequirementItemsList(@RequestBody Map<String, Object> params);
	
	@RequestMapping("wms-service/out/requirement/close")
	R closeRequirement(@RequestBody Map<String, Object> params);

	/**
	 * 导入预览
	 */
	@RequestMapping(value = "wms-service/out/innerorder/previewNbdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewNbdd(@RequestBody List<Map<String,Object>> params);

	/**
	 * 导入预览
	 */
	@RequestMapping(value = "wms-service/out/createRequirement/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R preview(@RequestBody List<Map<String,Object>> params);

	/*
		导入预览
	 */
	@RequestMapping(value = "wms-service/out/producelinewarehouse/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewBy311(@RequestBody List<Map<String,Object>> params);

	/**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException
	 */
	@RequestMapping(value = "wms-service/out/createRequirement/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<CreateProduceOrderAO> entityList) ;

	@RequestMapping(value = "wms-service/out/createRequirement/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R uploadBy311(List<CreateProduceOrderAO> entityList);

	@RequestMapping(value = "wms-service/out/outreq/validateCostomer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R validateCostomer(@RequestParam Map<String, Object> params);

}
