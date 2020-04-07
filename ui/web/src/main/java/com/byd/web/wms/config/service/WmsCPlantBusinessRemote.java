package com.byd.web.wms.config.service;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.byd.utils.R;
import com.byd.web.wms.config.entity.WmsCPlantBusinessEntity;


@FeignClient(name = "WMS-SERVICE")
public interface WmsCPlantBusinessRemote {
	
	/**
     * 分页查询
     */
	@RequestMapping(value = "/wms-service/config/plantbusiness/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);
    /**
     * 单条记录查询By ID
     */
	@RequestMapping(value = "/wms-service/config/plantbusiness/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value="id") Long id);
    
	@RequestMapping(value = "/wms-service/config/plantbusiness/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody WmsCPlantBusinessEntity wmsCMatUsing);

	@RequestMapping(value = "/wms-service/config/plantbusiness/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody WmsCPlantBusinessEntity wmsCMatUsing);

	@RequestMapping(value = "/wms-service/config/plantbusiness/delById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delById(@RequestBody WmsCPlantBusinessEntity wmsCMatUsing);
	
	@RequestMapping(value = "/wms-service/config/plantbusiness/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping(value = "/wms-service/config/plantbusiness/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R upload(@RequestBody List<WmsCPlantBusinessEntity> entityList) ;
	
	@RequestMapping(value = "/wms-service/config/plantbusiness/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody List<Map<String, Object>> params);
	
	@RequestMapping(value = "/wms-service/config/plantbusiness/getWmsBusinessCodeList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getWmsBusinessCodeList(@RequestBody Map<String, Object> params);
	/**
     * 复制保存
     * @param entitys
     * @return
     */
	@RequestMapping(value = "/wms-service/config/plantbusiness/saveCopyData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveCopyData(@RequestBody List<Map<String, Object>> list);
}
