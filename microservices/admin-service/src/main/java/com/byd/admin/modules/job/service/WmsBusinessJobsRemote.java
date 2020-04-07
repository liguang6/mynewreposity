package com.byd.admin.modules.job.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * WMS-BUSINESS微服务提供物料、供应商、生产订单、采购订单等数据同步定时任务服务
 * @author develop01
 * @since 2019-04-19
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsBusinessJobsRemote {
	
	@RequestMapping(value = "/wms-service/wmsJobs/sapMaterialSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sapMaterialSync(@RequestParam(value = "params") String params) ;
	
	@RequestMapping(value = "/wms-service/wmsJobs/sapCustomerSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sapCustomerSync(@RequestParam(value = "params") String params);
	
	@RequestMapping(value = "/wms-service/wmsJobs/sapVendorSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sapVendorSync(@RequestParam(value = "params") String params);
	
	
	@RequestMapping(value = "/wms-service/wmsJobs/sapMoSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sapMoSync(@RequestParam(value = "params") String params);
	
	@RequestMapping(value = "/wms-service/wmsJobs/sapPOSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sapPOSync(@RequestParam(value = "params") String params);
	
	@RequestMapping(value = "/wms-service/wmsJobs/sapPostJobSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sapPostJobSync();
	
	@RequestMapping(value = "/wms-service/wmsJobs/getAllWhSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getAllWhJobSync();
	
	@RequestMapping(value = "/wms-service/wmsJobs/saveSAPMatDoc.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void saveSAPMatDoc();
	
	@RequestMapping(value = "/wms-service/wmsJobs/vendorCompareSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void vendorCompareSync(@RequestParam(value = "params") String params);

	@RequestMapping(value = "/wms-service/wmsJobs/matCompareSync.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void matCompareSync(@RequestParam(value = "params") String params);


	/**
	 * 出入库库存日报表定时任务
	 */
	@RequestMapping(value = "/wms-service/wmsJobs/inOutStockQtyReport.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void inOutStockQtyReport() ;
	
	/**
	 * 发动机物料报表定时任务
	 */
	@RequestMapping(value = "/wms-service/wmsJobs/saveProject.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void saveProject() ;

	@RequestMapping(value = "/wms-service/wmsJobs/saveStock.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void saveStock();
	
	@RequestMapping(value = "/wms-service/wmsJobs/stockClear.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void stockClear(@RequestParam(value = "params") String params);

	@RequestMapping(value = "/wms-service/wmsJobs/sendEmail.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sendEmail();
	
	@RequestMapping(value = "/wms-service/wmsJobs/freeze.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void freeze();
}
