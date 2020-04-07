package com.byd.web.qms.processQuality.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;

@FeignClient(name = "QMS-SERVICE")
public interface QmsProcessTestRemote {

	@RequestMapping(value = "/qms-service/processQuality/test/getTestList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getProcessTestList(@RequestBody Map<String,Object> params);
	
	@RequestMapping(value = "/qms-service/processQuality/test/getFaultList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getFaultList(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/qms-service/processQuality/test/saveTestRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveTestRecord(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/qms-service/processQuality/test/saveAbnormalInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveAbnormalInfo(@RequestBody Map<String, Object> params);
	
	@RequestMapping(value = "/qms-service/processQuality/test/delAbnormalInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delAbnormalInfo(@RequestParam(value="ABNORMAL_ID") String ABNORMAL_ID,@RequestParam(value="RECORD_ID") String RECORD_ID);

	@RequestMapping(value = "/qms-service/processQuality/test/getProcessTestRecordList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getProcessTestRecordList(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/qms-service/processQuality/test/getProcessTestDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getProcessTestDetail(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/qms-service/processQuality/test/getPartsTestRecordList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getPartsTestRecordList(@RequestBody Map<String, Object> params);

	@RequestMapping(value = "/qms-service/processQuality/test/getProcessDpuData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getProcessDpuData(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/processQuality/test/confirmProcessTest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R confirmProcessTest(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/processQuality/test/getProcessFTYData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getProcessFTYData(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/processQuality/test/getUnProcessTestList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getUnProcessTestList(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/processQuality/test/saveUnTestRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R saveUnTestRecord(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/processQuality/test/getUnProcessReportData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getUnProcessReportData(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/processQuality/test/getBJFTYData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getBJFTYData(@RequestBody Map<String, Object> condMap);

	@RequestMapping(value = "/qms-service/processQuality/test/getFaultScatterData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getFaultScatterData(@RequestBody Map<String, Object> condMap);

	
	
}
