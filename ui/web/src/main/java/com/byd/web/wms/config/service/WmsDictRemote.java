package com.byd.web.wms.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.wms.config.entity.SysDictEntity;

/**
 * 数据字典
 *
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@FeignClient(name = "WMS-SERVICE")
public interface WmsDictRemote {

    /**
     * 列表
     */
    @RequestMapping(value = "/wms-service/config/dict/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);


    /**
     * 信息
     */
    @RequestMapping(value = "/wms-service/config/dict/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);

    /**
     * 保存
     */
    @RequestMapping(value = "/wms-service/config/dict/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody SysDictEntity dict);

    /**
     * 修改
     */
    @RequestMapping(value = "/wms-service/config/dict/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody SysDictEntity dict);

    /**
     * 删除
     */
    @RequestMapping(value = "/wms-service/config/dict/deleteById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteById(@RequestParam(value="id") long id);
    
    /**
     * 删除
     */
    @RequestMapping(value = "/wms-service/config/dict/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);
    /**
     * 获取列表
     */
    @RequestMapping(value = "/wms-service/config/dict/getDictlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getDictlist(@RequestParam(value="params") Map<String, Object> params);
    
    @RequestMapping(value = "/wms-service/config/dict/selectByMap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SysDictEntity> selectByMap(@RequestParam(value="params") Map<String,Object> params);
}
