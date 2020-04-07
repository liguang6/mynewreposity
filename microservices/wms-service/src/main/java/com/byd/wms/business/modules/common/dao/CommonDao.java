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

package com.byd.wms.business.modules.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 通用Dao
 * 
 * @author tangj
 * @email 
 * @date 2018年08月02日 下午15:46:16
 */
public interface CommonDao {
	// 通过工厂代码、仓库代码模糊查找仓库信息
	public List<Map<String,Object>> getWhList(@Param("whNumber")String whNumber,@Param("Lang")String Lang);
	
	// 通过供应商代码模糊查找供应商数据
	public List<Map<String,Object>> getVendorList(@Param("lifnr")String lifnr,@Param("werks")String werks);
	
	// 通过工厂代码、物料号模糊查找物料数据
	public List<Map<String,Object>> getMaterialList(@Param("arg0") String werks, @Param("arg1") String matnr);
	public Map<String,Object> getMaterialInfo(@Param("arg0") String werks,@Param("arg1") String matnr);

	public List<Map<String, Object>> getBusinessList(Map<String, Object> params);
	/**
	 * 根据工厂代码、库存类型查询库位列表
	 */
	List<Map<String,Object>> getLoList(Map<String, Object> params);
	
	List<Map<String,Object>> getGrAreaList(@Param(value="WERKS") String WERKS);
	
	
	/**
	 * 根据仓库号、料号等条件获取推荐的物料存储储位
	 * @param params Map
	 * WH_NUMBER：仓库号
	 * BIN_TYPE:储位类型 00 虚拟储位 01 进仓位 02 出仓位 03 拣配位 04 试装位 05 立库位
	 * BIN_STATUS：储位状态 00 未启用 01 可用 02 不可用
	 * STORAGE_MODEL：物料存储模式 00 固定存储 01随机存储 
	 * @return WMS_CORE_WH_BIN 所有字段
	 */
	public List<Map<String, Object>> getMatBinList(Map<String, Object> params);
	
	/**
	 * 获取SAP移动类型
	 * @param cdmap MAP 
	 * BUSINESS_NAME：业务类型名称
	 * BUSINESS_TYPE： WMS业务类型
	 * BUSINESS_CLASS：仓库业务分类 
	 * WERKS：工厂代码
	 * @param matList  SOBKZ：特殊库存类型
	 * @return List<Map> SAP_MOVE_TYPE
	 */
	public List<Map<String, Object>> getSapMoveType(@Param(value="cdmap")Map<String, Object> cdmap,@Param(value="matList")List<Map<String, Object>> matList);

	public List<Map<String, Object>> getPlantToInfoList(@Param(value="cdmap")Map<String, Object> cdmap,@Param(value="matList")List<Map<String, Object>> matList);
	
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
	 * 获取工厂间调拨移动类型
	 * @param cdmap
	 * @return
	 */
	public Map<String, Object> getPlantToInfo(Map<String, Object> cdmap);
	
	/**
	 * 根据工厂号获取全部仓库信息@YK20180829
	 * @param WERKS
	 */
	public List<Map<String,Object>> getWhDataByWerks(@Param(value="WERKS") String WERKS);

	public void insertWMSDocHead(Map<String, Object> wms_doc_head);

	public void insertWMSDocDetail(List<Map<String, Object>> itemList);
	
	public List<Map<String,String>> getQcReturnReason(String REASON_DESC);
	/**
	 * 查询SAP过账任务信息
	 * @param params 任务状态
	 * @return
	 */
	public List<Map<String, Object>> getSapPostJob(Map<String, Object> params);
	
	public List<Map<String,Object>> getSapPostJobSeq(Map<String, Object> params);
	
	/**
	 * 查询SAP过账任务行项目信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getSapPostJobItem(Map<String, Object> params);
	
	/**
	 * 根据过账任务状态查询过账任务清单
	 */
	public List<Map<String, Object>> getSapPostJobListByJobFlag(List<Map<String, Object>> sapPostJobItemList);
	
	/**
	 * 更新WMS凭证抬头的SAP凭证号
	 */
	public void updateWMSDocHeadSapDocInfo(Map<String,Object> map);
	
	/**
	 * 更新WMS凭证行项目的SAP凭证
	 */
	public int updateWMSDocItemSapDocInfo(List<Map<String,String>> list);
	
	/**
	 * 保存SAP凭证表-抬头信息
	 * @param sap_head_list
	 */
	void insertSapDocHead(List<Map<String, String>> sap_head_list);
	/**
	 * 保存SAP凭证表-明细信息
	 * @param sap_detail_list
	 */
	void insertSapDocItems(List<Map<String, String>> sap_detail_list);
	
	/**
	 * 修改SAP过账任务抬头信息
	 * @param job_head
	 */
	void updateSapJobHead(Map<String, Object> job_head);
	void updateSapJobItem(List<Map<String, Object>> sapPostJobItemList);
	
	/**
	 * 修改过账任务行项目过账状态
	 * @param sapPostJobItemList
	 * @return
	 */
	int updateSapJobItemJobFlag(List<Map<String, Object>> sapPostJobItemList);
	
	/**
	 * 保存SAP过账任务抬头信息
	 * @param job_head
	 */
	void insertSapJobHead(Map<String, Object> job_head);
	/**
	 * 保存SAP过账任务明细信息
	 * @param job_items
	 */
	void insertSapJobItems(List<Map<String, Object>> job_items);
	/**
	 * 更新WMS库存信息，包含新增和修改库存
	 * @param updateList
	 */
	void updateWmsStock(List<Map<String, Object>> updateList);
	/**
	 * 修改WMS库存信息
	 * 根据工厂代码、仓库号、库位、储位代码、物料号、wms批次、特殊库存类型、供应商代码，修改库存数量
	 * @param stockMatList 更新物料库存清单
	 */
	void modifyWmsStock(List<Map<String,Object>> stockMatList);
	
	/**
	 * 根据SAP物料凭证号和行项目信息获取凭证行项目详细信息
	 * @param SAP_DOC_NO SAP凭证号
	 * @param item SAP凭证行项目号
	 * @return 凭证行项目信息
	 */
	public List<Map<String,Object>> getSapDocItemInfo(@Param(value="SAP_DOC_NO")String SAP_DOC_NO,@Param(value="item")List<Map<String,Object>> item);
	
	/**
	 * 根据工厂查询工厂配置：是否启用供应商管理、是否启用智能储位、是否启用核销业务
	 * @param WERKS
	 * @return
	 */
	public Map<String, Object> getPlantSetting(@Param(value="WH_NUMBER")String WH_NUMBER);

	public List<Map<String, Object>> getDictList(@Param(value="TYPE")String type);
	
	/**
	 * 根据SAP移动类型查询SAP过账移动原因
	 * @param MOVE_TYPE SAP移动类型
	 * @return
	 */
	Map<String,Object> getSapMoveReasByMoveType(@Param(value="MOVE_TYPE")String MOVE_TYPE);
	

    /**
     * 查询物料在WMS系统的虚拟库存、非限制库存并按照批次号升序排序
     * @param matList 物料信息
     * @return  包含了库存信息
     */
    List<Map<String, Object>> getMaterialStock(@Param("matList")List<Map<String, Object>> matList);
	
	/**
	 * 查询物料包规信息
	 * @param params 物料清单
	 * @return
	 */
	List<Map<String, Object>> getMatPackageList(List<Map<String, Object>> params);

	/**
	 * 模糊查出入库策略控制标识
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getControlFlagList(Map<String, Object> params);
	
	/**
	 * 拣配下架，搜索库存推荐储位
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> searchBinForPick(Map<String, Object> params);
	
	/**
	 * 检索库存
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getWmsStock(@Param("matList")List<Map<String, Object>> matList);
	
	/**
	 * 检索库存
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getWmsStockforMap(Map<String, Object> params);
	
	/**
	 * 根据工厂、仓库号、库位、料号、批次、储位、供应商等条件查询虚拟库存是否小于传入的物料数量物料信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> checkMatVirtualStock(@Param("matList") List<Map<String, Object>> matList);

	public Map<String, Object> scanLabel(Map<String, Object> params);
	
	public Map<String, Object> querySeqNo(Map<String, Object> params);

	public List<String> getMatUnit(@Param("matList")List<Map<String,Object>> matList);
	
	public List<String> getMatBatchFlag(@Param("matList")List<Map<String,Object>> matList);

	/**
	 * 更新下架数量
	 * @param
	 * <ul>
	 * <li>werks:工厂 必填</li>
	 * <li>matnr:料号 必填</li>
	 * <li>batch:批次 必填</li>
	 * <li>xjQty:下架数量</li>
	 * <li>xjBinCode:下架储位</li>
	 * <li>qty:下架数量/取消下架数量</li>
	 * </ul>
	 * @return
	 */
	public int updateStockXJQty(Map<String,Object> obj);
	
	public List<Map<String,Object>> getWmsBusinessClass(Map<String,Object> map);
	
	/**
	 * 精确批量查找物料信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getMaterialInfoBatch(List<Map<String, Object>> params);
	
	/**
	 * 查询所有过账任务信息-异步过账失败
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getSapPostJobList(Map<String, Object> params);
	
	public int getSapPostJobListCount(Map<String, Object> params);
	
	/**
	 * 根据凭证号，获取凭证信息
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getWmsDocList(@Param("WMS_NO") String WMS_NO);
	
	void updateLabel(List<Map<String, Object>> updateList);

	/**
	 * 保存未同步的供应商信息
	 * @param condMap
	 */
	public void saveUnSyncVendor(Map<String, Object> condMap);
	/**
	 * 从SCHEDULE_JOB_MSG表中删除已经同步了的供应商信息
	 */
	public void deleteSyncVendor();

	public void saveUnSyncMat(Map<String, Object> condMap);

	public void deleteSyncMat();

	public List<Map<String, Object>> getMatStockInfo(Map<String, Object> params);
	List<Map<String, Object>> getMatManager(Map<String, Object> params);
	public List<Map<String, Object>> getMatManagerAuthCodeList(Map<String, Object> params);
	public List<Map<String, Object>> getMatByAuthCode(Map<String, Object> params);
	/**
	 * 根据工厂代码、查询所有库位列表
	 */
	List<Map<String,Object>> getAllLoList(Map<String, Object> params);

	public List<Map<String, Object>> getBinCode(Map<String, Object> params);

	public List<Map<String, Object>> getLabelInfo(Map<String, Object> params);

	public List<Map<String, Object>> getVendor(String lifnr);
	
	/**
	 * 清理零库存
	 */
	public void deleteStockforZero(Map<String, Object> params);
	
	public void deleteStockLableforZero(Map<String, Object> params);
	
	/**
	 * 根据工厂、业务类型查询工厂业务配置参数
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPlantBusiness(Map<String, Object> params);
	/**
	 * 屏蔽凭证时，需要把对应的凭证明细表字段 -是否需要过账到SAP系统标识 改成00
	 */
	void updateSapFlagItem(List<Map<String, Object>> sapPostJobItemList);

	/**
	 * 根据工厂和物料号获取SAP物料主数据信息
	 */
	public List<Map<String, Object>> getSAPMatDetail(Map<String, Object> params);

	public List<Map<String, Object>> getMatManagerList(List<Map<String, Object>> params);
	
	// 根据用户和(菜单KEY)Menu_key ,获取条码
	public List<Map<String, Object>> getLabelCacheInfo(Map<String, Object> params);
	// 根据用户和(菜单KEY)Menu_key ,保存条码
	public int insertLabelCacheInfo(List<Map<String, Object>> params);
	// 根据用户和(菜单KEY)Menu_key ,删除条码
	public int deleteLabelCacheInfo(List<Map<String, Object>> params);
	// 批量根据用户和(菜单KEY)Menu_key ,删除条码
	public int batchDeleteLabelCacheInfo(List<String> list);
	/**
	 * 获取条码缓存表已扫箱数
	 * @param params
	 * @return
	 */
	public int getLabelCacheInfoCount(Map<String, Object> params);
	
	/**
	 * 出入库过账保存条码记录
	 * @param params
	 * @return
	 */
	public int insertBarcodeLog(List<Map<String, Object>> params);
	
	/**
	 * 获取过账条码记录
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getBarcodeLog(Map<String, Object> params);
	
	/**
	 * 根据条码批量查询条码表
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getLabelInfoBatch(List<Map<String, Object>> params);
	
	public List<Map<String,Object>> getLabelCacheInfoNoPage(Map<String, Object> params);
}
