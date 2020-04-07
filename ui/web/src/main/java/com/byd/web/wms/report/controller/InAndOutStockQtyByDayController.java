package com.byd.web.wms.report.controller;

import com.byd.utils.R;
import com.byd.web.wms.report.service.QueryComponentRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date 2019年6月27日
 * @Description //TODO $end$
 **/

@RestController
@RequestMapping("report/InAndOutStockQtyByDay")
public class InAndOutStockQtyByDayController {

	@Autowired private QueryComponentRemote queryComponentRemote;
	
	/**
	 * 出入库库存日报表
	 * @param params
	 * @return
	 */
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return queryComponentRemote.inAndOutStockQtyByDayListStock(params);
	}
}
