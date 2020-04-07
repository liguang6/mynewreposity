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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.SysDictEntity;
import com.byd.web.wms.config.service.WmsDictRemote;

/**
 * 数据字典
 *
 * @author Mark 
 * @since 3.1.0 2018-01-27
 */
@RestController
@RequestMapping("wms/dict")
public class SysDictController {
    @Autowired
    private WmsDictRemote sysDictRemote;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	return sysDictRemote.list(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
    	return sysDictRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysDictEntity dict){
        return sysDictRemote.save(dict);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysDictEntity dict){
    	return sysDictRemote.update(dict);
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
    	return sysDictRemote.deleteById(id);
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
    	return sysDictRemote.delete(ids);
    }
    /**
     * 获取列表
     */
    @RequestMapping("/getDictlist")
    public R getDictlist(@RequestParam Map<String, Object> params){
    	return sysDictRemote.getDictlist(params);
    }
}
