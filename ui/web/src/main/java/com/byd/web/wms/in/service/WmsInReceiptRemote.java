package com.byd.web.wms.in.service;

import java.util.Map;

import com.byd.utils.PageUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.in.service.fallback.WmsInReceiptRemoteFallBackFactory;

@FeignClient(name = "WMS-SERVICE", fallbackFactory = WmsInReceiptRemoteFallBackFactory.class)
public interface WmsInReceiptRemote {
	
	/**
	 * 从SAP系统获取物料凭证信息
	 * @param materialdocument
	 * @param matdocumentyear
	 * @return
	 */
	@RequestMapping(value = "/wms-service/in/wmsinreceipt/listScmMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listScmMat(@RequestBody Map<String, Object> params);
	
    /**
     * 获取包装箱信息
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/in/wmsinreceipt/listSKInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R listSKInfo(@RequestBody Map<String, Object> params);
    
    
    /* PO采购订单收货：
    * 供应商根据采购订单数据，手工开送货单并送货到收料房，收料员收货后，用采购订单在系统做收货，产生事务记录，
    * 凭证记录（103）（过账到SAP），收料房库存，送检单，创建收货单，包装信息。
    * 根据工厂配置是否启用核销，如果启用核销还需判断采购订单行项目核销数据表是否有 还需核销数量，
    * 如果有，需要先做核销的实物收货，冲销核销的虚收数量。工厂配置不启用核销，就不需要检查核销数据。
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/listPOMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R listPOMat(@RequestBody Map<String,Object> params);
   
   /**
    * SAP交货单收货：
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/listOutMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R listOutMat(@RequestBody Map<String,Object> params);
   
   /**
    * 跨工厂采购订单收货
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/listPOCFMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R listPOCFMat(@RequestBody Map<String,Object> params);
   
   /**
    * 303调拨收货
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/list303Mat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R list303Mat(@RequestBody Map<String, Object> params);
   
   /**
    * 303调拨收货
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/list303AMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R  list303AMat(@RequestBody Map<String, Object> params);
   
   /**
    * SAP采购订单收料（A）
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/listPOAMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R  listPOAMat(@RequestBody Map<String, Object> params);
   
   /**
    * SAP交货单收料（A）
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/listOutAMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R  listOutAMat(@RequestBody Map<String, Object> params);
   
   /**
    * 收货确认
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/boundIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R boundIn(@RequestBody Map<String, Object> params);
   
   /**
    * 根据物料号获取物料信息（行文本、物料描述、是否紧急物料、是否危化品）
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/getMatInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public R getMatInfo(@RequestParam Map<String, Object> params);
   
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/info/{qty}")
   public R info(@PathVariable("qty") Long qty);
   
   /**
    * 获取云平台送货单数据
    * @param params
    * @return
    */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/listCloudMat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R listCloudMat(@RequestBody Map<String, Object> params);

    /**
     * 根据工厂和物料号获取SAP物料主数据信息
     * @param params
     * @return
     */
   @RequestMapping(value = "/wms-service/in/wmsinreceipt/getSAPMatDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getSAPMatDetail(@RequestParam(value = "params") Map<String, Object> params);

   @RequestMapping(value = "/wms-service/in/wmsinreceipt/exportExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   R exportExcel(@RequestParam(value = "params") Map<String, Object> params);
}
