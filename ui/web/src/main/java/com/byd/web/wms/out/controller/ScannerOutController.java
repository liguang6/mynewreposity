package com.byd.web.wms.out.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;

/**
 * 扫描枪出库
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/scanner")
public class ScannerOutController {
	@Autowired
	WmsOutReqServiceRemote outReqServiceRemote;
	
	/**
	 * 根据标签号，查询标签信息
	 * @param params
	 * @return
	 */
	@RequestMapping("/queryScanner")
	public R queryScannerOut(@RequestBody Map<String,Object> params){	
		return outReqServiceRemote.queryScannerOut(params);
	}
	
	//校验需求号
	@RequestMapping("/validReqNo")
	public R validReqNo(@RequestBody Map<String,Object> params){
		return outReqServiceRemote.validReqNo(params);
	}
	
	//业务类型
	@RequestMapping("/queryBusinessName")
	public R queryBusinessName(@RequestBody Map<String,Object> params){
		return outReqServiceRemote.queryBusinessName(params);
	}
	
	/**
	 * 下架
	 * @return
	 */
	@RequestMapping("/obtained")
	public R obtained(@RequestBody List<Map<String,Object>> params){
		params.forEach(m->m.put("USERNAME", ""));
		return outReqServiceRemote.obtained(params);
	}
	
	/**
	 * 取消下架
	 * @return 成功 / 失败
	 */
	@RequestMapping("/cancelObtained")
	public R cancelObtained(@RequestBody List<Map<String,Object>> params){
		params.forEach(m->m.put("USERNAME", ""));
		return outReqServiceRemote.cancelObtained(params);
	}
	
	/**
	 * 交接确认
	 * @return
	 */
	@RequestMapping("/handoverComfirm")
	public R handoverComfirm(@RequestBody List<Map<String,Object>> params){
		params.forEach(m->m.put("USERNAME", ""));
		return outReqServiceRemote.handoverComfirm(params);
	}

	/**
	 * WEB页面查询条码缓存表数据
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryBarcodeCache")
	@CrossOrigin
	public R queryBarcodeCache(@RequestBody Map<String,Object> data){
		List<Map<String,Object>> list =  outReqServiceRemote.queryBarcodeCache(data);
		return R.ok().put("data", list);
	}
	
}
