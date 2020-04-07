package com.byd.wms.webservice.httpclient.controller;

import com.byd.wms.webservice.httpclient.entity.HttpClientResultVO;
import com.byd.wms.webservice.httpclient.util.HttpClientUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @ClassName
 * @Author rain
 * @Date 2019年11月12日09:14:43
 * @Description HTTPClient方式接口
 **/

@RestController
@RequestMapping("/httpclientservice")
public class HttpClientServiceController {



    /**
	 * @Author rain
     * HTTPClient接口请求,通过json串
     * param:  {"url":"xxx","jsonStr":"xxxxx"}
     * @return
     */
    @RequestMapping("/doPostWithJsonStr")
    public HttpClientResultVO doPostWithJsonStr(@RequestBody HashMap paramMap) throws Exception {

    	try {
			String url = (String) paramMap.get("url");
			String jsonStr = (String) paramMap.get("jsonStr");
			if (url == null || jsonStr == null) {
				return new HttpClientResultVO(1, "云平台接口交互传输参数丢失！");
			}
			return HttpClientUtil.doPost(url, jsonStr);
		}catch (Exception e){
			return new HttpClientResultVO(1,"云平台接口交互发生异常！");
		}

    }

}
