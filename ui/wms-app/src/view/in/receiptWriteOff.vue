<!-- 入库模块 收货凭证冲销 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'收货凭证冲销'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-show="modalPage=='index'"> 
      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col width="10%" style="text-align:right"><ons-icon  style="color: green;padding-top:5px" size="25px" icon="fa-barcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%" >
          <input  placeholder="请扫条码" style="width:60%" v-model="barcode" @keyup.enter="ScanBarcode" data-index="0">  
          <label>合计：{{count}}箱</label>       
        </v-ons-col>
 
      </v-ons-row>  
    </v-ons-card >

    <v-ons-card v-show="modalPage=='index'">
      <v-ons-list>
        <p v-if="barcodeList.length === 0">请扫描或输入条码后回车...<br/>{{msg}}</p>
        <v-ons-row v-for="(li,$index) in barcodeList" :key="li.LABEL_NO">
          <v-ons-col width="10%" style="padding-top:6px"><i @click="deleteLable($index)" class="ion-android-close action-sheet-icon--material" style="color:red"></i></v-ons-col>
          <v-ons-col width="90%" style="padding-top:6px"><b>条码号:</b> {{li.LABEL_NO}}&nbsp;&nbsp;<b>料号:</b> {{li.MATNR}}&nbsp;&nbsp;<b>数量:</b> {{li.BOX_QTY}}</v-ons-col>
          
        </v-ons-row>  
      </v-ons-list>  
    </v-ons-card> 

    <v-ons-card v-show="modalPage=='pzinfo'">
      <v-ons-list>
        <v-ons-row v-for="(li) in dataList" :key="li.WMS_NO + '-' + li.WMS_ITEM_NO">
          <v-ons-col  >
              <v-ons-list-item  expandable>
                <v-ons-row>
                  <v-ons-col width="100%">
                    <b>凭证号/行号:</b>{{li.WMS_NO}}/{{li.WMS_ITEM_NO}}&nbsp;&nbsp;
                    <b>料号:</b>{{li.MATNR}}&nbsp;&nbsp;
                    <b>WMS移动类型:</b>{{li.WMS_MOVE_TYPE}}&nbsp;&nbsp;
                    <b>冲销数量:</b>{{li.QTY_WMS}}
                  </v-ons-col>
                </v-ons-row>
                <div class="expandable-content">工厂/仓库号：{{li.WERKS}}/{{li.WH_NUMBER}}<br/>物料描述：{{li.MAKTX}}<br/>库位：{{li.LGORT}}<br/>
                批次：{{li.BATCH}}<br/>单位：{{li.UNIT}}<br/>可冲销数量：{{li.QTY_WMS_MAX}}<br/>库存类型：{{li.SOBKZ}}<br/>
                </div>
              </v-ons-list-item>
          </v-ons-col>
        </v-ons-row>  
      </v-ons-list> 
    </v-ons-card> 

    <v-ons-card v-show="modalPage=='confirm'">
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>凭证日期:</center></v-ons-col>
        <v-ons-col width="75%" ><center><datepicker id="pz_date" name="凭证日期" placeholder="凭证日期" style="width:95%" v-model="pz_date" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>记帐日期:</center></v-ons-col>
        <v-ons-col width="75%" ><center><datepicker id="jz_date" name="记帐日期" placeholder="记帐日期" style="width:95%" v-model="jz_date" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>抬头文本:</center></v-ons-col>
        <v-ons-col width="75%" ><center><v-ons-input id="header_txt" name="抬头文本" v-model="header_txt" style="padding-top:8px;width:95%" modifier="underbar" placeholder="  抬头文本" float v-validate="'required'"></v-ons-input></center></v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center v-show="modalPage=='index'">
        <v-ons-button @click="reset" style="background-color:grey" ><v-ons-icon icon="fa-refresh"></v-ons-icon> 重置</v-ons-button>&nbsp;&nbsp;
        <v-ons-button @click="showDocTable" style="margin: 6px 0"><v-ons-icon icon="fa-table"></v-ons-icon> 凭证信息</v-ons-button>   
      </center>
      <center v-show="modalPage=='pzinfo'">
        <v-ons-button @click="goBack" style="background-color:grey" ><v-ons-icon icon="fa-reply"></v-ons-icon> 返回</v-ons-button>&nbsp;&nbsp;
        <v-ons-button @click="showWriteOff" style="margin: 6px 0" ><v-ons-icon icon="fa-cogs"></v-ons-icon> 确认冲销</v-ons-button>
      </center>
      <center v-show="modalPage=='confirm'">
        <v-ons-button @click="goBack" style="background-color:grey" ><v-ons-icon icon="fa-reply"></v-ons-icon> 返回</v-ons-button>&nbsp;&nbsp;
        <v-ons-button @click="confirm" style="margin: 6px 0" ><v-ons-icon icon="fa-cogs"></v-ons-icon> 过账</v-ons-button>
      </center>   
    </ons-bottom-toolbar>
  </v-ons-page>
</template>

<script>
  import customToolbar from '_c/toolbar'
  import { mapActions, mapMutations,mapState } from 'vuex'
  import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";
  import {
    getLabelInfoByBarcode,confirmWriteOffInfo
  } from '@/api/in'

  export default {
    computed: mapState({
      userWerks: (state) => sessionStorage.getItem('UserWerks'),
      userWhNumber: (state) => sessionStorage.getItem('UserWhNumber'),
      userName: (state) => sessionStorage.getItem('UserName'),
    }),
    mounted () {
      this.setFocus('jump',0)
      this.pz_date=this.tools.getFormatDate(new Date(),'YYYY-MM-DD');
      this.jz_date=this.tools.getFormatDate(new Date(),'YYYY-MM-DD');
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'first', // 自动聚焦的行为类型
        count: 0,              
        barcode: '',
        barcodeList:[],
        pz_date:'',
        jz_date:'',
        dataList:[],
        msg:'',
        modalPage:'index',
        format: "yyyy-MM-dd",
        header_txt:'',
        WMS_NO:'',
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar,Datepicker },
    methods: {
      ...mapActions([
        
      ]),
      ...mapMutations([
        
      ]),     
      ScanBarcode(e){
         return new Promise((resolve, reject) => {
            let data ={
              'LABEL_NO' : this.barcode,
              'WERKS' : this.userWerks,
              'WH_NUMBER' : this.userWhNumber,
              'ACTION_TYPE':'01'
            }
            for(let i=0;i<this.barcodeList.length;i++){
              if(this.barcodeList[i].LABEL_NO == this.barcode){
                this.$ons.notification.toast('该标签已录入', {timeout: 3000});
                this.barcode=''
                return false;
              }
            }
            getLabelInfoByBarcode(data).then(res=>{
              console.info(res);
              if(res.data.code=='500'){
                this.$ons.notification.toast(res.data.msg, {timeout: 3000});
              }else{
                let info=res.data.data[0]
                if(this.WMS_NO==''){
                  this.WMS_NO=info.WMS_NO;
                }     
                //头文本赋值
                this.header_txt=info.WMS_NO
                //凭证日期赋值
                this.pz_date=info.PZ_DATE

                //判断是否和前一个条码属于同一个凭证
                if(info.WMS_NO != this.WMS_NO){
                  this.$ons.notification.toast("扫入的标签不属于同一凭证 "+this.WMS_NO+"，请确认！", {timeout: 3000});
                  this.barcode=''
                  return false;
                }
                
                //按凭证、行项目合并冲销源数据
                let isExist=false;//是否找到相同的凭证、行项目
                let isExceed=false;//是否超出了凭证最大可冲销数量

                this.dataList.forEach(function(v,i){
                  if(v.WMS_NO==info.WMS_NO && v.WMS_ITEM_NO==info.WMS_ITEM_NO){
                    isExist=true;
                    if((Number)(v.QTY_WMS)+(Number)(info.BOX_QTY)>info.QTY_WMS_MAX){//判断是否超出了凭证最大可冲销数量
                      isExceed=true;
                      return false;
                    } 
                    v.QTY_WMS=(Number)(v.QTY_WMS)+(Number)(info.BOX_QTY)
                    console.info(v.QTY_WMS+"/"+info.QTY_WMS_MAX)     
                                      
                    return false;
                  }
                })
                
                if(isExceed){
                  this.$ons.notification.toast("冲销数量不能超出可冲销数量： "+info.QTY_WMS_MAX, {timeout: 3000});
                  this.barcode=''
                  return false;
                }

                let barcode={};
                barcode.LABEL_NO=this.barcode;
                barcode.MATNR=info.MATNR;
                barcode.BOX_QTY=info.BOX_QTY;
                barcode.WMS_NO=info.WMS_NO;
                barcode.WMS_ITEM_NO=info.WMS_ITEM_NO;
                this.barcodeList.push(barcode)

                if(!isExist){
                  info.QTY_WMS=info.BOX_QTY
                  this.dataList.push(info)
                }

                this.count++
              }  
              
              this.barcode=''
              this.setFocus('jump',0)
              resolve()
            }).catch(err=>{             
              reject(err)
            })
          })
      },
      reset(){
        this.barCode = ''
        this.msg = ''
        this.barcodeList=[]
        this.dataList=[]
        this.count=0
        this.modalPage='index'
        this.pz_date=this.tools.getFormatDate(new Date(),'YYYY-MM-DD')
        this.jz_date=this.tools.getFormatDate(new Date(),'YYYY-MM-DD')
        this.header_txt=''
      },     
      //展示凭证信息表格
      showDocTable() {
        this.modalPage = 'pzinfo'
        //console.log(this.modalPage)
      },
      goBack(){
        if(this.modalPage=='pzinfo'){
          this.modalPage='index'
        }
        if(this.modalPage=='confirm'){
          this.modalPage='pzinfo'
        }
      },
      showWriteOff(){
        this.modalPage='confirm'
      },
      setFocus(actionType,index) {
        if (actionType === 'jump') {
          this.currentIndex = index
        }
        this.focusCtrl++
        this.actionType = actionType
      },
      //删除
      deleteLable(index){
        let info=this.barcodeList[index]
        console.info(JSON.stringify(info))
        this.dataList.forEach(function(v,i){
          if(v.WMS_NO==info.WMS_NO && v.WMS_ITEM_NO==info.WMS_ITEM_NO){
            v.QTY_WMS=(Number)(v.QTY_WMS)-(Number)(info.BOX_QTY)            
          }
        })       
        this.barcodeList.splice(index,1) 
        this.count--
        if(this.barcodeList.length==0){
          this.reset()
        }
      },
      //过账
      confirm(){      
        console.info(JSON.stringify(this.dataList))

        return new Promise((resolve,reject)=>{
          let params={
            WERKS:this.userWerks,
            WH_NUMBER:this.userWhNumber,
            VO_NO:this.WMS_NO,
            PZ_DATE:this.tools.getFormatDate(new Date(this.pz_date),'YYYY-MM-DD'),
            JZ_DATE:this.tools.getFormatDate(new Date(this.jz_date),'YYYY-MM-DD'),
            HEADER_TXT:this.header_txt,
            BUSINESS_CLASS:'01',
            USERNAME:this.userName,
            ARRLIST:JSON.stringify(this.dataList)
          }

          confirmWriteOffInfo(params).then(res =>{
            if(res.data.code == '500'){
              this.$ons.notification.alert(res.data.msg,{title:''});
            }else{
              this.$ons.notification.toast(res.data.result, {timeout: 3000});
              this.reset();
            }
            resolve()
          }).catch(err =>{
            this.$ons.notification.alert('系统异常,请联系管理员!',{title:''});
            reject(err)
          })

        })

      }
      
    }
  }

</script>  

<style>
.is-danger{
  border-style: dashed;
  border-width: 1px;
  border-color: crimson
}

input,
select {
  padding: 0.75em 0.5em;
  border:none;
  font-size: 100%;
  border-bottom: 1px solid #ccc;
  width: 100%;
}
</style>