package com.byd.bjmes.modules.product.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.byd.bjmes.modules.common.remote.MasterInfoRemote;
import com.byd.bjmes.modules.product.dao.ProductManageDao;
import com.byd.bjmes.modules.product.service.ProductManageService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.baomidou.mybatisplus.plugins.Page;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("productManageService")
public class ProductManageServiceImpl implements ProductManageService{
  private Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private ProductManageDao productManageDao;
  @Autowired
  private MasterInfoRemote masterInfoRemote;

  @Override
  public List<Map<String,Object>> getProductsForNoManage(Map<String, Object> params){
    return productManageDao.getProductsForNoManage(params);
  }

  @Override
  public int generateProductsNo(Map<String, Object> params) {
    logger.info("-->productManageService :: generateProductsNo");
    String werks = params.get("WERKS").toString();
    //获取工厂对应的ORDERNUM
    int orderNum = masterInfoRemote.queryMasterDictWerksOrderNum(params);
    logger.info("-->werks : " + werks + "|orderNum : " + orderNum);
    //获取要生成产品编号的订单数据列表
    List<Map<String,Object>> proList = productManageDao.getProductsForGenerateNo(params);
    int count = 0;
    for(int i=0;i<proList.size();i++){
      logger.info(i + "|" + proList.get(i).get("order_no").toString() + "|" + proList.get(i).get("product_code").toString());
      //获取此产品类型下已生产的编号数
      Map<String,Object> parMap = new HashMap<String,Object>();
      parMap.put("YEAR", DateUtils.format(new Date(),"yyyy"));
      parMap.put("ORDER_NO",  proList.get(i).get("order_no").toString());
      parMap.put("ORDER_NUM", orderNum);
      parMap.put("PRODUCT_CODE", proList.get(i).get("product_code").toString());
      int pro_no_count = productManageDao.getProductsNoCount(parMap);   //已生成编号数量
      int pro_no_max = productManageDao.getProductsNoMax(parMap);
      int all_no_count = Integer.valueOf(proList.get(i).get("order_qty").toString()) * Integer.valueOf(proList.get(i).get("single_qty").toString());
      logger.info("-->已生成编号数量 : " + pro_no_count + "|共需产生数量 : " + all_no_count + "|当前流水号：" + pro_no_max);

      int todoNum = all_no_count - pro_no_count;
      count += todoNum;
      List<Map<String,Object>> noList = new ArrayList<Map<String,Object>>();
      for(int j=0;j<todoNum;j++){
        pro_no_max++;
        Map<String,Object> itemMap = new HashMap<String,Object>();
        //CJ0-2019-12345
        String product_no = proList.get(i).get("product_code").toString() + orderNum + "-" + DateUtils.format(new Date(),"yyyy") + "-" + this.getNoNumStr(pro_no_max,5);
        itemMap.put("product_no", product_no);
        itemMap.put("werks", proList.get(i).get("werks").toString());
        itemMap.put("werks_name", proList.get(i).get("werks_name").toString());
        itemMap.put("workshop", proList.get(i).get("workshop").toString());
        itemMap.put("workshop_name", proList.get(i).get("workshop_name").toString());
        itemMap.put("product_code", proList.get(i).get("product_code").toString());
        itemMap.put("order_no", proList.get(i).get("order_no").toString());
        noList.add(itemMap);
      }
      int pointsDataLimit = 200;      //限制条数
      if(pointsDataLimit<todoNum){  	//判断是否有必要分批
        int part = todoNum/pointsDataLimit;//分批数
        logger.info("共有 ： "+todoNum+"条，！"+" 分为 ："+part+"批  generateProductsNo");
        for (int j = 0; j < part; j++) {
          List<Map<String,Object>> listPage = noList.subList(0, pointsDataLimit);
          productManageDao.insertProductsNo(listPage);
          noList.subList(0, pointsDataLimit).clear();
        }
        if(!noList.isEmpty()){//表示最后剩下的数据
          productManageDao.insertProductsNo(noList);
        }
      }else {
        if(noList.size() > 0){
          productManageDao.insertProductsNo(noList);
        }else{
          return -1;
        }
       
      }
    }
    return count;
  }

  @Override
	public PageUtils getOrderList(Map<String, Object> params) {
		Page<Map<String, Object>> roderPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		roderPage.setRecords(productManageDao.getOrderList(params));
		roderPage.setSize(pageSize);
		roderPage.setTotal(productManageDao.getOrderListTotalCount(params));
		PageUtils page = new PageUtils(roderPage);		
		return page;
  }
  
  @Override
  public List<Map<String, Object>> getExceptionList(Map<String, Object> params){
    return productManageDao.getExceptionList(params);
  }

  @Override
  public List<Map<String, Object>> getProductNoinfo(Map<String, Object> params){
    return productManageDao.getProductNoinfo(params);
  }

  @Override
  public int insertProductionException(Map<String, Object> params){
    return productManageDao.insertProductionException(params);
  }

  @Override
  public int editProductionException(Map<String, Object> params){
    JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){
      JSONObject outData=  jarr.getJSONObject(i);
      Map<String, Object> itemMap = new HashMap <String, Object>();
      itemMap.put("id",outData.getString("id"));
      itemMap.put("process_name",outData.getString("process_name"));
      itemMap.put("exception_type_name",outData.getString("exception_type_name"));
      itemMap.put("reason_type_name",outData.getString("reason_type_name"));
      itemMap.put("severity_level",outData.getString("severity_level"));
      itemMap.put("start_time",outData.getString("start_time"));
      itemMap.put("duty_department_name",outData.getString("duty_department_name"));
      itemMap.put("editor",params.get("editor").toString());
      itemMap.put("edit_date",params.get("edit_date").toString());
      productManageDao.editProductionException(itemMap);
    }
    return 0;
  }

  /**
   * 获取位数不足补零的产品编号流水号
   * @param num 流水号
   * @param len 流水位长度
   * @return String 补零的产品编号流水号
   */
  public String getNoNumStr(int num,int len){
    String numStr = "";
    for(int i=0;i<len-String.valueOf(num).length();i++){
      numStr += "0";
    }
    numStr += String.valueOf(num);
    return numStr;
  }

  
}