package com.byd.web.sys.masterdata.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.sys.masterdata.entity.ProcessEntity;



/**
 * 标准工序表-基础数据
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface ProcessRemote {
    
    /**
     * 列表
     */
    @RequestMapping(value = "/admin-service/masterdata/process/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);


    /**
     * 信息
     */
    @RequestMapping(value = "/admin-service/masterdata/process/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);

    /**
     * 保存
     */
    @RequestMapping(value = "/admin-service/masterdata/process/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody ProcessEntity settingProcess);

    /**
     * 修改
     */
    @RequestMapping(value = "/admin-service/masterdata/process/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody ProcessEntity settingProcess);

    /**
     * 删除
     */
    @RequestMapping(value = "/admin-service/masterdata/process/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);
    
    /**
     * 删除单条记录
     * @param id
     * @return
     */
    @RequestMapping(value = "/admin-service/masterdata/process/delete_single", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteSingle(@RequestParam(value="id") Long id);
    
    @RequestMapping(value = "/admin-service/masterdata/process/editorProcess", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R editorProcess(@RequestParam(value="processId") Long processId);
    
    /**
     * 通过线别查询
     */
    @RequestMapping(value = "/admin-service/masterdata/process/listByLineName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value="params") Map<String, Object> params);
    // 模糊查找工序
    @RequestMapping(value = "/admin-service/masterdata/process/getProcessList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getProcessList(@RequestParam(value="map") Map<String,String> map);
}
