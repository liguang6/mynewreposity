package com.byd.web.zzjmes.produce.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.ExcelWriter;
import com.byd.utils.R;
import com.byd.web.zzjmes.produce.service.PmdManagerRemote;
/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
@RestController
@RequestMapping("zzjmes/pmdManager")
public class PmdManagerController {
	@Autowired
	PmdManagerRemote pmdManagerRemote;
	
	@RequestMapping("/getPmdList")
    public R getPmdList(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.getPmdList(paramMap);
	}
	
	@RequestMapping("/getPmdListPage")
    public R getPmdListPage(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.getPmdListPage(paramMap);
	}
	
	@RequestMapping("/getSubcontractingHeadPage")
    public R getSubcontractingHeadPage(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.getSubcontractingHeadPage(paramMap);
	}
	
	@RequestMapping("/getSubcontractingItemList")
    public R getSubcontractingItemList(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.getSubcontractingItemList(paramMap);
	}

	@RequestMapping("/editSubcontractingItem")
	public R editSubcontractingItem(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.editSubcontractingItem(paramMap);
	}

	@RequestMapping("/getSubcontractingPage")
    public R getSubcontractingPage(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.getSubcontractingPage(paramMap);
	}
	
	@RequestMapping("/editPmdList")
    public R editPmdList(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.editPmdList(paramMap);
	}

	@RequestMapping("/deletePmdList")
	public R deletePmdList(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.deletePmdList(paramMap);
	}
	
	@RequestMapping("/editSubcontractingList")
    public R editSubcontractingList(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.editSubcontractingList(paramMap);
	}
	
	@RequestMapping("/getPmdProcessPlanCount")
    public R getPmdProcessPlanCount(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.getPmdProcessPlanCount(paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/exportPmd")
	public ResponseEntity<byte[]> exportPmd(@RequestParam Map<String, Object> params){
		HttpHeaders header = new HttpHeaders();
		String filename = "pmd.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("no-序号");
		fieldTitleList.add("order_no-订单编号");
		fieldTitleList.add("werks_name-生产工厂");
		fieldTitleList.add("workshop_name-生产车间");
		fieldTitleList.add("line_name-线别");
		fieldTitleList.add("material_no-图号");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("mat_description-物料描述");
		fieldTitleList.add("mat_type-物料类型");
		fieldTitleList.add("specification-材料/规格");
		fieldTitleList.add("filling_size-下料尺寸");
		fieldTitleList.add("cailiao_type-材料类型");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("process-使用工序代码");
		fieldTitleList.add("process_name-使用工序名称");
		fieldTitleList.add("process_sequence-加工顺序");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("process_time-加工工时");
		fieldTitleList.add("process_machine-加工设备");
		fieldTitleList.add("subcontracting_type-分包类型");
		fieldTitleList.add("aperture-孔特征");
		fieldTitleList.add("accuracy_demand-精度要求");
		fieldTitleList.add("surface_treatment-表面处理");
		fieldTitleList.add("weight-单重");
		fieldTitleList.add("use_workshop-使用车间");
		fieldTitleList.add("unit-单位");
		fieldTitleList.add("quantity-单车用量");
		fieldTitleList.add("loss-单车损耗");
		fieldTitleList.add("total_weight-总重含损耗");
		fieldTitleList.add("memo-备注");
		fieldTitleList.add("processflow_memo-工艺备注");
		fieldTitleList.add("section-工段");
		fieldTitleList.add("maiban-埋板");
		fieldTitleList.add("banhou-板厚");
		fieldTitleList.add("filling_size_max-特殊大尺寸");
		fieldTitleList.add("sap_mat-SAP码");
		fieldTitleList.add("change_description-变更说明");
		fieldTitleList.add("change_subject-变更主体");
		fieldTitleList.add("change_type-变更类型");
		
		List<Map<String,Object>> entityList = (List<Map<String, Object>>)pmdManagerRemote.getPmdList(params).get("result");
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
		} catch (Exception e) {
			FileUtils.deleteQuietly(tmpExcel);//抛出异常时，先删除临时文件
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/exportSubcontracting")
	public ResponseEntity<byte[]> exportSubcontracting(@RequestParam Map<String, Object> params){
		HttpHeaders header = new HttpHeaders();
		String filename = "subcontracting.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("order_no-订单编号");
		fieldTitleList.add("product_order-SAP工单号");
		fieldTitleList.add("order_no-订单编号");
		fieldTitleList.add("sap_mat-SAP料号");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("process-委外工序");
		fieldTitleList.add("outsourcing_quantity-委外数量");
		fieldTitleList.add("unit-单位");
		fieldTitleList.add("weight-单重");
		fieldTitleList.add("total_weight-重量");
		fieldTitleList.add("quantity-单次重量");
		fieldTitleList.add("memo-备注");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("use_workshop-使用车间");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("sender-发料人");
		fieldTitleList.add("editor-操作人");
		fieldTitleList.add("edit_date-操作时间");
		
		List<Map<String,Object>> entityList = (List<Map<String, Object>>)pmdManagerRemote.getSubcontractingList(params).get("result");
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
		} catch (Exception e) {
			FileUtils.deleteQuietly(tmpExcel);//抛出异常时，先删除临时文件
			return null;
		}
	}
	
}
