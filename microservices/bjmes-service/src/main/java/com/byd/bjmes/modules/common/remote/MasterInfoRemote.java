package com.byd.bjmes.modules.common.remote;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ADMIN-SERVICE")
public interface MasterInfoRemote {
  @RequestMapping(value = "/admin-service/masterdata/dict/queryMasterDictWerksOrderNum", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public int queryMasterDictWerksOrderNum(@RequestParam(value="params") Map<String, Object> params);
}