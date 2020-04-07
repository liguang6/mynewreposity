package com.byd.web.bjmes.product.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
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
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelWriter;
import com.byd.utils.FileUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.bjmes.product.service.ProductHandoverRemote;

/**
 * 车间内交接
 * @author cscc tangj
 * @email 
 * @date 2019-10-23 16:16:08
 */
@RestController
@RequestMapping("bjmes/productHandover")
public class ProductHandoverController {
    @Autowired
	private ProductHandoverRemote productHandoverRemote;
	@Autowired
    private UserUtils userUtils;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	//@Autowired
	//private ConfigConstant configConstant;
//	@Autowired
//	private FreeMarkerConfigurer configurer;
    /**查询分页**/
    @RequestMapping("/queryPage")
    public R queryPage(@RequestParam Map<String, Object> params){
        return productHandoverRemote.queryPage(params);
    }
   
    /**
     * 交接保存
     * @param params
     * @return
     */
    @RequestMapping("/save")
  	public R save(@RequestParam Map<String,Object> params){
		params.put("editor",userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	params.put("edit_date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
  		return productHandoverRemote.save(params);
	}
	// 获取生产编号信息
	@RequestMapping("/getProductInfo")
	public R getProductInfo(@RequestParam Map<String, Object> params){
		return productHandoverRemote.getProductInfo(params);
	}
    
    // 导出查询的数据
 	@RequestMapping("/expData")
     public ResponseEntity<byte[]> expData(@RequestParam Map<String, Object> params) throws Exception{
 		
 		HttpHeaders header = new HttpHeaders();
 		String filename = "data.xlsx";
		List<String> fieldTitleList = new ArrayList<String>();
		fieldTitleList.add("product_no-产品编号");
		fieldTitleList.add("product_name-产品名称");
		fieldTitleList.add("order_no-订单");
 		fieldTitleList.add("product_type_name-产品类别");
 		fieldTitleList.add("werks-供应工厂");
 		fieldTitleList.add("workshop_name-供应车间");
 		fieldTitleList.add("receive_workshop_name-接收车间");
 		fieldTitleList.add("deliver_workgroup_name-供应班组");
 		fieldTitleList.add("receive_workgroup_name-接收班组");
 		fieldTitleList.add("deliver_user-交付人");
		fieldTitleList.add("receive_user-接收人");
		fieldTitleList.add("receive_date-交接日期");
 		fieldTitleList.add("creator-维护人");
 		fieldTitleList.add("create_date-维护日期");
 		header.setContentDispositionFormData("attachment", filename);
 		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
 		File tmpExcel = new File(filename);
 		try {
			R r =  productHandoverRemote.queryPage(params);
            Map<String,Object> page= (Map<String,Object>) r.get("page");
			List<Map<String,Object>> list= (List<Map<String, Object>>) page.get("list");
 			tmpExcel.createNewFile();
 			OutputStream outputstream = new FileOutputStream(tmpExcel);
 			ExcelWriter.writeRecordToFile(outputstream,list,fieldTitleList);		
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