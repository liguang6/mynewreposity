package com.byd.wms.business.modules.inpda.controller;


import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import com.byd.wms.business.modules.inpda.service.WmsPoReceiptPdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author rain
 * @version 2019年12月4日20:28:52
 * PDA采购订单收货
 */
@RestController
@RequestMapping("inpda/wmsporeceiptpda")
public class WmsPoReceiptPdaController {


	@Autowired
	private WmsPoReceiptPdaService wmsPoReceiptPdaService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsInReceiptService wmsInReceiptService;
	@Autowired
	private WmsCTxtService wmsCTxtService;






	/**
	 * 初始化请求参数
	 * @param params
	 * @return
	 */
	public Map<String,Object> initParams(Map<String,Object> params){
		try {
			params.put("MENU_KEY", "PDA_GR_02");

			params.put("BUSINESS_CODE", "02");
			params.put("BUSINESS_NAME", "02");
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
			PageUtils page = wmsPoReceiptPdaService.getGridPoreDataPage(params);
			R r= wmsPoReceiptPdaService.getPorecCache(params);
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
			return wmsPoReceiptPdaService.validatePoReceiptLable(params);
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
			return wmsPoReceiptPdaService.getPoDetailByBarcode(params);//查询单个标签

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
		PageUtils page = wmsPoReceiptPdaService.getGridPoreDataPage(params);
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
		PageUtils page = wmsPoReceiptPdaService.getBarGridPoreDataPage(params);
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
		return wmsPoReceiptPdaService.poReDeleteBarInfo(params);
	}

	/**
	 * PDA SAP采购订单收货确认(输入凭证日期\记账日期和抬头文本)
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/poReceiptPdaIn")
	public R poReceiptPdaIn(@RequestBody Map<String, Object> params){
		List<Map<String,Object>> matList= new ArrayList<>();
		try {
			this.initParams(params);
			String matListStr=params.get("matList").toString();
			List<Map<String, Object>> allLabelInfos = wmsPoReceiptPdaService.getAllLabelInfos(params);
			if(allLabelInfos==null || allLabelInfos.isEmpty()){
				return R.error("标签信息为空!");
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
			String TEST_FLAG = wmsInReceiptService.getTestFlag(params);//工厂质检配置
			if(TEST_FLAG ==null || "".equals(TEST_FLAG)) {
				//工厂质检主数据未配置
				if("02".equals(BUSINESS_NAME)){
					return R.error("收货工厂"+WERKS+"未配置SAP采购订单收料的工厂质检标识主数据！");
				}
			}
			params.put("TEST_FLAG", TEST_FLAG);

			JSONObject.parseArray(matListStr, Map.class).forEach(m->{
				m=(Map<String,Object>)m;
				m.put("WERKS", params.get("WERKS"));
				m.put("WH_NUMBER", params.get("WH_NUMBER"));
				m.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
				m.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
				m.put("CREATE_DATE", CREATE_DATE);
				m.put("QTY_SAP", m.get("RECEIPT_QTY"));
				matList.add(m);
			});
			params.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			params.put("CREATE_DATE", CREATE_DATE);
			params.put("SEARCH_DATE", curDate);
			params.put("matList", matList);

			//读取和匹配物料质检配置表的免检物料数据
			List<String> matnr_list=new ArrayList<String>();
			if("00".equals(TEST_FLAG)||"01".equals(TEST_FLAG)) {
				List<Map<String,Object>> qc_mat_list=null;
				Map<String,Object> qc_params=new HashMap<String,Object>();
				qc_params.putAll(params);
				if("00".equals(TEST_FLAG)) {
					qc_params.put("TEST_FLAG", "01");// 免检
				}else if("01".equals(TEST_FLAG))  {
					qc_params.put("TEST_FLAG", "00");//质检
				}
				qc_mat_list = wmsInReceiptService.getQCMatList(qc_params);
				if(qc_mat_list!=null && qc_mat_list.size()>0) {
					qc_mat_list.forEach(q->{
						q=(Map)q;
						matnr_list.add((String) q.get("MATNR"));
					});
				}
			}

			for (Map m : matList) {
				//质检
				if("00".equals(TEST_FLAG)) {
					if(matnr_list.contains(m.get("MATNR"))) {
						//免检
						m.put("TEST_FLAG", "01");
					}else {
						//质检
						m.put("TEST_FLAG", "00");
					}
				}
				//免检
				if("01".equals(TEST_FLAG)) {
					if(matnr_list.contains(m.get("MATNR"))) {
						//质检
						m.put("TEST_FLAG", "00");
					}else {
						m.put("TEST_FLAG", "01");
					}
				}
				if("02".equals(TEST_FLAG)) {
					m.put("TEST_FLAG", "02");
				}

			}
			//生成WMS批次信息
			wmsInReceiptService.setMatBatch(params, matList);
			params.put("matList", matList);
			R r = wmsPoReceiptPdaService.poReceiptPdaIn(params);
			Map<String,Object> reMap =(Map<String,Object>) r.get("result");
			if("0".equals(reMap.get("code").toString())) {
				//计算执行时间
				Date endDate = new Date();
				reMap.put("TIME_SLOT", this.computeTimeSlot(startDate, endDate, "sec"));
				return R.ok("SAP采购订单收货成功!").put("result", reMap);
			}else {
				return  R.error(reMap.get("msg")==null?"":reMap.get("msg").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
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
