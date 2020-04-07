package com.byd.web.wms.report.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.R;
import java.util.Map;

/**
 * @author 刘宇健
 * @version 创建时间：2019年12月27日11:50:49
 * @description 条码出入库记录报表，条码明细查询
 */
@FeignClient(name = "WMS-SERVICE")
public interface BarcodeInOutRecordQueryRemote {
    @RequestMapping(value = "/wms-service/report/wmsReportBarcodeLog/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R list(@RequestParam(value = "params") Map<String, Object> params);
}
