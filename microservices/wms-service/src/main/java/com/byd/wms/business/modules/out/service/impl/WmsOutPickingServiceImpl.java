package com.byd.wms.business.modules.out.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.ListUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.dao.WmsCoreWHTaskDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.SysUserRemote;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.out.dao.WmsOutPickingDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.service.CreateRequirementService;
import com.byd.wms.business.modules.out.service.WmsOutPickingService;

/**
 * 需求拣配服务类
 * @author 作者 : ren.wei3@byd.com
 * @version 创建时间：2019年5月31日 上午9:36:36
 *
 */
@Service("wmsOutPickingService")
public class WmsOutPickingServiceImpl implements WmsOutPickingService {

	@Autowired
	private WmsOutPickingDao wmsOutPickingDao;

	@Autowired
	private WarehouseTasksService warehouseTasksService;

	@Autowired
	private UserUtils userUtils;

	@Autowired
	private SysUserRemote sysUserRemote;

	@Autowired
	private CreateRequirementService createRequirementService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private WmsCoreWhBinService wmsCoreWhBinService;

	@Autowired
	private WmsCDocNoService wmsCDocNoService;

	@Autowired
	private WmsCoreWHTaskDao wmsCoreWHTaskDao;

	@Autowired
	private WmsOutRequirementHeadDao headDao;

	@Autowired
	private WmsOutRequirementItemDao itemDao;

	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;

	@Autowired
	private WmsInInboundDao wmsInInboundDao;

	@Autowired
	private RedisUtils redisUtils;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		List<Map<String, Object>> list= wmsOutPickingDao.selectRequirement(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(list.size());
		page.setSize(list.size());
		return new PageUtils(page);
	}

	/**
	 * 拣配推荐
	 */

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PageUtils recommend(Map<String, Object> params) {
		String whcode = params.get("WH_NUMBER").toString();
		String werks = params.get("WERKS").toString();
		String requirementNo = params.get("REQUIREMENT_NO").toString();
		Map<String,Object> user = userUtils.getUser();

		//人料关系绑定，筛选数据
		String manager = params.get("MANAGER") == null ? "" : params.get("MANAGER").toString();
		if (!manager.equals("")) {
			params.put("AUTHORIZE_CODE", params.get("MANAGER"));
			List<Map<String,Object>> relatedAreaName=wmsInInboundDao.getRelatedAreaName(params);//根据工厂仓库号查询
			if(relatedAreaName!=null&&relatedAreaName.size()>0){
				if("00".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
					params.put("CONDMG", "1"); //MAT_MANAGER_TYPE为00， 筛选物料
				}else if("20".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
					params.put("CONDMG", "2"); //MAT_MANAGER_TYPE为20, 筛选库位
				}
			}
		}

		List<Map<String, Object>> list = wmsOutPickingDao.selectRequirement(params);

		if(list.size() <= 0) {
			Page page=new Query<Map<String,Object>>(params).getPage();
			return new PageUtils(page);
		}

		for (Map<String, Object> wlist : list) {
			if (wlist.get("REQUIREMENT_STATUS").equals("07")) {
				throw new RuntimeException("此需求已关闭!");
			}
			wlist.put("WH_NUMBER", whcode);
			wlist.put("WERKS", werks);
			if (null != wlist.get("WT_STATUS") && !wlist.get("WT_STATUS").equals("")) {
				if (wlist.get("WT_STATUS").equals("00")) {
					wlist.put("REQ_ITEM_STATUS", "99");
				} else if(wlist.get("WT_STATUS").equals("01")){
					wlist.put("REQ_ITEM_STATUS", "02");
				} else if(wlist.get("WT_STATUS").equals("02")){
					wlist.put("REQ_ITEM_STATUS", "03");
				} else if(wlist.get("WT_STATUS").equals("03")){
					wlist.put("REQ_ITEM_STATUS", "06");
				}

			}
		}

		//查询工厂业务类型配置，是否可超需求
		params.put("BUSINESS_NAME", list.get(0).get("BUSINESS_NAME"));
		params.put("BUSINESS_TYPE", list.get(0).get("BUSINESS_TYPE"));
		List<Map<String, Object>> pblist = commonService.selectPlantBusiness(params);


		List<Map<String, Object>> newlist = wtSplit(list);

		List<Map<String, Object>> list1 = warehouseTasksService.searchBinForPick(newlist);

		List<Map<String, Object>> stockMatList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> addWTList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> queryStockParams = new ArrayList<Map<String, Object>>(); //库存查询条件
		List<Map<String, Object>> bStockMatList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> param : list1) {
			//已过账的行不处理
			if (!param.get("REQ_ITEM_STATUS").equals("05")) {
				Map<String, Object> stockMat = new HashMap<String, Object>();
				stockMat.put("WERKS", werks);
				stockMat.put("WH_NUMBER", whcode);
				stockMat.put("MATNR", param.get("MATNR"));
				stockMat.put("LGORT", param.get("LGORT"));
				stockMat.put("BIN_CODE", param.get("BIN_CODE"));
				stockMat.put("BATCH", param.get("BATCH"));
				stockMat.put("SOBKZ", param.get("SOBKZ"));
				stockMat.put("LIFNR", param.get("LIFNR"));
				stockMat.put("SO_NO",param.get("SO_NO"));
				stockMat.put("SO_ITEM_NO",param.get("SO_ITEM_NO"));
				queryStockParams.add(stockMat);
			}
		}

		if (queryStockParams.size() > 0)
			bStockMatList = commonService.getWmsStock(queryStockParams);

		//批量生成仓库任务号
		int wtCount = 0;
		for (Map<String, Object> param : list1) {
			String tasknum = param.get("TASK_NUM") == null ? "": param.get("TASK_NUM").toString();
			BigDecimal recommendQty = param.get("RECOMMEND_QTY") == null ? BigDecimal.ZERO:new BigDecimal(param.get("RECOMMEND_QTY").toString());

			if (tasknum.equals("") && recommendQty.compareTo(BigDecimal.ZERO) > 0) {
				wtCount++;
			}
		}
		//获取仓库任务号
		List<String> docnolist = new ArrayList<String>();
		if (wtCount > 0) {
			Map<String,Object> docparam = new HashMap<String,Object>();
			docparam.put("WMS_DOC_TYPE", "14");//仓库任务
			docparam.put("INCREMENT_NUM", wtCount);//长度
			Map<String,Object> doc = wmsCDocNoService.getDocNoBatch(docparam);
			if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
				throw new RuntimeException(doc.get("MSG").toString());
			}
			docnolist = (ArrayList<String>)doc.get("docno"); //仓库任务号
		}


		for (Map<String, Object> param : list1) {
			String tasknum = param.get("TASK_NUM") == null ? "": param.get("TASK_NUM").toString();
			BigDecimal recommendQty = param.get("RECOMMEND_QTY") == null ? BigDecimal.ZERO:new BigDecimal(param.get("RECOMMEND_QTY").toString());

			if (tasknum.equals("") && recommendQty.compareTo(BigDecimal.ZERO) > 0) {
				Map<String, Object> paramtemp = new HashMap<String, Object>();
				paramtemp.putAll(param);
				paramtemp.put("WH_NUMBER", whcode);
				paramtemp.put("FROM_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
				paramtemp.put("FROM_BIN_CODE", param.get("BIN_CODE"));
				paramtemp.put("TO_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
				paramtemp.put("TO_BIN_CODE", "BBBB");
				paramtemp.put("PROCESS_TYPE", "01");
				if (recommendQty.compareTo(BigDecimal.ZERO) == 0) {
					throw new IllegalArgumentException("下架数量不能为0！");
				}
				paramtemp.put("QUANTITY", recommendQty);
				paramtemp.put("REFERENCE_DELIVERY_NO", requirementNo);
				paramtemp.put("REFERENCE_DELIVERY_ITEM", param.get("REQUIREMENT_ITEM_NO"));
				paramtemp.put("CREATOR", user.get("USERNAME"));
				paramtemp.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				if (param.get("HX_QTY_FLAG") != null && !param.get("HX_QTY_FLAG").equals("")) {
					paramtemp.put("HX_FLAG", param.get("HX_QTY_FLAG"));
				}

				String WTNO = "";
				if (docnolist.size() > 0) {
					WTNO = docnolist.get(docnolist.size()-1);
					docnolist.remove(WTNO);
				} else {
					throw new IllegalArgumentException("仓库任务未生成！");
				}

				paramtemp.put("TASK_NUM", WTNO);
				param.put("TASK_NUM", WTNO);
				paramtemp.put("WT_STATUS", "00");

				//更新需求准备
				paramtemp.put("REQUIREMENT_NO", requirementNo);
				paramtemp.put("REQUIREMENT_ITEM_NO", param.get("REQUIREMENT_ITEM_NO"));
				paramtemp.put("REQ_ITEM_STATUS", "01");
				paramtemp.put("EDITOR", user.get("USERNAME"));
				paramtemp.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				paramtemp.put("QTY_REAL", "");
				paramtemp.put("WERKS", werks);
				addWTList.add(paramtemp);

				//更新库存准备数据
				boolean stockflag = false;
				for (Map<String, Object> bstockmat:bStockMatList) {
					if (param.get("MATNR").equals(bstockmat.get("MATNR")) && param.get("LGORT").equals(bstockmat.get("LGORT"))
							&& param.get("BIN_CODE").equals(bstockmat.get("BIN_CODE"))
							&& (param.get("BATCH") == null || param.get("BATCH").equals("") ? bstockmat.get("BATCH")==null:param.get("BATCH").equals(bstockmat.get("BATCH")))
							&& (param.get("SOBKZ") == null || param.get("SOBKZ").equals("") ? bstockmat.get("SOBKZ")==null:param.get("SOBKZ").equals(bstockmat.get("SOBKZ")))
							&& (param.get("LIFNR") == null || param.get("LIFNR").equals("") ? bstockmat.get("LIFNR")==null:param.get("LIFNR").equals(bstockmat.get("LIFNR")))) {


						Map<String, Object> aStockMat = new HashMap<String,Object>();
						BigDecimal stockqty = bstockmat.get("STOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(bstockmat.get("STOCK_QTY").toString());
						//核销标记如果为1，则扣减锁定库存，否则扣减非限制库存
						if (param.get("HX_FLAG") != null && !param.get("HX_FLAG").equals("") && param.get("HX_QTY_FLAG") != null && param.get("HX_QTY_FLAG").equals("1")) {
							stockqty = bstockmat.get("LOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(bstockmat.get("LOCK_QTY").toString());
						}
						if (stockqty.compareTo(recommendQty) >= 0) {
							aStockMat.putAll(bstockmat);
							if (param.get("HX_FLAG") != null && !param.get("HX_FLAG").equals("") && param.get("HX_QTY_FLAG") != null && param.get("HX_QTY_FLAG").equals("1")) { //核销业务扣锁定库存
								aStockMat.put("LOCK_QTY", recommendQty.negate());
								aStockMat.put("VIRTUAL_LOCK_QTY", "");
								aStockMat.put("STOCK_QTY", "");
							} else if (param.get("HX_FLAG") != null && !param.get("HX_FLAG").equals("") && param.get("HX_QTY_FLAG") != null && param.get("HX_QTY_FLAG").equals("2")){ //核销业务扣非限制库存
								aStockMat.put("STOCK_QTY", recommendQty.negate());
								aStockMat.put("LOCK_QTY", "");
								aStockMat.put("VIRTUAL_LOCK_QTY", recommendQty.negate());
							} else { //正常业务扣非限制库存
								aStockMat.put("STOCK_QTY", recommendQty.negate());
								aStockMat.put("LOCK_QTY", "");
								aStockMat.put("VIRTUAL_LOCK_QTY", "");
							}
							aStockMat.put("RSB_QTY", recommendQty);
							aStockMat.put("XJ_QTY", "");
							aStockMat.put("VIRTUAL_QTY", "");
							aStockMat.put("FREEZE_QTY", "");
							aStockMat.put("EDITOR", user.get("USERNAME"));
							aStockMat.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
							stockMatList.add(aStockMat);
							stockflag = true;
							break;
						}else {
							throw new IllegalArgumentException("找不到库存，下架失败！");
						}
					}
				}
				if (!stockflag) {
					throw new IllegalArgumentException("找不到库存，下架失败！");
				}
			}

			param.put("BATCH_OLD", param.get("BATCH"));
			param.put("BIN_CODE_OLD", param.get("BIN_CODE"));
			param.put("LIFNR_OLD", param.get("LIFNR"));
			param.put("LGORT_OLD", param.get("LGORT"));
			param.put("SOBKZ_OLD", param.get("SOBKZ"));

			//附上是否可超需求拣配标识
			for(Map<String, Object> plantBusiness:pblist) {
				if (plantBusiness.get("SOBKZ").equals(param.get("SOBKZ"))) {
					param.put("OVERSTEP_REQ_FLAG", plantBusiness.get("OVERSTEP_REQ_FLAG"));
				}
			}
		}


		if (addWTList.size() > 0) {
			//生成仓库任务
			wmsCoreWHTaskDao.insertCoreWHTask(addWTList);
			//更新需求状态为备料中
			ListUtils.sort(addWTList, "REQUIREMENT_ITEM_NO", true);
			itemDao.updateOutboundItemQtyANDStatus(addWTList);
			for(Map<String, Object> head:addWTList) {
				head.put("REQUIREMENT_STATUS", "02");
			}
			headDao.updateRequirementStatus(addWTList);
		}

		//更新库存,扣减仓位非限制库存,增加下架数量。
		if (stockMatList.size() > 0) {
			ListUtils.sort(stockMatList, "ID", true); //先排序防死锁
			commonService.modifyWmsStock(stockMatList);
		}

		Page page=new Query<Map<String,Object>>(params).getPage();

		if (params.get("CONDMG") != null && params.get("CONDMG").equals("2")) {
			List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> param : list1) {
				if (param.get("LGORT").equals(params.get("MANAGER")))
					list2.add(param);
			}
			page.setRecords(list2);
			return new PageUtils(page);
		}

		page.setRecords(list1);
		return new PageUtils(page);
	}

	/**
	 * G需求数量已部分推荐的行，再推荐时需要拆分未推荐数量
	 * @param lists
	 * @return
	 */
	public static List<Map<String,Object>> wtSplit(List<Map<String,Object>> lists) {
		List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();

		for (Map<String,Object> list : lists) {
			if (!list.get("REQ_ITEM_STATUS").equals("05")) {
				String reqno = list.get("REQUIREMENT_NO").toString();
				String reqitem = list.get("REQUIREMENT_ITEM_NO").toString();
				int flag = 0;// 0为新增数据，1为增加

				for (Map<String,Object> clist : countList) {
					String reqnotemp = clist.get("REQUIREMENT_NO").toString();
					String reqitemtemp = clist.get("REQUIREMENT_ITEM_NO").toString();

					if (reqno.equals(reqnotemp) && reqitem.equals(reqitemtemp) && clist.get("QUANTITY") == null) {
						BigDecimal lqty = list.get("QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(list.get("QUANTITY").toString());
						BigDecimal cqty = clist.get("REQ_QTY") == null ? BigDecimal.ZERO:new BigDecimal(clist.get("REQ_QTY").toString());
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
					Map<String,Object> dataMapTemp = new HashMap<String,Object>();

					dataMapTemp.putAll(list);
					//仓库任务数量
					BigDecimal lqty = dataMapTemp.get("QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(dataMapTemp.get("QUANTITY").toString());
					//需求数量
					BigDecimal cqty = dataMapTemp.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(dataMapTemp.get("QTY").toString());
					//需求行已交接数量
					BigDecimal realqty = dataMapTemp.get("QTY_REAL") == null ? BigDecimal.ZERO:new BigDecimal(dataMapTemp.get("QTY_REAL").toString());
					//仓库任务已交接数量
					BigDecimal realtaskqty = dataMapTemp.get("REAL_QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(dataMapTemp.get("REAL_QUANTITY").toString());
					BigDecimal reqty = cqty.subtract(realqty).subtract(lqty).add(realtaskqty);

					if (lqty.compareTo(BigDecimal.ZERO) > 0) { //已推荐仓库任务的部分
						countList.add(list);
					}
					if (reqty.compareTo(BigDecimal.ZERO) > 0 && lqty.compareTo(BigDecimal.ZERO) > 0) { //部分推荐的，剩余数量拆分出来
						dataMapTemp.put("REQ_QTY", reqty);
						dataMapTemp.put("QUANTITY", null);
						dataMapTemp.put("QTY_XJ", null);
						dataMapTemp.put("TASK_NUM", null);
						dataMapTemp.put("RECOMMEND_QTY", null);
						dataMapTemp.put("CONFIRM_QUANTITY", null);
						dataMapTemp.put("QTY_REAL", null);
						dataMapTemp.put("REAL_QUANTITY", null);
						dataMapTemp.put("REQ_ITEM_STATUS", 00);
						dataMapTemp.put("STORAGE_AREA_CODE", null);
						dataMapTemp.put("BIN_CODE", null);
						dataMapTemp.put("BATCH", null);
						dataMapTemp.put("LGORT", dataMapTemp.get("LGORT_REQ"));
						dataMapTemp.put("SOBKZ", null);
						dataMapTemp.put("LIFNR", dataMapTemp.get("LIFNR_REQ"));
						countList.add(dataMapTemp);
					} else if (reqty.compareTo(BigDecimal.ZERO) > 0 && lqty.compareTo(BigDecimal.ZERO) <= 0) { //需求行未推荐过的
						dataMapTemp.put("REQ_QTY", reqty);
						countList.add(dataMapTemp);
					}

				} else {
					BigDecimal lqty = list.get("QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(list.get("QUANTITY").toString());
					BigDecimal confirmqty = list.get("CONFIRM_QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(list.get("CONFIRM_QUANTITY").toString());
					if (lqty.compareTo(BigDecimal.ZERO) > 0 || confirmqty.compareTo(BigDecimal.ZERO) > 0) {
						countList.add(list);
					}
				}
			} else {
				list.put("QUANTITY", list.get("QTY"));
				list.put("QTY_XJ", list.get("XJ"));
				countList.add(list);
			}

		}

		return countList;
	}

	/**
	 * PC下架
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void picking(Map<String, Object> params) {
		String saveData = params.get("SAVE_DATA").toString();
		String whcode = params.get("WH_NUMBER").toString();
		String werks = params.get("WERKS").toString();
		String requirementNo = params.get("REQUIREMENT_NO").toString();
		Map<String,Object> user = userUtils.getUser();

		List<Map<String, Object>> saveDataList = (List<Map<String, Object>>) JSONArray.parse(saveData);

		List<Map<String, Object>> updateWTList = new ArrayList<Map<String, Object>>(); //更新仓库任务准备
		List<Map<String, Object>> addWTList = new ArrayList<Map<String, Object>>(); //新增仓库任务准备
		List<Map<String, Object>> refWTList = new ArrayList<Map<String, Object>>(); //新增时参考的仓库任务
		List<Map<String, Object>> binList = new ArrayList<Map<String, Object>>(); //更新储位占用单元准备
		List<Map<String, Object>> queryStockParams = new ArrayList<Map<String, Object>>(); //库存查询条件
		List<Map<String, Object>> queryStockForUpdate = new ArrayList<Map<String, Object>>(); //库存查询条件,（不按推荐的下架，有更改批次、储位、供应商的）

		if(saveDataList.size() < 1) {
			redisUtils.unlock(requirementNo, params.get("UID").toString());
			throw new IllegalArgumentException("数据为空");
		}

		for (Map<String, Object> param : saveDataList) {
			param.put("WH_NUMBER", whcode);
			param.put("FROM_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
			param.put("FROM_BIN_CODE", param.get("BIN_CODE"));
//    		param.put("TO_STORAGE_AREA", param.get("STORAGE_AREA_CODE"));
			BigDecimal recommendQty = param.get("RECOMMEND_QTY") == null || param.get("RECOMMEND_QTY").equals("") ? BigDecimal.ZERO:new BigDecimal(param.get("RECOMMEND_QTY").toString());
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

			//是否超需求下架，更新仓库任务数量
			if (param.get("OVERSTEP_REQ_FLAG").equals("X") && (recommendQty.add(confirmQty)).compareTo(taskqty) > 0) {
				param.put("QUANTITY_OLD", taskqty);
				param.put("QUANTITY", recommendQty.add(confirmQty));
				param.put("OVER_QTY", recommendQty.add(confirmQty).subtract(taskqty)); //超过数量
			}

			param.put("CONFIRMOR", user.get("USERNAME"));
			param.put("CONFIRM_TIME", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			param.put("EDITOR", user.get("USERNAME"));
			param.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			param.put("REFERENCE_DELIVERY_NO", requirementNo);
			param.put("REFERENCE_DELIVERY_ITEM", param.get("REQUIREMENT_ITEM_NO"));

			//仓库任务有拆分的
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

				//如果仓库任务拆分后，原始任务没有提交。 则需更新原始任务数量
				Map<String, Object> refParam = new HashMap<String, Object>();
				refParam.put("WH_NUMBER", whcode);
				refParam.put("TASK_NUM", param.get("TASK_NUM_REF"));
				BigDecimal refQty = param.get("QUANTITY_REF") == null || param.get("QUANTITY_REF").equals("") ? BigDecimal.ZERO:new BigDecimal(param.get("QUANTITY_REF").toString());
				refParam.put("QUANTITY", refQty.subtract(taskqty));
				refParam.put("EDITOR", user.get("USERNAME"));
				refParam.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				refWTList.add(refParam);
			} else {
				updateWTList.add(param);
			}

			if (!param.get("BATCH").equals(param.get("BATCH_OLD"))
					|| !param.get("BIN_CODE").equals(param.get("BIN_CODE_OLD"))
					|| !param.get("LIFNR").equals(param.get("LIFNR_OLD"))) {
				Map<String, Object> stockMat = new HashMap<String, Object>();
				stockMat.put("WERKS", werks);
				stockMat.put("WH_NUMBER", whcode);
				stockMat.put("MATNR", param.get("MATNR"));
				stockMat.put("LGORT", param.get("LGORT_OLD"));
				stockMat.put("BIN_CODE", param.get("BIN_CODE_OLD"));
				stockMat.put("BATCH", param.get("BATCH_OLD"));
				stockMat.put("SOBKZ", param.get("SOBKZ_OLD"));
				stockMat.put("LIFNR", param.get("LIFNR_OLD"));
				queryStockForUpdate.add(stockMat);

				param.put("changeflag", "X");
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
			stockMat.put("SO_NO", param.get("SO_NO"));
			stockMat.put("SO_ITEM_NO", param.get("SO_ITEM_NO"));
			queryStockParams.add(stockMat);

		}

		/**
		 * 检查数据是否过期
		 */
		List<Map<String, Object>> checkWTList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> cwt:updateWTList) {
			Map<String, Object> checkWT = new HashMap<String, Object>();
			checkWT.put("WH_NUMBER", cwt.get("WH_NUMBER"));
			checkWT.put("TASK_NUM", cwt.get("TASK_NUM"));
			checkWT.put("VERIFY", cwt.get("VERIFY"));
			checkWT.put("WT_STATUS", "");
			checkWT.put("CONFIRMOR", "");
			checkWTList.add(checkWT);
		}
		List<Map<String, Object>> wtlist = warehouseTasksService.selectCoreWHTaskList(checkWTList);
		if (wtlist.size() != updateWTList.size()) {
			redisUtils.unlock(requirementNo, params.get("UID").toString());
			throw new IllegalArgumentException("待确认的仓库任务数据过期");
		}

		//仓库任务拆分后，参考的原始任务没有提交。 则需更新原始任务数量
		if (refWTList.size() > 0) {
			for (Map<String, Object> param : updateWTList) {
				String tasknum = param.get("TASK_NUM").toString();
				for (Map<String, Object> refparam :refWTList) {
					String reftasknum = param.get("TASK_NUM").toString();
					if (tasknum.equals(reftasknum)) {
						refWTList.remove(refparam);
						break;
					}
				}
			}
		}

		if (refWTList.size() > 0) {
			updateWTList.addAll(refWTList);
		}

		//更新仓库任务
		if (updateWTList.size() > 0) {
			ListUtils.sort(updateWTList, "TASK_NUM", true);
			warehouseTasksService.updateCoreWHTask(updateWTList);
		}

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
		//不按推荐的下架，有更改批次、储位、供应商的
		List<Map<String, Object>> oStockMatList = new ArrayList<Map<String, Object>>();
		if (queryStockForUpdate.size() > 0) {
			oStockMatList = commonService.getWmsStock(queryStockForUpdate);
		}

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
			BigDecimal overQty = param.get("OVER_QTY") == null ? BigDecimal.ZERO:new BigDecimal(param.get("OVER_QTY").toString());

			boolean stockflag = false;
			for (Map<String, Object> bstockmat:bStockMatList) {
				if (param.get("MATNR").equals(bstockmat.get("MATNR")) && param.get("LGORT").equals(bstockmat.get("LGORT"))
						&& param.get("BIN_CODE").equals(bstockmat.get("BIN_CODE"))
						&& (param.get("BATCH") == null || param.get("BATCH").equals("") ? bstockmat.get("BATCH")==null:param.get("BATCH").equals(bstockmat.get("BATCH")))
						&& (param.get("SOBKZ") == null || param.get("SOBKZ").equals("") ? bstockmat.get("SOBKZ")==null:param.get("SOBKZ").equals(bstockmat.get("SOBKZ")))
						&& (param.get("LIFNR") == null || param.get("LIFNR").equals("") ? bstockmat.get("LIFNR")==null:param.get("LIFNR").equals(bstockmat.get("LIFNR")))) {

					Map<String, Object> uStockMat = new HashMap<String,Object>();
					uStockMat.putAll(bstockmat);


					if (overQty.compareTo(BigDecimal.ZERO) > 0  ) {
						BigDecimal stockQty = bstockmat.get("STOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(bstockmat.get("STOCK_QTY").toString());
						if (overQty.compareTo(stockQty) <= 0) {
							uStockMat.put("STOCK_QTY", overQty.negate());
						} else {
							redisUtils.unlock(requirementNo, params.get("UID").toString());
							throw new IllegalArgumentException("超需求下架，超过数量库存不足，下架失败！");
						}
					}

					if (param.get("changeflag") != null && param.get("changeflag").equals("X")) {
						uStockMat.put("STOCK_QTY", recommendQty.negate());
						uStockMat.put("XJ_QTY", recommendQty);
						uStockMat.put("RSB_QTY", "");
					} else if (overQty.compareTo(BigDecimal.ZERO) > 0  ) { //是否超需求下架，超过部分从非限制扣
						BigDecimal stockQty = bstockmat.get("STOCK_QTY") == null ? BigDecimal.ZERO:new BigDecimal(bstockmat.get("STOCK_QTY").toString());
						if (overQty.compareTo(stockQty) <= 0) {
							uStockMat.put("STOCK_QTY", overQty.negate());
							uStockMat.put("XJ_QTY", recommendQty);
							uStockMat.put("RSB_QTY", (recommendQty.subtract(overQty)).negate());
						} else {
							redisUtils.unlock(requirementNo, params.get("UID").toString());
							throw new IllegalArgumentException("超需求下架，超过数量库存不足，下架失败！");
						}
					} else {
						uStockMat.put("STOCK_QTY", "");
						uStockMat.put("XJ_QTY", recommendQty);
						uStockMat.put("RSB_QTY", recommendQty.negate());
					}
					uStockMat.put("VIRTUAL_QTY", "");
					uStockMat.put("VIRTUAL_LOCK_QTY", "");
					uStockMat.put("LOCK_QTY", "");
					uStockMat.put("FREEZE_QTY", "");
					uStockMat.put("EDITOR", user.get("USERNAME"));
					uStockMat.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
					stockMatList.add(uStockMat);
					stockflag = true;
					break;
				}
			}
			if (!stockflag) {
				redisUtils.unlock(requirementNo, params.get("UID").toString());
				throw new IllegalArgumentException("找不到库存，下架失败！");
			}
			//减预留数量，返回至非限制库存
			if(!param.get("BATCH").equals(param.get("BATCH_OLD")) || !param.get("BIN_CODE").equals(param.get("BIN_CODE_OLD"))
					|| !param.get("SOBKZ").equals(param.get("SOBKZ_OLD")) || !param.get("LIFNR").equals(param.get("LIFNR_OLD"))
					|| !param.get("LGORT").equals(param.get("LGORT_OLD")))
				if (oStockMatList.size() > 0) {
					stockflag = false;
					for (Map<String, Object> bstockmat:oStockMatList) {

						if (param.get("MATNR").equals(bstockmat.get("MATNR")) && param.get("LGORT_OLD").equals(bstockmat.get("LGORT"))
								&& param.get("BIN_CODE_OLD").equals(bstockmat.get("BIN_CODE"))
								&& (param.get("BATCH_OLD") == null || param.get("BATCH_OLD").equals("") ? bstockmat.get("BATCH")==null:param.get("BATCH_OLD").equals(bstockmat.get("BATCH")))
								&& (param.get("SOBKZ_OLD") == null || param.get("SOBKZ_OLD").equals("") ? bstockmat.get("SOBKZ")==null:param.get("SOBKZ_OLD").equals(bstockmat.get("SOBKZ")))
								&& (param.get("LIFNR_OLD") == null || param.get("LIFNR_OLD").equals("") ? bstockmat.get("LIFNR")==null:param.get("LIFNR_OLD").equals(bstockmat.get("LIFNR")))) {

							Map<String, Object> uStockMat = new HashMap<String,Object>();
							uStockMat.putAll(bstockmat);
							uStockMat.put("STOCK_QTY", recommendQty);
							uStockMat.put("XJ_QTY", "");
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
						redisUtils.unlock(requirementNo, params.get("UID").toString());
						throw new IllegalArgumentException("找不到库存，下架失败！");
					}
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

	/**
	 * 获取需求已下架数据，打配送标签用（总装）
	 */
	@Override
	public PageUtils queryShippingLabel(Map<String, Object> params) {

		List<Map<String, Object>> reqItem = itemDao.queryShippingLabel(params);

		if (reqItem.size() < 1)
			throw new IllegalArgumentException("无已下架数据！");

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(reqItem);
		page.setTotal(reqItem.size());
		return  new PageUtils(page);
	}

	/**
	 * 获取需求已下架数据，打配送标签用（CS）
	 */
	@Override
	public PageUtils queryShippingLabelcs(Map<String, Object> params) {

		List<Map<String, Object>> reqItem = itemDao.queryShippingLabelcs(params);

		List<String> queryUserInfoParams = new ArrayList<>();
		for (Map<String, Object> map : reqItem) {
			String username = map.get("CONFIRMOR")==null?"":map.get("CONFIRMOR").toString();
			if(!StringUtils.isEmpty(username) && queryUserInfoParams.indexOf(username) <0 ) {
				queryUserInfoParams.add(username);
			}

		}
		if(queryUserInfoParams.size()>0) {
			R userInfoR = sysUserRemote.getUserMap(queryUserInfoParams);
			if(userInfoR.get("data")!=null) {
				Map<String,Object> userInfoMap_all = (Map<String,Object>)userInfoR.get("data");
				for (Map<String, Object> map : reqItem) {
					String username = map.get("CONFIRMOR")==null?"":map.get("CONFIRMOR").toString();
					Map<String, Object> userInfoMap =(Map<String, Object>) userInfoMap_all.get(username);
					map.put("FULL_NAME", userInfoMap.get("FULL_NAME"));
					map.put("full_name", userInfoMap.get("FULL_NAME"));
				}
			}
		}

		if (reqItem.size() < 1)
			throw new IllegalArgumentException("无已下架数据！");

		params.put("pageSize", Integer.MAX_VALUE+"");
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(reqItem);
		page.setTotal(reqItem.size());
		return  new PageUtils(page);
	}

	/**
	 * 获取需求已下架数据，打关键零部件标签用
	 */
	@Override
	public PageUtils queryKeyPartsLabel(Map<String, Object> params) {

		List<Map<String, Object>> reqItem = itemDao.queryKeyPartsLabel(params);

		if (reqItem.size() < 1)
			throw new IllegalArgumentException("无已下架关键零部件数据！");

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(reqItem);
		page.setTotal(reqItem.size());
		return  new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> saveShippingLabel(Map<String, Object> params) {
		List<Map<String, Object>> saveDataList = (List<Map<String, Object>>) JSONArray.parse(params.get("params").toString());

		//按需求行打标签，过滤掉重复的数据
//    	List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
//        for (Map<String,Object> list : saveDataList) {
//            String reqno = list.get("REQUIREMENT_NO").toString();
//            String reqitem = list.get("REQUIREMENT_ITEM_NO").toString();
//            BigDecimal lqty = new BigDecimal(list.get("CONFIRM_QUANTITY").toString());
//            int flag = 0;// 0为新增数据，1为增加
//
//            for (Map<String,Object> clist : countList) {
//                String reqnotemp = clist.get("REQUIREMENT_NO").toString();
//                String reqitemtemp = clist.get("REQUIREMENT_ITEM_NO").toString();
//
//                if (reqno.equals(reqnotemp) && reqitem.equals(reqitemtemp)) {
//                	BigDecimal cqty = new BigDecimal(clist.get("CONFIRM_QUANTITY").toString());
//                	clist.put("CONFIRM_QUANTITY", cqty.add(lqty));
//                	flag = 1;
//                    break;
//                }
//            }
//            if (flag == 0) {
//            	Map<String,Object> dataMapTemp = new HashMap<String,Object>();
//				dataMapTemp.putAll(list);
//            	countList.add(dataMapTemp);
//            }
//        }

		return this.saveCoreLabel(saveDataList);
	}

	/**
	 * 保存标签数据
	 */
	public List<Map<String, Object>> saveCoreLabel(List<Map<String, Object>> matList) {
		String LABEL_STATUS = "10";//00创建，01已收料（待质检），02已收料（无需质检）03待进仓(已质检)，04待退货(已质检)，05收料房退货，06库房退货，07已进仓，08已上架，09已下架，10已出库，11已冻结，12已锁定，20关闭）
		//从行项目中获取收货单号和收货单行项目号
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("WMS_DOC_TYPE", "08");//标签号
		params.put("WERKS", matList.get(0).get("WERKS"));

		List<Map<String,Object>> skList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> updateTaskList = new ArrayList<Map<String,Object>>();
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		Map<String,Object> user = userUtils.getUser();

		for(Map<String, Object> m:matList) {

			Double RECEIPT_QTY=Double.valueOf(m.get("CONFIRM_QUANTITY").toString());
			Double FULL_BOX_QTY=Double.valueOf(m.get("DOSAGE") == null?"1":m.get("DOSAGE").toString());
			int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);
			StringBuffer labelsb = new StringBuffer();

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
				if(i==box_num && i!=1) {
					BOX_QTY = RECEIPT_QTY-(box_num-1)*FULL_BOX_QTY;
					if(BOX_QTY.compareTo(FULL_BOX_QTY) != 0) {
						END_FLAG="X";
					}
				}

				sk.put("WERKS", m.get("WERKS"));
				sk.put("LABEL_NO", LABEL_NO);
				sk.put("LABEL_STATUS", LABEL_STATUS);
				sk.put("SOBKZ", m.get("SOBKZ"));
				sk.put("CREATOR", user.get("USERNAME"));
				sk.put("CREATE_DATE", curDate);
				sk.put("WH_NUMBER", m.get("WH_NUMBER"));
				sk.put("LGORT", m.get("LGORT"));
				sk.put("BOX_SN", BOX_SN);
				sk.put("FULL_BOX_QTY", FULL_BOX_QTY);
				sk.put("BOX_QTY", BOX_QTY);
				sk.put("END_FLAG", END_FLAG);
				sk.put("LIFNR", m.get("LIFNR"));
				sk.put("LIKTX", m.get("LIKTX"));
				sk.put("BATCH", m.get("BATCH"));
				sk.put("BIN_CODE", m.get("POU")==null?"":m.get("POU").toString());
				sk.put("PRODUCT_CODE", m.get("END_MATERIAL_CODE")==null?"":m.get("END_MATERIAL_CODE").toString());
				sk.put("STATION", m.get("POINT_OF_USE")==null?"":m.get("POINT_OF_USE").toString());
				sk.put("ON_LINE_TYPE", m.get("DELIVERY_TYPE")==null?"":m.get("DELIVERY_TYPE").toString());
				sk.put("ON_LINE_MOUTH", m.get("LINE_FEEDING_ROUTE")==null?"":m.get("LINE_FEEDING_ROUTE").toString());
				sk.put("CAR_TYPE", m.get("CAR_TYPE")==null?"":m.get("CAR_TYPE").toString());
				sk.put("DOSAGE", m.get("DOSAGE")==null?"":m.get("DOSAGE").toString());
				sk.put("CONFIGURATION", m.get("CONFIGURATION")==null?"":m.get("CONFIGURATION").toString());
				skList.add(sk);
				labelsb.append(LABEL_NO+ ",");

			}

			Map<String,Object> task=new HashMap<String,Object>();
			String labelstr = labelsb.toString();
			task.put("LABEL_NO", labelstr.equals("") ? "":labelstr.substring(0,labelsb.length()-1));
			task.put("WH_NUMBER", m.get("WH_NUMBER"));
			task.put("REFERENCE_DELIVERY_NO", m.get("REQUIREMENT_NO"));
			task.put("REFERENCE_DELIVERY_ITEM", m.get("REQUIREMENT_ITEM_NO"));
			task.put("TASK_NUM", m.get("TASK_NUM"));
			task.put("EDITOR", user.get("USERNAME"));
			task.put("EDIT_DATE", curDate);
			updateTaskList.add(task);

		}

		warehouseTasksService.updateCoreWHTaskByReq(updateTaskList);

		wmsInReceiptDao.insertCoreLabel(skList);
		return skList;
	}

	@Override
	public PageUtils selectPickList(Map<String, Object> params) {
		List<Map<String, Object>> list= wmsOutPickingDao.selectPickList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		return new PageUtils(page);
	}

	@Override
	public List<WmsOutRequirementHeadEntity> getReq(Map<String, Object> params){
		String requirementNo = params.get("REQUIREMENT_NO").toString();
		String werks = params.get("WERKS").toString();
		String whnumber = params.get("WH_NUMBER").toString();
		List<WmsOutRequirementHeadEntity> head = headDao.selectList(new EntityWrapper<WmsOutRequirementHeadEntity>()
				.eq(StringUtils.isNotBlank(requirementNo),"REQUIREMENT_NO", requirementNo)
				.eq(StringUtils.isNotBlank(werks),"WERKS", werks)
				.eq(StringUtils.isNotBlank(whnumber),"WH_NUMBER", whnumber)
		);

		return head;
	}

}
