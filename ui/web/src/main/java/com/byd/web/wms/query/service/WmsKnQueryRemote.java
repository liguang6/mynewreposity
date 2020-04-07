package com.byd.web.wms.query.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
/**
 * 库内查询（冻结、盘点、移储）
 *
 * @author cscc tangj
 * @email 
 * @date 2018-11-21 15:52:08
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsKnQueryRemote {
    /**
     * 查询移储记录
     */
    @RequestMapping(value = "/wms-service/query/knQuery/getStorageMoveList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getStorageMoveList(@RequestParam Map<String, Object> params);
    /**
     * 查询盘点记录
     */
    @RequestMapping(value = "/wms-service/query/knQuery/getInventoryList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getInventoryList(@RequestParam Map<String, Object> params);
    /**
     * 查询冻结记录
     */
    @RequestMapping(value = "/wms-service/query/knQuery/getFreezeRecordList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getFreezeRecordList(@RequestParam Map<String, Object> params);

}
