package com.byd.web.zzjmes.produce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.ConfigConstant;
import com.byd.utils.DateUtils;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.zzjmes.produce.service.OutsourcingRemote;

/**
 * 委外发货
 * @author cscc thw
 * @email 
 * @date 2019-09-03 16:16:08
 */
@RestController
@RequestMapping("zzjmes/outsourcing")
public class OutsourcingController {
    @Autowired
    private OutsourcingRemote outsourcingRemote;
	@Autowired
	private FreeMarkerConfigurer configurer;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	private ConfigConstant configConstant;
    
    /**
   	 * 根据工厂、车间、线别、订单编号、批次编号、零部件号、工序获取下料明细信息及上工序产量信息
   	 * @param condMap 
   	 * 	"werks":"workshop": "line": "order_no": "zzj_plan_batch": 
   		"zzj_no": "process_name" 工序编号  machine_plan_items_id
   	 * @return
   	 */
    @RequestMapping("/getOutsourcingMatInfo")
 	public R getOutsourcingMatInfo(@RequestParam Map<String,Object> params){
 		return outsourcingRemote.getOutsourcingMatInfo(params);
 	}
    
    /**
     * 委外发货保存
     * @param params
     * @return
     */
    @RequestMapping("/saveOutsourcing")
  	public R saveOutsourcing(@RequestParam Map<String,Object> params){
  		return outsourcingRemote.saveOutsourcing(params);
  	}
    
	/**
	 * 委外清单打印预览
	 * 
	 * @param params
	 */
	@RequestMapping(value = "/outsourcingDocPreview")
	public void inspectionLabelPreview(@RequestParam Map<String, Object> params) {
		String headInfo_str = params.get("headInfo") == null ? "" : params.get("headInfo").toString();
		String arr_str = params.get("matList") == null ? "" : params.get("matList").toString();
		String baseDir = configConstant.getLocation() + "/barcode/";
		
		Map<String, Object> variables = new HashMap<>();// 模板数据封装
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf文件数
		List<Map<String, Object>> listVars = new ArrayList<>();
		
		Map headInfo = new HashMap<>();
		if(headInfo_str.length()>0) {
			headInfo = JSONObject.parseObject(headInfo_str);
			
			variables.put("vendor", headInfo.get("vendor"));
			variables.put("werks", headInfo.get("werks"));
			variables.put("werks_name", headInfo.get("werks_name"));
			variables.put("workshop", headInfo.get("workshop"));
			variables.put("workshop_name", headInfo.get("workshop_name"));
			variables.put("line", headInfo.get("line"));
			variables.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			variables.put("editor", headInfo.get("creator")==null?headInfo.get("editor"):headInfo.get("creator"));
		}
		
	
		List<Map> dataList = null;
		if (arr_str.length() > 0) {
			List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
			try {
				dataList = JSONObject.parseArray(arr_str, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String outsourcing_no = dataList.get(0).get("outsourcing_no")==null?"":dataList.get(0).get("outsourcing_no").toString();
			List<String> zzj_no_list = new ArrayList<>();
			for (int i = 0; i < dataList.size(); i++) {
				Map<String, Object> m = (Map<String, Object>) dataList.get(i);
				m.put("INDEX", i + 1);
				itemList.add(m);
				if(!zzj_no_list.contains(m.get("zzj_no").toString())) {
					zzj_no_list.add(m.get("zzj_no").toString());
				}
			}
			// 生成二维码
			try {
				String picturePath = ""; // 图片路径
				StringBuffer sb = new StringBuffer();
				sb.append("{\"outsourcing_no\":\"" + outsourcing_no+"\"");
				sb.append(",\"werks':\"" + headInfo.get("werks"));
				sb.append("\",\"workshop\":'" + headInfo.get("workshop"));
				sb.append("\",\"line\":'" + headInfo.get("line"));
		/*		StringBuffer zzj_no_sb = new StringBuffer();
				for (String s : zzj_no_list) {
					zzj_no_sb.append("{zzj_no:" + s + "}");
				}
				sb.append(",zzj_no_list:").append(zzj_no_sb.toString());*/
				
				sb.append("\"}");

				picturePath = QRCodeUtil.encode(sb.toString(), outsourcing_no, "", baseDir, true);
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

		PdfUtils.preview(configurer, "zzjmes/print/outsourcingDoc.html", listVars, response);
	}
    
    
}