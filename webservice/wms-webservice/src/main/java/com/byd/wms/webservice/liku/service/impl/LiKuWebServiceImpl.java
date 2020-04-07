package com.byd.wms.webservice.liku.service.impl;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.byd.wms.webservice.common.Constant.WebserviceInfo;
import com.byd.wms.webservice.common.annotation.WebServicePath;
import com.byd.wms.webservice.liku.service.LiKuWebService;

@Service
public class LiKuWebServiceImpl implements LiKuWebService {

	@Override
	@WebServicePath(path = WebserviceInfo.LIKU_PATH, methodName = WebserviceInfo.LIKU_METHOD_OUT_INSTRUCTION)
	public HashMap liKuOutInstruction(HashMap hashMap) {
		// TODO Auto-generated method stub
		HashMap hmm = new HashMap();
//		hmm.put("DATA", ((JSONArray) js.get("DATA")).get(0));
		return hmm;
	}

}
