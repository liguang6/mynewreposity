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
package com.byd.wms.business.modules.common.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

/**
 * 通用Service接口
 * 
 * @author tangj
 * @email 
 * @date 2018年08月02日 15:49:01
 */
public interface CommonService {

	public List<Map<String,Object>> getWhList(String whNumber,String lang);
	
	public List<Map<String,Object>> getVendorList(String lifnr,String werks);
	
	public List<Map<String,Object>> getMaterialList(String werks,String matnr);

	public List<Map<String, Object>> getBusinessList(Map<String, Object> params);

	public List<Map<String, Object>> getLoList(Map<String, Object> params);

	public List<Map<String, Object>> getGrAreaList(String factory);
	
	/**
	 * 根据仓库号、料号等条件获取推荐的物料存储储位
	 * @param params Map
	 * WH_NUMBER：仓库号
	 * BIN_TYPE:储位类型 00 虚拟储位 01 进仓位 02 出仓位 03 拣配位 04 试装位 05 立库位
	 * BIN_STATUS：储位状态 00 未启用 01 可用 02 不可用
	 * STORAGE_MODEL：物料存储模式 00 固定存储 01随机存储 
	 * @return WMS_CORE_WH_BIN 所有
	 */
	public List<Map<String, Object>> getMatBinList(Map<String, Object> params);
	
	/**
	 * 根据工厂号获取全部仓库信息@YK20180829
	 * @param WERKS
	 */
	public List<Map<String,Object>> getWhDataByWerks(@Param(value="WERKS") String WERKS);
	
	/**
	 * 保存WMS凭证记录抬头和明细
	 * @param head PZ_DATE：凭证日期  JZ_DATE：记账日期  HEADER_TXT：头文本 TYPE：凭证类型  SAP_MOVE_TYPE：SAP移动类型
	 * @param matList
	 * @return
	 */
	String saveWMSDoc(Map<String,Object> head,List<Map<String, Object>> matList);
	
	/**
	 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
	 * @param cdmap MAP 
	 * BUSINESS_NAME：业务类型名称
	 * BUSINESS_TYPE： WMS业务类型
	 * BUSINESS_CLASS：仓库业务分类 
	 * WERKS：工厂代码
	 * SOBKZ：特殊库存类型
	 * @return Map
	 * 过账参数键值对：WMS_MOVE_TYPE：WMS移动类型  SAP_MOVE_TYPE：SAP过账移动类型  SYN_FLAG：异步标识 MOVE_REAS：移动原因
	 * GM_CODE：BAPI货物移动分配事务代码 特定值
	 */
	public Map<String, Object> getMoveAndSyn(Map<String, Object> cdmap);
	
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
	public String doSapPost(Map<String, Object> params);
	
	/**
	 * SAP过账通用方法供异步线程调用
	 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
	 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
	 * SAP过账失败回滚
	 * @param params
	 * 过账内容键值对： WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期 HEADER_TXT：抬头文本 ,
	 * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型,BUSINESS_CLASS:业务分类
	 * 过账行项目List:matList
	 * @return String SAP_NO
	 */
	public String doSapPostBusiness(Map<String, Object> params);
	
	/**
	 * SAP异步过账任务调度
	 */
	public void sapPostJobSync();
	
	/**
	 * 查询所有过账任务信息-异步过账失败
	 * @param params
	 * @return
	 */
	public PageUtils getSapPostJobList(Map<String, Object> params);
	
	/**
	 * 根据过账任务状态查询过账任务清单
	 */
	public List<Map<String, Object>> getSapPostJobListByJobFlag(List<Map<String, Object>> sapPostJobItemList);
	/**
	 * 修改过账任务行项目过账状态
	 * @param sapPostJobItemList
	 * @return
	 */
	public int updateSapJobItemJobFlag(List<Map<String, Object>> sapPostJobItemList);
	
	/**
	 * SAP异步过账任务处理-过账
	 * @return
	 */
	public R sapPostJobPost(Map<String,Object> params, List<Map<String, Object>> sapPostJobItemList);
	
	/**
	 * SAP异步过账任务处理-取消
	 * @return
	 */
	public R sapPostJobCancel(List<Map<String, Object>> sapPostJobItemList);
	
	/**
	 * SAP物料凭证取消
	 * @param params WERKS：过账工厂  WMS_NO：待冲销WMS凭证号  MAT_DOC：待冲销物料凭证号 DOC_YEAR：待冲销凭证年份 JZ_DATE：冲销过账日期
	 * matDocItemList-List<Map>：冲销凭证行项目 MATDOC_ITM:行项目号
	 * @return 返回取消的物料凭证号
	 */
	public String sapGoodsMvtCancel(Map<String, Object> params);
	
	/**
	 * WMS凭证冲销关联的SAP凭证
	 * SAP过账失败回滚
	 * @param params 
	 * 过账内容键值对： WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期  HEADER_TXT：抬头文本 ,REF_WMS_NO：待冲销WMS凭证号  WMS_NO：新产生的WMS凭证号 
	 * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型, BUSINESS_CLASS:业务分类
	 * 过账行项目matDocItemList-List<Map>  WMS_SAP_MAT_DOC：WMS凭证行项目对应的SAP凭证信息
	 * @return String SAP_NO
	 */
	public String sapGoodsMvtReversal(Map<String, Object> params);
	 
	public List<Map<String,String>> getQcReturnReason(String REASON_DESC);
	/**
	 * 更新WMS库存信息，包含新增和修改库存
	 * 保存WMS库存:根据工厂代码、仓库号、库位、储位代码、物料号、wms批次、特殊库存类型、供应商代码，判断新增库存记录，或修改库存数量
	 * @param updateList
	 */
	public void saveWmsStock(Map<String, Object> params);
	/**
	 * 更新WMS库存信息
	 * 根据工厂代码、仓库号、库位、储位代码、物料号、wms批次、特殊库存类型、供应商代码，修改库存数量
	 * @param stockMatList 更新物料库存清单
	 */
	public void modifyWmsStock(List<Map<String,Object>> stockMatList);
	/**
	 * 根据工厂查询工厂配置：是否启用供应商管理、是否启用智能储位、是否启用核销业务
	 * @param wERKS
	 * @return
	 */
	public Map<String, Object> getPlantSetting(String WERKS);
	/**
	 * 根据字典类别查询字典列表
	 * @param type
	 * @return
	 */
	public List<Map<String, Object>> getDictList(String type);
	
	/**
	 * 获取冲销/取消 凭证类型 ：原类型数字部分+1
	 * @param moveType
	 * @return
	 */
	public String getRevokeMoveType(String moveType);
	
	public String getRevokeSapMoveType(String moveType);
	
	public List<Map<String,Object>> getMaterialStock(List<Map<String, Object>> matList);

	/**
	 * 查询物料包规信息
	 * @param params 物料清单
	 * @return
	 */
    List<Map<String, Object>> getMatPackageList(List<Map<String, Object>> params);

	
	List<Map<String, Object>> getControlFlagList(Map<String, Object> params);
	
	//拣配下架，搜索库存推荐储位
	public List<Map<String, Object>> searchBinForPick(Map<String, Object> params);
	
	/**
	 * 检索库存
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getWmsStock(List<Map<String, Object>> matList);
	
	/**
	 * 检索库存
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getWmsStockforMap(Map<String, Object> params);
	
	public List<Map<String,Object>> getWmsBusinessClass(Map<String,Object> map);
	
	/**
	 * 精确批量查找物料信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getMaterialInfoBatch(List<Map<String, Object>> params);
	
	/**
	 * 根据工厂、仓库号、库位、料号、批次、储位、供应商等条件查询虚拟库存是否小于传入的物料数量物料信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> checkMatVirtualStock(List<Map<String, Object>> matList);
	
	/**
	 * 根据凭证号，获取凭证信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getWmsDocList(String WMS_NO);
	
	/**
	 * 更新标签信息
	 */
	public void updateLabel(List<Map<String,Object>> list);
	
	public String doSapPostForWlms(Map<String, Object> params);

	/**
	 * 保存为同步的供应商信息
	 * @param condMap
	 */
	public void saveUnSyncVendor(Map<String, Object> condMap);
	/**
	 * 保存同步的物料信息
	 * @param condMap
	 */
	public void saveUnSyncMat(Map<String, Object> condMap);
	
	/**
	 * 根据工厂、物料编码查询物料信息
	 * @param params
	 * @return
	 */
	public Map<String,Object> getMaterialInfo(Map<String, Object> params);
	/**
	 * 根据工厂、仓库号、
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getMatStockInfo(Map<String, Object> params);
	
	List<Map<String, Object>> getMatManager(Map<String, Object> params);
	
	/**
	 * 根据仓库号、工号查询物料管理授权码及物料管理方式清单
	 * @param params
	 * @return 
	 */
	List<Map<String, Object>> getMatManagerAuthCodeList(Map<String, Object> params);
	
	/**
	 * 根据物料管理授权码获取物料清单
	 * @param params
	 * @return 
	 */
	List<Map<String, Object>> getMatByAuthCode(Map<String, Object> params);
	
	
	/**
	 * 根据工厂代码、查询所有库位列表（包括不良品库位）
	 */
	public List<Map<String, Object>> getAllLoList(Map<String, Object> params);

	public List<Map<String, Object>> getBinCode(Map<String, Object> params);

	public List<Map<String, Object>> getLabelInfo(Map<String, Object> params);
	
	public List<Map<String, Object>> getVendor(String lifnr);
	
	/**
	 * 清理零库存
	 */
	public void stockClear(Map<String, Object> params);
	
	/**
	 * 根据工厂、业务类型查询工厂业务配置参数
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPlantBusiness(Map<String, Object> params);

	/**
	 * 根据工厂和物料号获取SAP物料主数据信息
	 * @param params
	 * @return
	 */
	public PageUtils getSAPMatDetail(Map<String, Object> params);
	
	/**
	 * 根据仓库号、物料获取绑定仓管员
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getMatManagerList(List<Map<String, Object>> params);
	
	/**
	 * 根据仓库号，获取仓库表信息
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>> getWmsCoreWhList(Map<String,Object> param);
	
	//根据用户和(菜单KEY)Menu_key,仓库号 ,获取条码
	public List<Map<String,Object>> getLabelCacheInfo(Map<String, Object> params);
	//根据用户和(菜单KEY)Menu_key,仓库号 ,保存条码
	
	public int saveLabelCacheInfo(List<Map<String, Object>> params);
	//根据用户和(菜单KEY)Menu_key,仓库号 ,删除条码
	
	public int deleteLabelCacheInfo(List<Map<String, Object>> params);
	
	public int getLabelCacheInfoCount(Map<String, Object> params);
	
	public int insertBarcodeLog(List<Map<String, Object>> params);
	
	public List<Map<String,Object>> getBarcodeLog(Map<String, Object> params);
	
	public List<Map<String, Object>> getLabelInfoBatch(List<Map<String, Object>> params);
	
	public List<Map<String,Object>> getLabelCacheInfoNoPage(Map<String, Object> params);
}
