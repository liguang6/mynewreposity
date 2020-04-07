package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCControlFlagEntity;

/**
 * 
 * 控制标识配置
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCControlFlagRemote {
	/**
	 * 
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/config/controlflag/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;
		
	@RequestMapping(value = "/wms-service/config/controlflag/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@RequestParam(value="id") Long id) ;
	
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/config/controlflag/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody WmsCControlFlagEntity wmsCControlFlag) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/wms-service/config/controlflag/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody WmsCControlFlagEntity wmsCControlFlag) ;
	
	/**
     * 单条记录删除
     */
    @RequestMapping(value = "/wms-service/config/controlflag/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCControlFlagEntity wmsCControlFlag) ;
	
}
