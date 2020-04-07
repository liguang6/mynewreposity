package com.byd.wms.webservice.common.remote;

import com.byd.utils.R;
import com.byd.wms.webservice.common.fallback.BusinessRemoteFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date 2019/05/17
 * @Description 调用 WMS-SERVICE 模块接口服务
 **/

@FeignClient(name = "WMS-SERVICE",fallback = BusinessRemoteFallBackFactory.class)
public interface BusinessRemote {

    @RequestMapping(value = "/wms-service/webservices/handover/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R handover(@RequestParam(value = "params") Map<String, Object> params);
    
    
    @RequestMapping(value = "/wms-service/report/materialStore/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/common/saveWMSDoc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveWMSDoc(@RequestBody Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/common/doSapPost", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String doSapPost(@RequestBody Map<String, Object> params);

}
