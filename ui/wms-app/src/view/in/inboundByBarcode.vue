<!-- 入库模块 扫描进仓单入库 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'条码入库'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-show="modalPage=='index'">
      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col vertical-align="center" width="60px;" style="text-align:right">标签号：</v-ons-col>
        <v-ons-col width="80%">
          <input
            placeholder="请扫条码"
            style="width:90%"
            v-model="LABEL_NO"
            @keyup.enter="scanLabelNO"
            data-index="0"
          >
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col vertical-align="center" width="60px;" style="text-align:right">储位：</v-ons-col>
        <v-ons-col width="80%">
          <input style="width:90%" v-model="BIN_CODE" @keyup.enter="checkBinCode">
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card v-show="modalPage=='index'">
      <v-ons-list>
        <p v-if="itemList.length === 0">
          请扫描或输入条码后回车...
          <br>
        </p>
        <v-ons-row v-for="(li,$index) in itemList" :key="li.LABEL_NO">
          <v-ons-col width="10%" style="padding-top:6px">
            <i
              @click="deleteItem($index)"
              class="ion-android-close action-sheet-icon--material"
              style="color:red"
            ></i>
          </v-ons-col>
          <v-ons-col width="90%" style="padding-top:6px;line-height:25px;">
            <b>标签号:</b>
            <span style="color:blue">{{li.LABEL_NO}}</span>&nbsp;&nbsp;
            <b>料号:</b>
            {{li.MATNR}}&nbsp;&nbsp;
            <b>描述:</b>
            {{li.MAKTX}}&nbsp;&nbsp;
            <b>数量:</b>
            {{li.MAY_IN_QTY}}&nbsp;&nbsp;
            <b>箱序:</b>
            {{li.BOX_SN}}&nbsp;&nbsp;
            <b>储位:</b>
            {{li.BIN_CODE}}
          </v-ons-col>
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>

    <v-ons-card v-show="modalPage=='info'">
      <v-ons-list>
        <p v-if="inboundList.length === 0">
          未查询到进仓单数据...
          <br>
        </p>
        <v-ons-row v-for="(li) in inboundList" :key="li.INBOUND_NO+'-'+li.INBOUND_ITEM_NO+'-'+li.BIN_CODE">
          <v-ons-col width="90%" style="padding-top:6px" >
            <v-ons-list-item  >
                <v-ons-row>
                  <v-ons-col width="100%">
                    <b v-if="li.NO_TYPE='外购'">送货单/行号:</b>{{li.INBOUND_NO}}/{{li.INBOUND_ITEM_NO}}&nbsp;&nbsp;
                    <b>进仓数量:</b>{{li.MAY_IN_QTY}}&nbsp;&nbsp;
                    <b>箱数:</b>{{li.BOX_COUNT}}&nbsp;&nbsp;
                    <b>储位:</b>{{li.BIN_CODE}}
                  </v-ons-col>
                </v-ons-row>
                <!--<div class="expandable-content">
                  <div v-for="(i) in li.inboundList" :key="i.MATNR">
                    料号：{{i.MATNR}}&nbsp;&nbsp;物料描述：{{i.MAKTX}}&nbsp;&nbsp;进仓数量：{{i.MAY_IN_QTY}}<br/>
                    箱数：{{i.BOX_COUNT}}<br/>
                  </div>  
               
                </div>-->
              </v-ons-list-item>
          </v-ons-col>
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>

    <v-ons-card v-show="modalPage=='pzinfo'">
      <v-ons-list>
        <v-ons-list-item>{{pz_msg}}</v-ons-list-item>
      </v-ons-list>
    </v-ons-card>

    <v-ons-card v-show="modalPage=='confirm'">
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px">
          <center>凭证日期:</center>
        </v-ons-col>
        <v-ons-col width="75%">
          <center>
            <datepicker
              id="pz_date"
              name="凭证日期"
              placeholder="凭证日期"
              style="width:95%"
              v-model="pz_date"
              :format="format"
              :language="zh"
            ></datepicker>
          </center>
        </v-ons-col>
      </v-ons-row>
          
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px">
          <center>记帐日期:</center>
        </v-ons-col>
        <v-ons-col width="75%">
          <center>
            <datepicker
              id="jz_date"
              name="记帐日期"
              placeholder="记帐日期"
              style="width:95%"
              v-model="jz_date"
              :format="format"
              :language="zh"
            ></datepicker>
          </center>
        </v-ons-col>
      </v-ons-row>
      <!--<v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px">
          <center>抬头文本:</center>
        </v-ons-col>
        <v-ons-col width="75%">
          <center>
            <v-ons-input
              id="header_txt"
              name="抬头文本"
              v-model="header_txt"
              style="padding-top:8px;width:95%"
              modifier="underbar"
              placeholder="  抬头文本"
              float
              v-validate="'required'"
            ></v-ons-input>
          </center>
        </v-ons-col>
      </v-ons-row>-->
    </v-ons-card>

    <ons-bottom-toolbar>
      <center v-show="modalPage=='index'">
        <v-ons-button @click="reset" style="background-color:gray">
          <v-ons-icon icon="fa-reply"></v-ons-icon>重置
        </v-ons-button>&nbsp;&nbsp;
        <v-ons-button @click="modalPage='info'" style="margin: 6px 0">
          <v-ons-icon icon="fa-table"></v-ons-icon>数据表
        </v-ons-button>
      </center>
      <center v-show="modalPage=='info'">
        <v-ons-button @click="goBack" style="background-color:grey;">
          <v-ons-icon icon="fa-reply"></v-ons-icon>返回
        </v-ons-button>&nbsp;&nbsp;
        <v-ons-button @click="modalPage='confirm'" style="margin: 6px 0">
          <v-ons-icon icon="fa-cogs"></v-ons-icon>过账
        </v-ons-button>
      </center>
      <center v-show="modalPage=='pzinfo'">
        <v-ons-button @click="goBack" style="background-color:green;margin: 6px 0">
          <v-ons-icon icon="fa-check-square-o"></v-ons-icon>确认
        </v-ons-button>
      </center>
      <center v-show="modalPage=='confirm'">
        <v-ons-button @click="goBack" style="background-color:grey">
          <v-ons-icon icon="fa-reply"></v-ons-icon>返回
        </v-ons-button>&nbsp;&nbsp;
        <v-ons-button @click="confirm" style="margin: 6px 0">
          <v-ons-icon icon="fa-cogs"></v-ons-icon>确认过账
        </v-ons-button>
      </center>
    </ons-bottom-toolbar>
  </v-ons-page>
</template>

<script>
import customToolbar from "_c/toolbar";
import { mapActions, mapMutations, mapState } from "vuex";
import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";
import { zh } from "vuejs-datepicker/dist/locale";
import {
  getLabelDetailByBarcode,
  confirmInboundInfo,
  validateLableQty,
  checkBinCode
} from "@/api/in";
import { isNullOrUndefined } from 'util';

export default {
  computed: mapState({
    userWerks: state => sessionStorage.getItem("UserWerks"),
    userWhNumber: state => sessionStorage.getItem("UserWhNumber"),
    userName: state => sessionStorage.getItem("UserName")
  }),
  mounted() {
    this.setFocus("jump", 0);
    this.pz_date = this.tools.getFormatDate(new Date(), "YYYY-MM-DD");
    this.jz_date = this.tools.getFormatDate(new Date(), "YYYY-MM-DD");
  },
  data() {
    return {
      focusCtrl: 0, // 自动聚焦控制,变动时,执行自动聚焦指令
      currentIndex: 0, // 当前聚焦元素的索引
      actionType: "first", // 自动聚焦的行为类型
      BIN_CODE: "",
      LABEL_NO: "",
      itemList: [], //扫描条码后获取的数据列表
      label_list: [], //扫描的条码列表
      inboundList: [], //合并的进仓单行项目信息
      pz_date: "",
      jz_date: "",
      modalPage: "index",
      format: "yyyy-MM-dd",
      //header_txt: "",
      zh: zh,
      pz_msg: ""
    };
  },
  props: ["toggleMenu"],
  components: { customToolbar, Datepicker },
  watch: {
 
  },
  methods: {
    ...mapActions([]),
    ...mapMutations([]),
    checkBinCode(){
      // 判断输入的储位+仓库号是否有效
      let data={
          WH_NUMBER : this.userWhNumber,
          BIN_CODE : this.BIN_CODE
        }
      checkBinCode(data).then(res => {
        console.info(JSON.stringify(res))
        if(res.data.result == undefined || res.data.result.length == 0){
          this.BIN_CODE=''
          this.$ons.notification.toast('储位无效，请确认后重新输入！', {timeout: 3000 })
         
       }
      })  
      
    },
    scanLabelNO() {
      return new Promise((resolve, reject) => {
        for (let i = 0; i < this.label_list.length; i++) {
          if (this.label_list[i] == this.LABEL_NO) {
            this.$ons.notification.toast("该标签已录入", { timeout: 3000 });
            //this.INBOUND_NO=''
            return false;
          }
        }
        let label_param = {
          LABEL_NO: this.LABEL_NO,
          WERKS: this.userWerks,
          WH_NUMBER: this.userWhNumber,
          ARRLIST: JSON.stringify(this.itemList)
        };

        let data = {
          USERNAME: this.userName,
          WERKS: this.userWerks,
          WH_NUMBER: this.userWhNumber,
          LABEL_NO: this.LABEL_NO
        };

        validateLableQty(label_param)
          .then(res => {
            if (res.data.code != "0") {
              this.$ons.notification.toast(res.data.msg, { timeout: 3000 });
              return false;
            } else {
              getLabelDetailByBarcode(data)
                .then(res => {
                  console.info(res);
                  if (res.data.code == "500") {
                    this.$ons.notification.toast(res.data.msg, {
                      timeout: 3000
                    });
                  } else {
                    if (res.data.result.length > 0) {
                      let label = res.data.result[0];
                      if (this.BIN_CODE != "") {
                        label.BIN_CODE = this.BIN_CODE;
                      }
                      //判断是否单据类型是否一致，外购收货单或者自制进仓单
                      
                      if(!isNullOrUndefined(label.INBOUND_NO)){
                        label.NO_TYPE='自制'
                      }
                      if(!isNullOrUndefined(label.RECEIPT_NO)){
                        label.NO_TYPE='外购'
                      }
                      if(this.inboundList.length>0&&this.inboundList[0].NO_TYPE!=label.NO_TYPE){
                        this.$ons.notification.toast('标签单据类型不一致！', {
                          timeout: 3000
                        });
                        return false;
                      }


                      //判断收货单行项目数据列表中储位是否该标签对应的储位是否一致，不一致不允许录入
                      //按照进仓单、行项目合并数据
                      let INBOUND_NO=label.INBOUND_NO||label.RECEIPT_NO
                      let INBOUND_ITEM_NO=label.INBOUND_ITEM_NO||label.RECEIPT_ITEM_NO
                      let isSameBinCode = true;
                      let isExist = false; //是否找到相同的进仓单、行项目、储位
                      let bincode_check_msg = "";
                      this.inboundList.forEach(function(v, i) {
                        
                        if (
                          v.INBOUND_NO == INBOUND_NO &&
                          v.INBOUND_ITEM_NO == INBOUND_ITEM_NO &&
                          v.BIN_CODE != label.BIN_CODE
                        ) {
                          bincode_check_msg =
                            "该标签储位" +
                            label.BIN_CODE +
                            "与所属进仓单行项目对应的储位" +
                            v.BIN_CODE +
                            "不一致！";
                          isSameBinCode = false;
                          return false;
                        }

                        if (
                          v.INBOUND_NO == INBOUND_NO &&
                          v.INBOUND_ITEM_NO == INBOUND_ITEM_NO
                          &&v.BIN_CODE == label.BIN_CODE
                        ) {
                          v.MAY_IN_QTY += label.MAY_IN_QTY;
                          v.BOX_COUNT++;
                          isExist = true;
                          return false;
                        }
                      });
                      if (!isSameBinCode) {
                        this.$ons.notification.toast(bincode_check_msg, {
                          timeout: 3000
                        });
                        return false;
                      }

                      if (!isExist) {
                        let inbound = {};
                        inbound.NO_TYPE=label.NO_TYPE
                        inbound.INBOUND_NO = label.INBOUND_NO||label.RECEIPT_NO;
                        inbound.INBOUND_ITEM_NO = label.INBOUND_ITEM_NO||label.RECEIPT_ITEM_NO;
                        inbound.MAY_IN_QTY = label.MAY_IN_QTY;
                        inbound.BOX_COUNT = 1;
                        inbound.MATNR = label.MATNR;
                        inbound.BIN_CODE = label.BIN_CODE;
                        this.inboundList.push(inbound);
                      }

                      this.itemList.push(label);
                      this.label_list.push(this.LABEL_NO);
                      //this.header_txt = res.data.result[0]["TXTRULE"];
                    } else {
                      this.$ons.notification.toast(res.data.retMsg.retMsg, {
                        timeout: 3000
                      });
                    }
                  }

                  this.LABEL_NO = "";
                  this.setFocus("jump", 0);
                  resolve();
                })
                .catch(err => {
                  reject(err);
                });
            }
          })
          .catch(err => {
            is_valid = false;
            this.$ons.notification.toast(err, { timeout: 3000 });
          });
      });
    },
    reset() {
      this.LABEL_NO = "";
      this.msg = "请扫描或输入标签号后回车...";
      this.itemList = [];
      this.label_list = [];
      this.inboundList = [];
      //this.modalPage='index'
      this.pz_date = this.tools.getFormatDate(new Date(), "YYYY-MM-DD");
      this.jz_date = this.tools.getFormatDate(new Date(), "YYYY-MM-DD");
      //this.header_txt = "";
      this.BIN_CODE = "";
      this.pz_msg = "";
      this.setFocus('jump',0)
    },
    goBack() {
      if (this.modalPage == "info") {
        this.modalPage = "index";
      }
      if (this.modalPage == "confirm") {
        this.modalPage = "info";
      }
      if (this.modalPage == "pzinfo") {
        this.modalPage = "index";
      }
    },
    setFocus(actionType, index) {
      if (actionType === "jump") {
        this.currentIndex = index;
      }
      this.focusCtrl++;
      this.actionType = actionType;
    },
    //删除
    deleteItem(index) {
      let info = this.itemList[index];
      this.inboundList.forEach(function(v, i,arr) {
        if (
          v.INBOUND_NO == info.INBOUND_NO &&
          v.INBOUND_ITEM_NO == info.INBOUND_ITEM_NO
        ) {
          v.BOX_COUNT--;
          v.MAY_IN_QTY -= info.MAY_IN_QTY;
        }
        if(v.BOX_COUNT==0){
            arr.splice(i,1)
        }
      });
      
      this.itemList.splice(index, 1);
      this.label_list.splice(index, 1);
      if (this.itemList.length == 0) {
        this.reset();
      }
    },
    //校验实收数量不能超过单据数量减去已进仓数量!
    checkInboundQty(index) {
      let item = this.itemList[index];
      if (item.MAY_IN_QTY - (item.IN_QTY - item.REAL_QTY) > 0) {
        this.$ons.notification.toast(
          "进仓数量不能大于单据数量减去已进仓数量!",
          { timeout: 3000 }
        );
        this.itemList[index].MAY_IN_QTY =
          Number(this.itemList[index].IN_QTY) -
          Number(this.itemList[index].REAL_QTY);
      }
    },
    //过账
    confirm() {
      this.pz_msg = "";
      
      console.info(JSON.stringify(this.itemList));
      return new Promise((resolve, reject) => {
        let params = {
          WERKS: this.userWerks,
          WH_NUMBER: this.userWhNumber,
          PZDDT: this.tools.getFormatDate(new Date(this.pz_date), "YYYY-MM-DD"),
          JZDDT: this.tools.getFormatDate(new Date(this.jz_date), "YYYY-MM-DD"),
          ARRLIST: JSON.stringify(this.itemList),
          USERNAME: this.userName,
          PDA_FLAG: "PDA"
        };

        confirmInboundInfo(params)
          .then(res => {
            if (res.data.code == "500") {
              this.$ons.notification.alert(res.data.msg, { title: "" });
            } else {
              this.reset();
              this.pz_msg = res.data.msg;
              this.modalPage = "pzinfo";
            }
            resolve();
          })
          .catch(err => {
            this.$ons.notification.alert("系统异常,请联系管理员!", {
              title: ""
            });
            reject(err);
          });
      });
    }
  }
};
</script>  

<style>
.is-danger {
  border-style: dashed;
  border-width: 1px;
  border-color: crimson;
}

input,
select {
  padding: 0.75em 0.5em;
  border: none;
  font-size: 100%;
  border-bottom: 1px solid #ccc;
  width: 100%;
}
</style>
    
