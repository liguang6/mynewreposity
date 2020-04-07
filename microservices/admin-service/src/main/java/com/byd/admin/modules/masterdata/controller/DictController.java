package com.byd.admin.modules.masterdata.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.modules.masterdata.entity.DictEntity;
import com.byd.admin.modules.masterdata.service.DictService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.StringUtils;
import com.byd.utils.validator.ValidatorUtils;

/**
 * 数据字典
 *
 * @author Mark 
 * @since 3.1.0 2018-01-27
 */
@RestController
@RequestMapping("/masterdata/dict")
public class DictController {
    @Autowired
    private DictService sysDictService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysDictService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DictEntity dict = sysDictService.selectById(id);

        return R.ok().put("dict", dict);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);
        dict.setDelFlag(0);
        try{
        Map<String, Object> condMap=new HashMap<String, Object>();
        condMap.put("TYPE", dict.getType());
        condMap.put("CODE", dict.getCode());
        List<DictEntity>  retDict=sysDictService.selectByMap(condMap);
        if(retDict!=null&&retDict.size()>0){
			throw new RuntimeException("该类型和编码已经存在！");
		}
        sysDictService.insert(dict);
        } catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody DictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);

        sysDictService.updateById(dict);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteById")
    public R deleteById(long id){
        sysDictService.deleteById(id);

        return R.ok();
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        sysDictService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
    /**
     * 获取列表
     */
    @RequestMapping("/getDictlist")
    public R getDictlist(@RequestParam Map<String, Object> params){
        List<DictEntity> list = sysDictService.selectByMap(params);
        return R.ok().put("list", list);
    }
    
    /**
     * 获取列表
     */
    @RequestMapping("/getDictlistByType")
    public List<Map<String,Object>> getDictlistByType(@RequestParam Map<String, Object> params){
    	String TYPE = params.get("TYPE")==null?params.get("NAME")==null?"":params.get("NAME").toString():params.get("TYPE").toString();
    	if(StringUtils.isEmpty(TYPE)) {
    		return null;
    	}
        return sysDictService.getDictlistByType(params);
    }
    
    /**
     * 获取列表
     */
    @RequestMapping("/selectByMap")
    public List<DictEntity> selectByMap(@RequestParam Map<String, Object> params){
        return sysDictService.selectByMap(params);
    }
    
    //获取字典表工厂OrderNum用于部件MES生成编号
    @RequestMapping("/queryMasterDictWerksOrderNum")
    public int queryMasterDictWerksOrderNum(@RequestParam Map<String, Object> params){
        return sysDictService.queryMasterDictWerksOrderNum(params);
    }
}
