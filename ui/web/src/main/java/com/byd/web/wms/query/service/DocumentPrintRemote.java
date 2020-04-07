package com.byd.web.wms.query.service;

import java.text.ParseException;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 单据打印通用控制器
 * @author develop01
 * @since 2018-12-13
 */

@FeignClient(name = "WMS-SERVICE")
public interface DocumentPrintRemote {
	
    
    /**
     * 收货进仓单打印预览
     *
     */
    @RequestMapping(value = "/wms-service/docPrint/inBoundPreview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> inBoundPreview(@RequestParam Map<String, Object> params) ;

    /**
     * 自制进仓单标签打印预览
     *
     */
    @RequestMapping(value = "/wms-service/docPrint/inInternalBoundPreview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> inInternalBoundPreview(@RequestParam Map<String, Object> params) ;
    
    /**
     * 自制进仓单列表打印预览
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ParseException 
     */
    @RequestMapping(value = "/wms-service/docPrint/inInternalBoundListPreview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> inInternalBoundListPreview(@RequestParam Map<String, Object> params) ;
    
    
    /**
     * 收料房退货打印标签
     * @param params
     * PageSize:0(默认)：A4；    1：A4高度减半
     */
    @RequestMapping(value = "/wms-service/docPrint/receiveRoomOutPrint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> receiveRoomOutPrint(@RequestParam Map<String, Object> params) ;
    @RequestMapping(value = "/wms-service/docPrint/wareHouseOutPrint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> wareHouseOutPrint(@RequestParam Map<String, Object> params) ;
    @RequestMapping(value = "/wms-service/docPrint/wareHouseOutPickupPrint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> wareHouseOutPickupPrint(@RequestParam Map<String, Object> params) ;
    /**
     * jis拣配单打印
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/docPrint/dispatchingJISPrintPreview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> dispatchingJISPrintPreview(@RequestParam Map<String, Object> params) ;
    /**
     * 非jis拣配单打印
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-service/docPrint/dispatchingFeiJISPrintPreview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> dispatchingFeiJISPrintPreview(@RequestParam Map<String, Object> params) ;
    
}
