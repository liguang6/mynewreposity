package com.byd.web.wms.kn.service;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.byd.utils.R;

@FeignClient(name = "WMS-SERVICE")
public interface WmsKnStockModifyRemote {
	

	@RequestMapping(value = "/wms-service/kn/stockModify/previewExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewExcel(@RequestBody Map<String,Object> params);
	
	@RequestMapping(value = "/wms-service/kn/stockModify/previewModifyExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewModifyExcel(@RequestBody Map<String,Object> params);

	@RequestMapping(value = "/wms-service/kn/stockModify/previewLabelExcel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R previewLabelExcel(@RequestBody Map<String,Object> params);
    /**
     * 保存
     */
	@RequestMapping(value = "/wms-service/kn/stockModify/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody Map<String, Object> params);
	
	/**
     * 查询标签数据
     */
	@RequestMapping(value = "/wms-service/kn/stockModify/getLabelList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getLabelList(@RequestBody Map<String, Object> params);
}
