package com.byd.wms.business.modules.config.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.modules.config.service.PrintTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 模板管理
 *
 */
@RestController
@RequestMapping("/config/printTemplate")
public class PrintTemplateController {

	@Autowired
	private PrintTemplateService printTemplateService;

	/**
	 * 查询模板
	 */
	@RequestMapping("/queryTemplate")
	public R queryTemplate(@RequestParam Map<String, Object> params){

		PageUtils page = printTemplateService.queryTemplate(params);

		return R.ok().put("page", page);
	}

	/**
	 * 查询配置
	 */
	@RequestMapping("/queryTempConfig")
	public R queryTempConfig(@RequestParam Map<String, Object> params){

		PageUtils page = printTemplateService.queryTempConfig(params);

		return R.ok().put("page", page);
	}


	/**
	 * 查询字典中打印模板的配置
	 * @return
	 */
	@RequestMapping("/getPrintTemplateBySysDict/{type}")
	public R getPrintTemplateBySysDict(@PathVariable("type") String type){
		List<Map<String,Object>> list =  printTemplateService.getPrintTemplateBySysDict(type);
		return R.ok().put("data", list);
	}

	/**
	 * 查询模板
	 */
	@RequestMapping("/getPrintTemplate")
	public List<Map<String, Object>> getPrintTemplate(@RequestBody Map<String, Object> params){

		List<Map<String, Object>> list = printTemplateService.getPrintTemplate(params);

		return list;
	}

	/**
	 * 保存配置
	 */
	@RequestMapping("/saveConfig")
	public R saveConfig(@RequestBody Map<String, Object> params){

		printTemplateService.saveConfig(params);

		return R.ok();
	}

	/**
	 * 保存模板
	 */
	@RequestMapping("/saveTemplate")
	public R saveTemplate(@RequestBody Map<String, Object> params){

		printTemplateService.saveTemplate(params);

		return R.ok();
	}

	/**
	 * 修改配置
	 */
	@RequestMapping("/updateConfig")
	public R update(@RequestBody Map<String, Object> params){

		printTemplateService.updateConfig(params);

		return R.ok();
	}

	/**
	 * 修改模板
	 */
	@RequestMapping("/updateTemplate")
	public R updateTemplate(@RequestBody Map<String, Object> params){

		printTemplateService.updateTemplate(params);

		return R.ok();
	}

	/**
	 * 删除配置
	 */
	@RequestMapping("/deleteConfig/{id}")
	public R deleteConfig(@PathVariable("id") Long id){

		int result = printTemplateService.deleteConfig(id);

		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
	}

	/**
	 * 删除模板
	 */
	@RequestMapping("/deleteTemplate/{id}")
	public R deleteTemplate(@PathVariable("id") Long id){

		int result = printTemplateService.deleteTemplate(id);

		return result>0 ? R.ok(): R.error("删除失败，请先删除该模板的所有配置！");
	}
}
