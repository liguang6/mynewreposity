package com.byd.wms.business.modules.account.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.out.service.WmsOutHandoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * STO交货单收货--PDA
 * @author chu.fuxiang
 */
@RestController
@RequestMapping("acPda/task")
public class WmsPostAccountPdaController {

	@Autowired
	private WmsOutHandoverService wmsOutHandoverService;
	@Autowired
	private RedisUtils redisUtils;

	/**
	 *
	 */
	@RequestMapping("/getwhTask")
	public R getwhTask(@RequestBody Map<String,Object> params) {

		PageUtils page = wmsOutHandoverService.list(params);
		return R.ok().put("page", page);
	}

	/**
	 *
	 */
	@RequestMapping("/posttingAc")
	public R posttingAc(@RequestBody Map<String,Object> params) {

		R r = R.ok();

		String requirementNo = params.get("REQUIREMENT_NO").toString();
		String uid = UUID.randomUUID().toString();
		if (redisUtils.tryLock(requirementNo, uid)){
			params.put("UID", uid);
			Map<String, Object> retMap = wmsOutHandoverService.handover(params);
			redisUtils.unlock(requirementNo, uid);
			r.putAll(retMap);
		} else {
			return R.error(500,requirementNo+",正在执行中，请稍后再试！");
		}

		return r;
	}



}
