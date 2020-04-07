package com.byd.wms.business.modules.returnpda.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialPdaService;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author liguang6
 * @version 2019年1月2日12:28:34
 * 立库余料退回（312）
 */
@RestController
@RequestMapping("retrunpda/wmsReMaterialWarehouse")
public class WmsReMaterialWareHouseController {


	@Autowired
	private WmsReMaterialWarehouseService wmsReMaterialWarehouseService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsInReceiptService wmsInReceiptService;
	@Autowired
	private WmsCTxtService wmsCTxtService;
	@Autowired
	private WmsReMaterialPdaService wmsReMaterialPdaService;


	/**
	 * 初始化请求参数
	 * @param params
	 * @return
	 */
	public Map<String,Object> initParams(Map<String,Object> params){
		try {
			Map<String, Object> currentUser = userUtils.getUser();
			params.put("USERNAME", currentUser.get("USERNAME"));
			params.put("FULL_NAME", currentUser.get("FULL_NAME"));
			String curDate = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			params.put("CREATOR", params.get("USERNAME") + "：" + params.get("FULL_NAME"));
			params.put("CREATE_DATE", curDate);


		}catch (Exception e){
			throw new RuntimeException("初始化请求参数出错!");
		}

		return params;
	}
	/**
	 * PDA SAP采购订单进入查询缓存信息
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getPorecCache")
	public R getPorecCache(@RequestBody Map<String, Object> params){

		try {
			this.initParams(params);
			PageUtils page = wmsReMaterialWarehouseService.getGridPoreDataPage(params);
			R r= wmsReMaterialWarehouseService.getPorecCache(params);
			r.put("page", page);
			return r;

		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
	}
	/**
	 * PDA SAP采购订单校验
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/validatePoReceiptLable")
	public R validatePoReceiptLable(@RequestBody Map<String, Object> params){

		try{
			this.initParams(params);
			return wmsReMaterialWarehouseService.validatePoReceiptLable(params);
		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
	}

	/**
	 * 通过条形码获取采购订单信息(扫描条码或者输入条码回车确定)
	 * 通过条码编号查询相关订单信息并且保存缓存信息
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getPoDetailByBarcode")
	public R getPoDetailByBarcode(@RequestBody Map<String, Object> params){

		try {
			this.initParams(params);
			return wmsReMaterialWarehouseService.getPoDetailByBarcode(params);//查询单个标签

		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
	}

	/**
	 * @description 获取明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getGridPoreData")
	public R getGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		PageUtils page = wmsReMaterialWarehouseService.getGridPoreDataPage(params);
		return R.ok().put("page", page);
	}

	/**
	 * 获取采购订单条码明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getBarGridPoreData")
	public R getBarGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		PageUtils page = wmsReMaterialWarehouseService.getBarGridPoreDataPage(params);
		return R.ok().put("page", page);
	}

	/**
	 * 删除标签条码信息
	 * @param params
	 * @return
	 */
	@RequestMapping("/poReDeleteBarInfo")
	public R poReDeleteBarInfo(@RequestParam Map<String, Object> params){
		this.initParams(params);
		return wmsReMaterialWarehouseService.poReDeleteBarInfo(params);
	}

	/**
	 * PDA SAP采购订单收货确认(输入凭证日期\记账日期和抬头文本)
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/poReceiptPdaIn")
	public R poReceiptPdaIn(@RequestBody Map<String, Object> params){
		Map<String,Object> result = new TreeMap<>();
		List<Map<String,Object>> list = new ArrayList<>();
		list = (List<Map<String, Object>>) JSONArray.parse(params.get("params").toString());
		params.put("list",list);
		String msg = "";
		try {
			result = wmsReMaterialWarehouseService.poReceiptPdaIn(params);
		} catch (Exception e) {
			return R.error(e.getMessage());
		}
		if(result.get("code") =="0"){
			params.putAll(result);
           result = wmsReMaterialPdaService.confirmWorkshopReturned(params);
		}
		Date startDate = DateUtils.stringToDate(result.get("CREAT_DATE").toString(),"yyyy-MM-dd HH:mm:ss");
		Date endDate = new Date();
		//计算执行时间
		String returnFlag = "sec";
		result.put("TIME_SLOT",this.computeTimeSlot(startDate,endDate,returnFlag));
		//删除,过账成功删除这个缓存表对应条码数据
		wmsReMaterialPdaService.removeScanInfo(params);

		return R.ok().put("res",result);

	}

	/**
	 * 保存数据
	 * @param JSON
	 * @param R
	 */

	@RequestMapping("/saveData")
	public R saveData(Map<String,Object> params){
		try {
			wmsReMaterialPdaService.saveData(params);
		}catch (Exception e){
			e.printStackTrace();
			return R.error(e.getMessage());
		}
            return R.ok();
	}
	/**
	 * 计算两个时间段相差的秒数
	 * @param date1
	 * @param date2
	 */
	public long computeTimeSlot(Date startDate, Date endDate,String returnFlag) {
		long lstartDate = startDate.getTime();
		long lendDate = endDate.getTime();
		long diff = (lstartDate < lendDate) ? (lendDate - lstartDate) : (lstartDate - lendDate);
		long day = diff / (24 * 60 * 60 * 1000);
		long hour = diff / (60 * 60 * 1000) - day * 24;
		long min = diff / (60 * 1000) - day * 24 * 60 - hour * 60;
		long sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
		System.out.println("date1 与 date2 相差 " + day + "天" + hour + "小时" + min + "分" + sec + "秒");
		if("day".equals(returnFlag)){
			return day;
		}else if("hour".equals(returnFlag)){
			return hour + day*24;
		}else if("min".equals(returnFlag)){
			return min + day*24*60 + hour*60;
		}else if("sec".equals(returnFlag)){
			return sec + day*24*60*60 + hour*60*60 +min*60;
		}
		return sec;
	}

	/**
	 * 生成抬头文本
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/createheadText")
	public R createheadText(@RequestBody Map<String, Object> params){
		try {
			this.initParams(params);
			//行文本：根据行文本配置规则自动生成，允许修改。
			Map<String, String> txtMap = new HashMap<>();
			txtMap.put("FULL_NAME",params.get("USERNAME").toString());
			txtMap.put("WERKS",params.get("WERKS").toString());
			txtMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME").toString());
			Map<String,Object> txt=wmsCTxtService.getRuleTxt(txtMap);
			if(!"success".equals(txt.get("msg").toString())) {
				return R.error("生成抬头文本失败!");
			}
			else{
				return R.ok().put("HEADER_TXT",txt.get("txtruleitem"));
			}

		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
	}

}
