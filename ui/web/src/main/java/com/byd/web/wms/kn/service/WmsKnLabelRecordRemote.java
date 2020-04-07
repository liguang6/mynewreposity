package com.byd.web.wms.kn.service;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCoreStorageSearchEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "WMS-SERVICE")
public interface WmsKnLabelRecordRemote {
	/**
     * 查询
     */
	@RequestMapping(value = "/wms-service/kn/labelRecord/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);

	/**
	 * 单条记录查询By ID
	 */
	@RequestMapping(value = "/wms-service/kn/labelRecord/info", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
	public R info(@RequestParam(value = "id") Long id);

	/**
	 * 新增标签页面查询
	 */
	@RequestMapping(value = "/wms-service/kn/labelRecord/poList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R poList(@RequestParam(value = "params") Map<String, Object> params);

	/**
	 *保存生成标签
	 */
	@RequestMapping(value = "/wms-service/kn/labelRecord/saveLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveLabel(@RequestParam(value = "params") Map<String, Object> params);

	/**
	 * 根据物料号查询物料数据
	 * @param werks
	 * @param mat
	 * @return
	 */
	@RequestMapping("wms-service/kn/labelRecord/mat")
	public R getMat(@RequestParam("werks")String werks,@RequestParam("mat") String mat);

	/**
	 * 更新
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/wms-service/kn/labelRecord/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody Map map);

	/**
	 * 软删BY ID
	 */
	@RequestMapping(value = "/wms-service/kn/labelRecord/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delById(@RequestBody Map map);

	@RequestMapping(value = "/wms-service/kn/labelRecord/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody Map map);

	@RequestMapping(value = "/wms-service/kn/labelRecord/saveByCf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveByCf(Map<String, Object> params);

	@RequestMapping(value = "/wms-service/kn/labelRecord/queryByCf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryByCf(Map<String, Object> params);

	@RequestMapping(value = "/wms-service/kn/labelRecord/getLabelList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	List<Map<String, Object>> getLabelList(@RequestBody Map<String, Object> params);
	
	/*
	 * 条码重复打印 更新有效期
	 */
	@RequestMapping(value = "/wms-service/kn/labelRecord/updateEffectDate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	List<Map<String, Object>> updateEffectDate(@RequestParam Map<String, Object> params) ;
}
