package com.byd.wms.business.modules.config.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreStorageSearchEntity;
import com.byd.wms.business.modules.config.service.WmsCoreSearchSequenceService;
import com.byd.wms.business.modules.config.service.WmsCoreStorageSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * @ClassName WmsCoreStorageSearchController
 * @Description  分配存储类型至搜索顺序配置
 * @Author qiu.jiaming1
 * @Date 2019/2/28
 */

@RestController
@RequestMapping("config/wmsCoreStorageSearch")
public class WmsCoreStorageSearchController {
    @Autowired
    private WmsCoreStorageSearchService wmsCoreStorageSearchService;

     @Autowired
     private RedisTemplate redisTemplate;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCoreStorageSearchService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        WmsCoreStorageSearchEntity storageType = wmsCoreStorageSearchService.selectById(id);
        return R.ok().put("storageType", storageType);
    }

    /**
     * 查询名称
     */
    @RequestMapping("/infoName")
    public R info(String warehouseCode,String storageAreaCode){
        String name = wmsCoreStorageSearchService.queryAreaName(warehouseCode,storageAreaCode);
        System.out.println(name);
        return R.ok().put("resuslt", name);
    }

    /**
     * 根据仓库号查询搜索顺序列表
     */
    @RequestMapping("/getStorageAreaSearch")
    public R getStorageAreaSearch(String warehouseCode){
        List name = wmsCoreStorageSearchService.getStorageAreaSearch(warehouseCode);
        //System.out.println(name.toString());
        return R.ok().put("resuslt", name);
    }

    /**
     * 根据仓库号查询储存类型列表
     */
    @RequestMapping("/getStorageAreaCode")
    public R getStorageAreaCode(String warehouseCode){
        List name = wmsCoreStorageSearchService.getStorageAreaCode(warehouseCode);
        //System.out.println(name.toString());
        return R.ok().put("resuslt", name);
    }



    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreStorageSearchEntity storage){
        List<WmsCoreStorageSearchEntity> list=wmsCoreStorageSearchService.selectList(
    			new EntityWrapper<WmsCoreStorageSearchEntity>()
                  .eq("WH_NUMBER", storage.getWarehouseCode())
                  .eq("SEARCH_SEQ", storage.getStorageAreaSearch())
                  .eq("STORAGE_AREA_CODE", storage.getStorageAreaCode())
                  .eq("del","0")
        );
    	if(list.size()>0) {
    		return R.error("存储类型至搜索顺序配置已维护！");
    	}else {
            List<WmsCoreStorageSearchEntity> list2=wmsCoreStorageSearchService.selectList(
                    new EntityWrapper<WmsCoreStorageSearchEntity>()
                            .eq("WH_NUMBER", storage.getWarehouseCode())
                            .eq("SEARCH_SEQ", storage.getStorageAreaSearch())
                            .eq("del","0"));

            if(list2.size()>0){
                for (int i = 0 ;i<list2.size();i++){
                    if(list2.get(i).getPriority().equals(storage.getPriority())){
                        return R.error("同一仓库代码、搜索顺序，优先级不可重复！");
                    }
                }
            }

            wmsCoreStorageSearchService.insert(storage);
        	return R.ok();
    	}
    }

    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreStorageSearchEntity wh){
        wmsCoreStorageSearchService.updateById(wh);
        return R.ok();
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCoreStorageSearchEntity entity){
        wmsCoreStorageSearchService.updateById(entity);
		return R.ok();
    }
    // 查询All
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params){
        List<WmsCoreStorageSearchEntity> list =
                wmsCoreStorageSearchService.selectList(new EntityWrapper<WmsCoreStorageSearchEntity>().
    			eq("DEL","0"));
        return R.ok().put("list", list);
    }
}


	 