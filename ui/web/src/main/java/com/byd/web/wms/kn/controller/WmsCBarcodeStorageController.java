package com.byd.web.wms.kn.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.byd.utils.ConfigConstant;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.kn.service.WmsCBarcodeStorageRemote;

/**
 * 
 * 储位标签打印
 *
 */
@RestController
@RequestMapping("kn/barcodeStorage")
public class WmsCBarcodeStorageController {
	@Autowired
	private WmsCBarcodeStorageRemote WmsCBarcodeStorageRemote;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private FreeMarkerConfigurer configurer;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	private ConfigConstant configConstant;

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		return WmsCBarcodeStorageRemote.list(params);
	}

	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException {
		List<String[]> sheet = ExcelReader.readExcel(excel);
		List<Map<String, Object>> entityList = new ArrayList<Map<String, Object>>();
		int index = 0;
		if (sheet != null && sheet.size() > 0) {
			for (String[] row : sheet) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("rowNo", ++index);
				map.put("WH_NUMBER", row[0]);
				map.put("BIN_CODE", row[1]);
				map.put("BIN_NAME", row[2]);
				entityList.add(map);
			}
		}
		return R.ok().put("data", entityList);
	}

	@RequestMapping("/import")
	public R importExel(@RequestBody List<Map<String, Object>> params) {
		return WmsCBarcodeStorageRemote.importExel(params);
	}

}
