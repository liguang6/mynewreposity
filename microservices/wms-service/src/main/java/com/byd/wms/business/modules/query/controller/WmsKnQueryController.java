package com.byd.wms.business.modules.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.query.service.WmsKnQueryService;
/**
 * 库内查询（冻结、盘点、移储）
 *
 * @author cscc tangj
 * @email 
 * @date 2018-11-21 15:52:08
 */
@RestController
@RequestMapping("query/knQuery")
public class WmsKnQueryController {
    @Autowired
    private WmsKnQueryService wmsKnQueryService;
    /**
     * 查询移储记录
     */
    @RequestMapping("/getStorageMoveList")
    public R getStorageMoveList(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsKnQueryService.queryStorageMovePage(params);
        return R.ok().put("page", page);
    }
    /**
     * 查询盘点记录
     */
    @RequestMapping("/getInventoryList")
    public R getInventoryList(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsKnQueryService.queryInventoryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 查询冻结记录
     */
    @RequestMapping("/getFreezeRecordList")
    public R getFreezeRecordList(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsKnQueryService.queryFreezeRecordPage(params);
        return R.ok().put("page", page);
    }

}
