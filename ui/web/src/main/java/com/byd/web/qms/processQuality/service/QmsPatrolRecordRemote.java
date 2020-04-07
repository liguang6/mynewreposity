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
public interface QmsPatrolRecordRemote {
	
    /**
     * 保存
     */
	@RequestMapping(value = "/qms-service/processQuality/patrolRecord/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	/**
     * 保存or更新
     */
	@RequestMapping(value = "/qms-service/processQuality/patrolRecord/saveOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveOrUpdate(@RequestBody Map<String, Object> params);
	/**
     * 分页查询抬头数据
     */
	@RequestMapping(value = "/qms-service/processQuality/patrolRecord/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryPage(@RequestBody Map<String, Object> params);
	/**
     * 查询模板数据
     */
	@RequestMapping(value = "/qms-service/processQuality/patrolRecord/getTemplateList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getTemplateList(@RequestBody Map<String, Object> params);
	/**
     * 删除
     */ 
	@RequestMapping(value = "/qms-service/processQuality/patrolRecord/delete/{patrolRecordNo}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@PathVariable("patrolRecordNo") String patrolRecordNo);
	
	/**
     * 查询明细数据
     */
	@RequestMapping(value = "/qms-service/processQuality/patrolRecord/queryDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryDetail(@RequestBody Map<String, Object> params);
	
}
