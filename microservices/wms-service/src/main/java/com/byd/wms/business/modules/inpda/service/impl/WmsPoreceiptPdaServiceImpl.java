package com.byd.wms.business.modules.inpda.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.inpda.dao.WmsPoreceiptPdaDao;
import com.byd.wms.business.modules.inpda.service.WmsPoReceiptPdaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author rain
 * @version 2019年12月4日20:28:52
 * PDA采购订单收货
 */
@Service
public class WmsPoreceiptPdaServiceImpl implements WmsPoReceiptPdaService {


	@Autowired
	private WmsPoreceiptPdaDao wmsPoreceiptPdaDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
    private WmsCDocNoService wmsCDocNoService;



	@Override
	public R getPorecCache(Map<String, Object> map) {
		try {
			//获取采购订单收货缓存信息
			List<Map<String,Object>> result = wmsPoreceiptPdaDao.getPorecCache(map);
			return R.ok().put("result", result);
		}catch (Exception e){
			return R.error(e.getMessage());
		}
	}

	@Override
	public R validatePoReceiptLable(Map<String, Object> params) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date();
		String curStrTime=dateFormat.format(currentDate);
		if(params.get("LABEL_NO")==null || "".equals(params.get("LABEL_NO").toString())){
			return R.error("标签条码号丢失,请重试!");
		}else if(params.get("WERKS")==null || "".equals(params.get("WERKS").toString())){
			return R.error("工厂号丢失,请重试!");

		}else if(params.get("WH_NUMBER")==null || "".equals(params.get("WH_NUMBER").toString())){
			return R.error("仓库号丢失,请重试!");
		}
		//验证缓存表里面是否缓存该标签条码
		List<Map<String,Object>> cacheValidateMap= wmsPoreceiptPdaDao.getPorecCache(params);
		if(cacheValidateMap != null && !cacheValidateMap.isEmpty()){
			return R.error("该条码已经扫描,请不要重复扫描！");
		}
		Map<String, Object> labelMap = new HashMap<>();
		labelMap.put("LABEL_NO", params.get("LABEL_NO"));
		labelMap.put("WERKS", params.get("WERKS"));
		labelMap.put("WH_NUMBER", params.get("WH_NUMBER"));
		List<Map<String, Object>> retLabelList=wmsPoreceiptPdaDao.getPoLabelList(labelMap);//获取页面即将查询的这一条标签
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
				if(!"00".equals(map.get("LABEL_STATUS").toString())){
					return R.error("该标签条码："+map.get("LABEL_NO")+"的状态不为创建！");
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

	@Override
	public R getPoDetailByBarcode(Map<String, Object> params) {
		try {
			List<Map<String,Object>> result = wmsPoreceiptPdaDao.getPoDetailListByBarcode(params);
			if(result == null || result.isEmpty()){
				return R.error("标签号 "+params.get("LABEL_NO")+"在条码信息中不存在！");
			}
			//先保存缓存扫描信息(通过工号/工厂/仓库号/业务类型/业务code,确定唯一性)todo 加上菜单code
			wmsPoreceiptPdaDao.insertPoReceiptPdaCache(params);
			return R.ok().put("result", result.get(0));
		}catch (Exception e){
			return R.error(e.getMessage());
		}
	}

	/**
	 * 获取采购订单收货明细页面
	 * @param params
	 * @return
	 */
	@Override
	public PageUtils getGridPoreDataPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsPoreceiptPdaDao.getGridPoreDataCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=wmsPoreceiptPdaDao.getGridPoreData(params);

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
	 * @return
	 */
	@Override
	public PageUtils getBarGridPoreDataPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsPoreceiptPdaDao.getBarGridPoreDataCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=wmsPoreceiptPdaDao.getBarGridPoreData(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
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
			wmsPoreceiptPdaDao.delPorecBarInfos(params);
			return R.ok();
		}catch (Exception e){
			return R.error(e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> getAllLabelInfos(Map<String, Object> map) {
		List<Map<String, Object>> allLabelInfos= wmsPoreceiptPdaDao.getAllLabelInfos(map);
		return allLabelInfos;
	}

	/**
	 * SAP采购订单收货确认
	 * @param params
	 * @return
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R poReceiptPdaIn(Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();
		try {
			List<Map<String, Object>> matList = (List<Map<String, Object>>) params.get("matList");
			List<Map<String,Object>> skList=(List<Map<String,Object>>) params.get("skList");//条码清单
			String WERKS = params.get("WERKS").toString();
			String SAP_MOVE_TYPE = "";//SAP移动类型，为空不进行SAP过账
			String WMS_MOVE_TYPE = "";

			params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//PO采购订单收料
			params.put("BUSINESS_TYPE", "02");//PO采购订单
			if ("04".equals(params.get("BUSINESS_NAME"))) {
				params.put("BUSINESS_TYPE", "04");//跨工厂PO采购订单
			}
			params.put("BUSINESS_CLASS", "01");
			/**
			 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
			 */
			Map<String, Object> cdmap = new HashMap<>();
			cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//PO采购订单收料
			cdmap.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE"));//PO采购订单
			cdmap.put("BUSINESS_CLASS", "01");
			cdmap.put("WERKS", WERKS);
			cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
			if ("04".equals(params.get("BUSINESS_NAME"))) {//跨工厂PO采购订单,固定特殊库存类型为'Z'
				params.put("SOBKZ", "Z");
			}
			Map<String, Object> moveSyn = commonDao.getMoveAndSyn(cdmap);
			if (moveSyn == null || moveSyn.get("WMS_MOVE_TYPE") == null) {
				result.put("code", 500);
				result.put("msg","收货工厂" + WERKS + "未配置PO采购订单收料业务类型！");
				return R.error("收货工厂" + WERKS + "未配置PO采购订单收料业务类型！").put("result",result);
			}
			if (moveSyn != null) {
				SAP_MOVE_TYPE = (String) moveSyn.get("SAP_MOVE_TYPE");
				WMS_MOVE_TYPE = (String) moveSyn.get("WMS_MOVE_TYPE");
				params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
			}
			params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);

			/**
			 * 生成WMS批次信息
			 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
			params.put("matList", matList);

			/**
			 * 保存收货日志表(标签日志表)
			 */
			wmsPoreceiptPdaDao.insertWmsBarCodeLog((List<Map<String, Object>>) params.get("matList"));

			/**
			 * 保存收货单数据
			 */
			String RECEIPT_NO = this.saveReceiptInfo(params);//收货单号

			/**
			 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
			 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
			 */
			this.saveRhStock(params);

			/**
			 * 保存标签数据或者更新标签状态
			 */
			this.saveOrUpdateCoreLabel(skList, (List<Map<String, Object>>) params.get("matList"));
			Map<String, Object> labMap = new HashMap<>();
			labMap.put("skList", skList);
			wmsPoreceiptPdaDao.updateLabelStatus(labMap);//x修改标签状态为01

			/**
			 * 产生WMS凭证记录(WMS事务记录)
			 *
			 */
			String WMS_NO = commonService.saveWMSDoc(params, (List<Map<String, Object>>) params.get("matList"));
			params.put("WMS_NO", WMS_NO);

			/**
			 *  保存质检信息
			 */
			String INSPECTION_NO = this.doQualityCheck(params);      //送检单号

			/**
			 *  TODO 是否需要走质检系统接口,传输质检信息.
			 */

			/**
			 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
			 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
			 * SAP过账失败回滚
			 */
			String SAP_NO = commonService.doSapPost(params);

			StringBuilder msg = new StringBuilder();
			msg.append("操作成功！收货单号:");
			msg.append(RECEIPT_NO);
			if (StringUtils.isNotBlank(INSPECTION_NO)) {
				msg.append(" 送检单号:");
				msg.append(INSPECTION_NO);
			}
			msg.append(" WMS凭证:");
			msg.append(WMS_NO);
			if (StringUtils.isNotBlank(SAP_NO)) {
				msg.append(" SAP凭证:");
				msg.append(SAP_NO);
			}
			//删除PDA缓存表里面的数据,根据用户账号\工厂\仓库号\业务类型(#{BUSINESS_CODE}\#{USERNAME}\#{WERKS}\#{WH_NUMBER})
			wmsPoreceiptPdaDao.delPorecCache(params);

			//构造返回到页面的数据
			result.put("code", 0);
			result.put("msg", "success");
			result.put("WMS_NO", WMS_NO);
			result.put("SAP_NO", SAP_NO);
			result.put("RECEIPT_NO", RECEIPT_NO);
			result.put("INSPECTION_NO", INSPECTION_NO);//需要走质检接口
			return R.ok().put("result",result);
//		return R.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);
		}catch (Exception e){
			System.out.println(e.getMessage());
			result.put("code", 500);
			result.put("msg", e.getMessage());
			return R.error(e.getMessage()).put("result",result);
		}

	}

	/**
	 * 保存收货单
	 * @param params
	 */
	@Transactional
	public String saveReceiptInfo(Map<String,Object> params) {
		params.put("WMS_DOC_TYPE", "02");//收货单
		String RECEIPT_NO="";//收货单号
		Map<String,Object> doc=null;
		doc=wmsCDocNoService.getDocNo(params);
		if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
			throw new RuntimeException(doc.get("MSG").toString());
		}
		RECEIPT_NO=doc.get("docno").toString();

		List<Map<String,Object>> matList=(List<Map<String, Object>>) params.get("matList");

		int i=1;
		for(Map m:matList) {
			m.put("RECEIPT_NO", RECEIPT_NO);
			m.put("RECEIPT_DATE", m.get("CREATE_DATE"));
			m.put("RECEIPT_ITEM_NO", i);
			m.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
			m.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE"));
			m.put("GR_AREA", m.get("BIN_CODE"));
			m.put("INABLE_QTY", 0);
			i++;
		}

		wmsInReceiptDao.insertReceiptInfo(matList);

		return RECEIPT_NO;
	}

	/**
	 *保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
	 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
	 * @param params
	 */
	@Transactional
	public void saveRhStock(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String, Object>>) params.get("matList");
		for(Map<String,Object> m:matList) {
			m.put("WERKS", m.get("WERKS")==null?"":m.get("WERKS"));
			m.put("WH_NUMBER", m.get("WH_NUMBER")==null?"":m.get("WH_NUMBER"));
			m.put("LGORT", m.get("LGORT")==null?"":m.get("LGORT"));
			m.put("MATNR", m.get("MATNR")==null?"":m.get("MATNR"));
			m.put("MAKTX", m.get("MAKTX")==null?"":m.get("MAKTX"));
			m.put("F_BATCH", m.get("F_BATCH")==null?"":m.get("F_BATCH"));
			m.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH"));
			m.put("UNIT", m.get("UNIT")==null?"":m.get("UNIT"));
			m.put("RH_QTY", m.get("RH_QTY")==null?m.get("RECEIPT_QTY"):m.get("RH_QTY"));
			m.put("SOBKZ", m.get("SOBKZ")==null?"":m.get("SOBKZ"));
			m.put("LIFNR", m.get("LIFNR")==null?"":m.get("LIFNR"));
			m.put("LIKTX", m.get("LIKTX")==null?"":m.get("LIKTX"));
		}

		if(matList.size()>0) {
			wmsInReceiptDao.updateRhStock(matList);
		}
	}

	/**
	 * 保存标签数据
	 */
	@Transactional
	public void saveOrUpdateCoreLabel(List<Map<String, Object>> skList, List<Map<String, Object>> matList) {
		String BUSINESS_NAME=matList.get(0).get("BUSINESS_NAME").toString();
		String LABEL_STATUS = "01";//00创建，01已收料（待质检），02已收料（无需质检）03待进仓(已质检)，04待退货(已质检)，05收料房退货，06库房退货，07已进仓，08已上架，09已下架，10已出库，11已冻结，12已锁定，20关闭）
		String QC_RESULT_CODE = null;
		//从行项目中获取收货单号和收货单行项目号
		if(BUSINESS_NAME.equals("01")||BUSINESS_NAME.equals("78")) {//SCM收货单、云平台送货单
			for (Map<String, Object> sk : skList) {
				for(Map<String,Object> m:matList) {
					String TEST_FLAG = m.get("TEST_FLAG").toString();
					if(TEST_FLAG.endsWith("01")) {
						//免检
						LABEL_STATUS = "03";
						QC_RESULT_CODE = "11";
					}
					if(TEST_FLAG.endsWith("02")) {
						//无需质检
						LABEL_STATUS = "02";
					}

					String mat_item_no="";
					String sk_item_no="";
					if(BUSINESS_NAME.equals("01")) {
						mat_item_no=m.get("ASNITM").toString();
						sk_item_no=sk.get("ASNITM").toString();
					}
					if(BUSINESS_NAME.equals("78")) {
						mat_item_no=m.get("ASNITM").toString();
						sk_item_no=sk.get("ASNITM").toString();
					}

					if(m.get("WERKS").equals(sk.get("WERKS"))&&m.get("LIFNR").equals(sk.get("LIFNR"))
							&&m.get("MATNR").equals(sk.get("MATNR"))&&mat_item_no.equals(sk_item_no)
					) {
						String LABEL_NO="";
						if(sk.get("SKUID")!=null) {
							LABEL_NO=sk.get("SKUID").toString();
						}else {
							LABEL_NO=sk.get("LABEL_NO").toString();
						}
						sk.put("TRY_QTY", m.get("TRY_QTY"));
						sk.put("GR_QTY", m.get("RECEIPT_QTY"));
						sk.put("SOBKZ", m.get("SOBKZ"));
						sk.put("WH_NUMBER", m.get("WH_NUMBER"));
						sk.put("LGORT", m.get("LGORT"));
						sk.put("LABEL_NO", LABEL_NO);
						sk.put("LABEL_STATUS", LABEL_STATUS);
						sk.put("QC_RESULT_CODE", QC_RESULT_CODE);
						sk.put("RECEIPT_NO", m.get("RECEIPT_NO"));
						sk.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO")+"");
						sk.put("PRODUCT_DATE", m.get("PRDDT")==null?m.get("PRODUCT_DATE"):m.get("PRDDT"));
						sk.put("EFFECT_DATE", m.get("EFFECT_DATE")==null?"":m.get("EFFECT_DATE").toString());
						sk.put("LIFNR", m.get("LIFNR"));
						sk.put("LIKTX", m.get("LIKTX"));
						sk.put("CREATOR", m.get("CREATOR"));
						sk.put("CREATE_DATE", m.get("CREATE_DATE"));
						sk.put("BEDNR", m.get("BEDNR")==null?"":m.get("BEDNR").toString());
						sk.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH").toString());
						break;
					}
				}
			}
		}else {//非SCM收货单情况需要拆分行项目计算包装箱信息
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("WMS_DOC_TYPE", "08");//标签号
			params.put("WERKS", matList.get(0).get("WERKS"));

			for(Map m:matList) {
				String TEST_FLAG = m.get("TEST_FLAG").toString();
				if(TEST_FLAG.endsWith("01")) {
					//免检
					LABEL_STATUS = "03";
					QC_RESULT_CODE = "11";
				}
				if(TEST_FLAG.endsWith("02")) {
					//无需质检
					LABEL_STATUS = "02";
				}

				Double RECEIPT_QTY=Double.valueOf(m.get("RECEIPT_QTY").toString());
				Double FULL_BOX_QTY=Double.valueOf(m.get("FULL_BOX_QTY").toString());
				int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);

				StringBuffer LABEL_NO_BF = new StringBuffer();

				for(int i=1;i<=box_num;i++) {
					Map<String,Object> sk=new HashMap<String,Object>();
					sk.putAll(m);

					String LABEL_NO="";
					Map<String,Object> doc=null;
					doc=wmsCDocNoService.getDocNo(params);
					if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
						throw new RuntimeException(doc.get("MSG").toString());
					}
					LABEL_NO=doc.get("docno").toString();
					String BOX_SN=i+"/"+box_num;
					Double BOX_QTY=FULL_BOX_QTY;//装箱数量，计算得出
					String END_FLAG="0";
					if(i==box_num) {
						BOX_QTY = RECEIPT_QTY-(box_num-1)*FULL_BOX_QTY;
						END_FLAG="X";
					}

					sk.put("LABEL_NO", LABEL_NO);
					sk.put("LABEL_STATUS", LABEL_STATUS);
					sk.put("QC_RESULT_CODE", QC_RESULT_CODE);
					sk.put("RECEIPT_NO", m.get("RECEIPT_NO"));
					sk.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO")+"");
					sk.put("TRY_QTY", m.get("TRY_QTY"));
					sk.put("GR_QTY", m.get("RECEIPT_QTY"));
					sk.put("SOBKZ", m.get("SOBKZ"));
					sk.put("CREATOR", m.get("CREATOR"));
					sk.put("CREATE_DATE", m.get("CREATE_DATE"));
					sk.put("WH_NUMBER", m.get("WH_NUMBER"));
					sk.put("LGORT", m.get("LGORT"));
					sk.put("BOX_SN", BOX_SN);
					sk.put("FULL_BOX_QTY", m.get("FULL_BOX_QTY"));
					sk.put("BOX_QTY", BOX_QTY);
					sk.put("END_FLAG", END_FLAG);

					sk.put("PRODUCT_DATE", m.get("PRODUCT_DATE")==null?null:m.get("PRODUCT_DATE"));
					sk.put("EFFECT_DATE", m.get("EFFECT_DATE")==null?"":m.get("EFFECT_DATE").toString());
					sk.put("LIFNR", m.get("LIFNR"));
					sk.put("LIKTX", m.get("LIKTX"));
					sk.put("BEDNR", m.get("BEDNR")==null?"":m.get("BEDNR").toString());
					sk.put("PO_NO", m.get("PO_NO")==null?"":m.get("PO_NO").toString());
					sk.put("PO_ITEM_NO", m.get("PO_ITEM_NO")==null?"":m.get("PO_ITEM_NO").toString());

					skList.add(sk);

					LABEL_NO_BF.append(LABEL_NO).append(",");
				}

				if(LABEL_NO_BF.length()>0) {
					m.put("LABEL_NO", LABEL_NO_BF.toString().substring(0, LABEL_NO_BF.length()-1));
				}else {
					m.put("LABEL_NO", "");
				}

			}

		}
		if(skList.size()>0) {
			wmsPoreceiptPdaDao.saveOrUpdateCoreLabel(skList);
		}

	}

	/**
	 * 质检:读取工厂质检配置表：
	 *  00质检：产生送检单，读取和匹配物料质检配置表的免检物料数据，产生质检结果为免检的质检结果数据和质检记录数据，对应送检单抬头状态为全部完成，
	 *  行项目状态为完成，收货单可进仓数量等于对应送检单数量。
	 *  01免检 产生送检单，读取和匹配物料质检配置表的免检物料数据，产生质检结果为免检的质检结果数据和质检记录数据，对应送检单抬头状态为全部完成，
	 *  行项目状态为完成，收货单可进仓数量等于对应送检单数量。
	 *  02 无需质检 不产生送检单，收货单可进仓数量等于收货数量
	 * @return 收货单可进仓数量
	 */
	@Transactional
	public String doQualityCheck(Map<String, Object> params) {
		String TEST_FLAG=params.get("TEST_FLAG").toString();//工厂维护质检配置
		List<Map<String, Object>> matList=(List<Map<String, Object>>) params.get("matList");
		List<Map<String,Object>> match_list=new ArrayList<Map<String,Object>>();//检验结果明细数据列表

		String INSPECTION_NO="";//送检单号
		String INSPECTION_STATUS="00";//送检单抬头状态默认为创建（00） 送检单状态  字典定义：（00创建，01部分完成，02全部完成，04关闭）
		String INSPECTION_TYPE="01";//送检单类型为来料质检（01）
		String WH_NUMBER=(String)matList.get(0).get("WH_NUMBER");//仓库号
		String INSPECTION_ITEM_STATUS="00";//送检单行项目状态默认为未质检  （00未质检，01已质检，02关闭）
		String CREATOR=(String)matList.get(0).get("CREATOR");
		String CREATE_DATE=(String)matList.get(0).get("CREATE_DATE");
		//送检单抬头数据
		Map<String,Object> INSPECTION_MAP=new HashMap<String,Object>();

		//工厂维度配置的质检状态为“质检”、免检情况
		if("00".equals(TEST_FLAG)||"01".equals(TEST_FLAG)) {
			params.put("WMS_DOC_TYPE", "03");//送检单
			Map<String,Object> doc=wmsCDocNoService.getDocNo(params);
			if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
				throw new RuntimeException(doc.get("MSG").toString());
			}
			INSPECTION_NO=doc.get("docno").toString();//送检单号

			int i=1;
			for(Map m:matList) {
				m.put("INSPECTION_NO", INSPECTION_NO);
				m.put("INSPECTION_ITEM_NO", i++);//送检单行项目号
				m.put("INSPECTION_ITEM_STATUS", INSPECTION_ITEM_STATUS);
				m.put("STOCK_SOURCE", "01");//送检单明细库存来源为收料房
				m.put("INSPECTION_QTY", m.get("RECEIPT_QTY"));//送检数量

				//判断物料维护质检配置是否为“免检”
				if(m.get("TEST_FLAG")!=null && "01".equals(m.get("TEST_FLAG").toString())) {
					//免检
					m.put("INSPECTION_ITEM_STATUS", "01"); //送检单状态修改为，已质检
					match_list.add(m);
				}

			}
			String QC_RECORD_NO="";//检验记录号
			String QC_RESULT_NO="";//检验结果号
			if(match_list !=null && match_list.size()>0) {
				//免检物料，产生质检结果和质检记录
				doc=new HashMap<String,Object>();
				params.put("WMS_DOC_TYPE", "04");//检验记录
				doc=wmsCDocNoService.getDocNo(params);//检验记录号
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				QC_RECORD_NO=doc.get("docno").toString();//检验记录号

				params.put("WMS_DOC_TYPE", "05");//检验结果
				doc=wmsCDocNoService.getDocNo(params);//检验结果
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				QC_RESULT_NO=doc.get("docno").toString();//检验结果

				//根据匹配行项目生成质检记录和质检结果
				int j=1;
				for(Map<String,Object> m:match_list) {
					m.put("QC_RECORD_NO", QC_RECORD_NO);
					m.put("QC_RESULT_NO", QC_RESULT_NO);

					m.put("QC_RESULT_CODE", "11");//检验结果：免检
					m.put("QC_RESULT_TEXT", "免检");//检验结果：免检
					m.put("QC_RESULT", "免检");
					m.put("QC_STATUS", "02");//质检状态 字典定义：00未质检 01质检中 02已质检
					m.put("QC_RESULT_ITEM_NO", j);
					m.put("QC_RECORD_ITEM_NO", j);
					m.put("QC_RECORD_TYPE", "01");//质检记录类型 字典定义： 01初判 02重判
					m.put("RESULT_QTY", m.get("RECEIPT_QTY"));//质检结果数量
					m.put("RECORD_QTY", m.get("RECEIPT_QTY"));//质检结果数量
					m.put("QC_DATE", m.get("CREATE_DATE"));
					j++;
				}
				/**
				 * 保存检验记录和检验结果
				 */
				wmsInReceiptDao.insertQCResult(match_list);
				wmsInReceiptDao.insertQCRecord(match_list);

				//根据质检结果行项目更新收货单行项目的可进仓数量
				wmsInReceiptDao.updateReceiptInableQty(match_list);

				if(match_list.size()<matList.size()) {
					INSPECTION_STATUS="01";//送检单抬头状态默认为创建（00） 送检单状态  字典定义：（00创建，01部分完成，02全部完成，04关闭）
				}else {
					INSPECTION_STATUS="02";
				}
			}

			INSPECTION_MAP.put("INSPECTION_NO", INSPECTION_NO);
			INSPECTION_MAP.put("INSPECTION_STATUS", INSPECTION_STATUS);
			INSPECTION_MAP.put("INSPECTION_TYPE", INSPECTION_TYPE);
			INSPECTION_MAP.put("WMS_NO", params.get("WMS_NO"));
			INSPECTION_MAP.put("WERKS", params.get("WERKS"));
			INSPECTION_MAP.put("WH_NUMBER", WH_NUMBER);
			INSPECTION_MAP.put("CREATOR", CREATOR);
			INSPECTION_MAP.put("CREATE_DATE", CREATE_DATE);
			INSPECTION_MAP.put("IS_AUTO", "X");
			INSPECTION_MAP.put("DEL", "0");
			//保存送检单抬头
			wmsInReceiptDao.insertInspectionHead(INSPECTION_MAP);
			//保存送检单明细
			wmsInReceiptDao.insertInspectionItem(matList);

			//更新WMS凭证行项目的送检单号和行项目号
			wmsInReceiptDao.updateWMSDocItemInspection(matList);

		}else {//无需质检，更新收货单行项目的可进仓数量为收货数量
			for(Map m:matList) {
				m.put("RESULT_QTY", m.get("RECEIPT_QTY"));//质检结果数量
			}
			wmsInReceiptDao.updateReceiptInableQty(matList);
		}
		return INSPECTION_NO;

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

