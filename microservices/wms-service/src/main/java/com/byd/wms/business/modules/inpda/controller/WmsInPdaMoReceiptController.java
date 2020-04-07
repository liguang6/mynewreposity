package com.byd.wms.business.modules.inpda.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.*;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.in.service.WmsInHandoverService;
import com.byd.wms.business.modules.inpda.service.WmsInPdaMoReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author rain
 * @version 2019年12月22日11:22:24
 * PDA生产订单101收货
 */
@RestController
@RequestMapping("inpda/wmsmoreceiptpda")
public class WmsInPdaMoReceiptController {


	@Autowired
	private WmsInPdaMoReceiptService wmsInPdaMoReceiptService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsCTxtService wmsCTxtService;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private WmsInHandoverService wmsInHandoverService;


	/**
	 * 初始化请求参数
	 * @param params
	 * @return
	 */
	public Map<String,Object> initParams(Map<String,Object> params){
		try {
			params.put("MENU_KEY", "PDA_GR_03");

			params.put("BUSINESS_CODE", "GR_301");
			params.put("BUSINESS_NAME", "11");
			params.put("BUSINESS_TYPE", "10");
			params.put("BUSINESS_CLASS", "03");
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
	 * PDA 生产订单进入查询缓存信息
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getMorecCache")
	public R getMorecCache(@RequestBody Map<String, Object> params){
		try {
			this.initParams(params);
			PageUtils page = wmsInPdaMoReceiptService.getGridPoreDataPage(params); //需要查询出,明细页面信息.
			R r= wmsInPdaMoReceiptService.getMorecCache(params);
			r.put("page", page);
			return r;

		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
	}

	/**
	 * PDA 生产订单扫描校验
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/validatePoReceiptLable")
	public R validatePoReceiptLable(@RequestBody Map<String, Object> params){
		try{
			this.initParams(params);
			return wmsInPdaMoReceiptService.validlableinfo(params);
		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
	}

	/**
	 * 通过条形码获取生产订单信息(扫描条码或者输入条码回车确定)
	 * 通过条码编号查询相关订单信息并且保存缓存信息
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getMoDetailByBarcode")
	public R getMoDetailByBarcode(@RequestBody Map<String, Object> params){
		try {
			this.initParams(params);
			return wmsInPdaMoReceiptService.getMoDetailByBarcode(params);//查询单个标签

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
		PageUtils page = wmsInPdaMoReceiptService.getGridPoreDataPage(params);
		return R.ok().put("page", page);
	}

	/**
	 * 获取生产订单条码明细页面
	 * @param params
	 * @return
	 */
	@RequestMapping("/getBarGridPoreData")
	public R getBarGridPoreData(@RequestParam Map<String, Object> params){
		this.initParams(params);
		PageUtils page = wmsInPdaMoReceiptService.getBarGridPoreDataPage(params);
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
		return wmsInPdaMoReceiptService.poReDeleteBarInfo(params);
	}

	/**
	 * PDA 生产订单收货确认(输入凭证日期\记账日期和抬头文本)
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/moReceiptPdaIn")
	public R moReceiptPdaIn(@RequestBody Map<String, Object> params){
		List<Map<String,Object>> matList= new ArrayList<>();
		try {
			this.initParams(params);
			String matListStr=params.get("ARRLIST").toString();
			List<Map<String, Object>> allLabelInfos = wmsInPdaMoReceiptService.getAllLabelInfos(params);
			if(allLabelInfos==null || allLabelInfos.isEmpty()){
				return R.error("已扫描标签信息为空!");
			}
			//按照创建时间升序查询缓存的信息,获取第一个则为开始扫描的时间.用于记录操作时长
			Date startDate=DateUtils.stringToDate(allLabelInfos.get(0).get("CREATE_DATE").toString(),"yyyy-MM-dd HH:mm:ss");
			String skListStr=allLabelInfos.toString();
			params.put("skList", allLabelInfos);
			String WERKS=params.get("WERKS").toString();
			String BUSINESS_NAME=params.get("BUSINESS_NAME").toString();
			String curDate=DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
			String CREATE_DATE=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
			//工厂维度质检配置
			params.put("SEARCH_DATE", curDate);
			JSONObject.parseArray(matListStr, Map.class).forEach(m->{
				m=(Map<String,Object>)m;
				m.put("WERKS", params.get("WERKS"));
				m.put("WH_NUMBER", params.get("WH_NUMBER"));
				m.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
				m.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE"));
				m.put("BUSINESS_CLASS", params.get("BUSINESS_CLASS"));
				m.put("PZ_DATE", params.get("PZ_DATE"));
				m.put("JZ_DATE", params.get("JZ_DATE"));
				m.put("PZ_YEAR", params.get("PZ_DATE").toString().substring(0,4));
				m.put("HEADER_TXT", params.get("HEADER_TXT"));
				m.put("BIN_CODE", params.get("BIN_CODE"));
				m.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
				m.put("CREATE_DATE", CREATE_DATE);
				m.put("QTY_SAP", m.get("MAY_IN_QTY"));
				m.put("IN_QTY", m.get("MAY_IN_QTY"));
				matList.add(m);
			});
			params.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			params.put("CREATE_DATE", CREATE_DATE);
			params.put("SEARCH_DATE", curDate);
			params.put("ARRLIST", JSON.toJSONString(matList));

			Map<String, Object> retMap = new HashMap<>();
			//加锁
			boolean keyFlag = true;
			List<String> keylist = new ArrayList<>();
			String uid = UUID.randomUUID().toString();
			StringBuffer inberror = new StringBuffer();
			JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
			for(int i=0;i<jarr.size();i++){
				JSONObject jsonData=  jarr.getJSONObject(i);
				String inboundNo = jsonData.getString("INBOUND_NO");
				if(keylist.contains(inboundNo)) {
					continue;
				}

				if (redisUtils.tryLock(inboundNo, uid))
				{
					keylist.add(inboundNo);
				} else {
					keyFlag = false;
					inberror.append(inboundNo);
					break;
				}

			}

			if (keyFlag) {
				params.put("UID", uid);
				params.put("keylist", keylist);
				retMap=wmsInPdaMoReceiptService.handover_new(params);
			}

			for(String keys : keylist) {
				redisUtils.unlock(keys, uid); //解锁
			}

			if (!keyFlag) {
				return R.error(inberror.toString() + ",正在执行中，请稍后再试！");
			}


//			R r = wmsInPdaMoReceiptService.moReceiptPdaIn(params);
			Map<String,Object> reMap =(Map<String,Object>)retMap.get("result");
			if("0".equals(reMap.get("code").toString())) {
				//计算执行时间
				Date endDate = new Date();
				reMap.put("TIME_SLOT", this.computeTimeSlot(startDate, endDate, "sec"));
				reMap.put("INBOUND_NO", matList.get(0).get("INBOUND_NO"));
				return R.ok("SAP采购订单收货成功!").put("result", reMap);
			}else {
				return  R.error(reMap.get("msg")==null?"":reMap.get("msg").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
//		return R.ok().put("result", result);
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
