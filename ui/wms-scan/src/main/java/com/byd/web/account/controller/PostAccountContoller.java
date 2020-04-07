package com.byd.web.account.controller;

import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.account.service.PostAccountPadRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("acPda/accout")
public class PostAccountContoller {

	@Autowired
	protected HttpSession session;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private PostAccountPadRemote postAccountPadRemote;


	@RequestMapping("/whTask")
	public R getwhTask(@RequestParam Map<String, Object> params){

        params = initParams(params);

		String reqno = params.get("REQUIREMENT_NO") == null?"":params.get("REQUIREMENT_NO").toString().trim();
		params.put("REQUIREMENT_NO", reqno);

		return postAccountPadRemote.getwhTask(params);
	}

	@RequestMapping("/posttingAc")
	public R posttingAc(@RequestParam Map<String, Object> params){
        params = initParams(params);

        return postAccountPadRemote.posttingAc(params);
	}


	private Map<String, Object> initParams(Map<String, Object> params){

        params.put("WH_NUMBER", session.getAttribute("warehouse"));
        params.put("WERKS", session.getAttribute("werks"));
        params.put("FACT_NO", session.getAttribute("werks"));

        params.put("CREATOR", userUtils.getUser().get("USERNAME"));
        params.put("USER_NAME", userUtils.getUser().get("USERNAME"));
		params.put("USERNAME", userUtils.getUser().get("USERNAME"));
		params.put("FULL_NAME", userUtils.getUser().get("FULL_NAME"));

        params.put("MENU_KEY", "PDA_GR_STO");
        params.put("BUSINESS_NAME", "03");
        params.put("BUSINESS_CODE", "06");

        return params;
    }
}
