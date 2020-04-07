
package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCoreStorageSearchEntity;
import com.byd.web.wms.config.service.WmsCoreStorageSearchRemote;

/**
 * @ClassName WmsCoreStorageSearchController
 * @Description  分配存储类型至搜索顺序配置
 * @Author qiu.jiaming1
 * @Date 2019/3/4
 */
@RestController
@RequestMapping("config/wmsCoreStorageSearch")
public class WmsCoreStorageSearchController {
    @Autowired
    private WmsCoreStorageSearchRemote wmsCoreStorageSearchRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCoreStorageSearchRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        return wmsCoreStorageSearchRemote.info(id);
    }


    /**
     * 查询存储区名称
     * @param warehouseCode
     * @param storageAreaCode
     * @return
     */
    @RequestMapping("/infoName")
    public R info(@RequestParam String warehouseCode,@RequestParam String storageAreaCode){
        System.err.println(wmsCoreStorageSearchRemote.queryAreaName(warehouseCode,storageAreaCode));
        return wmsCoreStorageSearchRemote.queryAreaName(warehouseCode,storageAreaCode);
    }


    /**
     * 根据仓库号查询存储区列表
     * @param warehouseCode
     * @return
     */
    @RequestMapping("/getStorageAreaSearch")
    public R getStorageAreaSearch(@RequestParam String warehouseCode){
        System.err.println(wmsCoreStorageSearchRemote.getStorageAreaSearch(warehouseCode));
        return wmsCoreStorageSearchRemote.getStorageAreaSearch(warehouseCode);
    }

    /**
     * 根据仓库号查询存储区列表
     * @param warehouseCode
     * @return
     */
    @RequestMapping("/getStorageAreaCode")
    public R getStorageAreaCode(@RequestParam String warehouseCode){
        System.err.println(wmsCoreStorageSearchRemote.getStorageAreaCode(warehouseCode));
        return wmsCoreStorageSearchRemote.getStorageAreaCode(warehouseCode);
    }



    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreStorageSearchEntity entity){
		entity.setCreator(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		entity.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setDel("0");
    	return wmsCoreStorageSearchRemote.save(entity);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreStorageSearchEntity entity){
    	entity.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCoreStorageSearchRemote.update(entity);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
        WmsCoreStorageSearchEntity params = new WmsCoreStorageSearchEntity();
    	params.setId(id);
    	params.setDel("X");
		return wmsCoreStorageSearchRemote.delById(params);
    }
    
    // 查询All
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params){
        return wmsCoreStorageSearchRemote.queryAll(params);
    }
}


	 