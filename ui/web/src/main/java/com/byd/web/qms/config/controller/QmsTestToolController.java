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
import com.byd.web.qms.config.service.QmsTestToolRemote;
/**
 * 品质 检具库
 * @author cscc tangj
 * @email 
 * @date 2019-07-22 14:12:08
 */
@RestController
@RequestMapping("qms/config/testTool")
public class QmsTestToolController {
    @Autowired
    private QmsTestToolRemote qmsTestToolRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return qmsTestToolRemote.queryPage(params);
    }
    /**
     * 
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return qmsTestToolRemote.info(id);
    }
    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	return qmsTestToolRemote.delById(id);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return qmsTestToolRemote.save(params);
    }
    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return qmsTestToolRemote.update(params);
    }
    /**
     * 根据条件查询  不分页
     */
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
    	return qmsTestToolRemote.getList(params);
    }
}