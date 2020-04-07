package com.byd.web.wms.config.service;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCOutRuleDetailEntity;
import com.byd.web.wms.config.entity.WmsCOutRuleEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 
 * 出库规则配置
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCOutRuleDetailRemote {
	/**
	 *
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/config/outruleDetail/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;

	@RequestMapping(value = "/wms-service/config/outruleDetail/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@RequestParam(value = "id") Long id) ;
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/config/outruleDetail/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody WmsCOutRuleDetailEntity wmsCOutRule) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/wms-service/config/outruleDetail/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsCOutRuleDetailEntity wmsCOutRule) ;
	
	/**
     * 单条记录删除
     */
    @RequestMapping(value = "/wms-service/config/outruleDetail/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCOutRuleDetailEntity wmsCOutRule) ;
	
}
