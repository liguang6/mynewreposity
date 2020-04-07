package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCSapUserEntity;


@FeignClient(name = "WMS-SERVICE")
public interface WmsCSapUserRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/CSapUser/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/CSapUser/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@RequestParam("id") Long id);

    /**
     * 保存
     */
	@RequestMapping(value = "/wms-service/config/CSapUser/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R add(@RequestBody WmsCSapUserEntity wmsSapMaterial);

    /**
     * 修改
     */
	@RequestMapping(value = "/wms-service/config/CSapUser/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCSapUserEntity wmsSapMaterial);

    /**
     * 删除
     */
	@RequestMapping(value = "/wms-service/config/CSapUser/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);
}
