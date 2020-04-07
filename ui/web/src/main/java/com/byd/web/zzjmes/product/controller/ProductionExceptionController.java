package com.byd.web.zzjmes.product.controller;

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

@RestController
@RequestMapping("zzjmes/productionException")
public class ProductionExceptionController {

	@Autowired
	PmdManagerRemote pmdManagerRemote;

	@RequestMapping("/getProductionExceptionPage")
    public R getProductionExceptionPage(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.getProductionExceptionPage(paramMap);
	}
	
	@RequestMapping("/exceptionConfirm")
    public R exceptionConfirm(@RequestParam Map<String, Object> paramMap){
		return pmdManagerRemote.exceptionConfirm(paramMap);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/exportException")
	public ResponseEntity<byte[]> exportException(@RequestParam Map<String, Object> params){
		HttpHeaders header = new HttpHeaders();
		String filename = "exception.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("werks_name-生产工厂");
		fieldTitleList.add("order_no-订单编号");
		fieldTitleList.add("product_no-零部件号");
		fieldTitleList.add("zzj_name-名称");
		fieldTitleList.add("product_no-图号");
		fieldTitleList.add("exception_type_name-异常类型");
		fieldTitleList.add("reason_type_name-异常原因");
		fieldTitleList.add("solution-处理方案");
		fieldTitleList.add("editor-录入人");
		
		List<Map<String,Object>> entityList = (List<Map<String, Object>>)pmdManagerRemote.getProductionExceptionList(params).get("result");
		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
			OutputStream outputstream = new FileOutputStream(tmpExcel);
			ExcelWriter.writeRecordToFile(outputstream,entityList,fieldTitleList);		
			outputstream.close();
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
					FileUtils.readFileToByteArray(tmpExcel), header,
					HttpStatus.CREATED);
			return responseEntity;
		} catch (Exception e) {
			FileUtils.deleteQuietly(tmpExcel);
			return null;
		}
	}
}
