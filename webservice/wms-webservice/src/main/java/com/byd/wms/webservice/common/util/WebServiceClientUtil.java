package com.byd.wms.webservice.common.util;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date $time$ $date$
 * @Description //TODO $end$
 **/

@Component
public class WebServiceClientUtil {

        private static JaxWsDynamicClientFactory dcf ;
        private Client client;
        private Logger logger = LoggerFactory.getLogger(WebServiceClientUtil.class);
//        public WebServiceClientUtil(){
//        	dcf=JaxWsDynamicClientFactory.newInstance();
//        }
//
        static{
                dcf=JaxWsDynamicClientFactory.newInstance();
        }

		public Client getClient(String str){
                client = dcf.createClient(str);
                return client;
        }

        public Object[] invoke(String operationName,Object... params){
                Object[] objects = null;
                try {
                        objects = client.invoke(operationName,params);
                }catch (Exception e){
                        e.printStackTrace();
                        logger.error("webService===>>> "+operationName+" 调用失败!");
                }
                return objects;
        }

        public Object[] invoke(String clientStr,String operationName,Object... params){
                Object[] objects = new Object[0];
                try {
                		client = dcf.createClient(clientStr);
                        objects = client.invoke(operationName,params);
                }catch (Exception e){
                        e.printStackTrace();
                        logger.error("webService===>>> "+operationName+" 调用失败!");
                }
                return objects;
        }



}
