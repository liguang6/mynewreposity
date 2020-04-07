package com.byd.web.in.controller;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.in.service.WmsPoReceiptPdaRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/** 
 * @author rain
 * @version 2019年12月4日20:28:52
 * PDA采购订单收货
 */
@RestController
@RequestMapping("inpda/wmsporeceiptpda")
public class WmsPoReceiptPdaController {

	@Autowired
	protected HttpSession httpSession;
	@Autowired
	private WmsPoReceiptPdaRemote wmsPoReceiptPdaRemote;
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
			params.put("MENU_KEY", "PDA_GR_02");
		}catch (Exception e){
			throw new RuntimeException("SCAN初始化请求参数出错!");
		}

		return params;
	}

	@RequestMapping("/getPorecCache")
	public R getPorecCache(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsPoReceiptPdaRemote.getPorecCache(params);
	}

	@RequestMapping("/validatePoReceiptLable")
	public R validatePoReceiptLable(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsPoReceiptPdaRemote.validatePoReceiptLable(params);
	}

	@RequestMapping("/getPoDetailByBarcode")
	public R getPoDetailByBarcode(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsPoReceiptPdaRemote.getPoDetailByBarcode(params);
	}

	/**
	 * 获取采购订单明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getGridPoreData")
	public R getGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsPoReceiptPdaRemote.getGridPoreData(params);
	}
//	@RequestMapping("/getGridPoreData")
//	public R getGridPoreData(@RequestParam Map<String,Object> params) {
//		params.put("username", userUtils.getUser().get("USERNAME"));
//		List<Map<String,Object>> whlist = (List<Map<String,Object>>)wmsPoReceiptPdaRemote.getGridPoreData(params);
//		Page page=new Query<Map<String,Object>>(params).getPage();
//		page.setRecords(whlist);
//		page.setTotal(whlist.size());
//		page.setSize(5);
//		page.setCurrent(1);
//		PageUtils pages =new PageUtils(page);
//		return R.ok().put("page", pages);
//	}
	/**
	 * 获取采购订单条码明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getBarGridPoreData")
	public R getBarGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsPoReceiptPdaRemote.getBarGridPoreData(params);
	}

	@RequestMapping("/poReDeleteBarInfo")
	public R poReDeleteBarInfo(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsPoReceiptPdaRemote.poReDeleteBarInfo(params);
	}


	@RequestMapping("/poReceiptPdaIn")
	public R poReceiptPdaIn(@RequestParam Map<String, Object> params){
		this.initParams(params);
//		Map<String,Object> currentUser = userUtils.getUser();
//		params.put("USERNAME", currentUser.get("USERNAME"));
//		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsPoReceiptPdaRemote.poReceiptPdaIn(params);
	}

	@RequestMapping("/createheadText")
	public R createheadText(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsPoReceiptPdaRemote.createheadText(params);
	}



}
