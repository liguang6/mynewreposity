package com.byd.web.wms.kn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.*;
import com.byd.utils.qrcode.QRCodeUtil;
import com.byd.web.wms.config.entity.WmsCoreStorageSearchEntity;
import com.byd.web.wms.kn.service.WmsKnLabelRecordRemote;
import com.byd.web.wms.kn.service.WmsKnStorageMoveRemote;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 条码管理
 *
 * @author qjm
 * @email 
 * @date 2019-04-01
 */
@RestController
@RequestMapping("kn/labelRecord")
public class WmsKnLabelRecordController {
    @Autowired
    private WmsKnLabelRecordRemote wmsKnLabelRecordRemote;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private FreeMarkerConfigurer configurer;
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private ConfigConstant configConstant;


    /**
     * 拆分页面查询
     */
    @RequestMapping("/listByCf")
    public R listByCf(@RequestParam Map<String, Object> params){
        return wmsKnLabelRecordRemote.queryByCf(params);
    }

    /**
     *
     * 保存
     */
    @RequestMapping("/saveByCf")
    public R saveByCf(@RequestParam Map<String, Object> params) {
        List<Map> matListMap = JSONObject.parseArray(params.get("params").toString(), Map.class);
        return wmsKnLabelRecordRemote.saveByCf(params);
    }

    @RequestMapping("/labelLabelPreview")
    public void labelPreview(@RequestParam Map<String, Object> params) {
        String labelListStr = params.get("labelList")==null?null:params.get("labelList").toString();
        String LIKTX = params.get("LIKTX")==null?"":params.get("LIKTX").toString();
        // 构造freemarker模板引擎参数,listVars.size()个数对应pdf页数
        List<Map<String,Object>> listVars = new ArrayList<>();
        String baseDir = configConstant.getLocation()+"/barcode/";
        if(labelListStr!=null && labelListStr.length()>0) {
            JSONObject.parseArray(labelListStr, Map.class).forEach(m->{
                m=(Map<String,Object>)m;
                Map<String,Object> variables = new HashMap<>();
                variables.put("LIKTX",LIKTX==null? m.get("LIKTX") :LIKTX);
                variables.put("LABEL_NO",m.get("LABEL_NO")==null?"":m.get("LABEL_NO"));
                variables.put("MATNR",m.get("MATNR")==null?"":m.get("MATNR"));
                variables.put("MAKTX",m.get("MAKTX")==null?"":m.get("MAKTX"));
                variables.put("LIFNR",m.get("LIFNR")==null?"":m.get("LIFNR"));
                variables.put("WERKS",m.get("WERKS")==null?"":m.get("WERKS"));
                variables.put("PO_NO",m.get("PO_NO")==null?"":m.get("PO_NO"));
                variables.put("PO_ITEM_NO",m.get("PO_ITEM_NO")==null?"":m.get("PO_ITEM_NO"));
                variables.put("BEDNR",m.get("BEDNR")==null?"":m.get("BEDNR"));
                variables.put("PRODUCT_DATE",m.get("PRODUCT_DATE")==null?"":m.get("PRODUCT_DATE"));
                variables.put("BATCH",m.get("BATCH")==null?"":m.get("BATCH"));
                variables.put("BOX_QTY",m.get("BOX_QTY")==null?"":m.get("BOX_QTY"));
                variables.put("UNIT",m.get("UNIT")==null?"":m.get("UNIT"));

                //生成二维码
                try {
                    String picturePath = ""; //图片路径
                    StringBuffer sb = new StringBuffer();
                    sb.append("{LABEL_NO:"+m.get("LABEL_NO").toString());
                    sb.append(",MATNR:"+m.get("MATNR").toString());
                    sb.append(",BATCH:"+m.get("BATCH").toString()+",BOX_QTY:");
                    sb.append(m.get("BOX_QTY")==null?"0":m.get("BOX_QTY").toString());
                    sb.append(",UNIT:"+m.get("UNIT").toString()+"}");
                    //System.err.println(sb.toString());
                    picturePath = QRCodeUtil.encode(sb.toString(),m.get("LABEL_NO").toString(),"",baseDir,true);
                    picturePath = picturePath.replaceAll("\\\\", "//");
                    variables.put("barCode","file:"+picturePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                listVars.add(variables);
            });
        }

        PdfUtils.preview(configurer,"wms/print/labelTmp_Label.html",listVars,response);
    }


    /**
     * 查询
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return wmsKnLabelRecordRemote.list(params);
    }
    /**
     * 根据ID查询实体记录
     */
    @RequestMapping("/info")
    public R info(@RequestParam String id){
        return wmsKnLabelRecordRemote.info(Long.valueOf(id));
    }

    /**
     * 新增标签页面查询
     */
    @RequestMapping("/poList")
    public R poList(@RequestParam Map<String, Object> params){
        return wmsKnLabelRecordRemote.poList(params);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody Map map){
        return wmsKnLabelRecordRemote.update(map);
    }

    /**
     * 单条记录删除(软删)
     */
    @RequestMapping("/delById")
    public R delById(Long id){
        if(id == null){
            return R.error("参数错误");
        }
        Map map = new HashMap();
        map.put("ID",id);
        map.put("del","X");
        return wmsKnLabelRecordRemote.delById(map);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody Map map){
        return wmsKnLabelRecordRemote.save(map);
    }

    /**
     * 保存生成标签
     */
    @RequestMapping("/saveLabel")
    public R saveLabel(@RequestParam Map<String, Object> params){
        return wmsKnLabelRecordRemote.saveLabel(params);
    }


}