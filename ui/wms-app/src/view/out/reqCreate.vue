<template>
  <v-ons-page>
    <custom-toolbar :title="'扫描看板卡创建需求'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row class="in-row">
        <v-ons-col width="25%">工厂:</v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input type="text" modifier="material" v-model="werks"></v-ons-input>
        </v-ons-col>
        <v-ons-col width="20%"></v-ons-col>
      </v-ons-row>
       
      <v-ons-row class="in-row">
        <v-ons-col width="25%">线边仓:</v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input type="text" modifier="material" v-model="aceptLgortList"></v-ons-input>
        </v-ons-col>
        <v-ons-col width="20%"></v-ons-col>
      </v-ons-row>

      <v-ons-row class="in-row">
        <v-ons-col width="25%">超市:</v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input type="text" modifier="material"></v-ons-input>
        </v-ons-col>
        <v-ons-col width="20%"></v-ons-col>
      </v-ons-row>

      <v-ons-row class="in-row">
        <v-ons-col width="25%">
          <span class="red-star">*</span>条码:
        </v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input
            type="text"
            modifier="material"
            v-model="barcode"
            @keypress="ScanBarcode"
            placeholder="请扫条码"
          ></v-ons-input>
        </v-ons-col>
        <v-ons-col width="20%"></v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="this.list.length === 0">请扫条码...</p>
      <v-ons-list v-if="this.list.length > 0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">
            <v-ons-checkbox v-model="ischeck" @click="checkall"></v-ons-checkbox>
          </v-ons-col>        
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">料号</v-ons-col>
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">物料描述</v-ons-col>
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">单位</v-ons-col>
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">配送工位</v-ons-col>
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">配送地址</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in this.list" :key="mat.MATNR">
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">
            <v-ons-checkbox
              :input-id="'checkbox-' + mat.MATNR"
              :value="$index"
              v-model="checkDataList"
            ></v-ons-checkbox>
          </v-ons-col>
          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MAKTX}}</v-ons-col>

          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >
          <v-ons-input
            type="text"
            modifier="material"
            v-model="mat.REQ_QTY"  
          ></v-ons-input>
          
          
          </v-ons-col>

          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.UNIT}}</v-ons-col>

          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.DIS_STATION}}</v-ons-col>
          
          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.DIS_ADDRSS}}</v-ons-col>
        
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>
    <ons-bottom-toolbar>
      <center>
        <v-ons-button style="margin: 6px 0" @click="confirmCreate">
          <v-ons-icon icon="fa-check"></v-ons-icon>&nbsp;创建需求
        </v-ons-button>&nbsp;&nbsp;
      </center>
    </ons-bottom-toolbar>
        <v-ons-dialog :visible.sync="dialogVisible" cancelable>
            <div style="text-align: center">
                <p>需求创建成功,需求号:</p>
                  <v-ons-input placeholder="需求号" v-model="requirementNo"> </v-ons-input>
                  <v-ons-button @click="closeDialog">确认</v-ons-button>
            </div>
        </v-ons-dialog>
  </v-ons-page>
</template>
<script>
import customToolbar from "_c/toolbar";
import { mapActions, mapMutations,mapState } from 'vuex'
export default {
  props: ["toggleMenu"],
  components: { customToolbar },
  data() {
    return {
      ischeck: false,
      checkDataList: [],
      barcode: "",
      werks: "",
      aceptLgortList:'',
      list: [],
      checkData: [],
      requirementNo:'',
      dialogVisible:false
    };
  },
  updated : function(){
     this.$nextTick(function(){      
     })
 },
  watch: {
    checkDataList(curVal, oldVal) {
      this.ischeck = curVal.length === this.list.length && this.list.length > 0 ? true : false;
    },
    barcode(curVal, oldVal) {    
      var reg = /M:(.+);/
      var result = reg.exec(curVal)
      if(result)
       this.barcode = result[1]
       else{
         reg = /M:(.+)/
         result = reg.exec(curVal)
         if(result)
           this.barcode = result[1]
         else
           return
       }
    }
  },
  methods: {
     ...mapActions([
        'queryOutReqPda311','createOutReqPda311','queryUNIT'
      ]),
    ScanBarcode(e) {
      if (13 === window.event.keyCode) {
        if(!this.barcode){
          this.$ons.notification.toast('条码不能为空', {timeout: 2000});
        }else{
           this.queryOutReqPda311({barcode:this.barcode,werks:this.werks}).then(this.queryUNIT).then(res => {
              if(res.data.length > 0){  
                res.data[0].werks=sessionStorage.getItem('UserWerks')
                res.data[0].whNumber=sessionStorage.getItem('UserWhNumber')  
                res.data[0].aceptLgortList=this.aceptLgortList                         
                if(this.list.length>0){
                  let indexList = [];
                  this.list.forEach(ele => {
                   indexList.push(ele.MATNR);
                  }); 
                     let i = indexList.indexOf(this.barcode);
                     if (i == -1) {
                       if(res.data[0].DIS_ADDRSS===this.list[0].DIS_ADDRSS)
                        this.list.push(res.data[0]);
                        else
                        this.$ons.notification.toast('配送地址不一致', {timeout: 2000});
                     } else {
                          this.list[i].REQ_QTY = (Number)(res.data[0].REQ_QTY)+(Number)(this.list[i].REQ_QTY);
                      }
                }    
                if(0 === this.list.length){
                  this.list.push(res.data[0])
                }                          
              }else{ 
                this.$ons.notification.toast('物料号未维护', {timeout: 1000});
               }
        })
        }
      }
    },
    closeDialog(){                                                      
      this.requirementNo = ''
      this.dialogVisible = false
    },    
    checkall() {
      this.ischeck = !this.ischeck;
      let temp = [];
      for (var i = 0; i < this.list.length; i++) {
        temp[i] = i + "";
      }
      this.checkDataList = this.ischeck ? temp : [];
    },
    confirmCreate(){
      this.checkData=[]
      if(this.checkDataList.length>0&&this.list.length>0){
        this.checkDataList.forEach(ele=>{
        this.checkData.push(this.list[ele])
      })
      }    
      if(this.checkData.length>0){
        this.createOutReqPda311({list:this.checkData}).then(res=>{
          if(0 === res.data.code){
            this.list=[]
            this.checkDataList = []
            this.requirementNo=res.data.data
            this.dialogVisible = true
            //this.$ons.notification.toast('需求创建成功', {timeout: 2000});
          }
        }).catch(e=>{
            this.$ons.notification.toast('系统错误', {timeout: 2000});
        })  
      }  
      else{
        this.$ons.notification.toast('请选择数据', {timeout: 2000});
      }
    }
  },

  mounted() {
    
  }
};
</script>