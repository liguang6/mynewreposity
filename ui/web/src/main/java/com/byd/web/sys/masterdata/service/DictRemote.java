package com.byd.web.sys.masterdata.service;

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
import com.byd.web.sys.masterdata.entity.DictEntity;

/**
 * 数据字典
 *
 * @author develop01 
 * @since 3.1.0 2019-03-21
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface DictRemote {

    /**
     * 列表
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);


    /**
     * 信息
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);

    /**
     * 保存
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody DictEntity dict);

    /**
     * 修改
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody DictEntity dict);

    /**
     * 删除
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/deleteById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteById(@RequestParam(value="id") long id);
    
    /**
     * 删除
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);
    /**
     * 获取列表
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/getDictlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getDictlist(@RequestParam(value="params") Map<String, Object> params);
    
    @RequestMapping(value = "/admin-service/masterdata/dict/selectByMap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DictEntity> selectByMap(@RequestParam(value="params") Map<String,Object> params);
}
