package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCOutRuleEntity;

/**
 * 
 * 出库规则配置
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCOutRuleRemote {
	/**
	 * 
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/config/outrule/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;
		
	@RequestMapping(value = "/wms-service/config/outrule/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@RequestParam(value="id") Long id) ;
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/config/outrule/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody WmsCOutRuleEntity wmsCOutRule) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/wms-service/config/outrule/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsCOutRuleEntity wmsCOutRule) ;
	
	/**
     * 单条记录删除
     */
    @RequestMapping(value = "/wms-service/config/outrule/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCOutRuleEntity wmsCOutRule) ;
	
}
