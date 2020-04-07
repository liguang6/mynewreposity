package com.byd.wms.business.modules.out.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tempuri.ArrayOfWMSDispatchingComponent;
import org.tempuri.WMSDispatchingComponent;
import org.tempuri.WMSDispatchingReturnResult;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.out.dao.DispatchingDeliveryDao;
import com.byd.wms.business.modules.out.service.DispatchingDeliveryService;

/**
 *
 */
@Service
public class DispatchingDeliveryServiceImpl implements DispatchingDeliveryService {

	@Autowired
	private DispatchingDeliveryDao dispatchingDeliveryDao;

	@Autowired
	private WmsCDocNoService wmsCDocNoService;

	@Autowired
	private UserUtils userUtils;

	@Override
	public PageUtils list(Map<String, Object> params) {
		List<Map<String, Object>> list = dispatchingDeliveryDao.list(params);
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		Iterator<Map<String, Object>> it = list.iterator();
		Map<String, Object> map = new HashMap<String, Object>();
		Object labelNo = null;
		while (it.hasNext()) {
			map = it.next();
			labelNo = map.get("LABEL_NO");
			if (labelNo != null) {
				if (labelNo.toString().indexOf(",") > -1) {
					String[] arr = labelNo.toString().split(",");
					for (int i = 0; i < arr.length; i++) {
						Map<String, Object> m = new HashMap<String, Object>();
						m.putAll(map);
						m.put("LABEL_NO", arr[i]);
						l.add(m);
					}
				}
			}
			it.remove();
		}
		list.addAll(l);
		for (Map<String, Object> ele : list) {
			Map<String, Object> m = new HashMap<String, Object>();
			if (ele.get("LABEL_NO") != null) {
				m = dispatchingDeliveryDao.getLabelInfo(ele);
				if (m != null) {
					ele.put("QUANTY", m.get("BOX_QTY"));
					ele.put("VENDOR_CODE", m.get("LIFNR"));
					ele.put("VENDOR", m.get("LIKTX"));
					ele.put("PRODUCT_NO", m.get("PRODUCT_CODE"));
				}
			}
		}
		Page page = new Query<Map<String, Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(list.size());
		page.setSize(list.size());
		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String delivery(List<Map<String, Object>> params) throws Exception {
		String No = "";
		Map<String, Object> param = new HashMap<>();
		param.put("WMS_DOC_TYPE", "09");
		param.put("WERKS", params.get(0).get("WERKS"));
		Map<String, Object> docNo = wmsCDocNoService.getDocNo(param);
		if (docNo.get("MSG") != null && !"success".equals(docNo.get("MSG"))) {
			throw new RuntimeException(docNo.get("MSG").toString());
		}
		No = docNo.get("docno").toString();
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client wlmsclient = dcf
				.createClient("http://10.8.15.9/WLMSTEST/WebService/WS_WLMS_ToWMS.asmx?wsdl");
		ArrayOfWMSDispatchingComponent arrayOfWMSDispatchingComponent = new ArrayOfWMSDispatchingComponent();
		List<WMSDispatchingComponent> list = arrayOfWMSDispatchingComponent.getWMSDispatchingComponent();
		for (Map<String, Object> ele : params) {
			WMSDispatchingComponent component = new WMSDispatchingComponent();
			list.add(component);
			component.setWMSDELIVERYNO(No);
			component.setDELIVERYNO(ele.get("DELIVERY_NO") == null ? "" : ele.get("DELIVERY_NO").toString());
			component.setDELIVERYITEMNO(ele.get("DELIVERY_ITEM_NO") == null ? "" : ele.get("DELIVERY_ITEM_NO").toString());
			component.setREQUIREMENTNO(ele.get("REQUIREMENT_NO") == null ? "" : ele.get("REQUIREMENT_NO").toString());
			component.setWMSREQUIREMENTNO(ele.get("WMS_REQUIREMENT_NO") == null ? "" : ele.get("WMS_REQUIREMENT_NO").toString());
			component.setWMSREQUIREMENTITEMNO(ele.get("WMS_REQUIREMENT_ITEM_NO") == null ? "" : ele.get("WMS_REQUIREMENT_ITEM_NO").toString());
			component.setWAREHOUSECODE(ele.get("WAREHOUSE_CODE") == null ? "" : ele.get("WAREHOUSE_CODE").toString());
			component.setPLCD(ele.get("PLCD") == null ? "" : ele.get("PLCD").toString());
			component.setLOCD(ele.get("LOCD") == null ? "" : ele.get("LOCD").toString());
			component.setMATNR(ele.get("MATNR") == null ? "" : ele.get("MATNR").toString());
			component.setMATEDS(ele.get("MATEDS") == null ? "" : ele.get("MATEDS").toString());
			component.setQUANTY(Double.valueOf(ele.get("QUANTY").toString()));
			component.setSTATION(ele.get("STATION") == null ? "" : ele.get("STATION").toString());
			component.setPOU(ele.get("POU") == null ? "" : ele.get("POU").toString());
			component.setPRODUCTIONLINE(ele.get("PRODUCTION_LINE") == null ? "" : ele.get("PRODUCTION_LINE").toString());
			component.setBATCH(ele.get("BATCH") == null ? "" : ele.get("BATCH").toString());
			component.setDeliveryRoute(ele.get("DELIVERYROUTE") == null ? "" : ele.get("DELIVERYROUTE").toString());
			component.setLineFeedingRoute(ele.get("LINEFEEDINGROUTE") == null ? "" : ele.get("LINEFEEDINGROUTE").toString());
			component.setDeliveryType(ele.get("DELIVERYTYPE") == null ? "" : ele.get("DELIVERYTYPE").toString());
			component.setDeliveryTime(ele.get("DELIVERYTIME") == null ? "" : ele.get("DELIVERYTIME").toString());
			component.setVENDORCODE(ele.get("VENDOR_CODE") == null ? "" : ele.get("VENDOR_CODE").toString());
			component.setVENDOR(ele.get("VENDOR") == null ? "" : ele.get("VENDOR").toString());
			component.setSTTPNM(ele.get("STTPNM") == null ? "" : ele.get("STTPNM").toString());
			component.setReceivePLCD(ele.get("RECEIVEPLCD") == null ? "" : ele.get("RECEIVEPLCD").toString());
			component.setReceiveLOCD(ele.get("RECEIVELOCD") == null ? "" : ele.get("RECEIVELOCD").toString());
			component.setReceiveLOCDType(ele.get("RECEIVELOCDTYPE") == null ? "" : ele.get("RECEIVELOCDTYPE").toString());
			component.setMOTYPE("ADD");
			component.setProductNo(ele.get("PRODUCT_NO") == null ? "" : ele.get("PRODUCT_NO").toString());
			component.setREMARK(ele.get("RMARK") == null ? "" : ele.get("RMARK").toString());
		}
		Object[] objects = wlmsclient.invoke("Add_F_WMS_DispatchingDelivery", arrayOfWMSDispatchingComponent);
		WMSDispatchingReturnResult wmsDispatchingReturnResult = (WMSDispatchingReturnResult) objects[0];
		String message = wmsDispatchingReturnResult.getMessage();
		ArrayOfWMSDispatchingComponent arrayOfWMSDispatchingComponent2 = wmsDispatchingReturnResult.getWMSDispatchingComponentList();
		List<WMSDispatchingComponent> list2 = arrayOfWMSDispatchingComponent2.getWMSDispatchingComponent();
		StringBuilder builder = new StringBuilder();
		for (WMSDispatchingComponent c : list2) {
			if (StringUtils.isNotBlank(c.getMESSAGE()))
				builder.append(c.getMESSAGE());
		}
		String message1 = builder.toString();
		if ("".equals(message)) {
			Map<String, Object> head = new HashMap<>();
			List<Map<String, Object>> item = new ArrayList<>();
			Map<String, Object> currentUser = userUtils.getUser();
			Map<String, Object> m = (Map<String, Object>) params.get(0);
			head.put("DELIVERY_NO", m.get("DELIVERY_NO"));
			head.put("WH_NUMBER", m.get("PLCD"));
			head.put("WERKS", m.get("PLCD"));
			head.put("STATUS", "00");
			head.put("DELIVERY_TYPE", m.get("DELIVERYTYPE"));
			head.put("DELIVERY_TIME", m.get("DELIVERYTIME"));
			head.put("RECEIVE_PLCD", m.get("RECEIVEPLCD"));
			head.put("RECEIVE_LOCD", m.get("RECEIVELOCD"));
			head.put("RECEIVE_TYPE", m.get("RECEIVELOCDTYPE"));
			head.put("CREATOR", currentUser.get("USERNAME"));
			head.put("CREATE_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			head.put("EDITOR", currentUser.get("USERNAME"));
			head.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			head.put("RMARK", m.get("RMARK"));
			
			int itemNo = 1;
			for (Map<String, Object> ele : params) {
				Map<String, Object> map = new HashMap<>();
				item.add(map);
				map.put("DELIVERY_NO", ele.get("No"));
				map.put("DELIVERY_ITEM_NO", itemNo);itemNo++;
				map.put("ITEM_STATUS", "00");
				map.put("WLMS_DELIVERY_NO", ele.get("DELIVERY_NO"));
				map.put("WLMS_DELIVERY_ITEM_NO", ele.get("DELIVERY_ITEM_NO"));
				map.put("WLMS_REQUIREMENT_NO", ele.get("REQUIREMENT_NO"));
				map.put("REQUIREMENT_NO", ele.get("WMS_REQUIREMENT_NO"));
				map.put("REQUIREMENT_ITEM_NO", ele.get("WMS_REQUIREMENT_ITEM_NO"));
				map.put("TASK_NUM", ele.get("TASK_NUM"));
				map.put("LGORT", ele.get("LOCD"));
				map.put("MATNR", ele.get("MATNR"));
				map.put("MAKTX", ele.get("MATEDS"));
				map.put("QTY", ele.get("QUANTY"));
				map.put("POU", ele.get("POU"));
				map.put("STATION", ele.get("STATION"));
				map.put("LINE_NO", ele.get("PRODUCTION_LINE"));
				map.put("BATCH", ele.get("BATCH"));
				map.put("DELIVERY_ROUTE", ele.get("DELIVERYROUTE"));
				map.put("LINE_FEEDING_ROUTE", ele.get("LINEFEEDINGROUTE"));
				map.put("LIFNR", ele.get("VENDOR_CODE"));
				map.put("LIKTX", ele.get("VENDOR"));
				map.put("SOBKZ", ele.get("STTPNM"));
				map.put("MOTYPE", "ADD");
				map.put("RFID_NO", "");
				map.put("LABEL_NO", ele.get("LABEL_NO"));
				map.put("MOULD_NO", ele.get("MOULD_NO"));
				map.put("CREATOR", currentUser.get("USERNAME"));
				map.put("CREATE_DATE",  DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
				map.put("EDITOR", currentUser.get("USERNAME"));
				map.put("EDIT_DATE",  DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			}                                            
			dispatchingDeliveryDao.insertHead(head);
			dispatchingDeliveryDao.insertItem(item);
		}
		return message;
	}
}
