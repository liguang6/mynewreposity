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

package com.byd.zzjmes.common.aspect;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.utils.DateUtils;
import com.byd.utils.HttpContextUtils;
import com.byd.utils.IPUtils;
import com.byd.zzjmes.common.annotation.SysLog;
import com.byd.zzjmes.common.remote.SysLogRemote;
import com.google.gson.Gson;

/**
 * 系统日志，切面处理类
 *
 * @author Mark 
 * @since 1.3.0 2017-03-08
 */
@Aspect
@Component
public class SysLogAspect {
	@Autowired
	private SysLogRemote sysLogRemote;
	@Autowired
	
	
	@Pointcut("@annotation(com.byd.zzjmes.common.annotation.SysLog)")
	public void logPointCut() { 
		
	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long beginTime = System.currentTimeMillis();
		//执行方法
		Object result = point.proceed();
		//执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;

		//保存日志
		saveSysLog(point, time);

		return result;
	}

	private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Map<String, Object> sysLog = new HashMap<String, Object>();
		SysLog syslog = method.getAnnotation(SysLog.class);
		if(syslog != null){
			//注解上的描述
			sysLog.put("OPERATION", syslog.value());
		}

		//请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLog.put("METHOD",className + "." + methodName + "()");

		//请求的参数
		Object[] args = joinPoint.getArgs();
		try{
			String params = new Gson().toJson(args[0]);
			sysLog.put("PARAMS", params);
		}catch (Exception e){

		}

		//获取request
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		//设置IP地址
		sysLog.put("IP",IPUtils.getIpAddr(request));

		//用户名
		String username = request.getParameter("username");
		sysLog.put("USERNAME",username);

		sysLog.put("TIME",time);
		sysLog.put("CREATE_DATE",DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		//保存系统日志
		sysLogRemote.addLog(sysLog);
	}
}
