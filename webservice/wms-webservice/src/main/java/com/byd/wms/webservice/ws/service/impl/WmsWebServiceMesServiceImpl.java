package com.byd.wms.webservice.ws.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.datasources.DataSourceNames;
import com.byd.utils.datasources.annotation.DataSource;
import com.byd.wms.webservice.common.util.WebServiceAccessLock;
import com.byd.wms.webservice.ws.dao.MesServiceDao;
import com.byd.wms.webservice.ws.service.WmsWebServiceMesService;

@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.ws.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.ws.service.WmsWebServiceMesService" // 接口地址
)
public class WmsWebServiceMesServiceImpl implements WmsWebServiceMesService {
	@Autowired
	MesServiceDao mesServiceDao;

	@DataSource(name = DataSourceNames.THIRD)
	@Override
	public String transferMapInfo(String map_info) {
		JSONObject res = new JSONObject();
		WebServiceAccessLock.operatelock("PDMMapService", WebServiceAccessLock.COUNT_TYPE_ADD);
		int ac_count=WebServiceAccessLock.currentCount;
		System.out.println("PDMMapService当前访问数:" + ac_count);
		if(ac_count >=WebServiceAccessLock.MAX_COUNT) {
			res.put("success", false);
			res.put("msg", "接口访问次数过多，请稍后重试！");	
			return res.toString();
		}
		JSONArray jsa=null;
		try {
			System.out.println("PDMMAP:"+map_info);
			jsa=JSONArray.parseArray(map_info);
			
		}catch(Exception e) {
			res.put("success", false);
			res.put("msg", "参数不是有效JSON数据！");
			return res.toString();
		}	
		List<Map<String,Object>> map_list=new ArrayList<Map<String,Object>>();
		for (int i = 0; i < jsa.size(); i++) {
			JSONObject sa = jsa.getJSONObject(i);
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("edit_date", sa.get("edit_date"));
			m.put("editor", sa.get("editor"));
			m.put("map_name", sa.get("map_name"));
			m.put("map_no", sa.get("map_no"));
			m.put("map_sheet", sa.get("map_sheet"));
			m.put("map_url", sa.get("map_url"));
			m.put("publish_date", sa.get("publish_date"));
			m.put("publisher", sa.get("publisher"));
			m.put("status", sa.get("status"));
			m.put("version", sa.get("version"));
			map_list.add(m);
		}
		
		Map<String,Object> condMap= new HashMap<String,Object>();
		condMap.put("map_list", map_list);
		try {
			//查询列表图纸是否存在
			List<Map<String,Object>> update_list = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> add_list = new ArrayList<Map<String,Object>>();
			List<String> exist_list = mesServiceDao.getExistPDMMap(condMap);
			if(exist_list!=null && exist_list.size()>0) {
				String key_compare = null;
				for(Map<String,Object> m : map_list) {
					key_compare=m.get("map_no")+"##"+m.get("version");
					if(exist_list.contains(key_compare)) {
						update_list.add(m);
					}else {
						add_list.add(m);
					}
				}
			}else {
				add_list=map_list;
			}
			if(update_list.size()>0) {
				condMap.put("update_list", update_list);
				mesServiceDao.updatePDMMap(condMap);
			}
			if(add_list.size()>0) {
				condMap.put("add_list", add_list);
				mesServiceDao.savePDMMap(condMap);	
			}

			res.put("success", true);
			res.put("msg", "传输成功！");
			System.out.println("PDMMAP 更新数据:"+update_list.size());		
			System.out.println("PDMMAP 插入数据:"+add_list.size());	
			WebServiceAccessLock.operatelock("PDMMapService", WebServiceAccessLock.COUNT_TYPE_SUB);
			return res.toString();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("transferMapInfo: "+e.getMessage());
			res.put("success", false);
			res.put("msg", "接口异常，传输失败！");
			if(e.getMessage().contains("Duplicate entry")) {
				res.put("msg", e.getMessage());
			}
			WebServiceAccessLock.operatelock("PDMMapService", WebServiceAccessLock.COUNT_TYPE_SUB);
			return res.toString();
		}
	}

}
