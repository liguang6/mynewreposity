package com.byd.wms.business.modules.out.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.byd.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.impl.CommonServiceImpl;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.impl.WmsCWhServiceImpl;
import com.byd.wms.business.modules.out.dao.SendCreateRequirementDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.CreateProduceOrderAO;
import com.byd.wms.business.modules.out.entity.ProduceOrderVO;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.service.ProduceLineWarehouseService;

@Service
public class ProduceLineWarehouseServiceImpl implements ProduceLineWarehouseService{

	@Autowired
	SendCreateRequirementDao  sendCreateRequirementDao;
	
	
	@Autowired
	WmsCPlantService wmsCPlantService;
	
	@Autowired
	WmsCDocNoService docNoService;

	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private WmsCWhServiceImpl wmsCWhServiceImpl;
	
	@Autowired
	private CommonServiceImpl comService;
	
	@Override
	public Map<String, Integer> validProduceOrders(
			List<ProduceOrderVO> orders,List<String> depts) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (ProduceOrderVO order : orders) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("aufnr", order.getOrderNo());
			List<Map<String, Object>> existOrder = sendCreateRequirementDao.selectWMSSapMoHead(params);
			// 1.检验订单是否同步到了WMS数据库
			if (CollectionUtils.isEmpty(existOrder)) {
				resultMap.put(order.getOrderNo(), 1);
				continue;
			}
			// 3.判断是否有订单所属工厂的操作权限
			//List<SysDeptEntity> premissionPlants = tagUtils.deptListWithUser("3", null);
			boolean premissionFlag = false;
/*			for (String plant : depts) {
				if (((String) existOrder.get(0).get("WERKS")).equals(plant)) {
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
					|| (istatText.indexOf("DLFL") != -1 
					||  istatText.indexOf("LKD") != -1 
					|| istatText.indexOf("TECO") != -1)
					|| istatText.indexOf("CLSD") != -1) {
					// 不可发料的状态 (可发料状态-->:包含REL状态，且不包含‘TECO’‘DLFL‘‘LKD’、‘CLSD’字符)
					resultMap.put(order.getOrderNo(), 4);
					continue;
				}
			} else {
				// 启用核销
				// 5.判断【生产组件核销表】是否存在剩余核销数量大于0的组件
				Map<String, Object> hxMoParams = new HashMap<String, Object>();
				hxMoParams.put("aufnr", order.getOrderNo());
				List<Map<String, Object>> hxMoComponentList = sendCreateRequirementDao
						.selectHxMoComponents(hxMoParams);
				boolean hasHxQtyGtZeroFlag = false;
				for (Map<String, Object> hxMoComponent : hxMoComponentList) {
					if (((BigDecimal)hxMoComponent.get("HX_QTY")).doubleValue() > 0) {
						hasHxQtyGtZeroFlag = true;
						break;
					}
				}
				String istatText = (String) existOrder.get(0).get("ISTAT_TXT");

				if (hasHxQtyGtZeroFlag == true) {
					// 存在：判断MO状态是否为可发料状态（抬头状态字段包含‘REL’或者包含‘TECO’字符串）
					if (istatText.indexOf("REL") == -1 && istatText.indexOf("TECO") == -1) {
						resultMap.put(order.getOrderNo(), 5);
					}
				} else {
					// 可发料状态：订单状态（ISTAT_TXT）”，包含REL状态且且不包含‘TECO’‘DLFL‘‘LKD’、‘CLSD’字符串
					       if(istatText.indexOf("REL") == -1
							|| (istatText.indexOf("DLFL") != -1 
							||  istatText.indexOf("LKD") != -1 
							|| istatText.indexOf("TECO") != -1
							|| istatText.indexOf("CLSD") != -1)) {
						//不可发料状态
						resultMap.put(order.getOrderNo(), 4);
					}
				}
			}

		}
		return resultMap;
	}

	@Autowired
	OutCommonService commonService;
	
	@Autowired
	WmsOutRequirementItemDao itemDao;
	
	@Autowired
	WmsOutRequirementHeadDao headDao;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String createProduceOrderOutReqSplit(List<CreateProduceOrderAO> orderItems,String userName) throws Exception {
		String reqestNo = "";
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
				if (reqestNo.equals("")) {
					reqestNo = this.createProduceOrderOutReq(orderlist,userName);
				} else {
					reqestNo = reqestNo+ "," + this.createProduceOrderOutReq(orderlist,userName);
				}
			}
		} else {
			reqestNo = this.createProduceOrderOutReq(orderItems,userName);
		}
		return reqestNo;
	}
	
	@Override
	public String createProduceOrderOutReq(List<CreateProduceOrderAO> orderItems,String userName) throws Exception {
		if (CollectionUtils.isEmpty(orderItems)) {
			throw new IllegalArgumentException("参数为空");
		}
		// 校验必填字段
		/*for (CreateProduceOrderAO order : orderItems) {
			if (StringUtils.isBlank(order.getAUFNR())) {
				throw new IllegalArgumentException("必填字段不能为空");
			}
		}*/
		//SysUserEntity user = ShiroUtils.getUserEntity();
		String requirementNo = docNoService.getDocNo(orderItems.get(0).getWerks(), WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		// 创建出库需求头
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		head.setCreator(userUtils.getUser().get("USERNAME").toString());
		head.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		head.setRequirementNo(requirementNo);
		head.setWerks(orderItems.get(0).getWerks());
		head.setWhNumber(orderItems.get(0).getWhNumber());
		head.setPurpose(orderItems.get(0).getUse());
		head.setRequirementType("48");
		head.setRequirementStatus("00");
		head.setRequiredTime(orderItems.get(0).getRequireTime());
		head.setRequiredDate(orderItems.get(0).getRequireDate());
		head.setRequiredModel(orderItems.get(0).getSummaryMode());
		//备料模式
		// 创建出库需求项
		int requirementItemNo  = 1;
		for (CreateProduceOrderAO order : orderItems) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();		
			setCommomPrams(item,order,requirementNo,requirementItemNo);	
			if(order.getHX_QTY() != null && order.getHX_QTY() > 0){
				//剩余核销数量 > 0
				if(order.getREQ_QTY().doubleValue() < order.getHX_QTY().doubleValue()){
					//需求数量小于剩余核销数量
					item.setBusinessType("00");//无收据
					item.setBusinessName("48");//库存地点调拨(类型转移311)
				}else{
					/* 
					 * 组件需求拆成两个行项目,
					 *  生产订单(A)，数量=核销数量，生产订单，数量=需求数量-核销数量
					 */
					WmsOutRequirementItemEntity itemA = new WmsOutRequirementItemEntity();	
					WmsOutRequirementItemEntity itemB = new WmsOutRequirementItemEntity();

					setCommomPrams(itemA,order,requirementNo,requirementItemNo);
					setCommomPrams(itemB,order,requirementNo,requirementItemNo);
					
					itemA.setBusinessName("48");
					itemA.setBusinessType("00");
					//是否需要审批
					if(commonService.checkApproval(orderItems.get(0).getWerks(), itemA.getBusinessType(), itemA.getBusinessName())){
						head.setCheckFlag("X");
					}
					itemA.setQty(order.getHX_QTY());
					itemB.setBusinessType("00");
					itemB.setBusinessName("48");
					//是否需要审批
					if(commonService.checkApproval(orderItems.get(0).getWerks(), itemB.getBusinessType(), itemB.getBusinessName())){
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
			item.setBusinessType("00");//无收据
			item.setBusinessName("48");//库存地点调拨(类型转移311)
			//是否需要审批
			if(commonService.checkApproval(orderItems.get(0).getWerks(), item.getBusinessType(), item.getBusinessName())){
				head.setCheckFlag("X");
			}
			setHxFlag(item);
			itemDao.insert(item);
			requirementItemNo++;
		}
		headDao.insert(head);	
		return requirementNo;
	}
	
	/**
	 * 生产订单出库需求的 hx_flag字段
	 * @param item
	 */
	private void setHxFlag(WmsOutRequirementItemEntity item){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessType", item.getBusinessType());
		params.put("businessName", item.getBusinessName());
		List<Map<String,Object>> hxFlagMap =  sendCreateRequirementDao.selectHxFlag(params);
		if(!CollectionUtils.isEmpty(hxFlagMap)){
			item.setHxFlag((String)hxFlagMap.get(0).get("SPECIAL_FLAG"));
		}
			
	}
	
	/**
	 * 设置生产订单通用属性
	 * @param item
	 * @param order
	 * @param requirementNo
	 * @param requirementItemNo
	 */
	private void setCommomPrams(WmsOutRequirementItemEntity item,CreateProduceOrderAO order,String requirementNo,int requirementItemNo){
		item.setLine(order.getLINE());
		item.setUnit(order.getMEINS());
		item.setStation(order.getSTATION());
		item.setSobkz(order.getSOBKZ());
		item.setRequirementNo(requirementNo);
		item.setRequirementItemNo(String.valueOf(requirementItemNo));
		requirementItemNo ++;
		item.setReqItemStatus("00");
		item.setQty(order.getREQ_QTY());
		item.setMoNo(order.getAUFNR());
		item.setMatnr(order.getMATNR().trim());
		item.setMaktx(order.getMAKTX());
		item.setLifnr(order.getVENDOR() == null ? "":order.getVENDOR().trim());
		item.setLgort(order.getLGORT() == null ? "":order.getLGORT().trim());
		item.setCreator(userUtils.getUser().get("USERNAME").toString());
		item.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
		item.setReceiveLgort(order.getAceptLgortList());
		item.setStation(order.getSTATION());
		item.setWhManager(order.getWhManager());
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

}
