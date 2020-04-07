<!-- 入库模块 数据表 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'数据表'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <p v-if="pageData.list.length === 0">当前没有任何数据</p>
      <v-ons-list>
        
        <v-ons-row v-for="(li, $index) in pageData.list" :key="li.id">
        <v-ons-col width="6%" style="padding-top:15px">
          <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="checkDataList" >
          </v-ons-checkbox>
        </v-ons-col>
        <v-ons-col width="94%" >
            <v-ons-list-item expandable >
              <v-ons-row>
                <v-ons-col width="10%" style="padding-top:6px" >{{$index+1}}</v-ons-col>
                <v-ons-col width="50%" style="padding-top:6px" >{{li.MATNR}}</v-ons-col>
                <v-ons-col width="30%" style="padding-top:6px" >数量:{{li.QTY}}/</v-ons-col>
                <v-ons-col width="10%" ><ons-input id="re_date" readonly="true" :value="li.RECEIPT_QTY" modifier="underbar" float></ons-input></v-ons-col>
              </v-ons-row>
              <div class="expandable-content">
                工厂/仓库号：{{li.WERKS}}/{{li.WH_NUMBER}}<br/>
                送货单：{{li.ASNNO}}/{{li.ASNITM}}<br/>
                物料号：{{li.MATNR}}<br/>
                物料描述：{{li.MAKTX}}<br/>
                PO：{{li.PO_NO}}/{{li.PO_ITEM_NO}}<br/>
                库位：{{li.LGORT}}<br/>
                单位：{{li.UNIT}}<br/>
                箱数：{{li.BOX_COUNT}}<br/>
                <v-ons-button v-if="page != 'in_receipt_cloudBarcode'" @click="matCheck($index)" style="width:125px;padding: 0 5px;background-color:green" modifier="material"><v-ons-icon icon="fa-barcode"></v-ons-icon>&nbsp;物料点收</v-ons-button>
                </div>
            </v-ons-list-item>
        </v-ons-col>
        </v-ons-row>

      </v-ons-list>
    </v-ons-card>
    
    <v-ons-dialog :visible.sync="hasResult">
      <p style="text-align: center">{{msg}}</p>
      <center>
        <v-ons-button @click="goback" style="margin: 2px 0"><v-ons-icon icon="fa-reply"></v-ons-icon>&nbsp;返回</v-ons-button>
      </center>
    </v-ons-dialog>

    <ons-bottom-toolbar>
      <center>
        <v-ons-checkbox @click="checkall" v-model="ischeck" style="padding-top:14px">
        </v-ons-checkbox>&nbsp;全选&nbsp;&nbsp;
        <!--<v-ons-button @click="del" style="margin: 2px 0"><v-ons-icon icon="fa-remove"></v-ons-icon>&nbsp;删除</v-ons-button> &nbsp;-->
        <v-ons-button @click="goback" style="margin: 2px 0"><v-ons-icon icon="fa-reply"></v-ons-icon>&nbsp;返回</v-ons-button> &nbsp;
        <v-ons-button @click="confirm" v-bind:disabled="isConfirm" style="margin: 2px 0"><v-ons-icon icon="fa-cogs"></v-ons-icon>&nbsp;{{confirmTxt}}</v-ons-button>
      </center>
    </ons-bottom-toolbar>

    <v-ons-modal :visible="modalVisible">
      <p style="text-align: center">
          <v-ons-icon icon="fa-spinner" size="2x" spin></v-ons-icon>
          <br><br>
          正在操作...
      </p>
    </v-ons-modal>

  </v-ons-page>
</template>

<script>
  import customToolbar from '_c/toolbar'
  import { mapActions, mapMutations,mapState } from 'vuex'
  export default {
    computed: mapState({
      page: (state) => state.wms_in.page,
      st_barCode: (state) => state.wms_in.barCode,
      st_pageData: (state) => state.wms_in.pageData,
    }),
    mounted () {
      switch(this.page){
        case 'in_carriage_createTask':
          this.confirmTxt = '创建'
          break
        default:
          this.confirmTxt = '过帐'
      }
      if(undefined === this.st_pageData.barCodeInfo){
        this.pageData = {
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'01'
        }
      }else{
        console.log("-->st_pageData length = "+this.st_pageData.barCodeInfo)
        this.pageData = this.st_pageData
      }
      this.barCode = this.st_barCode
    },
    data(){
      return{
        barCode :'',
        msg: '',
        pageData:{
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'01'
        },
        checkDataList:[],
        isConfirm :false,
        modalVisible: false,
        ischeck: false,
        hasResult:false,
        confirmTxt:'创建'
      }
    },
    watch:{
      checkDataList(curVal,oldVal){
        this.ischeck = (curVal.length === this.pageData.list.length&&this.pageData.list.length>0)?true:false
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        
      ]),
      ...mapMutations([
        'setPage','setPageData','setCheckDataList','setMetCheckId'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      checkall(){
        if(this.ischeck){
          this.ischeck = false
        }else{
          this.ischeck = true
        }
        var temp = []
        for(var i = 0;i<this.pageData.list.length;i++){
          temp[i] = i + ''
        }
        this.checkDataList = (this.ischeck)?temp:[]
      },
      confirm(){
        if(0===this.checkDataList.length){
            this.$ons.notification.toast('当前没有选择任何行项目！', {timeout: 2000});
          return false;
        }
        switch(this.page){
          case 'in_carriage_createTask':        //创建上架任务单
            this.isConfirm = true
            this.modalVisible = true
            var that = this;
            setTimeout(function (){
              that.modalVisible = false
              that.hasResult = true
              that.msg = '创建成功，上架单号：20190001234'
            },1000)
            break
          case 'in_receipt_scmBarcode':
            for(var i = 0;i<this.pageData.list.length;i++){
              for(var j = 0;j<this.pageData.skInfoList.length;j++){
                if(this.pageData.list[i].ASNITM === this.pageData.skInfoList[j].ASNITM){
                  this.pageData.list[i].PRODUCT_DATE=this.pageData.skInfoList[j].PRDDT;
                  this.pageData.list[i].F_BATCH=this.pageData.skInfoList[j].BATCH;
                  this.pageData.list[i].LIFNR=this.pageData.skInfoList[j].LIFNR;
                  this.pageData.list[i].FULL_BOX_QTY=this.pageData.skInfoList[j].FULL_BOX_QTY;
                }
              }
            }
            console.log('-->this.pageData.list : ' ,this.pageData.list)
            this.setPageData(this.pageData)
            this.setCheckDataList(this.checkDataList)
            this.$emit('gotoPageEvent','in_confirm')
            break
          default:
            this.setPageData(this.pageData)
            this.setCheckDataList(this.checkDataList)
            this.$emit('gotoPageEvent','in_confirm')
        }
        
      },
      matCheck(id){
        this.setPageData(this.pageData)
        this.setMetCheckId(id)
        this.$emit('gotoPageEvent','in_matCheck')
      },
      del(){

      },
      goback(){
        console.log('-->当前操作页面：' + this.page)
        switch(this.page){
          case 'in_receipt_scm':
            this.$emit('gotoPageEvent','in_receipt_scm');
            break
          case 'in_receipt_scmBarcode':
            this.$emit('gotoPageEvent','in_receipt_scmBarcode');
            break
          case 'in_receipt_305Barcode':
            this.$emit('gotoPageEvent','in_receipt_305Barcode');
            break
          case 'in_receipt_stoDelivery':
            this.$emit('gotoPageEvent','in_receipt_stoDelivery');
            break
          case 'in_receipt_305Barcode':
            this.$emit('gotoPageEvent','in_receipt_305Barcode');
            break
          case 'in_carriage_createTask':
            this.$emit('gotoPageEvent','in_carriage_createTask');
            break
          case 'in_receipt_cloudBarcode':
            this.$emit('gotoPageEvent','in_receipt_cloudBarcode');
            break
        }
      },
      
    }
  }
</script>