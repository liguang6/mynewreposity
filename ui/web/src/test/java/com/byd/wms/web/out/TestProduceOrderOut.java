package com.byd.wms.web.out;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.byd.web.WebApplication;

/**
 * 出库模块单元测试
 * @author develop07
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestProduceOrderOut {
	
	@LocalServerPort
	private int port;
	
	@Test
	public void test1(){
		
		Assert.assertTrue(true);
	}

}
