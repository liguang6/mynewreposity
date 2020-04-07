package com.byd.web.wms.account.controller;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.wms.account.service.WmsOutReversalRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("account/wmsOutReversal")
public class WmsOutReversalController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private WmsOutReversalRemote wmsOutReversalRemote;
    @Autowired
    private RedisUtils redisUtils;
	@Autowired
	private UserUtils userUtils;


	/*@RequestMapping("/getOutReversalData")
	public R getVoucherReversalData(@RequestParam Map<String, Object> params) {
		System.out.println(params.toString());
		//Map<String,Object> currentUser = redisUtils.getMap("currentUser");
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	return wmsOutReversalRemote.getOutReversalData(params);
	}*/
	
	@RequestMapping("/confirmOutReversalData")
	public R confirmVoucherReversalData(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmVoucherReversalData:BUSINESS_CLASS=" + params.get("BUSINESS_CLASS").toString());
		try {
			Map<String,Object> currentUser = userUtils.getUser();
		   	params.put("USERNAME", currentUser.get("USERNAME"));
		   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		   	return wmsOutReversalRemote.confirmOutReversalData(params);
		}catch(Exception e) {
			e.printStackTrace();
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
	}

	@RequestMapping("/confirmOutCancelData")
	public R confirmOutCancel(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmOutCancelData:BUSINESS_CLASS=" + params.get("BUSINESS_CLASS").toString());
		try {
			Map<String,Object> currentUser = userUtils.getUser();
		   	params.put("USERNAME", currentUser.get("USERNAME"));
		   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
			return wmsOutReversalRemote.confirmOutCancelData(params);
		}catch(Exception e) {
			e.printStackTrace();
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
	}

}
