package com.byd.wms.business.webservice.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.byd.utils.R;
import com.byd.wms.business.modules.cswlms.dao.DispatchingJISBillPickingDAO;
import com.byd.wms.business.webservice.WmsWebService;

@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://webservice.business.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.business.webservice.WmsWebService" // 接口地址
)
public class WmsWebServiceImpl implements WmsWebService{
	@Autowired
	private DispatchingJISBillPickingDAO dispatchingJISBillPickingDAO;
	
    @Override
    public String hello(String name) {
        return "hello"+name;
    }

	

    /**
     * 创建拣配单
     * 需要向T_DISPATCHING_HEADER   T_DISPATCHING_ITEM T_DISPATCHING_COMPONENT
     * 三个表插入数据
     */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> createNewDispatching(String params) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Map<String, Object> retMap=new HashMap<String, Object>();
		//json字符串转为json对象
		List<Map> jsonList = JSONObject.parseArray(params,Map.class);//解析出 多行
		
		//存放待插入的header集合
		List<Map<String, Object>> headerMapList=new ArrayList<Map<String, Object>>();
		//存放待插入的item集合
		List<Map<String, Object>> itemsMapList=new ArrayList<Map<String, Object>>();
		//存放待插入的component集合
		List<Map<String, Object>> componentsMapList=new ArrayList<Map<String, Object>>();
		
		if(jsonList!=null&&jsonList.size()>0){
			try{
			//取header信息
			for(int i=0;i<jsonList.size();i++){
				
				Map<String, Object> headerMap=jsonList.get(i);
				
				headerMap.put("CREATE_DATE", sdf.format(date));
				headerMap.put("CREATE_USER_ID", "1");
				headerMap.put("CREATE_USER_NAME", "ws");
				
				headerMap.put("UPDATE_DATE", sdf.format(date));
				headerMap.put("UPDATE_USER_ID", "1");
				headerMap.put("UPDATE_USER_NAME", "ws");
				
				headerMap.put("VERIFY","1");
				headerMapList.add(headerMap);
				
				if(headerMap.get("items")!=null){
				
				List<Map<String, Object>> itemsJosnList=(List<Map<String, Object>>) headerMap.get("items");
				
				for(int j=0;j<itemsJosnList.size();j++){
					
					Map<String, Object> itemsMap=itemsJosnList.get(j);
					
					itemsMap.put("CREATE_DATE", sdf.format(date));
					itemsMap.put("CREATE_USER_ID", "1");
					itemsMap.put("CREATE_USER_NAME", "ws");
					
					itemsMap.put("UPDATE_DATE", sdf.format(date));
					itemsMap.put("UPDATE_USER_ID", "1");
					itemsMap.put("UPDATE_USER_NAME", "ws");
					
					itemsMap.put("VERIFY","1");
					
					itemsMapList.add(itemsMap);
					
					//取components信息
					if(itemsMap.get("components")!=null){
					List<Map<String, Object>> componentsJosnList=(List<Map<String, Object>>) itemsMap.get("components");
					for(int z=0;z<componentsJosnList.size();z++){
						Map<String, Object> componentsMap=componentsJosnList.get(z);
						
						componentsMap.put("CREATE_DATE", sdf.format(date));
						componentsMap.put("CREATE_USER_ID", "1");
						componentsMap.put("CREATE_USER_NAME", "ws");
						
						componentsMap.put("UPDATE_DATE", sdf.format(date));
						componentsMap.put("UPDATE_USER_ID", "1");
						componentsMap.put("UPDATE_USER_NAME", "ws");
						
						componentsMap.put("VERIFY","1");
						
						componentsMapList.add(componentsMap);
					}
					}
				}
				}
				
				
			}
			
			dispatchingJISBillPickingDAO.insertHeader(headerMapList);
			dispatchingJISBillPickingDAO.insertItem(itemsMapList);
			dispatchingJISBillPickingDAO.insertComponent(componentsMapList);
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.println(e.getCause());
				retMap.put("message", e.getMessage());
				return R.error("系统异常，请联系管理员！"+e.getMessage());
			}
			
			retMap.put("message", "success");
		}else{
			retMap.put("message", "传入参数不能为空");
		}
		
		return retMap;
	}
	/**
	 * 参数为T_DISPATCHING_COMPONENT 集合
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> deleteDispatchingByArray(String params) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Map<String, Object> retMap=new HashMap<String, Object>();
		
		//json字符串转为json对象
		List<Map> jsonList = JSONObject.parseArray(params,Map.class);//解析出 多行
		if(jsonList!=null&&jsonList.size()>0){
			for(int i=0;i<jsonList.size();i++){
				Map<String, Object> componentMap=new HashMap<String, Object>();
				if(jsonList.get(i).get("DISPATCHING_NO")!=null&&!"".equals(jsonList.get(i).get("DISPATCHING_NO"))){
					componentMap.put("DISPATCHING_NO", jsonList.get(i).get("DISPATCHING_NO"));
				}
				if(jsonList.get(i).get("ITEM_NO")!=null&&!"".equals(jsonList.get(i).get("ITEM_NO"))){
					componentMap.put("ITEM_NO", jsonList.get(i).get("ITEM_NO"));
				}
				if(jsonList.get(i).get("COMPONENT_NO")!=null&&!"".equals(jsonList.get(i).get("COMPONENT_NO"))){
					componentMap.put("COMPONENT_NO", jsonList.get(i).get("COMPONENT_NO"));
				}
				//通过DISPATCHING_NO ITEM_NO  COMPONENT_NO查询组件表,返回是唯一一条记录
				List<Map<String,Object>> retComponentList=dispatchingJISBillPickingDAO.selectDispatchingComponent(componentMap);
				if(retComponentList!=null&&retComponentList.size()>0){
					retComponentList.get(0).put("DELETE_REMARK", jsonList.get(i).get("DELETE_REMARK")==null?"":jsonList.get(i).get("DELETE_REMARK"));
					
					retComponentList.get(0).put("UPDATE_DATE", sdf.format(date));
					retComponentList.get(0).put("UPDATE_USER_ID", "1");
					retComponentList.get(0).put("UPDATE_USER_NAME", "ws");
					
					dispatchingJISBillPickingDAO.deleteComponentByID(retComponentList.get(0));
					
					Integer count =dispatchingJISBillPickingDAO.countOfComponentByItem(retComponentList.get(0));
					//如果是最后一条component，则要删除item选项
					if (count < 1) {
						dispatchingJISBillPickingDAO.deleteItem(retComponentList.get(0));
						Integer hCount =dispatchingJISBillPickingDAO.selectCountHeaderByBillNo(retComponentList.get(0));
						//如果是最后一条item，则要删除header选项
						if (hCount < 1) {
							dispatchingJISBillPickingDAO.deleteHeader(retComponentList.get(0));
						}
					}
				}
			}
			retMap.put("message", "success");
		}else{
			retMap.put("message", "传入参数不能为空");
		}
		
		return retMap;
	}

	/**
	 * 参数为T_DISPATCHING_COMPONENT 集合
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> closeDispatchingByArray(String params) {
		Map<String, Object> retMap=new HashMap<String, Object>();
		
		//json字符串转为json对象
		List<Map> jsonList = JSONObject.parseArray(params,Map.class);//解析出 多行
		if(jsonList!=null&&jsonList.size()>0){
			for(int i=0;i<jsonList.size();i++){
				//查询出来是唯一一条
				List<Map<String, Object>> retComponentList=dispatchingJISBillPickingDAO.selectComponentByBarcode(jsonList.get(i));
				if(retComponentList!=null&&retComponentList.size()>0){
					//判断当前的barcode对应的行能否进行关闭   07,04状态不能关闭
					if("07".equals(retComponentList.get(i).get("STATUS"))||"04".equals(retComponentList.get(i).get("STATUS"))){
						retMap.put("message", "关闭或者交接的状态不能进行关闭!");
						return retMap;
					}
					//通过barcode更改component表的状态为关闭
					Map<String, Object> componentMap=new HashMap<String, Object>();
					componentMap.put("BARCODE", retComponentList.get(0).get("BARCODE"));
					componentMap.put("STATUS", "07");//关闭
					dispatchingJISBillPickingDAO.updateDispatchingStatusByBarcode(componentMap);
					//判断是否需要更改行项目表和头表
					if(ifWholeComponentChangedStatus(retComponentList.get(0).get("DISPATCHING_NO").toString(),retComponentList.get(0).get("ITEM_NO").toString(),"07")){
						Map<String, Object> itemparam=new HashMap<String, Object>();
						itemparam.put("DISPATCHING_NO", retComponentList.get(0).get("DISPATCHING_NO"));
						itemparam.put("ITEM_NO", retComponentList.get(0).get("ITEM_NO"));
						itemparam.put("STATUS", "07");
						dispatchingJISBillPickingDAO.updateDispatchingStatusItem(itemparam);
					}
					
					if(ifWholeItemChangedStatus(retComponentList.get(0).get("DISPATCHING_NO").toString(),"07")){
						Map<String, Object> headerparam=new HashMap<String, Object>();
						headerparam.put("DISPATCHING_NO", retComponentList.get(0).get("DISPATCHING_NO"));
						headerparam.put("STATUS", "07");
						dispatchingJISBillPickingDAO.updateDispatchingStatusHeader(headerparam);
					}
				}
			}
			retMap.put("message", "success");
		}else{
			retMap.put("message", "传入参数不能为空");
		}
		
		return retMap;
		
	}
	
	public boolean ifWholeComponentChangedStatus(String dispathingNo,String ItemNo,String status){
		Map<String, Object> componentP=new HashMap<String, Object>();
		componentP.put("DISPATCHING_NO", dispathingNo);
		componentP.put("ITEM_NO", ItemNo);
		componentP.put("STATUS", status);
		int conts=dispatchingJISBillPickingDAO.ifWholeComponentChangedStatus(componentP);
		boolean flg=true;
		if(conts>0){
			flg=false;
		}
		return flg;
	}
	
	public boolean ifWholeItemChangedStatus(String dispathingNo,String status){
		Map<String, Object> itemP=new HashMap<String, Object>();
		itemP.put("DISPATCHING_NO", dispathingNo);
		itemP.put("STATUS", status);
		int conts=dispatchingJISBillPickingDAO.ifWholeItemChangedStatus(itemP);
		boolean flg=true;
		if(conts>0){
			flg=false;
		}
		return flg;
	}
}
