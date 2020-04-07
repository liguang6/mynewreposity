package com.byd.web.wms.config.service;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.web.wms.config.entity.WmsCoreStorageSearchEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "WMS-SERVICE")
public interface WmsCoreStorageSearchRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/list",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value = "params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/info", method = RequestMethod.POST, produces = MediaType.ALL_VALUE)
    public R info(@RequestParam(value = "id") Long id);
    
	@RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCoreStorageSearchEntity entity);

	@RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCoreStorageSearchEntity entity);

    /**
     * 软删BY ID
     */
	@RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCoreStorageSearchEntity entity);
    
	@RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/queryAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryAll(@RequestParam Map<String, Object> params);

    /**
     * 查询名称
     * @param arg1
     * @param arg2
     * @return
     */
    @RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/infoName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R queryAreaName(@RequestParam(value="warehouseCode") String arg1, @RequestParam(value="storageAreaCode") String arg2);

    /**
     * 根据仓库号查询搜索顺序列表
     * @param
     * @return
     */
    @RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/getStorageAreaSearch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getStorageAreaSearch(@RequestParam(value="warehouseCode") String warehouseCode);

    /**
     * 根据仓库号查询存储类型列表
     * @param
     * @return
     */
    @RequestMapping(value = "/wms-service/config/wmsCoreStorageSearch/getStorageAreaCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R getStorageAreaCode(@RequestParam(value="warehouseCode") String warehouseCode);

}
