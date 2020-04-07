package com.byd.web.sys.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.common.service.CommonRemote;

@RestController
public class SelectWarehouseController {

	@Autowired 
	protected HttpSession session;
	@Autowired
    private CommonRemote commonRemote;
	
	@RequestMapping("SelectWarehouse")
    public R login(@RequestParam  Map<String,Object> params) {
		if (params.get("warehouse") != null && !params.get("warehouse").equals("")) {
			String warehouse = params.get("warehouse").toString();
			session.setAttribute("warehouse", warehouse);
			List<Map<String,Object>> whlist = commonRemote.wmsCoreWhList(params);
			if(whlist.size() > 0) {
				session.setAttribute("werks", whlist.get(0).get("WERKS"));
			}
		} else {
			return R.error("无仓库号权限");
		}
    	 return R.ok();
	}
}
