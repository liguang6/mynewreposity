package com.byd.web.qms.config.service;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.byd.utils.R;

@FeignClient(name = "QMS-SERVICE")
public interface QmsDictRemote {
	

	@RequestMapping(value = "/qms-service/config/qmsDict/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestBody Map<String,Object> params);

	@RequestMapping(value = "/qms-service/config/qmsDict/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    /**
     * 保存
     */
	@RequestMapping(value = "/qms-service/config/qmsDict/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	
	/**
     * 更新
     */
	@RequestMapping(value = "/qms-service/config/qmsDict/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody Map<String, Object> params);
	
	
	@RequestMapping(value = "/qms-service/config/qmsDict/delById/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/qms-service/config/qmsDict/getList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getList(@RequestBody Map<String,Object> params);
	
	@RequestMapping(value = "/qms-service/config/qmsDict/getDictList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String,Object>> getDictList(@RequestBody Map<String,Object> params);
	
}
