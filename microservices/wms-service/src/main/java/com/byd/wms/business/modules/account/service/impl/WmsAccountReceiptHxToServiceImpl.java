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
import com.byd.wms.business.modules.account.dao.WmsAccountReceiptHxToDao;
import com.byd.wms.business.modules.account.entity.WmsHxToEntity;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxToService;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;


@Service("wmsAccountReceiptHxToService")
public class WmsAccountReceiptHxToServiceImpl extends ServiceImpl<WmsAccountReceiptHxToDao, WmsHxToEntity> implements WmsAccountReceiptHxToService {
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WmsAccountReceiptHxToDao wmsAccountReceiptHxToDao;
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<WmsHxToEntity> page = this.selectPage(
                new Query<WmsHxToEntity>(params).getPage(),
                new EntityWrapper<WmsHxToEntity>()
        );
        return new PageUtils(page);
    }  
    
    /**
     * 根据303物料凭证编号+行项目号更新SAP303调拨单核销信息
     * @param wmsHxToEntity 必须包含值：MAT_DOC：303物料凭证编号  MAT_DOC_ITM：行项目号
     * @return boolean
     */
    @Override
    public boolean updateByMatDoc(WmsHxToEntity wmsHxToEntity) {
    	Wrapper wrapper = new EntityWrapper<WmsHxToEntity>().eq("SAP_MATDOC_NO", wmsHxToEntity.getSapMatDocItemNo()).eq("SAP_MATDOC_ITEM_NO", wmsHxToEntity.getSapMatDocItemNo());
    	List<WmsHxToEntity> list = this.selectList(wrapper);
    	if(null != list && list.size()==1) {
    		wmsHxToEntity.setId(list.get(0).getId());
    		return this.updateById(wmsHxToEntity);
    	}else {
    		return false;
    	}
    }
    
    /**
     * 根据303物料凭证行项目，查询关联的核销信息
     * @param toItemList 303物料凭证行项目
     * @return
     */
    @Override
    public List<Map<String, Object>> getHxToListByMatDocItem(List<Map<String,Object>> toItemList){
    	List<Map<String, Object>> list = wmsAccountReceiptHxToDao.getHxToListByMatDocItem(toItemList);
    	for(int i=0;i<toItemList.size();i++) {
    		Map<String, Object> toItemMap = toItemList.get(i);
    		for(int j=0;j<list.size();j++) {
    			Map<String, Object> map = list.get(j);
    			if(map.get("SAP_MATDOC_ITEM_NO")!=null&&
    					map.get("SAP_MATDOC_NO").equals(toItemMap.get("SAP_MATDOC_NO"))
    					&&map.get("SAP_MATDOC_ITEM_NO").equals(toItemMap.get("SAP_MATDOC_ITEM_NO"))) {
    				toItemMap.put("MAKTX", map.get("MAKTX"));
    	    		toItemMap.put("MEINS", map.get("MEINS"));
    	    		toItemMap.put("UMREZ", map.get("UMREZ")==null?"1":map.get("UMREZ"));
    	    		toItemMap.put("UMREN", map.get("UMREN")==null?"1":map.get("UMREN"));
    	    		
    	    		toItemMap.put("HX_TO_ID", map.get("HX_TO_ID")==null?"0":map.get("HX_TO_ID"));
    	    		toItemMap.put("F_WH_NUMBER", map.get("F_WH_NUMBER")==null?"":map.get("F_WH_NUMBER"));
    	    		toItemMap.put("XS305", map.get("XS305")==null?"0":map.get("XS305"));
    	    		toItemMap.put("XS306", map.get("XS306")==null?"0":map.get("XS306"));
    	    		
    	    		toItemMap.put("HX_QTY_XS", map.get("HX_QTY_XS")==null?"0":map.get("HX_QTY_XS"));//
    	    		if(Double.parseDouble(toItemMap.get("HX_QTY_XS").toString())>0) {
    	    			toItemMap.put("RECEIPT_FLAG", "X");
    	    		}else {
    	    			toItemMap.put("RECEIPT_FLAG", "0");
    	    		}
    	    		break;
    			}
    		}
    	}
    	/*for(int i=0;i<list.size();i++) {
    		Map<String, Object> toItemMap = toItemList.get(i);
    		Map<String, Object> map = list.get(i);
    		toItemMap.put("MAKTX", map.get("MAKTX"));
    		toItemMap.put("MEINS", map.get("MEINS"));
    		toItemMap.put("UMREZ", map.get("UMREZ")==null?"1":map.get("UMREZ"));
    		toItemMap.put("UMREN", map.get("UMREN")==null?"1":map.get("UMREN"));
    		
    		toItemMap.put("HX_TO_ID", map.get("HX_TO_ID")==null?"0":map.get("HX_TO_ID"));
    		toItemMap.put("F_WH_NUMBER", map.get("F_WH_NUMBER")==null?"":map.get("F_WH_NUMBER"));
    		toItemMap.put("XS305", map.get("XS305")==null?"0":map.get("XS305"));
    		toItemMap.put("HX_QTY_XS", map.get("HX_QTY_XS")==null?"0":map.get("HX_QTY_XS"));//
    		if(Double.parseDouble(toItemMap.get("HX_QTY_XS").toString())>0) {
    			toItemMap.put("RECEIPT_FLAG", "X");
    		}else {
    			toItemMap.put("RECEIPT_FLAG", "0");
    		}
    	}*/

    	return toItemList;
    }
	
	/**
	 * 303调拨收货确认（V）-核销业务
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_TOV(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		String WERKS = params.get("WERKS").toString();//收货工厂
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		params.put("SEARCH_DATE", curDate);
		params.put("BUSINESS_NAME", "24");//SAP采购订单收料(V)
		params.put("BUSINESS_TYPE", "08");//物料凭证
		params.put("BUSINESS_CLASS", "08");
		
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", "24");//SAP采购订单收料(V)
		cdmap.put("BUSINESS_TYPE", "08");//物料凭证
		cdmap.put("BUSINESS_CLASS", "08");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn = commonDao.getMoveAndSyn(cdmap);
		if(moveSyn == null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			/**
			 * 系统未配置SAP移动类型
			 */
			return R.error("系统未配置调拨收货（V）业务类型！");
		}
		SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
		//SAP移动类型JSON字符串拼装
		if(StringUtils.isBlank(SAP_MOVE_TYPE)) {
			return R.error("收货工厂"+WERKS+"未配置303调拨收货(V)业务类型的SAP过账移动类型！");
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
		 * 保存303调拨收货（V）核销信息
		 */
		List<WmsHxToEntity> hxToEntityList = new ArrayList<WmsHxToEntity>();
		for (Map<String,Object> mat : matList) {
			mat.put("QTY_WMS", mat.get("RECEIPT_QTY"));
			mat.put("QTY_SAP", mat.get("RECEIPT_QTY"));
			mat.put("BATCH_SAP",mat.get("BATCH"));
			
			WmsHxToEntity hxToEntity = new WmsHxToEntity();
			double XS305 = Double.valueOf(mat.get("RECEIPT_QTY").toString());
			double hxQtyXs = Double.valueOf(mat.get("HX_QTY_XS")==null?"0":mat.get("HX_QTY_XS").toString());
			if(null!=mat.get("HX_TO_ID")&&!"".equals(mat.get("HX_TO_ID").toString()) && !"0".equals(mat.get("HX_TO_ID").toString())) {
				//更新
				hxToEntity = this.selectById(mat.get("HX_TO_ID").toString());
				hxQtyXs = hxToEntity.getHxQtyXs()+XS305;
				XS305 += hxToEntity.getXs305();
				hxToEntity.setEditor(mat.get("CREATOR").toString());
				hxToEntity.setEditDate(mat.get("CREATE_DATE").toString());
			}else {
				hxToEntity.setWerks(mat.get("WERKS").toString());
				hxToEntity.setWhNumber(mat.get("WH_NUMBER").toString());
				hxToEntity.setLifnr(mat.get("LIFNR").toString());
				hxToEntity.setfWerks(mat.get("F_WERKS").toString());
				hxToEntity.setfWhNumber(mat.get("F_WH_NUMBER").toString());
				hxToEntity.setSapMatDocNo(mat.get("SAP_MATDOC_NO").toString());
				hxToEntity.setSapMatDocItemNo(mat.get("SAP_MATDOC_ITEM_NO").toString());
				hxToEntity.setMatnr(mat.get("MATNR").toString());
				hxToEntity.setMaktx(mat.get("MAKTX").toString());
				hxToEntity.setEntryQnt(Double.valueOf(mat.get("ENTRY_QNT").toString()));
				hxToEntity.setEntryUom(mat.get("UNIT").toString());
				hxQtyXs = XS305;
				
				hxToEntity.setCreator(mat.get("CREATOR").toString());
				hxToEntity.setCreateDate(mat.get("CREATE_DATE").toString());
			}

			hxToEntity.setXs305(XS305);
			hxToEntity.setHxQtyXs(hxQtyXs);
			
			hxToEntityList.add(hxToEntity);
		}
		
		this.insertOrUpdateBatch(hxToEntityList);
		params.put("matList", matList);
		/**
		 * 产生WMS凭证记录 
		 * 
		 */
		String WMS_NO = commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		
		for (Map<String,Object> mat : matList) {
			mat.put("LIFNR", null);
		}
		
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