package com.byd.web.zzjmes.produce.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelWriter;
import com.byd.utils.FileUtils;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.zzjmes.produce.service.MatHandoverRemote;

/**
 * 车间内交接
 * @author cscc tangj
 * @email 
 * @date 2019-09-23 16:16:08
 */
@RestController
@RequestMapping("zzjmes/matHandover")
public class MatHandoverController {
    @Autowired
    private MatHandoverRemote matHandoverRemote;
    @Autowired
	private FreeMarkerConfigurer configurer;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	private ConfigConstant configConstant;
    /**查询分页**/
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
        return matHandoverRemote.queryPage(params);
    }
    /**车间供货查询分页**/
    @RequestMapping("/querySupplyPage")
    public R querySupplyPage(@RequestParam Map<String, Object> params){
        return matHandoverRemote.querySupplyPage(params);
    }
    
    /**车间供货查询明细**/
    @RequestMapping("/getSupplyDetailList")
    public R getSupplyDetailList(@RequestParam Map<String, Object> params){
        return matHandoverRemote.getSupplyDetailList(params);
    }
    
    @RequestMapping("/getMatInfo")
 	public R getMatInfo(@RequestParam Map<String,Object> params){
 		return matHandoverRemote.getMatInfo(params);
 	}
    
    /**
     * 交接保存
     * @param params
     * @return
     */
    @RequestMapping("/save")
  	public R save(@RequestParam Map<String,Object> params){
  		return matHandoverRemote.save(params);
  	}
    /**
     * 获取车间供货
     * @param params
     * @return
     */
    @RequestMapping("/getSupplyMatInfo")
 	public R getSupplyMatInfo(@RequestParam Map<String,Object> params){
 		return matHandoverRemote.getSupplyMatInfo(params);
 	}
    /**
     * 车间供货保存
     * @param params
     * @return
     */
    @RequestMapping("/saveSupply")
  	public R saveSupply(@RequestParam Map<String,Object> params){
  		return matHandoverRemote.saveSupply(params);
  	}
 // 导出查询的数据
 	@RequestMapping("/expData")
     public ResponseEntity<byte[]> expData(@RequestParam Map<String, Object> params) throws Exception{
 		
 		HttpHeaders header = new HttpHeaders();
 		String filename = "data.xlsx";
 		List<String> fieldTitleList = new ArrayList<String>();
 		fieldTitleList.add("werks-供应工厂");
 		fieldTitleList.add("workshop_name-供应车间");
 		//fieldTitleList.add("line_name-供应线别");
 		fieldTitleList.add("zzj_no-零部件号");
 		fieldTitleList.add("zzj_name-零部件名称");
 		fieldTitleList.add("assembly_position-装配位置");
 		fieldTitleList.add("process-使用工序");
 		fieldTitleList.add("process_flow-工艺流程");
 		fieldTitleList.add("deliver_qty-交接数量");
 		fieldTitleList.add("receive_date-交接日期");
 		fieldTitleList.add("deliver_user-交付人");
		fieldTitleList.add("receive_user-接收人");
		fieldTitleList.add("handover_type_desc-交接方式");
 		fieldTitleList.add("deliver-交付工序/班组");
 		fieldTitleList.add("receive-接收工序/班组");
 		fieldTitleList.add("receive_workshop-接收车间");
 		fieldTitleList.add("order_desc-订单");
 		fieldTitleList.add("zzj_plan_batch-批次");
 		header.setContentDispositionFormData("attachment", filename);
 		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
 		File tmpExcel = new File(filename);
 		try {
 			List<Map<String,Object>> entityList =new ArrayList<Map<String,Object>>();
 	 		if(matHandoverRemote.getList(params).get("data")!=null) {
 	 		    entityList=(List<Map<String, Object>>) matHandoverRemote.getList(params).get("data");
 	 		}
 	 		if(entityList.size()==0) {
 	 			throw new RuntimeException("未找到导出数据！");
 	 		}
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
     @RequestMapping("/getHandoverRuleList")
     public R getHandoverRuleList(@RequestParam Map<String, Object> params){
    	 
    	 return matHandoverRemote.getHandoverRuleList(params);
 	 }
     
     /**
 	 * 车间供货清单打印预览
 	 * 
 	 * @param params
 	 */
 	@RequestMapping(value = "/workshopSupplyPreview")
 	public void workshopSupplyPreview(@RequestParam Map<String, Object> params) {
 		String arr_str = params.get("matList") == null ? "" : params.get("matList").toString();
 		String baseDir = configConstant.getLocation() + "/barcode/";
 		
 		Map<String, Object> variables = new HashMap<>();// 模板数据封装
 		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf文件数
 		List<Map<String, Object>> listVars = new ArrayList<>();
 		
 		List<Map> dataList = null;
 		if (arr_str.length() > 0) {
 			List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
 			try {
 				dataList = JSONObject.parseArray(arr_str, Map.class);
 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 			variables.put("werks", dataList.get(0).get("werks"));
 			variables.put("werks_name", dataList.get(0).get("werks_name"));
 			variables.put("workshop", dataList.get(0).get("workshop"));
 			variables.put("workshop_name", dataList.get(0).get("workshop_name"));
 			variables.put("receive_workshop_name", dataList.get(0).get("receive_workshop_name"));
 			variables.put("deliver_user", dataList.get(0).get("deliver_user"));
 			variables.put("receive_user", dataList.get(0).get("receive_user"));
 			variables.put("deliver_date", dataList.get(0).get("deliver_date"));
 			Integer total_supply_quantity=0;
 			for (int i = 0; i < dataList.size(); i++) {
 				Map<String, Object> m = (Map<String, Object>) dataList.get(i);
 				m.put("INDEX", i + 1);
 				total_supply_quantity+=Integer.valueOf(dataList.get(i).get("supply_quantity").toString());
 				itemList.add(m);
 			}
 			variables.put("total_supply_quantity", total_supply_quantity);
 			// 生成二维码
 			try {
 				String picturePath = ""; // 图片路径
 				StringBuffer sb = new StringBuffer();
 				sb.append("{werks:" + dataList.get(0).get("werks"));
 				sb.append(",workshop:" + dataList.get(0).get("workshop"));
 				sb.append(",use_workshop:" + dataList.get(0).get("use_workshop"));
 				sb.append(",order_no:" + dataList.get(0).get("order_no") + "}");

 				picturePath = QRCodeUtil.encode(sb.toString(), DateUtils.format(new Date(),"yyyyMMddHHmmss"), "", baseDir, true);
 				picturePath = picturePath.replaceAll("\\\\", "//");
 				variables.put("barCode", "file:" + picturePath);
 			} catch (Exception e) {
 				e.printStackTrace();
 			}

 			variables.put("itemList", itemList);
 			String basePath = "http://" + request.getServerName();
 			int port = request.getServerPort();
 			String contextPath = request.getContextPath();
 			variables.put("contextPath", basePath + ":" + port + contextPath);

 			listVars.add(variables);
 		}

 		PdfUtils.preview(configurer, "zzjmes/print/workshopSupplyDoc.html", listVars, response);
 	}
   
}