package com.byd.web.zzjmes.produce.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface PmdManagerRemote {
	@RequestMapping(value = "/zzjmes-service/pmdManager/getPmdList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getPmdList(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/pmdManager/getPmdListPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getPmdListPage(@RequestParam Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/pmdManager/getSubcontractingItemList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSubcontractingItemList(@RequestParam Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/pmdManager/editSubcontractingItem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R editSubcontractingItem(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/pmdManager/getSubcontractingHeadPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSubcontractingHeadPage(@RequestParam Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/pmdManager/getSubcontractingPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSubcontractingPage(@RequestParam Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/pmdManager/getSubcontractingList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getSubcontractingList(@RequestParam Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/pmdManager/editPmdList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R editPmdList(@RequestParam Map<String, Object> paramMap);
	@RequestMapping(value = "/zzjmes-service/pmdManager/deletePmdList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deletePmdList(@RequestParam Map<String, Object> paramMap);

	@RequestMapping(value = "/zzjmes-service/pmdManager/editSubcontractingList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R editSubcontractingList(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/pmdManager/getProductionExceptionPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getProductionExceptionPage(@RequestParam Map<String, Object> paramMap);
	@RequestMapping(value = "/zzjmes-service/pmdManager/getProductionExceptionList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getProductionExceptionList(@RequestParam Map<String, Object> paramMap);
	@RequestMapping(value = "/zzjmes-service/pmdManager/exceptionConfirm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R exceptionConfirm(@RequestParam Map<String, Object> paramMap);
	
	@RequestMapping(value = "/zzjmes-service/pmdManager/getPmdProcessPlanCount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getPmdProcessPlanCount(@RequestParam Map<String, Object> paramMap);
}
