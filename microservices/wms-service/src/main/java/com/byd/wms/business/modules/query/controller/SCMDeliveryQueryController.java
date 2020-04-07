package com.byd.wms.business.modules.query.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import com.byd.wms.business.modules.query.service.SCMDeliveryQueryService;
import com.byd.wms.business.modules.query.service.WmsInReceiptQueryService;


/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年11月21日 上午9:25:31 
 * 类说明 
 */
@RestController
@RequestMapping("query/scmquery")
public class SCMDeliveryQueryController {
	@Autowired
    private SCMDeliveryQueryService scmDeliveryQueryService;
	@Autowired
    private WmsInReceiptService wmsInReceiptService;
	@Autowired
	WmsInReceiptQueryService wmsInReceiptQueryService;
	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = scmDeliveryQueryService.queryPage(params);
        return R.ok().put("page", page);
	}
	
	@RequestMapping("/listitem")
	public R listitem(@RequestParam Map<String, Object> params){
		PageUtils page = scmDeliveryQueryService.queryPageDetail(params);
		List<Map<String, Object>> retlist=(List<Map<String, Object>>) page.getList();
		if(retlist!=null&&retlist.size()>0){
			for(int n=0;n<retlist.size();n++){
				//获取已收货数量
				if(retlist.get(n).get("ASNNO")!=null&&retlist.get(n).get("ASNITM")!=null){
					Map<String, Object> p=new HashMap<String, Object>();
					p.put("ASNNO", retlist.get(n).get("ASNNO"));
					p.put("ASNITM", retlist.get(n).get("ASNITM").toString());
					List<Map<String, Object>> retReceipt_qty=scmDeliveryQueryService.getHasReceiveQty(p);
					if(retReceipt_qty!=null&&retReceipt_qty.size()>0){
						retlist.get(n).put("HAS_RECEIPT_QTY", retReceipt_qty.get(0).get("HAS_RECEIPT_QTY"));
					}
				}
				//获取需求跟踪号
				
				if(retlist.get(n).get("PONO")!=null&&retlist.get(n).get("POITM")!=null){
					Map<String, Object> bednrmap=new HashMap<String, Object>();
					bednrmap.put("PO_NO", retlist.get(n).get("PONO").toString());
					bednrmap.put("PO_ITEM_NO", retlist.get(n).get("POITM").toString());
					List<Map<String, Object>> retbednrlist=wmsInReceiptQueryService.getPOITEMBednr(bednrmap);
					if(retbednrlist!=null&&retbednrlist.size()>0){
						retlist.get(n).put("BEDNR", retbednrlist.get(0).get("BEDNR"));
					}
				}
			}
		}
		return R.ok().put("page", page);
	}
	/**
	 * 关闭送货单
	 * @param params
	 * @return
	 */
	@RequestMapping("/update")
	public R updateState(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> updateDateList=new ArrayList<Map<String, Object>>();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){
			Map<String, Object> tempMap=new HashMap<String, Object>();
			JSONObject itemData=  jarr.getJSONObject(i);
			tempMap.put("ASNNO", itemData.getString("ASNNO"));
			updateDateList.add(tempMap);
		}
		//不能在一个事务里面 一起执行
		scmDeliveryQueryService.updateHEAD(updateDateList);
		scmDeliveryQueryService.updateITEM(updateDateList);
		scmDeliveryQueryService.updateDETAIL(updateDateList);
		
		//前台送货单查询出来的 明细,通过采购订单号和行项目号更新数量
		List<Map<String, Object>> retdetaillist=scmDeliveryQueryService.queryAllItemBySCM(updateDateList);
		
		List<Map<String, Object>> updateqtyMapList=new ArrayList<Map<String, Object>>();
		if(retdetaillist!=null&retdetaillist.size()>0){
			for(Map<String, Object> detailMap:retdetaillist){
				Map<String, Object> updateqtyMap=new HashMap<String, Object>();
				
				if(detailMap.get("ASNNO")!=null&&detailMap.get("ASNITM")!=null){
					BigDecimal hasReciveQty=BigDecimal.ZERO;
					BigDecimal onwayQty=BigDecimal.ZERO;
					
					Map<String, Object> p=new HashMap<String, Object>();
					p.put("ASNNO", detailMap.get("ASNNO").toString());
					p.put("ASNITM", detailMap.get("ASNITM").toString());
					List<Map<String, Object>> retReceipt_qty=scmDeliveryQueryService.getHasReceiveQty(p);
					if(retReceipt_qty!=null&&retReceipt_qty.size()>0){
						hasReciveQty=retReceipt_qty.get(0).get("HAS_RECEIPT_QTY")==null?BigDecimal.ZERO:new BigDecimal(retReceipt_qty.get(0).get("HAS_RECEIPT_QTY").toString());

					}
					onwayQty=detailMap.get("QTY")==null?BigDecimal.ZERO:new BigDecimal(detailMap.get("QTY").toString());
					updateqtyMap.put("ONWAYQTY", onwayQty.subtract(hasReciveQty));
					
					updateqtyMap.put("PO_NO", detailMap.get("PONO").toString());
					updateqtyMap.put("PO_ITEM_NO", detailMap.get("POITM").toString());
				}
				updateqtyMapList.add(updateqtyMap);
			}
			
			scmDeliveryQueryService.updateTPO_onWayAmount(updateqtyMapList);
		}
        return R.ok();
	}
}
