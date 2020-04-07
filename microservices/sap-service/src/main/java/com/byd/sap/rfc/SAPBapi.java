package com.byd.sap.rfc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.byd.sap.modules.job.dao.ISapSyncDao;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;  

@Service("sapBapi")
public class SAPBapi {
	@Autowired
	private SapConfigConstant sapConfigConstant;
	@Autowired
	private ISapSyncDao sapSyncDao;
	private static Properties connectProperties = new Properties();
	private static final String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";  
	
	public String init(String WERKS) {
		Map<String,String> sapUserMap = sapSyncDao.getSapUserByWerks(WERKS);
		if(null == sapUserMap || null ==sapUserMap.get("SAP_USER")) {
			return "工厂"+WERKS+"未配置SAP通讯账号！";
		}
		String jco_user = sapUserMap.get("SAP_USER");
		String jco_pass = sapUserMap.get("SAP_PASSWORD");
		System.out.println("-->Host = " + sapConfigConstant.getHost() + ",jco_user = " + jco_user);
		if(sapConfigConstant.getGroup_flag()) {
			connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME,sapConfigConstant.getR3name());// prd
		    connectProperties.setProperty(DestinationDataProvider.JCO_GROUP,sapConfigConstant.getGroup());// bydprd
		    connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, sapConfigConstant.getHost());//	SAP message server host
		    connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV, "3600");//SAP message server service or port number (optional)
		} else {
			connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapConfigConstant.getHost());//服务器  192.168.100.23
	        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");        	//系统编号  
		}
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "800");       	//SAP集团  
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   jco_user);  		//SAP用户名  tanmeirong
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, jco_pass);     	//密码  20180625
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "zh");       		//登录语言  
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "20");  	//最大连接数    
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");     	//最大连接线程     

        createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);  
        return "";
	}
	
	public void init() {
		System.out.println("-->Host = " + sapConfigConstant.getHost());
		if(sapConfigConstant.getGroup_flag()) {
			connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME,sapConfigConstant.getR3name());// prd
		    connectProperties.setProperty(DestinationDataProvider.JCO_GROUP,sapConfigConstant.getGroup());// bydprd
		    connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, sapConfigConstant.getHost());//	SAP message server host
		    connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV, "3600");//SAP message server service or port number (optional)
		    connectProperties.setProperty(DestinationDataProvider.JCO_MAX_GET_TIME, sapConfigConstant.getTime_out());     //最大连接时间
		} else {
			connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapConfigConstant.getHost());//服务器  192.168.100.23
	        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,  "00");        	//系统编号  
		}
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "800");       	//SAP集团  
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,   sapConfigConstant.getUser());  		//SAP用户名  tanmeirong
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapConfigConstant.getPassword());     //密码  20180625
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   "zh");       		//登录语言  
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "20");  	//最大连接数    
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "40");     	//最大连接线程 

        createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties); 
	}
	
	/** 
     * 创建SAP接口属性文件。 
     * @param name  		ABAP管道名称 
     * @param suffix    	属性文件后缀 
     * @param properties    属性文件内容 
     */  
    private static void createDataFile(String name, String suffix, Properties properties){  
        File cfg = new File(name+"."+suffix);  
        if(cfg.exists()){  
            cfg.deleteOnExit();  
        }  
        try{  
            FileOutputStream fos = new FileOutputStream(cfg, false);  
            properties.store(fos, "for tests only !");  
            fos.close();  
        }catch (Exception e){  
            System.out.println("Create Data file fault, error msg: " + e.toString());  
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);  
        }  
    }  
    
    /** 
     * 获取SAP连接 
     * @return  SAP连接对象 
     */  
    public JCoDestination connect(){  
        JCoDestination destination =null;  
        try {  
            destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);  
        } catch (JCoException e) {  
        	System.out.println("Connect SAP fault, error msg: " + e.toString());  
        }  
        return destination;  
    }  
    
}
