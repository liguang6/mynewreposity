package com.byd.web.zzjmes.produce.controller;

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
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.ExcelReader;
import com.byd.utils.ExcelWriter;
import com.byd.utils.FileUtils;
import com.byd.utils.R;
import com.byd.web.zzjmes.produce.service.PmdImportRemote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 下料明细导入
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("zzjmes/pmdImport")
public class PmdImportController {
	@Autowired
	PmdImportRemote pmdImportRemote;
	
	@RequestMapping("/upload")
    public R upload(MultipartFile excel,@RequestParam Map<String, Object> params){
		List<String[]> sheet =  null;
		try {
			sheet = ExcelReader.readExcel(excel);
		} catch (IOException e) {
			R.error("解析模板数据失败："+e.getMessage());
		}
		
		if(sheet != null && sheet.size() > 0){
			if(sheet.size() > 1000){
				return R.error("一次不能导入超过1000条数据！");
			}
			params.put("entityList", sheet);
			return pmdImportRemote.upload(params);
		}else {
			return R.error("没有找到需要上传的数据！");
		}
	}

	// 导出上传时校验出错的数据
	@RequestMapping("/exportExcel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam Map<String, Object> params) throws Exception{
		Gson gson=new Gson();
		List<Map<String,Object>> entityList =gson.fromJson((String) params.get("entityList"),
				new TypeToken<List<Map<String,Object>>>() {}.getType());
		HttpHeaders header = new HttpHeaders();
		String filename = "pmd_import_error.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		
		fieldTitleList.add("no-序号");
		fieldTitleList.add("sap_mat-SAP码");
		fieldTitleList.add("mat_description-物料描述");
		fieldTitleList.add("mat_type-物料类型");
		fieldTitleList.add("specification-材料/规格");
		fieldTitleList.add("unit-单位");
		fieldTitleList.add("loss-单车损耗%");
		fieldTitleList.add("quantity-单车用量");
		fieldTitleList.add("weight-单重");
		fieldTitleList.add("total_weight-总重含损耗");
		fieldTitleList.add("use_workshop-使用车间");
		fieldTitleList.add("process-工序");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("zzj_no-工艺标识");
		fieldTitleList.add("filling_size-下料尺寸");
		fieldTitleList.add("accuracy_demand-精度要求");
		fieldTitleList.add("surface_treatment-表面处理");
		fieldTitleList.add("memo-备注");
		fieldTitleList.add("processflow_memo-工艺备注");
		fieldTitleList.add("change_description-变更说明");
		fieldTitleList.add("change_subject-变更主体");
		fieldTitleList.add("cailiao_type-材料类型");
		fieldTitleList.add("material_no-图号");
		fieldTitleList.add("zzj_name-名称");
		fieldTitleList.add("subcontracting_type-分包类型");
		fieldTitleList.add("process_sequence-加工顺序");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("process_time-加工工时");
		fieldTitleList.add("process_machine-加工设备");
		fieldTitleList.add("section-工段");
		fieldTitleList.add("aperture-孔特征");
		fieldTitleList.add("maiban-埋板");
		fieldTitleList.add("banhou-板厚");
		fieldTitleList.add("filling_size_max-特殊大尺寸");
		fieldTitleList.add("errorMsg-错误信息");
		
		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
			// write records to file --
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
}
