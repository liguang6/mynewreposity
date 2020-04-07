package com.byd.web.wms.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsSapCustomerEntity;


@FeignClient(name = "WMS-SERVICE")
public interface WmsSapCustomerRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/sapCustomer/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    
	@RequestMapping(value = "/wms-service/config/sapCustomer/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@RequestParam(value="id")Long id);
	
	@RequestMapping(value = "/wms-service/config/sapCustomer/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R addOrUpdate(@RequestBody WmsSapCustomerEntity entity);
	
	//删除
	@RequestMapping(value = "/wms-service/config/sapCustomer/delLogic", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delLogic(@RequestParam(value="id") Long id);
	
	//查询预览
	@RequestMapping(value = "/wms-service/config/sapCustomer/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R preview(@RequestBody List<WmsSapCustomerEntity> entitys);
	
	//批量导入
	@RequestMapping(value = "/wms-service/config/sapCustomer/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R importBatch(@RequestBody List<WmsSapCustomerEntity> entitys);
	//导出excel文件
	@RequestMapping(value = "/wms-service/config/sapCustomer/export2Excel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> export2Excel(@RequestParam(value="params") Map<String,Object> params);
}
