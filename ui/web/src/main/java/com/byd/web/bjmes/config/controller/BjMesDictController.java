package com.byd.web.bjmes.config.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.bjmes.config.service.BjMesDictRemote;
/**
 * 品质 字典数据
 * @author cscc
 * @email 
 * @date 2019-07-29 09:12:08
 */
@RestController
@RequestMapping("bjmes/config/bjMesDict")
public class BjMesDictController {
    @Autowired
    private BjMesDictRemote bjMesDictRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return bjMesDictRemote.queryPage(params);
    }
    /**
     * 
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return bjMesDictRemote.info(id);
    }
    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	return bjMesDictRemote.delById(id);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return bjMesDictRemote.save(params);
    }
    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return bjMesDictRemote.update(params);
    }
    /**
     * 根据条件查询  不分页
     */
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
    	return bjMesDictRemote.getList(params);
    }
}