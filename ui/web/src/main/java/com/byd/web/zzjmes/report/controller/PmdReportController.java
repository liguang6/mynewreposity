package com.byd.web.zzjmes.report.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.ExcelWriter;
import com.byd.utils.FileUtils;
import com.byd.utils.R;
import com.byd.web.sys.masterdata.service.MasterDataRemote;
import com.byd.web.zzjmes.report.service.PmdReportRemote;

/**
 * 自制件报表通用控制器
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("zzjmes/report")
public class PmdReportController {
    @Autowired
    private PmdReportRemote pmdReportRemote; 
    @Autowired
    private MasterDataRemote masterDataRemote; 
    
    /**
     * 订单达成明细报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdOutputReachReport")
    public R pmdOutputReachReport(@RequestParam Map<String, Object> params){
    	return pmdReportRemote.pmdOutputReachReport(params);
    }
    
	/**
	 *  订单达成明细报表导出
	 */
	@RequestMapping("/pmdOutputReachReportExport")
    public ResponseEntity<byte[]> pmdOutputReachReportExport(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) pmdReportRemote.getPmdOutputReachList(params).get("data");
		HttpHeaders header = new HttpHeaders();
		String filename = "pmdOutputReachReport.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		
		fieldTitleList.add("no-序号");
		fieldTitleList.add("material_no-图号");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("batch-批次");
		fieldTitleList.add("batch-车付数");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("prod_process-生产工序");
		fieldTitleList.add("pmd_batch_req_qty-需求数量");
		fieldTitleList.add("plan_quantity-计划数量");
		fieldTitleList.add("prod_quantity-生产数量");
		fieldTitleList.add("un_prod_quantity-欠产数量");
		fieldTitleList.add("current_process_name-当前工序");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("process-使用工序");
		fieldTitleList.add("section-工段");
		fieldTitleList.add("order_desc-订单");

		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
			OutputStream outputstream = new FileOutputStream(tmpExcel);
			ExcelWriter.writeRecordToFile(outputstream,entityList,fieldTitleList);		
			outputstream.close();//写完后,关闭输出流
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(tmpExcel), header,
					HttpStatus.CREATED);
			return responseEntity;
		} catch (IOException e) {
			FileUtils.deleteQuietly(tmpExcel);//抛出异常时，先删除临时文件
			throw e;// 重新抛出异
		} finally {
			FileUtils.deleteQuietly(tmpExcel);// 删除临时文件
		}
	}
    
    /**
     * 批次达成明细报表
     * @param params
     * @return
     */
    @RequestMapping("/batchOutputReachReport")
    public R batchOutputReachReport(@RequestParam Map<String, Object> params){
    	return pmdReportRemote.batchOutputReachReport(params);
    }
    
	/**
	 *  订单达成明细报表导出
	 */
	@RequestMapping("/batchOutputReachReportExport")
    public ResponseEntity<byte[]> batchOutputReachReportExport(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) pmdReportRemote.getBatchOutputReachList(params).get("data");
		HttpHeaders header = new HttpHeaders();
		String filename = "batchOutputReachReport.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		
		fieldTitleList.add("no-序号");
		fieldTitleList.add("material_no-图号");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("batch-批次");
		fieldTitleList.add("batch-车付数");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("prod_process-生产工序");
		fieldTitleList.add("pmd_batch_req_qty-需求数量");
		fieldTitleList.add("plan_quantity-计划数量");
		fieldTitleList.add("prod_quantity-生产数量");
		fieldTitleList.add("un_prod_quantity-欠产数量");
		fieldTitleList.add("current_process_name-当前工序");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("process-使用工序");
		fieldTitleList.add("section-工段");
		fieldTitleList.add("order_desc-订单");

		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
			OutputStream outputstream = new FileOutputStream(tmpExcel);
			ExcelWriter.writeRecordToFile(outputstream,entityList,fieldTitleList);		
			outputstream.close();//写完后,关闭输出流
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(tmpExcel), header,
					HttpStatus.CREATED);
			return responseEntity;
		} catch (IOException e) {
			FileUtils.deleteQuietly(tmpExcel);//抛出异常时，先删除临时文件
			throw e;// 重新抛出异
		} finally {
			FileUtils.deleteQuietly(tmpExcel);// 删除临时文件
		}
	}
    
    /**
     * 订单需求达成报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdReqReachReport")
    public R pmdReqReachReport(@RequestParam Map<String, Object> params){
    	return pmdReportRemote.pmdReqReachReport(params);
    }
    
    /**
     * 订单批次需求达成报表
     * @param params
     * @return
     */
    @RequestMapping("/orderBatchReachReport")
    public R orderBatchReachReport(@RequestParam Map<String, Object> params){
    	return pmdReportRemote.orderBatchReachReport(params);
    }
    
    /**
     * 班组计划达成报表
     * @param params
     * @return
     */
    @RequestMapping("/workgroupReachReport")
    public R workgroupReachReport(@RequestParam Map<String, Object> params){
    	R r = pmdReportRemote.workgroupReachReport(params);
    	return r;
    }
    
    /**
     * 组合件加工进度报表
     * @param params
     * @return
     */
    @RequestMapping("/orderAssemblyReport")
    public R orderAssemblyReport(@RequestParam Map<String, Object> params){
    	return pmdReportRemote.orderAssemblyReport(params);
    }
    
	/**
	 *  订单达成明细报表导出
	 */
	@RequestMapping("/orderAssemblyReportExport")
    public ResponseEntity<byte[]> orderAssemblyReportExport(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) pmdReportRemote.getOrderAssemblyList(params).get("data");
		HttpHeaders header = new HttpHeaders();
		String filename = "orderAssemblyReport.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		
		fieldTitleList.add("material_no-图号");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("batch-批次");
		fieldTitleList.add("batch-车付数");
		fieldTitleList.add("pmd_batch_req_qty-需求数量");
		fieldTitleList.add("plan_quantity-计划数量");
		fieldTitleList.add("prod_quantity-生产数量");
		fieldTitleList.add("un_prod_quantity-欠产数量");
		fieldTitleList.add("component_req_quantity-需求子件种类");
		fieldTitleList.add("component_finished_quantity-完成子件种类");
		fieldTitleList.add("component_un_finished_quantity-欠产子件种类");
		fieldTitleList.add("order_desc-订单");

		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
			OutputStream outputstream = new FileOutputStream(tmpExcel);
			ExcelWriter.writeRecordToFile(outputstream,entityList,fieldTitleList);		
			outputstream.close();//写完后,关闭输出流
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(tmpExcel), header,
					HttpStatus.CREATED);
			return responseEntity;
		} catch (IOException e) {
			FileUtils.deleteQuietly(tmpExcel);//抛出异常时，先删除临时文件
			throw e;// 重新抛出异
		} finally {
			FileUtils.deleteQuietly(tmpExcel);// 删除临时文件
		}
	}

	/**
     * 工序工时-加工时长报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdProcessTimeReport")
    public R pmdProcessTimeReport(@RequestParam Map<String, Object> params){
    	return pmdReportRemote.pmdProcessTimeReport(params);
	}
	
	/**
     * 工序工时-流转时间报表
     * @param params
     * @return
     */
    @RequestMapping("/pmdProcessTransferTimeReport")
    public R pmdProcessTransferTimeReport(@RequestParam Map<String, Object> params){
    	return pmdReportRemote.pmdProcessTransferTimeReport(params);
    }

}