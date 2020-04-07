package com.byd.web.wms.out.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.out.service.WmsOutHandoverServiceRemote;

/**
 * 需求交接控制器
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("/out/handover")
public class WmsOutHandoverController {

	@Autowired
	private WmsOutHandoverServiceRemote wmsOutHandoverServiceRemote;
	
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
    	params.put("REQUIREMENT_NO", reqno);
    	return wmsOutHandoverServiceRemote.list(params);
    }
    
    /**
	 * 需求交接
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/save")
	public R handover(@RequestParam Map<String, Object> params)  {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsOutHandoverServiceRemote.handover(params);
	}
}
