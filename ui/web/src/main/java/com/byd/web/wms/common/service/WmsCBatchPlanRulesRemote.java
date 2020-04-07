package com.byd.web.wms.common.service;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCBatchPlanRulesRemote {
	
	@RequestMapping(value = "/wms-service/common/batchRules/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/common/batchRules/querybatchcode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryBatchCode(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/common/batchRules/listBatchOnly", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryBatchOnly(@RequestParam(value = "params") Map<String, Object> params);
	
	@RequestMapping(value = "/wms-service/common/batchRules/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R add(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 根据ID查询实体记录
     */
    @RequestMapping(value = "/wms-service/common/batchRules/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
    @RequestMapping(value = "/wms-service/common/batchRules/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestParam(value = "params") Map<String, Object> params);
	
    @RequestMapping(value = "/wms-service/common/batchRules/dels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deletes(@RequestParam(value = "ids")String ids);
    
    /**
     * 复制保存
     * @param entitys
     * @return
     */
	@RequestMapping(value = "/wms-service/common/batchRules/saveCopyData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveCopyData(@RequestBody List<Map<String, Object>> list);
}
