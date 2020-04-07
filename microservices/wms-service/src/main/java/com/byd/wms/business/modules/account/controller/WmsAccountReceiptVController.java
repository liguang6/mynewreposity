package com.byd.wms.business.modules.account.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.wms.business.modules.account.entity.WmsHxPoEntity;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxDnService;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxMoService;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxPoService;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxToService;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/**
 * 账务处理-收货V（核销业务虚收）
 * 包含：SAP采购订单收料(V)、工厂间调拨收料（V）、SAP交货单收料（V)、成品入库（V）
 * @author (changsha) thw
 * @date 2018-09-11
 */
@RestController
@RequestMapping("account/wmsAccountReceiptV")
public class WmsAccountReceiptVController {
    @Autowired
    private WmsAccountReceiptHxPoService wmsAccountReceiptHxPoService;
    @Autowired
    private WmsAccountReceiptHxToService wmsAccountReceiptHxToService;   
    @Autowired
    private WmsAccountReceiptHxDnService wmsAccountReceiptHxDnService;  
    @Autowired
    private WmsAccountReceiptHxMoService wmsAccountReceiptHxMoService;  
    @Autowired
	WmsSapRemote wmsSapRemote;
    @Autowired
    WmsCTxtService wmsCTxtService;
    @Autowired
    CommonService commonService;
    @Autowired
    WmsCPlantService wmsCPlantService;
    @Autowired
    private UserUtils userUtils;

    /**
     * SAP采购订单核销信息-保存
     */
    @RequestMapping("/povSave")
    public R povSave(@RequestBody Map<String,Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("creator", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
	   	
    	WmsHxPoEntity wmsAccountReceiptPoVEntity = new WmsHxPoEntity();
    	
    	String werks = params.get("werks")==null?"":params.get("werks").toString();
    	String whNumber = params.get("whNumber")==null?"":params.get("whNumber").toString();
    	String lifnr = params.get("lifnr")==null?"":params.get("lifnr").toString();
    	String ebeln = params.get("ebeln")==null?"":params.get("ebeln").toString();
    	String ebelp = params.get("ebelp")==null?"":params.get("ebelp").toString();
    	String lgort = params.get("lgort")==null?"":params.get("lgort").toString();
    	String matnr = params.get("matnr")==null?"":params.get("matnr").toString();
    	String maktx = params.get("maktx")==null?"":params.get("maktx").toString();
    	String unit = params.get("unit")==null?"":params.get("unit").toString();
    	Double menge = params.get("menge")==null?null:Double.parseDouble(params.get("menge").toString());
    	Double xs101 = params.get("xs101")==null?null:Double.parseDouble(params.get("xs101").toString());
    	Double xs102 = params.get("xs102")==null?null:Double.parseDouble(params.get("xs102").toString());
    	Double ss103 = params.get("ss103")==null?null:Double.parseDouble(params.get("ss103").toString());
    	Double ss104 = params.get("ss104")==null?null:Double.parseDouble(params.get("ss104").toString());
    	Double ss124 = params.get("ss124")==null?null:Double.parseDouble(params.get("ss124").toString());
    	Double ss125 = params.get("ss125")==null?null:Double.parseDouble(params.get("ss125").toString());
    	Double ss105 = params.get("ss105")==null?null:Double.parseDouble(params.get("ss105").toString());
    	Double ss106 = params.get("ss106")==null?null:Double.parseDouble(params.get("ss106").toString());
    	Double hxQty = params.get("hxQty")==null?null:Double.parseDouble(params.get("hxQty").toString());
    	String cancelFlag = params.get("cancelFlag")==null?"":params.get("cancelFlag").toString();
    	String creator = params.get("creator")==null?"":params.get("creator").toString();
    	String createDate = params.get("createDate")==null?"":params.get("createDate").toString();
    	
    	wmsAccountReceiptPoVEntity.setWerks(werks);
    	wmsAccountReceiptPoVEntity.setWhNumber(whNumber);
    	wmsAccountReceiptPoVEntity.setLifnr(lifnr);
    	wmsAccountReceiptPoVEntity.setEbeln(ebeln);
    	wmsAccountReceiptPoVEntity.setEbelp(ebelp);
    	wmsAccountReceiptPoVEntity.setLgort(lgort);
    	wmsAccountReceiptPoVEntity.setMatnr(matnr);
    	wmsAccountReceiptPoVEntity.setMaktx(maktx);
    	wmsAccountReceiptPoVEntity.setUnit(unit);
    	wmsAccountReceiptPoVEntity.setMenge(menge);
    	wmsAccountReceiptPoVEntity.setXs101(xs101);
    	wmsAccountReceiptPoVEntity.setXs102(xs102);
    	wmsAccountReceiptPoVEntity.setSs103(ss103);
    	wmsAccountReceiptPoVEntity.setSs104(ss104);
    	wmsAccountReceiptPoVEntity.setSs124(ss124);
    	wmsAccountReceiptPoVEntity.setSs125(ss125);
    	wmsAccountReceiptPoVEntity.setSs105(ss105);
    	wmsAccountReceiptPoVEntity.setSs106(ss106);
    	wmsAccountReceiptPoVEntity.setHxQty(hxQty);
    	wmsAccountReceiptPoVEntity.setCancelFlag(cancelFlag);
    	wmsAccountReceiptPoVEntity.setCreator(creator);
    	wmsAccountReceiptPoVEntity.setCreateDate(createDate);
    	
    	wmsAccountReceiptHxPoService.insert(wmsAccountReceiptPoVEntity);
        return R.ok();
    }

    /**
     * SAP采购订单核销信息-修改
     */
    @RequestMapping("/povUpdate")
    public R povUpdate(@RequestBody Map<String,Object> params){
    	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("editor", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    	WmsHxPoEntity wmsAccountReceiptPoVEntity = new WmsHxPoEntity();
    	String werks = params.get("werks")==null?"":params.get("werks").toString();
    	String whNumber = params.get("whNumber")==null?"":params.get("whNumber").toString();
    	String lifnr = params.get("lifnr")==null?"":params.get("lifnr").toString();
    	String ebeln = params.get("ebeln")==null?"":params.get("ebeln").toString();
    	String ebelp = params.get("ebelp")==null?"":params.get("ebelp").toString();
    	String lgort = params.get("lgort")==null?"":params.get("lgort").toString();
    	String matnr = params.get("matnr")==null?"":params.get("matnr").toString();
    	String maktx = params.get("maktx")==null?"":params.get("maktx").toString();
    	String unit = params.get("unit")==null?"":params.get("unit").toString();
    	Double menge = params.get("menge")==null?null:Double.parseDouble(params.get("menge").toString());
    	Double xs101 = params.get("xs101")==null?null:Double.parseDouble(params.get("xs101").toString());
    	Double xs102 = params.get("xs102")==null?null:Double.parseDouble(params.get("xs102").toString());
    	Double ss103 = params.get("ss103")==null?null:Double.parseDouble(params.get("ss103").toString());
    	Double ss104 = params.get("ss104")==null?null:Double.parseDouble(params.get("ss104").toString());
    	Double ss124 = params.get("ss124")==null?null:Double.parseDouble(params.get("ss124").toString());
    	Double ss125 = params.get("ss125")==null?null:Double.parseDouble(params.get("ss125").toString());
    	Double ss105 = params.get("ss105")==null?null:Double.parseDouble(params.get("ss105").toString());
    	Double ss106 = params.get("ss106")==null?null:Double.parseDouble(params.get("ss106").toString());
    	Double hxQty = params.get("hxQty")==null?null:Double.parseDouble(params.get("hxQty").toString());
    	String cancelFlag = params.get("cancelFlag")==null?"0":params.get("cancelFlag").toString();
    	String editor = params.get("editor")==null?"":params.get("editor").toString();
    	String editDate = params.get("editDate")==null?"":params.get("editDate").toString();
    	
    	wmsAccountReceiptPoVEntity.setId(Long.valueOf(params.get("id").toString()));
    	wmsAccountReceiptPoVEntity.setWerks(werks);
    	wmsAccountReceiptPoVEntity.setWhNumber(whNumber);
    	wmsAccountReceiptPoVEntity.setLifnr(lifnr);
    	wmsAccountReceiptPoVEntity.setEbeln(ebeln);
    	wmsAccountReceiptPoVEntity.setEbelp(ebelp);
    	wmsAccountReceiptPoVEntity.setLgort(lgort);
    	wmsAccountReceiptPoVEntity.setMatnr(matnr);
    	wmsAccountReceiptPoVEntity.setMaktx(maktx);
    	wmsAccountReceiptPoVEntity.setUnit(unit);
    	if(menge!=null) {
    		wmsAccountReceiptPoVEntity.setMenge(menge);
    	}
    	if(xs101!=null) {
    		wmsAccountReceiptPoVEntity.setXs101(xs101);
    	}
    	if(xs102!=null) {
    		wmsAccountReceiptPoVEntity.setXs102(xs102);
    	}
    	if(ss103!=null) {
    		wmsAccountReceiptPoVEntity.setSs103(ss103);
    	}
    	if(ss104!=null) {
    		wmsAccountReceiptPoVEntity.setSs104(ss104);
    	}
    	if(ss124!=null) {
    		wmsAccountReceiptPoVEntity.setSs124(ss124);
    	}
    	if(ss125!=null) {
    		wmsAccountReceiptPoVEntity.setSs125(ss125);
    	}
    	if(ss105!=null) {
    		wmsAccountReceiptPoVEntity.setSs105(ss105);
    	}
    	if(ss106!=null) {
    		wmsAccountReceiptPoVEntity.setSs106(ss106);
    	}
    	if(hxQty!=null) {
    		wmsAccountReceiptPoVEntity.setHxQty(hxQty);
    	}
    	wmsAccountReceiptPoVEntity.setCancelFlag(cancelFlag);
    	wmsAccountReceiptPoVEntity.setEditor(editor);
    	wmsAccountReceiptPoVEntity.setEditDate(editDate);
    	
        ValidatorUtils.validateEntity(wmsAccountReceiptPoVEntity);
        wmsAccountReceiptHxPoService.updateAllColumnById(wmsAccountReceiptPoVEntity);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/povDelete")
    public R povDelete(@RequestParam List<Double> qtys){
    	wmsAccountReceiptHxPoService.deleteBatchIds(qtys);

        return R.ok();
    }
    
    /**
     * 物料排序
     * @param datalist
     */
    private void sortMatList(List<Map<String,Object>> datalist) {
    	
    	Collections.sort(datalist, new Comparator<Map<String,Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int sortNo=0;
				if(Integer.parseInt(o1.get("sortNo").toString())>Integer.parseInt(o2.get("sortNo").toString())) {
					sortNo= 1;
				}
				if(Integer.parseInt(o1.get("sortNo").toString())<Integer.parseInt(o2.get("sortNo").toString())) {
					sortNo= -1;
				}
				return sortNo;
			}
   		
    	});
    }
    
    /**
     * PO采购订单收货V-核销：
     * 特殊业务：采购订单先过账，以后根据生产情况，再通知供应商送货。
     * 虚收货（V）：用采购订单在系统做收货，产生事务记录，凭证记录（XS101）（过账到SAP,101）
     * @param params
     * @return
     */
    @RequestMapping("/listPOVMat")
    public R listPOVMat(@RequestBody Map<String,Object> params) {
       	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
	   	
    	String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
    	String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
    	String PO_NO= params.get("PO_NO").toString();//采购订单号
    	R r=new R();
    	/**
    	 * 根据采购订单号，查询满足条件的行项目数据（检验了行项目工厂是否启用核销业务）
    	 */
    	List<Map<String,Object>> matList = wmsAccountReceiptHxPoService.getPoItems(params);
    	if(matList==null||matList.size()==0) {
    		return r.error("采购订单"+PO_NO+"不存在 ，请核对是否输入有误，如果无误，请使用采购订单同步功能，同步SAP数据并检查工厂是否已配置收货（V）业务！");
    	}
    	String matErrorStr = "";
    	String purUnitErrorStr = "";
    	for (Map<String, Object> map : matList) {
			/**
			 * 判断物料主数据是否在工厂存在
			 */
    		if(map.get("MEINS") ==null || "".equals(map.get("MEINS").toString())){
    			//物料主数据不存在
    			matErrorStr +="物料"+map.get("MATNR").toString()+"，";
    		}
		}
    	if(!"".equals(matErrorStr)) {
    		matErrorStr += "信息在工厂"+matList.get(0).get("WERKS").toString()+"不存在，请使用物料信息同步功能同步物料信息！";
    		return r.error(matErrorStr);
    	}
    	String WERKS=matList.get(0).get("WERKS").toString();
    	String BSART=matList.get(0).get("BSART")==null?"":matList.get(0).get("BSART").toString();//采购凭证类型 BSART 采购订单类型 如QH00 前海采购订单
    	String LIFNR=matList.get(0).get("LIFNR")==null?"":matList.get(0).get("LIFNR").toString();//供应商代码
    	
    	params.put("WERKS", WERKS); 
    	params.put("LIFNR", LIFNR);
       	/**
    	 * 判断交货单收货工厂是否启用核销业务，否则提示错误
    	 */
    	List<WmsCPlant> cPlantList = (List<WmsCPlant>)wmsCPlantService.queryPage(params).getList();
    	if(cPlantList==null || cPlantList.size()!=1 || !"X".equals(cPlantList.get(0).getHxFlag())) {
    		return R.error("收货工厂"+WERKS+"未启用核销业务，请检查工厂配置!");
    	}
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
    	Set<Map<String,Object>> deptList= userUtils.getUserWerks("ACCOUNT_RECEIPT_V");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(WERKS)) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return r.error("您无权操作"+WERKS+"工厂的采购订单！");
    	}   	
    	/**
    	 * 如果输入的单号是STO标准采购订单，报错提示：STO采购订单，请用SAP交货单收货 （通过采购订单抬头的采购凭证类型判断，如果是BYDS就报错
    	 */
    	if("BYDS".equals(BSART)) {
    		return r.error("STO采购订单，请用SAP交货单收货（V）！");
    	}
    	/**
    	 * 获取储位
    	 */
    	params.put("BIN_TYPE", "00");
    	params.put("BIN_STATUS", "01");
    	params.put("STORAGE_MODEL", "01");
    	List<Map<String, Object>> binList = commonService.getMatBinList(params);
    	if(binList == null || binList.size()<=0) {
    		return r.error("仓库号"+params.get("WH_NUMBER").toString()+"未配置储位类型为（虚拟储位）的储位！");
    	}
    	
    	/**
    	 * 获取采购和供应商简称（判断配置：是否启用供应商管理，如果没有启用，灰色不可填入，
    	 * 启用，读取供应商管理数据表，带出数据，可以编辑，如果没有数据，必须输入。）
    	 */
    	Map<String,Object> vendor = wmsAccountReceiptHxPoService.getVendorInfo(params);
    	
    	if(null == vendor || null == vendor.get("NAME1")) {
    		return R.error("凭证关联的供应商："+LIFNR+"信息未同步至WMS系统，请先同步供应商信息！");
    	}
    	
    	/**
    	 * 根据工厂代码、业务类型获取SAP采购订单("22")抬头文本与行文本
    	 */
    	Map<String,String>txtMap=new HashMap<String,String>();
    	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
    	txtMap.put("WERKS", WERKS);
    	txtMap.put("BUSINESS_NAME","22" );
    	txtMap.put("JZ_DATE", JZ_DATE);
    	String SHORT_NAME = vendor.get("SHORT_NAME")==null?"":(String)vendor.get("SHORT_NAME");
    	txtMap.put("LIKTX", SHORT_NAME);
    	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
    	txtMap.put("PO_NO", PO_NO);
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);

    	List<Map<String,Object>> dangerList=wmsAccountReceiptHxPoService.getDangerMatList(params);//危化品物料校验列表
    	for(Map<String,Object> _m:matList) {
    		_m.put("WH_NUMBER", params.get("WH_NUMBER"));
    		_m.put("sortNo", _m.get("PO_ITEM_NO"));
    		_m.put("PRODUCT_DATE", CUR_DATE);
    		_m.put("CREATE_DATE", CREATE_DATE);
    		_m.put("CREATOR", params.get("USERNAME").toString());
    		_m.put("DANGER_FLAG", "0");
    		_m.put("HEADER_TXT", txt.get("txtrule"));
    		_m.put("ITEM_TEXT", txt.get("txtruleitem"));
    		Double RECEIPT_QTY = Double.valueOf(_m.get("MAX_MENGE").toString())-Double.valueOf(_m.get("XSQTY").toString());
    		_m.put("RECEIPT_QTY", RECEIPT_QTY);
    		
    		for(Map<String,Object> m:dangerList) {
    			if(m.get("MATNR").equals(_m.get("MATNR"))) {
    				_m.put("DANGER_FLAG", "X");
    				_m.put("GOOD_DATES", m.get("GOOD_DATES"));
    				break;
    			}
    		}
    		if(vendor.get("SHORT_NAME") !=null) {
    			_m.put("SHORT_NAME", vendor.get("SHORT_NAME"));
    		}
    		if(vendor.get("VENDOR_MANAGER") !=null) {
    			_m.put("VENDOR_MANAGER", vendor.get("VENDOR_MANAGER"));
    		}
    		_m.put("VENDOR_FLAG", vendor.get("VENDOR_FLAG"));
    		
    		_m.put("BIN_CODE", binList.get(0).get("BIN_CODE"));
    		_m.put("BIN_NAME", binList.get(0).get("BIN_NAME"));
    	}
    	
    	/**
    	 * 排序，安照紧急物料排最前，然后按照采购订单行号排序
    	 */  	
    	this.sortMatList(matList);
    	
    	PageUtils page=new PageUtils(new Page().setRecords(matList));
    	
    	return R.ok().put("page", page);
    } 
    
    /**
     * 根据SAP交货单获取物料列表；
     * 通过调用SAP服务接口，读取SAP交货单数据，如果数据不存在,提示交货单不存在
     * 判断状态：如果交货单对应的状态是已收货，已关闭，已取消，不需要显示交货单数据，提示已收货（或者 已关闭，已取消）
     */
    @RequestMapping("/listDNVMat")
    public R listDNVMat(@RequestBody Map<String, Object> params){
       	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    	
    	String PO_NO= params.get("PO_NO").toString();//交货单号
    	/**
    	 * 通过调用SAP服务接口，读取SAP交货单数据
    	 */
    	Map<String,Object> dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(PO_NO);
    	if(null != dnMap.get("CODE") && "-1".equals(dnMap.get("CODE").toString())) {
    		//获取SAP交货单数据失败
    		return R.error("获取SAP交货单数据失败："+dnMap.get("MESSAGE").toString());
    	}
    	//交货单抬头信息
    	Map<String,Object> dnHeaderMap = (Map<String,Object>)dnMap.get("header");
    	
    	//交货单行项目信息
    	List<Map<String,Object>> dnItemList = (ArrayList<Map<String,Object>>)dnMap.get("item");
/*    	//交货单关联的凭证信息 docList
    	List<Map<String,Object>> dnDocList = (ArrayList<Map<String,Object>>)dnMap.get("docList");*/
    	if(dnHeaderMap==null) {
    		return R.error("SAP交货单:"+PO_NO+"不存在，请检查是否输入有误！");
    	}
    	/*
    	 * 判断交货单收货工厂是否启用核销业务，否则提示错误
    	 */
    	params.put("werks", dnHeaderMap.get("WERKS"));
    	List<WmsCPlant> cPlantList = (List<WmsCPlant>)wmsCPlantService.queryPage(params).getList();
    	if(cPlantList==null || cPlantList.size()!=1 || !"X".equals(cPlantList.get(0).getHxFlag())) {
    		return R.error("收货工厂"+dnHeaderMap.get("WERKS")+"未启用核销业务，请检查工厂配置!");
    	}
    	/*
    	 * 判断账号是否有对应交货单工厂的操作权限，如果没有提示无权限 WERKS
    	 */
    	Set<Map<String,Object>> deptList= userUtils.getUserWerks("ACCOUNT_RECEIPT_V");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(dnHeaderMap.get("WERKS"))) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return R.error("您无权操作"+dnHeaderMap.get("WERKS")+"工厂（收货）的单据!");
    	}   	
    	if(dnItemList==null||dnItemList.size()==0) {
    		return R.error("SAP交货单:"+PO_NO+"行项目不存在，请检查交货单是否输入有误！");
    	}
    	/*
    	 * 交货单是否已删除
    	 */
    	if(null != dnHeaderMap.get("SPE_LOEKZ") && "X".equals(dnHeaderMap.get("SPE_LOEKZ"))) {
    		//删除的交货单
    		return R.error("SAP交货单："+PO_NO+"已删除！");
    	}
     	/*
    	 * 交货单类型是否正确  VBTYP == J
    	 */
    	if(null == dnHeaderMap.get("VBTYP") || (!"J".equals(dnHeaderMap.get("VBTYP")) && !"T".equals(dnHeaderMap.get("VBTYP")) ) ) {
    		//凭证类别 VBTYP不正确
    		return R.error("SAP交货单："+PO_NO+"不是正确类型的交货单！");
    	}
    	if(null != dnHeaderMap.get("VBTYP") && "T".equals(dnHeaderMap.get("VBTYP"))) {
    		//凭证类别 VBTYP不正确 退货交货单
    		return R.error("SAP交货单："+PO_NO+"为退货交货单，不允许收货！");
    	}
    	/**
    	 * 判断SAP交货单状态是否可收货，状态必须为A
    	 */
    	if("B".equals(dnHeaderMap.get("WBSTK"))) {
    		//交货单状态必须为B WBSTK
    		return R.error("SAP交货单："+PO_NO+"已部分交货，不允许收货！");
    	}
    	if("C".equals(dnHeaderMap.get("WBSTK"))) {
    		//交货单状态必须为C WBSTK
    		return R.error("SAP交货单："+PO_NO+"已交货，不允许收货！");
    	}
    	
    	/**
    	 * 获取储位
    	 */
    	params.put("BIN_TYPE", "00");
    	params.put("BIN_STATUS", "01");
    	params.put("STORAGE_MODEL", "01");
    	List<Map<String, Object>> binList = commonService.getMatBinList(params);
    	if(binList == null || binList.size()<=0) {
    		return R.error("仓库号"+params.get("WH_NUMBER").toString()+"未配置储位类型为（虚拟储位）的储位！");
    	}
    	
    	params.put("WERKS", dnHeaderMap.get("WERKS")); //交货单收货工厂
    	params.put("LIFNR", "VBYD"+dnHeaderMap.get("VKORG"));//供应商代码为VBYD拼接交货单抬头的销售机构字段
    	/**
    	 * 根据供应商代码获取描述信息
    	 */
    	Map<String,Object> vendorMap = wmsAccountReceiptHxDnService.getSapVendorByNo("VBYD"+dnHeaderMap.get("VKORG"));
    	if(null == vendorMap) {
    		return R.error("交货单关联的供应商："+"VBYD"+dnHeaderMap.get("VKORG")+"信息未同步至WMS系统，请先同步供应商信息！");
    	}
    	/**
    	 * 查询是否存在危化品
    	 */
    	List<Map<String,Object>> dangerList = wmsAccountReceiptHxPoService.getDangerMatList(params);//危化品物料校验列表
    	
    	/**
    	 * 根据工厂代码、业务类型获取SAP交货单收料(V)("23")抬头文本与行文本
    	 */
    	Map<String,String>txtMap=new HashMap<String,String>();
    	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
    	txtMap.put("WERKS", dnHeaderMap.get("WERKS").toString());
    	txtMap.put("BUSINESS_NAME","23" );
    	txtMap.put("JZ_DATE", JZ_DATE);
    	txtMap.put("currentUser", params.get("FULL_NAME").toString());
    	txtMap.put("SAP_OUT_NO", dnHeaderMap.get("VBELN").toString());
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
    	
    	/**
    	 * 根据交货单行项目，查询关联的采购订单信息和SAP交货单 虚收101数量，并校验物料信息是否已同步到WMS系统，否则报错
    	 */
    	dnItemList = wmsAccountReceiptHxDnService.getPoItemListByDnItem(dnItemList,params);
    	String matErrorStr = "";
    	String poErrorStr = "";
    	for (Map<String, Object> dnItemMap : dnItemList) {
    		
    		Double RECEIPT_QTY = Double.valueOf(dnItemMap.get("LFIMG").toString());
    		dnItemMap.put("RECEIPT_QTY", RECEIPT_QTY);
    		if(dnItemMap.get("LGORT")==null || "".equals(dnItemMap.get("LGORT"))) {
    			dnItemMap.put("LGORT","00ZT");
    		}
    		dnItemMap.put("F_WERKS", dnItemMap.get("WERKS"));
    		dnItemMap.put("WERKS", dnHeaderMap.get("WERKS"));
    		dnItemMap.put("F_BATCH", dnItemMap.get("CHARG"));
    		dnItemMap.put("UMREZ", dnItemMap.get("UMVKZ"));
    		dnItemMap.put("UMREN", dnItemMap.get("UMVKN"));
    		
    		dnItemMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
    		dnItemMap.put("UNIT", dnItemMap.get("VRKME"));//交货单位
    		dnItemMap.put("BIN_CODE", binList.get(0).get("BIN_CODE"));
    		dnItemMap.put("BIN_NAME", binList.get(0).get("BIN_NAME"));
    		dnItemMap.put("RECEIVER", "");
    		dnItemMap.put("DANGER_FLAG", "0");
    		dnItemMap.put("LIFNR", vendorMap.get("LIFNR"));
    		dnItemMap.put("LIKTX", vendorMap.get("NAME1"));
    		
    		for(Map<String,Object> m:dangerList) {
    			if(m.get("MATNR").equals(dnItemMap.get("MATNR"))) {
    				dnItemMap.put("DANGER_FLAG", "X");
    				dnItemMap.put("GOOD_DATES", m.get("GOOD_DATES"));
    				break;
    			}
    		}
    		String SOBKZ="Z";
    		dnItemMap.put("SOBKZ", SOBKZ);   
    		dnItemMap.put("HEADER_TXT", txt.get("txtrule"));
    		dnItemMap.put("ITEM_TEXT", txt.get("txtruleitem"));
    		/**
    		 * 判断交货单行项目关联的PO信息是否存在
    		 */
    		if(dnItemMap.get("EBELN") ==null || "".equals(dnItemMap.get("EBELN").toString())){
    			//交货单行项目关联的PO信息未同步到WMS系统
    			poErrorStr ="交货单行项目关联的PO："+dnItemMap.get("VGBEL").toString();
    		}
			/**
			 * 判断物料主数据是否在工厂存在
			 */
    		if(dnItemMap.get("MAKTX") ==null || "".equals(dnItemMap.get("MAKTX").toString())){
    			//物料主数据不存在
    			matErrorStr += dnItemMap.get("MATNR").toString()+"，";
    		}
		}
    	if(!"".equals(poErrorStr)) {
    		poErrorStr += "信息未同步到WMS系统，请使用采购订单同步功能同步采购订单信息！";
    		return R.error(poErrorStr);
    	}
    	if(!"".equals(matErrorStr)) {
    		matErrorStr += "信息在工收货工厂"+dnHeaderMap.get("WERKS")+"不存在，请使用物料信息同步功能同步物料信息！";
    		return R.error(matErrorStr);
    	}
    	
    	PageUtils page=new PageUtils(new Page().setRecords(dnItemList));

        return R.ok().put("page", page);
    }
    
    
    /**
     * 输入303凭证，从SAP及时读取凭证信息，如果没有返回数据，报错提示：凭证号:0000000105不存在 ；
     * 校验对应凭证移动类型是否是303，如果不是报错提示：凭证号:0000000105不是有效的303凭证。
     * 核对凭证接收工厂和当前账号工厂的权限是否一致，如果不一致报错提示：您无权操作***工厂的凭证；
     * 校验凭证接收工厂对应工厂配置表是否启用核销【WMS_C_PLANT】-- HX_FLAG ，如果没有启用，提示：凭证号0000000105 接收工厂没有启用核销业务！，如果启用核销，允许带出凭证数据
     */
    @RequestMapping("/listTOVMat")
    public R listTOVMat(@RequestBody Map<String, Object> params){
       	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    	
    	String MAT_DOC = params.get("MAT_DOC").toString();//303调拨单号
    	String MAT_DOC_YEAR = params.get("MAT_DOC_YEAR").toString();//凭证年份;
    	/**
    	 * 通过调用SAP服务接口，读取SAP物料凭证数据
    	 */
    	Map<String,Object> toMap = wmsSapRemote.getSapBapiGoodsmvtDetail(MAT_DOC, MAT_DOC_YEAR);
    	if(null != toMap.get("CODE") && "-1".equals(toMap.get("CODE").toString())) {
    		//获取SAP物料凭证数据失败
    		return R.error("获取SAP物料凭证数据失败："+toMap.get("MESSAGE").toString());
    	}
    	//SAP物料凭证抬头信息
    	Map<String,Object> toHeaderMap = (Map<String,Object>)toMap.get("GOODSMVT_HEADER");
    	//SAP物料凭证行项目信息
    	List<Map<String,Object>> toItemList = (ArrayList<Map<String,Object>>)toMap.get("GOODSMVT_ITEMS");
    	if(toItemList==null ||toItemList.size()<=0) {
    		return R.error("SAP物料凭证:"+MAT_DOC+"不存在，请检查是否输入有误！");
    	}
    	
     	/*
    	 * 判断物料凭证类型是否为303凭证 MOVE_TYPE = 303
    	 */
    	if(null == toItemList.get(0).get("MOVE_TYPE") || (!"303".equals(toItemList.get(0).get("MOVE_TYPE")) ) ) {
    		//凭证类型不正确
    		return R.error("SAP物料凭证:"+MAT_DOC+"不是303物料凭证，请检查是否输入有误！");
    	}
    	
    	//删除303凭证里自动创建的行项目
    	for (int i=0;i<toItemList.size();i++) {
    		Map<String, Object> map = toItemList.get(i);
			if(map.get("X_AUTO_CRE")!=null && "X".equals(map.get("X_AUTO_CRE"))) {
				//给上一行设置收货库位信息
				Map<String, Object> pmap = toItemList.get(i-1);
				pmap.put("MOVE_STLOC", map.get("MOVE_STLOC"));
				//自动创建的行项目，删除
				toItemList.remove(i);
			}
		}
    	/*
    	 * 判断调拨收货工厂是否启用核销业务，否则提示错误
    	 */
    	String werks = toItemList.get(0).get("MOVE_PLANT").toString();//调拨收货工厂
    	params.put("werks", werks);
    	List<WmsCPlant> cPlantList = (List<WmsCPlant>)wmsCPlantService.queryPage(params).getList();
    	if(cPlantList==null || cPlantList.size()!=1 || !"X".equals(cPlantList.get(0).getHxFlag())) {
    		return R.error("收货工厂"+werks+"未启用核销业务，请检查工厂配置!");
    	}
    	/*
    	 * 判断账号是否有对应收货工厂的操作权限，如果没有提示无权限 WERKS
    	 */
    	Set<Map<String,Object>> deptList= userUtils.getUserWerks("ACCOUNT_RECEIPT_V");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(werks)) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return R.error("您无权操作"+werks+"工厂（收货）的单据!");
    	}   	
    	
    	/**
    	 * 获取储位
    	 */
    	params.put("BIN_TYPE", "00");
    	params.put("BIN_STATUS", "01");
    	params.put("STORAGE_MODEL", "01");
    	List<Map<String, Object>> binList = commonService.getMatBinList(params);
    	if(binList == null || binList.size()<=0) {
    		return R.error("仓库号"+params.get("WH_NUMBER").toString()+"未配置储位类型为（虚拟储位）的储位！");
    	}
    	
    	params.put("WERKS", werks); //303调拨收货工厂
    	params.put("LIFNR", "VBYD"+toItemList.get(0).get("PLANT"));//供应商代码为VBYD拼接PLANT代码
    	/**
    	 * 根据供应商代码获取描述信息
    	 */
    	Map<String,Object> vendorMap = wmsAccountReceiptHxDnService.getSapVendorByNo("VBYD"+toItemList.get(0).get("PLANT"));
    	if(null == vendorMap) {
    		return R.error("交货单关联的供应商："+"VBYD"+toItemList.get(0).get("PLANT")+"信息未同步至WMS系统，请先同步供应商信息！");
    	}
    	/**
    	 * 查询是否存在危化品
    	 */
    	List<Map<String,Object>> dangerList = wmsAccountReceiptHxPoService.getDangerMatList(params);//危化品物料校验列表
    	
    	/**
    	 * 根据工厂代码、业务类型获取SAP303调拨收料(V)("24")抬头文本与行文本
    	 */
    	Map<String,String>txtMap=new HashMap<String,String>();
    	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
    	txtMap.put("WERKS", werks);
    	txtMap.put("BUSINESS_NAME","24" );
    	txtMap.put("JZ_DATE", JZ_DATE);
    	txtMap.put("currentUser", params.get("FULL_NAME").toString());
    	txtMap.put("SAP_303_NO", MAT_DOC);
    	Map<String, Object> txt=wmsCTxtService.getRuleTxt(txtMap);
    	
    	/**
    	 * 根据303物料凭证行项目，查询关联的核销信息
    	 */
    	toItemList = wmsAccountReceiptHxToService.getHxToListByMatDocItem(toItemList);
    	String matErrorStr = "";
    	for (Map<String, Object> toItemMap : toItemList) {
    		toItemMap.put("F_WERKS", toItemMap.get("PLANT"));//发货工厂
    		toItemMap.put("F_LGORT", toItemMap.get("STGE_LOC"));//发货库位
    		toItemMap.put("F_BATCH", toItemMap.get("BATCH"));//发货批次
    		toItemMap.put("WERKS", werks);
			toItemMap.put("LGORT", toItemMap.get("MOVE_STLOC"));//收货库位
    		toItemMap.put("BATCH", toItemMap.get("MOVE_BATCH"));//收货批次
    		toItemMap.put("WH_NUMBER", params.get("WH_NUMBER").toString());
    		toItemMap.put("UNIT", toItemMap.get("ENTRY_UOM"));//交货单位
    		toItemMap.put("BIN_CODE", binList.get(0).get("BIN_CODE"));
    		toItemMap.put("BIN_NAME", binList.get(0).get("BIN_NAME"));
    		toItemMap.put("RECEIVER", "");
    		toItemMap.put("DANGER_FLAG", "0");
    		toItemMap.put("LIFNR", vendorMap.get("LIFNR"));
    		toItemMap.put("LIKTX", vendorMap.get("NAME1"));  
    		//toItemMap.put("UMREZ", toItemMap.get("UMVKZ"));//销售单位转换为基本单位的分子(因子)
    		//toItemMap.put("UMREN", toItemMap.get("UMVKN"));//销售单位转换为基本单位的值（除数）
    		
    		toItemMap.put("BEDNR", vendorMap.get("ITEM_TEXT"));
    		toItemMap.put("AFNAM", vendorMap.get("GR_RCPT"));
    		String XS305_QTY=toItemMap.get("XS305")==null?"0":toItemMap.get("XS305").toString();
    		String XS306_QTY=toItemMap.get("XS306")==null?"0":toItemMap.get("XS306").toString();
    		String ENTRY_QNT=toItemMap.get("ENTRY_QNT")==null?"0":toItemMap.get("ENTRY_QNT").toString();
    		Double RECEIPT_QTY = Double.valueOf(ENTRY_QNT)-Double.valueOf(XS305_QTY)+Double.valueOf(XS306_QTY);
    		toItemMap.put("RECEIPT_QTY", RECEIPT_QTY);
    		
    		toItemMap.put("XS305", Double.valueOf(XS305_QTY)-Double.valueOf(XS306_QTY));
    		
    		for(Map<String,Object> m:dangerList) {
    			if(m.get("MATNR").equals(toItemMap.get("MATNR"))) {
    				toItemMap.put("DANGER_FLAG", "X");
    				toItemMap.put("GOOD_DATES", m.get("GOOD_DATES"));
    				toItemMap.put("PRODUCT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
    				break;
    			}
    		}
    		String SOBKZ="Z";
    		toItemMap.put("SOBKZ", SOBKZ);   
    		toItemMap.put("HEADER_TXT", txt.get("txtrule"));
    		toItemMap.put("ITEM_TEXT", txt.get("txtruleitem"));
			/**
			 * 判断物料主数据是否在工厂存在
			 */
    		if(toItemMap.get("MAKTX") ==null){
    			//物料主数据不存在
    			matErrorStr += toItemMap.get("MATNR").toString()+"，";
    		}
		}
    	if(!"".equals(matErrorStr)) {
    		matErrorStr += "信息在收货工厂"+werks+"不存在，请使用物料信息同步功能同步物料信息！";
    		return R.error(matErrorStr);
    	}
    	
    	PageUtils page=new PageUtils(new Page().setRecords(toItemList));

        return R.ok().put("page", page);
    }
    
    /**
     * SAP生产订单收货过账(V)、副产品收货过账(V)-核销：
     * 特殊业务：生产订单先过账，以后根据实际生产情况，再实物收货入库（A）。
     * 虚收货（V）：用生产订单在系统做收货（包含主产品，联产品、副产品），产生事务记录，凭证记录（XS101M）（过账到SAP,101）
     * @param params
     * @return
     */
    @RequestMapping("/listMOVMat")
    public R listMOVMat(@RequestBody Map<String,Object> params) {
    	R r=new R();
    	
       	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    	
    	String CUR_DATE = DateUtils.format(new Date(),DateUtils.DATE_PATTERN);
    	String CREATE_DATE = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
    	
    	String receiptType = params.get("receiptType").toString();//收货类型
    	
    	String moStr= params.get("PO_NO").toString();//生产订单号，可能包含多个，拼接参数
    	String WERKS = params.get("WERKS").toString();//工厂
    	String LGORT = params.get("LGORT").toString();//库存地点
    	String LGORT_NAME = params.get("LGORT_NAME").toString();//库存地点名称
    	List<String> moList = new ArrayList<String>();
    	String[] moArray = null;
    	if(moStr.contains(",")) {
    		moArray = moStr.split(",");
    	}else if(moStr.contains(";")) {
    		moArray = moStr.split(";");
    	}else if(moStr.contains("，")) {
    		moArray = moStr.split("，");
    	}else if(moStr.contains("、")) {
    		moArray = moStr.split("、");
    	}else {
    		moList.add(moStr);
    	}
    	if(moArray!=null) {
        	for (String string : moArray) {
        		if(!string.trim().equals("")) {
        			moList.add(string);
        		}
    		}
    	}
    	if(moList.size()<=0) {
    		return r.error("生产订单"+moStr+"输入有误，！");
    	}
    	
    	/**
    	 * 根据生产订单号，查询生产订单信息（抬头和组件信息）（检验抬头生产工厂是否启用核销业务）
    	 */
    	params.put("AUFNR", moList);
    	Map<String,Object> moInfo = wmsAccountReceiptHxMoService.getMoInfoByMoNo(moList,WERKS);
    	List<Map<String,Object>> moHeadInfoList = moInfo.get("moHeadInfoList")==null?null:(List<Map<String,Object>>)moInfo.get("moHeadInfoList");
    	List<Map<String,Object>> moItemInfoList = moInfo.get("moItemInfoList")==null?null:(List<Map<String,Object>>)moInfo.get("moItemInfoList");
    	List<Map<String,Object>> moComponentInfoList = moInfo.get("moComponentInfoList")==null?null:(List<Map<String,Object>>)moInfo.get("moComponentInfoList");
    	
		/**
		 * 返回前台的数据集合
		 */
		List<Map<String,Object>>  grMatList = new ArrayList<Map<String,Object>>();
		
    	if(( "60".equals(receiptType) && (moHeadInfoList ==null || moHeadInfoList.size()<=0 ))) {
    		return r.error("生产订单"+moStr+"在生产工厂"+WERKS+"下不存在 ，请核对是否输入有误，如果无误，请使用生产订单同步功能，同步SAP数据并检查工厂是否已配置收货（V）业务！");
    	}
    	if("62".equals(receiptType) ) {
    		if(moHeadInfoList ==null || moHeadInfoList.size()<=0 ) {
    			return r.error("生产订单"+moStr+"在生产工厂"+WERKS+"下不存在 ，请核对是否输入有误，如果无误，请使用生产订单同步功能，同步SAP数据并检查工厂是否已配置收货（V）业务！");
    		}
    		if(moHeadInfoList !=null && moHeadInfoList.size()>0 && (moComponentInfoList==null || moComponentInfoList.size()<=0)){
    			return r.error("生产订单"+moStr+"没有可收货的副产品！");
    		}

    	}
    	
		/**
		 * 校验工厂是否已启用核销管理功能
		 */
		String HX_FLAG = moHeadInfoList.get(0).get("HX_FLAG") == null?"":moHeadInfoList.get(0).get("HX_FLAG").toString();
		if(!"X".equals(HX_FLAG)) {
			return r.error("收货工厂"+moHeadInfoList.get(0).get("WERKS")+"未启用核销业务，请检查工厂配置!");
		}
    	
    	/**
    	 * 校验操作账号是否有每一个生产订单关联的生产工厂的权限，移除没有操作权限的生产订单数据
    	 */
    	params.put("WERKS", WERKS);
    	
    	String grArror = "";
    	for (Map<String, Object> headMap : moHeadInfoList) {
    		if(!WERKS.equals(headMap.get("WERKS").toString())) {
    			return r.error("不允许同时操作多个工厂的生产订单("+moHeadInfoList.get(0).get("AUFNR")+"-"+WERKS+"，"+headMap.get("AUFNR")+"-"+headMap.get("WERKS")+")！");
    		}
    		/**
    		 * 校验生产订单状态是否可收货
    		 */
    		String ISTAT_TXT = headMap.get("ISTAT_TXT") == null?"":headMap.get("ISTAT_TXT").toString();//生产订单状态 
    		String ELIKZ =  headMap.get("ELIKZ") == null?null:headMap.get("ELIKZ").toString(); // 交货已完成标识
    		//生产订单状态必须包含REL（释放）字符串，且交货已完成标识为空的订单才允许收货
    		if(!ISTAT_TXT.contains("REL")|| ISTAT_TXT.contains("DLFL") || ISTAT_TXT.contains("LKD") || "X".equals(ELIKZ)) {
    			grArror = ","+headMap.get("ELIKZ");
    		}
		}
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
    	Set<Map<String,Object>> deptList= userUtils.getUserWerks("ACCOUNT_RECEIPT_V");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(WERKS)) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return r.error("您无权操作"+WERKS+"工厂的生产订单！");
    	}
    	
    	if(!"".equals(grArror)) {
    		return r.error("生产订单"+grArror+"未释放或已收货完成或已结算，不允许再收货！");
    	}
    	if("60".equals(receiptType)) {
    		//SAP生产订单收货过账(V)
        	/**
        	 * 处理产成品
        	 */
        	for (Map<String,Object> moItemMap : moItemInfoList) {
        		moItemMap.put("MO_NO", moItemMap.get("AUFNR"));
        		moItemMap.put("MO_ITEM_NO", moItemMap.get("POSNR"));
        		Double XS101M = Double.valueOf( moItemMap.get("XS101M")==null?"0": moItemMap.get("XS101M").toString());
        		Double XS102M = Double.valueOf( moItemMap.get("XS102M")==null?"0": moItemMap.get("XS102M").toString());
        		Double XSQTY = XS101M-XS102M; //已收货数量
        		Double RECEIPT_QTY = Double.valueOf(moItemMap.get("PSMNG").toString())-XSQTY; //可收货数量
        		moItemMap.put("XSQTY", XSQTY);
        		moItemMap.put("RECEIPT_QTY", RECEIPT_QTY);
        		moItemMap.put("LGORT", LGORT);
        		moItemMap.put("UNIT", moItemMap.get("MEINS"));
        		moItemMap.put("SOBKZ", "Z");
        		moItemMap.put("BDMNG", moItemMap.get("PSMNG"));
        		
        		grMatList.add(moItemMap);

    		}
    	}else {
    		//副产品收货过账(V)
        	/**
        	 * 处理产成品
        	 */
        	for (Map<String,Object> moComponentMap : moComponentInfoList) {
        		moComponentMap.put("MO_NO", moComponentMap.get("AUFNR"));
        		moComponentMap.put("POSNR", moComponentMap.get("RSPOS"));
        		
        		Double XS531 = Double.valueOf( moComponentMap.get("XS531")==null?"0": moComponentMap.get("XS531").toString());
        		Double XS532 = Double.valueOf( moComponentMap.get("XS532")==null?"0": moComponentMap.get("XS532").toString());
        		Double XSQTY = XS531-XS532; //已收货数量
        		Double RECEIPT_QTY = Double.valueOf(moComponentMap.get("BDMNG").toString())-XSQTY; //可收货数量
        		moComponentMap.put("XSQTY", XSQTY);
        		moComponentMap.put("RECEIPT_QTY", RECEIPT_QTY);
        		moComponentMap.put("LGORT", LGORT);
        		moComponentMap.put("UNIT", moComponentMap.get("MEINS"));
        		moComponentMap.put("SOBKZ", "Z");
        		if(moComponentMap.get("BDMNG")==null ||moComponentMap.get("BDMNG").toString().length()<=0) {
        			moComponentMap.put("BDMNG", moComponentMap.get("PSMNG"));
        		}
        		
        		
        		grMatList.add(moComponentMap);

    		}
    	}

    	/**
    	 * 获取储位
    	 */
    	params.put("BIN_TYPE", "00");
    	params.put("BIN_STATUS", "01");
    	params.put("STORAGE_MODEL", "01");
    	List<Map<String, Object>> binList = commonService.getMatBinList(params);
    	if(binList == null || binList.size()<=0) {
    		return r.error("仓库号"+params.get("WH_NUMBER").toString()+"未配置储位类型为（虚拟储位）的储位！");
    	}
    	/**
    	 * 根据工厂代码、业务类型获取SAP生产订单收货（V）("60")抬头文本与行文本
    	 */
    	Map<String,String>txtMap=new HashMap<String,String>();
    	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
    	txtMap.put("WERKS", WERKS);
    	txtMap.put("BUSINESS_NAME","60" );
    	txtMap.put("JZ_DATE", JZ_DATE);
    	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
    	txtMap.put("MO_NO", moStr);
    	txtMap.put("MO_NO", moStr);
    	txtMap.put("LGORT_NAME", LGORT_NAME);
    	
    	Map<String, Object> txt = wmsCTxtService.getRuleTxt(txtMap);
    	
    	for(Map<String,Object> _m:grMatList) {
    		_m.put("sortNo", _m.get("MO_NO"));
    		_m.put("PRODUCT_DATE", CUR_DATE);
    		_m.put("CREATE_DATE", CREATE_DATE);
    		_m.put("CREATOR", params.get("USERNAME").toString());
    		_m.put("DANGER_FLAG", "0");
    		_m.put("HEADER_TXT", txt.get("txtrule"));
    		_m.put("ITEM_TEXT", txt.get("txtruleitem"));
    		
    		_m.put("BIN_CODE", binList.get(0).get("BIN_CODE"));
    		_m.put("BIN_NAME", binList.get(0).get("BIN_NAME"));
    	}
    	
    	PageUtils page=new PageUtils(new Page().setRecords(grMatList));
    	
    	return R.ok().put("page", page);
    } 
    
    
    /**
     * 收货（V）确认-核销业务
     * @param params
     * @return
     */
    @RequestMapping("/boundIn")
    public R boundIn(@RequestBody Map<String, Object> params) {
       	Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME")+":"+currentUser.get("FULL_NAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("CREATOR", currentUser.get("USERNAME")+"："+currentUser.get("FULL_NAME"));
    	String BUSINESS_NAME=params.get("BUSINESS_NAME").toString();
    	String curDate=DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
    	try {
    		if("22".equals(BUSINESS_NAME)) {//PO采购订单确认收货（V）
    	    	String matListStr=params.get("matList").toString();
    	    	
    	    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("USERNAME").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("QTY_SAP", m.get("RECEIPT_QTY"));
    	    		
    	    		matList.add(m);
    	    	});
    	    	wmsAccountReceiptHxPoService.setMatBatch(params, matList);
    			params.put("matList", matList);
    	    	params.put("CREATOR",params.get("USERNAME").toString());
    	    	params.put("CREATE_DATE", curDate);
        		return wmsAccountReceiptHxPoService.boundIn_POV(params);
        	}else if("23".equals(BUSINESS_NAME)) {//SAP交货单确认收货（V）
    			/*
    			 * 交货单过账，需处理前台修改的交货数量和未勾选行项目
    			 */
    	    	String matListStr=params.get("matList").toString();
    	    	String allDataListStr = params.get("allDataList").toString();
    			
    	    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	
    	    	List<Map> allDataListMap = JSONObject.parseArray(allDataListStr, Map.class);
    	    	List<Map> matListMap = JSONObject.parseArray(matListStr, Map.class);
    	    	
    	    	for (Map a : allDataListMap) {
    	    		String VBELN = a.get("VBELN").toString();//交货单
    	    		String POSNR = a.get("POSNR").toString();//行项目
    	    		a.put("RECEIPT_QTY", "0");
    	    		for (Map m : matListMap) {
    	    			if(VBELN.equals(m.get("VBELN").toString()) && POSNR.equals(m.get("POSNR").toString())) {
        	    			//覆盖
    	    				a = m;
    	    				break;
        	    		}
					}
    	    		a.put("CREATOR", params.get("USERNAME").toString());
    	    		a.put("CREATE_DATE", curDate);
    	    		a.put("QTY_SAP", a.get("RECEIPT_QTY"));
    	    		
    	    		matList.add(a);
				}
    	    	wmsAccountReceiptHxPoService.setMatBatch(params, matList);
    	    	params.put("matList", matList);
    	    	params.put("CREATOR", params.get("USERNAME").toString());
    	    	params.put("CREATE_DATE", curDate);
        		return wmsAccountReceiptHxDnService.boundIn_DNV(params);
        	}else if("24".equals(BUSINESS_NAME)) {//303凭证调拨收货（V）
    	    	String matListStr=params.get("matList").toString();
    			
    	    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("USERNAME").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("QTY_SAP", m.get("RECEIPT_QTY"));
    	    		
    	    		matList.add(m);
    	    	});
    	    	wmsAccountReceiptHxPoService.setMatBatch(params, matList);
    	    	params.put("matList", matList);
    	    	params.put("CREATOR", params.get("USERNAME").toString());
    	    	params.put("CREATE_DATE", curDate);
        		return wmsAccountReceiptHxToService.boundIn_TOV(params);
        	}else if("60".equals(BUSINESS_NAME) || "62".equals(BUSINESS_NAME)) {//SAP生产订单收货过账(V) 副产品收货过账(V)
    	    	String matListStr=params.get("matList").toString();
    			
    	    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("USERNAME").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("QTY_SAP", m.get("RECEIPT_QTY"));
    	    		//m.put("EDITOR", params.get("USERNAME").toString());
    	    		//m.put("EDIT_DATE", curDate);
    	    		
    	    		matList.add(m);
    	    	});
    	    	wmsAccountReceiptHxPoService.setMatBatch(params, matList);
    	    	params.put("matList", matList);
    	    	params.put("CREATOR", params.get("USERNAME").toString());
    	    	params.put("CREATE_DATE", curDate);
        		return wmsAccountReceiptHxMoService.boundIn_MOV(params);
        	}else {
        		return R.error("未知业务，系统无法处理，请联系系统管理员！");
        	}
        	
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}

    }
    
}
