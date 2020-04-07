package com.byd.wms.webservice.ws.entity;

public class EwmLableEntity {

    private String WHMNO;       //仓库编号          Ewm根据仓库号转换为工厂代码
    private String TMIDN;       //条码ID -wms：LABELNO
    private String TMIDN_F;     //源条码ID
    private String MATNR;       //物料号           不带前置0
    private String MAKTX;       //物料描述         空值时，wms自动补充
    private String LIFNR;       //供应商           不带前置0
    private String LIFNR_TXT;   //供应商           空值时，wms自动补充
    private String PONUM;       //采购订单号、STO号码、UB转储单号
    private String POITM;       //订单行项目号      不带前置0
    private String TMQTY;       //数量
    private String MEINS;       //单位
    private String PDATE;       //生产日期          日期格式YYYYMMDD
    private String VALTO;       //有效期至          日期格式YYYYMMDD
    private String VBTCH;       //供应商批次
    private String BYDBTH;      //BYD批次
    private String ASNQTY;      //ASN数量           Asn数量默认=数量
    private String RMARK;       //备注
    private String STATS;       //S-成功，E-错误
    private String BSART;       //特殊库存类型	默认传：Z
    private String SKUWEI;      //库存地点默认传：00ZJ  （移动类型：STO，DB）

    private String YLZD1;       //预留字段1
    private String YLZD2;       //预留字段2
    private String YLZD3;       //预留字段3
    private String YLZD4;       //预留字段4
    private String YLZD5;       //预留字段5
    private String YLZD6;       //预留字段6
    private String YLZD7;       //预留字段7
    private String YLZD8;       //预留字段8
    private String YLZD9;       //预留字段9
    private String YLZD0;       //预留字段0

    public String getWHMNO() {
        return WHMNO;
    }

    public void setWHMNO(String WHMNO) {
        this.WHMNO = WHMNO;
    }

    public String getMATNR() {
        return MATNR;
    }

    public void setMATNR(String MATNR) {
        this.MATNR = MATNR;
    }

    public String getLIFNR() {
        return LIFNR;
    }

    public void setLIFNR(String LIFNR) {
        this.LIFNR = LIFNR;
    }

    public String getTMIDN() {
        return TMIDN;
    }

    public void setTMIDN(String TMIDN) {
        this.TMIDN = TMIDN;
    }

    public String getTMIDN_F() {
        return TMIDN_F;
    }

    public void setTMIDN_F(String TMIDN_F) {
        this.TMIDN_F = TMIDN_F;
    }

    public String getPONUM() {
        return PONUM;
    }

    public void setPONUM(String PONUM) {
        this.PONUM = PONUM;
    }

    public String getPOITM() {
        return POITM;
    }

    public void setPOITM(String POITM) {
        this.POITM = POITM;
    }


    public String getPDATE() {
        return PDATE;
    }

    public void setPDATE(String PDATE) {
        this.PDATE = PDATE;
    }

    public String getVALTO() {
        return VALTO;
    }

    public void setVALTO(String VALTO) {
        this.VALTO = VALTO;
    }

    public String getVBTCH() {
        return VBTCH;
    }

    public void setVBTCH(String VBTCH) {
        this.VBTCH = VBTCH;
    }

    public String getBYDBTH() {
        return BYDBTH;
    }

    public void setBYDBTH(String BYDBTH) {
        this.BYDBTH = BYDBTH;
    }

    public String getRMARK() {
        return RMARK;
    }

    public void setRMARK(String RMARK) {
        this.RMARK = RMARK;
    }

    public String getSTATS() {
        return STATS;
    }

    public void setSTATS(String STATS) {
        this.STATS = STATS;
    }

    public String getYLZD1() {
        return YLZD1;
    }

    public void setYLZD1(String YLZD1) {
        this.YLZD1 = YLZD1;
    }

    public String getYLZD2() {
        return YLZD2;
    }

    public void setYLZD2(String YLZD2) {
        this.YLZD2 = YLZD2;
    }

    public String getYLZD3() {
        return YLZD3;
    }

    public void setYLZD3(String YLZD3) {
        this.YLZD3 = YLZD3;
    }

    public String getYLZD4() {
        return YLZD4;
    }

    public void setYLZD4(String YLZD4) {
        this.YLZD4 = YLZD4;
    }

    public String getYLZD5() {
        return YLZD5;
    }

    public void setYLZD5(String YLZD5) {
        this.YLZD5 = YLZD5;
    }

    public String getYLZD6() {
        return YLZD6;
    }

    public void setYLZD6(String YLZD6) {
        this.YLZD6 = YLZD6;
    }

    public String getYLZD7() {
        return YLZD7;
    }

    public void setYLZD7(String YLZD7) {
        this.YLZD7 = YLZD7;
    }

    public String getYLZD8() {
        return YLZD8;
    }

    public void setYLZD8(String YLZD8) {
        this.YLZD8 = YLZD8;
    }

    public String getYLZD9() {
        return YLZD9;
    }

    public void setYLZD9(String YLZD9) {
        this.YLZD9 = YLZD9;
    }

    public String getYLZD0() {
        return YLZD0;
    }

    public void setYLZD0(String YLZD0) {
        this.YLZD0 = YLZD0;
    }

    public String getASNQTY() {
        return ASNQTY;
    }

    public void setASNQTY(String ASNQTY) {
        this.ASNQTY = ASNQTY;
    }

    public String getMAKTX() {
        return MAKTX;
    }

    public void setMAKTX(String MAKTX) {
        this.MAKTX = MAKTX;
    }

    public String getLIFNR_TXT() {
        return LIFNR_TXT;
    }

    public void setLIFNR_TXT(String LIFNR_TXT) {
        this.LIFNR_TXT = LIFNR_TXT;
    }

    public String getTMQTY() {
        return TMQTY;
    }

    public void setTMQTY(String TMQTY) {
        this.TMQTY = TMQTY;
    }

    public String getMEINS() {
        return MEINS;
    }

    public void setMEINS(String MEINS) {
        this.MEINS = MEINS;
    }

    public String getBSART() {
        return BSART;
    }

    public void setBSART(String BSART) {
        this.BSART = BSART;
    }

    public String getSKUWEI() {
        return SKUWEI;
    }

    public void setSKUWEI(String SKUWEI) {
        this.SKUWEI = SKUWEI;
    }
}