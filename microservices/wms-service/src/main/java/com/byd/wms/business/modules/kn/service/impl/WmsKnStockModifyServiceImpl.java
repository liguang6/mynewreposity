package com.byd.wms.business.modules.kn.service.impl;

import com.byd.utils.R;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.config.dao.WmsCWhDao;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.kn.dao.WmsKnStockModifyDao;
import com.byd.wms.business.modules.kn.service.WmsKnStockModifyService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Service("wmsKnStockModifyService")
public class WmsKnStockModifyServiceImpl implements WmsKnStockModifyService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static String ZW_201_CODE="ZW_201"; // Z 库存增加
	private static String ZW_202_CODE="ZW_202"; // K 库存增加
	private static String ZW_203_CODE="ZW_203"; // Z 库存减少
	private static String ZW_204_CODE="ZW_204"; // K 库存减少
	@Autowired
	private WmsKnStockModifyDao wmsKnStockModifyDao;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsCWhDao wmsCWhDao;

	// 库存调整保存 ModBy:YK190722 把生成批次的方法移到事务外面 避免重复生成批次
	@Override
	@Transactional
	public Map<String,Object> save(Map<String, Object> params) {
		Map<String,Object> result=new HashMap<String,Object>();

		String wmsNo=null;
		String type=params.get("type").toString();
		List<Map<String,Object>> list = (List<Map<String, Object>>) params.get("list");

		for (Map<String,Object> item : list) {
			if(item.get("QTY_TYPE").toString().equals("非限制")) {
				item.put("STOCK_QTY", item.get("QTY"));
			}
			if(item.get("QTY_TYPE").toString().equals("冻结")) {
				item.put("FREEZE_QTY", item.get("QTY"));
			}
			item.put("CREATOR", params.get("USERNAME").toString());
			item.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		}
		// 库存新增
		if(type.equals("00")) {
			List<Map<String,Object>>labelList=this.stockAdd(params,list);
			wmsNo=saveWmsDoc(params,list);
			result.put("labelList", labelList);
			result.put("wmsNo", wmsNo);
		}
		// 库存修改
		if(type.equals("01")) {
			this.stockModify(params,list);
			wmsNo=saveWmsDoc(params,list);
			result.put("wmsNo", wmsNo);
		}
		return result;
	}

	//新增库存  返回标签List
	public List<Map<String,Object>> stockAdd(Map<String, Object> params,List<Map<String,Object>> list) {
//		// 校验导入库存数据是否已存在
//		List<Map<String,Object>> checkStockList=wmsKnStockModifyDao.checkStockList(params);
//		for (Map<String,Object> item : list) {
//			String matnr=item.get("MATNR").toString();
//			String lgort=item.get("LGORT")!=null ? item.get("LGORT").toString() : "";
//			String binCode=item.get("BIN_CODE").toString();
//			String lifnr=item.get("LIFNR")!=null ? item.get("LIFNR").toString() : "";
//			String sobkz=item.get("SOBKZ")!=null ? item.get("SOBKZ").toString() : "";
//			String batch=item.get("BATCH").toString();
//			for(Map<String,Object> stock : checkStockList) {
//				String smatnr=stock.get("MATNR").toString();
//				String slgort=stock.get("LGORT")!=null ? stock.get("LGORT").toString() : "";
//				String sbinCode=stock.get("BIN_CODE").toString();
//				String slifnr=stock.get("LIFNR")!=null ? stock.get("LIFNR").toString() : "";
//				String ssobkz=stock.get("SOBKZ")!=null ? stock.get("SOBKZ").toString() : "";
//				String sbatch=stock.get("BATCH").toString();
//				if(matnr.equals(smatnr) && lgort.equals(slgort)
//					&& lifnr.equals(slifnr) && sobkz.equals(ssobkz)
//					&& batch.equals(sbatch) && binCode.equals(sbinCode)) {
//					throw new RuntimeException("错误行" + item.get("ROW_NO") +":系统对应的库存记录已存在,请选择‘库存修改’导入类型导入");
//				}	
//			}
//		}
		// 保存到库存表
		wmsKnStockModifyDao.saveStockByBatch(list);
		// 保存标签
		List<Map<String,Object>> stockLabelList=saveCoreLabel(list);
		//启用条码管理 需要保存到库存标签表
		if(params.get("barcodeFlag")!=null && params.get("barcodeFlag").toString().equals("X")) {
			wmsKnStockModifyDao.insertWmsCoreStockLabel(stockLabelList);
		}

		return stockLabelList;
	}
	//库存修改
	public void stockModify(Map<String, Object> params,List<Map<String,Object>> list) {
		List<Map<String,Object>> labelList=new ArrayList<Map<String,Object>>();
		for (Map<String,Object> item : list) {
			//List<Map<String,Object>> stockList=wmsKnStockModifyDao.getStockList(item);
			//检查是否已存在该批次
//            if (stockList.size()==0) {
//                throw new RuntimeException("错误行" + item.get("ROW_NO") +":系统库存表未找到对应的修改数据");
//            }
//            if (stockList.size()>1) {
//                throw new RuntimeException("错误行" + item.get("ROW_NO") + ":该记录在系统中存在多条对应的的库存数据");
//            }
//            
//            BigDecimal addQuantity = new BigDecimal(Double.parseDouble(item.get("QTY").toString()));
//            if(item.get("QTY_TYPE").toString().equals("非限制")) {
//            	BigDecimal oldQuantity = new BigDecimal(Double.parseDouble(stockList.get(0).get("STOCK_QTY").toString()));
//                BigDecimal newQuantity = oldQuantity.add(addQuantity);
//                if (newQuantity.compareTo(BigDecimal.ZERO) == -1) {
//                  throw new RuntimeException("错误行" + item.get("ROW_NO") + ":该条记录将导致库存数量为负,系统中库存" + oldQuantity);
//                }
//                item.put("STOCK_QTY", String.valueOf(newQuantity));
//            }
//            if(item.get("QTY_TYPE").toString().equals("冻结")) {
//            	BigDecimal oldQuantity = new BigDecimal(Double.parseDouble(stockList.get(0).get("FREEZE_QTY").toString()));
//                BigDecimal newQuantity = oldQuantity.add(addQuantity);
//                if (newQuantity.compareTo(BigDecimal.ZERO) == -1) {
//                  throw new RuntimeException("错误行" + item.get("ROW_NO") + ":该条记录将导致库存数量为负,系统中库存" + oldQuantity);
//                }
//                item.put("FREEZE_QTY", newQuantity);
//            }
//            item.put("ID", stockList.get(0).get("ID"));

			// 更新标签表【WMS_CORE_LABEL】
			String labelNo=item.get("LABEL_NO")==null?"":item.get("LABEL_NO").toString();
			String [] labelNoArr=labelNo.trim().split(",");
			if(labelNo.trim().length()>0&&labelNoArr.length>0) {
				for(String label : labelNoArr) {
					Map<String,Object> labelMap=new HashMap<String,Object>();
					labelMap.put("LABEL_NO", label.split(":")[0]);
					labelMap.put("BOX_QTY", label.split(":")[1]);
					labelList.add(labelMap);
				}
			}

		}
		// 批量更新库存记录
		wmsKnStockModifyDao.batchUpdateStock(list);
		if(labelList.size()>0) {
			// 批量更新标签记录
			wmsKnStockModifyDao.batchUpdateLabel(labelList);
		}
	}
	//生成批次
	@Override
	public String createBatch(Map<String,Object> params) {
		Map<String, Object> condMap=new HashMap<String, Object>();
		condMap.put("WERKS", params.get("WERKS").toString());
		condMap.put("PRODUCT_DATE", params.get("PRODUCTION_DATE").toString());
		condMap.put("RECEIPT_DATE", params.get("PRODUCTION_DATE").toString());
		condMap.put("MATNR", params.get("MATNR").toString());
		condMap.put("LIFNR", params.get("LIFNR").toString());
		condMap.put("DANGER_FLAG", "0");
		condMap.put("LGORT", params.get("LGORT").toString());

		condMap.put("BUSINESS_NAME", "11"); //库存初始化默认使用生产订单进仓批次规则

		List<Map<String, Object>> condMapList = new ArrayList<Map<String, Object>>();
		condMapList.add(condMap);
		List<Map<String,Object>> retmapList=wmsMatBatchService.getBatch(condMapList);
		if("success".equals(retmapList.get(0).get("MSG"))){
			return retmapList.get(0).get("BATCH").toString();
		}else{
			throw new RuntimeException((String) retmapList.get(0).get("MSG"));
		}
	}
	public List<Map<String, Object>> saveCoreLabel(List<Map<String, Object>> matList) {
		List<Map<String, Object>> skList=new ArrayList<Map<String, Object>>();
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("WMS_DOC_TYPE", "08");//标签号
		params.put("WERKS", matList.get(0).get("WERKS"));

		for(Map<String,Object> m:matList) {
			Double RECEIPT_QTY=0d;
			if(m.get("STOCK_QTY")!=null) {
				RECEIPT_QTY=Double.valueOf(m.get("STOCK_QTY").toString());
			}
			if(m.get("FREEZE_QTY")!=null) {
				RECEIPT_QTY=Double.valueOf(m.get("FREEZE_QTY").toString());
			}
			Double FULL_BOX_QTY=Double.valueOf(m.get("BOX_QTY").toString());
			int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);
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
				Double BOX_QTY=FULL_BOX_QTY;//非尾箱  默认等于满箱数量
				String END_FLAG="0";
				if(i==box_num) {
					END_FLAG="X";
					BOX_QTY=RECEIPT_QTY-FULL_BOX_QTY*(box_num-1);
				}

				sk.put("LABEL_NO", LABEL_NO);
				sk.put("LABEL_STATUS", "01");
				sk.put("RECEIPT_NO", m.get("RECEIPT_NO"));
				sk.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO"));
				sk.put("BOX_SN", BOX_SN);
				sk.put("FULL_BOX_QTY", m.get("BOX_QTY"));
				sk.put("BOX_QTY", BOX_QTY);//装箱数量，计算得出
				sk.put("END_FLAG", END_FLAG);

				sk.put("LABEL_STATUS", "00");
				sk.put("WH_NUMBER", m.get("WH_NUMBER"));
				sk.put("LGORT", m.get("LGORT"));
				sk.put("BIN_CODE", m.get("BIN_CODE"));
				skList.add(sk);
			}
		}
		if(skList.size()>0) {
			wmsKnStockModifyDao.saveCoreLabel(skList);
		}

		return skList;
	}
	// 保存凭证记录；返回凭证号
	public String saveWmsDoc(Map<String,Object> params,List<Map<String,Object>> list) {

		String werks=list.get(0).get("WERKS").toString();
		// 业务类型代码
		List<Map<String,Object>> businessList=wmsKnStockModifyDao.getBusinessCode(werks);
		// 文本值【查询出库存调整的业务类型文本】 查询工厂维度
		List<Map<String,Object>> txtList=wmsKnStockModifyDao.getTxtList(werks);
		if(txtList.size()==0) { // 查询通用业务类型文本
			txtList=wmsKnStockModifyDao.getTxtList("");
		}
		Map<String,Object> ZW_201=null;
		Map<String,Object> ZW_202=null;
		Map<String,Object> ZW_203=null;
		Map<String,Object> ZW_204=null;
		Map<String,Object> ZW_201_TXT=null;
		Map<String,Object> ZW_202_TXT=null;
		Map<String,Object> ZW_203_TXT=null;
		Map<String,Object> ZW_204_TXT=null;
		for(Map<String,Object> bussinessMap : businessList) {
			if(bussinessMap.get("BUSINESS_CODE").toString().equals(ZW_201_CODE)) {
				ZW_201=bussinessMap;
			}
			if(bussinessMap.get("BUSINESS_CODE").toString().equals(ZW_202_CODE)) {
				ZW_202=bussinessMap;
			}
			if(bussinessMap.get("BUSINESS_CODE").toString().equals(ZW_203_CODE)) {
				ZW_203=bussinessMap;
			}
			if(bussinessMap.get("BUSINESS_CODE").toString().equals(ZW_204_CODE)) {
				ZW_204=bussinessMap;
			}
		}
		for(Map<String,Object> txtMap : txtList) {
			if(txtMap.get("BUSINESS_CODE").toString().equals(ZW_201_CODE)) {
				ZW_201_TXT=txtMap;
			}
			if(txtMap.get("BUSINESS_CODE").toString().equals(ZW_202_CODE)) {
				ZW_202_TXT=txtMap;
			}
			if(txtMap.get("BUSINESS_CODE").toString().equals(ZW_203_CODE)) {
				ZW_203_TXT=txtMap;
			}
			if(txtMap.get("BUSINESS_CODE").toString().equals(ZW_204_CODE)) {
				ZW_204_TXT=txtMap;
			}
		}
		// 产生WMS凭证记录 ,返回凭证号
		String WMS_NO=null;
		Map<String,Object> wmsDocHeadMap=new HashMap<String,Object>();
		wmsDocHeadMap.put("PZ_DATE", DateUtils.format(new Date(), "yyyy-MM-dd"));
		wmsDocHeadMap.put("JZ_DATE", DateUtils.format(new Date(), "yyyy-MM-dd"));
		wmsDocHeadMap.put("PZ_YEAR", DateUtils.format(new Date(), "yyyy"));
		wmsDocHeadMap.put("HEADER_TXT", "库存修改");
		wmsDocHeadMap.put("TYPE",  "00");// 标准凭证
		wmsDocHeadMap.put("CREATOR", params.get("USERNAME"));
		wmsDocHeadMap.put("CREATE_DATE", DateUtils.format(new Date(), "yyyy-MM-dd"));
		for(Map<String,Object> map : list) {
			BigDecimal qty=new BigDecimal(map.get("QTY").toString());
			String sobkz=map.get("SOBKZ").toString();
			//  库存类型：Z 库存增加
			if(qty.compareTo(BigDecimal.ZERO) == 1 && sobkz.equals("Z")) {
				if(ZW_201!=null) {
					map.putAll(ZW_201);
				}else {
					throw new RuntimeException(werks+"未配置"+ZW_201_CODE+"工厂业务类型代码");
				}
				map.put("ITEM_TEXT",setTxt(map,ZW_201_TXT));
			}
			//  库存类型：K 库存增加
			if(qty.compareTo(BigDecimal.ZERO) == 1 && sobkz.equals("K")) {
				if(ZW_202!=null) {
					map.putAll(ZW_202);
				}else {
					throw new RuntimeException(werks+"未配置"+ZW_202_CODE+"工厂业务类型代码");
				}
				map.put("ITEM_TEXT",setTxt(map,ZW_202_TXT));
			}
			//  库存类型：Z 库存减少
			if(qty.compareTo(BigDecimal.ZERO) == -1 && sobkz.equals("Z")) {
				if(ZW_203!=null) {
					map.putAll(ZW_203);
				}else {
					throw new RuntimeException(werks+"未配置"+ZW_203_CODE+"工厂业务类型代码");
				}
				map.put("ITEM_TEXT",setTxt(map,ZW_203_TXT));
			}
			// 库存类型：K 库存减少
			if(qty.compareTo(BigDecimal.ZERO) == -1 && sobkz.equals("K")) {
				if(ZW_204!=null) {
					map.putAll(ZW_204);
				}else {
					throw new RuntimeException(werks+"未配置 "+ZW_204_CODE+" 工厂业务类型代码");
				}
				map.put("ITEM_TEXT",setTxt(map,ZW_204_TXT));
			}
			map.put("SAP_FLAG", "00"); // SAP过账标识 00 无需过账 
			map.put("REVERSAL_FLAG", "X");// X否，不可冲销
			map.put("CANCEL_FLAG", "X"); // X否，不可取消
			map.put("QTY_WMS",map.get("QTY"));
		}

		try{
			// 保存凭证，返回凭证号
			WMS_NO= saveWMSDoc2(wmsDocHeadMap,list);
		}catch(Exception e) {
			System.err.println(e.getMessage());
			return e.getMessage();
		}
		return WMS_NO;
	}
	@Override
	public List<Map<String, Object>> getCoreLabelList(Map<String, Object> params) {
		List<WmsCWhEntity> whList=wmsCWhDao.selectList(new EntityWrapper<WmsCWhEntity>().eq("WERKS",params.get("WERKS")).eq("WH_NUMBER",params.get("WH_NUMBER")));
		if(whList.size()>0) {
			WmsCWhEntity k=whList.get(0);
			// 仓库启动标签管理 查询标签数据需要关联库存标签表【WMS_CORE_STOCK_LABEL】
			if(k.getBarcodeFlag()!=null && "X".equals(k.getBarcodeFlag())) {
				return wmsKnStockModifyDao.getCoreStockLabelList(params);
			}
		}
		return wmsKnStockModifyDao.getCoreLabelList(params);
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> saveStockLabel(Map<String, Object> params) {
		Map<String,Object> result=new HashMap<String,Object>();
		String wmsNo=null;
		List<Map<String,Object>> stockList=new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		Gson gson=new Gson();
		String type = (String) params.get("type");
		String showList = (String) params.get("showList");
		if(showList.equals("true")) { //启用条码管理的工厂点击保存按钮触发
			list = gson.fromJson((String) params.get("SAVE_DATA"), new TypeToken<List<Map<String, Object>>>() {
			}.getType());
		}else{ //点击导入按钮触发
			list = (List<Map<String,Object>>)params.get("entityList");
		}
		list.forEach(k -> {
			k = (Map<String, Object>) k;
			k.put("EDITOR", params.get("USERNAME"));
			k.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		});

		// 不同标签号 同工厂、仓库号、物料号、批次、库位、储位、供应商、特殊库存类型数量求和，用于更新库存表、插入冻结记录表
		Map<String, List<Map<String, Object>>> groupByMap = list.stream().collect(Collectors.groupingBy(o -> o.get("WERKS") + "_" + o.get("WH_NUMBER")
				+ "_" + o.get("MATNR") + "_" + o.get("BATCH") + "_" + o.get("LGORT") + "_" + o.get("BIN_CODE") + "_" + o.get("LIFNR") + "_" + o.get("SOBKZ")));
		Iterator entries = groupByMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			List<Map<String, Object>> value = (List<Map<String, Object>>) entry.getValue();
			Double totalQty = value.stream().mapToDouble(m -> Double.parseDouble(m.get("BOX_QTY").toString())).sum();
			Map<String, Object> stockMap = value.get(0);
			stockMap.put("QTY", totalQty);
			stockMap.put("STOCK_QTY", totalQty);
			stockMap.put("EDITOR", params.get("USERNAME"));
			stockMap.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			stockList.add(stockMap);
		}

		//获取最大ID
		int maxId = wmsKnStockModifyDao.getMatBatchId() ;
		for (Map<String, Object> item : stockList) {
			List<Map<String, Object>> coreStockList = wmsKnStockModifyDao.getStockList(item);
			//检查记录是否已存在
			if (coreStockList.size() > 0) {
				throw new RuntimeException("错误行" + item.get("ROW_NO") + ":系统对应的库存数据已存在,不能新增");
			}
			//检查是否已存在该批次,存在则更新或者插入
			// 更新插入：工厂，料号，批次 如果批次流水表相同的数据存在，就更新LAST_INBOUND_DATE为当前时间，如果不存在则插入新的数据,id+1
			if (item.get("BATCH") != null && !item.get("BATCH").toString().isEmpty()) {
				try {
					int stockCount = wmsKnStockModifyDao.getMatBatchCount(item.get("BATCH").toString(), item.get("WERKS").toString(), item.get("MATNR").toString());
					if (stockCount > 0) {
						wmsKnStockModifyDao.updateInboundDate(item.get("BATCH").toString(), item.get("WERKS").toString(), item.get("MATNR").toString());
					} else {
						maxId += 1;
						item.put("maxId",maxId);
						//保存至批次表
						wmsKnStockModifyDao.insertMatBatch(item);
					}
				}catch(Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		}

		// 保存到库存表
		wmsKnStockModifyDao.saveStockByBatch(stockList);

		//启用条码管理 保存到库存标签表
		if(params.get("BARCODE_FLAG")!=null && "X".equals(params.get("BARCODE_FLAG").toString())) {
			wmsKnStockModifyDao.insertWmsCoreStockLabel(list);
		}

		wmsNo=saveWmsDoc(params,stockList);
		for(Map<String,Object>  k:list){
			k.put("WMS_NO", wmsNo);
		}
		try{
		// 分批保存标签
			int length = list.size();
			//每500条插入一次数据
			int pointsDataLimit = 500;
			//判断是否需要分批
			if(pointsDataLimit<length){
				//分批数
				int part = length/pointsDataLimit;
				for (int i = 0; i < part; i++) {
					List<Map<String,Object>> listPage=new ArrayList<Map<String,Object>>();
					listPage = list.subList(0, pointsDataLimit);
					wmsKnStockModifyDao.saveCoreLabel(listPage);
					list.subList(0, pointsDataLimit).clear();
				}
			}
			wmsKnStockModifyDao.saveCoreLabel(list);
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		result.put("labelList", list);
		result.put("wmsNo", wmsNo);
		return result;

	}
	@Override
	public List<Map<String,Object>> checkLabelList(List<String> list) {
		List<Map<String, Object>> list2 =wmsKnStockModifyDao.checkLabelList(list);
		return list2;
	}
	public String setTxt(Map<String,Object> param,Map<String,Object> zwTxtMap){
		String txt=(zwTxtMap!=null && zwTxtMap.get("TXT_RULE_ITEM")!=null) ? zwTxtMap.get("TXT_RULE_ITEM").toString():"";
		String[] txtruleitemArray=StringUtils.substringsBetween(txt, "{", "}");//行文本规则

		for(int m=0;m<txtruleitemArray.length;m++){//头文本替换
			String val=param.get(txtruleitemArray[m])!=null ?param.get(txtruleitemArray[m]).toString() : "";
			txt=txt.replace("{"+txtruleitemArray[m]+"}", val);
		}
		return txt;
	}

	@Override
	public List<Map<String, Object>> checkMaterialList(Map<String, Object> params) {
		return wmsKnStockModifyDao.checkMaterialList(params);
	}

	@Override
	public List<Map<String, Object>> checkVendorList(Map<String, Object> params) {
		return wmsKnStockModifyDao.checkVendorList(params);
	}

	@Override
	public List<Map<String, Object>> checkLgortList(Map<String, Object> params) {
		return wmsKnStockModifyDao.checkLgortList(params);
	}

	@Override
	public List<Map<String, Object>> checkBinList(Map<String, Object> params) {
		return wmsKnStockModifyDao.checkBinList(params);
	}

	@Override
	public List<Map<String, Object>> checkStockList(Map<String, Object> params) {
		return wmsKnStockModifyDao.checkStockList(params);
	}

	/**
	 * 保存WMS凭证记录抬头和明细
	 * @param head PZ_DATE：凭证日期  JZ_DATE：记账日期  HEADER_TXT：头文本 TYPE：凭证类型  SAP_MOVE_TYPE：SAP移动类型
	 *  行项目列表 itemList
	 */
	@SuppressWarnings("unchecked")

	public String saveWMSDoc2(Map<String,Object> head,List<Map<String,Object>> itemList) {
		head.put("WMS_DOC_TYPE", "01");
		Map<String,Object> doc=null;
		String WMS_NO="";//WMS凭证号

		doc=wmsCDocNoService.getDocNo(head);
		if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
			throw new RuntimeException(doc.get("MSG").toString());
		}
		WMS_NO=doc.get("docno").toString();

		Map<String,Object> wms_doc_head=new HashMap<String,Object>();
		wms_doc_head.put("WMS_NO", WMS_NO);
		wms_doc_head.put("PZ_DATE", head.get("PZ_DATE"));
		wms_doc_head.put("JZ_DATE", head.get("JZ_DATE"));
		wms_doc_head.put("PZ_YEAR", head.get("PZ_DATE").toString().substring(0,4));
		String HEADER_TXT=head.get("HEADER_TXT")==null?(String)itemList.get(0).get("HEADER_TXT"):(String)head.get("HEADER_TXT");
		wms_doc_head.put("HEADER_TXT", HEADER_TXT);
		wms_doc_head.put("TYPE",  head.get("TYPE")==null?"00":head.get("TYPE"));//默认为标准凭证
		wms_doc_head.put("CREATOR", head.get("CREATOR"));
		wms_doc_head.put("CREATE_DATE", head.get("CREATE_DATE"));

		wmsKnStockModifyDao.insertWMSDocHead(wms_doc_head);

		//2019-07-03 add by thw 处理委外采购订单进仓交接原材料消耗
		List<Map<String,Object>> allPoComonentList = new ArrayList<>();

		Stream.iterate(0, i -> i + 1).limit(itemList.size()).forEach(i->{
			Map<String,Object> mat=itemList.get(i);
			mat.put("WMS_NO", wms_doc_head.get("WMS_NO"));
			mat.put("WMS_ITEM_NO", i+1);

			String SAP_MOVE_TYPE = "";
			if(mat.get("SAP_MOVE_TYPE")!=null&&!"".equals(mat.get("SAP_MOVE_TYPE").toString().trim())) {
				SAP_MOVE_TYPE = mat.get("SAP_MOVE_TYPE").toString();
			}
			if(StringUtils.isEmpty(SAP_MOVE_TYPE)) {
				SAP_MOVE_TYPE = head.get("SAP_MOVE_TYPE")==null?"":head.get("SAP_MOVE_TYPE").toString();
			}
			String SAP_FLAG="00";//SAP过账标识 00 无需过账 01 过账(包含实时过账和异步过账)
			if(StringUtils.isNotEmpty(SAP_MOVE_TYPE)) {
				SAP_FLAG = "01";
			}

			mat.put("SAP_FLAG", SAP_FLAG);
			mat.put("BUSINESS_NAME", mat.get("BUSINESS_NAME")==null?head.get("BUSINESS_NAME"):mat.get("BUSINESS_NAME"));
			mat.put("BUSINESS_TYPE", mat.get("BUSINESS_TYPE")==null?head.get("BUSINESS_TYPE"):mat.get("BUSINESS_TYPE"));
			mat.put("BUSINESS_CLASS", mat.get("BUSINESS_CLASS")==null?head.get("BUSINESS_CLASS"):mat.get("BUSINESS_CLASS"));
			mat.put("WMS_MOVE_TYPE", mat.get("WMS_MOVE_TYPE")==null?head.get("WMS_MOVE_TYPE"):mat.get("WMS_MOVE_TYPE"));
			mat.put("SAP_MOVE_TYPE", mat.get("SAP_MOVE_TYPE")==null?head.get("SAP_MOVE_TYPE"):mat.get("SAP_MOVE_TYPE"));
			mat.put("REVERSAL_FLAG", mat.get("REVERSAL_FLAG")==null?"0":mat.get("REVERSAL_FLAG"));
			mat.put("CANCEL_FLAG", mat.get("CANCEL_FLAG")==null?"0":mat.get("CANCEL_FLAG"));
			mat.put("SOBKZ", mat.get("SOBKZ")==null?"Z":mat.get("SOBKZ"));
			mat.put("F_WERKS", mat.get("F_WERKS")==null?"":mat.get("F_WERKS"));
			mat.put("F_WH_NUMBER", mat.get("F_WH_NUMBER")==null?"":mat.get("F_WH_NUMBER"));
			mat.put("F_LGORT", mat.get("F_LGORT")==null?"":mat.get("F_LGORT"));
			mat.put("MATNR", mat.get("MATNR")==null?"":mat.get("MATNR"));
			mat.put("MAKTX", mat.get("MAKTX")==null?"":mat.get("MAKTX"));
			mat.put("F_BATCH", mat.get("F_BATCH")==null?"":mat.get("F_BATCH"));
			mat.put("WERKS", mat.get("WERKS")==null?"":mat.get("WERKS"));
			mat.put("WH_NUMBER", mat.get("WH_NUMBER")==null?"":mat.get("WH_NUMBER"));
			mat.put("LGORT", mat.get("LGORT")==null?"":mat.get("LGORT"));
			mat.put("BIN_CODE", mat.get("BIN_CODE")==null?"":mat.get("BIN_CODE"));
			mat.put("UNIT", mat.get("UNIT")==null?"":mat.get("UNIT"));
			mat.put("QTY_WMS", mat.get("QTY_WMS")==null?mat.get("RECEIPT_QTY"):mat.get("QTY_WMS"));
			if(mat.get("SAP_MOVE_TYPE")!=null && !"".equals(mat.get("SAP_MOVE_TYPE"))) {
				mat.put("QTY_SAP", mat.get("QTY_SAP")==null?null:mat.get("QTY_SAP"));
			}else {
				mat.put("QTY_SAP", null);
			}
			mat.put("QTY_CANCEL", mat.get("QTY_CANCEL")==null?"":mat.get("QTY_CANCEL"));
			mat.put("BATCH", mat.get("BATCH")==null?"":mat.get("BATCH"));
			mat.put("BATCH_SAP", mat.get("BATCH_SAP")==null?(mat.get("BATCH")==null?"":mat.get("BATCH")):mat.get("BATCH_SAP"));
			mat.put("BEDNR", mat.get("BEDNR")==null?"":mat.get("BEDNR"));
			mat.put("HANDOVER", mat.get("HANDOVER")==null?"":mat.get("HANDOVER"));
			mat.put("RECEIPT_NO", mat.get("RECEIPT_NO")==null?"":mat.get("RECEIPT_NO"));
			mat.put("RECEIPT_ITEM_NO", mat.get("RECEIPT_ITEM_NO")==null?"":mat.get("RECEIPT_ITEM_NO"));
			mat.put("ASNNO", mat.get("ASNNO")==null?"":mat.get("ASNNO"));
			mat.put("ASNITM", mat.get("ASNITM")==null?"":mat.get("ASNITM"));
			mat.put("PO_NO", mat.get("PO_NO")==null?"":mat.get("PO_NO"));
			mat.put("PO_ITEM_NO", mat.get("PO_ITEM_NO")==null?"":mat.get("PO_ITEM_NO"));
			mat.put("LIFNR", mat.get("LIFNR")==null?"":mat.get("LIFNR"));
			mat.put("LIKTX", mat.get("LIKTX")==null?"":mat.get("LIKTX"));
			mat.put("COST_CENTER", mat.get("COST_CENTER")==null?"":mat.get("COST_CENTER"));

			mat.put("IO_NO", mat.get("IO_NO")==null?"":mat.get("IO_NO"));
			mat.put("WBS", mat.get("WBS")==null?"":mat.get("WBS"));
			mat.put("SAKTO", mat.get("SAKTO")==null?"":mat.get("SAKTO"));
			mat.put("ANLN1", mat.get("ANLN1")==null?"":mat.get("ANLN1"));
			mat.put("PARTNER", mat.get("PARTNER")==null?"":mat.get("PARTNER"));
			mat.put("RECEIVER", mat.get("RECEIVER")==null?"":mat.get("RECEIVER"));
			mat.put("MO_NO", mat.get("MO_NO")==null?"":mat.get("MO_NO"));
			mat.put("MO_ITEM_NO", mat.get("MO_ITEM_NO")==null?"":mat.get("MO_ITEM_NO"));
			mat.put("RSNUM", mat.get("RSNUM")==null?"":mat.get("RSNUM"));
			mat.put("RSPOS", mat.get("RSPOS")==null?"":mat.get("RSPOS"));
			mat.put("SO_NO", mat.get("SO_NO")==null?"":mat.get("SO_NO"));
			mat.put("SO_ITEM_NO", mat.get("SO_ITEM_NO")==null?"":mat.get("SO_ITEM_NO"));
			mat.put("SAP_OUT_NO", mat.get("SAP_OUT_NO")==null?"":mat.get("SAP_OUT_NO"));

			mat.put("SAP_OUT_ITEM_NO", mat.get("SAP_OUT_ITEM_NO")==null?"":mat.get("SAP_OUT_ITEM_NO"));
			mat.put("SAP_MATDOC_NO", mat.get("SAP_MATDOC_NO")==null?"":mat.get("SAP_MATDOC_NO"));
			mat.put("SAP_MATDOC_ITEM_NO", mat.get("SAP_MATDOC_ITEM_NO")==null?"":mat.get("SAP_MATDOC_ITEM_NO"));
			mat.put("REF_WMS_NO", mat.get("REF_WMS_NO")==null?"":mat.get("REF_WMS_NO"));
			mat.put("REF_WMS_ITEM_NO", mat.get("REF_WMS_ITEM_NO")==null?"":mat.get("REF_WMS_ITEM_NO"));
			mat.put("DISTRIBUTION_NO", mat.get("DISTRIBUTION_NO")==null?"":mat.get("DISTRIBUTION_NO"));
			mat.put("DISTRIBUTION_ITEM_NO", mat.get("DISTRIBUTION_ITEM_NO")==null?"":mat.get("DISTRIBUTION_ITEM_NO"));
			mat.put("INSPECTION_NO", mat.get("INSPECTION_NO")==null?"":mat.get("INSPECTION_NO"));
			mat.put("INSPECTION_ITEM_NO", mat.get("INSPECTION_ITEM_NO")==null?"":mat.get("INSPECTION_ITEM_NO"));
			mat.put("RETURN_NO", mat.get("RETURN_NO")==null?"":mat.get("RETURN_NO"));
			mat.put("RETURN_ITEM_NO", mat.get("RETURN_ITEM_NO")==null?"":mat.get("RETURN_ITEM_NO"));
			mat.put("INBOUND_NO", mat.get("INBOUND_NO")==null?"":mat.get("INBOUND_NO"));
			mat.put("INBOUND_ITEM_NO", mat.get("INBOUND_ITEM_NO")==null?"":mat.get("INBOUND_ITEM_NO"));

			mat.put("REQUIREMENT_NO", mat.get("REQUIREMENT_NO")==null?"":mat.get("REQUIREMENT_NO"));
			mat.put("REQUIREMENT_ITEM_NO", mat.get("REQUIREMENT_ITEM_NO")==null?"":mat.get("REQUIREMENT_ITEM_NO"));
			mat.put("PICK_NO", mat.get("PICK_NO")==null?"":mat.get("PICK_NO"));
			mat.put("PICK_ITEM_NO", mat.get("PICK_ITEM_NO")==null?"":mat.get("PICK_ITEM_NO"));

			mat.put("CREATOR", mat.get("CREATOR")==null?head.get("CREATOR"):mat.get("CREATOR"));
			mat.put("CREATE_DATE", mat.get("CREATE_DATE")==null?head.get("CREATE_DATE"):mat.get("CREATE_DATE"));
			mat.put("ITEM_TEXT", mat.get("ITEM_TEXT")==null?"":mat.get("ITEM_TEXT"));
			mat.put("LABEL_NO", mat.get("LABEL_NO")==null?"":mat.get("LABEL_NO"));

			//2019-07-03 add by thw 处理委外采购订单进仓交接原材料消耗
			List<Map<String,Object>> poComonentList = mat.get("poComonentList")==null?null:(List<Map<String,Object>>)mat.get("poComonentList");
			if(poComonentList !=null && poComonentList.size()>0) {
				for (Map<String, Object> poComonentMap : poComonentList) {
					poComonentMap.put("WMS_NO", wms_doc_head.get("WMS_NO"));
					poComonentMap.put("WMS_ITEM_NO", mat.get("WMS_ITEM_NO")==null?0:0-(Integer.parseInt(mat.get("WMS_ITEM_NO").toString())));//peng.tao1 20190820 update 避免和105的行项目重复

					poComonentMap.put("WMS_MOVE_TYPE", poComonentMap.get("WMS_MOVE_TYPE")==null?"":poComonentMap.get("WMS_MOVE_TYPE"));
					poComonentMap.put("SAP_MOVE_TYPE", poComonentMap.get("SAP_MOVE_TYPE")==null?"":poComonentMap.get("SAP_MOVE_TYPE"));
					poComonentMap.put("MATNR", poComonentMap.get("MATN2")==null?"":poComonentMap.get("MATN2"));
					poComonentMap.put("MAKTX", poComonentMap.get("MAKTX2")==null?"":poComonentMap.get("MAKTX2"));
					poComonentMap.put("LGORT", poComonentMap.get("LGORT")==null?"":poComonentMap.get("LGORT"));
					poComonentMap.put("BIN_CODE", poComonentMap.get("BIN_CODE")==null?"":poComonentMap.get("BIN_CODE"));
					poComonentMap.put("UNIT", poComonentMap.get("MEIN2")==null?"":poComonentMap.get("MEIN2"));
					poComonentMap.put("QTY_WMS", poComonentMap.get("MENG2").toString());
					if(mat.get("SAP_MOVE_TYPE")!=null && !"".equals(mat.get("SAP_MOVE_TYPE"))) {
						poComonentMap.put("QTY_SAP", poComonentMap.get("MENG2").toString());
					}else {
						poComonentMap.put("QTY_SAP", null);
					}
					poComonentMap.put("QTY_CANCEL", "");
					poComonentMap.put("BATCH", poComonentMap.get("BATCH")==null?"":poComonentMap.get("BATCH"));
					poComonentMap.put("BATCH_SAP", poComonentMap.get("BATCH_SAP")==null?(poComonentMap.get("BATCH")==null?"":poComonentMap.get("BATCH")):poComonentMap.get("BATCH_SAP"));

					poComonentMap.put("SAP_FLAG", SAP_FLAG);
					poComonentMap.put("BUSINESS_NAME", mat.get("BUSINESS_NAME")==null?head.get("BUSINESS_NAME"):mat.get("BUSINESS_NAME"));
					poComonentMap.put("BUSINESS_TYPE", mat.get("BUSINESS_TYPE")==null?head.get("BUSINESS_TYPE"):mat.get("BUSINESS_TYPE"));
					poComonentMap.put("BUSINESS_CLASS", mat.get("BUSINESS_CLASS")==null?head.get("BUSINESS_CLASS"):mat.get("BUSINESS_CLASS"));
					poComonentMap.put("REVERSAL_FLAG", "X");
					poComonentMap.put("CANCEL_FLAG", "X");
					poComonentMap.put("SOBKZ", "Z");
					poComonentMap.put("F_WERKS", mat.get("F_WERKS")==null?"":mat.get("F_WERKS"));
					poComonentMap.put("F_WH_NUMBER", mat.get("F_WH_NUMBER")==null?"":mat.get("F_WH_NUMBER"));
					poComonentMap.put("F_LGORT", mat.get("F_LGORT")==null?"":mat.get("F_LGORT"));
					poComonentMap.put("F_BATCH", mat.get("F_BATCH")==null?"":mat.get("F_BATCH"));
					poComonentMap.put("WERKS", mat.get("WERKS")==null?"":mat.get("WERKS"));
					poComonentMap.put("WH_NUMBER", mat.get("WH_NUMBER")==null?"":mat.get("WH_NUMBER"));
					poComonentMap.put("BEDNR", mat.get("BEDNR")==null?"":mat.get("BEDNR"));
					poComonentMap.put("HANDOVER", mat.get("HANDOVER")==null?"":mat.get("HANDOVER"));
					poComonentMap.put("RECEIPT_NO", mat.get("RECEIPT_NO")==null?"":mat.get("RECEIPT_NO"));
					poComonentMap.put("RECEIPT_ITEM_NO", mat.get("RECEIPT_ITEM_NO")==null?"":mat.get("RECEIPT_ITEM_NO"));
					poComonentMap.put("ASNNO", mat.get("ASNNO")==null?"":mat.get("ASNNO"));
					poComonentMap.put("ASNITM", mat.get("ASNITM")==null?"":mat.get("ASNITM"));
					poComonentMap.put("PO_NO", mat.get("PO_NO")==null?"":mat.get("PO_NO"));
					poComonentMap.put("PO_ITEM_NO", mat.get("PO_ITEM_NO")==null?"":mat.get("PO_ITEM_NO"));
					poComonentMap.put("LIFNR", mat.get("LIFNR")==null?"":mat.get("LIFNR"));
					poComonentMap.put("LIKTX", mat.get("LIKTX")==null?"":mat.get("LIKTX"));
					poComonentMap.put("COST_CENTER", mat.get("COST_CENTER")==null?"":mat.get("COST_CENTER"));
					poComonentMap.put("IO_NO", mat.get("IO_NO")==null?"":mat.get("IO_NO"));
					poComonentMap.put("WBS", mat.get("WBS")==null?"":mat.get("WBS"));
					poComonentMap.put("SAKTO", mat.get("SAKTO")==null?"":mat.get("SAKTO"));
					poComonentMap.put("ANLN1", mat.get("ANLN1")==null?"":mat.get("ANLN1"));
					poComonentMap.put("PARTNER", mat.get("PARTNER")==null?"":mat.get("PARTNER"));
					poComonentMap.put("RECEIVER", mat.get("RECEIVER")==null?"":mat.get("RECEIVER"));
					poComonentMap.put("MO_NO", mat.get("MO_NO")==null?"":mat.get("MO_NO"));
					poComonentMap.put("MO_ITEM_NO", mat.get("MO_ITEM_NO")==null?"":mat.get("MO_ITEM_NO"));
					poComonentMap.put("RSNUM", mat.get("RSNUM")==null?"":mat.get("RSNUM"));
					poComonentMap.put("RSPOS", mat.get("RSPOS")==null?"":mat.get("RSPOS"));
					poComonentMap.put("CUSTOMER", "");
					poComonentMap.put("SO_NO", mat.get("SO_NO")==null?"":mat.get("SO_NO"));
					poComonentMap.put("SO_ITEM_NO", mat.get("SO_ITEM_NO")==null?"":mat.get("SO_ITEM_NO"));
					poComonentMap.put("SAP_OUT_NO", mat.get("SAP_OUT_NO")==null?"":mat.get("SAP_OUT_NO"));
					poComonentMap.put("SAP_OUT_ITEM_NO", mat.get("SAP_OUT_ITEM_NO")==null?"":mat.get("SAP_OUT_ITEM_NO"));
					poComonentMap.put("SAP_MATDOC_NO", mat.get("SAP_MATDOC_NO")==null?"":mat.get("SAP_MATDOC_NO"));
					poComonentMap.put("SAP_MATDOC_ITEM_NO", mat.get("SAP_MATDOC_ITEM_NO")==null?"":mat.get("SAP_MATDOC_ITEM_NO"));
					poComonentMap.put("REF_WMS_NO", mat.get("REF_WMS_NO")==null?"":mat.get("REF_WMS_NO"));
					poComonentMap.put("REF_WMS_ITEM_NO", mat.get("REF_WMS_ITEM_NO")==null?"":mat.get("REF_WMS_ITEM_NO"));
					poComonentMap.put("DISTRIBUTION_NO", mat.get("DISTRIBUTION_NO")==null?"":mat.get("DISTRIBUTION_NO"));
					poComonentMap.put("DISTRIBUTION_ITEM_NO", mat.get("DISTRIBUTION_ITEM_NO")==null?"":mat.get("DISTRIBUTION_ITEM_NO"));
					poComonentMap.put("INSPECTION_NO", mat.get("INSPECTION_NO")==null?"":mat.get("INSPECTION_NO"));
					poComonentMap.put("INSPECTION_ITEM_NO", mat.get("INSPECTION_ITEM_NO")==null?"":mat.get("INSPECTION_ITEM_NO"));
					poComonentMap.put("RETURN_NO", mat.get("RETURN_NO")==null?"":mat.get("RETURN_NO"));
					poComonentMap.put("RETURN_ITEM_NO", mat.get("RETURN_ITEM_NO")==null?"":mat.get("RETURN_ITEM_NO"));
					poComonentMap.put("INBOUND_NO", mat.get("INBOUND_NO")==null?"":mat.get("INBOUND_NO"));
					poComonentMap.put("INBOUND_ITEM_NO", mat.get("INBOUND_ITEM_NO")==null?"":mat.get("INBOUND_ITEM_NO"));
					poComonentMap.put("REQUIREMENT_NO", mat.get("REQUIREMENT_NO")==null?"":mat.get("REQUIREMENT_NO"));
					poComonentMap.put("REQUIREMENT_ITEM_NO", mat.get("REQUIREMENT_ITEM_NO")==null?"":mat.get("REQUIREMENT_ITEM_NO"));
					poComonentMap.put("PICK_NO", mat.get("PICK_NO")==null?"":mat.get("PICK_NO"));
					poComonentMap.put("PICK_ITEM_NO", mat.get("PICK_ITEM_NO")==null?"":mat.get("PICK_ITEM_NO"));
					poComonentMap.put("CREATOR", mat.get("CREATOR")==null?head.get("CREATOR"):mat.get("CREATOR"));
					poComonentMap.put("CREATE_DATE", mat.get("CREATE_DATE")==null?head.get("CREATE_DATE"):mat.get("CREATE_DATE"));
					poComonentMap.put("ITEM_TEXT", mat.get("ITEM_TEXT")==null?"":mat.get("ITEM_TEXT"));
					poComonentMap.put("LABEL_NO", "");
					allPoComonentList.add(poComonentMap);
				}
			}
		});
		if(allPoComonentList.size()>0) {
			itemList.addAll(allPoComonentList);
		}

		//commonDao.insertWMSDocDetail(itemList);
		//2019-09-20 优化 解决ORACLE数据库插入SQL临时变量太多导致数据库服务异常问题， 异常数据：行项目有1082行 每行数据需要76个临时变量 超过了 ORALCE数据设置的最大变量值6.5万个
		int pointsDataLimit = 500;//限制条数
		Integer size = itemList.size();
		//判断是否有必要分批
		if(pointsDataLimit<size){
			int part = size/pointsDataLimit;//分批数
			System.out.println("共有 ： "+size+"条，！"+" 分为 ："+part+"批  insertOrUpdateSapMoItem");
			for (int i = 0; i < part; i++) {
				//1000条
				List<Map<String,Object>> listPage = itemList.subList(0, pointsDataLimit);
				wmsKnStockModifyDao.insertWMSDocDetail(listPage);
				//剔除
				itemList.subList(0, pointsDataLimit).clear();
			}
			if(!itemList.isEmpty()){//表示最后剩下的数据
				wmsKnStockModifyDao.insertWMSDocDetail(itemList);
			}
		}else {
			try {
				wmsKnStockModifyDao.insertWMSDocDetail(itemList);
			}catch(Exception e) {

				return e.getMessage();
			}
		}

		return WMS_NO;
	}
}
