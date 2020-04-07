package com.byd.wms.business.modules.config.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.wms.business.modules.config.service.WmsCoreSearchSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @ClassName WmsCGrAreaController
 * @Description  入库存储类型搜索顺序配置管理
 * @Author qiu.jiaming1
 * @Date 2019/2/28
 */

@RestController
@RequestMapping("config/wmsCoreSearchSequence")
public class WmsCoreSearchSequenceController {
    @Autowired
    private WmsCoreSearchSequenceService wmsCoreSearchSequenceService;

     @Autowired
     private RedisTemplate redisTemplate;
    /**
     * 分页查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCoreSearchSequenceService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 单条记录查询By ID
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        WmsCoreSearchSequenceEntity storageType = wmsCoreSearchSequenceService.selectById(id);
        return R.ok().put("storageType", storageType);
    }

    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreSearchSequenceEntity storage){
        List<WmsCoreSearchSequenceEntity> list=wmsCoreSearchSequenceService.selectList(
    			new EntityWrapper<WmsCoreSearchSequenceEntity>()
                  .eq("WH_NUMBER", storage.getWarehouseCode())
                  .eq("SEARCH_SEQ", storage.getStorageAreaSearch())
                  .eq("SEARCH_SEQ_TYPE", storage.getSearchSequenceType())
                  .eq("del","0")
        );
        
    	if(list.size()>0) {
    		return R.error("存储类型搜索顺序已维护！");
    	}else {
            wmsCoreSearchSequenceService.insert(storage);
        	return R.ok();
    	}
    }

    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreSearchSequenceEntity wh){
        wmsCoreSearchSequenceService.updateById(wh);
        return R.ok();
    }

    /**
     * 软删BY ID
     */
    @RequestMapping("/delById")
    public R delById(@RequestBody WmsCoreSearchSequenceEntity entity){
        wmsCoreSearchSequenceService.updateById(entity);
		return R.ok();
    }
    // 查询All
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params){
        List<WmsCoreSearchSequenceEntity> list =
                wmsCoreSearchSequenceService.selectList(new EntityWrapper<WmsCoreSearchSequenceEntity>().
    			eq("DEL","0"));
        return R.ok().put("list", list);
    }
}


	 