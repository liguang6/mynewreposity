package com.byd.wms.business.modules.report.controller;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.report.service.InAndOutStockQtyByDayService;
import com.byd.wms.business.modules.report.service.QueryComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName 出入库库存日报表
 * @Author qiu.jiaming1
 * @Date 2019年6月27日
 * @Description //TODO $end$
 **/

@RestController
@RequestMapping("report/InAndOutStockQtyByDay")
public class InAndOutStockQtyByDayController {

	@Autowired
    private InAndOutStockQtyByDayService inAndOutStockQtyByDayService;
    
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        System.err.println(params.toString());
    	PageUtils page = inAndOutStockQtyByDayService.queryStockPage(params);
        return R.ok().put("page", page);
    }
}
