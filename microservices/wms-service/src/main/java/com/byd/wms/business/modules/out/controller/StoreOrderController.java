package com.byd.wms.business.modules.out.controller;

import com.alibaba.fastjson.JSONObject;
import com.byd.wms.business.modules.out.service.StoreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/18
 * @description：出库单创建
 */

@RestController
@RequestMapping("/webservices/storeorder")
public class StoreOrderController {

    @Autowired
    StoreOrderService storeOrderService;

    @RequestMapping("/callMaterials")
    public String callMaterials(@RequestBody String params) throws Exception {
        try {
        String rMsg = storeOrderService.callMaterial(params);
       return rMsg;

        }catch (Exception e){
            Map<String, Object> remap = new HashMap<>();
            remap.put("MSGTY","E");
            remap.put("MSGTX",e.getMessage());
            return JSONObject.toJSONString(remap);
        }
    }
}
