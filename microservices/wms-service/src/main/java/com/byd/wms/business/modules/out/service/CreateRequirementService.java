package com.byd.wms.business.modules.out.service;

import com.byd.utils.R;
import com.byd.wms.business.modules.out.entity.*;

import java.util.List;
import java.util.Map;

public interface CreateRequirementService {
	
	List<Map<String,Object>> getPlantBusinessTypes(Map<String,Object> params);
	
	/**
	 * 
	 * @param orders
	 * @return 校验失败的订单
	 * key:订单号 - value:不合格原因
	 * value 列表：
	 * 1. 未同步
	 * 2. 不存在
	 * 3. 无权限
	 * 4. 未发布或已关闭
	 * 5. 未发布
	 */
	Map<String,Integer> validProduceOrders(List<ProduceOrderVO> orders);
	
	/**
	 * 创建成本中心领料需求
	 * @param items
	 * @return 需求号
	 */
	String createRequirementFromICQCenter(List<CostCenterAO> items);
	
	void saveOutRequirementHead(WmsOutRequirementHeadEntity head);
	void saveOutRequirementItemBatch(List<WmsOutRequirementItemEntity> items);
	
	/**
	 *创建内部订单 CO工单领料需求
	 * @param items
	 */
	String saveInternalOrderRequirement(List<InternalOrderAO> items);
	
	/**
	 * 创建WBS元素发货需求
	 * @param items
	 * @return
	 */
	String createWBSElementOutRequirement(List<WbsElementAO> items);
	
	/**
	 * 根据条件，查询SAP生产订单
	 * @param referOrderLgort 参考订单库位
	 * @param filterZeroRequireLine 过滤零需求行
	 * @param queryOrderParamsList 查询参数列表（多个订单）
	 * @param IS_PRO_ORD_ADD_MAT 是否为生产订单补料
	 * @return
	 */
	List<Map<String,Object>> queryProducerOrders(String referOrderLgort,String filterZeroRequireLine,List<ProduceOrderVO> queryOrderParamsList,String werks,String whNumber,boolean IS_PRO_ORD_ADD_MAT,boolean IS_PRO_LINE_WAREHOUSE);
	
	/**
	 * 创建生产订单领料需求
	 * @return
	 */
	String createProduceOrderOutReq(List<CreateProduceOrderAO> orderItems) throws Exception;
	
	
	List<Map<String,Object>> queryMatUseing(String werks);
	
	/**
	 * 校验委外订单，返回不合格原因
	 * 1.不存在
	 * 2.未审批
	 * @param list
	 * @return
	 */
	Map<String,Integer> validateOutItems6(List<Map<String,Object>> list);
	
	/**
	 * 查询委外订单出库，行项目信息
	 * @param list
	 * @return
	 */
	List<Map<String,Object>> queryOutItems6(List<Map<String,Object>> list);
	
	/**
	 * 创建委外订单出库需求
	 * @param list
	 * @return
	 */
	String outReqCreate6(List<Map<String,Object>> list);
	
	/**
	 * 创建SAP销售单出库需求
	 * @param list
	 * @return
	 */
	String outReqCreate7(List<Map<String,Object>> list);
	
	/**
	 * 校验UB转储单输入数据
	 * @param list
	 * @return
	 */
	Map<String,Integer> validateOutItem8(List<Map<String,Object>> list);
	
	/**
	 * 查询UB转储单出库需求数据源
	 * @param list
	 * @return
	 */
	List<Map<String,Object>> queryOutItem8(List<Map<String,Object>> list);
	/**
	 * 查询外部销售发货（251）
	 * @param list
	 * @return
	 */
	List<Map<String,Object>> queryOutItem10(List<Map<String,Object>> list);
	
	/**
	 * 创建外部销售发货（251）
	 * @param list
	 * @return
	 */
	String createReqOutItem10(List<Map<String,Object>> list);
	
	/**
	 * 创建UB转储单出库需求
	 * @param list
	 * @return
	 */
	public String createReqOutItem8(List<Map<String, Object>> list);
	
	/**
	 * 校验外部销售发货
	 * @param list
	 * @return
	 */
	public Map<String, Integer> validateOutItems10(List<Map<String, Object>> list,List<String> depts);
	
	public Double selectTotalStockQty(Map<String,Object> params);
	
	/**
	 * 创建工厂间调拨出库
	 * @param list
	 * @return
	 */
	public String createOutReq10(List<Map<String, Object>> list);
	
	/**
	 * 校验料号
	 * 如果该工厂启用了核销业务，
	 * 工厂+接收工厂+料号关联查询【WMS_HX_TO】表的HX_QTY_XF是否>0
	 * ，大于0时讲剩余料号及剩余核销数量体现在报错中
	 * @param list 传入料号 MATNR
	 * @return 料号 - 报错信息
	 */
	public Map<String,Object> checkMatrialsHx(List<Map<String, Object>> list);
	
	/**
	 * 库存地点调拨311—类型不转移
	 * @param list
	 * @return
	 */
	public String createOutReq13(List<Map<String, Object>> list);
	
	/**
	 * 校验 - 工厂间调拨发货（A303）
	 * @param list
	 * @return map key-value 凭证号 -  1.凭证不存在 2.凭证号已无可出库物料
	 */
	public Map<String,Integer> validateOutReq16(List<Map<String, Object>> list);
	
	/**
	 * 查询 - 工厂间调拨发货（A303）
	 * @param list
	 * @return
	 */
	public List<Map<String,Object>> queryOutReq16(List<Map<String, Object>> list);
	/**
	 * 创建 - 工厂间调拨发货（A303）
	 * @param list
	 * @return
	 */
	public String createOutReq16(List<Map<String, Object>> list);
	
	/**
	 * 校验 - SAP交货单销售发货（A311T）
	 * @param list
	 * @return
	 */
	public Map<String,Integer> validate17(List<Map<String, Object>> list);
	
	/**
	 * 查询 - SAP交货单销售发货（A311T）
	 * @param list
	 * @return
	 */
	public List<Map<String,Object>> query17(List<Map<String, Object>> list);
	
	/**
	 * 创建 - SAP交货单销售发货（A311T）
	 * @param list
	 * @return
	 */
	public String create17(List<Map<String, Object>> list);
	
	/**
	 * 扫描枪出库，查询
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> queryCoreLabel(Map<String,Object> params);

	/**
	 * 报废（551）
	 * @param list
	 * @return
	 */
	public String createOutReq18(List<Map<String, Object>> list);
	
	/**
	 * update更新需求
	 * @param params
	 */
	public void updateRequirement(List<Map<String,Object>> params);
	
	/**
	   * 扫描看板卡
	   * @param params
	   * @return
	   */	
	public String createOutReqPda311(List<Map<String, Object>> list);

	/**
	 * STO一步联动311
	 * @param list
	 * @return
	 */
	public Object createOutReq20(List<Map<String, Object>> list);

	/**
	 * 验证配送单
	 * 工厂间调拨301（总装）
	 * @param list
	 * @return
	 */
	public Map<String, Integer> validateOutReq21(List<Map<String, Object>> list);

	/**
	 * 查询 - 工厂间调拨301（总装）
	 * @param list
	 * @return
	 */
	public List<Map<String,Object>> queryOutReq21(List<Map<String, Object>> list);
	
	/**
	 *创建 -  工厂间调拨301（总装）
	 * @param list
	 * @return
	 */
	public String createOutReq21(List<Map<String, Object>> list);

	public String creteScddByUpload(List<CreateProduceOrderAO> entityList) throws Exception;
	
	/**
	 * 创建生产订单领料需求(含人料关系拆单)
	 * @return
	 */
	public String createProduceOrderOutReqSplit(List<CreateProduceOrderAO> orderItems) throws Exception;
	
	public String createRequirementFromICQCenterSplit(List<CostCenterAO> items);
	
	public String saveInternalOrderRequirementSplit(List<InternalOrderAO> items);
	
	public String outReqCreate6Split(List<Map<String, Object>> list);
	
	public String outReqCreate7Split(List<Map<String, Object>> list);
	
	public String createReqOutItem8Split(List<Map<String, Object>> list);
	
	public String createReqOutItem10Split(List<Map<String, Object>> list);
	
	public String createOutReq10Split(List<Map<String, Object>> list);
	
	public String createWBSElementOutRequirementSplit(List<WbsElementAO> items);
	
	public String createOutReq13Split(List<Map<String, Object>> list);
	
	public String createOutReq16Split(List<Map<String, Object>> list);
	
	public String create17Split(List<Map<String, Object>> list);
	
	public String createOutReq18Split(List<Map<String, Object>> list);
	
	public String createOutReqPda311Split(List<Map<String, Object>> list);
	
	public String createOutReq20Split(List<Map<String, Object>> list);
	
	public String createOutReq21Split(List<Map<String, Object>> list);
	
}
