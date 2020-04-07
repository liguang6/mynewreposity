package com.byd.web.wms.common.service.fallback;

import java.util.Map;

import com.byd.utils.R;
import com.byd.web.wms.common.service.CommonRemote;

import feign.hystrix.FallbackFactory;

public class CommonRemoteFallBackFactory implements FallbackFactory<CommonRemote>{

	@Override
	public CommonRemote create(Throwable cause) {
		return new CommonRemote() {

			@Override
			public R getPlantList(String werks) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getWhList(String whNumber, String language) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getVendorList(String lifnr, String werks) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getMaterialList(String werks, String matnr) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getBusinessList(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getLoList(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getGrAreaList(String WERKS) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getWhDataByWerks(String WERKS) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getReasonData(String REASON_DESC) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getPlantSetting(String WERKS) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getDictList(String type) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getControlFlagList(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getMaterialInfo(Map<String, Object> map) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getMatStockInfo(Map<String, Object> map) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}
	

			@Override
			public R getAllLoList(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R checkPassword(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}
			
			@Override
			public R getMatManager(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getVendor(String lifnr) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}
			
		};
	}

}
