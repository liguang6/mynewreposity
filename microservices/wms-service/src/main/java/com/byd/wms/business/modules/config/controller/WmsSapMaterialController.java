package com.byd.wms.business.modules.config.controller;


import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;

/**
 * 物料信息表 SAP同步获取
 *
 * @author cscc
 * @email 
 * @date 2018-08-14 08:45:52
 */
@RestController
@RequestMapping("config/sapmaterial")
public class WmsSapMaterialController {
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsSapMaterialService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info")
    public R info(@RequestParam("id") Long id){
        WmsSapMaterialEntity wmsSapMaterial = wmsSapMaterialService.selectById(id);

        return R.ok().put("wmsSapMaterial", wmsSapMaterial);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsSapMaterialEntity wmsSapMaterial){
        wmsSapMaterialService.insert(wmsSapMaterial);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsSapMaterialEntity wmsSapMaterial){
        wmsSapMaterialService.updateAllColumnById(wmsSapMaterial);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsSapMaterialService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
