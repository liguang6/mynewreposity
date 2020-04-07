package com.byd.web.qms.common.service.fallback;

import java.util.Map;

import com.byd.utils.R;
import com.byd.web.qms.common.service.QmsCommonRemote;

import feign.hystrix.FallbackFactory;

public class QmsCommonRemoteFallBackFactory implements FallbackFactory<QmsCommonRemote>{

	@Override
	public QmsCommonRemote create(Throwable cause) {
		return new QmsCommonRemote() {

			@Override
			public R getBusTypeCodeList(String params) {
				return R.error(500, "QMS-SERVICE服务异常！");
			}

			@Override
			public R getTestNodes(String testType,String TEST_CLASS) {
				return R.error(500, "QMS-SERVICE服务异常！");
			}

			@Override
			public R getOrderNoList(String params) {
				return R.error(500, "QMS-SERVICE服务异常！");
			}

			@Override
			public R getBusList(Map<String, Object> condMap) {
				return R.error(500, "QMS-SERVICE服务异常！");
			}

			@Override
			public R getTestTools(Map<String, Object> condMap) {
				return R.error(500, "QMS-SERVICE服务异常！");
			}

			@Override
			public R getQmsTestRecords(Map<String, Object> condMap) {
				return R.error(500, "QMS-SERVICE服务异常！");
			}
		
		};
	}

}
