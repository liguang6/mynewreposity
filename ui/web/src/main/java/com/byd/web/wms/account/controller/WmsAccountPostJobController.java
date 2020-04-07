package com.byd.web.wms.account.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.account.service.WmsAccountPostJobRemote;

/**
 * 过账失败任务处理，包含失败任务再次过账，失败任务删除等功能
 * @author (changsha) thw
 * @date 2019-5-16
 */
@RestController
@RequestMapping("account/wmsAccountPostJob")
public class WmsAccountPostJobController {
	@Autowired
	WmsAccountPostJobRemote wmsAccountPostJobRemote;

	@RequestMapping("/listPostJob")
    public R listPostJob(@RequestParam Map<String, Object> params) {
		return wmsAccountPostJobRemote.listPostJob(params);
	}
	
	
    /**
     * 失败任务过账
     * @param params
     * @return
     */
    @RequestMapping("/post")
    public R post(@RequestParam Map<String, Object> params) {
    	try {
    		return wmsAccountPostJobRemote.post(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
    
    /**
     * 失败任务关闭（删除过账任务）
     * @param params
     * @return
     */
    @RequestMapping("/closePostJob")
    public R closePostJob(@RequestParam Map<String, Object> params) {
    	try {
    		return wmsAccountPostJobRemote.closePostJob(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
}
