package com.byd.web.wms.account.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.web.wms.account.service.WmsAccountOutVRemote;

/**
 * 账务处理-发货V（核销业务虚发）
 * 包含：SAP采购订单收料(V)、工厂间调拨收料（V）、SAP交货单收料（V)、成品入库（V）
 * @author (changsha) thw
 * @date 2018-11-01
 */
@RestController
@RequestMapping("account/wmsAccountOutV")
public class WmsAccountOutVController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	WmsAccountOutVRemote wmsAccountOutVRemote;
    @Autowired
    private RedisUtils redisUtils;

	@RequestMapping("/listMOVMat")
    public R listMOVMat(@RequestParam Map<String, Object> params) {
		Map<String,Object> currentUser = redisUtils.getMap("currentUser");
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
    	List<Map<String,Object>> deptMapList = redisUtils.getList("authWhList");
    	params.put("deptList", deptMapList);
    	return wmsAccountOutVRemote.listMOVMat(params);
	}
	
	
    /**
     * 发货（V）过账-核销业务
     * @param params
     * @return
     */
    @RequestMapping("/postGI")
    public R postGI(@RequestParam Map<String, Object> params) {
    	try {
    		Map<String,Object> currentUser = redisUtils.getMap("currentUser");
    	   	params.put("USERNAME", currentUser.get("USERNAME"));
    	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	   	return wmsAccountOutVRemote.postGI(params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
    
    /**
     * 根据料号、工厂、仓库号获取物料信息、库存信息、行文本、头文本
     * @param params
     * @return
     */
    @RequestMapping("/getMatInfo")
    public R getMatInfo(@RequestParam Map<String, Object> params) {
	   	return wmsAccountOutVRemote.getMatInfo(params);
    }
    
    /**
     * STO虚发物料数据获取
     * @param params
     * @return
     */
    @RequestMapping("/getSTOVMatList")
    public R getSTOVMatList(@RequestParam Map<String, Object> params) {
  		Map<String,Object> currentUser = redisUtils.getMap("currentUser");
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	return wmsAccountOutVRemote.getSTOVMatList(params);
    }
}
