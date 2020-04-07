package com.byd.wms.business.modules.account.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.byd.wms.business.modules.account.dao.WmsAccountReceiptHxDnDao;
import com.byd.wms.business.modules.account.entity.WmsHxDnEntity;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxDnService;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;


@Service("wmsAccountReceiptHxDnService")
public class WmsAccountReceiptHxDnServiceImpl extends ServiceImpl<WmsAccountReceiptHxDnDao, WmsHxDnEntity> implements WmsAccountReceiptHxDnService {
	@Autowired
	private WmsAccountReceiptHxDnDao wmsAccountReceiptHxDnDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private CommonService commonService;
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<WmsHxDnEntity> page = this.selectPage(
                new Query<WmsHxDnEntity>(params).getPage(),
                new EntityWrapper<WmsHxDnEntity>()
        );
        return new PageUtils(page);
    }  
    
    /**
     * 根据交货单编号+交货单行项目号更新SAP交货单核销信息
     * @param wmsHxDnEntity 必须包含值：VBELN：交货单号  POSNR：交货单行项目号
     * @return boolean
     */
    @Override
    public boolean updateByDnNo(WmsHxDnEntity wmsHxDnEntity) {
    	Wrapper wrapper = new EntityWrapper<WmsHxDnEntity>().eq("VBELN", wmsHxDnEntity.getVbeln()).eq("POSNR", wmsHxDnEntity.getPosnr());
    	List<WmsHxDnEntity> list = this.selectList(wrapper);
    	if(null != list && list.size()==1) {
    		wmsHxDnEntity.setId(list.get(0).getId());
    		return this.updateById(wmsHxDnEntity);
    	}else {
    		return false;
    	}
    }
	
    /**
     * 根据供应商代码获取供应商信息
     * @param LIFNR 供应商编号
     * @return
     */
    @Override
    public Map<String,Object> getSapVendorByNo(String LIFNR){
    	return wmsAccountReceiptHxDnDao.getSapVendorByNo(LIFNR);
    }
    
    /**
     * 根据交货单行项目，查询关联的采购订单信息和SAP交货单 虚收101数量，并校验物料信息是否已同步到WMS系统
     * @param dnItemList 交货单行项目
     * @return
     */
    @Override
    public List<Map<String, Object>> getPoItemListByDnItem(List<Map<String,Object>> dnItemList,Map<String,Object> params){
    	List<Map<String, Object>> list = wmsAccountReceiptHxDnDao.getPoItemListByDnItem(dnItemList,params);
    	for (Map<String, Object> dnItemMap : dnItemList) {
    		dnItemMap.put("F_LGORT", dnItemMap.get("LGORT"));
    		dnItemMap.put("JP_LGORT", dnItemMap.get("LGORT"));
    		String VGBEL = dnItemMap.get("VGBEL").toString().replaceAll("^(0+)", "");//采购订单号
    		String VGPOS = dnItemMap.get("VGPOS").toString().replaceAll("^(0+)", "");//采购订单行项目号
			for (Map<String, Object> map : list) {
				String EBELN = map.get("EBELN").toString().replaceAll("^(0+)", "");//采购订单号
				String EBELP = map.get("EBELP").toString().replaceAll("^(0+)", "");//采购订单行项目号
				if(VGBEL.equals(EBELN) && VGPOS.endsWith(EBELP)) {
					//dnItemMap.put("S_LGORT", dnItemMap.get("LGORT"));
					dnItemMap.put("S_LGORT", map.get("LGORT"));//采购订单库位
					dnItemMap.put("EBELN", map.get("EBELN"));//采购订单号
					dnItemMap.put("EBELP", map.get("EBELP"));//采购订单行项目号
					dnItemMap.put("BEDNR", map.get("BEDNR"));//需求跟踪号
					dnItemMap.put("AFNAM", map.get("AFNAM"));//需求者/请求者
					dnItemMap.put("MAKTX", map.get("MAKTX"));//物料描述 
					dnItemMap.put("XS101T", map.get("XS101T"));//虚收
					dnItemMap.put("FULL_BOX_QTY", map.get("FULL_BOX_QTY"));//虚收
					break;
				}
			}
		}
    	return dnItemList;
    }
    
	/**
	 * SAP交货单收货确认（V）-核销业务
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_DNV(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		String WERKS = params.get("WERKS").toString();//收货工厂
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		params.put("SEARCH_DATE", curDate);
		params.put("BUSINESS_NAME", "23");//SAP采购订单收料(V)
		params.put("BUSINESS_TYPE", "06");//PO
		params.put("BUSINESS_CLASS", "08");
		
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", "23");//SAP采购订单收料(V)
		cdmap.put("BUSINESS_TYPE", "06");//PO
		cdmap.put("BUSINESS_CLASS", "08");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			/**
			 * 系统未配置SAP移动类型
			 */
			return R.error("系统未配置SAP交货单收货（V）的SAP过账移动类型！");
		}
		SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
		//SAP移动类型JSON字符串拼装
		if(StringUtils.isBlank(SAP_MOVE_TYPE)) {
			return R.error("收货工厂"+WERKS+"未配置SAP交货单收货（V）业务类型的SAP过账移动类型！");
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
		 * 保存SAP交货单收货（V）核销信息
		 */
		List<WmsHxDnEntity> hxDnEntityList = new ArrayList<WmsHxDnEntity>();
		for (Map<String,Object> mat : matList) {
			mat.put("QTY_WMS", mat.get("RECEIPT_QTY"));
			mat.put("QTY_SAP", mat.get("RECEIPT_QTY"));
			mat.put("BATCH_SAP",mat.get("BATCH"));
			mat.put("SAP_OUT_NO", mat.get("VBELN"));
			mat.put("SAP_OUT_ITEM_NO", mat.get("POSNR"));
			
			mat.put("REVERSAL_FLAG", "X");
			mat.put("CANCEL_FLAG", "X");
			
			WmsHxDnEntity hxDnEntity = new WmsHxDnEntity();
			double xs101t = Double.valueOf(mat.get("RECEIPT_QTY").toString());
			double hxQtyXs = Double.valueOf(mat.get("HX_QTY_XS")==null?"0":mat.get("HX_QTY_XS").toString());
			if(null!=mat.get("HXID")&&!"".equals(mat.get("HXID").toString())) {
				hxDnEntity = this.selectById(mat.get("HXID").toString());
				hxQtyXs = hxDnEntity.getHxQtyXs()+xs101t;
				hxDnEntity.setEditor(mat.get("CREATOR").toString());
				hxDnEntity.setEditDate(mat.get("CREATE_DATE").toString());
			}else {
				hxDnEntity.setVbeln(mat.get("VBELN").toString());
				hxDnEntity.setPosnr(mat.get("POSNR").toString());
				hxDnEntity.setLifnr(mat.get("LIFNR").toString());
				hxDnEntity.setEbeln(mat.get("EBELN")==null?mat.get("PO_NO").toString():mat.get("EBELN").toString());
				hxDnEntity.setEbelp(mat.get("EBELP")==null?mat.get("PO_ITEM_NO").toString():mat.get("EBELP").toString());
				hxDnEntity.setMatnr(mat.get("MATNR").toString());
				hxDnEntity.setMaktx(mat.get("MAKTX").toString());
				hxDnEntity.setLfimg(Double.valueOf(mat.get("LFIMG").toString()));
				hxDnEntity.setUnit(mat.get("UNIT").toString());
				hxQtyXs = xs101t;
				
				hxDnEntity.setCreator(mat.get("CREATOR").toString());
				hxDnEntity.setCreateDate(mat.get("CREATE_DATE").toString());
			}
			
			hxDnEntity.setWerks(mat.get("WERKS").toString());
			hxDnEntity.setWhNumber(mat.get("WH_NUMBER").toString());
			hxDnEntity.setCancelFlag("0");
			hxDnEntity.setXs101t(xs101t);
			hxDnEntity.setHxQtyXs(hxQtyXs);
			
			hxDnEntityList.add(hxDnEntity);
		}
		Iterator<WmsHxDnEntity> hxDnIt = hxDnEntityList.iterator();
		while(hxDnIt.hasNext()) {
			WmsHxDnEntity wmsHxDnEntity = hxDnIt.next();
			while(hxDnIt.hasNext()) {
				WmsHxDnEntity wmsHxDnEntity2 = hxDnIt.next();
				if(wmsHxDnEntity2.getVbeln().equals(wmsHxDnEntity.getVbeln()) && wmsHxDnEntity2.getPosnr().equals(wmsHxDnEntity.getPosnr())) {
					//重复行 合并
					wmsHxDnEntity.setXs101t(wmsHxDnEntity.getXs101t()+wmsHxDnEntity2.getXs101t());
					wmsHxDnEntity.setHxQtyXs(wmsHxDnEntity.getHxQtyXs()+wmsHxDnEntity2.getHxQtyXs());
					hxDnEntityList.remove(wmsHxDnEntity2);
					break;
				}
			}
		}
		this.insertOrUpdateBatch(hxDnEntityList);
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