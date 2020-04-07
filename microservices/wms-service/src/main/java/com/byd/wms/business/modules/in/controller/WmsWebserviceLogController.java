package com.byd.wms.business.modules.in.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsQmsPlatformRemote;
import com.byd.wms.business.modules.in.entity.WmsWebServiceLogEntity;
import com.byd.wms.business.modules.in.service.WmsWebserviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author rain
 * @date 2019年11月27日14:10:37
 * @description webserviceLog控制类
 */
@RestController
@RequestMapping("in/webservicelog")
public class WmsWebserviceLogController {
	@Autowired
    private WmsWebserviceLogService wmsWebserviceLogService;
	@Autowired
	private WmsQmsPlatformRemote wmsQmsPlatformRemote;

	/**
	 * @description 列表页面查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = wmsWebserviceLogService.queryPage(params);
        return R.ok().put("page", page);
	}

	/**
	 * 接口重新触发
	 * @param pkLog
	 * @return
	 * @throws IllegalAccessException
	 */
	@RequestMapping("/retrigger")
	@ResponseBody
	public R retrigger(Long pkLog) throws IllegalAccessException{
		WmsWebServiceLogEntity webServiceLogEntity = wmsWebserviceLogService.queryByPkLog(pkLog);
		if(webServiceLogEntity == null){
			throw new IllegalAccessException("loginfo not exist!");
		}
		//TODO 接口重新触发，直接调用即可

		return R.ok();
	}

	/**
	 *
	 * @param params
	 * @return
	 */
	@RequestMapping("/retriggerLogs")
	public R retriggerLogs(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> logList=new ArrayList<Map<String, Object>>();
		List<WmsWebServiceLogEntity> logEntityList=new ArrayList<>();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		long pkLog;
		for(int i=0;i<jarr.size();i++){
			Map<String, Object> tempMap=new HashMap<String, Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			tempMap.put("PK_LOG", itemData.getString("PK_LOG"));
			try {
				pkLog = Long.parseLong(itemData.getString("PK_LOG"));
			}catch (Exception e){
				throw new RuntimeException("日志pk丢失，请重试！");
			}
			WmsWebServiceLogEntity webServiceLogEntity = wmsWebserviceLogService.queryByPkLog(pkLog);
			//TODO 接口重新触发，直接调用即可
			JSONObject jsondata = JSONObject.parseObject(webServiceLogEntity.getFromJsonData());
//			JSONArray jsonArr = JSONArray.parseArray(webServiceLogEntity.getFromJsonData());
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("quality_info", jsondata.toString());
			Map<String,Object> res = wmsQmsPlatformRemote.sendQmsData(jsonMap);
//			String msgty = (String) res.get("msgty");
			if("500".equals(res.get("code").toString())){
				return R.error(res.get("msg").toString());
			}else{
				return R.ok(res.get("msg").toString());
			}
//			logEntityList.add(webServiceLogEntity);
//			logList.add(tempMap);
		}



		return R.ok();
	}

}
