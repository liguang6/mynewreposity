package com.byd.admin.modules.job.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * WMS-WEB微服务提供国际化数据同步定时任务服务
 * @author develop01
 * @since 2019-04-19
 */
@FeignClient(name = "WEB")
public interface WmsWebJobsRemote {
	@RequestMapping(value = "/web/WmsLocal/webLocalJobs.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void languageSync() ;

	@RequestMapping(value = "/web/qms/common/exportQmsTestRecord.task", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void exportQmsTestRecord(@RequestParam(value = "params") String params);

}
