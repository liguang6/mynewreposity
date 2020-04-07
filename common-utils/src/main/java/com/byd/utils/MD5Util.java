package com.byd.utils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringEscapeUtils;

import sun.misc.BASE64Encoder;

public class MD5Util {  
    private static final String secureKey="cswms";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
      
    /** 
     * 验证口令是否合法 
     *  
     * @param password 
     * @param passwordInDb 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException 
     */  
    public static boolean validPassword(String password, String passwordInDb) {  
/*        if(getEncryptedPwd(password).equals(passwordInDb)){
        	return true;
        }  else{
        	return false;
        }*/
    	try {
    		String plain = MD5Util.unescapeHtml(password);
    		byte[] salt;
			salt = MD5Util.decodeHex(passwordInDb.substring(0,16));
    		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
    		password = MD5Util.encodeHex(salt)+MD5Util.encodeHex(hashPassword);
	    	System.out.println("加密密码："+password);
	    	System.out.println("数据库密码："+passwordInDb);
	        if(password.equals(passwordInDb)){
	        	return true;
	        }  else{
	        	return false;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }  
  
    /** 
     * 获得加密后的16进制形式口令 
     *  
     * @param password 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException 
     */  
    public static String getEncryptedPwd(String password)  
            throws NoSuchAlgorithmException, UnsupportedEncodingException {  
    	//确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        StringBuffer sb=new StringBuffer(password);
        sb.append(secureKey);
        String encrptedPwd=base64en.encode(md5.digest(sb.toString().getBytes("utf-8")));
		return encrptedPwd;
    }  
    
    public static String getEncryptedString(String str)  {
		try {
	    	//确定计算方法
	        MessageDigest md5=MessageDigest.getInstance("MD5");
	        BASE64Encoder base64en = new BASE64Encoder();
	        //加密后的字符串
	        StringBuffer sb=new StringBuffer(str);
	        sb.append(secureKey);
	        String encrptedPwd=base64en.encode(md5.digest(sb.toString().getBytes("utf-8")));
			return encrptedPwd;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
    
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		String plain = MD5Util.unescapeHtml(plainPassword);
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword=null;
		try {
			hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MD5Util.encodeHex(salt)+MD5Util.encodeHex(hashPassword);
	}
    
	/**
	 * Hex编码.
	 */
	public static String encodeHex(byte[] input) {
		return new String(Hex.encodeHex(input));
	}

	/**
	 * Hex解码.
	 * @throws Exception 
	 */
	public static byte[] decodeHex(String input) throws Exception {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Html 转码.
	 */
	public static String escapeHtml(String html) {
		return StringEscapeUtils.escapeHtml(html);
	}

	/**
	 * Html 解码.
	 */
	public static String unescapeHtml(String htmlEscaped) {
		return StringEscapeUtils.unescapeHtml(htmlEscaped);
	}
}  
