package com.byd.wms.business.modules.in.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.datasources.DataSourceNames;
import com.byd.utils.datasources.annotation.DataSource;
import com.byd.wms.business.modules.in.dao.SCMDeliveryDao;
import com.byd.wms.business.modules.in.entity.SCMDeliveryEntity;
import com.byd.wms.business.modules.in.entity.Wmin00an00Entity;
import com.byd.wms.business.modules.in.entity.Wmin00anEntity;
import com.byd.wms.business.modules.in.entity.Wmin00skEntity;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;

@Service
public class SCMDeliveryServiceImpl extends ServiceImpl<SCMDeliveryDao, SCMDeliveryEntity> implements SCMDeliveryService{
	
	/*@DataSource(name = DataSourceNames.SECOND)
	@Override
	public List<Map<String, Object>> querySCMDelivery(Map<String, Object> params){
		List<Map<String, Object>> rtn = new ArrayList<Map<String, Object>>();
		rtn = baseMapper.querySCMDelivery(params);
		return rtn;
		
	}*/

	@DataSource(name = DataSourceNames.SECOND)
	@Override
	/**
	 * 参数说明：PONO 送货单号 POITM送货单行项目 必须都传值
	 * 修改可送数量就改DELIVERYAMOUNT这个字段就行
	       初始值是0，退货就是0-退货数量，冲销就是DELIVERYAMOUNT-冲销数量
	 */
	public Map<String, Object> updateTPO(List<Map<String,Object>> params) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		for(int i=0;i<params.size();i++){
			if("".equals(params.get(i).get("PO_NO"))||"".equals(params.get(i).get("PO_ITEM_NO"))
					||params.get(i).get("PO_NO")==null||params.get(i).get("PO_ITEM_NO")==null){
				retMap.put("msg", "参数PO_NO和PO_ITEM_NO不能为空!");
				return retMap;
			}
		}
		int ret=baseMapper.updateTPO(params);
		if(ret>0){
			retMap.put("MSG", "success");
			retMap.put("updateCount", ret);
		}else{
			retMap.put("MSG", "更新失败！");
			retMap.put("updateCount", ret);
		}
		
		return retMap;
	}
	/*
	 * 参数说明  送货单号pono  行项目号poitm
	 * 
	 */
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public List<Map<String, Object>> querytpo(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return baseMapper.querytpo(params);
	}
	/**
	 * 参数说明 BARCODE 包装箱条码  state 状态
	 */
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public Map<String, Object> updateTDELIVERYDETAIL(List<Map<String,Object>> params) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		// barcode 为包装号
		for(int i=0;i<params.size();i++){
			if("".equals(params.get(i).get("BARCODE"))||params.get(i).get("BARCODE")==null){
				retMap.put("msg", "参数BARCODE不能为空!");
				return retMap;
			}
		}
		int ret=baseMapper.updateTDELIVERYDETAIL(params);
		
		if(ret>0){
			retMap.put("msg", "success");
			retMap.put("updateCount", ret);
		}else{
			retMap.put("msg", "更新失败！");
			retMap.put("updateCount", ret);
		}
		return retMap;
	}
	/**
	 * 参数说明  BARCODE：送货单号   it.ROWNO：行项目号  STATE 状态
	 * 所有行项目的状态为3 更新头表状态为3，否则头表状态为8
	 */
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public Map<String, Object> updateTDELIVERYROWITEM(List<Map<String,Object>> params) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		for(int i=0;i<params.size();i++){
			if("".equals(params.get(i).get("BARCODE"))||params.get(i).get("BARCODE")==null){
				retMap.put("msg", "参数BARCODE不能为空!");
				return retMap;
			}
		}
		int ret=baseMapper.updateTDELIVERYROWITEM(params);
		
		//更新头表状态
		Map<String, Object> asn00temp=new HashMap<String, Object>();
		for(int i=0;i<params.size();i++){
			asn00temp.put("ASNNO", params.get(i).get("BARCODE"));
			List<Map<String, Object>> rtn_ = baseMapper.queryWin00an00BySCM(asn00temp);//查询出行项目
			boolean issh=true;
			for(int j=0;j<rtn_.size();j++){
				String state=rtn_.get(j).get("STATE")==null?"":rtn_.get(j).get("STATE").toString();
				if(!"3".equals(state)){
					issh=false;
				}
			}
			
			if(issh){// 行项目状态都为 3，则更新头表状态为3
				List<Map<String,Object>> paramAsn=new ArrayList<Map<String,Object>>();
				for(int z=0;z<params.size();z++){
					Map<String, Object> asntemp=new HashMap<String, Object>();
					asntemp.put("BARCODE", params.get(z).get("BARCODE"));
					asntemp.put("STATE", "3");
					paramAsn.add(asntemp);
				}
				baseMapper.updateTDELIVERYNOTE(paramAsn);
			}else{// 则更新头表状态为8
				List<Map<String,Object>> paramAsn_=new ArrayList<Map<String,Object>>();
				for(int z=0;z<params.size();z++){
					Map<String, Object> asntemp_=new HashMap<String, Object>();
					asntemp_.put("BARCODE", params.get(z).get("BARCODE"));
					asntemp_.put("STATE", "8");
					paramAsn_.add(asntemp_);
				}
				baseMapper.updateTDELIVERYNOTE(paramAsn_);
			}
		}
		
		if(ret>0){
			retMap.put("msg", "success");
			retMap.put("updateCount", ret);
		}else{
			retMap.put("msg", "更新失败！");
			retMap.put("updateCount", ret);
		}
		return retMap;
	}
	/**
	 * 参数说明 barcode：送货单号  state：状态
	 */
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public Map<String, Object> updateTDELIVERYNOTE(List<Map<String,Object>> params) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		for(int i=0;i<params.size();i++){
			if("".equals(params.get(i).get("BARCODE"))||params.get(i).get("BARCODE")==null){
				retMap.put("msg", "参数BARCODE不能为空!");
				return retMap;
			}
		}
		int ret=baseMapper.updateTDELIVERYNOTE(params);
		
		if(ret>0){
			retMap.put("msg", "success");
			retMap.put("updateCount", ret);
		}else{
			retMap.put("msg", "更新失败！");
			retMap.put("updateCount", ret);
		}
		return retMap;
	}
	
	private static long parseTime(String strTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long time = 0L;
        try {
            time = format.parse(strTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
	
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public List<Map<String, Object>> getMatBarcodeList(Map<String, Object> params){
		List<Map<String, Object>> result = baseMapper.getMatBarcodeList(params);
		return result;
	}	
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public List<Map<String, Object>> getAllMatBarcodeList(Map<String, Object> params){
		List<Map<String, Object>> result = baseMapper.getAllMatBarcodeList(params);
		return result;
	}
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public List<Map<String, Object>> getScmBarCodeInfo(Map<String, Object> params){
		List<Map<String, Object>> result = baseMapper.getScmBarCodeInfo(params);
		return result;
	}
	@Override
	public List<Map<String, Object>> getAllMatBarcodeListBySapMatDocNo(Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> labelList = baseMapper.getLabelsBySapMetDocNo(params);
		if(labelList!=null && labelList.size()>0) {
			String labels = labelList.get(0).get("LABEL_NOS").toString();
			for (String label: labels.split(",")){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("BARCODE", label);
				map.put("LABEL_NO", label);
				if(params.get("WERKS")!=null) {
					map.put("WERKS", params.get("WERKS")==null?null:params.get("WERKS").toString());
				}
				
				result.add(map);
			}
		}
		return result;
	}
	
	/**
	 * 参数说明 asnno 送货单号
	 */
	@Override
	@DataSource(name = DataSourceNames.SECOND)
	public Map<String, Object> querySCMDelivery(Map<String, Object> params) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		if("".equals(params.get("ASNNO"))||params.get("ASNNO")==null){
			retMap.put("MSG", "送货单号不能为空！");
			return retMap;
		}
		//头表
		List<Map<String, Object>> rtn = baseMapper.queryWin00anBySCM(params);
		Wmin00anEntity wmin00an=new Wmin00anEntity();
		if(rtn!=null&&rtn.size()>0){
			wmin00an.setASNNO(rtn.get(0).get("ASNNO")==null?"":rtn.get(0).get("ASNNO").toString());
			wmin00an.setST(rtn.get(0).get("ST")==null?"":rtn.get(0).get("ST").toString());
			wmin00an.setETD(rtn.get(0).get("ETD")==null?"":rtn.get(0).get("ETD").toString());
			wmin00an.setETA(rtn.get(0).get("ETA")==null?"":rtn.get(0).get("ETA").toString());
			wmin00an.setLIFNR(rtn.get(0).get("VDCD")==null?"":rtn.get(0).get("VDCD").toString());
			wmin00an.setLIKTX(rtn.get(0).get("VDNM")==null?"":rtn.get(0).get("VDNM").toString());
			wmin00an.setASNTP(rtn.get(0).get("ASNTP")==null?"":rtn.get(0).get("ASNTP").toString());
			wmin00an.setCNUID(rtn.get(0).get("CNUID")==null?"":rtn.get(0).get("CNUID").toString());
			wmin00an.setCNUPH(rtn.get(0).get("CNUPH")==null?"":rtn.get(0).get("CNUPH").toString());
			wmin00an.setWH_NUMBER(rtn.get(0).get("WHNO")==null?"":rtn.get(0).get("WHNO").toString());
			wmin00an.setWERKS(rtn.get(0).get("PLCD")==null?"":rtn.get(0).get("PLCD").toString());
			wmin00an.setLGORT(rtn.get(0).get("LOCD")==null?"":rtn.get(0).get("LOCD").toString());
			wmin00an.setKITTINGFLAG(rtn.get(0).get("KITTINGFLAG")==null?"":rtn.get(0).get("KITTINGFLAG").toString());
			
			wmin00an.setMSG("success");
		}
		retMap.put("wmin00an", wmin00an);
		//行表
		List<Wmin00an00Entity> wmin00an00list=new ArrayList<Wmin00an00Entity>();
		List<Map<String, Object>> rtn00 = baseMapper.queryWin00an00BySCM(params);
		if(rtn00!=null&&rtn00.size()>0){
			for(int i=0;i<rtn00.size();i++){
				Wmin00an00Entity wmin00an00=new Wmin00an00Entity();
				wmin00an00.setASNNO(rtn00.get(i).get("ASNNO")==null?"":rtn00.get(i).get("ASNNO").toString());//testflag
				wmin00an00.setASNITM(rtn00.get(i).get("ASNITM")==null?"":rtn00.get(i).get("ASNITM").toString());
				wmin00an00.setWERKS(rtn00.get(i).get("PLCD")==null?"":rtn00.get(i).get("PLCD").toString());
				wmin00an00.setLGORT(rtn00.get(i).get("LOCD")==null?"":rtn00.get(i).get("LOCD").toString());
				wmin00an00.setMATNR(rtn00.get(i).get("MACD")==null?"":rtn00.get(i).get("MACD").toString());
				wmin00an00.setMAKTX(rtn00.get(i).get("MADS")==null?"":rtn00.get(i).get("MADS").toString());
				wmin00an00.setLIFNR(rtn00.get(i).get("VDCD")==null?"":rtn00.get(i).get("VDCD").toString());
				wmin00an00.setLIKTX(rtn00.get(i).get("VDNM")==null?"":rtn00.get(i).get("VDNM").toString());
				wmin00an00.setQTY(rtn00.get(i).get("QTY")==null?BigDecimal.ZERO:new BigDecimal(rtn00.get(i).get("QTY").toString()));
				wmin00an00.setUNIT(rtn00.get(i).get("UNIT")==null?"":rtn00.get(i).get("UNIT").toString());
				wmin00an00.setBOX_COUNT(rtn00.get(i).get("BXCNT")==null?BigDecimal.ZERO:new BigDecimal(rtn00.get(i).get("BXCNT").toString()));
				wmin00an00.setPONO(rtn00.get(i).get("PONO")==null?"":rtn00.get(i).get("PONO").toString());
				wmin00an00.setPOITM(rtn00.get(i).get("POITM")==null?"":rtn00.get(i).get("POITM").toString());
				wmin00an00.setTESTFLAG(rtn00.get(i).get("TESTFLAG")==null?"":rtn00.get(i).get("TESTFLAG").toString());
				wmin00an00.setSPEC(rtn00.get(i).get("SPEC")==null?BigDecimal.ZERO:new BigDecimal(rtn00.get(i).get("SPEC").toString()));
				wmin00an00.setMSG("success");
				
				wmin00an00list.add(wmin00an00);
			}
		}
		retMap.put("wmin00an00list", wmin00an00list);
		//标签信息
		List<Wmin00skEntity> wmin00sklist=new ArrayList<Wmin00skEntity>();
		List<Map<String, Object>> rtn00sk = baseMapper.queryWin00skBySCM(params);
		if(rtn00sk!=null&&rtn00sk.size()>0){
			for(int i=0;i<rtn00sk.size();i++){
				Wmin00skEntity wmin00sk=new Wmin00skEntity();
				wmin00sk.setSKUID(rtn00sk.get(i).get("SKUID")==null?"":rtn00sk.get(i).get("SKUID").toString());
				wmin00sk.setLIFNR(rtn00sk.get(i).get("VDCD")==null?"":rtn00sk.get(i).get("VDCD").toString());//unit
				wmin00sk.setLIKTX(rtn00sk.get(i).get("VDNM")==null?"":rtn00sk.get(i).get("VDNM").toString());
				wmin00sk.setWERKS(rtn00sk.get(i).get("PLCD")==null?"":rtn00sk.get(i).get("PLCD").toString());
				wmin00sk.setMATNR(rtn00sk.get(i).get("MACD")==null?"":rtn00sk.get(i).get("MACD").toString());
				wmin00sk.setMAKTX(rtn00sk.get(i).get("MADS")==null?"":rtn00sk.get(i).get("MADS").toString());
				wmin00sk.setBATCH(rtn00sk.get(i).get("BATCH")==null?"":rtn00sk.get(i).get("BATCH").toString());
				wmin00sk.setUNIT(rtn00sk.get(i).get("UNIT")==null?"":rtn00sk.get(i).get("UNIT").toString());
				wmin00sk.setPO_NO(rtn00sk.get(i).get("PO_NO")==null?"":rtn00sk.get(i).get("PO_NO").toString());
				wmin00sk.setPO_ITEM_NO(rtn00sk.get(i).get("PO_ITEM_NO")==null?"":rtn00sk.get(i).get("PO_ITEM_NO").toString());
				if(rtn00sk.get(i).get("PRDDT")!=null){
				 long prddt_long=parseTime(rtn00sk.get(i).get("PRDDT").toString());
				 SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd");
				 wmin00sk.setPRDDT(aDate.format(prddt_long));
				}else{
					wmin00sk.setPRDDT("");
				}
				 
				wmin00sk.setBXIDX(rtn00sk.get(i).get("BXIDX")==null?"":rtn00sk.get(i).get("BXIDX").toString());
				wmin00sk.setASNNO(rtn00sk.get(i).get("ASNNO")==null?"":rtn00sk.get(i).get("ASNNO").toString());
				wmin00sk.setASNITM(rtn00sk.get(i).get("ASNITM")==null?"":rtn00sk.get(i).get("ASNITM").toString());
				
				wmin00sk.setBOX_QTY(rtn00sk.get(i).get("BOXQTY")==null?BigDecimal.ZERO:new BigDecimal(rtn00sk.get(i).get("BOXQTY").toString()));
				wmin00sk.setFULL_BOX_QTY(rtn00sk.get(i).get("FULLBOXQTY")==null?BigDecimal.ZERO:new BigDecimal(rtn00sk.get(i).get("FULLBOXQTY").toString()));
				
				if(rtn00sk.get(i).get("VALIDDATE")!=null){ //validdate
					 long prddt_long=parseTime(rtn00sk.get(i).get("VALIDDATE").toString());
					 SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd");
					 wmin00sk.setValiddate(aDate.format(prddt_long));
				}else{
					wmin00sk.setValiddate("");
				}
				
				wmin00sk.setMSG("success");
				
				wmin00sklist.add(wmin00sk);
			}
		}
		retMap.put("wmin00sklist", wmin00sklist);
		retMap.put("MSG", "success");
		return retMap;
	}



}
