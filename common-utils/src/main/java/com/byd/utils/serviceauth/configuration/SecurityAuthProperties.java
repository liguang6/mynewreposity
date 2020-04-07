package com.byd.utils.serviceauth.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author develop01
 * @date 2019-01-18
 */
@ConfigurationProperties(prefix = "security.auth")
@Component
public class SecurityAuthProperties {
    private static Logger LOGGER = LoggerFactory.getLogger(SecurityAuthProperties.class);

    /**
     * 需要鉴权的uri
     */
    private String authUri = "/admin-service/,/wms-service/,/qms-service/";
    //需要认证的uri
    private String checkUri ="/sap-service/";

    private List<String> authUriList;
    private List<String> checkUriList;

    /**
     * debug模式
     */
    private boolean debug = false;

    /**
     * 是否只生成token
     */
    private boolean onlyGenerateToken = false;

    public SecurityAuthProperties() {
    	authUriList = initList(authUri);
    	checkUriList = initList(checkUri);
    }

    private List<String> initList(String str) {
        if (null != str && str.trim().length() > 0) {
            return Arrays.asList(str.split(","));
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getAuthUriList() {
        return authUriList;
    }

    public String getAuthUri() {
        return authUri;
    }

    public void setAuthUri(String authUri) {
        if (StringUtils.isNotBlank(authUri)) {
            this.authUri += authUri;
        }
        LOGGER.info("需要安全认证的uri:{}", this.authUri);
        this.authUriList = initList(this.authUri);
        this.authUri = authUri;
    }
    
    public List<String> getCheckUriList() {
        return checkUriList;
    }

    public String getCheckUri() {
        return checkUri;
    }

    public void setCheckUri(String checkUri) {
        if (StringUtils.isNotBlank(checkUri)) {
            this.checkUri += checkUri;
        }
        LOGGER.info("需要安全认证的uri:{}", this.checkUri);
        this.checkUriList = initList(this.checkUri);
        this.checkUri = checkUri;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isOnlyGenerateToken() {
        return onlyGenerateToken;
    }

    public void setOnlyGenerateToken(boolean onlyGenerateToken) {
        this.onlyGenerateToken = onlyGenerateToken;
    }
}
