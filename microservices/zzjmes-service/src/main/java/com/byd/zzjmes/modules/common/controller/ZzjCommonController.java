package com.byd.zzjmes.modules.common.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.common.service.ZzjCommonService;
import com.byd.zzjmes.modules.produce.service.MasterDataRemote;

@RestController
@RequestMapping("common")
public class ZzjCommonController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ZzjCommonService zzjCommonService;
	@Autowired
    private UserUtils userUtils;
    @Autowired
    private MasterDataRemote masterDataRemote; 
	
	@RequestMapping("/getPlanBatchList")
    public R getPlanBatchList(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> list = zzjCommonService.getPlanBatchList(params);
		return R.ok().put("data", list);
	}
	/**
	 * 获取用户权限机台列表
	 */
	@RequestMapping("/getJTProcess")
	public R getJTProcess(@RequestParam Map<String, Object> params) {
		Map<String,Object> _user = userUtils.getUser();
		params.put("username", _user.get("STAFF_NUMBER"));
		List<Map<String, Object>> list = zzjCommonService.getJTProcess(params);	
		return R.ok().put("process",list);
	}
	/**
	 * 模糊查找机台列表
	 */
	@RequestMapping("/getMachineList")
	public R getMachineList(@RequestParam Map<String, Object> params) {
		List<Map<String, Object>> list = zzjCommonService.getMachineList(params);	
		return R.ok().put("data",list);
	}
	/**
	 * 模糊查找装配位置
	 */
	@RequestMapping("/getAssemblyPositionList")
	public R getAssemblyPositionList(@RequestParam Map<String, Object> params) {
		List<Map<String, Object>> list = zzjCommonService.getAssemblyPositionList(params);	
		return R.ok().put("data",list);
	}
	
	@RequestMapping("/productionExceptionManage")
	public R productionExceptionManage(@RequestParam Map<String, Object> params) {
		int result = zzjCommonService.productionExceptionManage(params);
		return R.ok().put("result",result);
	}
	@RequestMapping("/getProductionExceptionList")
	public R getProductionExceptionList(@RequestParam Map<String, Object> params) {
		List<Map<String, Object>> list = zzjCommonService.getProductionExceptionList(params);	
		return R.ok().put("data",list);
	}
}
