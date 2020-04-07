package com.byd.wms.business.modules.out.service;


import java.util.List;
import java.util.Map;

/**
 * @auther: peng.yang8
 * @data: 2019/12/18
 * @description：
 */
public interface StoreOrderService {
    /**
     * 叫料接口
     * @param params
     * @return
     * @throws Exception
     */
    public String callMaterial(String params) throws Exception;
    /**
     * 接收第三方系统叫料需求
     */
    public Map<String, Object> receiveOtherSystemREQ(String params);
    /**
     * 创建出库需求(含人料关系拆单)
     */
    public String createOutStoreOrder(List<Map<String, Object>> addList) throws Exception;

    //结果返回
    String selectReturnMsg(String dispatching_no)throws Exception;

    //创建出库清单
    String selectOutStoreOrder(String requirement_no);
}
