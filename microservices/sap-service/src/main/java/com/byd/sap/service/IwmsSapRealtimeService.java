package com.byd.sap.service;

import java.util.Map;

/**
 * SAP实时过帐类接口
 */
/**
 * @author develop05
 *
 */
public interface IwmsSapRealtimeService {
	
	/** 货物移动过账  - 按采购订单收货过账
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 *  ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,BATCH,MOVE_TYPE,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,
	 *  	PO_NUMBER,PO_ITEM,ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB01(Map<String,Object> paramMap);
	
	/** 货物移动过账  - 按订单收货过账
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,BATCH,MOVE_TYPE,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,ORDERID,
	 *  	ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB02(Map<String,Object> paramMap);
	
	/** 生产订单-CO订单 内部订单投料
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,GR_RCPT,BATCH,MOVE_TYPE,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,ORDERID,
	 *  	ITEM_TEXT,MVT_IND,RESERV_NO,RES_ITEM<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB03(Map<String,Object> paramMap);
	
	/** 成本中心领料(201) 退料(202)
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,GR_RCPT,BATCH,MOVE_TYPE,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,COSTCENTER,
	 *  	ITEM_TEXT,MVT_IND,MOVE_REAS<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB03b(Map<String,Object> paramMap);
	
	/** WBS元素领料(221) 退料(222)
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,GR_RCPT,BATCH,MOVE_TYPE,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,COSTCENTER,
	 *  	ITEM_TEXT,MVT_IND,MOVE_REAS<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB03c(Map<String,Object> paramMap);
	
	/** 外部销售发货(251)
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,GR_RCPT,BATCH,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,CUSTOMER,
	 *  	ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB03_251(Map<String,Object> paramMap);
	
	/** 库存调拨
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,GR_RCPT,BATCH,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,CUSTOMER,
	 *  	ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB04(Map<String,Object> paramMap);
	
	/** 其它收货-半成品无生产订单(521)
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,BATCH,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,ORDERID,
	 *  	ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB05_521(Map<String,Object> paramMap);
	
	/** 其它收货-交接进仓-客供物料(501)
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,BATCH,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,ORDERID,
	 *  	ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB05_501(Map<String,Object> paramMap);
	
	/** 其它收货-交接进仓-免费品(511)
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,BATCH,VENDOR,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,ORDERID,
	 *  	ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB05_511(Map<String,Object> paramMap);
	
	/** 其它收货-交接进仓-备品(903)
	 * paramMap:<BR/>
	 * 	WERKS,PSTNG_DATE:凭证中的过账日期,	DOC_DATE:凭证中的凭证日期,HEADER_TXT:凭证抬头文本<BR/>
	 * 	ITEMLIST:List&lt;Map&lt;String,String>>:
	 *  	MATERIAL,PLANT,STGE_LOC,BATCH,VENDOR,SPEC_STOCK,ENTRY_QNT,ENTRY_UOM,
	 *  	ITEM_TEXT,MVT_IND<BR/>
	 *  return:CODE,MESSAGE,MATERIALDOCUMENT,MATDOCUMENTYEAR
	 */
	public Map<String,Object> sapGoodsmvtCreateMB05_903(Map<String,Object> paramMap);
	
	/** 交接过帐 通用接口
	 *  需手动配置 GM_CODE MOVE_TYPE
	 * @param paramMap
	 * @return
	 */
	public Map<String,Object> sapGoodsmvtCommonCreate(Map<String,Object> paramMap);
	
	/**
	 * 冲销货物移动凭证
	 * @return
	 */
	public Map<String,Object> sapGoodsmvtCancel(Map<String,Object> paramMap);
	
	/**
	 * TODO 交货单修改、简配
	 * @return
	 */
	public Map<String,Object> sapDeliveryChange(Map<String,Object> paramMap);
	
	/**
	 * TODO 交货单过账
	 * @return
	 */
	public Map<String,Object> sapDeliveryUpdate(Map<String,Object> paramMap);
}
