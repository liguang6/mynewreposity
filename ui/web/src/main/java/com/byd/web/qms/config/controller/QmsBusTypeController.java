package com.byd.web.qms.config.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.qms.config.service.QmsBusTypeRemote;
/**
 * 品质 车型
 * @author cscc tangj
 * @email 
 * @date 2019-07-30 14:12:08
 */
@RestController
@RequestMapping("qms/config/busType")
public class QmsBusTypeController {
    @Autowired
    private QmsBusTypeRemote qmsBusTypeRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return qmsBusTypeRemote.queryPage(params);
    }
    /**
     * 
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return qmsBusTypeRemote.info(id);
    }
    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	return qmsBusTypeRemote.delById(id);
    }
    /**
     * 模板导入批量保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return qmsBusTypeRemote.save(params);
    }
    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return qmsBusTypeRemote.update(params);
    }
    /**
     * 根据条件查询  不分页
     */
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
    	return qmsBusTypeRemote.getList(params);
    }
}