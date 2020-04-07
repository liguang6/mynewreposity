package com.byd.web.wms.account.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.wms.account.service.WmsVoucherReversalRemote;

@RestController
@RequestMapping("account/wmsVoucherReversal")
public class WmsVoucherReversalController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private WmsVoucherReversalRemote wmsVoucherReversalRemote;
    @Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/getVoucherReversalData")
	public R getVoucherReversalData(@RequestParam Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	return wmsVoucherReversalRemote.getVoucherReversalData(params);
	}
	
	@RequestMapping("/confirmVoucherReversalData")
	public R confirmVoucherReversalData(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmVoucherReversalData:BUSINESS_CLASS=" + params.get("BUSINESS_CLASS").toString());
		try {
			Map<String,Object> currentUser = userUtils.getUser();
		   	params.put("USERNAME", currentUser.get("USERNAME"));
		   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		   	return wmsVoucherReversalRemote.confirmVoucherReversalData(params);
		}catch(Exception e) {
			e.printStackTrace();
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
	}
}
