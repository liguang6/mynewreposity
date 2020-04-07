package com.byd.admin.modules.sys.service;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import com.byd.utils.PageUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年5月5日 上午8:42
 * 类说明 
 */
public interface SysNewsService{
	//分页查询
	PageUtils queryPage(Map<String, Object> params);
	
	PageUtils query(Map<String, Object> params);

    void saveNoticeMail(Map<String, Object> params);

	Map<String, Object> selectById(Long id);

	void updateById(Map<String, Object> params);

    void delById(Long id);
    
    List<Map<String,Object>> getUserNews(Map<String, Object> params);
}
