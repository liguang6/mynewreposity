package com.byd.web.zzjmes.config.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.ConfigConstant;
import com.byd.utils.PdfUtils;
import com.byd.utils.R;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.zzjmes.config.service.MachineAssignRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月11日 下午3:04:13 
 * 类说明 
 */
@RestController
@RequestMapping("zzjmes/machineAssign")
public class MachineAssignController {
	@Autowired
	MachineAssignRemote machineAssignRemote;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	private ConfigConstant configConstant;
	@Autowired
	private FreeMarkerConfigurer configurer;
	
	@RequestMapping("/getMachineAssignList")
    public R getMachineAssignList(@RequestParam Map<String, Object> paramMap){
		return machineAssignRemote.getMachineAssignList(paramMap);
	}
	
	@RequestMapping("/save")
    public R insertMachineAssign(@RequestParam Map<String, Object> paramMap){
		return machineAssignRemote.insertMachineAssign(paramMap);
	}
	
	@RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return machineAssignRemote.info(id);
    }
	
	@RequestMapping("/update")
    public R updateMachineAssign(@RequestParam Map<String, Object> paramMap){
		return machineAssignRemote.updateMachineAssign(paramMap);
	}
	
	/**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	Map<String, Object> params=new HashMap<String, Object>();
    	params.put("id", id);
		return machineAssignRemote.delMachineAssign(params);
    }
    
    /**
	 * 机台标签打印
	 *
	 */
	@RequestMapping(value = "/machinePreview")
	public void machinePreview(@RequestParam Map<String, Object> params) {
		String basePath = "http://" + request.getServerName();
		int port = request.getServerPort();
		String contextPath = basePath + ":" + port + request.getContextPath();
		params.put("contextPath", contextPath);
		// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
		List<Map<String, Object>> listVars = new ArrayList<>();
		
		String labelListStr = params.get("labelList")==null?"":params.get("labelList").toString();
		if (!StringUtils.isEmpty(labelListStr)) {
			String baseDir = configConstant.getLocation() + "/barcode/";
			
			List<Map> mapList = JSONObject.parseArray(labelListStr, Map.class);
			for (Map m : mapList) {
				Map<String, Object> variables = new HashMap<>();
				variables.put("WERKS", m.get("werks_name"));
				variables.put("WORKSHOP", m.get("workshop_name"));
				variables.put("WORKGROUP", m.get("workgroup_name"));
				variables.put("MACHINE_CODE", m.get("machine_code"));
				variables.put("MACHINE_NAME", m.get("machine_name"));
				
				// 生成二维码
				try {
					String picturePath = ""; // 图片路径
					StringBuffer sb = new StringBuffer();
					
					String machine_code = m.get("machine_code") == null ? "" : m.get("machine_code").toString();
					sb.append(";m:" + machine_code + ";");

					picturePath = QRCodeUtil.encode(sb.toString(), m.get("machine_code").toString(), "", baseDir, true);
					picturePath = picturePath.replaceAll("\\\\", "//");
					variables.put("barCode", "file:" + picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				variables.put("contextPath", params.get("contextPath").toString());
				listVars.add(variables);
			}
		}
		
		PdfUtils.preview(configurer, "zzjmes/print/machineTmp_Label.html", listVars, response);
	}
}
