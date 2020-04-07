package com.byd.web.in.service;

import com.byd.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * PDA一步联动收货
 * @author ren.wei3
 *
 */

@FeignClient(name = "WMS-SERVICE")
public interface WmsStoReceiptPadRemote {

	@RequestMapping(value = "/wms-service/in/stoReceiptPda/scan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R scan(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/wms-service/in/stoReceiptPda/validateSapOutNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R validateSapOutNo(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/wms-service/in/stoReceiptPda/validateStorage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R validateStorage(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/wms-service/in/stoReceiptPda/deleteLabelCacheInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R deleteLabelCacheInfo(@RequestBody List<String> list);

	@RequestMapping(value = "/wms-service/in/stoReceiptPda/docItem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R docItem(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/wms-service/in/stoReceiptPda/defaultLabelCache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R defaultLabelCache(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/wms-service/in/stoReceiptPda/defaultSTOCache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R defaultSTOCache(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/wms-service/in/stoReceiptPda/boundIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    R boundIn(@RequestBody Map<String, Object> params);

}
