package com.byd.wms.business.modules.timejob.controller;

import com.byd.wms.business.modules.timejob.service.EmailSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("returngoods")
public class EmailSendController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmailSendService EmailSendService;
	/*
	已质检未进仓物料通知
	 */
    public void sendEmail01(){
		EmailSendService.sendEmail01();
	}
	/*
	已质检未进仓物料通知
	 */
	public void sendEmail02(){
		EmailSendService.sendEmail02();
	}
	/*
	IQC来料检验不合格物料通知
	 */
	public void sendEmail03(){
		EmailSendService.sendEmail03();
	}
	/*
	待过期物料复检通知
	 */
	public void sendEmail04(){
		EmailSendService.sendEmail04();
	}
}
