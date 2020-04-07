package com.byd.wms.webservice.httpclient.util;

import com.alibaba.fastjson.JSON;
import com.byd.wms.webservice.httpclient.entity.HttpClientResultVO;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Description: httpClient工具类
 * @author rain
 * @date Created on 2019年10月31日14:16:12
 */
public class HttpClientUtil {

	// 编码格式:发送编码格式统一用"UTF-8"
	private static final String ENCODING = "UTF-8";
	
	// 设置连接超时时间，单位毫秒。
	private static final int CONNECT_TIMEOUT = 360000;
	
	// 请求获取数据的超时时间(即响应时间)，单位毫秒。
	private static final int SOCKET_TIMEOUT = 360000;

	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";



	/**
	 * 发送post请求；带请求参数
	 * 
	 * @param url 请求地址
	 * @param jsonStr 请求json
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResultVO doPost(String url, String jsonStr) throws Exception {
		return doPost(url, null, jsonStr);
	}

	/**
	 * 发送post请求；带请求头和请求参数
	 * @param url 请求地址
	 * @param headers 请求头集合
	 * @param jsonStr json传输方式
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResultVO doPost(String url, Map<String, String> headers, String jsonStr) throws Exception {
		// 创建httpClient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// 创建http对象
		HttpPost httpPost = new HttpPost(url);
		/**
		 * setConnectTimeout：设置连接超时时间，单位毫秒。
		 * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
		 * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
		 * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
		 */
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpPost.setConfig(requestConfig);
		// 设置请求头
		/*httpPost.setHeader("Cookie", "");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");*/
		packageHeader(headers, httpPost);
		
		// 封装请求参数
//		packageParam(params, httpPost);
		if(jsonStr!=null) {
			StringEntity s = new StringEntity(jsonStr,ENCODING);
			s.setContentEncoding(ENCODING);//编码格式
			s.setContentType("application/json");//发送json数据需要设置contentType
			httpPost.setEntity(s);
		}

		// 创建httpResponse对象
		CloseableHttpResponse httpResponse = null;
		try {
			// 执行请求并获得响应结果
			return getHttpClientResult(httpResponse, httpClient, httpPost);
		} catch (Exception e){
			return new HttpClientResultVO(1,"Httpclient请求云平台接口交互发生异常！");
		}finally {
			// 必须要释放资源
			release(httpResponse, httpClient);
		}
	}


	/**
	 * 发送post请求；带请求参数
	 *
	 * @param url 请求地址
	 * @param params Map参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResultVO doPost(String url, Map<String, String> params) throws Exception {
		return doPost(url, null, params);
	}

	/**
	 * 发送post请求；带请求参数
	 *
	 * @param url 请求地址
	 * @param headers 请求头Map参数集合
	 * @param params Map参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResultVO doPostHeaderParm(String url,Map<String, String> headers, Map<String, String> params) throws Exception {
		return doPost(url, headers, params);
	}

	/**
	 * 发送post请求；带请求头和请求参数
	 *
	 * @param url 请求地址
	 * @param headers 请求头集合
	 * @param params Map参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResultVO doPost(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
		// 创建httpClient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// 创建http对象
		HttpPost httpPost = new HttpPost(url);
		/**
		 * setConnectTimeout：设置连接超时时间，单位毫秒。
		 * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
		 * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
		 * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
		 */
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpPost.setConfig(requestConfig);
		// 设置请求头
		/*httpPost.setHeader("Cookie", "");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");*/
		packageHeader(headers, httpPost);

		// 封装请求参数
		packageParam(params, httpPost);

		// 创建httpResponse对象
		CloseableHttpResponse httpResponse = null;

		try {
			// 执行请求并获得响应结果
			return getHttpClientResult(httpResponse, httpClient, httpPost);
		} catch (Exception e){
			return new HttpClientResultVO(1,"云平台接口交互发生异常！");
		}finally {
			// 释放资源
			release(httpResponse, httpClient);
		}
	}
	
	/**
	 * Description: 封装请求头
	 * @param params
	 * @param httpMethod
	 */
	public static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
		// 封装请求头
		if (params != null) {
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				// 设置到请求头到HttpRequestBase对象中
				httpMethod.setHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Description: 封装请求参数
	 * 
	 * @param params
	 * @param httpMethod
	 * @throws UnsupportedEncodingException
	 */
	public static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod)
			throws UnsupportedEncodingException {
		// 封装请求参数
		if (params != null) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

			// 设置到请求的http对象中
			httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
		}
	}

	/**
	 * Description: 获得响应结果
	 * 
	 * @param httpResponse
	 * @param httpClient
	 * @param httpMethod
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResultVO getHttpClientResult(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
		// 执行请求
		httpResponse = httpClient.execute(httpMethod);

		 //获取返回结果
		if (httpResponse != null && httpResponse.getStatusLine() != null) {
			if (httpResponse.getEntity() != null) {
				String content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
				HttpClientResultVO resultVO= JSON.parseObject(content,HttpClientResultVO.class);
				return  resultVO;
			}
		}
		return new HttpClientResultVO(1,"云平台接口交互失败！");
	}

	/**
	 * Description: 释放资源
	 * @param httpResponse
	 * @param httpClient
	 * @throws IOException
	 */
	public static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) throws IOException {
		// 释放资源
		if (httpResponse != null) {
			httpResponse.close();
		}
		if (httpClient != null) {
			httpClient.close();
		}
	}

}