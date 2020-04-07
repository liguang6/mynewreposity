package com.byd.admin.common.runner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.byd.admin.modules.sys.service.SysMenuService;
import com.byd.utils.RedisKeys;
import com.byd.utils.RedisUtils;
import com.byd.utils.StringUtils;


/**
 * @author develop01
 *
 */
@Component
public class StartLoadingApplicationRunner implements ApplicationRunner{
	@Autowired
    private SysMenuService sysMenuService;
	@Autowired
    private RedisUtils redisUtils;
	
	@Override
    public void run(ApplicationArguments args) throws Exception {
		//admin项目启动时，从数据库加载系统所有权限清单
		 //查询系统所有权限信息
		List<Map<String, Object>> allMenuList = sysMenuService.getAllAuthList();
        Set<String> allMenuSet = new HashSet<>();
  		//所有权限列表
		Set<String> allPermsSet = new HashSet<>();
        for (Map<String, Object> map : allMenuList) {
			if(StringUtils.isNotBlank(map.get("URL")==null?"":map.get("URL").toString())){
				allMenuSet.addAll(Arrays.asList(map.get("URL").toString().trim()));
			}
			if(StringUtils.isNotBlank(map.get("PERMS")==null?"":map.get("PERMS").toString())){
				allPermsSet.addAll(Arrays.asList(map.get("PERMS").toString().trim().split(",")));
			}
		}
        redisUtils.set(RedisKeys.getAllMenuKey(), allMenuSet,redisUtils.NOT_EXPIRE);
        redisUtils.set(RedisKeys.getAllAuthKey(), allPermsSet,redisUtils.NOT_EXPIRE);

    }
}
