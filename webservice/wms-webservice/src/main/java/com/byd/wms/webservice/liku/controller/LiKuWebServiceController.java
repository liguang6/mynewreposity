package com.byd.wms.webservice.liku.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.wms.webservice.liku.service.LiKuWebService;

@RestController
@RequestMapping("/likuwebservice")
public class LiKuWebServiceController {
	@Autowired
    private LiKuWebService liKuWebService;
	@RequestMapping("/liKuOutInstruction")
    public R deliveryWmsService(@RequestParam HashMap params){
        HashMap hm = liKuWebService.liKuOutInstruction(params);
        if(hm.get("STATUS").equals("E")){
            return R.error("调用接口失败！" + hm.get("MSG"));
        }
        return R.ok();
    }
}
