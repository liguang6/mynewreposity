package com.byd.admin.modules.masterdata.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.masterdata.service.TestStandardService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月20日 上午9:22:53 
 * 类说明 
 */
@RestController
@RequestMapping("/masterdata/testStandard")
public class TestStandardController {
	@Autowired
    private TestStandardService testStandardService;
	@Autowired
    private UserUtils userUtils;

	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = testStandardService.getQmsTestStandardListByPage(params);

        return R.ok().put("page", page);
    }
    
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("ID", id);
    	Map<String, Object> retMap=testStandardService.selectById(param);

        return R.ok().put("result", retMap);
    }
    
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("DEL", "0");
    	params.put("CREATOR", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	params.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	params.put("EDITOR", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	params.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	testStandardService.insertQmsTestStandard(params);
    	return R.ok();
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
    	params.put("EDITOR", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
    	params.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	testStandardService.updateQmsTestStandard(params);
    	return R.ok();
    }
    
    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	Map<String, Object> param=new HashMap<String, Object>();
    	param.put("ID", id);
    	testStandardService.delQmsTestStandard(param);

        return R.ok();
    }
}
