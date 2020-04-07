package com.byd.admin.modules.sys.remote;

import com.byd.admin.modules.httpclient.entity.HttpClientResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author rain
 * @date 2019年11月12日10:38:52
 * @description HttpClient接口
 */
@FeignClient(name = "WMS-WEBSERVICE")
public interface YptMenuRemote {

	/**
	 * @author rain
	 * @date 2019年11月12日10:38:52
	 * @description 云平台菜单同步，HttpClient接口，jsonStr请求参数
	 */
	@RequestMapping(value = "/wms-webservice/httpclientservice/doPostWithJsonStr", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	HttpClientResultVO doPostWithJsonStr(@RequestBody Map<String,Object> paramMap);

}
