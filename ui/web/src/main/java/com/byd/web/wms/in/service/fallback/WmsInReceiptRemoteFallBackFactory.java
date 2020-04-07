package com.byd.web.wms.in.service.fallback;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.byd.utils.R;
import com.byd.web.wms.in.service.WmsInReceiptRemote;

import feign.hystrix.FallbackFactory;

@Component
public class WmsInReceiptRemoteFallBackFactory implements FallbackFactory<WmsInReceiptRemote> {

	@Override
	public WmsInReceiptRemote create(Throwable cause) {
		return new WmsInReceiptRemote() {

			@Override
			public R listScmMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R listSKInfo(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R listPOMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R listOutMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R listPOCFMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R list303Mat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R list303AMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R listPOAMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R listOutAMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R boundIn(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getMatInfo(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R info(Long qty) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R listCloudMat(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R getSAPMatDetail(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}

			@Override
			public R exportExcel(Map<String, Object> params) {
				return R.error(500, "WMS-BUSINESS-SERVICE服务异常！");
			}
		};
	}
	
}
