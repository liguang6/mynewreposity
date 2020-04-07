package com.byd.utils.serviceauth.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.websocket.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMethod;

import com.byd.utils.Constant;
import com.byd.utils.HttpRequestWrapper;
import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;
import com.byd.utils.SpringContextUtils;
import com.byd.utils.StringUtils;
import com.byd.utils.serviceauth.JWTUtil;
import com.byd.utils.serviceauth.configuration.SecurityAuthProperties;

/**
 * 安全认证拦截器
 *
 * @author develop01
 * @date 2019-01-18
 */
@Order(1)
@WebFilter(filterName = "/SecurityAuthFilter", urlPatterns = "/*")
public class SecurityAuthFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(SecurityAuthFilter.class);

    public SecurityAuthFilter() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    	String basePath = httpServletRequest.getContextPath();  
 		String	uri = httpServletRequest.getRequestURI().replaceFirst(basePath+"/", "");
 		String remoteip = httpServletRequest.getRemoteAddr();
    	if(checkAllowedUri(uri) || checkAllowedUri(basePath) || httpServletRequest.getMethod().equals( RequestMethod.OPTIONS.toString()) 
    	) {
    		//无需过滤资源
    		chain.doFilter(request, response);
    	} else if(Constant.WEBGATE_IP.contains(remoteip)) { //云平台集成、OA单点
    		String username = "";
    		HttpSession session = httpServletRequest.getSession();
        	if(session!=null && session.getAttribute("username")!=null) {
        		//web端发送的请求，从session里获取用户名
        		username = session.getAttribute("username").toString();
        	}
        	if(StringUtils.isEmpty(username)) {
        		//尝试从请求头获取token
        		if(httpServletRequest.getHeader(Constants.AUTHORIZATION_HEADER_NAME)!=null) {
        			String requestToken = httpServletRequest.getHeader(Constants.AUTHORIZATION_HEADER_NAME).toString();
            		if(!StringUtils.isEmpty(requestToken)) {
            			username = JWTUtil.getUsername(requestToken);
            		}
        		}
        		
        	}
    		//权限认证
        	RedisUtils redisUtils = SpringContextUtils.getBean("redisUtils",RedisUtils.class);
    		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpServletRequest);
    		String auth_token = redisUtils.get(RedisKeys.getUserTokenKey(username));
    		requestWrapper.putHeader(Constants.AUTHORIZATION_HEADER_NAME, auth_token);
    		chain.doFilter(requestWrapper, response);
    	} else {
    		//logger.info("SecurityAuthFilter拦截的请求路径为："+httpServletRequest.getRequestURI());
    		String username = "";
    		HttpSession session = httpServletRequest.getSession();
        	if(session!=null && session.getAttribute("username")!=null) {
        		//web端发送的请求，从session里获取用户名
        		username = session.getAttribute("username").toString();
        	}
        	if(StringUtils.isEmpty(username)) {
        		//尝试从请求头获取token
        		if(httpServletRequest.getHeader(Constants.AUTHORIZATION_HEADER_NAME)!=null) {
        			String requestToken = httpServletRequest.getHeader(Constants.AUTHORIZATION_HEADER_NAME).toString();
            		if(!StringUtils.isEmpty(requestToken)) {
            			username = JWTUtil.getUsername(requestToken);
            		}
        		}
        		
        	}
        	
        	//logger.info("SecurityAuthFilter获取的用户名为："+username);
            if(username.length()>0 && !StringUtils.isEmpty(username)) {
            	boolean authed = true;
            	//权限认证
            	RedisUtils redisUtils = SpringContextUtils.getBean("redisUtils",RedisUtils.class);
            	Map<String,Object> currentUser = redisUtils.getMap(RedisKeys.getUserKey(username));
            	if(currentUser == null) {
                	if(basePath.equals("/web")||basePath.equals("/web/")||basePath.equals("")) {
                		chain.doFilter(request, response);
                	}else {
                		//用户token已经失效
                        HttpServletResponse resp = (HttpServletResponse) response;
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter pw = resp.getWriter();
                        pw.write("{\n" +
                                "    \"data\": null,\n" +
                                "    \"code\": 403,\n" +
                                "    \"pagination\": null,\n" +
                                "    \"message\": \"用户Token已失效，请重新登录！\",\n" +
                                "    \"successful\": false\n" +
                                "}");
                        pw.flush();
                        pw.close();
                        return;
                	}

            	}
            	if(!username.toUpperCase().equals(Constant.SUPER)) {
            		//超级管理员，直接放行，其他用户需要验证权限
            		SecurityAuthProperties authProperties = SpringContextUtils.getBean("securityAuthProperties",SecurityAuthProperties.class);
            		//访问权限认证，只需要认证
                    if (this.isContain(httpServletRequest.getRequestURI(), authProperties.getCheckUriList())) {
                        if(redisUtils.get(RedisKeys.getUserKey(username))!=null) {
                        	logger.warn("认证失败，非法用户 remoteAddr:{}, url:{}", httpServletRequest.getRemoteAddr(),
                                    httpServletRequest.getRequestURL());
                            HttpServletResponse resp = (HttpServletResponse) response;
                            resp.setContentType("application/json;charset=utf-8");
                            PrintWriter pw = resp.getWriter();
                            pw.write("{\n" +
                                    "    \"data\": null,\n" +
                                    "    \"code\": 403,\n" +
                                    "    \"pagination\": null,\n" +
                                    "    \"message\": \"非法用户，未授权\",\n" +
                                    "    \"successful\": false\n" +
                                    "}");
                            pw.flush();
                            pw.close();
                            return;
                        }
                    }
                    //访问权限认证，需要认证用户是否有权限
                    if (this.isContain(httpServletRequest.getRequestURI(), authProperties.getAuthUriList())) {
            			Set allMenuSet = redisUtils.getSet(RedisKeys.getAllMenuKey());
            			Set allPermsSet = redisUtils.getSet(RedisKeys.getAllAuthKey());
            			//用户已经认证，判断是否有菜单访问权限，
            			Set userMenuSet = redisUtils.getSet(RedisKeys.getUserMenuKey(username));
            			Set userPermsSet = redisUtils.getSet(RedisKeys.getUserAuthKey(username));
            			if(allMenuSet.contains(uri.trim())) {
            				authed = false;
            				//需要校验权限的路径
            				if(userMenuSet.contains(uri.trim())) {
            					authed = true;
            				}
            			}
            			if(!authed && allPermsSet.contains(uri.trim())) {
            				authed = false;
            				//需要校验权限的路径
            				if(userPermsSet.contains(uri.trim())) {
            					authed = true;
            				}
            			}
            			if(!authed) {
                        	logger.warn("认证失败，非法请求 remoteAddr:{}, url:{}", httpServletRequest.getRemoteAddr(),
                                    httpServletRequest.getRequestURL());
                            HttpServletResponse resp = (HttpServletResponse) response;
                            resp.setContentType("application/json;charset=utf-8");
                            PrintWriter pw = resp.getWriter();
                            pw.write("{\n" +
                                    "    \"data\": null,\n" +
                                    "    \"code\": 403,\n" +
                                    "    \"pagination\": null,\n" +
                                    "    \"message\": \"非法请求，未授权\",\n" +
                                    "    \"successful\": false\n" +
                                    "}");
                            pw.flush();
                            pw.close();
                            return;
            			}
                    }
            	}
               	//logger.info("SecurityAuthFilter封装请求头信息"+username);
        		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpServletRequest);
        		String auth_token = redisUtils.get(RedisKeys.getUserTokenKey(username));
        		requestWrapper.putHeader(Constants.AUTHORIZATION_HEADER_NAME, auth_token);
        		chain.doFilter(requestWrapper, response);
            }else {
            	if(basePath.equals("/web")||basePath.equals("/web/")||basePath.equals("")||basePath.equals("/scan")) {
            		chain.doFilter(request, response);
            	}else {
                   	logger.warn("认证失败，非法用户 remoteAddr:{}, url:{}", httpServletRequest.getRemoteAddr(),
                            httpServletRequest.getRequestURL());
                    HttpServletResponse resp = (HttpServletResponse) response;
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter pw = resp.getWriter();
                    pw.write("{\n" +
                            "    \"data\": null,\n" +
                            "    \"code\": 403,\n" +
                            "    \"pagination\": null,\n" +
                            "    \"message\": \"非法用户，未授权\",\n" +
                            "    \"successfull\": false\n" +
                            "}");
                    pw.flush();
                    pw.close();
                    return;
            	}

            }
    	}
        
    }

    @Override
    public void destroy() {

    }
    
    private boolean checkAllowedUri(String uri) {
    	//判断是否为静态资源
    	for (String string : Constant.ALLOWED_STATICS) {
            if (uri.indexOf(string.trim()) >= 0) {
                return true;
            }
		}
    	//判断是否为允许的路径
    	return Constant.ALLOWED_PATHS.contains(uri);
    }
    
    private boolean isContain(String url,List<String> list) {
    	for (String string : list) {
            if (url.indexOf(string.trim()) >= 0) {
                return true;
            }
		}
    	return false;
    }
}
