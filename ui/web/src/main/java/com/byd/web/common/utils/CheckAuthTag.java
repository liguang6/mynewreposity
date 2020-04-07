package com.byd.web.common.utils;

import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.utils.Constant;
import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;

/**
 * 权限标签
 * 
 * @author cscc
 * @email 
 * @date 2019-03-28
 */
@Component
public class CheckAuthTag {
	@Autowired
	private HttpSession httpSession;
	@Autowired
	RedisUtils redisUtils;
	/**
	 * 是否拥有该权限
	 * @param permission  权限标识
	 * @return   true：是     false：否
	 */
	public boolean hasPermission(String permission) {
		String username = httpSession.getAttribute("USERNAME").toString();
		if(username.toUpperCase().equals(Constant.SUPER)) {
			return true;
		}
		Set userPermsSet = redisUtils.getSet(RedisKeys.getUserAuthKey(username));
		return userPermsSet.contains(permission);
	}

}
