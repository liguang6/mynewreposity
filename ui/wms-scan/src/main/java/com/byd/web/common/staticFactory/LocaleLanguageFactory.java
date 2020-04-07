package com.byd.web.common.staticFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.byd.utils.Constant;
import com.byd.web.sys.entity.SysLocaleEntity;

/**
 * @author ren.wei3
 *
 */
public class LocaleLanguageFactory {
	/**
	 * 用于存储所有的国际化 <key ,desc>
	 * key = pkey+lkey+showType
	 */ 
	private static Map<String,String> languageMap = new HashMap<String,String>();    
	/**
	 * 用于存储所有的国际化 <key ,showType>
	 * key = pkey+lkey 
	 */ 
	private static Map<String,String> showTypeMap = new HashMap<String,String>();    
	/**
	 * 默认语言，这个值会在系统加载时从数据库的语言表中更新
	 */
	private static String defLanguage = "ZH_CN";
	private static String defShowType = "M";
	
 
	public static String getValue(String key) {
		return getMessage(key,defShowType);
	}
	
	public static String getValue(String key,Object... params) {
		String value = getMessage(key,defShowType);
		return parsePropertyTokens(value, params);
	}
	
	public static String getValue(String key, String showType) {
		return getMessage(key,showType);
	}
	
	public static String getValue(String key, String showType,Object... params) {
		String value = getMessage(key,showType);
		return parsePropertyTokens(value, params);
	}
	
	public static String getMessage(String key,String showType) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String language = (String)request.getSession().getAttribute(Constant.SESSION_LANGUAGE_KEY);  
		return getMessage(key, language, showType,null);
	}
	
	/**
	 * 提取某个Key的翻译
	 * @Author : CaoQingqing
	 * @Date : Jan 21, 2014 3:45:34 PM
	 * @param key
	 * @param language 
	 * @showType 文本的长、中、短 L M S 
	 * @return
	 */
	public static String getMessage(String key,String language,String showType){
		return getMessage(key,language,showType,null);
	}

	/**
	 * 提取某个Key的翻译
	 * @Author : CaoQingqing
	 * @Date : Jan 21, 2014 3:45:34 PM
	 * @param key
	 * @param language
	 * @param args 参数，部分提示需要带入参数，例如“您的账号将于{0}过期，如果有需要还请于{1}联系。”
	 * @showType 文本的长、中、短 L M S 
	 * @return
	 */
	public static String getMessage(String key,String language,String showType, String[] args) { 
		
		String rtn = null;
		if(language==null){
			language= defLanguage;//默认中文
		}
		key = key.toUpperCase();
		language = language.toUpperCase();
		if(showType!=null){ 
			rtn = languageMap.get(key+"_"+language+"_"+showType);
			if(rtn==null){
				String defShowType=showTypeMap.get(key+"_"+language);
				if(!showType.equals(defShowType)){
					showType = defShowType;
					rtn = languageMap.get(key+"_"+language+"_"+showType);
				}
			}
		}else{ 
			showType=showTypeMap.get(key+"_"+language); 
			if(showType==null)showType = defShowType;
			rtn = languageMap.get(key+"_"+language+"_"+showType);
		} 
		 
		if(args!=null && args.length>0){ 
    		for(int i=0;i<args.length;i++){ 
    			rtn = rtn.replaceAll("\\{"+(i)+"\\}", args[i]); 
    		}
		}
		return rtn;
	}
	
	public static void reloadData(List<SysLocaleEntity> datas){  
		for(SysLocaleEntity c:datas){
			 String showTypeKey = c.getpKey()+"_"+c.getlKey();
			 showTypeMap.put(showTypeKey, c.getDescDef());

			 languageMap.put(showTypeKey+"_L", c.getLongDesc());
			 languageMap.put(showTypeKey+"_M", c.getMiddleDesc());
			 languageMap.put(showTypeKey+"_S", c.getShortDesc());
		}	
	}

	public static String getDefLanguage() {
		return defLanguage;
	}

	public static void setDefLanguage(String defLanguage) {
		LocaleLanguageFactory.defLanguage = defLanguage;
	}
	
	public static String parsePropertyTokens(String string, Object... params) {
		final String OPEN = "{";
        final String CLOSE = "}";
        String newString = string;

        if (newString != null && !newString.isEmpty() && params != null && params.length > 0) {
            int start = newString.indexOf(OPEN);
            int end = newString.indexOf(CLOSE);
            int num = params.length;
            int i = 0;

            while (start > -1 && end > start && i < num) {

                String prepend = newString.substring(0, start);
                String append = newString.substring(end + CLOSE.length());
                Object propValue = params[i++];
                if (propValue == null) {
                    newString = prepend + append;
                } else {
                    newString = prepend + String.valueOf(propValue) + append;
                }
                start = newString.indexOf(OPEN);
                end = newString.indexOf(CLOSE);
            }
        }
        return newString;
	}
}

