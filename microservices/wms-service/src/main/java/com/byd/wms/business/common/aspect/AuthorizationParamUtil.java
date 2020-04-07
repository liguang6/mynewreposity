package com.byd.wms.business.common.aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.utils.Constant;
import com.byd.utils.StringUtils;
import com.byd.utils.UserUtils;
import com.byd.utils.exception.RRException;
import com.byd.wms.business.common.annotation.PermissionDataFilter;
import com.byd.wms.business.common.remote.SysDataPermissionRemote;

/**
 * 数据过滤，切面处理类
 *
 * @author ren.wei3@byd.com
 * @since 3.0.0 2019-03-06
 */
@Aspect
@Component
public class AuthorizationParamUtil {

    @Autowired
    private UserUtils userUtils;
    @Autowired
    private SysDataPermissionRemote sysDataPermissionRemote;

    @Pointcut("@annotation(com.byd.wms.business.common.annotation.PermissionDataFilter)")
    public void PermissionDataFilterCut() {

    }

    @Before("PermissionDataFilterCut()")
    public void PermissionDataFilter(JoinPoint point) throws Throwable {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){
            Map<String,Object> user = userUtils.getUser();
            MethodSignature signature = (MethodSignature) point.getSignature();
            PermissionDataFilter permissionDataFilter= signature.getMethod().getAnnotation(PermissionDataFilter.class);
            //如果不是超级管理员，则进行数据过滤
//            if(Long.parseLong(user.get("USER_ID").toString()) != Constant.SUPER_ADMIN){
            	//menuType 1 菜单权限，2 按钮操作权限
            	if (permissionDataFilter.menuType().equals("2")) {
	            	Map map = (Map)params;
	                getAuthFilter(user,permissionDataFilter.menuKey(),map);
	            } else {
		            Map map = (Map)params;
		            map.put(Constant.SQL_FILTER, getSQLFilter(user, permissionDataFilter.menuKey(),map));
	            }
//            }

            return ;
        }

        throw new RRException("数据权限接口，只能是Map类型参数，且不能为NULL");
    }



    /**
     * 获取数据过滤的SQL
     */
    public  List<Map<String,Object>> queryDataPermission(String userId,String menuKey){
        HashMap hm = new HashMap();
        hm.put("userId",userId);
        hm.put("menuKey",menuKey);
        List<Map<String,Object>> lists = sysDataPermissionRemote.queryDataPermissionByMenuKey(hm);
        return lists;
    }

    public String getSQLFilter(Map<String,Object> user,String menuKey,Map map) {
        List<Map<String,Object>> lists = queryDataPermission(user.get("USER_ID").toString(),menuKey);

        //把key一样的value合并
        List<Map<String,Object>> listEnd = Combine(user.get("USER_ID").toString(),lists);

        String authsqlstr = "";
        for(int i = 0 ;i<listEnd.size();i++){
        		boolean delflag = false;
        		String authsqlstrtemp = "";
	        	List<String> insqllist = new ArrayList<String>();
	            List<String> sqllikelist = new ArrayList<String>();
	            List<String> andsqllist = new ArrayList<String>();
                String value = listEnd.get(i).get("AUTH_VALUE").toString();
                String values[] = null;
                values = value.split(",");
                if (value.equals("*")) { //*号表示所有权限, 所以不作为查询条件
                    delflag = true;
                } else if (value.contains("*")) { //权限值中包含通配符*时, 拼接LIKE条件语句
                    if(values.length>1){
                    	String authsqlstrTemp = "";
                    	List<String> insqllistTemp = new ArrayList<String>();
                        List<String> sqllikelistTemp = new ArrayList<String>();
                        for(int j= 0 ;j<values.length;j++){
                            String val = values[j];
                            if(val.contains("*")){
                                val = val.replaceAll("\\*", "%");
                                String sqllike = listEnd.get(i).get("AUTH_FIELDS") +" like '" + val + "' ";
                                sqllikelistTemp.add(sqllike);
                            }else{
                            	insqllistTemp.add(val);
                            }
                        }
                        
                        //拼接in类型条件
                        if (insqllistTemp.size() > 0) {
                        	authsqlstrTemp = authsqlstrTemp+ listEnd.get(i).get("AUTH_FIELDS") + " in (";
                            for (String insql:insqllistTemp) {
                            	authsqlstrTemp = authsqlstrTemp + "'" + insql + "',";
                            }
                            authsqlstrTemp = authsqlstrTemp.substring(0,authsqlstrTemp.lastIndexOf(",")) + ") ";
                        }

                        //拼接LIKE类型条件
                        if (sqllikelistTemp.size() > 0) {
                            for (String likesql:sqllikelistTemp) {
                            	if (StringUtils.isBlank(authsqlstrTemp)) {
                            		authsqlstrTemp = authsqlstrTemp +" " + likesql;
                            	} else {
                            		authsqlstrTemp = authsqlstrTemp +" or " + likesql;
                            	}
                            }
                        }
                        
                        String andsqlstr = " ( " + authsqlstrTemp + " ) ";
                        andsqllist.add(andsqlstr);
                    }else {
                    	String val = values[0].replaceAll("\\*", "%");
                        String sqllike = listEnd.get(i).get("AUTH_FIELDS") +" like '" + val + "' ";
                        sqllikelist.add(sqllike);
                    }

                } else {
                    if(value.contains(",")){
                        insqllist = Arrays.asList(values);
                    }else {
                        //insqllist.clear();
                        insqllist.add(value); //多值使用IN
                    }

                }

                if (!delflag && (insqllist.size() > 0 || sqllikelist.size() > 0 || andsqllist.size() > 0)) {

                    //拼接in类型条件
                    if (insqllist.size() > 0) {
                    	authsqlstrtemp = authsqlstrtemp+ listEnd.get(i).get("AUTH_FIELDS") + " in (";
                        for (String insql:insqllist) {
                        	authsqlstrtemp = authsqlstrtemp + "'" + insql + "',";
                        }
                        authsqlstrtemp = authsqlstrtemp.substring(0,authsqlstrtemp.lastIndexOf(",")) + ") ";
                    }

                    //拼接LIKE类型条件
                    if (StringUtils.isBlank(authsqlstrtemp) && sqllikelist.size() > 0) {
                        for (String likesql:sqllikelist) {
                        	authsqlstrtemp = authsqlstrtemp +" " + likesql +" or ";
                        }
                        authsqlstrtemp = authsqlstrtemp.substring(0,authsqlstrtemp.length()-3);
                    }else if(StringUtils.isNotBlank(authsqlstrtemp) && sqllikelist.size() > 0){
                        for (String likesql:sqllikelist) {
                        	authsqlstrtemp = authsqlstrtemp + likesql;
                        }
                    }
                    
                  //拼接and 后接一组权限值
                    if (andsqllist.size() > 0) {
                        for (String andsql:andsqllist) {
                        	authsqlstrtemp = authsqlstrtemp + andsql ;
                        }
                    }
                    
                    map.put("AUTHSQL_" + listEnd.get(i).get("AUTH_FIELDS"), authsqlstrtemp); //KEY格式，权限对象加前缀 "AUTHSQL_"，于业务数据KEY区分开
                    
                    authsqlstr = authsqlstr + authsqlstrtemp + "and ";
                    
                }

        }

        //去掉最后多余的“and”
        if (!authsqlstr.equals("") && authsqlstr.substring(authsqlstr.length()-4, authsqlstr.length()).equals("and ")) {

            authsqlstr = authsqlstr.substring(0, authsqlstr.length()-5);
        }
        return authsqlstr;
    }
    
    public void getAuthFilter(Map<String,Object> user,String menuKey,Map map) {
    	List<Map<String,Object>> lists = queryDataPermission(user.get("USER_ID").toString(),menuKey);

        //把key一样的value合并
        List<Map<String,Object>> listEnd = Combine(user.get("USER_ID").toString(),lists);

        for(int i = 0 ;i<listEnd.size();i++){
        		
        		List<String> authlist = new ArrayList<String>();
        		boolean delflag = false;
                String value = listEnd.get(i).get("AUTH_VALUE").toString();
                String values[] = null;
                values = value.split(",");
                if (value.equals("*")) { //*号表示所有权限, 所以不作为查询条件
                    delflag = true;
                } else if (value.contains("*")) { //权限值中包含通配符*时, 拼接LIKE条件语句
                    if(values.length>1){
                        for(int j= 0 ;j<values.length;j++){
                            String val = values[j];
                            if(val.contains("*")){
                                val = val.replaceAll("\\*", ".*");
                                authlist.add(val);
                            }else{
                            	authlist.add(val);
                            }
                        }
                    }else {
                    	String val = values[0].replaceAll("\\*", ".*");
                        authlist.add(val);
                    }

                } else {
                    if(value.contains(",")){
                    	authlist = Arrays.asList(values);
                    }else {
                        //insqllist.clear();
                    	authlist.add(value); //多值使用IN
                    }

                }

                if (!delflag && authlist.size() > 0 ) {
                	
                	map.put("auth_"+listEnd.get(i).get("AUTH_FIELDS"), authlist); //KEY格式，权限对象加前缀 "auth_"，于业务数据KEY区分开
                    
                }

        }
    }

    /**
     * Z合并重复的数据权限，
     * rules(规则):
     * 1、优先取维护在用户上的权限，后取角色上的。
     * 2、优先取指定菜单的权限，后取所有菜单的权限
     * @param userid
     * @param lists
     * @return
     */
    public static List<Map<String,Object>> Combine(String userid,List<Map<String,Object>> lists) {
    	List<Map<String,Object>>lmm= new ArrayList();
    	List<Map<String,Object>>relmm= new ArrayList();
        for(int i = 0;i<lists.size();i++){
            if(lists.get(i).get("USER_ID")!=null && lists.get(i).get("USER_ID").toString()!=""){
                String userId = lists.get(i).get("USER_ID").toString();
                if(userId.equals(userid)){
                    lmm.add(lists.get(i));
                    lists.remove(i);
                    i--;
                }
            }
        }
        for(int i = 0;i<lists.size();i++){
            for(int j = 0;j<lmm.size();j++){
                if(lists.size()>0&&(lists.get(i).get("MENU_ID")+"-"+lists.get(i).get("AUTH_FIELDS"))
                        .equals(lmm.get(j).get("MENU_ID")+"-"+lmm.get(j).get("AUTH_FIELDS"))){
                    lists.remove(i);
                    if(lists.size()>0){
                        i--;
                        break;
                    }
                }
            }
        }
        lmm.addAll(lists);
        
        for(int i = 0;i<lmm.size();i++){
        	if(lmm.size()>0 && !lmm.get(i).get("MENU_ID").toString().equals("0")){
        		relmm.add(lmm.get(i));
        		lmm.remove(i);
        		i--;
            }
        }
        
        for(int i = 0;i<lmm.size();i++){
            for(int j = 0;j<relmm.size();j++){
                if(lmm.size()>0&&(lmm.get(i).get("AUTH_FIELDS")).equals(relmm.get(j).get("AUTH_FIELDS"))){
                	lmm.remove(i);
                    if(lmm.size()>0){
                        i--;
                        break;
                    }
                }
            }
        }
        relmm.addAll(lmm);
        
        HashMap<String,String> mapAll = new HashMap<String,String>();
        for(Map<String,Object> data:relmm){
            String key = data.get("AUTH_FIELDS").toString();
            String value = data.get("AUTH_VALUE").toString();
            String value2 =  mapAll.get(key);
            if(value2 == null){
                mapAll.put(key, value);
            }else{
                value2 = value+","+value2;
                mapAll.put(key, value2);
            }
        }

        Iterator it = mapAll.entrySet().iterator();
        Map<String,Object> sb = null;
        List<Map<String,Object>> listEnd = new ArrayList<>();
        while(it.hasNext()){
            sb = new HashMap<String,Object>();
            Map.Entry en = (Map.Entry) it.next();
            sb.put("AUTH_FIELDS",(String) en.getKey());
            sb.put("AUTH_VALUE",(String) en.getValue());
            listEnd.add(sb);
        }

        return listEnd;

    }










}
