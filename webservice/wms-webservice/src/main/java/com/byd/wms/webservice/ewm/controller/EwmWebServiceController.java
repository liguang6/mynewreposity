package com.byd.wms.webservice.ewm.controller;

import com.byd.utils.R;
import com.byd.wms.webservice.ewm.service.EwmWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 条码共享 EWM台接口
 **/

@RestController
@RequestMapping("/ewmWebservice")
public class EwmWebServiceController {

    @Autowired
    private EwmWebService ewmWebService;

    /**
     *EWM提供接口, WMS发送标签变动信息
     */
    @RequestMapping("/sendLabel")
    public R sendLabel2Ewm(@RequestParam HashMap params){

        ewmWebService.sendLabel2Ewm(params);

        return R.ok();
    }


}
