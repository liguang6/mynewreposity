package com.byd.wms.business.modules.out.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.out.service.WmsOutPickingService;
import com.byd.wms.business.modules.out.service.WmsOutResersalPickingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 取消需求拣配控制器
 * @author qiu.jiaming1
 *
 */
@RestController
@RequestMapping("/out/resersalPicking")
public class WmsOutReversalPickingController {

	@Autowired
	private WmsOutResersalPickingService wmsOutResersalPickingService;
	
	@Autowired
	private CommonService commonService;

    @Autowired
    private UserUtils userUtils;

	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsOutResersalPickingService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){

        Map<String,Object> currentUser = userUtils.getUser();
        params.put("USERNAME", currentUser.get("USERNAME"));
        Map<String, Object> retMap=new HashMap<String, Object>();
        try{
            retMap = wmsOutResersalPickingService.update(params);
            String message = retMap.get("MESSAGE") == null ? "":retMap.get("MESSAGE").toString();
            //String timestampstr = wmsInAutoPutawayService.saveStepLog(params);

            if (!message.equals("")) {
                return R.error(message);
            }
            return R.ok();
        } catch (Exception e) {
            return R.error("系统异常："+e.getMessage());
        }
    }

}
