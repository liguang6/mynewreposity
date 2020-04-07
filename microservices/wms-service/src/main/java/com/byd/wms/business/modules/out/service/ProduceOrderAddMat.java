package com.byd.wms.business.modules.out.service;

import java.util.List;
import java.util.Map;

import com.byd.wms.business.modules.out.entity.CreateProduceOrderAO;
import com.byd.wms.business.modules.out.entity.ProduceOrderVO;

/**
 * 生产订单补料 - service
 * @author develop07
 *
 */
public interface ProduceOrderAddMat {
	
	/**
	 * 校验生产订单有效性
	 * @param produceOrders
	 * @return
	 */
	Map<String,Integer> validProduceOrders(List<ProduceOrderVO> produceOrders,List<String> depts);
	
	/**
	 * 创建出库需求
	 * @param cList
	 * @return
	 */
	String createOutReq(List<CreateProduceOrderAO> cList,String staffNumber) throws Exception;
	
	/**
	 * 创建出库需求(含人料关系拆单)
	 * @param cList
	 * @return
	 */
	String createOutReqSplit(List<CreateProduceOrderAO> cList,String staffNumber) throws Exception;

}
