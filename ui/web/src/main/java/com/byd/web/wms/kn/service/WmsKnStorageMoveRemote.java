package com.byd.web.wms.kn.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.google.gson.Gson;

@FeignClient(name = "WMS-SERVICE")
public interface WmsKnStorageMoveRemote {
	/**
     * 查询移储记录
     */
	@RequestMapping(value = "/wms-service/kn/storageMove/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestBody Map<String, Object> params);
    /**
     * 查询库存信息（用于移储操作）
     */
	@RequestMapping(value = "/wms-service/kn/storageMove/getStockList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getStockList(@RequestBody Map<String, Object> params);
    /**
     * 保存移储记录
     */
	@RequestMapping(value = "/wms-service/kn/storageMove/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody List<Map<String, Object>> params);
	/**
     * 校验储位
     */
	@RequestMapping(value = "/wms-service/kn/storageMove/checkBin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkBin(@RequestBody Map<String, Object> params);
    
}
