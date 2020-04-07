package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCControlSearchEntity;

/**
 * 
 * 分配存储类型搜索顺序至控制标识
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCControlSearchRemote {
	/**
	 * 
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/config/controlsearch/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;
		
	@RequestMapping(value = "/wms-service/config/controlsearch/info", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
	public R info(@RequestParam(value="id") Long id) ;
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/config/controlsearch/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody WmsCControlSearchEntity wmsCControlSearch) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/wms-service/config/controlsearch/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsCControlSearchEntity wmsCControlSearch) ;
	
	/**
     * 单条记录删除
     */
    @RequestMapping(value = "/wms-service/config/controlsearch/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCControlSearchEntity wmsCControlSearch) ;
    
    @RequestMapping(value = "/wms-service/config/controlsearch/getControlFlag", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
	public R getControlFlag(@RequestBody WmsCControlSearchEntity wmsCControlSearch);
    
    @RequestMapping(value = "/wms-service/config/controlsearch/getAreaSearch", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
	public R getAreaSearch(@RequestBody WmsCControlSearchEntity wmsCControlSearch);
    
    @RequestMapping(value = "/wms-service/config/controlsearch/getOutRule", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
	public R getOutRule(@RequestBody WmsCControlSearchEntity wmsCControlSearch);
	
}
