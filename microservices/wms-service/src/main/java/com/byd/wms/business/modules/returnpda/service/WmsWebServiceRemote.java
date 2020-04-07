package com.byd.wms.business.modules.returnpda.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.attribute.standard.Media;
import java.awt.*;
import java.util.Map;

/**
 * @author liguang6
 * @date 2020/1/16 15:50
 * @title 立库余料退回接口
 */

@FeignClient(name="WMS-WEBSERVICE")
public interface WmsWebServiceRemote {

    /**
     * 将立库需要过账信息通过webService接口传给立库系统
     * @param params
     * @return
     */
    @RequestMapping(value = "/wms-webservice/supMaReturn/sendSupMaData",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String,Object> getWebServiceReWare(@RequestParam(value = "ReWare") Map<String,Object> params);
}
