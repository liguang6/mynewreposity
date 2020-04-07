package com.byd.wms.business.modules.kn.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.*;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.config.entity.WmsCMatDangerEntity;
import com.byd.wms.business.modules.config.service.WmsCMatDangerService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.kn.dao.WmsKnLabelRecordDao;
import com.byd.wms.business.modules.kn.dao.WmsKnStorageMoveDao;
import com.byd.wms.business.modules.kn.entity.WmsKnLabelRecordEntity;
import com.byd.wms.business.modules.kn.entity.WmsKnStorageMoveEntity;
import com.byd.wms.business.modules.kn.service.WmsKnLabelRecordService;
import com.byd.wms.business.modules.kn.service.WmsKnStorageMoveService;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service("wmsKnLabelRecordService")
public class WmsKnLabelRecordImpl implements WmsKnLabelRecordService {
	@Autowired
	private WmsKnLabelRecordDao wmsKnLabelRecordDao;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private WmsCoreMatBatchService wmsCoreMatBatchService;
	@Autowired
	private WmsCMatDangerService wmsCMatDangerService;

	@Override
	public void save(Map map) {
		wmsKnLabelRecordDao.save(map);
	}

	@Override
	public PageUtils queryByCf(Map params) {
		List<Map> lp = JSONObject.parseArray(params.get("params").toString(), Map.class);
		Map<String,Object> barcodeSterilisation=new HashMap<>();
		barcodeSterilisation.put("WMS_DOC_TYPE", "08");//标签号
		barcodeSterilisation.put("WERKS", lp.get(0).get("WERKS"));
		String LABEL_NO="";
		Map<String,Object> doc=null;
		doc=wmsCDocNoService.getDocNo(barcodeSterilisation);
		if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
			throw new RuntimeException(doc.get("MSG").toString());
		}
		LABEL_NO=doc.get("docno").toString();
		List list = new ArrayList();
		int cxQty = Integer.parseInt(params.get("numNo").toString());
		List<Map> matListMap = JSONObject.parseArray(params.get("params").toString(), Map.class);
		int boxQty = Integer.parseInt(matListMap.get(0).get("BOX_QTY").toString());
		int syQty = boxQty - cxQty;
		HashMap hm = (HashMap) matListMap.get(0);
		hm.put("BOX_QTY",syQty);
		list.add(hm);
		HashMap hm2 = new HashMap();
		hm2.putAll((HashMap) matListMap.get(0));
		hm2.put("BOX_QTY",cxQty);
		hm2.put("LABEL_NO",LABEL_NO);
		hm2.put("ID","");
		list.add(hm2);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(list.size());
		return  new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<Map> saveByCf(Map params) {
		List<Map> matListMap = JSONObject.parseArray(params.get("params").toString(), Map.class);
		//插入标签
		matListMap.get(0).put("EDITOR",userUtils.getUser().get("USERNAME"));
		matListMap.get(0).put("EDIT_DATE",new SimpleDateFormat("yyyy-MM-dd ").format(new Date()));
		wmsKnLabelRecordDao.updateBfCf(matListMap.get(0));
		matListMap.get(1).put("F_LABEL_NO",matListMap.get(0).get("LABEL_NO"));
		matListMap.get(1).put("PRODUCT_DATE",new SimpleDateFormat("yyyy-MM-dd ").format(new Date()));
		matListMap.get(1).put("CREATOR",userUtils.getUser().get("USERNAME"));
		matListMap.get(1).put("CREATE_DATE",new SimpleDateFormat("yyyy-MM-dd ").format(new Date()));
		wmsKnLabelRecordDao.insertByCf(matListMap.get(1));
		return matListMap;
	}

	@Override
	public Map queryById(Long id) {
		return wmsKnLabelRecordDao.queryById(id);
	}

	@Override
	public void updateLabel(Map map) {
		map.put("EDITOR",userUtils.getUser().get("USERNAME"));
		map.put("EDIT_DATE",new SimpleDateFormat("yyyy-MM-dd ").format(new Date()));
		wmsKnLabelRecordDao.updateLabel(map);
	}

	@Override
	public void deleteLabel(Map map) {
		wmsKnLabelRecordDao.deleteLabel(map);
	}


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		
		List resultList = new ArrayList<>();
		String wmsNo = params.get("wmsNo")==null?"":params.get("wmsNo").toString();
		if(wmsNo.length()>0) {
			List<Map<String, Object>> labelList = wmsKnLabelRecordDao.getLabelBywmsNo(wmsNo); 
			if(labelList.size()>0) {
				for(Map<String, Object> map : labelList) {
					String label = map.get("LABEL_NO")==null?"":map.get("LABEL_NO").toString();
					if(label.length()>0) {
						if(label.indexOf(",")==-1) {
							resultList.add(label);
						}else {
							resultList.addAll(Arrays.asList(label.split(",")));
						}
					}
				}
			}
		}
		if(resultList.size()>0) 
			params.put("labelList", resultList);
		
		
		List<Map<String,Object>> list=wmsKnLabelRecordDao.getLabelRecordList(params);
		if(list==null || list.size()==0){
			pageNo = "1";
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
			params.put("start", start);params.put("end", end);
			list=wmsKnLabelRecordDao.getLabelRecordList(params);
		}
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsKnLabelRecordDao.getLabelRecordCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

    /**
     * 二部 根据采购订单打印条码-新增标签页面采购订单查询
     */
	@Override
	public PageUtils poQueryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;
		int end = 6000;
		start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
		end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> list=wmsKnLabelRecordDao.getPoList(params);
		//将前台仓库号传入结果
        LocalDate today = LocalDate.now();
		list.forEach(item-> {
		    item.put("WH_NUMBER",params.get("WH_NUMBER"));
            item.put("PRODUCT_DATE",today);
            if(item.get("PRFRQ")!= null && !item.get("PRFRQ").toString().isEmpty()) {
                LocalDate effect = today.plusDays(Long.parseLong(item.get("PRFRQ").toString()));
                item.put("EFFECT_DATE",effect);
            }

		});
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsKnLabelRecordDao.getPOCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

    /**
     * 二部 根据采购订单打印条码-保存标签数据
     */
    public R saveCoreLabel(Map<String, Object> params) {
        String BUSINESS_NAME = "02";//收货单
        String LABEL_STATUS = "00";//00创建
        String QC_RESULT_CODE = null;
        //从行项目中获取收货单号和收货单行项目号
        JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
        List<Map<String, Object>> list = new ArrayList<>();
        String labels = "";
        for (Object obj : jarr) {
            Map map = JSON.parseObject(obj.toString(), Map.class);

            Double RECEIPT_QTY = Double.valueOf(map.get("MENGE").toString());
            Double FULL_BOX_QTY = Double.valueOf(map.get("FULL_BOX_QTY").toString());
            int box_num = (int) Math.ceil(RECEIPT_QTY / FULL_BOX_QTY);

            for (int i = 1; i <= box_num; i++) {
                Map<String, Object> sk = new HashMap();
                //生成LABEL_NO
                String LABEL_NO = "";
                Map<String, Object> doc = null;
                Map<String, Object> docParam = new HashMap<String, Object>();
                docParam.put("WMS_DOC_TYPE", BUSINESS_NAME);//收货单
                docParam.put("WERKS", map.get("WERKS").toString());
                doc = wmsCDocNoService.getDocNo(docParam);
                if (doc.get("MSG") != null && !"success".equals(doc.get("MSG"))) {
                    throw new RuntimeException(doc.get("MSG").toString());
                }
                LABEL_NO = doc.get("docno").toString();
                labels += LABEL_NO +",";
                String BOX_SN = i + "/" + box_num;
                Double BOX_QTY = FULL_BOX_QTY;//装箱数量，计算得出
                String END_FLAG = "0";//尾箱
                if (i == box_num && i != 1) {
                    BOX_QTY = RECEIPT_QTY - (box_num - 1) * FULL_BOX_QTY;
                    if (BOX_QTY.compareTo(FULL_BOX_QTY) != 0) {
                        END_FLAG = "X";
                    }
                }
                sk.put("LABEL_NO", LABEL_NO);
                sk.put("LABEL_STATUS", LABEL_STATUS);
                sk.put("CREATOR", params.get("USERNAME").toString());
                sk.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
                sk.put("BOX_SN", BOX_SN);
                sk.put("FULL_BOX_QTY", FULL_BOX_QTY);
                sk.put("BOX_QTY", BOX_QTY);
                sk.put("END_FLAG", END_FLAG);
                sk.put("DEL", 0);
                sk.putAll(map);

                //确定批次规则 ：库位LGORT、危化品DANGER_FLAG、业务类型BUSINESS_NAME、删除标记DEL
				sk.put("BUSINESS_NAME",BUSINESS_NAME);
				Map<String, Object> danger = new HashMap<String, Object>();
				danger.put("matnr", map.get("MATNR").toString());
				danger.put("werks", map.get("WERKS").toString());
				danger.put("lifnr", map.get("LIFNR").toString());
				PageUtils page = wmsCMatDangerService.queryPage(danger);
				if(page.getList().size() == 0){
					sk.put("DANGER_FLAG","0");
				} else{
					char danger_flag = ((WmsCMatDangerEntity) page.getList().get(0)).getDangerFlag();
					sk.put("DANGER_FLAG",String.valueOf(danger_flag));
				}
				//二部先创建收货单，后收货，所以在生产批次是没有收货日期，默认收货日期等于生产日期
				sk.put("RECEIPT_DATE",map.get("PRODUCT_DATE"));
				//生产批次
				List<Map<String, Object>> recepitInfo = new ArrayList<>();
				recepitInfo.add(sk);
				List<Map<String, Object>> retmapList = wmsCoreMatBatchService.getBatch(recepitInfo);
				for(Map<String,Object> retmap:retmapList){
					if(retmap.get("MSG")!=null&&!"success".equals(retmap.get("MSG"))) {
						throw new RuntimeException((String) retmap.get("MSG"));
					}
				}
				sk.put("BATCH", retmapList.get(0).get("BATCH").toString());
                list.add(sk);
            }
        }
        if (list.size() > 0) {
			labels = labels.substring(0, labels.length() -1);

			wmsKnLabelRecordDao.insertCoreLabel(list);
        }
        return R.ok().put("labels",labels);
    }

	@Override
	public List<Map<String, Object>> getLabelList(Map<String, Object> params) {

		params.put("LABEL_NO_LIST",params.get("LABEL_NOS").toString().split(","));
		return wmsKnLabelRecordDao.getLabelList(params);
	}

	@Override
	public List<Map<String, Object>> updateEffectDate(Map<String, Object> params) {
		List<Map<String, Object>> resultList= new ArrayList<Map<String, Object>>();
		String labelListStr = params.get("labelListBD") == null ? null : params.get("labelListBD").toString();
		String TEMP_SIZE = params.get("labelSizeBD") == null ? "" : params.get("labelSizeBD").toString();
		String TEMP_TYPE = params.get("labelTypeBD") == null ? "" : params.get("labelTypeBD").toString();
		String LIKTX = params.get("LIKTX") == null ? "" : params.get("LIKTX").toString();
		List<Map> mapList = JSONObject.parseArray(labelListStr, Map.class);
		
		for(Map m : mapList) {
			String WERKS = m.get("WERKS") == null ? "" : m.get("WERKS").toString();
			String LIFNR = m.get("LIFNR") == null ? "" : m.get("LIFNR").toString();
			String MATNR = m.get("MATNR") == null ? "" : m.get("MATNR").toString();
			String BATCH = m.get("BATCH") == null ? "" : m.get("BATCH").toString();
			String PRODUCT_DATE = m.get("PRODUCT_DATE") == null ? "" : m.get("PRODUCT_DATE").toString();
			String F_BATCH = m.get("F_BATCH") == null ? "" : m.get("F_BATCH").toString();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("WERKS", WERKS);
			//param.put("LIFNR", LIFNR);
			param.put("MATNR", MATNR);
			param.put("PRODUCT_DATE", PRODUCT_DATE);
			param.put("BATCH", BATCH);
			//param.put("F_BATCH", F_BATCH);
			//获取物料有效期
			List<Map<String,Object>> list=wmsKnLabelRecordDao.getMatEffectList(param);
			for(Map<String,Object> n:list) {
				String effect_date=n.get("EFFECT_DATE")==null?null:n.get("EFFECT_DATE").toString();
				String update_date=n.get("UPDATE_DATE")==null?null:n.get("UPDATE_DATE").toString();
				m.put("EFFECT_DATE", update_date);
				//是否更新有效期
				int updateCount=0;
				int updateCountA=0;
				if(effect_date==null&&update_date!=null) {
					param.put("EFFECT_DATE", update_date);
					updateCount=wmsKnLabelRecordDao.updateMatEffect(param);
					updateCountA=wmsKnLabelRecordDao.updateLabelMatEffect(param);
					
				}else if(effect_date!=null&&update_date!=null&&!effect_date.equals(update_date)){
					param.put("EFFECT_DATE", update_date);
					updateCount=wmsKnLabelRecordDao.updateMatEffect(param);
					updateCountA=wmsKnLabelRecordDao.updateLabelMatEffect(param);
				}
				if(updateCount<=0||updateCountA<=0) {
					System.out.println("有效期更新失败");
				}				
			}
			resultList.add(m);
		}
		
		return resultList;
	}
}
