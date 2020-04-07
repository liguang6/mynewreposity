package com.byd.web.wms.config.controller;

import java.util.Date;
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
import com.byd.web.wms.config.entity.WmsCKeyPartsEntity;
import com.byd.web.wms.config.service.WmsCKeyPartsRemote;

/**
 * 工厂关键零部件配置表
 *
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
@RestController
@RequestMapping("config/keyparts")
public class WmsCKeyPartsController {
    @Autowired
    private WmsCKeyPartsRemote wmsCKeyPartsRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCKeyPartsRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCKeyPartsRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCKeyPartsEntity wmsCKeyParts){
        Map<String,Object> user = userUtils.getUser();
		wmsCKeyParts.setCreator(user.get("USERNAME")+"："+user.get("FULL_NAME"));
		wmsCKeyParts.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		wmsCKeyParts.setDel("0");
    	return wmsCKeyPartsRemote.save(wmsCKeyParts);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCKeyPartsEntity wmsCKeyParts){
        Map<String,Object> user = userUtils.getUser();
        wmsCKeyParts.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
        wmsCKeyParts.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCKeyPartsRemote.update(wmsCKeyParts);
    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Long[] ids){
//        wmsCKeyPartsService.deleteBatchIds(Arrays.asList(ids));
//
//        return R.ok();
//    }
    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCKeyPartsEntity entity = new WmsCKeyPartsEntity();
        Map<String,Object> user = userUtils.getUser();
        entity.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
    	entity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		entity.setId(id);
		entity.setDel("X");
		return wmsCKeyPartsRemote.delById(entity);
    }
    
    /**
	 * 批量保存
	 * @return
	 * @throws IllegalAccessException 
	 */
	@RequestMapping("/batchSave")
	@ResponseBody
	public R batchSave(@RequestParam Map<String, Object> params) throws IllegalAccessException{
        Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME")+"："+user.get("FULL_NAME"));
		return wmsCKeyPartsRemote.batchSave(params);
	}
	/**
	 * 粘贴数据校验
	 * @param excel
	 */
	@RequestMapping("/checkPasteData")
	@ResponseBody
	public R checkPasteData(@RequestParam Map<String,Object> params){
		return wmsCKeyPartsRemote.checkPasteData(params);
	}
}
