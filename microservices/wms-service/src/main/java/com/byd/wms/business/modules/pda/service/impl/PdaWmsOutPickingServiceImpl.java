package com.byd.wms.business.modules.pda.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.ListUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.dao.WmsCoreWHTaskDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreStorageSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsSBestRuleEntity;
import com.byd.wms.business.modules.config.service.WmsCControlSearchService;
import com.byd.wms.business.modules.config.service.WmsCMatFixedStorageService;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsCoreStorageSearchService;
import com.byd.wms.business.modules.config.service.WmsCoreWhAreaService;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.config.service.WmsSBestRuleService;
import com.byd.wms.business.modules.out.dao.WmsOutPickingDao;
import com.byd.wms.business.modules.out.service.CreateRequirementService;
import com.byd.wms.business.modules.pda.dao.WmsPickingDao;
import com.byd.wms.business.modules.pda.service.PdaWmsOutPickingService;

@Service("PdaWmsOutPickingService")
public class PdaWmsOutPickingServiceImpl implements PdaWmsOutPickingService {

	@Autowired
	private WmsOutPickingDao wmsOutPickingDao;
	@Autowired
	private WarehouseTasksService warehouseTasksService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private CreateRequirementService createRequirementService;
	@Autowired
	private WmsCoreWHTaskDao wmsCoreWHTaskDao;
	@Autowired
	private WmsSBestRuleService wmsSBestRuleService;
	@Autowired
	private WmsCControlSearchService wmsCControlSearchService;
	@Autowired
	private WmsCMatStorageService wmsCMatStorageService;
	@Autowired
	private WmsCoreStorageSearchService wmsCoreStorageSearchService;
	@Autowired
	private WmsCoreWhAreaService wmsCoreWhAreaService;
	@Autowired
	private WmsCMatFixedStorageService wmsCMatFixedStorageService;
	@Autowired
	private WmsCoreWhBinService wmsCoreWhBinService;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsPickingDao wmsPickingDao;

	/**
	 * G需求数量已部分推荐的行，再推荐时需要拆分未推荐数量
	 * 
	 * @param lists
	 * @return
	 */
	public static List<Map<String, Object>> wtSplit(List<Map<String, Object>> lists) {
		List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> list : lists) {
			String reqno = list.get("REQUIREMENT_NO").toString();
			String reqitem = list.get("REQUIREMENT_ITEM_NO").toString();
			int flag = 0;// 0为新增数据，1为增加

			for (Map<String, Object> clist : countList) {
				String reqnotemp = clist.get("REQUIREMENT_NO").toString();
				String reqitemtemp = clist.get("REQUIREMENT_ITEM_NO").toString();

				if (reqno.equals(reqnotemp) && reqitem.equals(reqitemtemp) && clist.get("QUANTITY") == null) {
					BigDecimal lqty = list.get("QUANTITY") == null ? BigDecimal.ZERO
							: new BigDecimal(list.get("QUANTITY").toString());
					BigDecimal cqty = clist.get("REQ_QTY") == null ? BigDecimal.ZERO
							: new BigDecimal(clist.get("REQ_QTY").toString());
					BigDecimal reqty = cqty.subtract(lqty);
					if (reqty.compareTo(BigDecimal.ZERO) <= 0) {
						countList.remove(clist);
					} else {
						clist.put("REQ_QTY", reqty);
					}

					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				Map<String, Object> dataMapTemp = new HashMap<String, Object>();

				dataMapTemp.putAll(list);
				BigDecimal lqty = dataMapTemp.get("QUANTITY") == null ? BigDecimal.ZERO
						: new BigDecimal(dataMapTemp.get("QUANTITY").toString());
				BigDecimal cqty = dataMapTemp.get("QTY") == null ? BigDecimal.ZERO
						: new BigDecimal(dataMapTemp.get("QTY").toString());
				BigDecimal reqty = cqty.subtract(lqty);

				if (lqty.compareTo(BigDecimal.ZERO) > 0) { // 已推荐仓库任务的部分
					countList.add(list);
				}
				if (reqty.compareTo(BigDecimal.ZERO) > 0 && lqty.compareTo(BigDecimal.ZERO) > 0) { // 部分推荐的，剩余数量拆分出来
					dataMapTemp.put("REQ_QTY", reqty);
					dataMapTemp.put("QUANTITY", null);
					dataMapTemp.put("TASK_NUM", null);
					dataMapTemp.put("RECOMMEND_QTY", null);
					dataMapTemp.put("REQ_ITEM_STATUS", 00);
					dataMapTemp.put("STORAGE_AREA_CODE", null);
					dataMapTemp.put("BIN_CODE", null);
					dataMapTemp.put("BATCH", null);
					dataMapTemp.put("LGORT", null);
					dataMapTemp.put("SOBKZ", null);
					dataMapTemp.put("LIFNR", null);
					countList.add(dataMapTemp);
				} else if (reqty.compareTo(BigDecimal.ZERO) > 0 && lqty.compareTo(BigDecimal.ZERO) <= 0) { // 需求行未推荐过的
					dataMapTemp.put("REQ_QTY", reqty);
					countList.add(dataMapTemp);
				}

			} else {
				BigDecimal lqty = list.get("QUANTITY") == null ? BigDecimal.ZERO
						: new BigDecimal(list.get("QUANTITY").toString());
				if (lqty.compareTo(BigDecimal.ZERO) > 0) {
					countList.add(list);
				}
			}
		}

		return countList;
	}

	/**
	 * PDA推荐储位
	 */
	@Override
	public List pdaRecommend(Map<String, Object> params) {
		List<Map<String, Object>> list = wmsOutPickingDao.selectRequirement(params);
		return searchBinForPick(wtSplit(list));
	}

	/**
	 * PDA下架
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void pdaPicking(Map<String, Object> params) {
    	String saveData = params.get("SAVE_DATA").toString();
    	String whcode = params.get("WH_NUMBER").toString();
    	String werks = params.get("WERKS").toString();
    	String requirementNo = params.get("REQUIREMENT_NO").toString();
    	String addlist = params.get("addList").toString();
		String modifylist = params.get("modifyList").toString();
    	Map<String,Object> user = userUtils.getUser();
    	
    	List<Map<String, Object>> saveDataList = (List<Map<String, Object>>) JSONArray.parse(saveData);
		List<Map<String, Object>> addList = (List<Map<String, Object>>) JSONArray.parse(addlist);
		List<Map<String, Object>> modifyList = (List<Map<String, Object>>) JSONArray.parse(modifylist);
    	
    	List<Map<String, Object>> updateWTList = new ArrayList<Map<String, Object>>(); //更新仓库任务准备
    	List<Map<String, Object>> addWTList = new ArrayList<Map<String, Object>>(); //新增仓库任务准备
    	List<Map<String, Object>> binList = new ArrayList<Map<String, Object>>(); //更新储位占用单元准备
    	List<Map<String, Object>> queryStockParams = new ArrayList<Map<String, Object>>(); //库存查询条件
    	
    	if(saveDataList.size() < 1) {
    		throw new IllegalArgumentException("数据为空");
    	}
    	
    	for (Map<String, Object> param : saveDataList) {
    		param.put("WH_NUMBER", whcode);
    		param.put("FROM_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
    		param.put("FROM_BIN_CODE", param.get("BIN_CODE"));
//    		param.put("TO_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
    		BigDecimal recommendQty = param.get("RECOMMEND_QTY") == null ? BigDecimal.ZERO:new BigDecimal(param.get("RECOMMEND_QTY").toString());
    		BigDecimal confirmQty = param.get("CONFIRM_QUANTITY") == null || param.get("CONFIRM_QUANTITY").equals("")? BigDecimal.ZERO:new BigDecimal(param.get("CONFIRM_QUANTITY").toString());
    		
    		BigDecimal taskqty = param.get("QUANTITY") == null || param.get("QUANTITY").equals("") ? BigDecimal.ZERO:new BigDecimal(param.get("QUANTITY").toString());
    		if (recommendQty.compareTo(BigDecimal.ZERO) == 0) {
    			throw new IllegalArgumentException("下架数量不能为0！");
    		}
    		param.put("CONFIRM_QUANTITY", recommendQty);
    		if ((recommendQty.add(confirmQty)).compareTo(taskqty) < 0) {
    			param.put("WT_STATUS", "01"); //部分下架
    		} else {
    			param.put("WT_STATUS", "02"); //已下架
    		}
    		param.put("CONFIRMOR", user.get("USERNAME"));
    		param.put("CONFIRM_TIME", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		param.put("EDITOR", user.get("USERNAME"));
    		param.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    		param.put("REFERENCE_DELIVERY_NO", requirementNo);
    		param.put("REFERENCE_DELIVERY_ITEM", param.get("REQUIREMENT_ITEM_NO"));
    		
    		if (param.get("TASK_NUM").equals("")) {
	    		param.put("TO_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
	    		param.put("TO_BIN_CODE", "BBBB");
	    		param.put("PROCESS_TYPE", "01");
	    		param.put("QUANTITY", recommendQty);
	    		param.put("WT_STATUS", "02"); //已下架
	    		if (param.get("HX_FLAG") != null && !param.get("HX_FLAG").equals("")) {
	    			param.put("HX_FLAG", "1");
	    		}
	    		param.put("CREATOR", user.get("USERNAME"));
	    		param.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    		param.put("WERKS", werks);
	    		
    			addWTList.add(param);
    		} else {
    			updateWTList.add(param);
    		}
    		
    		Map<String, Object> stockMat = new HashMap<String, Object>();
    		stockMat.put("WERKS", werks);
    		stockMat.put("WH_NUMBER", whcode);
    		stockMat.put("MATNR", param.get("MATNR"));
    		stockMat.put("LGORT", param.get("LGORT"));
    		stockMat.put("BIN_CODE", param.get("BIN_CODE"));
    		stockMat.put("BATCH", param.get("BATCH"));
    		stockMat.put("SOBKZ", param.get("SOBKZ"));
    		stockMat.put("LIFNR", param.get("LIFNR"));
    		queryStockParams.add(stockMat);
    		
    	}
    	
    	
		List<Map<String, Object>> labList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> o : saveDataList) {
			JSONArray labarr = JSON.parseArray(o.get("LABEL").toString());
			for (int i = 0; i < labarr.size(); i++) {
				Map<String, Object> labMap = new HashMap<String, Object>();
				labMap.put("LABEL_NO", labarr.get(i).toString()); // LABEL_NO
				labMap.put("WMS_NO", o.get("REQUIREMENT_NO").toString()); // WMS凭证号
				labMap.put("WMS_ITEM_NO", o.get("REQUIREMENT_ITEM_NO").toString()); // 凭证行项目
				labMap.put("LABEL_STATUS", "09");
				labList.add(labMap);
			}
		}
				
		if (labList.size() > 0)
			commonService.updateLabel(labList);
		if(modifyList.size()>0) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < modifyList.size(); i++) {
					Map<String, Object> labMap = new HashMap<String, Object>();
					JSONObject object = (JSONObject) modifyList.get(i);
					labMap.put("LABEL_NO", object.getString("labelNo")); // LABEL_NO
					labMap.put("BOX_QTY", object.getString("BOX_QTY"));
					list.add(labMap);
				}
				commonService.updateLabel(list);
		}
		if(addList.size()>0) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> o  = saveDataList.get(0);
				for (int i = 0; i < addList.size(); i++) {
					Map<String, Object> labMap = new HashMap<String, Object>();
					JSONObject object = (JSONObject) addList.get(i);
					labMap.put("LABEL_NO", object.getString("LABEL_NO")); // LABEL_NO
					labMap.put("BOX_QTY", object.getString("QTY"));
					labMap.put("FULL_BOX_QTY", object.getString("FULL_BOX_QTY"));
					labMap.put("F_LABEL_NO", object.getString("F_LABEL_NO"));
					labMap.put("BOX_SN", object.getString("BOX_SN"));
					labMap.put("WMS_NO", o.get("REQUIREMENT_NO")); // WMS凭证号
					labMap.put("WMS_ITEM_NO", o.get("REQUIREMENT_ITEM_NO")); // 凭证行项目
					labMap.put("LABEL_STATUS", "09");
					labMap.put("BATCH", object.getString("BATCH"));
					labMap.put("BIN_CODE", object.getString("BIN_CODE"));
					labMap.put("DEL", object.getString("DEL"));
					labMap.put("DOSAGE", object.getString("DOSAGE"));
					labMap.put("LGORT", object.getString("LGORT"));
					labMap.put("LIFNR", object.getString("LIFNR"));
					labMap.put("LIKTX", object.getString("LIKTX"));
					labMap.put("MAKTX", object.getString("MAKTX"));
					labMap.put("MATNR", object.getString("MATNR"));
					labMap.put("SOBKZ", object.getString("SOBKZ"));
					labMap.put("STATION", object.getString("STATION"));
					labMap.put("UNIT", object.getString("UNIT"));
					labMap.put("WERKS", object.getString("WERKS"));
					labMap.put("WH_NUMBER", object.getString("WH_NUMBER"));
					list.add(labMap);
				}
				wmsPickingDao.insertCoreLabel(list);
		}	
    	
    	//更新仓库任务
    	ListUtils.sort(updateWTList, "TASK_NUM", true);
    	warehouseTasksService.updateCoreWHTask(updateWTList);
    	
    	//有新增的,保存任务
    	if (addWTList.size() > 0) {
    		warehouseTasksService.saveWHTask(addWTList);
    	}
    	
    	//更新需求
    	createRequirementService.updateRequirement(saveDataList);
    	
    	//总装配送需求,工厂间调拨301（总装）
		if("77".equals(saveDataList.get(0).get("BUSINESS_NAME")) && "19".equals(saveDataList.get(0).get("BUSINESS_TYPE"))){
			wmsOutPickingDao.updateCallMaterial(params);
    	}
    	
    	List<Map<String, Object>> stockMatList = new ArrayList<Map<String, Object>>();
    	
    	//START 更新库存,减预留数量，增加下架数量。  
    	List<Map<String, Object>> bStockMatList = commonService.getWmsStock(queryStockParams);
    	for (Map<String, Object> param : saveDataList) {
    		Map<String, Object> stockMat = new HashMap<String, Object>();
    		stockMat.put("WERKS", werks);
    		stockMat.put("WH_NUMBER", whcode);
    		stockMat.put("MATNR", param.get("MATNR"));
    		stockMat.put("LGORT", param.get("LGORT"));
    		stockMat.put("BIN_CODE", param.get("BIN_CODE"));
    		stockMat.put("BATCH", param.get("BATCH"));
    		stockMat.put("SOBKZ", param.get("SOBKZ"));
    		stockMat.put("LIFNR", param.get("LIFNR"));
    		BigDecimal recommendQty = param.get("RECOMMEND_QTY") == null ? BigDecimal.ZERO:new BigDecimal(param.get("RECOMMEND_QTY").toString());
    		
    		boolean stockflag = false;
    		for (Map<String, Object> bstockmat:bStockMatList) {
    			if (param.get("MATNR").equals(bstockmat.get("MATNR")) && param.get("LGORT").equals(bstockmat.get("LGORT")) 
    				&& param.get("BIN_CODE").equals(bstockmat.get("BIN_CODE")) 
    				&& (param.get("BATCH") == null || param.get("BATCH").equals("") ? true:param.get("BATCH").equals(bstockmat.get("BATCH")))
    				&& (param.get("SOBKZ") == null || param.get("SOBKZ").equals("") ? true:param.get("SOBKZ").equals(bstockmat.get("SOBKZ")))
    				&& (param.get("LIFNR") == null || param.get("LIFNR").equals("") ? true:param.get("LIFNR").equals(bstockmat.get("LIFNR")))) {
    					
    				Map<String, Object> uStockMat = new HashMap<String,Object>();
    				uStockMat.putAll(bstockmat);
    				uStockMat.put("STOCK_QTY", ""); 
    				uStockMat.put("XJ_QTY", recommendQty);
    				uStockMat.put("VIRTUAL_QTY", "");
    				uStockMat.put("VIRTUAL_LOCK_QTY", "");
    				uStockMat.put("LOCK_QTY", "");
    				uStockMat.put("FREEZE_QTY", "");
    				uStockMat.put("RSB_QTY", recommendQty.negate());
    				uStockMat.put("EDITOR", user.get("USERNAME"));
    				uStockMat.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    				stockMatList.add(uStockMat);
    	    		stockflag = true;
    	    		break;
    			}
    		}
    		if (!stockflag) {
    			throw new IllegalArgumentException("找不到库存，下架失败！");
    		}
    	}
    	
    	if (stockMatList.size() > 0) {
	    	ListUtils.sort(stockMatList, "ID", true); //先排序防死锁
	    	commonService.modifyWmsStock(stockMatList);
    	}
    	//END 更新库存,减预留数量，增加下架数量。  
    	
    	//更新仓位占用存储单元
    	for (Map<String, Object> param : saveDataList) {
    		Map<String, Object> bin = new HashMap<String, Object>();
    		bin.put("WH_NUMBER", whcode);
    		bin.put("MATNR", param.get("MATNR"));
    		bin.put("FROM_BIN_CODE", param.get("FROM_BIN_CODE").toString());
	    	bin.put("CONFIRM_QUANTITY", param.get("CONFIRM_QUANTITY"));
	    	binList.add(bin);
    	}
    	wmsCoreWhBinService.updateBinStorageUnit(binList);

	}

	public List<Map<String, Object>> searchBinForPick(List<Map<String, Object>> params) {
		List<Map<String, Object>> returnParams = new ArrayList<Map<String, Object>>();
		BigDecimal tqty = BigDecimal.ZERO;
		for (Map<String, Object> dataMap : params) {
			String whNumber = dataMap.get("WH_NUMBER") == null ? "" : dataMap.get("WH_NUMBER").toString();
			String werks = dataMap.get("WERKS") == null ? "" : dataMap.get("WERKS").toString();
			String matnr = dataMap.get("MATNR") == null ? "" : dataMap.get("MATNR").toString();
			String lgort = dataMap.get("LGORT") == null ? "" : dataMap.get("LGORT").toString(); // 库位
			String sobkz = dataMap.get("SOBKZ") == null ? "" : dataMap.get("SOBKZ").toString(); // 库存类型
			String lifnr = dataMap.get("LIFNR") == null ? "" : dataMap.get("LIFNR").toString(); // 供应商
			String hxFlag = dataMap.get("HX_FLAG") == null ? "" : dataMap.get("HX_FLAG").toString(); // 核销标识

			BigDecimal reqQty = dataMap.get("REQ_QTY") == null ? BigDecimal.ZERO
					: new BigDecimal(dataMap.get("REQ_QTY").toString()); // 需求数量

			if (reqQty.compareTo(BigDecimal.ZERO) > 0) {

				// 获取仓库最优出库规则
				List<WmsSBestRuleEntity> list = wmsSBestRuleService.selectList(new EntityWrapper<WmsSBestRuleEntity>()
						.eq(StringUtils.isNotBlank(whNumber), "WH_NUMBER", whNumber).eq("RULE_TYPE", "2").eq("DEL", "0")
						.orderBy("SEQNO"));

				if (list.size() > 0) {
					loop1: for (WmsSBestRuleEntity bestRuleEntity : list) {
						String outControl = "";
						String businessType = "";
						String lgortFlag = "";
						String sobkzFlag = "";

						if (bestRuleEntity.getControlFlag().equals("Y")) {
							// 取物料仓库主数据中的出库标识
							WmsCMatStorageEntity matStorageEntity = wmsCMatStorageService
									.selectOne(new EntityWrapper<WmsCMatStorageEntity>()
											.eq(StringUtils.isNotBlank(whNumber), "WH_NUMBER", whNumber)
											.eq(StringUtils.isNotBlank(werks), "WERKS", werks)
											.eq(StringUtils.isNotBlank(matnr), "MATNR", matnr).eq("DEL", "0"));

							if (matStorageEntity != null) {
								outControl = matStorageEntity.getOutControlFlag(); // 控制标识
							}
						}
						if (bestRuleEntity.getBusinessTypeFlag().equals("Y")) {
							businessType = dataMap.get("BUSINESS_NAME") == null ? ""
									: dataMap.get("BUSINESS_NAME").toString(); // WMS业务类型名称
						}
						if (bestRuleEntity.getLgortFlag().equals("Y")) {
							lgortFlag = lgort; // 库位标记
						}
						if (bestRuleEntity.getStockTypeFlag().equals("Y")) {
							sobkzFlag = sobkz; // 库存类型标记
						}

						// 根据最优出库规则,获取存储类型搜索顺序
						WmsCControlSearchEntity controlSearchEntity = wmsCControlSearchService
								.selectOne(new EntityWrapper<WmsCControlSearchEntity>()
										.eq(StringUtils.isNotBlank(whNumber), "WH_NUMBER", whNumber)
										.eq(StringUtils.isNotBlank(outControl), "CONTROL_FLAG", outControl)
										.eq(StringUtils.isNotBlank(businessType), "BUSINESS_NAME", businessType)
										.eq(StringUtils.isNotBlank(lgortFlag), "LGORT", lgortFlag)
										.eq(StringUtils.isNotBlank(sobkzFlag), "SOBKZ", sobkzFlag)
										.isNull(StringUtils.isBlank(outControl), "CONTROL_FLAG")
										.isNull(StringUtils.isBlank(businessType), "BUSINESS_NAME")
										.isNull(StringUtils.isBlank(lgortFlag), "LGORT")
										.isNull(StringUtils.isBlank(sobkzFlag), "SOBKZ").eq("FLAG_TYPE", "01")
										.eq("DEL", "0"));

						if (controlSearchEntity != null) {
							String searchSeq = controlSearchEntity.getStorageAreaSearch(); // 存储类型搜索顺序

							// 获取存储区集合
							List<WmsCoreStorageSearchEntity> storageSearchlist = wmsCoreStorageSearchService
									.selectList(new EntityWrapper<WmsCoreStorageSearchEntity>()
											.eq(StringUtils.isNotBlank(whNumber), "WH_NUMBER", whNumber)
											.eq(StringUtils.isNotBlank(searchSeq), "SEARCH_SEQ", searchSeq)
											.eq("DEL", "0").orderBy("SEQNO"));

							if (storageSearchlist.size() > 0) {
								for (WmsCoreStorageSearchEntity storageSearch : storageSearchlist) {
									String storageAreaCode = storageSearch.getStorageAreaCode();

									Map<String, Object> findParams = new HashMap<>();
									findParams.put("whNumber", whNumber);
									findParams.put("werks", werks);
									findParams.put("matnr", matnr);
									findParams.put("storageAreaCode", storageAreaCode);
									findParams.put("lgort", lgort);
									findParams.put("sobkz", sobkz);
									findParams.put("lifnr", lifnr);
									if (!hxFlag.equals("")) {
										findParams.put("hxFlag", hxFlag);
									}

									// 搜索库存，暂时默认根据批次先进先出推荐。后续用出库规则排序
									List<Map<String, Object>> stockList = commonService.searchBinForPick(findParams);
									if (stockList.size() > 0) {
										for (Map<String, Object> stock : stockList) {
											BigDecimal stockQty = stock.get("STOCK_QTY") == null ? BigDecimal.ZERO
													: new BigDecimal(stock.get("STOCK_QTY").toString()); // 仓位现有库存数量
											stockQty = stockQty.subtract(tqty);
											if (!hxFlag.equals("")) {
												stockQty = stock.get("LOCK_QTY") == null ? BigDecimal.ZERO
														: new BigDecimal(stock.get("LOCK_QTY").toString());
											}

											if (reqQty.compareTo(stockQty) <= 0) {
												Map<String, Object> dataMapTemp = new HashMap<String, Object>();
												dataMapTemp.putAll(dataMap);
												dataMapTemp.put("STORAGE_AREA_CODE", stock.get("STORAGE_AREA_CODE"));
												dataMapTemp.put("BIN_CODE", stock.get("BIN_CODE"));
												dataMapTemp.put("BATCH", stock.get("BATCH"));
												dataMapTemp.put("QUANTITY", reqQty);
												dataMapTemp.put("RECOMMEND_QTY", reqQty);
												dataMapTemp.put("LGORT", stock.get("LGORT"));
												dataMapTemp.put("SOBKZ", stock.get("SOBKZ"));
												dataMapTemp.put("LIFNR", stock.get("LIFNR"));
												dataMapTemp.put("REQ_ITEM_STATUS", "99");
												if (!hxFlag.equals("")) {
													dataMapTemp.put("HX_QTY_FLAG", "1"); // 核销标记，1、扣减锁定库存；2、扣除非限制库存
												}
												returnParams.add(dataMapTemp);

												reqQty = BigDecimal.ZERO;
												break loop1;
											} else {
												Map<String, Object> dataMapTemp = new HashMap<String, Object>();
												dataMapTemp.putAll(dataMap);
												dataMapTemp.put("STORAGE_AREA_CODE", stock.get("STORAGE_AREA_CODE"));
												dataMapTemp.put("BIN_CODE", stock.get("BIN_CODE"));
												dataMapTemp.put("BATCH", stock.get("BATCH"));
												dataMapTemp.put("QUANTITY", stockQty);
												dataMapTemp.put("RECOMMEND_QTY", stockQty);
												dataMapTemp.put("LGORT", stock.get("LGORT"));
												dataMapTemp.put("SOBKZ", stock.get("SOBKZ"));
												dataMapTemp.put("LIFNR", stock.get("LIFNR"));
												dataMapTemp.put("REQ_ITEM_STATUS", "99");
												if (!hxFlag.equals("")) {
													dataMapTemp.put("HX_QTY_FLAG", "1"); // 核销标记，1、扣减锁定库存；2、扣除非限制库存
												}
												returnParams.add(dataMapTemp);

												reqQty = reqQty.subtract(stockQty);
											}
										}
									}
									// 核销业务，锁定库存不足时，扣除非限制库存
									if (!hxFlag.equals("")) {
										findParams.put("hxFlag", null);
										List<Map<String, Object>> stockListhx = commonService
												.searchBinForPick(findParams); // 再次搜索库存
										if (stockListhx.size() > 0) {
											for (Map<String, Object> stock : stockListhx) {
												BigDecimal stockQty = stock.get("STOCK_QTY") == null ? BigDecimal.ZERO
														: new BigDecimal(stock.get("STOCK_QTY").toString()); // 仓位现有库存数量

												if (reqQty.compareTo(stockQty) <= 0) {
													Map<String, Object> dataMapTemp = new HashMap<String, Object>();
													dataMapTemp.putAll(dataMap);
													dataMapTemp.put("STORAGE_AREA_CODE",
															stock.get("STORAGE_AREA_CODE"));
													dataMapTemp.put("BIN_CODE", stock.get("BIN_CODE"));
													dataMapTemp.put("BATCH", stock.get("BATCH"));
													dataMapTemp.put("QUANTITY", reqQty);
													dataMapTemp.put("RECOMMEND_QTY", reqQty);
													dataMapTemp.put("LGORT", stock.get("LGORT"));
													dataMapTemp.put("SOBKZ", stock.get("SOBKZ"));
													dataMapTemp.put("LIFNR", stock.get("LIFNR"));
													dataMapTemp.put("REQ_ITEM_STATUS", "99");
													dataMapTemp.put("HX_QTY_FLAG", "2"); // 核销标记，1、扣减锁定库存；2、扣除非限制库存
													returnParams.add(dataMapTemp);

													reqQty = BigDecimal.ZERO;
													break loop1;
												} else {
													Map<String, Object> dataMapTemp = new HashMap<String, Object>();
													dataMapTemp.putAll(dataMap);
													dataMapTemp.put("STORAGE_AREA_CODE",
															stock.get("STORAGE_AREA_CODE"));
													dataMapTemp.put("BIN_CODE", stock.get("BIN_CODE"));
													dataMapTemp.put("BATCH", stock.get("BATCH"));
													dataMapTemp.put("QUANTITY", stockQty);
													dataMapTemp.put("RECOMMEND_QTY", stockQty);
													dataMapTemp.put("LGORT", stock.get("LGORT"));
													dataMapTemp.put("SOBKZ", stock.get("SOBKZ"));
													dataMapTemp.put("LIFNR", stock.get("LIFNR"));
													dataMapTemp.put("REQ_ITEM_STATUS", "99");
													dataMapTemp.put("REQ_ITEM_STATUS", "99");
													dataMapTemp.put("HX_QTY_FLAG", "2"); // 核销标记，1、扣减锁定库存；2、扣除非限制库存
													returnParams.add(dataMapTemp);

													reqQty = reqQty.subtract(stockQty);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				if (reqQty.compareTo(BigDecimal.ZERO) > 0) {
					Map<String, Object> dataMapTemp = new HashMap<String, Object>();
					dataMapTemp.putAll(dataMap);
					dataMapTemp.put("QTY", reqQty);
					dataMapTemp.put("QUANTITY", reqQty);
					dataMapTemp.put("MSG", matnr + ":未满足数量" + reqQty);
					returnParams.add(dataMapTemp);
				}
			} else {
				BigDecimal QUANTITY = new BigDecimal(dataMap.get("QUANTITY").toString());
				tqty.add(QUANTITY);
				returnParams.add(dataMap);
			}
		}
		return returnParams;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String getLabel(Map<String, Object> params) {
		params.put("WMS_DOC_TYPE", "08");
		Map<String, Object> docNo = wmsCDocNoService.getDocNo(params);
		if (docNo.get("MSG") != null && !"success".equals(docNo.get("MSG"))) {
			throw new RuntimeException(docNo.get("MSG").toString());
		}
		return docNo.get("docno").toString();
	}
}
