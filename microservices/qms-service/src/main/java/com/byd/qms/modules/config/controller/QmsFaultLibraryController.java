package com.byd.qms.modules.config.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.qms.modules.config.service.QmsFaultLibraryService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
/**
 * 品质 标准故障库
 * @author cscc tangj
 * @email 
 * @date 2019-07-23 09:12:08
 */
@RestController
@RequestMapping("config/qmsFaultLibrary")
public class QmsFaultLibraryController {
    @Autowired
    private QmsFaultLibraryService qmsFaultLibraryService;

    /**
     * 列表
     */
    @RequestMapping("/queryPage")
    public R queryPage(@RequestBody Map<String, Object> params){
        PageUtils page = qmsFaultLibraryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        Map<String,Object> entity = qmsFaultLibraryService.getById(id);
        return R.ok().put("entity", entity);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map<String, Object> param){
    	param.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	List<Map<String,Object>> list=qmsFaultLibraryService.getList(param);
    	if(list.size()>0) {
    		return R.error("故障库信息【"+param.get("faultName").toString()+"】已维护!");
    	}
    	int result=qmsFaultLibraryService.save(param);

    	return result>0 ? R.ok(): R.error("新增失败，请联系管理员");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map<String, Object> param){
    	param.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	int result=qmsFaultLibraryService.update(param);
        
    	return result>0 ? R.ok(): R.error("更新失败，请联系管理员");
    }

    /**
     * 删除
     */
    @RequestMapping("/delById/{id}")
    public R delById(@PathVariable("id") Long id){
    	
    	int result=qmsFaultLibraryService.delete(id);
		return result>0 ? R.ok(): R.error("删除失败，请联系管理员");
    }

}