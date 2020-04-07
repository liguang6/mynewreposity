package com.byd.web.qms.config.service;

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
public interface QmsFaultLibraryRemote {
	

	@RequestMapping(value = "/qms-service/config/qmsFaultLibrary/queryPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryPage(@RequestBody Map<String,Object> params);

	@RequestMapping(value = "/qms-service/config/qmsFaultLibrary/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);
    /**
     * 保存
     */
	@RequestMapping(value = "/qms-service/config/qmsFaultLibrary/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	
	/**
     * 更新
     */
	@RequestMapping(value = "/qms-service/config/qmsFaultLibrary/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody Map<String, Object> params);
	
	
	@RequestMapping(value = "/qms-service/config/qmsFaultLibrary/delById/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@PathVariable("id") Long id);
	
}
