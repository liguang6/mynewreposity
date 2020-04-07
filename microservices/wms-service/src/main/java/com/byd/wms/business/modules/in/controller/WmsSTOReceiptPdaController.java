package com.byd.wms.business.modules.in.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxDnService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import com.byd.wms.business.modules.in.service.WmsSTOReceiptPdaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * STO交货单收货--PDA
 * @author chu.fuxiang
 */
@RestController
@RequestMapping("in/stoReceiptPda")
public class WmsSTOReceiptPdaController {

	@Autowired
	private WmsSTOReceiptPdaService stoReceiptPdaService;
	@Autowired
	WmsSapRemote wmsSapRemote;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsInReceiptService wmsInReceiptService;
	@Autowired
	WmsCTxtService wmsCTxtService;
	@Autowired
	private WmsAccountReceiptHxDnService wmsAccountReceiptHxDnService;

	/**
	 * 进入页面后，先检查对应业务在PDA扫描缓存表【WMS_PDA_SCAN_CACHE】
	 * 是否有对应账号和业务类型的数据，
	 * 如果有 显示到列表，
	 * 如果没有 列表内容为空。
	 *
	 * 带出STO交货单的缓存信息
	 */
	@RequestMapping("/defaultSTOCache")
	public R defaultSTOCache(@RequestBody Map<String,Object> params) {

		PageUtils page =  stoReceiptPdaService.defaultSTOCache(params);

		return R.ok().put("page", page);
	}

	/**
	 * 带出STO交货单的标签扫描缓存信息
	 */
	@RequestMapping("/defaultLabelCache")
	public R defaultLabelCache(@RequestBody Map<String,Object> params) {

		PageUtils page =  stoReceiptPdaService.defaultLabelCache(params);

		return R.ok().put("page", page);
	}

	/**
	 *交货单有两个来源
	 * 1、同步 --数据存在表 WMS_IN_DELIVERY_PACKING 里
	 * 2、自建 --数据存在表 WMS_CORE_LABEL 里
	 * 扫描的标签号需要保存相关数据到PDA扫描缓存表【WMS_PDA_SCAN_CACHE】，
	 * 如果扫描的标签号在PDA扫描缓存表【WMS_PDA_SCAN_CACHE】存在，
	 * 系统报错提示：请勿重复扫描；
	 */

	@RequestMapping("/scan")
	public R scan(@RequestBody Map<String,Object> params) {

		return stoReceiptPdaService.scan(params);

	}

	/**
	 * SAP交货单收货校验
	 * 调用sap交货单接口带出交货单明细
	 * @param params
	 * @return
	 */
	@RequestMapping("/validateSapOutNo")
	public R validateSapOutNo(@RequestBody Map<String,Object> params) {
		Map<String,Object> retMap = new HashMap<>();
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
		String SAP_OUT_NO= params.get("SAP_OUT_NO").toString();//交货单号
		/**
		 * 通过调用SAP服务接口，读取SAP交货单数据
		 */
		Map<String,Object> dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(SAP_OUT_NO);

		if((null != dnMap.get("CODE") && "-1".equals(dnMap.get("CODE").toString()) )
				|| 	dnMap.get("MESSAGE")!=null) {
			//获取SAP交货单数据失败
			return R.error(500,"获取SAP交货单数据失败："+dnMap.get("MESSAGE").toString());
		}
		//交货单抬头信息
		Map<String,Object> dnHeaderMap = (Map<String,Object>)dnMap.get("header");
		//交货单行项目信息
		List<Map<String,Object>> dnItemList = (ArrayList<Map<String,Object>>)dnMap.get("item");
		if(dnHeaderMap==null) {
			return R.error(500,"SAP交货单:"+SAP_OUT_NO+"不存在，请检查是否输入有误！");
		}
		params.put("werks", dnHeaderMap.get("WERKS"));
		/**
		 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
		 */
		Set<Map<String,Object>> deptMapList = userUtils.getUserWerks("IN_RECEIPT");
		boolean auth=false;
		for(Map dept:deptMapList) {
			if(dept.get("CODE").equals(dnHeaderMap.get("WERKS"))) {
				auth=true;
				break;
			}
		}
		if(!auth) {
			return R.error(500,"您无权操作"+dnHeaderMap.get("WERKS")+"工厂（收货）的单据!");
		}
		if(dnItemList==null||dnItemList.size()==0) {
			return R.error(500,"SAP交货单:"+SAP_OUT_NO+"行项目不存在，请检查交货单是否输入有误！");
		}
		/*
		 * 交货单是否已删除
		 */
		if(null != dnHeaderMap.get("SPE_LOEKZ") && "X".equals(dnHeaderMap.get("SPE_LOEKZ"))) {
			//删除的交货单
			return R.error(500,"SAP交货单："+SAP_OUT_NO+"已删除！");
		}
		/*
		 * 交货单类型是否正确  VBTYP == J
		 */
		if(null == dnHeaderMap.get("VBTYP") || (!"J".equals(dnHeaderMap.get("VBTYP")) && !"T".equals(dnHeaderMap.get("VBTYP")) ) ) {
			//凭证类别 VBTYP不正确
			return R.error(500,"SAP交货单："+SAP_OUT_NO+"不是正确类型的交货单！");
		}
		if(null != dnHeaderMap.get("VBTYP") && "T".equals(dnHeaderMap.get("VBTYP"))) {
			//凭证类别 VBTYP不正确 退货交货单
			return R.error(500,"SAP交货单："+SAP_OUT_NO+"为退货交货单，不允许收货！");
		}
		/**
		 * 判断SAP交货单状态是否可收货，状态必须为A
		 */
		if("B".equals(dnHeaderMap.get("WBSTK"))) {
			//交货单状态必须为B WBSTK
			return R.error(500,"SAP交货单："+SAP_OUT_NO+"已部分交货，不允许收货！");
		}
		if("C".equals(dnHeaderMap.get("WBSTK"))) {
			//交货单状态必须为C WBSTK
			return R.error(500,"SAP交货单："+SAP_OUT_NO+"已交货，不允许收货！");
		}
		params.put("WERKS", dnHeaderMap.get("WERKS")); //交货单收货工厂
		params.put("LIFNR", "VBYD"+dnHeaderMap.get("VSTEL"));//供应商代码为VBYD拼接交货单抬头的交货工厂字段
		/**
		 * 根据供应商代码获取描述信息
		 */
		Map<String,Object> vendorMap = wmsInReceiptService.getVendorInfo(params);
		if(null == vendorMap || null == vendorMap.get("NAME1")) {
			return R.error(500,"交货单关联的供应商："+"VBYD"+dnHeaderMap.get("VSTEL")+"信息未同步至WMS系统，请先同步供应商信息！");
		}
		/**
		 * 查询是否存在危化品
		 */
		List<Map<String,Object>> dangerList =wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表
		/**
		 * 紧急物料列表
		 */
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表
		/**
		 * 根据工厂代码、业务类型获取SAP交货单收料(V)("23")抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", dnHeaderMap.get("WERKS").toString());
		txtMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME").toString());
		txtMap.put("JZ_DATE", JZ_DATE);
		txtMap.put("currentUser", params.get("FULL_NAME").toString());
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		txtMap.put("SAP_OUT_NO", SAP_OUT_NO);
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
		/**
		 * 根据交货单行项目，查询关联的采购订单信息和SAP交货单 虚收101数量，并校验物料信息是否已同步到WMS系统，否则报错
		 */
		dnItemList = wmsAccountReceiptHxDnService.getPoItemListByDnItem(dnItemList,params);
		String matErrorStr = "";
		String poErrorStr = "";
		for (Map<String, Object> dnItemMap : dnItemList) {
			dnItemMap.put("F_WERKS", dnItemMap.get("WERKS"));
			dnItemMap.put("WERKS", dnHeaderMap.get("WERKS"));
			dnItemMap.put("F_BATCH", dnItemMap.get("CHARG"));
			dnItemMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
			dnItemMap.put("UNIT", dnItemMap.get("VRKME"));//交货单位
			dnItemMap.put("sortNo", dnItemMap.get("POSNR"));
			dnItemMap.put("RECEIVER", currentUser.get("FULL_NAME"));
			dnItemMap.put("DANGER_FLAG", "0");
			dnItemMap.put("LIFNR", params.get("LIFNR"));
			dnItemMap.put("LIKTX", vendorMap.get("NAME1"));
			dnItemMap.put("RECEIPT_QTY", dnItemMap.get("LFIMG"));
			dnItemMap.put("PRODUCT_DATE", CUR_DATE);
			dnItemMap.put("SAP_OUT_NO", SAP_OUT_NO);
			dnItemMap.put("SAP_OUT_ITEM_NO", dnItemMap.get("POSNR"));
			dnItemMap.put("UMREZ",dnItemMap.get("UMVKZ"));
			dnItemMap.put("UMREN",dnItemMap.get("UMVKN"));
			dnItemMap.put("LGORT", dnItemMap.get("S_LGORT"));
			dnItemMap.put("PO_NO", dnItemMap.get("VGBEL"));
			dnItemMap.put("PO_ITEM_NO", dnItemMap.get("VGPOS"));

			if(dnItemMap.get("JP_LGORT")==null || "".equals(dnItemMap.get("JP_LGORT"))) {
				dnItemMap.put("JP_LGORT", "00ZT");
			}

			if(dnItemMap.get("FULL_BOX_QTY")!=null && StringUtils.isNotEmpty(dnItemMap.get("FULL_BOX_QTY").toString())) {
				dnItemMap.put("FULL_BOX_QTY", dnItemMap.get("FULL_BOX_QTY"));
				dnItemMap.put("FULL_BOX_FLAG", "X");
			}else {
				dnItemMap.put("FULL_BOX_QTY", dnItemMap.get("LFIMG"));
				dnItemMap.put("FULL_BOX_FLAG", "0");
			}

			for(Map<String,Object> m:dangerList) {
				if(m.get("MATNR").equals(dnItemMap.get("MATNR"))) {
					dnItemMap.put("DANGER_FLAG", "X");
					dnItemMap.put("GOOD_DATES", m.get("GOOD_DATES"));
					dnItemMap.put("MIN_GOOD_DATES", m.get("MIN_GOOD_DATES"));

					break;
				}
			}
			if(urgentList.contains(dnItemMap.get("MATNR"))) {
				dnItemMap.put("sortNo", 0);//紧急物料置顶
				dnItemMap.put("URGENT_FLAG", "X");
			}

			String SOBKZ="Z";
			dnItemMap.put("SOBKZ", SOBKZ);
			dnItemMap.put("HEADER_TXT", txt.get("txtrule"));
			dnItemMap.put("ITEM_TEXT", txt.get("txtruleitem"));
			/**
			 * 判断交货单行项目关联的PO信息是否存在
			 */
			if(dnItemMap.get("EBELN") ==null || "".equals(dnItemMap.get("EBELN").toString())){
				//交货单行项目关联的PO信息未同步到WMS系统
				poErrorStr ="交货单行项目关联的PO："+dnItemMap.get("VGBEL").toString();
			}
			/**
			 * 判断物料主数据是否在工厂存在
			 */
			if(dnItemMap.get("MAKTX") ==null || "".equals(dnItemMap.get("MAKTX").toString())){
				//物料主数据不存在
				matErrorStr += dnItemMap.get("MATNR").toString()+",";
			}

			dnItemMap.put("MAKTX", dnItemMap.get("ARKTX"));

		}

		//查询物料库存
		List<String> matStock = wmsInReceiptService.getMatStock(dnItemList);
		for (Map<String, Object> dnItemMap : dnItemList) {
			String MATNR_IT = dnItemMap.get("MATNR").toString();
			for (String string : matStock) {
				String MATNR_STR = string.split("\\#\\*")[0];
				String STOCK_QTY = string.split("\\#\\*")[1];
				String STOCK_L = string.split("\\#\\*")[2];
				if(MATNR_IT.equals(MATNR_STR)) {
					dnItemMap.put("STOCK_QTY", STOCK_QTY);
					dnItemMap.put("STOCK_L", STOCK_L);
					dnItemMap.put("STOCK_ALL", STOCK_QTY+"/"+STOCK_L);
					break;
				}
			}
		}

		if(!"".equals(poErrorStr)) {
			poErrorStr += "信息未同步到WMS系统，请使用采购订单同步功能同步采购订单信息！";
			return R.error(500,poErrorStr);
		}
		if(!"".equals(matErrorStr)) {
			matErrorStr += "信息在收货工厂"+dnHeaderMap.get("WERKS")+"不存在，请使用物料数据同步功能同步物料信息！";
			return R.error(500,matErrorStr);
		}

		BigDecimal account = BigDecimal.ZERO;
		for (Map<String, Object> item : dnItemList) {
		 	//汇总交货单数量
			String qty = item.get("LFIMG") == null ? "0": item.get("LFIMG").toString();
			qty = qty.substring(0, qty.indexOf("."));
			account = account.add(BigDecimal.valueOf(Long.parseLong(qty)));
		}
		retMap.put("ACCOUNT", account);
		retMap.put("HEADER_TXT", txt.get("txtrule"));
		retMap.put("data", dnItemList);

		return R.ok().put("sto",retMap);
	}

	/**
	 * 储位校验
	 */
	@RequestMapping("/validateStorage")
	public R validateStorage(@RequestBody Map<String,Object> params) {

		return stoReceiptPdaService.validateStorage(params);

	}

	/**
	 * 收货确认
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/boundIn")
	public R boundIn(@RequestBody Map<String, Object> params) {

		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		R r=null;
		String matListStr=params.get("matList").toString();

		String WERKS=params.get("WERKS").toString();
		String BUSINESS_NAME=params.get("BUSINESS_NAME").toString();
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
		String CREATE_DATE=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);

		List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
		//工厂维度质检配置
		params.put("SEARCH_DATE", curDate);
		String TEST_FLAG = wmsInReceiptService.getTestFlag(params);//工厂质检配置
		if(TEST_FLAG ==null || TEST_FLAG.equals("")) {
			//工厂质检主数据未配置
			return R.error("收货工厂"+WERKS+"未配置SAP交货单收料的工厂质检标识主数据！");
		}
		params.put("TEST_FLAG", TEST_FLAG);

		JSONObject.parseArray(matListStr, Map.class).forEach(m->{
			m=(Map<String,Object>)m;
			m.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			m.put("CREATE_DATE", CREATE_DATE);
			m.put("QTY_SAP", m.get("RECEIPT_QTY"));

			int BOX_COUNT = (int)Math.ceil(Double.valueOf(m.get("RECEIPT_QTY").toString())/Double.valueOf(m.get("FULL_BOX_QTY").toString()));
			m.put("BOX_COUNT", BOX_COUNT);

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
		try {
			//SAP交货单确认收货
			List<Map<String,Object>> matListNew=new ArrayList<Map<String,Object>>();
			List<Map> allDataListMap = JSONObject.parseArray(matListStr, Map.class);

			for (Map map : allDataListMap) {

				map.put("REVERSAL_FLAG", "X");
				map.put("CANCEL_FLAG", "X");

				map.put("LGORT", map.get("JP_LGORT"));

				map.put("CREATOR", params.get("USERNAME"));
				map.put("CREATE_DATE", curDate);

				//质检
				if("00".equals(TEST_FLAG)) {
					if(matnr_list.contains(map.get("MATNR"))) {
						//免检
						map.put("TEST_FLAG", "01");
					}else {
						//质检
						map.put("TEST_FLAG", "00");
					}
				}
				//免检
				if("01".equals(TEST_FLAG)) {
					if(matnr_list.contains(map.get("MATNR"))) {
						//质检
						map.put("TEST_FLAG", "00");
					}else {
						map.put("TEST_FLAG", "01");
					}
				}
				if("02".equals(TEST_FLAG)) {
					map.put("TEST_FLAG", "02");
				}

				matListNew.add(map);
			}
			params.put("matListNew", matListNew);
			wmsInReceiptService.setMatBatch(params, matList);
			params.put("matList", matList);
			r = wmsInReceiptService.boundIn_03(params);
			//过账成功后，
			// 1.计算操作时长
			// 2.删除所有相关的缓存数据
			// 3.变更交货单状态
			if(Integer.parseInt(r.get("code").toString()) == 0){
				List<Map<String, Object>> list = new ArrayList<>();
				list.add(params);
				params.put("WMS_NO",r.get("WMS_NO"));
				stoReceiptPdaService.calcOpsTime(params);
				stoReceiptPdaService.deleteAllLabelCache(list);
			}

			return r;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}

	}

	/**
	 * 删除 用户在PDA端STO交货单收货页面的缓存数据
	 * @param params
	 * @return
	 */
	@RequestMapping("/deleteLabelCacheInfo")
	public R deleteLabelCacheInfo(@RequestBody List<String> list) {

		int num = stoReceiptPdaService.deleteLabelCacheInfo(list);
		return R.ok();
	}


}
