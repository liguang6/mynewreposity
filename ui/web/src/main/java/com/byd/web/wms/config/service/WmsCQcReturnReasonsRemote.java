package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCQcReturnReasonsEntity;


@FeignClient(name = "WMS-SERVICE")
public interface WmsCQcReturnReasonsRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wmscqcreturnreasons/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/wmscqcreturnreasons/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("id") Long id);

    /**
     * 保存
     */
	@RequestMapping(value = "/wms-service/config/wmscqcreturnreasons/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R add(@RequestBody WmsCQcReturnReasonsEntity wmsSapMaterial);

    /**
     * 修改
     */
	@RequestMapping(value = "/wms-service/config/wmscqcreturnreasons/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCQcReturnReasonsEntity wmsSapMaterial);

    /**
     * 删除
     */
	@RequestMapping(value = "/wms-service/config/wmscqcreturnreasons/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);
}
