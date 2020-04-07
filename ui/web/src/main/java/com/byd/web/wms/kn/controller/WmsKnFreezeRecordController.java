package com.byd.web.wms.kn.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.kn.service.WmsKnFreezeRecordRemote;
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
    private WmsKnFreezeRecordRemote wmsKnFreezeRecordRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsKnFreezeRecordRemote.list(params);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return wmsKnFreezeRecordRemote.save(params);
    }
    /**
     * 查询冻结记录
     */
    @RequestMapping("/query")
    public R query(@RequestParam Map<String, Object> params){
    	return wmsKnFreezeRecordRemote.query(params);
    }
    
    /**
     * app扫描标签号获取数据
     * @param  
     */
    @RequestMapping("/getDataByLabelNo")
    public R getDataByLabelNo(@RequestParam Map<String, Object> params){
    	return wmsKnFreezeRecordRemote.getDataByLabelNo(params);
    }
}
