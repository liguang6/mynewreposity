package com.byd.wms.business.modules.kn.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.kn.dao.WmsCBarcodeSterilisationDao;
import com.byd.wms.business.modules.kn.service.WmsCBarcodeSterilisationService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("wmsCBarcodeSterilisationService")
public class WmsCBarcodeSterilisationImpl  implements WmsCBarcodeSterilisationService{
	@Autowired
	private WmsCBarcodeSterilisationDao WmsCBarcodeSterilisationDao;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String credentialsNum = params.get("credentialsNum")==null?null:String.valueOf(params.get("credentialsNum"));
		String credentialsYear = params.get("credentialsYear")==null?null:String.valueOf(params.get("credentialsYear"));
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=WmsCBarcodeSterilisationDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		params.put("orderBy", "id");
		List<Map<String, Object>> list=WmsCBarcodeSterilisationDao.queryBarcodeSterilisation(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

    @Override
    public PageUtils queryOne(Map<String, Object> params) {
		List<Map<String,Object>> mapList=new ArrayList<Map<String,Object>>();
		Map<String, Object> list=WmsCBarcodeSterilisationDao.queryBarcodeSterilisationOne(params);
        Page page=new Query<Map<String,Object>>(params).getPage();
		mapList.add(list);
        page.setRecords(mapList);
        return new PageUtils(page);
    }

	@Override
	public PageUtils saveCoreLabel(Map<String, Object> params) {
		List labels = new ArrayList();
		List<Map<String, Object>> skList=new ArrayList<Map<String, Object>>();
		Map<String,Object> barcodeSterilisation=WmsCBarcodeSterilisationDao.queryBarcodeSterilisationOne(params);;
		barcodeSterilisation.put("WMS_DOC_TYPE", "08");//标签号
		barcodeSterilisation.put("WERKS", barcodeSterilisation.get("WERKS"));
			Double RECEIPT_QTY=Double.valueOf(params.get("qtyNum").toString());
			Double FULL_BOX_QTY=Double.valueOf(params.get("boxNum").toString());
			int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);
			for(int i=1;i<=box_num;i++) {
				Map<String,Object> sk=new HashMap<String,Object>();
//				sk.putAll(m);

				String LABEL_NO="";
				Map<String,Object> doc=null;
				doc=wmsCDocNoService.getDocNo(barcodeSterilisation);
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				LABEL_NO=doc.get("docno").toString();
				labels.add(LABEL_NO);
				String BOX_SN=i+"/"+box_num;
				Double BOX_QTY=FULL_BOX_QTY;//装箱数量，计算得出
				String END_FLAG="0";
				if(i==box_num) {
					BOX_QTY = RECEIPT_QTY-(box_num-1)*FULL_BOX_QTY;
					END_FLAG="X";
				}
                sk.put("WERKS", barcodeSterilisation.get("WERKS"));
				sk.put("LABEL_NO", LABEL_NO);
				sk.put("LABEL_STATUS", "01");
				sk.put("RECEIPT_NO", barcodeSterilisation.get("RECEIPTNO"));
				sk.put("RECEIPT_ITEM_NO", barcodeSterilisation.get("RECEIPTITEMNO")+"");
//				sk.put("TRY_QTY", barcodeSterilisation.get("TRY_QTY"));
//				sk.put("GR_QTY", barcodeSterilisation.get("RECEIPT_QTY"));
				sk.put("SOBKZ", barcodeSterilisation.get("SOBKZ"));
				sk.put("CREATOR", barcodeSterilisation.get("CREATOR"));
				sk.put("CREATE_DATE", barcodeSterilisation.get("CREATEDATE"));
				sk.put("WH_NUMBER", barcodeSterilisation.get("WHNUMBER"));
				sk.put("LGORT", barcodeSterilisation.get("LGORT"));
				sk.put("BOX_SN", BOX_SN);
				sk.put("FULL_BOX_QTY", params.get("boxNum"));
				sk.put("BOX_QTY", BOX_QTY);
				sk.put("END_FLAG", END_FLAG);
                sk.put("MATNR", barcodeSterilisation.get("MATNR"));
                sk.put("MAKTX", barcodeSterilisation.get("MAKTX"));
                sk.put("BIN_CODE", barcodeSterilisation.get("BINCODE"));
                sk.put("UNIT", barcodeSterilisation.get("UNIT"));
                sk.put("BATCH", barcodeSterilisation.get("BATCH"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
				String PRODUCT_DATE = dateFormat.format(new Date());
				sk.put("PRODUCT_DATE", PRODUCT_DATE);
				sk.put("LIFNR", barcodeSterilisation.get("LIFNR"));
				sk.put("LIKTX", barcodeSterilisation.get("LIKTX"));
				sk.put("WMS_NO", barcodeSterilisation.get("WMSNO"));

				skList.add(sk);
			}
			//插入标签
		        wmsInReceiptDao.insertCoreLabel(skList);
			//
				String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
				String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
				int start = 0;int end = 0;
				int count=WmsCBarcodeSterilisationDao.getBarcodeCount(labels);
		        List<Map<String, Object>> list=WmsCBarcodeSterilisationDao.queryBarcode(labels);
				if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
					start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
					end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
				}else {
					end=count;
				}
				params.put("START", start);
				params.put("END", end);
				params.put("orderBy", "id");
				Page page=new Query<Map<String,Object>>(params).getPage();
				page.setRecords(list);
				page.setTotal(list.size());
				page.setSize(Integer.valueOf(pageSize));
				page.setCurrent(Integer.valueOf(pageNo));
				return new PageUtils(page);
	}
}
