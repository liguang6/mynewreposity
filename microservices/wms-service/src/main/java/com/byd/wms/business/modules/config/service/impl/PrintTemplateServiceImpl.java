package com.byd.wms.business.modules.config.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.PrintTemplateDao;
import com.byd.wms.business.modules.config.service.PrintTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * 角色service
 * @author develop03
 *
 */
@Service("sysRoleService")
public class PrintTemplateServiceImpl implements PrintTemplateService {

	@Autowired
	private PrintTemplateDao printTemplateDao;


	@Override
	public PageUtils queryTemplate(Map<String, Object> params) {

		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> records = printTemplateDao.queryTemplate(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(records);
		page.setTotal(printTemplateDao.queryTemplateCount(params));
		page.setSize(Integer.valueOf(pageSize));
		if(params.get("flag") == null || !params.get("flag") .equals("true")){
			page.setSize(Integer.MAX_VALUE);
		}
		return new PageUtils(page);
	}

	@Override
	public PageUtils queryTempConfig(Map<String, Object> params) {

		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> records = printTemplateDao.queryTempConfig(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(records);
		page.setTotal(printTemplateDao.queryTempConfigCount(params));
		page.setSize(Integer.valueOf(pageSize));
		if(params.get("flag") == null || !params.get("flag") .equals("true")){
			page.setSize(Integer.MAX_VALUE);
		}
		return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getPrintTemplateBySysDict(String type) {
		return printTemplateDao.getPrintTemplateBySysDict(type);
	}

	@Override
	public List<Map<String, Object>> getPrintTemplate(Map<String, Object> params) {
		return printTemplateDao.getPrintTemplate(params);
	}

	@Override
	public int deleteConfig(Long id) {

		return printTemplateDao.deleteConfig(id);
	}

	@Override
	public int deleteTemplate(Long id) {

		return printTemplateDao.deleteTemplate(id);
	}

	@Override
	public void saveConfig(Map<String, Object> params) {

		printTemplateDao.saveConfig(params);
	}

	@Override
	public void saveTemplate(Map<String, Object> params) {

		printTemplateDao.saveTemplate(params);
	}

	@Override
	public void updateConfig(Map<String, Object> params) {

		printTemplateDao.updateConfig(params);
	}

	@Override
	public void updateTemplate(Map<String, Object> params) {

		printTemplateDao.updateTemplate(params);
	}

}
