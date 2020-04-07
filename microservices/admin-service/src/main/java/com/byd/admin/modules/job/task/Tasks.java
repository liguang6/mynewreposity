package com.byd.admin.modules.job.task;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.admin.modules.job.service.WmsBusinessJobsRemote;
import com.byd.admin.modules.job.service.WmsWebJobsRemote;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;

/**
 * 定时任务
 *
 * Tasks 为spring bean的名称
 *
 * @author develop01 
 * @since 1.2.0 2019-04-19
 */
@Component("Tasks")
public class Tasks {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DeptService sysDeptService;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private WmsBusinessJobsRemote wmsBusinessJobsRemote; //WMS-BUSINESS微服务提供物料、供应商、生产订单、采购订单等数据同步定时任务服务
	@Autowired
	private WmsWebJobsRemote wmsWebJobsRemote;//WEB web微服务提供的定时任务服务

	
	/**
	 * 加载系统所有工厂信息-工厂从SYS_DEPT表获取
	 */
	public void getAllWerksJobSync() {
		//logger.info("---->WMS-BUSINESS WmsJobs getAllWerksJobSync Start");
		//wmsBusinessJobsRemote.getAllWerksJobSync();
		//从SYS_DEPT表获取所有工厂
		List<Map<String, Object>> allWerks = sysDeptService.getPlantList("");
		//更新redis值
		redisUtils.set(RedisKeys.getAllWerksKey(), allWerks,RedisUtils.NOT_EXPIRE);
	}
	
	/**
	 * 加载系统所有仓库信息
	 */
	public void getAllWhJobSync() {
		logger.info("---->WMS-BUSINESS WmsJobs getAllWhJobSync Start" );
		
		wmsBusinessJobsRemote.getAllWhJobSync();
	}
	
	public void sapMaterialSync(String params) {
		logger.info("---->WMS-BUSINESS WmsJobs sapMaterialSync Start 参数为：" + params);
		wmsBusinessJobsRemote.sapMaterialSync(params);
	}	
	public void sapCustomerSync(String params) {
		logger.info("---->WMS-BUSINESS WmsJobs sapCustomerSync Start");
		wmsBusinessJobsRemote.sapCustomerSync(params);
	}
	
	public void sapVendorSync(String params) {
		logger.info("---->WMS-BUSINESS WmsJobs sapVendorSync Start");
		wmsBusinessJobsRemote.sapVendorSync(params);
	}
	public void sapMoSync(String params) {
		logger.info("---->WMS-BUSINESS WmsJobs sapMoSync Start");
		wmsBusinessJobsRemote.sapMoSync(params);
	}
	public void sapPOSync(String params) {
		logger.info("---->WMS-BUSINESS WmsJobs sapPOSync Start 参数为：" + params);
		wmsBusinessJobsRemote.sapPOSync(params);
	}
	
	public void sapPostJobSync() {
		logger.info("---->WMS-BUSINESS WmsJobs sapPostJobSync Start");
		wmsBusinessJobsRemote.sapPostJobSync();
	}
	
	public void languageSync() {
		logger.info("---->WEB WmsJobs languageSync Start");
		wmsWebJobsRemote.languageSync();
	}
	
	/**
	 * WMS过账成功，未及时返回SAP凭证的，定时任务再次获取
	 */
	public void saveSAPMatDoc() {
		logger.info("---->WMS-BUSINESS WmsJobs saveSAPMatDoc Start" );
		
		wmsBusinessJobsRemote.saveSAPMatDoc();
	}
	
	/**
	 * 供应商比对同步
	 */
	public void vendorCompareSync(String params) {
		wmsBusinessJobsRemote.vendorCompareSync(params);
	}
	/**
	 * 物料主数据比对同步
	 * @param params
	 */
	public void matCompareSync(String params) {
		wmsBusinessJobsRemote.matCompareSync(params);
	}


	/**
	 * 出入库库存日报表定时任务
	 */
	public void inOutStockQtyReport() {
		logger.info("---->WMS-BUSINESS WmsJobs inOutStockQtyReport Start" );
		wmsBusinessJobsRemote.inOutStockQtyReport();
	}
	
    //发动机物料报表定时任务

	public void saveProject() {		
		wmsBusinessJobsRemote.saveProject();
	}
	
	public void saveStock() {		
		wmsBusinessJobsRemote.saveStock();
	}

	public void exportQmsTestRecord(String params) {
		wmsWebJobsRemote.exportQmsTestRecord(params);
	}
	
	/**
	 * 清理零库存
	 */
	public void stockClear(String params) {
		wmsBusinessJobsRemote.stockClear(params);
	}

	/**
	 * 邮件通知定时任务
	 */
	public void sendEmail(){
		logger.info("---->WMS-BUSINESS WmsJobs sendEmail Start");
		wmsBusinessJobsRemote.sendEmail();
	}
	
	/**
	 * 过期冻结定时任务
	 */
	public void freeze(){
		wmsBusinessJobsRemote.freeze();
	}
}
