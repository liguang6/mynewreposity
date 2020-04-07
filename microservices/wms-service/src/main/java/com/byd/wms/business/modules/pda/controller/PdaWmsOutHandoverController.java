package com.byd.wms.business.modules.pda.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.out.service.WmsOutHandoverService;
import com.byd.wms.business.modules.pda.service.PdaWmsOutHandoverService;

@RestController
@RequestMapping("/out/pdahandover")
public class PdaWmsOutHandoverController {
	@Autowired
	private PdaWmsOutHandoverService pdaWmsOutHandoverService;
	@Autowired
	private WmsOutHandoverService wmsOutHandoverService;
	/**
     * 列表
     */
	@CrossOrigin
    @RequestMapping("/list")
    public R list(@RequestBody Map<String, Object> params){
        PageUtils page = pdaWmsOutHandoverService.list(params);
        return R.ok().put("page", page);
    }
    
    /**
	 * 需求交接
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@CrossOrigin
	@RequestMapping("/save")
	public R handover(@RequestBody Map<String, Object> params)  {
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
//			retMap = pdaWmsOutHandoverService.handover(params);
			retMap = wmsOutHandoverService.handover(params);
			//return R.ok().put("msg", retMap.get("msg"));
			return (R) retMap;
		} catch (Exception e) {
			//return R.error("系统异常："+e.getMessage());	
			e.printStackTrace();
			return R.error();
		}	
	}

}
