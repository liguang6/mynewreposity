package com.byd.wms.business.modules.cswlms.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tempuri.ArrayOfWSWMSDemand;
import org.tempuri.ArrayOfWSWMSLogisMvt;
import org.tempuri.WSWMSDemand;
import org.tempuri.WSWMSDispatchingList;
import org.tempuri.WSWMSList;
import org.tempuri.WSWMSLogisMvt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.cswlms.service.DisPatchingJISBillPickingService;
import com.byd.wms.business.modules.in.utils.CompareObject;

/** 
 * @author 作者 E-mail: peng.tao1
 * @version 创建时间：2019年1月19日 上午9:54:43 
 * 类说明 
 */
@RestController
@RequestMapping("/cswlms/dispatchingBillPicking")
public class DispatchingBillPickingController {
	@Autowired
	DisPatchingJISBillPickingService disPatchingJISService;
	@Autowired
    WmsCPlantService wmsCPlantService;
	@Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/listJIS")
    public R listJIS(@RequestParam Map<String, Object> params){
		List<String> fromPlantCodelist=new ArrayList<String>();
		if("".equals(params.get("FROM_PLANT_CODE"))){//全部工厂
			Set<Map<String,Object>> werksSet=userUtils.getUserWerks("WLMS_JIS");
			if(!werksSet.isEmpty()){
				for(Map<String,Object> maptemp:werksSet){
					fromPlantCodelist.add(maptemp.get("WERKS").toString());
				}
				
				params.put("fromPlantCodelist", fromPlantCodelist);
			}
		}else{
			fromPlantCodelist.add(params.get("FROM_PLANT_CODE").toString());
			params.put("fromPlantCodelist", fromPlantCodelist);
		}
    	List<Map<String, Object>> retjislist = new ArrayList<Map<String, Object>>();
    	retjislist=disPatchingJISService.selectDispatchingJISBillPicking(params);
    	
    	List<Map> items = new ArrayList<Map>();
    	List<Map<String, Object>> details =new ArrayList<Map<String, Object>>();
    	Map map = null;
        Map<String, Map> _dispatching_mapping = new HashMap<String, Map>();
        for (Map<String, Object> retmap : retjislist) {
        	//获取排序类别名称
        	Map<String, Object> sortMap=new HashMap<String, Object>();
        	sortMap.put("F_WERKS", retmap.get("FROM_PLANT_CODE"));
        	sortMap.put("SORT_NUM", retmap.get("SORT_TYPE"));
        	List<Map<String, Object>> sortTypeList=disPatchingJISService.selectAssemblySortType(sortMap);
        	if(sortTypeList!=null&&sortTypeList.size()>0){
        		retmap.put("SORT_TYPE_NAME", sortTypeList.get(0).get("JIS_SORT_TYPE"));
        	}
        	
        	if (!_dispatching_mapping.containsKey(retmap.get("DISPATCHING_NO"))) {//一个dispatching_no保存一个map值
        		map = new HashMap();
        		map.put("FROM_PLANT_CODE", retmap.get("FROM_PLANT_CODE"));
        		map.put("PLANT_CODE", retmap.get("PLANT_CODE"));
        		map.put("STATUS", retmap.get("STATUS"));
        		map.put("CAR_SERIES", retmap.get("CAR_SERIES"));
        		map.put("CAR_MODEL", retmap.get("CAR_MODEL"));
        		map.put("JIS_SERIAL_NUMBER", retmap.get("JIS_SERIAL_NUMBER"));
        		map.put("BATCH", retmap.get("BATCH"));
        		map.put("LEFT_PRINT_TIMES", retmap.get("LEFT_PRINT_TIMES"));
        		map.put("HANDOVER_DATE", retmap.get("HANDOVER_DATE"));
        		map.put("SORT_TYPE", retmap.get("SORT_TYPE"));
        		map.put("MATERIAL_CODE", retmap.get("MATERIAL_CODE"));
        		map.put("MATERIAL_DESC", retmap.get("MATERIAL_DESC"));
        		map.put("DISPATCHING_NO", retmap.get("DISPATCHING_NO"));
        		map.put("VENDOR_NAME", retmap.get("VENDOR_NAME"));
        		map.put("QUANTITY", retmap.get("QUANTITY"));
        		map.put("TYPE", retmap.get("TYPE"));
        		map.put("LINE_CATEGORY", retmap.get("LINE_CATEGORY"));
        		map.put("SORT_TYPE_NAME", retmap.get("SORT_TYPE_NAME"));
        		
        		details =new ArrayList<Map<String, Object>>();
        		details.add(retmap);
        		map.put("details", details);
        		_dispatching_mapping.put(retmap.get("DISPATCHING_NO").toString(), map);
        	}else {
                List<Map<String, Object>> mapdetails = (List<Map<String, Object>>) _dispatching_mapping.get(retmap.get("DISPATCHING_NO")).get("details");
                if (mapdetails != null) {
                    mapdetails.add(retmap);
                }
            }
        	
        }
        
        Collection<Map> maps = _dispatching_mapping.values();
        Iterator<Map> it = maps.iterator();
        Map tmpMap = null;
        while (it.hasNext()) {
        	tmpMap = it.next();
        	List<Map<String, Object>> mapdetails=(List<Map<String, Object>>) tmpMap.get("details");
        	Collections.sort(mapdetails, new CompareObject<Map<String, Object>>().addProperty("JIS_SEQUENCE").addProperty("MATERIAL_DESC"));
        	items.add(tmpMap);
        }
        
        Collections.sort(items, new Comparator<Map>() {

            public int compare(Map o1, Map o2) {

                String sortTypeA = (String) o1.get("SORT_TYPE");
                String sortTypeB = (String) o2.get("SORT_TYPE");
                if (sortTypeA.compareTo(sortTypeB) == 0) {
                    if ((o2.get("JIS_SERIAL_NUMBER") != "") && (o1.get("JIS_SERIAL_NUMBER") != "")) {
                        int jisSerialNumberA = Integer.parseInt((String) o1.get("JIS_SERIAL_NUMBER"));
                        int jisSerialNumberB = Integer.parseInt((String) o2.get("JIS_SERIAL_NUMBER"));
                        return jisSerialNumberA - jisSerialNumberB;
                    } else {
                        BigDecimal time1 = (BigDecimal) o1.get("LEFT_PRINT_TIMES");
                        BigDecimal time2 = (BigDecimal) o2.get("LEFT_PRINT_TIMES");
                        return time1.compareTo(time2);
                    }
                }
                return sortTypeA.compareTo(sortTypeB);
            }

        });
        return R.ok().put("result", items);
    }
	
	@RequestMapping("/listJISDetail")
    public R listJISDetail(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> retjisDetaillist = new ArrayList<Map<String, Object>>();
		
		retjisDetaillist=disPatchingJISService.selectDispatchingJISBillPicking(params);
		Collections.sort(retjisDetaillist, new CompareObject<Map<String, Object>>().addProperty("JIS_SEQUENCE").addProperty("MATERIAL_DESC"));
		Collections.sort(retjisDetaillist, new Comparator<Map>() {

            public int compare(Map o1, Map o2) {

                String sortTypeA = (String) o1.get("SORT_TYPE");
                String sortTypeB = (String) o2.get("SORT_TYPE");
                if (sortTypeA.compareTo(sortTypeB) == 0) {
                    if ((o2.get("JIS_SEQUENCE") != "") && (o1.get("JIS_SEQUENCE") != "")) {
                        int jisSerialNumberA = Integer.parseInt(o1.get("JIS_SEQUENCE")==null?"0":o1.get("JIS_SEQUENCE").toString());
                        int jisSerialNumberB = Integer.parseInt(o2.get("JIS_SEQUENCE")==null?"0":o2.get("JIS_SEQUENCE").toString());
                        return jisSerialNumberA - jisSerialNumberB;
                    } else {
                        BigDecimal time1 = (BigDecimal) o1.get("LEFT_PRINT_TIMES");
                        BigDecimal time2 = (BigDecimal) o2.get("LEFT_PRINT_TIMES");
                        return time1.compareTo(time2);
                    }
                }
                return sortTypeA.compareTo(sortTypeB);
            }

        });
		return R.ok().put("result", retjisDetaillist);
	}
	
	@RequestMapping("/listAssembly")
    public R listAssembly(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    	data=disPatchingJISService.selectAssemblySortType(params);
        return R.ok().put("data", data);
    }
	/**
	 * jis拣配下架
	 * @param params
	 * @return
	 */
	@RequestMapping("/picking")
    public R picking(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
	   	params.put("USERNAME", currentUser.get("USERNAME"));
	   	params.put("FULL_NAME", currentUser.get("FULL_NAME"));
	   	params.put("USERID", currentUser.get("USER_ID"));
		//判断是否上wms,如果已经上了wms，判断状态是否是01
		List<String> werks_f_list=new ArrayList<String>();//保存供应工厂
		
		List<String> dispatchingNoList=new ArrayList<String>();
    	JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
    	for(int m=0;m<jarr.size();m++){
			JSONObject itemData=  jarr.getJSONObject(m);
			dispatchingNoList.add(itemData.getString("DISPATCHING_NO").toString());//
		}
    	Map<String, Object> tempMap=new HashMap<String, Object>();
    	tempMap.put("DISPATCHINGNOList", dispatchingNoList);
    	List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		retList=disPatchingJISService.selectDispatchingJISBillPickingByDispatcingNo(tempMap);//通过拣配单号查询,相当于明细,待拣配的记录 
		
		for(int k=0;k<retList.size();k++){
			retList.get(k).put("USERNAME", params.get("USERNAME"));
			retList.get(k).put("USERID", params.get("USERID"));
			String WERKS_F=retList.get(k).get("FROM_PLANT_CODE")==null?"":retList.get(k).get("FROM_PLANT_CODE").toString();//
			werks_f_list.add(WERKS_F);
		}
		//删除供应工厂集合中重复的元素
		for(int i=0;i<werks_f_list.size()-1;i++ ){       
		      for ( int j=werks_f_list.size()-1;j>i;j--){       
		           if  (werks_f_list.get(j).equals(werks_f_list.get(i)))  {       
		        	   werks_f_list.remove(j);       
		            }        
		        }        
		    }   
		try {
		//判断是否上wms
		Map<String, Object> logortMap=new HashMap<String, Object>();//保存供应工厂对应的发货库位
		for(int n=0;n<werks_f_list.size();n++){
			Map<String, Object> logisticsMap=new HashMap<String, Object>();
			logisticsMap.put("WERKS_F", werks_f_list.get(n));
			List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
			if(assemblyLogistics!=null&&assemblyLogistics.size()>0){
				String wms_flag_f=assemblyLogistics.get(0).get("WMS_FLAG_F")==null?"":assemblyLogistics.get(0).get("WMS_FLAG_F").toString();
				if("0".equals(wms_flag_f)){
					throw new RuntimeException("该"+werks_f_list.get(n)+"工厂未启用该功能!");
				}
				logortMap.put(werks_f_list.get(n), assemblyLogistics.get(0).get("LGORT_F")==null?"":assemblyLogistics.get(0).get("LGORT_F").toString());
			}else{
				throw new RuntimeException("该"+werks_f_list.get(n)+"工厂未配置总装物流参数!");
			}
		}
		//判断状态是否 01
		for(int m=0;m<retList.size();m++){
			String status=retList.get(m).get("STATUS")==null?"":retList.get(m).get("STATUS").toString();//
			String werks_f=retList.get(m).get("FROM_PLANT_CODE")==null?"":retList.get(m).get("FROM_PLANT_CODE").toString();//取出当前行的供应工厂
			if("01".equals(status)){//设置发货工厂的发货库位
				if(logortMap.get(werks_f)!=null){//获取供应工厂对应的发货库位
					retList.get(m).put("LGORT_F", logortMap.get(werks_f));
				}
				
			}else{
				throw new RuntimeException("单号"+retList.get(m).get("DISPATCHING_NO")+"  ,行项目 "+retList.get(m).get("ITEM_NO")+" ,组件号"+retList.get(m).get("COMPONENT_NO")+" 的状态，不能进行拣配下架");
			}
		}
		disPatchingJISService.JISPicking(retList);
		return R.ok();
		}catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
    }
	
	@RequestMapping("/listFeiJIS")
    public R listFeiJIS(@RequestParam Map<String, Object> params){
		List<String> fromPlantCodelist=new ArrayList<String>();
		if("".equals(params.get("FROM_PLANT_CODE"))){//全部工厂
			Set<Map<String,Object>> werksSet=userUtils.getUserWerks("WLMS_JIS");
			if(!werksSet.isEmpty()){
				for(Map<String,Object> maptemp:werksSet){
					fromPlantCodelist.add(maptemp.get("WERKS").toString());
				}
				
				params.put("fromPlantCodelist", fromPlantCodelist);
			}
		}else{
			fromPlantCodelist.add(params.get("FROM_PLANT_CODE").toString());
			params.put("fromPlantCodelist", fromPlantCodelist);
		}
		
		List<Map<String, Object>> retfeijislist = new ArrayList<Map<String, Object>>();
		retfeijislist=disPatchingJISService.selectDispatchingFeiJISBillPicking(params);
		return R.ok().put("result", retfeijislist);
	}
	
	/**
	 * 非jis拣配下架
	 * @param params
	 * @return
	 */
	@RequestMapping("/feijispicking")
    public R feijispicking(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
        params.put("USERNAME", currentUser.get("USERNAME"));
        params.put("FULL_NAME", currentUser.get("FULL_NAME"));
        params.put("USERID", currentUser.get("USER_ID"));
		//判断是否上wms,如果已经上了wms，判断状态是否是01
		try{
		List<String> werks_f_list=new ArrayList<String>();//保存供应工厂
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int m=0;m<jarr.size();m++){
			JSONObject itemData=  jarr.getJSONObject(m);
			String WERKS_F=itemData.getString("FROM_PLANT_CODE")==null?"":itemData.getString("FROM_PLANT_CODE").toString();//
			werks_f_list.add(WERKS_F);
			
		}
		//判断是否上wms
				Map<String, Object> logortMap=new HashMap<String, Object>();//保存供应工厂对应的发货库位
				for(int n=0;n<werks_f_list.size();n++){
					Map<String, Object> logisticsMap=new HashMap<String, Object>();
					logisticsMap.put("WERKS_F", werks_f_list.get(n));
					List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
					if(assemblyLogistics!=null&&assemblyLogistics.size()>0){
						String wms_flag_f=assemblyLogistics.get(0).get("WMS_FLAG_F")==null?"":assemblyLogistics.get(0).get("WMS_FLAG_F").toString();
						if("0".equals(wms_flag_f)){
							throw new RuntimeException("该"+werks_f_list.get(n)+"工厂未启用该功能!");
						}
						logortMap.put(werks_f_list.get(n), assemblyLogistics.get(0).get("LGORT_F")==null?"":assemblyLogistics.get(0).get("LGORT_F").toString());
					}else{
						throw new RuntimeException("该"+werks_f_list.get(n)+"工厂未配置总装物流参数!");
					}
				}
				
				//判断状态是否 01
				for(int m=0;m<jarr.size();m++){
					JSONObject itemData=jarr.getJSONObject(m);
					String status=itemData.getString("STATUS")==null?"":itemData.getString("STATUS").toString();//
					String werks_f=itemData.getString("FROM_PLANT_CODE")==null?"":itemData.getString("FROM_PLANT_CODE").toString();//取出当前行的供应工厂
					if("01".equals(status)){//设置发货工厂的发货库位
						if(logortMap.get(werks_f)!=null){//获取供应工厂对应的发货库位
							itemData.put("LGORT_F", logortMap.get(werks_f));
						}
						
					}else{
						throw new RuntimeException("单号"+itemData.getString("DISPATCHING_NO")+"  ,行项目 "+itemData.getString("ITEM_NO")+" ,组件号"+itemData.getString("COMPONENT_NO")+" 的状态，不能进行拣配下架");
					}
				}
				List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
				for(int n=0;n<jarr.size();n++){
					Map<String, Object> retMap=new HashMap<String, Object>();
					JSONObject itemData=  jarr.getJSONObject(n);
					retMap.put("WAITING_LOCATION", itemData.get("WAITING_LOCATION"));
					retMap.put("BARCODE", itemData.get("BARCODE"));
					retMap.put("DISPATCHING_NO", itemData.get("DISPATCHING_NO"));
					retMap.put("ITEM_NO", itemData.get("ITEM_NO"));
					retMap.put("COMPONENT_NO", itemData.get("COMPONENT_NO"));
					retMap.put("FROM_PLANT_CODE", itemData.get("FROM_PLANT_CODE"));
					retMap.put("STATUS", itemData.get("STATUS"));
					retMap.put("MATERIAL_CODE", itemData.get("MATERIAL_CODE"));
					retMap.put("MATERIAL_DESC", itemData.get("MATERIAL_DESC"));
					retMap.put("BATCH", itemData.get("BATCH"));
					retMap.put("VENDOR_NAME", itemData.get("VENDOR_NAME"));
					retMap.put("VENDOR_CODE", itemData.get("VENDOR_CODE"));
					retMap.put("QUANTITY", itemData.get("QUANTITY"));
					retMap.put("PICKING_ADDRESS", itemData.get("PICKING_ADDRESS"));
					retMap.put("LEFT_PRINT_TIMES", itemData.get("LEFT_PRINT_TIMES"));
					retMap.put("HANDOVER_DATE", itemData.get("HANDOVER_DATE"));
					retMap.put("LINE_CATEGORY", itemData.get("LINE_CATEGORY"));
					retMap.put("TRIAL_FLAG", itemData.get("TRIAL_FLAG"));
					retMap.put("LGORT_F", itemData.get("LGORT_F"));
					retMap.put("USERNAME", params.get("USERNAME"));
					retMap.put("USERID", params.get("USERID"));
					retMap.put("ID", itemData.get("ID"));
					retList.add(retMap);
				}
				disPatchingJISService.FeiJISPicking(retList);
				return R.ok();
	}catch (Exception e) {
		e.printStackTrace();
		return R.error(e.getMessage());
	}
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping("/printjis")
    public R printjis(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> dispatchingList=new ArrayList<Map<String, Object>>();
	try{
		Map<String, Object> logisticsMap=new HashMap<String, Object>();
		//总装物流参数配置表所有信息
		List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
		
    	JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
    	for(int m=0;m<jarr.size();m++){
			JSONObject itemData=  jarr.getJSONObject(m);
			Map<String, Object> itemMap=new HashMap<String, Object>();
			itemMap.put("DISPATCHING_NO", itemData.getString("DISPATCHING_NO"));
			dispatchingList.add(itemMap);
			
			//比较总装物流参数的werks_f和供应工厂 
			for(Map<String, Object> logisticsMapTemp:assemblyLogistics){
				if((logisticsMapTemp.get("WERKS_F")).equals(itemData.getString("FROM_PLANT_CODE"))){
					String wms_flag_f=logisticsMapTemp.get("WMS_FLAG_F")==null?"":logisticsMapTemp.get("WMS_FLAG_F").toString();
					String status=itemData.getString("STATUS")==null?"":itemData.getString("STATUS").toString();//
					if("0".equals(wms_flag_f)){//没有上wms
						
						if(!"01".equals(status)){
							throw new RuntimeException("单号"+itemData.getString("DISPATCHING_NO")+" 的状态，不能进行打印");
						}
					}else{
						if(!"02".equals(status)){
							throw new RuntimeException("单号"+itemData.getString("DISPATCHING_NO")+" 的状态，不能进行打印");
						}
					}
				}
			}
		}
    	
    	
		disPatchingJISService.printJIS(dispatchingList);
		return R.ok();
		}catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
	}
	
	@RequestMapping("/checkJISDetailStatus")
    public R checkJISDetailStatus(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> retjisDetaillist = new ArrayList<Map<String, Object>>();
		
		List<String> dispatchingNoList=new ArrayList<String>();
    	JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
    	for(int m=0;m<jarr.size();m++){
			JSONObject itemData=  jarr.getJSONObject(m);
			dispatchingNoList.add(itemData.getString("DISPATCHING_NO").toString());//
		}
    	Map<String, Object> tempMap=new HashMap<String, Object>();
    	tempMap.put("DISPATCHINGNOList", dispatchingNoList);
    	retjisDetaillist=disPatchingJISService.selectDispatchingJISBillPickingByDispatcingNo(tempMap);
    	String retStr="";
    	for(Map<String, Object> beanMap:retjisDetaillist){
    		if(!"02".equals(beanMap.get("STATUS"))){//状态不是02
    			retStr=retStr+" 单号为:"+beanMap.get("DISPATCHING_NO")+" 行项目为:"+beanMap.get("ITEM_NO")+" 组件号为:"+beanMap.get("COMPONENT_NO")+" 状态不正确，不能进行打印! ";
    		}
    	}
    	
		if("".equals(retStr)){
			return R.ok().put("result", retjisDetaillist);
		}else{
			return R.error(retStr);
		}
		
	}
	
	@RequestMapping("/listQueRen")
    public R listQueRen(@RequestParam Map<String, Object> params){
		List<String> fromPlantCodelist=new ArrayList<String>();
		if("".equals(params.get("FROM_PLANT_CODE"))){//全部工厂
			Set<Map<String,Object>> werksSet=userUtils.getUserWerks("WLMS_JIS");
			if(!werksSet.isEmpty()){
				for(Map<String,Object> maptemp:werksSet){
					fromPlantCodelist.add(maptemp.get("WERKS").toString());
				}
				
				params.put("fromPlantCodelist", fromPlantCodelist);
			}
		}else{
			fromPlantCodelist.add(params.get("FROM_PLANT_CODE").toString());
			params.put("fromPlantCodelist", fromPlantCodelist);
		}
		
		List<Map<String, Object>> retlist = new ArrayList<Map<String, Object>>();
		retlist=disPatchingJISService.selectDispatchingQueRen(params);
		if(retlist.size()==0){
			return R.error("没有查询到对应的数据！");
		}
		return R.ok().put("result", retlist);
	}
	@CrossOrigin
	@RequestMapping("/listQueRenPDA")
    public R listQueRenPDA(@RequestBody Map<String, Object> params){
		List<String> fromPlantCodelist=new ArrayList<String>();
		if("".equals(params.get("FROM_PLANT_CODE"))){//全部工厂
			Set<Map<String,Object>> werksSet=userUtils.getUserWerks("WLMS_JIS");
			if(!werksSet.isEmpty()){
				for(Map<String,Object> maptemp:werksSet){
					fromPlantCodelist.add(maptemp.get("WERKS").toString());
				}
				
				params.put("fromPlantCodelist", fromPlantCodelist);
			}
		}else{
			fromPlantCodelist.add(params.get("FROM_PLANT_CODE").toString());
			params.put("fromPlantCodelist", fromPlantCodelist);
		}
		
		List<Map<String, Object>> retlist = new ArrayList<Map<String, Object>>();
		retlist=disPatchingJISService.selectDispatchingQueRen(params);
		if(retlist.size()==0){
			return R.error("没有查询到对应的数据！");
		}
		return R.ok().put("result", retlist);
	}
	
	/**
	 * 验证 是否上wms 以及库存的下架数量
	 * @param params
	 * @return
	 */
	
	@RequestMapping("/checkQueRen")
	public R checkQueRen(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> querenList=new ArrayList<Map<String, Object>>();
		List<String> werks_f_list=new ArrayList<String>();//保存供应工厂
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
	try{
		for(int m=0;m<jarr.size();m++){
			JSONObject itemData=  jarr.getJSONObject(m);
			String WERKS_F=itemData.getString("FROM_PLANT_CODE")==null?"":itemData.getString("FROM_PLANT_CODE").toString();//
			werks_f_list.add(WERKS_F);
			
			Map<String, Object> querenMap=new HashMap<String, Object>();
			querenMap.put("FROM_PLANT_CODE", itemData.getString("FROM_PLANT_CODE"));
			querenMap.put("BATCH", itemData.getString("BATCH"));
			querenMap.put("MATNR", itemData.getString("MATERIAL_CODE"));
			querenMap.put("XJ_QTY", itemData.getString("XJ_QTY"));
			querenMap.put("QUANTITY", itemData.getString("QUANTITY"));
			querenMap.put("LGORT", itemData.getString("LGORT"));
			querenList.add(querenMap);
		}
		for(int i=0;i<werks_f_list.size()-1;i++ ){       
		      for ( int j=werks_f_list.size()-1;j>i;j--){       
		           if  (werks_f_list.get(j).equals(werks_f_list.get(i)))  {       
		        	   werks_f_list.remove(j);       
		            }        
		        }        
		    } 
		String wms_flag="";
		for(int n=0;n<werks_f_list.size();n++){
			Map<String, Object> logisticsMap=new HashMap<String, Object>();
			logisticsMap.put("WERKS_F", werks_f_list.get(n));
			List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
			if(assemblyLogistics!=null&&assemblyLogistics.size()>0){
				String wms_flag_f=assemblyLogistics.get(0).get("WMS_FLAG_F")==null?"":assemblyLogistics.get(0).get("WMS_FLAG_F").toString();
				if(!"".equals(wms_flag)){
					if(!wms_flag.equals(wms_flag_f)){
						throw new RuntimeException("拣配确认的必须都是上wms或者不上wms的数据!");
					}
				}
				wms_flag=wms_flag_f;
				
				}else{
				throw new RuntimeException("该"+werks_f_list.get(n)+"工厂未配置总装物流参数!");
			}
		}
		
		if("0".equals(wms_flag)){//不上wms
			
		}else{//上wms
			//验证库存 
			//首先按照批次和库位汇总下架数量
			for(int r=0;r<querenList.size()-1;r++){
				for(int s=querenList.size()-1;s>r;s--){
					if((querenList.get(r).get("BATCH").equals(querenList.get(s).get("BATCH")))&&
							(querenList.get(r).get("LGORT").equals(querenList.get(s).get("LGORT")))
							){
						String xj_qty_str="0";
						if(querenList.get(s).get("XJ_QTY")==null||"".equals(querenList.get(s).get("XJ_QTY"))){
							xj_qty_str="0";
						}
						querenList.get(r).put("XJ_QTY", new BigDecimal(xj_qty_str)
						.add(new BigDecimal(xj_qty_str)));
						querenList.remove(s);
					}
				}
			}
			disPatchingJISService.checkDispatchingQueRen(querenList);
		}
		return R.ok();
	}catch (Exception e) {
		e.printStackTrace();
		return R.error(e.getMessage());
	}
	}
	@CrossOrigin
	@RequestMapping("/checkQueRenPDA")
	public R checkQueRenPDA(@RequestBody Map<String, Object> params){
		List<Map<String, Object>> querenList=new ArrayList<Map<String, Object>>();
		List<String> werks_f_list=new ArrayList<String>();//保存供应工厂
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
	try{
		for(int m=0;m<jarr.size();m++){
			JSONObject itemData=  jarr.getJSONObject(m);
			String WERKS_F=itemData.getString("FROM_PLANT_CODE")==null?"":itemData.getString("FROM_PLANT_CODE").toString();//
			werks_f_list.add(WERKS_F);
			
			Map<String, Object> querenMap=new HashMap<String, Object>();
			querenMap.put("FROM_PLANT_CODE", itemData.getString("FROM_PLANT_CODE"));
			querenMap.put("BATCH", itemData.getString("BATCH"));
			querenMap.put("MATNR", itemData.getString("MATERIAL_CODE"));
			querenMap.put("XJ_QTY", itemData.getString("XJ_QTY"));
			querenMap.put("QUANTITY", itemData.getString("QUANTITY"));
			querenMap.put("LGORT", itemData.getString("LGORT"));
			querenList.add(querenMap);
		}
		for(int i=0;i<werks_f_list.size()-1;i++ ){       
		      for ( int j=werks_f_list.size()-1;j>i;j--){       
		           if  (werks_f_list.get(j).equals(werks_f_list.get(i)))  {       
		        	   werks_f_list.remove(j);       
		            }        
		        }        
		    } 
		String wms_flag="";
		for(int n=0;n<werks_f_list.size();n++){
			Map<String, Object> logisticsMap=new HashMap<String, Object>();
			logisticsMap.put("WERKS_F", werks_f_list.get(n));
			List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
			if(assemblyLogistics!=null&&assemblyLogistics.size()>0){
				String wms_flag_f=assemblyLogistics.get(0).get("WMS_FLAG_F")==null?"":assemblyLogistics.get(0).get("WMS_FLAG_F").toString();
				if(!"".equals(wms_flag)){
					if(!wms_flag.equals(wms_flag_f)){
						throw new RuntimeException("拣配确认的必须都是上wms或者不上wms的数据!");
					}
				}
				wms_flag=wms_flag_f;
				
				}else{
				throw new RuntimeException("该"+werks_f_list.get(n)+"工厂未配置总装物流参数!");
			}
		}
		
		if("0".equals(wms_flag)){//不上wms
			
		}else{//上wms
			//验证库存 
			//首先按照批次和库位汇总下架数量
			for(int r=0;r<querenList.size()-1;r++){
				for(int s=querenList.size()-1;s>r;s--){
					if((querenList.get(r).get("BATCH").equals(querenList.get(s).get("BATCH")))&&
							(querenList.get(r).get("LGORT").equals(querenList.get(s).get("LGORT")))
							){
						String xj_qty_str="0";
						if(querenList.get(s).get("XJ_QTY")==null||"".equals(querenList.get(s).get("XJ_QTY"))){
							xj_qty_str="0";
						}
						querenList.get(r).put("XJ_QTY", new BigDecimal(xj_qty_str)
						.add(new BigDecimal(xj_qty_str)));
						querenList.remove(s);
					}
				}
			}
			disPatchingJISService.checkDispatchingQueRen(querenList);
		}
		return R.ok();
	}catch (Exception e) {
		e.printStackTrace();
		return R.error(e.getMessage());
	}
	}
	
	@RequestMapping("/updateQueRen")
	public R updateQueRen(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
        params.put("USERNAME", currentUser.get("USERNAME"));
        params.put("FULL_NAME", currentUser.get("FULL_NAME"));
        params.put("USERID", currentUser.get("USER_ID"));
        try{   
        	disPatchingJISService.updateQueRenService(params);
        	return R.ok();
		}catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
	}
	
	@CrossOrigin
	@RequestMapping("/updateQueRenPDA")
	public R updateQueRenPDA(@RequestBody Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
        params.put("USERNAME", currentUser.get("USERNAME"));
        params.put("FULL_NAME", currentUser.get("FULL_NAME"));
        params.put("USERID", currentUser.get("USER_ID"));
        
		try{
			disPatchingJISService.updateQueRenService(params);
			return R.ok();
		}catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
	}
	
	@RequestMapping("/listjiaojie")
    public R listjiaojie(@RequestParam Map<String, Object> params){
		List<String> fromPlantCodelist=new ArrayList<String>();
		if("".equals(params.get("FROM_PLANT_CODE"))){//全部工厂
			Set<Map<String,Object>> werksSet=userUtils.getUserWerks("WLMS_JIS");
			if(!werksSet.isEmpty()){
				for(Map<String,Object> maptemp:werksSet){
					fromPlantCodelist.add(maptemp.get("WERKS").toString());
				}
				
				params.put("fromPlantCodelist", fromPlantCodelist);
			}
		}else{
			fromPlantCodelist.add(params.get("FROM_PLANT_CODE").toString());
			params.put("fromPlantCodelist", fromPlantCodelist);
		}
		List<Map<String, Object>> retlist = new ArrayList<Map<String, Object>>();
		retlist=disPatchingJISService.selectDispatchingQueRen(params);
		if(retlist.size()==0){
			return R.error("没有查询到对应的数据！");
		}
		return R.ok().put("result", retlist);
	}
	@CrossOrigin
	@RequestMapping("/listjiaojiePDA")
    public R listjiaojiePDA(@RequestBody Map<String, Object> params){
		List<String> fromPlantCodelist=new ArrayList<String>();
		if("".equals(params.get("FROM_PLANT_CODE"))){//全部工厂
			Set<Map<String,Object>> werksSet=userUtils.getUserWerks("WLMS_JIS");
			if(!werksSet.isEmpty()){
				for(Map<String,Object> maptemp:werksSet){
					fromPlantCodelist.add(maptemp.get("WERKS").toString());
				}
				
				params.put("fromPlantCodelist", fromPlantCodelist);
			}
		}else{
			fromPlantCodelist.add(params.get("FROM_PLANT_CODE").toString());
			params.put("fromPlantCodelist", fromPlantCodelist);
		}
		List<Map<String, Object>> retlist = new ArrayList<Map<String, Object>>();
		retlist=disPatchingJISService.selectDispatchingQueRen(params);
		if(retlist.size()==0){
			return R.error("没有查询到对应的数据！");
		}
		return R.ok().put("result", retlist);
	}
	
	@RequestMapping("/dispatchingHandover")
	public R dispatchingHandover(@RequestParam Map<String, Object> params){
		
		Map<String,Object> currentUser = userUtils.getUser();
        params.put("USERNAME", currentUser.get("USERNAME"));
        params.put("FULL_NAME", currentUser.get("FULL_NAME"));
        params.put("USERID", currentUser.get("USER_ID"));
		
		try{
			Map<String, Object> retMap=disPatchingJISService.dispatchingHandoverService(params);
			return R.ok().put("jkinfo", retMap.get("jkinfo")).put("retsap", retMap.get("retsap"));
		}catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
	}
	
	@CrossOrigin
	@RequestMapping("/dispatchingHandoverPDA")
	public R dispatchingHandoverPDA(@RequestBody Map<String, Object> params){
		
		Map<String,Object> currentUser = userUtils.getUser();
        params.put("USERNAME", currentUser.get("USERNAME"));
        params.put("FULL_NAME", currentUser.get("FULL_NAME"));
        params.put("USERID", currentUser.get("USER_ID"));
		
		try{
			Map<String, Object> retMap=disPatchingJISService.dispatchingHandoverService(params);
			return R.ok().put("jkinfo", retMap.get("jkinfo")).put("retsap", retMap.get("retsap"));
		}catch (Exception e) {
		e.printStackTrace();
		return R.error(e.getMessage()+"系统异常，请联系管理员！");
	}
	}
	
	/**
	 * jis拣配下架打印
	 * @param params
	 * @return
	 */
	@RequestMapping("/printfeijis")
    public R printfeijis(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> dispatchingList=new ArrayList<Map<String, Object>>();
	try{
		Map<String, Object> logisticsMap=new HashMap<String, Object>();
		//总装物流参数配置表所有信息
		List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
		
    	JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
    	for(int m=0;m<jarr.size();m++){
			JSONObject itemData=  jarr.getJSONObject(m);
			Map<String, Object> itemMap=new HashMap<String, Object>();
			itemMap.put("DISPATCHING_NO", itemData.getString("DISPATCHING_NO"));
			itemMap.put("ITEM_NO", itemData.getString("ITEM_NO"));
			itemMap.put("COMPONENT_NO", itemData.getString("COMPONENT_NO"));
			dispatchingList.add(itemMap);
			
			//比较总装物流参数的werks_f和供应工厂 
			for(Map<String, Object> logisticsMapTemp:assemblyLogistics){
				if((logisticsMapTemp.get("WERKS_F")).equals(itemData.getString("FROM_PLANT_CODE"))){
					String wms_flag_f=logisticsMapTemp.get("WMS_FLAG_F")==null?"":logisticsMapTemp.get("WMS_FLAG_F").toString();
					String status=itemData.getString("STATUS")==null?"":itemData.getString("STATUS").toString();//
					if("0".equals(wms_flag_f)){//没有上wms
						
						if(!"01".equals(status)){
							throw new RuntimeException("单号"+itemData.getString("DISPATCHING_NO")+"  ,行项目 "+itemData.getString("ITEM_NO")+" ,组件号"+itemData.getString("COMPONENT_NO")+" 的状态，不能进行打印");
						}
					}else{
						if(!"02".equals(status)){
							throw new RuntimeException("单号"+itemData.getString("DISPATCHING_NO")+"  ,行项目 "+itemData.getString("ITEM_NO")+" ,组件号"+itemData.getString("COMPONENT_NO")+" 的状态，不能进行打印");
						}
					}
				}
			}
		}
    	
    	
		disPatchingJISService.printFeiJIS(dispatchingList);
		return R.ok();
		}catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
	}
	
	@RequestMapping("/listfabu")
    public R listfabu(@RequestParam Map<String, Object> params){
		List<String> fromPlantCodelist=new ArrayList<String>();
		if("".equals(params.get("FROM_PLANT_CODE"))){//全部工厂
			Set<Map<String,Object>> werksSet=userUtils.getUserWerks("WLMS_JIS");
			if(!werksSet.isEmpty()){
				for(Map<String,Object> maptemp:werksSet){
					fromPlantCodelist.add(maptemp.get("WERKS").toString());
				}
				
				params.put("fromPlantCodelist", fromPlantCodelist);
			}
		}else{
			fromPlantCodelist.add(params.get("FROM_PLANT_CODE").toString());
			params.put("fromPlantCodelist", fromPlantCodelist);
		}
		//应该只接受barcode的条件
		List<Map<String, Object>> retlist = new ArrayList<Map<String, Object>>();
		retlist=disPatchingJISService.selectDispatchingByfabu(params);
		return R.ok().put("result", retlist);
	}
	
	@RequestMapping("/updatefabu")
	public R updatefabu(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> paramlist=new ArrayList<Map<String, Object>>();
		
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		try{
		
		for(int m=0;m<jarr.size();m++){
			JSONObject itemData=jarr.getJSONObject(m);
			
			Map<String, Object> querenMap=new HashMap<String, Object>();
			querenMap.put("FROM_PLANT_CODE", itemData.getString("FROM_PLANT_CODE"));
			querenMap.put("BATCH", itemData.getString("BATCH"));
			querenMap.put("MATNR", itemData.getString("MATERIAL_CODE"));
			querenMap.put("XJ_QTY", itemData.getString("XJ_QTY"));
			querenMap.put("QUANTITY", itemData.getString("QUANTITY"));
			querenMap.put("LGORT", itemData.getString("LGORT"));
			querenMap.put("DISPATCHING_NO", itemData.getString("DISPATCHING_NO"));
			querenMap.put("ITEM_NO", itemData.getString("ITEM_NO"));
			querenMap.put("COMPONENT_NO", itemData.getString("COMPONENT_NO"));
			querenMap.put("STATUS", itemData.getString("STATUS"));
			querenMap.put("BARCODE", itemData.getString("BARCODE"));
			querenMap.put("TYPE", itemData.getString("TYPE"));
			paramlist.add(querenMap);
			
		}
		String xwlStr=disPatchingJISService.dispatchingFabu(paramlist);
		
		return R.ok().put("jkinfo", xwlStr);
	}catch (Exception e) {
		e.printStackTrace();
		return R.error(e.getMessage());
	}
	}
	
	@RequestMapping("/dispatchingchaif")
	public R dispatchingchaif(@RequestParam Map<String, Object> params){
		
		try{
		
		String xwlStr=disPatchingJISService.dispatchingChaifen(params);
		
		return R.ok().put("jkinfo", xwlStr);
	}catch (Exception e) {
		e.printStackTrace();
		return R.error(e.getMessage());
	}
	}
	
	@RequestMapping("/listwmlsException")
    public R listwmlsException(@RequestParam Map<String, Object> params){
		List<Map<String, Object>> retlist = new ArrayList<Map<String, Object>>();
		retlist=disPatchingJISService.selectToWlmsException(params);
		return R.ok().put("result", retlist);
	}
	
	@RequestMapping("/listPickRecordNoCount")
    public R listPickRecordNoCount(@RequestParam Map<String, Object> params){
		Map<String, Object>  retMap=new HashMap<String, Object>();
		try{
			if(!"".equals(params.get("BARCODE"))){
				String barcode=params.get("BARCODE").toString();
			    retMap=disPatchingJISService.pickRecordNoCount(barcode);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}	
		return R.ok().put("result", retMap);
	}
}
