package com.byd.web.zzjmes.produce.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;

/**
 * 下料明细导入
 * @author thw
 * @date 2019-09-05
 */
@FeignClient(name = "ZZJMES-SERVICE")
public interface PmdImportRemote {
	@RequestMapping(value = "/zzjmes-service/pmdImport/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody Map<String, Object> params);
}
