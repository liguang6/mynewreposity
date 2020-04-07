package com.byd.sap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.WebApplicationInitializer;

@EnableEurekaClient
@SpringBootApplication
@EnableCaching
@MapperScan(value = "com.byd.sap.modules.job.dao")
/*@MapperScan({"com.byd.admin.modules.*.dao","com.byd.sap.modules.job.dao"}) */
@ComponentScan(basePackages= {"com.byd.sap.controller","com.byd.sap.rfc","com.byd.sap.modules","com.byd.sap.dao","com.byd.sap.service","com.byd.sap.bapi"})
public class SapServiceApplication  extends SpringBootServletInitializer implements WebApplicationInitializer {
	
    public static void main(String[] args) {
    	
    	SpringApplication.run(SapServiceApplication.class, args);
    }
    
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SapServiceApplication.class);
	}

}	