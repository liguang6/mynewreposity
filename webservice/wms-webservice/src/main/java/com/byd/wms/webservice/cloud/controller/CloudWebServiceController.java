package com.byd.wms.webservice.cloud.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.webservice.cloud.service.CloudWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date 2019/5/15
 * @Description 云平台接口
 **/

@RestController
@RequestMapping("/webservice")
public class CloudWebServiceController {

    @Autowired
    private CloudWebService cloudWebService;

    /**
     * 读取送货单
     * param:  {"deliveryNo":"xxx"}
     * @return
     */
    @RequestMapping("/deliveryWmsService")
    public R deliveryWmsService(@RequestParam HashMap params){
        //System.err.println("deliveryWmsService");
        /*HashMap params = new HashMap();
        JSONObject js= new JSONObject();
        js.put("deliveryNo","N190500000000009");
        params.put("param",js.toString());*/
        HashMap hm = cloudWebService.deliveryWmsService(params);
        if(hm.get("STATUS").equals("E")){
            return R.error("调用接口失败！" + hm.get("MSG"));
        }
        return R.ok();
    }

    /**
     * 读取云平台送货单明细数据
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDeliveryDetail")
    public R getDeliveryDetail(@RequestBody HashMap params){
        //System.err.println("deliveryWmsService");
        /*HashMap params = new HashMap();
        JSONObject js= new JSONObject();
        js.put("deliveryNo","N190400000000015");
        params.put("param",js.toString());
        HashMap hm = cloudWebService.deliveryWmsService(params);*/
    	JSONObject js= new JSONObject();
    	 js.put("deliveryNo",params.get("deliveryNo"));
    	 HashMap _param = new HashMap();
    	 _param.put("param", js.toString());
    	 HashMap hm = cloudWebService.getDeliveryData(_param);
        if(hm.get("STATUS").equals("E")){
            return R.error("调用接口失败！" + hm.get("MSG"));
        }
        return R.ok().put("DATA",hm.get("DATA"));
    }

    @ResponseBody
    @RequestMapping("/getDeliveryDetailByBarcode")
    public R getDeliveryDetailByBarcode(@RequestBody HashMap params){
    	JSONObject js= new JSONObject();
	   	js.put("BARCODE_NO",params.get("BARCODE_NO"));
	   	HashMap _param = new HashMap();
	   	_param.put("param", "[" + js.toString() + "]");
		HashMap hm = cloudWebService.getDeliveryDataByBarcode(_param);
		if (hm.get("STATUS").equals("E")) {
			return R.error("调用接口失败！" + hm.get("MSG"));
		}
		return R.ok().put("DATA", hm.get("DATA"));
    }

    /**
     *云平台提供接口，WMS返回信息 ,信息内容
     *
     * param: [ {"deliveryNo":"xxx","posnr":"","flag":"","qty":"","user":"","date":""}]
     * （送货单号deliveryNo、
     * 送货单行项目posnr、
     * 返回标识flag（1送检（103）、2收货（105）），
     * 数量qty、
     * 操作人user、
     * 操作时间date）
     *
     * @return
     */
    @RequestMapping("/sendDeliveryData")
    public R sendDeliveryData(@RequestBody HashMap params){
        /*HashMap params = new HashMap();
        JSONArray jsonArray = new JSONArray();
        JSONObject js= new JSONObject();
        js.put("deliveryNo","N190400000000015");
        js.put("posnr","00001");
        js.put("flag","0");
        js.put("qty","100");
        js.put("user","admin");
        js.put("date","2019-05-10 14:55:53");
        jsonArray.add(js);
        params.put("param",jsonArray.toString());*/
        HashMap hm = cloudWebService.sendDeliveryData(params);
        if(hm.get("STATUS").equals("E")){
            return R.error("调用接口失败！" + hm.get("MSG"));
        }
        return R.ok().put("STATUS",hm.get("STATUS")).put("DATA",hm.get("DATA"));
    }


}
