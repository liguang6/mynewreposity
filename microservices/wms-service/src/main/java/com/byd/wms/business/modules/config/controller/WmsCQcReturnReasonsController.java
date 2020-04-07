package com.byd.wms.business.modules.config.controller;


import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCQcReturnReasonsEntity;
import com.byd.wms.business.modules.config.service.WmsCQcReturnReasonsService;


/**
 * 退货原因配置表
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-15 17:10:53
 */
@RestController
@RequestMapping("config/wmscqcreturnreasons")
public class WmsCQcReturnReasonsController {
    @Autowired
    private WmsCQcReturnReasonsService wmsCQcReturnReasonsService;

    /**
     * 列表
     */
    @CrossOrigin
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wmsCQcReturnReasonsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @CrossOrigin
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        WmsCQcReturnReasonsEntity wmsCQcReturnReasons = wmsCQcReturnReasonsService.selectById(id);

        return R.ok().put("wmsCQcReturnReasons", wmsCQcReturnReasons);
    }

    /**
     * 保存
     */
    @CrossOrigin
    @RequestMapping("/save")
    public R save(@RequestBody WmsCQcReturnReasonsEntity wmsCQcReturnReasons){
        wmsCQcReturnReasonsService.insert(wmsCQcReturnReasons);

        return R.ok();
    }

    /**
     * 修改
     */
    @CrossOrigin
    @RequestMapping("/update")
    public R update(@RequestBody WmsCQcReturnReasonsEntity wmsCQcReturnReasons){
        wmsCQcReturnReasonsService.updateAllColumnById(wmsCQcReturnReasons);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @CrossOrigin
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        wmsCQcReturnReasonsService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
