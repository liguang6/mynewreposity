package com.byd.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpRequestWrapper extends HttpServletRequestWrapper {
		 
	    private Map<String, String> customHeaders;
	 
	    public HttpRequestWrapper(HttpServletRequest request) {
	        super(request);
	        this.customHeaders = new HashMap<>();
	    }
	 
	    public void putHeader(String name, String value) {
	        this.customHeaders.put(name, value);
	    }
	 
	    public String getHeader(String name) {
	        String value = this.customHeaders.get(name);
	        if (value != null) {
	            return value;
	        }
	        return ((HttpServletRequest) getRequest()).getHeader(name);
	    }
	 
	    public Enumeration<String> getHeaderNames() {
	        Set<String> set = new HashSet<>(customHeaders.keySet());
	        Enumeration<String> enumeration = ((HttpServletRequest) getRequest()).getHeaderNames();
	        while (enumeration.hasMoreElements()) {
	            String name = enumeration.nextElement();
	            set.add(name);
	        }
	        return Collections.enumeration(set);
	    }
	 
	}

