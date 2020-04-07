package com.byd.wms.webservice.ws.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.StringUtils;
import com.byd.wms.webservice.common.remote.BusinessRemote;
import com.byd.wms.webservice.ws.dao.ReceiveOtherSystemReqDao;
import com.byd.wms.webservice.ws.service.WmsWebServiceByLogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.jws.WebService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:48:59 
 *
 */
@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.ws.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.ws.service.WmsWebServiceByLogisticsService" // 接口地址
)
public class WmsWebServiceByLogisticsImpl implements WmsWebServiceByLogisticsService {

    @Autowired
    private ReceiveOtherSystemReqDao receiveOtherSystemReqDao;

    @Autowired
	private BusinessRemote businessRemote;


    /**
     * 接收第三方系统叫料需求
     */
    @Override
    public Map<String,String> receiveOtherSystemREQ(String params){
    	Map<String,String> remap = new HashMap<String,String>();
    	if(params != null && !params.equals("")) {
    		List<Map<String,Object>> addlist = new ArrayList<Map<String,Object>>(); //新增准备数据
    		List<Map<String,Object>> updatelist = new ArrayList<Map<String,Object>>(); //修改准备数据
    		List<Map<String,Object>> closelist = new ArrayList<Map<String,Object>>(); //关闭准备数据
    		
    		//json字符串转为json对象
    		List<Map> jsonList = JSONObject.parseArray(params,Map.class);//解析出 多行
    		
    		for(int i=0;i<jsonList.size();i++){
    			Map<String, Object> reqItemMap = jsonList.get(i);
    			
    			reqItemMap.put("DLV_ITEM", reqItemMap.get("DELIVERY_ITEM_NO") == null?"":reqItemMap.get("DELIVERY_ITEM_NO"));//配送单号行项目
    			reqItemMap.put("KANBAN_NO", reqItemMap.get("REQUIREMENT_NO") == null?"":reqItemMap.get("REQUIREMENT_NO"));//物流需求号
    			reqItemMap.put("WERKS", reqItemMap.get("PLCD") == null?"":reqItemMap.get("PLCD"));
    			reqItemMap.put("LGORT", reqItemMap.get("LOCD") == null?"":reqItemMap.get("LOCD"));
    			reqItemMap.put("MAKTX", reqItemMap.get("MATEDS") == null?"":reqItemMap.get("MATEDS"));//物料描述
    			reqItemMap.put("QTY", reqItemMap.get("QUANTITY") == null?BigDecimal.ZERO:new BigDecimal(String.valueOf(reqItemMap.get("QUANTITY"))));//需求数量
    			reqItemMap.put("LIFNR", reqItemMap.get("VERDOR_CODE") == null?"":reqItemMap.get("VERDOR_CODE"));//供应商
    			reqItemMap.put("POINT_OF_USE", reqItemMap.get("STATION") == null?"":reqItemMap.get("STATION"));//工位
    			reqItemMap.put("LINE_NO", reqItemMap.get("PRODUCTION_LINE") == null?"":reqItemMap.get("PRODUCTION_LINE"));//产线
    			reqItemMap.put("DOSAGE", reqItemMap.get("MINQTY") == null?"":reqItemMap.get("MINQTY"));//标准用量（总装最小包装）
    			reqItemMap.put("START_SHIPPING_TIME", reqItemMap.get("LATEST_DELIVERY_TIME") == null?"":reqItemMap.get("LATEST_DELIVERY_TIME"));//开始运输时间（总装最晚配送时间）
    			reqItemMap.put("DEMAND_TIME", reqItemMap.get("LATEST_DEMAND_TIME") == null?"":reqItemMap.get("LATEST_DEMAND_TIME"));//预计生产时间（总装最晚需求时间）
    			reqItemMap.put("EMERGENCY", reqItemMap.get("PRIORITY") == null?"":reqItemMap.get("PRIORITY"));//紧急度：1紧急、0正常
    			
    			if (reqItemMap.get("TRANS").equals("ADD")) {
    				reqItemMap.put("STATUS", "00");
    				reqItemMap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    			addlist.add(reqItemMap);
    			} else {
    				
    				List<Map<String, Object>> reqlist = receiveOtherSystemReqDao.selectOtherSystemREQ(reqItemMap);
    				//状态为未开始才允许修改
    				if(reqlist.size() > 0) {
    					if(reqlist.get(0).get("STATUS").equals("00")) {
    						reqItemMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    					if(reqItemMap.get("TRANS").equals("Update")) {
	    						updatelist.add(reqItemMap);
	    					} else if(reqItemMap.get("TRANS").equals("Close")){
	    						reqItemMap.put("STATUS", "06");
	    	    				reqItemMap.put("DEL", "X");
	    	    				closelist.add(reqItemMap);
	    					}
    					} else if(reqlist.get(0).get("STATUS").equals("06")) {
    						remap.put("STATUS", "E");
        		    		remap.put("MESSAGE", "此单已关闭");
        		    		return remap;
    					} else {
        					remap.put("STATUS", "E");
        		    		remap.put("MESSAGE", "此配送单wms系统已备料");
        		    		return remap;
        				}
    					
    				} else {
    					remap.put("STATUS", "E");
    		    		remap.put("MESSAGE", "此配送单不存在");
    		    		return remap;
    				}
    			} 
    		}
    		
    		//新增叫料需求
    		if(addlist.size() > 0) {
    			receiveOtherSystemReqDao.saveOtherSystemREQ(addlist);
    		}
    		//修改叫料需求
    		if(updatelist.size() > 0) {
    			receiveOtherSystemReqDao.updateOtherSystemREQ(updatelist);
    		}
    		//关闭
    		if(closelist.size() > 0) {
    			receiveOtherSystemReqDao.updateOtherSystemREQ(closelist);
    		}
    		
    	} else {
    		remap.put("STATUS", "E");
    		remap.put("MESSAGE", "数据空");
    		return remap;
    	}
    	
    	remap.put("STATUS", "S");
    	remap.put("MESSAGE", "成功");
    	return remap;
    }

	/**
	 *
	 *   接收物流信息 过账sap
	 *
	 * @param params
	 * @return
	 */
	@Override
	public String handoverOtherSystemREQ(String params) {
		//System.err.println("进入handoverOtherSystemREQhandoverOtherSystemREQhandoverOtherSystemREQ");
		JSONObject resultMap = new JSONObject();
		if(StringUtils.isBlank(params)){
			resultMap.put("STATUS","E");
			resultMap.put("MESSAGE","参数不能为空！");
		}
		Map map = JSON.parseObject(params,Map.class);
		R result = null;
		try {
			result = businessRemote.handover(map);
		}catch (Exception e){
			e.printStackTrace();
			resultMap.put("STATUS","E");
			resultMap.put("MESSAGE",e.getMessage());
		}
		System.err.println("result================>>>"+result);
		if(result!=null && result.get("code").equals("0")){
			resultMap.put("STATUS","S");
			resultMap.put("MESSAGE",result.get("msg"));
		}else {
			resultMap.put("STATUS","E");
			resultMap.put("MESSAGE",result.get("msg"));
		}

		return resultMap.toString();
	}





}
