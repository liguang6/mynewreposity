package com.byd.web.wms.config.controller;

import com.byd.utils.R;
import com.byd.web.wms.config.service.PrintTemplateRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 打印模板管理
 */
@RestController
@RequestMapping("config/printTemplate")
public class PrintTemplatController {
	@Autowired
	private PrintTemplateRemote printTemplateRemote;

	/**
	 * 查询模板列表
	 */
	@RequestMapping("/queryTemplate")
	public R queryTemplate(@RequestParam Map<String, Object> params){
		return printTemplateRemote.queryTemplate(params);
	}

	/**
	 * 查询模板配置
	 */
	@RequestMapping("/queryTempConfig")
	public R queryTempConfig(@RequestParam Map<String, Object> params){
		return printTemplateRemote.queryTempConfig(params);
	}

	/**
	 * 查询字典中打印模板的配置
	 * @return
	 */
	@RequestMapping("/getPrintTemplateBySysDict/{type}")
	public R getPrintTemplateBySysDict(@PathVariable("type") String type){
		return printTemplateRemote.getPrintTemplateBySysDict(type);
	}

	/**
	 * 新增配置
	 */
	@RequestMapping("/saveConfig")
	public R saveConfig(@RequestBody Map<String, Object> params){
		return printTemplateRemote.saveConfig(params);
	}

	/**
	 * 新增模板
	 */
	@RequestMapping("/saveTemplate")
	public R saveTemplate(@RequestBody Map<String, Object> params){
		return printTemplateRemote.saveTemplate(params);
	}

	/**
	 * 修改配置
	 */
	@RequestMapping("/updateConfig")
	public R updateConfig(@RequestBody Map<String, Object> params){
		return printTemplateRemote.updateConfig(params);
	}

	/**
	 * 修改模板
	 */
	@RequestMapping("/updateTemplate")
	public R updateTemplate(@RequestBody Map<String, Object> params){
		return printTemplateRemote.updateTemplate(params);
	}

	/**
	 * 删除配置
	 */
	@RequestMapping("/deleteConfig/{id}")
	public R deleteConfig(@PathVariable("id") Long id){
		return printTemplateRemote.deleteConfig(id);
	}

	/**
	 * 删除模板
	 */
	@RequestMapping("/deleteTemplate/{id}")
	public R deleteTemplate(@PathVariable("id") Long id){
		return printTemplateRemote.deleteTemplate(id);
	}
}
