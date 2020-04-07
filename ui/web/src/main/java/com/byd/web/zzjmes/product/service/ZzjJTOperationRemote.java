package com.byd.web.zzjmes.product.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.R;

@FeignClient(name = "ZZJMES-SERVICE")
public interface ZzjJTOperationRemote {

	@RequestMapping(value = "/zzjmes-service/jtOperation/getJTPlan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getJTPlan(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/getPmdItems", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPmdItems(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/getMachineAchieve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getMachineAchieve(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/saveJTBindData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveJTBindData(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/getPmdOutputItems", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPmdOutputItems(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/saveJTOutputData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R saveJTOutputData(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/startOpera", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R startOpera(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/queryOutputRecords", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryOutputRecords(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/getPmdAbleQty", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPmdAbleQty(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/savePmdOutQty", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R savePmdOutQty(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/deletePmdOutInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R deletePmdOutInfo(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/scrapePmdOutInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R scrapePmdOutInfo(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/queryCombRecords", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R queryCombRecords(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/zzjmes-service/jtOperation/getPmdBaseInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R getPmdBaseInfo(@RequestBody Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/jtOperation/checkBindPlan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	R checkBindPlan(@RequestBody Map<String, Object> params);

}
