package com.byd.wms.webservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.WebApplicationInitializer;

import com.byd.utils.serviceauth.annotation.EnableUserInfoTransmitter;

@EnableFeignClients
@EnableEurekaClient
@EnableCaching
@MapperScan(value = "com.byd.wms.webservice.*.dao")
@ComponentScan(basePackages= {"com.byd.wms.webservice","com.byd.utils"})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableUserInfoTransmitter
public class WebServiceApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WebServiceApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebServiceApplication.class);
	}



}

