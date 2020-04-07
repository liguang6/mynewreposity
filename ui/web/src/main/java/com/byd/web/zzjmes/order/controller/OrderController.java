package com.byd.web.zzjmes.order.controller;

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
import com.byd.web.zzjmes.order.service.OrderRemote;
/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
@RestController
@RequestMapping("zzjmes/order")
public class OrderController {
	@Autowired
	OrderRemote orderRemote;
	
	@RequestMapping("/getOrderList")
    public R getOrderList(@RequestParam Map<String, Object> paramMap){
		return orderRemote.getOrderList(paramMap);
	}
	@RequestMapping("/addOrder")
    public R addOrder(@RequestParam Map<String, Object> paramMap){
		return orderRemote.addOrder(paramMap);
	}
	@RequestMapping("/editOrder")
    public R editOrder(@RequestParam Map<String, Object> paramMap){
		return orderRemote.editOrder(paramMap);
	}
	@RequestMapping("/delOrder")
    public R delOrder(@RequestParam Map<String, Object> paramMap){
		return orderRemote.delOrder(paramMap);
	}
	@SuppressWarnings("unchecked")
	@RequestMapping("/exportOrder")
	public ResponseEntity<byte[]> exportOrder(@RequestParam Map<String, Object> params){
		HttpHeaders header = new HttpHeaders();
		String filename = "order.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("werks_name-生产工厂");
		fieldTitleList.add("order_no-订单编号");
		fieldTitleList.add("order_desc-订单描述");
		fieldTitleList.add("order_area_name-订单区域");
		fieldTitleList.add("sale_dept_name-销售部");
		fieldTitleList.add("productive_year-生产年份");
		fieldTitleList.add("delivery_date-订单交期");
		fieldTitleList.add("order_type_name-订单类型");
		fieldTitleList.add("creator-创建人");
		fieldTitleList.add("creat_date-创建时间");
		
		List<Map<String,Object>> entityList = (List<Map<String, Object>>) ((Map<String,Object>)orderRemote.getOrderList(params).get("page")).get("list");
		
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
