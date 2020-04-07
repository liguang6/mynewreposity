/**
 * Copyright 2018 cscc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.admin.modules.sys.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.sys.service.SysMenuService;
import com.byd.admin.modules.sys.service.SysUserService;
import com.byd.utils.Constant;
import com.byd.utils.MD5Util;
import com.byd.utils.R;
import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;
import com.byd.utils.StringUtils;
import com.byd.utils.serviceauth.JWTUtil;
import com.google.code.kaptcha.Producer;

/**
 * 登录相关
 * 
 * @author cscc
 * @email 
 * @date 2016年11月10日 下午1:15:31
 */
@RestController
public class SysLoginController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private Producer producer;
	@Autowired
	private HttpServletResponse response;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
	
	@RequestMapping("captcha.jpg")
	public void captcha()throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        //保存到shiro session
        
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
	}
	
	/**
     * 登录
     */
     @CrossOrigin
     @RequestMapping(value = "login", method = RequestMethod.POST)
     public R login(@RequestParam  Map<String,Object> params) {
/*           String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
            if(!captcha.equalsIgnoreCase(kaptcha)){
                   return R.error("验证码不正确");
            }*/
        logger.info("-->getLogin username:" + params.get("username"));
        String username=params.get("username").toString();
        String password=params.get("password")==null?"":(String) params.get("password");
        String language=params.get("language").toString();
        
        //根据用户名查询用户信息
        Map<String,Object> currentUser = sysUserService.getUserByUserName(username);
        if(currentUser == null) {
        	return R.error("用户名"+username+"不存在！");
        }
        if(MD5Util.validPassword(password, currentUser.get("PASSWORD")==null?"":currentUser.get("PASSWORD").toString())){
        	//验证通过，登录成功，将用户信息写入redis
            //用户信息写入redis
            redisUtils.set(RedisKeys.getUserKey(username), currentUser);

            //查询用户权限列表
            List<Map<String,Object>> userMenuList = sysMenuService.getUserAuthMenuList(Long.valueOf(currentUser.get("USER_ID").toString()));
            Set<String> menuSet = new HashSet<String>();
            //用户权限列表
    		Set<String> permsSet = new HashSet<String>();
    		
    		//用户工厂权限清单
            List<Map<String,Object>> userWerkList = new ArrayList<Map<String,Object>>();
            //用户仓库号权限清单
            List<Map<String,Object>> userWhList = new ArrayList<Map<String,Object>>();
            //用户业务类型名称权限清单
            List<Map<String,Object>> userBusinessNameList = new ArrayList<Map<String,Object>>();
            //用户检验分组权限清单-QMS
            List<Map<String,Object>> userQmsTestGroupList = new ArrayList<Map<String,Object>>();
            
            //用户车间权限清单
            List<Map<String,Object>> userMesWorkshopList = new ArrayList<Map<String,Object>>();
            //用户线别权限清单
            List<Map<String,Object>> userMesLineList = new ArrayList<Map<String,Object>>();
    		
            for (Map<String,Object> sysMenuEntity : userMenuList) {
    			if(StringUtils.isNotBlank(sysMenuEntity.get("URL")==null?"":sysMenuEntity.get("URL").toString())){
    				menuSet.addAll(Arrays.asList(sysMenuEntity.get("URL").toString().trim()));
    			}
    			if(StringUtils.isNotBlank(sysMenuEntity.get("PERMS")==null?"":sysMenuEntity.get("PERMS").toString())){
    				permsSet.addAll(Arrays.asList(sysMenuEntity.get("PERMS").toString().trim().split(",")));
    			}
            	
            	String menuAuth_werks = "";
            	String menuAuth_wh = ""; 
            	String menuAuth_businessName = "";
            	String menuAuth_qmsTestGroup = "";
            	String menuAuth_mesWorkshop = "";
            	String menuAuth_mesLine = "";
            	
            	Map<String,Object> menuAuthMap_werks = null;
            	Map<String,Object> menuAuthMap_wh = null;
            	Map<String,Object> menuAuthMap_businessName = null;
            	Map<String,Object> menuAuthMap_qmsTestGroup = null;
            	Map<String,Object> menuAuthMap_mesWorkshop = null;
            	Map<String,Object> menuAuthMap_mesLine = null;
            	
            	boolean newWerks = false;
            	boolean newWh = false;
            	boolean newBusinessName = false;
            	boolean newQmsTestGroup = false;
            	boolean newMesWorkshop = false;
            	boolean newMesLine = false;
            	
            	
            	for (Map<String, Object> map : userWerkList) {
					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
						menuAuthMap_werks = map;
						menuAuth_werks = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
						break;
					}
				}
            	
            	if(menuAuthMap_werks ==null) {
            		menuAuthMap_werks = new HashMap<String,Object>();
            		newWerks = true;
            	}
               	
            	for (Map<String, Object> map : userWhList) {
					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
						menuAuthMap_wh = map;
						menuAuth_wh = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
						break;
					}
				}
            	
            	if(menuAuthMap_wh ==null) {
            		menuAuthMap_wh = new HashMap<String,Object>();
            		newWh = true;
            	}
               	
            	for (Map<String, Object> map : userBusinessNameList) {
					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
						menuAuthMap_businessName = map;
						menuAuth_businessName = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
						break;
					}
				}
            	
            	if(menuAuthMap_businessName ==null) {
            		menuAuthMap_businessName = new HashMap<String,Object>();
            		newBusinessName = true;
            	}
            	
            	for (Map<String, Object> map : userQmsTestGroupList) {
					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
						menuAuthMap_qmsTestGroup = map;
						menuAuth_qmsTestGroup = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
						break;
					}
				}
            	
            	if(menuAuthMap_qmsTestGroup ==null) {
            		menuAuthMap_qmsTestGroup = new HashMap<String,Object>();
            		newQmsTestGroup = true;
            	}
            	
               	for (Map<String, Object> map : userMesWorkshopList) {
					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
						menuAuthMap_mesWorkshop = map;
						menuAuth_mesWorkshop = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
						break;
					}
				}
            	
            	if(menuAuthMap_mesWorkshop ==null) {
            		menuAuthMap_mesWorkshop = new HashMap<String,Object>();
            		newMesWorkshop = true;
            	}
            	
               	for (Map<String, Object> map : userMesLineList) {
					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
						menuAuthMap_mesLine = map;
						menuAuth_mesLine = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
						break;
					}
				}
            	
            	if(menuAuthMap_mesLine ==null) {
            		menuAuthMap_mesLine = new HashMap<String,Object>();
            		newMesLine = true;
            	}
    			
    			//用户数据权限(如果某个权限对象,用户数据权限中没有配置,则取角色中该权限对象维护的值) 所以此处不能用 if.....else.....
    			if(StringUtils.isNotBlank(sysMenuEntity.get("USER_DATA_ATUH")==null?"":sysMenuEntity.get("USER_DATA_ATUH").toString())){
    				//具有用户维度的数据权限
    				String USER_DATA_ATUH_STR = sysMenuEntity.get("USER_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
    				String[] USER_DATA_ATUH_A = USER_DATA_ATUH_STR.split(";");
    				for (String fieldStr : USER_DATA_ATUH_A) {
    					String field = fieldStr.split(":")[0];
    					field = field.replaceAll(",", "").trim();
    					String fieldValue = fieldStr.split(":")[1];
    					if("WERKS".equals(field)) {
    						//工厂数据权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_werks.contains(string)) {
    								menuAuth_werks += string +",";
    							}
							}
    					}
    					if("WH_NUMBER".equals(field)) {
    						//仓库数据权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_wh.contains(string)) {
    								menuAuth_wh += string +",";
    							}
							}
    					}
    					if("BUSINESS_NAME".equals(field)) {
    						//作业类型名称权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_businessName.contains(string)) {
    								menuAuth_businessName += string +",";
    							}
							}
    					}
    					
    					if("QMS_TEST_GROUP".equals(field)) {
    						//QMS系统检验分组权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_qmsTestGroup.contains(string)) {
    								menuAuth_qmsTestGroup += string +",";
    							}
							}
    					}
    					
    					if("WORKSHOP".equals(field)) {
    						//MES系统车间权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_mesWorkshop.contains(string)) {
    								menuAuth_mesWorkshop += string +",";
    							}
							}
    					}
    					
    					if("LINE".equals(field)) {
    						//MES系统线别权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_mesLine.contains(string)) {
    								menuAuth_mesLine += string +",";
    							}
							}
    					}
    					
					}
    				
    			}
    			
    			//如果工厂、仓库、业务类型任何有一个为空，则取角色中的。
    			if (menuAuth_werks.equals("") || menuAuth_wh.equals("") || menuAuth_businessName.equals("") ) {
    				//没有用户维护的数据权限，处理角色维度的数据权限
        			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
        				//具有角色维度的数据权限
        				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
        				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
        				for (String fieldStr : ROLE_DATA_ATUH_A) {
        					String field = fieldStr.split(":")[0];
        					field = field.replaceAll(",", "").trim();
        					String fieldValue = fieldStr.split(":")[1];
        					if("WERKS".equals(field) && menuAuth_werks.equals("")) {
        						//工厂数据权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_werks.contains(string)) {
        								menuAuth_werks += string +",";
        							}
    							}
        					}
        					if("WH_NUMBER".equals(field) && menuAuth_wh.equals("")) {
        						//仓库数据权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_wh.contains(string)) {
        								menuAuth_wh += string +",";
        							}
    							}
        					}
        					if("BUSINESS_NAME".equals(field) && menuAuth_businessName.equals("")) {
        						//作业类型名称权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_businessName.contains(string)) {
        								menuAuth_businessName += string +",";
        							}
    							}
        					}
        					if("QMS_TEST_GROUP".equals(field) && menuAuth_qmsTestGroup.equals("")) {
        						//作业类型名称权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_qmsTestGroup.contains(string)) {
        								menuAuth_qmsTestGroup += string +",";
        							}
    							}
        					}
        					
        					
        					if("WORKSHOP".equals(field)) {
        						//MES系统车间权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_mesWorkshop.contains(string)) {
        								menuAuth_mesWorkshop += string +",";
        							}
    							}
        					}
        					
        					if("LINE".equals(field)) {
        						//MES系统线别权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_mesLine.contains(string)) {
        								menuAuth_mesLine += string +",";
        							}
    							}
        					}
        					
    					}

        			}
    			}
    			
    			//如果检验分组为空，则取角色中的。
    			if (menuAuth_qmsTestGroup.equals("")) {
    				//没有用户维护的数据权限，处理角色维度的数据权限
        			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
        				//具有角色维度的数据权限
        				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
        				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
        				for (String fieldStr : ROLE_DATA_ATUH_A) {
        					String field = fieldStr.split(":")[0];
        					field = field.replaceAll(",", "").trim();
        					String fieldValue = fieldStr.split(":")[1];
        		
        					if("QMS_TEST_GROUP".equals(field) && menuAuth_qmsTestGroup.equals("")) {
        						//作业类型名称权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_qmsTestGroup.contains(string)) {
        								menuAuth_qmsTestGroup += string +",";
        							}
    							}
        					}
    					}

        			}
    			}
    			
    			//如果工厂、车间、线别任何一个为空，则取角色中的。
    			if (menuAuth_werks.equals("") || menuAuth_mesWorkshop.equals("") || menuAuth_mesLine.equals("") ) {
    				//没有用户维护的数据权限，处理角色维度的数据权限
        			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
        				//具有角色维度的数据权限
        				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
        				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
        				for (String fieldStr : ROLE_DATA_ATUH_A) {
        					String field = fieldStr.split(":")[0];
        					field = field.replaceAll(",", "").trim();
        					String fieldValue = fieldStr.split(":")[1];
        					if("WERKS".equals(field) && menuAuth_werks.equals("")) {
        						//工厂数据权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_werks.contains(string)) {
        								menuAuth_werks += string +",";
        							}
    							}
        					}
        					
        					if("WORKSHOP".equals(field)) {
        						//MES系统车间权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_mesWorkshop.contains(string)) {
        								menuAuth_mesWorkshop += string +",";
        							}
    							}
        					}
        					
        					if("LINE".equals(field)) {
        						//MES系统线别权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_mesLine.contains(string)) {
        								menuAuth_mesLine += string +",";
        							}
    							}
        					}
        					
    					}

        			}
    			}
    			
    			
    			if (sysMenuEntity.get("MENU_KEY") != null) {
					if (!menuAuth_werks.equals("")) {
						menuAuthMap_werks.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_werks);
					}
					if (!menuAuth_wh.equals("")) {
						menuAuthMap_wh.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_wh);
					}
					if (!menuAuth_businessName.equals("")) {
						menuAuthMap_businessName.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_businessName);
					}
					if (!menuAuth_qmsTestGroup.equals("")) {
						menuAuthMap_qmsTestGroup.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_qmsTestGroup);
					}
					if (!menuAuth_mesWorkshop.equals("")) {
						menuAuthMap_mesWorkshop.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_mesWorkshop);
					}
					if (!menuAuth_mesLine.equals("")) {
						menuAuthMap_mesLine.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_mesLine);
					}
					
				}
    			
    			if(newWerks) {
    				userWerkList.add(menuAuthMap_werks);
    			}
    			if(newWh) {
    				userWhList.add(menuAuthMap_wh);
    			}
    			
    			if(newBusinessName) {
    				userBusinessNameList.add(menuAuthMap_businessName);
    			}
    			if(newQmsTestGroup) {
    				userQmsTestGroupList.add(menuAuthMap_qmsTestGroup);
    			}
    			if(newMesWorkshop) {
    				userMesWorkshopList.add(menuAuthMap_mesWorkshop);
    			}
    			if(newMesLine) {
    				userMesLineList.add(menuAuthMap_mesLine);
    			}
    			
			}
            redisUtils.set(RedisKeys.getUserWerksKey(username), userWerkList);
            redisUtils.set(RedisKeys.getUserWhKey(username), userWhList);
            redisUtils.set(RedisKeys.getUserBusinessNameKey(username), userBusinessNameList);
            redisUtils.set(RedisKeys.getUserQmsTestGroupKey(username), userQmsTestGroupList);
            redisUtils.set(RedisKeys.getUserMesWorkshopKey(username), userMesWorkshopList);
            redisUtils.set(RedisKeys.getUserMesLineKey(username), userMesLineList);
            
            //用户权限信息写入redis
            redisUtils.set(RedisKeys.getUserMenuKey(username), menuSet);
            redisUtils.set(RedisKeys.getUserAuthKey(username), permsSet);
            
            //产生用户token
        	logger.info("-->login 开始产生用户TOKEN！！！");
            String userToken = JWTUtil.generateToken(username);
            logger.info("-->login 产生的用户TOKEN：" + userToken);
            redisUtils.set(RedisKeys.getUserTokenKey(username), userToken);
            return R.ok().put("userToken", userToken);
        }else {
        	return R.error("登录失败！密码不正确");
        }
        
	}
	
	/**
	 * 语言设置
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/setlanguage")
	public R setlanguage(HttpServletRequest request, HttpServletResponse response) {
		//根据session判段语言
		String sessionLanguage = (String)request.getSession().getAttribute(Constant.SESSION_LANGUAGE_KEY);
		//根据本地浏览器判段语言
		String localLanguage = (String)request.getParameter("language");
		System.out.println("sessionLanguage="+sessionLanguage);
		String language = (localLanguage==null || localLanguage.isEmpty()) ? sessionLanguage : localLanguage;
		request.getSession().setAttribute(Constant.SESSION_LANGUAGE_KEY, language);  
		request.setAttribute("language", language); 
		return R.ok();
	}
	
	/**
	 * 退出
	 */
	@CrossOrigin
	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public R logout(@RequestParam  Map<String,Object> params) {
		//ShiroUtils.logout();
		String username=params.get("username").toString();
		//删除redis里用户信息
		redisUtils.delete(RedisKeys.getUserKey(username));
		redisUtils.delete(RedisKeys.getUserAuthKey(username));
		redisUtils.delete(RedisKeys.getUserMenuKey(username));
		redisUtils.delete(RedisKeys.getUserTokenKey(username));
		redisUtils.delete(RedisKeys.getUserWerksKey(username));
		redisUtils.delete(RedisKeys.getUserWhKey(username));
		redisUtils.delete(RedisKeys.getUserBusinessNameKey(username));
		redisUtils.delete(RedisKeys.getUserQmsTestGroupKey(username));
		return R.ok();
	}
	
	/**
     * PDA登录界面输入用户名获取用户有权限的仓库号清单
     */
     @CrossOrigin
     @RequestMapping(value = "getPdaUserAuthWh", method = RequestMethod.POST)
     public R getPdaUserAuthWh(@RequestParam  Map<String,Object> params) {
    	 Set<Map<String,Object>> userAllWh = new HashSet();
         String username=params.get("username").toString();
         //根据用户名查询用户信息
         Map<String,Object> currentUser = sysUserService.getUserByUserName(username);
         if(currentUser == null) {
         	return R.error("用户名"+username+"不存在！");
         }
         //查询用户权限列表
         List<Map<String,Object>> userMenuList = sysMenuService.getUserPdaAuthMenuList(Long.valueOf(currentUser.get("USER_ID").toString()));
         
         /**
     	 * 从redis获取所有仓库号信息
     	 */
         List<Map<String,Object>> allWh = (List<Map<String,Object>>)redisUtils.getList(RedisKeys.getAllWhKey());
         for (Map<String,Object> sysMenuEntity : userMenuList) {
 			 //用户数据权限
 			 if(StringUtils.isNotBlank(sysMenuEntity.get("USER_DATA_ATUH")==null?"":sysMenuEntity.get("USER_DATA_ATUH").toString())){
 				//具有用户维度的数据权限
 				String USER_DATA_ATUH_STR = sysMenuEntity.get("USER_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
 				String[] USER_DATA_ATUH_A = USER_DATA_ATUH_STR.split(";");
 				for (String fieldStr : USER_DATA_ATUH_A) {
 					String field = fieldStr.split(":")[0];
 					field = field.replaceAll(",", "").trim();
 					String fieldValue = fieldStr.split(":")[1];
 					if("WH_NUMBER".equals(field)) {
 						//仓库数据权限
 						for (String string : fieldValue.split(",")) {
 							if(string.equals("*")) {
 								for (Map<String, Object> whMap : allWh) {
 									whMap.put("code", whMap.get("WH_NUMBER"));
 									whMap.put("CODE", whMap.get("WH_NUMBER"));
 									userAllWh.add(whMap);
 								}
 							 } else if(string.contains("*")) {
 										if("*".equals(string)) {
 											string = "";
 										}else {
 											string = string.toUpperCase().split("\\*")[0];
 										}
										if(allWh!=null) {
										for (Map<String, Object> whMap : allWh) {
											if(whMap.get("WH_NUMBER")!=null&&whMap.get("WH_NUMBER").toString().startsWith(string)) {
												whMap.put("code", whMap.get("WH_NUMBER").toString());
												whMap.put("CODE", whMap.get("WH_NUMBER").toString());
												userAllWh.add(whMap);
											}
										}
										}
									}else {
										for (Map<String, Object> whMap : allWh) {
											if(whMap.get("WH_NUMBER")!=null&&whMap.get("WH_NUMBER").toString().equals(string)) {
												whMap.put("code", whMap.get("WH_NUMBER").toString());
												whMap.put("CODE", whMap.get("WH_NUMBER").toString());
												userAllWh.add(whMap);
												break;
											}
										}
								}
 								
						}
 					}
 					
				 }
 			 }else {
 				//没有用户维护的数据权限，处理角色维度的数据权限
     			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
     				//具有角色维度的数据权限
     				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
     				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
     				for (String fieldStr : ROLE_DATA_ATUH_A) {
     					String field = fieldStr.split(":")[0];
     					field = field.replaceAll(",", "").trim();
     					String fieldValue = fieldStr.split(":")[1];
     					if("WH_NUMBER".equals(field)) {
     						//仓库数据权限
     						for (String string : fieldValue.split(",")) {
     							if(string.equals("*")) {
     								for (Map<String, Object> whMap : allWh) {
     									whMap.put("code", whMap.get("WH_NUMBER"));
     									whMap.put("CODE", whMap.get("WH_NUMBER"));
     									userAllWh.add(whMap);
     								}
     							 } else if(string.contains("*")) {
 										if("*".equals(string)) {
 											string = "";
 										}else {
 											string = string.toUpperCase().split("\\*")[0];
 										} 										
 										for (Map<String, Object> whMap : allWh) {
 											if(whMap.get("WH_NUMBER")!=null&&whMap.get("WH_NUMBER").toString().startsWith(string)) {
 												whMap.put("code", whMap.get("WH_NUMBER"));
 												whMap.put("CODE", whMap.get("WH_NUMBER"));
 												userAllWh.add(whMap);
 											}
 										}
 									}else {
 										for (Map<String, Object> whMap : allWh) {
 											if(whMap.get("WH_NUMBER")!=null&&whMap.get("WH_NUMBER").toString().equals(string)) {
 												whMap.put("code", whMap.get("WH_NUMBER"));
 												whMap.put("CODE", whMap.get("WH_NUMBER"));
 												userAllWh.add(whMap);
 												break;
 											}
 										}
 									}
 							}
     					}
 					}
     				
     			}
 			 }
		 }
    	 return R.ok().put("userAuthWh", userAllWh);
     }

 	
 	/**
      * 登录
      */
      @CrossOrigin
      @RequestMapping(value = "pdaLogin", method = RequestMethod.POST)
      public R pdaLogin(@RequestParam  Map<String,Object> params) {
         String username=params.get("username").toString();
         String password=params.get("password")==null?"":(String) params.get("password");
         String whnumber = params.get("whnumber").toString();
         
         //根据用户名查询用户信息
         Map<String,Object> currentUser = sysUserService.getUserByUserName(username);
         if(currentUser == null) {
         	return R.error("用户名"+username+"不存在！");
         }
         if(MD5Util.validPassword(password, currentUser.get("PASSWORD")==null?"":currentUser.get("PASSWORD").toString())){
         	 //验证通过，登录成功，将用户信息写入redis
             //用户信息写入redis
             redisUtils.set(RedisKeys.getUserKey(username), currentUser);

             //查询用户权限列表
             List<Map<String,Object>> userMenuList = sysMenuService.getUserPdaAuthMenuList(Long.valueOf(currentUser.get("USER_ID").toString()));
             Set<String> menuSet = new HashSet<String>();
             //用户权限列表
     		 Set<String> permsSet = new HashSet<String>();
     		 
             //PDA端用户选择的仓库号对应的有权限的URL
     		 Set<String> userAuthUrlSet = new HashSet<String>();
     		 
     		 //用户工厂权限清单
             List<Map<String,Object>> userWerkList = new ArrayList<Map<String,Object>>();
             //用户仓库号权限清单
             List<Map<String,Object>> userWhList = new ArrayList<Map<String,Object>>();
             //用户业务类型名称权限清单
             List<Map<String,Object>> userBusinessNameList = new ArrayList<Map<String,Object>>();
     		
             for (Map<String,Object> sysMenuEntity : userMenuList) {
            	 
            	   	String menuAuth_werks = "";
                	String menuAuth_wh = ""; 
                	String menuAuth_businessName = "";
                   	
                	Map<String,Object> menuAuthMap_werks = null;
                	Map<String,Object> menuAuthMap_wh = null;
                	Map<String,Object> menuAuthMap_businessName = null;
                	boolean newWerks = false;
                	boolean newWh = false;
                	boolean newBusinessName = false;
                	
                	for (Map<String, Object> map : userWerkList) {
    					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
    						menuAuthMap_werks = map;
    						menuAuth_werks = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
    						break;
    					}
    				}
                	
                	if(menuAuthMap_werks ==null) {
                		menuAuthMap_werks = new HashMap<String,Object>();
                		newWerks = true;
                	}
                   	
                	for (Map<String, Object> map : userWhList) {
    					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
    						menuAuthMap_wh = map;
    						menuAuth_wh = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
    						break;
    					}
    				}
                	
                	if(menuAuthMap_wh ==null) {
                		menuAuthMap_wh = new HashMap<String,Object>();
                		newWh = true;
                	}
                   	
                	for (Map<String, Object> map : userBusinessNameList) {
    					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
    						menuAuthMap_businessName = map;
    						menuAuth_businessName = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
    						break;
    					}
    				}
                	
                	if(menuAuthMap_businessName ==null) {
                		menuAuthMap_businessName = new HashMap<String,Object>();
                		newBusinessName = true;
                	}
             	
     			if(StringUtils.isNotBlank(sysMenuEntity.get("URL")==null?"":sysMenuEntity.get("URL").toString())){
     				menuSet.addAll(Arrays.asList(sysMenuEntity.get("URL").toString().trim()));
     			}
     			if(StringUtils.isNotBlank(sysMenuEntity.get("PERMS")==null?"":sysMenuEntity.get("PERMS").toString())){
     				permsSet.addAll(Arrays.asList(sysMenuEntity.get("PERMS").toString().trim().split(",")));
     			}
     			
    			//用户数据权限(如果某个权限对象,用户数据权限中没有配置,则取角色中该权限对象维护的值) 所以此处不能用 if.....else.....
    			if(StringUtils.isNotBlank(sysMenuEntity.get("USER_DATA_ATUH")==null?"":sysMenuEntity.get("USER_DATA_ATUH").toString())){
    				//具有用户维度的数据权限
    				String USER_DATA_ATUH_STR = sysMenuEntity.get("USER_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
    				String[] USER_DATA_ATUH_A = USER_DATA_ATUH_STR.split(";");
    				for (String fieldStr : USER_DATA_ATUH_A) {
    					String field = fieldStr.split(":")[0];
    					field = field.replaceAll(",", "").trim();
    					String fieldValue = fieldStr.split(":")[1];
    					if("WERKS".equals(field)) {
    						//工厂数据权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_werks.contains(string)) {
    								menuAuth_werks += string +",";
    							}
							}
    					}
    					if("WH_NUMBER".equals(field)) {
    						//仓库数据权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_wh.contains(string)) {
    								menuAuth_wh += string +",";
    							}
							}
    					}
    					if("BUSINESS_NAME".equals(field)) {
    						//作业类型名称权限
    						for (String string : fieldValue.split(",")) {
    							if(!menuAuth_businessName.contains(string)) {
    								menuAuth_businessName += string +",";
    							}
							}
    					}
    					
					}
    				
    			}
    			
    			//如果工厂、仓库、业务类型任何有一个为空，则取角色中的。
    			if (menuAuth_werks.equals("") || menuAuth_wh.equals("") || menuAuth_businessName.equals("")) {
    				//没有用户维护的数据权限，处理角色维度的数据权限
        			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
        				//具有角色维度的数据权限
        				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
        				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
        				for (String fieldStr : ROLE_DATA_ATUH_A) {
        					String field = fieldStr.split(":")[0];
        					field = field.replaceAll(",", "").trim();
        					String fieldValue = fieldStr.split(":")[1];
        					if("WERKS".equals(field) && menuAuth_werks.equals("")) {
        						//工厂数据权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_werks.contains(string)) {
        								menuAuth_werks += string +",";
        							}
    							}
        					}
        					if("WH_NUMBER".equals(field) && menuAuth_wh.equals("")) {
        						//仓库数据权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_wh.contains(string)) {
        								menuAuth_wh += string +",";
        							}
    							}
        					}
        					if("BUSINESS_NAME".equals(field) && menuAuth_businessName.equals("")) {
        						//作业类型名称权限
        						for (String string : fieldValue.split(",")) {
        							if(!menuAuth_businessName.contains(string)) {
        								menuAuth_businessName += string +",";
        							}
    							}
        					}
    					}

        			}
    			}
    			
    			if (sysMenuEntity.get("MENU_KEY") != null) {
					if (!menuAuth_werks.equals("")) {
						menuAuthMap_werks.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_werks);
					}
					if (!menuAuth_wh.equals("")) {
						menuAuthMap_wh.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_wh);
					}
					if (!menuAuth_businessName.equals("")) {
						menuAuthMap_businessName.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_businessName);
					}
				}
    			
    			if(newWerks) {
    				userWerkList.add(menuAuthMap_werks);
    			}
    			if(newWh) {
    				userWhList.add(menuAuthMap_wh);
    			}
    			
    			if(newBusinessName) {
    				userBusinessNameList.add(menuAuthMap_businessName);
    			}
     			
    			 //用户数据权限
    			 if(StringUtils.isNotBlank(sysMenuEntity.get("URL")==null?"":sysMenuEntity.get("URL").toString()) && 
    					 StringUtils.isNotBlank(sysMenuEntity.get("USER_DATA_ATUH")==null?"":sysMenuEntity.get("USER_DATA_ATUH").toString())){
    				//具有用户维度的数据权限
    				String USER_DATA_ATUH_STR = sysMenuEntity.get("USER_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
    				String[] USER_DATA_ATUH_A = USER_DATA_ATUH_STR.split(";");
    				for (String fieldStr : USER_DATA_ATUH_A) {
    					String field = fieldStr.split(":")[0];
    					field = field.replaceAll(",", "").trim();
    					String fieldValue = fieldStr.split(":")[1];
    					if("WH_NUMBER".equals(field)) {
    						//仓库数据权限
    						for (String string : fieldValue.split(",")) {
								if(string.contains("*")) {
									if("*".equals(string)) {
										string = "";
									}else {
										string = string.toUpperCase().split("\\*")[0];
									}
									if(whnumber.startsWith(string)) {
										userAuthUrlSet.addAll(Arrays.asList(sysMenuEntity.get("URL").toString().trim()));
										break;
									}
								}else {
									if(whnumber.equals(string)) {
										userAuthUrlSet.addAll(Arrays.asList(sysMenuEntity.get("URL").toString().trim()));
										break;
									}
								}
    						}
    						break;
    					}
    					
   				 	}
    			 }else {
    				//没有用户维护的数据权限，处理角色维度的数据权限
        			if(StringUtils.isNotBlank(sysMenuEntity.get("URL")==null?"":sysMenuEntity.get("URL").toString()) && 
        					StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
        				//具有角色维度的数据权限
        				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
        				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
        				for (String fieldStr : ROLE_DATA_ATUH_A) {
        					String field = fieldStr.split(":")[0];
        					field = field.replaceAll(",", "").trim();
        					String fieldValue = fieldStr.split(":")[1];
        					if("WH_NUMBER".equals(field)) {
        						//仓库数据权限
        						for (String string : fieldValue.split(",")) {
    								if(string.contains("*")) {
    									if("*".equals(string)) {
 											string = "";
 										}else {
 											string = string.toUpperCase().split("\\*")[0];
 										}
										if(whnumber.startsWith(string)) {
											userAuthUrlSet.addAll(Arrays.asList(sysMenuEntity.get("URL").toString().trim()));
											break;
										}
    								}else {
    									if(whnumber.equals(string)) {
    										userAuthUrlSet.addAll(Arrays.asList(sysMenuEntity.get("URL").toString().trim()));
    										break;
    									}
    								}
    							
        						}
    						}
        				}
        			}
    			 }
     			
 			 }
             
             redisUtils.set(RedisKeys.getUserWerksKey(username), userWerkList);
             redisUtils.set(RedisKeys.getUserWhKey(username), userWhList);
             redisUtils.set(RedisKeys.getUserBusinessNameKey(username), userBusinessNameList);
             
             //用户权限信息写入redis
             redisUtils.set(RedisKeys.getUserMenuKey(username), menuSet);
             redisUtils.set(RedisKeys.getUserAuthKey(username), permsSet);
             
             //产生用户token
         	 logger.info("-->login 开始产生用户TOKEN！！！");
             String userToken = JWTUtil.generateToken(username);
             logger.info("-->login 产生的用户TOKEN：" + userToken);
             redisUtils.set(RedisKeys.getUserTokenKey(username), userToken);
             return R.ok().put("userToken", userToken).put("userAuthUrlSet", userAuthUrlSet);
         }else {
         	return R.error("登录失败！密码不正确");
         }
         
 	}
      /**
       * 登录校验
       */
       @CrossOrigin
       @RequestMapping(value = "checkLogin", method = RequestMethod.POST)
       public R checkLogin(@RequestParam  Map<String,Object> params) {

          logger.info("-->getLogin username:" + params.get("username"));
          String username=params.get("username").toString();
          String password=params.get("password")==null?"":(String) params.get("password");
          
          //根据用户名查询用户信息
          Map<String,Object> currentUser = sysUserService.getUserByUserName(username);
          if(currentUser == null) {
          	return R.error("用户名"+username+"不存在！");
          }
          if(MD5Util.validPassword(password, currentUser.get("PASSWORD")==null?"":currentUser.get("PASSWORD").toString())){
              return R.ok().put("currentUser", currentUser);
          }else {
          	return R.error("登录失败！密码不正确");
          }
          
  	}
       
       @CrossOrigin
       @RequestMapping(value = "oalogin", method = RequestMethod.POST)
       public R oalogin(@RequestParam  Map<String,Object> params) {
          logger.info("-->getLogin username:" + params.get("username"));
          String username=params.get("username").toString();

          //根据用户名查询用户信息
          Map<String,Object> currentUser = sysUserService.getUserByUserName(username);
          if(currentUser == null) {
          	return R.error("用户名"+username+"不存在！");
          }
              
          //用户信息写入redis
          redisUtils.set(RedisKeys.getUserKey(username), currentUser);

          //查询用户权限列表
          List<Map<String,Object>> userMenuList = sysMenuService.getUserAuthMenuList(Long.valueOf(currentUser.get("USER_ID").toString()));
          Set<String> menuSet = new HashSet<String>();
          //用户权限列表
      	  Set<String> permsSet = new HashSet<String>();
      		
      		//用户工厂权限清单
              List<Map<String,Object>> userWerkList = new ArrayList<Map<String,Object>>();
              //用户仓库号权限清单
              List<Map<String,Object>> userWhList = new ArrayList<Map<String,Object>>();
              //用户业务类型名称权限清单
              List<Map<String,Object>> userBusinessNameList = new ArrayList<Map<String,Object>>();
              //用户检验分组权限清单-QMS
              List<Map<String,Object>> userQmsTestGroupList = new ArrayList<Map<String,Object>>();
              
              //用户车间权限清单
              List<Map<String,Object>> userMesWorkshopList = new ArrayList<Map<String,Object>>();
              //用户线别权限清单
              List<Map<String,Object>> userMesLineList = new ArrayList<Map<String,Object>>();
      		
              for (Map<String,Object> sysMenuEntity : userMenuList) {
      			if(StringUtils.isNotBlank(sysMenuEntity.get("URL")==null?"":sysMenuEntity.get("URL").toString())){
      				menuSet.addAll(Arrays.asList(sysMenuEntity.get("URL").toString().trim()));
      			}
      			if(StringUtils.isNotBlank(sysMenuEntity.get("PERMS")==null?"":sysMenuEntity.get("PERMS").toString())){
      				permsSet.addAll(Arrays.asList(sysMenuEntity.get("PERMS").toString().trim().split(",")));
      			}
              	
              	String menuAuth_werks = "";
              	String menuAuth_wh = ""; 
              	String menuAuth_businessName = "";
              	String menuAuth_qmsTestGroup = "";
              	String menuAuth_mesWorkshop = "";
              	String menuAuth_mesLine = "";
              	
              	Map<String,Object> menuAuthMap_werks = null;
              	Map<String,Object> menuAuthMap_wh = null;
              	Map<String,Object> menuAuthMap_businessName = null;
              	Map<String,Object> menuAuthMap_qmsTestGroup = null;
              	Map<String,Object> menuAuthMap_mesWorkshop = null;
              	Map<String,Object> menuAuthMap_mesLine = null;
              	
              	boolean newWerks = false;
              	boolean newWh = false;
              	boolean newBusinessName = false;
              	boolean newQmsTestGroup = false;
              	boolean newMesWorkshop = false;
              	boolean newMesLine = false;
              	
              	
              	for (Map<String, Object> map : userWerkList) {
  					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
  						menuAuthMap_werks = map;
  						menuAuth_werks = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
  						break;
  					}
  				}
              	
              	if(menuAuthMap_werks ==null) {
              		menuAuthMap_werks = new HashMap<String,Object>();
              		newWerks = true;
              	}
                 	
              	for (Map<String, Object> map : userWhList) {
  					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
  						menuAuthMap_wh = map;
  						menuAuth_wh = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
  						break;
  					}
  				}
              	
              	if(menuAuthMap_wh ==null) {
              		menuAuthMap_wh = new HashMap<String,Object>();
              		newWh = true;
              	}
                 	
              	for (Map<String, Object> map : userBusinessNameList) {
  					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
  						menuAuthMap_businessName = map;
  						menuAuth_businessName = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
  						break;
  					}
  				}
              	
              	if(menuAuthMap_businessName ==null) {
              		menuAuthMap_businessName = new HashMap<String,Object>();
              		newBusinessName = true;
              	}
              	
              	for (Map<String, Object> map : userQmsTestGroupList) {
  					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
  						menuAuthMap_qmsTestGroup = map;
  						menuAuth_qmsTestGroup = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
  						break;
  					}
  				}
              	
              	if(menuAuthMap_qmsTestGroup ==null) {
              		menuAuthMap_qmsTestGroup = new HashMap<String,Object>();
              		newQmsTestGroup = true;
              	}
              	
                 	for (Map<String, Object> map : userMesWorkshopList) {
  					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
  						menuAuthMap_mesWorkshop = map;
  						menuAuth_mesWorkshop = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
  						break;
  					}
  				}
              	
              	if(menuAuthMap_mesWorkshop ==null) {
              		menuAuthMap_mesWorkshop = new HashMap<String,Object>();
              		newMesWorkshop = true;
              	}
              	
                 	for (Map<String, Object> map : userMesLineList) {
  					if(map.get(sysMenuEntity.get("MENU_KEY")) !=null) {
  						menuAuthMap_mesLine = map;
  						menuAuth_mesLine = map.get(sysMenuEntity.get("MENU_KEY").toString()).toString();
  						break;
  					}
  				}
              	
              	if(menuAuthMap_mesLine ==null) {
              		menuAuthMap_mesLine = new HashMap<String,Object>();
              		newMesLine = true;
              	}
      			
      			//用户数据权限(如果某个权限对象,用户数据权限中没有配置,则取角色中该权限对象维护的值) 所以此处不能用 if.....else.....
      			if(StringUtils.isNotBlank(sysMenuEntity.get("USER_DATA_ATUH")==null?"":sysMenuEntity.get("USER_DATA_ATUH").toString())){
      				//具有用户维度的数据权限
      				String USER_DATA_ATUH_STR = sysMenuEntity.get("USER_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
      				String[] USER_DATA_ATUH_A = USER_DATA_ATUH_STR.split(";");
      				for (String fieldStr : USER_DATA_ATUH_A) {
      					String field = fieldStr.split(":")[0];
      					field = field.replaceAll(",", "").trim();
      					String fieldValue = fieldStr.split(":")[1];
      					if("WERKS".equals(field)) {
      						//工厂数据权限
      						for (String string : fieldValue.split(",")) {
      							if(!menuAuth_werks.contains(string)) {
      								menuAuth_werks += string +",";
      							}
  							}
      					}
      					if("WH_NUMBER".equals(field)) {
      						//仓库数据权限
      						for (String string : fieldValue.split(",")) {
      							if(!menuAuth_wh.contains(string)) {
      								menuAuth_wh += string +",";
      							}
  							}
      					}
      					if("BUSINESS_NAME".equals(field)) {
      						//作业类型名称权限
      						for (String string : fieldValue.split(",")) {
      							if(!menuAuth_businessName.contains(string)) {
      								menuAuth_businessName += string +",";
      							}
  							}
      					}
      					
      					if("QMS_TEST_GROUP".equals(field)) {
      						//QMS系统检验分组权限
      						for (String string : fieldValue.split(",")) {
      							if(!menuAuth_qmsTestGroup.contains(string)) {
      								menuAuth_qmsTestGroup += string +",";
      							}
  							}
      					}
      					
      					if("WORKSHOP".equals(field)) {
      						//MES系统车间权限
      						for (String string : fieldValue.split(",")) {
      							if(!menuAuth_mesWorkshop.contains(string)) {
      								menuAuth_mesWorkshop += string +",";
      							}
  							}
      					}
      					
      					if("LINE".equals(field)) {
      						//MES系统线别权限
      						for (String string : fieldValue.split(",")) {
      							if(!menuAuth_mesLine.contains(string)) {
      								menuAuth_mesLine += string +",";
      							}
  							}
      					}
      					
  					}
      				
      			}
      			
      			//如果工厂、仓库、业务类型任何有一个为空，则取角色中的。
      			if (menuAuth_werks.equals("") || menuAuth_wh.equals("") || menuAuth_businessName.equals("") ) {
      				//没有用户维护的数据权限，处理角色维度的数据权限
          			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
          				//具有角色维度的数据权限
          				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
          				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
          				for (String fieldStr : ROLE_DATA_ATUH_A) {
          					String field = fieldStr.split(":")[0];
          					field = field.replaceAll(",", "").trim();
          					String fieldValue = fieldStr.split(":")[1];
          					if("WERKS".equals(field) && menuAuth_werks.equals("")) {
          						//工厂数据权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_werks.contains(string)) {
          								menuAuth_werks += string +",";
          							}
      							}
          					}
          					if("WH_NUMBER".equals(field) && menuAuth_wh.equals("")) {
          						//仓库数据权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_wh.contains(string)) {
          								menuAuth_wh += string +",";
          							}
      							}
          					}
          					if("BUSINESS_NAME".equals(field) && menuAuth_businessName.equals("")) {
          						//作业类型名称权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_businessName.contains(string)) {
          								menuAuth_businessName += string +",";
          							}
      							}
          					}
          					if("QMS_TEST_GROUP".equals(field) && menuAuth_qmsTestGroup.equals("")) {
          						//作业类型名称权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_qmsTestGroup.contains(string)) {
          								menuAuth_qmsTestGroup += string +",";
          							}
      							}
          					}
          					
          					
          					if("WORKSHOP".equals(field)) {
          						//MES系统车间权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_mesWorkshop.contains(string)) {
          								menuAuth_mesWorkshop += string +",";
          							}
      							}
          					}
          					
          					if("LINE".equals(field)) {
          						//MES系统线别权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_mesLine.contains(string)) {
          								menuAuth_mesLine += string +",";
          							}
      							}
          					}
          					
      					}

          			}
      			}
      			
      			//如果检验分组为空，则取角色中的。
      			if (menuAuth_qmsTestGroup.equals("")) {
      				//没有用户维护的数据权限，处理角色维度的数据权限
          			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
          				//具有角色维度的数据权限
          				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
          				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
          				for (String fieldStr : ROLE_DATA_ATUH_A) {
          					String field = fieldStr.split(":")[0];
          					field = field.replaceAll(",", "").trim();
          					String fieldValue = fieldStr.split(":")[1];
          		
          					if("QMS_TEST_GROUP".equals(field) && menuAuth_qmsTestGroup.equals("")) {
          						//作业类型名称权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_qmsTestGroup.contains(string)) {
          								menuAuth_qmsTestGroup += string +",";
          							}
      							}
          					}
      					}

          			}
      			}
      			
      			//如果工厂、车间、线别任何一个为空，则取角色中的。
      			if (menuAuth_werks.equals("") || menuAuth_mesWorkshop.equals("") || menuAuth_mesLine.equals("") ) {
      				//没有用户维护的数据权限，处理角色维度的数据权限
          			if(StringUtils.isNotBlank(sysMenuEntity.get("ROLE_DATA_ATUH")==null?"":sysMenuEntity.get("ROLE_DATA_ATUH").toString())){
          				//具有角色维度的数据权限
          				String ROLE_DATA_ATUH_STR = sysMenuEntity.get("ROLE_DATA_ATUH").toString(); //数据类似 WERKS:C190,C161;,WH_NUMBER:C190,C161;
          				String[] ROLE_DATA_ATUH_A = ROLE_DATA_ATUH_STR.split(";");
          				for (String fieldStr : ROLE_DATA_ATUH_A) {
          					String field = fieldStr.split(":")[0];
          					field = field.replaceAll(",", "").trim();
          					String fieldValue = fieldStr.split(":")[1];
          					if("WERKS".equals(field) && menuAuth_werks.equals("")) {
          						//工厂数据权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_werks.contains(string)) {
          								menuAuth_werks += string +",";
          							}
      							}
          					}
          					
          					if("WORKSHOP".equals(field)) {
          						//MES系统车间权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_mesWorkshop.contains(string)) {
          								menuAuth_mesWorkshop += string +",";
          							}
      							}
          					}
          					
          					if("LINE".equals(field)) {
          						//MES系统线别权限
          						for (String string : fieldValue.split(",")) {
          							if(!menuAuth_mesLine.contains(string)) {
          								menuAuth_mesLine += string +",";
          							}
      							}
          					}
          					
      					}

          			}
      			}
      			
      			
      			if (sysMenuEntity.get("MENU_KEY") != null) {
  					if (!menuAuth_werks.equals("")) {
  						menuAuthMap_werks.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_werks);
  					}
  					if (!menuAuth_wh.equals("")) {
  						menuAuthMap_wh.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_wh);
  					}
  					if (!menuAuth_businessName.equals("")) {
  						menuAuthMap_businessName.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_businessName);
  					}
  					if (!menuAuth_qmsTestGroup.equals("")) {
  						menuAuthMap_qmsTestGroup.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_qmsTestGroup);
  					}
  					if (!menuAuth_mesWorkshop.equals("")) {
  						menuAuthMap_mesWorkshop.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_mesWorkshop);
  					}
  					if (!menuAuth_mesLine.equals("")) {
  						menuAuthMap_mesLine.put(sysMenuEntity.get("MENU_KEY").toString(), menuAuth_mesLine);
  					}
  					
  				}
      			
      			if(newWerks) {
      				userWerkList.add(menuAuthMap_werks);
      			}
      			if(newWh) {
      				userWhList.add(menuAuthMap_wh);
      			}
      			
      			if(newBusinessName) {
      				userBusinessNameList.add(menuAuthMap_businessName);
      			}
      			if(newQmsTestGroup) {
      				userQmsTestGroupList.add(menuAuthMap_qmsTestGroup);
      			}
      			if(newMesWorkshop) {
      				userMesWorkshopList.add(menuAuthMap_mesWorkshop);
      			}
      			if(newMesLine) {
      				userMesLineList.add(menuAuthMap_mesLine);
      			}
      			
  			}
              redisUtils.set(RedisKeys.getUserWerksKey(username), userWerkList);
              redisUtils.set(RedisKeys.getUserWhKey(username), userWhList);
              redisUtils.set(RedisKeys.getUserBusinessNameKey(username), userBusinessNameList);
              redisUtils.set(RedisKeys.getUserQmsTestGroupKey(username), userQmsTestGroupList);
              redisUtils.set(RedisKeys.getUserMesWorkshopKey(username), userMesWorkshopList);
              redisUtils.set(RedisKeys.getUserMesLineKey(username), userMesLineList);
              
              //用户权限信息写入redis
              redisUtils.set(RedisKeys.getUserMenuKey(username), menuSet);
              redisUtils.set(RedisKeys.getUserAuthKey(username), permsSet);
              
              //产生用户token
          	logger.info("-->login 开始产生用户TOKEN！！！");
              String userToken = JWTUtil.generateToken(username);
              logger.info("-->login 产生的用户TOKEN：" + userToken);
              redisUtils.set(RedisKeys.getUserTokenKey(username), userToken);
              return R.ok().put("userToken", userToken);
  	}
}
