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

package com.byd.utils;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.xss.SQLFilter;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 查询参数
 *
 * @author Mark 
 * @since 2.0.0 2017-03-14
 */
public class Query<T> extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
    /**
     * mybatis-plus分页参数
     */
    private Page<T> page;
    /**
     * 当前页码
     */
    private int currPage = 1;
    /**
     * 每页条数
     */
    private int limit = 0;

    public Query(Map<String, Object> params){
        this.putAll(params);

        //分页参数
        if(params.get("pageNo") != null&&!params.get("pageNo").equals("")){
            //currPage = Integer.parseInt((String)params.get("page"));
        	currPage = Integer.parseInt((String)params.get("pageNo"));
        }
/*        if(params.get("limit") != null){
            limit = Integer.parseInt((String)params.get("limit"));
        }*/
        if(params.get("pageSize") != null&&!params.get("pageSize").equals("")){
            limit = Integer.parseInt((String)params.get("pageSize"));
        }
        
        this.put("offset", (currPage - 1) * limit);
        this.put("page", currPage);
    	this.put("limit", limit);
        
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String[] orderArr=new String[1];
        if(params.get("orderBy")!=null) {
        	orderArr =params.get("orderBy").toString().split(" ");
        }
       
        String sidx="";
        String order ="";
        for(int i=0;i<orderArr.length;i++) {
        	if(i==0) {
        		sidx= SQLFilter.sqlInject(orderArr[i]);
        	}
        	if(i==1) {
            	order= SQLFilter.sqlInject(orderArr[i]);
        	}
        }
  /*      if(orderArr.length>0) {
        	sidx= SQLFilter.sqlInject(orderArr[0]);
        	order= SQLFilter.sqlInject(orderArr[1]);
        }*/
       
        this.put("sidx", sidx);
        this.put("order", order);
        //mybatis-plus分页
        if(limit>0) {
        	this.page = new Page<>(currPage, limit);
        }else {
        	this.page=new Page<>(currPage,Integer.MAX_VALUE);
        }       

        //排序
        if(StringUtils.isNotBlank(sidx) && StringUtils.isNotBlank(order)){
            this.page.setOrderByField(sidx);
            this.page.setAsc("ASC".equalsIgnoreCase(order));
        }

    }

    public Page<T> getPage() {
        return page;
    }

    public int getCurrPage() {
        return currPage;
    }

    public int getLimit() {
        return limit;
    }
}
