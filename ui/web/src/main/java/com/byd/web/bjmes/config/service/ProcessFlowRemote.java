package com.byd.web.bjmes.config.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

/** 
 * @author 作者 tangjin 
 * @version 创建时间：2019年10月30日 下午4:51:17 
 * 类说明 加工流程
 */
@FeignClient(name = "BJMES-SERVICE")
public interface ProcessFlowRemote {
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/getNodeList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getNodeList(@RequestParam Map<String,String> map);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/saveProcessFlow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveProcessFlow(@RequestParam Map<String,Object> map);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/updateProcessFlow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R updateProcessFlow(@RequestParam Map<String,Object> map);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/deleteProcessFlow/{process_flow_code}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deleteProcessFlow(@PathVariable("process_flow_code") String process_flow_code);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/saveNode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveNode(@RequestParam Map<String,Object> map);
	
	@RequestMapping(value = "/bjmes-service/config/bjMesProcessFlow/deleteNode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteNode(@RequestParam Map<String,Object> map);

}
