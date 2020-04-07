package com.byd.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.websocket.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.byd.utils.serviceauth.JWTUtil;

@Component
public class UserUtils {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisUtils redisUtils;
    
	private HttpServletRequest getHttpServletRequest() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    public String getTokenUsername() {
    	String username = "";
    	//调用token工具类从请求头里获取用户名
    	HttpServletRequest request = getHttpServletRequest();
    	
    	if(request.getParameter("username")!=null) {
    		username = request.getParameter("username");
    	}
    	
    	if(StringUtils.isEmpty(username) && request.getAttribute("username") != null) {
    		username = request.getAttribute("username").toString();
    	}
    	
    	if(StringUtils.isEmpty(username)) {
    		//尝试从请求头获取token
    		String requestToken = request.getHeader(Constants.AUTHORIZATION_HEADER_NAME).toString();
    		if(!StringUtils.isEmpty(requestToken)) {
    			username = JWTUtil.getUsername(requestToken);
    		}
    		
    	}
    	logger.info("UserUtils里获取的用户名为："+username);
    	return username;
    }
    
	public Map<String,Object> getUser() {
		//调用token工具类从请求头里获取用户名
		String username = getTokenUsername();
		//此处需要修改为从redis里获取用户信息
		Map<String,Object> currentUser = redisUtils.getMap(RedisKeys.getUserKey(username));
		return currentUser;
	}
	
	public Set<Map<String,Object>> getAllWerks(){
		List<Map<String,Object>> allWerks = (List<Map<String,Object>>)redisUtils.getList(RedisKeys.getAllWerksKey());
		Set<Map<String,Object>> rtn = new HashSet<>();
		for (Map<String, Object> allWerk : allWerks) {
			allWerk.put("code", allWerk.get("WERKS").toString());
			allWerk.put("CODE", allWerk.get("WERKS").toString());
			rtn.add(allWerk);
		}
		return rtn;
	}
	
	/**
	 * 获取用户有权限的工厂清单
	 * @return Set<Map<String,Object>>
       NAME：名称
       CODE：代码
       DEPT_ID：部门ID
       PARENT_ID
	 */
	public Set<Map<String,Object>> getUserWerks(String MENU_KEY) {
		List<Map<String,Object>> allWerks = (List<Map<String,Object>>)redisUtils.getList(RedisKeys.getAllWerksKey());
		
		Set<Map<String,Object>> rtn = new HashSet<>();
		String username = getTokenUsername();
		List<Map<String,Object>> userWerkList = redisUtils.getList(RedisKeys.getUserWerksKey(username));
		for (Map<String, Object> map : userWerkList) {
			if(map.get(MENU_KEY)!=null && StringUtils.isNotEmpty(map.get(MENU_KEY).toString())) {
				//找到用户工厂权限
				String userWerkStr = map.get(MENU_KEY).toString();
				for (String werks : userWerkStr.split(",")) {
					if(werks.equals("*")) {
						for (Map<String, Object> allWerk : allWerks) {
							if(allWerk.get("WERKS")!=null) {
								allWerk.put("code", allWerk.get("WERKS").toString());
								allWerk.put("CODE", allWerk.get("WERKS").toString());
								rtn.add(allWerk);
							}
						}
					} else if(werks.contains("*")) {
						if("*".equals(werks)) {
							werks = "";
						}else {
							werks = werks.toUpperCase().split("\\*")[0];
						}
						for (Map<String, Object> allWerk : allWerks) {
							if(allWerk.get("WERKS")!=null&&allWerk.get("WERKS").toString().startsWith(werks)) {
								allWerk.put("code", allWerk.get("WERKS").toString());
								allWerk.put("CODE", allWerk.get("WERKS").toString());
								rtn.add(allWerk);
							}
						}
					}else {
						for (Map<String, Object> allWerk : allWerks) {
							if(allWerk.get("WERKS")!=null&&allWerk.get("WERKS").toString().equals(werks)) {
								allWerk.put("code", allWerk.get("WERKS").toString());
								allWerk.put("CODE", allWerk.get("WERKS").toString());
								rtn.add(allWerk);
								break;
							}
						}
					}
					
				}
				break;
			}
		}
		return rtn;
	}
	
	/**
	 * 根据菜单KEY获取当前登录用户拥有的仓库号权限
	 * @param MENU_KEY
	 * @return
	 */
	public Set<Map<String,Object>> getUserWh(String MENU_KEY){
    	/**
    	 * 从redis获取所有仓库号信息
    	 */
        List<Map<String,Object>> allWh = (List<Map<String,Object>>)redisUtils.getList(RedisKeys.getAllWhKey());
        
        Set<Map<String,Object>> rtn = new HashSet<>();
		String username = getTokenUsername();
		List<Map<String,Object>> userWhList = redisUtils.getList(RedisKeys.getUserWhKey(username));
		for (Map<String, Object> map : userWhList) {
			if(map.get(MENU_KEY)!=null && StringUtils.isNotEmpty(map.get(MENU_KEY).toString())) {
				//找到用户仓库号权限
				String userWhStr = map.get(MENU_KEY).toString();
				for (String wh : userWhStr.split(",")) {
					if(wh.contains("*")) {
						if("*".equals(wh)) {
							wh = "";
						}else {
							wh = wh.toUpperCase().split("\\*")[0];
						}
						
						for (Map<String, Object> whMap : allWh) {
							if(whMap.get("WH_NUMBER").toString().startsWith(wh)) {
								whMap.put("code", whMap.get("WH_NUMBER").toString());
								whMap.put("CODE", whMap.get("WH_NUMBER").toString());
								rtn.add(whMap);
							}
						}
					}else {
						for (Map<String, Object> whMap : allWh) {
							if(whMap.get("WH_NUMBER").toString().equals(wh)) {
								whMap.put("code", whMap.get("WH_NUMBER").toString());
								whMap.put("CODE", whMap.get("WH_NUMBER").toString());
								rtn.add(whMap);
								break;
							}
						}
					}
				}
				break;
			}
		}
		return rtn;
	}
	
	/**
	 * 根据菜单KEY获取当前登录用户拥有的业务类型名称权限
	 * @param MENU_KEY
	 * @return
	 */
	public List<Map<String,Object>> getUserBusinessName(String MENU_KEY){
		List<Map<String,Object>> rtn = new ArrayList<>();
		String username = getTokenUsername();
		List<Map<String,Object>> userBusinessNameList = redisUtils.getList(RedisKeys.getUserBusinessNameKey(username));
		for (Map<String, Object> map : userBusinessNameList) {
			if(map.get(MENU_KEY)!=null && StringUtils.isNotEmpty(map.get(MENU_KEY).toString())) {
				//找到用户业务类型名称权限
				String userBusinessNameStr = map.get(MENU_KEY).toString();
				for (String businessName : userBusinessNameStr.split(",")) {
					Map<String,Object> businessNameMap = new HashMap<>();
					businessNameMap.put("BUSINESS_NAME", businessName);
					rtn.add(businessNameMap);
				}
				break;
			}
		}
		return rtn;
	}
	
	/**
	 * 根据菜单KEY从 redis获取当前登录用户拥有特定数据对象的权限值
	 * @param MENU_KEY
	 * @param DATA_KEY
	 * @return
	 */
	public Set<Map<String,Object>> getUserDataAuth(String MENU_KEY,String DATA_FIELD){
		Set<Map<String,Object>> rtn = new HashSet<>();
		String username = getTokenUsername();
		String redisKey = null;
		if(DATA_FIELD.equals("BUSINESS_NAME")) {
			redisKey = RedisKeys.getUserBusinessNameKey(username);
		}
		if(DATA_FIELD.equals("QMS_TEST_GROUP")) {
			redisKey = RedisKeys.getUserQmsTestGroupKey(username);
		}
		
		List<Map<String,Object>> userDataFieldList = redisUtils.getList(redisKey);
		for (Map<String, Object> map : userDataFieldList) {
			if(map.get(MENU_KEY)!=null && StringUtils.isNotEmpty(map.get(MENU_KEY).toString())) {
				String userDataFieldStr = map.get(MENU_KEY).toString();
				for (String dataFieldAuth : userDataFieldStr.split(",")) {
					Map<String,Object> dataFieldMap = new HashMap<>();
					dataFieldMap.put(DATA_FIELD, dataFieldAuth);
					dataFieldMap.put("CODE", dataFieldAuth);
					rtn.add(dataFieldMap);
				}
				break;
			}
		}
		return rtn;
	}
	
	
	public List<Map<String,Object>> getUserMenu() {
		//调用token工具类从请求头里获取用户名
		String username = getTokenUsername();
		//此处需要修改为从redis里获取用户信息
		return redisUtils.getList(RedisKeys.getUserMenuKey(username));
	}
	
	public String getUserAuth() {
		//调用token工具类从请求头里获取用户名
		String username = getTokenUsername();;
		//此处需要修改为从redis里获取用户信息
		String userAuth = redisUtils.get(RedisKeys.getUserKey(username));
		return userAuth;
	}

	public Long getUserId() {
		return Long.valueOf(getUser().get("USER_ID").toString());
	}

	public Long getDeptId() {
		return Long.valueOf(getUser().get("DEPT_ID").toString());
	}
	
	
	/**
	 * g根据菜单、工厂获取用户仓库号
	 * @param menuKey
	 * @param werks
	 * @return
	 */
	public Set<Map<String,Object>> getUserWhByWerks(String menuKey,String werks){
    	/**
    	 * 从redis获取所有仓库号信息
    	 */
        List<Map<String,Object>> allWh = (List<Map<String,Object>>)redisUtils.getList(RedisKeys.getAllWhKey());
        
        Set<Map<String,Object>> rtn = new HashSet<>();
		String username = getTokenUsername();
		List<Map<String,Object>> userWhList = redisUtils.getList(RedisKeys.getUserWhKey(username));
		for (Map<String, Object> map : userWhList) {
			if(map.get(menuKey)!=null && StringUtils.isNotEmpty(map.get(menuKey).toString())) {
				//找到用户仓库号权限
				String userWhStr = map.get(menuKey).toString();
				for (String wh : userWhStr.split(",")) {
					if(wh.contains("*")) {
						if("*".equals(wh)) {
							wh = "";
						}else {
							wh = wh.toUpperCase().split("\\*")[0];
						}
						
						for (Map<String, Object> whMap : allWh) {
							String alwerks = whMap.get("WERKS") == null ? "":whMap.get("WERKS").toString();
							if(whMap.get("WH_NUMBER").toString().startsWith(wh) && alwerks.equals(werks)) {
								whMap.put("code", whMap.get("WH_NUMBER").toString());
								whMap.put("CODE", whMap.get("WH_NUMBER").toString());
								rtn.add(whMap);
							}
						}
					}else {
						for (Map<String, Object> whMap : allWh) {
							String alwerks = whMap.get("WERKS") == null ? "":whMap.get("WERKS").toString();
							if(whMap.get("WH_NUMBER").toString().equals(wh) && alwerks.equals(werks)) {
								whMap.put("code", whMap.get("WH_NUMBER").toString());
								whMap.put("CODE", whMap.get("WH_NUMBER").toString());
								rtn.add(whMap);
								break;
							}
						}
					}
				}
				break;
			}
		}
		return rtn;
	}
}
