
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
import com.byd.web.wms.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.web.wms.config.service.WmsCoreSearchSequenceRemote;

/**
 * @ClassName SysDataPermissionController
 * @Description  存储类型搜索顺序配置
 * @Author qiu.jiaming1
 * @Date 2019/2/28
 */
@RestController
@RequestMapping("config/wmsCoreSearchSequence")
public class WmsCoreSearchSequenceController {
    @Autowired
    private WmsCoreSearchSequenceRemote wmsCoreSearchSequenceRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCoreSearchSequenceRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Long id){
        return wmsCoreSearchSequenceRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreSearchSequenceEntity entity){
		entity.setCreator(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
		entity.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setDel("0");
    	return wmsCoreSearchSequenceRemote.save(entity);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreSearchSequenceEntity entity){
    	entity.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCoreSearchSequenceRemote.update(entity);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
        WmsCoreSearchSequenceEntity params = new WmsCoreSearchSequenceEntity();
    	params.setId(id);
    	params.setDel("X");
		return wmsCoreSearchSequenceRemote.delById(params);
    }
    
    // 查询All
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params){
        return wmsCoreSearchSequenceRemote.queryAll(params);
    }
}


	 