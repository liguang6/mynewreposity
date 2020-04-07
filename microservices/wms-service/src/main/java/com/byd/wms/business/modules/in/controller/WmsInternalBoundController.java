package com.byd.wms.business.modules.in.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.service.WmsInternetboundService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年1月17日 下午3:19:12 
 * 类说明 
 */
@RestController
@RequestMapping("in/wmsinternalbound")
public class WmsInternalBoundController {
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private WmsSapRemote wmssapremote;
	@Autowired
	private WmsInternetboundService wmsinternetbound;
	@Autowired
    private WmsSapMaterialService wmsSapMaterialService;
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	@Autowired
	private WmsCMatStorageService matstorageservice;
	@Autowired
	private WmsSapPlantService sapplantservice;
	@Autowired
    private WmsCPlantService wmsCPlantService;
	
	@RequestMapping("/list")
	
    public R list(@RequestParam Map<String, Object> params){
    	String pzh=params.get("pz303").toString();
    	String year=params.get("pzyear").toString();
    	String werks=params.get("werks").toString();
    	String whNumber=params.get("whNumber").toString();
    	String rLgort=params.get("rLgort").toString();
    	String vendor=params.get("vendor")==null?"":params.get("vendor").toString();
    	String xqgzh=params.get("xqgzh").toString();
    	String prddt=params.get("prddt").toString();
    	String inbound_type=params.get("inbound_type").toString();
    	Map<String,Object> result=wmssapremote.getSapBapiGoodsmvtDetail(pzh, year);
    	
    	result.put("recWerks", werks);
    	result.put("whNumber", whNumber);
    	result.put("rLgort", rLgort);
    	result.put("VENDORCODE", vendor);//供应商编码
    	result.put("BEDNR", xqgzh);//需求跟踪号
    	result.put("prddt", prddt);//生产日期
    	result.put("inbound_type", inbound_type);
    	
    	List<Map<String,Object>> newResult=wmsinternetbound.trunedResult303(result);
    	List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
    	Map<String,Object> retMsg=new HashMap<String,Object>();
    	if(!newResult.isEmpty()){//返回的是否有数据
    		retMsg.put("retMsg", newResult.get(0).get("retMsg"));
    	}/*else{
    		retMsg.put("retMsg", "凭证号 "+pzh+" 已经进仓！");
    	}*/
    	
    	
    	
    	return R.ok().put("result", newResult).put("retMsg", retMsg);
    }
	
	@RequestMapping("/save")
    public R saveInbound(@RequestParam Map<String, Object> params) throws Exception {
    	//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
			 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
    	return R.ok().put("ininternetbountNo", ininternetbountNo);
    }
	@RequestMapping("/querymaterial")
	public R querymaterial(@RequestParam Map<String, Object> params){
		BigDecimal fullBoxQty_d=null;
		String werks=params.get("werks").toString();
    	String material=params.get("material").toString();
    	Map<String, Object> sapMaterialMap = new HashMap <String, Object>();
		sapMaterialMap.put("WERKS", werks);
		sapMaterialMap.put("MATNR", material);
		List<WmsSapMaterialEntity> sapMateriallist=wmsSapMaterialService.selectByMap(sapMaterialMap);
		
		String mads="";
		String unit="";
		if(sapMateriallist!=null&&sapMateriallist.size()>0){
			mads=sapMateriallist.get(0).getMaktx();
			unit=sapMateriallist.get(0).getMeins();
		}
		//查询物料信息
		List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(sapMaterialMap);
		// new 
		sapMaterialMap.put("PACKAGE_TYPE", "01");
		List<Map<String,Object>> matlilist_new=wmsInInboundDao.getWmsCMatPackageHead(sapMaterialMap);
		
		if(matlilist!=null&&matlilist.size()>0){
			Object fullBoxQty=matlilist.get(0).get("FULL_BOX_QTY");
			if(fullBoxQty!=null){
				fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
			}
		}
		
		if(fullBoxQty_d!=null){//满箱数量
			
		}else{
			fullBoxQty_d=BigDecimal.ZERO;
		}
		
		
		BigDecimal fullBoxQty_d_n=null;
		if(matlilist_new!=null&&matlilist_new.size()>0){
			Object fullBoxQty_n=matlilist_new.get(0).get("FULL_BOX_QTY");
			if(fullBoxQty_n!=null){
				fullBoxQty_d_n=new BigDecimal(fullBoxQty_n.toString());
			}
		}
		
		if(fullBoxQty_d_n!=null){//满箱数量_new
			
		}else{
			fullBoxQty_d_n=BigDecimal.ZERO;
		}
		
		String LT_WARE="";
		String CAR_TYPE="";
		String MOULD_NO="";
		String DIS_STATION="";
		if(matlilist!=null&&matlilist.size()>0){
		//物流器具
		 LT_WARE=matlilist.get(0).get("LT_WARE")==null?"":matlilist.get(0).get("LT_WARE").toString();
		
		//车型
		 CAR_TYPE=matlilist.get(0).get("CAR_TYPE")==null?"":matlilist.get(0).get("CAR_TYPE").toString();
		
		//模具编号 
		 MOULD_NO=matlilist.get(0).get("MOULD_NO")==null?"":matlilist.get(0).get("MOULD_NO").toString();
		
		//工位
		 DIS_STATION=matlilist.get(0).get("DIS_STATION")==null?"":matlilist.get(0).get("DIS_STATION").toString();
		}
		//
		return R.ok().put("mads", mads).put("unit", unit).put("fullBoxQty_d", fullBoxQty_d).put("LT_WARE", LT_WARE)
				.put("CAR_TYPE", CAR_TYPE).put("MOULD_NO", MOULD_NO).put("DIS_STATION", DIS_STATION).put("fullBoxQty_d_n", fullBoxQty_d_n);
	}
	/**获取满箱数量
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/queryfullbox")
	public R queryfullbox(@RequestParam Map<String, Object> params){
		BigDecimal fullBoxQty_d=null;
		Map<String,Object> mattem=new HashMap<String,Object>();
		mattem.put("WERKS", params.get("werks"));
		mattem.put("MATNR", params.get("material"));
		
		String entry_qty_str=params.get("entry_qty").toString();//进仓数量
		
		List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(mattem);
		mattem.put("PACKAGE_TYPE", "01");
		List<Map<String,Object>> matlilist_new=wmsInInboundDao.getWmsCMatPackageHead(mattem);
		
		if(matlilist!=null&&matlilist.size()>0){
			Object fullBoxQty=matlilist.get(0).get("FULL_BOX_QTY");
			if(fullBoxQty!=null){
				fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
			}
		}
		
		BigDecimal box_count=new BigDecimal(1);//件数
		
		BigDecimal entry_qty=BigDecimal.ZERO;
		if(!"".equals(entry_qty_str)){
			entry_qty=new BigDecimal(entry_qty_str);
		}
		if(fullBoxQty_d!=null){
			box_count=entry_qty.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
		}else{
			fullBoxQty_d=entry_qty;
		}
		
		String LT_WARE="";
		String CAR_TYPE="";
		String MOULD_NO="";
		String DIS_STATION="";
		if(matlilist!=null&&matlilist.size()>0){
		//物流器具
		 LT_WARE=matlilist.get(0).get("LT_WARE")==null?"":matlilist.get(0).get("LT_WARE").toString();
		
		//车型
		 CAR_TYPE=matlilist.get(0).get("CAR_TYPE")==null?"":matlilist.get(0).get("CAR_TYPE").toString();
		
		//模具编号 
		 MOULD_NO=matlilist.get(0).get("MOULD_NO")==null?"":matlilist.get(0).get("MOULD_NO").toString();
		
		//工位
		 DIS_STATION=matlilist.get(0).get("DIS_STATION")==null?"":matlilist.get(0).get("DIS_STATION").toString();
		}
		
		return R.ok().put("fullBoxQty_d", fullBoxQty_d).put("box_count", box_count).put("LT_WARE", LT_WARE)
				.put("CAR_TYPE", CAR_TYPE).put("MOULD_NO", MOULD_NO).put("DIS_STATION", DIS_STATION);
	}
	/**
	 * 获取储位
	 * @param params
	 * @return
	 */
	@RequestMapping("/querybincode")
	public R querybincode(@RequestParam Map<String, Object> params){
		Map<String,Object> bincodemap=new HashMap<String,Object>();
		bincodemap.put("WERKS", params.get("werks"));
		bincodemap.put("WH_NUMBER", params.get("whNumber"));
		bincodemap.put("MATNR", params.get("material"));
		
		String bin_code="";
		/*List<WmsCMatStorageEntity> matstoragelist=matstorageservice.selectByMap(bincodemap);
		if(matstoragelist!=null&&matstoragelist.size()>0){
			bin_code ="";//储位
		}else{
			bin_code= "AAAA";
		}*/
		//储位目前都在创建进仓单的 保存动作去获取 20190429 add
		return R.ok().put("bin_code", bin_code);
	}
	/**
	 * 获取仓管员
	 * @param params
	 * @return
	 */
	@RequestMapping("/querywhmanager")
	public R querywhmanager(@RequestParam Map<String, Object> params){
		Map<String,Object> whmap=new HashMap<String,Object>();
		whmap.put("WERKS", params.get("werks").toString());
		whmap.put("WHNUMBER", params.get("whNumber").toString());
		whmap.put("MATNR", params.get("material"));
		whmap.put("LGORT", params.get("lgort"));
		
		String wh_manager="";
		/*List<Map<String,Object>> whlist=wmsInInboundDao.getWhManager(whmap);
		if(whlist!=null&&whlist.size()>0){
			wh_manager=whlist.get(0).get("WH_MANAGER").toString();
		}*/
		
		Map<String,Object> retWhManger=wmsinternetbound.getwhManager_n(whmap);
		if(retWhManger!=null){
			wh_manager=retWhManger.get("WH_MANAGER")==null?"":retWhManger.get("WH_MANAGER").toString();
		}
		
		return R.ok().put("wh_manager", wh_manager);
	}
	
	@RequestMapping("/save501")
    public R saveInbound501(@RequestBody Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	@RequestMapping("/save903")
    public R saveInbound903(@RequestParam Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	@RequestMapping("/save511")
    public R saveInbound511(@RequestBody Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	@RequestMapping("/preview511")
	public R previewExcel(@RequestParam Map<String, Object> params,MultipartFile excel) throws IOException{
		String werks=params.get("werks_511_upload").toString();
		String whnumber=params.get("whNumber_511_upload").toString();
		String vendor=params.get("vendor_511_upload").toString();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String, Object>> entityList = new ArrayList<Map<String, Object>>();
		int index=0;
		if(!CollectionUtils.isEmpty(sheet)){
			
			for(String[] row:sheet){
				String msg="";
				Map<String, Object> entity = new HashMap<String, Object>();
				if(0<row.length){
				if(!"".equals(row[0])){//物料号
					entity.put("MATERIAL", row[0].trim());
					
					//查询物料描述 单位
					Map<String, Object> sapMaterialMap = new HashMap <String, Object>();
					sapMaterialMap.put("WERKS", werks);
					sapMaterialMap.put("MATNR", row[0].trim());
					List<WmsSapMaterialEntity> sapMateriallist=wmsSapMaterialService.selectByMap(sapMaterialMap);
					
					String mads="";
					String unit="";
					if(sapMateriallist!=null&&sapMateriallist.size()>0){
						mads=sapMateriallist.get(0).getMaktx();
						unit=sapMateriallist.get(0).getMeins();
					}
					entity.put("MATERIALDESC", mads);
					entity.put("UNIT", unit);
					
					//查询储位
					Map<String,Object> bincodemap=new HashMap<String,Object>();
					bincodemap.put("WERKS", werks);
					bincodemap.put("WH_NUMBER", whnumber);
					bincodemap.put("MATNR", row[0].trim());
					
					String bin_code="";
					List<WmsCMatStorageEntity> matstoragelist=matstorageservice.selectByMap(bincodemap);
					if(matstoragelist!=null&&matstoragelist.size()>0){
						//bin_code =matstoragelist.get(0).getBinCode();//储位
						bin_code="";
					}else{
						bin_code= "AAAA";
					}
					entity.put("BIN_CODE", bin_code);
					//查询仓管员
					Map<String,Object> whmap=new HashMap<String,Object>();
					whmap.put("WERKS", werks);
					whmap.put("WHNUMBER", whnumber);
					whmap.put("MATNR", row[0].trim());
					
					String wh_manager="";
					List<Map<String,Object>> whlist=wmsInInboundDao.getWhManager(whmap);
					if(whlist!=null&&whlist.size()>0){
						wh_manager=whlist.get(0).get("WH_MANAGER").toString();
					}
					entity.put("WH_MANAGER", wh_manager);
				}
				}
				if(1<row.length){//进仓数量  
				if(!"".equals(row[1])){
					entity.put("IN_QTY", row[1]);
					//获取满箱数量 件数

					BigDecimal fullBoxQty_d=null;
					Map<String,Object> mattem=new HashMap<String,Object>();
					mattem.put("WERKS", werks);
					mattem.put("MATNR", row[0].trim());
					
					String entry_qty_str=row[1].trim();//进仓数量
					
					List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(mattem);
					if(matlilist!=null&&matlilist.size()>0){
						Object fullBoxQty=matlilist.get(0).get("FULL_BOX_QTY");
						if(fullBoxQty!=null){
							fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
						}
					}
					
					BigDecimal box_count=new BigDecimal(1);//件数
					
					BigDecimal entry_qty=BigDecimal.ZERO;
					if(!"".equals(entry_qty_str)){
						entry_qty=new BigDecimal(entry_qty_str);
					}
					if(fullBoxQty_d!=null){
						box_count=entry_qty.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
					}else{
						fullBoxQty_d=entry_qty;
					}
					entity.put("FULL_BOX_QTY", fullBoxQty_d);
					entity.put("BOX_COUNT", box_count);
				
				}
				}
				if(2<row.length){//需求跟踪号
					entity.put("BEDNR", row[2]);
				}
				if(3<row.length){//备注
					entity.put("REMARK", row[3]);
				}
				
				entityList.add(entity);
				
			}
		}
		return R.ok().put("data", entityList);
	}
	
	@RequestMapping("/listMo101")
	
    public R listMo101(@RequestBody Map<String, Object> params){
		List<Map<String,Object>> retResult=new ArrayList<Map<String,Object>>();
		try{
		   retResult=wmsinternetbound.queryList101_531(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		return R.ok().put("result", retResult);
	}
	
	@RequestMapping("/listMo531")
	
    public R listMo531(@RequestBody Map<String, Object> params){
		List<Map<String,Object>> retResult=new ArrayList<Map<String,Object>>();
		try{
		   retResult=wmsinternetbound.queryList101_531(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		return R.ok().put("result", retResult);
	}
	
	@RequestMapping("/save521")
    public R saveInbound521(@RequestParam Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//验证订单是否存在以及状态正确
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int m=0;m<jarr.size();m++){
			if(jarr.getJSONObject(m).getString("IO_NO")!=null||!"".equals(jarr.getJSONObject(m).getString("IO_NO"))){
				Map<String, Object> condtmp=new HashMap<String, Object>();
				condtmp.put("CO_NO", jarr.getJSONObject(m).getString("IO_NO"));
				condtmp.put("WERKS", jarr.getJSONObject(m).getString("RECWERKS"));
				
				Map<String, Object> retMap=new HashMap<String, Object>();
				try{
					 retMap=wmsinternetbound.queryListCO(condtmp);
					 if(retMap.get("CODE")!=null&&"-1".equals(retMap.get("CODE"))){
						 return R.error("该订单号不存在！");
					 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return R.error(e.getMessage()+"系统异常，请联系管理员！");
				}
				if(retMap.get("LOEKZ")!=null&&"X".equals(retMap.get("LOEKZ").toString())){//判断删除标识
					if(retMap.get("PHAS1")!=null&&!"X".equals(retMap.get("PHAS1").toString())){//判断状态
						return R.error(params.get("CO_NO").toString()+"订单不允许货物移动！");
					}
				}
				if(retMap.get("WERKS")!=null){
					if(!(retMap.get("WERKS").toString()).equals(condtmp.get("WERKS").toString())){//查询出来的工厂和页面传的工厂不一致
						return R.error(condtmp.get("CO_NO").toString()+" 该订单号不属于"+condtmp.get("WERKS").toString()+"工厂，请确认接收工厂及订单号是否正确！");
					}
				}
			}else{
				return R.error("订单号不能为空！");
			}
		}
		//
		
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	@RequestMapping("/save202")
    public R saveInbound202(@RequestParam Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//验证订单是否存在以及状态正确
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int m=0;m<jarr.size();m++){
			if(jarr.getJSONObject(m).getString("COST_CENTER")!=null||!"".equals(jarr.getJSONObject(m).getString("COST_CENTER"))){
				
			}else{
				return R.error("成本中心号不能为空！");
			}
		}
		//
		
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	@RequestMapping("/save222")
    public R saveInbound222(@RequestParam Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//验证订单是否存在以及状态正确
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int m=0;m<jarr.size();m++){
			if(jarr.getJSONObject(m).getString("WBS")!=null||!"".equals(jarr.getJSONObject(m).getString("WBS"))){
				
			}else{
				return R.error("WBS元素号不能为空！");
			}
		}
		//
		
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	@RequestMapping("/save262")
    public R saveInbound262(@RequestParam Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//验证订单是否存在以及状态正确
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int m=0;m<jarr.size();m++){
			if(jarr.getJSONObject(m).getString("IO_NO")!=null||!"".equals(jarr.getJSONObject(m).getString("IO_NO"))){
				
			}else{
				return R.error("订单号不能为空！");
			}
		}
		//
		
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	/**
	 * 创建CO订单
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/saveCo101")
    public R saveInboundCo101(@RequestParam Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	
	/**
	 * 查询CO订单
	 * @param params
	 * @return
	 */
	@RequestMapping("/listCo101")
    public R listCo101(@RequestParam Map<String, Object> params){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
			 retMap=wmsinternetbound.queryListCO(params);
			 if(retMap.get("CODE")!=null&&"-1".equals(retMap.get("CODE"))){
				 return R.error("该订单号不存在！");
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		if(retMap.get("LOEKZ")!=null&&"X".equals(retMap.get("LOEKZ").toString())){//判断删除标识
			if(retMap.get("PHAS1")!=null&&!"X".equals(retMap.get("PHAS1").toString())){//判断状态
				return R.error(params.get("CO_NO").toString()+"订单不允许货物移动！");
			}
		}
		if(retMap.get("WERKS")!=null){
			if(!(retMap.get("WERKS").toString()).equals(params.get("WERKS").toString())){//查询出来的工厂和页面传的工厂不一致
				return R.error(params.get("CO_NO").toString()+" 该订单号不属于"+params.get("WERKS").toString()+"工厂，请确认接收工厂及订单号是否正确！");
			}
		}
		//获取描述
		//AUTYP-订单类别（2） 值为 01：内部订单 值为04：CO订单
		String co_desc=retMap.get("KTEXT")==null?"":retMap.get("KTEXT").toString();
		String dd_type=retMap.get("AUTYP")==null?"":retMap.get("AUTYP").toString();
		return R.ok().put("desc", co_desc).put("dd_type", dd_type);
	}
	
	/**
	 * 查询成本中心
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/listCostCenter202")
    public R listCostCenter202(@RequestParam Map<String, Object> params) throws ParseException{
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
			 retMap=wmsinternetbound.queryListCostCenter(params);
			 if(retMap.get("COSTCENTER")==null||"".equals(retMap.get("COSTCENTER"))){
				 return R.error("该订单号不存在！");
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
		if(retMap.get("COMP_CODE")!=null){//验证返回的公司代码
			Map<String, Object> sapPlantMap=new HashMap<String, Object>();
			sapPlantMap.put("WERKS", params.get("WERKS").toString());
			List<WmsSapPlant> sapplantlist=sapplantservice.selectByMap(sapPlantMap);//通过工厂找出 公司代码
			if(sapplantlist!=null&&sapplantlist.size()>0){
				if(!(retMap.get("COMP_CODE").toString()).equals(sapplantlist.get(0).getBukrs())){//
					return R.error(params.get("COST_CENTER").toString()+" 该订单号不属于"+params.get("WERKS").toString()+"工厂，请确认接收工厂及订单号是否正确！");
				}
			}
		}
		//验证有效期
		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
		String nowtimeString =sdf.format(new Date());
		if(retMap.get("VALID_FROM")!=null&&retMap.get("VALID_TO")!=null){
			if(!"".equals(retMap.get("VALID_FROM"))&&!"".equals(retMap.get("VALID_TO"))){
				Date validFrom=sdf.parse(retMap.get("VALID_FROM").toString());
				Date validTo=sdf.parse(retMap.get("VALID_TO").toString());
				Date nowtimeTimeDate = sdf.parse(nowtimeString);
				if(nowtimeTimeDate.after(validTo)||validTo.before(validFrom)){
					return R.error(params.get("COST_CENTER").toString()+" 不在有效期之内！");
				}
			}
		}
		
		
		//获取描述
		String costcenter_desc=retMap.get("DESCRIPT")==null?"":retMap.get("DESCRIPT").toString();
		return R.ok().put("desc", costcenter_desc);
	}
	
	/**
	 * 查询WBS元素号
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/listWBS222")
    public R listWBS222(@RequestParam Map<String, Object> params){
		Map<String, Object> retMap=new HashMap<String, Object>();
		try{
			 retMap=wmsinternetbound.queryListWBS(params);
			 if(retMap.get("CODE")!=null&&"-1".equals(retMap.get("CODE"))){
				 return R.error("该订单号不存在！");
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		//验证公司代码
		if(retMap.get("COMP_CODE")!=null){//验证返回的公司代码
			Map<String, Object> sapPlantMap=new HashMap<String, Object>();
			sapPlantMap.put("WERKS", params.get("WERKS").toString());
			List<WmsSapPlant> sapplantlist=sapplantservice.selectByMap(sapPlantMap);//通过工厂找出 公司代码
			if(sapplantlist!=null&&sapplantlist.size()>0){
				if(!(retMap.get("COMP_CODE").toString()).equals(sapplantlist.get(0).getBukrs())){//
					return R.error(params.get("WBS").toString()+" 该元素号不属于"+params.get("WERKS").toString()+"工厂，请确认接收工厂及订单号是否正确！");
				}
			}
		}
		
		//获取描述
		String wbs_desc=retMap.get("DESCRIPTION")==null?"":retMap.get("DESCRIPTION").toString();
		return R.ok().put("desc", wbs_desc);
	}
	
	/**
	 * 查询研发订单号
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/listyf262")
    public R listyf262(@RequestParam Map<String, Object> params){
		Map<String, Object> retMap=new HashMap<String, Object>();
		/*try{
			 retMap=wmsinternetbound.queryListYf(params);
			 if(retMap.get("CODE")!=null&&"KO104".equals(retMap.get("CODE"))){
				 return R.error("该订单号不存在！");
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		//验证公司代码
		if(retMap.get("PLANT")!=null){
				if(!(retMap.get("PLANT").toString()).equals(params.get("WERKS").toString())){//
					return R.error(params.get("IO_NO").toString()+" 该订单号不属于"+params.get("WERKS").toString()+"工厂，请确认接收工厂及订单号是否正确！");
				}
			
		}
		
		//获取描述
		String yf_desc=retMap.get("ORDER_NAME")==null?"":retMap.get("ORDER_NAME").toString();
		return R.ok().put("desc", yf_desc);*/
		
		try{
			 retMap=wmsinternetbound.queryListCO(params);
			 if(retMap.get("CODE")!=null&&"-1".equals(retMap.get("CODE"))){
				 //return R.error("该订单号不存在！");
				 //如果不存在 ，继续查找本地sap订单同步表 Wms_Sap_Mo_Head
				 Map<String, Object> tempmap=new HashMap<String, Object>();
					tempmap.put("AUFNR", params.get("CO_NO"));
					tempmap.put("WERKS", params.get("WERKS"));
				List<Map<String,Object>> mohead=wmsInInboundDao.getMOHEADInfo(tempmap);
				if(mohead!=null&&mohead.size()>0){
					String status=mohead.get(0).get("ISTAT_TXT").toString();
					if(!status.contains("REL")||status.contains("DLFL") ||status.contains("LKD")){
						throw new RuntimeException("订单号 "+params.get("CO_NO")+" 没有释放，不能做进仓！");
					}else{//存在 的，就取WMS_SAP_MO_ITEM表对应的第一条 主物料的描述
						List<Map<String,Object>> maktxRetlist=wmsInInboundDao.getMOITEMMaktx(tempmap);
						if(maktxRetlist!=null&&maktxRetlist.size()>0){
							return R.ok().put("desc", maktxRetlist.get(0).get("MAKTX")).put("dd_type", "01");
						}else{
							throw new RuntimeException("订单号 "+params.get("CO_NO")+" 不存在，请检查是否输入有误，如果确认无误！");
						}
						
					}
				}else{
					throw new RuntimeException("订单号 "+params.get("CO_NO")+" 不存在，请检查是否输入有误，如果确认无误！");
				}
			 }else{
				 if(retMap.get("LOEKZ")!=null&&"X".equals(retMap.get("LOEKZ").toString())){//判断删除标识
						if(retMap.get("PHAS1")!=null&&!"X".equals(retMap.get("PHAS1").toString())){//判断状态
							return R.error(params.get("CO_NO").toString()+"订单不允许货物移动！");
						}
					}
					if(retMap.get("WERKS")!=null){
						if(!(retMap.get("WERKS").toString()).equals(params.get("WERKS").toString())){//查询出来的工厂和页面传的工厂不一致
							return R.error(params.get("CO_NO").toString()+" 该订单号不属于"+params.get("WERKS").toString()+"工厂，请确认接收工厂及订单号是否正确！");
						}
					}
					//获取描述
					//AUTYP-订单类别（2） 值为 01：内部订单 值为04：CO订单
					String co_desc=retMap.get("KTEXT")==null?"":retMap.get("KTEXT").toString();
					String dd_type=retMap.get("AUTYP")==null?"":retMap.get("AUTYP").toString();
					return R.ok().put("desc", co_desc).put("dd_type", dd_type);
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/listA101")
    public R listA101(@RequestParam Map<String, Object> params){
		//判断核销
		
		Map<String, Object> hxparams=new HashMap<String, Object>();
		hxparams.put("werks", params.get("WERKS"));
		hxparams.put("del_flag", "0");
		
		List<WmsCPlant> cplantList=wmsCPlantService.selectByMap(hxparams);
		String hx_flag="0";
		if(cplantList.size()>0){
			 hx_flag=cplantList.get(0).getHxFlag();
		}
		if("0".equals(hx_flag)){//启用核销
			return R.error("工厂"+params.get("WERKS")+"没有该业务！");
		}
		
		List<Map<String,Object>> listA101=wmsinternetbound.queryListA101(params);
		return R.ok().put("result", listA101);
	}
	
	@RequestMapping("/listA531")
    public R listA531(@RequestParam Map<String, Object> params){
		//判断核销
		
		Map<String, Object> hxparams=new HashMap<String, Object>();
		hxparams.put("werks", params.get("WERKS"));
		hxparams.put("del_flag", "0");
		
		List<WmsCPlant> cplantList=wmsCPlantService.selectByMap(hxparams);
		String hx_flag="0";
		if(cplantList.size()>0){
			 hx_flag=cplantList.get(0).getHxFlag();
		}
		if("0".equals(hx_flag)){//启用核销
			return R.error("工厂"+params.get("WERKS")+"没有该业务！");
		}
		
		List<Map<String,Object>> listA531=wmsinternetbound.queryListA531(params);
		return R.ok().put("result", listA531);
	}
	
	@RequestMapping("/listpo101")
    public R listpo101(@RequestBody Map<String, Object> params){
		try{
			
		List<Map<String,Object>> listpo=wmsinternetbound.queryListPO101(params);
		return R.ok().put("result", listpo);
		
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
	}
	
	@RequestMapping("/listMo262")
    public R listMo262(@RequestBody Map<String, Object> params){
		try{
			
		wmsinternetbound.validMo262(params);
		return R.ok().put("result", "");
		
		} catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
	}
	
	@RequestMapping("/saveMo262")
    public R saveInboundMo262(@RequestParam Map<String, Object> params) throws Exception {
		String arrlist=params.get("ARRLIST").toString();
		String USERNAME=params.get("USERNAME").toString();
		String newarrlist=arrlist.replace("\"", "");//由于多出了引号，所以要替换掉
		params.clear();
		params.put("ARRLIST", newarrlist);
		params.put("USERNAME", USERNAME);
		
		
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	
	private Map<String, Object> getInternetbountNo(Map<String, Object> params)  {
		// 获取前台传输的待进仓行并转化成json数据  ARRLIST
    	JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
    	//进仓单行项目清单
    	List<Map<String, Object>> itemlist=new ArrayList<Map<String, Object>>();
		for(int i=0;i<jarr.size();i++){
    		Map<String, Object> tempItemmap=new HashMap();
			tempItemmap.put("DEL", "0");
			tempItemmap.put("WERKS", jarr.getJSONObject(i).getString("RECWERKS"));
			tempItemmap.put("WH_NUMBER", jarr.getJSONObject(i).getString("WHNUMBER"));
			tempItemmap.put("LGORT", jarr.getJSONObject(i).getString("RLGORT"));
			tempItemmap.put("MATNR", jarr.getJSONObject(i).getString("MATERIAL"));
			tempItemmap.put("MAKTX", jarr.getJSONObject(i).getString("MATERIALDESC"));
			tempItemmap.put("BIN_CODE", jarr.getJSONObject(i).getString("BIN_CODE")==null?"":jarr.getJSONObject(i).getString("BIN_CODE"));
			tempItemmap.put("UNIT", jarr.getJSONObject(i).getString("UNIT")==null?"":jarr.getJSONObject(i).getString("UNIT"));
			tempItemmap.put("IN_QTY", jarr.getJSONObject(i).getString("IN_QTY")==null?"":jarr.getJSONObject(i).getString("IN_QTY"));
			tempItemmap.put("FULL_BOX_QTY", jarr.getJSONObject(i).getString("FULL_BOX_QTY")==null?"":jarr.getJSONObject(i).getString("FULL_BOX_QTY"));
			tempItemmap.put("BOX_COUNT", jarr.getJSONObject(i).getString("BOX_COUNT")==null?"":jarr.getJSONObject(i).getString("BOX_COUNT"));
			tempItemmap.put("WH_MANAGER", jarr.getJSONObject(i).getString("WH_MANAGER")==null?"":jarr.getJSONObject(i).getString("WH_MANAGER"));
			tempItemmap.put("LIFNR", jarr.getJSONObject(i).getString("LIFNR")==null?"":jarr.getJSONObject(i).getString("LIFNR"));
			tempItemmap.put("LIKTX", jarr.getJSONObject(i).getString("LIKTX")==null?"":jarr.getJSONObject(i).getString("LIKTX"));
			tempItemmap.put("PRODUCT_DATE", jarr.getJSONObject(i).getString("PRDDT")==null?"":jarr.getJSONObject(i).getString("PRDDT"));
			
			tempItemmap.put("SAP_MATDOC_NO", jarr.getJSONObject(i).getString("MAT_DOC")==null?"":jarr.getJSONObject(i).getString("MAT_DOC"));
			tempItemmap.put("SAP_MATDOC_ITEM_NO", jarr.getJSONObject(i).getString("MATDOC_ITM")==null?"":jarr.getJSONObject(i).getString("MATDOC_ITM"));
			
			tempItemmap.put("CREATOR", params.get("USERNAME").toString());
			tempItemmap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    				
			tempItemmap.put("BEDNR", jarr.getJSONObject(i).getString("BEDNR")==null?"":jarr.getJSONObject(i).getString("BEDNR"));
			tempItemmap.put("MEMO", jarr.getJSONObject(i).getString("REMARK")==null?"":jarr.getJSONObject(i).getString("REMARK"));
			tempItemmap.put("CUSTOMER_MATNR", jarr.getJSONObject(i).getString("CUSTOMER_MACD")==null?"":jarr.getJSONObject(i).getString("CUSTOMER_MACD"));
			
			tempItemmap.put("MO_NO", jarr.getJSONObject(i).getString("MO_NO")==null?"":jarr.getJSONObject(i).getString("MO_NO"));
			tempItemmap.put("MO_ITEM_NO", jarr.getJSONObject(i).getString("MO_ITEM_NO")==null?"":jarr.getJSONObject(i).getString("MO_ITEM_NO"));
			//内部/生产订单号
			tempItemmap.put("IO_NO", jarr.getJSONObject(i).getString("IO_NO")==null?"":jarr.getJSONObject(i).getString("IO_NO"));
			//成本中心号
			tempItemmap.put("COST_CENTER", jarr.getJSONObject(i).getString("COST_CENTER")==null?"":jarr.getJSONObject(i).getString("COST_CENTER"));
			//WBS元素号
			tempItemmap.put("WBS", jarr.getJSONObject(i).getString("WBS")==null?"":jarr.getJSONObject(i).getString("WBS"));
			
			//车间
			tempItemmap.put("WORKSHOP", jarr.getJSONObject(i).getString("WORKSHOP")==null?"":jarr.getJSONObject(i).getString("WORKSHOP"));
			//班次
			tempItemmap.put("WORKGROUP_NO", jarr.getJSONObject(i).getString("WORKGROUP_NO")==null?"":jarr.getJSONObject(i).getString("WORKGROUP_NO"));
			//车型
			tempItemmap.put("CAR_TYPE", jarr.getJSONObject(i).getString("CAR_TYPE")==null?"":jarr.getJSONObject(i).getString("CAR_TYPE"));
			//模具编号
			tempItemmap.put("MOULD_NO", jarr.getJSONObject(i).getString("MOULD_NO")==null?"":jarr.getJSONObject(i).getString("MOULD_NO"));
			
			//作业员
			tempItemmap.put("OPERATOR", jarr.getJSONObject(i).getString("ZYY")==null?"":jarr.getJSONObject(i).getString("ZYY"));
			//物流器具
			tempItemmap.put("LT_WARE", jarr.getJSONObject(i).getString("LT_WARE")==null?"":jarr.getJSONObject(i).getString("LT_WARE"));
			//产品图号
			tempItemmap.put("DRAWING_NO", jarr.getJSONObject(i).getString("CPTH")==null?"":jarr.getJSONObject(i).getString("CPTH"));
			//工位
			tempItemmap.put("PRO_STATION", jarr.getJSONObject(i).getString("DIS_STATION")==null?"":jarr.getJSONObject(i).getString("DIS_STATION"));
			tempItemmap.put("PO_NO", jarr.getJSONObject(i).getString("PO_NO")==null?"":jarr.getJSONObject(i).getString("PO_NO"));
			tempItemmap.put("PO_ITEM_NO", jarr.getJSONObject(i).getString("PO_ITEM_NO")==null?"":jarr.getJSONObject(i).getString("PO_ITEM_NO"));
			
			tempItemmap.put("SO_NO", jarr.getJSONObject(i).getString("KDAUF")==null?"":jarr.getJSONObject(i).getString("KDAUF"));
			tempItemmap.put("SO_ITEM_NO", jarr.getJSONObject(i).getString("KDPOS")==null?"":jarr.getJSONObject(i).getString("KDPOS"));
			tempItemmap.put("RN", jarr.getJSONObject(i).getString("RN")==null?"":jarr.getJSONObject(i).getString("RN"));
			
			tempItemmap.put("DANGER_FLAG", "0");
			tempItemmap.put("RECEIPT_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
			
			//
			Map<String, Object> businessmap = new HashMap<>();
			businessmap.put("BUSINESS_NAME", jarr.getJSONObject(0).getString("INBOUND_TYPE"));
			String SOBKZ=jarr.getJSONObject(0).getString("SOBKZ")==null?"Z":jarr.getJSONObject(0).getString("SOBKZ").toString();//库存类型
			businessmap.put("SOBKZ", SOBKZ);
			List<Map<String, Object>> businesslist=wmsInInboundDao.getBusinessInfo(businessmap);
			
			if(businesslist.size()>0){
				
			  tempItemmap.put("BUSINESS_NAME", businesslist.get(0).get("BUSINESS_NAME").toString());
				
			}
			itemlist.add(tempItemmap);
			
		}
		

		//产生批次
		
		List<Map<String,Object>> retmapList = wmsMatBatchService.getBatch(itemlist);
		for(Map<String,Object> retmap:retmapList){
		if(retmap.get("MSG")!=null&&!"success".equals(retmap.get("MSG"))) {
			throw new RuntimeException((String) retmap.get("MSG"));
		}
		}
		
		params.put("retmapList", retmapList);
		params.put("jarr", jarr);
		return params;
		
	}
	
	@RequestMapping("/save262import")
    public R saveInbound262import(@RequestParam Map<String, Object> params) throws Exception {
		
		//验证订单是否存在以及状态正确
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int m=0;m<jarr.size();m++){
			if(jarr.getJSONObject(m).getString("IO_NO")!=null||!"".equals(jarr.getJSONObject(m).getString("IO_NO"))){
				Map cond=new HashMap();
				cond.put("CO_NO", jarr.getJSONObject(m).getString("IO_NO"));
				cond.put("WERKS", jarr.getJSONObject(m).getString("RECWERKS"));
				//验证订单
				try{
					Map<String, Object> retMap=new HashMap<String, Object>();
					 retMap=wmsinternetbound.queryListCO(cond);
					 if(retMap.get("CODE")!=null&&"-1".equals(retMap.get("CODE"))){
						 //return R.error("该订单号不存在！");
						 //如果不存在 ，继续查找本地sap订单同步表 Wms_Sap_Mo_Head
						 Map<String, Object> tempmap=new HashMap<String, Object>();
							tempmap.put("AUFNR", cond.get("CO_NO"));
							tempmap.put("WERKS", cond.get("WERKS"));
						List<Map<String,Object>> mohead=wmsInInboundDao.getMOHEADInfo(tempmap);
						if(mohead!=null&&mohead.size()>0){
							String status=mohead.get(0).get("ISTAT_TXT").toString();
							if(!status.contains("REL")||status.contains("DLFL") ||status.contains("LKD")){
								throw new RuntimeException("订单号 "+cond.get("CO_NO")+" 没有释放，不能做进仓！");
							}else{//存在 的，就取WMS_SAP_MO_ITEM表对应的第一条 主物料的描述
								List<Map<String,Object>> maktxRetlist=wmsInInboundDao.getMOITEMMaktx(tempmap);
								if(maktxRetlist!=null&&maktxRetlist.size()>0){
									return R.ok().put("desc", maktxRetlist.get(0).get("MAKTX")).put("dd_type", "01");
								}else{
									throw new RuntimeException("订单号 "+cond.get("CO_NO")+" 不存在，请检查是否输入有误，如果确认无误！");
								}
								
							}
						}else{
							throw new RuntimeException("订单号 "+cond.get("CO_NO")+" 不存在，请检查是否输入有误，如果确认无误！");
						}
					 }else{
						 if(retMap.get("LOEKZ")!=null&&"X".equals(retMap.get("LOEKZ").toString())){//判断删除标识
								if(retMap.get("PHAS1")!=null&&!"X".equals(retMap.get("PHAS1").toString())){//判断状态
									return R.error(cond.get("CO_NO").toString()+"订单不允许货物移动！");
								}
							}
							if(retMap.get("WERKS")!=null){
								if(!(retMap.get("WERKS").toString()).equals(cond.get("WERKS").toString())){//查询出来的工厂和页面传的工厂不一致
									return R.error(cond.get("CO_NO").toString()+" 该订单号不属于"+cond.get("WERKS").toString()+"工厂，请确认接收工厂及订单号是否正确！");
								}
							}
							//获取描述
							//AUTYP-订单类别（2） 值为 01：内部订单 值为04：CO订单
							String co_desc=retMap.get("KTEXT")==null?"":retMap.get("KTEXT").toString();
							String dd_type=retMap.get("AUTYP")==null?"":retMap.get("AUTYP").toString();
							
							if(!"01".equals(dd_type)){
								return R.error(cond.get("CO_NO").toString()+" 该订单不是研发/内部订单！");
							}
							
					 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return R.error(e.getMessage()+"系统异常，请联系管理员！");
				}
				//
			}else{
				return R.error("订单号不能为空！");
			}
		}
		//
		
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
	
	
	@RequestMapping("/saveMo262import")
    public R saveInboundMo262import(@RequestParam Map<String, Object> params) throws Exception {
		
		//inboundType
		String ininternetbountNo="";
		try{
			this.getInternetbountNo(params);
		 ininternetbountNo=wmsinternetbound.saveInInternetbound(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
		
	return R.ok().put("ininternetbountNo", ininternetbountNo);
	}
}
