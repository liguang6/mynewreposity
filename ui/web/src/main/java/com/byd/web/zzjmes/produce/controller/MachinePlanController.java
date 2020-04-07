package com.byd.web.zzjmes.produce.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.zzjmes.produce.service.MachinePlanRemote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 机台计划
 * @author cscc tangj
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("zzjmes/machinePlan")
public class MachinePlanController {
	@Autowired
	MachinePlanRemote machinePlanRemote;
	@Autowired
    private UserUtils userUtils;
	@RequestMapping("/getMachinePlanList")
    public R getMachinePlanList(@RequestParam Map<String, Object> params){
		
		return machinePlanRemote.getMachinePlanList(params);
	}
	
	/**查询分页**/
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
        return machinePlanRemote.queryPage(params);
    }
	@RequestMapping("/upload")
    public R upload(MultipartFile excel,@RequestParam Map<String, Object> params){
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		Map<String,Object> user = userUtils.getUser();
		params.put("username", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		List<String[]> sheet =  null;
		try {
			sheet = ExcelReader.readExcel(excel);
		} catch (IOException e) {
			R.error(e.getMessage());
		}
		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				//map.put("no", row[index]);
				map.put("sap_mat", row[index]);
				map.put("process", row[++index]);
				map.put("assembly_position", row[++index]);
				map.put("zzj_no", row[++index]);
				map.put("zzj_name", row[++index]);
				map.put("process_flow", row[++index]);
				map.put("plan_process", row[++index]);
				map.put("plan_quantity", row[++index]);
				map.put("machine", row[++index]);
				map.put("plan_date", row[++index]);
				map.put("product_order", row[++index]);
				entityList.add(map);
				index=0;
			}
		}
		if(entityList.size()>1000){
            return R.error("导入数据大于1000条，请拆分Excel分批导入！");
		}
		params.put("entityList", entityList);
		return machinePlanRemote.upload(params);
	}
	
	@RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
		Map<String,Object> user = userUtils.getUser();
		params.put("username", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		return machinePlanRemote.save(params);
	}
	@RequestMapping("/del")
    public R del(@RequestParam Map<String, Object> params){
		Map<String,Object> user = userUtils.getUser();
		params.put("username", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		return machinePlanRemote.del(params);
	}
	@RequestMapping("/checkExsist")
    public R checkExsist(@RequestParam Map<String, Object> params){
		return machinePlanRemote.checkExsist(params);
	}
	
	@RequestMapping("/getMachineInfo")
	public R getMachineInfo(@RequestParam Map<String, Object> params) {
		return machinePlanRemote.getMachineInfo(params);
	}
	// 导出查询的数据
	@RequestMapping("/expData")
    public ResponseEntity<byte[]> expData(@RequestParam Map<String, Object> params) throws Exception{
		List<Map<String,Object>> entityList =(List<Map<String, Object>>) machinePlanRemote.getMachinePlanList(params).get("data");
		HttpHeaders header = new HttpHeaders();
		String filename = "machinePlan.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("sap_mat-SAP码");
		fieldTitleList.add("process-使用工序");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("plan_process-计划工序");
		fieldTitleList.add("plan_quantity-计划数量");
		fieldTitleList.add("machine-机台");
		fieldTitleList.add("plan_date-生产日期");
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
	// 导出上传时校验出错的数据
	@RequestMapping("/expUploadData")
    public ResponseEntity<byte[]> expUploadData(@RequestParam Map<String, Object> params) throws Exception{
		Gson gson=new Gson();
		List<Map<String,Object>> entityList =gson.fromJson((String) params.get("entityList"),
				new TypeToken<List<Map<String,Object>>>() {}.getType());
		HttpHeaders header = new HttpHeaders();
		String filename = "machinePlan.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("sap_mat-SAP码");
		fieldTitleList.add("process-使用工序");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("plan_process-计划工序");
		fieldTitleList.add("plan_quantity-计划数量");
		fieldTitleList.add("machine-机台");
		fieldTitleList.add("plan_date-生产日期");
		fieldTitleList.add("product_order-生产工单");
		fieldTitleList.add("msg-校验信息");
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
    
    // 导出模板数据
 	@RequestMapping("/expTemplateData")
     public ResponseEntity<byte[]> expTemplateData(@RequestParam Map<String, Object> params) throws Exception{
 		Gson gson=new Gson();
		List<Map<String,Object>> entityList =new ArrayList<Map<String,Object>>();
		Object data=machinePlanRemote.getTemplateData(params).get("data");
		if(data!=null) {
			entityList=(List<Map<String, Object>>) data;
		}
				
 		HttpHeaders header = new HttpHeaders();
 		String filename = "template.xlsx";
 		List<String> fieldTitleList = new ArrayList<String>();
 		fieldTitleList.add("sap_mat-SAP码");
 		fieldTitleList.add("process-使用工序");
 		fieldTitleList.add("assembly_position-装配位置");
 		fieldTitleList.add("zzj_no-零部件号");
 		fieldTitleList.add("zzj_name-零部件名称");
 		fieldTitleList.add("process_flow-工艺流程");
 		fieldTitleList.add("plan_process-计划工序");
 		fieldTitleList.add("plan_quantity-计划数量");
 		fieldTitleList.add("machine-机台");
 		fieldTitleList.add("plan_date-生产日期");
 		fieldTitleList.add("product_order-生产工单");
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
    @RequestMapping("/getOutputRecords")
    public R getOutputRecords(@RequestParam Map<String, Object> params){
		
		return machinePlanRemote.getOutputRecords(params);
	}
}
