package com.byd.web.wms.config.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.web.wms.config.entity.WmsSapMaterialEntity;
import com.byd.web.wms.config.service.WmsSapMaterialRemote;

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
    private WmsSapMaterialRemote wmsSapMaterialRemote;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsSapMaterialRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return wmsSapMaterialRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsSapMaterialEntity wmsSapMaterial){
    	return wmsSapMaterialRemote.save(wmsSapMaterial);

    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsSapMaterialEntity wmsSapMaterial){
        ValidatorUtils.validateEntity(wmsSapMaterial);
        return wmsSapMaterialRemote.update(wmsSapMaterial);//全部更新
        
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
    	return wmsSapMaterialRemote.delete(ids);
    }

}
