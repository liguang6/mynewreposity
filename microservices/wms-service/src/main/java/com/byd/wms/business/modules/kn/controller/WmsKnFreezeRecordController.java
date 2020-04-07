package com.byd.wms.business.modules.kn.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.kn.service.WmsKnFreezeRecordService;

/**
 * 库存冻结记录
 *
 * @author cscc tangj
 * @email 
 * @date 2018-10-11 10:12:08
 */
@RestController
@RequestMapping("kn/freezerecord")
public class WmsKnFreezeRecordController {
    @Autowired
    private WmsKnFreezeRecordService wmsKnFreezeRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestBody Map<String, Object> params){
        PageUtils page = wmsKnFreezeRecordService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
    	boolean isOk=wmsKnFreezeRecordService.freeze(params);
    	if(!isOk) {
    		return R.error("操作失败");
    	}
        return R.ok();
    }
    /**
     * 查询冻结记录
     */
    @RequestMapping("/query")
    public R query(@RequestBody Map<String, Object> params){
        PageUtils page = wmsKnFreezeRecordService.queryFreezeRecordPage(params);
        return R.ok().put("page", page);
    }
    /**
     * app扫描标签号获取数据
     */
    @CrossOrigin
    @RequestMapping("/getDataByLabelNo")
    public R getDataByLabelNo(@RequestBody Map<String, Object> params){
    	List<Map<String,Object>> list = wmsKnFreezeRecordService.getDataByLabelNo(params.get("LABEL_NO").toString());
    	Map<String,Object> result=null;
    	if(list.size()>0) {
    		result=list.get(0);
        }
        return R.ok().put("labelData", result);
    }
    /**
     * app冻结、解冻保存
     */
    @CrossOrigin
    @RequestMapping("/appFreeze")
    public R appFreeze(@RequestBody Map<String, Object> params){
    	boolean isOk=wmsKnFreezeRecordService.appFreeze(params);
    	if(!isOk) {
    		return R.error("操作失败");
    	}
        return R.ok("保存成功");
    }
}
