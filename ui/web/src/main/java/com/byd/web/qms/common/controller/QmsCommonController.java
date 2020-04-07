package com.byd.web.qms.common.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.ConfigConstant;
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelWriter;
import com.byd.utils.FileUtils;
import com.byd.utils.HttpContextUtils;
import com.byd.utils.ImageUtils;
import com.byd.utils.R;
import com.byd.web.qms.common.service.QmsCommonRemote;

/**
 * 通用Controller
 *
 * @author tangj 
 * @since 2018-08-02
 */
@RestController
@RequestMapping("qms/common")
public class QmsCommonController {
	
	private static Logger logger = LoggerFactory.getLogger(QmsCommonController.class);
    @Autowired
    private QmsCommonRemote qmsCommonRemote;
    @Autowired
	private ConfigConstant configConstant;
    
    /**
     * qms 车型模糊查找
     * @return
     */
    @RequestMapping("/getBusTypeCodeList")
    public R getBusTypeCodeList(@RequestParam String busTypeCode) {
    	return qmsCommonRemote.getBusTypeCodeList(busTypeCode);
    }
    
    @RequestMapping("/getTestNodes")
    public R getTestNodes(@RequestParam String testType,@RequestParam String TEST_CLASS) {
    	return qmsCommonRemote.getTestNodes(testType,TEST_CLASS);
    }

    /**
     * qms 订单模糊查找
     * @return
     */
    @RequestMapping("/getOrderNoList")
    public R getOrderNoList(@RequestParam String orderNo) {
    	return qmsCommonRemote.getOrderNoList(orderNo);
    }
    
    /**
     * qms 订单模糊查找
     * @return
     */
    @RequestMapping("/getBusList")
    public R getBusList(@RequestParam Map<String,Object> condMap) {
    	return qmsCommonRemote.getBusList(condMap);
    }

    @RequestMapping("/uploadFile")
    public R uploadFile(@RequestParam MultipartFile file,@RequestParam(value="file_name") String file_name) {
    	try {
    		Base64 base64 = new Base64();
    		//String file_name=file.getOriginalFilename();
			//String dest_path=ResourceUtils.getURL("classpath:").getPath();
    		String file_path=configConstant.getLocation()+"/qms/";
    		/*File f=new File(ResourceUtils.getURL("classpath:").getPath());
    		System.out.println(f.getParentFile().getParentFile().getParent());*/    		
    		//ServletContext servletContext = HttpContextUtils.getHttpServletRequest().getServletContext();
    		file_name=base64.encodeToString(file_name.getBytes("UTF-8"))+".jpg";
    		String imgFilePath = HttpContextUtils.getHttpServletRequest().getContextPath()+ "/upload/qms/" +file_name;
			file_path=file_path+file_name;
			System.out.println(imgFilePath);
			
			ImageUtils.resizeFix(file, file_path, 800, 800);
			return R.ok().put("img_url", imgFilePath);
		} catch (FileNotFoundException e) {
			System.out.println("路径不存在");
			e.printStackTrace();
			return R.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}

    }
    
    /**
     * qms 检具模糊查找
     * @return
     */
    @RequestMapping("/getTestTools")
    public R getTestTools(@RequestParam Map<String,Object> condMap) {
    	return qmsCommonRemote.getTestTools(condMap);
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping("/exportQmsTestRecord.task")
    public void exportQmsTestRecord(@RequestParam String params) {
    	String[] param_list = params.split(";");
    	String WERKS ="";
    	String TEST_TYPE = "";
    	for(String param : param_list) {
    		if(param.split("=")[0].equals("WERKS")) {
    			WERKS=param.split("=")[1];
    		}
    		if(param.split("=")[0].equals("TEST_TYPE")) {
    			TEST_TYPE=param.split("=")[1];
    		}
    	}
    	
    	//获取上个月日期字符串
    	String last_month = DateUtils.format(DateUtils.addDateMonths(new Date(), -1), "yyyy-MM");
    	
    	Map<String,Object> condMap = new HashMap<String,Object>();
    	condMap.put("WERKS", WERKS);
    	condMap.put("TEST_TYPE", TEST_TYPE);
    	condMap.put("STR_MONTH", last_month);
    	
    	R r =qmsCommonRemote.getQmsTestRecords(condMap);
    	List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>(); 
    	if(r.get("dataList")!=null) {
    		dataList=(List<Map<String, Object>>) r.get("dataList");
    	}
    	List<String> fieldList = new ArrayList<String>();
    	fieldList.add("WERKS-工厂");
    	fieldList.add("ORDER_NO-订单编号");
    	fieldList.add("ORDER_TEXT-订单描述");
    	fieldList.add("BUS_NO-车号");
    	fieldList.add("VIN-VIN号");
    	fieldList.add("CAB_NO-驾驶室编号");
    	fieldList.add("CUSTOM_NO-自编号");
    	fieldList.add("TEST_TYPE-车辆类型");
    	fieldList.add("TEST_NODE-检验节点");
    	fieldList.add("TEST_GROUP-检验分组");
    	fieldList.add("TEST_TOOL_NO-检具编号");
    	fieldList.add("PROCESS_NAME-工序名称");
    	fieldList.add("TEST_ITEM-检验项目");
    	fieldList.add("TEST_STANDARD-标准要求");
    	fieldList.add("TEST_RESULT-检验结果");
    	fieldList.add("JUDGE-判定");
    	fieldList.add("RE_TEST_RESULT-复检结果");
    	fieldList.add("RE_JUDGE-复判");
    	fieldList.add("BAD_CLASS-不良类别");
    	fieldList.add("RESP_UNIT-责任单位");
    	fieldList.add("RESP_WORKGROUP-责任班组");
    	fieldList.add("ONE_PASSED_FLAG-一次交检");
    	fieldList.add("CONFIRMOR-确认人");
    	fieldList.add("CONFIRM_DATE-确认时间");
    	fieldList.add("CREATOR-创建人");
    	fieldList.add("CREATE_DATE-创建时间");
    	fieldList.add("EDITOR-编辑人");
    	fieldList.add("EDIT_DATE-编辑时间");
    	
    	StringBuffer descFileName=new StringBuffer(configConstant.getLocation());
    	descFileName.append("/qms/testRecord/");
    	descFileName.append(WERKS);
    	if((TEST_TYPE.equals("01"))) {
    		descFileName.append("大巴");
    	}
    	if((TEST_TYPE.equals("02"))) {
    		descFileName.append("专用车");
    	}
    	descFileName.append(last_month);
    	descFileName.append(".xlsx");
    	logger.info("---->品质检验数据导出开始：");
    	logger.info("---->生成文件："+descFileName.toString());
    	
    	 if(descFileName!=null){
	            File destFile = new File(descFileName.toString());
	    		// 判断目标文件是否存在
	    		if (destFile.exists()) {
	    			// 如果目标文件存在，并且允许覆盖
	    			if (!FileUtils.delFile(descFileName.toString())) {
	    				logger.info("删除目标文件 " + descFileName + " 失败!");
	    			}
	    		}
	    		if (!destFile.getParentFile().exists()) {
 				// 如果目标文件所在的目录不存在，则创建目录
 				logger.debug("目标文件所在的目录不存在，创建目录!");
 				// 创建目标文件所在的目录
 				if (!destFile.getParentFile().mkdirs()) {
 					logger.info("创建目标文件所在的目录失败!");
 				}
 			}
	    		
        	try {
        		FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流
    	    	ExcelWriter.writeRecordToFile(out, dataList, fieldList);
    	    	out.close();
    	    	out.flush();
        	}catch(Exception e) {
        		logger.info(e.getMessage());
        	}
	    	
    	
    	 }
    }
    
    
}