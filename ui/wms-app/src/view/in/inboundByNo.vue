<!-- 入库模块 扫描进仓单入库 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'进仓单入库'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-show="modalPage=='index'"> 
      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col vertical-align="center" width="60px;" style="text-align:right">进仓单：</v-ons-col>
        <v-ons-col width="80%" >           
            <input :readonly="canInput?false:'readonly'" placeholder="请扫条码" style="width:90%" v-model="INBOUND_NO" @keyup.enter="scanInboundNO" data-index="0">                 
        </v-ons-col> 
      </v-ons-row> 
      <v-ons-row>
        <v-ons-col vertical-align="center" width="60px;" style="text-align:right">储位：</v-ons-col>
        <v-ons-col width="80%" >
            <input   style="width:90%" v-model="BIN_CODE" >                 
        </v-ons-col> 
      </v-ons-row>  
    </v-ons-card >

    <v-ons-card v-show="modalPage=='index'">
      <v-ons-list>
        <p v-if="itemList.length === 0">请扫描或输入进仓单号后回车...<br/></p>  
        <v-ons-row v-for="(li,$index) in itemList" :key="li.LABEL_NO">
          <v-ons-col width="10%" style="padding-top:6px"><i @click="deleteItem($index)" class="ion-android-close action-sheet-icon--material" style="color:red"></i></v-ons-col>
          <v-ons-col width="90%" style="padding-top:6px;line-height:25px;">
              <b>进仓单/行号:</b><span style="color:blue" @click="showLabelInfo(li.INBOUND_NO,li.INBOUND_ITEM_NO)"> {{li.INBOUND_NO}}/{{li.INBOUND_ITEM_NO}}</span><br>
              <b>料号:</b> {{li.MATNR}}&nbsp;&nbsp;
              <b>进仓数量:</b>  <input :disabled="BARCODE_FLAG=='0'" @change="checkInboundQty($index)" style="height:25px;width:30px;color:blue;padding:0 0" v-model="li.MAY_IN_QTY" ><br>
              <b>箱数:</b> {{li.BOX_COUNT}}&nbsp;&nbsp;<b>库位:</b> <input style="width:50px;color:blue;padding:0 0" v-model="li.LGORT" >&nbsp;&nbsp;
              <b>储位:</b> {{li.BIN_CODE}}
          </v-ons-col>         
        </v-ons-row>  
      </v-ons-list>  
    </v-ons-card> 

    <v-ons-card v-show="modalPage=='info'">
      <v-ons-list>
        <p v-if="labelList.length === 0">未查询到标签数据...<br/></p>  
        <v-ons-row v-for="(li) in labelList" :key="li.LABEL_NO">
          <v-ons-col width="90%" style="padding-top:6px">
              <b>条码号:</b> {{li.LABEL_NO}}&nbsp;&nbsp;<b>料号:</b> {{li.MATNR}}&nbsp;&nbsp;
              <b>箱序:</b> {{li.BOX_SN}}&nbsp;&nbsp;<b>数量:</b> {{li.BOX_QTY}}
          </v-ons-col>         
        </v-ons-row>  
      </v-ons-list>  
    </v-ons-card> 

    <v-ons-card v-show="modalPage=='check'">
      <v-ons-row >
        <v-ons-col width="10%" style="text-align:right"><ons-icon  style="color: green;padding-top:5px" size="25px" icon="fa-barcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%" >
          <input  placeholder="请扫条码" style="width:90%" v-model="LABEL_NO" @keyup.enter="checkLable" data-index="0">         
        </v-ons-col> 
      </v-ons-row>
       
    </v-ons-card> 
    <v-ons-card v-show="modalPage=='check'">
        <v-ons-list>
        <p >{{msg}}<br/></p>  
      </v-ons-list> 
    </v-ons-card>   

    <v-ons-card v-show="modalPage=='pzinfo'">
        <v-ons-list>
            <v-ons-list-item>
                {{pz_msg}}
            </v-ons-list-item>    
      </v-ons-list> 
    </v-ons-card>

    <v-ons-card v-show="modalPage=='confirm'">
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>凭证日期:</center></v-ons-col>
        <v-ons-col width="75%" ><center><datepicker id="pz_date" name="凭证日期" placeholder="凭证日期" style="width:95%" v-model="pz_date" :format="format" :language="zh" ></datepicker></center></v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>记帐日期:</center></v-ons-col>
        <v-ons-col width="75%" ><center><datepicker id="jz_date" name="记帐日期" placeholder="记帐日期" style="width:95%" v-model="jz_date" :format="format" :language="zh"></datepicker></center></v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>抬头文本:</center></v-ons-col>
        <v-ons-col width="75%" ><center><v-ons-input id="header_txt" name="抬头文本" v-model="header_txt" style="padding-top:8px;width:95%" modifier="underbar" placeholder="  抬头文本" float v-validate="'required'"></v-ons-input></center></v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center v-show="modalPage=='index'">
        <v-ons-button @click="reset" style="background-color:grey" ><v-ons-icon icon="fa-refresh"></v-ons-icon> 重置</v-ons-button>&nbsp;&nbsp;
        <v-ons-button @click="modalPage='check'" style="margin: 6px 0;background-color:#e06327"><v-ons-icon icon="fa-search"></v-ons-icon> 条码校验</v-ons-button> &nbsp;&nbsp;
        <v-ons-button @click="modalPage='confirm'" style="margin: 6px 0"><v-ons-icon icon="fa-table"></v-ons-icon> 过账</v-ons-button>   
      </center>
      <center v-show="modalPage=='info'">
        <v-ons-button @click="goBack" style="background-color:grey;margin: 6px 0" ><v-ons-icon icon="fa-reply"></v-ons-icon> 返回</v-ons-button>
      </center>
      <center v-show="modalPage=='check'">
        <v-ons-button @click="goBack" style="background-color:grey;margin: 6px 0" ><v-ons-icon icon="fa-reply"></v-ons-icon> 返回</v-ons-button>
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
  import {zh} from 'vuejs-datepicker/dist/locale'
  import {
    getInboundInfoByBarcode,getLabelInfoInbound,confirmInboundInfo
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
        BIN_CODE: '',
        INBOUND_NO:'',
        LABEL_NO:'',
        BARCODE_FLAG:'',
        itemList:[],
        inbound_no_list:[],
        labelList:[],
        pz_date:'',
        jz_date:'',
        dataList:[],
        msg:'请扫描或输入标签号后回车...',
        modalPage:'index',
        format: "yyyy-MM-dd",
        header_txt:'',
        BUSINESS_CLASS :'',
        zh:zh,
        pz_msg:'',
        canInput:true
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar,Datepicker },
    methods: {
      ...mapActions([
        
      ]),
      ...mapMutations([
        
      ]),     
     scanInboundNO(){
         return new Promise((resolve, reject) => {
             for(let i=0;i<this.inbound_no_list.length;i++){
              if(this.inbound_no_list[i] == this.INBOUND_NO){
                this.$ons.notification.toast('该进仓单已录入', {timeout: 3000});
                //this.INBOUND_NO=''
                return false;
              }
            }
            let data ={
                'USERNAME':this.userName,
                'WERKS':this.userWerks,
                'WH_NUMBER' : this.userWhNumber,
                'INBOUND_NO':this.INBOUND_NO
            }
            getInboundInfoByBarcode(data).then(res=>{
              console.info(res);
              if(res.data.code=='500'){
                this.$ons.notification.toast(res.data.msg, {timeout: 3000});
              }else{
                if(res.data.result.length>0){
                    this.BARCODE_FLAG=res.data.BARCODE_FLAG
                    this.itemList=res.data.result
                    this.inbound_no_list.push(this.INBOUND_NO)
                    this.header_txt=res.data.result[0]['TXTRULE']
                }else{
                    this.$ons.notification.toast(res.data.retMsg.retMsg, {timeout: 3000});
                }
                
              }  
              
              this.canInput=false
              //this.INBOUND_NO=''
              this.setFocus('jump',0)
              resolve()
            }).catch(err=>{             
              reject(err)
            })

         })    
     },
      reset(){
        this.LABEL_NO = ''
        this.msg = '请扫描或输入标签号后回车...'
        this.itemList=[]
        this.lableList=[]
        this.dataList=[]
        this.inbound_no_list=[]
        //this.modalPage='index'
        this.pz_date=this.tools.getFormatDate(new Date(),'YYYY-MM-DD')
        this.jz_date=this.tools.getFormatDate(new Date(),'YYYY-MM-DD')
        this.header_txt=''
        this.INBOUND_NO=''
        this.BIN_CODE=''
        this.pz_msg=''
        this.canInput=true
        this.setFocus('jump',0)
      },     
      goBack(){
        if(this.modalPage=='info'){
          this.modalPage='index'
        }
        if(this.modalPage=='check'){
          this.modalPage='index'
        }
        if(this.modalPage=='confirm'){
          this.modalPage='index'
        }
        if(this.modalPage=='pzinfo'){
          this.modalPage='index'
        }
      },
      setFocus(actionType,index) {
        if (actionType === 'jump') {
          this.currentIndex = index
        }
        this.focusCtrl++
        this.actionType = actionType
      },
      //删除
      deleteItem(index){
        let info=this.itemList[index]  
        this.itemList.splice(index,1) 
        if(this.itemList.length==0){
          this.reset()
        }
      },
      //校验实收数量不能超过单据数量减去已进仓数量!
      checkInboundQty(index){
          let item = this.itemList[index]
          if(item.MAY_IN_QTY-(item.IN_QTY-item.REAL_QTY)>0){
            this.$ons.notification.toast('进仓数量不能大于单据数量减去已进仓数量!', {timeout: 3000});
			this.itemList[index].MAY_IN_QTY=Number(this.itemList[index].IN_QTY)-Number(this.itemList[index].REAL_QTY)
		}
      },
      //过账
      confirm(){             
        //this.pz_msg='WMS凭证: A2343SCCW222 SAP凭证: 2321SDS3'
        let BIN_CODE = this.BIN_CODE
        let HEADER_TXT = this.header_txt
        this.itemList.forEach(function(v,i){
            v.BIN_CODE=BIN_CODE==''?v.BIN_CODE:BIN_CODE
            v.TXTRULE=HEADER_TXT
        })
        console.info(JSON.stringify(this.itemList))
        return new Promise((resolve,reject)=>{
          let params={
            WERKS:this.userWerks,
            WH_NUMBER:this.userWhNumber,
            PZDDT:this.tools.getFormatDate(new Date(this.pz_date),'YYYY-MM-DD'),
            JZDDT:this.tools.getFormatDate(new Date(this.jz_date),'YYYY-MM-DD'),
            ARRLIST:JSON.stringify(this.itemList),
            USERNAME:this.userName,
            PDA_FLAG:'PDA'
          }

          confirmInboundInfo(params).then(res =>{
            if(res.data.code == '500'){
              this.$ons.notification.alert(res.data.msg,{title:''});
            }else{             
              this.reset();
              this.pz_msg=res.data.msg
              this.modalPage='pzinfo'
            }
            resolve()
          }).catch(err =>{
            this.$ons.notification.alert('系统异常,请联系管理员!',{title:''});
            reject(err)
          }) 

        })

      },
      showLabelInfo(INBOUND_NO,INBOUND_ITEM_NO){
          this.modalPage='info'
          let data = {
              'INBOUND_NO':INBOUND_NO,
              'INBOUND_ITEM_NO':INBOUND_ITEM_NO
          }
          getLabelInfoInbound(data).then(res => {
              this.labelList=res.data.data             
          })

      },

      checkLable(){
          if(this.LABEL_NO.includes("{")){
            this.LABEL_NO = this.tools.resolveLabelBarcode(this.LABEL_NO).LABEL_NO
          }
          let data = {
              'INBOUND_NO':this.INBOUND_NO,
              'LABEL_NO':this.LABEL_NO
          }
          getLabelInfoInbound(data).then(res => {
              let list=res.data.data 
              if(list.length>0){
                 this.msg='扫描的条码包含在此数据内'        
              }else{
                 this.msg='扫描的条码不在此数据内' 
              }    
              this.LABEL_NO=''       
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
    
