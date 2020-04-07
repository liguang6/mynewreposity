package com.byd.web.wms.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCKanbanInfoRemote {
	/**
	 *
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/config/KanbanInfo/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;

	 
	@RequestMapping(value = "/wms-service/config/KanbanInfo/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R query(@RequestParam Map<String, Object> params) ;

	 
	@RequestMapping(value = "/wms-service/config/KanbanInfo/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("id") Long id) ;
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/config/KanbanInfo/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestParam Map<String, Object> params) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/wms-service/config/KanbanInfo/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestParam Map<String, Object> params) ;
	
	/**
    * 单条记录删除
    * @param id
    */
   @RequestMapping(value = "/wms-service/config/KanbanInfo/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R delById(@RequestParam(value="id") Long id) ;
	
}
