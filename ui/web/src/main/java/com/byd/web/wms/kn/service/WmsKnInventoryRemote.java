package com.byd.web.wms.kn.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsKnInventoryRemote {
	
	/**
     * 列表
     */
	@RequestMapping(value = "/wms-service/kn/inventory/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestBody Map<String, Object> params);
    /**
     * 打印、导出、明细查询BY inventoryNo
     */
	@RequestMapping(value = "/wms-service/kn/inventory/getItemByInventoryNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getItemByInventoryNo(@RequestBody String INVENTORY_NO);
	/**
     * 打印数据查询BY inventoryNo
     */
	@RequestMapping(value = "/wms-service/kn/inventory/print", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R print(@RequestBody Map<String, Object> params);
    /**
     * 创建盘点表
     */
	@RequestMapping(value = "/wms-service/kn/inventory/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R create(@RequestBody Map<String, Object> params);
    
    /**
     * 盘点结果录入页面查询
     */
	@RequestMapping(value = "/wms-service/kn/inventory/getInventoryResult", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getInventoryResult(@RequestBody Map<String, Object> params);
    /**
     * 盘点结果录入保存
     */
	@RequestMapping(value = "/wms-service/kn/inventory/saveResult", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveResult(@RequestBody Map<String, Object> params);
    // 盘点结果导入保存
	@RequestMapping(value = "/wms-service/kn/inventory/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody Map<String, Object> params);
	// Excel导入盘点结果
	@RequestMapping(value = "/wms-service/kn/inventory/previewExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody Map<String, Object> params);
	/**
     * 盘点结果确认页面查询
     */
	@RequestMapping(value = "/wms-service/kn/inventory/getInventoryConfirm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getInventoryConfirm(@RequestBody Map<String, Object> params);
    /**
     * 盘点结果确认保存
     */
	@RequestMapping(value = "/wms-service/kn/inventory/saveConfirm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveConfirm(@RequestBody Map<String, Object> params);
    /**
     * 获取库位
     */
	@RequestMapping(value = "/wms-service/kn/inventory/lgortlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R lgortlist(@RequestBody Map<String, Object> params);
    /**
     * 加载区域管理员
     */
	@RequestMapping(value = "/wms-service/kn/inventory/getWhManagerList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWhManagerList(@RequestBody Map<String, Object> params);
}
