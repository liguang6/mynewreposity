package com.byd.web.common.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byd.web.common.staticFactory.LocaleLanguageFactory;
import com.byd.web.sys.entity.SysLocaleEntity;
import com.byd.web.sys.service.SysLocaleService;

/**
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("WmsLocal")
public class WmsLocalJobs {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private SysLocaleService sysLocaleService;
	
	@RequestMapping("/webLocalJobs.task")
	public void languageSync() {
		logger.info("---->WMS-WEB WmsLocalJobs languageSync Start");
		List<SysLocaleEntity> records = sysLocaleService.refreshModifyData();
		LocaleLanguageFactory.reloadData(records);
	}

}
