package com.byd.wms.webservice.common.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import java.util.List;
import java.util.Map;

import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.byd.utils.MD5Util;
import com.byd.wms.webservice.ws.dao.AuthUserDao;

@Component
public class AuthInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
	
	@Autowired
	private AuthUserDao authUserDao;
	 
	// 在拦截器调用方法之前,拦截SOAP消息.
	public AuthInterceptor() {
		super(Phase.PRE_INVOKE);
	}
 
	@Override
	/**
	 * 拦截器处理方法
	 */
	public void handleMessage(SoapMessage message) throws Fault {
		System.out.println("---- 进入拦截器，直接放行 ----");
 
/*		// 获取SOAP携带的所有Header
		List<Header> headers = message.getHeaders();
		if (null == headers || headers.size() < 1)
			throw new Fault(new IllegalArgumentException("拦截器实施拦截 , 没有Header"));
 
		// 获取Header携带的用户名和密码
		Header firstHeader = headers.get(0);
		Element element = (Element) firstHeader.getObject();
 
		NodeList userNameElement = element.getElementsByTagName("userName");
		NodeList passWordElement = element.getElementsByTagName("passWord");
 
		if (userNameElement.getLength() != 1)
			throw new Fault(new IllegalArgumentException("用户名不能为空.."));
		if (passWordElement.getLength() != 1)
			throw new Fault(new IllegalArgumentException("密码不能为空.."));
 
		// 获取元素中的文本内容
		String username = userNameElement.item(0).getTextContent();
		String password = passWordElement.item(0).getTextContent();
		System.err.println("username============>>>"+username);
		System.err.println("password============>>>"+password);
		Map<String,Object> currentUser = authUserDao.getUserByUserName(username);
		
		if(currentUser == null) {
        	throw new Fault(new IllegalArgumentException("用户名"+username+"不存在！"));
        }
        if(MD5Util.validPassword(password, currentUser.get("PASSWORD")==null?"":currentUser.get("PASSWORD").toString())){
			System.out.println("验证成功!!");
		} else {
			throw new Fault(new IllegalArgumentException("用户名或密码不正确.."));
		}*/
 
	}
 
}