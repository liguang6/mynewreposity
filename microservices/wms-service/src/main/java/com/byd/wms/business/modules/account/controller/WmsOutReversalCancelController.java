package com.byd.wms.business.modules.account.controller;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.account.service.WmsReturnGoodsOutReversalCancelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;


/**
 * 出库凭证冲销,取消
 * @author qiu.jiaming1
 *
 */
@RestController
@RequestMapping("account/wmsOutReversal")
public class WmsOutReversalCancelController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private WmsReturnGoodsOutReversalCancelService wmsReturnGoodsOutReversalService;
	@Autowired
    private RedisUtils redisUtils;
	
	@RequestMapping("/confirmOutReversalData")
	public R confirmVoucherReversalData(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmOutReversalData:BUSINESS_CLASS=" + params.get("BUSINESS_CLASS").toString());
		String result = "";
		try {
			String vo_no = params.get("VO_NO").toString();
			String uid = UUID.randomUUID().toString();
    		if (redisUtils.tryLock(vo_no, uid))
    		{
				//调用过账接口
				result=wmsReturnGoodsOutReversalService.confirmOutReversal(params);
				redisUtils.unlock(vo_no, uid);
    		} else {
    			return R.error(vo_no+",正在执行中，请稍后再试！");	
    		}

		}catch(Exception e) {
			e.printStackTrace();
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
		return R.ok().put("result", result);
	}

	@RequestMapping("/confirmOutCancelData")
	public R confirmOutCancel(@RequestParam Map<String, Object> params) {
		logger.info("-->confirmOutCancelData:BUSINESS_CLASS=" + params.get("BUSINESS_CLASS").toString());
		String result = "";
		try {
			String vo_no = params.get("VO_NO").toString();
			String uid = UUID.randomUUID().toString();
    		if (redisUtils.tryLock(vo_no, uid))
    		{

				//调用过账接口
				result=wmsReturnGoodsOutReversalService.confirmOutCancel(params);
				redisUtils.unlock(vo_no, uid);
    		} else {
    			return R.error(vo_no+",正在执行中，请稍后再试！");	
    		}

		}catch(Exception e) {
			e.printStackTrace();
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
		return R.ok().put("result", result);
	}
}
