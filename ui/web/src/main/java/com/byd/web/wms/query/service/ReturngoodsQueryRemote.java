package com.byd.web.wms.query.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 退货单记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-30 09:32:38
 */

@FeignClient(name = "WMS-SERVICE")
public interface ReturngoodsQueryRemote {
    /**
     * 退货查询(抬头)
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
    /**
     * 收料房退货查询
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/receiveRoomOutList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R receiveRoomOutList(@RequestParam Map<String, Object> params);
    /**
     * 库房退货查询
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/workshopReturnList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R warehouseOutList(@RequestParam Map<String, Object> params);
    /**
     * 车间退货查询
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/workshopReturnList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R workshopReturnList(@RequestParam Map<String, Object> params);
    /**
     * 明细
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/detail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R detail(@RequestParam Map<String, Object> params);
    /**
     * 删除关闭
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/del", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R del(@RequestParam Map<String, Object> params);
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/close", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R close(@RequestParam Map<String, Object> params);
    /**
     * 查询退货单类型
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/queryReturnDocTypeList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryReturnDocTypeList();
    /**
     * 查询退货类型
     */
    @RequestMapping(value = "/wms-service/query/returngoodsQuery/queryReturnTypeList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryReturnTypeList(@RequestParam(value="type") String type);
}
