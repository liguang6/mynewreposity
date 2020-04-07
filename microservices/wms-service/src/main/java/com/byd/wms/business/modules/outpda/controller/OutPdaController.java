package com.byd.wms.business.modules.outpda.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.out.service.WmsOutPickingService;
import com.byd.wms.business.modules.outpda.service.OutPdaService;
import com.byd.wms.business.modules.query.service.WmsWhTaskQueryService;

@RestController
@RequestMapping("outPda")
public class OutPdaController {
	@Autowired
	private WmsWhTaskQueryService wmsWhTaskQuery;
	@Autowired
	private WmsOutPickingService wmsOutPickingService;
	@Autowired
	private OutPdaService outPdaService;
	
	/**
	 * 
	 * @param params
	 * @return
	 */
    @RequestMapping("/xiaJiaJianPei")
    public R list(@RequestParam Map<String, Object> params){
    	wmsOutPickingService.recommend(params);
    	PageUtils page = outPdaService.queryTaskPage(params);
    	String strXJJP=outPdaService.getChaFaBiaoShi(params);
        return R.ok().put("page", page).put("OVERSTEP_REQ_FLAG", strXJJP);
    }
    
    /**
	 * 根据页面扫描的条码进行校验，校验通过返回条码和数量，否则返回count=0
	 */
    @RequestMapping("/scanLabel")
    public R getLabel(@RequestParam Map<String, Object> params){
    	Map<String, Object> rsMap = outPdaService.getLabelInfo(params);
        return R.ok().put("page", rsMap);
    }
    
    /**
	 * 保存下架信息
	 * 
	 * @param params
	 * @return
	 */
    @RequestMapping("/saveXiaJiaXinXi")
    public R saveXiaJiaXinXi(@RequestBody Map<String, Object> params){
    	try {
    		outPdaService.saveXiaJiaXinXi(params);
            return R.ok("保存成功");
    	}catch(Exception e) {
    		return R.error();
    	}    	
    }
}
