/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.web.wms.config.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCoreWhEntity;
import com.byd.web.wms.config.entity.WmsCoreWh_AddressEntity;
import com.byd.web.wms.config.service.WmsCoreWhRemote;

/**
 * 仓库信息维护
 *
 * @author tangj 
 * @since 2018-07-31
 */
@RestController
@RequestMapping("config/wh")
public class WmsCoreWhController {
    @Autowired
    private WmsCoreWhRemote wmsCoreWhRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCoreWhRemote.list(params);
    }
    /**
     * 根据ID查找记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam Map<String, Object> params){
        return wmsCoreWhRemote.info(params);
    }
    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreWh_AddressEntity wh){
    	wh.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	wh.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    	wh.setDel("0");
    	return wmsCoreWhRemote.save(wh);
    	
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreWh_AddressEntity wh){
    	wh.setEditor(userUtils.getUser().get("USERNAME")+":"+userUtils.getUser().get("FULL_NAME"));
    	wh.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return wmsCoreWhRemote.update(wh);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(@RequestParam Map<String, Object> params){
    	if(params == null){
			return R.error("参数错误");
		}
		return wmsCoreWhRemote.delById(params);
    }
    
    /**
     * 批量删除(软删)
     */
//    @RequestMapping("/delete")
//   // @RequiresPermissions("sys:dict:delete")
//    public R delete(@RequestBody Long[] ids){
//        wmsCoreWhService.deleteBatch(Arrays.asList(ids));
//        return R.ok();
//    }
    
}
