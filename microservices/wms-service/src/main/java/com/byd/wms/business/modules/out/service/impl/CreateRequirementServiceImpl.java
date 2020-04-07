package com.byd.wms.business.modules.out.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.ListUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.common.service.impl.CommonServiceImpl;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.impl.WmsCWhServiceImpl;
import com.byd.wms.business.modules.out.dao.SendCreateRequirementDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.*;
import com.byd.wms.business.modules.out.enums.OutModuleEnum;
import com.byd.wms.business.modules.out.enums.RequirementStatusEnum;
import com.byd.wms.business.modules.out.enums.RequirementTypeEnum;
import com.byd.wms.business.modules.out.service.CreateRequirementService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

@Service
public class CreateRequirementServiceImpl implements CreateRequirementService {

	final static Logger logger = LoggerFactory
			.getLogger(CreateRequirementServiceImpl.class);

	@Autowired
	SendCreateRequirementDao sendCreateRequirementDao;
	
	@Autowired
	WmsCPlantService wmsCPlantService;
	
	@Autowired
    private UserUtils userUtils;

	@Autowired
	private WmsSapRemote wmsSapRemote;

	@Autowired
	private OutCommonService commonService;

	@Autowired
	private CommonServiceImpl comService;
	
	@Autowired
	private WmsCWhServiceImpl wmsCWhServiceImpl;

	@Override
	public List<Map<String, Object>> getPlantBusinessTypes(Map<String, Object> params) {
		return sendCreateRequirementDao.selectPlantBusinessTypes(params);
	}

	@Override
	public Map<String, Integer> validProduceOrders(List<ProduceOrderVO> orders) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (ProduceOrderVO order : orders) {
			String orderno = order.getOrderNo().trim();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("aufnr", orderno);
			List<Map<String, Object>> existOrder = sendCreateRequirementDao
					.selectWMSSapMoHead(params);
			// 1.检验订单是否同步到了WMS数据库
			if (CollectionUtils.isEmpty(existOrder)) {
				resultMap.put(orderno, 1);
				continue;
			}
			// TODO:2.检验订单是否存在
			// 3.判断是否有订单所属工厂的操作权限
			/*List<SysDeptEntity> premissionPlants = tagUtils.deptListWithUser("3", null);
			boolean premissionFlag = false;
			for (SysDeptEntity plant : premissionPlants) {
				if (((String) existOrder.get(0).get("WERKS")).equals(plant
						.getCode())) {
					premissionFlag = true;
					break;
				}
			}
			if (premissionFlag == false) {
				resultMap.put(order.getOrderNo(), 3);
				continue;
			}*/

			// 4.判断工厂配置有没有启用核销，没有启用，判断生产订单状态是否为可发料的状态
			WmsCPlant wmsCPlants = wmsCPlantService
					.selectOne(new EntityWrapper<WmsCPlant>().eq("WERKS",
							(String) existOrder.get(0).get("WERKS")));
			if (wmsCPlants == null) {
				throw new IllegalArgumentException("数据错误，工厂配置缺失.");
			}
			if (wmsCPlants.getHxFlag().equals("0")) {
				// 没启用核销
				String istatText = (String) existOrder.get(0).get("ISTAT_TXT");
				if (istatText.indexOf("REL") == -1
						|| (istatText.indexOf("DLFL") != -1 || istatText
								.indexOf("LKD") != -1)) {
					// 不可发料的状态 (可发料状态-->:包含REL状态，不包含LKD和DLFL状态的即为有效订单)
					resultMap.put(orderno, 4);
					continue;
				}
			} else {
				// 启用核销
				// 5.判断【生产组件核销表】是否存在剩余核销数量大于0的组件
				Map<String, Object> hxMoParams = new HashMap<String, Object>();
				hxMoParams.put("aufnr", orderno);
				List<Map<String, Object>> hxMoComponentList = sendCreateRequirementDao
						.selectHxMoComponents(hxMoParams);
				boolean hasHxQtyGtZeroFlag = false;
				for (Map<String, Object> hxMoComponent : hxMoComponentList) {
					if (((BigDecimal) hxMoComponent.get("HX_QTY"))
							.doubleValue() > 0) {
						hasHxQtyGtZeroFlag = true;
						break;
					}
				}
				String istatText = (String) existOrder.get(0).get("ISTAT_TXT");

				if (hasHxQtyGtZeroFlag == true) {
					// 存在：判断MO状态是否为可发料状态（抬头状态字段包含‘REL’或者包含‘TECO’字符串）
					if (istatText.indexOf("REL") == -1
							&& istatText.indexOf("TECO") == -1) {
						resultMap.put(orderno, 5);
					}
				} else {
					// 可发料状态：订单状态（ISTAT_TXT）”，包含REL状态，不包含LKD和DLFL状态的即为有效订单
					if (istatText.indexOf("REL") == -1
							|| (istatText.indexOf("DLFL") != -1 || istatText
									.indexOf("LKD") != -1)) {
						// 不可发料状态
						resultMap.put(orderno, 4);
					}
				}
			}

		}
		return resultMap;
	}

	@Autowired
	WmsCDocNoService docNoService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createRequirementFromICQCenterSplit(List<CostCenterAO> items) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<CostCenterAO>> newList = new ArrayList<List<CostCenterAO>>();
		String errmsg = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("werks", items.get(0).getWerks());
		params.put("whNumber", items.get(0).getWhNumber());
		Map<String, Object> cwh = wmsCWhServiceImpl.getWmsCWh(params);
		if (null != cwh && cwh.get("MAT_MANAGER_FLAG").equals("X")) {
			List<Map<String, Object>> paramslist = new ArrayList<Map<String, Object>>();
			for (CostCenterAO order : items) {
				Map<String, Object> orderparams = new HashMap<String, Object>();
				orderparams.put("WERKS", order.getWerks());
				orderparams.put("WH_NUMBER", order.getWhNumber());
				orderparams.put("MATNR", order.getMATNR().trim());
				paramslist.add(orderparams);
			}
			List<Map<String, Object>> matmanagerlist = comService.getMatManagerList(paramslist);
			for (CostCenterAO order : items) {
				boolean flag = false;
				String werks = order.getWerks();
				String whnumber = order.getWhNumber();
				String matnr = order.getMATNR().trim();
				for (Map<String,Object> matmanager : matmanagerlist) {
					String werks1 = matmanager.get("WERKS").toString();
					String whnumber1 = matmanager.get("WH_NUMBER").toString();
					String matnr1 = matmanager.get("MATNR").toString();
					if (werks.equals(werks1) && whnumber.equals(whnumber1) && matnr.equals(matnr1)) {
						order.setWhManager(matmanager.get("MANAGER_STAFF").toString());
						flag = true;
						break;
					}
				}
				
				if (!flag) {
					errmsg = errmsg + matnr +"：未维护人料关系!" + "<br/>";
				}
			}
			
			if (!errmsg.equals("")) {
				throw new RuntimeException(errmsg);
			}
			
			newList = getListByGroupCostCenter(items);
		}
		
		if (newList.size() > 0 ) {
			for (List<CostCenterAO> orderlist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createRequirementFromICQCenter(orderlist);
				} else {
					reqestNo = reqestNo+ "," + this.createRequirementFromICQCenter(orderlist);
				}
			}
		} else {
			reqestNo = this.createRequirementFromICQCenter(items);
		}
		return reqestNo;
	}
	
	@Override
	public String createRequirementFromICQCenter(List<CostCenterAO> items) {
		WmsOutRequirementHeadEntity outReuirementHead = new WmsOutRequirementHeadEntity();

		outReuirementHead.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		outReuirementHead.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		outReuirementHead.setWerks(items.get(0).getWerks());
		outReuirementHead.setWhNumber(items.get(0).getWhNumber());
		//System.err.println("asdasdasdas   "+items.get(0).getRequireDate());
		outReuirementHead.setRequiredDate(items.get(0).getRequireDate());
		outReuirementHead.setRequiredTime(items.get(0).getRequireTime());
		outReuirementHead.setPurpose(items.get(0).getUse());
		outReuirementHead.setReceiver(items.get(0).getReceiver());
		
		outReuirementHead.setRequiredModel(RequirementTypeEnum.TAKE_MATERIALS
				.getCode());
		outReuirementHead.setRequirementStatus(RequirementStatusEnum.CREATED
				.getCode());
		String requirementNo = docNoService.getDocNo(items.get(0).getWerks(),
				WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		outReuirementHead.setRequirementNo(requirementNo);
		//变更: 头表的需求类型字段保存 BUSINESS_NAME
		outReuirementHead.setRequirementType(OutModuleEnum.ICQ_BUSINESS_NAME.getCode());
		outReuirementHead.setDel("0");
		if(needApproval(OutModuleEnum.ICQ_BUSINESS_NAME, OutModuleEnum.ICQ_BUSINESS_TYPR, OutModuleEnum.BUSINESS_CLASS, items.get(0).getWerks())){
			outReuirementHead.setCheckFlag("X");
		}

		int itemNo = 1;
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		for (CostCenterAO item : items) {
			WmsOutRequirementItemEntity outRequirementItemEntity = new WmsOutRequirementItemEntity();
			outRequirementItemEntity.setBusinessName("44");
			outRequirementItemEntity.setBusinessType("14");
			outRequirementItemEntity.setCostCenter(item.getCostcenterCode());
			outRequirementItemEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			outRequirementItemEntity.setCreateDate(DateUtils.format(new Date(),
					DateUtils.DATE_TIME_PATTERN));
			outRequirementItemEntity.setLgort(item.getLGORT() == null ? "":item.getLGORT().trim());
			outRequirementItemEntity.setLifnr(item.getLIFNR() == null ? "":item.getLIFNR().trim());
			outRequirementItemEntity.setMaktx(item.getMAKTX());
			outRequirementItemEntity.setMatnr(item.getMATNR().trim());
			outRequirementItemEntity.setQty(item.getREQ_QTY());
			outRequirementItemEntity.setReqItemStatus("00");
			outRequirementItemEntity.setUnit(item.getMEINS());
			outRequirementItemEntity.setRequirementNo(requirementNo);
			outRequirementItemEntity.setRequirementItemNo(String
					.valueOf(itemNo));
			outRequirementItemEntity.setDel("0");
			outRequirementItemEntity.setWhManager(item.getWhManager());
			itemList.add(outRequirementItemEntity);
			itemNo++;
		}
		saveOutRequirementHead(outReuirementHead);
		saveOutRequirementItemBatch(itemList);
		return requirementNo;
	}

	@Autowired
	WmsOutRequirementHeadDao headDao;

	@Autowired
	WmsOutRequirementItemDao itemDao;

	@Override
	public void saveOutRequirementHead(WmsOutRequirementHeadEntity head) {
		headDao.insert(head);
	}

	@Override
	public void saveOutRequirementItemBatch(
			List<WmsOutRequirementItemEntity> items) {
		for (WmsOutRequirementItemEntity item : items) {
			itemDao.insert(item);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String saveInternalOrderRequirementSplit(List<InternalOrderAO> items) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<InternalOrderAO>> newList = new ArrayList<List<InternalOrderAO>>();
		String errmsg = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("werks", items.get(0).getWerks());
		params.put("whNumber", items.get(0).getWhNumber());
		Map<String, Object> cwh = wmsCWhServiceImpl.getWmsCWh(params);
		if (null != cwh && cwh.get("MAT_MANAGER_FLAG").equals("X")) {
			List<Map<String, Object>> paramslist = new ArrayList<Map<String, Object>>();
			for (InternalOrderAO order : items) {
				Map<String, Object> orderparams = new HashMap<String, Object>();
				orderparams.put("WERKS", order.getWerks());
				orderparams.put("WH_NUMBER", order.getWhNumber());
				orderparams.put("MATNR", order.getMATNR().trim());
				paramslist.add(orderparams);
			}
			List<Map<String, Object>> matmanagerlist = comService.getMatManagerList(paramslist);
			for (InternalOrderAO order : items) {
				boolean flag = false;
				String werks = order.getWerks();
				String whnumber = order.getWhNumber();
				String matnr = order.getMATNR().trim();
				for (Map<String,Object> matmanager : matmanagerlist) {
					String werks1 = matmanager.get("WERKS").toString();
					String whnumber1 = matmanager.get("WH_NUMBER").toString();
					String matnr1 = matmanager.get("MATNR").toString();
					if (werks.equals(werks1) && whnumber.equals(whnumber1) && matnr.equals(matnr1)) {
						order.setWhManager(matmanager.get("MANAGER_STAFF").toString());
						flag = true;
						break;
					}
				}
				
				if (!flag) {
					errmsg = errmsg + matnr +"：未维护人料关系!" + "<br/>";
				}
			}
			
			if (!errmsg.equals("")) {
				throw new RuntimeException(errmsg);
			}
			
			newList = getListByGroupInternalOrder(items);
		}
		
		if (newList.size() > 0 ) {
			for (List<InternalOrderAO> orderlist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.saveInternalOrderRequirement(orderlist);
				} else {
					reqestNo = reqestNo+ "," + this.saveInternalOrderRequirement(orderlist);
				}
			}
		} else {
			reqestNo = this.saveInternalOrderRequirement(items);
		}
		return reqestNo;
	}
	
	@Override
	public String saveInternalOrderRequirement(List<InternalOrderAO> items) {

		WmsOutRequirementHeadEntity outReuirementHead = new WmsOutRequirementHeadEntity();
		//SysUserEntity user = ShiroUtils.getUserEntity();

		outReuirementHead.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		outReuirementHead.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		outReuirementHead.setWerks(items.get(0).getWerks());
		outReuirementHead.setWhNumber(items.get(0).getWhNumber());
		outReuirementHead.setRequiredDate(items.get(0).getRequirementDate());
		outReuirementHead.setPurpose(items.get(0).getUse());
		outReuirementHead.setRequiredModel(RequirementTypeEnum.TAKE_MATERIALS
				.getCode());
		outReuirementHead.setRequirementStatus(RequirementStatusEnum.CREATED
				.getCode());
		String requirementNo = docNoService.getDocNo(items.get(0).getWerks(),
				WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		outReuirementHead.setRequirementNo(requirementNo);
		outReuirementHead.setRequirementType(OutModuleEnum.INNER_ORDER_BUSINESS_NAME.getCode());
		outReuirementHead.setRequiredDate(items.get(0).getRequireDate());
		outReuirementHead.setRequiredTime(items.get(0).getRequireTime());
		outReuirementHead.setReceiver(items.get(0).getReceiver());

		/*if(needApproval(OutModuleEnum.INNER_ORDER_BUSINESS_NAME, OutModuleEnum.INNER_ORDER_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, items.get(0).getWerks())){
			outReuirementHead.setCheckFlag("X");
		}*/

		int itemNo = 1;
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		for (InternalOrderAO item : items) {
			WmsOutRequirementItemEntity outRequirementItemEntity = new WmsOutRequirementItemEntity();
			outRequirementItemEntity.setBusinessName(OutModuleEnum.INNER_ORDER_BUSINESS_NAME.getCode());
			outRequirementItemEntity.setBusinessType(OutModuleEnum.INNER_ORDER_BUSINESS_TYPE.getCode());
			outRequirementItemEntity.setIoNo(item.getInternalOrder());// 内部订单号
			outRequirementItemEntity.setAutyp(item.getAutyp());
			outRequirementItemEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			outRequirementItemEntity.setCreateDate(DateUtils.format(new Date(),
					DateUtils.DATE_TIME_PATTERN));
			outRequirementItemEntity.setLgort(item.getLGORT() == null ? "":item.getLGORT().trim());
			outRequirementItemEntity.setLifnr(item.getLIFNR() == null ? "":item.getLIFNR().trim());
			if(item.getVENDOR()!=null && !item.getVENDOR().equals("")){
				outRequirementItemEntity.setLifnr(item.getVENDOR().trim());
			}
			if(item.getSTATION()!=null && !item.getSTATION().equals("")){
				outRequirementItemEntity.setStation(item.getSTATION());
			}
			outRequirementItemEntity.setMaktx(item.getMAKTX());
			outRequirementItemEntity.setMatnr(item.getMATNR().trim());
			outRequirementItemEntity.setQty(item.getREQ_QTY());
			outRequirementItemEntity.setReqItemStatus("00");
			outRequirementItemEntity.setUnit(item.getMEINS());
			outRequirementItemEntity.setRequirementNo(requirementNo);
			outRequirementItemEntity.setRequirementItemNo(String
					.valueOf(itemNo));
			outRequirementItemEntity.setWhManager(item.getWhManager());
			itemList.add(outRequirementItemEntity);
			itemNo++;
		}
		saveOutRequirementHead(outReuirementHead);
		saveOutRequirementItemBatch(itemList);
		return requirementNo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createWBSElementOutRequirementSplit(List<WbsElementAO> items) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<WbsElementAO>> newList = new ArrayList<List<WbsElementAO>>();
		String errmsg = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("werks", items.get(0).getWerks());
		params.put("whNumber", items.get(0).getWhNumber());
		Map<String, Object> cwh = wmsCWhServiceImpl.getWmsCWh(params);
		if (null != cwh && cwh.get("MAT_MANAGER_FLAG").equals("X")) {
			List<Map<String, Object>> paramslist = new ArrayList<Map<String, Object>>();
			for (WbsElementAO order : items) {
				Map<String, Object> orderparams = new HashMap<String, Object>();
				orderparams.put("WERKS", order.getWerks());
				orderparams.put("WH_NUMBER", order.getWhNumber());
				orderparams.put("MATNR", order.getMATNR().trim());
				paramslist.add(orderparams);
			}
			List<Map<String, Object>> matmanagerlist = comService.getMatManagerList(paramslist);
			for (WbsElementAO order : items) {
				boolean flag = false;
				String werks = order.getWerks();
				String whnumber = order.getWhNumber();
				String matnr = order.getMATNR().trim();
				for (Map<String,Object> matmanager : matmanagerlist) {
					String werks1 = matmanager.get("WERKS").toString();
					String whnumber1 = matmanager.get("WH_NUMBER").toString();
					String matnr1 = matmanager.get("MATNR").toString();
					if (werks.equals(werks1) && whnumber.equals(whnumber1) && matnr.equals(matnr1)) {
						order.setWhManager(matmanager.get("MANAGER_STAFF").toString());
						flag = true;
						break;
					}
				}
				
				if (!flag) {
					errmsg = errmsg + matnr +"：未维护人料关系!" + "<br/>";
				}
			}
			
			if (!errmsg.equals("")) {
				throw new RuntimeException(errmsg);
			}
			
			newList = getListByGroupWbs(items);
		}
		
		if (newList.size() > 0 ) {
			for (List<WbsElementAO> orderlist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createWBSElementOutRequirement(orderlist);
				} else {
					reqestNo = reqestNo+ "," + this.createWBSElementOutRequirement(orderlist);
				}
			}
		} else {
			reqestNo = this.createWBSElementOutRequirement(items);
		}
		return reqestNo;
	}
	
	@Override
	public String createWBSElementOutRequirement(List<WbsElementAO> items) {
		WmsOutRequirementHeadEntity outReuirementHead = new WmsOutRequirementHeadEntity();
		//SysUserEntity user = ShiroUtils.getUserEntity();

		outReuirementHead.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		outReuirementHead.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		outReuirementHead.setWerks(items.get(0).getWerks());
		outReuirementHead.setWhNumber(items.get(0).getWhNumber());
		outReuirementHead.setRequiredDate(items.get(0).getRequirementDate());
		outReuirementHead.setPurpose(items.get(0).getUse());
		outReuirementHead.setRequiredModel(RequirementTypeEnum.TAKE_MATERIALS
				.getCode());
		outReuirementHead.setRequirementStatus(RequirementStatusEnum.CREATED
				.getCode());
		String requirementNo = docNoService.getDocNo(items.get(0).getWerks(),
				WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		outReuirementHead.setRequirementNo(requirementNo);
		outReuirementHead.setRequirementType(OutModuleEnum.WBS_BUSINESS_NAME.getCode());
		outReuirementHead.setMemo(items.get(0).getMEMO());
		outReuirementHead.setRequiredDate(items.get(0).getRequireDate());
		outReuirementHead.setRequiredTime(items.get(0).getRequireTime());
		outReuirementHead.setReceiver(items.get(0).getReceiver());
		
		if(needApproval(OutModuleEnum.WBS_BUSINESS_NAME, OutModuleEnum.WBS_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, items.get(0).getWerks())){
			outReuirementHead.setCheckFlag("X");
		}

		int itemNo = 1;
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		for (WbsElementAO item : items) {
			WmsOutRequirementItemEntity outRequirementItemEntity = new WmsOutRequirementItemEntity();
			outRequirementItemEntity.setBusinessName(OutModuleEnum.WBS_BUSINESS_NAME.getCode());
			outRequirementItemEntity.setBusinessType(OutModuleEnum.WBS_BUSINESS_TYPE.getCode());
			outRequirementItemEntity.setWbs(item.getWbsElementNo());// 设置WBS元素号
			outRequirementItemEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			outRequirementItemEntity.setCreateDate(DateUtils.format(new Date(),
					DateUtils.DATE_TIME_PATTERN));
			outRequirementItemEntity.setLgort(item.getLGORT() == null ? "":item.getLGORT().trim());
			outRequirementItemEntity.setLifnr(item.getLIFNR() == null ? "":item.getLIFNR().trim());
			outRequirementItemEntity.setMaktx(item.getMAKTX());
			outRequirementItemEntity.setMatnr(item.getMATNR().trim());
			outRequirementItemEntity.setQty(item.getREQ_QTY());
			outRequirementItemEntity.setReqItemStatus("00");
			outRequirementItemEntity.setUnit(item.getMEINS());
			outRequirementItemEntity.setRequirementNo(requirementNo);
			outRequirementItemEntity.setRequirementItemNo(String
					.valueOf(itemNo));
			outRequirementItemEntity.setWhManager(item.getWhManager());

			itemList.add(outRequirementItemEntity);
			itemNo++;
		}
		saveOutRequirementHead(outReuirementHead);
		saveOutRequirementItemBatch(itemList);
		return requirementNo;
	}

	@Autowired
	private WmsCMatStorageService cMatStorageService;

	@Override
	public List<Map<String, Object>> queryProducerOrders(
			String referOrderLgort, String filterZeroRequireLine,
			List<ProduceOrderVO> queryOrderParamsList, String werks,
			String whNumber, boolean IS_PRO_ORD_ADD_MAT,
			boolean IS_PRO_LINE_WAREHOUSE) {
		// 获取工厂配置
		WmsCPlant plantCfg = wmsCPlantService
				.selectOne(new EntityWrapper<WmsCPlant>().eq("werks", werks));

		List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
		//System.err.println("queryOrderParamsList "+queryOrderParamsList);
		for (ProduceOrderVO order : queryOrderParamsList) {
			// 构造查询参数
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("AUFNR", order.getOrderNo());
			params.put("MATNR", order.getMat());
			if (StringUtils.isNotBlank(order.getLocation())) {
				params.put("LGORT",
						Arrays.asList(order.getLocation().split(",")));
			}
			params.put("SORTF", order.getStation());// 工位
			params.put("filterZeroRequireLine", filterZeroRequireLine);
			params.put("WERKS", werks);
			if (IS_PRO_ORD_ADD_MAT) {
				params.put("IS_PRO_ORD_ADD_MAT", "1");
			}
			if (IS_PRO_LINE_WAREHOUSE) {
				params.put("IS_PRO_LINE_WAREHOUSE", "1");
			}
			// 查询生产订单组件信息
			List<Map<String, Object>> sapPOrderList = sendCreateRequirementDao
					.selectProducerOrder(params);
			//System.err.println("sapPOrderListsapPOrderListsapPOrderList "+sapPOrderList);
			// 计算库存数量 & 填充页面的需求数量，库位，工位
			for (Map<String, Object> m : sapPOrderList) {
				Map<String, Object> queryStockMap = new HashMap<String, Object>();
				queryStockMap.put("MATNR", m.get("MATNR"));
				queryStockMap.put("WERKS", werks);
				queryStockMap.put("WH_NUMBER", whNumber);
				if(!StringUtils.isBlank(referOrderLgort) && Integer.parseInt(referOrderLgort)==0){
					m.put("LGORT", "");// 填否 库位置空
				}
				if (StringUtils.isBlank(referOrderLgort)) {
					// 取前端传入的库位
					if (StringUtils.isNotBlank(order.getLocation())) {
						queryStockMap.put("LGORT_LIST",
								Arrays.asList(order.getLocation().split(",")));
					}
					m.put("LGORT", order.getLocation());// 库位
				} else if (m.get("LGORT") != null) {
					// 取订单的库位
					queryStockMap.put("LGORT_LIST", Arrays
							.asList(new String[] { (String) m.get("LGORT") }));
				}
				Double totalStock = sendCreateRequirementDao
						.selectTotalStockQty(queryStockMap);
				m.put("TOTAL_STOCK_QTY", totalStock);

				// 计算已领数量
//				Double  receivedQTy = sendCreateRequirementDao
//						.selectHasReceivedQty2("41",m.get("AUFNR").toString(), m.get("MATNR").toString(),m.get("POSNR").toString());
				//m.put("TL_QTY",m.get("OUT_QTY"));

				/*BUG #1567
				已领数量：通过生产订单+料号+行项目号查询【生产订单组件表-WMS_SAP_MO_COMPONENT】中TL_QTY
				可领数量：订单需求-已领数量-状态未关闭且未删除，实发数量为0的行项目需求数量【WMS出库需求明细-WMS_OUT_REQUIREMENT_ITEM】
				-----2019.11.5*/
				// 计算可领数量KL_QTY =m.get("BDMNG") -m.get(TL_QTY) - count

				Integer  count = itemDao.queryRequirementByRealQty(m);
				BigDecimal bdmng = m.get("BDMNG") == null ? BigDecimal.ZERO : new BigDecimal(m.get("BDMNG").toString());
				BigDecimal tl_qty = m.get("TL_QTY") == null ? BigDecimal.ZERO : new BigDecimal(m.get("TL_QTY").toString());
				BigDecimal requirement = count == null ? BigDecimal.ZERO : BigDecimal.valueOf(count);
				BigDecimal kl_qty = bdmng.subtract(tl_qty).subtract(requirement);
				m.put("KL_QTY",kl_qty);
				m.put("TL_QTY",tl_qty);

				// 如果用户明确输入了，需求数量不计算
				if (order.getRequireSuitQty()==null){
						m.put("REQ_QTY", null);
				}else if (m.get("PSMNG") != null
						&& order.getRequireSuitQty() != null
						&& m.get("BDMNG") != null) {
					// 需求数量 = 需求套数 * (订单项需求数量/订单头需求数量)
					Double BDMNG = ((BigDecimal) m.get("BDMNG")).doubleValue();// 订单组件需求数量
																				// (例:轮胎需求数
																				// 40)
					Double PSMNG = ((BigDecimal) m.get("PSMNG")).doubleValue();// 订单数量
																				// (例:定购车辆数
																				// 10)
                    //System.err.println("order.getRequireSuitQty()================= "+order.getRequireSuitQty());
                    //System.err.println("PSMNG================= "+PSMNG);
                    //System.err.println("BDMNG================= "+BDMNG);
					m.put("REQ_QTY", order.getRequireSuitQty()
							* (BDMNG / PSMNG));
					if (IS_PRO_LINE_WAREHOUSE && m.get("REQ_QTY") != null) {
						// 线边仓领料类型，需要计算最小包装数量。
						if (plantCfg != null
								&& plantCfg.getPackageFlag() != null
								&& plantCfg.getPackageFlag().equals("X")) {
							// 配置了需要计算最小包装，查询物料储存配置。
							WmsCMatStorageEntity matStoCfg = cMatStorageService
									.selectOne(new EntityWrapper<WmsCMatStorageEntity>()
											.eq("werks", werks)
											.eq("matnr", m.get("MATNR"))
											.eq("wh_Number", whNumber));
							if (matStoCfg != null) {
								Long minQty = matStoCfg.getQty();
								// 需求数设置为最小包装的整数倍
								int p = (int) (((Double) m.get("REQ_QTY"))
										.doubleValue() / minQty) + 1;
								m.put("REQ_QTY", p * minQty);
							}
						}
					}
				}
				//System.err.println("map.getREQ_QTY "+ m.get("REQ_QTY"));
				if(!StringUtils.isBlank(order.getStation())){
					m.put("STATION", order.getStation());// 设置工位字段（从查询页面输入的）
				}else {
					m.put("STATION", m.get("SORTF"));//
				}

			}
			System.err.println("end ===================== "+sapPOrderList.toString());
			// 合格的生产订单行项目，添加到结果集。
			orderList.addAll(sapPOrderList);
		}
		return orderList;
	}

	/**
	 *  创建生产订单领料需求(含人料关系拆单)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createProduceOrderOutReqSplit(List<CreateProduceOrderAO> orderItems) throws Exception {
		String reqNo = "";
		//是否校验人料关系
		List<List<CreateProduceOrderAO>> newList = new ArrayList<List<CreateProduceOrderAO>>();
		String errmsg = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("werks", orderItems.get(0).getWerks());
		params.put("whNumber", orderItems.get(0).getWhNumber());
		Map<String, Object> cwh = wmsCWhServiceImpl.getWmsCWh(params);
		if (null != cwh && cwh.get("MAT_MANAGER_FLAG").equals("X")) {
			List<Map<String, Object>> paramslist = new ArrayList<Map<String, Object>>();
			for (CreateProduceOrderAO order : orderItems) {
				Map<String, Object> orderparams = new HashMap<String, Object>();
				orderparams.put("WERKS", order.getWerks());
				orderparams.put("WH_NUMBER", order.getWhNumber());
				orderparams.put("MATNR", order.getMATNR().trim());
				paramslist.add(orderparams);
			}
			List<Map<String, Object>> matmanagerlist = comService.getMatManagerList(paramslist);
			for (CreateProduceOrderAO order : orderItems) {
				boolean flag = false;
				String werks = order.getWerks();
				String whnumber = order.getWhNumber();
				String matnr = order.getMATNR().trim();
				for (Map<String,Object> matmanager : matmanagerlist) {
					String werks1 = matmanager.get("WERKS").toString();
					String whnumber1 = matmanager.get("WH_NUMBER").toString();
					String matnr1 = matmanager.get("MATNR").toString();
					if (werks.equals(werks1) && whnumber.equals(whnumber1) && matnr.equals(matnr1)) {
						order.setWhManager(matmanager.get("MANAGER_STAFF").toString());
						flag = true;
						break;
					}
				}
				
				if (!flag) {
					errmsg = errmsg + matnr +"：未维护人料关系!" + "<br/>";
				}
			}
			
			if (!errmsg.equals("")) {
				throw new RuntimeException(errmsg);
			}
			
			newList = getListByGroup(orderItems);
		}
		
		if (newList.size() > 0 ) {
			for (List<CreateProduceOrderAO> orderlist : newList) {
				if (reqNo.equals("")) {
					reqNo = this.createProduceOrderOutReq(orderlist);
				} else {
					reqNo = reqNo+ "," + this.createProduceOrderOutReq(orderlist);
				}
			}
		} else {
			reqNo = this.createProduceOrderOutReq(orderItems);
		}
		return reqNo;
	}
	
	/**
	 * 创建生产订单领料需求
	 * 
	 * @throws CloneNotSupportedException
	 */
	@Override
	public String createProduceOrderOutReq(List<CreateProduceOrderAO> orderItems) throws Exception {
		if (CollectionUtils.isEmpty(orderItems)) {
			throw new IllegalArgumentException("参数为空");
		}
		// 校验必填字段
		for (CreateProduceOrderAO order : orderItems) {
			if (StringUtils.isBlank(order.getAUFNR())) {
				throw new IllegalArgumentException("必填字段不能为空");
			}
		}
		//SysUserEntity user = ShiroUtils.getUserEntity();
		String requirementNo = docNoService.getDocNo(orderItems.get(0)
				.getWerks(), WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		// 创建出库需求头
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		head.setRequirementNo(requirementNo);
		head.setWerks(orderItems.get(0).getWerks());
		head.setWhNumber(orderItems.get(0).getWhNumber());
		head.setPurpose(orderItems.get(0).getUse());
		//TODO:改成枚举类型
		head.setRequirementType(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());
		head.setRequirementStatus("00");
		head.setRequiredTime(orderItems.get(0).getRequireTime());
		//head.setRequiredDate(orderItems.get(0).getRequireDate());
		head.setRequiredModel(orderItems.get(0).getSummaryMode());// 备料模式
		head.setDel("0");
		head.setRequiredDate(orderItems.get(0).getRequireDate());
		head.setRequiredTime(orderItems.get(0).getRequireTime());
		head.setReceiver(orderItems.get(0).getReceiver());
		// 创建出库需求项
		int requirementItemNo = 1;
		System.err.println("orderItems===>>> "+orderItems.toString());
		for (CreateProduceOrderAO order : orderItems) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			setCommomPrams(item, order, requirementNo, requirementItemNo);
			System.err.println("requirementItemNo===>>> "+requirementItemNo);
			if (order.getHX_QTY() != null && order.getHX_QTY() > 0) {
				// 剩余核销数量 > 0
				if (order.getREQ_QTY().doubleValue() < order.getHX_QTY()
						.doubleValue()) {
					// 需求数量小于剩余核销数量
					item.setBusinessType(OutModuleEnum.OUT_PRO_HX_BUSINESS_TYPE.getCode());// 生产订单(A)
					item.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());// 生产订单领料(261)
				} else {
					/*
					 * 组件需求拆成两个行项目, 生产订单(A)，数量=核销数量，生产订单，数量=需求数量-核销数量
					 */
					WmsOutRequirementItemEntity itemA = new WmsOutRequirementItemEntity();
					WmsOutRequirementItemEntity itemB = new WmsOutRequirementItemEntity();

					setCommomPrams(itemA, order, requirementNo,
							requirementItemNo);
					setCommomPrams(itemB, order, requirementNo,
							requirementItemNo);

					itemA.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());
					itemA.setBusinessType(OutModuleEnum.OUT_PRO_HX_BUSINESS_TYPE.getCode());
					// 是否需要审批
					System.err.println("================================================");
					System.err.println(orderItems.get(0)
							.getWerks());
					System.err.println(itemA.getBusinessType());
					System.err.println(itemA
							.getBusinessName());
					if (commonService.checkApproval(orderItems.get(0)
							.getWerks(), itemA.getBusinessType(), itemA
							.getBusinessName())) {
						head.setCheckFlag("X");
					}
					itemA.setQty(order.getHX_QTY());

					itemB.setBusinessType(OutModuleEnum.OUT_PRO_BUSINESS_TYPE.getCode());
					itemB.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());
					// 是否需要审批
					if (commonService.checkApproval(orderItems.get(0)
							.getWerks(), itemB.getBusinessType(), itemB
							.getBusinessName())) {
						head.setCheckFlag("X");
					}
					itemB.setQty(order.getREQ_QTY() - order.getHX_QTY());

					setHxFlag(itemA);
					setHxFlag(itemB);
					itemDao.insert(itemA);
					itemDao.insert(itemB);

					continue;
				}
			}

			item.setBusinessType(OutModuleEnum.OUT_PRO_BUSINESS_TYPE.getCode());// 生产订单
			item.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());// 生产订单领料(261)
			// 是否需要审批
			if (commonService.checkApproval(orderItems.get(0).getWerks(),
					item.getBusinessType(), item.getBusinessName())) {
				head.setCheckFlag("X");
			}

			// 设置 hx_flag
			System.err.println("item===>>> "+item.toString());
			setHxFlag(item);
			itemDao.insert(item);
			requirementItemNo++;
		}
		headDao.insert(head);
		return requirementNo;
	}

	/**
	 * 生产订单出库需求的 hx_flag字段
	 * 
	 * @param item
	 */
	private void setHxFlag(WmsOutRequirementItemEntity item) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessType", item.getBusinessType());
		params.put("businessName", item.getBusinessName());
		List<Map<String, Object>> hxFlagMap = sendCreateRequirementDao
				.selectHxFlag(params);
		if (!CollectionUtils.isEmpty(hxFlagMap)) {
			item.setHxFlag((String) hxFlagMap.get(0).get("SPECIAL_FLAG"));
		}

	}

	/**
	 * 设置生产订单通用属性
	 * 
	 * @param item
	 * @param order
	 * @param requirementNo
	 * @param requirementItemNo
	 */
	private void setCommomPrams(WmsOutRequirementItemEntity item,
			CreateProduceOrderAO order, String requirementNo,
			Integer requirementItemNo) {
		item.setUnit(order.getMEINS());
		item.setStation(order.getSTATION());
		item.setSobkz(order.getSOBKZ());
		item.setRequirementNo(requirementNo);
		item.setRequirementItemNo(String.valueOf(requirementItemNo));
		//requirementItemNo++;
		item.setReqItemStatus("00");
		item.setQty(order.getREQ_QTY());
		item.setMoNo(order.getAUFNR());
		item.setMoItemNo(order.getPOSNR());
		item.setRsnum(order.getRSNUM());
		item.setRspos(order.getRSPOS());
		item.setMatnr(order.getMATNR().trim());
		item.setMaktx(order.getMAKTX());
		item.setLifnr(order.getVENDOR());
		item.setLgort(order.getLGORT());
		item.setDel("0");
		item.setLine(order.getLINE());
		//item.setLifnr(order.getL);
		//SysUserEntity user = ShiroUtils.getUserEntity();
		item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		item.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		item.setWhManager(order.getWhManager());
	}

	@Override
	public List<Map<String, Object>> queryMatUseing(String werks) {
		return sendCreateRequirementDao.selectMatUsing(werks);
	}

	@Override
	public Map<String, Integer> validateOutItems6(List<Map<String, Object>> list) {
		Map<String, Integer> mapResp = new HashMap<String, Integer>();
		//List<SysDeptEntity> depts = tagUtils.deptListByType("3");
		for (Map<String, Object> item : list) {
			List<Map<String, Object>> result = sendCreateRequirementDao.selectSapPoHead(item);
			if (result.size() < 1) {
				// SAP采购订单未同步到WMS
				mapResp.put((String) item.get("EBELN"), 1);
			} else if ("X".equals(((String) result.get(0).get("FRGRL")))) {
				// SAP采购订单未审批
				mapResp.put((String) item.get("EBELN"), 2);
			}
			
			/*boolean prems = false;
			for(SysDeptEntity dept:depts){
				if(dept.getCode().equals((String)result.get(0).get("WERKS"))){
					prems = true;
				}
			}
			if(prems == false){
				//权限检查
				mapResp.put((String) item.get("EBELN"), 3);
			}*/
		}
		return mapResp;
	}

	@Override
	public List<Map<String, Object>> queryOutItems6(
			List<Map<String, Object>> list) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> params : list) {
			List<Map<String, Object>> sapPoList = sendCreateRequirementDao.selectOutReqItems6(params);
			resultList.addAll(sapPoList);
		}
		return resultList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String outReqCreate6Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.outReqCreate6(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.outReqCreate6(maplist);
				}
			}
		} else {
			reqestNo = this.outReqCreate6(list);
		}
		return reqestNo;
	}
	
	@Override
	public String outReqCreate6(List<Map<String, Object>> list) {
		System.err.println(list.toString());
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");

		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus("00");
		//TODO:...
		head.setRequirementType(OutModuleEnum.WY_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setRequiredModel("01");// 只有订单备料??
		head.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());

		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),
					DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setPoNo((String) reqObj.get("EBELN"));
			item.setPoItemNo((String) reqObj.get("EBELP"));
			item.setQty(Double.parseDouble((String) reqObj.get("REQ_QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("TXZ01"));
			item.setLifnr((String) reqObj.get("VENDOR"));
			item.setCustomer((String) reqObj.get("w_vendor"));
			item.setLgort((String) reqObj.get("LGORT"));
			item.setReqItemStatus("00");
			item.setRequirementItemNo(String.valueOf(reqItemNo));
			reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("MEINS"));
			item.setBusinessName(OutModuleEnum.WY_BUSINESS_NAME.getCode());
			item.setBusinessType(OutModuleEnum.WY_BUSINESS_TYPE.getCode());
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			// TODO： ..还有其他字段??
			itemList.add(item);
		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String outReqCreate7Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.outReqCreate7(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.outReqCreate7(maplist);
				}
			}
		} else {
			reqestNo = this.outReqCreate7(list);
		}
		return reqestNo;
	}
	
	@Override
	public String outReqCreate7(List<Map<String, Object>> list) {

		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException();
		}
		//String staffNumber = (String) list.get(0).get("staffNumber");
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String reqNo = docNoService.getDocNo(werks, docType);
		System.err.println("reqNo   "+reqNo);
		String requireDate = (String) list.get(0).get("requireDate");
		String use = (String) list.get(0).get("use");
		String acceptPlant = (String) list.get(0).get("acceptPlant");
		String MEMO = (String) list.get(0).get("MEMO");//备注

		WmsOutRequirementHeadEntity outReqHeadEntity = new WmsOutRequirementHeadEntity();
		outReqHeadEntity.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		outReqHeadEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		outReqHeadEntity.setRequirementNo(reqNo);
//		outReqHeadEntity.setRequirementType(OutModuleEnum.OUT_SALES_BUSINESS_NAME.getCode());
		outReqHeadEntity.setRequirementStatus("00");
		outReqHeadEntity.setWerks(werks);
		outReqHeadEntity.setWhNumber(whNumber);
		outReqHeadEntity.setRequiredDate(requireDate);
		outReqHeadEntity.setRequiredTime((String) list.get(0).get("requireTime"));
		outReqHeadEntity.setPurpose(use);
		outReqHeadEntity.setReceiveWerks(acceptPlant != null?acceptPlant.toUpperCase():null);
		outReqHeadEntity.setMemo(MEMO);
		outReqHeadEntity.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());

		//outReqHeadEntity.setCheckFlag(needApproval(OutModuleEnum.OUT_SALES_BUSINESS_NAME,OutModuleEnum.OUT_SALES_BUSINESS_TYPE,OutModuleEnum.BUSINESS_CLASS,werks)?"X":"");
		System.err.println(list.toString());
		List<WmsOutRequirementItemEntity> items = new ArrayList<WmsOutRequirementItemEntity>();
		int itemNo = 1;
		for (Map<String, Object> item : list) {
			WmsOutRequirementItemEntity outReqItemEntity = new WmsOutRequirementItemEntity();
			String VBELN = (String) item.get("VBELN");
			String POSNR = (String) item.get("POSNR");
			String MATNR = (String) item.get("MATNR");
			//String MAKTX = (String) item.get("TXZ01");
			String MAKTX = (String) item.get("ARKTX");
			String MEINS = (String) item.get("MEINS");
			String UNIT = (String) item.get("LMEIN");
			String QTY = (String) item.get("REQ_QTY");
			String LGORT = (String) item.get("LGORT");
			String aceptLgortList = (String) item.get("aceptLgortList");
			String LIFNR = (String) item.get("LIFNR");
			String PO_NO = (String) item.get("VGBEL");
			String PO_ITEM_NO = (String) item.get("VGPOS");
			String EXPECT_BOX_QTY = (String) item.get("EXPECT_BOX_QTY");//预计箱数
			String BWART = (String) item.get("BWART");//移动类型
			//VGBEL VGPOS
			String VBELV = (String) item.get("VBELV");
			String POSNV = (String) item.get("POSNV");
			String SOBKZ = (String) item.get("SOBKZ");
			System.out.println("EXPECT_BOX_QTY   "+EXPECT_BOX_QTY);
			System.out.println("BWART   "+BWART);
			outReqItemEntity.setRequirementNo(reqNo);
			outReqItemEntity.setRequirementItemNo(String.valueOf(itemNo));
			/*if(BWART!=null && BWART.equals("601")){
				outReqItemEntity.setBusinessName(OutModuleEnum.SAP_BUSINESS_NAME2.getCode());//业务名称
				outReqItemEntity.setBusinessType(OutModuleEnum.SAP_BUSINESS_TYPE2.getCode());//业务类型
				outReqHeadEntity.setRequirementType(OutModuleEnum.SAP_BUSINESS_NAME2.getCode());
			}else if(BWART!=null && BWART.equals("645")){
				outReqItemEntity.setBusinessName(OutModuleEnum.SAP_BUSINESS_NAME.getCode());//业务名称
				outReqItemEntity.setBusinessType(OutModuleEnum.SAP_BUSINESS_TYPE.getCode());//业务类型
				outReqHeadEntity.setRequirementType(OutModuleEnum.SAP_BUSINESS_NAME.getCode());
			}*/
			outReqItemEntity.setSapOutNo(VBELN);
			outReqItemEntity.setSapOutItemNo(POSNR);
			outReqItemEntity.setReqItemStatus("00");
			outReqItemEntity.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			outReqItemEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			outReqItemEntity.setMatnr(MATNR.trim());
			outReqItemEntity.setMaktx(MAKTX);
			outReqItemEntity.setQty(Double.parseDouble(QTY));
			outReqItemEntity.setLgort(LGORT);
			outReqItemEntity.setReceiveLgort(aceptLgortList);
			outReqItemEntity.setLifnr(LIFNR);

			outReqItemEntity.setSobkz(SOBKZ);

			//VGBEL 采购订单 VGPOS    VBELV 销售订单 POSNV
			//311采购
			if(item.get("requireTypes").equals("50")){
				outReqItemEntity.setPoNo(PO_NO);
				outReqItemEntity.setPoItemNo(PO_ITEM_NO);
				outReqItemEntity.setBusinessName(OutModuleEnum.SAP_BUSINESS_NAME.getCode());//业务名称
				outReqItemEntity.setBusinessType(OutModuleEnum.SAP_BUSINESS_TYPE.getCode());//业务类型
				outReqHeadEntity.setRequirementType(OutModuleEnum.SAP_BUSINESS_NAME.getCode());
			}
			//outReqItemEntity.setUnit(UNIT);
			outReqItemEntity.setUnit(MEINS);
			outReqItemEntity.setMeins(MEINS);
			outReqItemEntity.setBoxCount(Long.valueOf(EXPECT_BOX_QTY));
			//601销售
			if(item.get("requireTypes").equals("51")){
				VBELV= VBELV.replaceAll("^(0+)", "");
				outReqItemEntity.setSoNo(VBELV);
				outReqItemEntity.setSoItemNo(POSNV);
				outReqItemEntity.setBusinessName(OutModuleEnum.SAP_BUSINESS_NAME2.getCode());//业务名称
				outReqItemEntity.setBusinessType(OutModuleEnum.SAP_BUSINESS_TYPE2.getCode());//业务类型
				outReqHeadEntity.setRequirementType(OutModuleEnum.SAP_BUSINESS_NAME2.getCode());
			}
			
			outReqItemEntity.setWhManager((String) item.get("WH_MANAGER"));
			
			items.add(outReqItemEntity);
			itemDao.insert(outReqItemEntity);
			itemNo++;
		}
		headDao.insert(outReqHeadEntity);
		return reqNo;
	}


	@Override
	public Map<String, Integer> validateOutItem8(List<Map<String, Object>> list) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (Map<String, Object> item : list) {
			// 采购订单抬头-WMS_SAP_PO_HEAD
			// 1.查询采购订单是否存在
			Map<String, Object> params = new HashMap<String, Object>();
			String EBELN = (String) item.get("EBELN");
			params.put("EBELN", EBELN);
			List<Map<String, Object>> sapPoList = sendCreateRequirementDao
					.selectSapPoHead(params);
			if (CollectionUtils.isEmpty(sapPoList)) {
				resultMap.put(EBELN, 1);// 不存在
				break;
			}
			// 2.判断类型是否为转储单
			Map<String, Object> sapPoHead = sapPoList.get(0);
			String BSTYP = (String) sapPoHead.get("BSTYP");
			if (StringUtils.isBlank(BSTYP)
					|| (BSTYP.indexOf("UB") == -1 && BSTYP.indexOf("ZUB") == -1)) {
				resultMap.put(EBELN, 2);// 不是UB转储类型
				break;
			}
			// 3.判断是否为已审批状态
			String FRGRL = (String) sapPoHead.get("FRGRL");
			if ("X".equals(FRGRL)) {
				resultMap.put(EBELN, 3);// 没有审批
				break;
			}
			// 4.判断是否存在该工厂下的采购订单
			// TODO:
			// 5.判断采购订单的werks是否与接收工厂一致
			String acceptWerks = (String) item.get("acceptWerks");
			if (!acceptWerks.equals((String) sapPoHead.get("WERKS"))) {
				resultMap.put(EBELN, 5);
			}
		}
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> queryOutItem8(
			List<Map<String, Object>> list) {
		List<Map<String, Object>> outItemList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> item : list) {
			String reqQty = (String) item.get("qty");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("EBELN", (String) item.get("EBELN"));
			params.put("MATNR", (String) item.get("MATNR"));
			List<Map<String, Object>> sappo = sendCreateRequirementDao
					.selectSapPoHead(params);

			String werks = (String) item.get("werks");
			String whNumber = (String) item.get("whNumber");
            System.err.println("sappo "+sappo.toString());
			for (Map<String, Object> sapPoItem : sappo) {
				// 计算库存数量
				String matnr = (String) sapPoItem.get("MATNR");
				String poNo = (String) sapPoItem.get("EBELN");
				String poItemNo = (String) sapPoItem.get("EBELP");
				Map<String, Object> stockParams = new HashMap<String, Object>();
				stockParams.put("WERKS", werks);
				stockParams.put("MATNR", matnr);
				stockParams.put("WH_NUMBER", whNumber);
				Double totalStockQty = sendCreateRequirementDao
						.selectTotalStockQty(stockParams);
				sapPoItem.put("TOTAL_STOCK_QTY", totalStockQty);
				Double menGe = Double.parseDouble(sapPoItem.get("MENGE").toString());
				// 计算已领数量
				Double  receivedQTy = sendCreateRequirementDao
						.selectHasReceivedQty("64",poNo, matnr,poItemNo);
				sapPoItem.put("RECEIVED_QTY", receivedQTy);
				// 计算可领数量
				/*Double availableQty = sendCreateRequirementDao
						.selectAvaliableQty("64",poNo, matnr);*/
				sapPoItem.put("AVAILABLE_QTY", menGe-receivedQTy);
				// 需求数量,填充从页面的到的值
				sapPoItem.put("REQ_QTY", reqQty);
			}
			outItemList.addAll(sappo);
		}
		return outItemList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createReqOutItem8Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createReqOutItem8(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createReqOutItem8(maplist);
				}
			}
		} else {
			reqestNo = this.createReqOutItem8(list);
		}
		return reqestNo;
	}
	
	/**
	 * 创建UB转储单出库需求
	 * 
	 * @param list
	 * @return
	 */
	@Override
	public String createReqOutItem8(List<Map<String, Object>> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException();
		}
		//SysUserEntity user = ShiroUtils.getUserEntity();
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String reqNo = docNoService.getDocNo(werks, docType);
		String requireDate = (String) list.get(0).get("requireDate");
		String use = (String) list.get(0).get("use");
		String acceptPlant = (String) list.get(0).get("acceptPlant");
		WmsOutRequirementHeadEntity outReqHeadEntity = new WmsOutRequirementHeadEntity();
		outReqHeadEntity.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		outReqHeadEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		outReqHeadEntity.setRequirementNo(reqNo);
		outReqHeadEntity.setRequirementType(OutModuleEnum.UB_BUSINESS_NAME.getCode());
		outReqHeadEntity.setRequirementStatus("00");
		outReqHeadEntity.setWerks(werks);
		outReqHeadEntity.setWhNumber(whNumber);
		outReqHeadEntity.setRequiredDate(requireDate);
		outReqHeadEntity.setRequiredTime((String) list.get(0).get("requireTime"));
		outReqHeadEntity.setPurpose(use);
		outReqHeadEntity.setReceiveWerks(acceptPlant != null?acceptPlant.toUpperCase():null);
		
		if(needApproval(OutModuleEnum.UB_BUSINESS_NAME,OutModuleEnum.UB_BUSINESS_TYPE,OutModuleEnum.BUSINESS_CLASS,werks)){
			outReqHeadEntity.setCheckFlag("X");
		}

		List<WmsOutRequirementItemEntity> items = new ArrayList<WmsOutRequirementItemEntity>();
		int itemNo = 1;
		for (Map<String, Object> item : list) {
			WmsOutRequirementItemEntity outReqItemEntity = new WmsOutRequirementItemEntity();
			String MATNR = (String) item.get("MATNR");
			String MAKTX = (String) item.get("TXZ01");
			String MEINS = (String) item.get("MEINS");//采购单位
			String UNIT = (String) item.get("LMEIN");//基本单位
			String QTY = (String) item.get("REQ_QTY");
			String LGORT = (String) item.get("LGORT");
			String LIFNR = (String) item.get("LIFNR");
			String PO_NO = (String) item.get("EBELN");
			String PO_ITEM_NO = (String) item.get("EBELP");

			/**
			 * bug191118004
			 * @author rain
			 * UB转储：1、当基本单位为空
			 * 		  2、则查询物料主数据的MEINS（基本单位）
			 * 		  3、比较物料主数据的基本单位，与UB转储的采购单位是否一致，不一致提示手工同步！
			 */
			if(UNIT==null || "".equals(UNIT) || "null".equals(UNIT)){
				Map<String, Object> params_a = new HashMap<String, Object>();
				params_a.put("WERKS", werks);
				params_a.put("MATNR", MATNR);
				Map<String,Object> matnrinfo = comService.getMaterialInfo(params_a);
				String sapMaterMeins=matnrinfo.get("MEINS")==null?"":matnrinfo.get("MEINS").toString() ;
				if(sapMaterMeins.equals(MEINS)){
					UNIT = sapMaterMeins;
				}else{
					throw new IllegalArgumentException("采购订单:【\""+PO_NO+"\"】物料:【\""+MATNR+"\"】的采购单位:【\""+MEINS+"\"】和SAP物料的基本单位:【\""+sapMaterMeins+"\"】不一致，请手工同步物料的基本单位！");
				}
			}

			outReqItemEntity.setRequirementNo(reqNo);
			outReqItemEntity.setRequirementItemNo(String.valueOf(itemNo));
			outReqItemEntity.setBusinessName(OutModuleEnum.UB_BUSINESS_NAME.getCode());
			outReqItemEntity.setBusinessType(OutModuleEnum.UB_BUSINESS_TYPE.getCode());
			outReqItemEntity.setReqItemStatus("00");
			outReqItemEntity.setCreateDate(DateUtils.format(new Date(),
					DateUtils.DATE_TIME_PATTERN));
			outReqItemEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			outReqItemEntity.setMatnr(MATNR.trim());
			outReqItemEntity.setMaktx(MAKTX);
			outReqItemEntity.setQty(Double.parseDouble(QTY));
			outReqItemEntity.setLgort(LGORT);
			outReqItemEntity.setLifnr(LIFNR);
			outReqItemEntity.setPoNo(PO_NO);
			outReqItemEntity.setPoItemNo(PO_ITEM_NO);
			outReqItemEntity.setUnit(UNIT);
			outReqItemEntity.setMeins(MEINS);
			outReqItemEntity.setWhManager((String) item.get("WH_MANAGER"));

			items.add(outReqItemEntity);
			itemDao.insert(outReqItemEntity);
			itemNo++;
		}
		headDao.insert(outReqHeadEntity);
		return reqNo;
	}

	public Map<String, Integer> validateOutItems10(List<Map<String, Object>> list,List<String> depts) {
		//校验外部销售发货（251）
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		//List<SysDeptEntity> depts = tagUtils.deptListByType("3");
		for (Map<String, Object> item : list) {
			// 1.查询采购订单是否存在
			Map<String, Object> params = new HashMap<String, Object>();
			String EBELN = (String) item.get("EBELN");
			params.put("EBELN", EBELN);
			List<Map<String, Object>> sapPoList = sendCreateRequirementDao
					.selectSapPoHead(params);
			if (CollectionUtils.isEmpty(sapPoList)) {
				resultMap.put(EBELN, 1);// 不存在
				break;
			}

			Map<String, Object> sapPoHead = sapPoList.get(0);
			
			// 2.判断是否为已审批状态
			String FRGRL = (String) sapPoHead.get("FRGRL");
			if ("X".equals(FRGRL)) {
				resultMap.put(EBELN, 2);
				//没有审批
				break;
			}
			
			// 3.判断是否有操作权限
			boolean deptflag =false;
			/*for(String dept:depts){
				if(dept.equals((String)sapPoHead.get("WERKS"))){
					deptflag = true;
					break;
				}
			}
			if(deptflag == false){
				resultMap.put(EBELN, 3);
			}*/
		}
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> queryOutItem10(
			List<Map<String, Object>> list) {
		List<Map<String, Object>> outItemList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> item : list) {
			String reqQty = (String) item.get("qty");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("EBELN", (String) item.get("EBELN"));
			params.put("MATNR", (String) item.get("MATNR"));
			List<Map<String, Object>> sappo = sendCreateRequirementDao
					.selectSapPoHead(params);

			String werks = (String) item.get("werks");
			String whNumber = (String) item.get("whNumber");
			for (Map<String, Object> sapPoItem : sappo) {
				// 计算库存数量
				String matnr = (String) sapPoItem.get("MATNR");
				String poNo = (String) sapPoItem.get("EBELN");
				String poItemNo = (String) sapPoItem.get("EBELP");
				Map<String, Object> stockParams = new HashMap<String, Object>();
				stockParams.put("WERKS", werks);
				stockParams.put("MATNR", matnr);
				stockParams.put("WH_NUMBER", whNumber);
				Double totalStockQty = sendCreateRequirementDao
						.selectTotalStockQty(stockParams);
				sapPoItem.put("TOTAL_STOCK_QTY", totalStockQty);
				Double menGe = Double.parseDouble(sapPoItem.get("MENGE").toString());
				// 计算已领数量
				Double receivedQTy = sendCreateRequirementDao
						.selectHasReceivedQty("52",poNo, matnr,poItemNo);
				sapPoItem.put("RECEIVED_QTY", receivedQTy);
				// 计算可领数量
				sapPoItem.put("AVAILABLE_QTY", menGe-receivedQTy);
/*				Double availableQty = sendCreateRequirementDao
						.selectAvaliableQty("52",poNo, matnr);
				sapPoItem.put("AVAILABLE_QTY", availableQty);*/

				// 需求数量,填充从页面的到的值
				sapPoItem.put("REQ_QTY", reqQty);
			}
			outItemList.addAll(sappo);
		}
		return outItemList;
	}

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createReqOutItem10Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createReqOutItem10(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createReqOutItem10(maplist);
				}
			}
		} else {
			reqestNo = this.createReqOutItem10(list);
		}
		return reqestNo;
	}
	
	@Override
	public String createReqOutItem10(List<Map<String, Object>> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException();
		}
		//String staffNumber = (String) list.get(0).get("staffNumber");
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String reqNo = docNoService.getDocNo(werks, docType);
		String requireDate = (String) list.get(0).get("requireDate");
		String use = (String) list.get(0).get("use");
		String acceptPlant = (String) list.get(0).get("acceptPlant");
		String MEMO = (String) list.get(0).get("MEMO");//备注
		
		WmsOutRequirementHeadEntity outReqHeadEntity = new WmsOutRequirementHeadEntity();
		outReqHeadEntity.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		outReqHeadEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		outReqHeadEntity.setRequirementNo(reqNo);
		outReqHeadEntity.setRequirementType(OutModuleEnum.OUT_SALES_BUSINESS_NAME.getCode());
		outReqHeadEntity.setRequirementStatus("00");
		outReqHeadEntity.setWerks(werks);
		outReqHeadEntity.setWhNumber(whNumber);
		outReqHeadEntity.setRequiredDate(requireDate);
		outReqHeadEntity.setRequiredTime((String) list.get(0).get("requireTime"));
		outReqHeadEntity.setPurpose(use);
		outReqHeadEntity.setReceiveWerks(acceptPlant != null?acceptPlant.toUpperCase():null);
		outReqHeadEntity.setMemo(MEMO);
		outReqHeadEntity.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());
		//businessName:52-外部销售发货(251) businessType:00-无单据 businessClass:07-发货
		outReqHeadEntity.setCheckFlag(needApproval(OutModuleEnum.OUT_SALES_BUSINESS_NAME,OutModuleEnum.OUT_SALES_BUSINESS_TYPE,OutModuleEnum.BUSINESS_CLASS,werks)?"X":"");	
		
		List<WmsOutRequirementItemEntity> items = new ArrayList<WmsOutRequirementItemEntity>();
		int itemNo = 1;
		for (Map<String, Object> item : list) {
			WmsOutRequirementItemEntity outReqItemEntity = new WmsOutRequirementItemEntity();
			String MATNR = (String) item.get("MATNR");
			String MAKTX = (String) item.get("TXZ01");
			String MEINS = (String) item.get("MEINS");
			String UNIT = (String) item.get("LMEIN");
			String QTY = (String) item.get("REQ_QTY");
			String LGORT = (String) item.get("LGORT");
			String LIFNR = (String) item.get("LIFNR");
			String PO_NO = (String) item.get("EBELN");
			String PO_ITEM_NO = (String) item.get("EBELP");
			String EXPECT_BOX_QTY = (String) item.get("EXPECT_BOX_QTY");//预计箱数
			String costomerCode = (String) item.get("costomerCode");

			outReqItemEntity.setRequirementNo(reqNo);
			outReqItemEntity.setRequirementItemNo(String.valueOf(itemNo));
			outReqItemEntity.setBusinessName(OutModuleEnum.OUT_SALES_BUSINESS_NAME.getCode());//业务名称
			outReqItemEntity.setBusinessType(OutModuleEnum.OUT_SALES_BUSINESS_TYPE.getCode());//业务类型
			outReqItemEntity.setReqItemStatus("00");
			outReqItemEntity.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			outReqItemEntity.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			outReqItemEntity.setMatnr(MATNR.trim());
			outReqItemEntity.setMaktx(MAKTX);
			outReqItemEntity.setQty(Double.parseDouble(QTY));
			outReqItemEntity.setLgort(LGORT);
			outReqItemEntity.setLifnr(LIFNR);
			outReqItemEntity.setPoNo(PO_NO);
			outReqItemEntity.setPoItemNo(PO_ITEM_NO);
			outReqItemEntity.setUnit(MEINS);
			outReqItemEntity.setMeins(MEINS);
			outReqItemEntity.setBoxCount(Long.valueOf(EXPECT_BOX_QTY));
			outReqItemEntity.setCustomer(costomerCode);
			outReqItemEntity.setWhManager((String) item.get("WH_MANAGER"));
			items.add(outReqItemEntity);
			itemDao.insert(outReqItemEntity);
			itemNo++;
		}
		headDao.insert(outReqHeadEntity);
		return reqNo;
	}
	
	/**
	 * <p>是否需要审批 <br>
	 * 传入业务名称，业务类型，和工厂</p>
	 * @param businessName
	 * @param businessType
	 * @param businessClass
	 * @param werks
	 * @return
	 */
	private boolean needApproval(OutModuleEnum businessName,OutModuleEnum businessType,OutModuleEnum businessClass,String werks){
		//查询业务代码
		List<Map<String,Object>> businessCodeList = sendCreateRequirementDao.selectApprovalBusinessCode(businessName.getCode(), businessType.getCode(), businessClass.getCode());
		if(CollectionUtils.isEmpty(businessCodeList)){
			logger.warn(String.format("BUSINESS_NAME:%s 的业务配置数据不存在", businessName.getCode()));
			throw new IllegalArgumentException(String.format("BUSINESS_NAME:%s 的业务配置数据不存在", businessName.getCode()));
		}
		//检查业务是否需要审批
		Map<String,Object> params = new HashMap<String,Object>();
		String businessCode = (String) businessCodeList.get(0).get("BUSINESS_CODE");
		params.put("businessCode", businessCode);
		params.put("werks", werks);
		List<Map<String,Object>> plantsCfgs = sendCreateRequirementDao.selectApprovalFlag(params);
	    if(CollectionUtils.isEmpty(plantsCfgs)){
	    	logger.warn(String.format("%s工厂,%s业务类型的配置数据不存在", werks,businessCode));
	    	throw new IllegalArgumentException(String.format("%s工厂,%s业务类型的配置数据不存在", werks,businessCode));
	    }
	    String approvalFlag= (String) plantsCfgs.get(0).get("APPROVAL_FLAG");
	    return "X".equals(approvalFlag);
	}
	
	
	public Double selectTotalStockQty(Map<String,Object> params){
		return sendCreateRequirementDao.selectTotalStockQty(params);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOutReq10Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createOutReq10(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReq10(maplist);
				}
			}
		} else {
			reqestNo = this.createOutReq10(list);
		}
		return reqestNo;
	}
	
	@Override
	public String createOutReq10(List<Map<String, Object>> list) {
		//System.err.println(list.toString());
		logger.warn(list.toString());
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}
		
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		//String staffNumber = (String) list.get(0).get("staffNumber");
		//接收工厂
		//String acceptPlant = (String) list.get(0).get("acceptPlant");
		//接收库位
		//String aceptLgortList = (String) list.get(0).get("aceptLgortList");

		//werks whNumber acceptPlant aceptLgortList查wms_c_plant_to的WMS_MOVE_TYPE SAP_MOVE_TYPE


		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.WPLANT_MOVE_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setReceiveWerks(list.get(0).get("acceptPlant") == null?"":list.get(0).get("acceptPlant").toString().toUpperCase());
		head.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());
		
/*		if(needApproval(OutModuleEnum.PLANT_MOVE_BUSINESS_NAME, OutModuleEnum.PLANT_MOVE_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}*/
		
		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setLine((String) reqObj.get("LINE"));
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setQty(Double.parseDouble((String) reqObj.get("REQ_QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));
			if(!StringUtils.isBlank((String)reqObj.get("LIFNR"))){
				item.setLifnr(((String) reqObj.get("LIFNR")).trim());
			}
			if(!StringUtils.isBlank((String)reqObj.get("VENDOR"))){
				item.setLifnr(((String) reqObj.get("VENDOR")).trim());
			}
			if(!StringUtils.isBlank((String)reqObj.get("LGORT"))){
				item.setLgort(((String) reqObj.get("LGORT")).trim());
			}
			item.setBoxCount(Long.valueOf((String) reqObj.get("BOX_COUNT")));
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("MEINS"));
			//工厂间调拨 根据配置表
			//WMS_C_PLANT_TO
			//String businessName = "";
			//String businessType = "";


			item.setBusinessName(OutModuleEnum.WPLANT_MOVE_BUSINESS_NAME.getCode());
			item.setBusinessType(OutModuleEnum.PLANT_MOVE_BUSINESS_TYPE.getCode());
			String sendLgortList = (String)reqObj.get("sendLgortList");
			String aceptLgortList = (String)reqObj.get("aceptLgortList");


			item.setReceiveLgort(aceptLgortList);
			if(!StringUtils.isBlank(sendLgortList)){
				if (!sendLgortList.equals(""))
					item.setLgort(sendLgortList);
			}
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);
		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;		
	}

	//修改成通过工厂仓库去查是否核销
	@Override
	public Map<String, Object> checkMatrialsHx(List<Map<String, Object>> list) {
		Map<String, Object> errors = new HashMap<String,Object>();
		for(Map<String,Object> map:list){
			if(map.get("werks") == null ||  map.get("whNumber") == null ){
				errors.put("error", "必填参数为空");
				continue;
			}
			//String fwerks = (String) map.get("werks");
			//String matnr = (String) map.get("MATNR");
			String werks = (String) map.get("werks");
			String whNumber = (String) map.get("whNumber");
			List<Map<String,Object>> results = sendCreateRequirementDao.selectPlantHx(werks, whNumber);
			if(results==null || results.size()==0){
				errors.put("error", "工厂： "+werks+", 仓库： "+whNumber+" 下没有启动冲销业务!");
				return errors;
			}
			String HX_FLAG = (String) results.get(0).get("HX_FLAG");
			if(HX_FLAG!=null && HX_FLAG.equals("X")){
				errors.put("error", "工厂： "+werks+", 仓库： "+whNumber+" 下没有启动冲销业务!");
			}
			/*for(Map<String,Object> result:results){
				errors.put((String) result.get("MATNR"), String.format("%s料号存在%s单位的V类业务，请先处理V类业务", result.get("MATNR"),result.get("HX_QTY_XF")));
			}*/
		}
		return errors;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOutReq13Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createOutReq13(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReq13(maplist);
				}
			}
		} else {
			reqestNo = this.createOutReq13(list);
		}
		return reqestNo;
	}

	@Override
	public String createOutReq13(List<Map<String, Object>> list) {
		//System.err.println(list.toString());
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}
		
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		//String staffNumber = (String) list.get(0).get("staffNumber");
		
		
		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());
		
		if(needApproval(OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_NAME, OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}
		
		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
            item.setLine((String)reqObj.get("LINE"));
            item.setStation((String)reqObj.get("STATION"));
            //
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setQty(Double.parseDouble((String) reqObj.get("REQ_QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));
			if(!StringUtils.isBlank((String) reqObj.get("LIFNR"))){
				item.setLifnr(((String) reqObj.get("LIFNR")).trim());
			}
			//
			if(!StringUtils.isBlank((String) reqObj.get("VENDOR"))){
                item.setLifnr(((String) reqObj.get("VENDOR")).trim());
            }
			if(!StringUtils.isBlank((String) reqObj.get("LGORT"))){
				item.setLgort(((String) reqObj.get("LGORT")).trim());
			}
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("MEINS"));
			item.setBusinessName(OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_NAME.getCode());
			item.setBusinessType(OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_TYPE.getCode());
			String lgortList = (String) reqObj.get("lgortList");//库位
			String aceptLgortList = (String) reqObj.get("aceptLgortList");//接收库位
			item.setReceiveLgort(aceptLgortList);
			if (lgortList != null && !lgortList.equals(""))
				item.setLgort(lgortList);
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);
		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOutReq20Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createOutReq20(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReq20(maplist);
				}
			}
		} else {
			reqestNo = this.createOutReq20(list);
		}
		return reqestNo;
	}

	@Override
	public String createOutReq20(List<Map<String, Object>> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}

		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		//String staffNumber = (String) list.get(0).get("staffNumber");
		String receiveWerk = (String) list.get(0).get("acceptPlant");

		String deliveryArea = (String) list.get(0).get("shipArea");
		String receiptArea = (String) list.get(0).get("acceptArea");
		String shipmentModel = (String) list.get(0).get("transportType");
		String transportDays = (String) list.get(0).get("transportDay");

		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.STO_YBLD_MOVE_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setReceiveWerks(receiveWerk);
		head.setDeliveryArea(deliveryArea);
		head.setReceiptArea(receiptArea);
		head.setShipmentModel(shipmentModel);
		if(!StringUtils.isBlank(transportDays)){
			head.setTransportDays(Long.parseLong(transportDays));
		}
		head.setReceiveWerks(receiveWerk);
		head.setReceiveWerks(receiveWerk);
		head.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());

/*		if(needApproval(OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_NAME, OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}*/

		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setQty(Double.parseDouble((String) reqObj.get("REQ_QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));
			item.setLifnr((String) reqObj.get("LIFNR"));
			item.setLgort((String) reqObj.get("LGORT"));
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("MEINS"));

			item.setBusinessType(OutModuleEnum.STO_YBLD_BUSINESS_TYPR.getCode());
			item.setBusinessName(OutModuleEnum.STO_YBLD_MOVE_BUSINESS_NAME.getCode());

			String lgortList = (String) reqObj.get("lgortList");//库位
			String aceptLgortList = (String) reqObj.get("aceptLgortList");//接收库位
			item.setReceiveLgort(aceptLgortList);
			if (lgortList != null && !lgortList.equals("")) 
				item.setLgort(lgortList);
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);
		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;
	}



	@Override
	public Map<String, Integer> validateOutReq16(List<Map<String, Object>> list) {
		Map<String,Integer> map = new HashMap<String,Integer>();
		for(Map<String, Object> item:list){
			if(item.get("SAP_MATDOC_NO") == null  || item.get("werks") == null  || item.get("acceptWerks") == null){
				throw new IllegalArgumentException("发货工厂，接收工厂，凭证号不能为空");
			}
			String fwerks = (String) item.get("werks");//发货工厂
			String werks = (String)item.get("acceptWerks");//接收工厂
			String SAP_MATDOC_NO = (String)item.get("SAP_MATDOC_NO");//凭证号
			String MATNR = (String)item.get("MATNR");//物料号			
			
			List<Map<String,Object>> resultList = sendCreateRequirementDao.selectHxQtyXf(werks, fwerks,SAP_MATDOC_NO, MATNR,null);
			if(resultList.size() < 1){
				map.put(SAP_MATDOC_NO, 1);
			}
			
			boolean hasOverZero = false;
			for(Map<String,Object> xfqty:resultList){
				Double qty = ((BigDecimal)xfqty.get("HX_QTY_XF")).doubleValue();
				if(qty.doubleValue() > 0){
					hasOverZero = true;
					break;
				}
			}
			if(!hasOverZero){
				map.put(SAP_MATDOC_NO, 2);
			}
		}
		return map;
	}

	@Override
	public List<Map<String, Object>> queryOutReq16(List<Map<String, Object>> list) {
		List<Map<String, Object>> displayItems = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> item:list){
			if(item.get("SAP_MATDOC_NO") == null  || item.get("werks") == null  || item.get("acceptWerks") == null){
				throw new IllegalArgumentException("发货工厂，接收工厂，凭证号不能为空");
			}
			String fwerks = (String) item.get("werks");//发货工厂
			String werks = (String)item.get("acceptWerks");//接收工厂
			String SAP_MATDOC_NO = (String)item.get("SAP_MATDOC_NO");//凭证号
			String MATNR = (String)item.get("MATNR");//物料号	
			String REQ_QTY = (String)item.get("REQ_QTY");
			List<Map<String, Object>> items  =  sendCreateRequirementDao.selectHxQtyXf(werks, fwerks, SAP_MATDOC_NO, MATNR,"1");
			if(item.get("REQ_QTY") != null && !"".equals(REQ_QTY.trim())){
				//填充从页面传入的 需求数量
				for(Map<String, Object> m:items){
					m.put("REQ_QTY", REQ_QTY);
				}
			}
			displayItems.addAll(items);
		}
		return displayItems;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOutReq16Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createOutReq16(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReq16(maplist);
				}
			}
		} else {
			reqestNo = this.createOutReq16(list);
		}
		return reqestNo;
	}

	@Override
	public String createOutReq16(List<Map<String, Object>> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}
		//System.err.println("createOutReq16：：： "+list.toString());
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		String receiveWerks =  (String) list.get(0).get("acceptPlant");//接收工厂
		//String staffNumber = (String) list.get(0).get("staffNumber");
		
		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.A303_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setReceiveWerks(receiveWerks);
		head.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());
		
		if(needApproval(OutModuleEnum.A303_BUSINESS_NAME, OutModuleEnum.A303_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}
		
		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setQty(Double.parseDouble((String) reqObj.get("REQ_QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));
			item.setLifnr((String) reqObj.get("LIFNR"));
			item.setLgort((String) reqObj.get("LGORT"));
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("MEINS"));
			item.setBusinessName(OutModuleEnum.A303_BUSINESS_NAME.getCode());
			item.setBusinessType(OutModuleEnum.A303_BUSINESS_TYPE.getCode());	
			if (reqObj.get("lgortList") != null && !reqObj.get("lgortList").equals("")) 
				item.setLgort((String)reqObj.get("lgortList"));
			//凭证号
			item.setSapMatdocNo((String) reqObj.get("SAP_MATDOC_NO"));
			//行项目号
			item.setSapMatdocItemNo((String) reqObj.get("SAP_MATDOC_ITEM_NO"));
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);
		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;		
	}

	@Override
	public Map<String, Integer> validate17(List<Map<String, Object>> list) {
		Map<String,Integer> errMap = new HashMap<String,Integer>();
		for(Map<String,Object> map:list){
			String werks = (String) map.get("werks");
			String vbeln = (String) map.get("deliveryNO");
			List<Map<String,Object>>  records = sendCreateRequirementDao.selectWmsHxDn(werks,vbeln,null);
			if(CollectionUtils.isEmpty(records)){
				//SAP交货单不存在
				errMap.put(vbeln, 1);
			}else{
				//是否存在核销数量大于0的记录
				boolean hasRecordHx = false;
				for(Map<String,Object> record:records){
					if(record.get("HX_QTY_XF") != null && ((BigDecimal)record.get("HX_QTY_XF")).doubleValue() > 0){
						hasRecordHx = true;
						break;
					}
				}
				if(hasRecordHx == false){
					errMap.put(vbeln, 2);
				}
			}
		}
		
		return errMap;
	}

	@Override
	public List<Map<String, Object>> query17(List<Map<String, Object>> list) {
		List<Map<String, Object>> respList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> queryObj:list)
		{
			String werks = (String) queryObj.get("werks");
			String vbeln = (String) queryObj.get("deliveryNO");
			List<Map<String,Object>> records =  sendCreateRequirementDao.selectWmsHxDn(werks, vbeln,"true");
			respList.addAll(records);
		}
		return respList;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String create17Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.create17(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.create17(maplist);
				}
			}
		} else {
			reqestNo = this.create17(list);
		}
		return reqestNo;
	}

	@Override
	public String create17(List<Map<String, Object>> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}
		
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		String receiveWerks =  (String) list.get(0).get("acceptPlant");//接收工厂
		//String staffNumber = (String) list.get(0).get("staffNumber");
		//TODO:..
		//String costomerCode = (String) list.get(0).get("costomerCode");
		String transportDay = (String) list.get(0).get("transportDay");
		String transportType = (String) list.get(0).get("transportType");
		String shipArea = (String) list.get(0).get("shipArea");
		String acceptArea = (String) list.get(0).get("acceptArea");
		
		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.SAP_A311T_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setReceiveWerks(receiveWerks);
		head.setDeliveryArea(shipArea);
		head.setReceiptArea(acceptArea);
		head.setShipmentModel(transportType);
		head.setTransportDays(Long.valueOf(transportDay));
		
		
		if(needApproval(OutModuleEnum.SAP_A311T_BUSINESS_NAME, OutModuleEnum.SAP_A311T_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}
		
		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setQty(Double.parseDouble((String) reqObj.get("REQ_QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));
			item.setLifnr((String) reqObj.get("LIFNR"));
			item.setLgort((String) reqObj.get("LGORT"));
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("UNIT"));
			item.setBusinessName(OutModuleEnum.SAP_A311T_BUSINESS_NAME.getCode());
			item.setBusinessType(OutModuleEnum.SAP_A311T_BUSINESS_TYPE.getCode());	
			if (reqObj.get("lgortList") != null && !reqObj.get("lgortList").equals("")) 
				item.setLgort((String)reqObj.get("lgortList"));
			
			//SAP交货单号
			item.setSapOutNo((String) reqObj.get("VBELN"));
			//行项目号
			item.setSapOutItemNo((String) reqObj.get("POSNR"));
			//采购订单
			item.setPoNo((String) reqObj.get("EBELN"));
			//预计箱数
			item.setBoxCount(Long.parseLong((String)reqObj.get("BOX_COUNT")));
			//基本单位
			item.setMeins((String) reqObj.get("MEINS"));
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);
		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		itemList.stream().forEach(e -> itemDao.insert(e));
		// 返回出库需求号
		return reqNo;		
	}
	
	

	@Override
	public List<Map<String, Object>> queryCoreLabel(Map<String, Object> params) {
		
		List<Map<String,Object>> list =  sendCreateRequirementDao.selectCoreLabel(params);		
		return list;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOutReq18Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createOutReq18(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReq18(maplist);
				}
			}
		} else {
			reqestNo = this.createOutReq18(list);
		}
		return reqestNo;
	}

	@Override
	public String createOutReq18(List<Map<String, Object>> list) {

		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}

		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		//String staffNumber = (String) list.get(0).get("staffNumber");

		String bfyy = (String) list.get(0).get("bfyy");

		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.BF_MOVE_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setBfyy(bfyy);
		/*if(needApproval(OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_NAME, OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}*/
		head.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());

		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			//item.setBusinessName("44");
			//item.setBusinessType("14");
			item.setReqItemStatus("00");
			item.setCostCenter((String)reqObj.get("costcenterCode"));
			item.setQty(Double.parseDouble((String) reqObj.get("REQ_QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));
			item.setLifnr((String) reqObj.get("LIFNR"));
			item.setLgort((String) reqObj.get("LGORT"));
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("MEINS"));
			item.setDel("0");
			item.setBusinessName(OutModuleEnum.BF_MOVE_BUSINESS_NAME.getCode());
			item.setBusinessType(OutModuleEnum.BF_MOVE_BUSINESS_TYPR.getCode());
			String lgortList = (String) reqObj.get("lgortList");//库位
			String aceptLgortList = (String) reqObj.get("aceptLgortList");//接收库位
			item.setReceiveLgort(aceptLgortList);
			if (lgortList != null && !lgortList.equals("")) 
				item.setLgort(lgortList);
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);
			reqItemNo++;

		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;

	}

	/**
	 * update更新需求
	 * @param params
	 */
	@Override
	public void updateRequirement(List<Map<String,Object>> params) {
		Map<String,Object> user = userUtils.getUser();
		List<Map<String, Object>> itemlist = new ArrayList<Map<String, Object>>();
		String requirementNo = "";
		
		List<Map<String,Object>> newparams = Combine(params); 
		for (Map<String, Object> param : newparams) {
			Map<String, Object> item = new HashMap<String, Object>();
			requirementNo = param.get("REFERENCE_DELIVERY_NO").toString();
			
			BigDecimal confirmQty = new BigDecimal(param.get("RECOMMEND_QTY").toString()); //确认数量
			item.put("REQUIREMENT_NO", requirementNo);
			item.put("REQUIREMENT_ITEM_NO", param.get("REFERENCE_DELIVERY_ITEM"));
			item.put("QTY_XJ", confirmQty);
			item.put("EDITOR", user.get("USERNAME").toString());
			item.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
//			item.setId(Long.valueOf(param.get("ID").toString()));
			itemlist.add(item);
		}
		
		//更新需求行, 先排序防死锁
		if (itemlist.size() > 0) {
			ListUtils.sort(itemlist, "REFERENCE_DELIVERY_ITEM", true); 
			itemDao.updateOutboundItemQtyANDStatus(itemlist);
		}
		
		//更新需求抬头
		WmsOutRequirementHeadEntity headEntity= new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> ilist = itemDao.selectList(new EntityWrapper<WmsOutRequirementItemEntity>()
                .eq(StringUtils.isNotBlank(requirementNo),"REQUIREMENT_NO", requirementNo)
                );

		boolean createflag = false; //已创建
		boolean blflag = false; //备料中
		boolean partxjflag = false; //部分下架
		boolean xjflag = false; //已下架
		boolean partgzflag = false; //部分交接
		boolean gzflag = false; //已交接
		for (WmsOutRequirementItemEntity item : ilist) {
			if(item.getReqItemStatus().equals("00")) {
				createflag = true;
			}
			if(item.getReqItemStatus().equals("01")) {
				blflag = true;
			}
			if(item.getReqItemStatus().equals("02")) {
				partxjflag = true;
			}
			if(item.getReqItemStatus().equals("03")) {
				xjflag = true;
			}
			if(item.getReqItemStatus().equals("04")) {
				partgzflag = true;
			}
			if(item.getReqItemStatus().equals("05")) {
				gzflag = true;
			}
		}
		//根据行状态，计算头状态
		if (partgzflag || gzflag) {
			headEntity.setRequirementStatus("05"); //部分交接
		} else if (createflag || blflag || partxjflag) {
			headEntity.setRequirementStatus("03"); //部分下架
		} else if (xjflag) {
			headEntity.setRequirementStatus("04"); //已下架
		}
		headEntity.setEditor(user.get("USERNAME").toString());
		headEntity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		headDao.update(headEntity, new EntityWrapper<WmsOutRequirementHeadEntity>()
				.eq("REQUIREMENT_NO", requirementNo));
		
	}
	
	public static List<Map<String,Object>> Combine(List<Map<String,Object>> lists) {
        List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
        
        for (Map<String,Object> list : lists) {  
            String requirementNo = list.get("REFERENCE_DELIVERY_NO").toString();
            String requirementItem = list.get("REFERENCE_DELIVERY_ITEM").toString();
            
            int flag = 0;// 0为新增数据，1为增加 
            
            for (Map<String,Object> clist : countList) {  
            	String reqNo = clist.get("REFERENCE_DELIVERY_NO").toString();
                String reqItem = clist.get("REFERENCE_DELIVERY_ITEM").toString();
                
                if (requirementNo.equals(reqNo) && requirementItem.equals(reqItem)) {  
                	BigDecimal lqty = new BigDecimal(list.get("RECOMMEND_QTY").toString());
                	BigDecimal cqty = new BigDecimal(clist.get("RECOMMEND_QTY").toString());
                    BigDecimal sum = lqty.add(cqty);
                    clist.put("RECOMMEND_QTY", sum);  
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
	@Transactional(rollbackFor = Exception.class)
	public String createOutReqPda311Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createOutReqPda311(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReqPda311(maplist);
				}
			}
		} else {
			reqestNo = this.createOutReqPda311(list);
		}
		return reqestNo;
	}

	@Override
	public String createOutReqPda311(List<Map<String, Object>> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}

		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		//String staffNumber = (String) list.get(0).get("staffNumber");
		
		String receiptArea = (String) list.get(0).get("DIS_ADDRSS");
		String station = (String) list.get(0).get("DIS_STATION");
		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);
		String supermarket = (String) list.get(0).get("supermarket");
		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.PDA311_BUSINESS_NAME.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setSupermarket(supermarket);
		head.setReceiptArea(receiptArea);
		head.setReceiver(list.get(0).get("receiver")==null?null:list.get(0).get("receiver").toString());
		if(needApproval(OutModuleEnum.PDA311_BUSINESS_NAME, OutModuleEnum.STORE_PLACE_MOVE_BUSINESS_TYPE, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}

		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setReqItemStatus("00");
			item.setCostCenter((String)reqObj.get("costcenterCode"));
			item.setQty(Double.parseDouble(reqObj.get("REQ_QTY").toString()));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));		
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("UNIT"));
			item.setStation((String) reqObj.get("DIS_STATION"));
			item.setReceiveLgort(reqObj.get("aceptLgortList")==null?"":reqObj.get("aceptLgortList").toString());
			item.setDel("0");
			item.setBusinessName(OutModuleEnum.PDA311_BUSINESS_NAME.getCode());
			item.setBusinessType(OutModuleEnum.PDA311_BUSINESS_TYPR.getCode());
			item.setLgort(reqObj.get("lgortList")==null?(String) reqObj.get("LGORT"):reqObj.get("lgortList").toString());
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);

		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;

	}
	
	/**
	 * 创建总装需求-验证配送单
	 */
	@Override
	public Map<String, Integer> validateOutReq21(List<Map<String, Object>> list) {
		Map<String,Integer> map = new HashMap<String,Integer>();
		for(Map<String, Object> item:list){
			String distribution = (String)item.get("distribution");//配送单号
			String deliveryType = (String) item.get("deliveryType");//配送模式		
			
			if(item.get("distribution") == null || item.get("distribution").equals("") ){
				map.put(distribution, 3);
				break;
			}
			
			
			List<Map<String,Object>> resultList = sendCreateRequirementDao.selectCallMaterial(distribution,deliveryType);
			if(resultList.size() < 1){
				map.put(distribution, 1);
				break;
			}
			
			boolean hasOverZero = false;
			for(Map<String,Object> req :resultList){
				if(req.get("STATUS").equals("00")){
					hasOverZero = true;
					break;
				}
			}
			if(!hasOverZero){
				map.put(distribution, 2);
			}
		}
		return map;
	}
	
	/**
	 * 创建总装需求-查询配送单
	 */
	@Override
	public List<Map<String, Object>> queryOutReq21(List<Map<String, Object>> list) {
		List<Map<String, Object>> displayItems = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> item:list){
			if(item.get("distribution") == null){
				throw new IllegalArgumentException("配送单号不能为空");
			}
			String distribution = (String)item.get("distribution");//配送单号
			String deliveryType = (String) item.get("deliveryType");//配送模式		
			
			List<Map<String,Object>> resultList = sendCreateRequirementDao.selectCallMaterial(distribution,deliveryType);

			displayItems.addAll(resultList);
		}
		return displayItems;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOutReq21Split(List<Map<String, Object>> list) {
		String reqestNo = "";
		//是否校验人料关系
		List<List<Map<String, Object>>> newList = getMatManagerSplit(list);
		if (newList.size() > 0 ) {
			for (List<Map<String, Object>> maplist : newList) {
				if (reqestNo.equals("")) {
					reqestNo = this.createOutReq21(maplist);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReq21(maplist);
				}
			}
		} else {
			reqestNo = this.createOutReq21(list);
		}
		return reqestNo;
	}
	
	/**
	 * 创建 -  工厂间调拨301（总装）
	 */
	@Override
	public String createOutReq21(List<Map<String, Object>> list) {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("入参为空");
		}
		Map<String,Object> user = userUtils.getUser();
		String werks = (String) list.get(0).get("werks");
		String whNumber = (String) list.get(0).get("whNumber");
		String docType = WmsDocTypeEnum.OUT_WAREHOURSE.getCode();
		String memo = (String) list.get(0).get("MEMO");
		String use = (String) list.get(0).get("use");
		String reqDate = (String) list.get(0).get("requireDate");
		//String staffNumber = user.get("USERNAME") == null?"":user.get("USERNAME").toString();
		String priority = (String) list.get(0).get("priority");
		
		//SysUserEntity user = ShiroUtils.getUserEntity();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		String reqNo = docNoService.getDocNo(werks, docType);

		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setMemo(memo);
		head.setPurpose(use);
		head.setRequiredDate(reqDate);
		head.setRequiredTime((String) list.get(0).get("requireTime"));
		head.setRequirementStatus(RequirementStatusEnum.CREATED.getCode());
		head.setRequirementType(OutModuleEnum.PLANT_MOVE_BUSINESS_NAME_301.getCode());
		head.setWerks(werks);
		head.setWhNumber(whNumber);
		head.setPriority(priority);
		head.setReceiveWerks(list.get(0).get("acceptPlant") == null?"":list.get(0).get("acceptPlant").toString().toUpperCase());
		
		
		if(needApproval(OutModuleEnum.PLANT_MOVE_BUSINESS_NAME_301, OutModuleEnum.PLANT_MOVE_BUSINESS_TYPE_301, OutModuleEnum.BUSINESS_CLASS, werks)){
			//审批标识
			head.setCheckFlag("X");
		}
		
		int reqItemNo = 1;
		for (Map<String, Object> reqObj : list) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
			item.setQty(Double.parseDouble((String) reqObj.get("QTY")));
			item.setMatnr(((String) reqObj.get("MATNR")).trim());
			item.setMaktx((String) reqObj.get("MAKTX"));
			item.setLifnr((String) reqObj.get("LIFNR"));
			item.setLgort((String) reqObj.get("LGORT"));
			item.setReqItemStatus(RequirementStatusEnum.CREATED.getCode());
			item.setRequirementItemNo(String.valueOf(reqItemNo));reqItemNo++;
			item.setRequirementNo(reqNo);
			item.setUnit((String) reqObj.get("MEINS"));
			item.setBusinessName(OutModuleEnum.PLANT_MOVE_BUSINESS_NAME_301.getCode());
			item.setBusinessType(OutModuleEnum.PLANT_MOVE_BUSINESS_TYPE_301.getCode());
			String aceptLgortList = (String) reqObj.get("aceptLgortList");//接收库位
			item.setReceiveLgort(aceptLgortList);
			item.setStation(reqObj.get("POINT_OF_USE") ==null?"":reqObj.get("POINT_OF_USE").toString());
			String sendLgortList = (String) reqObj.get("sendLgortList");//库位
			if (!sendLgortList.equals("")) {
				item.setLgort(sendLgortList);
			}
			item.setDispatchingNo((String) reqObj.get("DELIVERY_NO"));
			item.setDispatchingItemNo((String) reqObj.get("DLV_ITEM"));
			item.setWhManager((String) reqObj.get("WH_MANAGER"));
			itemList.add(item);
		}
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		// 返回出库需求号
		return reqNo;		
	}

	@Override
	public String creteScddByUpload(List<CreateProduceOrderAO> orderItems) throws Exception {
		System.err.println("================================ 	"+orderItems.toString());
		if (CollectionUtils.isEmpty(orderItems)) {
			throw new IllegalArgumentException("参数为空");
		}
		//SysUserEntity user = ShiroUtils.getUserEntity();
		String requirementNo = docNoService.getDocNo(orderItems.get(0)
				.getWerks(), WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		// 创建出库需求头
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		head.setCreator(userUtils.getUser().get("USERNAME").toString()+":"+userUtils.getUser().get("FULL_NAME").toString());
		head.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		head.setRequirementNo(requirementNo);
		head.setWerks(orderItems.get(0)
				.getWerks());
		head.setWhNumber(orderItems.get(0)
				.getWhNumber());
		head.setPurpose(orderItems.get(0).getUse());
		//领料 41
		if(orderItems.get(0).getRequireTypes().equals("41")){
			head.setRequirementType(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());
		}
		//备料 42
		if(orderItems.get(0).getRequireTypes().equals("42")){
			head.setRequirementType(OutModuleEnum.OUT_PRO_BL_BUSINESS_NAME.getCode());
		}
		head.setRequirementStatus("00");
		head.setRequiredModel(orderItems.get(0).getSummaryMode());//备料模式
		//head.setRequiredDate(orderItems.get(0).getRequireDate());
		head.setDel("0");
		head.setRequiredDate(DateUtils.format(new Date(),"yyyy-MM-dd"));
		/**
		 * <option value="00">上午8:00-12:00</option>
		 * 										<option value="01">下午13:00-17:30</option>
		 * 										<option value="02">加班18:00-20:00</option>
		 * 										<option value="03">夜班20:00-8:00</option>
		 */
		//head.setRequiredTime(DateUtils.format(new Date(),"HH:mm:ss"));

		// 创建出库需求项
		int requirementItemNo = 1;
		System.err.println("orderItems===>>> "+orderItems.toString());
		for (CreateProduceOrderAO order : orderItems) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			setCommomPrams(item, order, requirementNo, requirementItemNo);
			System.err.println("requirementItemNo===>>> "+requirementItemNo);
			//#Bug1449# 取预留号和预留行项目号信息
			Map<String,String> params = new HashMap<>();
			params.put("aufnr",order.getAUFNR());
			params.put("posnr",StringUtils.leftPad(order.getPOSNR(),4,'0'));
			params.put("matnr",order.getMATNR());
			Map<String,String> resVal = sendCreateRequirementDao.selectPOPro(params);
			if(resVal != null && !resVal.isEmpty()){
				item.setRsnum(resVal.get("RSNUM"));
				item.setRspos(resVal.get("RSPOS"));
			}
			if (order.getHX_QTY() != null && order.getHX_QTY() > 0) {
				// 剩余核销数量 > 0
				if (order.getREQ_QTY().doubleValue() < order.getHX_QTY()
						.doubleValue()) {
					// 需求数量小于剩余核销数量
					item.setBusinessType(OutModuleEnum.OUT_PRO_HX_BUSINESS_TYPE.getCode());// 生产订单(A)
					item.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());// 生产订单领料(261)
				} else {
					/*
					 * 组件需求拆成两个行项目, 生产订单(A)，数量=核销数量，生产订单，数量=需求数量-核销数量
					 */
					WmsOutRequirementItemEntity itemA = new WmsOutRequirementItemEntity();
					WmsOutRequirementItemEntity itemB = new WmsOutRequirementItemEntity();

					setCommomPrams(itemA, order, requirementNo,
							requirementItemNo);
					setCommomPrams(itemB, order, requirementNo,
							requirementItemNo);

					itemA.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());
					itemA.setBusinessType(OutModuleEnum.OUT_PRO_HX_BUSINESS_TYPE.getCode());
					// 是否需要审批
					System.err.println("================================================");
					System.err.println(orderItems.get(0)
							.getWerks());
					System.err.println(itemA.getBusinessType());
					System.err.println(itemA
							.getBusinessName());
					if (commonService.checkApproval(orderItems.get(0)
							.getWerks(), itemA.getBusinessType(), itemA
							.getBusinessName())) {
						head.setCheckFlag("X");
					}
					itemA.setQty(order.getHX_QTY());

					itemB.setBusinessType(OutModuleEnum.OUT_PRO_BUSINESS_TYPE.getCode());
					itemB.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());
					// 是否需要审批
					if (commonService.checkApproval(orderItems.get(0)
							.getWerks(), itemB.getBusinessType(), itemB
							.getBusinessName())) {
						head.setCheckFlag("X");
					}
					itemB.setQty(order.getREQ_QTY() - order.getHX_QTY());

					setHxFlag(itemA);
					setHxFlag(itemB);
					itemDao.insert(itemA);
					itemDao.insert(itemB);

					continue;
				}
			}
			//领料 41
			if(orderItems.get(0).getRequireTypes().equals("41")){
				item.setBusinessType(OutModuleEnum.OUT_PRO_BUSINESS_TYPE.getCode());// 生产订单
				item.setBusinessName(OutModuleEnum.OUT_PRO_BUSINESS_NAME.getCode());// 生产订单领料(261)
			}
			//备料 42
			if(orderItems.get(0).getRequireTypes().equals("42")){
				item.setBusinessType(OutModuleEnum.OUT_PRO_BUSINESS_TYPE.getCode());// 生产订单
				item.setBusinessName(OutModuleEnum.OUT_PRO_BL_BUSINESS_NAME.getCode());// 生产订单领料(261)
			}

			// 是否需要审批
			if (commonService.checkApproval(orderItems.get(0).getWerks(),
					item.getBusinessType(), item.getBusinessName())) {
				head.setCheckFlag("X");
			}

			// 设置 hx_flag
			System.err.println("item===>>> "+item.toString());
			setHxFlag(item);
			itemDao.insert(item);
			requirementItemNo++;
		}
		headDao.insert(head);
		return requirementNo;
	}
	
	private static List<List<CreateProduceOrderAO>> getListByGroup(List<CreateProduceOrderAO> list) {
        List<List<CreateProduceOrderAO>> result = new ArrayList<List<CreateProduceOrderAO>>();
        Map<String, List<CreateProduceOrderAO>> map = new TreeMap<String, List<CreateProduceOrderAO>>();
 
        for (CreateProduceOrderAO bean : list) {
            if (map.containsKey(bean.getWhManager())) {
                List<CreateProduceOrderAO> t = map.get(bean.getWhManager());
                t.add(bean);
                map.put(bean.getWhManager(), t);
            } else {
                List<CreateProduceOrderAO> t = new ArrayList<CreateProduceOrderAO>();
                t.add(bean);
                map.put(bean.getWhManager(), t);
            }
        }
        for (Entry<String, List<CreateProduceOrderAO>> entry : map.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }
	
	private static List<List<CostCenterAO>> getListByGroupCostCenter(List<CostCenterAO> list) {
        List<List<CostCenterAO>> result = new ArrayList<List<CostCenterAO>>();
        Map<String, List<CostCenterAO>> map = new TreeMap<String, List<CostCenterAO>>();
 
        for (CostCenterAO bean : list) {
            if (map.containsKey(bean.getWhManager())) {
                List<CostCenterAO> t = map.get(bean.getWhManager());
                t.add(bean);
                map.put(bean.getWhManager(), t);
            } else {
                List<CostCenterAO> t = new ArrayList<CostCenterAO>();
                t.add(bean);
                map.put(bean.getWhManager(), t);
            }
        }
        for (Entry<String, List<CostCenterAO>> entry : map.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }
	
	private static List<List<InternalOrderAO>> getListByGroupInternalOrder(List<InternalOrderAO> list) {
        List<List<InternalOrderAO>> result = new ArrayList<List<InternalOrderAO>>();
        Map<String, List<InternalOrderAO>> map = new TreeMap<String, List<InternalOrderAO>>();
 
        for (InternalOrderAO bean : list) {
            if (map.containsKey(bean.getWhManager())) {
                List<InternalOrderAO> t = map.get(bean.getWhManager());
                t.add(bean);
                map.put(bean.getWhManager(), t);
            } else {
                List<InternalOrderAO> t = new ArrayList<InternalOrderAO>();
                t.add(bean);
                map.put(bean.getWhManager(), t);
            }
        }
        for (Entry<String, List<InternalOrderAO>> entry : map.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }
	
	private static List<List<WbsElementAO>> getListByGroupWbs(List<WbsElementAO> list) {
        List<List<WbsElementAO>> result = new ArrayList<List<WbsElementAO>>();
        Map<String, List<WbsElementAO>> map = new TreeMap<String, List<WbsElementAO>>();
 
        for (WbsElementAO bean : list) {
            if (map.containsKey(bean.getWhManager())) {
                List<WbsElementAO> t = map.get(bean.getWhManager());
                t.add(bean);
                map.put(bean.getWhManager(), t);
            } else {
                List<WbsElementAO> t = new ArrayList<WbsElementAO>();
                t.add(bean);
                map.put(bean.getWhManager(), t);
            }
        }
        for (Entry<String, List<WbsElementAO>> entry : map.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }
	
	private List<List<Map<String, Object>>> getMatManagerSplit(List<Map<String, Object>> list) {
		List<List<Map<String, Object>>> newList = new ArrayList<List<Map<String, Object>>>();
		String errmsg = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("werks", list.get(0).get("werks"));
		params.put("whNumber", list.get(0).get("whNumber"));
		Map<String, Object> cwh = wmsCWhServiceImpl.getWmsCWh(params);
		if (null != cwh && cwh.get("MAT_MANAGER_FLAG").equals("X")) {
			List<Map<String, Object>> paramslist = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> order : list) {
				Map<String, Object> orderparams = new HashMap<String, Object>();
				orderparams.put("WERKS", order.get("werks"));
				orderparams.put("WH_NUMBER", order.get("whNumber"));
				orderparams.put("MATNR", order.get("MATNR").toString().trim());
				paramslist.add(orderparams);
			}
			List<Map<String, Object>> matmanagerlist = comService.getMatManagerList(paramslist);
			for (Map<String, Object> order : list) {
				boolean flag = false;
				String werks = (String) order.get("werks");
				String whnumber = (String) order.get("whNumber");
				String matnr = ((String) order.get("MATNR")).trim();
				for (Map<String,Object> matmanager : matmanagerlist) {
					String werks1 = matmanager.get("WERKS").toString();
					String whnumber1 = matmanager.get("WH_NUMBER").toString();
					String matnr1 = matmanager.get("MATNR").toString();
					if (werks.equals(werks1) && whnumber.equals(whnumber1) && matnr.equals(matnr1)) {
						order.put("WH_MANAGER", matmanager.get("MANAGER_STAFF").toString());
						flag = true;
						break;
					}
				}
				
				if (!flag) {
					errmsg = errmsg + matnr +"：未维护人料关系!" + "<br/>";
				}
			}
			
			if (!errmsg.equals("")) {
				throw new RuntimeException(errmsg);
			}
			
			newList = ListUtils.getListByGroup(list,"WH_MANAGER");
		}
		
		return newList;
	}
}
