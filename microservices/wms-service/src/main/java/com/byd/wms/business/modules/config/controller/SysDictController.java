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

package com.byd.wms.business.modules.config.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.common.annotation.SysLog;
import com.byd.wms.business.modules.config.entity.SysDictEntity;
import com.byd.wms.business.modules.config.service.SysDictService;

/**
 * 数据字典
 *
 * @author Mark 
 * @since 3.1.0 2018-01-27
 */
@RestController
@RequestMapping("config/dict")
public class SysDictController {
    @Autowired
    private SysDictService sysDictService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysDictService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        SysDictEntity dict = sysDictService.selectById(id);

        return R.ok().put("dict", dict);
    }

    /**
     * 保存
     */
    @SysLog("字段管理-新增")
    @RequestMapping("/save")
    public R save(@RequestBody SysDictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);

        sysDictService.insert(dict);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("字段管理-修改")
    @RequestMapping("/update")
    public R update(@RequestBody SysDictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);

        sysDictService.updateById(dict);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("字段管理-修改")
    @RequestMapping("/deleteById")
    public R deleteById(long id){
        sysDictService.deleteById(id);

        return R.ok();
    }
    
    /**
     * 删除
     */
    @SysLog("字段管理-批量删除")
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        sysDictService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 获取列表
     */
    @RequestMapping("/getDictlist")
    public R getDictlist(@RequestParam Map<String, Object> params){
        List<SysDictEntity> list = sysDictService.selectByMap(params);
        return R.ok().put("list", list);
    }
    
    /**
     * 获取列表
     */
    @RequestMapping("/selectByMap")
    public List<SysDictEntity> selectByMap(@RequestParam Map<String, Object> params){
        List<SysDictEntity> list = sysDictService.selectByMap(params);
        return list;
    }
}
