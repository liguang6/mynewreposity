package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.service.WmsCMatManagerTypeRemote;

/**
 * 仓库人料关系模式配置
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
@RestController
@RequestMapping("config/matmanagertype")
public class WmsCMatManagerTypeController {
    @Autowired
    private WmsCMatManagerTypeRemote wmsCMatManagerTypeRemote;
    @Autowired
    private UserUtils userUtils;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCMatManagerTypeRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCMatManagerTypeRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
	    params.put("creator",user.get("USERNAME")+"："+user.get("FULL_NAME"));
	    params.put("createDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    params.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
	    params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    params.put("del","0");
        return wmsCMatManagerTypeRemote.save(params);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
        params.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
	    params.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCMatManagerTypeRemote.update(params);
    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody String[] memos){
//        wmsCMatManagerService.deleteBatchIds(Arrays.asList(memos));
//
//        return R.ok();
//    }
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	Map<String,Object> user = userUtils.getUser();
    	if(id == null){
			return R.error("参数错误");
		}
    	Map<String,Object> entity = new HashMap<String,Object>();
    	entity.put("editor",user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	entity.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.put("id",id);
		entity.put("del","X");
		return wmsCMatManagerTypeRemote.delById(entity);
    }
    /**
	 * 粘贴数据校验
	 * @param excel
	 * @return
	 */
	@RequestMapping("/checkPasteData")
	@ResponseBody
	public R checkPasteData(@RequestParam Map<String,Object> params){
		return wmsCMatManagerTypeRemote.checkPasteData(params);
	}
	/**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/batchSave")
	@ResponseBody
	public R batchSave(@RequestParam Map<String,Object> params) throws IllegalAccessException{
		Map<String,Object> user = userUtils.getUser();
		params.put("user",user.get("USERNAME")+"："+user.get("FULL_NAME"));
		return wmsCMatManagerTypeRemote.batchSave(params);
	}
    /**
     * 加载储位select
     */
    @RequestMapping("/getLgortSelect")
    public R getLgortSelect(@RequestParam Map<String, Object> params){
        return wmsCMatManagerTypeRemote.getLgortSelect(params);
    }
    
    @RequestMapping("/getWhAreaSelect")
    public R getWhAreaSelect(@RequestParam Map<String, Object> params){
        return wmsCMatManagerTypeRemote.getWhAreaSelect(params);
    }
    @RequestMapping("/getAuthorityCodeSelect")
    public R getAuthorityCodeSelect(@RequestParam Map<String, Object> params){
        return wmsCMatManagerTypeRemote.getAuthorityCodeSelect(params);
    }
}
