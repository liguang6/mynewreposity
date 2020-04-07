package com.byd.web.wms.in.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.utils.UserUtils;
import com.byd.web.wms.in.service.WmsInternalBoundRemote;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年1月17日 下午4:05:44 
 * 类说明 
 */
@RestController
@RequestMapping("in/wmsinternalbound")
public class WmsInternalBoundController {
	
	@Autowired
	private WmsInternalBoundRemote wmsInternalBoundRemote;
	@Autowired
    private UserUtils userUtils;
	@Autowired
    private RedisUtils redisUtils;
	
	@RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.list(params);
	}
	
	@RequestMapping("/save")
    public R saveInbound(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInternalBoundRemote.saveInbound(params);
	}
	
	@RequestMapping("/querymaterial")
	public R querymaterial(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.querymaterial(params);
	}
	
	/**获取满箱数量
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/queryfullbox")
	public R queryfullbox(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.queryfullbox(params);
	}
	
	/**
	 * 获取储位
	 * @param params
	 * @return
	 */
	@RequestMapping("/querybincode")
	public R querybincode(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.querybincode(params);
	}
	
	/**
	 * 获取仓管员
	 * @param params
	 * @return
	 */
	@RequestMapping("/querywhmanager")
	public R querywhmanager(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.querywhmanager(params);
	}
	
	@RequestMapping("/save501")
    public R saveInbound501(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
		return wmsInternalBoundRemote.saveInbound501(params);
	}
	
	@RequestMapping("/save903")
    public R saveInbound903(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInternalBoundRemote.saveInbound903(params);
	}
	
	@RequestMapping("/save511")
    public R saveInbound511(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
		return wmsInternalBoundRemote.saveInbound511(params);
	}
	
	@RequestMapping("/preview511")
	public R previewExcel(@RequestParam Map<String, Object> params,MultipartFile excel) throws IOException{
		return wmsInternalBoundRemote.previewExcel(params);
	}
	
	@RequestMapping("/listMo101")
    public R listMo101(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
    	Set<Map<String,Object>> deptMapList = userUtils.getUserWerks("INTERNAL_INBOUND");
    	params.put("deptList", deptMapList);
		
		return wmsInternalBoundRemote.listMo101(params);
	}
	
	@RequestMapping("/listMo531")
    public R listMo531(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
	   	Set<Map<String,Object>> deptMapList = userUtils.getUserWerks("INTERNAL_INBOUND");
    	params.put("deptList", deptMapList);
		
		return wmsInternalBoundRemote.listMo531(params);
	}
	
	@RequestMapping("/save521")
    public R saveInbound521(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInternalBoundRemote.saveInbound521(params);
	}
	
	@RequestMapping("/save202")
    public R saveInbound202(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
		return wmsInternalBoundRemote.saveInbound202(params);
	}
	
	@RequestMapping("/save222")
    public R saveInbound222(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
		return wmsInternalBoundRemote.saveInbound222(params);
	}
	
	@RequestMapping("/save262")
    public R saveInbound262(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
		return wmsInternalBoundRemote.saveInbound262(params);
	}
	
	/**
	 * 创建CO订单
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/saveCo101")
    public R saveInboundCo101(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
   		params.put("USERNAME", currentUser.get("USERNAME"));
   		params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInternalBoundRemote.saveInboundCo101(params);
	}
	
	/**
	 * 查询CO订单
	 * @param params
	 * @return
	 */
	@RequestMapping("/listCo101")
    public R listCo101(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.listCo101(params);
	}
	
	/**
	 * 查询成本中心
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/listCostCenter202")
    public R listCostCenter202(@RequestParam Map<String, Object> params) throws ParseException{
		return wmsInternalBoundRemote.listCostCenter202(params);
	}
	
	/**
	 * 查询WBS元素号
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/listWBS222")
    public R listWBS222(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.listWBS222(params);
	}
	
	/**
	 * 查询研发订单号
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/listyf262")
    public R listyf262(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.listyf262(params);
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/listA101")
    public R listA101(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.listA101(params);
	}
	
	@RequestMapping("/listA531")
    public R listA531(@RequestParam Map<String, Object> params){
		return wmsInternalBoundRemote.listA531(params);
	}
	
	
	@RequestMapping("/listpo101")
    public R listpo101(@RequestParam Map<String, Object> params){
		/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
		Set<Map<String,Object>> deptMapList = userUtils.getUserWerks("INTERNAL_INBOUND");
    	params.put("deptList", deptMapList);
		return wmsInternalBoundRemote.listpo101(params);
	}
	
	@RequestMapping("/listMo262")
    public R listMo262(@RequestParam Map<String, Object> params){
		/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
		Set<Map<String,Object>> deptMapList = userUtils.getUserWerks("INTERNAL_INBOUND");
    	params.put("deptList", deptMapList);
		return wmsInternalBoundRemote.listMo262(params);
	}
	
	@RequestMapping("/saveMo262")
    public R saveInboundMo262(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInternalBoundRemote.saveInboundMo262(params);
	}
	
	@RequestMapping("/save262import")
    public R saveInbound262import(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
    	
		return wmsInternalBoundRemote.saveInbound262import(params);
	}
	
	
	@RequestMapping("/saveMo262import")
    public R saveInboundMo262import(@RequestParam Map<String, Object> params) throws Exception {
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
		return wmsInternalBoundRemote.saveInboundMo262import(params);
	}


}
