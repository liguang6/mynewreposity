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
import com.byd.web.qms.config.service.QmsFaultLibraryRemote;
/**
 * 品质  标准故障库
 * @author cscc tangj
 * @email 
 * @date 2019-07-22 14:12:08
 */
@RestController
@RequestMapping("qms/config/qmsFaultLibrary")
public class QmsFaultLibraryController {
    @Autowired
    private QmsFaultLibraryRemote qmsFaultLibraryRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return qmsFaultLibraryRemote.queryPage(params);
    }
    /**
     * 
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return qmsFaultLibraryRemote.info(id);
    }
    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	return qmsFaultLibraryRemote.delById(id);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return qmsFaultLibraryRemote.save(params);
    }
    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return qmsFaultLibraryRemote.update(params);
    }
}