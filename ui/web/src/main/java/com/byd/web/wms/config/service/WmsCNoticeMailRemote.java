package com.byd.web.wms.config.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 
 * 控制标识配置
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsCNoticeMailRemote {
	/**
	 *
	 * 列表
	 */
	@RequestMapping(value = "/wms-service/config/noticemail/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params) ;

	@RequestMapping(value = "/wms-service/config/noticemail/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("id") Long id) ;
	/**
	 * 
	 * 保存
	 */
	@RequestMapping(value = "/wms-service/config/noticemail/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestParam Map<String, Object> params) ;
	
	/**
	 * 
	 * 修改
	 */
	@RequestMapping(value = "/wms-service/config/noticemail/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestParam Map<String, Object> params) ;
	
	/**
     * 单条记录删除
     * @param id
     */
    @RequestMapping(value = "/wms-service/config/noticemail/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestParam(value="id") Long id) ;
	
}
