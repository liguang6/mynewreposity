package com.byd.wms.webservice.ws.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.StringUtils;
import com.byd.wms.webservice.ws.dao.EwmServiceDao;
import com.byd.wms.webservice.ws.entity.EwmLableEntity;
import com.byd.wms.webservice.ws.entity.WmsKnLabelRecordEntity;
import com.byd.wms.webservice.ws.service.WmsWebServiceEwmService;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;


@Service
@WebService(serviceName = "WmsWebService", // 与接口中指定的name一致
        targetNamespace = "http://service.ws.webservice.wms.byd.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.byd.wms.webservice.ws.service.WmsWebServiceEwmService" // 接口地址
)
public class WmsWebServiceEwmServiceImpl extends ServiceImpl<EwmServiceDao, WmsKnLabelRecordEntity> implements WmsWebServiceEwmService {

	/**
     * EWM-WMS条码共享
     */
	@Override
	public String ewm2WmsByLabel(String param) {

		JSONObject res = new JSONObject();
		JSONArray jsonArray = JSON.parseArray(param);
    	List labels = new ArrayList();

		try {
			// 解析参数
			for(Object obj : jsonArray){
				WmsKnLabelRecordEntity wmsLabel = new WmsKnLabelRecordEntity();
				EwmLableEntity ewmLabel = JSON.parseObject(obj.toString(), EwmLableEntity.class);

				if(ewmLabel.getTMIDN()== null || ewmLabel.getTMIDN().isEmpty()){
					res.put("STATS","E");
					res.put("MSG", "\"TMIDN\" is empty");
					return res.toString();
				}
				if(ewmLabel.getWHMNO()== null || ewmLabel.getWHMNO().isEmpty()){
					res.put("STATS","E");
					res.put("MSG", "\"WHMNO\" is empty");
					return res.toString();
				}
				wmsLabel.setLabel_no(ewmLabel.getTMIDN());
				wmsLabel.setF_label_no(ewmLabel.getTMIDN_F());
				wmsLabel.setWerks(ewmLabel.getWHMNO());
				wmsLabel.setMatnr(ewmLabel.getMATNR());
				wmsLabel.setLifnr(ewmLabel.getLIFNR());
				wmsLabel.setLiktx(ewmLabel.getLIFNR_TXT());
				wmsLabel.setPo_no(ewmLabel.getPONUM());
				wmsLabel.setPo_item_no(ewmLabel.getPOITM());
				wmsLabel.setBox_qty(ewmLabel.getTMQTY());
				wmsLabel.setUnit(ewmLabel.getMEINS());
				wmsLabel.setProduct_date(ewmLabel.getPDATE());
				wmsLabel.setEffect_date(ewmLabel.getVALTO());
				wmsLabel.setF_batch(ewmLabel.getVBTCH());
				wmsLabel.setBatch(ewmLabel.getBYDBTH());
				wmsLabel.setRemark(ewmLabel.getRMARK());

				wmsLabel.setDel("0");
				wmsLabel.setLabel_status("00");
				wmsLabel.setSobkz((ewmLabel.getBSART() == null || ewmLabel.getBSART().isEmpty()) ? "Z" : ewmLabel.getBSART());
				wmsLabel.setLgort((ewmLabel.getSKUWEI() == null || ewmLabel.getSKUWEI().isEmpty()) ? "00ZJ" : ewmLabel.getSKUWEI());
				wmsLabel.setMaktx((ewmLabel.getMAKTX() == null || ewmLabel.getMAKTX().isEmpty()) ? this.getMaktx(ewmLabel.getMAKTX()) : ewmLabel.getMAKTX() );
				wmsLabel.setLiktx((ewmLabel.getLIFNR_TXT() == null || ewmLabel.getLIFNR_TXT().isEmpty()) ? this.getLiktx(ewmLabel.getLIFNR()) : ewmLabel.getLIFNR_TXT());

			labels.add(wmsLabel);

			}
			this.insertOrUpdateBatch(labels);

			res.put("STATS","S");
			res.put("MSG", "SUCCESS");

			return res.toString();

		} catch (Exception e) {
			e.printStackTrace();

			res.put("STATS","E");
			res.put("MSG", e.getMessage());

			return res.toString();
		}

	}

	/**
	 *
	 * @param mantr
	 * @return
	 */
	private String getMaktx(String mantr){

		if(StringUtils.isBlank(mantr))
			return "";
		return baseMapper.getMaktx(mantr);
	}

    /**
	 *
	 * @param lifnr
	 * @return
	 */
	private String getLiktx(String lifnr){

		if(StringUtils.isBlank(lifnr))
			return "";
		return baseMapper.getLiktx(lifnr);
	}
}
