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
import com.byd.web.sys.masterdata.entity.SysDeptEntity;

/**
 * 标准班组、小班组服务
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface WorkgroupRemote {
    
    /**
     * 列表
     */
    @RequestMapping(value = "/admin-service/masterdata/workgroup/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R list(@RequestParam(value="params") Map<String, Object> params);


    /**
     * 信息
     */
    @RequestMapping(value = "/admin-service/masterdata/workgroup/info/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@PathVariable("id") Long id);

    /**
     * 保存
     */
    @RequestMapping(value = "/admin-service/masterdata/workgroup/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R save(@RequestBody SysDeptEntity dept);

    /**
     * 修改
     */
    @RequestMapping(value = "/admin-service/masterdata/workgroup/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R update(@RequestBody SysDeptEntity dept);

    /**
     * 删除
     */
    @RequestMapping(value = "/admin-service/masterdata/workgroup/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@RequestBody Long[] ids);
    
    /**
     * 删除单条记录
     * @param id
     * @return
     */
    @RequestMapping(value = "/admin-service/masterdata/workgroup/delete_single", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteSingle(@RequestParam(value="id") Long id);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/editWorkgroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R editWorkgroup(@RequestParam(value="id") Long id);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/editTeam", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R editTeam(@RequestParam(value="id") Long id);
    
    /**
     * 通过线别查询
     */
    @RequestMapping(value = "/admin-service/masterdata/workgroup/listByLineName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R info(@RequestParam(value="params") Map<String, Object> params);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getWorkgroupList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkgroupList(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getTeamList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getTeamList(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/workGrouplistByPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R workGrouplistByPage(@RequestParam(value="map") Map<String,String> map);
    
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getStandardWorkGroupList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getStandardWorkGroupList(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getWorkShopByCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkShopByCode(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/saveDept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R saveDept(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getWorkGroupById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkGroupById(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/updateDept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R updateDept(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/delete_dept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete_dept(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getWorkTeamListByPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkTeamListByPage(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getWorkGroupByCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkGroupByCode(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getWorkTeamById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkTeamById(@RequestParam(value="map") Map<String,String> map);
    
    @RequestMapping(value = "/admin-service/masterdata/workgroup/getDeptWorkTeamById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getDeptWorkTeamById(@RequestParam(value="map") Map<String,String> map);
}
