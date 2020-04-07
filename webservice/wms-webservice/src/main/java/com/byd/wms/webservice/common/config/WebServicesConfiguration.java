package com.byd.wms.webservice.common.config;

import com.byd.wms.webservice.labelmaster.service.LabelWebService;
import com.byd.wms.webservice.ws.service.*;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.byd.wms.webservice.common.interceptor.AuthInterceptor;
import javax.xml.ws.Endpoint;


/**
 * web service 配置类
 */
@Configuration
public class WebServicesConfiguration {
    @Autowired
    private WmsWebServiceByCloudService wmsWebService;

    @Autowired
	private AuthInterceptor authInterceptor;

    @Autowired
    private WmsWebServiceByLogisticsService wmsWebServiceByLogistics;

    @Autowired
	private WmsWebServiceMesService wmsWebServiceMesService;

    @Autowired
    private WmsWebServiceQmsService wmsWebServiceQmsService;

    @Autowired
    private LabelWebService labelWebService;

    @Autowired
    private WmsWebServiceEwmService ewmService;
    @Autowired
    private WmsStockQueryService wmsStockQueryService;
    @Autowired
    private WmsCallMaterialsService wmsCallMaterialsService;
    /**
     * 注入servlet  bean name不能dispatcherServlet 否则会覆盖dispatcherServlet
     * @return
     */
    @Bean(name = "cxfServlet")
    public ServletRegistrationBean cxfServlet() {
        return new ServletRegistrationBean(new CXFServlet(),"/ws/service/*");
    }


    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    /**
     * 注册QMS接口到webservice服务
     * @return
     */
    @Bean(name = "WmsWebServiceQmsEndpoint")
    public Endpoint qmsEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), wmsWebServiceQmsService);
        endpoint.publish("/qms");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }
    /**
     * 注册立库接口到webservice服务
     * @return
     */
    @Bean(name = "WmsWebServiceLiKu")
    public Endpoint liKuEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), wmsCallMaterialsService);
        endpoint.publish("/liku");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }
    /**
     * 注册云平台接口到webservice服务
     * @return
     */
    @Bean(name = "WmsWebServiceEndpointByCloud")
    public Endpoint yptEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), wmsWebService);
        endpoint.publish("/cloud");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }

    /**
     * 注册总装物流接口到webservice服务
     * @return
     */
    @Bean(name = "WmsWebServiceEndpointByLogistics")
    public Endpoint sweptPayEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), wmsWebServiceByLogistics);
        endpoint.publish("/logistics");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }

    @Bean(name = "WmsWebServiceEndpointByMes")
    public Endpoint mesEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), wmsWebServiceMesService);
        endpoint.publish("/mes");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }

    /**
     * 条码主数据查询接口
     * @return
     */
    @Bean(name = "WmsWebServiceEndpointByLabel")
    public Endpoint labelEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), labelWebService);
        endpoint.publish("/label");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }

    /**
     * EWM->WMS 条码共享接口
     * @return
     */
    @Bean(name = "WmsWebServiceEwmService")
    public Endpoint ewmEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), ewmService);
        endpoint.publish("/ewm");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }
    @Bean(name = "WmsWebServiceEndpointBystocks")
    public Endpoint stockEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), wmsStockQueryService);
        endpoint.publish("/stock");
        endpoint.getInInterceptors().add(authInterceptor);
        return endpoint;
    }

}
