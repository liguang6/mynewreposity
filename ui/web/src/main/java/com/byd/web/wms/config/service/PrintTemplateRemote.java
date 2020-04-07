package com.byd.web.wms.config.service;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysRoleEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 打印模板管理
 */
@FeignClient(name = "WMS-SERVICE")
public interface PrintTemplateRemote {

	/**
	 * 模板列表
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/queryTemplate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryTemplate(@RequestParam Map<String, Object> params);

	/**
	 * 模板配置
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/queryTempConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryTempConfig(@RequestParam Map<String, Object> params);

	/**
	 * 删除配置
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/deleteConfig/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deleteConfig(@PathVariable("id") Long id);

	/**
	 * 删除模板
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/deleteTemplate/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deleteTemplate(@PathVariable("id") Long id);

	/**
	 * 新增配置
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/saveConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveConfig(@RequestBody Map<String, Object> params);

	/**
	 * 新增模板
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/saveTemplate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveTemplate(@RequestBody Map<String, Object> params);

	/**
	 * 修改配置
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/updateConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R updateConfig(@RequestBody Map<String, Object> params);

	/**
	 * 修改模板
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/updateTemplate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R updateTemplate(@RequestBody Map<String, Object> params);

	/**
	 * 查询字典中打印模板配置
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/getPrintTemplateBySysDict/{type}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getPrintTemplateBySysDict(@PathVariable("type") String type);

	/**
	 * 根据业务类型和工厂字段查询模板
	 */
	@RequestMapping(value = "/wms-service/config/printTemplate/getPrintTemplate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	List<Map<String, String>> getPrintTemplate(@RequestBody Map<String, Object> params);
}
