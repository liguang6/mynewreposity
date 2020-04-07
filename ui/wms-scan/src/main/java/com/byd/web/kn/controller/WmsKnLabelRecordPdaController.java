package com.byd.web.kn.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;

/*PDA 条码拆分
 * @author chen.yafei
 * @email chen.yafei1@byd.com
 * @date 2019-12-11
 */
@RestController
@RequestMapping("kn/labelRecordCf")
public class WmsKnLabelRecordPdaController {
	
	//获取标签信息
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		
		return null;
	} 
	//条码缓存
	@RequestMapping("/saveLabelCache")
	public R saveLabelCache(@RequestParam Map<String, Object> params) {
		
		return null;
	}

}
