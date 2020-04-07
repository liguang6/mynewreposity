package com.byd.web.wms.account.controller;

import java.util.List;
import java.util.Map;

import com.byd.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.web.wms.account.service.WmsVoucherCancelRemote;

@RestController
@RequestMapping("account/wmsVoucherCancel")
public class WmsVoucherCancelController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private WmsVoucherCancelRemote wmsVoucherCancelRemote;
    @Autowired
    private RedisUtils redisUtils;
	@Autowired
	private UserUtils userUtils;
	
	@RequestMapping("/getVoucherCancelData")
	public R getVoucherCancelData(@RequestParam Map<String, Object> params) {
		logger.info("-->getVoucherCancelData");
		//Map<String,Object> currentUser = redisUtils.getMap("currentUser");
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
    	List<Map<String,Object>> deptMapList = redisUtils.getList("authWhList");
    	params.put("deptList", deptMapList);
	   	return wmsVoucherCancelRemote.getVoucherCancelData(params);
	}
	
	@RequestMapping("/confirmVoucherCancelData")
	public R confirmVoucherCancelData(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmVoucherCancelData:BUSINESS_CLASS=" + params.get("BUSINESS_CLASS").toString());
		try {
			Map<String,Object> currentUser = userUtils.getUser();
		   	params.put("USERNAME", currentUser.get("USERNAME"));
		   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		   	return wmsVoucherCancelRemote.confirmVoucherCancelData(params);
		}catch(Exception e) {
			e.printStackTrace();
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
		
	}
	
}
