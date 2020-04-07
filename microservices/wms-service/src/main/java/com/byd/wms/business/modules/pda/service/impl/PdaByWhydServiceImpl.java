package com.byd.wms.business.modules.pda.service.impl;

import com.byd.utils.StringUtils;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.pda.dao.PdaByWhydDao;
import com.byd.wms.business.modules.pda.service.PdaByWhydService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date $time$ $date$
 * @Description //TODO $end$
 **/

@Service
public class PdaByWhydServiceImpl implements PdaByWhydService {

    @Autowired
    private PdaByWhydDao pdaByWhydDao;

    @Autowired
    private WarehouseTasksService warehouseTasksService;

    @Override
    public List<Map<String, Object>> queryByBarcode(Map<String, Object> map) {
        return pdaByWhydDao.queryByBarcode(map);
    }

    @Override
    public List<Map<String,Object>> validateBinCode(Map<String, Object> map) {
        return pdaByWhydDao.validateBinCode(map);
    }

    @Override
    public List<Map<String, Object>> queryBin(HashMap hm) {
        return pdaByWhydDao.queryBin(hm);
    }

    @Override
    public List<Map<String, Object>> queryStockQty(HashMap hm) {
        return pdaByWhydDao.queryStockQty(hm);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean commitByHwyd(HashMap params) {
        System.err.println(params.toString());
        //更新库存条码表储位
        boolean flag = false;
        boolean b1 = pdaByWhydDao.updateBin(params);
        //System.err.println("b1 "+b1);
        //更新库存表储位
        boolean b2 = pdaByWhydDao.updateBinByStock(params);
        //System.err.println("b2 "+b2);
        //插入移储记录表
        List<Map<String,Object>> lm = new ArrayList<>();
        HashMap hmp = new HashMap();
        hmp.put("PROCESS_TYPE","02");
        hmp.put("WT_STATUS","02");
        hmp.put("PRIORITY_LEVEL","1");
        hmp.put("FROM_BIN_CODE",params.get("BIN_CODE"));
        hmp.put("TO_BIN_CODE",params.get("mbBin_Code"));
        hmp.put("LIFNR",params.get("LIFNR"));
        hmp.put("QUANTITY",params.get("STOCK_QTY"));
        hmp.putAll(params);
        lm.add(hmp);
        String WTNO = warehouseTasksService.saveWHTask(lm);
        boolean b4 = false;
        if(!StringUtils.isBlank(WTNO)){
            b4 = true;
        }
        if(b1 && b2 && b4){
            flag = true;
        }
        if(!flag){
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
        public boolean commitByHwyd2(HashMap hm) {
        System.err.println(hm.toString());
        //拆分库存表，更新stock_qty 插入一条记录
        boolean flag = false;
        boolean b1 = pdaByWhydDao.insertByStock(hm);
        //System.err.println("b1 "+b1);
        boolean b2 = pdaByWhydDao.updateByStock(hm);
        //System.err.println("b2 "+b2);
        //更新库存条码表储位
        boolean b3 = pdaByWhydDao.updateBin(hm);
        //System.err.println("b3 "+b3);
        //插入移储记录表
        List<Map<String,Object>> lm = new ArrayList<>();
        HashMap hmp = new HashMap();
        hmp.put("PROCESS_TYPE","02");
        hmp.put("WT_STATUS","02");
        hmp.put("PRIORITY_LEVEL","1");
        hmp.put("FROM_BIN_CODE",hm.get("BIN_CODE"));
        hmp.put("TO_BIN_CODE",hm.get("mbBin_Code"));
        hmp.put("LIFNR",hm.get("LIFNR"));
        hmp.put("QUANTITY",hm.get("STOCK_QTY"));
        hmp.putAll(hm);
        lm.add(hmp);
/*        hmp.put("MATNR","");
        hmp.put("MAKTX","");
        hmp.put("QUANTITY","");
        hmp.put("CONFIRM_QUANTITY","");
        hmp.put("UNIT","");
        hmp.put("SOBKZ","");
        hmp.put("BATCH","");
        hmp.put("LABEL_NO","");
        hmp.put("FROM_STORAGE_AREA","");
        hmp.put("FROM_BIN_CODE","");
        hmp.put("TO_STORAGE_AREA","");
        hmp.put("TO_BIN_CODE","");*/
        //
        String WTNO = warehouseTasksService.saveWHTask(lm);
        boolean b4 = false;
        if(!StringUtils.isBlank(WTNO)){
            b4 = true;
        }
        if(b1 && b2 && b3 && b4){
            flag = true;
        }
        if(!flag){
            return false;
        }
        return true;
    }





}
