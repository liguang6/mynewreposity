package com.byd.wms.business.modules.pda.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.wms.business.modules.pda.service.JITService;

@RestController
@RequestMapping("/cswlms")
public class JITController {
	@Autowired
	private JITService jITService;

	@CrossOrigin
	@RequestMapping("/JITScanLabel")
	public R JITScanLabel(@RequestBody Map<String, Object> params) {
		return R.ok().put("data", jITService.JITScanLabel(params));
	}

	@CrossOrigin
	@RequestMapping("/JITScanDeliveryNo")
	public R JITScanDeliveryNo(@RequestBody Map<String, Object> params) {
		return R.ok().put("data", jITService.JITScanDeliveryNo(params));
	}

	@CrossOrigin
	@RequestMapping("/JITPick")
	public R JITPick(@RequestBody Map<String, Object> params) {
		jITService.JITPick(params);
		return R.ok();
	}
}