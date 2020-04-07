package com.byd.utils.serviceauth;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;



public class JWTUtil {
	    public static final String SECRET = "wms_auth#cs@byd";
	    public static final String TOKEN_PREFIX = "wms.byd.com";
	    public static final String JTI="JWT_WMS";
	    public static final String ISSUER ="WMS_ADMIN";
	    /** token 过期时间: 1天 */
	    public static final int calendarField = Calendar.DATE;
	    public static final int EXPTIME = 1;

	    /**
	     * 
	     * @param custClaim 自定义传输数据 ：username等
	     * @param jti jwt id 编号
	     * @param iss  签发人
	     * @return
	     */
	    public static String generateToken(String username) {
	        Date iatDate = new Date();
	        // expire time
	        Calendar nowTime = Calendar.getInstance();
	        nowTime.add(calendarField, EXPTIME);
	        Date expiresDate = nowTime.getTime();

	        // header Map
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("alg", "HS256");
	        map.put("typ", "JWT");
            
	        String jwt = JWT.create().withHeader(map) // header
	        	  .withClaim("ISSUER", ISSUER) // payload
	        	  .withClaim("JTI", JTI)
	        	  .withClaim("username", null == username ? null : username.toString())
    			  .withIssuedAt(iatDate) // sign time
                  .withExpiresAt(expiresDate) // expire time
                  .sign(Algorithm.HMAC256(SECRET)); // signature
	        String finalJwt = TOKEN_PREFIX +jwt;
	        return finalJwt;
	    }

	    /**
	     * 解析token
	     * @param token
	     * @return
	     */
	    public static Map<String,Claim> parseToken(String token) {
	    	DecodedJWT jwt = null;
	    	try {
		        if (token != null) {
		        	JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
		        	jwt = verifier.verify(token.replace(TOKEN_PREFIX, ""));
		        	return jwt.getClaims();
		        } else {
		        	return null;
		        }
	        } catch (Exception e) {
	            // e.printStackTrace();
	            // token 校验失败, 抛出Token验证非法异常
	        }
	        return null;
	    }
	    
	    public static String getUsername(String token) {
	        Map<String, Claim> claims = parseToken(token);
	        if(null == claims) {
	        	return "";
	        }
	        Claim username_claim = claims.get("username");
	        if (null == username_claim || StringUtils.isEmpty(username_claim.asString())) {
	            // token 校验失败, 抛出Token验证非法异常
	        	return "";
	        }
	        return username_claim.asString();
	    }
	    
	}
