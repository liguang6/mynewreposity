package com.byd.web.wms.common.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface CommonRemote {
	
    /**
     * 列表
     */
    @RequestMapping(value = "/wms-service/common/getPlantList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPlantList(@RequestParam(value = "werks") String werks);
	
    @RequestMapping(value = "/wms-service/common/getWhList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWhList(@RequestParam(value = "whNumber") String whNumber,@RequestParam(value = "language") String language);




    @RequestMapping(value = "/wms-service/common/getVendorList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getVendorList(@RequestParam(value = "lifnr") String lifnr,@RequestParam(value = "werks") String werks);
    
    @RequestMapping(value = "/wms-service/common/getVendor", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getVendor(@RequestParam(value = "lifnr") String lifnr);
    
	@RequestMapping(value = "/wms-service/common/getMaterialList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getMaterialList(@RequestParam(value = "werks") String werks,@RequestParam(value = "matnr") String matnr);
    
    /**
     * 根据权限获取业务类型下拉列表
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/common/getBusinessList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getBusinessList(@RequestParam Map<String, Object> params);
	
    /**
     * 根据工厂代码、库存类型查询库位列表
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/common/getLoList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getLoList(@RequestParam Map<String,Object> params);
    
    /**
     * 根据工厂代码查询收料房存放区列表
     * @return
     */
    @RequestMapping(value = "/wms-service/common/getGrAreaList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getGrAreaList(@RequestParam(value = "WERKS") String WERKS);
    
    /**
	 * 根据工厂号获取全部仓库信息@YK20180829
	 * @param WERKS
	 */
    @RequestMapping(value = "/wms-service/common/getWhDataByWerks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWhDataByWerks(@RequestParam(value = "WERKS") String WERKS);
    
    @RequestMapping(value = "/wms-service/common/getReasonData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getReasonData(@RequestParam(value = "REASON_DESC") String REASON_DESC);
    
    @RequestMapping(value = "/wms-service/common/getPlantSetting", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPlantSetting(@RequestParam(value = "WH_NUMBER") String WH_NUMBER);
    
    @RequestMapping(value = "/wms-service/common/getDictList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getDictList(@RequestParam(value = "type") String type);

    @RequestMapping(value = "/wms-service/common/getControlFlagList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getControlFlagList(@RequestParam(value = "params") Map<String, Object> params);

    @RequestMapping(value = "/wms-service/common/getAllLoList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getAllLoList(@RequestParam Map<String,Object> params);
    
    @RequestMapping(value = "/wms-service/common/getMatManager", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getMatManager(@RequestParam Map<String,Object> params);
    
    @RequestMapping(value = "/wms-service/common/getMaterialInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getMaterialInfo(@RequestParam Map<String, Object> map);

    @RequestMapping(value = "/wms-service/common/getMatStockInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getMatStockInfo(@RequestParam Map<String, Object> map);

    @RequestMapping(value = "/wms-service/common/checkPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkPassword(@RequestParam Map<String, Object> params);

}
