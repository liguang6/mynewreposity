package com.byd.wms.business.modules.query.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.ConfigConstant;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.wms.business.modules.cswlms.dao.DispatchingJISBillPickingDAO;
import com.byd.wms.business.modules.cswlms.service.DisPatchingJISBillPickingService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.utils.CompareObject;
import com.byd.wms.business.modules.returngoods.service.WmsReturnGoodsService;

/**
 * 单据打印通用控制器
 * @author develop01
 * @since 2018-12-13
 */

@RestController
@RequestMapping("docPrint")
public class DocumentPrintController {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private ConfigConstant configConstant;
    @Autowired
	private WmsInInboundDao wmsInInboundDao;
	@Autowired
    private WmsReturnGoodsService wmsReturnGoodsService;
	@Autowired
	DisPatchingJISBillPickingService disPatchingJISService;
	@Autowired
	private DispatchingJISBillPickingDAO dispatchingJISBillPickingDAO;
 
    
    /**
     * 收货进仓单打印预览
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/inBoundPreview")
    /*@RequiresPermissions("docPrint:inBoundPreview")*/
    public Map<String,Object> inBoundPreview(@RequestParam Map<String, Object> params) {
		System.err.println(" 收货进仓单打印wms-service_controller=================");
		Map<String,Object> rtnMap = new HashMap<String,Object>();
    	String inboundNo="";
    	//inboundNo3
    	if(params.get("inboundNo")!=null){
    		inboundNo=params.get("inboundNo").toString();
    	}else if(params.get("inboundNo3")!=null){
    		inboundNo=params.get("inboundNo3").toString();
    	}
    	// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String,Object>> listVars = new ArrayList<>();
        
    	if(inboundNo!=null){
    		Map<String,Object> inboundMap=new HashMap<String,Object>();
    		inboundMap.put("INBOUND_NO", inboundNo);
    		List<Map<String,Object>>  inboundList=wmsInInboundDao.getInBoundItemAllSt(inboundMap);
    		
    		//获取采购订单
    		for(int m=0;m<inboundList.size();m++){
    			String receipt_str=inboundList.get(m).get("RECEIPT_NO")==null?"":inboundList.get(m).get("RECEIPT_NO").toString();
    			String receipt_item_str=inboundList.get(m).get("RECEIPT_ITEM_NO")==null?"":inboundList.get(m).get("RECEIPT_ITEM_NO").toString();
    			String po_no_str="";
    			if(!"".equals(receipt_str)){
					Map<String, Object> inboundreceiptMap = new HashMap <String, Object>();
					inboundreceiptMap.put("RECEIPT_NO", receipt_str);
					inboundreceiptMap.put("RECEIPT_ITEM_NO", receipt_item_str);
					List<Map<String, Object>> receiptPolist=wmsInInboundDao.getReceiptByReceiptNo(inboundreceiptMap);
					
					if(receiptPolist!=null&&receiptPolist.size()>0){
						if(receiptPolist.get(0)!=null&&receiptPolist.get(0).get("PO_NO")!=null){
							 po_no_str=receiptPolist.get(0).get("PO_NO").toString();
							 
						}
					}
    			}
    			inboundList.get(m).put("PO_NO", po_no_str);
    		}
    		
    		String baseDir = configConstant.getLocation()+"/barcode/";
            
            if(inboundList!=null&&inboundList.size()>0){
            	
            	Map<String,Object> variables = new HashMap<>();
                variables.put("INBOUND_NO",inboundList.get(0).get("INBOUND_NO"));
                variables.put("WERKS",inboundList.get(0).get("WERKS"));
                variables.put("CREATE_DATE",inboundList.get(0).get("CREATE_DATE"));
                variables.put("CREATOR",inboundList.get(0).get("CREATOR"));
                variables.put("WH_NUMBER",inboundList.get(0).get("WH_NUMBER"));
                
                variables.put("LGORT",inboundList.get(0).get("LGORT")==null?"":inboundList.get(0).get("LGORT"));
                variables.put("WORKSHOP",inboundList.get(0).get("WORKSHOP")==null?"":inboundList.get(0).get("WORKSHOP"));
                Map<String,Object> deptMap=new HashMap<String,Object>();
        		deptMap.put("WERKS", inboundList.get(0).get("WERKS"));
        		List<Map<String,Object>> divlist=wmsInInboundDao.getDeptNameByWerk(deptMap);
        		String divName="";
        		if(divlist!=null&&divlist.size()>0){
        			divName=divlist.get(0).get("NAME").toString();
        		}
        		List<Map<String,Object>> complist=wmsInInboundDao.getCompNameByWerk(deptMap);
        		String compName="";
        		if(complist!=null&&complist.size()>0){
        			compName=complist.get(0).get("NAME").toString();
        		}
                variables.put("COMP", compName);
                variables.put("DIV", divName);
                
              //根据进仓单号 获取wms凭证号和sap凭证号
        		String wmsdocNo="";
        		String sapdocNo="";
        		List<Map<String,Object>> wmsdoclist=wmsInInboundDao.getWMSDOCINFOByInBoundNo(inboundMap);
        		if(wmsdoclist!=null&&wmsdoclist.size()>0){
        			if(wmsdoclist.get(0).get("WMS_NO")!=null){
        			wmsdocNo=wmsdoclist.get(0).get("WMS_NO").toString();
        			}
        			if(wmsdoclist.get(0).get("WMS_SAP_MAT_DOC")!=null){
        				String sapmatlist=wmsdoclist.get(0).get("WMS_SAP_MAT_DOC").toString();
        				String sapMoveType=wmsdoclist.get(0).get("SAP_MOVE_TYPE").toString();
        				String[] sapmatArr=sapmatlist.split(";");
        				
        				String matstr_="";
        				for(String m:sapmatArr){
        					String[] matArr=m.split(":");
        					matstr_=matstr_+matArr[0].toString().concat("("+sapMoveType+") ");
        				}
        				sapdocNo=matstr_;
        			}
        		}
        		//
        		variables.put("WMSDOC", wmsdocNo);
                variables.put("SAPDOC", sapdocNo);
        		
                List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
            	for(int i=0;i<inboundList.size();i++){
            		Map<String,Object> item = new HashMap<>();
            		item.put("INDEX",i+1);
            		item.put("BATCH",inboundList.get(i).get("BATCH"));
            		item.put("MATNR",inboundList.get(i).get("MATNR")==null?"":inboundList.get(i).get("MATNR"));
            		item.put("MAKTX",inboundList.get(i).get("MAKTX"));
            		item.put("LGORT",inboundList.get(i).get("LGORT")==null?"":inboundList.get(i).get("LGORT"));
            		item.put("BIN_CODE",inboundList.get(i).get("BIN_CODE")==null?"":inboundList.get(i).get("BIN_CODE"));
            		item.put("IN_QTY",inboundList.get(i).get("IN_QTY"));
            		item.put("UNIT",inboundList.get(i).get("UNIT"));
            		item.put("SOBKZ",inboundList.get(i).get("SOBKZ")==null?"":inboundList.get(i).get("SOBKZ"));
            		item.put("BEDNR",inboundList.get(i).get("BEDNR")==null?"":inboundList.get(i).get("BEDNR"));
            		item.put("LIFNR",inboundList.get(i).get("LIFNR")==null?"":inboundList.get(i).get("LIFNR"));
            		item.put("LIKTX",inboundList.get(i).get("LIKTX")==null?"":inboundList.get(i).get("LIKTX"));
            		item.put("WH_MANAGER",inboundList.get(i).get("WH_MANAGER")==null?"":inboundList.get(i).get("WH_MANAGER"));
            		item.put("REAL_QTY",inboundList.get(i).get("REAL_QTY")==null?"":inboundList.get(i).get("REAL_QTY"));
            		if(inboundList.get(i).get("MO_NO")!=null){
            			item.put("DDH",inboundList.get(i).get("MO_NO"));//订单号
            		}else if(inboundList.get(i).get("IO_NO")!=null){
            			item.put("DDH",inboundList.get(i).get("IO_NO"));//订单号
            		}else{
            			item.put("DDH", "");
            		}
            		item.put("PO_NO",inboundList.get(i).get("PO_NO"));
            		item.put("REF_SAP_MATDOC_NO",inboundList.get(i).get("REF_SAP_MATDOC_NO")==null?"":inboundList.get(i).get("REF_SAP_MATDOC_NO").toString());
            		
            		itemList.add(item);
            	}
            	variables.put("itemList",itemList);
            	
                //生成二维码
     		        try {
     		        	String picturePath = ""; //图片路径
     		        	StringBuffer sb = new StringBuffer();
     		        	/*sb.append("{INBOUND_NO:"+inboundList.get(0).get("INBOUND_NO"));
     		        	sb.append(",WERKS:"+inboundList.get(0).get("WERKS"));
     		        	sb.append(",WH_NUMBER:"+inboundList.get(0).get("WH_NUMBER")+"}");*/
     		        	
     		        	String inbound_no=inboundList.get(0).get("INBOUND_NO")==null?"":inboundList.get(0).get("INBOUND_NO").toString();
     		        	String werks=inboundList.get(0).get("WERKS")==null?"":inboundList.get(0).get("WERKS").toString();
     		        	sb.append("INBOUND_NO:"+inbound_no);
     		        	sb.append(";WERKS:"+werks);
     		        	
     					picturePath = QRCodeUtil.encode(sb.toString(),inboundList.get(0).get("INBOUND_NO").toString(),"",baseDir,true);
     					picturePath = picturePath.replaceAll("\\\\", "//");
     					variables.put("barCode","file:"+picturePath);
     				} catch (Exception e) {
     					e.printStackTrace();
     				}
     		        
     	         variables.put("contextPath",params.get("contextPath").toString());
     		     listVars.add(variables);
            	
            }
    	}
    	
    	rtnMap.put("listVars", listVars);
    	
    	return rtnMap;

    }

    /**
     * 自制进仓单标签打印预览
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ParseException 
     */
    @RequestMapping(value = "/inInternalBoundPreview")
    /*@RequiresPermissions("docPrint:inBoundPreview")*/
    public Map<String,Object> inInternalBoundPreview(@RequestParam Map<String, Object> params) throws ParseException {
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String,Object>> listVars = new ArrayList<>();
        
    	SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
    	String inboundNo = params.get("inboundNo")==null?null:params.get("inboundNo").toString();
    	if(inboundNo!=null){
    		
    		Map<String,Object> inboundMap=new HashMap<String,Object>();
    		inboundMap.put("INBOUND_NO", inboundNo);
    		List<Map<String,Object>>  inboundList=wmsInInboundDao.getLabelList(inboundMap);
    		
    		String baseDir = configConstant.getLocation()+"/barcode/";
            
            if(inboundList!=null&&inboundList.size()>0){
            	Map<String,Object> deptMap=new HashMap<String,Object>();
        		deptMap.put("WERKS", inboundList.get(0).get("WERKS"));
        		List<Map<String,Object>> divlist=wmsInInboundDao.getDeptNameByWerk(deptMap);
        		String divName="";
        		if(divlist!=null&&divlist.size()>0){
        			divName=divlist.get(0).get("NAME").toString();
        		}
        		
                
            	for(int i=0;i<inboundList.size();i++){
            		Map<String,Object> variables = new HashMap<>();
            		variables.put("DIV", divName);
            		variables.put("WERKS",inboundList.get(i).get("WERKS"));
            		variables.put("LABEL_NO",inboundList.get(i).get("LABEL_NO"));
            		variables.put("BATCH",inboundList.get(i).get("BATCH"));
            		variables.put("MATNR",inboundList.get(i).get("MATNR")==null?"":inboundList.get(i).get("MATNR"));
            		variables.put("MAKTX",inboundList.get(i).get("MAKTX")==null?"":inboundList.get(i).get("MAKTX"));
            		variables.put("IN_QTY",inboundList.get(i).get("IN_QTY"));
            		variables.put("UNIT",inboundList.get(i).get("UNIT"));
            		variables.put("BEDNR",inboundList.get(i).get("BEDNR")==null?"":inboundList.get(i).get("BEDNR"));
            		variables.put("DRAWING_NO",inboundList.get(i).get("DRAWING_NO")==null?"":inboundList.get(i).get("DRAWING_NO"));
            		variables.put("PRO_STATION",inboundList.get(i).get("PRO_STATION")==null?"":inboundList.get(i).get("PRO_STATION"));
            		variables.put("CAR_TYPE",inboundList.get(i).get("CAR_TYPE")==null?"":inboundList.get(i).get("CAR_TYPE"));
            		variables.put("WORKGROUP_NO",inboundList.get(i).get("WORKGROUP_NO")==null?"":inboundList.get(i).get("WORKGROUP_NO"));
            		variables.put("MOULD_NO",inboundList.get(i).get("MOULD_NO")==null?"":inboundList.get(i).get("MOULD_NO"));
            		variables.put("OPERATOR",inboundList.get(i).get("OPERATOR")==null?"":inboundList.get(i).get("OPERATOR"));
            		variables.put("PRODUCT_DATE",inboundList.get(i).get("PRODUCT_DATE")==null?"":sdf.format(sdf.parse(inboundList.get(i).get("PRODUCT_DATE").toString())));
            		variables.put("SO_NO",inboundList.get(i).get("SO_NO")==null?"":inboundList.get(i).get("SO_NO"));
            		variables.put("SO_ITEM_NO",inboundList.get(i).get("SO_ITEM_NO")==null?"":inboundList.get(i).get("SO_ITEM_NO"));
            		
            		String business_name=inboundList.get(i).get("BUSINESS_NAME")==null?"":inboundList.get(i).get("BUSINESS_NAME").toString();
            		if("11".equals(business_name)||"13".equals(business_name)){//交接进仓-半成品生产订单(101) 取 MO_NO/MO_ITEM_NO
            			variables.put("DDH",inboundList.get(i).get("MO_NO")==null?"":inboundList.get(i).get("MO_NO").toString().concat("/")
            					.concat(inboundList.get(i).get("MO_ITEM_NO")==null?"":inboundList.get(i).get("MO_ITEM_NO").toString()));
            		}else if("12".equals(business_name)||"14".equals(business_name)||"15".equals(business_name)){//12 交接进仓-半成品无生产订单(521),14 交接进仓-CO订单(101),15 交接进仓-研发/内部订单(262) 取IO_NO
            			variables.put("DDH",inboundList.get(i).get("IO_NO")==null?"":inboundList.get(i).get("IO_NO").toString());
            		}else if("16".equals(business_name)){// 交接进仓-成本中心(202) 取COST_CENTER
            			variables.put("DDH",inboundList.get(i).get("COST_CENTER")==null?"":inboundList.get(i).get("COST_CENTER").toString());
            		}else if("17".equals(business_name)){// 交接进仓-WBS元素(222)取WBS
            			variables.put("DDH",inboundList.get(i).get("WBS")==null?"":inboundList.get(i).get("WBS").toString());
            		}else if("67".equals(business_name)){// 采购订单（101）取PO_NO/PO_ITEM_NO
            			variables.put("DDH",inboundList.get(i).get("PO_NO")==null?"":inboundList.get(i).get("PO_NO").toString().concat("/")
            					.concat(inboundList.get(i).get("PO_ITEM_NO")==null?"":inboundList.get(i).get("PO_ITEM_NO").toString()));
            		}else{
            			variables.put("DDH", "");
            		}
            		
            		//生成二维码
    		        try {
    		        	String picturePath = ""; //图片路径
    		        	StringBuffer sb = new StringBuffer();
/*    		        	sb.append("{INBOUND_NO:"+inboundList.get(i).get("INBOUND_NO"));
    		        	sb.append(",MATNR:"+inboundList.get(i).get("MATNR"));
    		        	sb.append(",BATCH:"+inboundList.get(i).get("BATCH"));
    		        	sb.append(",IN_QTY:"+inboundList.get(i).get("IN_QTY")==null?"0":inboundList.get(i).get("IN_QTY"));
    		        	sb.append(",UNIT:"+inboundList.get(i).get("UNIT")+"}");*/
    		        	Map<String,Object> m = inboundList.get(i);
    		        	String WERKS = m.get("WERKS")==null?"":m.get("WERKS").toString();
    		        	sb.append("P:"+WERKS);
    		        	String MATNR = m.get("MATNR")==null?"":m.get("MATNR").toString();
    		        	MATNR += "/";
    		        	MATNR += m.get("UNIT")==null?"":m.get("UNIT").toString();
    		        	sb.append(";M:"+MATNR);
    		        	String BATCH = m.get("BATCH")==null?"":m.get("BATCH").toString();
    		        	sb.append(";B:"+BATCH);
    		        	/*String MO_NO = m.get("MO_NO")==null?"":m.get("MO_NO").toString();
    		        	MO_NO += "/";
    		        	MO_NO += m.get("MO_ITEM_NO")==null?"":m.get("MO_ITEM_NO").toString();*/
    		        	String DDH=variables.get("DDH")==null?"":variables.get("DDH").toString();
    		        	sb.append(";PO:"+DDH);
    		        	String IN_QTY = m.get("IN_QTY")==null?"":m.get("IN_QTY").toString();
    		        	IN_QTY += "/";
    		        	IN_QTY += m.get("BOX_SN")==null?"":m.get("BOX_SN").toString();
    		        	sb.append(";Q:"+IN_QTY);
    		        	String MOULD_NO = m.get("MOULD_NO")==null?"":m.get("MOULD_NO").toString();
    		        	sb.append(";MO:"+MOULD_NO);
    		        	String OPERATOR = m.get("OPERATOR")==null?"":m.get("OPERATOR").toString();
    		        	sb.append(";W:"+OPERATOR);
    		        	String DRAWING_NO = m.get("DRAWING_NO")==null?"":m.get("DRAWING_NO").toString();
    		        	sb.append(";PR:"+DRAWING_NO);
    		        	String PRO_STATION = m.get("PRO_STATION")==null?"":m.get("PRO_STATION").toString();
    		        	sb.append(";WP:"+PRO_STATION);
    		        	String CAR_TYPE = m.get("CAR_TYPE")==null?"":m.get("CAR_TYPE").toString();
    		        	sb.append(";C:"+CAR_TYPE);
    		        	String WORKGROUP_NO = m.get("WORKGROUP_NO")==null?"":m.get("WORKGROUP_NO").toString();
    		        	sb.append(";T:"+WORKGROUP_NO);
    		        	String LABEL_NO = m.get("LABEL_NO")==null?"":m.get("LABEL_NO").toString();
    		        	sb.append(";PN:"+LABEL_NO);
    		        	String PRODUCT_DATE = m.get("PRODUCT_DATE")==null?"":m.get("PRODUCT_DATE").toString();
    		        	sb.append(";D:"+PRODUCT_DATE+";");
    		        	
    					picturePath = QRCodeUtil.encode(sb.toString(),m.get("LABEL_NO").toString(),"",baseDir,true);
    					picturePath = picturePath.replaceAll("\\\\", "//");
    					variables.put("barCode","file:"+picturePath);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    		        
       	            variables.put("contextPath",params.get("contextPath").toString());
                    listVars.add(variables);
                    
            	}
            	}
            }
    	rtnMap.put("listVars", listVars);
    	return rtnMap;
    	
    }
    
    /**
     * 自制进仓单列表打印预览
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ParseException 
     */
    @RequestMapping(value = "/inInternalBoundListPreview")
    public Map<String,Object> inInternalBoundListPreview(@RequestParam Map<String, Object> params) throws ParseException {
		System.err.println("进仓单打印wms-service_controller=================");
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	String inboundNo ="";
    	if(params.get("inboundNo1")!=null){//自制大letter打印
    		 inboundNo = params.get("inboundNo1").toString();
    	}else if(params.get("inboundNo2")!=null){//自制和外购小letter打印
    		inboundNo = params.get("inboundNo2").toString();
    	}
    	// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String,Object>> listVars = new ArrayList<>();
        
    	if(inboundNo!=null){
    		Map<String,Object> inboundMap=new HashMap<String,Object>();
    		inboundMap.put("INBOUND_NO", inboundNo);
    		List<Map<String,Object>>  inboundList=wmsInInboundDao.getInBoundItemAllSt(inboundMap);
    		//获取采购订单
    		for(int m=0;m<inboundList.size();m++){
    			String receipt_str=inboundList.get(m).get("RECEIPT_NO")==null?"":inboundList.get(m).get("RECEIPT_NO").toString();
    			String receipt_item_str=inboundList.get(m).get("RECEIPT_ITEM_NO")==null?"":inboundList.get(m).get("RECEIPT_ITEM_NO").toString();
    			String po_no_str="";
    			if(!"".equals(receipt_str)){
					Map<String, Object> inboundreceiptMap = new HashMap <String, Object>();
					inboundreceiptMap.put("RECEIPT_NO", receipt_str);
					inboundreceiptMap.put("RECEIPT_ITEM_NO", receipt_item_str);
					List<Map<String, Object>> receiptPolist=wmsInInboundDao.getReceiptByReceiptNo(inboundreceiptMap);
					
					if(receiptPolist!=null&&receiptPolist.size()>0){
						if(receiptPolist.get(0)!=null&&receiptPolist.get(0).get("PO_NO")!=null){
							 po_no_str=receiptPolist.get(0).get("PO_NO").toString();
							 
						}
					}
    			}
    			inboundList.get(m).put("PO_NO", po_no_str);
    		}
    		
    		//根据进仓单号 获取wms凭证号和sap凭证号
    		String wmsdocNo="";
    		String sapdocNo="";
    		List<Map<String,Object>> wmsdoclist=wmsInInboundDao.getWMSDOCINFOByInBoundNo(inboundMap);
    		if(wmsdoclist!=null&&wmsdoclist.size()>0){
    			if(wmsdoclist.get(0).get("WMS_NO")!=null){
    			wmsdocNo=wmsdoclist.get(0).get("WMS_NO").toString();
    			}
    			if(wmsdoclist.get(0).get("WMS_SAP_MAT_DOC")!=null){
    				String sapmatlist=wmsdoclist.get(0).get("WMS_SAP_MAT_DOC").toString();
    				String sapMoveType=wmsdoclist.get(0).get("SAP_MOVE_TYPE").toString();
    				String[] sapmatArr=sapmatlist.split(";");
    				
    				String matstr_="";
    				for(String m:sapmatArr){
    					String[] matArr=m.split(":");
    					matstr_=matstr_+matArr[0].toString().concat("("+sapMoveType+") ");
    				}
    				sapdocNo=matstr_;
    			}
    		}
    		//
    		
    		String baseDir = configConstant.getLocation()+"/barcode/";
            
            if(inboundList!=null&&inboundList.size()>0){
            	
            	Map<String,Object> deptMap=new HashMap<String,Object>();
        		deptMap.put("WERKS", inboundList.get(0).get("WERKS"));
        		List<Map<String,Object>> divlist=wmsInInboundDao.getDeptNameByWerk(deptMap);
        		String divName="";
        		if(divlist!=null&&divlist.size()>0){
        			divName=divlist.get(0).get("NAME").toString();
        		}
        		List<Map<String,Object>> complist=wmsInInboundDao.getCompNameByWerk(deptMap);
        		String compName="";
        		if(complist!=null&&complist.size()>0){
        			compName=complist.get(0).get("NAME").toString();
        		}
            	
            	Map<String,Object> variables = new HashMap<>();
                variables.put("INBOUND_NO",inboundList.get(0).get("INBOUND_NO"));
                variables.put("WERKS",inboundList.get(0).get("WERKS"));
                variables.put("CREATE_DATE",inboundList.get(0).get("CREATE_DATE"));
                variables.put("CREATOR",inboundList.get(0).get("CREATOR"));
                variables.put("WH_NUMBER",inboundList.get(0).get("WH_NUMBER")==null?"":inboundList.get(0).get("WH_NUMBER"));
                variables.put("LGORT",inboundList.get(0).get("LGORT")==null?"":inboundList.get(0).get("LGORT"));
                variables.put("WORKSHOP",inboundList.get(0).get("WORKSHOP")==null?"":inboundList.get(0).get("WORKSHOP"));
                variables.put("COMP", compName);
                variables.put("DIV", divName);
                variables.put("WMSDOC", wmsdocNo);
                variables.put("SAPDOC", sapdocNo);
                List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
            	for(int i=0;i<inboundList.size();i++){
            		Map<String,Object> item = new HashMap<>();
            		item.put("INDEX",i+1);
            		item.put("BATCH",inboundList.get(i).get("BATCH"));
            		item.put("MATNR",inboundList.get(i).get("MATNR")==null?"":inboundList.get(i).get("MATNR"));
            		item.put("MAKTX",inboundList.get(i).get("MAKTX")==null?"":inboundList.get(i).get("MAKTX"));
            		item.put("LGORT",inboundList.get(i).get("LGORT")==null?"":inboundList.get(i).get("LGORT"));
            		item.put("BIN_CODE",inboundList.get(i).get("BIN_CODE")==null?"":inboundList.get(i).get("BIN_CODE"));
            		item.put("IN_QTY",inboundList.get(i).get("IN_QTY"));
            		item.put("REAL_QTY",inboundList.get(i).get("REAL_QTY")==null?"":inboundList.get(i).get("REAL_QTY"));
            		item.put("UNIT",inboundList.get(i).get("UNIT"));
            		item.put("SOBKZ",inboundList.get(i).get("SOBKZ")==null?"":inboundList.get(i).get("SOBKZ"));
            		item.put("BEDNR",inboundList.get(i).get("BEDNR")==null?"":inboundList.get(i).get("BEDNR"));
            		item.put("LIFNR",inboundList.get(i).get("LIFNR")==null?"":inboundList.get(i).get("LIFNR"));
            		item.put("LIKTX",inboundList.get(i).get("LIKTX")==null?"":inboundList.get(i).get("LIKTX"));
            		item.put("WH_MANAGER",inboundList.get(i).get("WH_MANAGER")==null?"":inboundList.get(i).get("WH_MANAGER"));
            		item.put("CREATOR",inboundList.get(i).get("CREATOR")==null?"":inboundList.get(i).get("CREATOR"));
            		if(inboundList.get(i).get("MO_NO")!=null){
            			item.put("DDH",inboundList.get(i).get("MO_NO"));//订单号
            		}else if(inboundList.get(i).get("IO_NO")!=null){
            			item.put("DDH",inboundList.get(i).get("IO_NO"));//订单号
            		}else{
            			item.put("DDH", "");
            		}
            		
            		item.put("PO_NO",inboundList.get(i).get("PO_NO"));
            		item.put("REF_SAP_MATDOC_NO",inboundList.get(i).get("REF_SAP_MATDOC_NO")==null?"":inboundList.get(i).get("REF_SAP_MATDOC_NO").toString());
            		
            		itemList.add(item);
            	}
            	variables.put("itemList",itemList);
            	
                //生成二维码
     		        try {
     		        	
     		        	Map<String,Object> m = inboundList.get(0);
     		        	
     		        	String picturePath = ""; //图片路径
     		        	StringBuffer sb = new StringBuffer();
     		        	String inbound_no=m.get("INBOUND_NO")==null?"":m.get("INBOUND_NO").toString();
     		        	String werks=m.get("WERKS")==null?"":m.get("WERKS").toString();
     		        	sb.append("INBOUND_NO:"+inbound_no);
     		        	sb.append(";WERKS:"+werks);
     		        	
     					picturePath = QRCodeUtil.encode(sb.toString(),inboundList.get(0).get("INBOUND_NO").toString(),"",baseDir,true);
     					picturePath = picturePath.replaceAll("\\\\", "//");
     					variables.put("barCode","file:"+picturePath);
     				} catch (Exception e) {
     					e.printStackTrace();
     				}
     		      
    	          variables.put("contextPath",params.get("contextPath").toString());
     		      listVars.add(variables);
            	
            }
    	}
    	rtnMap.put("listVars", listVars);
    	return rtnMap;
    
    }
    
    /**
     * 收料房退货打印标签
     * @param params
     * PageSize:0(默认)：A4；    1：A4高度减半
     */
    @RequestMapping(value = "/receiveRoomOutPrint")
    public Map<String,Object> receiveRoomOutPrint(@RequestParam Map<String, Object> params) {
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	System.out.println("---->receiveRoomOutPrint");
    	String baseDir = configConstant.getLocation()+"/barcode/";
    	String business_name = "";
    	String SAP_OUT_NO = "";
    	String outNo = params.get("outNo").toString();
    	String PageSize = params.get("PageSize")==null?"0":params.get("PageSize").toString();
    	String PageWidth = (PageSize.equals("1"))?"148.5mm":"297mm";
    	List<Map<String,Object>> listVars = new ArrayList<>();
    	
    	List<String> out_list=Arrays.asList(outNo.split(";"));
		for(int i=0;i<out_list.size();i++) {
			System.out.println("-->outNo = " + out_list.get(i));
			Map<String,Object> variables = new HashMap<>();// 模板数据封装 
	    	variables.put("INBOUND_NO", out_list.get(i));
	    	
	    	Map<String, Object> par = new HashMap <String, Object>();
	    	par.put("RETURN_NO", out_list.get(i));
	    	
	    	List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
	    	itemList = wmsReturnGoodsService.getReceiveRoomOutReturnPrintData(par);	
	    	for (Map<String,Object> item :itemList) {
	    		if (item.get("REAL_QTY").toString().equals("0")) 
	    			item.put("REAL_QTY", "");
	    	}
	    	variables.put("itemList",itemList);

	    	try {
	        	String picturePath = ""; //图片路径
	        	StringBuffer sb = new StringBuffer();
	        	sb.append("{INSPECTION_NO:"+out_list.get(i));
	        	sb.append(",WERKS:"+itemList.get(0).get("WERKS").toString()+"}");
	        	
				picturePath = QRCodeUtil.encode(sb.toString(),out_list.get(i),"",baseDir,true);
				picturePath = picturePath.replaceAll("\\\\", "//");
				variables.put("barCode","file:"+picturePath);
			} catch (Exception e) {
				e.printStackTrace();
			}   
	        business_name = itemList.get(0).get("BUSINESS_NAME").toString();
	        SAP_OUT_NO = (itemList.get(0).get("SAP_OUT_NO") == null)?"":itemList.get(0).get("SAP_OUT_NO").toString();
	        variables.put("contextPath",params.get("contextPath").toString());
	        variables.put("PAGEWIDTH",PageWidth);
	        variables.put("SAP_OUT_NO",SAP_OUT_NO);
	        variables.put("RETURN_NO",itemList.get(0).get("RETURN_NO").toString());   
	        variables.put("WERKS",itemList.get(0).get("WERKS").toString());   
	        variables.put("LGORT",itemList.get(0).get("LGORT").toString());   
	        variables.put("F_WERKS",(itemList.get(0).get("F_WERKS") == null)?" ":itemList.get(0).get("F_WERKS").toString());
	        variables.put("COST_CENTER",(itemList.get(0).get("COST_CENTER") == null)?" ":itemList.get(0).get("COST_CENTER").toString());
	        variables.put("IO_NO",(itemList.get(0).get("IO_NO") == null)?" ":itemList.get(0).get("IO_NO").toString());
	        variables.put("PO_NO",(itemList.get(0).get("PO_NO") == null)?" ":itemList.get(0).get("PO_NO").toString());
	        variables.put("RETURN_REASON_DESC",(itemList.get(0).get("RETURN_REASON_DESC") == null)?" ":itemList.get(0).get("RETURN_REASON_DESC").toString());
	        variables.put("WBS",itemList.get(0).get("WBS").toString());
	        variables.put("WMS_NO",itemList.get(0).get("WMS_NO").toString());   
	        variables.put("SAP_NO",itemList.get(0).get("SAP_NO").toString()); 
	        variables.put("PZ_DATE",itemList.get(0).get("PZ_DATE").toString());   
	        variables.put("JZ_DATE",itemList.get(0).get("JZ_DATE").toString());   
	        variables.put("LIFNR",itemList.get(0).get("LIFNR").toString());   
	        variables.put("BUKRS_NAME",itemList.get(0).get("BUKRS_NAME").toString());  
	        if(itemList.get(0).get("BUKRS_NAME").toString().indexOf("(") >0 ) {
	        	variables.put("DIV",itemList.get(0).get("BUKRS_NAME").toString().substring(itemList.get(0).get("BUKRS_NAME").toString().indexOf("(")+1, itemList.get(0).get("BUKRS_NAME").toString().indexOf(")")));  
	        }else {
	        	variables.put("DIV",itemList.get(0).get("BUKRS_NAME").toString());
	        }
	        variables.put("CREATOR",itemList.get(0).get("CREATOR").toString()); 
	        variables.put("CREATE_DATE",itemList.get(0).get("CREATE_DATE").toString());
	        variables.put("HEADER_TXT",(itemList.get(0).get("HEADER_TXT") == null)?"":itemList.get(0).get("HEADER_TXT").toString()); 
	        listVars.add(variables);
		}    	
		
    	rtnMap.put("listVars", listVars);
    	rtnMap.put("business_name", business_name);
    	return rtnMap;
    	
    }
    
    @RequestMapping(value = "/wareHouseOutPrint")
    public Map<String,Object> wareHouseOutPrint(@RequestParam Map<String, Object> params) {
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	System.out.println("---->wareHouseOutPrint");
    	String baseDir = configConstant.getLocation()+"/barcode/";
    	String business_name = "";
    	String SAP_OUT_NO = "";
    	String outNo = params.get("outNo").toString();
    	String PageSize = params.get("PageSize")==null?"0":params.get("PageSize").toString();
    	String PageWidth = (PageSize.equals("1"))?"148.5mm":"297mm";
    	List<Map<String,Object>> listVars = new ArrayList<>();
    	
    	List<String> out_list=Arrays.asList(outNo.split(";"));
		for(int i=0;i<out_list.size();i++) {
			System.out.println("-->outNo = " + out_list.get(i));
			Map<String,Object> variables = new HashMap<>();// 模板数据封装 
	    	variables.put("RETURN_NO", out_list.get(i));
			
	    	List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
	    	
	    	Map<String, Object> par = new HashMap <String, Object>();
	    	par.put("RETURN_NO", out_list.get(i));
	    	itemList = wmsReturnGoodsService.getWareHouseOutReturnPrintData(par);	    	
	    	variables.put("itemList",itemList);
	    	
	    	try {
	        	String picturePath = ""; //图片路径
	        	StringBuffer sb = new StringBuffer();
	        	sb.append("{INSPECTION_NO:"+out_list.get(i));
	        	sb.append(",WERKS:"+itemList.get(0).get("WERKS").toString()+"}");
	        	
				picturePath = QRCodeUtil.encode(sb.toString(),out_list.get(i),"",baseDir,true);
				picturePath = picturePath.replaceAll("\\\\", "//");
				variables.put("barCode","file:"+picturePath);
			} catch (Exception e) {
				e.printStackTrace();
			}   
	        business_name = itemList.get(0).get("BUSINESS_NAME").toString();
	        SAP_OUT_NO = (itemList.get(0).get("SAP_OUT_NO") == null)?" ":itemList.get(0).get("SAP_OUT_NO").toString();

	        variables.put("contextPath",params.get("contextPath").toString());
	        variables.put("PAGEWIDTH",PageWidth);
	        variables.put("SAP_OUT_NO",SAP_OUT_NO);
	        variables.put("RETURN_NO",itemList.get(0).get("RETURN_NO").toString());   
	        variables.put("WERKS",itemList.get(0).get("WERKS").toString());   
	        variables.put("LGORT",itemList.get(0).get("LGORT").toString());
	        variables.put("BUKRS_NAME",itemList.get(0).get("BUKRS_NAME").toString());  
	        if(itemList.get(0).get("BUKRS_NAME").toString().indexOf("(") >0 ) {
	        	variables.put("DIV",itemList.get(0).get("BUKRS_NAME").toString().substring(itemList.get(0).get("BUKRS_NAME").toString().indexOf("(")+1, itemList.get(0).get("BUKRS_NAME").toString().indexOf(")")));  
	        }else {
	        	variables.put("DIV",itemList.get(0).get("BUKRS_NAME").toString());
	        }
	        variables.put("CREATOR",itemList.get(0).get("CREATOR").toString()); 
	        variables.put("CREATE_DATE",itemList.get(0).get("CREATE_DATE").toString());
	        variables.put("LIFNR",((itemList.get(0).get("LIFNR") == null)?"-":itemList.get(0).get("LIFNR").toString()) + " " + ((itemList.get(0).get("LIKTX") == null)?"-":itemList.get(0).get("LIKTX").toString()));  
	        variables.put("HEADER_TXT",(itemList.get(0).get("HEADER_TXT") == null)?"":itemList.get(0).get("HEADER_TXT").toString()); 
	        listVars.add(variables);
		}
		
    	rtnMap.put("listVars", listVars);
    	rtnMap.put("business_name", business_name);
    	return rtnMap;
    }
    
    @RequestMapping(value = "/wareHouseOutPickupPrint")
    public Map<String,Object> wareHouseOutPickupPrint(@RequestParam Map<String, Object> params) {
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	System.out.println("---->wareHouseOutPrint");
    	String baseDir = configConstant.getLocation()+"/barcode/";
    	String business_name = "";
    	String SAP_OUT_NO = "";
    	String outNo = params.get("outNo").toString();
    	String PageSize = params.get("PageSize")==null?"0":params.get("PageSize").toString();
    	String PageWidth = (PageSize.equals("1"))?"148.5mm":"297mm";
    	List<Map<String,Object>> listVars = new ArrayList<>();
    	
    	List<String> out_list=Arrays.asList(outNo.split(";"));
		for(int i=0;i<out_list.size();i++) {
			System.out.println("-->outNo = " + out_list.get(i));
			Map<String,Object> variables = new HashMap<>();// 模板数据封装 
	    	variables.put("RETURN_NO", out_list.get(i));
			
	    	List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
	    	
	    	Map<String, Object> par = new HashMap <String, Object>();
	    	par.put("RETURN_NO", out_list.get(i));
	    	itemList = wmsReturnGoodsService.getWareHouseOutReturnPrintData(par);	    	
	    	variables.put("itemList",itemList);
	    	
	    	try {
	        	String picturePath = ""; //图片路径
	        	StringBuffer sb = new StringBuffer();
	        	sb.append("{INSPECTION_NO:"+out_list.get(i));
	        	sb.append(",WERKS:"+itemList.get(0).get("WERKS").toString()+"}");
	        	
				picturePath = QRCodeUtil.encode(sb.toString(),out_list.get(i),"",baseDir,true);
				picturePath = picturePath.replaceAll("\\\\", "//");
				variables.put("barCode","file:"+picturePath);
			} catch (Exception e) {
				e.printStackTrace();
			}   
	        business_name = itemList.get(0).get("BUSINESS_NAME").toString();
	        SAP_OUT_NO = (itemList.get(0).get("SAP_OUT_NO") == null)?" ":itemList.get(0).get("SAP_OUT_NO").toString();

	        variables.put("contextPath",params.get("contextPath").toString());
	        variables.put("PAGEWIDTH",PageWidth);
	        variables.put("SAP_OUT_NO",SAP_OUT_NO);
	        variables.put("RETURN_NO",itemList.get(0).get("RETURN_NO").toString());   
	        variables.put("WERKS",itemList.get(0).get("WERKS").toString());   
	        variables.put("LGORT",itemList.get(0).get("LGORT").toString());
	        variables.put("BUKRS_NAME",itemList.get(0).get("BUKRS_NAME").toString());  
	        if(itemList.get(0).get("BUKRS_NAME").toString().indexOf("(") >0 ) {
	        	variables.put("DIV",itemList.get(0).get("BUKRS_NAME").toString().substring(itemList.get(0).get("BUKRS_NAME").toString().indexOf("(")+1, itemList.get(0).get("BUKRS_NAME").toString().indexOf(")")));  
	        }else {
	        	variables.put("DIV",itemList.get(0).get("BUKRS_NAME").toString());
	        }
	        variables.put("CREATOR",itemList.get(0).get("CREATOR").toString()); 
	        variables.put("CREATE_DATE",itemList.get(0).get("CREATE_DATE").toString());
	        variables.put("LIFNR",itemList.get(0).get("LIFNR").toString() + " " + itemList.get(0).get("LIKTX").toString());  
	        variables.put("HEADER_TXT",(itemList.get(0).get("HEADER_TXT") == null)?"":itemList.get(0).get("HEADER_TXT").toString()); 
	        listVars.add(variables);
		}
		
    	rtnMap.put("listVars", listVars);
    	rtnMap.put("business_name", business_name);
    	return rtnMap;
    }
    
    /**
     * 仓库物流 jis拣配单打印
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/dispatchingJISPrintPreview")
    /*@RequiresPermissions("docPrint:dispatchingJISPrintPreview")*/
    public Map<String,Object> dispatchingJISPrintPreview(@RequestParam Map<String, Object> params) {
    	// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String,Object>> listVars = new ArrayList<>();
        String baseDir = configConstant.getLocation()+"/barcode/";
        
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	String dispatchingNo ="";
    	if(params.get("dispatchingNo")!=null){//
    		dispatchingNo = params.get("dispatchingNo").toString();
    	}
    	if(dispatchingNo!=null||!"".equals(dispatchingNo)){//
    	
        //判断是否补打印
    	String buprint="";
    	if(params.get("BUPRINT")!=null){
    		buprint="补";
    	}
    	
    	List<String> typelist=new ArrayList<String>();
		typelist.add("02");
		
		params.put("typelist", typelist);//
		
    	List<Map<String,Object>> retjisDetaillist=disPatchingJISService.selectDispatchingBillPickingByPrint(params);
		Collections.sort(retjisDetaillist, new CompareObject<Map<String, Object>>().addProperty("HANDOVER_DATE"));
		Collections.sort(retjisDetaillist, new Comparator<Map>() {
            public int compare(Map o1, Map o2) {
                String handoverDateA = (String) o1.get("HANDOVER_DATE");
                String handoverDateB = (String) o2.get("HANDOVER_DATE");
                //升序
                return handoverDateA.compareTo(handoverDateB);
            }

        });
		if(retjisDetaillist!=null&&retjisDetaillist.size()>0){
			Map<String,Object> variables = new HashMap<>();
			variables.put("BUPRINT",buprint);//补打印
			
			variables.put("WERKS",retjisDetaillist.get(0).get("FROM_PLANT_CODE"));
			variables.put("DISPATCHING_NO",retjisDetaillist.get(0).get("DISPATCHING_NO"));
			variables.put("PACKAGE_MODEL",retjisDetaillist.get(0).get("PACKAGE_MODEL"));
			
			String compSize=String.valueOf(retjisDetaillist.size()).toString();
			String hedan=retjisDetaillist.get(0).get("HEDAN_QUANTITY")==null?"0":retjisDetaillist.get(0).get("HEDAN_QUANTITY").toString();
			
			variables.put("HEDAN",compSize+"/"+hedan);
			
			variables.put("WORKLOCATION",retjisDetaillist.get(0).get("WORKING_LOCATION"));
			variables.put("LINE_CATEGORY",retjisDetaillist.get(0).get("LINE_CATEGORY"));
			variables.put("WAITING_LOCATION",retjisDetaillist.get(0).get("WAITING_LOCATION"));
			variables.put("ELEVATOR_CODE",retjisDetaillist.get(0).get("ELEVATOR_CODE"));
			
			Map<String, Object> corewhbinMap=new HashMap<String, Object>();
			corewhbinMap.put("WH_NUMBER", retjisDetaillist.get(0).get("FROM_PLANT_CODE"));//仓库号暂时取供应工厂
			corewhbinMap.put("BIN_TYPE", "01");
			List<Map<String,Object>> bin_code_list=dispatchingJISBillPickingDAO.selectCoreWhBin(corewhbinMap);
			if(bin_code_list!=null&&bin_code_list.size()>0){
				variables.put("JCQ",bin_code_list.get(0).get("BIN_NAME"));
			}else{
				variables.put("JCQ","");
			}
			variables.put("PICKING_USER_ID",retjisDetaillist.get(0).get("PICKING_USER_ID"));
			
			//
			Map<String,Object> assemblyMap=new HashMap<String,Object>();
			assemblyMap.put("F_WERKS", retjisDetaillist.get(0).get("FROM_PLANT_CODE"));
			assemblyMap.put("SORT_NUM", retjisDetaillist.get(0).get("SORT_TYPE"));
			List<Map<String, Object>> sortTypeList=disPatchingJISService.selectAssemblySortType(assemblyMap);
			if(sortTypeList!=null&&sortTypeList.size()>0){
				variables.put("SORT_TYPE_NAME", sortTypeList.get(0).get("JIS_SORT_TYPE").toString()+"-"+retjisDetaillist.get(0).get("JIS_SERIAL_NUMBER"));
			}
	    	
			List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
        	for(int i=0;i<retjisDetaillist.size();i++){
        		Map<String,Object> item = new HashMap<>();
        		item.put("ITEM_NO",retjisDetaillist.get(i).get("ITEM_NO"));
        		item.put("JIS_SEQUENCE",retjisDetaillist.get(i).get("JIS_SEQUENCE")==null?"":retjisDetaillist.get(i).get("JIS_SEQUENCE").toString());//
        		if(retjisDetaillist.get(i).get("UNIQUE_IDENTIFY_CODE")!=null){
        			item.put("VIN",retjisDetaillist.get(i).get("UNIQUE_IDENTIFY_CODE").toString().substring(retjisDetaillist.get(i).get("UNIQUE_IDENTIFY_CODE").toString().length()-8, retjisDetaillist.get(i).get("UNIQUE_IDENTIFY_CODE").toString().length()));
        		}else{
        			item.put("VIN","");
        		}
        		item.put("CAR_SERIES",retjisDetaillist.get(i).get("CAR_SERIES")==null?"":retjisDetaillist.get(i).get("CAR_SERIES").toString());
        		
        		if(retjisDetaillist.get(i).get("CAR_MODEL")!=null){
        			item.put("CAR_MODEL",retjisDetaillist.get(i).get("CAR_MODEL").toString().substring(retjisDetaillist.get(i).get("CAR_MODEL").toString().indexOf("(")+1, retjisDetaillist.get(i).get("CAR_MODEL").toString().indexOf(")")));
        		}else{
        			item.put("CAR_MODEL","");
        		}
        		item.put("MATERIAL_CODE",retjisDetaillist.get(i).get("MATERIAL_CODE"));
        		item.put("MATERIAL_DESC",retjisDetaillist.get(i).get("MATERIAL_DESC"));
        		item.put("QUANTITY",retjisDetaillist.get(i).get("QUANTITY"));
        		item.put("VENDOR_NAME",retjisDetaillist.get(i).get("VENDOR_NAME")==null?"":retjisDetaillist.get(i).get("VENDOR_NAME").toString());
        		item.put("PICKING_ADDRESS",retjisDetaillist.get(i).get("PICKING_ADDRESS")==null?"":retjisDetaillist.get(i).get("PICKING_ADDRESS").toString());
        		item.put("REMARK",retjisDetaillist.get(i).get("REMARK")==null?"":retjisDetaillist.get(i).get("REMARK").toString());
        		itemList.add(item);
        	}
        	variables.put("itemList",itemList);
        	
        	//取最早的HANDOVER_DATE,已经排序了的，第一条是最早的交接时间
			if(retjisDetaillist.get(0).get("HANDOVER_DATE")!=null&&!"".equals(retjisDetaillist.get(0).get("HANDOVER_DATE"))){
				String handover_date_str=retjisDetaillist.get(0).get("HANDOVER_DATE").toString();
				variables.put("HANDOVER_DATE",handover_date_str.substring(0, 10));
				variables.put("HANDOVER_TIME",handover_date_str.substring(10,handover_date_str.length()));
			}else{
				variables.put("HANDOVER_DATE","");
				variables.put("HANDOVER_TIME","");
			}
        	//生成二维码
		        try {
		        	String picturePath = ""; //图片路径
		        	StringBuffer sb = new StringBuffer();
		        	Map<String,Object> m=retjisDetaillist.get(0);
		        	String dispatcing_no=m.get("DISPATCHING_NO")==null?"":m.get("DISPATCHING_NO").toString();
		        	String werks=m.get("FROM_PLANT_CODE")==null?"":m.get("FROM_PLANT_CODE").toString();
		        	
		        	sb.append("DISPATCHING_NO:"+dispatcing_no);
		        	sb.append(";WERKS:"+werks);
		        	
					picturePath = QRCodeUtil.encode(sb.toString(),retjisDetaillist.get(0).get("DISPATCHING_NO").toString(),"",baseDir,true);
					picturePath = picturePath.replaceAll("\\\\", "//");
					variables.put("barCode","file:"+picturePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
		      
          variables.put("contextPath",params.get("contextPath").toString());
        	
          listVars.add(variables);
		}
		
		
    	}
    	rtnMap.put("listVars", listVars);
    	return rtnMap;
    }
    
    /**
     * 仓库物流非jis标签打印
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ParseException 
     */
    @RequestMapping(value = "/dispatchingFeiJISPrintPreview")
    public Map<String,Object> dispatchingFeiJISPrintPreview(@RequestParam Map<String, Object> params) throws ParseException {
    	Map<String,Object> rtnMap = new HashMap<String,Object>();
    	//判断是否补打印
    	String buprint="";
    	if(params.get("BUPRINT")!=null){
    		buprint="补";
    	}
    	
    	// 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String,Object>> listVars = new ArrayList<>();
        
    	SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
    	if(params.get("BARCODE")!=null){
    		Map<String, Object> paramtemp=new HashMap<String, Object>();
    		String[] barcodeArray=params.get("BARCODE").toString().split(",");
    		paramtemp.put("barcodelist", barcodeArray);
    		
    		List<String> typelist=new ArrayList<String>();
    		typelist.add("01");
    		typelist.add("03");
    		
    		paramtemp.put("typelist", typelist);//
        	List<Map<String,Object>> retfeijisDetaillist=disPatchingJISService.selectDispatchingBillPickingByPrint(paramtemp);
        	
        	String baseDir = configConstant.getLocation()+"/barcode/";
        	
        	if(retfeijisDetaillist!=null&&retfeijisDetaillist.size()>0){
        		for(int i=0;i<retfeijisDetaillist.size();i++){
        			
        			Map<String,Object> variables = new HashMap<>();
        			variables.put("BUPRINT",buprint);//补打印
            		variables.put("DISPATCHING_ADDRESS",retfeijisDetaillist.get(i).get("DISPATCHING_ADDRESS")==null?"":retfeijisDetaillist.get(i).get("DISPATCHING_ADDRESS"));
            		variables.put("LINE_CATEGORY",retfeijisDetaillist.get(i).get("LINE_CATEGORY")==null?"":retfeijisDetaillist.get(i).get("LINE_CATEGORY"));
            		variables.put("WAITING_LOCATION",retfeijisDetaillist.get(i).get("WAITING_LOCATION")==null?"":retfeijisDetaillist.get(i).get("WAITING_LOCATION"));
            		variables.put("ELEVATOR_CODE",retfeijisDetaillist.get(i).get("ELEVATOR_CODE")==null?"":retfeijisDetaillist.get(i).get("ELEVATOR_CODE"));
            		variables.put("BARCODE",retfeijisDetaillist.get(i).get("BARCODE"));
            		variables.put("DISPATCHING_NO",retfeijisDetaillist.get(i).get("DISPATCHING_NO"));
            		variables.put("FROM_PLANT_CODE",retfeijisDetaillist.get(i).get("FROM_PLANT_CODE"));
            		variables.put("QUANTITY",retfeijisDetaillist.get(i).get("QUANTITY")==null?"0":retfeijisDetaillist.get(i).get("QUANTITY").toString());
            		variables.put("PACKAGE_QTY",retfeijisDetaillist.get(i).get("PACKAGE_QTY")==null?"":retfeijisDetaillist.get(i).get("PACKAGE_QTY").toString());
            		variables.put("VENDOR_NAME",retfeijisDetaillist.get(i).get("VENDOR_NAME")==null?"":retfeijisDetaillist.get(i).get("VENDOR_NAME").toString());
            		variables.put("PACKAGE_MODEL",retfeijisDetaillist.get(i).get("PACKAGE_MODEL")==null?"":retfeijisDetaillist.get(i).get("PACKAGE_MODEL").toString());
            		variables.put("BATCH",retfeijisDetaillist.get(i).get("BATCH")==null?"":retfeijisDetaillist.get(i).get("BATCH").toString());
            		variables.put("PICKING_ADDRESS",retfeijisDetaillist.get(i).get("PICKING_ADDRESS")==null?"":retfeijisDetaillist.get(i).get("PICKING_ADDRESS").toString());
            		
            		Map<String, Object> corewhbinMap=new HashMap<String, Object>();
        			corewhbinMap.put("WH_NUMBER", retfeijisDetaillist.get(i).get("FROM_PLANT_CODE"));//仓库号暂时取供应工厂
        			corewhbinMap.put("BIN_TYPE", "01");
        			List<Map<String,Object>> bin_code_list=dispatchingJISBillPickingDAO.selectCoreWhBin(corewhbinMap);
        			if(bin_code_list!=null&&bin_code_list.size()>0){
        				variables.put("JCQ",bin_code_list.get(0).get("BIN_NAME"));
        			}else{
        				variables.put("JCQ","");
        			}
        			variables.put("MATERIAL_CODE",retfeijisDetaillist.get(i).get("MATERIAL_CODE"));
        			variables.put("MATERIAL_DESC",retfeijisDetaillist.get(i).get("MATERIAL_DESC")==null?"":retfeijisDetaillist.get(i).get("MATERIAL_DESC").toString());
        			variables.put("PICKING_USER_NAME",retfeijisDetaillist.get(i).get("PICKING_USER_NAME")==null?"":retfeijisDetaillist.get(i).get("PICKING_USER_NAME").toString());
        			variables.put("REMARK",retfeijisDetaillist.get(i).get("REMARK")==null?"":retfeijisDetaillist.get(i).get("REMARK").toString());
        			variables.put("HANDOVER_DATE",retfeijisDetaillist.get(i).get("HANDOVER_DATE")==null?"":retfeijisDetaillist.get(i).get("HANDOVER_DATE"));
        			
        			//生成二维码
    		        try {
    		        	String picturePath = ""; //图片路径
    		        	StringBuffer sb = new StringBuffer();
    		        	Map<String, Object> m=retfeijisDetaillist.get(i);
    		        	String dispatching_no=m.get("DISPATCHING_NO")==null?"":m.get("DISPATCHING_NO").toString();
    		        	String barcode=m.get("BARCODE")==null?"":m.get("BARCODE").toString();
    		        	String batch=m.get("BATCH")==null?"":m.get("BATCH").toString();
    		        	String materialCode=m.get("MATERIAL_CODE")==null?"":m.get("MATERIAL_CODE").toString();
    		        	String materialDesc=m.get("MATERIAL_DESC")==null?"":m.get("MATERIAL_DESC").toString();
    		        	String qty=m.get("QUANTITY")==null?"":m.get("QUANTITY").toString();
    		        	
    		        	sb.append("DISPATCHING_NO:"+dispatching_no);
    		        	sb.append(";BARCODE:"+barcode);
    		        	sb.append(";BATCH:"+batch);
    		        	sb.append(";MATERIAL_CODE:"+materialCode);
    		        	sb.append(";MATERIAL_DESC:"+materialDesc);
    		        	sb.append(";QUANTITY:"+qty);
    		        	
    					picturePath = QRCodeUtil.encode(sb.toString(),retfeijisDetaillist.get(i).get("BARCODE").toString(),"",baseDir,true);
    					picturePath = picturePath.replaceAll("\\\\", "//");
    					variables.put("barCode","file:"+picturePath);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    		        
       	            variables.put("contextPath",params.get("contextPath").toString());
                    listVars.add(variables);
        		}
        	}
    	}
    	rtnMap.put("listVars", listVars);
    	return rtnMap;
    }
}
