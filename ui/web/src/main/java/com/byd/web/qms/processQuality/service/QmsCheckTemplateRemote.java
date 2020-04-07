package com.byd.web.qms.processQuality.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.byd.utils.R;

@FeignClient(name = "QMS-SERVICE")
public interface QmsCheckTemplateRemote {
	

	@RequestMapping(value = "/qms-service/processQuality/checkTemplate/previewExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody Map<String,Object> params);
    /**
     * 保存
     */
	@RequestMapping(value = "/qms-service/processQuality/checkTemplate/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	/**
     * 保存or更新
     */
	@RequestMapping(value = "/qms-service/processQuality/checkTemplate/saveOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveOrUpdate(@RequestBody Map<String, Object> params);
	/**
     * 分页查询抬头数据
     */
	@RequestMapping(value = "/qms-service/processQuality/checkTemplate/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryPage(@RequestBody Map<String, Object> params);
	/**
     * 查询明细数据
     */
	@RequestMapping(value = "/qms-service/processQuality/checkTemplate/queryDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryDetail(@RequestBody Map<String, Object> params);
	/**
     * 整体删除(抬头、行项目)
     */ 
	@RequestMapping(value = "/qms-service/processQuality/checkTemplate/delete/{tempNo}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@PathVariable("tempNo") String tempNo);
	/**
     * 删除行项目
     */ 
	@RequestMapping(value = "/qms-service/processQuality/checkTemplate/deleteItem/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteItem(@PathVariable("id") Long id);
}
