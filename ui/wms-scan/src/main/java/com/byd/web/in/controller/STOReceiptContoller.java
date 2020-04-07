package com.byd.web.in.controller;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.in.service.WmsStoReceiptPadRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("inPda/sto")
public class STOReceiptContoller {

	@Autowired
	protected HttpSession session;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsStoReceiptPadRemote stoReceiptRemote;


	@RequestMapping("/defaultSTOCache")
	public R defaultSTOCache(){

		Map<String, Object> params = new HashMap<>();
        params = initParams(params);
		return stoReceiptRemote.defaultSTOCache(params);
	}

	@RequestMapping("/defaultLabelCache")
	public R defaultLabelCache(@RequestParam Map<String, Object> params){
        params = initParams(params);

		return stoReceiptRemote.defaultLabelCache(params);
	}

	@RequestMapping("/validateSapOutNo")
	public R validateSapOutNo(@RequestParam Map<String, Object> params){

        params = initParams(params);
		return stoReceiptRemote.validateSapOutNo(params);
	}

	/**
	 * 需要校验储位是否在收料房存放区配置表【WMS_C_GR_AREA】里面
	 * 如果不在则报错，
	 * 如果当前仓库只设置一个收料房储位则可以不输入
	 */
	@RequestMapping("/validateStorage")
	public R validateStorage(@RequestParam Map<String, Object> params){

	    params = initParams(params);
		return stoReceiptRemote.validateStorage(params);
	}

	@RequestMapping("/scan")
	public R scan(@RequestParam Map<String, Object> params){

        params = initParams(params);
        String createDate = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
        params.put("CREATE_DATE",createDate);
        params.put("LABEL_NO", params.get("BARCODE_NO"));

        return stoReceiptRemote.scan(params);
	}

	@RequestMapping("/deleteLabelCacheInfo")
	public R deleteLabelCacheInfo(@RequestParam Map<String, Object> params){

		String deleteListStr=params.get("deleteList").toString();
		List<String> list = JSONObject.parseArray(deleteListStr,String.class);

		return stoReceiptRemote.deleteLabelCacheInfo(list);
	}
	/**
	 * STO交货单必须全部收完才能过账
	 *
	 */
	@RequestMapping("/boundIn")
	public R inBound(@RequestParam Map<String, Object> params){

        params = initParams(params);
      /*  params.put("ASNNO",params.get("SAP_OUT_NO"));
        params.put("LABEL_NO", params.get("BARCODE_NO"));*/

        return stoReceiptRemote.boundIn(params);

	}

	private Map<String, Object> initParams(Map<String, Object> params){

        params.put("WH_NUMBER", session.getAttribute("warehouse"));
        params.put("WERKS", session.getAttribute("werks"));
        params.put("FACT_NO", session.getAttribute("werks"));

        params.put("CREATOR", userUtils.getUser().get("USERNAME"));
        params.put("USER_NAME", userUtils.getUser().get("USERNAME"));

        params.put("MENU_KEY", "PDA_GR_STO");
        params.put("BUSINESS_NAME", "03");
        params.put("BUSINESS_CODE", "06");

        return params;
    }
}
