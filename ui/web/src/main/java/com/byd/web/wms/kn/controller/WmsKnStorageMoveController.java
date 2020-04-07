package com.byd.web.wms.kn.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.wms.kn.service.WmsKnStorageMoveRemote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 移储
 *
 * @author cscc tangj
 * @email 
 * @date 2018-11-07 10:12:08
 */
@RestController
@RequestMapping("kn/storageMove")
public class WmsKnStorageMoveController {
    @Autowired
    private WmsKnStorageMoveRemote wmsKnStorageMoveRemote;
    @Autowired
    private UserUtils userUtils;
    
    /**
     * 查询移储记录
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return wmsKnStorageMoveRemote.list(params);
    }
    /**
     * 查询库存信息（用于移储操作）
     */
    @RequestMapping("/getStockList")
    public R getStockList(@RequestParam Map<String, Object> params){
        return wmsKnStorageMoveRemote.getStockList(params);
    }
    /**
     * 保存移储记录
     */
    @RequestMapping("/save")
    public R save(@RequestParam Map<String, Object> params){
    	Map<String,Object> user = userUtils.getUser();
    	Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) params.get("SAVE_DATA"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		list.forEach(k->{
			k=(Map<String,Object>)k;	
			k.put("EDITOR",user.get("USERNAME")+"："+user.get("FULL_NAME"));
			k.put("EDIT_DATE",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		});
		return wmsKnStorageMoveRemote.save(list);
    	
    }
    /**
     * 目标储位校验
     */
    @RequestMapping("/checkBin")
    public R checkBin(@RequestParam Map<String, Object> params){
		return wmsKnStorageMoveRemote.checkBin(params);

    }
}
