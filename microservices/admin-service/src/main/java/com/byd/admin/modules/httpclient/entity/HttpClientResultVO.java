package com.byd.admin.modules.httpclient.entity;


/**
 * @author lu.yupei
 * @Description httpClient返回
 * @version 1.0
 * @date 2019/10/30
 */
public class HttpClientResultVO {


	/**
	 * errCode : 0或者1(0:成功，1：失败)
	 * errMsg : 具体错误信息
	 */

	private int errCode;
	private String errMsg;

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public HttpClientResultVO(int errCode) {
		this.errCode = errCode;
	}

	public HttpClientResultVO(int errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	@Override
	public String toString() {
		return "HttpClientResult [code=" + errCode + ", errMsg=" + errMsg + "]";
	}


}
