package com.byd.web.sys.masterdata.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.masterdata.service.TestRulesRemote;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月20日 上午10:07:53 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/testRules")
public class TestRulesController {
	@Autowired
    private TestRulesRemote testRulesRemote;
	@Autowired
    private UserUtils userUtils;
	
	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return testRulesRemote.list(params);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return testRulesRemote.info(id);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	return testRulesRemote.save(params);
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
        return testRulesRemote.update(params);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	return testRulesRemote.deleteById(id);
    }
}
