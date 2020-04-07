package com.byd.wms.business.modules.returnpda.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.*;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.returngoods.dao.WorkshopReturnDao;
import com.byd.wms.business.modules.returnpda.dao.WmsReMaterialWarehouseDao;
import com.byd.wms.business.modules.returnpda.dao.WmsReMaterialWarehouseTwoDao;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialPdaService;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialWarehouseService;
import com.byd.wms.business.modules.returnpda.service.WmsReMaterialWarehouseTwoService;
import com.byd.wms.business.modules.returnpda.service.WmsWebServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liguang6
 * @version 2019年1月2日12:28:34
 * 立库余料退回（312）
 */
@Service
public class WmsReMaterialWarehouseTwoServiceImpl implements WmsReMaterialWarehouseTwoService {


	@Autowired
	private WmsReMaterialWarehouseTwoDao wmsReMaterialWarehouseDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
    private WmsCDocNoService wmsCDocNoService;
    @Autowired
	private WmsReMaterialPdaService wmsReMaterialPdaService;
    @Autowired
	private UserUtils userUtils;
    @Autowired
	private WmsCDocNoService wmscDocNoService;
	@Autowired
	private WorkshopReturnDao workshopReturnDao;
	@Autowired
	private WmsWebServiceRemote wmsWebServiceRemote;


	@Override
	public R getPorecCache(Map<String, Object> map) {
		try {
			//获取采购订单收货缓存信息
			List<Map<String,Object>> result = wmsReMaterialWarehouseDao.getPorecCache(map);
			return R.ok().put("result", result);
		}catch (Exception e){
			return R.error(e.getMessage());
		}
	}
	/**
	 * 条码校验
	 * @param params
	 * @return R.ok
	 */
	@Override
	public R validatePoReceiptLable(Map<String, Object> params) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		String curStrTime=dateFormat.format(currentDate);
		if(params.get("BIN_CODE") ==null||"".equals(params.get("BIN_CODE").toString())){
			return  R.error("储位不能为空，请填写储位信息");
		}else {
			try {
				Map<String,Object> bin_code = wmsReMaterialWarehouseDao.valiateBinCode(params);
				if(bin_code == null ||bin_code.equals("")){
					return R.error("输入的储位有误，请重新输入");
				}
			}catch (Exception e){
				return R.error("储位数据有重复");
			}

		}
		if(params.get("LABEL_NO")==null || "".equals(params.get("LABEL_NO").toString())){
			return R.error("标签条码号丢失,请重试!");
		}else if(params.get("WERKS")==null || "".equals(params.get("WERKS").toString())){
			return R.error("工厂号丢失,请重试!");

		}else if(params.get("WH_NUMBER")==null || "".equals(params.get("WH_NUMBER").toString())){
			return R.error("仓库号丢失,请重试!");
		}
		//验证缓存表里面是否缓存该标签条码
		List<Map<String,Object>> cacheValidateMap= wmsReMaterialWarehouseDao.getPorecCache(params);
		if(cacheValidateMap != null && !cacheValidateMap.isEmpty()){
			return R.error("该条码已经扫描,请不要重复扫描！");
		}
		//验证条码是否存在或存在多条
		try {
			List<Map<String, Object>> result = wmsReMaterialWarehouseDao.getPoDetailListByBarcode(params);
			if (result == null || result.isEmpty()) {
				return R.error("标签号 " + params.get("LABEL_NO") + "在条码信息中不存在！");
			}
		}catch (Exception e){
			return R.error("在条码信息表中存在重复条码号");
		}
		Map<String, Object> labelMap = new HashMap<>();
		labelMap.put("LABEL_NO", params.get("LABEL_NO"));
		labelMap.put("WERKS", params.get("WERKS"));
		labelMap.put("WH_NUMBER", params.get("WH_NUMBER"));
		List<Map<String, Object>> retLabelList=wmsReMaterialWarehouseDao.getPoLabelList(labelMap);//获取页面即将查询的这一条标签
		JSONArray jarr = new JSONArray();
		String lifnr = null;
		if(params.get("ARRLIST")!=null && params.get("ARRLIST").toString().contains("{")) {
			jarr = JSON.parseArray(params.get("ARRLIST").toString());
			JSONObject jarr0Obj = jarr.getJSONObject(0);
			if(jarr0Obj.getString("LIFNR")==null){
				return R.error("已扫的条码的供应商信息丢失！");
			}
			lifnr=jarr0Obj.getString("LIFNR");
		}

		/**
		 * 校验：1、条码与状态权限（工厂是否是该登录用户所选的工厂）【待之后加入用户登录所选工厂查询】,带入查询sql,查不出来即是没有权限.
		 * 	2、条码状态为创建
		 * 	3、同一供应商（已扫条码）
		 * 	4、需要看工厂是否启用有效期WMS_C_WH，条码有效期（不能为空、不能小于生产日期、不能小于当前收货日期）
		 */
		if(retLabelList!=null&&retLabelList.size()>0){
			for (Map<String, Object> map : retLabelList) {
				//验证条码状态要为创建
				if(!"00".equals(map.get("LABEL_STATUS").toString())&&!"10".equals(map.get("LABEL_STATUS").toString())){
					return R.error("该标签条码："+map.get("LABEL_NO")+"的状态不为创建或已出库！");
				}
				//验证所有条码要为同一个供应商
				if(lifnr != null && !lifnr.equals(map.get("LIFNR").toString())){
					return R.error("该标签条码："+map.get("LABEL_NO")+"的供应商与之前扫描的供应商不一致！");
				}

				//工厂要启用有效期，条码有效期（不能为空、不能小于生产日期、不能小于当前收货日期）
				if("X".equals(map.get("PRFRQFLAG").toString())) {
					if ("".equals(map.get("EFFECT_DATE") == null ? "" : map.get("EFFECT_DATE").toString())) {
						return R.error("该标签条码：" + map.get("LABEL_NO") + "的有效期不能为空！");
					}
					else if (compareTime(map.get("PRODUCT_DATE").toString(), (map.get("EFFECT_DATE")).toString(), "yyyy-MM-dd") >= 0) {
						//生产日期大于等于有效日期
						return R.error("该标签条码：" + map.get("LABEL_NO") + "的生产日期大于等于有效期！");
					}
					else if (compareTime(curStrTime, (map.get("EFFECT_DATE")).toString(), "yyyy-MM-dd") >= 0) {
						//当前收货日期大于等于有效日期
						return R.error("该标签条码：" + map.get("LABEL_NO") + "的当前收货日期大于等于有效期！");
					}
				}
			}
		}else{
			return R.error("没有权限操作该工厂条码信息或者该条码的当前标签条码信息不存在！");
		}

		return R.ok();
	}
	/**
	 * 获取条码详细信息并插入缓存表
	 * @param params
	 * @return result
	 */
	@Override
	public R getPoDetailByBarcode(Map<String, Object> params) {
		try {
			List<Map<String,Object>> result = wmsReMaterialWarehouseDao.getPoDetailListByBarcode(params);
			if(result == null || result.isEmpty()){
				return R.error("标签号 "+params.get("LABEL_NO")+"在条码信息中不存在！");
			}
			if(params.get("TPNUM")==null||params.get("TPNUM") ==""){
				return R.error("请输入托盘号");
			}
			//先保存缓存扫描信息(通过工号/工厂/仓库号/业务类型/业务code,确定唯一性)todo 加上菜单code
			wmsReMaterialWarehouseDao.insertPoReceiptPdaCache(params);
			return R.ok().put("result", result.get(0));
		}catch (Exception e){
			return R.error(e.getMessage());
		}
	}

	/**
	 * 分组获取采购订单收货明细页面
	 * @param params
	 * @return page
	 */
	@Override
	public PageUtils getGridPoreDataPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsReMaterialWarehouseDao.getGridPoreDataCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=wmsReMaterialWarehouseDao.getGridPoreData(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

	/**
	 * 获取采购订单条码明细页面
	 * @param params
	 * @return page
	 */
	@Override
	public PageUtils getBarGridPoreDataPage(Map<String, Object> params) {
		List<Map<String,Object>> result = new ArrayList<>();
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsReMaterialWarehouseDao.getBarGridPoreDataCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=wmsReMaterialWarehouseDao.getBarGridPoreData(params);
		int i = 1;
		for(Map<String,Object> map : list){
			map.put("ROWID",i);
			i++;
			result.add(map);
		}

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(result);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}


	@Override
	public R poReDeleteBarInfo(Map<String, Object> params) {
		try {
			List<String> labelList = (List<String>) JSONArray.parse(params.get("LABEL_ARR").toString());
			params.put("labelList", labelList);
			wmsReMaterialWarehouseDao.delPorecBarInfos(params);
			return R.ok();
		}catch (Exception e){
			return R.error(e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> getAllLabelInfos(Map<String, Object> map) {
		List<Map<String, Object>> allLabelInfos= wmsReMaterialWarehouseDao.getAllLabelInfos(map);
		return allLabelInfos;
	}

	/**
	 * 立库余料（312）确认
	 * 1生成退料单号
	 * 2调用立库接口，将条码号和托盘等信息传给立库系统
	 * @param params
	 * @return 退料单号——RETURN_NO WNS凭证号——WMS_NO,退货单行项目——RETURN_ITEM_NO
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> poReceiptPdaIn(Map<String, Object> params) {

		String RETURN_NO = "";
		Map<String,Object> result = new TreeMap<>();
		Map<String, Object> currentUser = userUtils.getUser();
		JSONArray jarr = JSON.parseArray(params.get("params").toString());
		//【退货单抬头表】
		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("WERKS", params.get("WERKS").toString());
		params2.put("WMS_DOC_TYPE", "06");
		RETURN_NO = wmscDocNoService.getDocNo(params2).get("docno").toString();
		if (null == RETURN_NO) {
			throw new RuntimeException("没有生成退料单号！");
		}
		String HEADER_TXT = "";
		Map<String, Object> headMap = new HashMap<String, Object>();
		headMap.put("RETURN_NO", RETURN_NO);
		headMap.put("RETURN_TYPE", "06");        //ModBy:YK190621 改为存BUSINESS_CLASS
		headMap.put("RETURN_STATUS", "00");
		headMap.put("WERKS", params.get("WERKS").toString());
		headMap.put("LIFNR", "");
		headMap.put("LIKTX", "");
		headMap.put("HEADER_TXT", HEADER_TXT);
		headMap.put("IS_AUTO", "");
		headMap.put("DEL", "0");
		headMap.put("CREATOR", currentUser.get("USERNAME"));
		headMap.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		headMap.put("EDITOR", "");
		headMap.put("EDIT_DATE", "");
		workshopReturnDao.insertWmsOutReturnHead(headMap);
		//【退货单行项目表】
		int RETURN_ITEM_NO = 1;
		for (int j = 0; j < jarr.size(); j++) {
			JSONObject outData = jarr.getJSONObject(j);
			Map<String, Object> itemMap = new HashMap<String, Object>();
			Map<String, Object> itemDetailMap = new HashMap<String, Object>();
			itemMap.put("RETURN_NO", RETURN_NO);
			itemMap.put("RETURN_ITEM_NO", RETURN_ITEM_NO);
			itemMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME").toString());
			itemMap.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE").toString());
			itemMap.put("WERKS", params.get("WERKS").toString());
			itemMap.put("MO_NO", outData.getString("MO_NO").toString());
			itemMap.put("LGORT", outData.getString("LGORT").toString());
			itemMap.put("MATNR", outData.getString("MATNR"));
			itemMap.put("MAKTX", outData.getString("MAKTX"));
			itemMap.put("UNIT", outData.getString("MEINS"));
			itemMap.put("TOTAL_RETURN_QTY", Float.valueOf(outData.getString("TOTALL_QTY").trim()));
			itemMap.put("RETURN_PEOPLE", currentUser.get("USERNAME"));
			itemMap.put("ITEM_STATUS", "00");
			itemMap.put("MEMO", (outData.getString("MEMO") == null) ? "" : outData.getString("MEMO"));
			itemMap.put("RECEIPT_NO", (outData.getString("RECEIPT_NO") == null) ? "" : outData.getString("RECEIPT_NO"));
			itemMap.put("RECEIPT_ITEM_NO", (outData.getString("RECEIPT_ITEM_NO") == null) ? "" : outData.getString("RECEIPT_ITEM_NO"));
			itemMap.put("LIFNR", (outData.getString("LIFNR") == null) ? "" : outData.getString("LIFNR"));
			itemMap.put("LIKTX", (outData.getString("LIKTX") == null) ? "" : outData.getString("LIKTX"));
			String PO_NO = "";
			String PO_ITEM_NO = "";
			if ("69".equals(params.get("BUSINESS_NAME").toString())) {
				PO_NO = (outData.getString("RSNUM") == null) ? "" : outData.getString("RSNUM");
				PO_ITEM_NO = (outData.getString("RSPOS") == null) ? "" : outData.getString("RSPOS");
			} else {
				PO_NO = (outData.getString("PO_NO") == null) ? "" : outData.getString("PO_NO");
				PO_ITEM_NO = (outData.getString("PO_ITEM_NO") == null) ? "" : outData.getString("PO_ITEM_NO");
			}
			itemMap.put("PO_NO", PO_NO);
			itemMap.put("PO_ITEM_NO", PO_ITEM_NO);
			itemMap.put("SAP_OUT_NO", (outData.getString("SAP_OUT_NO") == null) ? "" : outData.getString("SAP_OUT_NO"));
			itemMap.put("SAP_OUT_ITEM_NO", (outData.getString("SAP_OUT_ITEM_NO") == null) ? "" : outData.getString("SAP_OUT_ITEM_NO"));
			itemMap.put("RSNUM", (outData.getString("RSNUM") == null) ? "" : outData.getString("RSNUM"));
			itemMap.put("RSPOS", (outData.getString("RSPOS") == null) ? "" : outData.getString("RSPOS"));
			itemMap.put("SAP_MATDOC_NO", (outData.getString("SAP_MATDOC_NO") == null) ? "" : outData.getString("SAP_MATDOC_NO"));
			itemMap.put("SAP_MATDOC_ITEM_NO", (outData.getString("SAP_MATDOC_ITEM_NO") == null) ? "" : outData.getString("SAP_MATDOC_ITEM_NO"));
			itemMap.put("INBOUND_NO", (outData.getString("INBOUND_NO") == null) ? "" : outData.getString("INBOUND_NO"));
			itemMap.put("INBOUND_ITEM_NO", (outData.getString("INBOUND_ITEM_NO") == null) ? "" : outData.getString("INBOUND_ITEM_NO"));
			itemMap.put("SQE", (outData.getString("SQE") == null) ? "" : outData.getString("SQE"));
			itemMap.put("PICK_FLAG", "0");
			itemMap.put("DEL", "0");
			itemMap.put("CREATOR", currentUser.get("USERNAME"));
			itemMap.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			itemMap.put("EDITOR", "");
			itemMap.put("EDIT_DATE", "");


			itemDetailMap.put("RETURN_NO", RETURN_NO);
			itemDetailMap.put("RETURN_ITEM_NO", RETURN_ITEM_NO);
			itemDetailMap.put("MATNR", outData.getString("MATNR"));
			itemDetailMap.put("MO_NO", outData.getString("MO_NO"));
			itemDetailMap.put("LGORT", outData.getString("LGORT"));
			itemDetailMap.put("F_BATCH", (outData.getString("F_BATCH") == null) ? "" : outData.getString("F_BATCH"));
			itemDetailMap.put("BATCH", (outData.getString("BATCH") == null) ? "" : outData.getString("BATCH"));
			itemDetailMap.put("RETURN_QTY", Float.valueOf(outData.getString("TOTALL_QTY").trim()));
			itemDetailMap.put("LIFNR", (outData.getString("LIFNR") == null) ? "" : outData.getString("LIFNR"));
			itemDetailMap.put("SOBKZ", outData.getString("SOBKZ"));
			itemDetailMap.put("BIN_CODE", (outData.getString("BIN_CODE") == null) ? "" : outData.getString("BIN_CODE"));
			itemDetailMap.put("BIN_CODE_XJ", (outData.getString("BIN_CODE_XJ") == null) ? "" : outData.getString("BIN_CODE_XJ"));
			itemDetailMap.put("XJ_QTY", (outData.getString("XJ_QTY") == null) ? "" : outData.getString("XJ_QTY"));
			itemDetailMap.put("REAL_QTY", (outData.getString("REAL_QTY") == null) ? "" : outData.getString("REAL_QTY"));
			itemDetailMap.put("ITEM_TEXT", (outData.getString("ITEM_TEXT") == null) ? "" : outData.getString("ITEM_TEXT"));
			itemDetailMap.put("RETURN_PEOPLE", currentUser.get("USERNAME"));
			itemDetailMap.put("MEMO", (outData.getString("MEMO") == null) ? "" : outData.getString("MEMO"));
			itemDetailMap.put("DEL", "0");
			itemDetailMap.put("CREATOR", currentUser.get("USERNAME"));
			itemDetailMap.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			itemDetailMap.put("EDITOR", "");
			itemDetailMap.put("EDIT_DATE", "");
			itemDetailMap.put("LABEL_NO", "");

			workshopReturnDao.insertWmsOutReturnItem(itemMap);
			workshopReturnDao.insertWmsOutReturnItemDetail(itemDetailMap);
			RETURN_ITEM_NO++;

		}
		//调用立库接口传输数据
		JSONObject res = new JSONObject();
		res.put("RETURN_NO",RETURN_NO);
		res.put("RETURN_ITEM_NO",RETURN_ITEM_NO);
		//获取条码信息
		List<Map<String,Object>> labelList = wmsReMaterialPdaService.getAllScanInfo(params);
		res.put("labelList",labelList);
		res.put("RETURN_NO",RETURN_NO);
		res.put("RETURN_ITEM_NO",RETURN_ITEM_NO);
		res.put("WSDL","http://10.27.78.114:8089/EWMService.asmx?WSDL");
		result = wmsWebServiceRemote.getWebServiceReWare(res);
		result.put("RETURN_NO",RETURN_NO);
		result.put("RETURN_ITEM_NO",RETURN_ITEM_NO);
		result.put("CREAT_DATE",jarr.getJSONObject(0).getString("CREAT_DATE"));
		return result;
	}




	/**
	 * @description: 两个String类型，按照日期格式对比
	 *              eg:
	 *                  dateOne：2015-12-26
	 *                  dateTwo：2015-12-26
	 *                  dateFormatType: yyyy-MM-dd
	 *                  返回类型：-1：dateOne小于dateTwo， 0：dateOne=dateTwo ，1：dateOne大于dateTwo
	 * @param dateOne
	 * @param dateTwo
	 * @param dateFormatType：yyyy-MM-dd / yyyy-MM-dd HH:mm:ss /等
	 * @return -1，0，1，100
	 * @throws
	 * @author rain
	 * @data:2019年12月5日11:05:16
	 */
	public static int compareTime(String dateOne, String dateTwo , String dateFormatType){

		DateFormat df = new SimpleDateFormat(dateFormatType);
		Calendar calendarStart = Calendar.getInstance();
		Calendar calendarEnd = Calendar.getInstance();

		try {
			calendarStart.setTime(df.parse(dateOne));
			calendarEnd.setTime(df.parse(dateTwo));
		} catch (ParseException e) {
			e.printStackTrace();
			return 100;
		}
		int result = calendarStart.compareTo(calendarEnd);
		if(result > 0){
			result = 1;
		}else if(result < 0){
			result = -1;
		}else{
			result = 0 ;
		}
		return result ;
	}





}

