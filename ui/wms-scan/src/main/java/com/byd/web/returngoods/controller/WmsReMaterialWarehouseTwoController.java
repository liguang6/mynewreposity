package com.byd.web.returngoods.controller;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.returngoods.service.WmsReMaterialWarehouseRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author liguang6
 * @version 2019年1月2日14:28:52
 * 立库余料退回
 */
@RestController
@RequestMapping("returngoods/wmsReMaterialTwoWarehouse")
public class WmsReMaterialWarehouseTwoController {

	@Autowired
	protected HttpSession session;
	@Autowired
	private WmsReMaterialWarehouseRemote wmsReMaterialWarehouseRemote;
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
			params.put("WH_NUMBER", session.getAttribute("warehouse"));
			params.put("WERKS", session.getAttribute("werks"));
			params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
			params.put("MENU_KEY", "PDA_RW_262");
			params.put("BUSINESS_NAME","35");
			params.put("BUSINESS_TYPE","10");
			params.put("BUSINESS_CLASS","06");
			params.put("BUSINESS_CODE","RT_301");
			params.put("SAP_MOVE_TYPE","261");
		}catch (Exception e){
			throw new RuntimeException("SCAN初始化请求参数出错!");
		}

		return params;
	}

	@RequestMapping("/getPorecCache")
	public R getPorecCache(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsReMaterialWarehouseRemote.getPorecCache(params);
	}

	@RequestMapping("/validatePoReceiptLable")
	public R validatePoReceiptLable(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsReMaterialWarehouseRemote.validatePoReceiptLable(params);
	}

	@RequestMapping("/getPoDetailByBarcode")
	public R getPoDetailByBarcode(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsReMaterialWarehouseRemote.getPoDetailByBarcode(params);
	}

	/**
	 * 获取采购订单明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getGridPoreData")
	public R getGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsReMaterialWarehouseRemote.getGridPoreData(params);
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
		return wmsReMaterialWarehouseRemote.getBarGridPoreData(params);
	}

	@RequestMapping("/deleteLabel")
	public R poReDeleteBarInfo(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsReMaterialWarehouseRemote.poReDeleteBarInfo(params);
	}


	@RequestMapping("/poReceiptPdaIn")
	public R poReceiptPdaIn(@RequestParam Map<String, Object> params){
		this.initParams(params);
//		Map<String,Object> currentUser = userUtils.getUser();
//		params.put("USERNAME", currentUser.get("USERNAME"));
//		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsReMaterialWarehouseRemote.poReceiptPdaIn(params);
	}

	@RequestMapping("/createheadText")
	public R createheadText(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsReMaterialWarehouseRemote.createheadText(params);
	}



}
