package com.byd.wms.business.modules.out.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.out.service.WmsOutHandoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 需求交接控制器
 * @author ren.wei3
 *
 */
@RestController
@RequestMapping("/webservices/handover")
public class WmsWebServiceOutHandoverController {

	@Autowired
	private WmsOutHandoverService wmsOutHandoverService;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsOutHandoverService.list(params);
        return R.ok().put("page", page);
    }
    
    /**
	 * 需求交接
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/save")
	public R handover(@RequestParam Map<String, Object> params)  {
		//System.err.println("savesavesavesavesavesave");
		Map<String, Object> retMap=new HashMap<String, Object>();
		//return R.ok().put("msg", retMap.get("msg"));
		try{
			retMap = wmsOutHandoverService.handover(params);
			return R.ok().put("msg", retMap.get("msg"));
		} catch (Exception e) {
			return R.error("系统异常："+e.getMessage());	
		}
	}
}
