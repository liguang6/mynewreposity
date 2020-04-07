package com.byd.wms.business.modules.in.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.in.dao.WmsSTOReceiptPdaDao;
import com.byd.wms.business.modules.in.service.WmsSTOReceiptPdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class WmsSTOReceiptPdaServiceImpl implements WmsSTOReceiptPdaService {


    @Autowired
    private WmsSTOReceiptPdaDao stoReceiptPdaServiceDao;
    @Autowired
    private CommonDao commonDao;
    /**
     * 获取发货单包装箱信息
     * @param params
     * @return
     */
    @Override
    public R scan(Map<String, Object> params) {

        //校验是否重复扫描(再缓存表已存在或者标签表中状态为已收货的)
        int count = commonDao.getLabelCacheInfoCount(params);
        if(count >0){
            return R.error(500,"重复扫描:标签已存在");
        }
        //获取标签信息
        List<Map<String, Object>> deliveryPackingList = stoReceiptPdaServiceDao.getDeliveryPacking(params);

        //存入暂存表
        if (deliveryPackingList !=null && deliveryPackingList.size()!= 0) {
            if(deliveryPackingList.size()> 1)
                return R.error(500, "标签重复打印！");

            Map<String, Object> deliveryPacking = deliveryPackingList.get(0);
            //判断标签状态
            String status = String.valueOf(deliveryPacking.get("LABEL_STATUS"));
            if("00".equals(status) || "10".equals(status) || "1".equals(status)){

                //标签层级:同一次收货，只能扫描同一个层级的标签做收货
                String typeNoO = deliveryPacking.get("TYPE_NO") == null ? "" : deliveryPacking.get("TYPE_NO").toString();
                String typeNoP = params.get("TYPE_NO") == null ? "" : params.get("TYPE_NO").toString();
                if(!typeNoO.isEmpty() && ! typeNoP.isEmpty() && !typeNoO.equals(typeNoP)){
                    return R.error(500, "标签层级不一致！");
                }

                List<Map<String, Object>> list = new ArrayList<>();
                deliveryPacking.putAll(params);
                list.add(deliveryPacking);
                int flag = commonDao.insertLabelCacheInfo(list);
                if(flag > 0){

                    return R.ok().put("data", deliveryPacking);
                }else{
                   return R.error(500, "保存信息失败，请联系管理员！");
                }
            }else{
                return R.error(500, "标签必须是创建或已出库状态！");
            }
        }else{
            return R.error(500, "未找到标签信息");
        }


    }

    @Override
    public R validateStorage(Map<String, Object> params) {

        List<Map<String,Object>> list=stoReceiptPdaServiceDao.validateStorage(params);

        if(list == null || list.size() == 0){
            return R.error(500, "未找到储位信息");
        }else{
            if((params.get("AREA_CODE") == null || params.get("AREA_CODE").equals("")) && list.size() >1) {
                return R.ok().put("AREA_CODE", "AAAA");
            }else{
                String areaCode = list.get(0).get("AREA_CODE").toString();
                return R.ok().put("AREA_CODE", areaCode);
            }
        }

    }

    @Override
    public int deleteLabelCacheInfo(List<String> list) {
        return commonDao.batchDeleteLabelCacheInfo(list);
    }

    @Override
    public PageUtils defaultSTOCache(Map<String, Object> params) {
        String pageNo=(params.get("page")!=null && !params.get("page").equals(""))?params.get("page").toString():"1";
        String pageSize=(params.get("rows")!=null && !params.get("rows").equals(""))?params.get("rows").toString():"-1";
        int start = 0;
        int end=0;
        int count = stoReceiptPdaServiceDao.getSTOCacheCount(params);
        if(params.get("rows")!=null && !params.get("rows").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }else {
            end=count;
        }
        params.put("start", start);
        params.put("end", end);
        List<Map<String,Object>> list=stoReceiptPdaServiceDao.defaultSTOCache(params);
        if(list != null && list.size() !=0){

            BigDecimal REC_QTY = BigDecimal.ZERO;
            for (Map<String,Object> item : list) {
                Long boxs = Long.parseLong(item.get("COUNT")== null || item.get("COUNT").equals("") ?
                "0" : item.get("COUNT").toString());
                REC_QTY = REC_QTY.add(BigDecimal.valueOf(boxs));
            }
            for (Map<String,Object> item : list) {
                item.put("REC_QTY",REC_QTY);
            }

        }

        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(list);
        page.setTotal(count);
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));

        return new PageUtils(page);
    }

    @Override
    public PageUtils defaultLabelCache(Map<String, Object> params) {
        String pageNo=(params.get("page")!=null && !params.get("page").equals(""))?params.get("page").toString():"1";
        String pageSize=(params.get("rows")!=null && !params.get("rows").equals(""))?params.get("rows").toString():"-1";
        int start = 0;
        int end=0;
        int count = commonDao.getLabelCacheInfoCount(params);
        if(params.get("rows")!=null && !params.get("rows").equals("")) {
            start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
            end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
        }else {
            end=count;
        }
        params.put("start", start);
        params.put("end", end);
        List<Map<String,Object>> list=commonDao.getLabelCacheInfo(params);
        Page page=new Query<Map<String,Object>>(params).getPage();
        page.setRecords(list);
        page.setTotal(count);
        page.setSize(Integer.valueOf(pageSize));
        page.setCurrent(Integer.valueOf(pageNo));

        return new PageUtils(page);
    }

    @Override
    public int deleteAllLabelCache(List<Map<String, Object>> list) {

        return commonDao.deleteLabelCacheInfo(list);
    }

    @Override
    public void calcOpsTime(Map<String, Object> params) {

        LocalDateTime curDate = LocalDateTime .now();
        Map<String, Object> map = stoReceiptPdaServiceDao.getfirstOpsTime(params);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(map.get("CREATE_DATE").toString(), fmt);
        Duration duration = Duration.between(startTime, curDate);
        long seconds = duration.getSeconds();
        long hour = seconds / 3600;
        long minute = (seconds - 3600 * hour) / 60;
        long second = (seconds - 3600 * hour) - minute * 60;
        String durationStr = hour + "小时"+minute+"分"+second+"秒";
        params.put("OPERATION_TIME",durationStr);
        stoReceiptPdaServiceDao.calcOpsTime(params);
    }

}
