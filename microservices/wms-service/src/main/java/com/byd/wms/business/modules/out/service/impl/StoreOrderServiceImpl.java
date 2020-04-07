package com.byd.wms.business.modules.out.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.UserUtils;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.impl.CommonServiceImpl;
import com.byd.wms.business.modules.config.service.impl.WmsCWhServiceImpl;
import com.byd.wms.business.modules.out.dao.SendCreateRequirementDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.StoreOrderDao;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementLabelEntity;
import com.byd.wms.business.modules.out.service.StoreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/18
 * @description：
 */

@Service
public class StoreOrderServiceImpl implements StoreOrderService {

    @Autowired
    SendCreateRequirementDao sendCreateRequirementDao;

    @Autowired
    WmsCDocNoService docNoService;
    @Autowired
    OutCommonService commonService;

    @Autowired
    WmsOutRequirementHeadDao headDao;

    @Autowired
    StoreOrderDao storeOrderDao;

    @Transactional(rollbackFor = Exception.class)
    public String callMaterial(String params) throws Exception {
            Map<String, Object> rsMap = receiveOtherSystemREQ(params);
            if(rsMap.size() <= 0){
                throw new Exception("叫料号已存在,请重新输入");
            }
            else if (rsMap.get("STATUS").equals("E")) {
                throw new Exception(rsMap.get("MESSAGE").toString());
            }
            String DISPATCHING_NO = createOutStoreOrder((List<Map<String, Object>>) rsMap.get("addlist"));
            return selectReturnMsg(DISPATCHING_NO);

    }

    //接收第三方系统叫料需求
    @Override
    public Map<String, Object> receiveOtherSystemREQ(String params) {
        Map<String, Object> remap = new HashMap<>();
        if (params != null && !params.equals("")) {
            List<Map<String, Object>> addlist = new ArrayList<>(); //新增准备数据
            List<Map<String, Object>> updatelist = new ArrayList<>(); //修改准备数据
            List<Map<String, Object>> closelist = new ArrayList<>(); //关闭准备数据

            //json字符串转为json对象
            List<Map> jsonList = JSONObject.parseArray(params, Map.class);//解析出 多行

            //查询叫料需求号是否存在
            List<Map<String, Object>> returnList = storeOrderDao.selectReturnMsg(jsonList.get(0).get("DISPATCHING_NO").toString());
            if(returnList.size() > 0){
                return remap;
            }

            for (int i = 0; i < jsonList.size(); i++) {
                Map<String, Object> reqItemMap = jsonList.get(i);

                reqItemMap.put("KANBAN_NO", reqItemMap.get("DISPATCHING_NO") == null ? "" : reqItemMap.get("DISPATCHING_NO"));//叫料需求号
                reqItemMap.put("KANBAN_ITEM", reqItemMap.get("DISPATCHING_ITEM_NO") == null ? "" : reqItemMap.get("DISPATCHING_ITEM_NO"));//叫料行项目
                reqItemMap.put("WH_NUMBER", reqItemMap.get("WH_NUMBER") == null ? "" : reqItemMap.get("WH_NUMBER"));//
                reqItemMap.put("RECEIVE_LGORT", reqItemMap.get("RECEIVE_LGORT") == null ? "" : reqItemMap.get("RECEIVE_LGORT"));//
                reqItemMap.put("MATNR", reqItemMap.get("MATNR") == null ? "" : reqItemMap.get("MATNR"));//
                reqItemMap.put("LIFNR", reqItemMap.get("LIFNR") == null ? "" : reqItemMap.get("LIFNR"));//
                reqItemMap.put("QTY", reqItemMap.get("QTY") == null ? "" : reqItemMap.get("QTY"));//
                reqItemMap.put("BATCH", reqItemMap.get("BATCH") == null ? "" : reqItemMap.get("BATCH"));//
                reqItemMap.put("BUSINESS_NAME", reqItemMap.get("BUSINESS_NAME") == null ? "" : reqItemMap.get("BUSINESS_NAME"));//
                reqItemMap.put("BUSINESS_CODE", reqItemMap.get("BUSINESS_CODE") == null ? "" : reqItemMap.get("BUSINESS_CODE"));//
                reqItemMap.put("STATION", reqItemMap.get("STATION") == null ? "" : reqItemMap.get("STATION"));//
                reqItemMap.put("SPLIT", reqItemMap.get("SPLIT") == null ? "" : reqItemMap.get("SPLIT"));//
                reqItemMap.put("LABEL_NO", reqItemMap.get("LABEL_NO") == null ? "" : reqItemMap.get("LABEL_NO"));//
                reqItemMap.put("CREATE_DATE", reqItemMap.get("CREATE_DATE") == null ? "" : reqItemMap.get("CREATE_DATE"));//
                reqItemMap.put("UPDATE_DATE", reqItemMap.get("UPDATE_DATE") == null ? "" : reqItemMap.get("UPDATE_DATE"));//
                reqItemMap.put("STATUS", reqItemMap.get("STATUS") == null ? "" : reqItemMap.get("STATUS"));//
                reqItemMap.put("DLV_ITEM", reqItemMap.get("DELIVERY_ITEM_NO") == null ? "" : reqItemMap.get("DELIVERY_ITEM_NO"));//配送单号行项目
                reqItemMap.put("WERKS", reqItemMap.get("WERKS") == null ? "" : reqItemMap.get("WERKS"));
//                reqItemMap.put("WERKS", reqItemMap.get("PLCD") == null ? "" : reqItemMap.get("PLCD"));
//                reqItemMap.put("LGORT", reqItemMap.get("LOCD") == null ? "" : reqItemMap.get("LOCD"));
//                reqItemMap.put("MAKTX", reqItemMap.get("MATEDS") == null ? "" : reqItemMap.get("MATEDS"));//物料描述
//                reqItemMap.put("QTY", reqItemMap.get("QUANTITY") == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(reqItemMap.get("QUANTITY"))));//需求数量
//                reqItemMap.put("LIFNR", reqItemMap.get("VERDOR_CODE") == null ? "" : reqItemMap.get("VERDOR_CODE"));//供应商
//                reqItemMap.put("POINT_OF_USE", reqItemMap.get("STATION") == null ? "" : reqItemMap.get("STATION"));//工位
//                reqItemMap.put("LINE_NO", reqItemMap.get("PRODUCTION_LINE") == null ? "" : reqItemMap.get("PRODUCTION_LINE"));//产线
//                reqItemMap.put("DOSAGE", reqItemMap.get("MINQTY") == null ? "" : reqItemMap.get("MINQTY"));//标准用量（总装最小包装）
//                reqItemMap.put("START_SHIPPING_TIME", reqItemMap.get("LATEST_DELIVERY_TIME") == null ? "" : reqItemMap.get("LATEST_DELIVERY_TIME"));//开始运输时间（总装最晚配送时间）
//                reqItemMap.put("DEMAND_TIME", reqItemMap.get("LATEST_DEMAND_TIME") == null ? "" : reqItemMap.get("LATEST_DEMAND_TIME"));//预计生产时间（总装最晚需求时间）
//                reqItemMap.put("EMERGENCY", reqItemMap.get("PRIORITY") == null ? "" : reqItemMap.get("PRIORITY"));//紧急度：1紧急、0正常

                //判断工单类型
                if (reqItemMap.get("MO_NO") != null) {
                    //生产工单
                    reqItemMap.put("REFERENCE_NO", reqItemMap.get("MO_NO"));
                    reqItemMap.put("REFERENCE_TYPE", 0);
                } else if (reqItemMap.get("IO_NO") != null) {//内部订单
                    reqItemMap.put("REFERENCE_NO", reqItemMap.get("IO_NO"));
                    reqItemMap.put("REFERENCE_TYPE", 1);
                } else {//成本中心
                    reqItemMap.put("REFERENCE_NO", reqItemMap.get("COST_CENTER"));
                    reqItemMap.put("REFERENCE_TYPE", 4);
                }


                if (reqItemMap.get("TRANS") == null || reqItemMap.get("TRANS").equals("ADD")) {
                    reqItemMap.put("STATUS", "00");
                    reqItemMap.put("CREATE_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
                    addlist.add(reqItemMap);
                } else {

                    List<Map<String, Object>> reqlist = storeOrderDao.selectOtherSystemREQ(reqItemMap);
                    //状态为未开始才允许修改
                    if (reqlist.size() > 0) {
                        if (reqlist.get(0).get("STATUS").equals("00")) {
                            reqItemMap.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
                            if (reqItemMap.get("TRANS").equals("Update")) {
                                updatelist.add(reqItemMap);
                            } else if (reqItemMap.get("TRANS").equals("Close")) {
                                reqItemMap.put("STATUS", "06");
                                reqItemMap.put("DEL", "X");
                                closelist.add(reqItemMap);
                            }
                        } else if (reqlist.get(0).get("STATUS").equals("06")) {
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
            if (addlist.size() > 0) {
                remap.put("addlist", addlist);
                storeOrderDao.saveOtherSystemREQ(addlist);
            }
            //修改叫料需求
            if (updatelist.size() > 0) {
                storeOrderDao.updateOtherSystemREQ(updatelist);
            }
            //关闭
            if (closelist.size() > 0) {
                storeOrderDao.updateOtherSystemREQ(closelist);
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

    //创建需求表
    @Override
    public String createOutStoreOrder(List<Map<String, Object>> addlist) throws Exception {
            String staffNumber = "";
            if(addlist.get(0).get("staffNumber") != null)
                staffNumber = addlist.get(0).get("staffNumber").toString();
            String DISPATCHING_NO="";
            if (CollectionUtils.isEmpty(addlist.get(0))) {
                throw new IllegalArgumentException("参数为空");
            }
            // 校验必填字段
		/*for (CreateProduceOrderAO order : orderItems) {
			if (StringUtils.isBlank(order.getAUFNR())) {
				throw new IllegalArgumentException("必填字段不能为空");
			}
		}*/
            //SysUserEntity user = ShiroUtils.getUserEntity();
            String requirementNo = docNoService.getDocNo(addlist.get(0).get("WERKS").toString(), WmsDocTypeEnum.OUT_WAREHOURSE.getCode());

            // 创建出库需求头
            WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
            head.setCreator(staffNumber);
            head.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
            head.setRequirementNo(requirementNo);
            head.setWerks(addlist.get(0).get("WERKS") == null ? "" : addlist.get(0).get("WERKS").toString());
            head.setWhNumber(addlist.get(0).get("WHNUMBER") == null ? "" : addlist.get(0).get("WHNUMBER").toString());
            head.setPurpose(addlist.get(0).get("USE") == null ? "" : addlist.get(0).get("USE").toString());
            head.setRequirementType(addlist.get(0).get("BUSINESS_NAME") == null ? "" : addlist.get(0).get("BUSINESS_NAME").toString());
            head.setRequirementStatus(addlist.get(0).get("STATUS") == null ? "" : addlist.get(0).get("STATUS").toString());
            head.setRequiredTime(addlist.get(0).get("REQUIRETIME") == null ? "" : addlist.get(0).get("REQUIRETIME").toString());
            head.setRequiredDate(addlist.get(0).get("REQUIREDATE") == null ? "" : addlist.get(0).get("REQUIREDATE").toString());
            head.setRequiredModel(addlist.get(0).get("SUMMARYMODE") == null ? "" :addlist.get(0).get("SUMMARYMODE").toString());//备料模式


            // 创建行项目
            int requirementItemNo = 1;
            List<Map<String, Object>> listHX = new ArrayList<>();
            List<Map<String, Object>> listTM = new ArrayList<>();
//            List<Map> jsonList = JSONObject.parseArray(JSON.toJSONString(addlist.get(0).get("KANBAN_ITEM")), Map.class);//解析出 多行
            for (int i = 0; i < addlist.size(); i++) {
                Map<String, Object> order = addlist.get(i);
                Map<String, Object> item = new HashMap<>();
                DISPATCHING_NO=order.get("DISPATCHING_NO").toString();
                setCommomPrams(item, order, requirementNo, requirementItemNo);
                item.put("BUSINESS_CODE", (order.get("BUSINESS_CODE") == null ? "" : (addlist.get(0).get("BUSINESS_CODE"))));
                item.put("BUSINESS_NAME", (order.get("BUSINESS_NAME") == null ? "" : (addlist.get(0).get("BUSINESS_NAME"))));
//                //是否需要审批
//                if (commonService.checkApproval(JSON.toJSONString(addlist.get(0).get("WERKS")), item.get("BUSINESS_CODE").toString(), item.get("BUSINESS_NAME").toString())) {
//                    head.setCheckFlag("X");
//                }
                //创建条码
                List<Map> jsonList2 = JSONObject.parseArray(order.get("LABEL_NO").toString(), Map.class);//解析出 多行
                    for (int j = 0; j < jsonList2.size(); j++) {
                    Map<String, Object> orderTM = jsonList2.get(j);
                    Map<String, Object> label = new HashMap<>();
                    label.put("LABEL_NO", orderTM.get("LABEL_NO"));
                    label.put("REQUIREMENT_NO", item.get("REQUIREMENT_NO"));
                    label.put("REQUIREMENT_ITEM_NO", item.get("REQUIREMENT_ITEM_NO"));
                    label.put("EDITOR", orderTM.get("EDITOR"));
                    label.put("EDIT_DATE", orderTM.get("EDIT_DATE"));
                    listTM.add(label);
                }

                listHX.add(item);
            }
            headDao.insert(head);
            storeOrderDao.insertHX(listHX);
            storeOrderDao.insertTM(listTM);
            return DISPATCHING_NO;
    }

    //创建结果返回
    @Override
    public String selectReturnMsg(String dispatching_no) throws Exception{
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> returnList = new ArrayList<>();
            List<Map<String, Object>> item = new ArrayList<>();
            map.put("MSGTY", "S");
            returnList = storeOrderDao.selectReturnMsg(dispatching_no);
            data.put("DISPATCHING_NO", returnList.get(0).get("DISPATCHING_NO"));
            data.put("REQUIREMENT_NO", returnList.get(0).get("REQUIREMENT_NO"));
            for (Map<String, Object> each : returnList) {
                Map<String, Object> tmp = new HashMap<>();
                tmp.put("DISPATCHING_ITEM_NO", each.get("DISPATCHING_ITEM_NO"));
                tmp.put("REQUIREMENT_ITEM_NO", each.get("REQUIREMENT_ITEM_NO"));
                item.add(tmp);
            }
            data.put("item", item);
            map.put("data", data);
            map.put("MSGTY", "S");
        } catch (Exception e) {
            map.put("MSGTY", "E");
            map.put("MSG", e.getMessage());
            throw e;
        }

        return JSON.toJSONString(map);
    }


    //创建出库清单
    @Override
    public String selectOutStoreOrder(String requirement_no) {
        String params;
        Map<String, Object> headMap = storeOrderDao.selectHead(requirement_no);
        List<Map<String, Object>> itemMap = storeOrderDao.selectItem(requirement_no);
        for (Map<String, Object> each : itemMap) {
            List<Map<String, Object>> labelMap = storeOrderDao.selectLabel(each.get("REQUIREMENT_ITEM_NO").toString(), requirement_no);
            each.put("LABEL_NO", labelMap);
        }
        headMap.put("REQUIREMENT_ITEM", itemMap);
        return headMap.toString();
    }


    //设置行项目通用属性
    private void setCommomPrams(Map<String, Object> item, Map<String, Object> order, String requirementNo, int requirementItemNo) {

        item.put("REQUIREMENT_NO", requirementNo);
        item.put("REQUIREMENT_ITEM_NO", order.get("DISPATCHING_ITEM_NO").toString());
        item.put("BUSINESS_NAME", order.get("BUSINESS_NAME"));
        item.put("BUSINESS_TYPE", order.get("BUSINESS_TYPE"));
        item.put("HX_FLAG", order.get("HX_FLAG"));
        item.put("REQ_ITEM_STATUS", order.get("00"));
        item.put("DEL", order.get("DEL"));
        item.put("MATNR", order.get("MATNR"));
        item.put("MAKTX", order.get("MAKTX"));
        item.put("UNIT", order.get("UNIT"));
        item.put("MEINS", order.get("MEINS"));
        item.put("UMREZ", order.get("UMREZ"));
        item.put("UMREN", order.get("UMREN"));
        item.put("QTY", order.get("QTY"));
        item.put("QTY_XJ", order.get("QTY_XJ"));
        item.put("QTY_REAL", order.get("QTY_REAL"));
        item.put("QTY_CANCEL", order.get("QTY_CANCEL"));
        item.put("LGORT", order.get("LGORT"));
        item.put("RECEIVE_LGORT", order.get("RECEIVE_LGORT"));
        item.put("BOX_COUNT", order.get("BOX_COUNT"));
        item.put("SORT_SEQ", order.get("SORT_SEQ"));
        item.put("LIFNR", order.get("LIFNR"));
        item.put("MODEL_GROUP", order.get("MODEL_GROUP"));
        item.put("STATION", order.get("STATION"));
        item.put("PO_NO", order.get("PO_NO"));
        item.put("PO_ITEM_NO", order.get("PO_ITEM_NO"));
        item.put("MO_NO", order.get("MO_NO"));
        item.put("MO_ITEM_NO", order.get("MO_ITEM_NO"));
        item.put("RSNUM", order.get("RSNUM"));
        item.put("RSPOS", order.get("RSPOS"));
        item.put("SO_NO", order.get("SO_NO"));
        item.put("SO_ITEM_NO", order.get("SO_ITEM_NO"));
        item.put("SAP_OUT_NO", order.get("SAP_OUT_NO"));
        item.put("SAP_OUT_ITEM_NO", order.get("SAP_OUT_ITEM_NO"));
        item.put("COST_CENTER", order.get("COST_CENTER"));
        item.put("IO_NO", order.get("IO_NO"));
        item.put("WBS", order.get("WBS"));
        item.put("CUSTOMER", order.get("CUSTOMER"));
        item.put("SAKTO", order.get("SAKTO"));
        item.put("SOBKZ", order.get("SOBKZ"));
        item.put("CREATOR", order.get("CREATOR"));
        item.put("CREATE_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        item.put("EDITOR", order.get("EDITOR"));
        item.put("EDIT_DATE", order.get("EDIT_DATE"));
        item.put("SAP_MATDOC_NO", order.get("SAP_MATDOC_NO"));
        item.put("SAP_MATDOC_ITEM_NO", order.get("SAP_MATDOC_ITEM_NO"));
        item.put("DISPATCHING_NO", order.get("DISPATCHING_NO"));
        item.put("DISPATCHING_ITEM_NO", order.get("DISPATCHING_ITEM_NO"));
        item.put("LINE", order.get("LINE"));
        item.put("MEMO", order.get("MEMO"));
        item.put("AUTYP", order.get("AUTYP"));
        item.put("WH_MANAGER", order.get("WH_MANAGER"));
        item.put("BATCH", order.get("BATCH"));
        item.put("SPLIT", order.get("SPLIT"));

    }

}
