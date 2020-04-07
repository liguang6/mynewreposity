/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.wms.business.modules.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.IfutureTaskService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.dao.WmsCPlantToDao;
import com.byd.wms.business.modules.config.dao.WmsCoreWhDao;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;

@Service("commonService")
public class CommonServiceImpl implements CommonService {
	private static final Logger log = LoggerFactory.getLogger("CommonServiceImpl");
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WmsSapRemote wmsSapRemote;
	@Autowired
	private WmsCPlantToDao wmsCPlantToDao;
	@Autowired
	private IfutureTaskService futureTaskService;
	@Autowired
	private WmsCoreWhDao wmsCoreWhDao;

	@Override
	public List<Map<String, Object>> getWhList(String whNumber,String lang) {
		return commonDao.getWhList(whNumber,lang);
	}

	@Override
	public List<Map<String, Object>> getVendorList(String lifnr,String werks) {
		return commonDao.getVendorList(lifnr,werks);
	}

	@Override
	public List<Map<String, Object>> getVendor(String lifnr) {
		return commonDao.getVendor(lifnr);
	}

	@Override
	public List<Map<String, Object>> getMaterialList(String werks, String matnr) {
		return commonDao.getMaterialList(werks, matnr);
	}

	@Override
	public List<Map<String, Object>> getBusinessList(Map<String, Object> params) {
		return commonDao.getBusinessList(params);
	}

	@Override
	public List<Map<String, Object>> getLoList(Map<String, Object> params) {
		return commonDao.getLoList(params);
	}

	@Override
	public List<Map<String, Object>> getGrAreaList(String WERKS) {
		return commonDao.getGrAreaList(WERKS);
	}

	/**
	 * 根据仓库号、料号等条件获取推荐的物料存储储位
	 * @param params Map
	 * WH_NUMBER：仓库号
	 * BIN_TYPE:储位类型 00 虚拟储位 01 进仓位 02 出仓位 03 拣配位 04 试装位 05 立库位
	 * BIN_STATUS：储位状态 00 未启用 01 可用 02 不可用
	 * STORAGE_MODEL：物料存储模式 00 固定存储 01随机存储
	 * @return WMS_CORE_WH_BIN 所有字段
	 */
	@Override
	public List<Map<String, Object>> getMatBinList(Map<String, Object> params){
		return commonDao.getMatBinList(params);
	}

	/**
	 * 根据工厂获取工厂下所有仓库号信息
	 */
	@Override
	public List<Map<String,Object>> getWhDataByWerks(@Param(value="WERKS") String WERKS){
		return commonDao.getWhDataByWerks(WERKS);
	}

	/**
	 * 获取退货原因
	 */
	@Override
	public List<Map<String,String>> getQcReturnReason(String REASON_DESC){
		return commonDao.getQcReturnReason(REASON_DESC);
	}

	/**
	 *
	 * @param matList
	 * @return
	 */
	private List<Map<String, Object>> getSapMoveType(Map<String, Object> cdmap,List<Map<String, Object>> matList){
		if("46".equals(cdmap.get("BUSINESS_NAME").toString())){
			List<Map<String, Object>> plantToInfo = commonDao.getPlantToInfoList(cdmap, matList);

			for (Map<String, Object> map : plantToInfo) {
				if(map.get("MOVE_TYPE") !=null && !"".equals(map.get("MOVE_TYPE").toString())) {
					String moveTypeStr = map.get("MOVE_TYPE").toString();
					String[] moveTypeArr = moveTypeStr.split("\\#\\*");
					int index = Integer.valueOf(moveTypeArr[0]);
					String  WMS_MOVE_TYPE = moveTypeArr[1];
					String  SAP_MOVE_TYPE = moveTypeArr[2];
					if(matList.get(index) !=null) {
						matList.get(index).put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
						matList.get(index).put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
					}
				}
			}
		}else {
			List<Map<String, Object>> rtn = commonDao.getSapMoveType(cdmap, matList);
			for (Map<String, Object> map : rtn) {
				if(map.get("SAP_MOVE_TYPE") !=null && !"".equals(map.get("SAP_MOVE_TYPE").toString())) {
					String sapMoveTypeStr = map.get("SAP_MOVE_TYPE").toString();
					String[] sapMoveTypeArr = sapMoveTypeStr.split("\\#\\*");
					int index = Integer.valueOf(sapMoveTypeArr[0]);
					String  SAP_MOVE_TYPE = sapMoveTypeArr[1];
					String WMS_MOVE_TYPE = sapMoveTypeArr[2];
					if(matList.get(index) !=null) {
						matList.get(index).put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
						matList.get(index).put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
					}
				}
			}
		}
		return matList;
	}

	/**
	 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
	 * @param cdmap MAP
	 * BUSINESS_NAME：业务类型名称
	 * BUSINESS_TYPE： WMS业务类型
	 * BUSINESS_CLASS：仓库业务分类
	 * WERKS：工厂代码（发货工厂）
	 * WERKS_T：收货工厂代码
	 * SOBKZ：特殊库存类型
	 * @return Map
	 * 过账参数键值对：WMS_MOVE_TYPE：WMS移动类型  SAP_MOVE_TYPE：SAP过账移动类型  SYN_FLAG：异步标识 MOVE_REAS：移动原因
	 * GM_CODE：BAPI货物移动分配事务代码 特定值
	 */
	@Override
	public Map<String, Object> getMoveAndSyn(Map<String, Object> cdmap){
		Map<String, Object> rtn = commonDao.getMoveAndSyn(cdmap);
		//未获取到WMS移动类型，调拨业务从工厂调拨关联关系配置表或者移动类型
		if(rtn==null || rtn.get("WMS_MOVE_TYPE")==null || StringUtils.isEmpty(rtn.get("WMS_MOVE_TYPE").toString())) {
			String F_WERKS = cdmap.get("F_WERKS")==null?null:cdmap.get("F_WERKS").toString();
			if(F_WERKS != null && !StringUtils.isEmpty(F_WERKS)){
				cdmap.put("F_WERKS", F_WERKS);
				Map<String, Object> plantToInfo = commonDao.getPlantToInfo(cdmap);
				rtn.put("WMS_MOVE_TYPE", plantToInfo.get("WMS_MOVE_TYPE"));
				rtn.put("SAP_MOVE_TYPE", plantToInfo.get("SAP_MOVE_TYPE"));
			}

		}
		return rtn;
	}

	/**
	 * 供收料、进仓、出库等调用
	 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
	 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
	 * SAP过账失败回滚
	 * @param params
	 * 过账内容键值对： WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期 HEADER_TXT：抬头文本 ,
	 * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型,BUSINESS_CLASS:业务分类
	 * 过账行项目List:matList
	 * @return String SAP_NO
	 */
	@Override
	public String doSapPost(Map<String, Object> params) {
		String SAP_NO = "";
		/**
		 * 待过账物料数据
		 */
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		/**
		 * 根据特殊库存类型，拆分过账项目数据,目前只能处理 自有、寄售
		 * 20190517优化逻辑：按过账移动类型步骤拆分过账项目数据，分为 一步过账，两步
		*/
		List<Map<String,Object>> oneSepMatList = new ArrayList<Map<String,Object>>(); //一步过账物料行项目
		List<Map<String,Object>> twoSepMatList = new ArrayList<Map<String,Object>>();  //两步过账物料行项目

		String WERKS = params.get("WERKS").toString(); //过账工厂
		String JZ_DATE = params.get("JZ_DATE").toString();//过账日期
		String PZ_DATE = params.get("PZ_DATE").toString();//凭证日期
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();//业务类型描述
		String BUSINESS_TYPE = params.get("BUSINESS_TYPE").toString();//WMS业务类型
		String BUSINESS_CLASS = params.get("BUSINESS_CLASS").toString();//业务分类

		//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", BUSINESS_NAME);
		cdmap.put("BUSINESS_TYPE", BUSINESS_TYPE);
		cdmap.put("BUSINESS_CLASS", BUSINESS_CLASS);
		cdmap.put("WERKS", WERKS);
		this.getSapMoveType(cdmap, matList);

		for (Map<String, Object> matMap : matList) {

			String SAP_MOVE_TYPE = matMap.get("SAP_MOVE_TYPE")==null?"":matMap.get("SAP_MOVE_TYPE").toString();
			if(SAP_MOVE_TYPE.contains("#")) {
				twoSepMatList.add(matMap);
			}else if(!"".equals(SAP_MOVE_TYPE)){
				oneSepMatList.add(matMap);
			}
		}
		if(oneSepMatList.size() ==0 && twoSepMatList.size() == 0) {
			SAP_NO = "无需过账到SAP";
			return SAP_NO;
		}

		Map<String,Object> moveSyn = null;
		Map<String,Object> moveSynTO = null;

		if(oneSepMatList.size()>0) {
			/*
			 * 一步过账
			 */
			//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
			cdmap.put("SOBKZ", oneSepMatList.get(0).get("SOBKZ"));
			cdmap.put("LGORT", oneSepMatList.get(0).get("LGORT"));
			cdmap.put("RECEIVE_WERKS", oneSepMatList.get(0).get("MOVE_PLANT"));
			cdmap.put("RECEIVE_LGORT", oneSepMatList.get(0).get("MOVE_STLOC"));
			moveSyn = this.getMoveAndSyn(cdmap);
		}

		if(twoSepMatList.size()>0) {
			//多步过账
			//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
			cdmap.put("SOBKZ", twoSepMatList.get(0).get("SOBKZ"));
			cdmap.put("LGORT", twoSepMatList.get(0).get("LGORT"));
			cdmap.put("RECEIVE_WERKS", twoSepMatList.get(0).get("MOVE_PLANT"));
			cdmap.put("RECEIVE_LGORT", twoSepMatList.get(0).get("MOVE_STLOC"));
			moveSyn = commonDao.getMoveAndSyn(cdmap);
		}

		//工厂间调拨业务、根据发货工厂、接收工厂等条件取SAP移动类型
		if (BUSINESS_NAME.equals("46") &&(moveSyn.get("SAP_MOVE_TYPE") ==null || "".equals(moveSyn.get("SAP_MOVE_TYPE")))) {
			moveSynTO = wmsCPlantToDao.getCPlantTo(cdmap);
			if(moveSynTO==null) {
				moveSyn.put("SAP_MOVE_TYPE", "303");
			} else {
				moveSyn.put("SAP_MOVE_TYPE", moveSynTO.get("SAP_MOVE_TYPE"));
			}
		}

		if(moveSyn!=null && moveSyn.get("SAP_MOVE_TYPE")!=null && !"".equals(moveSyn.get("SAP_MOVE_TYPE"))) {
			//需要过账
			String SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")!=null?moveSyn.get("SAP_MOVE_TYPE").toString():"";
			String SYN_FLAG = moveSyn.get("SYN_FLAG")!=null?moveSyn.get("SYN_FLAG").toString():"";
			String GM_CODE = moveSyn.get("GM_CODE")!=null?moveSyn.get("GM_CODE").toString():null;

			String REF_WMS_NO ="";
			if(oneSepMatList.size()>0){
				 REF_WMS_NO = params.get("WMS_NO")==null?oneSepMatList.get(0).get("WMS_NO")==null?null:oneSepMatList.get(0).get("WMS_NO").toString():params.get("WMS_NO").toString();
			}else{
				 REF_WMS_NO = params.get("WMS_NO")==null?twoSepMatList.get(0).get("WMS_NO")==null?null:twoSepMatList.get(0).get("WMS_NO").toString():params.get("WMS_NO").toString();
			}
			//WERKS：工厂、WMS_NO：WMS凭证号、JZ_DATE：记账日期、PZ_DATE：凭证日期、SAP_MOVE_TYPE：SAP过账移动类型、GM_CODE：
			Map<String,Object> asynPostHead = new HashMap<String,Object>();

			asynPostHead.put("WERKS", WERKS);
			asynPostHead.put("WMS_NO", REF_WMS_NO);
			asynPostHead.put("REF_WMS_NO", REF_WMS_NO);
			asynPostHead.put("PSTNG_DATE", JZ_DATE.replaceAll("-", ""));
			asynPostHead.put("DOC_DATE", PZ_DATE.replaceAll("-", ""));
			asynPostHead.put("JZ_DATE", JZ_DATE.toString().replaceAll("-", ""));
			asynPostHead.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			asynPostHead.put("PZ_DATE", PZ_DATE.toString().replaceAll("-", ""));
			asynPostHead.put("GM_CODE", GM_CODE);
			asynPostHead.put("LGORT_CONFIG", moveSyn.get("LGORT")!=null?moveSyn.get("LGORT").toString():null);
			//一步过账、两步过账同时存在时，后面失败无法取消前面成功过账SAP的凭证。
			List<Map<String,Object>> sap_success_list=new ArrayList<Map<String,Object>>();

			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE) && twoSepMatList.size()>0 && "X".equals(SYN_FLAG)) {
				//先处理多步过账 解决任务同时包含一步过账和多步过账，一步过账成功产生了SAP凭证，多步过账失败，冲销一步过账凭证，导致SAP出现垃圾凭证
				//业务实例：生产订单投料 包含 自有物料和寄售物料，投料过账时，寄售物料异常导致SAP会产生多组自有物料261和262的物料凭证
				//异步过账，创建过账定时任务
				SAP_NO += this.asynPost(asynPostHead, twoSepMatList);
			}
			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE) && oneSepMatList.size()>0 && "X".equals(SYN_FLAG)) {//需要过账
				//异步过账，创建过账定时任务
				SAP_NO += this.asynPost(asynPostHead, oneSepMatList);
			}

			//2019-10-19 thw优化为调用线程处理
/*			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE) && twoSepMatList.size()>0 && !"X".equals(SYN_FLAG)) {
				//同步过账
				SAP_NO += this.sapPost(asynPostHead, twoSepMatList, sap_success_list);
			}
			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE) && oneSepMatList.size()>0 && !"X".equals(SYN_FLAG)) {//需要过账
				//同步过账
				SAP_NO += this.sapPost(asynPostHead, oneSepMatList, sap_success_list);
			}
			//2019-10-19 thw 方法移出去
			Object REF_DOC_NO = asynPostHead.get("REF_DOC_NO")==null?(matList.get(0).get("REF_DOC_NO")==null?null:matList.get(0).get("WMS_NO")):asynPostHead.get("REF_DOC_NO");
			asynPostHead.put("REF_DOC_NO", REF_DOC_NO);
			SAP_NO = this.saveWmsDocSapMatDocAndSapDoc(asynPostHead, sap_success_list);*/
			//2019-10-19 thw优化为调用线程处理
			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE) && !"X".equals(SYN_FLAG) && (twoSepMatList.size()>0 || oneSepMatList.size()>0)){
				//2019-10-19 thw 考虑SAP资源紧张，响应慢，设置等待时长1分钟，如果超过1分钟未响应，转入后台等待
				Future<String> functionExecute = null;
		    	try {
		    		 asynPostHead.put("oneSepMatList", oneSepMatList); //一步过账物料行项目
		    		 asynPostHead.put("twoSepMatList", twoSepMatList);  //两步过账物料行项目
		    		 asynPostHead.put("sap_success_list", sap_success_list);
		    		 //开启SAP过账异步线程
		    		 functionExecute = futureTaskService.sapPost(asynPostHead);
				} catch (Exception e) {
		            e.printStackTrace();
		            SAP_NO = "开启SAP过账异步线程失败："+e.getMessage();
		            throw new RuntimeException(SAP_NO);
				}
		    	try {
		    		//获取结果，设置超过1分钟超时
		    		SAP_NO = functionExecute.get(1, TimeUnit.MINUTES);
				} catch (Exception e) {
					//1分钟内SAP过账未响应，SAP资源紧张，过账自动转为后台任务，后台任务执行完成后会自动完成WMS凭证关联的SAP凭证信息更新操作
		            //e.printStackTrace();
		            SAP_NO = "SAP系统繁忙，过账自动转为后台任务！";
		            return SAP_NO;
				}
		    	if(SAP_NO.contains("SAP过账失败")) {
		    		throw new RuntimeException(SAP_NO);
		    	}else {
		    		//1分钟返回结果后，线程里执行保存WMS凭证关联的SAP凭证信息逻辑不会生效：原因-WMS凭证事务未提交，WMS凭证在数据库表不存在，但是线程里插入SAP凭证信息的SQL会生效
		    		//主动执行如下WMS凭证更新关联SAP凭证逻辑
		    		Object REF_DOC_NO = asynPostHead.get("REF_DOC_NO")==null?(matList.get(0).get("REF_DOC_NO")==null?null:matList.get(0).get("WMS_NO")):asynPostHead.get("REF_DOC_NO");
					asynPostHead.put("REF_DOC_NO", REF_DOC_NO);
					SAP_NO = this.saveWmsDocSapMatDoc(asynPostHead, sap_success_list);
		    	}
			}

		}else {
			throw new RuntimeException("工厂未配置("+BUSINESS_NAME+")业务类型!");
		}
    	return SAP_NO;
	}

	/**SAP过账通用方法供，异步线程调用
	 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
	 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
	 * SAP过账失败回滚
	 * @param params
	 * 过账内容键值对： WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期 HEADER_TXT：抬头文本 ,
	 * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型,BUSINESS_CLASS:业务分类
	 * 过账行项目List: matList
	 * @return String SAP_NO
	 */
	@Override
	public String doSapPostBusiness(Map<String, Object> params) {
		String SAP_NO="";
			 Object REF_DOC_NO = null;
			 List<Map<String,Object>> oneSepMatList = (List<Map<String,Object>>) params.get("oneSepMatList"); //一步过账物料行项目
			 List<Map<String,Object>> twoSepMatList = (List<Map<String,Object>>) params.get("twoSepMatList");  //两步过账物料行项目
			 List<Map<String,Object>> sap_success_list = (List<Map<String,Object>>) params.get("sap_success_list");
			 if(twoSepMatList.size()>0) {
			 //同步过账
			 SAP_NO += this.sapPost(params, twoSepMatList, sap_success_list);
			 REF_DOC_NO = params.get("REF_DOC_NO")==null?(twoSepMatList.get(0).get("REF_DOC_NO")==null?null:twoSepMatList.get(0).get("WMS_NO")):params.get("REF_DOC_NO");
			 }
			 if(oneSepMatList.size()>0) {//需要过账
			 //同步过账
			 SAP_NO += this.sapPost(params, oneSepMatList, sap_success_list);
			 REF_DOC_NO = params.get("REF_DOC_NO")==null?(oneSepMatList.get(0).get("REF_DOC_NO")==null?null:oneSepMatList.get(0).get("WMS_NO")):params.get("REF_DOC_NO");
			 }

			 //2019-10-19 thw 方法移出去
			 params.put("REF_DOC_NO", REF_DOC_NO);
			 SAP_NO = this.saveWmsDocSapMatDocAndSapDoc(params, sap_success_list);
			 return SAP_NO;
			 }
			 /**
			 * WMS凭证冲销关联的SAP凭证
			 * SAP过账失败回滚
			 * @param params
			 * 过账内容键值对： WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期  HEADER_TXT：抬头文本 ,REF_WMS_NO：待冲销WMS凭证号  WMS_NO：新产生的WMS凭证号
			 * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型, BUSINESS_CLASS:业务分类
			 * 过账行项目matDocItemList-List<Map>  WMS_SAP_MAT_DOC：WMS凭证行项目对应的SAP凭证信息
			 * @return String SAP_NO
			 */
			@Override
			@Transactional(rollbackFor=Exception.class)
			public String sapGoodsMvtReversal(Map<String, Object> params) {
//		System.err.println("params "+params.toString());
				String SAP_NO = "";
				try {
					String LGORT = params.get("LGORT")==null?null:params.get("LGORT").toString();

					String WERKS = params.get("WERKS")==null?null:params.get("WERKS").toString();
					/**
					 * 待冲销WMS凭证号
					 */
					String REF_WMS_NO = params.get("REF_WMS_NO")==null?null:params.get("REF_WMS_NO").toString();
					/**
					 * 冲销凭证新产生的WMS凭证号
					 */
					String WMS_NO = params.get("WMS_NO")==null?null:params.get("WMS_NO").toString();
					if(REF_WMS_NO == null) {
						throw new RuntimeException("需要冲销的WMS凭证不能为空！");
					}
					/**
			 * 过账日期-记账日期
			 */
			String JZ_DATE = params.get("JZ_DATE").toString();
			String PZ_DATE = params.get("PZ_DATE")==null?JZ_DATE:params.get("PZ_DATE").toString();
			String HEADER_TXT = params.get("HEADER_TXT")==null?null:params.get("HEADER_TXT").toString();
			/**
			 * 待冲销过账物料数据
			 */
			List<Map<String, Object>> matDocItemList = params.get("matDocItemList")==null?null:(List<Map<String, Object>>)params.get("matDocItemList");
			if(matDocItemList==null||matDocItemList.size()<=0) {
				//无取消凭证行项目
				throw new RuntimeException("未勾选凭证行项目！");
			}

			List<Map<String,Object>> sap_success_list=new ArrayList<Map<String,Object>>();
			List<String> exception_list=new ArrayList<String>();

			/**
			 * WMS凭证行项目数据处理
			 */
			Map<String,Object> matDocMap = new HashMap<String,Object>(); //待冲销SAP凭证清单
			List<String> allReversalSapMatList = new ArrayList<String>();
			for (Map<String, Object> matDocItem : matDocItemList) {
				//WMS凭证行项目关联的SAP物料凭证行项目
				String WMS_SAP_MAT_DOC = matDocItem.get("WMS_SAP_MAT_DOC")==null?null:matDocItem.get("WMS_SAP_MAT_DOC").toString();
				if(WMS_SAP_MAT_DOC!=null&&WMS_SAP_MAT_DOC.length()>0) {
					String[] sapDocArray = WMS_SAP_MAT_DOC.split(";");
					for (String sapDocStr : sapDocArray) {
						if(sapDocStr.length()>0) {
							String[] sapDocItemArray = sapDocStr.split(":");
							if(sapDocItemArray.length>0) {
								String matDoc = sapDocItemArray[0]; //SAP物料凭证号
								String matItem = sapDocItemArray[1]; //SAP物料凭证行项目号
								if(!allReversalSapMatList.contains(matDoc)) {
									allReversalSapMatList.add(matDoc);
								}

								/**
								 * 待冲销SAP凭证行项目信息
								 */
								Map<String, Object> matDocItem1 = new HashMap<String,Object>();
								matDocItem1.putAll(matDocItem);
								matDocItem1.put("MATDOC_ITEM", matItem);
								matDocItem1.put("WMS_ITEM_NO", matDocItem.get("WMS_ITEM_NO"));


								List<Map<String,Object>> matItemList = matDocMap.get(matDoc)==null?null:(List<Map<String,Object>>)matDocMap.get(matDoc);
								if(matItemList==null) {
									matItemList = new ArrayList<Map<String,Object>>();
/*									Map<String,Object> refWmsItemMap = new HashMap<String,Object>();
									refWmsItemMap.put("REF_WMS_ITEM_NO", matDocItem.get("REF_WMS_ITEM_NO")==null?null:matDocItem.get("REF_WMS_ITEM_NO").toString());
									matItemList.add(refWmsItemMap);*/
									matItemList.add(matDocItem1);
								}else {
									matItemList.add(matDocItem1);
								}

								matDocMap.put(matDoc, matItemList);
							}
						}

					}
				}

			}
			/**
			 * 处理多步凭证，先冲销后产生的凭证，再冲销前一步凭证
			 */
			String LGORT_CONFIG = "";
			for(int i=allReversalSapMatList.size()-1;i>=0;i--) {
				String reversalSapMatDoc = allReversalSapMatList.get(i);//待冲销SAP凭证号
				//待冲销SAP凭证行项目
				List<Map<String,Object>> matItemList = matDocMap.get(reversalSapMatDoc)==null?null:(List<Map<String,Object>>)matDocMap.get(reversalSapMatDoc);
				if(matItemList!=null && matItemList.size()>0) {
					//根据SAP凭证号和行项目号获取库位
					List<Map<String,Object>> sapDocItemInfoList =  commonDao.getSapDocItemInfo(reversalSapMatDoc, matItemList);
					for (Map<String, Object> matItemMap : matItemList) {
						String MATDOC_ITEM = matItemMap.get("MATDOC_ITEM").toString();
						for (Map<String, Object> sapDocItem : sapDocItemInfoList) {
							if(MATDOC_ITEM.equals(sapDocItem.get("MATDOC_ITM"))){
								//更改库位
								matItemMap.put("STGE_LOC", sapDocItem.get("STGE_LOC"));
								matItemMap.put("MOVE_TYPE", sapDocItem.get("MOVE_TYPE"));
								matItemMap.put("DOC_YEAR", sapDocItem.get("DOC_YEAR"));
								matItemMap.put("REF_MATDOC_ITM", sapDocItem.get("MATDOC_ITM"));
								//冲销、取消是否带K 放在这里判断
								if (i== 0 && !"Z".equals(matItemMap.get("SOBKZ")))
									matItemMap.put("SPEC_STOCK", matItemMap.get("SOBKZ"));
								else
									matItemMap.put("SPEC_STOCK", sapDocItem.get("SPEC_STOCK"));

								matItemMap.put("MOVE_BATCH", sapDocItem.get("MOVE_BATCH"));
								//MB1B移动类型，需要接收工厂
								if (i== 0 && !sapDocItem.get("MOVE_TYPE").equals("311")) //最后一个凭证
									matItemMap.put("MOVE_PLANT", matItemMap.get("MOVE_PLANT"));
								else
									matItemMap.put("MOVE_PLANT", sapDocItem.get("MOVE_PLANT"));

								matItemMap.put("MOVE_MAT", sapDocItem.get("MOVE_MAT"));

								if (!sapDocItem.get("MOVE_TYPE").equals("411"))
									LGORT_CONFIG = matItemMap.get("MOVE_STLOC") == null ? "":matItemMap.get("MOVE_STLOC").toString(); //把前一步凭证的库位保存到LGORT_CONFIG（中转库位），
								else
									LGORT_CONFIG = LGORT_CONFIG.equals("") ? (String)matItemMap.get("MOVE_STLOC"):LGORT_CONFIG;

								//MB1B移动类型，需要接收库位
								matItemMap.put("MOVE_STLOC", LGORT_CONFIG);

								if (!sapDocItem.get("MOVE_TYPE").equals("411"))
									LGORT_CONFIG = sapDocItem.get("STGE_LOC")==null?null:sapDocItem.get("STGE_LOC").toString(); //把前一步凭证的库位保存到LGORT_CONFIG（中转库位），

								break;
							}
						}
					}
				}

				Map<String,Object> sapSynMap = new HashMap<String,Object>();
				sapSynMap.put("WERKS", WERKS);
				//sapSynMap.put("REF_DOC_NO", reversalSapMatDoc);
				sapSynMap.put("PSTNG_DATE", JZ_DATE.replaceAll("-", ""));
				sapSynMap.put("DOC_DATE", PZ_DATE.replaceAll("-", ""));
				sapSynMap.put("JZ_DATE", JZ_DATE.toString().replaceAll("-", ""));
				sapSynMap.put("PZ_DATE", PZ_DATE.toString().replaceAll("-", ""));
				sapSynMap.put("HEADER_TXT", HEADER_TXT);
				String GM_CODE = matItemList.get(0).get("GM_CODE")==null?null:matItemList.get(0).get("GM_CODE").toString();
				sapSynMap.put("GM_CODE", GM_CODE);

				//获取行项目单位换算信息
				List<String> matUnitList = commonDao.getMatUnit(matItemList);
				//单位替换
				for (String string : matUnitList) {
					String[] unitArr = string.split("\\#\\*");
					int index = Integer.valueOf(unitArr[0]);
					String  UNIT_EN = unitArr[1];
					if(matItemList.get(index) !=null) {
						matItemList.get(index).put("ENTRY_UOM", UNIT_EN);
					}

				}

				//封装过账参数
				List<Map<String,String>> ITEMLIST = new ArrayList<Map<String,String>>();
				String MOVE_TYPE = null;
				String SAP_MOVE_TYPE = null;
				for (Map<String, Object> m : matItemList) {
					MOVE_TYPE = (String)m.get("MOVE_TYPE");
					SAP_MOVE_TYPE = (String)m.get("SAP_MOVE_TYPE");
					Map<String,String> _m=new HashMap<String,String>();
					//采购订单105凭证冲销时需要传入源105凭证关联的103凭证及行项目号，否则可能会报错：短缺  收货数量
					if("105".equals(SAP_MOVE_TYPE) ||"106".equals(SAP_MOVE_TYPE) || "103".equals(SAP_MOVE_TYPE) ||"104".equals(SAP_MOVE_TYPE)) {
						_m.put("REF_DOC_YR", m.get("REF_SAP_MATDOC_YEAR")==null?m.get("DOC_YEAR")==null?null:m.get("DOC_YEAR").toString():m.get("REF_SAP_MATDOC_YEAR").toString());//参考凭证年份
						_m.put("REF_DOC",  m.get("REF_SAP_MATDOC_NO")==null?null:m.get("REF_SAP_MATDOC_NO").toString());
						_m.put("REF_DOC_IT", m.get("REF_SAP_MATDOC_ITEM_NO")==null?null:m.get("REF_SAP_MATDOC_ITEM_NO").toString());
					}
					//_m.put("LGORT",m.get("LGORT")==null?null:m.get("LGORT").toString());
					_m.put("WMS_NO", m.get("WMS_NO")==null?null:m.get("WMS_NO").toString());
					_m.put("WMS_ITEM_NO", m.get("WMS_ITEM_NO")==null?null:m.get("WMS_ITEM_NO").toString());

					_m.put("STGE_LOC", m.get("STGE_LOC")==null?null:m.get("STGE_LOC").toString());

					if(MOVE_TYPE.equals("411") || MOVE_TYPE.equals("412") || MOVE_TYPE.equals("311") || MOVE_TYPE.equals("312")) {
						GM_CODE= "04";
					} else {
						GM_CODE = m.get("GM_CODE")==null?null:m.get("GM_CODE").toString();
					}
					_m.put("GM_CODE", GM_CODE);
					_m.put("MATERIAL", m.get("MATNR").toString());//物料号
					_m.put("MATNR", m.get("MATNR").toString());//物料号
					_m.put("PLANT", WERKS);//工厂代码
					_m.put("WERKS", WERKS);//工厂代码
					_m.put("F_WERKS", (String)m.get("F_WERKS"));//工厂代码
					_m.put("BATCH", (String)m.get("BATCH"));//WMS批次
					_m.put("CHARG", (String)m.get("CHARG"));//交货批次 交货单过账使用
					_m.put("MOVE_TYPE", MOVE_TYPE);//SAP过账移动类型
//					if(!"Z".equals(m.get("SOBKZ"))) {  冲销、取消是否带K 放在上面判断
					_m.put("SPEC_STOCK",m.get("SPEC_STOCK")==null?null:m.get("SPEC_STOCK").toString());//特殊库存标识
//					}
					String ENTRY_QNT = m.get("RECEIPT_QTY")==null?(
							(m.get("ENTRY_QNT")==null?(m.get("QTY_SAP")==null?"0":m.get("QTY_SAP").toString())
									:m.get("ENTRY_QNT").toString())):m.get("RECEIPT_QTY").toString();
					_m.put("ENTRY_QNT", ENTRY_QNT);//收货数量

					_m.put("LFIMG", ENTRY_QNT);//实际交货数量  交货单过账使用
					_m.put("UMREZ", m.get("UMREZ")==null?"1":m.get("UMREZ").toString());//实际交货数量 交货单过账使用
					_m.put("UMREN", m.get("UMREN")==null?"1":m.get("UMREN").toString());//实际交货数量 交货单过账使用
					String ENTRY_UOM = m.get("ENTRY_UOM")==null?(m.get("UNIT")==null?"PCS":m.get("UNIT").toString()):m.get("ENTRY_UOM").toString();
					_m.put("ENTRY_UOM", ENTRY_UOM);//单位

					String PO_NUMBER = m.get("PO_NO")==null?(m.get("PO_NUMBER")==null?(m.get("EBELN")==null?null:m.get("EBELN").toString()):m.get("PO_NUMBER").toString()):m.get("PO_NO").toString();
					_m.put("PO_NUMBER",PO_NUMBER);//采购订单号
					String PO_ITEM = m.get("PO_ITEM_NO")==null?(m.get("PO_ITEM")==null?null:m.get("PO_ITEM").toString()):m.get("PO_ITEM_NO").toString();
					_m.put("PO_ITEM", PO_ITEM);//采购订单行项目号

					_m.put("VENDOR", m.get("VENDOR")==null?(m.get("LIFNR")==null?null:m.get("LIFNR").toString()):m.get("VENDOR").toString());//供应商
					_m.put("CUSTOMER", m.get("CUSTOMER")==null?null:m.get("CUSTOMER").toString());//客户代码
					_m.put("COSTCENTER", m.get("COSTCENTER")==null?(m.get("COST_CENTER")==null?null:m.get("COST_CENTER").toString()):m.get("COSTCENTER").toString());//成本中心
					_m.put("WBS_ELEM", m.get("WBS_ELEM")==null?(m.get("WBS")==null?null:m.get("WBS").toString()):m.get("WBS_ELEM").toString());//WBS元素
					_m.put("GR_RCPT", m.get("GR_RCPT")==null?(m.get("RECEIVER")==null?"":m.get("RECEIVER").toString()):m.get("GR_RCPT").toString());//收货方/运达方

					String ORDERID = null;
					if(m.get("ORDERID")!=null && !m.get("ORDERID").toString().trim().equals("")) {
						ORDERID = m.get("ORDERID").toString();
					}else if(m.get("MO_NO")!=null && !m.get("MO_NO").toString().trim().equals("")) {
						ORDERID = m.get("MO_NO").toString();
					}else if(m.get("IO_NO")!=null && !m.get("IO_NO").toString().trim().equals("")) {
						ORDERID = m.get("IO_NO").toString();
					}
					_m.put("ORDERID", ORDERID);//内部/生产订单号
					String ORDER_ITNO = m.get("MO_ITEM_NO")==null?(m.get("POSNR")==null?null:m.get("POSNR").toString()):m.get("MO_ITEM_NO").toString();
					//261、262不传订单行
					if (!MOVE_TYPE.equals("261") && !MOVE_TYPE.equals("262"))
						_m.put("ORDER_ITNO", ORDER_ITNO);//生产订单行项目号

					if (!MOVE_TYPE.equals("411")) {
						_m.put("RESERV_NO", m.get("RESERV_NO")==null?(m.get("RSNUM")==null?null:m.get("RSNUM").toString()):m.get("RESERV_NO").toString());//预留号
						_m.put("RES_ITEM", m.get("RES_ITEM")==null?(m.get("RSPOS")==null?null:m.get("RSPOS").toString()):m.get("RES_ITEM").toString());//预留行项目号
					}
					String VBELN = m.get("VBELN")==null?(m.get("SAP_OUT_NO")==null?null:m.get("SAP_OUT_NO").toString()):m.get("VBELN").toString();//交货单号
					String POSNR_VL = m.get("POSNR_VL")==null?(m.get("SAP_OUT_ITEM_NO")==null?null:m.get("SAP_OUT_ITEM_NO").toString()):m.get("POSNR_VL").toString();
					_m.put("VBELN_VL", VBELN);//交货单号
					_m.put("POSNR_VL", POSNR_VL);//交货单行项目号
					_m.put("VBELN", VBELN);//交货单号
					_m.put("POSNN", POSNR_VL);//交货过账使用 后续凭证行项目号（6） 可填
					_m.put("DELIVERY", VBELN);//交货单号
					_m.put("ITEM_TEXT",m.get("ITEM_TEXT")==null?null:m.get("ITEM_TEXT").toString() );//行文本
					_m.put("LGORT", LGORT);//

					String MVT_IND = null; //移动标识
					if(null != PO_NUMBER && !"".equals(PO_NUMBER)) {
							if(!MOVE_TYPE.equals("351") && !MOVE_TYPE.equals("352") && !MOVE_TYPE.equals("601") && !MOVE_TYPE.equals("645")
									&& !MOVE_TYPE.equals("657") && !MOVE_TYPE.equals("675") && !MOVE_TYPE.equals("311") && !MOVE_TYPE.equals("312")
									&& !MOVE_TYPE.equals("411") && !MOVE_TYPE.equals("412")) {
							MVT_IND = "B"; //采购订单过账
						}
					}
					if(null != ORDERID && !"".equals(ORDERID) && GM_CODE.equals("02")) {
						MVT_IND = "F"; //生产订单过账
					}
					if(MOVE_TYPE.equals("541")) {
						//分包发料
						MVT_IND = "";
						_m.put("VENDOR",m.get("CUSTOMER")==null?"":m.get("CUSTOMER").toString()); //541需求类型，委外单位放在事务表 合作伙伴字段上
					}

					if(MOVE_TYPE.equals("531")) {
						//副产品
						MVT_IND = null;
						_m.put("ORDER_ITNO", null);//生产订单行项目号
					}
					if(null !=MVT_IND) {
						_m.put("MVT_IND", MVT_IND);//移动标识
					}

					Map<String,Object> sapMoveReas = commonDao.getSapMoveReasByMoveType(MOVE_TYPE);
					String MOVE_REAS = sapMoveReas == null?null:sapMoveReas.get("MOVE_REAS")==null?null:sapMoveReas.get("MOVE_REAS").toString();
					_m.put("MOVE_REAS", MOVE_REAS);

					_m.put("MOVE_BATCH", m.get("MOVE_BATCH")==null?(m.get("F_BATCH")==null?null:m.get("F_BATCH").toString()):m.get("MOVE_BATCH").toString());
					//MB1B移动类型，需要接收工厂
					_m.put("MOVE_PLANT", m.get("MOVE_PLANT")==null?(m.get("F_WERKS")==null?null:m.get("F_WERKS").toString()):m.get("MOVE_PLANT").toString());
					_m.put("MOVE_MAT", m.get("MOVE_MAT")==null?null:m.get("MOVE_MAT").toString());
					//MB1B移动类型，需要接收库位
					_m.put("MOVE_STLOC", m.get("MOVE_STLOC")==null?(m.get("F_LGORT")==null?null:m.get("F_LGORT").toString()):m.get("MOVE_STLOC").toString());

					_m.put("XSTOB", "X");//冲销凭证标识

					ITEMLIST.add(_m);
//
				}
				sapSynMap.put("ITEMLIST", ITEMLIST);

				Map<String,Object> sapDoc = new HashMap<String,Object>();
				if(ITEMLIST.size()>0) {
					sapDoc =	wmsSapRemote.getSapBapiGoodsmvtCreate(sapSynMap);
				}

				if("0".equals(sapDoc.get("CODE"))) {
					sapDoc.put("WERKS", WERKS);
					sapDoc.put("MOVE_TYPE", MOVE_TYPE);
					sapDoc.put("REF_DOC_NO", reversalSapMatDoc);

					sapDoc.put("GOODSMVT_PSTNG_DATE", PZ_DATE.replaceAll("-", ""));
					sapDoc.put("ITEMLIST", ITEMLIST);
					sapDoc.put("REF_WMS_NO", WMS_NO);
					sapDoc.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
					sap_success_list.add(sapDoc);
				}else {//过账失败，抛出异常，cancel掉已过账的记录
					exception_list.add(MOVE_TYPE+"过账失败："+sapDoc.get("MESSAGE")+";");
					Iterator it =sap_success_list.iterator();
					while(it.hasNext()) {
						Map<String,Object> c=(Map<String,Object>)it.next();
						Map<String,Object>rmap = wmsSapRemote.getSapBapiGoodsmvtCancel(c);
						if(!"0".equals(rmap.get("CODE"))) {
							exception_list.add("凭证冲销失败，凭证号："+c.get("MATERIALDOCUMENT")+",凭证年份："+c.get("MATDOCUMENTYEAR")+
									", 移动类型："+MOVE_TYPE);
						}
						it.remove();
					}
				}

			}

			if(exception_list.size()>0) {//有过账失败抛出异常
				throw new RuntimeException(StringUtils.join(exception_list, ";"));
			}
			if(sap_success_list.size()>0) {//过账成功，保存SAP过账记录
				String MAT_DOC_STR = "";

				for(Map<String,Object> m:sap_success_list) {
					List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
					List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
					List<Map<String,String>> updateWmsdocList = new ArrayList<Map<String,String>>();//更新WMS凭证行项目数据

					List<Map<String,String>> ITEMLIST = (List<Map<String,String>>)m.get("ITEMLIST");
					Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(m.get("MATERIALDOCUMENT").toString(),m.get("MATDOCUMENTYEAR").toString());
					Map<String,String> head=(Map<String, String>) rm.get("GOODSMVT_HEADER");
					List<Map<String,String>> itemList =(List<Map<String, String>>) rm.get("GOODSMVT_ITEMS");
					if(head!=null) {
						head.put("REF_WMS_NO", WMS_NO==null?null:WMS_NO.toString());
						head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
						head.put("REF_DOC_NO", m.get("REF_DOC_NO").toString());
						sap_head_list.add(head);
						SAP_NO+=head.get("MAT_DOC")+"(" +itemList.get(0).get("MOVE_TYPE")+"); <br/>";
					}
					List<Integer> indexList = new ArrayList<Integer>();
					for(int i=0;i<itemList.size();i++) {
						Map<String,String> im=itemList.get(i);
						if(im.get("PLANT").equals(WERKS)) {
							indexList.add(i);
							//im.put("REF_WMS_ITEM_NO", ITEMLIST.get(i).get("WMS_ITEM_NO"));
						}
						im.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
						sap_detail_list.add(im);

						Map<String,String> updateWmsdoc = new HashMap<String,String>();
						updateWmsdoc.put("WMS_NO", WMS_NO.toString());
						//updateWmsdoc.put("WMS_ITEM_NO", ITEMLIST.get(i).get("WMS_ITEM_NO"));
						updateWmsdoc.put("WMS_SAP_MAT_DOC", itemList.get(i).get("MAT_DOC")+":"+itemList.get(i).get("MATDOC_ITM")+";");

						updateWmsdocList.add(updateWmsdoc);
					}
					for(int x=0;x<indexList.size();x++) {
						int y = x <ITEMLIST.size() ? x:ITEMLIST.size()-1;
						sap_detail_list.get(indexList.get(x)).put("REF_WMS_ITEM_NO", ITEMLIST.get(y).get("WMS_ITEM_NO"));
						sap_detail_list.get(indexList.get(x)).put("REF_MATDOC_ITM", ITEMLIST.get(y).get("REF_DOC_IT"));
						updateWmsdocList.get(indexList.get(x)).put("WMS_ITEM_NO", ITEMLIST.get(y).get("WMS_ITEM_NO"));
					}
					//MAT_DOC_STR += m.get("MOVE_TYPE")+":"+head.get("MAT_DOC")+";";
					//pengtao 20190701 update
					MAT_DOC_STR += m.get("SAP_MOVE_TYPE")+":"+head.get("MAT_DOC")+";";

					/**
					 * 更新WMS凭证行项目SAP物料凭证信息
					 */
					commonDao.updateWMSDocItemSapDocInfo(updateWmsdocList);

					/**
					 * 更新WMS凭证抬头SAP物料凭证信息
					 */
					Map<String,Object> wmsDocMap = new HashMap<String,Object>();
					//pengtao 20190701 update
					wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
					wmsDocMap.put("WMS_NO", WMS_NO);
					commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);

					commonDao.insertSapDocHead(sap_head_list);	//SAP凭证表-抬头
					commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
				}

			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return SAP_NO;
	}


	/**
	 * SAP异步过账任务调度
	 */
	@Override
	public void sapPostJobSync() {
		/*
		 * 查询待过账（任务标识为01-未过账，03-过账失败）的任务
		 */
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JOB_FLAG", "01");
		/*
		 * 过账任务清单
		 */
		List<Map<String, Object>> sapPostJobList = commonDao.getSapPostJob(params);

		for (Map<String, Object> sapPostJob : sapPostJobList) {
			/*
			 * 执行单任务过账
			 */
			this.sapPostJob(sapPostJob);
		}
	}
	/**
	 *
	 * @param sapPostJob 单个SAP过账任务
	 */
	@Transactional(rollbackFor=Exception.class)
	private void sapPostJob(Map<String, Object> sapPostJob) {
		String SAP_JOB_NO = sapPostJob.get("SAP_JOB_NO").toString();//SAP过账任务编号
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SAP_JOB_NO", SAP_JOB_NO);

		/**
		 * 拆分同任务号，序号不同任务
		 */
		List<Map<String, Object>> sapPostJobSeqList = commonDao.getSapPostJobSeq(params);
		for (Map<String, Object> sapPostJobSeq : sapPostJobSeqList) {
			String JOB_SEQ = sapPostJobSeq.get("JOB_SEQ").toString();
			params.put("JOB_SEQ", JOB_SEQ);
			List<Map<String, Object>> sapPostJobItemList = commonDao.getSapPostJobItem(params);
			if(sapPostJobItemList.size()>0) {

				try {
					//2019-07-03 add by thw 处理105过账委外PO组件物料消耗
					Iterator<Map<String,Object>> it = sapPostJobItemList.iterator();
			    	while(it.hasNext()) {
			    		Map<String,Object> map=it.next();

						List<Map<String,Object>> poComonentList = new ArrayList<>();
						String SAP_JOB_ITEM_NO = map.get("SAP_JOB_ITEM_NO").toString();
						while(it.hasNext()) {
							Map<String,Object> map2=it.next();

							String PARENT_JOB_ITEM_NO = map2.get("PARENT_JOB_ITEM_NO")==null?"":map2.get("PARENT_JOB_ITEM_NO").toString();
							if(SAP_JOB_ITEM_NO.equals(PARENT_JOB_ITEM_NO)) {
								poComonentList.add(map2);
								it.remove();
							}
						}
						map.put("poComonentList", poComonentList);
			    	}
			    	//一步过账、两步过账同时存在时，后面失败无法取消前面成功过账SAP的凭证。
			    	List<Map<String,Object>> sap_success_list=new ArrayList<Map<String,Object>>();
					this.sapPost(sapPostJob, sapPostJobItemList, sap_success_list);

					//过账成功后，更新事务表关联SAP凭证信息
					Object REF_WMS_NO = sapPostJob.get("WMS_NO")==null?(sapPostJob.get("REF_WMS_NO")==null?sapPostJobItemList.get(0).get("WMS_NO"):sapPostJob.get("REF_WMS_NO")):sapPostJob.get("WMS_NO");
					Object REF_DOC_NO = sapPostJob.get("REF_DOC_NO")==null?(sapPostJobItemList.get(0).get("REF_DOC_NO")==null?null:sapPostJobItemList.get(0).get("WMS_NO")):sapPostJob.get("REF_DOC_NO");
					if(sap_success_list.size()>0) {
						//过账成功，保存SAP过账记录
						for(Map<String,Object> m:sap_success_list) {
							String MAT_DOC_STR = "";

							List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
							List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
							List<Map<String,String>> updateWmsdocList = new ArrayList<Map<String,String>>();//更新WMS凭证行项目数据

							List<Map<String,String>> ITEMLIST = (List<Map<String,String>>)m.get("ITEMLIST"); //待过账物料清单
							Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(m.get("MATERIALDOCUMENT").toString(),m.get("MATDOCUMENTYEAR").toString());
							Map<String,String> head=(Map<String, String>) rm.get("GOODSMVT_HEADER");
							List<Map<String,String>> itemList =(List<Map<String, String>>) rm.get("GOODSMVT_ITEMS");
							if(head!=null) {
								head.put("REF_WMS_NO", REF_WMS_NO==null?"":REF_WMS_NO.toString());
								head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
								head.put("REF_DOC_NO", REF_DOC_NO==null?"":REF_DOC_NO.toString());
								sap_head_list.add(head);
							}
							//解决SAP凭证中不同行项目物料、单位、数量完全一致无法正确匹配WMS行项目和SAP凭证行项目
							String MATDOC_ITM_MATCHD = "";
							for (Map<String, String> wmsDocMap : ITEMLIST) {
								String wmdDoc_MATNR = wmsDocMap.get("MATNR").toString();
								String wmdDoc_WERKS = wmsDocMap.get("PLANT").toString();
								String wmdDoc_UNIT = wmsDocMap.get("ENTRY_UOM").toString();
								Double wmdDoc_QTY_SAP = Double.valueOf(wmsDocMap.get("ENTRY_QNT").toString());

								for (Map<String, String> sapDocMap : itemList) {
									String sapDoc_MATERIAL = sapDocMap.get("MATNR").toString();
									String sapDoc_PLANT = sapDocMap.get("PLANT").toString();
									String sapDoc_ENTRY_UOM = sapDocMap.get("ENTRY_UOM").toString();
									Double sapDoc_ENTRY_QNT = Double.valueOf(sapDocMap.get("ENTRY_QNT").toString());
									String sapDoc_MATDOC_ITM = sapDocMap.get("MATDOC_ITM");
									if(wmdDoc_MATNR.equals(sapDoc_MATERIAL) && wmdDoc_WERKS.equals(sapDoc_PLANT) &&
											wmdDoc_UNIT.equals(sapDoc_ENTRY_UOM) &&
											wmdDoc_QTY_SAP.doubleValue() ==sapDoc_ENTRY_QNT.doubleValue() &&
											!MATDOC_ITM_MATCHD.contains(sapDoc_MATDOC_ITM) &&
											(sapDocMap.get("X_AUTO_CRE")==null || !"X".equals(sapDocMap.get("X_AUTO_CRE")))) {
										MATDOC_ITM_MATCHD +=sapDoc_MATDOC_ITM+";";
										//匹配
										Map<String,String> updateWmsdoc = new HashMap<String,String>();
										updateWmsdoc.put("WMS_NO", wmsDocMap.get("WMS_NO"));
										updateWmsdoc.put("WMS_ITEM_NO", wmsDocMap.get("WMS_ITEM_NO"));
										updateWmsdoc.put("WMS_SAP_MAT_DOC", sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";");
										updateWmsdocList.add(updateWmsdoc);

										sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
										sapDocMap.put("REF_WMS_ITEM_NO", sapDocMap.get("REF_WMS_ITEM_NO")!=null?(sapDocMap.get("REF_WMS_ITEM_NO")+";"+wmsDocMap.get("WMS_ITEM_NO")):wmsDocMap.get("WMS_ITEM_NO"));
										sapDocMap.put("REF_MATDOC_ITM", wmsDocMap.get("REF_MATDOC_ITM"));

										sap_detail_list.add(sapDocMap);
										break;
									}
								}
							}

							MAT_DOC_STR += m.get("MOVE_TYPE")+":"+head.get("MAT_DOC")+";";
							/**
							 * 更新WMS凭证行项目SAP物料凭证信息
							*/
							commonDao.updateWMSDocItemSapDocInfo(updateWmsdocList);

							/**
							 * 更新WMS凭证抬头SAP物料凭证信息
							*/
							Map<String,Object> wmsDocMap = new HashMap<String,Object>();
							wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
							wmsDocMap.put("WMS_NO", REF_WMS_NO);
							commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);

							commonDao.insertSapDocHead(sap_head_list);	//SAP凭证表-抬头
							commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
						}
					}

				} catch (Exception e) {

				}finally {
					for (Map<String, Object> map : sapPostJobItemList) {
						if("03".equals(sapPostJob.get("JOB_FLAG").toString())) {
							map.put("JOB_FLAG", "03");
							map.put("POST_QTY", 0);
						}else {
							Double ALL_ENTRY_QNT = Double.valueOf(map.get("ALL_ENTRY_QNT")==null?"0":map.get("ALL_ENTRY_QNT").toString());
							Double ENTRY_QNT = Double.valueOf(map.get("ENTRY_QNT")==null?"0":map.get("ENTRY_QNT").toString());
							Double POST_QTY = Double.valueOf(map.get("POST_QTY")==null?"0":map.get("POST_QTY").toString());

							if(ALL_ENTRY_QNT <=0) {
								map.put("JOB_FLAG", "02");
								map.put("POST_QTY", POST_QTY);
							}else {
								if(ENTRY_QNT+POST_QTY <ALL_ENTRY_QNT) {
									map.put("JOB_FLAG", "05");
									map.put("POST_QTY", POST_QTY+ENTRY_QNT);
								}else {
									map.put("JOB_FLAG", "02");
									map.put("POST_QTY", POST_QTY+ENTRY_QNT);
								}
							}
						}
					}
					//更新任务抬头信息
					commonDao.updateSapJobHead(sapPostJob);
					//更新任务行项目信息
					commonDao.updateSapJobItem(sapPostJobItemList);
				}

			}

		}

	}

	/**
	 * SAP物料凭证取消
	 * @param params WERKS：过账工厂  REF_WMS_NO：待冲销WMS凭证号  WMS_NO：新产生的WMS凭证号   DOC_YEAR：待冲销凭证年份 JZ_DATE：冲销过账日期
	 * matDocItemList-List<Map>：REF_WMS_ITEM_NO：WMS凭证行行项目号 WMS_ITEM_NO:新产生的WMS凭证行项目号
	 * WMS_SAP_MAT_DOC：WMS凭证行项目对应的SAP凭证信息
	 * @return 返回取消的物料凭证号
	 */
	public String sapGoodsMvtCancel(Map<String, Object> params) {
		String rtn = "";
		Map<String,Object> sapDoc = new HashMap<String,Object>();
		try {
			String WERKS = params.get("WERKS")==null?null:params.get("WERKS").toString();
			/**
			 * 待取消WMS凭证号
			 */
			String REF_WMS_NO = params.get("REF_WMS_NO")==null?null:params.get("REF_WMS_NO").toString();
			/**
			 * 冲销凭证新产生的WMS凭证号
			 */
			String WMS_NO = params.get("WMS_NO")==null?null:params.get("WMS_NO").toString();
			if(REF_WMS_NO == null) {
				throw new RuntimeException("需要取消的WMS凭证不能为空！");
			}
			/**
			 * 过账日期-记账日期
			 */
			String JZ_DATE = params.get("JZ_DATE").toString();
			List<Map<String, Object>> matDocItemList = params.get("matDocItemList")==null?null:(List<Map<String, Object>>)params.get("matDocItemList");
			if(matDocItemList==null||matDocItemList.size()<=0) {
				//无取消凭证行项目
				throw new RuntimeException("未勾选凭证行项目！");
			}

			/**
			 * WMS凭证行项目数据处理
			 */
			List<String> sapDocList = new ArrayList<>();
			Map<String,Object> matDocMap = new HashMap<String,Object>();
			for (Map<String, Object> matDocItem : matDocItemList) {
				//WMS凭证行项目关联的SAP物料凭证行项目
				String WMS_SAP_MAT_DOC = matDocItem.get("WMS_SAP_MAT_DOC")==null?null:matDocItem.get("WMS_SAP_MAT_DOC").toString();
				if(WMS_SAP_MAT_DOC!=null&&WMS_SAP_MAT_DOC.length()>0) {
					String[] sapDocArray = WMS_SAP_MAT_DOC.split(";");
					for (int i=sapDocArray.length-1;i>=0;i--) {
						String sapDocStr = sapDocArray[i];
						if(sapDocStr.length()>0) {
							String[] sapDocItemArray = sapDocStr.split(":");
							if(sapDocItemArray.length>0) {
								String matDoc = sapDocItemArray[0]; //SAP物料凭证号
								if(!sapDocList.contains(matDoc)) {
									sapDocList.add(matDoc);
								}

								String matItem = sapDocItemArray[1]; //SAP物料凭证行项目号
								Map matDocItemMap = new HashMap<String,String>();
								matDocItemMap.put("MATDOC_ITEM", matItem);
								matDocItemMap.put("WMS_ITEM_NO", matDocItem.get("WMS_ITEM_NO"));

								List<Map<String,String>> matItemList = matDocMap.get(matDoc)==null?null:(List<Map<String,String>>)matDocMap.get(matDoc);
								if(matItemList==null) {
									matItemList = new ArrayList<Map<String,String>>();
									Map refWmsItemMap = new HashMap<String,String>();
									refWmsItemMap.put("REF_WMS_ITEM_NO", matDocItem.get("REF_WMS_ITEM_NO")==null?null:matDocItem.get("REF_WMS_ITEM_NO").toString());
									matItemList.add(refWmsItemMap);

									matItemList.add(matDocItemMap);
								}else {
									matItemList.add(matDocItemMap);
								}

								matDocMap.put(matDoc, matItemList);
							}
						}

					}
				}

			}
			for (String matDoc : sapDocList) {
				System.out.println("Key = " + matDoc + ", Value = " + matDocMap.get(matDoc));
				List<Map<String,String>> ITEMLIST = (List<Map<String,String>>)matDocMap.get(matDoc);
				String REF_WMS_ITEM_NO = ITEMLIST.get(0).get("REF_WMS_ITEM_NO");
				Iterator<Map<String,String>> it =ITEMLIST.iterator();
				it.next();
				it.remove();
				params.put("MAT_DOC", matDoc);
				params.put("JZ_DATE", JZ_DATE.replaceAll("-", ""));
				params.put("ITEMLIST", ITEMLIST);
				//取消凭证过账
				sapDoc = wmsSapRemote.getSapBapiGoodsmvtCancel(params);

				if("0".equals(sapDoc.get("CODE"))) {
					/**
					 * 获取产生的取消凭证信息
					 */
					Map<String,Object> rm = wmsSapRemote.getSapBapiGoodsmvtDetail(sapDoc.get("MATERIALDOCUMENT").toString(),sapDoc.get("MATDOCUMENTYEAR").toString());
					Map<String,String> head=(Map<String, String>) rm.get("GOODSMVT_HEADER");
					List<Map<String,String>> itemList =(List<Map<String, String>>) rm.get("GOODSMVT_ITEMS");
					//删除掉SAP凭证自动创建的行项目
					Iterator<Map<String,String>> sapItemIt =itemList.iterator();
					while(sapItemIt.hasNext()) {
						Map<String,String> sapItemMap =sapItemIt.next();
						if(sapItemMap.get("X_AUTO_CRE")!=null && "X".equals(sapItemMap.get("X_AUTO_CRE"))) {
							sapItemIt.remove();
						}
					}

					List<Map<String,String>> sap_head_list = new ArrayList<Map<String,String>>();
					List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
					List<Map<String,String>> updateWmsdocList = new ArrayList<Map<String,String>>();//更新WMS凭证行项目数据


					if(head!=null) {

						head.put("REF_WMS_NO", WMS_NO);
						head.put("REF_DOC_NO", matDoc);
						head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));

						sap_head_list.add(head);
					}
					List<String> indexList = new ArrayList<String>();
					for(int i=0;i<itemList.size();i++) {
						Map<String,String> im = itemList.get(i);
						im.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
						sap_detail_list.add(im);

						if(im.get("PLANT").equals(WERKS)) {
							indexList.add(i+"");
						}

						Map<String,String> updateWmsdoc = new HashMap<String,String>();
						updateWmsdoc.put("WMS_NO", WMS_NO);
						updateWmsdoc.put("WMS_SAP_MAT_DOC", itemList.get(i).get("MAT_DOC")+":"+itemList.get(i).get("MATDOC_ITM")+";");

						updateWmsdocList.add(updateWmsdoc);
					}

					for(int x=0;x<indexList.size();x++) {
						int index = Integer.valueOf(indexList.get(x).toString());
						Map<String,String> sap_detail = sap_detail_list.get(index);
						Map<String,String> updateWmsdoc = updateWmsdocList.get(index);

						Map<String,String> ITEM = ITEMLIST.get(x);

						sap_detail.put("REF_WMS_ITEM_NO", REF_WMS_ITEM_NO);
						sap_detail.put("REF_MATDOC_ITM", ITEM.get("MATDOC_ITM")==null?null:String.valueOf(ITEM.get("MATDOC_ITM")));
						updateWmsdoc.put("WMS_ITEM_NO", ITEM.get("WMS_ITEM_NO")==null?null:String.valueOf(ITEM.get("WMS_ITEM_NO")));
					}

					rtn += sapDoc.get("MATERIALDOCUMENT").toString()+"("+itemList.get(0).get("MOVE_TYPE")+");";

					/**
					 * 更新WMS凭证行项目SAP物料凭证信息
					 */
					commonDao.updateWMSDocItemSapDocInfo(updateWmsdocList);

					commonDao.insertSapDocHead(sap_head_list);	//SAP凭证表-抬头
					commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细

				}else {
					//过账失败，抛出异常
					throw new RuntimeException("凭证取消过账失败："+sapDoc.get("MESSAGE")+";");
				}

			}

			/**
			 * 更新WMS凭证抬头SAP物料凭证信息
			 */
			Map<String,Object> wmsDocMap = new HashMap<String,Object>();
			wmsDocMap.put("MAT_DOC", rtn);
			wmsDocMap.put("WMS_NO", REF_WMS_NO);
			commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return rtn;
	}

	/**
	 * 保存WMS凭证记录抬头和明细
	 * @param head PZ_DATE：凭证日期  JZ_DATE：记账日期  HEADER_TXT：头文本 TYPE：凭证类型  SAP_MOVE_TYPE：SAP移动类型
	 *  行项目列表 itemList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String saveWMSDoc(Map<String,Object> head,List<Map<String,Object>> itemList) {
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
		wms_doc_head.put("OPERATION_TIME", head.get("OPERATION_TIME"));

		commonDao.insertWMSDocHead(wms_doc_head);

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
			mat.put("HANDLE_FTU", mat.get("HANDLE_FTU")==null?"":mat.get("HANDLE_FTU"));

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
				commonDao.insertWMSDocDetail(listPage);
				//剔除
				itemList.subList(0, pointsDataLimit).clear();
			}
			if(!itemList.isEmpty()){//表示最后剩下的数据
				commonDao.insertWMSDocDetail(itemList);
			}
		}else {
			commonDao.insertWMSDocDetail(itemList);
		}

		return WMS_NO;
	}

	/**
	 *保存WMS库存:根据工厂代码、仓库号、库位、储位代码、物料号、wms批次、特殊库存类型、供应商代码，判断新增库存记录，或修改库存数量
	 * @param params STOCK_TYPE 对应WMS_CORE_STOCK 需要更新的字段名称
	 */
	@Override
	@Transactional
	public void saveWmsStock(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String, Object>>) params.get("matList");
		String STOCK_TYPE = params.get("STOCK_TYPE")==null?"STOCK_QTY":params.get("STOCK_TYPE").toString();

		Map<String,Object> pMap = new HashMap<>();

		for(Map<String,Object> m:matList) {
			m.put("WERKS", m.get("WERKS")==null?"":m.get("WERKS"));
			m.put("WH_NUMBER", m.get("WH_NUMBER")==null?"":m.get("WH_NUMBER"));
			m.put("MATNR", m.get("MATNR")==null?"":m.get("MATNR"));
			m.put("MAKTX", m.get("MAKTX")==null?"":m.get("MAKTX"));
			m.put("LGORT", m.get("LGORT")==null?"":m.get("LGORT"));
			m.put("F_BATCH", m.get("F_BATCH")==null?"":m.get("F_BATCH"));
			m.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH"));
			m.put("BIN_CODE", m.get("BIN_CODE")==null?"":m.get("BIN_CODE"));
			m.put("BIN_NAME", m.get("BIN_NAME")==null?"":m.get("BIN_NAME"));

			String UNIT = m.get("UNIT")==null?"":m.get("UNIT").toString();//采购单位/入库单位
			String MEINS = m.get("MEINS")==null?"":m.get("MEINS").toString();//物料基本计量单位
			m.put("MEINS", MEINS);
			//20190722 peng.tao1将double改成bigdecimal
			BigDecimal RECEIPT_QTY = new BigDecimal(m.get("RECEIPT_QTY")!=null?m.get("RECEIPT_QTY").toString():
					m.get("STOCK_QTY")!=null?m.get("STOCK_QTY").toString():m.get("VIRTUAL_QTY")!=null?m.get("VIRTUAL_QTY").toString():
							m.get("LOCK_QTY")!=null?m.get("LOCK_QTY").toString():m.get("VIRTUAL_LOCK_QTY")!=null?m.get("VIRTUAL_LOCK_QTY").toString():
									m.get("FREEZE_QTY")!=null?m.get("FREEZE_QTY").toString():m.get("XJ_QTY")!=null?m.get("XJ_QTY").toString():"0");
			if(!UNIT.equals(MEINS)) {
				//采购单位与物料基本计量单位不同，需使用换算关系计算出 实际收货数量
				BigDecimal UMREZ = new BigDecimal(m.get("UMREZ")==null?"1":m.get("UMREZ").toString());//转换分子
				BigDecimal UMREN = new BigDecimal(m.get("UMREN")==null?"1":m.get("UMREN").toString());//转换分母
				//RECEIPT_QTY = (RECEIPT_QTY*UMREN)/UMREZ;
				RECEIPT_QTY=(RECEIPT_QTY.multiply(UMREN)).divide(UMREZ,3,BigDecimal.ROUND_DOWN).setScale(3,BigDecimal.ROUND_DOWN);
			}

			if(STOCK_TYPE.equals("VIRTUAL_QTY")) {
				m.put("VIRTUAL_QTY",RECEIPT_QTY);
				m.put("STOCK_QTY",0);
				m.put("LOCK_QTY",0);
				m.put("VIRTUAL_LOCK_QTY",0);
				m.put("FREEZE_QTY",0);
				m.put("XJ_QTY",0);

			}else if(STOCK_TYPE.equals("STOCK_QTY")) {
				m.put("STOCK_QTY",RECEIPT_QTY);
				m.put("VIRTUAL_QTY",0);
				m.put("LOCK_QTY",0);
				m.put("VIRTUAL_LOCK_QTY",0);
				m.put("FREEZE_QTY",0);
				m.put("XJ_QTY",0);
			}else if(STOCK_TYPE.equals("LOCK_QTY")) {
				m.put("STOCK_QTY",0);
				m.put("VIRTUAL_QTY",0);
				m.put("LOCK_QTY",RECEIPT_QTY);
				m.put("VIRTUAL_LOCK_QTY",0);
				m.put("FREEZE_QTY",0);
				m.put("XJ_QTY",0);
			}else if(STOCK_TYPE.equals("VIRTUAL_LOCK_QTY")) {
				m.put("STOCK_QTY",0);
				m.put("VIRTUAL_QTY",0);
				m.put("LOCK_QTY",0);
				m.put("VIRTUAL_LOCK_QTY",RECEIPT_QTY);
				m.put("FREEZE_QTY",0);
				m.put("XJ_QTY",0);
			}else if(STOCK_TYPE.equals("FREEZE_QTY")) {
				m.put("STOCK_QTY",0);
				m.put("VIRTUAL_QTY",0);
				m.put("LOCK_QTY",0);
				m.put("VIRTUAL_LOCK_QTY",0);
				m.put("FREEZE_QTY",RECEIPT_QTY);
				m.put("XJ_QTY",0);
			}else if(STOCK_TYPE.equals("XJ_QTY")) {
				m.put("STOCK_QTY",0);
				m.put("VIRTUAL_QTY",0);
				m.put("LOCK_QTY",0);
				m.put("VIRTUAL_LOCK_QTY",0);
				m.put("FREEZE_QTY",0);
				m.put("XJ_QTY",RECEIPT_QTY);
			}

			m.put("SOBKZ", m.get("SOBKZ")==null?"Z":m.get("SOBKZ"));
			m.put("LIFNR", m.get("LIFNR")==null?null:"".equals(m.get("LIFNR").toString().trim())?null:m.get("LIFNR").toString().trim());
			m.put("LIKTX", m.get("LIKTX")==null?null:m.get("LIKTX"));

			m.put("SO_NO", m.get("SO_NO")==null?null:m.get("SO_NO"));
			m.put("SO_ITEM_NO", m.get("SO_ITEM_NO")==null?null:m.get("SO_ITEM_NO"));

			m.put("EDITOR", m.get("EDITOR")==null?m.get("CREATOR"):m.get("EDITOR"));
			m.put("EDIT_DATE", m.get("EDIT_DATE")==null?m.get("CREATE_DATE"):m.get("EDIT_DATE"));

			String stockKey = m.get("WERKS")+"#*"+m.get("WH_NUMBER")+"#*"+m.get("LGORT")+"#*"+m.get("BIN_CODE")+"#*"+
					m.get("MATNR")+"#*"+m.get("BATCH")+"#*"+m.get("SOBKZ")+"#*";
			if(null != m.get("LIFNR")) {
				stockKey += m.get("LIFNR")+"#*";
			}
			if(null != m.get("SO_NO")) {
				stockKey += m.get("SO_NO")+"#*";
			}
			if(null != m.get("SO_ITEM_NO")) {
				stockKey += m.get("SO_ITEM_NO")+"#*";
			}

			//合并重复行
			Map<String,Object> stockMap = null;
			if(pMap.get(stockKey) !=null) {
				stockMap = (Map<String,Object>)pMap.get(stockKey);
				stockMap.put("STOCK_QTY",Double.valueOf(stockMap.get("STOCK_QTY").toString()) + Double.valueOf(m.get("STOCK_QTY").toString()));
				stockMap.put("VIRTUAL_QTY",Double.valueOf(stockMap.get("VIRTUAL_QTY").toString()) + Double.valueOf(m.get("VIRTUAL_QTY").toString()));
				stockMap.put("LOCK_QTY",Double.valueOf(stockMap.get("LOCK_QTY").toString()) + Double.valueOf(m.get("LOCK_QTY").toString()));
				stockMap.put("VIRTUAL_LOCK_QTY",Double.valueOf(stockMap.get("VIRTUAL_LOCK_QTY").toString()) + Double.valueOf(m.get("VIRTUAL_LOCK_QTY").toString()));
				stockMap.put("FREEZE_QTY",Double.valueOf(stockMap.get("FREEZE_QTY").toString()) + Double.valueOf(m.get("FREEZE_QTY").toString()));
				stockMap.put("XJ_QTY",Double.valueOf(stockMap.get("XJ_QTY").toString()) + Double.valueOf(m.get("XJ_QTY").toString()));
			}else {
				stockMap = m;
			}

			pMap.put(stockKey, stockMap);
		}
		List<Map<String,Object>> stockList = new ArrayList<>();
		for(Map.Entry<String, Object> entry : pMap.entrySet()){
		    Map<String,Object> mapValue = (Map<String,Object>)entry.getValue();
		    stockList.add(mapValue);
		}

		if(stockList.size()>0) {
			commonDao.updateWmsStock(stockList);
		}
	}

	/**
	 * 修改WMS库存信息
	 * 根据工厂代码、仓库号、库位、储位代码、物料号、wms批次、特殊库存类型、供应商代码，修改库存数量
	 * @param stockMatList 更新物料库存清单
	 */
	@Override
	@Transactional
	public void modifyWmsStock(List<Map<String,Object>> stockMatList) {
		commonDao.modifyWmsStock(stockMatList);
	}

	@Override
	public Map<String, Object> getPlantSetting(String WH_NUMBER) {

		return commonDao.getPlantSetting(WH_NUMBER);
	}

	@Override
	public List<Map<String, Object>> getDictList(String type) {
		return commonDao.getDictList(type);
	}

	/**
	 * SAP异步过账-创建后台过账任务
	 * @param sapSynMap WERKS：工厂、WMS_NO：WMS凭证号、JZ_DATE：记账日期、PZ_DATE：凭证日期、SAP_MOVE_TYPE：SAP过账移动类型、GM_CODE：
	 * @param matList 过账行项目
	 */
	private String asynPost(Map<String,Object> sapSynMap,List<Map<String,Object>> matList) {
		String WERKS = sapSynMap.get("WERKS").toString();
		String JZ_DATE = sapSynMap.get("JZ_DATE").toString();
		String PZ_DATE = sapSynMap.get("PZ_DATE").toString();
		String SAP_MOVE_TYPE = sapSynMap.get("SAP_MOVE_TYPE")==null?null:sapSynMap.get("SAP_MOVE_TYPE").toString();
		String GM_CODE = sapSynMap.get("GM_CODE")==null?null:sapSynMap.get("GM_CODE").toString();
		Object REF_WMS_NO = sapSynMap.get("WMS_NO")==null?(sapSynMap.get("REF_WMS_NO")==null?matList.get(0).get("WMS_NO"):sapSynMap.get("REF_WMS_NO")):sapSynMap.get("WMS_NO");
		Object REF_DOC_NO = sapSynMap.get("REF_DOC_NO")==null?(matList.get(0).get("REF_DOC_NO")==null?null:matList.get(0).get("WMS_NO")):sapSynMap.get("REF_DOC_NO");
		String LGORT_CONFIG = sapSynMap.get("LGORT_CONFIG")==null?null:sapSynMap.get("LGORT_CONFIG").toString();

		if(null!=SAP_MOVE_TYPE && !"".equals("SAP_MOVE_TYPE")) {
			//SAP过账任务抬头
			Map<String,Object> job_head=new HashMap<String,Object>();

			String SAP_JOB_NO=""; //SAP过账任务编号
			sapSynMap.put("WMS_DOC_TYPE", "10");//SAP过账任务编号
			Map<String,Object> doc=null;
			doc = wmsCDocNoService.getDocNo(sapSynMap);
			if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
				throw new RuntimeException(doc.get("MSG").toString());
			}
			SAP_JOB_NO=doc.get("docno").toString();
			job_head.put("SAP_JOB_NO", SAP_JOB_NO);
			job_head.put("WERKS", WERKS);
			job_head.put("REF_WMS_NO", REF_WMS_NO);
			job_head.put("REF_DOC_NO", REF_DOC_NO);
			job_head.put("PSTNG_DATE", JZ_DATE.replaceAll("-", ""));
			job_head.put("DOC_DATE", PZ_DATE.replaceAll("-", ""));
			job_head.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			job_head.put("HEADER_TXT", matList.get(0).get("HEADER_TXT"));
			job_head.put("JOB_FLAG", "01");//过账任务标识 01 未过账 02 已过账 03 过账失败 04 屏蔽过账
			job_head.put("LGORT_CONFIG", LGORT_CONFIG);
			job_head.put("DEL", "0");
			job_head.put("CREATOR", matList.get(0).get("CREATOR"));
			job_head.put("CREATE_DATE", matList.get(0).get("CREATE_DATE"));

			commonDao.insertSapJobHead(job_head);//保存SAP过账任务抬头信息

			//SAP过账任务明细
			List<Map<String,Object>> job_items=new ArrayList<Map<String,Object>>();
			int JOB_SEQ=1;
			int item_no = 1;
			for(Map<String,Object> m:matList) {
				Map<String,Object> _m=new HashMap<String,Object>();
				_m.put("SAP_JOB_NO", SAP_JOB_NO);
				_m.put("SAP_JOB_ITEM_NO", item_no);
				_m.put("REF_WMS_NO", REF_WMS_NO);
				_m.put("REF_WMS_ITEM_NO", m.get("WMS_ITEM_NO")==null?item_no:m.get("WMS_ITEM_NO"));
				String REF_SAP_MATDOC_NO = m.get("REF_SAP_MATDOC_NO")!=null?(m.get("REF_SAP_MATDOC_NO").equals("")?null:m.get("REF_SAP_MATDOC_NO").toString()):null;
				if(REF_SAP_MATDOC_NO!=null) {
					_m.put("REF_SAP_MATDOC_YEAR", m.get("REF_SAP_MATDOC_YEAR")==null?null:m.get("REF_SAP_MATDOC_YEAR").toString());
					_m.put("REF_SAP_MATDOC_NO", REF_SAP_MATDOC_NO);
					_m.put("REF_SAP_MATDOC_ITEM_NO", m.get("REF_SAP_MATDOC_ITEM_NO")==null?null:m.get("REF_SAP_MATDOC_ITEM_NO").toString());
				}
				_m.put("MATERIAL", m.get("MATNR").toString());//物料号
				_m.put("MATNR", m.get("MATNR").toString());//物料号 交货单过账使用
				_m.put("PLANT", m.get("MOVE_PLANT")==null ? WERKS : m.get("MOVE_PLANT"));//工厂代码
				_m.put("WERKS", WERKS);//工厂代码
				_m.put("STGE_LOC", m.get("STGE_LOC")==null ? m.get("LGORT") : m.get("STGE_LOC"));//库位
				_m.put("LGORT", (String)m.get("LGORT"));//库位
				_m.put("BATCH", (String)m.get("BATCH"));//WMS批次
				_m.put("CHARG", m.get("CHARG")==null?null:m.get("CHARG").toString());//交货批次 交货单过账使用
				_m.put("WMS_MOVE_TYPE", m.get("WMS_MOVE_TYPE"));
				_m.put("MOVE_TYPE", SAP_MOVE_TYPE);//SAP过账移动类型
				if(SAP_MOVE_TYPE.equals("411") || SAP_MOVE_TYPE.equals("412") || SAP_MOVE_TYPE.equals("311") || SAP_MOVE_TYPE.equals("312")) {
					GM_CODE= "04";
					_m.put("GM_CODE", GM_CODE);
				} else {
					_m.put("GM_CODE", GM_CODE);
				}
				String ENTRY_QNT = m.get("RECEIPT_QTY")==null?(m.get("LGMNG")==null?
						(m.get("ENTRY_QNT")==null?(m.get("QTY_SAP")==null?"0":m.get("QTY_SAP").toString())
								:m.get("ENTRY_QNT").toString()):m.get("LGMNG").toString()):m.get("RECEIPT_QTY").toString();
				_m.put("ENTRY_QNT", ENTRY_QNT);//收货数量
				_m.put("LFIMG", ENTRY_QNT);//实际交货数量 交货单过账使用
				_m.put("UMREZ", m.get("UMREZ")==null?"1":m.get("UMREZ"));//实际交货数量 交货单过账使用
				_m.put("UMREN", m.get("UMREN")==null?"1":m.get("UMREN"));//实际交货数量 交货单过账使用
				String ENTRY_UOM = m.get("ENTRY_UOM")==null?(m.get("UNIT")==null?"PCS":m.get("UNIT").toString()):m.get("ENTRY_UOM").toString();
				_m.put("ENTRY_UOM", ENTRY_UOM);//单位
				_m.put("MEINS", m.get("MEINS")==null?null:m.get("MEINS").toString());
				_m.put("MEINS_QNT", m.get("MEINS_QNT")==null?null:m.get("MEINS_QNT").toString());
				String PO_NUMBER = m.get("PO_NO")==null?(m.get("PO_NUMBER")==null?null:m.get("PO_NUMBER").toString()):m.get("PO_NO").toString();
				_m.put("PO_NUMBER",PO_NUMBER);//采购订单号
				String PO_ITEM = m.get("PO_ITEM_NO")==null?(m.get("PO_ITEM")==null?null:m.get("PO_ITEM").toString()):m.get("PO_ITEM_NO").toString();
				_m.put("PO_ITEM", PO_ITEM);//采购订单行项目号
				_m.put("VENDOR", m.get("VENDOR")==null?(m.get("LIFNR")==null?null:m.get("LIFNR")):m.get("VENDOR").toString());//供应商
				_m.put("CUSTOMER", m.get("CUSTOMER")==null?null:m.get("CUSTOMER").toString());//客户代码
				_m.put("COSTCENTER", m.get("COSTCENTER")==null?(m.get("COST_CENTER")==null?null:m.get("COST_CENTER")):m.get("COSTCENTER").toString());//成本中心
				_m.put("WBS_ELEM", m.get("WBS_ELEM")==null?(m.get("WBS")==null?null:m.get("WBS")):m.get("WBS_ELEM").toString());//WBS元素
				_m.put("GR_RCPT", m.get("GR_RCPT")==null?(m.get("RECEIVER")==null?"":m.get("RECEIVER")):m.get("GR_RCPT").toString());//收货方/运达方

				String ORDERID = null;
				if(m.get("ORDERID")!=null && !m.get("ORDERID").toString().trim().equals("")) {
					ORDERID = m.get("ORDERID").toString();
				}else if(m.get("MO_NO")!=null && !m.get("MO_NO").toString().trim().equals("")) {
					ORDERID = m.get("MO_NO").toString();
				}else if(m.get("IO_NO")!=null && !m.get("IO_NO").toString().trim().equals("")) {
					ORDERID = m.get("IO_NO").toString();
				}
				_m.put("ORDERID", ORDERID);//内部/生产订单号
				String ORDER_ITNO = m.get("MO_ITEM_NO")==null?(m.get("POSNR")==null?null:m.get("POSNR").toString()):m.get("MO_ITEM_NO").toString();
				_m.put("ORDER_ITNO", ORDER_ITNO);//生产订单行项目号
				_m.put("RESERV_NO", m.get("RESERV_NO")==null?(m.get("RSNUM")==null?null:m.get("RSNUM")):m.get("RESERV_NO").toString());//预留号
				_m.put("RES_ITEM", m.get("RES_ITEM")==null?(m.get("RSPOS")==null?null:m.get("RSPOS")):m.get("RES_ITEM").toString());//预留行项目号

				String VBELN = m.get("VBELN")==null?(m.get("SAP_OUT_NO")==null?null:m.get("SAP_OUT_NO").toString()):m.get("VBELN").toString();//交货单号
				String POSNR_VL = m.get("POSNR_VL")==null?(m.get("SAP_OUT_ITEM_NO")==null?null:m.get("SAP_OUT_ITEM_NO").toString()):m.get("POSNR_VL").toString();
				_m.put("VBELN_VL", VBELN);//交货单号
				_m.put("POSNR_VL", POSNR_VL);//交货单行项目号
				_m.put("VBELN", VBELN);//交货单号
				_m.put("POSNN", POSNR_VL);//交货过账使用 后续凭证行项目号（6） 可填
				_m.put("DELIVERY", VBELN);//交货单号
				_m.put("WADAT_IST", JZ_DATE);//实际货物移动日期  交货单过账使用

				_m.put("MOVE_BATCH", m.get("MOVE_BATCH")==null?null:m.get("MOVE_BATCH").toString());
				//MB1B移动类型，需要接收工厂
				_m.put("MOVE_PLANT", m.get("MOVE_PLANT")==null ? m.get("PLANT")!=null && GM_CODE.equals("04")?m.get("PLANT").toString():null : m.get("MOVE_PLANT").toString());
				_m.put("MOVE_MAT", m.get("MOVE_MAT")==null?null:m.get("MOVE_MAT").toString());
				//MB1B移动类型，需要接收库位
				_m.put("MOVE_STLOC", m.get("MOVE_STLOC")==null ? m.get("STGE_LOC")!=null && GM_CODE.equals("04")?m.get("STGE_LOC").toString():null : m.get("MOVE_STLOC").toString());

				_m.put("ITEM_TEXT",(String)m.get("ITEM_TEXT") );//行文本
				_m.put("JOB_SEQ", JOB_SEQ);
				_m.put("CREATOR", m.get("CREATOR"));
				_m.put("CREATE_DATE", m.get("CREATE_DATE"));
				_m.put("JOB_FLAG", "01");//过账任务标识 01 未过账 02 已过账 03 过账失败 04 屏蔽过账
				_m.put("POST_QTY", 0);
				_m.put("WMS_MOVE_TYPE", m.get("WMS_MOVE_TYPE"));
				_m.put("PARENT_JOB_ITEM_NO", "");

				job_items.add(_m);

				//2019-07-03 add by thw 处理105过账委外PO组件物料消耗
				List<Map<String,Object>> poComonentList = m.get("poComonentList")==null?null:(List<Map<String,Object>>)m.get("poComonentList");
				if(poComonentList != null && poComonentList.size()>0) {
					//委外PO且存在需要消耗的组件数据
					for (Map<String, Object> poComonentMap : poComonentList) {
						poComonentMap.put("MATERIAL", poComonentMap.get("MATN2").toString());
						poComonentMap.put("PLANT", poComonentMap.get("WERKS").toString());
						poComonentMap.put("BATCH", poComonentMap.get("BATCH")==null?"":poComonentMap.get("BATCH").toString());
						poComonentMap.put("MOVE_TYPE", poComonentMap.get("SAP_MOVE_TYPE").toString());
						poComonentMap.put("SPEC_STOCK", "O");
						poComonentMap.put("VENDOR", poComonentMap.get("VENDOR")==null?(poComonentMap.get("LIFNR")==null?"":poComonentMap.get("LIFNR").toString()):poComonentMap.get("VENDOR").toString());//供应商
						poComonentMap.put("ENTRY_QNT", poComonentMap.get("QTY_SAP")==null?"0":poComonentMap.get("QTY_SAP").toString());
						poComonentMap.put("ENTRY_UOM", poComonentMap.get("UNIT")==null?"":poComonentMap.get("UNIT").toString());
						poComonentMap.put("PO_NUMBER",PO_NUMBER);//采购订单号
						poComonentMap.put("PO_ITEM", PO_ITEM);//采购订单行项目号

						poComonentMap.put("PARENT_JOB_ITEM_NO", _m.get("SAP_JOB_ITEM_NO"));
						poComonentMap.put("JOB_SEQ", JOB_SEQ);
						poComonentMap.put("CREATOR", m.get("CREATOR"));
						poComonentMap.put("CREATE_DATE", m.get("CREATE_DATE"));
						poComonentMap.put("JOB_FLAG", "01");//过账任务标识 01 未过账 02 已过账 03 过账失败 04 屏蔽过账
						poComonentMap.put("POST_QTY", 0);
						//20190821 peng.tao1 update 改从poComonentMap中取凭证号和行项目号
						/*poComonentMap.put("REF_WMS_NO", REF_WMS_NO);
						poComonentMap.put("REF_WMS_ITEM_NO", m.get("WMS_ITEM_NO")==null?item_no:m.get("WMS_ITEM_NO"));*/
						poComonentMap.put("REF_WMS_NO", poComonentMap.get("WMS_NO")==null?"":poComonentMap.get("WMS_NO").toString());
						poComonentMap.put("REF_WMS_ITEM_NO", poComonentMap.get("WMS_ITEM_NO")==null?"":poComonentMap.get("WMS_ITEM_NO").toString());

						job_items.add(poComonentMap);
					}

				}

				item_no++;
			}

			commonDao.insertSapJobItems(job_items);//保存SAP过账任务明细信息
		}
		return "后台过账任务创建成功！";
	}

	/**
	 * SAP实时过账
	 * @param head 过账抬头
	 * @param matList 过账行项目
	 * @return
	 */
	private String sapPost(Map<String,Object> sapSynMap,List<Map<String,Object>> matList,List<Map<String,Object>> sap_success_list) {
		String  SAP_NO = "";
//		List<Map<String,Object>> sap_success_list=new ArrayList<Map<String,Object>>();
		List<String> exception_list=new ArrayList<String>();
		String WERKS = sapSynMap.get("WERKS")==null?(matList.get(0).get("WERKS")==null?null:matList.get(0).get("WERKS").toString()):sapSynMap.get("WERKS").toString();
		String PZ_DATE = sapSynMap.get("PZ_DATE")==null?sapSynMap.get("DOC_DATE")==null?"":sapSynMap.get("DOC_DATE").toString():sapSynMap.get("PZ_DATE").toString();
		Object REF_WMS_NO = sapSynMap.get("WMS_NO")==null?(sapSynMap.get("REF_WMS_NO")==null?matList.get(0).get("WMS_NO"):sapSynMap.get("REF_WMS_NO")):sapSynMap.get("WMS_NO");
		Object REF_DOC_NO = sapSynMap.get("REF_DOC_NO")==null?(matList.get(0).get("REF_DOC_NO")==null?null:matList.get(0).get("WMS_NO")):sapSynMap.get("REF_DOC_NO");
		//处理头文本为空问题
		if(sapSynMap.get("HEADER_TXT")==null) {
			sapSynMap.put("HEADER_TXT", (String)matList.get(0).get("HEADER_TXT"));
		}

		String GM_CODE = null;
		String PRE_MATDOCUMENTYEAR = null;
		String PRE_MATERIALDOCUMENT = null;
		String LGORT_CONFIG= null;//库存地点
//		for(String MOVE_TYPE: sapSynMap.get("SAP_MOVE_TYPE").toString().split("\\+")) {
		//考虑到自有和寄售同时存在的情况，此处改为取行里的移动类型才正确。 update by renwei
		String SAP_MOVE_TYPE = matList.get(0).get("SAP_MOVE_TYPE")==null?(matList.get(0).get("MOVE_TYPE").toString()):matList.get(0).get("SAP_MOVE_TYPE").toString();
		for(String MOVE_TYPE: SAP_MOVE_TYPE.split("#")) {
			String[] stringArray = MOVE_TYPE.split("\\_");
			String SOBKZ_CONFIG = null;

			if(stringArray.length>1) {
				//特殊库存类型过账移动类型
				SOBKZ_CONFIG = stringArray[1];
			}
			LGORT_CONFIG = sapSynMap.get("LGORT_CONFIG") == null ? null:sapSynMap.get("LGORT_CONFIG").toString();
			MOVE_TYPE = stringArray[0];
			List<Map<String,String>> ITEMLIST = new ArrayList<Map<String,String>>();

			//获取行项目单位换算信息
			List<String> matUnitList = commonDao.getMatUnit(matList);
			//单位替换
			for (String string : matUnitList) {
				String[] unitArr = string.split("\\#\\*");
				int index = Integer.valueOf(unitArr[0]);
				String  UNIT_EN = unitArr[1];
				if(matList.get(index) !=null) {
					matList.get(index).put("ENTRY_UOM", UNIT_EN);
				}
			}

			//获取物料是否启用批次
			List<String> matBatchFlagList = commonDao.getMatBatchFlag(matList);
			for (String string : matBatchFlagList) {
				String[] batchArr = string.split("\\#\\*");
				int index = Integer.valueOf(batchArr[0]);
				String  XCHPF = batchArr[1];
				if(StringUtils.isEmpty(XCHPF)) {
					XCHPF = "0";
				}
				if(matList.get(index) !=null) {
					matList.get(index).put("XCHPF", XCHPF);
				}
			}

			int i = 1;
			int LINE_ID = 1; //处理105过账委外PO组件物料消耗
			for (Map<String, Object> m : matList) {
				String smovetype = m.get("SAP_MOVE_TYPE")==null?"":m.get("SAP_MOVE_TYPE").toString();
				if(smovetype.equals("311_K")) {
					SOBKZ_CONFIG = "K";
				} else if(smovetype.equals("311")) {
					SOBKZ_CONFIG = null;
				}

				Map<String,String> _m=new HashMap<String,String>();
				_m.put("XCHPF", m.get("XCHPF")==null?"0":m.get("XCHPF").toString());

				_m.put("WMS_NO", m.get("WMS_NO")==null?"":m.get("WMS_NO").toString());
				_m.put("WMS_ITEM_NO", m.get("WMS_ITEM_NO")==null?"":m.get("WMS_ITEM_NO").toString());
				if(MOVE_TYPE.equals("411") || MOVE_TYPE.equals("412") || MOVE_TYPE.equals("311") || MOVE_TYPE.equals("312")) {
					GM_CODE= "04";
				} else {
					GM_CODE = sapSynMap.get("GM_CODE")==null?(m.get("GM_CODE")==null?"":m.get("GM_CODE").toString()):sapSynMap.get("GM_CODE").toString();
				}

				_m.put("GM_CODE", GM_CODE);
				_m.put("MATERIAL", m.get("MATNR").toString());//物料号
				_m.put("MATNR", m.get("MATNR").toString());//物料号
				_m.put("PLANT", WERKS);//工厂代码
				_m.put("WERKS", WERKS);//工厂代码
				_m.put("F_WERKS", m.get("F_WERKS")==null?"":m.get("F_WERKS").toString());//工厂代码
				_m.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH").toString());//WMS批次
				//2019-10-17处理发货方未启用批次 收货方启用批次业务，WMS收货产生的批次需传入收货方101移动类型里  m.get("BATCH")==null?"":m.get("BATCH").toString()
				_m.put("CHARG", m.get("CHARG")==null?"":m.get("CHARG").toString());//交货批次 交货单过账使用
				_m.put("MOVE_TYPE", MOVE_TYPE);//SAP过账移动类型


				if(SOBKZ_CONFIG!=null && ("K".equals(SOBKZ_CONFIG) || "E".equals(SOBKZ_CONFIG) || "O".equals(SOBKZ_CONFIG) )) {
					_m.put("SPEC_STOCK",SOBKZ_CONFIG);//特殊库存标识
				}
				_m.put("STGE_LOC", (String)m.get("LGORT"));//库位
				_m.put("LGORT", (String)m.get("LGORT"));//库位

				Double ENTRY_QNT = Double.parseDouble(m.get("RECEIPT_QTY")==null?(
						(m.get("ENTRY_QNT")==null?(m.get("QTY_SAP")==null?"0":m.get("QTY_SAP").toString())
								:m.get("ENTRY_QNT").toString())):m.get("RECEIPT_QTY").toString());

				Double POST_QTY = Double.parseDouble(m.get("POST_QTY")==null?"0":m.get("POST_QTY").toString());

				if(POST_QTY>0) {
					ENTRY_QNT = ENTRY_QNT-POST_QTY;
				}

				_m.put("ENTRY_QNT", String.format("%.3f", ENTRY_QNT));//收货数量

				_m.put("LFIMG", String.format("%.3f", ENTRY_QNT));//实际交货数量  交货单过账使用
				_m.put("UMREZ", m.get("UMREZ")==null?"1":m.get("UMREZ").toString());//实际交货数量 交货单过账使用
				_m.put("UMREN", m.get("UMREN")==null?"1":m.get("UMREN").toString());//实际交货数量 交货单过账使用
				String ENTRY_UOM = m.get("ENTRY_UOM")==null?(m.get("UNIT")==null?"PCS":m.get("UNIT").toString()):m.get("ENTRY_UOM").toString();
				_m.put("ENTRY_UOM", ENTRY_UOM);//单位

				String PO_NUMBER = m.get("PO_NO")==null?(m.get("PO_NUMBER")==null?(m.get("EBELN")==null?"":m.get("EBELN").toString()):m.get("PO_NUMBER").toString()):m.get("PO_NO").toString();
				String PO_ITEM = m.get("PO_ITEM_NO")==null?(m.get("PO_ITEM")==null?"":m.get("PO_ITEM").toString()):m.get("PO_ITEM_NO").toString();
				if (!MOVE_TYPE.equals("311")&&!MOVE_TYPE.equals("312")&&!MOVE_TYPE.equals("411")&&!MOVE_TYPE.equals("412")) {
					_m.put("PO_NUMBER",PO_NUMBER);//采购订单号
					_m.put("PO_ITEM", PO_ITEM);//采购订单行项目号
				}

				_m.put("VENDOR", m.get("VENDOR")==null?(m.get("LIFNR")==null?"":m.get("LIFNR").toString()):m.get("VENDOR").toString());//供应商
				_m.put("CUSTOMER", m.get("CUSTOMER")==null?"":m.get("CUSTOMER").toString());//客户代码
				_m.put("COSTCENTER", m.get("COSTCENTER")==null?(m.get("COST_CENTER")==null?"":m.get("COST_CENTER").toString()):m.get("COSTCENTER").toString());//成本中心
				_m.put("WBS_ELEM", m.get("WBS_ELEM")==null?(m.get("WBS")==null?"":m.get("WBS").toString()):m.get("WBS_ELEM").toString());//WBS元素
				_m.put("GR_RCPT", m.get("GR_RCPT")==null?(m.get("RECEIVER")==null?"":m.get("RECEIVER").toString()):m.get("GR_RCPT").toString());//收货方/运达方
				//String ORDERID = m.get("ORDERID")==null?(m.get("MO_NO")==null?(m.get("IO_NO")==null?"":m.get("IO_NO").toString()):m.get("MO_NO").toString()):m.get("ORDERID").toString();
				String ORDERID = "";
				if(m.get("ORDERID")!=null && !m.get("ORDERID").equals("")) {
					ORDERID = m.get("ORDERID").toString();
				}else if(m.get("MO_NO")!=null && !m.get("MO_NO").equals("")) {
					ORDERID = m.get("MO_NO").toString();
				}else if(m.get("IO_NO")!=null && !m.get("IO_NO").equals("")) {
					ORDERID = m.get("IO_NO").toString();
				}
				if(m.get("BUSINESS_TYPE")!=null && ("10".equals(m.get("BUSINESS_TYPE").toString())
						|| "11".equals(m.get("BUSINESS_TYPE").toString()) || "12".equals(m.get("BUSINESS_TYPE").toString()) ) ){
					_m.put("IO_NO_FLAG", "0");
				}
				if(m.get("BUSINESS_TYPE")!=null && "13".equals(m.get("BUSINESS_TYPE").toString())) {
					//AUTYP-订单类别值为 01：内部订单 值为04：CO订单
					if(m.get("AUTYP") !=null && "01".equals(m.get("AUTYP").toString()) ) {
						_m.put("IO_NO_FLAG", "X");
					}else {
						_m.put("IO_NO_FLAG", "0");
					}
				}

				//去除左侧0
				ORDERID = ORDERID.replaceAll("^(0+)", "");

				if (!MOVE_TYPE.equals("411") && !MOVE_TYPE.equals("412") && !MOVE_TYPE.equals("311") && !MOVE_TYPE.equals("312")) {
					_m.put("ORDERID", ORDERID);//内部/生产订单号
					String RESERV_NO = m.get("RESERV_NO")==null?(m.get("RSNUM")==null?"":m.get("RSNUM").toString()):m.get("RESERV_NO").toString();
					_m.put("RESERV_NO", RESERV_NO);//预留号
					_m.put("RES_ITEM", m.get("RES_ITEM")==null?(m.get("RSPOS")==null?"":m.get("RSPOS").toString()):m.get("RES_ITEM").toString());//预留行项目号
					if(!StringUtils.isEmpty(RESERV_NO)&&MOVE_TYPE.equals("262")) {
						_m.put("XSTOB", "X");
					}
				}

				String ORDER_ITNO = m.get("MO_ITEM_NO")==null?(m.get("POSNR")==null?"":m.get("POSNR").toString()):m.get("MO_ITEM_NO").toString();
				//261、262不传订单行
				if (!MOVE_TYPE.equals("261") && !MOVE_TYPE.equals("262") && !MOVE_TYPE.equals("411"))
					_m.put("ORDER_ITNO", ORDER_ITNO);//生产订单行项目号

				String VBELN = m.get("VBELN")==null?(m.get("SAP_OUT_NO")==null?(m.get("VBELN_VL")==null?"":m.get("VBELN_VL").toString()):m.get("SAP_OUT_NO").toString()):m.get("VBELN").toString();//交货单号
				String POSNR_VL = m.get("POSNR_VL")==null?(m.get("SAP_OUT_ITEM_NO")==null?"":m.get("SAP_OUT_ITEM_NO").toString()):m.get("POSNR_VL").toString();
				_m.put("VBELN_VL", VBELN);//交货单号
				_m.put("POSNR_VL", POSNR_VL);//交货单行项目号
				_m.put("VBELN", VBELN);//交货单号
				_m.put("POSNN", POSNR_VL);//交货过账使用 后续凭证行项目号（6） 可填
				_m.put("DELIVERY", VBELN);//交货单号

				_m.put("VGBEL", m.get("VGBEL")==null?null:m.get("VGBEL").toString());//交货单关联的采购订单号
				_m.put("VGPOS", m.get("VGPOS")==null?null:m.get("VGPOS").toString());//交货单关联的采购订单行项目号

				_m.put("ITEM_TEXT",(String)m.get("ITEM_TEXT") );//行文本

				String MVT_IND = null; //移动标识
				if(null != PO_NUMBER && !"".equals(PO_NUMBER)) {
					if(!MOVE_TYPE.equals("351") && !MOVE_TYPE.equals("352") && !MOVE_TYPE.equals("601") && !MOVE_TYPE.equals("645")
							&& !MOVE_TYPE.equals("657") && !MOVE_TYPE.equals("675") && !MOVE_TYPE.equals("311") && !MOVE_TYPE.equals("312") && !MOVE_TYPE.equals("411") && !MOVE_TYPE.equals("412")) {
						MVT_IND = "B"; //采购订单过账
					}
				}
				if(null != ORDERID && !"".equals(ORDERID) && GM_CODE.equals("02")) {
					MVT_IND = "F"; //生产订单过账
				}
				if(MOVE_TYPE.equals("541")) {
					//分包发料
//					MVT_IND = "O";
					MVT_IND = "";
					_m.put("VENDOR",m.get("CUSTOMER")==null?"":m.get("CUSTOMER").toString()); //541需求类型，委外单位放在需求行表 客户字段上
				}
				if(MOVE_TYPE.equals("531")) {
					//副产品
					MVT_IND = null;
					_m.put("ORDER_ITNO", null);//生产订单行项目号
				}
				if(null !=MVT_IND) {
					_m.put("MVT_IND", MVT_IND);//移动标识
				}
				if(null != VBELN && !"".equals(VBELN) && (MOVE_TYPE.equals("601") || MOVE_TYPE.equals("645") || MOVE_TYPE.equals("657")|| MOVE_TYPE.equals("675") )) {
					_m.put("deliveryFlag", "Y");
				}

				Map<String,Object> sapMoveReas = commonDao.getSapMoveReasByMoveType(MOVE_TYPE);
				String MOVE_REAS = sapMoveReas == null?"":sapMoveReas.get("MOVE_REAS")==null?"":sapMoveReas.get("MOVE_REAS").toString();
				_m.put("MOVE_REAS", MOVE_REAS);

				_m.put("MOVE_BATCH", m.get("MOVE_BATCH")==null?null:m.get("MOVE_BATCH").toString());
				//MB1B移动类型，需要接收工厂
				_m.put("MOVE_PLANT", m.get("MOVE_PLANT")==null ? m.get("PLANT")!=null && GM_CODE.equals("04")?m.get("PLANT").toString():null : m.get("MOVE_PLANT").toString());
				_m.put("MOVE_MAT", m.get("MOVE_MAT")==null?null:m.get("MOVE_MAT").toString());
				//MB1B移动类型，需要接收库位
				String MOVE_STLOC = m.get("MOVE_STLOC")==null ? null : m.get("MOVE_STLOC").toString();
				_m.put("MOVE_STLOC", MOVE_STLOC);

				if(MOVE_TYPE.equals("411") || MOVE_TYPE.equals("412")) {
					_m.put("MOVE_PLANT", "");
					if(LGORT_CONFIG!=null)
						_m.put("MOVE_STLOC", LGORT_CONFIG);//使用配置的中转库库位
					else
						_m.put("MOVE_STLOC", _m.get("STGE_LOC"));//默认使用原库位， 不能用m.get("MOVE_STLOC")，因为这个存的是第二步过账的接收库位。当411不配置中转库位时，库位不变，只做库存类型的转换
				}

				if(MOVE_TYPE.equals("311")||MOVE_TYPE.equals("312")) {
					_m.put("MOVE_PLANT", "");
//					if(LGORT_CONFIG!=null)
//						_m.put("MOVE_STLOC", LGORT_CONFIG);//使用配置的中转库库位
//					else
						_m.put("MOVE_STLOC", m.get("MOVE_STLOC")==null ? _m.get("STGE_LOC") : m.get("MOVE_STLOC").toString());//默认使用原库位
				}

				//判断是否两步过账，411_K#201(等) 如果配置了中转库位（LGORT_CONFIG），做411K时库存是转入中转库位的， 所以做第二步201时，发出库位应为中转库位，MOVE_STLOC为需求行上的接收库位。
				String[] moveTypeArray = SAP_MOVE_TYPE.split("#");
				if(moveTypeArray.length > 1 && LGORT_CONFIG!=null && (moveTypeArray[1].equals(MOVE_TYPE) || SAP_MOVE_TYPE.contains(MOVE_TYPE+"#"))) {
					_m.put("STGE_LOC", LGORT_CONFIG);//库位
					_m.put("LGORT", LGORT_CONFIG);//库位

					_m.put("MOVE_STLOC", MOVE_STLOC);
				}

				if(MOVE_TYPE.equals("303")||MOVE_TYPE.equals("304")) {
					_m.put("MOVE_STLOC", null);
				}

				if("Y".equals(_m.get("deliveryFlag")) && !MOVE_TYPE.equals("601")) {
					//交货单过账，库位固定为00ZT
					_m.put("STGE_LOC", "00ZT");//库位
					_m.put("LGORT", "00ZT");//库位
				}

				String REF_SAP_MATDOC_NO = m.get("REF_SAP_MATDOC_NO")!=null?(m.get("REF_SAP_MATDOC_NO").equals("")?"":m.get("REF_SAP_MATDOC_NO").toString()):null;
				if(REF_SAP_MATDOC_NO!=null) {
					_m.put("REF_DOC_YR", m.get("REF_SAP_MATDOC_YEAR")==null?"":m.get("REF_SAP_MATDOC_YEAR").toString());
					_m.put("REF_DOC", REF_SAP_MATDOC_NO);
					_m.put("REF_DOC_IT", m.get("REF_SAP_MATDOC_ITEM_NO")==null?"":m.get("REF_SAP_MATDOC_ITEM_NO").toString());
				}

				if(PRE_MATDOCUMENTYEAR!=null) {
					_m.put("REF_DOC_YR", PRE_MATDOCUMENTYEAR);
					_m.put("REF_DOC", PRE_MATERIALDOCUMENT);
					_m.put("REF_DOC_IT", i+"");
				}

				//2019-07-03 add by thw 处理105过账委外PO组件物料消耗
				List<Map<String,Object>> poComonentList = m.get("poComonentList")==null?null:(List<Map<String,Object>>)m.get("poComonentList");
				if(poComonentList != null && poComonentList.size()>0) {
					//委外PO且存在需要消耗的组件数据
					LINE_ID++;
					_m.put("LINE_ID", LINE_ID+"");
					for (Map<String, Object> poComonentMap : poComonentList) {
						Map<String,String> _p=new HashMap<String,String>();
						_p.put("WMS_NO", poComonentMap.get("WMS_NO")==null?"":poComonentMap.get("WMS_NO").toString());//peng.tao1 20190820 update
						_p.put("WMS_ITEM_NO", poComonentMap.get("WMS_ITEM_NO")==null?"":poComonentMap.get("WMS_ITEM_NO").toString());//peng.tao1 20190820 update
						_p.put("WERKS", WERKS);//工厂代码
						_p.put("MATNR", poComonentMap.get("MATN2").toString());
						_p.put("MATERIAL", poComonentMap.get("MATN2").toString());
						_p.put("PLANT", poComonentMap.get("WERKS").toString());
						_p.put("BATCH", poComonentMap.get("BATCH")==null?"":poComonentMap.get("BATCH").toString());
						_p.put("MOVE_TYPE", poComonentMap.get("SAP_MOVE_TYPE").toString());
						_p.put("SPEC_STOCK", "O");
						_p.put("VENDOR", poComonentMap.get("VENDOR")==null?(poComonentMap.get("LIFNR")==null?"":poComonentMap.get("LIFNR").toString()):poComonentMap.get("VENDOR").toString());//供应商
						_p.put("ENTRY_QNT", poComonentMap.get("QTY_SAP")==null?"0":poComonentMap.get("QTY_SAP").toString());
						_p.put("ENTRY_UOM", poComonentMap.get("UNIT")==null?"":poComonentMap.get("UNIT").toString());
						_p.put("PO_NUMBER",PO_NUMBER);//采购订单号
						_p.put("PO_ITEM", PO_ITEM);//采购订单行项目号
						_p.put("PARENT_ID", _m.get("LINE_ID"));

						ITEMLIST.add(_p);
					}

				}

				i++;
				if(ENTRY_QNT>0 || "Y".equals(_m.get("deliveryFlag"))) {
					ITEMLIST.add(_m);
				}

			}

			sapSynMap.put("ITEMLIST", ITEMLIST);

			Map<String,Object> sapDoc = new HashMap<String,Object>();
			try {
				if(ITEMLIST.size()>0) {
					if(null != ITEMLIST.get(0).get("deliveryFlag") && "Y".equals(ITEMLIST.get(0).get("deliveryFlag"))) {
						sapDoc = wmsSapRemote.getSapDeliveryUpdate(sapSynMap);
						String VBELN = ITEMLIST.get(0).get("VBELN")==null?(ITEMLIST.get(0).get("SAP_OUT_NO")==null?"":ITEMLIST.get(0).get("SAP_OUT_NO").toString()):ITEMLIST.get(0).get("VBELN").toString();//交货单号
						if("0".equals(sapDoc.get("CODE"))) {
							//交货单过账，通过交货单获取物料凭证
							/**
							 * 通过调用SAP服务接口，读取SAP交货单数据
							 */
							Map<String,Object> dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(VBELN);
							//交货单关联的凭证信息 docList
							List<Map<String,Object>> dnDocList = (ArrayList<Map<String,Object>>)dnMap.get("docList");
							if(null != dnDocList && dnDocList.size()>0) {
								boolean flag = false;
								for (Map<String, Object> map : dnDocList) {
									/**
									 * 判断获取的凭证是否为物料凭证（凭证号不为空且移动类型不为空）
									 */
									if(map.get("VBELN")!=null && map.get("BWART")!=null && !"".equals(map.get("BWART").toString())) {
										//过账成功，获取到了物料凭证
										flag = true;
										sapDoc.put("MATERIALDOCUMENT", map.get("VBELN"));
										sapDoc.put("MATDOCUMENTYEAR", map.get("MJAHR"));
										break;
									}
								}
								if(!flag) {
									//过账失败
									sapDoc.put("CODE", "-2");
									sapDoc.put("MESSAGE", "交货单"+VBELN+"过账失败，未产生过账凭证！");
								}

							}else {
								//过账失败
								sapDoc.put("CODE", "-2");
								sapDoc.put("MESSAGE", "交货单"+VBELN+"过账失败，未产生过账凭证！");
							}
						}else {
							//交货单修改失败
							sapDoc.put("CODE", sapDoc.get("CODE"));
							sapDoc.put("MESSAGE", sapDoc.get("MESSAGE"));
						}

					}else {
						//其他移动类型过账
						sapDoc =	wmsSapRemote.getSapBapiGoodsmvtCreate(sapSynMap);
					}
				}else {
					//过账失败
					sapDoc.put("CODE", "-5");
					sapDoc.put("MESSAGE", "无过账行项目数据！");
				}

			}catch(Exception e) {
				if(sapSynMap.get("SAP_JOB_NO")!=null) {
					/*
					 * 更新过账任务状态为03,并更新过账任务的SAP过账失败原因
					 */
					sapSynMap.put("JOB_FLAG", "03");
					sapSynMap.put("ERROR", e.getMessage());
				}
				System.err.println("SAP接口调用异常>>>>>>>>>>>>移动类型："+MOVE_TYPE);
			}

			if("0".equals(sapDoc.get("CODE"))) {
				sapDoc.put("WERKS", WERKS);
				sapDoc.put("MOVE_TYPE", MOVE_TYPE);
				sapDoc.put("GOODSMVT_PSTNG_DATE", PZ_DATE.replaceAll("-", ""));
				sapDoc.put("ITEMLIST", ITEMLIST);
				sapDoc.put("REF_WMS_NO", REF_WMS_NO);
				sap_success_list.add(sapDoc);

				if(GM_CODE!=null &&GM_CODE.equals("01")) {
					//采购订单多步骤过账，需记录前一步凭证
					PRE_MATDOCUMENTYEAR = sapDoc.get("MATDOCUMENTYEAR").toString();
					PRE_MATERIALDOCUMENT = sapDoc.get("MATERIALDOCUMENT").toString();
				}

			}else {//过账失败，抛出异常，cancel掉已过账的记录
				if(sapSynMap.get("SAP_JOB_NO")!=null) {
					/*
					 * 更新过账任务状态为03,并更新过账任务的SAP过账失败原因
					 */
					sapSynMap.put("JOB_FLAG", "03");
					sapSynMap.put("ERROR", MOVE_TYPE+"过账失败："+sapDoc.get("MESSAGE")+";");
				}
				try{
					exception_list.add(MOVE_TYPE+"过账失败："+sapDoc.get("MESSAGE")+";");
					log.info(MOVE_TYPE+"过账失败："+sapDoc.get("MESSAGE")+";");
					//待冲销SAP凭证号,先冲销后产生的凭证，再冲销前一步凭证
					for(int j=sap_success_list.size()-1;j>=0;j--) {
						Map<String,Object> c = sap_success_list.get(j);
						Map<String,Object>rmap = wmsSapRemote.getSapBapiGoodsmvtCancel(c);
						if(!"0".equals(rmap.get("CODE"))) {
							log.info("---->10秒后重试第一次");
							Thread.sleep(10000);
							rmap = wmsSapRemote.getSapBapiGoodsmvtCancel(c);
							if(!"0".equals(rmap.get("CODE"))) {
								log.info("---->20秒后重试第二次");
								Thread.sleep(20000);
								rmap = wmsSapRemote.getSapBapiGoodsmvtCancel(c);
								if(!"0".equals(rmap.get("CODE"))) {
									exception_list.add("凭证冲销失败，凭证号："+c.get("MATERIALDOCUMENT")+",凭证年份："+c.get("MATDOCUMENTYEAR")+
											", 移动类型："+MOVE_TYPE);
									log.info("凭证冲销失败，凭证号："+c.get("MATERIALDOCUMENT")+",凭证年份："+c.get("MATDOCUMENTYEAR")+
											", 移动类型："+MOVE_TYPE);
								}
							}
						}
						sap_success_list.remove(c);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				throw new RuntimeException(StringUtils.join(exception_list, ";"));
			}
		}

		if(sapSynMap.get("SAP_JOB_NO")!=null) {
			/*
			 * 更新过账任务状态为02,过账成功
			 */
			sapSynMap.put("JOB_FLAG", "02");
		}

		return SAP_NO;

	}

	@Override
	public String getRevokeMoveType(String moveType) {
		String revokeMoveType = "";
		try {
			if("Z25".equals(moveType)){//pengtao 20190820 add
				revokeMoveType="Z26";
			}else{
			for(int i=0;i<moveType.length();i++) {
				if (Character.isDigit(moveType.charAt(i))){
					revokeMoveType = moveType.substring(0, i) + String.valueOf(Integer.valueOf(moveType.substring(i, i+3))+1)
							+ moveType.substring(i+3, moveType.length());
					break;
				}
			}
			}
		}catch(Exception e) {
			revokeMoveType = "-";
		}
		return revokeMoveType;
	}

	@Override
	public String getRevokeSapMoveType(String moveType) {
		String revokeSapMoveType = "";
		try {
			if("Z25".equals(moveType)){//pengtao 20190820 add
				revokeSapMoveType="Z26";
			}else{
			String[] strlist = moveType.split("#");
			for(int i=0;i<strlist.length;i++ ) {
				System.out.println("--->" + strlist[strlist.length-1-i]);
				String str2="";
				for(int j=0;j<strlist[strlist.length-1-i].length();j++) {
					if (Character.isDigit(strlist[strlist.length-1-i].charAt(j))){
						str2 = strlist[strlist.length-1-i].substring(0, j) + String.valueOf(Integer.valueOf(strlist[strlist.length-1-i].substring(j, j+3))+1)
								+ strlist[strlist.length-1-i].substring(j+3, strlist[strlist.length-1-i].length());
						break;
					}
				}
				revokeSapMoveType += str2 + "#";
			}
			revokeSapMoveType = revokeSapMoveType.substring(0, revokeSapMoveType.length()-1);
			}
		}catch(Exception e) {
			revokeSapMoveType = "-";
		}
		return revokeSapMoveType;
	}

	@Override
	public List<Map<String, Object>> getMaterialStock(List<Map<String, Object>> matList) {
		return commonDao.getMaterialStock(matList) ;
	}

	@Override
	public List<Map<String, Object>> getMatPackageList(List<Map<String, Object>> params){
		return commonDao.getMatPackageList(params);
	}

	@Override
	public List<Map<String, Object>> getControlFlagList(Map<String, Object> params) {
		return commonDao.getControlFlagList(params);

	}

	/*@Override
	public List<Map<String, Object>> getFactoryList(String factoryName) {
		return commonDao.getFactoryList(factoryName) ;
	}
*/

	/**
	 * 拣配下架，搜索库存推荐储位
	 * @param params
	 */
	@Override
	public List<Map<String, Object>> searchBinForPick(Map<String, Object> params) {
		return commonDao.searchBinForPick(params);
	}

	/**
	 * 检索库存
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getWmsStock(List<Map<String, Object>> params) {
		return commonDao.getWmsStock(params);
	}

	/**
	 * 检索库存
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getWmsStockforMap(Map<String, Object> params) {
		return commonDao.getWmsStockforMap(params);
	}

	/**
	 * 根据工厂、仓库号、库位、料号、批次、储位、供应商等条件查询虚拟库存是否小于传入的物料数量物料信息
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> checkMatVirtualStock(List<Map<String, Object>> matList){
		return commonDao.checkMatVirtualStock(matList);
	}

	@Override
	public List<Map<String,Object>> getWmsBusinessClass(Map<String,Object> cdmap){
		return commonDao.getWmsBusinessClass(cdmap);
	}

	/**
	 * 精确批量查找物料信息
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getMaterialInfoBatch(List<Map<String, Object>> params) {
		return commonDao.getMaterialInfoBatch(params);
	}

	/**
	 * 查询所有过账任务信息-异步过账失败
	 * @param params
	 * @return
	 */
	@Override
	public PageUtils getSapPostJobList(Map<String, Object> params){
		params=ParamsFilterUtils.paramFilter(params);
		String JOB_FLAG = params.get("JOB_FLAG")==null?"":params.get("JOB_FLAG").toString();
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		if("03".equals(JOB_FLAG)||"05".equals(JOB_FLAG)) {
			pageSize = "100000";
		}
		int start = 0;int end = 0;
		int count = commonDao.getSapPostJobListCount(params);
		if(pageSize!=null && !pageSize.equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list = commonDao.getSapPostJobList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}


	/**
	 * 根据过账任务状态查询过账任务清单
	 */
	public List<Map<String, Object>> getSapPostJobListByJobFlag(List<Map<String, Object>> sapPostJobItemList){
		return commonDao.getSapPostJobListByJobFlag(sapPostJobItemList);
	}

	/**
	 * 修改过账任务行项目过账状态
	 * @param sapPostJobItemList
	 * @return
	 */
	public int updateSapJobItemJobFlag(List<Map<String, Object>> sapPostJobItemList) {
		return  commonDao.updateSapJobItemJobFlag(sapPostJobItemList);
	}

	/**
	 * SAP异步过账任务处理-过账
	 * @return
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R sapPostJobPost(Map<String,Object> params,List<Map<String, Object>> sapPostJobItemList) {
		String rtnMsg = "";

		Set<String> sapJobNoSet = new HashSet<>();
		for (Map<String, Object> map : sapPostJobItemList) {
			sapJobNoSet.add(map.get("SAP_JOB_NO").toString()+"*#"+map.get("JOB_SEQ").toString());
		}
		for (String string : sapJobNoSet) {
			String SAP_JOB_NO = string.split("\\*#")[0];
			String JOB_SEQ = string.split("\\*#")[1];

			List<Map<String, Object>> postList = new ArrayList<>();

			for (Map<String, Object> map : sapPostJobItemList) {
				if(SAP_JOB_NO.equals(map.get("SAP_JOB_NO").toString()) && JOB_SEQ.equals(map.get("JOB_SEQ").toString())){
					postList.add(map);
				}
			}
			if(postList.size()>0) {
				Map<String,Object> asynPostHead = new HashMap<String,Object>();

				String WERKS = params.get("WERKS").toString(); //过账工厂

				String JZ_DATE = null;
				if(params.get("JZ_DATE") !=null && !StringUtils.isEmpty(params.get("JZ_DATE").toString())) {
					JZ_DATE = params.get("JZ_DATE").toString();//过账日期
				}
				String PZ_DATE = null;
				if(params.get("JZ_DATE") !=null && !StringUtils.isEmpty(params.get("PZ_DATE").toString())) {
					PZ_DATE = params.get("PZ_DATE").toString();//凭证日期
				}
				String GM_CODE = null;
				if(postList.get(0).get("GM_CODE") !=null && !StringUtils.isEmpty(postList.get(0).get("GM_CODE").toString())) {
					GM_CODE = postList.get(0).get("GM_CODE").toString();
				}
				String LGORT_CONFIG = null;
				if(postList.get(0).get("LGORT_CONFIG") !=null && !StringUtils.isEmpty(postList.get(0).get("LGORT_CONFIG").toString())) {
					PZ_DATE = postList.get(0).get("LGORT_CONFIG").toString();
				}

				asynPostHead.put("WERKS", WERKS);
				asynPostHead.put("WMS_NO", postList.get(0).get("REF_WMS_NO"));
				asynPostHead.put("REF_WMS_NO", postList.get(0).get("REF_WMS_NO"));
				asynPostHead.put("PSTNG_DATE", JZ_DATE==null?postList.get(0).get("PSTNG_DATE"):JZ_DATE.replaceAll("-", ""));
				asynPostHead.put("DOC_DATE", PZ_DATE==null?postList.get(0).get("DOC_DATE"):PZ_DATE.replaceAll("-", ""));
				asynPostHead.put("JZ_DATE", JZ_DATE==null?postList.get(0).get("PSTNG_DATE"):JZ_DATE.replaceAll("-", ""));
				asynPostHead.put("SAP_MOVE_TYPE", postList.get(0).get("MOVE_TYPE"));
				asynPostHead.put("PZ_DATE", PZ_DATE==null?postList.get(0).get("DOC_DATE"):PZ_DATE.replaceAll("-", ""));
				asynPostHead.put("GM_CODE", GM_CODE);
				asynPostHead.put("LGORT_CONFIG", LGORT_CONFIG);
				asynPostHead.put("SAP_JOB_NO", SAP_JOB_NO);

				//2019-07-03 add by thw 处理105过账委外PO组件物料消耗
				Iterator<Map<String,Object>> it = postList.iterator();
		    	while(it.hasNext()) {
		    		Map<String,Object> map=it.next();

					List<Map<String,Object>> poComonentList = new ArrayList<>();
					String SAP_JOB_ITEM_NO = map.get("SAP_JOB_ITEM_NO").toString();
					while(it.hasNext()) {
						Map<String,Object> map2=it.next();

						String PARENT_JOB_ITEM_NO = map2.get("PARENT_JOB_ITEM_NO")==null?"":map2.get("PARENT_JOB_ITEM_NO").toString();
						if(SAP_JOB_ITEM_NO.equals(PARENT_JOB_ITEM_NO)) {
							poComonentList.add(map2);
							it.remove();
						}
					}
					map.put("poComonentList", poComonentList);
		    	}
		    	//一步过账、两步过账同时存在时，后面失败无法取消前面成功过账SAP的凭证。
		    	List<Map<String,Object>> sap_success_list=new ArrayList<Map<String,Object>>();
				rtnMsg += "异步过账任务-"+SAP_JOB_NO+",SAP凭证-"+this.sapPost(asynPostHead, postList, sap_success_list)+",";

				//将返回的SAP凭证和WMS凭证关联
				//过账成功后，更新事务表关联SAP凭证信息
				Object REF_WMS_NO = asynPostHead.get("WMS_NO")==null?(asynPostHead.get("REF_WMS_NO")==null?sapPostJobItemList.get(0).get("WMS_NO"):asynPostHead.get("REF_WMS_NO")):asynPostHead.get("WMS_NO");
				Object REF_DOC_NO = asynPostHead.get("REF_DOC_NO")==null?(postList.get(0).get("REF_DOC_NO")==null?null:postList.get(0).get("WMS_NO")):asynPostHead.get("REF_DOC_NO");
				if(sap_success_list.size()>0) {
					//过账成功，保存SAP过账记录
					try {

						for(Map<String,Object> m:sap_success_list) {
							String MAT_DOC_STR = "";

							List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
							List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
							List<Map<String,String>> updateWmsdocList = new ArrayList<Map<String,String>>();//更新WMS凭证行项目数据

							List<Map<String,String>> ITEMLIST = (List<Map<String,String>>)m.get("ITEMLIST"); //待过账物料清单
							Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(m.get("MATERIALDOCUMENT").toString(),m.get("MATDOCUMENTYEAR").toString());
							Map<String,String> head=(Map<String, String>) rm.get("GOODSMVT_HEADER");
							List<Map<String,String>> itemList =(List<Map<String, String>>) rm.get("GOODSMVT_ITEMS");
							if(head!=null) {
								head.put("REF_WMS_NO", REF_WMS_NO==null?"":REF_WMS_NO.toString());
								head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
								head.put("REF_DOC_NO", REF_DOC_NO==null?"":REF_DOC_NO.toString());
								sap_head_list.add(head);
							}
							//解决SAP凭证中不同行项目物料、单位、数量完全一致无法正确匹配WMS行项目和SAP凭证行项目
							String MATDOC_ITM_MATCHD = "";
							for (Map<String, String> wmsDocMap : ITEMLIST) {
								String wmdDoc_MATNR = wmsDocMap.get("MATNR").toString();
								String wmdDoc_WERKS = wmsDocMap.get("PLANT").toString();
								String wmdDoc_UNIT = wmsDocMap.get("ENTRY_UOM").toString();
								Double wmdDoc_QTY_SAP = Double.valueOf(wmsDocMap.get("ENTRY_QNT").toString());

								for (Map<String, String> sapDocMap : itemList) {
									String sapDoc_MATERIAL = sapDocMap.get("MATNR").toString();
									String sapDoc_PLANT = sapDocMap.get("PLANT").toString();
									String sapDoc_ENTRY_UOM = sapDocMap.get("ENTRY_UOM").toString();
									Double sapDoc_ENTRY_QNT = Double.valueOf(sapDocMap.get("ENTRY_QNT").toString());
									String sapDoc_MATDOC_ITM = sapDocMap.get("MATDOC_ITM");
									if(wmdDoc_MATNR.equals(sapDoc_MATERIAL) && wmdDoc_WERKS.equals(sapDoc_PLANT) &&
											wmdDoc_UNIT.equals(sapDoc_ENTRY_UOM) &&
											wmdDoc_QTY_SAP.doubleValue() ==sapDoc_ENTRY_QNT.doubleValue() &&
											!MATDOC_ITM_MATCHD.contains(sapDoc_MATDOC_ITM) &&
											(sapDocMap.get("X_AUTO_CRE")==null || !"X".equals(sapDocMap.get("X_AUTO_CRE")))) {
										MATDOC_ITM_MATCHD +=sapDoc_MATDOC_ITM+";";
										//匹配
										Map<String,String> updateWmsdoc = new HashMap<String,String>();
										updateWmsdoc.put("WMS_NO", wmsDocMap.get("WMS_NO"));
										updateWmsdoc.put("WMS_ITEM_NO", wmsDocMap.get("WMS_ITEM_NO"));
										updateWmsdoc.put("WMS_SAP_MAT_DOC", sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";");
										//updateWmsdoc.get("WMS_SAP_MAT_DOC")!=null?(updateWmsdoc.get("WMS_SAP_MAT_DOC")+sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";"):
										updateWmsdocList.add(updateWmsdoc);

										sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
										sapDocMap.put("REF_WMS_ITEM_NO", sapDocMap.get("REF_WMS_ITEM_NO")!=null?(sapDocMap.get("REF_WMS_ITEM_NO")+";"+wmsDocMap.get("WMS_ITEM_NO")):wmsDocMap.get("WMS_ITEM_NO"));
										sapDocMap.put("REF_MATDOC_ITM", wmsDocMap.get("REF_MATDOC_ITM"));

										sap_detail_list.add(sapDocMap);
										break;
									}
								}
							}

							MAT_DOC_STR += m.get("MOVE_TYPE")+":"+head.get("MAT_DOC")+";";


							/**
							 * 更新WMS凭证行项目SAP物料凭证信息
							 */
							commonDao.updateWMSDocItemSapDocInfo(updateWmsdocList);

							/**
							 * 更新WMS凭证抬头SAP物料凭证信息
							 */
							Map<String,Object> wmsDocMap = new HashMap<String,Object>();
							wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
							wmsDocMap.put("WMS_NO", REF_WMS_NO);
							commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);

							commonDao.insertSapDocHead(sap_head_list);	//SAP凭证表-抬头
							commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
						}
					} catch (Exception e2) {
						//过账成功，保存SAP过账记录失败
					}

				}

				for (Map<String, Object> map : postList) {
					if(asynPostHead.get("JOB_FLAG")!=null && "03".equals(asynPostHead.get("JOB_FLAG").toString())) {
						Double POST_QTY = Double.valueOf(map.get("POST_QTY")==null?"0":map.get("POST_QTY").toString());
						map.put("JOB_FLAG", "03");
						map.put("POST_QTY", POST_QTY);
					}else {
						Double ALL_ENTRY_QNT = Double.valueOf(map.get("ALL_ENTRY_QNT")==null?"0":map.get("ALL_ENTRY_QNT").toString());
						Double ENTRY_QNT = Double.valueOf(map.get("ENTRY_QNT")==null?"0":map.get("ENTRY_QNT").toString());
						Double POST_QTY = Double.valueOf(map.get("POST_QTY")==null?"0":map.get("POST_QTY").toString());

						if(ALL_ENTRY_QNT <=0) {
							map.put("JOB_FLAG", "02");
							map.put("POST_QTY", POST_QTY);
						}else {
							if(ENTRY_QNT+POST_QTY <ALL_ENTRY_QNT) {
								map.put("JOB_FLAG", "05");
								map.put("POST_QTY", POST_QTY+ENTRY_QNT);
							}else {
								map.put("JOB_FLAG", "02");
								map.put("POST_QTY", POST_QTY+ENTRY_QNT);
							}
						}
					}
				}
				//更新任务行项目信息
				commonDao.updateSapJobItem(postList);

			}

		}

		return R.ok("过账成功："+rtnMsg);
	}


	/**
	 * SAP异步过账任务处理-取消
	 * @return
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R sapPostJobCancel(List<Map<String, Object>> sapPostJobItemList) {
		//更新任务行项目信息
		commonDao.updateSapJobItem(sapPostJobItemList);
		//把对应的凭证明细表字段 -是否需要过账到SAP系统标识 改成00
		commonDao.updateSapFlagItem(sapPostJobItemList);
		return R.ok("操作成功！");
	}

	/**
	 * 根据凭证号，获取凭证信息
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getWmsDocList(String WMS_NO) {
		return commonDao.getWmsDocList(WMS_NO);
	}

	@Override
	public void updateLabel(List<Map<String, Object>> params) {
		 commonDao.updateLabel(params);
	}

	@Override
	public String doSapPostForWlms(Map<String, Object> params) {

		String SAP_NO="";
		/**
		 * 待过账物料数据
		 */
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		/**
		 * 根据特殊库存类型，拆分过账项目数据,目前只能处理 自有、寄售
		 * 20190517优化逻辑：按过账移动类型步骤拆分过账项目数据，分为 一步过账，两步
		*/
		List<Map<String,Object>> oneSepMatList = new ArrayList<Map<String,Object>>(); //一步过账物料行项目
		List<Map<String,Object>> twoSetMatList = new ArrayList<Map<String,Object>>();  //两步过账物料行项目

		String WERKS = params.get("WERKS").toString(); //过账工厂
		String JZ_DATE = params.get("JZ_DATE").toString();//过账日期
		String PZ_DATE = params.get("PZ_DATE").toString();//凭证日期
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();//业务类型描述
		String BUSINESS_TYPE = params.get("BUSINESS_TYPE").toString();//WMS业务类型
		String BUSINESS_CLASS = params.get("BUSINESS_CLASS").toString();//业务分类

		//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", BUSINESS_NAME);
		cdmap.put("BUSINESS_TYPE", BUSINESS_TYPE);
		cdmap.put("BUSINESS_CLASS", BUSINESS_CLASS);
		cdmap.put("WERKS", WERKS);
		//this.getSapMoveType(cdmap, matList);

		for (Map<String, Object> matMap : matList) {

			String SAP_MOVE_TYPE = matMap.get("SAP_MOVE_TYPE")==null?"":matMap.get("SAP_MOVE_TYPE").toString();
			if(SAP_MOVE_TYPE.contains("+")) {
				twoSetMatList.add(matMap);
			}else {
				oneSepMatList.add(matMap);
			}
		}

		Map<String,Object> moveSyn = null;

		if(oneSepMatList.size()>0) {
			/*
			 * 一步过账
			 */
			//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
			cdmap.put("SOBKZ", oneSepMatList.get(0).get("SOBKZ"));
			moveSyn = this.getMoveAndSyn(cdmap);
		}

		if(twoSetMatList.size()>0) {
			//多步过账
			//获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
			cdmap.put("SOBKZ", twoSetMatList.get(0).get("SOBKZ"));
			moveSyn = commonDao.getMoveAndSyn(cdmap);
		}
		//
		String SAP_MOVE_TYPE =params.get("SAP_MOVE_TYPE").toString();


			//需要过账
			//String SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")!=null?moveSyn.get("SAP_MOVE_TYPE").toString():"";
			String SYN_FLAG = moveSyn.get("SYN_FLAG")!=null?moveSyn.get("SYN_FLAG").toString():"";
			String GM_CODE = moveSyn.get("GM_CODE")!=null?moveSyn.get("GM_CODE").toString():null;

			String REF_WMS_NO = params.get("WMS_NO")==null?oneSepMatList.get(0).get("WMS_NO")==null?null:oneSepMatList.get(0).get("WMS_NO").toString():params.get("WMS_NO").toString();
			//WERKS：工厂、WMS_NO：WMS凭证号、JZ_DATE：记账日期、PZ_DATE：凭证日期、SAP_MOVE_TYPE：SAP过账移动类型、GM_CODE：
			Map<String,Object> asynPostHead = new HashMap<String,Object>();

			asynPostHead.put("WERKS", WERKS);
			asynPostHead.put("WMS_NO", REF_WMS_NO);
			asynPostHead.put("REF_WMS_NO", REF_WMS_NO);
			asynPostHead.put("PSTNG_DATE", JZ_DATE.replaceAll("-", ""));
			asynPostHead.put("DOC_DATE", PZ_DATE.replaceAll("-", ""));
			asynPostHead.put("JZ_DATE", JZ_DATE.toString().replaceAll("-", ""));
			asynPostHead.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			asynPostHead.put("PZ_DATE", PZ_DATE.toString().replaceAll("-", ""));
			asynPostHead.put("GM_CODE", GM_CODE);
			asynPostHead.put("LGORT_CONFIG", moveSyn.get("LGORT")!=null?moveSyn.get("LGORT").toString():null);
			//一步过账、两步过账同时存在时，后面失败无法取消前面成功过账SAP的凭证。
			List<Map<String,Object>> sap_success_list=new ArrayList<Map<String,Object>>();
			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE) && oneSepMatList.size()>0) {//需要过账
				if("X".equals(SYN_FLAG)) {
					//异步过账，创建过账定时任务
					SAP_NO += this.asynPost(asynPostHead, oneSepMatList);
				}else {
					//同步过账
					SAP_NO += this.sapPost(asynPostHead, oneSepMatList, sap_success_list);
				}
			}

			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE) && twoSetMatList.size()>0) {//需要过账
				if("X".equals(SYN_FLAG)) {
					//异步过账，创建过账定时任务
					SAP_NO += this.asynPost(asynPostHead, twoSetMatList);
				}else {
					//同步过账
					SAP_NO += this.sapPost(asynPostHead, twoSetMatList, sap_success_list);
				}
			}


		return SAP_NO;

	}

	@Override
	@Transactional
	public void saveUnSyncVendor(Map<String, Object> condMap) {
		commonDao.saveUnSyncVendor(condMap);
		commonDao.deleteSyncVendor();
	}

	@Override
	@Transactional
	public void saveUnSyncMat(Map<String, Object> condMap) {
		/*commonDao.saveUnSyncMat(condMap);
		commonDao.deleteSyncMat();*/
	}

	@Override
	public Map<String,Object> getMaterialInfo(Map<String, Object> params) {
		String werks=params.get("WERKS").toString();
		String matnr=params.get("MATNR").toString();
		return commonDao.getMaterialInfo(werks, matnr);
	}

	@Override
	public List<Map<String, Object>> getAllLoList(Map<String, Object> params) {
		return commonDao.getAllLoList(params);
	}

	@Override
	public List<Map<String,Object>> getMatStockInfo(Map<String, Object> params){
		return commonDao.getMatStockInfo(params);
	}
	@Override
	public List<Map<String, Object>> getMatManager(Map<String, Object> params){
		return commonDao.getMatManager(params);
	}
	@Override
	public List<Map<String, Object>> getMatManagerAuthCodeList(Map<String, Object> params){
		return commonDao.getMatManagerAuthCodeList(params);
	}
	@Override
	public List<Map<String, Object>> getMatByAuthCode(Map<String, Object> params){
		return commonDao.getMatByAuthCode(params);
	}

	@Override
	public List<Map<String, Object>> getBinCode(Map<String, Object> params) {
		return commonDao.getBinCode(params);
	}

	@Override
	public List<Map<String, Object>> getLabelInfo(Map<String, Object> params){
		return commonDao.getLabelInfo(params);
	}

	/**
	 * 清理零库存
	 */
	public void stockClear(Map<String, Object> params) {
		commonDao.deleteStockLableforZero(params);
		commonDao.deleteStockforZero(params);
	}

	/**
	 * 根据工厂、业务类型查询工厂业务配置参数
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPlantBusiness(Map<String, Object> params) {
		return commonDao.selectPlantBusiness(params);
	}

	/**
	 * SAP过账成功后，更新WMS凭证关联的SAP凭证信息
	 * @param sapSynMap 过账抬头
	 * @param matList 过账物料行项目
	 * @param sap_success_list 保存过账成功凭证清单
	 * @return
	 */
	protected String saveWmsDocSapMatDoc(Map<String,Object> sapSynMap,List<Map<String,Object>> sap_success_list) {
		String SAP_NO = "";
		int updateWmsDocRows = 0;
		String REF_WMS_NO = sapSynMap.get("REF_WMS_NO")==null?(sapSynMap.get("REF_DOC_NO")==null?null:sapSynMap.get("WMS_NO").toString()):sapSynMap.get("REF_WMS_NO").toString();
		Object REF_DOC_NO = sapSynMap.get("REF_DOC_NO")==null?null:sapSynMap.get("REF_DOC_NO");
		if(sap_success_list.size()>0) {
			//过账成功，保存SAP过账记录
			try {

				for(Map<String,Object> m:sap_success_list) {
					String MAT_DOC_STR = "";

					List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
					List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
					List<Map<String,String>> updateWmsdocList = new ArrayList<Map<String,String>>();//更新WMS凭证行项目数据

					List<Map<String,String>> ITEMLIST = (List<Map<String,String>>)m.get("ITEMLIST"); //待过账物料清单
					Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(m.get("MATERIALDOCUMENT").toString(),m.get("MATDOCUMENTYEAR").toString());
					Map<String,String> head=(Map<String, String>) rm.get("GOODSMVT_HEADER");
					List<Map<String,String>> itemList =(List<Map<String, String>>) rm.get("GOODSMVT_ITEMS");
					if(head!=null) {
						head.put("REF_WMS_NO", REF_WMS_NO==null?"":REF_WMS_NO.toString());
						head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
						head.put("REF_DOC_NO", REF_DOC_NO==null?"":REF_DOC_NO.toString());
						sap_head_list.add(head);
						SAP_NO+=head.get("MAT_DOC")+"(" +itemList.get(0).get("MOVE_TYPE")+"); <br/>";
					}
					//解决SAP凭证中不同行项目物料、单位、数量完全一致无法正确匹配WMS行项目和SAP凭证行项目
					String MATDOC_ITM_MATCHD = "";
					for (Map<String, String> wmsDocMap : ITEMLIST) {
						String wmdDoc_MATNR = wmsDocMap.get("MATNR").toString();
						String wmdDoc_WERKS = wmsDocMap.get("PLANT").toString();
						String wmdDoc_UNIT = wmsDocMap.get("ENTRY_UOM").toString();
						Double wmdDoc_QTY_SAP = Double.valueOf(wmsDocMap.get("ENTRY_QNT").toString());

						for (Map<String, String> sapDocMap : itemList) {
							String sapDoc_MATERIAL = sapDocMap.get("MATNR").toString();
							String sapDoc_PLANT = sapDocMap.get("PLANT").toString();
							String sapDoc_ENTRY_UOM = sapDocMap.get("ENTRY_UOM").toString();
							Double sapDoc_ENTRY_QNT = Double.valueOf(sapDocMap.get("ENTRY_QNT").toString());
							String sapDoc_MATDOC_ITM = sapDocMap.get("MATDOC_ITM");
							if(wmdDoc_MATNR.equals(sapDoc_MATERIAL) && wmdDoc_WERKS.equals(sapDoc_PLANT) &&
									wmdDoc_UNIT.equals(sapDoc_ENTRY_UOM) &&
									wmdDoc_QTY_SAP.doubleValue() ==sapDoc_ENTRY_QNT.doubleValue() &&
									!MATDOC_ITM_MATCHD.contains(sapDoc_MATDOC_ITM) &&
									(sapDocMap.get("X_AUTO_CRE")==null || !"X".equals(sapDocMap.get("X_AUTO_CRE")))) {
								MATDOC_ITM_MATCHD +=sapDoc_MATDOC_ITM+";";
								//匹配
								Map<String,String> updateWmsdoc = new HashMap<String,String>();
								updateWmsdoc.put("WMS_NO", wmsDocMap.get("WMS_NO"));
								updateWmsdoc.put("WMS_ITEM_NO", wmsDocMap.get("WMS_ITEM_NO"));
								updateWmsdoc.put("WMS_SAP_MAT_DOC", sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";");
								//updateWmsdoc.get("WMS_SAP_MAT_DOC")!=null?(updateWmsdoc.get("WMS_SAP_MAT_DOC")+sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";"):
								updateWmsdocList.add(updateWmsdoc);

								sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
								sapDocMap.put("REF_WMS_ITEM_NO", sapDocMap.get("REF_WMS_ITEM_NO")!=null?(sapDocMap.get("REF_WMS_ITEM_NO")+";"+wmsDocMap.get("WMS_ITEM_NO")):wmsDocMap.get("WMS_ITEM_NO"));
								sapDocMap.put("REF_MATDOC_ITM", wmsDocMap.get("REF_MATDOC_ITM"));

								sap_detail_list.add(sapDocMap);
								break;
							}
						}
					}

					MAT_DOC_STR += m.get("MOVE_TYPE")+":"+head.get("MAT_DOC")+";";


					/**
					 * 更新WMS凭证行项目SAP物料凭证信息
					 */
					updateWmsDocRows = commonDao.updateWMSDocItemSapDocInfo(updateWmsdocList);

					/**
					 * 更新WMS凭证抬头SAP物料凭证信息
					 */
					Map<String,Object> wmsDocMap = new HashMap<String,Object>();
					wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
					wmsDocMap.put("WMS_NO", REF_WMS_NO);
					commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);
				}
			} catch (Exception e2) {
				//过账成功，保存SAP过账记录失败
				SAP_NO += "更新WMS凭证关联的SAP凭证失败！";
			}

		}

		return SAP_NO; //+"-"+updateWmsDocRows;
	}

	/**
	 * SAP过账成功后，更新WMS凭证关联的SAP凭证信息并保存SAP凭证信息到SAP凭证抬头和明细表
	 * @param sapSynMap 过账抬头
	 * @param matList 过账物料行项目
	 * @param sap_success_list 保存过账成功凭证清单
	 * @return
	 */
	protected String saveWmsDocSapMatDocAndSapDoc(Map<String,Object> sapSynMap,List<Map<String,Object>> sap_success_list) {
		String SAP_NO = "";
		int updateWmsDocRows = 0;
		String REF_WMS_NO = sapSynMap.get("REF_WMS_NO")==null?(sapSynMap.get("REF_DOC_NO")==null?null:sapSynMap.get("WMS_NO").toString()):sapSynMap.get("REF_WMS_NO").toString();
		Object REF_DOC_NO = sapSynMap.get("REF_DOC_NO")==null?null:sapSynMap.get("REF_DOC_NO");
		if(sap_success_list.size()>0) {
			//过账成功，保存SAP过账记录
			try {

				for(Map<String,Object> m:sap_success_list) {
					String MAT_DOC_STR = "";

					List<Map<String,String>> sap_head_list=new ArrayList<Map<String,String>>();
					List<Map<String,String>> sap_detail_list=new ArrayList<Map<String,String>>();
					List<Map<String,String>> updateWmsdocList = new ArrayList<Map<String,String>>();//更新WMS凭证行项目数据

					List<Map<String,String>> ITEMLIST = (List<Map<String,String>>)m.get("ITEMLIST"); //待过账物料清单
					Map<String,Object> rm= wmsSapRemote.getSapBapiGoodsmvtDetail(m.get("MATERIALDOCUMENT").toString(),m.get("MATDOCUMENTYEAR").toString());
					Map<String,String> head=(Map<String, String>) rm.get("GOODSMVT_HEADER");
					List<Map<String,String>> itemList =(List<Map<String, String>>) rm.get("GOODSMVT_ITEMS");
					if(head!=null) {
						head.put("REF_WMS_NO", REF_WMS_NO==null?"":REF_WMS_NO.toString());
						head.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
						head.put("REF_DOC_NO", REF_DOC_NO==null?"":REF_DOC_NO.toString());
						sap_head_list.add(head);
						SAP_NO+=head.get("MAT_DOC")+"(" +itemList.get(0).get("MOVE_TYPE")+"); <br/>";
					}
					//解决SAP凭证中不同行项目物料、单位、数量完全一致无法正确匹配WMS行项目和SAP凭证行项目
					String MATDOC_ITM_MATCHD = "";
					for (Map<String, String> wmsDocMap : ITEMLIST) {
						String wmdDoc_MATNR = wmsDocMap.get("MATNR").toString();
						String wmdDoc_WERKS = wmsDocMap.get("PLANT").toString();
						String wmdDoc_UNIT = wmsDocMap.get("ENTRY_UOM").toString();
						Double wmdDoc_QTY_SAP = Double.valueOf(wmsDocMap.get("ENTRY_QNT").toString());

						for (Map<String, String> sapDocMap : itemList) {
							String sapDoc_MATERIAL = sapDocMap.get("MATNR").toString();
							String sapDoc_PLANT = sapDocMap.get("PLANT").toString();
							String sapDoc_ENTRY_UOM = sapDocMap.get("ENTRY_UOM").toString();
							Double sapDoc_ENTRY_QNT = Double.valueOf(sapDocMap.get("ENTRY_QNT").toString());
							String sapDoc_MATDOC_ITM = sapDocMap.get("MATDOC_ITM");
							if(wmdDoc_MATNR.equals(sapDoc_MATERIAL) && wmdDoc_WERKS.equals(sapDoc_PLANT) &&
									wmdDoc_UNIT.equals(sapDoc_ENTRY_UOM) &&
									wmdDoc_QTY_SAP.doubleValue() ==sapDoc_ENTRY_QNT.doubleValue() &&
									!MATDOC_ITM_MATCHD.contains(sapDoc_MATDOC_ITM) &&
									(sapDocMap.get("X_AUTO_CRE")==null || !"X".equals(sapDocMap.get("X_AUTO_CRE")))) {
								MATDOC_ITM_MATCHD +=sapDoc_MATDOC_ITM+";";
								//匹配
								Map<String,String> updateWmsdoc = new HashMap<String,String>();
								updateWmsdoc.put("WMS_NO", wmsDocMap.get("WMS_NO"));
								updateWmsdoc.put("WMS_ITEM_NO", wmsDocMap.get("WMS_ITEM_NO"));
								updateWmsdoc.put("WMS_SAP_MAT_DOC", sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";");
								//updateWmsdoc.get("WMS_SAP_MAT_DOC")!=null?(updateWmsdoc.get("WMS_SAP_MAT_DOC")+sapDocMap.get("MAT_DOC")+":"+sapDocMap.get("MATDOC_ITM")+";"):
								updateWmsdocList.add(updateWmsdoc);

								sapDocMap.put("CREATE_DATE", DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
								sapDocMap.put("REF_WMS_ITEM_NO", sapDocMap.get("REF_WMS_ITEM_NO")!=null?(sapDocMap.get("REF_WMS_ITEM_NO")+";"+wmsDocMap.get("WMS_ITEM_NO")):wmsDocMap.get("WMS_ITEM_NO"));
								sapDocMap.put("REF_MATDOC_ITM", wmsDocMap.get("REF_MATDOC_ITM"));

								sap_detail_list.add(sapDocMap);
								break;
							}
						}
					}

					MAT_DOC_STR += m.get("MOVE_TYPE")+":"+head.get("MAT_DOC")+";";


					/**
					 * 更新WMS凭证行项目SAP物料凭证信息
					 */
					updateWmsDocRows = commonDao.updateWMSDocItemSapDocInfo(updateWmsdocList);

					/**
					 * 更新WMS凭证抬头SAP物料凭证信息
					 */
					Map<String,Object> wmsDocMap = new HashMap<String,Object>();
					wmsDocMap.put("MAT_DOC", MAT_DOC_STR);
					wmsDocMap.put("WMS_NO", REF_WMS_NO);
					commonDao.updateWMSDocHeadSapDocInfo(wmsDocMap);

					commonDao.insertSapDocHead(sap_head_list);	//SAP凭证表-抬头
					commonDao.insertSapDocItems(sap_detail_list);//SAP凭证表-明细
				}
			} catch (Exception e2) {
				//过账成功，保存SAP过账记录失败
				SAP_NO += "更新WMS凭证关联的SAP凭证失败！";
			}

		}

		return SAP_NO; //+"-"+updateWmsDocRows;
	}
	/**
	 * 根据工厂和物料号获取SAP物料主数据信息
	 */
	public PageUtils getSAPMatDetail(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);

		List<Map<String, Object>> matDetailList = commonDao.getSAPMatDetail(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(matDetailList);
		page.setTotal(matDetailList.size());
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getMatManagerList(List<Map<String, Object>> params) {
		return commonDao.getMatManagerList(params);
	}

	@Override
	public List<Map<String,Object>> getWmsCoreWhList(Map<String,Object> param) {
		return wmsCoreWhDao.getWmsCoreWhList(param);
	}

	//根据用户和(菜单KEY)Menu_key,仓库号 ,获取条码
	@Override
	public List<Map<String,Object>> getLabelCacheInfo(Map<String, Object> params) {

		return commonDao.getLabelCacheInfo(params);
	}

	//根据用户和(菜单KEY)Menu_key,仓库号 ,保存条码
	@Override
	public int saveLabelCacheInfo(List<Map<String, Object>> params) {

		return commonDao.insertLabelCacheInfo(params);
	}

	//根据用户和(菜单KEY)Menu_key,仓库号 ,删除条码
	@Override
	public int deleteLabelCacheInfo(List<Map<String, Object>> params) {

		return commonDao.deleteLabelCacheInfo(params);
	}

	@Override
	public int getLabelCacheInfoCount(Map<String, Object> params) {
		return commonDao.getLabelCacheInfoCount(params);
	}

	@Override
	public int insertBarcodeLog(List<Map<String, Object>> params) {
		return commonDao.insertBarcodeLog(params);
	}

	@Override
	public List<Map<String,Object>> getBarcodeLog(Map<String, Object> params){
		return commonDao.getBarcodeLog(params);
	}

	@Override
	public List<Map<String, Object>> getLabelInfoBatch(List<Map<String, Object>> params){
		return commonDao.getLabelInfoBatch(params);
	}

	@Override
	public List<Map<String,Object>> getLabelCacheInfoNoPage(Map<String, Object> params){
		return commonDao.getLabelCacheInfoNoPage(params);
	}
}
