package com.byd.web.wms.config.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCoreWhBinEntity;
import com.byd.web.wms.config.service.WmsCoreWhBinRemote;
/**
 * 仓库储位
 *
 * @author tangj
 * @email 
 * @date 2018-08-07 15:36:51
 */
@RestController
@RequestMapping("config/corewhbin")
public class WmsCoreWhBinController {
    @Autowired
    private WmsCoreWhBinRemote wmsCoreWhBinRemote;
    @Autowired
    private UserUtils userUtils;
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsCoreWhBinRemote.list(params);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        return wmsCoreWhBinRemote.info(id);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WmsCoreWhBinEntity bin){
    	Map<String,Object> user = userUtils.getUser();
	    bin.setEditor(user.get("USERNAME")+"："+user.get("FULL_NAME"));
	    bin.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
	    bin.setDel("0");
        return wmsCoreWhBinRemote.save(bin);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WmsCoreWhBinEntity wmsCoreWhBin){
        return wmsCoreWhBinRemote.update(wmsCoreWhBin);
    }
    /**
     * 删除
     */
    @RequestMapping("/delById")
    public R delById(Long id){
    	if(id == null){
			return R.error("参数错误");
		}
    	WmsCoreWhBinEntity entity = new WmsCoreWhBinEntity();
		entity.setId(id);
		entity.setDel("X");
        return wmsCoreWhBinRemote.delById(entity);
    }
    @RequestMapping("/import")
	public R upload(@RequestBody List<WmsCoreWhBinEntity> entityList) throws IOException{
    	
		return wmsCoreWhBinRemote.upload(entityList);
	}
	
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel) throws IOException{
		Map<String,Object> user = userUtils.getUser();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", ++index);
				map.put("whNumber", row[0]);
				map.put("storageAreaCode", row[1]);
				map.put("binCode", row[2] );
				map.put("binName", row[3]);
				map.put("binType", row[4]);
				map.put("binRow", row[5] );
				map.put("binColumn", row[6] );
				map.put("binFloor", row[7] );
				map.put("vl", row[8] );
				map.put("vlUnit", row[9] );
				map.put("wt", row[10] );
				map.put("wtUnit", row[11] );
				map.put("vlUse", row[12] );
				map.put("wtUse", row[13] );
				map.put("turnoverRate", row[14] );
				map.put("aStorageUnit", row[15] );
				map.put("uStorageUnit", row[16] );
				map.put("x", row[17] );
				map.put("y", row[18] );
				map.put("z", row[19] );
				map.put("binStatus", row[20]);
				//map.put("del", "0");
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				//map.put("editDate",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				entityList.add(map);
			}
		}
				
		return wmsCoreWhBinRemote.preview(entityList);
	}  
	
	/**
     * 通过仓库号和储位查询
     */
    @RequestMapping("/queryBinCode")
    public R queryBinCode(@RequestParam Map<String, Object> params){
        return wmsCoreWhBinRemote.queryBinCode(params);
    }
}
