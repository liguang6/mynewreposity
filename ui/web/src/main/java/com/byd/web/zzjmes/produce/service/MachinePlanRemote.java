package com.byd.web.zzjmes.produce.service;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
/**
 * 机台计划
 * @author cscc tangj
 * @email 
 * @date 2019-09-03 16:16:08
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface MachinePlanRemote {
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/getMachinePlanList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getMachinePlanList(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/del", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R del(@RequestBody Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/checkExsist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkExsist(@RequestBody Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/getList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String,Object>> getList(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/expUploadData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R expUploadData(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/getOutputRecords", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getOutputRecords(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/getMachineInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getMachineInfo(@RequestParam Map<String, Object> params);
	
	@RequestMapping(value = "/zzjmes-service/produce/machinePlan/getTemplateData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getTemplateData(@RequestParam Map<String, Object> paramMap);
	
}
