package com.byd.web.runner;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.byd.web.common.staticFactory.LocaleLanguageFactory;
import com.byd.web.sys.entity.SysLocaleEntity;
import com.byd.web.sys.service.SysLocaleService;


/**
 * @author ren.wei3
 *
 */
@Component
public class StartLoadingApplicationRunner implements ApplicationRunner{
	@Autowired
    private SysLocaleService sysLocaleService;
	
	@Override
    public void run(ApplicationArguments args) throws Exception {
		//项目启动时，从数据库加载国际化配置
		List<SysLocaleEntity> records = sysLocaleService.getAllLocale();
		LocaleLanguageFactory.reloadData(records);

    }
}
