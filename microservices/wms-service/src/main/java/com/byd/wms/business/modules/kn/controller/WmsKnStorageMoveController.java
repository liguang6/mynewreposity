package com.byd.wms.business.modules.kn.controller;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.kn.service.WmsKnStorageMoveService;

/**
 * 移储
 *
 * @author cscc tangj
 * @email 
 * @date 2018-11-07 10:12:08
 */
@RestController
@RequestMapping("kn/storageMove")
public class WmsKnStorageMoveController {
    @Autowired
    private WmsKnStorageMoveService wmsKnStorageMoveService;
    @Autowired
    private WmsCoreWhBinService wmsCoreWhBinService;
    /**
     * 查询移储记录
     */
    @RequestMapping("/list")
    public R list(@RequestBody Map<String, Object> params){
    	PageUtils page = wmsKnStorageMoveService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 查询库存信息（用于移储操作）
     */
    @RequestMapping("/getStockList")
    public R getStockList(@RequestBody Map<String, Object> params){
    	String matnrStr=params.get("MATNR").toString();
    	List<String> matnr=Arrays.asList(matnrStr.split(","));
    	params.put("MATNRS", matnr);
    	List<Map<String,Object>> list = wmsKnStorageMoveService.queryStock(params);
        return R.ok().put("list", list);
    }
    /**
     * 保存移储记录
     */
    @RequestMapping("/save")
    public R save(@RequestBody List<Map<String, Object>> list){
    	try {
			wmsKnStorageMoveService.save(list);
	    	
	        return R.ok();
    	}catch(Exception e) {
    		// 捕获service抛出的异常信息
    		return R.error(e.getMessage());
    	}
    }
    /**
     * 目标储位校验
     */
    @RequestMapping("/checkBin")
    public R checkBin(@RequestBody Map<String, Object> params){
    	List<WmsCoreWhBinEntity> list=
    	wmsCoreWhBinService.selectList(new EntityWrapper<WmsCoreWhBinEntity>()
    			.eq("WH_NUMBER", params.get("WH_NUMBER"))
                .eq("BIN_CODE", params.get("BIN_CODE")).eq("DEL","0"));
    	return R.ok().put("list", list);
    }
    
}
