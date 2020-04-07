package com.byd.web.in.controller;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.in.service.WmsMoReceiptPdaRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/** 
 * @author rain
 * @version 2019年12月22日11:22:24
 * PDA生产订单101收货
 */
@RestController
@RequestMapping("inpda/wmsmoreceiptpda")
public class WmsMoReceiptPdaController {

	@Autowired
	protected HttpSession httpSession;
	@Autowired
	private WmsMoReceiptPdaRemote wmsMoReceiptPdaRemote;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserUtils userUtils;

	/**
	 * 初始化请求参数
	 * @param params
	 * @return
	 */
	public Map<String,Object> initParams(Map<String,Object> params){
		try {
			//选择仓库号存储在session里面的值
			params.put("WERKS", httpSession.getAttribute("werks").toString());
			params.put("WH_NUMBER", httpSession.getAttribute("warehouse").toString());
			params.put("MENU_KEY", "PDA_GR_03");
		}catch (Exception e){
			throw new RuntimeException("SCAN初始化请求参数出错!");
		}

		return params;
	}

	@RequestMapping("/getMorecCache")
	public R getMorecCache(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsMoReceiptPdaRemote.getMorecCache(params);
	}

	@RequestMapping("/validatePoReceiptLable")
	public R validatePoReceiptLable(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsMoReceiptPdaRemote.validatePoReceiptLable(params);
	}

	@RequestMapping("/getMoDetailByBarcode")
	public R getMoDetailByBarcode(@RequestParam Map<String, Object> params){
		this.initParams(params);
		R r= wmsMoReceiptPdaRemote.getMoDetailByBarcode(params);
		return r;
	}

	/**
	 * 获取生产订单明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getGridPoreData")
	public R getGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsMoReceiptPdaRemote.getGridPoreData(params);
	}

	/**
	 * 获取生产订单条码明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getBarGridPoreData")
	public R getBarGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsMoReceiptPdaRemote.getBarGridPoreData(params);
	}

	@RequestMapping("/poReDeleteBarInfo")
	public R poReDeleteBarInfo(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsMoReceiptPdaRemote.poReDeleteBarInfo(params);
	}


	@RequestMapping("/moReceiptPdaIn")
	public R moReceiptPdaIn(@RequestParam Map<String, Object> params){
		this.initParams(params);
//		Map<String,Object> currentUser = userUtils.getUser();
//		params.put("USERNAME", currentUser.get("USERNAME"));
//		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsMoReceiptPdaRemote.moReceiptPdaIn(params);
	}

	@RequestMapping("/createheadText")
	public R createheadText(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsMoReceiptPdaRemote.createheadText(params);
	}



}
