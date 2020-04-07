package com.byd.wms.business.modules.kn.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.config.entity.WmsCoreStorageSearchEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.kn.entity.WmsKnLabelRecordEntity;
import com.byd.wms.business.modules.kn.service.WmsKnLabelRecordService;
import com.byd.wms.business.modules.kn.service.WmsKnStorageMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 条码管理
 *
 * @author qjm
 * @email
 * @date 2019-04-01
 */
@RestController
@RequestMapping("kn/labelRecord")
public class WmsKnLabelRecordController {
    @Autowired
    private WmsKnLabelRecordService wmsKnLabelRecordService;

    @Autowired
    private UserUtils userUtils;

    /**
     * 查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	PageUtils page = wmsKnLabelRecordService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        Map map  = wmsKnLabelRecordService.queryById(id);
        return R.ok().put("storageType", map);
    }

    /**
     * 新增标签页面查询
     */
    @RequestMapping("/poList")
    public R poList(@RequestParam Map<String, Object> params){
        PageUtils page = wmsKnLabelRecordService.poQueryPage(params);
        return R.ok().put("page", page);
    }

    @RequestMapping("/queryByCf")
    public R queryByCf(@RequestBody Map map){
        System.err.println(map.toString());
        PageUtils page = wmsKnLabelRecordService.queryByCf(map);
        return R.ok().put("page", page);
    }

    @RequestMapping("/saveByCf")
    public R saveByCf(@RequestBody  Map map){
        System.err.println(map.toString());
        List<Map> listMap = wmsKnLabelRecordService.saveByCf(map);
        return R.ok().put("page",listMap);
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody Map map){
        wmsKnLabelRecordService.deleteLabel(map);
        return R.ok();
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map map){
        wmsKnLabelRecordService.updateLabel(map);
        return R.ok();
    }

    /**
     * 保存生成标签
     */
    @RequestMapping("/saveLabel")
    public R saveLabel(@RequestParam Map<String, Object> params){
        Map<String,Object> currentUser = userUtils.getUser();
        params.put("USERNAME", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
        params.put("FULL_NAME", currentUser.get("FULL_NAME"));

        return wmsKnLabelRecordService.saveCoreLabel(params);
    }

    /**
     * 根据标签号查询标签信息
     */
    @RequestMapping("/getLabelList")
    public List<Map<String, Object>> getLabelList(@RequestBody Map<String, Object> params){

        return wmsKnLabelRecordService.getLabelList(params);
    }
    
    /*
	 * 条码重复打印 更新有效期
	 */
    @RequestMapping("/updateEffectDate")
    public List<Map<String, Object>> updateEffectDate(@RequestParam Map<String, Object> params){
    	return wmsKnLabelRecordService.updateEffectDate(params);
    }
}
