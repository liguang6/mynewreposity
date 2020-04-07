package com.byd.wms.business.modules.in.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxDnService;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxToService;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsCloudPlatformRemote;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.byd.wms.business.modules.in.entity.Wmin00an00Entity;
import com.byd.wms.business.modules.in.entity.Wmin00anEntity;
import com.byd.wms.business.modules.in.entity.Wmin00skEntity;
import com.byd.wms.business.modules.in.entity.WmsInReceiptEntity;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;


/**
 * WMS收货单
 *
 * @author (changsha) byd_infomation_center
 * @email
 * @date 2018-08-20 16:06:38
 */
@RestController
@RequestMapping("in/wmsinreceipt")
public class WmsInReceiptController {
	@Autowired
	private WmsInReceiptService wmsInReceiptService;
	@Autowired
	private SCMDeliveryService scmDeliveryService;
	@Autowired
	WmsSapRemote wmsSapRemote;
	@Autowired
	WmsCDocNoService wmsCDocNoService;
	@Autowired
	WmsCTxtService wmsCTxtService;
	@Autowired
	WmsCPlantService wmsCPlantService;
	@Autowired
	private WmsAccountReceiptHxDnService wmsAccountReceiptHxDnService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsAccountReceiptHxToService wmsAccountReceiptHxToService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsCloudPlatformRemote wmsCloudPlatformRemote;
	@Autowired
	private WmsSapPlantService wmsSapPlantService;

	/**
	 * 根据SCM送货单号及物料号获取条码明细 SCM收货物料点收
	 *
	 * @param params : ASSNO,MATNR,PO_NO,PO_ITEM_NO
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/getMatBarcodeList")
	public R getMatBarcodeList(@RequestBody Map<String, Object> params){
		System.out.println(params.get("ASS_NO") + "|" + params.get("MATNR"));
		if(params.get("ASS_NO") == null ||params.get("MATNR") == null) {
			return R.error("SCM送货单和物料号不能为空！");
		}else if(params.get("ASS_NO").toString().equals("") ||params.get("MATNR").toString().equals("")) {
			return R.error("SCM送货单和物料号不能为空！");
		}else {
			return R.ok().put("data", scmDeliveryService.getMatBarcodeList(params));
		}
	}
	/**
	 * 根据SCM送货单号查询对应条码列表： PDA SCM条码收货
	 */
	@CrossOrigin
	@RequestMapping("/getAllMatBarcodeList")
	public R getAllMatBarcodeList(@RequestBody Map<String, Object> params){
		if(params.get("ASS_NO") == null) {
			return R.error("SCM送货单不能为空！");
		}else if(params.get("ASS_NO").toString().equals("")) {
			return R.error("SCM送货单不能为空！");
		}else {
			return R.ok().put("data", scmDeliveryService.getAllMatBarcodeList(params));
		}
	}
	/**
	 * 根据SAP凭证号和工厂号查询对应条码列表： PDA 303调拨收货 条码校验
	 */
	@CrossOrigin
	@RequestMapping("/getAllMatBarcodeListBySapMatDocNo")
	public R getAllMatBarcodeListBySapMatDocNo(@RequestBody Map<String, Object> params){
		if(params.get("SAP_MATDOC_NO") == null || params.get("WH_NUMBER") == null) {
			return R.error("SAP凭证号和工厂号不能为空！");
		}else if(params.get("SAP_MATDOC_NO").toString().equals("") || params.get("WH_NUMBER").toString().equals("")) {
			return R.error("SAP凭证号和工厂号不能为空！");
		}else {
			return R.ok().put("data", scmDeliveryService.getAllMatBarcodeListBySapMatDocNo(params));
		}
	}
	/**
	 * 查询条码信息
	 */
	@CrossOrigin
	@RequestMapping("/getLabelInfo")
	public R getLabelInfo(@RequestBody Map<String, Object> params){
		if(params.get("LabelNo") == null) {
			return R.error("条码不能为空！");
		}else if(params.get("LabelNo").toString().equals("")) {
			return R.error("条码不能为空！");
		}else {
			return R.ok().put("data", wmsInReceiptService.getLabelInfo(params));
		}
	}
	@CrossOrigin
	@RequestMapping("/getScmBarCodeInfo")
	public R getScmBarCodeInfo(@RequestBody Map<String, Object> params){
		if(params.get("BARCODE") == null) {
			return R.error("条码不能为空！");
		}else if(params.get("BARCODE").toString().equals("")) {
			return R.error("条码不能为空！");
		}else {
			return R.ok().put("data", scmDeliveryService.getScmBarCodeInfo(params));
		}
	}

	/**
	 * 根据SCM送货单获取物料列表；
	 * 判断状态：如果送货单对应的状态是已收货，已关闭，已取消，不需要显示送货单数据，提示已收货（或者 已关闭，已取消）
	 * 读取SCM送货单数据和包装箱数据，如果数据不存在,提示送货单不存在
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin
	@RequestMapping("/listScmMat")
	public R listScmMat(@RequestBody Map<String, Object> params){
		/**
		 * 查询SCM送货单信息
		 */

		Map<String, Object> scmDeliveryMap = scmDeliveryService.querySCMDelivery(params);
		Wmin00anEntity mat = (Wmin00anEntity)scmDeliveryMap.get("wmin00an");
		if(scmDeliveryMap==null||scmDeliveryMap.get("wmin00an")==null ||mat.getWERKS()==null) {
			return R.error("送货单:"+params.get("ASNNO")+"不存在！");
		}
		if("3".equals(mat.getST())) {
			return R.error("该送货单已发货！");
		}
		if("9".equals(mat.getST())) {
			return R.error("该送货单已关闭！");
		}
		if("4".equals(mat.getST())) {
			return R.error("该送货单已取消！");
		}
		if("5".equals(mat.getST())) {
			return R.error("该送货单已删除！");
		}
		if("1".equals(mat.getST())) {
			return R.error("该送货单已创建，不允许收货！");
		}
		if(scmDeliveryMap.get("wmin00an00list")==null) {
			return R.error("送货单:"+params.get("ASNNO")+"不存在！");
		}

		List<Wmin00an00Entity> list= (List<Wmin00an00Entity>)scmDeliveryMap.get("wmin00an00list");

		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		//判断账号是否有对应送货单工厂的操作权限，如果没有提示无权限
		boolean auth=false;
		try {
			Set<Map<String,Object>> deptList = userUtils.getUserWerks("IN_RECEIPT");
			if(deptList == null) {
				return R.error("您没有任何工厂的操作权限!");
			}
			for(Map dept:deptList) {
				if(dept.get("CODE").equals(mat.getWERKS())) {
					auth=true;
					break;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}

		if(!auth) {
			return R.error("您无权操作"+mat.getWERKS()+"工厂的单据!");
		}
		List<String> poList = new ArrayList<String>(); //需要校验采购订单凭证类型的采购订单号
		List<String> poitemList=new ArrayList<String>(); //需要校验的采购订单行项目数据
		List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）

		List<Map<String,Object>> datalist=new ArrayList<Map<String,Object>>();

		params.put("WERKS", mat.getWERKS());
		params.put("WH_NUMBER", mat.getWH_NUMBER());
		params.put("LIFNR", mat.getLIFNR());

		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表
		List<Map<String,Object>> dangerList=wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表
		Map<String, Object>  whSettingMap = commonService.getPlantSetting(mat.getWH_NUMBER());
		Map<String,Object> transfMap=null;

		String matnr_str ="";
		List<String> checkMatInfoList = new ArrayList<>();
		for(int i=0;i<list.size();i++) {
			transfMap=new HashMap<String,Object>();
			try {
				transfMap=BeanUtils.describe(list.get(i));
				/*Map<String,Object> tmap=new HashMap<String,Object>();
				Set<String> keys=transfMap.keySet();
				for(String k:keys) {
					tmap.put(k.toUpperCase(), transfMap.get(k));
				}
				transfMap=tmap;*/

			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				return R.error("系统异常："+e.getMessage());
			}


			//SCM送货单数据中的采购订单行项目去重，从SAP同步的PO行项目号为五位数
			String poValid=transfMap.get("PONO")+"#*"+String.format("%05d", Integer.valueOf(transfMap.get("POITM").toString()).intValue());
			if(!poitemList.contains(poValid)) {
				poitemList.add(poValid);
			}

			if(!poList.contains(transfMap.get("PONO").toString())) {
				poList.add(transfMap.get("PONO").toString());
			}

			asnList.add(transfMap.get("ASNNO")+"#*"+transfMap.get("ASNITM"));

			datalist.add(transfMap);
			datalist.get(i).put("BARCODE_FLAG", whSettingMap==null?"0":whSettingMap.get("BARCODE_FLAG"));
			datalist.get(i).put("IG_FLAG", whSettingMap.get("IG_FLAG"));
			datalist.get(i).put("PO_NO", transfMap.get("PONO"));
			datalist.get(i).put("PO_ITEM_NO", pixStrZero(transfMap.get("POITM").toString(),5));
			datalist.get(i).put("PONO", transfMap.get("PONO"));
			datalist.get(i).put("POITM", pixStrZero(transfMap.get("POITM").toString(),5));
			datalist.get(i).put("WH_NUMBER", mat.getWH_NUMBER());
			datalist.get(i).put("sortNo", transfMap.get("ASNITM"));
			datalist.get(i).put("TRY_QTY", "");
			datalist.get(i).put("BIN_CODE", "");
			datalist.get(i).put("RECEIVER", currentUser.get("FULL_NAME"));
			datalist.get(i).put("VENDOR_MANAGER", "");
			datalist.get(i).put("SHORT_NAME", "");
			datalist.get(i).put("DANGER_FLAG", "0");
			datalist.get(i).put("URGENT_FLAG", "0");
			String DELIVERY_DATE=mat.getETD();
    	/*	if(!"".equals(DELIVERY_DATE)) {
    			Date d=DateUtil.parseYYYYMMDDDate(DELIVERY_DATE);
    			DELIVERY_DATE=DateUtils.format(d, "yyyy-MM-dd");
    		}*/
			if(DELIVERY_DATE.length()>=10) {
				DELIVERY_DATE=DELIVERY_DATE.substring(0,10);
			}
			datalist.get(i).put("DELIVERY_DATE", DELIVERY_DATE);

			if(urgentList.contains(datalist.get(i).get("MATNR"))) {
				datalist.get(i).put("sortNo", 0);//紧急物料置顶
				datalist.get(i).put("URGENT_FLAG", "X");
			}

			for(Map<String,Object> m:dangerList) {
				if(m.get("MATNR").equals(datalist.get(i).get("MATNR"))) {
					datalist.get(i).put("DANGER_FLAG", "X");
					datalist.get(i).put("GOOD_DATES", m.get("GOOD_DATES"));
					datalist.get(i).put("MIN_GOOD_DATES", m.get("MIN_GOOD_DATES"));
					break;
				}
			}

			matnr_str += datalist.get(i).get("MATNR")+",";
			checkMatInfoList.add(mat.getWERKS()+"#*"+datalist.get(i).get("MATNR"));
		}
		//校验物料信息是否已经同步
		List<Map<String,Object>> matInfoList=new ArrayList<Map<String,Object>>();
		matInfoList=wmsInReceiptService.getMatListByMATNR(matnr_str,mat.getWERKS());
		String matErrorStr = "";
		for (String checkMat : checkMatInfoList) {
			boolean b = false;
			for (Map<String, Object> matInfoMap : matInfoList) {
				String matStr = matInfoMap.get("WERKS").toString()+"#*"+matInfoMap.get("MATNR");
				if(checkMat.equals(matStr)) {
					b = true ;
					break;
				}
			}
			if(!b) {
				matErrorStr += checkMat.split("#*")[1];
			}
		}
		if(!"".equals(matErrorStr)) {
			matErrorStr += "信息在收货工厂"+mat.getWERKS()+"不存在，请使用物料数据同步功能同步物料信息！";
			return R.error(matErrorStr);
		}

		/**
		 * 核对送货单对应的采购订单行项目数据是否在WMS采购订单数据表中存在，如果不存在，报错提示：请同步SAP采购订单
		 */
		List<String> poitemSynList=wmsInReceiptService.getPoItemListByPo(poitemList);
		List<String> poErrorList=new ArrayList<String>();
		for(String po:poitemList) {
			if(!poitemSynList.contains(po)) {
				if(!poErrorList.contains(po.split("\\#\\*")[0])) {
					poErrorList.add(po.split("\\#\\*")[0]);
				}
			}
		}
		if(poErrorList.size()>0) {
			return R.error("SCM送货单关联的采购订单信息不存在，请同步SAP采购订单:"+StringUtils.join(poErrorList, ","));
		}
		List<String> poTypeList = wmsInReceiptService.getPoTypeListByPo(poList);
		for (String poTypeStr : poTypeList) {
			String poType = poTypeStr.split("\\#\\*")[1];
			String poNo = poTypeStr.split("\\#\\*")[0];
			if(poType.equals("BYDS")) {
				poErrorList.add(poNo);
			}
		}
		if(poErrorList.size()>0) {
			return R.error("SCM送货单关联的采购订单信息:"+StringUtils.join(poErrorList, ",")+"，不能为STO类型采购订单，请使用STO送货单类型收料！");
		}

		/**
		 * 根据封装好的查询条件（送货单号##送货单行项目号）查询每条行项目已收货数量, 可收货数量
		 */
		Map<String,Object> receiptCount=wmsInReceiptService.getReceiptCount(asnList);
		/**
		 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
		 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
		 */
		Map<String,Object> vendor=wmsInReceiptService.getVendorInfo(params);
		if(null == vendor || null == vendor.get("NAME1")) {
			return R.error("SCM送货单关联的供应商："+mat.getLIFNR()+"信息未同步至WMS系统，请先同步供应商信息！");
		}

		/**
		 * 根据采购订单、订单行项目号从采购订单表中抓取申请人和需求跟踪号
		 */
		Map<String,Object> bendr_afnam=wmsInReceiptService.getBendrAfnam(poitemList);

		/**
		 * 获取物料包规信息
		 */
		List<Map<String,Object>> matPackageList = commonService.getMatPackageList(datalist);

		/**
		 * 根据工厂代码、业务类型获取收货单("01")抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE")==null?DateUtils.format(new Date(), "yyyyMMdd"):params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", mat.getWERKS());
		txtMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME")==null?"01":params.get("BUSINESS_NAME").toString() );
		txtMap.put("JZ_DATE", JZ_DATE);
		String SHORT_NAME=vendor==null?"":(String)vendor.get("SHORT_NAME");
		txtMap.put("LIKTX", SHORT_NAME);
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		txtMap.put("ASNNO", mat.getASNNO());
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);

		//查询物料库存
		List<String> matStock = wmsInReceiptService.getMatStock(datalist);

		Iterator<Map<String,Object>> it =datalist.iterator();
		int i=0;
		while(it.hasNext()) {
			Map<String,Object> m=it.next();

			String MATNR_IT = m.get("MATNR").toString();
			for (String string : matStock) {
				String MATNR_STR = string.split("\\#\\*")[0];
				String STOCK_QTY = string.split("\\#\\*")[1];
				String STOCK_L = string.split("\\#\\*")[2];
				if(MATNR_IT.equals(MATNR_STR)) {
					m.put("STOCK_QTY", STOCK_QTY);
					m.put("STOCK_L", STOCK_L);
					m.put("STOCK_ALL", STOCK_QTY+"/"+STOCK_L);
					break;
				}
			}

			double finishedQty=0;//已收数量
			double RECEIPT_QTY=0;//可收货数量
			String s=(String) receiptCount.get(m.get("ASNNO")+"#*"+m.get("MATNR")+"#*"+m.get("ASNITM"));
			if (s==null) {
				s="0";
			}
			String QTY=m.get("QTY").toString();
			finishedQty=Double.parseDouble(s);
			RECEIPT_QTY=Double.parseDouble(QTY)-finishedQty;
			if(RECEIPT_QTY<=0) {
				//datalist.remove(i); 报空指针
				it.remove();
			}else {
				m.put("FINISHED_QTY", finishedQty);
				m.put("RECEIPT_QTY", RECEIPT_QTY);
				m.put("DELIVERYAMOUNT", RECEIPT_QTY);
				if(vendor!=null&&!vendor.isEmpty()) {
					m.put("SHORT_NAME", vendor.get("SHORT_NAME"));
					m.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
					m.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
				}else {
					m.put("VENDOR_FLAG", "0");
				}

				String ba=(String) bendr_afnam.get(m.get("PO_NO")+"#*"+String.format("%05d", Integer.valueOf(m.get("PO_ITEM_NO").toString()).intValue()));
				String BEDNR="";
				String AFNAM="";
				String SOBKZ="Z";
				if(ba!=null) {
					BEDNR=ba.split("\\#\\*")[0];
					AFNAM=ba.split("\\#\\*")[1];
					SOBKZ=ba.split("\\#\\*")[2];
				}

				m.put("BEDNR", BEDNR);
				m.put("AFNAM", AFNAM);
				m.put("SOBKZ", SOBKZ);
				m.put("HEADER_TXT", txt.get("txtrule"));
				m.put("ITEM_TEXT", txt.get("txtruleitem"));
				m.put("FULL_BOX_QTY", "-1");//默认设置为未维护包规
				for (Map<String, Object> map : matPackageList) {
					String MATNR = m.get("MATNR").toString()+"**"+m.get("LIFNR").toString();
					if(map.get("MATNR").equals(MATNR)) {
						m.put("FULL_BOX_QTY", map.get("FULL_BOX_QTY"));
						if(Double.parseDouble(m.get("SPEC").toString()) != Double.parseDouble(map.get("FULL_BOX_QTY")==null?"0":map.get("FULL_BOX_QTY").toString())) {
							m.put("FULL_BOX_ERROR", MATNR+",");
						}
						break;
					}
				}

			}
			i++;
		}



		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(datalist);

		PageUtils page=new PageUtils(new Page().setRecords(datalist));
		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{qty}")
	public R info(@PathVariable("qty") Long qty){
		WmsInReceiptEntity wmsInReceipt = wmsInReceiptService.selectById(qty);

		return R.ok().put("wmsInReceipt", wmsInReceipt);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody WmsInReceiptEntity wmsInReceipt){
		wmsInReceiptService.insert(wmsInReceipt);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody WmsInReceiptEntity wmsInReceipt){
		ValidatorUtils.validateEntity(wmsInReceipt);
		wmsInReceiptService.updateAllColumnById(wmsInReceipt);//全部更新

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Double[] qtys){
		wmsInReceiptService.deleteBatchIds(Arrays.asList(qtys));

		return R.ok();
	}

	/**
	 * 手动同步生产订单数据@YK180823
	 */
	@RequestMapping("/syncProdordDetail")
	public R syncProdordDetail(@RequestParam Map<String, Object> params){
		System.out.println("-->WmsInReceiptController:syncProdordDetail");
		//System.out.println("-->orderNos = " + params.get("orderNo"));
		params.put("WMS_USER", params.get("USERNAME"));
		Map<String, Object> result = wmsSapRemote.getSapBapiProdordDetailSync(params);
		return R.ok(result.get("MESSAGE").toString());
	}

	/**
	 * 手动同步采购订单数据@YK180824
	 */
	@RequestMapping("/syncPoDetail")
	public R syncPoDetail(@RequestParam Map<String, Object> params){
		System.out.println("-->WmsInReceiptController:syncPoDetail");
		Map<String, Object> result = wmsSapRemote.getSapBapiPoDetailSync(params);
		return R.ok(result.get("MESSAGE").toString());
	}

	/**
	 * 手动同步物料数据@YK180905
	 */
	@RequestMapping("/syncMatDetail")
	public R syncMatDetail(@RequestParam Map<String, Object> params){
		System.out.println("-->WmsInReceiptController:syncMatDetail");
		Map<String, Object> result = wmsSapRemote.getSapBapiMaterialInfoSync(params);
		return R.ok(result.get("MESSAGE").toString());
	}

	/**
	 * 根据工厂和物料号获取SAP物料主数据信息
	 */
	@CrossOrigin
	@RequestMapping("/getSAPMatDetail")
	public R getSAPMatDetail(@RequestParam Map<String, Object> params){
		PageUtils page = commonService.getSAPMatDetail(params);
		return R.ok().put("page", page);
	}
	private void sortMatList(List<Map<String,Object>> datalist) {

		Collections.sort(datalist, new Comparator<Map<String,Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int sortNo=0;
				if(Integer.parseInt(o1.get("sortNo").toString())>Integer.parseInt(o2.get("sortNo").toString())) {
					sortNo= 1;
				}
				if(Integer.parseInt(o1.get("sortNo").toString())<Integer.parseInt(o2.get("sortNo").toString())) {
					sortNo= -1;
				}
				return sortNo;
			}

		});
	}

	/**
	 * 根据工厂和物料号获取SAP物料主数据信息
	 */
	@CrossOrigin
	@RequestMapping("/exportExcel")
	public R exportExcel(@RequestParam Map<String, Object> params){

		PageUtils page = commonService.getSAPMatDetail(params);
		return R.ok().put("page", page);
	}


	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping("/getDeliveryDetailByBarcode")
	public R getDeliveryDetailByBarcode(@RequestBody Map<String, Object> params) {
		//List<Map<String, Object>> skList = new ArrayList<Map<String, Object>>();;
		params.put("BARCODE_NO", params.get("BARCODE"));
		Map<String, Object> deliveryMap = wmsCloudPlatformRemote.getDeliveryDetailByBarcode(params);
		if(deliveryMap.get("code") !=null && "500".equals(deliveryMap.get("code").toString())) {
			return R.error("查询云平台送货单标签信息失败！"+deliveryMap.get("msg")==null?deliveryMap.get("MSG").toString():deliveryMap.get("msg").toString());
		}
		if(deliveryMap.get("STATUS") !=null && "E".equals(deliveryMap.get("STATUS").toString())) {
			return R.error("查询云平台送货单标签信息失败！"+deliveryMap.get("msg")==null?deliveryMap.get("MSG").toString():deliveryMap.get("msg").toString());
		}
		if(deliveryMap.get("DATA")!=null) {
			deliveryMap=(Map<String, Object>) deliveryMap.get("DATA");
		}
		return R.ok().put("data", deliveryMap);
	}

	/**
	 * 获取包装箱信息
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/listSKInfo")
	public R listSKInfo(@RequestBody Map<String, Object> params) {
		List<Map> skList=null;
		/**
		 * 查询SCM送货单信息
		 */
		if(params.get("BUSINESS_NAME").equals("01")) {
			Map<String, Object> scmDeliveryMap =scmDeliveryService.querySCMDelivery(params);
			List<Wmin00skEntity> sklist = (List<Wmin00skEntity>)scmDeliveryMap.get("wmin00sklist");
			skList=JSONObject.parseArray(JSONObject.toJSONString(sklist), Map.class);
		}

		if(params.get("BUSINESS_NAME").equals("78")) {
			skList=new ArrayList<Map>();
			params.put("deliveryNo", params.get("ASNNO"));//送货单号
			Map<String, Object> deliveryMap = wmsCloudPlatformRemote.getDeliveryDetail(params);
			if(deliveryMap.get("code") !=null && "500".equals(deliveryMap.get("code").toString())) {
				return R.error("查询云平台送货单标签信息失败！"+deliveryMap.get("msg")==null?deliveryMap.get("MSG").toString():deliveryMap.get("msg").toString());
			}
			if(deliveryMap.get("STATUS") !=null && "E".equals(deliveryMap.get("STATUS").toString())) {
				return R.error("查询云平台送货单标签信息失败！"+deliveryMap.get("msg")==null?deliveryMap.get("MSG").toString():deliveryMap.get("msg").toString());
			}
			if(deliveryMap.get("DATA")!=null) {
				deliveryMap=(Map<String, Object>) deliveryMap.get("DATA");
			}
			//包装箱信息列表
			List<Map>packList= (List<Map>)deliveryMap.get("PACKING_DETAIL_DATA");
			if(packList == null || packList.size()<=0) {
				return R.error("查询云平台送货单标签信息失败，未获取到标签信息！");
			}
			String WERKS = (String) deliveryMap.get("FACT_NO");		//工厂
			String LGORT= (String) deliveryMap.get("LGORT_NO");	//库位
			String LIFNR = (String) deliveryMap.get("PROVIDER_CODE");		//供应商代码
			String LIKTX = (String) deliveryMap.get("PROVIDER_NAME");		//供应商名称
			String ASNNO = (String) deliveryMap.get("DELIVERY_NO"); //送货单号

			for(Map<String,Object> m:packList) {
				Map<String,Object> _pack=(Map<String,Object>)m.get("OPI_DATA");
				String ASNITM=(String)_pack.get("DELIVERY_ITEM");
				_pack.put("LABEL_NO",m.get("BARCODE_NO"));
				_pack.put("WERKS", WERKS);
				_pack.put("LIFNR", LIFNR);
				_pack.put("LIKTX", LIKTX);
				_pack.put("MATNR",_pack.get("MAT_NO"));
				_pack.put("ASNNO",ASNNO);
				_pack.put("ASNITM",ASNITM);
				_pack.put("MAKTX", _pack.get("MAT_DESC"));
				_pack.put("BOX_SN", _pack.get("CONTAINERCODE"));
				_pack.put("FULL_BOX_QTY", _pack.get("SPEC_QTY"));
				_pack.put("BOX_QTY", _pack.get("PRODUCTION_QTY"));
				_pack.put("BATCH",_pack.get("BYD_BATCH"));
				_pack.put("PO_NO", _pack.get("PO_NO"));
				_pack.put("PO_ITEM_NO", pixStrZero(_pack.get("PO_ITEM").toString(),5));
				String PROD_DATE=(String)_pack.get("PROD_DATE");
        	/*	if(!"".equals(PROD_DATE)) {
        			Date d=DateUtils.stringToDate(PROD_DATE, "yyyyMMdd");
        			PROD_DATE=DateUtils.format(d, "yyyy-MM-dd");
        		}*/
				_pack.put("PRDDT",PROD_DATE );

				String EFFECT_DATE = _pack.get("PRFRQ")==null?"":_pack.get("PRFRQ").toString();
				if(!StringUtils.isEmpty(EFFECT_DATE)) {
					EFFECT_DATE = DateUtils.format(DateUtils.stringToDate(EFFECT_DATE, DateUtils.DATE_PATTERN_POINT),DateUtils.DATE_PATTERN);
				}
				_pack.put("EFFECT_DATE", EFFECT_DATE);

				String VALID_DATE = (String)_pack.get("EXPIRY_DATE");
				if(!"".equals(VALID_DATE)) {
					Date d=DateUtils.stringToDate(VALID_DATE, "yyyyMMdd");
					VALID_DATE=DateUtils.format(d, "yyyy-MM-dd");
				}
				_pack.put("VALID_DATE",VALID_DATE );

				skList.add(_pack);
			}

			//skList=JSONObject.parseArray(JSONObject.toJSONString(sklist), Map.class);
		}

		return R.ok().put("data", skList);
	}

	/**
	 * PO采购订单收货：
	 * 供应商根据采购订单数据，手工开送货单并送货到收料房，收料员收货后，用采购订单在系统做收货，产生事务记录，
	 * 凭证记录（103）（过账到SAP），收料房库存，送检单，创建收货单，包装信息。
	 * 根据工厂配置是否启用核销，如果启用核销还需判断采购订单行项目核销数据表是否有 还需核销数量，
	 * 如果有，需要先做核销的实物收货，冲销核销的虚收数量。工厂配置不启用核销，就不需要检查核销数据。
	 * @param params
	 * @return
	 */
	@RequestMapping("/listPOMat")
	public R listPOMat(@RequestBody Map<String,Object> params) {
		String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
		String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String PO_NO=params.get("PO_NO")==null?"":params.get("PO_NO").toString();//生产订单，多个使用;隔开
		String WH_NUMBER = params.get("WH_NUMBER").toString();
		R r=new R();
		List<Map<String, Object>> allMatList = new ArrayList<Map<String, Object>>();
		//单据号，多个使用;隔开
		String[] PO_NO_arr=PO_NO.split(";");
		//查询供应商
		List<String> lifnrList = new ArrayList<>();
		for(int b=0;b<PO_NO_arr.length;b++){
			List<String> lifnrList1 = new ArrayList<>();
			lifnrList1.add(PO_NO_arr[b]);
			lifnrList.addAll(lifnrList1);
		}
		List<String> matList1 = wmsInReceiptService.getPOLifnr(lifnrList);
		if(matList1.size() >= 2){
		    return r.error("订单必需为同一供应商");
        }
		for(int a=0;a<PO_NO_arr.length;a++) {
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.putAll(params);
			params1.put("PO_NO", PO_NO_arr[a]);

			List<Map<String, Object>> matList = wmsInReceiptService.getPoItems(params1);
			List<Map<String, Object>> matList_temp = new ArrayList<>();
			if (matList == null || matList.size() == 0) {
				return r.error("未查询到有效行项目数据，采购订单" + PO_NO + "不存在（未审批、行项目类别有误） ，请核对是否输入有误，如果无误，请使用采购订单同步功能，同步SAP数据。");
			}
			String WERKS = matList.get(0).get("WERKS").toString();
			String BSART = matList.get(0).get("BSART") == null ? "" : matList.get(0).get("BSART").toString();//采购凭证类型 BSART 采购订单类型 如QH00 前海采购订单
			String LIFNR = matList.get(0).get("LIFNR") == null ? "" : matList.get(0).get("LIFNR").toString();//供应商代码

			Map<String, Object> currentUser = userUtils.getUser();
			params1.put("USERNAME", currentUser.get("USERNAME"));
			params1.put("FULL_NAME", currentUser.get("FULL_NAME"));
			/**
			 * 判断账号是否有对应送货单工厂的操作权限和KNTTP是否为空，如果没有权限或KNTTP不为空则删除。
			 */
			Set<Map<String, Object>> deptList = userUtils.getUserWerks("IN_RECEIPT");
			for (Map<String, Object> dept : deptList) {
				for (int i = 0; i < matList.size(); i++) {
					if (matList.get(i).get("WERKS").toString().equals(dept.get("CODE"))) {
						matList_temp.add(matList.get(i));
						matList.remove(i);
						i--;
					}
				}
			}
			matList = matList_temp;
//			boolean auth = false;
//			for (Map dept : deptList) {
//				if (dept.get("CODE").equals(WERKS)) {
//					auth = true;
//					break;
//				}
//			}
//			if (!auth) {
//				return r.error("您无权操作" + WERKS + "工厂的采购订单!");
//			}
			/**
			 * 如果输入的单号是STO标准采购订单，报错提示：STO采购订单，请用SAP交货单收货 （通过采购订单抬头的采购凭证类型判断，如果是BYDS就报错
			 */
			if ("BYDS".equals(BSART)) {
				return r.error("STO采购订单，请用SAP交货单收货");
			}
			/**
			 * 校验采购订单和工厂对应供应商管理信息表【WMS_C_VENDOR】配置 ：是否上SCM系统 ,字段IS_SCM=X ,报错提示：供应商***已经上SCM系统，请用送货单收货！
			 */
			if ("X".equals(matList.get(0).get("IS_SCM"))) {
				return r.error("供应商" + LIFNR + "已经上SCM系统，请用送货单收货！");
			}
			params1.put("WERKS", WERKS);
			params1.put("LIFNR", LIFNR);
			List<String> asnList = new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）
			List<String> urgentList = wmsInReceiptService.getUrgentMatList(params1);//紧急物料校验列表
			List<Map<String, Object>> dangerList = wmsInReceiptService.getDangerMatList(params1);//危化品物料校验列表

			String matErrorStr = "";
			for (Map<String, Object> _m : matList) {
				asnList.add(_m.get("PO_NO") + "#*" + _m.get("PO_ITEM_NO"));
				_m.put("WH_NUMBER", WH_NUMBER);
				_m.put("sortNo", _m.get("PO_ITEM_NO"));
				_m.put("PRODUCT_DATE", CUR_DATE);
				_m.put("RECEIVER", currentUser.get("FULL_NAME"));
				_m.put("CREATE_DATE", CREATE_DATE);
				_m.put("CREATOR", params1.get("USERNAME"));
				_m.put("DANGER_FLAG", "0");
				if (urgentList.contains(_m.get("MATNR"))) {
					_m.put("sortNo", 0);//紧急物料置顶
					_m.put("URGENT_FLAG", "X");
				}

				for (Map<String, Object> m : dangerList) {
					if (m.get("MATNR").equals(_m.get("MATNR"))) {
						_m.put("DANGER_FLAG", "X");
						_m.put("GOOD_DATES", m.get("GOOD_DATES"));
						_m.put("MIN_GOOD_DATES", m.get("MIN_GOOD_DATES"));
						break;
					}
				}

				/**
				 * 判断物料主数据是否在工厂存在
				 */
				if ((_m.get("MAKTX") == null || "".equals(_m.get("MAKTX").toString()))
						&& _m.get("MATNR") != null && StringUtils.isNotBlank(_m.get("MATNR").toString())) {
					//物料主数据不存在
					matErrorStr += _m.get("MATNR").toString() + "，";
				}

			}

			if (!"".equals(matErrorStr)) {
				matErrorStr += "信息在收货工厂" + WERKS + "不存在，请使用物料数据同步功能同步物料信息！";
				return R.error(matErrorStr);
			}

			/**
			 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
			 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
			 */
			Map<String, Object> vendor = wmsInReceiptService.getVendorInfo(params1);
			if (null == vendor || null == vendor.get("NAME1")) {
				return R.error("采购订单关联的供应商：" + LIFNR + "信息未同步至WMS系统，请先同步供应商信息！");
			}
			/**
			 * 根据工厂代码、业务类型获取SAP采购订单("02")抬头文本与行文本
			 */
			Map<String, String> txtMap = new HashMap<String, String>();
			String JZ_DATE = params1.get("JZ_DATE").toString().replaceAll("-", "");
			txtMap.put("WERKS", WERKS);
			txtMap.put("BUSINESS_NAME", params1.get("BUSINESS_NAME") == null ? "02" : params1.get("BUSINESS_NAME").toString());
			txtMap.put("JZ_DATE", JZ_DATE);
			String SHORT_NAME = vendor == null ? "" : (String) vendor.get("SHORT_NAME");
			txtMap.put("LIKTX", SHORT_NAME);
			txtMap.put("FULL_NAME", params1.get("FULL_NAME").toString());
			txtMap.put("PO_NO", PO_NO);
			Map<String, Object> txt = wmsCTxtService.getRuleTxt(txtMap);

			/**
			 * 根据封装好的查询条件（SAP采购订单号##订单行项目号）查询每条行项目已收货数量, 可收货数量
			 */
			Map<String, Object> rmap = new HashMap<String, Object>();
			rmap.put("BILL_TYPE", "PO_NO");
			rmap.put("asnList", asnList);
			Map<String, Object> receiptCount = wmsInReceiptService.getReceiptCount(rmap);
			//查询物料库存
			List<String> matStock = wmsInReceiptService.getMatStock(matList);
			Iterator<Map<String, Object>> it = matList.iterator();
			int i = 0;
			while (it.hasNext()) {
				Map<String, Object> m = it.next();
				String MATNR_IT = m.get("MATNR").toString();
				for (String string : matStock) {
					String MATNR_STR = string.split("\\#\\*")[0];
					String STOCK_QTY = string.split("\\#\\*")[1];
					String STOCK_L = string.split("\\#\\*")[2];
					if (MATNR_IT.equals(MATNR_STR)) {
						m.put("STOCK_QTY", STOCK_QTY);
						m.put("STOCK_L", STOCK_L);
						m.put("STOCK_ALL", STOCK_QTY + "/" + STOCK_L);
						break;
					}
				}

				double finishedQty = 0;//已收数量
				double RECEIPT_QTY = 0;//可收货数量
				String s = (String) receiptCount.get(m.get("PO_NO") + "#*" + m.get("MATNR") + "#*" + m.get("PO_ITEM_NO"));
				if (s == null) {
					s = "0";
				}
				String MAX_MENGE = m.get("MAX_MENGE").toString();
				finishedQty = Double.parseDouble(s);
				RECEIPT_QTY = Double.parseDouble(MAX_MENGE) - finishedQty;
				if (RECEIPT_QTY <= 0) {
					it.remove();
				} else {
					m.put("FINISHED_QTY", finishedQty);
					m.put("RECEIPT_QTY", RECEIPT_QTY);
					if (m.get("FULL_BOX_QTY") != null && StringUtils.isNotEmpty(m.get("FULL_BOX_QTY").toString())) {
						m.put("FULL_BOX_QTY", m.get("FULL_BOX_QTY"));
						m.put("FULL_BOX_FLAG", "X");
					} else {
						m.put("FULL_BOX_QTY", RECEIPT_QTY);
						m.put("FULL_BOX_FLAG", "0");
					}

					m.put("DELIVERYAMOUNT", RECEIPT_QTY);
					if (vendor != null && !vendor.isEmpty()) {
						m.put("SHORT_NAME", vendor.get("SHORT_NAME"));
						m.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
						m.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
					} else {
						m.put("VENDOR_FLAG", "0");
					}

					m.put("HEADER_TXT", txt.get("txtrule"));
					m.put("ITEM_TEXT", txt.get("txtruleitem"));
				}
				i++;
			}
			/**
			 * 排序，安照紧急物料排最前，然后按照送货单行号排序
			 */
			this.sortMatList(matList);

			allMatList.addAll(matList);
		}
		PageUtils page = new PageUtils(new Page());
		page.setList(allMatList);
		return R.ok().put("page", page);
	}

	/**
	 * SAP交货单收货：
	 * @param params
	 * @return
	 */
	@RequestMapping("/listOutMat")
	public R listOutMat(@RequestBody Map<String,Object> params) {
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
			return R.error("获取SAP交货单数据失败："+dnMap.get("MESSAGE").toString());
		}
		//交货单抬头信息
		Map<String,Object> dnHeaderMap = (Map<String,Object>)dnMap.get("header");
		//交货单行项目信息
		List<Map<String,Object>> dnItemList = (ArrayList<Map<String,Object>>)dnMap.get("item");
/*    	//交货单关联的凭证信息 docList
    	List<Map<String,Object>> dnDocList = (ArrayList<Map<String,Object>>)dnMap.get("docList");*/
		if(dnHeaderMap==null) {
			return R.error("SAP交货单:"+SAP_OUT_NO+"不存在，请检查是否输入有误！");
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
			return R.error("您无权操作"+dnHeaderMap.get("WERKS")+"工厂（收货）的单据!");
		}
		if(dnItemList==null||dnItemList.size()==0) {
			return R.error("SAP交货单:"+SAP_OUT_NO+"行项目不存在，请检查交货单是否输入有误！");
		}
		/*
		 * 交货单是否已删除
		 */
		if(null != dnHeaderMap.get("SPE_LOEKZ") && "X".equals(dnHeaderMap.get("SPE_LOEKZ"))) {
			//删除的交货单
			return R.error("SAP交货单："+SAP_OUT_NO+"已删除！");
		}
		/*
		 * 交货单类型是否正确  VBTYP == J
		 */
		if(null == dnHeaderMap.get("VBTYP") || (!"J".equals(dnHeaderMap.get("VBTYP")) && !"T".equals(dnHeaderMap.get("VBTYP")) ) ) {
			//凭证类别 VBTYP不正确
			return R.error("SAP交货单："+SAP_OUT_NO+"不是正确类型的交货单！");
		}
		if(null != dnHeaderMap.get("VBTYP") && "T".equals(dnHeaderMap.get("VBTYP"))) {
			//凭证类别 VBTYP不正确 退货交货单
			return R.error("SAP交货单："+SAP_OUT_NO+"为退货交货单，不允许收货！");
		}
		/**
		 * 判断SAP交货单状态是否可收货，状态必须为A
		 */
		if("B".equals(dnHeaderMap.get("WBSTK"))) {
			//交货单状态必须为B WBSTK
			return R.error("SAP交货单："+SAP_OUT_NO+"已部分交货，不允许收货！");
		}
		if("C".equals(dnHeaderMap.get("WBSTK"))) {
			//交货单状态必须为C WBSTK
			return R.error("SAP交货单："+SAP_OUT_NO+"已交货，不允许收货！");
		}
		params.put("WERKS", dnHeaderMap.get("WERKS")); //交货单收货工厂
		params.put("LIFNR", "VBYD"+dnHeaderMap.get("VSTEL"));//供应商代码为VBYD拼接交货单抬头的交货工厂字段
		/**
		 * 根据供应商代码获取描述信息
		 */
		Map<String,Object> vendorMap = wmsInReceiptService.getVendorInfo(params);
		if(null == vendorMap || null == vendorMap.get("NAME1")) {
			return R.error("交货单关联的供应商："+"VBYD"+dnHeaderMap.get("VSTEL")+"信息未同步至WMS系统，请先同步供应商信息！");
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
			return R.error(poErrorStr);
		}
		if(!"".equals(matErrorStr)) {
			matErrorStr += "信息在收货工厂"+dnHeaderMap.get("WERKS")+"不存在，请使用物料数据同步功能同步物料信息！";
			return R.error(matErrorStr);
		}

		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(dnItemList);

		PageUtils page=new PageUtils(new Page().setRecords(dnItemList));

		return R.ok().put("page", page);
	}

	/**
	 * 跨工厂采购订单收货
	 * @param params
	 * @return
	 */
	@RequestMapping("/listPOCFMat")
	public R listPOCFMat(@RequestBody Map<String,Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
		String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String PO_NO=params.get("PO_NO").toString();
		String BUSINESS_NAME=(String)params.get("BUSINESS_NAME");
		R r=new R();
		List<Map<String,Object>> matList=wmsInReceiptService.getPoItems(params);
		if(matList==null||matList.size()==0) {
			return r.error("未查询到有效行项目数据，采购订单"+PO_NO+"不存在（未审批、行项目类别有误） ，请核对是否输入有误，如果无误，请使用采购订单同步功能，同步SAP数据。");
		}
		String WERKS_PO=matList.get(0).get("WERKS").toString();//采购工厂
		String WERKS = params.get("WERKS").toString();//收货工厂
		if(WERKS_PO.equals(WERKS)) {
			return r.error("采购订单"+PO_NO+"的采购工厂 "+WERKS_PO+"与收货工厂"+WERKS+"一致，请使用SAP采购订单收料功能！");
		}
		//根据工厂获取工厂所属公司代码
		List<WmsSapPlant> plantList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",WERKS).eq("DEL","0"));
		if(plantList.size() !=1) {
			return r.error("收货工厂"+WERKS+"信息未维护，请先维护SAP工厂信息！");
		}
		List<WmsSapPlant> poWerksList=wmsSapPlantService.selectList(
				new EntityWrapper<WmsSapPlant>().eq("WERKS",WERKS_PO).eq("DEL","0"));
		if(poWerksList.size() !=1) {
			return r.error("PO工厂"+WERKS_PO+"信息未维护，请先维护SAP工厂信息！");
		}
		if(plantList.get(0).getBukrs().equals(poWerksList.get(0).getBukrs()) ||
				plantList.get(0).getBukrs().substring(0, 1).equals(poWerksList.get(0).getBukrs().substring(0, 1))
		) {
			return r.error("采购订单"+PO_NO+"的采购工厂 "+WERKS_PO+"与收货工厂"+WERKS+"所属的法人为同法人，不允许做PO跨工厂收料功能！");
		}

		String BSART=matList.get(0).get("BSART")==null?"":matList.get(0).get("BSART").toString();//采购凭证类型 BSART 采购订单类型 如QH00 前海采购订单
		String LIFNR=matList.get(0).get("LIFNR")==null?"":matList.get(0).get("LIFNR").toString();//供应商代码

		/**
		 * 判断账号是否有对应送货单工厂的操作权限，如果没有提示无权限
		 */

		List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）
		boolean auth=true;
		List<Map<String,String>> werksList=new ArrayList<Map<String,String>>();
		werksList=wmsInReceiptService.getItemAuthWerksList(params);
		/**
		 * 如果输入的单号是STO标准采购订单，报错提示：STO采购订单，请用SAP交货单收货 （通过采购订单抬头的采购凭证类型判断，如果是BYDS就报错
		 */
		if("BYDS".equals(BSART)) {
			return r.error("STO采购订单，请用SAP交货单收货");
		}
		/**
		 * 校验采购订单和工厂对应供应商管理信息表【WMS_C_VENDOR】配置 ：是否上SCM系统 ,字段IS_SCM=X ,报错提示：供应商***已经上SCM系统，请用送货单收货！
		 */
		if("X".equals(matList.get(0).get("IS_SCM"))) {
			return r.error("供应商"+LIFNR+"已经上SCM系统，请用送货单收货！");
		}
		params.put("WERKS", WERKS);
		params.put("LIFNR", LIFNR);
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表
		List<Map<String,Object>> dangerList=wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表
		Iterator it_mat=matList.iterator();

		String matErrorStr = "";

		while(it_mat.hasNext()) {
			Map<String,Object> _m=(Map<String, Object>) it_mat.next();
			boolean remove = true;
			for(Map<String, String> dept:werksList) {
				if(dept.get("PO_ITEM_NO").toString().equals(_m.get("PO_ITEM_NO"))) {
					if(dept.get("AUTH_WERKS").indexOf(WERKS) >=0) {
						remove = false;
						break;
					}
				}

			}
			if(remove) {
				it_mat.remove();
			}else {
				asnList.add(_m.get("PO_NO")+"#*"+_m.get("PO_ITEM_NO"));
				_m.put("sortNo", _m.get("PO_ITEM_NO"));
				_m.put("PRODUCT_DATE", CUR_DATE);
				_m.put("CREATE_DATE", CREATE_DATE);
				_m.put("CREATOR", params.get("USERNAME"));
				_m.put("DANGER_FLAG", "0");
				_m.put("WERKS_PO", _m.get("WERKS"));
				_m.put("WERKS", WERKS);
				if(urgentList.contains(_m.get("MATNR"))) {
					_m.put("sortNo", 0);//紧急物料置顶
					_m.put("URGENT_FLAG", "X");
				}

				for(Map<String,Object> m:dangerList) {
					if(m.get("MATNR").equals(_m.get("MATNR"))) {
						_m.put("DANGER_FLAG", "X");
						_m.put("GOOD_DATES", m.get("GOOD_DATES"));
						break;
					}
				}
			}

			/**
			 * 判断物料主数据是否在工厂存在
			 */
			if(_m.get("MAKTX") ==null || "".equals(_m.get("MAKTX").toString())){
				//物料主数据不存在
				matErrorStr += _m.get("MATNR").toString()+"，";
			}

		}

		if(!"".equals(matErrorStr)) {
			matErrorStr += "信息在收货工厂"+WERKS+"不存在，请使用物料数据同步功能同步物料信息！";
			return R.error(matErrorStr);
		}

		/**
		 * 判断账号是否有对应送货单工厂的操作权限，如果没有提示无权限
		 */
		if(matList.size()==0) {
			return r.error("采购订单 "+PO_NO+" 没有授权"+WERKS+"工厂收货！");
		}
		/**
		 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
		 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
		 */
		Map<String,Object> vendor=wmsInReceiptService.getVendorInfo(params);

		if(null == vendor || null == vendor.get("NAME1")) {
			return R.error("采购订单关联的供应商："+LIFNR+"信息未同步至WMS系统，请先同步供应商信息！");
		}

		/**
		 * 根据工厂代码、业务类型获取SAP采购订单("02")抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", WERKS);
		txtMap.put("BUSINESS_NAME",BUSINESS_NAME );
		txtMap.put("JZ_DATE", JZ_DATE);
		String SHORT_NAME=vendor==null?"":(String)vendor.get("SHORT_NAME");
		txtMap.put("LIKTX", SHORT_NAME);
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		txtMap.put("PO_NO", PO_NO);
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);

		/**
		 * 根据封装好的查询条件（SAP采购订单号##订单行项目号）查询每条行项目已收货数量, 可收货数量
		 */
		Map<String,Object> rmap=new HashMap<String,Object>();
		rmap.put("BILL_TYPE", "PO_NO");
		rmap.put("asnList", asnList);
		Map<String,Object> receiptCount=wmsInReceiptService.getReceiptCount(rmap);
		//查询物料库存
		List<String> matStock = wmsInReceiptService.getMatStock(matList);

		Iterator<Map<String,Object>> it =matList.iterator();
		int i=0;
		while(it.hasNext()) {
			Map<String,Object> m=it.next();

			String MATNR_IT = m.get("MATNR").toString();
			for (String string : matStock) {
				String MATNR_STR = string.split("\\#\\*")[0];
				String STOCK_QTY = string.split("\\#\\*")[1];
				String STOCK_L = string.split("\\#\\*")[2];
				if(MATNR_IT.equals(MATNR_STR)) {
					m.put("STOCK_QTY", STOCK_QTY);
					m.put("STOCK_L", STOCK_L);
					m.put("STOCK_ALL", STOCK_QTY+"/"+STOCK_L);
					break;
				}
			}

			double finishedQty=0;//已收数量
			double RECEIPT_QTY=0;//可收货数量
			String match_str=m.get("PO_NO")+"#*"+m.get("MATNR")+"#*"+m.get("PO_ITEM_NO");
			String s=(String) receiptCount.get(match_str);
			if (s==null) {
				s="0";
			}
			String MAX_MENGE=m.get("MAX_MENGE").toString();
			finishedQty=Double.parseDouble(s);
			RECEIPT_QTY=Double.parseDouble(MAX_MENGE)-finishedQty;
			if(RECEIPT_QTY<=0) {
				it.remove();
			}else {
				m.put("FINISHED_QTY", finishedQty);
				m.put("RECEIPT_QTY", RECEIPT_QTY);
				if(m.get("FULL_BOX_QTY")!=null) {
					m.put("FULL_BOX_QTY", m.get("FULL_BOX_QTY"));
					m.put("FULL_BOX_FLAG", "X");
				}else {
					m.put("FULL_BOX_QTY", RECEIPT_QTY);
					m.put("FULL_BOX_FLAG", "0");
				}

				m.put("DELIVERYAMOUNT", RECEIPT_QTY);
				if(vendor!=null&&!vendor.isEmpty()) {
					m.put("SHORT_NAME", vendor.get("SHORT_NAME"));
					m.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
					m.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
				}else {
					m.put("VENDOR_FLAG", "0");
				}

				m.put("HEADER_TXT", txt.get("txtrule"));
				m.put("ITEM_TEXT", txt.get("txtruleitem"));
			}
			i++;
		}
		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(matList);

		PageUtils page=new PageUtils(new Page().setRecords(matList));

		return R.ok().put("page", page);
	}

	/**
	 * 303调拨收货
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/list303Mat")
	public R list303Mat(@RequestBody Map<String, Object> params) {

		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
		String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String SAP_MATDOC_NO = params.get("SAP_MATDOC_NO").toString();//303调拨单号
		String PZ_YEAR = params.get("PZ_YEAR").toString();//凭证年份;
		List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）
		/**
		 * 通过调用SAP服务接口，读取SAP物料凭证数据
		 */
		Map<String,Object> toMap = wmsSapRemote.getSapBapiGoodsmvtDetail(SAP_MATDOC_NO, PZ_YEAR);
		if(null != toMap.get("CODE") && "-1".equals(toMap.get("CODE").toString())) {
			//获取SAP物料凭证数据失败
			return R.error("获取SAP物料凭证数据失败："+toMap.get("MESSAGE").toString());
		}
		//SAP物料凭证抬头信息
		Map<String,Object> toHeaderMap = (Map<String,Object>)toMap.get("GOODSMVT_HEADER");
		//SAP物料凭证行项目信息
		List<Map<String,Object>> toItemList = (ArrayList<Map<String,Object>>)toMap.get("GOODSMVT_ITEMS");
		if(toItemList==null ||toItemList.size()<=0) {
			return R.error("SAP物料凭证:"+SAP_MATDOC_NO+"不存在，请检查是否输入有误！");
		}

		/*
		 * 判断物料凭证类型是否为303凭证 MOVE_TYPE = 303
		 */
		if(null == toItemList.get(0).get("MOVE_TYPE") || (!"303".equals(toItemList.get(0).get("MOVE_TYPE")) ) ) {
			//凭证类型不正确
			return R.error("SAP物料凭证:"+SAP_MATDOC_NO+"不是303物料凭证，请检查是否输入有误！");
		}

		//删除303凭证里自动创建的行项目
		for (int i=0;i<toItemList.size();i++) {
			Map<String, Object> map = toItemList.get(i);
			if(map.get("X_AUTO_CRE")!=null && "X".equals(map.get("X_AUTO_CRE"))) {
				//给上一行设置收货库位信息
				Map<String, Object> pmap = toItemList.get(i-1);
				pmap.put("MOVE_STLOC", map.get("MOVE_STLOC"));
				//自动创建的行项目，删除
				toItemList.remove(i);
			}
		}

		String WERKS = toItemList.get(0).get("MOVE_PLANT").toString();//调拨收货工厂
		if(!params.get("WERKS").equals(WERKS)) {
			return R.error("收货工厂"+WERKS+"与抬头工厂"+params.get("WERKS")+"不匹配!");
		}
		/*
		 * 判断账号是否有对应收货工厂的操作权限，如果没有提示无权限 WERKS
		 */
		Set<Map<String,Object>> deptList = userUtils.getUserWerks("IN_RECEIPT");
		boolean auth=false;
		for(Map dept:deptList) {
			if(dept.get("CODE").equals(WERKS)) {
				auth=true;
				break;
			}
		}
		if(!auth) {
			return R.error("您无权操作"+WERKS+"工厂（收货）的单据!");
		}

		params.put("WERKS", WERKS); //303调拨收货工厂
		params.put("LIFNR", "VBYD"+toItemList.get(0).get("PLANT"));//供应商代码为VBYD拼接PLANT代码
		/**
		 * 根据供应商代码获取描述信息
		 */
		Map<String,Object> vendorMap = wmsAccountReceiptHxDnService.getSapVendorByNo("VBYD"+toItemList.get(0).get("PLANT"));
		if(null == vendorMap) {
			return R.error("交货单关联的供应商："+"VBYD"+toItemList.get(0).get("PLANT")+"信息未同步至WMS系统，请先同步供应商信息！");
		}
		/**
		 * 查询是否存在危化品
		 */
		List<Map<String,Object>> dangerList = wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表

		/**
		 * 根据工厂代码、业务类型获取SAP303调拨收料(V)("24")抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", WERKS);
		txtMap.put("BUSINESS_NAME",params.get("BUSINESS_NAME")==null?"06":params.get("BUSINESS_NAME").toString() );
		txtMap.put("JZ_DATE", JZ_DATE);
		txtMap.put("currentUser", params.get("FULL_NAME").toString());
		txtMap.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
		txtMap.put("LIKTX", vendorMap.get("NAME1").toString());
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);

		/**
		 * 根据工厂代码、系统时间查询紧急物料列表
		 */
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表
		//获取仓库配置信息
		Map<String, Object>  whSettingMap = commonService.getPlantSetting(params.get("WH_NUMBER").toString());
		/**
		 * 根据303物料凭证行项目，查询关联的核销信息
		 */
		toItemList = wmsAccountReceiptHxToService.getHxToListByMatDocItem(toItemList);
		String matErrorStr = "";
		for (Map<String, Object> toItemMap : toItemList) {

			toItemMap.put("BARCODE_FLAG", whSettingMap==null?"0":whSettingMap.get("BARCODE_FLAG"));
			toItemMap.put("F_WERKS", toItemMap.get("PLANT"));//发货工厂
			toItemMap.put("F_LGORT", toItemMap.get("STGE_LOC"));//发货库位
			toItemMap.put("F_BATCH", toItemMap.get("BATCH"));//发货批次
			toItemMap.put("WERKS", WERKS);
			toItemMap.put("LGORT", toItemMap.get("MOVE_STLOC"));//收货库位
			toItemMap.put("BATCH", toItemMap.get("MOVE_BATCH"));//收货批次
			toItemMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
			toItemMap.put("UNIT", toItemMap.get("ENTRY_UOM"));//交货单位
			toItemMap.put("RECEIVER", "");
			toItemMap.put("DANGER_FLAG", "0");
			toItemMap.put("LIFNR", vendorMap.get("LIFNR"));
			toItemMap.put("LIKTX", vendorMap.get("NAME1"));
			toItemMap.put("SAP_MATDOC_NO", toItemMap.get("MAT_DOC"));
			toItemMap.put("SAP_MATDOC_ITEM_NO", toItemMap.get("MATDOC_ITM"));
			toItemMap.put("BEDNR", vendorMap.get("ITEM_TEXT"));
			toItemMap.put("AFNAM", vendorMap.get("GR_RCPT"));
			toItemMap.put("sortNo", toItemMap.get("MATDOC_ITM"));
			toItemMap.put("PRODUCT_DATE", CUR_DATE);
			toItemMap.put("CREATE_DATE", CREATE_DATE);
			toItemMap.put("PRODUCT_DATE", CUR_DATE);
			toItemMap.put("CREATOR", params.get("USERNAME").toString());

			asnList.add(toItemMap.get("SAP_MATDOC_NO")+"#*"+toItemMap.get("SAP_MATDOC_ITEM_NO"));

			for(Map<String,Object> m:dangerList) {
				if(m.get("MATNR").equals(toItemMap.get("MATNR"))) {
					toItemMap.put("DANGER_FLAG", "X");
					toItemMap.put("GOOD_DATES", m.get("GOOD_DATES"));
					toItemMap.put("MIN_GOOD_DATES", m.get("MIN_GOOD_DATES"));
					break;
				}
			}
			if(urgentList.contains(toItemMap.get("MATNR"))) {
				toItemMap.put("sortNo", 0);//紧急物料置顶
				toItemMap.put("URGENT_FLAG", "X");
			}
			String SOBKZ="Z";
			toItemMap.put("SOBKZ", SOBKZ);
			toItemMap.put("HEADER_TXT", txt.get("txtrule"));
			toItemMap.put("ITEM_TEXT", txt.get("txtruleitem"));
			/**
			 * 判断物料主数据是否在工厂存在
			 */
			if(toItemMap.get("MAKTX") ==null){
				//物料主数据不存在
				matErrorStr += toItemMap.get("MATNR").toString()+"，";
			}
		}
		if(!"".equals(matErrorStr)) {
			matErrorStr += "信息在收货工厂"+WERKS+"不存在，请使用物料信息同步功能同步物料信息！";
			return R.error(matErrorStr);
		}
		/**
		 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
		 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
		 */
		Map<String,Object> vendor=wmsInReceiptService.getVendorInfo(params);
		if(null == vendor || null == vendor.get("NAME1")) {
			return R.error("供应商："+"VBYD"+toItemList.get(0).get("PLANT")+"信息未同步至WMS系统，请先同步供应商信息！");
		}

		/**
		 * 根据封装好的查询条件（SAP采购订单号##订单行项目号）查询每条行项目已收货数量, 可收货数量
		 */
		Map<String,Object> rmap=new HashMap<String,Object>();
		rmap.put("BILL_TYPE", "SAP_MATDOC_NO");
		rmap.put("asnList", asnList);
		Map<String,Object> receiptCount = wmsInReceiptService.getReceiptCount(rmap);
		//获取包规信息
		List<Map<String,Object>> matPackageList = commonService.getMatPackageList(toItemList);

		/**
		 * 启用了条码的仓库号，需判断是否已存在标签
		 */
		List<Map<String, Object>> matLabelNoList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> labelNoList = new ArrayList<Map<String, Object>>();
		//if("X".equals(whSettingMap.get("BARCODE_FLAG"))) {
		//获取物料凭证关联的标签信息
		matLabelNoList = wmsInReceiptService.getLabelNoByMatDocNo(params);

		List<Map<String, Object>> allLabelNoList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : matLabelNoList) {
			List<Map<String, Object>> allLabelNoList_item = (List<Map<String, Object>>)map.get("allLabelNoList_item");
			allLabelNoList.addAll(allLabelNoList_item);
		}

		if(allLabelNoList !=null && allLabelNoList.size()>0) {
			labelNoList = wmsInReceiptService.getLabelInfoByLabelNo(allLabelNoList);
		}
		//}

		//查询物料库存
		List<String> matStock = wmsInReceiptService.getMatStock(toItemList);

		Iterator<Map<String,Object>> it = toItemList.iterator();
		while(it.hasNext()) {
			Map<String,Object> m=it.next();
			String MATNR_IT = m.get("MATNR").toString();
			for (String string : matStock) {
				String MATNR_STR = string.split("\\#\\*")[0];
				String STOCK_QTY = string.split("\\#\\*")[1];
				String STOCK_L = string.split("\\#\\*")[2];
				if(MATNR_IT.equals(MATNR_STR)) {
					m.put("STOCK_QTY", STOCK_QTY);
					m.put("STOCK_L", STOCK_L);
					m.put("STOCK_ALL", STOCK_QTY+"/"+STOCK_L);
					break;
				}
			}

			double finishedQty=0;//已收数量
			double RECEIPT_QTY=0;//可收货数量
			String match_str=m.get("SAP_MATDOC_NO")+"#*"+m.get("MATNR")+"#*"+m.get("SAP_MATDOC_ITEM_NO");
			String s=(String) receiptCount.get(match_str);
			if (s==null) {
				s="0";
			}
			String MAX_MENGE=m.get("ENTRY_QNT").toString();
			finishedQty=Double.parseDouble(s);
			RECEIPT_QTY=Double.parseDouble(MAX_MENGE)-finishedQty;
			if(RECEIPT_QTY<=0) {
				it.remove();
			}else {
				m.put("FINISHED_QTY", finishedQty);
				m.put("RECEIPT_QTY", RECEIPT_QTY);

				m.put("DELIVERYAMOUNT", RECEIPT_QTY);
				if(vendor!=null&&!vendor.isEmpty()) {
					m.put("SHORT_NAME", vendor.get("SHORT_NAME"));
					m.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
					m.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
				}else {
					m.put("VENDOR_FLAG", "0");
				}

				m.put("HEADER_TXT", txt.get("txtrule"));
				m.put("ITEM_TEXT", txt.get("txtruleitem"));

				//包规
				for (Map<String, Object> map : matPackageList) {
					String MATNR = m.get("MATNR").toString()+"**"+m.get("LIFNR").toString();
					if(map.get("MATNR").equals(MATNR)) {
						m.put("FULL_BOX_QTY", map.get("FULL_BOX_QTY"));
						m.put("FULL_BOX_FLAG", 'X');
						break;
					}
				}
				if(m.get("FULL_BOX_QTY")==null) {
					m.put("FULL_BOX_FLAG", '0');
					m.put("FULL_BOX_QTY", RECEIPT_QTY);
				}

				//仓库启用了条码
				//if("X".equals(whSettingMap.get("BARCODE_FLAG"))) {
				List<Map<String,Object>> ITEM_LABEL_LIST = null;
				String LABEL_NO_STR = "";
				for (Map<String, Object> map : matLabelNoList) {
					if(map.get("MATDOC_ITM").equals(m.get("MATDOC_ITM"))) {
						//匹配，获取条码字符串
						LABEL_NO_STR = map.get("LABEL_NO")==null?"":map.get("LABEL_NO").toString();
					}
				}
				//统计标签物料总数
				Double labelQty_total = 0.00;
				if(!LABEL_NO_STR.equals("")) {
					ITEM_LABEL_LIST = new ArrayList<Map<String,Object>>();
					for (Map<String, Object> map : labelNoList) {
						//判断标签状态
						m.put("BATCH", map.get("BATCH"));
						m.put("F_BATCH", map.get("BATCH"));
						String LABEL_STATUS = map.get("LABEL_STATUS").toString();
						//启用了条码，校验条码状态
						if("X".equals(whSettingMap.get("BARCODE_FLAG")) && !LABEL_STATUS.equals("00")) {
							//关联的条码状态不是已创建状态
							matErrorStr += m.get("MATNR").toString()+"("+m.get("MATDOC_ITM")+")存在关联的标签，但是状态不是已创建！";
							break;
						}
						if( ( ("X".equals(whSettingMap.get("BARCODE_FLAG")) && LABEL_STATUS.equals("00") ) || "0".equals(whSettingMap.get("BARCODE_FLAG")) )
								&& LABEL_NO_STR.contains(map.get("LABEL_NO").toString())) {
							//创建状态
							labelQty_total += Double.valueOf(map.get("BOX_QTY").toString());

							ITEM_LABEL_LIST.add(map);
						}
					}
				}
				m.put("LABEL_QTY", labelQty_total);
/*                	if(labelQty_total!=null && labelQty_total !=RECEIPT_QTY) {
                		//关联的条码存在，但是和凭证行项目可收货数量不相等，不允许收货
                		matErrorStr += m.get("MATNR").toString()+"("+m.get("MATDOC_ITM")+")存在关联的标签，但是可收货数量不等于所有状态为已创建的标签";
                		break;
                	}*/
				if(ITEM_LABEL_LIST !=null) {
					m.put("LABEL_NO", LABEL_NO_STR);
					m.put("ITEM_LABEL_LIST", JSONObject.toJSONString(ITEM_LABEL_LIST));
				}
			}

			//}

		}


		if(!"".equals(matErrorStr)) {
			return R.error(matErrorStr);
		}
		/**
		 * 判断凭证单是否完成收货
		 */
		if(toItemList.size()==0){
			return R.error("303凭证"+SAP_MATDOC_NO+"已收货");
		}
		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(toItemList);

		PageUtils page=new PageUtils(new Page().setRecords(toItemList));

		return R.ok().put("page", page);

	}
	/**
	 * 303调拨收货
	 * @param params
	 * @return
	 */
	@RequestMapping("/list303AMat")
	public R  list303AMat(@RequestBody Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
		String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String SAP_MATDOC_NO = params.get("SAP_MATDOC_NO").toString();//303调拨单号
		String WERKS = params.get("WERKS").toString();//收货工厂
		List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）
		/**
		 * 通过工厂、仓库号、303凭证从SAP303调拨单核销信息表--WMS_HX_TO，读取物料凭证数据
		 */

		List<Map<String,Object>> toItemList = wmsInReceiptService.getHXMatList(params);

		if(toItemList==null ||toItemList.size()<=0) {
			return R.error("凭证:"+SAP_MATDOC_NO+" 工厂："+WERKS+" 仓库号："+params.get("WH_NUMBER")+" 不存在核销数据！");
		}

		/*
		 * 判断调拨收货工厂是否启用核销业务，否则提示错误
		 */
		params.put("WERKS", WERKS);
		List<WmsCPlant> cPlantList = (List<WmsCPlant>)wmsCPlantService.queryPage(params).getList();
		if(cPlantList==null || cPlantList.size()!=1 || !"X".equals(cPlantList.get(0).getHxFlag())) {
			return R.error("收货工厂"+WERKS+"未启用核销业务，请检查工厂配置!");
		}
		String LIFNR="VBYD"+toItemList.get(0).get("F_WERKS");
		params.put("LIFNR", LIFNR);//供应商代码为VBYD拼接303凭证发货工厂
		/**
		 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
		 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
		 */
		Map<String,Object> vendor=wmsInReceiptService.getVendorInfo(params);
		if(null == vendor || null == vendor.get("NAME1")) {
			return R.error("凭证关联的供应商："+LIFNR+"信息未同步至WMS系统，请先同步供应商信息！");
		}

		/**
		 * 查询是否存在危化品
		 */
		List<Map<String,Object>> dangerList = wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表

		/**
		 * 根据工厂代码、业务类型获取303调拨收料(A)抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", WERKS);
		txtMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME")==null?"09":params.get("BUSINESS_NAME").toString());
		txtMap.put("JZ_DATE", JZ_DATE);
		txtMap.put("currentUser", params.get("USERNAME").toString());
		txtMap.put("SAP_MATDOC_NO", SAP_MATDOC_NO);
		txtMap.put("LIKTX", vendor.get("NAME1").toString());
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
		/**
		 * 根据工厂代码、系统时间查询紧急物料列表
		 */
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表


		String matErrorStr = "";
		for (Map<String, Object> toItemMap : toItemList) {
			toItemMap.put("F_WERKS", toItemMap.get("F_WERKS"));//发货工厂
			toItemMap.put("WH_NUMBER", toItemMap.get("WH_NUMBER"));//接收仓库号
			toItemMap.put("F_BATCH", toItemMap.get("BATCH"));//发货批次
			toItemMap.put("WERKS", WERKS);
			toItemMap.put("BATCH", toItemMap.get("MOVE_BATCH"));//收货批次
			toItemMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
			toItemMap.put("UNIT", toItemMap.get("ENTRY_UOM"));//交货单位
			toItemMap.put("RECEIVER", "");
			toItemMap.put("DANGER_FLAG", "0");
			toItemMap.put("LIKTX", vendor.get("NAME1"));
			toItemMap.put("SAP_MATDOC_NO", toItemMap.get("SAP_MATDOC_NO"));
			toItemMap.put("SAP_MATDOC_ITEM_NO", toItemMap.get("SAP_MATDOC_ITEM_NO"));
			toItemMap.put("BEDNR", "");
			toItemMap.put("sortNo", toItemMap.get("SAP_MATDOC_ITEM_NO"));
			toItemMap.put("PRODUCT_DATE", CUR_DATE);
			toItemMap.put("CREATE_DATE", CREATE_DATE);
			toItemMap.put("PRODUCT_DATE", CUR_DATE);
			toItemMap.put("CREATOR", params.get("USERNAME").toString());
			toItemMap.put("SHORT_NAME", vendor.get("SHORT_NAME"));
			toItemMap.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
			toItemMap.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
			toItemMap.put("RECEIPT_QTY", toItemMap.get("HX_QTY_XS"));
			toItemMap.put("FULL_BOX_QTY", toItemMap.get("HX_QTY_XS"));

			for(Map<String,Object> m:dangerList) {
				if(m.get("MATNR").equals(toItemMap.get("MATNR"))) {
					toItemMap.put("DANGER_FLAG", "X");
					toItemMap.put("GOOD_DATES", m.get("GOOD_DATES"));
					break;
				}
			}
			if(urgentList.contains(toItemMap.get("MATNR"))) {
				toItemMap.put("sortNo", 0);//紧急物料置顶
				toItemMap.put("URGENT_FLAG", "X");
			}
			String SOBKZ="Z";
			toItemMap.put("SOBKZ", SOBKZ);
			toItemMap.put("HEADER_TXT", txt.get("txtrule"));
			toItemMap.put("ITEM_TEXT", txt.get("txtruleitem"));

		}

		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(toItemList);

		PageUtils page=new PageUtils(new Page().setRecords(toItemList));

		return R.ok().put("page", page);

	}

	/**
	 * SAP采购订单收料（A）
	 * @param params
	 * @return
	 */
	@RequestMapping("/listPOAMat")
	public R  listPOAMat(@RequestBody Map<String, Object> params) {
		String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
		String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String PO_NO = params.get("PO_NO").toString();//SAP采购订单
		String WH_NUMBER = params.get("WH_NUMBER").toString();//仓库号
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		/**
		 * 通过仓库号、采购订单从采购订单单核销信息表--WMS_HX_PO，读取物料凭证数据
		 */
		List<Map<String,Object>> toItemList = wmsInReceiptService.getHXPOMatList(params);

		if(toItemList==null ||toItemList.size()<=0) {
			return R.error("采购订单:"+PO_NO+" 仓库号："+WH_NUMBER+" 不存在核销数据！");
		}

		String WERKS=toItemList.get(0).get("WERKS").toString();

		/**
		 * 判断账号是否有对应送货单工厂的操作权限，如果没有提示无权限
		 */
		Set<Map<String,Object>> deptList =userUtils.getUserWerks("IN_RECEIPT");
		List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）
		boolean auth=false;
		for(Map dept:deptList) {
			if(dept.get("CODE").equals(WERKS)) {
				auth=true;
				break;
			}
		}
		if(!auth) {
			return R.error("您无权操作"+WERKS+"工厂的采购订单!");
		}
		/*
		 * 判断调拨收货工厂是否启用核销业务，否则提示错误
		 */
		params.put("WERKS", WERKS);
		List<WmsCPlant> cPlantList = (List<WmsCPlant>)wmsCPlantService.queryPage(params).getList();
		if(cPlantList==null || cPlantList.size()!=1 || !"X".equals(cPlantList.get(0).getHxFlag())) {
			return R.error("收货工厂"+WERKS+"未启用核销业务，请检查工厂配置!");
		}
		String LIFNR=toItemList.get(0).get("LIFNR").toString();
		params.put("LIFNR", LIFNR);//供应商代码
		/**
		 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
		 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
		 */
		Map<String,Object> vendor=wmsInReceiptService.getVendorInfo(params);
		if(null == vendor || null == vendor.get("NAME1")) {
			return R.error("凭证关联的供应商："+LIFNR+"信息未同步至WMS系统，请先同步供应商信息！");
		}

		/**
		 * 查询是否存在危化品
		 */
		List<Map<String,Object>> dangerList = wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表

		/**
		 * 根据工厂代码、业务类型获取采购订单收料(A)抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", WERKS);
		txtMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME")==null?"07":params.get("BUSINESS_NAME").toString());
		txtMap.put("JZ_DATE", JZ_DATE);
		txtMap.put("currentUser", params.get("FULL_NAME").toString());
		txtMap.put("PO_NO", PO_NO);
		txtMap.put("LIKTX", vendor.get("NAME1").toString());
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
		/**
		 * 根据工厂代码、系统时间查询紧急物料列表
		 */
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表


		String matErrorStr = "";
		for (Map<String, Object> toItemMap : toItemList) {
			toItemMap.put("WH_NUMBER", toItemMap.get("WH_NUMBER"));//接收仓库号
			toItemMap.put("WERKS", WERKS);
			toItemMap.put("RECEIVER", "");
			toItemMap.put("DANGER_FLAG", "0");
			toItemMap.put("LIKTX", vendor.get("NAME1"));
			toItemMap.put("sortNo", toItemMap.get("PO_ITEM_NO"));
			toItemMap.put("PRODUCT_DATE", CUR_DATE);
			toItemMap.put("CREATE_DATE", CREATE_DATE);
			toItemMap.put("CREATOR", params.get("USERNAME").toString());
			toItemMap.put("SHORT_NAME", vendor.get("SHORT_NAME"));
			toItemMap.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
			toItemMap.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
			toItemMap.put("RECEIPT_QTY", toItemMap.get("HX_QTY"));
			toItemMap.put("FULL_BOX_QTY", toItemMap.get("HX_QTY"));

			for(Map<String,Object> m:dangerList) {
				if(m.get("MATNR").equals(toItemMap.get("MATNR"))) {
					toItemMap.put("DANGER_FLAG", "X");
					toItemMap.put("GOOD_DATES", m.get("GOOD_DATES"));
					break;
				}
			}
			if(urgentList.contains(toItemMap.get("MATNR"))) {
				toItemMap.put("sortNo", 0);//紧急物料置顶
				toItemMap.put("URGENT_FLAG", "X");
			}
			toItemMap.put("HEADER_TXT", txt.get("txtrule"));
			toItemMap.put("ITEM_TEXT", txt.get("txtruleitem"));

		}

		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(toItemList);

		PageUtils page=new PageUtils(new Page().setRecords(toItemList));

		return R.ok().put("page", page);

	}

	/**
	 * SAP采购订单收料（A）
	 * @param params
	 * @return
	 */
	@RequestMapping("/listOutAMat")
	public R  listOutAMat(@RequestBody Map<String, Object> params) {
		String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
		String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
		String SAP_OUT_NO = params.get("SAP_OUT_NO").toString();//SAP采购订单
		String WH_NUMBER = params.get("WH_NUMBER").toString();//仓库号

		/**
		 * 通过仓库号、交货单号从交货单核销信息表--WMS_HX_DN，读取物料凭证数据
		 */
		List<Map<String,Object>> toItemList = wmsInReceiptService.getHXDNMatList(params);

		if(toItemList==null ||toItemList.size()<=0) {
			return R.error("交货单:"+SAP_OUT_NO+" 仓库号："+WH_NUMBER+" 不存在核销数据！");
		}

		String WERKS=toItemList.get(0).get("WERKS").toString();

		/**
		 * 判断账号是否有对应送货单工厂的操作权限，如果没有提示无权限
		 */
		List<Map<String, Object>> deptList = (List<Map<String, Object>>)params.get("deptList");
		boolean auth=false;
		for(Map dept:deptList) {
			if(dept.get("CODE").equals(WERKS)) {
				auth=true;
				break;
			}
		}
		if(!auth) {
			return R.error("您无权操作"+WERKS+"工厂的采购订单!");
		}
		/*
		 * 判断调拨收货工厂是否启用核销业务，否则提示错误
		 */
		params.put("WERKS", WERKS);
		List<WmsCPlant> cPlantList = (List<WmsCPlant>)wmsCPlantService.queryPage(params).getList();
		if(cPlantList==null || cPlantList.size()!=1 || !"X".equals(cPlantList.get(0).getHxFlag())) {
			return R.error("收货工厂"+WERKS+"未启用核销业务，请检查工厂配置!");
		}
		String LIFNR=toItemList.get(0).get("LIFNR").toString();
		params.put("LIFNR", LIFNR);//供应商代码
		/**
		 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
		 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
		 */
		Map<String,Object> vendor=wmsInReceiptService.getVendorInfo(params);
		if(null == vendor || null == vendor.get("NAME1")) {
			return R.error("凭证关联的供应商："+LIFNR+"信息未同步至WMS系统，请先同步供应商信息！");
		}

		/**
		 * 查询是否存在危化品
		 */
		List<Map<String,Object>> dangerList = wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表

		/**
		 * 根据工厂代码、业务类型获取采购订单收料(A)抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", WERKS);
		txtMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME")==null?"08":params.get("BUSINESS_NAME").toString());
		txtMap.put("JZ_DATE", JZ_DATE);
		txtMap.put("currentUser", params.get("FULL_NAME").toString());
		txtMap.put("SAP_OUT_NO", SAP_OUT_NO);
		txtMap.put("LIKTX", vendor.get("NAME1").toString());
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
		/**
		 * 根据工厂代码、系统时间查询紧急物料列表
		 */
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表


		String matErrorStr = "";
		for (Map<String, Object> toItemMap : toItemList) {
			toItemMap.put("WH_NUMBER", toItemMap.get("WH_NUMBER"));//接收仓库号
			toItemMap.put("WERKS", WERKS);
			toItemMap.put("RECEIVER", "");
			toItemMap.put("DANGER_FLAG", "0");
			toItemMap.put("LIKTX", vendor.get("NAME1"));
			toItemMap.put("sortNo", toItemMap.get("PO_ITEM_NO"));
			toItemMap.put("PRODUCT_DATE", CUR_DATE);
			toItemMap.put("CREATE_DATE", CREATE_DATE);
			toItemMap.put("CREATOR", params.get("USERNAME").toString());
			toItemMap.put("SHORT_NAME", vendor.get("SHORT_NAME"));
			toItemMap.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
			toItemMap.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
			toItemMap.put("RECEIPT_QTY", toItemMap.get("HX_QTY_XS"));
			toItemMap.put("FULL_BOX_QTY", toItemMap.get("HX_QTY_XS"));

			for(Map<String,Object> m:dangerList) {
				if(m.get("MATNR").equals(toItemMap.get("MATNR"))) {
					toItemMap.put("DANGER_FLAG", "X");
					toItemMap.put("GOOD_DATES", m.get("GOOD_DATES"));
					break;
				}
			}
			if(urgentList.contains(toItemMap.get("MATNR"))) {
				toItemMap.put("sortNo", 0);//紧急物料置顶
				toItemMap.put("URGENT_FLAG", "X");
			}
			toItemMap.put("HEADER_TXT", txt.get("txtrule"));
			toItemMap.put("ITEM_TEXT", txt.get("txtruleitem"));

		}

		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(toItemList);

		PageUtils page=new PageUtils(new Page().setRecords(toItemList));

		return R.ok().put("page", page);

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
		String skListStr=params.get("skList").toString();
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
			if("01".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置SCM送货单收料的工厂质检标识主数据！");
			}
			if("04".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置PO跨工厂收料的工厂质检标识主数据！");
			}
			if("02".equals(BUSINESS_NAME)){
				return R.error("收货工厂"+WERKS+"未配置SAP采购订单收料的工厂质检标识主数据！");
			}
			if("03".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置SAP交货单收料的工厂质检标识主数据！");
			}
			if("05".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置无PO收料的工厂质检标识主数据！");
			}
			if("06".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置303调拨收料的工厂质检标识主数据！");
			}
			if("07".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置采购订单（A）收料的工厂质检标识主数据！");
			}
			if("08".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置SAP交货单（A）收料的工厂质检标识主数据！");
			}
			if("78".equals(BUSINESS_NAME)) {
				return R.error("收货工厂"+WERKS+"未配置云平台送货单收料的工厂质检标识主数据！");
			}

		}
		params.put("TEST_FLAG", TEST_FLAG);

		JSONObject.parseArray(matListStr, Map.class).forEach(m->{
			m=(Map<String,Object>)m;
			m.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			m.put("CREATE_DATE", CREATE_DATE);
			m.put("QTY_SAP", m.get("RECEIPT_QTY"));

			if(!"01".equals(BUSINESS_NAME)&& !"78".equals(BUSINESS_NAME)) {
				int BOX_COUNT = (int)Math.ceil(Double.valueOf(m.get("RECEIPT_QTY").toString())/Double.valueOf(m.get("FULL_BOX_QTY").toString()));
				m.put("BOX_COUNT", BOX_COUNT);
			}
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
			if("01".equals(BUSINESS_NAME) || "78".equals(BUSINESS_NAME)) {//SCM送货单确认收货、云平台送货单确认收货
				List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
				if(skListStr!=null) {
					JSONObject.parseArray(skListStr, Map.class).forEach(sk->{
						sk=(Map<String,Object>) sk;
						list.add(sk);
					});
				}
				Iterator it=list.iterator();
				while (it.hasNext()) {
					Map<String,Object> m=(Map <String,Object>)it.next();
					for(Map<String,Object> _m:matList) {
						if(_m.get("ASNNO").equals(m.get("ASNNO"))&&_m.get("ASNITM").equals(m.get("ASNITM"))
								&&_m.get("MATNR").equals(m.get("MATNR"))
						) {
							m.put("F_BATCH", m.get("BATCH"));
							if("78".equals(BUSINESS_NAME)) {
								if(StringUtils.isEmpty( m.get("EFFECT_DATE")==null?"":m.get("EFFECT_DATE").toString()) )
									m.put("EFFECT_DATE", _m.get("EFFECT_DATE"));
							}
							skList.add(m);
						}
					}
				}
				params.put("skList", skList);
				if("01".equals(BUSINESS_NAME)) {
					wmsInReceiptService.setMatBatch(params, matList);
					params.put("matList", matList);
					r=wmsInReceiptService.boundIn_01(params);
					if(!"500".equals(r.get("code"))) {
						this.updateSCMStauts(skList, matList);
//    	    			wmsInReceiptService.updateSCMStauts(skList, matList);
					}

				}else {
					wmsInReceiptService.setMatBatch(params, matList);
					params.put("matList", matList);
					r=wmsInReceiptService.boundIn_78(params);
					/**
					 * 过账后回写云平台数据
					 */
					JSONArray jaa =new JSONArray();
					JSONObject jsonObject=null;
					for(Map m:matList) {
						jsonObject =  new JSONObject();
						jsonObject.put("deliveryNo",m.get("ASNNO"));
						jsonObject.put("posnr",m.get("ASNITM"));
						jsonObject.put("flag","1");
						jsonObject.put("qty",m.get("RECEIPT_QTY"));
						jsonObject.put("user",currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
						jsonObject.put("date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
						jaa.add(jsonObject);
					}

					Map<String,Object> param =new HashMap<String,Object>();
					param.put("param", jaa.toString());
					if(!"500".equals(r.get("code").toString())) {
						Map<String,Object>res=wmsCloudPlatformRemote.sendDeliveryData(param);
						if("500".equals(res.get("code").toString())) {
							r.put("msg", r.get("msg")+"<br>调用云平台接口回写数据失败!");
						}
					}
				}

			}
			if("02".equals(BUSINESS_NAME)||"04".equals(BUSINESS_NAME)) {//PO采购订单确认收货/跨工厂PO采购订单收货
				//List<Map<String,Object>> matList2=(List<Map<String,Object>>) params.get("matList");
				wmsInReceiptService.setMatBatch(params, matList);
				params.put("matList", matList);
				r = wmsInReceiptService.boundIn_02(params);
			}

			if("03".equals(BUSINESS_NAME)) {//SAP交货单确认收货
				/*
				 * 交货单过账，需处理前台修改的交货数量和未勾选行项目
				 */
				String allDataListStr = params.get("allDataList").toString();
				List<Map<String,Object>> matListNew=new ArrayList<Map<String,Object>>();
				List<Map> allDataListMap = JSONObject.parseArray(allDataListStr, Map.class);
				List<Map> matListMap = JSONObject.parseArray(matListStr, Map.class);

				for (Map a : allDataListMap) {
					String VBELN = a.get("SAP_OUT_NO").toString();//交货单
					String POSNR = a.get("SAP_OUT_ITEM_NO").toString();//行项目
					String VGBEL = a.get("VGBEL").toString();
					String VGPOS =a.get("VGPOS").toString();
					a.put("RECEIPT_QTY", "0");
					a.put("PO_NO", VGBEL);
					a.put("PO_ITEM_NO", VGPOS);
					for (Map m : matListMap) {
						m.put("REVERSAL_FLAG", "X");
						m.put("CANCEL_FLAG", "X");
						if(VBELN.equals(m.get("SAP_OUT_NO").toString()) && POSNR.equals(m.get("SAP_OUT_ITEM_NO").toString())) {
							//覆盖
							m.put("LGORT", m.get("JP_LGORT"));
							a = m;
							break;
						}
					}
					a.put("CREATOR", params.get("USERNAME"));
					a.put("CREATE_DATE", curDate);

					//质检
					if("00".equals(TEST_FLAG)) {
						if(matnr_list.contains(a.get("MATNR"))) {
							//免检
							a.put("TEST_FLAG", "01");
						}else {
							//质检
							a.put("TEST_FLAG", "00");
						}
					}
					//免检
					if("01".equals(TEST_FLAG)) {
						if(matnr_list.contains(a.get("MATNR"))) {
							//质检
							a.put("TEST_FLAG", "00");
						}else {
							a.put("TEST_FLAG", "01");
						}
					}
					if("02".equals(TEST_FLAG)) {
						a.put("TEST_FLAG", "02");
					}

					matListNew.add(a);
				}
				params.put("matListNew", matListNew);
				wmsInReceiptService.setMatBatch(params, matList);
				params.put("matList", matList);
				r = wmsInReceiptService.boundIn_03(params);
			}

			if("06".equals(BUSINESS_NAME)) {//303调拨确认收货
				wmsInReceiptService.setMatBatch(params, matList);
				params.put("matList", matList);
				r = wmsInReceiptService.boundIn_06(params);
			}

			if("05".equals(BUSINESS_NAME)) {//无PO确认收货
				wmsInReceiptService.setMatBatch(params, matList);
				params.put("matList", matList);
				r = wmsInReceiptService.boundIn_05(params);
			}

			if("09".equals(BUSINESS_NAME)) {//303调拨(A)确认收货
				wmsInReceiptService.setMatBatch(params, matList);
				params.put("matList", matList);
				r = wmsInReceiptService.boundIn_09(params);
			}

			if("07".equals(BUSINESS_NAME)) {//SAP采购订单(A)确认收货
				wmsInReceiptService.setMatBatch(params, matList);
				params.put("matList", matList);
				r = wmsInReceiptService.boundIn_07(params);
			}
			if("08".equals(BUSINESS_NAME)) {//SAP交货单(A)确认收货
				wmsInReceiptService.setMatBatch(params, matList);
				params.put("matList", matList);
				r = wmsInReceiptService.boundIn_08(params);
			}
			return r;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}

	}

	/**
	 * 根据物料号获取物料信息（行文本、物料描述、是否紧急物料、是否危化品）
	 * @param params
	 * @return
	 */
	@RequestMapping("/getMatInfo")
	public R getMatInfo(@RequestParam Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		String JZ_DATE=params.get("JZ_DATE")==null?DateUtils.format(new Date()).replaceAll("-", "")
				:params.get("JZ_DATE").toString().replaceAll("-", "");
		String LIKTX=params.get("LIKTX")==null?"" :params.get("LIKTX").toString();

		Map<String,String> txtParams=new HashMap<String,String>();
		txtParams.put("JZ_DATE", JZ_DATE);
		txtParams.put("currentUser", params.get("FULL_NAME").toString());
		txtParams.put("FULL_NAME", params.get("FULL_NAME").toString());
		txtParams.put("BUSINESS_NAME", params.get("BUSINESS_NAME").toString());
		txtParams.put("WERKS", params.get("WERKS").toString());
		txtParams.put("LIKTX", LIKTX);
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtParams);
		txt.put("HEADER_TXT", txt.get("txtrule"));
		txt.put("ITEM_TEXT", txt.get("txtruleitem"));

		List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
		String matnr_str=params.get("matnr_list").toString();
		matList=wmsInReceiptService.getMatListByMATNR(matnr_str,params.get("WERKS").toString());
		for (Map<String, Object> map : matList) {
			map.put("WERKS", params.get("WERKS").toString());
			map.put("WH_NUMBER", params.get("WH_NUMBER").toString());
			map.put("LIFNR", params.get("LIFNR").toString());
		}
		//物料包装规格信息
		List<Map<String,Object>> matPackageList = new ArrayList<Map<String,Object>>();
		if(matList.size()>0) {
			//物料包装规格信息
			matPackageList = commonService.getMatPackageList(matList);
		}


		List<Map<String,Object>> dangerList = wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表

		List<String> matnr_list=Arrays.asList(matnr_str.split(","));
		List<String> compare_list=new ArrayList<String>();
		StringBuilder error_msg=new StringBuilder();

		//查询物料库存
		List<String> matStock = wmsInReceiptService.getMatStock(matList);

		Iterator it=matList.iterator();
		int i=1;
		while(it.hasNext()) {
			Map<String,Object> m=(Map<String,Object>)it.next();
			if(!params.get("WERKS").equals(m.get("WERKS"))) {
				error_msg.append("物料"+m.get("MATNR")+"不属于"+params.get("WERKS")+"工厂<br/>");
				it.remove();
				continue;
			}
			compare_list.add(m.get("MATNR").toString());
			m.put("sortNo", i);//紧急物料置顶
			m.put("HEADER_TXT", txt.get("HEADER_TXT"));
			m.put("ITEM_TEXT", txt.get("ITEM_TEXT"));
			m.put("DANGER_FLAG", "0");
			m.put("URGENT_FLAG", "0");

			if(urgentList.contains(m.get("MATNR"))) {
				m.put("sortNo", 0);//紧急物料置顶
				m.put("URGENT_FLAG", "X");
			}

			for(Map<String,Object> d:dangerList) {
				if(m.get("MATNR").equals(d.get("MATNR"))) {
					m.put("DANGER_FLAG", "X");
					m.put("GOOD_DATES", d.get("GOOD_DATES"));
					m.put("MIN_GOOD_DATES", d.get("MIN_GOOD_DATES"));
					break;
				}
			}
			for (Map<String, Object> map : matPackageList) {
				String MATNR = m.get("MATNR").toString()+"**"+params.get("LIFNR").toString();
				if(map.get("MATNR").equals(MATNR)) {
					m.put("FULL_BOX_QTY", map.get("FULL_BOX_QTY"));
					break;
				}
			}

			String MATNR_IT = m.get("MATNR").toString();
			for (String string : matStock) {
				String MATNR_STR = string.split("\\#\\*")[0];
				String STOCK_QTY = string.split("\\#\\*")[1];
				String STOCK_L = string.split("\\#\\*")[2];
				if(MATNR_IT.equals(MATNR_STR)) {
					m.put("STOCK_QTY", STOCK_QTY);
					m.put("STOCK_L", STOCK_L);
					m.put("STOCK_ALL", STOCK_QTY+"/"+STOCK_L);
					break;
				}
			}

			i++;
		}

		for(String s:matnr_list) {
			if(!compare_list.contains(s)) {
				error_msg.append("物料"+s+"不存在<br/>");
			}
		}

		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(matList);

		return R.ok().put("matList", matList).put("error_msg", error_msg.toString());
	}

	@CrossOrigin
	@RequestMapping("/listCloudMat")
	public R listCloudMat(@RequestBody Map<String, Object> params){
		/**
		 * 查询云平台送货单信息
		 */
		params.put("deliveryNo", params.get("ASNNO"));
		Map<String, Object> deliveryMap = wmsCloudPlatformRemote.getDeliveryDetail(params);
		if(deliveryMap.get("code") !=null && "500".equals(deliveryMap.get("code").toString())) {
			return R.error("查询云平台送货单标签信息失败！"+deliveryMap.get("msg")==null?deliveryMap.get("MSG").toString():deliveryMap.get("msg").toString());
		}
		if(deliveryMap.get("STATUS") !=null && "E".equals(deliveryMap.get("STATUS").toString())) {
			return R.error("查询云平台送货单标签信息失败！"+deliveryMap.get("msg")==null?deliveryMap.get("MSG").toString():deliveryMap.get("msg").toString());
		}

		if(deliveryMap.get("DATA")!=null) {
			deliveryMap=(Map<String, Object>) deliveryMap.get("DATA");
		}
		Map<String,Object> mat = new HashMap<String,Object>();
		if(deliveryMap==null) {
			return R.error("送货单:"+params.get("deliveryNo")+"不存在！");
		}

		if(!"5".equals(deliveryMap.get("STATUS_RMK")) && !"7".equals(deliveryMap.get("STATUS_RMK"))) {
			return R.error("该送货单不是“已发货”状态");
		}

		if(deliveryMap.get("DELIVERY_ITEM")==null) {
			return R.error("送货单:"+params.get("deliveryNo")+"不存在！");
		}

		String WERKS = (String) deliveryMap.get("FACT_NO");		//工厂
		String WH_NUMBER = (String) deliveryMap.get("WHCD_NO");		//仓库号
		String LGORT= (String) deliveryMap.get("LGORT_NO");	//库位
		String LIFNR = (String) deliveryMap.get("PROVIDER_CODE");		//供应商代码
		String LIKTX = (String) deliveryMap.get("PROVIDER_NAME");		//供应商名称
		String ASNNO = (String) deliveryMap.get("DELIVERY_NO"); //送货单号
		String DELIVERY_DATE=(String)deliveryMap.get("BOOKING_DATE");
		/*if(!"".equals(DELIVERY_DATE)) {
			Date d=DateUtils.stringToDate(DELIVERY_DATE, "yyyyMMdd");
			DELIVERY_DATE=DateUtils.format(d, "yyyy-MM-dd");
		}
		if(DELIVERY_DATE.length()>=10) {
			DELIVERY_DATE=DELIVERY_DATE.substring(0,10);
		}*/

		//如果仓库号为空，从WMS_CORE_WH_ADDRESS表中获取
		if(WH_NUMBER==null || WH_NUMBER=="") {
			WH_NUMBER=wmsInReceiptService.getWHAddr(WERKS,LGORT);
		}

		//行项目列表
		List<Map<String,Object>> list= (List<Map<String,Object>>)deliveryMap.get("DELIVERY_ITEM");
		String BATCH="";
		Map<String,Map<String,Object>> item_count = new HashMap<String,Map<String,Object>>();
		//包装箱信息列表
		List<Map<String,Object>> pack_list= (List<Map<String,Object>>)deliveryMap.get("PACKING_DETAIL_DATA");
		List<Map<String,Object>> matPackageList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> m:pack_list) {
			Map<String,Object> _pack=(Map<String,Object>)m.get("OPI_DATA");
			String ASNITM=(String)_pack.get("DELIVERY_ITEM");
			_pack.put("LABEL_NO",m.get("BARCODE_NO"));
			_pack.put("WERKS", WERKS);
			_pack.put("LIFNR", LIFNR);
			_pack.put("MATNR",_pack.get("MAT_NO"));
			_pack.put("ASNITM",ASNITM);
			_pack.put("MAKTX", _pack.get("MAT_DESC"));
			_pack.put("BOX_SN", _pack.get("CONTAINERCODE"));
			_pack.put("FULL_BOX_QTY", _pack.get("SPEC_QTY"));
			_pack.put("BOX_QTY", _pack.get("PRODUCTION_QTY"));
			BATCH=_pack.get("BYD_BATCH").toString();
			String PRODUCT_DATE=(String) _pack.get("PROD_DATE");
			String EFFECT_DATE = _pack.get("PRFRQ")==null?"":_pack.get("PRFRQ").toString();
			_pack.put("PRDDT", PRODUCT_DATE);
			_pack.put("PRODUCT_DATE", PRODUCT_DATE);
			_pack.put("EFFECT_DATE", EFFECT_DATE);
			/**
			 * 统计每个行项目“箱数”、“送货数量”、“批次”
			 */
			Map<String,Object> _m=new HashMap<String,Object>();
			Double item_qty =item_count.get(ASNITM)==null?0: Double.parseDouble(item_count.get(ASNITM).get("QTY").toString());
			Double pack_qty = Double.parseDouble(_pack.get("PRODUCTION_QTY").toString());
			_m.put("QTY", item_qty+pack_qty);

			int box_count=item_count.get(ASNITM)==null?0:(int)item_count.get(ASNITM).get("BOX_COUNT");
			_m.put("BOX_COUNT", box_count+1);
			_m.put("BATCH", BATCH);
			_m.put("PRODUCT_DATE", PRODUCT_DATE);
			_m.put("EFFECT_DATE", EFFECT_DATE);

			item_count.put(ASNITM, _m);

			matPackageList.add(_pack);

		}
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("USERNAME", currentUser.get("USERNAME"));
		params.put("FULL_NAME", currentUser.get("FULL_NAME"));

		//判断账号是否有对应送货单工厂的操作权限，如果没有提示无权限
		Set<Map<String,Object>> deptList = userUtils.getUserWerks("IN_RECEIPT");
		if(deptList == null) {
			return R.error("您没有任何工厂的操作权限!");
		}
		boolean auth=false;
		for(Map dept:deptList) {
			if(dept.get("CODE").equals(WERKS)) {
				auth=true;
				break;
			}
		}
		if(!auth) {
			return R.error("您无权操作"+WERKS+"工厂的单据!");
		}
		List<String> poList = new ArrayList<String>(); //需要校验采购订单凭证类型的采购订单号
		List<String> poitemList=new ArrayList<String>(); //需要校验的采购订单行项目数据
		List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）

		List<Map<String,Object>> datalist=new ArrayList<Map<String,Object>>();

		params.put("WERKS", WERKS);
		params.put("WH_NUMBER", WH_NUMBER);
		params.put("LIFNR", LIFNR);
		List<String> urgentList=wmsInReceiptService.getUrgentMatList(params);//紧急物料校验列表
		// 2019-09-23 优化 云平台启用货架寿命管控 此类收料不再校验是否为危化品
		//List<Map<String,Object>> dangerList=wmsInReceiptService.getDangerMatList(params);//危化品物料校验列表
		Map<String, Object>  whSettingMap = commonService.getPlantSetting(WH_NUMBER);
		Map<String,Object> transfMap=null;

		String matnr_str ="";
		List<String> checkMatInfoList = new ArrayList<>();

		for(int i=0;i<list.size();i++) {
			transfMap=new HashMap<String,Object>();
			Map<String,Object> tmap=list.get(i);
			if(!"5".equals(tmap.get("DTATUS")) && !"7".equals(tmap.get("DTATUS")) ) {
				continue;
			}
			transfMap.put("PONO", tmap.get("EBELN"));
			transfMap.put("POITM", pixStrZero(tmap.get("EBELP").toString(),5));
			transfMap.put("ASNNO", ASNNO);
			transfMap.put("ASNITM", tmap.get("ITEM_NO"));
			transfMap.put("LIKTX", LIKTX);
			transfMap.put("LIFNR", LIFNR);
			transfMap.put("WERKS", WERKS);
			transfMap.put("WH_NUMBER", WH_NUMBER);
			transfMap.put("QTY", item_count.get(tmap.get("ITEM_NO")).get("QTY"));
			transfMap.put("BOX_COUNT", item_count.get(tmap.get("ITEM_NO")).get("BOX_COUNT"));
			//transfMap.put("PRODUCT_DATE", item_count.get(tmap.get("ITEM_NO")).get("PRODUCT_DATE"));
			transfMap.put("MATNR", tmap.get("MAT_NO"));
			transfMap.put("MAKTX", tmap.get("MAT_DESC"));
			transfMap.put("UNIT", tmap.get("UNIT_NO"));
			transfMap.put("TRY_QTY", tmap.get("TESTMATERIAL_QTY"));
			transfMap.put("LGORT", LGORT);
			transfMap.put("BATCH", item_count.get(tmap.get("ITEM_NO")).get("BATCH"));

			transfMap.put("PRODUCT_DATE", item_count.get(tmap.get("ITEM_NO")).get("PRODUCT_DATE"));

			String EFFECT_DATE = item_count.get(tmap.get("ITEM_NO")).get("EFFECT_DATE")==null?null:item_count.get(tmap.get("ITEM_NO")).get("EFFECT_DATE").toString();
			if(StringUtils.isEmpty(EFFECT_DATE)) {
				//获取行项目有效期
				EFFECT_DATE = tmap.get("PRFRQ")==null?"":tmap.get("PRFRQ").toString();
			}
			EFFECT_DATE = DateUtils.format(DateUtils.stringToDate(EFFECT_DATE, DateUtils.DATE_PATTERN_POINT),DateUtils.DATE_PATTERN);
			transfMap.put("EFFECT_DATE", EFFECT_DATE);

			//云平台送货单数据中的采购订单行项目去重，从SAP同步的PO行项目号为五位数
			String poValid=transfMap.get("PONO")+"#*"+String.format("%05d", Integer.valueOf(transfMap.get("POITM").toString()).intValue());
			if(!poitemList.contains(poValid)) {
				poitemList.add(poValid);
			}

			if(!poList.contains(transfMap.get("PONO").toString())) {
				poList.add(transfMap.get("PONO").toString());
			}

			asnList.add(transfMap.get("ASNNO")+"#*"+transfMap.get("ASNITM"));


			transfMap.put("BARCODE_FLAG", whSettingMap==null?"0":whSettingMap.get("BARCODE_FLAG"));
			transfMap.put("IG_FLAG", whSettingMap==null?"0":whSettingMap.get("IG_FLAG"));
			transfMap.put("PO_NO", transfMap.get("PONO"));
			transfMap.put("PO_ITEM_NO", pixStrZero(transfMap.get("POITM").toString(),5));
			transfMap.put("PONO", transfMap.get("PONO"));
			transfMap.put("POITM", pixStrZero(transfMap.get("POITM").toString(),5));
			transfMap.put("WH_NUMBER", WH_NUMBER);
			transfMap.put("sortNo", transfMap.get("ASNITM"));
			//transfMap.put("TRY_QTY", transfMap.get("TESTMATERIAL_QTY"));
			transfMap.put("BIN_CODE", "");
			transfMap.put("RECEIVER", currentUser.get("FULL_NAME"));
			transfMap.put("VENDOR_MANAGER", "");
			transfMap.put("SHORT_NAME", "");
			transfMap.put("DANGER_FLAG", "0");
			transfMap.put("URGENT_FLAG", "0");

			transfMap.put("DELIVERY_DATE", DELIVERY_DATE);

			if(urgentList.contains(transfMap.get("MATNR"))) {
				transfMap.put("sortNo", 0);//紧急物料置顶
				transfMap.put("URGENT_FLAG", "X");
			}

   /* 		for(Map<String,Object> m:dangerList) {
    			if(m.get("MATNR").equals(transfMap.get("MATNR"))) {
    				transfMap.put("DANGER_FLAG", "X");
    				transfMap.put("GOOD_DATES", m.get("GOOD_DATES"));
    				transfMap.put("MIN_GOOD_DATES", m.get("MIN_GOOD_DATES"));
    				break;
    			}
    		}*/
			matnr_str += transfMap.get("MATNR")+",";
			checkMatInfoList.add(WERKS+"#*"+transfMap.get("MATNR"));

			datalist.add(transfMap);
		}
		if(datalist.size()<=0 && list.size() >0 ) {
			return R.error("送货单:"+params.get("deliveryNo")+"没有可收货的行项目数据！");
		}
		//校验物料信息是否已经同步
		List<Map<String,Object>> matInfoList=new ArrayList<Map<String,Object>>();
		matInfoList=wmsInReceiptService.getMatListByMATNR(matnr_str,WERKS);
		String matErrorStr = "";
		for (String checkMat : checkMatInfoList) {
			boolean b = false;
			for (Map<String, Object> matInfoMap : matInfoList) {
				String matStr = matInfoMap.get("WERKS").toString()+"#*"+matInfoMap.get("MATNR");
				if(checkMat.equals(matStr)) {
					b = true ;
					break;
				}
			}
			if(!b) {
				matErrorStr += checkMat.split("#*")[1];
			}
		}
		if(!"".equals(matErrorStr)) {
			matErrorStr += "信息在收货工厂"+WERKS+"不存在，请使用物料数据同步功能同步物料信息！";
			return R.error(matErrorStr);
		}

		/**
		 * 核对送货单对应的采购订单行项目数据是否在WMS采购订单数据表中存在，如果不存在，报错提示：请同步SAP采购订单
		 */
		List<String> poitemSynList=wmsInReceiptService.getPoItemListByPo(poitemList);
		List<String> poErrorList=new ArrayList<String>();
		for(String po:poitemList) {
			if(!poitemSynList.contains(po)) {
				if(!poErrorList.contains(po.split("\\#\\*")[0])) {
					poErrorList.add(po.split("\\#\\*")[0]);
				}
			}
		}
		if(poErrorList.size()>0) {
			return R.error("送货单关联的采购订单信息不存在，请同步SAP采购订单:"+StringUtils.join(poErrorList, ","));
		}
		List<String> poTypeList = wmsInReceiptService.getPoTypeListByPo(poList);
		for (String poTypeStr : poTypeList) {
			String poType = poTypeStr.split("\\#\\*")[1];
			String poNo = poTypeStr.split("\\#\\*")[0];
			if(poType.equals("BYDS")) {
				poErrorList.add(poNo);
			}
		}
		if(poErrorList.size()>0) {
			return R.error("送货单关联的采购订单信息:"+StringUtils.join(poErrorList, ",")+"，不能为STO类型采购订单，请使用STO送货单类型收料！");
		}

		/**
		 * 根据封装好的查询条件（送货单号##送货单行项目号）查询每条行项目已收货数量, 可收货数量
		 */
		Map<String,Object> receiptCount=wmsInReceiptService.getReceiptCount(asnList);
		/**
		 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
		 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
		 */
		Map<String,Object> vendor=wmsInReceiptService.getVendorInfo(params);
		if(null == vendor || null == vendor.get("NAME1")) {
			return R.error("送货单关联的供应商："+LIFNR+"信息未同步至WMS系统，请先同步供应商信息！");
		}

		/**
		 * 根据采购订单、订单行项目号从采购订单表中抓取申请人和需求跟踪号
		 */
		Map<String,Object> bendr_afnam=wmsInReceiptService.getBendrAfnam(poitemList);


		/**
		 * 根据工厂代码、业务类型获取收货单("01")抬头文本与行文本
		 */
		Map<String,String>txtMap=new HashMap<String,String>();
		String JZ_DATE=params.get("JZ_DATE")==null?DateUtils.format(new Date(), "yyyyMMdd"):params.get("JZ_DATE").toString().replaceAll("-", "");
		txtMap.put("WERKS", WERKS);
		txtMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME")==null?"78":params.get("BUSINESS_NAME").toString());
		txtMap.put("JZ_DATE", JZ_DATE);
		String SHORT_NAME=vendor==null?"":(String)vendor.get("SHORT_NAME");
		txtMap.put("LIKTX", SHORT_NAME);
		txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
		txtMap.put("ASNNO", ASNNO);
		Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
		//查询物料库存
		List<String> matStock = wmsInReceiptService.getMatStock(datalist);
		Iterator<Map<String,Object>> it =datalist.iterator();
		int i=0;
		while(it.hasNext()) {
			Map<String,Object> m=it.next();
			String MATNR_IT = m.get("MATNR").toString();
			for (String string : matStock) {
				String MATNR_STR = string.split("\\#\\*")[0];
				String STOCK_QTY = string.split("\\#\\*")[1];
				String STOCK_L = string.split("\\#\\*")[2];
				if(MATNR_IT.equals(MATNR_STR)) {
					m.put("STOCK_QTY", STOCK_QTY);
					m.put("STOCK_L", STOCK_L);
					m.put("STOCK_ALL", STOCK_QTY+"/"+STOCK_L);
					break;
				}
			}

			double finishedQty=0;//已收数量
			double RECEIPT_QTY=0;//可收货数量
			String s=(String) receiptCount.get(m.get("ASNNO")+"#*"+m.get("MATNR")+"#*"+m.get("ASNITM"));
			if (s==null) {
				s="0";
			}
			String QTY=m.get("QTY").toString();
			finishedQty=Double.parseDouble(s);
			RECEIPT_QTY=Double.parseDouble(QTY)-finishedQty;
			if(RECEIPT_QTY<=0) {
				//datalist.remove(i); 报空指针
				it.remove();
			}else {
				m.put("FINISHED_QTY", finishedQty);
				m.put("RECEIPT_QTY", RECEIPT_QTY);
				m.put("DELIVERYAMOUNT", RECEIPT_QTY);
				if(vendor!=null&&!vendor.isEmpty()) {
					m.put("SHORT_NAME", vendor.get("SHORT_NAME"));
					m.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
					m.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
				}else {
					m.put("VENDOR_FLAG", "0");
				}

				String ba=(String) bendr_afnam.get(m.get("PO_NO")+"#*"+String.format("%05d", Integer.valueOf(m.get("PO_ITEM_NO").toString()).intValue()));
				String BEDNR="";
				String AFNAM="";
				String SOBKZ="Z";
				if(ba!=null) {
					BEDNR=ba.split("\\#\\*")[0];
					AFNAM=ba.split("\\#\\*")[1];
					SOBKZ=ba.split("\\#\\*")[2];
				}

				m.put("BEDNR", BEDNR);
				m.put("AFNAM", AFNAM);
				m.put("SOBKZ", SOBKZ);
				m.put("HEADER_TXT", txt.get("txtrule"));
				m.put("ITEM_TEXT", txt.get("txtruleitem"));
/*        		for (Map<String, Object> map : matPackageList) {
        			String MATNR = m.get("MATNR").toString()+"**"+m.get("ASNITM").toString();
					if((map.get("MATNR")+"**"+map.get("ASNITM")).equals(MATNR)) {
						m.put("FULL_BOX_QTY", map.get("FULL_BOX_QTY"));
						if(Double.parseDouble(m.get("SPEC_QTY").toString()) != Double.parseDouble(map.get("FULL_BOX_QTY")==null?"0":map.get("FULL_BOX_QTY").toString())) {
							m.put("FULL_BOX_ERROR", MATNR+",");
						}
						break;
					}
				}*/

			}
			i++;
		}

		/**
		 * 排序，安照紧急物料排最前，然后按照送货单行号排序
		 */
		this.sortMatList(datalist);

		PageUtils page=new PageUtils(new Page().setRecords(datalist));
		return R.ok().put("page", page);
	}

	private String pixStrZero(String str,int length) {
		int pxnum = length-str.length();
		if(pxnum >0) str = String.format("%0"+(pxnum)+"d", 0) + str;
		if(pxnum <0) str = str.substring(0- length);
		return str;
	}


	private ExecutorService executor = Executors.newCachedThreadPool() ;

	public void updateSCMStauts(List<Map<String,Object>> skList,List<Map<String,Object>> matList) throws Exception {
		executor.submit(new Runnable(){
			@Override
			public void run() {
				try {
					wmsInReceiptService.updateSCMStauts(skList, matList);
				}catch(Exception e) {
					throw new RuntimeException();
				}
			}
		});
	}
}
