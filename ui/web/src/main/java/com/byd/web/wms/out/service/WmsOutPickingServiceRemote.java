package com.byd.web.wms.out.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/**
 * 需求拣配
 * @author ren.wei3
 *
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsOutPickingServiceRemote {

	/**
     * 查询
     */
	@RequestMapping(value = "/wms-service/out/picking/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 推荐
     */
	@RequestMapping(value = "/wms-service/out/picking/recommend", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R recommend(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 下架
     */
	@RequestMapping(value = "/wms-service/out/picking/picking", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R picking(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 获取库存
     */
	@RequestMapping(value = "/wms-service/out/picking/getStockInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getStockInfo(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
	 * 获取配送标签数据(总装)
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/wms-service/out/picking/shippingLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryShippingLabel(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
	 * 获取配送标签数据(CS)
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/wms-service/out/picking/shippingLabelcs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryShippingLabelcs(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
	 * 获取关键零部件标签
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/wms-service/out/picking/keyPartsLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R querykeyPartsLabel(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
	 * 保存配送标签
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/wms-service/out/picking/saveShippingLabel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveShippingLabel(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 拣配清单
     */
	@RequestMapping(value = "/wms-service/out/picking/pickList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R pickList(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
     * 获取需求头
     */
	@RequestMapping(value = "/wms-service/out/picking/getReq", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getReq(@RequestParam(value = "params") Map<String, Object> params);
	
}
