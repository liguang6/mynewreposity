package com.byd.web.zzjmes.config.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月11日 下午2:59:28 
 * 类说明 
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface MachineAssignRemote {
	@RequestMapping(value = "/zzjmes-service/machineAssign/getMachineAssignList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getMachineAssignList(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/machineAssign/insertMachineAssign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R insertMachineAssign(@RequestParam Map<String, Object> paramMap);
	
    @RequestMapping(value = "/zzjmes-service/machineAssign/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    
    @RequestMapping(value = "/zzjmes-service/machineAssign/updateMachineAssign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R updateMachineAssign(@RequestParam Map<String, Object> paramMap);
    
    @RequestMapping(value = "/zzjmes-service/machineAssign/delMachineAssign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delMachineAssign(@RequestParam Map<String, Object> paramMap);
    
    /**
     * 机台标签打印
     *
     */
    @RequestMapping(value = "/zzjmes-service/machineAssign/machinePreview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> machinePreview(@RequestParam Map<String, Object> params) ;
}
