package com.byd.wms.business.modules.account.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.dao.WmsAccountReceiptHxPoDao;
import com.byd.wms.business.modules.account.entity.WmsHxPoEntity;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxPoService;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;


@Service("wmsAccountReceiptPoVService")
public class WmsAccountReceiptHxPoServiceImpl extends ServiceImpl<WmsAccountReceiptHxPoDao, WmsHxPoEntity> implements WmsAccountReceiptHxPoService {
	@Autowired
	private WmsAccountReceiptHxPoDao wmsAccountReceiptHxPoDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private CommonService commonService;
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<WmsHxPoEntity> page = this.selectPage(
                new Query<WmsHxPoEntity>(params).getPage(),
                new EntityWrapper<WmsHxPoEntity>()
        );
        return new PageUtils(page);
    }
    
    /**
     * 根据采购订单号+采购订单行项目号更新PO核销信息
     * @param wmsHxPoEntity 
     * @return boolean true/false
     */
    @Override
    public boolean updateByPoNo(WmsHxPoEntity wmsHxPoEntity) {
    	Wrapper wrapper = new EntityWrapper<WmsHxPoEntity>().eq("EBELN", wmsHxPoEntity.getEbeln()).eq("EBELP", wmsHxPoEntity.getEbelp());
    	List<WmsHxPoEntity> list = this.selectList(wrapper);
    	if(null != list && list.size()==1) {
    		wmsHxPoEntity.setId(list.get(0).getId());
    		return this.updateById(wmsHxPoEntity);
    	}else {
    		return false;
    	}
    }
    
    /**
     * 根据工厂、供应商查询危化品物料清单
     * @param params Map WERKS：工厂代码  LIFNR：供应商代码
     * @return List
     * MATNR：料号 GOOD_DATES：保质期
     */
	@Override
	public List<Map<String, Object>> getDangerMatList(Map<String, Object> params) {
		return wmsAccountReceiptHxPoDao.getDangerMatList(params);
	}

	/**
	 * 根据供应商代码，工厂代码获取采购和供应商简称（判断WMS_C_PLANT表是否配置了工厂启用供应商管理）
	 */
	@Override
	public Map<String, Object> getVendorInfo(Map<String, Object> params) {
		
		return wmsAccountReceiptHxPoDao.getVendorInfo(params);
	}
	
	/**
	 * 生成WMS批次信息
	 */
	@Override
	public String setMatBatch(Map<String, Object> params,List<Map<String,Object>> matList) {
		String result = "";
		
		List<Map<String, Object>> condMapList = new ArrayList<Map<String, Object>>();
		
		matList.forEach(k->{
			k=(Map)k;
			
			Map<String, Object> condMap=new HashMap<String,Object>();
			condMap.put("WERKS", k.get("WERKS").toString());
			condMap.put("ASNNO", k.get("ASNNO")==null?"":k.get("ASNNO").toString());
			condMap.put("PO_NO", k.get("PO_NO")==null?"":k.get("PO_NO").toString());
			condMap.put("RECEIPT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));
			
			String DELIVERY_DATE=k.get("DELIVERY_DATE")==null?"":k.get("DELIVERY_DATE").toString();
			String PRODUCT_DATE=k.get("PRODUCT_DATE")==null?"":k.get("PRODUCT_DATE").toString();
			String EFFECT_DATE=k.get("EFFECT_DATE")==null?"":k.get("EFFECT_DATE").toString();
			String MATNR =k.get("MATNR").toString();
			String LIFNR=k.get("LIFNR")==null?"":k.get("LIFNR").toString();
			String DANGER_FLAG=k.get("DANGER_FLAG")==null?"0":k.get("DANGER_FLAG").toString();
			String LGORT=k.get("LGORT")==null?"":k.get("LGORT").toString();
			String F_BATCH=k.get("F_BATCH")==null?"":k.get("F_BATCH").toString();
			condMap.put("DELIVERY_DATE", DELIVERY_DATE);
			condMap.put("PRODUCT_DATE", PRODUCT_DATE);
			condMap.put("EFFECT_DATE", EFFECT_DATE);
			condMap.put("MATNR", MATNR);
			condMap.put("LIFNR", LIFNR);
			condMap.put("DANGER_FLAG", DANGER_FLAG);
			condMap.put("LGORT", LGORT);
			condMap.put("F_BATCH", F_BATCH);
			condMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
			condMapList.add(condMap);
		});
		
		List<Map<String,Object>> retmapList = wmsMatBatchService.getBatch(condMapList);
		
		for (int i=0;i<retmapList.size();i++) {
			Map<String,Object> retmap = retmapList.get(i); 
			/**
			 * 获取批次出错，抛出异常
			 */
			if(retmap.get("MSG")!=null&&!"success".equals(retmap.get("MSG"))) {
				throw new RuntimeException((String) retmap.get("MSG"));
			}
			matList.get(i).put("BATCH", retmap.get("BATCH"));
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getPoItems(Map<String, Object> params) {
		
		return wmsAccountReceiptHxPoDao.getPOItems(params);
	}
	/**
	 * SAP采购订单收货确认（V）-核销业务
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_POV(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		params.put("SEARCH_DATE", curDate);
		params.put("BUSINESS_NAME", "22");//SAP采购订单收料(V)
		params.put("BUSINESS_TYPE", "02");//PO
		params.put("BUSINESS_CLASS", "08");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", "22");//SAP采购订单收料(V)
		cdmap.put("BUSINESS_TYPE", "02");//PO
		cdmap.put("BUSINESS_CLASS", "08");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn == null|| moveSyn.get("WMS_MOVE_TYPE")==null) {
			/**
			 * 系统未配置SAP移动类型
			 */
			return R.error("系统未配置SAP采购订单收货（V）业务类型！");
		}
		SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
		//SAP移动类型JSON字符串拼装
		if(StringUtils.isBlank(SAP_MOVE_TYPE)) {
			return R.error("收货工厂"+WERKS+"未配置SAP采购订单收货（V）业务类型的SAP过账移动类型！");
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", moveSyn.get("WMS_MOVE_TYPE"));
		params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		
		/**
		 * 保存WMS库存
		 */
		params.put("STOCK_TYPE","VIRTUAL_QTY");
		commonService.saveWmsStock(params);
		
		/**
		 * 保存采购订单核销信息
		 */
		List<WmsHxPoEntity> hxPoEntityList = new ArrayList<WmsHxPoEntity>();
		for (Map mat : matList) {
			WmsHxPoEntity hxPoEntity = new WmsHxPoEntity();
			double xs101 = Double.valueOf(mat.get("RECEIPT_QTY").toString());
			double hxQty = Double.valueOf(mat.get("HX_QTY")==null?"0":mat.get("HX_QTY").toString());
			if(null!=mat.get("HXID")&&!"".equals(mat.get("HXID").toString())) {
				hxPoEntity = this.selectById(mat.get("HXID").toString());
				hxQty = hxPoEntity.getHxQty()+xs101;
				xs101 = hxPoEntity.getXs101()+xs101;
				hxPoEntity.setEditor(mat.get("CREATOR").toString());
				hxPoEntity.setEditDate(mat.get("CREATE_DATE").toString());
			}else {
				hxPoEntity.setWerks(mat.get("WERKS").toString());
				hxPoEntity.setWhNumber(mat.get("WH_NUMBER").toString());
				hxPoEntity.setLifnr(mat.get("LIFNR").toString());
				hxPoEntity.setEbeln(mat.get("EBELN")==null?mat.get("PO_NO").toString():mat.get("EBELN").toString());
				hxPoEntity.setEbelp(mat.get("EBELP")==null?mat.get("PO_ITEM_NO").toString():mat.get("EBELP").toString());
				hxPoEntity.setMatnr(mat.get("MATNR").toString());
				hxPoEntity.setMaktx(mat.get("MAKTX").toString());
				hxPoEntity.setMenge(Double.valueOf(mat.get("MAX_MENGE").toString()));
				hxPoEntity.setUnit(mat.get("UNIT").toString());
				hxQty = xs101;
				
				hxPoEntity.setCreator(mat.get("CREATOR").toString());
				hxPoEntity.setCreateDate(mat.get("CREATE_DATE").toString());
			}
			
			hxPoEntity.setLgort(mat.get("LGORT").toString());
			hxPoEntity.setXs101(xs101);
			hxPoEntity.setHxQty(hxQty);
			
			hxPoEntityList.add(hxPoEntity);
		}
		this.insertOrUpdateBatch(hxPoEntityList);
		params.put("matList", matList);
		/**
		 * 产生WMS凭证记录 
		 * 
		 */
		String WMS_NO = commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		
		String SAP_NO = commonService.doSapPost(params);
		
		StringBuilder msg=new StringBuilder();
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		return R.ok(msg.toString());
	}
	
}