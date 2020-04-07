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
import com.byd.web.qms.config.service.QmsCheckNodeRemote;
import com.byd.web.qms.config.service.QmsQualityTargetParamterRemote;
/**
 * 品质 质量目标参数
 * @author cscc tangj
 * @email 
 * @date 2019-07-29 16:12:08
 */
@RestController
@RequestMapping("qms/config/targetParamter")
public class QmsQualityTargetParamterController {
    @Autowired
    private QmsQualityTargetParamterRemote targetParamterRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return targetParamterRemote.queryPage(params);
    }
    /**
     * 
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return targetParamterRemote.info(id);
    }
    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	return targetParamterRemote.delById(id);
    }
    /**
     * 模板导入批量保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return targetParamterRemote.save(params);
    }
    /**
     * 更新
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> params){
        Map<String,Object> user = userUtils.getUser();
        params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	return targetParamterRemote.update(params);
    }
    /**
     * 根据条件查询  不分页
     */
    @RequestMapping("/getList")
    public R getList(@RequestParam Map<String, Object> params){
    	return targetParamterRemote.getList(params);
    }
}