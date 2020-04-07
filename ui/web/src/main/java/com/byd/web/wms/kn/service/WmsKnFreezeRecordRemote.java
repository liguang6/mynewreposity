package com.byd.web.wms.kn.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.PageUtils;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsKnFreezeRecordRemote {
	
	// 查询冻结、解冻库存
	@RequestMapping(value = "/wms-service/kn/freezerecord/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R list(@RequestBody Map<String, Object> params);
    // 冻结、解冻
	@RequestMapping(value = "/wms-service/kn/freezerecord/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R save(@RequestBody Map<String, Object> params);
    // 冻结、解冻记录查询
	@RequestMapping(value = "/wms-service/kn/freezerecord/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R query(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/kn/freezerecord/getDataByLabelNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getDataByLabelNo(@RequestBody Map<String, Object> params);
    
}
