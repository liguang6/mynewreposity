package com.byd.wms.business.modules.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.ListUtils;
import com.byd.wms.business.modules.common.dao.WmsCoreWHTaskDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatFixedStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreStorageSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
import com.byd.wms.business.modules.config.entity.WmsSBestRuleEntity;
import com.byd.wms.business.modules.config.service.WmsCControlSearchService;
import com.byd.wms.business.modules.config.service.WmsCMatFixedStorageService;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsCoreStorageSearchService;
import com.byd.wms.business.modules.config.service.WmsCoreWhAreaService;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.config.service.WmsSBestRuleService;

/**
 * 仓库任务处理服务
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:42:20 
 *
 */
@Service("warehouseTasksService")
public class WarehouseTasksServiceImpl implements WarehouseTasksService{

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
	
	/**
	 * 入库上架仓库任务，搜索仓位
	 * @params 待上架进仓单明细信息
	 */
	@Override
	public List<Map<String,Object>> searchBinForPutaway (List<Map<String,Object>> params){
		Map<String, BigDecimal> bincache = new HashMap<>();
		for (Map<String,Object> dataMap:params) {
			String whNumber = dataMap.get("WH_NUMBER") == null ? "":dataMap.get("WH_NUMBER").toString();
			String werks = dataMap.get("WERKS") == null ? "":dataMap.get("WERKS").toString();
			String matnr = dataMap.get("MATNR") == null ? "":dataMap.get("MATNR").toString();
			String lgort = dataMap.get("LGORT") == null ? "":dataMap.get("LGORT").toString(); //库位
			String sobkz = dataMap.get("SOBKZ") == null ? "":dataMap.get("SOBKZ").toString(); //库存类型
			String lifnr = dataMap.get("LIFNR") == null ? "":dataMap.get("LIFNR").toString(); //供应商
			
			BigDecimal coeQty = BigDecimal.ONE; //转换系数(数量/存储单位 包装与基本单位换算关系 单存储单元数量)
			
			//获取仓库最优入库规则
			List<WmsSBestRuleEntity> list = wmsSBestRuleService.selectList(new EntityWrapper<WmsSBestRuleEntity>()
					.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
					.eq("RULE_TYPE", "1")
					.eq("DEL", "0")
					.orderBy("SEQNO"));
			
			if (list.size() > 0) {
				loop1 : for (WmsSBestRuleEntity bestRuleEntity:list) {
					String inControl = "";
					String businessType = "";
					String lgortFlag = "";
					String sobkzFlag = "";
					
					//取物料仓库主数据中的入库标识
					WmsCMatStorageEntity matStorageEntity = wmsCMatStorageService.selectOne(new EntityWrapper<WmsCMatStorageEntity>()
							.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
							.eq(StringUtils.isNotBlank(werks),"WERKS", werks)
							.eq(StringUtils.isNotBlank(matnr),"MATNR", matnr)
							.eq("DEL", "0"));
					if (matStorageEntity != null) {
						//取转换系数
						if (matStorageEntity.getQty() != null) {
							coeQty = new BigDecimal(matStorageEntity.getQty());
						}
					}
					
					if (bestRuleEntity.getControlFlag().equals("Y")) {
						if (matStorageEntity != null) {
							inControl = matStorageEntity.getInControlFlag(); //控制标识
						}
					}
					if (bestRuleEntity.getBusinessTypeFlag().equals("Y")) {
						businessType = dataMap.get("BUSINESS_NAME") == null ? "":dataMap.get("BUSINESS_NAME").toString(); //WMS业务类型名称
					}
					if (bestRuleEntity.getLgortFlag().equals("Y")) {
						lgortFlag = lgort; //库位标记
					}
					if (bestRuleEntity.getStockTypeFlag().equals("Y")) {
						sobkzFlag = sobkz; //库存类型标记
					}
					
					// 根据最优入库规则,获取存储类型搜索顺序，粗放式仓库，整个仓库就一个规则
						WmsCControlSearchEntity controlSearchEntity =wmsCControlSearchService.selectOne(new EntityWrapper<WmsCControlSearchEntity>()
								.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
								.eq(StringUtils.isNotBlank(inControl),"CONTROL_FLAG", inControl)
								.eq(StringUtils.isNotBlank(businessType),"BUSINESS_NAME", businessType)
								.eq(StringUtils.isNotBlank(lgortFlag),"LGORT", lgortFlag)
								.eq(StringUtils.isNotBlank(sobkzFlag),"SOBKZ", sobkzFlag)
								.isNull(StringUtils.isBlank(inControl), "CONTROL_FLAG")
								.isNull(StringUtils.isBlank(businessType), "BUSINESS_NAME")
								.isNull(StringUtils.isBlank(lgortFlag), "LGORT")
								.isNull(StringUtils.isBlank(sobkzFlag), "SOBKZ")
								.eq("FLAG_TYPE", "00")
								.eq("DEL", "0"));
					
						if (controlSearchEntity != null) {
							String searchSeq = controlSearchEntity.getStorageAreaSearch(); //存储类型搜索顺序
							
							//获取存储区集合
							List<WmsCoreStorageSearchEntity> storageSearchlist = wmsCoreStorageSearchService.selectList(new EntityWrapper<WmsCoreStorageSearchEntity>()
								.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
								.eq(StringUtils.isNotBlank(searchSeq),"SEARCH_SEQ", searchSeq)
								.eq("DEL", "0")
								.orderBy("SEQNO"));
							
							if (storageSearchlist.size() > 0) {
								for (WmsCoreStorageSearchEntity storageSearch:storageSearchlist) {
									String storageAreaCode = storageSearch.getStorageAreaCode();
									//仓库存储区配置信息
									WmsCoreWhAreaEntity wmsCoreWhAreaEntity = wmsCoreWhAreaService.selectOne(new EntityWrapper<WmsCoreWhAreaEntity>()
									.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
									.eq(StringUtils.isNotBlank(storageAreaCode),"STORAGE_AREA_CODE", storageAreaCode)
									.eq("DEL", "0"));
									
									Map<String, Object> findParams = new HashMap<>();
									findParams.put("whNumber", whNumber);
									findParams.put("werks", werks);
									findParams.put("matnr", matnr);
									findParams.put("storageAreaCode", storageAreaCode);
									findParams.put("lgort", lgort);
									findParams.put("sobkz", sobkz);
									findParams.put("lifnr", lifnr);
									
									//固定存储模式
									if (wmsCoreWhAreaEntity != null && wmsCoreWhAreaEntity.getStorageModel().equals("00")) {
										//上架规则为找空仓位
										if (wmsCoreWhAreaEntity.getPutRule().equals("00")) {
											List<WmsCMatFixedStorageEntity> fixedList = wmsCMatFixedStorageService.findEmptyBin(findParams);
											if (fixedList.size() > 0) {
												for (WmsCMatFixedStorageEntity fixed:fixedList) {
													String flogrt = fixed.getLgort() == null?"":fixed.getLgort();
													String flifnr = fixed.getLgort() == null?"":fixed.getLifnr();
													String fsobkz = fixed.getLgort() == null?"":fixed.getSobkz();
													
													if (flogrt.equals(lgort) && flifnr.equals(lifnr) && fsobkz.equals(sobkz)) {
														dataMap.put("STORAGE_AREA_CODE", fixed.getStorageAreaCode());
														//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
														if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
															dataMap.put("BIN_CODE_SHELF", fixed.getBinCode()); 
														} else {
															dataMap.put("BIN_CODE_SHELF", "9010"); 
														}
														dataMap.put("BIN_CODE", fixed.getBinCode()); //储位（进仓）
														break loop1;
													} 
												}
											
												dataMap.put("STORAGE_AREA_CODE", fixedList.get(0).getStorageAreaCode());
												//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
												if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
													dataMap.put("BIN_CODE_SHELF", fixedList.get(0).getBinCode()); 
												} else {
													dataMap.put("BIN_CODE_SHELF", "9010"); 
												}
												dataMap.put("BIN_CODE", fixedList.get(0).getBinCode()); //储位（进仓）
												break loop1;
											}
										} 
										//上架规则为添加至现有仓位
										else if(wmsCoreWhAreaEntity.getPutRule().equals("01")){
											List<Map<String, Object>> fixedList = wmsCMatFixedStorageService.findAlreadyBin(findParams);
											if (fixedList.size() > 0) {
												//进行仓位库容检查
												if (wmsCoreWhAreaEntity.getStorageCapacityFlag().equals("X")) {
													BigDecimal fQty = dataMap.get("IN_QTY") == null ? BigDecimal.ZERO:new BigDecimal(dataMap.get("IN_QTY").toString()); //进仓数量
													for (Map<String, Object> fixedMap:fixedList) {
														BigDecimal stockl = (BigDecimal) fixedMap.get("STOCK_L"); //仓位最大可放数量
														BigDecimal stockQty = (BigDecimal) fixedMap.get("STOCK_QTY"); //仓位现有库存数量
														//如果仓位已推荐过，则现有库存数量需考虑之前推荐的数量
														if (bincache.get(fixedMap.get("BIN_CODE"))!= null) {
															stockQty = bincache.get(fixedMap.get("BIN_CODE"));
														}
														if (fQty.add(stockQty).compareTo(stockl) <= 0) {
															dataMap.put("STORAGE_AREA_CODE", fixedMap.get("STORAGE_AREA_CODE"));
															//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
															if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
																dataMap.put("BIN_CODE_SHELF", fixedMap.get("BIN_CODE")); 
															} else {
																dataMap.put("BIN_CODE_SHELF", "9010"); 
															}
															dataMap.put("BIN_CODE", fixedMap.get("BIN_CODE"));
															bincache.put(fixedMap.get("BIN_CODE").toString(), stockQty.add(fQty));
															break loop1;
														}
													}
													
												} 
												//不进行库容检查
												else {
													dataMap.put("STORAGE_AREA_CODE", fixedList.get(0).get("STORAGE_AREA_CODE"));
													//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
													if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
														dataMap.put("BIN_CODE_SHELF", fixedList.get(0).get("BIN_CODE")); 
													} else {
														dataMap.put("BIN_CODE_SHELF", "9010"); 
													}
													dataMap.put("BIN_CODE", fixedList.get(0).get("BIN_CODE"));
													break loop1;
												}
											}
											
											//如果现有库存仓位都放不下或者找不到现有储位，则找空仓位存放
											List<WmsCMatFixedStorageEntity> emptyFixedList = wmsCMatFixedStorageService.findEmptyBin(findParams);
											if (emptyFixedList.size() > 0) {
												for (WmsCMatFixedStorageEntity fixed:emptyFixedList) {
													String flogrt = fixed.getLgort() == null?"":fixed.getLgort();
													String flifnr = fixed.getLgort() == null?"":fixed.getLifnr();
													String fsobkz = fixed.getLgort() == null?"":fixed.getSobkz();
													
													if (flogrt.equals(lgort) && flifnr.equals(lifnr) && fsobkz.equals(sobkz)) {
														dataMap.put("STORAGE_AREA_CODE", fixed.getStorageAreaCode());
														//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
														if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
															dataMap.put("BIN_CODE_SHELF", fixed.getBinCode()); 
														} else {
															dataMap.put("BIN_CODE_SHELF", "9010"); 
														}
														dataMap.put("BIN_CODE", fixed.getBinCode()); //储位（进仓）
														break loop1;
													} 
												}
												
												dataMap.put("STORAGE_AREA_CODE", emptyFixedList.get(0).getStorageAreaCode());
												//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
												if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
													dataMap.put("BIN_CODE_SHELF", emptyFixedList.get(0).getBinCode()); 
												} else {
													dataMap.put("BIN_CODE_SHELF", "9010"); 
												}
												dataMap.put("BIN_CODE", emptyFixedList.get(0).getBinCode());
												break loop1;
											}
										} else {//上架规则为一般存储区（系统默认储位）
											dataMap.put("STORAGE_AREA_CODE", wmsCoreWhAreaEntity.getStorageAreaCode());
											//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
											if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
												dataMap.put("BIN_CODE_SHELF", "AAAA"); 
											} else {
												dataMap.put("BIN_CODE_SHELF", "9010"); 
											}
											dataMap.put("BIN_CODE", "AAAA");
											break loop1;
										}
									} 
									//随机存储模式
									else if (wmsCoreWhAreaEntity != null && wmsCoreWhAreaEntity.getStorageModel().equals("01")) {
										//上架规则为找空仓位
										if (wmsCoreWhAreaEntity.getPutRule().equals("00")) {

											Map<String, Object> kParams = new HashMap<>();
											kParams.put("seqType", wmsCoreWhAreaEntity.getBinSearchSequence());
											kParams.put("whNumber", whNumber);
											kParams.put("storageAreaCode", storageAreaCode);
											//根据仓位搜索顺序结果推荐
											List<Map<String, Object>> binList= wmsCoreWhBinService.findEmptyBin(kParams);
											if (binList.size() > 0) {
												dataMap.put("STORAGE_AREA_CODE", binList.get(0).get("STORAGE_AREA_CODE"));
												//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
												if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
													dataMap.put("BIN_CODE_SHELF", binList.get(0).get("BIN_CODE")); 
												} else {
													dataMap.put("BIN_CODE_SHELF", "9010"); 
												}
												dataMap.put("BIN_CODE", binList.get(0).get("BIN_CODE"));
												break loop1;
											}
										}
										//上架规则为添加至现有仓位
										else if(wmsCoreWhAreaEntity.getPutRule().equals("01")){
											List<Map<String, Object>> alreadyBinList = wmsCoreWhBinService.findAlreadyBinForRandom(findParams);
											if (alreadyBinList.size() > 0) {
												//进行仓位库容检查
												if (wmsCoreWhAreaEntity.getStorageCapacityFlag().equals("X")) {
													String lastBin = "";
													
													BigDecimal fQty = dataMap.get("IN_QTY") == null ? BigDecimal.ZERO:new BigDecimal(dataMap.get("IN_QTY").toString()); //进仓数量
													//基本单位数量换算为存储单元数量
													fQty = fQty.divide(coeQty,0,BigDecimal.ROUND_HALF_UP);
													
													for (Map<String, Object> alreadyBinMap:alreadyBinList) {
														BigDecimal aStorageUnit = (BigDecimal) alreadyBinMap.get("A_STORAGE_UNIT"); //可容存储单元
														BigDecimal uStorageUnit = (BigDecimal) alreadyBinMap.get("U_STORAGE_UNIT"); //占用存储单元
														
														//如果仓位已推荐过，则占用数需考虑之前推荐的数量
														if (bincache.get(alreadyBinMap.get("BIN_CODE"))!= null) {
															uStorageUnit = bincache.get(alreadyBinMap.get("BIN_CODE"));
														}
														
														if (fQty.add(uStorageUnit).compareTo(aStorageUnit) <= 0) {
															dataMap.put("STORAGE_AREA_CODE", alreadyBinMap.get("STORAGE_AREA_CODE"));
															//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
															if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
																dataMap.put("BIN_CODE_SHELF", alreadyBinMap.get("BIN_CODE")); 
															} else {
																dataMap.put("BIN_CODE_SHELF", "9010"); 
															}
															dataMap.put("BIN_CODE", alreadyBinMap.get("BIN_CODE"));
															bincache.put(alreadyBinMap.get("BIN_CODE").toString(), uStorageUnit.add(fQty));
															break loop1;
														}
														lastBin = alreadyBinMap.get("BIN_CODE").toString();
													}
													//如果现有库存仓位都放不下，则找空仓位存放
													Map<String, Object> kParams = new HashMap<>();
													kParams.put("seqType", wmsCoreWhAreaEntity.getBinSearchSequence());
													kParams.put("whNumber", whNumber);
													kParams.put("storageAreaCode", storageAreaCode);
													kParams.put("binCode", lastBin);
													//根据仓位搜索顺序结果推荐, 推荐现有仓位附近空仓位
													List<Map<String, Object>> binList= wmsCoreWhBinService.findEmptyBinForNeighbor(kParams);
													if (binList.size() > 0) {
														dataMap.put("STORAGE_AREA_CODE", binList.get(1).get("STORAGE_AREA_CODE"));
														//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
														if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
															dataMap.put("BIN_CODE_SHELF", binList.get(1).get("BIN_CODE")); 
														} else {
															dataMap.put("BIN_CODE_SHELF", "9010"); 
														}
														dataMap.put("BIN_CODE", binList.get(1).get("BIN_CODE"));
														break loop1;
													}
												}
												//不进行库容检查
												else {
													dataMap.put("STORAGE_AREA_CODE", alreadyBinList.get(0).get("STORAGE_AREA_CODE"));
													//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
													if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
														dataMap.put("BIN_CODE_SHELF", alreadyBinList.get(0).get("BIN_CODE")); 
													} else {
														dataMap.put("BIN_CODE_SHELF", "9010"); 
													}
													dataMap.put("BIN_CODE", alreadyBinList.get(0).get("BIN_CODE"));
													break loop1;
												}
											}
											
											//没有找到现有库存，则找空仓位存放
											Map<String, Object> kParams = new HashMap<>();
											kParams.put("seqType", wmsCoreWhAreaEntity.getBinSearchSequence());
											kParams.put("whNumber", whNumber);
											kParams.put("storageAreaCode", storageAreaCode);
											//根据仓位搜索顺序结果推荐
											List<Map<String, Object>> binList= wmsCoreWhBinService.findEmptyBin(kParams);
											if (binList.size() > 0) {
												dataMap.put("STORAGE_AREA_CODE", binList.get(0).get("STORAGE_AREA_CODE"));
												//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
												if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
													dataMap.put("BIN_CODE_SHELF", binList.get(0).get("BIN_CODE")); 
												} else {
													dataMap.put("BIN_CODE_SHELF", "9010"); 
												}
												dataMap.put("BIN_CODE", binList.get(0).get("BIN_CODE"));
												break loop1;
											}
											
										} else {//上架规则为一般存储区（系统默认储位）
											dataMap.put("STORAGE_AREA_CODE", wmsCoreWhAreaEntity.getStorageAreaCode());
											//上架缓存储位 自动上架时 上架缓存储位和储位（进仓）一致，进仓过账时库房库存更新到上架缓存储位
											if (wmsCoreWhAreaEntity.getAutoPutawayFlag().equals("X")) {
												dataMap.put("BIN_CODE_SHELF", "AAAA"); 
											} else {
												dataMap.put("BIN_CODE_SHELF", "9010"); 
											}
											dataMap.put("BIN_CODE", "AAAA");
											break loop1;
										}
									}
									//立库模式
									else if (wmsCoreWhAreaEntity != null && wmsCoreWhAreaEntity.getStorageModel().equals("02")) {
										//TODO
									}
								}
							}
						}
//					}
					dataMap.put("MSG", matnr+":未找到合适仓位");
				}
				
			}
			
		}
		return params;
	}
	
	/**
	 * 出库拣配，搜索仓位
	 * @params 待拣配需求明细信息
	 */
	@Override
	public List<Map<String,Object>> searchBinForPick (List<Map<String,Object>> params){
		List<Map<String,Object>> returnParams = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> stockListTemp = new ArrayList<Map<String,Object>>();
		for (Map<String,Object> dataMap:params) {
			String whNumber = dataMap.get("WH_NUMBER") == null ? "":dataMap.get("WH_NUMBER").toString();
			String werks = dataMap.get("WERKS") == null ? "":dataMap.get("WERKS").toString();
			String matnr = dataMap.get("MATNR") == null ? "":dataMap.get("MATNR").toString();
			String lgort = dataMap.get("LGORT") == null ? "":dataMap.get("LGORT").toString(); //库位
			String sobkz = dataMap.get("SOBKZ") == null ? "":dataMap.get("SOBKZ").toString(); //库存类型
			String lifnr = dataMap.get("LIFNR") == null ? "":dataMap.get("LIFNR").toString(); //供应商
			String hxFlag = dataMap.get("HX_FLAG") == null ? "":dataMap.get("HX_FLAG").toString(); //核销标识
			String soNo = dataMap.get("SO_NO") == null ? "":dataMap.get("SO_NO").toString(); //销售订单
			String soItemNo = dataMap.get("SO_ITEM_NO") == null ? "":dataMap.get("SO_ITEM_NO").toString(); //销售订单行
			
			BigDecimal reqQty = dataMap.get("REQ_QTY") == null ? BigDecimal.ZERO:new BigDecimal(dataMap.get("REQ_QTY").toString()); //需求数量
			
			if (reqQty.compareTo(BigDecimal.ZERO) > 0 ) {
				String configError = "";
			
				//获取仓库最优出库规则
				List<WmsSBestRuleEntity> list = wmsSBestRuleService.selectList(new EntityWrapper<WmsSBestRuleEntity>()
						.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
						.eq("RULE_TYPE", "2")
						.eq("DEL", "0")
						.orderBy("SEQNO"));
				
				if (list.size() > 0) {
					loop1 : for (WmsSBestRuleEntity bestRuleEntity:list) {
						String outControl = "";
						String businessType = "";
						String lgortFlag = "";
						String sobkzFlag = "";
						
						configError = "";
						
						if (bestRuleEntity.getControlFlag().equals("Y")) {
							//取物料仓库主数据中的出库标识
							WmsCMatStorageEntity matStorageEntity = wmsCMatStorageService.selectOne(new EntityWrapper<WmsCMatStorageEntity>()
									.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
									.eq(StringUtils.isNotBlank(werks),"WERKS", werks)
									.eq(StringUtils.isNotBlank(matnr),"MATNR", matnr)
									.eq("DEL", "0"));
							
							if (matStorageEntity != null) {
								outControl = matStorageEntity.getOutControlFlag(); //控制标识
							} else {
								configError = matnr+":物料存储配置未维护出库控制标识！";
								break;
							}
						}
						if (bestRuleEntity.getBusinessTypeFlag().equals("Y")) {
							businessType = dataMap.get("BUSINESS_NAME") == null ? "":dataMap.get("BUSINESS_NAME").toString(); //WMS业务类型名称
						}
						if (bestRuleEntity.getLgortFlag().equals("Y")) {
							lgortFlag = lgort; //库位标记
						}
						if (bestRuleEntity.getStockTypeFlag().equals("Y")) {
							sobkzFlag = sobkz; //库存类型标记
						}
						
						// 根据最优出库规则,获取存储类型搜索顺序
						WmsCControlSearchEntity controlSearchEntity =wmsCControlSearchService.selectOne(new EntityWrapper<WmsCControlSearchEntity>()
								.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
								.eq(StringUtils.isNotBlank(outControl),"CONTROL_FLAG", outControl)
								.eq(StringUtils.isNotBlank(businessType),"BUSINESS_NAME", businessType)
								.eq(StringUtils.isNotBlank(lgortFlag),"LGORT", lgortFlag)
								.eq(StringUtils.isNotBlank(sobkzFlag),"SOBKZ", sobkzFlag)
								.isNull(StringUtils.isBlank(outControl), "CONTROL_FLAG")
								.isNull(StringUtils.isBlank(businessType), "BUSINESS_NAME")
								.isNull(StringUtils.isBlank(lgortFlag), "LGORT")
								.isNull(StringUtils.isBlank(sobkzFlag), "SOBKZ")
								.eq("FLAG_TYPE", "01")
								.eq("DEL", "0"));
						
						if (controlSearchEntity != null) {
							String searchSeq = controlSearchEntity.getStorageAreaSearch(); //存储类型搜索顺序
								
							//获取存储区集合
							List<WmsCoreStorageSearchEntity> storageSearchlist = wmsCoreStorageSearchService.selectList(new EntityWrapper<WmsCoreStorageSearchEntity>()
								.eq(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
								.eq(StringUtils.isNotBlank(searchSeq),"SEARCH_SEQ", searchSeq)
								.eq("DEL", "0")
								.orderBy("SEQNO"));
								
							if (storageSearchlist.size() > 0) {
								for (WmsCoreStorageSearchEntity storageSearch:storageSearchlist) {
									String storageAreaCode = storageSearch.getStorageAreaCode();
										
									Map<String, Object> findParams = new HashMap<>();
									findParams.put("whNumber", whNumber);
									findParams.put("werks", werks);
									findParams.put("matnr", matnr);
									findParams.put("storageAreaCode", storageAreaCode);
									findParams.put("lgort", lgort);
									findParams.put("sobkz", sobkz);
									findParams.put("lifnr", lifnr);
									findParams.put("soNo", soNo);
									findParams.put("soItemNo", soItemNo);
									if (!hxFlag.equals("") && !hxFlag.equals("0")) {
										findParams.put("hxFlag", hxFlag);
									}
									
									//搜索库存，暂时默认根据批次先进先出推荐。后续用出库规则排序	
									List<Map<String, Object>> stockList = commonService.searchBinForPick(findParams);
									ListUtils.sort(stockList, "SOBKZ", false);
									if (stockList.size() > 0) {
										for (Map<String, Object> stock : stockList) {
											//解决需求中相同物料，如果库存已经推荐过，则用缓存库存计算。
											for (Map<String, Object> stocktemp : stockListTemp) {
												if (stocktemp.get("ID").equals(stock.get("ID"))) {
													stock.putAll(stocktemp);
													stockListTemp.remove(stocktemp);
													break;
												}
											}
											
											BigDecimal stockQty = stock.get("STOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(stock.get("STOCK_QTY").toString()); //仓位现有库存数量
											
											if (!hxFlag.equals("") && !hxFlag.equals("0")) {
												stockQty = stock.get("LOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(stock.get("LOCK_QTY").toString()); 
											}
											
											if (reqQty.compareTo(stockQty) <= 0) {
												Map<String,Object> dataMapTemp = new HashMap<String,Object>();
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
												if (!hxFlag.equals("") && !hxFlag.equals("0")) {
													dataMapTemp.put("HX_QTY_FLAG", "1"); //核销标记，1、扣减锁定库存；2、扣除非限制库存
												}
												returnParams.add(dataMapTemp);
												
												if (!hxFlag.equals("") && !hxFlag.equals("0")) {
													stock.put("LOCK_QTY", stockQty.subtract(reqQty));
												} else {
													stock.put("STOCK_QTY", stockQty.subtract(reqQty));
												}
												stockListTemp.add(stock);
												
												reqQty = BigDecimal.ZERO;
												break loop1;
											} else if(stockQty.compareTo(BigDecimal.ZERO) > 0) {
												Map<String,Object> dataMapTemp = new HashMap<String,Object>();
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
												if (!hxFlag.equals("") && !hxFlag.equals("0")) {
													dataMapTemp.put("HX_QTY_FLAG", "1"); //核销标记，1、扣减锁定库存；2、扣除非限制库存
												}
												returnParams.add(dataMapTemp);
												
												if (!hxFlag.equals("") && !hxFlag.equals("0")) {
													stock.put("LOCK_QTY", BigDecimal.ZERO);
												} else {
													stock.put("STOCK_QTY", BigDecimal.ZERO);
												}
												stockListTemp.add(stock);
													
												reqQty = reqQty.subtract(stockQty);
											}
										}
									}
									//核销业务，锁定库存不足时，扣除非限制库存
									if (!hxFlag.equals("") && !hxFlag.equals("0")) {
										findParams.put("hxFlag", null);
										List<Map<String, Object>> stockListhx = commonService.searchBinForPick(findParams); //再次搜索库存
										ListUtils.sort(stockList, "SOBKZ", false);
										if (stockListhx.size() > 0) {
											for (Map<String, Object> stock : stockListhx) {
												
												//解决需求中相同物料，如果库存已经推荐过，则用缓存库存计算。
												for (Map<String, Object> stocktemp : stockListTemp) {
													if (stocktemp.get("ID").equals(stock.get("ID"))) {
														stock.putAll(stocktemp);
														stockListTemp.remove(stocktemp);
														break;
													}
												}
												
												BigDecimal stockQty = stock.get("STOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(stock.get("STOCK_QTY").toString()); //仓位现有库存数量
												
												if (reqQty.compareTo(stockQty) <= 0) {
													Map<String,Object> dataMapTemp = new HashMap<String,Object>();
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
													dataMapTemp.put("HX_QTY_FLAG", "2"); //核销标记，1、扣减锁定库存；2、扣除非限制库存
													returnParams.add(dataMapTemp);
													
													if (!hxFlag.equals("") && !hxFlag.equals("0")) {
														stock.put("LOCK_QTY", stockQty.subtract(reqQty));
													} else {
														stock.put("STOCK_QTY", stockQty.subtract(reqQty));
													}
													stockListTemp.add(stock);
													
													reqQty = BigDecimal.ZERO;
													break loop1;
												} else if(stockQty.compareTo(BigDecimal.ZERO) > 0) {
													Map<String,Object> dataMapTemp = new HashMap<String,Object>();
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
													dataMapTemp.put("REQ_ITEM_STATUS", "99");
													dataMapTemp.put("HX_QTY_FLAG", "2"); //核销标记，1、扣减锁定库存；2、扣除非限制库存
													returnParams.add(dataMapTemp);
													
													if (!hxFlag.equals("") && !hxFlag.equals("0")) {
														stock.put("LOCK_QTY", BigDecimal.ZERO);
													} else {
														stock.put("STOCK_QTY", BigDecimal.ZERO);
													}
													stockListTemp.add(stock);
														
													reqQty = reqQty.subtract(stockQty);
												}
											}
										}
									}
								}
							} else {
								configError = whNumber+":分配存储区至搜索顺序未配置";
							}
						} else {
							configError = whNumber+":出入库搜索顺序未配置";
						}
					}
					if (!configError.equals("")) {
						Map<String,Object> dataMapTemp = new HashMap<String,Object>();
						dataMapTemp.putAll(dataMap);
						dataMapTemp.put("QTY", reqQty);
						dataMapTemp.put("QUANTITY", reqQty);
						dataMapTemp.put("MSG", configError);
						returnParams.add(dataMapTemp);
					} else if (reqQty.compareTo(BigDecimal.ZERO) > 0) {
						Map<String,Object> dataMapTemp = new HashMap<String,Object>();
						dataMapTemp.putAll(dataMap);
						dataMapTemp.put("QTY", reqQty);
						dataMapTemp.put("QUANTITY", reqQty);
						dataMapTemp.put("MSG", matnr+":未满足数量"+ reqQty);
						returnParams.add(dataMapTemp);
					}
				} else {
					Map<String,Object> dataMapTemp = new HashMap<String,Object>();
					dataMapTemp.putAll(dataMap);
					dataMapTemp.put("QTY", reqQty);
					dataMapTemp.put("QUANTITY", reqQty);
					dataMapTemp.put("MSG", whNumber+":最优出入库规则未配置");
					returnParams.add(dataMapTemp);
				}
				
			} else {
				returnParams.add(dataMap);
			}
		}
		return returnParams;
	}
	
	/**
	 * 获取仓库任务
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> selectCoreWHTask(Map<String, Object> params) {
		return wmsCoreWHTaskDao.selectCoreWHTask(params);
	}
	
	/**
	 * 获取仓库任务(批量)
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> selectCoreWHTaskList(List<Map<String, Object>> list) {
		return wmsCoreWHTaskDao.selectCoreWHTaskList(list);
	}
	
	/**
	 * 修改仓库任务（确认、取消等操作）
	 * @param params 必须要传仓库号、仓库任务两个字段
	 */
	@Override
	public int updateCoreWHTask(List<Map<String,Object>> params) {

//		ListUtils.sort(params, "ID", true);
		return wmsCoreWHTaskDao.updateCoreWHTask(params);
	}
	
	/**
	 * 保存仓库任务
	 * @params
	 */
	@Override
	public String saveWHTask(List<Map<String,Object>> params) {
		String WTNO = "";
		//不同条码推荐储位一致的，合并为一条仓库任务
		List<Map<String,Object>> newparams = Combine(params); 
		for (Map<String,Object> dataMap:newparams) {
			//获取仓库任务号
			Map<String,Object> docparam = new HashMap<String,Object>();
			docparam.put("WMS_DOC_TYPE", "14");//仓库任务
			Map<String,Object> doc = wmsCDocNoService.getDocNo(docparam);
			if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
				throw new RuntimeException(doc.get("MSG").toString());
			}
			WTNO = doc.get("docno").toString(); //仓库任务号
			
			dataMap.put("TASK_NUM", WTNO);
			if (dataMap.get("WT_STATUS") == null) {
				dataMap.put("WT_STATUS", "00"); //仓库任务状态  00：未清  01：部分确认  02：已确认  03：已取消
			}
			
		}
		wmsCoreWHTaskDao.insertCoreWHTask(newparams);
		return WTNO;
	}
	
	
	public static List<Map<String,Object>> Combine(List<Map<String,Object>> lists) {
        List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
        
        for (Map<String,Object> list : lists) {  
            String matnr = list.get("MATNR").toString();
            String batch = list.get("BATCH").toString();
            String frombin = list.get("FROM_BIN_CODE").toString();
            String tobin = list.get("TO_BIN_CODE").toString();
            String lifnr = list.get("LIFNR").toString();
            int flag = 0;// 0为新增数据，1为增加 
            
            for (Map<String,Object> clist : countList) {  
                String matnrtemp = clist.get("MATNR").toString();  
                String batchtemp = clist.get("BATCH").toString(); 
                String frombintemp = clist.get("FROM_BIN_CODE").toString();
                String tobintemp = clist.get("TO_BIN_CODE").toString();
                String lifnrtemp = clist.get("LIFNR").toString();
  
                if (matnr.equals(matnrtemp) && batch.equals(batchtemp) && frombin.equals(frombintemp) 
                		&& tobin.equals(tobintemp) && lifnr.equals(lifnrtemp)) {  
                	BigDecimal lqty = new BigDecimal(list.get("QUANTITY").toString());
                	BigDecimal cqty = new BigDecimal(clist.get("QUANTITY").toString());
                    BigDecimal sum = lqty.add(cqty);
                    clist.put("QUANTITY", sum);  
                    BigDecimal lqty1 = new BigDecimal(list.get("CONFIRM_QUANTITY")==null?(list.get("QTY_WMS")==null?"0":list.get("QTY_WMS").toString()):list.get("CONFIRM_QUANTITY").toString());
                	BigDecimal cqty1 = new BigDecimal(clist.get("CONFIRM_QUANTITY")==null?(clist.get("QTY_WMS")==null?"0":clist.get("QTY_WMS").toString()):clist.get("CONFIRM_QUANTITY").toString());
                    BigDecimal sum1 = lqty1.add(cqty1);
                    clist.put("CONFIRM_QUANTITY", sum1);  
                    flag = 1;  
                    continue;  
                }  
            }  
            if (flag == 0) {  
            	Map<String,Object> dataMapTemp = new HashMap<String,Object>();
				dataMapTemp.putAll(list);
                countList.add(dataMapTemp);  
            }  
        }
        
        return countList;
    }
	
	@Override
	public int updateCoreWHTaskStatus(List<Map<String,Object>> params) {
//		ListUtils.sort(params, "ID", true);
		return wmsCoreWHTaskDao.updateCoreWHTaskStatus(params);
	}
	
	@Override
	public int updateCoreWHTaskByReq(List<Map<String,Object>> params) {
		return wmsCoreWHTaskDao.updateCoreWHTaskByReq(params);
	}
	
	/**
	 * 保存拣配下架记录表
	 * @param updateList
	 */
	@Override
	public void mergeWmsOutPicking(List<Map<String, Object>> updateList) {
		wmsCoreWHTaskDao.mergeWmsOutPicking(updateList);
	}
}
