package com.byd.web.sys.masterdata.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.sys.masterdata.entity.DictEntity;


/**
 * 主数据对外提供服务类
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface MasterDataRemote{
	
	/**
	 * 获取所有工厂清单
	 * @param params CODE 工厂代码
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getWerksList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWerksList(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
	 * 根据工厂代码获取工厂下所有车间
	 * @param params CODE 工厂代码
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getWerksWorkshopList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWerksWorkshopList(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
	 * 根据工厂代码、车间获取车间下所有班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getWorkshopWorkgroupList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkshopWorkgroupList(@RequestParam Map<String, Object> params);
    
	/**
	 * 根据工厂代码、车间、班组获取所有小班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getTeamList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getTeamList(@RequestParam Map<String, Object> params);
    
	/**
	 * 获取所有车型清单
	 * @param params VEHICLE_TYPE 车辆类型
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getBusTypeList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getBusTypeList(@RequestParam Map<String, Object> params);
    
    /**
	 * 根据工厂代码获取用户权限车间
	 * @param params CODE/WERKS 工厂代码 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getUserWorkshopByWerks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getUserWorkshopByWerks(@RequestParam Map<String, Object> params);
    
    /**
	 * 根据工厂代码、车间获取用户权限线别
	 * @param params WERKS 工厂代码  WORKSHOP 车间
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getUserLine", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getUserLine(@RequestParam Map<String, Object> params);
    
	/**
	 * 根据工厂代码、车间获取车间下所有线别信息
	 * @param 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getWorkshopLineList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkshopLineList(@RequestParam Map<String, Object> params);
    
	/**
	 * 获取工厂、车间下所有标准工序信息
	 * @param 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getWorkshopProcessList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getWorkshopProcessList(@RequestParam Map<String, Object> params);
    
    /**
     * 获取主数据字典列表
     */
    @RequestMapping(value = "/admin-service/masterdata/dict/getDictlistByType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String,Object>> getDictlistByType(@RequestParam(value="params") Map<String, Object> params);
    
	/**
	 * 获取品质检验标准清单
	 * @param STANDARD_TYPE：检验标准类型 如外观标准，STANDARD_NAME：标准描述  pageSize 限制匹配记录条数
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getQmsTestStandard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getQmsTestStandard(@RequestParam Map<String, Object> params);
    
	/**
	 * 获取品质抽样规则
	 * @param WERKS：工厂代码 WORKSHOP：车间代码/名称  ORDER_AREA_CODE：订单区域代码
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getQmsTestRules", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getQmsTestRules(@RequestParam Map<String, Object> params);
    
	/**
	 * 获取品质检验工具
	 * @param params WERKS：工厂代码  TEST_TOOL_NO： 检具编码  TEST_TOOL_NAME：检具名称
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getQmsTestToolList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getQmsTestToolList(@RequestParam Map<String, Object> params);
    
	/**
	 * 根据图号获取图纸临时文件地址
	 * @param params material_no/MATERIAL_NO：图号
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getMaterialNoMapFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getMaterialNoMapFile(@RequestParam Map<String, Object> params);
    
	/**
	 * 图纸库检索
	 * @param 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/pmdMapQuery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R pmdMapQuery(@RequestParam Map<String, Object> params);
    
    @RequestMapping(value = "/admin-service/masterdataService/getPmdMapList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getPmdMapList(@RequestParam Map<String, Object> params);
    
    /**
	 * 根据工厂代码、车间获取所有小班组信息
	 * @param 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getTeamListByWorkshop", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getTeamListByWorkshop(@RequestParam Map<String, Object> params);
	
	/**
	 * 模糊查找工序
	 * @param 
	 * @return
	 */
    @RequestMapping(value = "/admin-service/masterdataService/getProcessList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getProcessList(@RequestParam Map<String, Object> params);
}
