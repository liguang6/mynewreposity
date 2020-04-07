package com.byd.wms.business.modules.out.service.impl;

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

import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.impl.CommonServiceImpl;
import com.byd.wms.business.modules.config.service.impl.WmsCWhServiceImpl;
import com.byd.wms.business.modules.out.dao.SendCreateRequirementDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.CreateProduceOrderAO;
import com.byd.wms.business.modules.out.entity.ProduceOrderVO;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.enums.OutModuleEnum;
import com.byd.wms.business.modules.out.service.ProduceOrderAddMat;
@Service
public class ProduceOrderAddMatServiceImpl implements ProduceOrderAddMat{

	@Autowired
	SendCreateRequirementDao sendCreateRequirementDao;

	@Autowired
	private UserUtils userUtils;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Integer> validProduceOrders(
			List<ProduceOrderVO> produceOrders,List<String> depts) {
		/*
		 * 根据生产订单号查【生产订单抬头数据-WMS_SAP_MO_HEAD】
		 * 效验生产订单号信息是否存在，如不存在，提示‘XXX订单信
		 * 息不存在/未同步，请确认输入是否正确或手动执行同步功能
		 * 生产订单’；判断账号是否有生产订单所属工厂的操作权限，
		 * 如果没有，报错：您无权操作**工厂的订单；有，判断生产订
		 * 单状态是否为可发料的状态（可发料状态：抬头状态字段包含
		 * ‘REL’且不包含‘TECO’‘DLFL‘‘LKD’、‘CLSD’字符串）：报错
		 * ‘**生产订单未发布或已关闭，不允许发料”
		 */

		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		for (ProduceOrderVO order : produceOrders) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("aufnr", order.getOrderNo());
			List<Map<String, Object>> existOrder = sendCreateRequirementDao
					.selectWMSSapMoHead(params);
			// 1.检验订单是否同步到了WMS数据库
			if (CollectionUtils.isEmpty(existOrder)) {
				resultMap.put(order.getOrderNo(), 1);
				continue;
			}
			// 3.判断是否有订单所属工厂的操作权限
			//List<SysDeptEntity> premissionPlants = tagUtils.deptListWithUser("3", null);
			boolean premissionFlag = false;
			/*for (String plant : depts) {
				if (((String) existOrder.get(0).get("WERKS")).equals(plant)) {
					premissionFlag = true;
					break;
				}
			}*/
			/*if (premissionFlag == false) {
				resultMap.put(order.getOrderNo(), 3);
				continue;
			}*/

			//4. 判断生产订单是否为可发料状态
			String istatTxt = (String) existOrder.get(0).get("ISTAT_TXT");
			if(istatTxt.indexOf("REL") == -1 || (istatTxt.indexOf("TECO") != -1 || istatTxt.indexOf("DLFL") != -1 || istatTxt.indexOf("LKD") != -1 || istatTxt.indexOf("CLSD") != -1)){
				//不可发料
				resultMap.put(order.getOrderNo(), 4);
			}
		}
		return resultMap;		
	}

	@Autowired
	WmsCDocNoService docNoService;
	@Autowired
	OutCommonService commonService;
	
	@Autowired
	WmsOutRequirementHeadDao headDao;
	@Autowired
	WmsOutRequirementItemDao itemDao;
	
	@Autowired
	private WmsCWhServiceImpl wmsCWhServiceImpl;
	
	@Autowired
	private CommonServiceImpl comService;

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String createOutReqSplit(List<CreateProduceOrderAO> orderItems,String staffNumber) throws Exception {
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
					reqestNo = this.createOutReq(orderlist,staffNumber);
				} else {
					reqestNo = reqestNo+ "," + this.createOutReq(orderlist,staffNumber);
				}
			}
		} else {
			reqestNo = this.createOutReq(orderItems,staffNumber);
		}
		return reqestNo;
	}
	
	@Override
	public String createOutReq(List<CreateProduceOrderAO> orderItems,String staffNumber) throws Exception {

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
		head.setCreator(staffNumber);
		head.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		head.setRequirementNo(requirementNo);
		head.setWerks(orderItems.get(0).getWerks());
		head.setWhNumber(orderItems.get(0).getWhNumber());
		head.setPurpose(orderItems.get(0).getUse());
		head.setRequirementType(OutModuleEnum.OUT_PRO_BL_BUSINESS_NAME.getCode());
		head.setRequirementStatus("00");
		head.setRequiredTime(orderItems.get(0).getRequireTime());
		head.setRequiredDate(orderItems.get(0).getRequireDate());
		head.setRequiredModel(orderItems.get(0).getSummaryMode());//备料模式
		
		
		// 创建出库需求项
		int requirementItemNo  = 1;
		for (CreateProduceOrderAO order : orderItems) {
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();		
			setCommomPrams(item,order,requirementNo,requirementItemNo);	
			if(order.getHX_QTY() != null && order.getHX_QTY() > 0){
				//剩余核销数量 > 0
				if(order.getREQ_QTY().doubleValue() < order.getHX_QTY().doubleValue()){
					//需求数量小于剩余核销数量
					item.setBusinessType(OutModuleEnum.OUT_PRO_HX_BUSINESS_TYPE.getCode());//生产订单(A)
					item.setBusinessName(OutModuleEnum.OUT_PRO_BL_BUSINESS_NAME.getCode());//生产订单领料(261)
				}else{
					/* 
					 * 组件需求拆成两个行项目,
					 *  生产订单(A)，数量=核销数量，生产订单，数量=需求数量-核销数量
					 */
					WmsOutRequirementItemEntity itemA = new WmsOutRequirementItemEntity();	
					WmsOutRequirementItemEntity itemB = new WmsOutRequirementItemEntity();

					setCommomPrams(itemA,order,requirementNo,requirementItemNo);
					setCommomPrams(itemB,order,requirementNo,requirementItemNo);
					
					itemA.setBusinessName(OutModuleEnum.OUT_PRO_BL_BUSINESS_NAME.getCode());//生产订单补料(不冲销预留261)
					itemA.setBusinessType(OutModuleEnum.OUT_PRO_HX_BUSINESS_TYPE.getCode());
					//是否需要审批
					if(commonService.checkApproval(orderItems.get(0).getWerks(), itemA.getBusinessType(), itemA.getBusinessName())){
						head.setCheckFlag("X");
					}
					itemA.setQty(order.getHX_QTY());
					
					itemB.setBusinessType(OutModuleEnum.OUT_PRO_BUSINESS_TYPE.getCode());
					itemB.setBusinessName(OutModuleEnum.OUT_PRO_BL_BUSINESS_NAME.getCode());//生产订单补料(不冲销预留261)
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
				

			item.setBusinessType(OutModuleEnum.OUT_PRO_BUSINESS_TYPE.getCode());//生产订单
			item.setBusinessName(OutModuleEnum.OUT_PRO_BL_BUSINESS_NAME.getCode());//生产订单补料(不冲销预留261)
			//是否需要审批
			if(commonService.checkApproval(orderItems.get(0).getWerks(), item.getBusinessType(), item.getBusinessName())){
				head.setCheckFlag("X");
			}
			
			//设置 hx_flag
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
		item.setLifnr(order.getVENDOR());
		item.setLgort(order.getLGORT());
		item.setCreator(userUtils.getUser().get("USERNAME").toString());
		item.setCreateDate(DateUtils.format(new Date(),
				DateUtils.DATE_TIME_PATTERN));
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
