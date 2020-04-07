package com.byd.web.zzjmes.product.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.ConfigConstant;
import com.byd.utils.ExcelWriter;
import com.byd.utils.FileUtils;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.zzjmes.product.service.ZzjJTOperationRemote;

@RestController
@RequestMapping("zzjmes/jtOperation")
public class ZzjJTOperationController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ZzjJTOperationRemote operationRemote;
	@Autowired
	private FreeMarkerConfigurer configurer;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	private ConfigConstant configConstant;

	@RequestMapping("/getJTPlan")
	public R getJTPlan(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.getJTPlan(paramMap);
	}

	@RequestMapping("/labelPreview")
	public void labelPreview(@RequestParam Map<String, Object> params) {
		String label_list = params.get("label_list") == null ? "" : params.get("label_list").toString();
		String baseDir = configConstant.getLocation() + "/qms/barcode/";
		List<Map> labelListMap = JSONObject.parseArray(label_list, Map.class);
		List<Map<String, Object>> listVars = new ArrayList<>();

		labelListMap.stream().forEach(m -> {
			String picturePath = baseDir + "zzj_" + m.get("zzj_pmd_items_id"); // 图片路径
			Map<String, Object> v = new HashMap<String, Object>();
			v.put("order_no", m.get("order_no"));
			v.put("zzj_plan_batch", m.get("zzj_plan_batch"));
			v.put("zzj_pmd_items_id", m.get("zzj_pmd_items_id"));
			v.put("werks", m.get("werks"));
			v.put("zzj_no", m.get("zzj_no"));
			v.put("workshop", m.get("workshop"));
			v.put("line", m.get("line"));
			// v.put("machine", m.get("machine"));

			try {
				File file = new File(picturePath);
				if (!file.exists()) {
					picturePath = QRCodeUtil.encode(JSONObject.toJSONString(v), "zzj_" + m.get("zzj_pmd_items_id")+m.get("zzj_plan_batch"), "",
							baseDir, false);
				}
				picturePath = picturePath.replaceAll("\\\\", "//");
				v.put("barCode", "file:" + picturePath);
				v.put("use_workshop", m.get("use_workshop"));
				v.put("process", m.get("process"));
				v.put("plan_quantity", m.get("plan_quantity"));
				v.put("plan_batch_qty", m.get("plan_batch_qty"));
				v.put("single_qty", m.get("single_qty"));
				v.put("filling_size", m.get("filling_size"));
				v.put("assembly_position", m.get("assembly_position"));
				v.put("process_flow", m.get("process_flow"));
				v.put("order_desc", m.get("order_desc"));
				v.put("zzj_name", m.get("zzj_name"));
				String aperture = m.get("aperture") == null ? "" : m.get("aperture").toString();
				v.put("aperture", StringUtils.isBlank(aperture) ? "" : "孔径：" + m.get("aperture"));
				v.put("specification", m.get("specification"));
				listVars.add(v);
				logger.info("---->自制件条码路径：" + picturePath);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return;
			}

		});

		PdfUtils.preview(configurer, "zzjmes/product/zzjLabelTmp.html", listVars, response);
	}

	@RequestMapping("/getPmdItems")
	public R getPmdItems(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.getPmdItems(paramMap);
	}

	@RequestMapping("/getPmdBaseInfo")
	public R getPmdBaseInfo(@RequestParam Map<String, Object> paramMap){
		return operationRemote.getPmdBaseInfo(paramMap);
	}

	@RequestMapping("/getMachineAchieve")
	public R getMachineAchieve(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.getMachineAchieve(paramMap);
	}

	@RequestMapping("/saveJTBindData")
	public R saveJTBindData(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.saveJTBindData(paramMap);
	}

	@RequestMapping("/getPmdOutputItems")
	public R getPmdOutputItems(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.getPmdOutputItems(paramMap);
	}

	@RequestMapping("/saveJTOutputData")
	public R saveJTOutputData(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.saveJTOutputData(paramMap);
	}

	@RequestMapping("/startOpera")
	public R startOpera(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.startOpera(paramMap);
	}

	@RequestMapping("/queryOutputRecords")
	public R queryOutputRecords(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.queryOutputRecords(paramMap);
	}

	@RequestMapping("/getPmdAbleQty")
	public R getPmdAbleQty(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.getPmdAbleQty(paramMap);
	}

	@RequestMapping("/savePmdOutQty")
	public R savePmdOutQty(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.savePmdOutQty(paramMap);
	}

	@RequestMapping("/deletePmdOutInfo")
	public R deletePmdOutInfo(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.deletePmdOutInfo(paramMap);
	}

	@RequestMapping("/scrapePmdOutInfo")
	public R scrapePmdOutInfo(@RequestParam Map<String, Object> paramMap) {
		return operationRemote.scrapePmdOutInfo(paramMap);
	}
	

	// 导出查询的数据
	@RequestMapping("/expData")
	public ResponseEntity<byte[]> expData(@RequestParam Map<String, Object> params) throws Exception {
		R r = operationRemote.queryOutputRecords(params);
		Map<String, Object> page = (Map<String, Object>) r.get("page");
		List<Map<String, Object>> recordlist = (List<Map<String, Object>>) page.get("list");
		recordlist.forEach(m -> {
			m.put("output_type", "1".equals(m.get("output_type")) ? "产量新增" : "产量报废");
		});
		HttpHeaders header = new HttpHeaders();
		String filename = "PmdOutputRecord.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("output_type-记录类型");
		fieldTitleList.add("material_no-图号");
		fieldTitleList.add("zzj_no-零部件号");
		fieldTitleList.add("zzj_name-零部件名称");
		fieldTitleList.add("order_no-订单");
		fieldTitleList.add("section-工段");
		fieldTitleList.add("workgroup-班组");
		fieldTitleList.add("zzj_plan_batch-生产批次");
		fieldTitleList.add("quantity-数量");
		fieldTitleList.add("productor-加工人");
		fieldTitleList.add("process-生产工序");
		fieldTitleList.add("process_flow-工艺流程");
		fieldTitleList.add("assembly_position-装配位置");
		fieldTitleList.add("user_process_name-使用工序");
		fieldTitleList.add("machine_code-加工机台");
		fieldTitleList.add("werks_name-工厂");
		fieldTitleList.add("workshop_name-车间");
		fieldTitleList.add("line_name-线别");
		fieldTitleList.add("editor-录入人");
		fieldTitleList.add("edit_date-录入时间");
		header.setContentDispositionFormData("attachment", filename);
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		File tmpExcel = new File(filename);
		try {
			tmpExcel.createNewFile();
			OutputStream outputstream = new FileOutputStream(tmpExcel);
			ExcelWriter.writeRecordToFile(outputstream, recordlist, fieldTitleList);
			outputstream.close();// 写完后,关闭输出流
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(tmpExcel),
					header, HttpStatus.CREATED);
			return responseEntity;
		} catch (IOException e) {
			FileUtils.deleteQuietly(tmpExcel);// 抛出异常时，先删除临时文件
			throw e;// 重新抛出异
		} finally {
			FileUtils.deleteQuietly(tmpExcel);// 删除临时文件
		}
	}

	
	@RequestMapping("/queryCombRecords")
	public R queryCombRecords(@RequestParam Map<String,Object> params) {	
		return operationRemote.queryCombRecords(params);
	}

	@RequestMapping("/checkBindPlan")
    public R checkBindPlan(@RequestParam Map<String, Object> params){
        return operationRemote.checkBindPlan(params);
    }
}
