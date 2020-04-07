package com.byd.wms.business.modules.out.service;

import java.util.List;
import java.util.Map;

import com.byd.wms.business.modules.out.entity.CreateProduceOrderAO;
import com.byd.wms.business.modules.out.entity.ProduceOrderVO;
/**
 * 线边仓领料
 * @author develop07
 *
 */
public interface ProduceLineWarehouseService {
	
	Map<String,Integer> validProduceOrders(List<ProduceOrderVO> produceOrders,List<String> depts);

	public String createProduceOrderOutReq(List<CreateProduceOrderAO> orderItems,String userName) throws Exception;
	
	public String createProduceOrderOutReqSplit(List<CreateProduceOrderAO> orderItems,String userName) throws Exception;
}
