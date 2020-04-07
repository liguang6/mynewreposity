package com.byd.wms.business.modules.knpda.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.wms.business.modules.knpda.service.WmsPdaKnLableRecordService;

/*PDA 条码拆分
 * @author chen.yafei
 * @email chen.yafei1@byd.com
 * @date 2019-12-11
 */

@RestController
@RequestMapping("knpda/labelRecord")
public class WmsPdaKnLableRecordController {
	
	@Autowired
	private WmsPdaKnLableRecordService wmsPdaKnLableRecordService;
	//根据条码查询 标签信息
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> result =new ArrayList<Map<String, Object>>();
	 	try{
	 		result = wmsPdaKnLableRecordService.queryLableList(params);
		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
        return  R.ok().put("result", result);
    }

}
