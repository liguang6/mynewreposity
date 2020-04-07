<!-- 入库模块 收料房收货 305工厂间调拨(303SAP凭证收货) 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'305调拨(303SAP凭证收货)'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row >
        <v-ons-col width="20%" style="text-align:right;line-height: 40px;">凭证：&nbsp;</v-ons-col>
        <v-ons-col width="80%">
          <input autocomplete="off" v-model="pageData.SAP_MATDOC_NO" style="width:55%" @keypress="ScanBarcode" placeholder="请扫描凭证号">  
          &nbsp;<v-ons-button @click="search" style="width:60px;padding: 0 3px;" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;查询</v-ons-button>
        </v-ons-col>
      </v-ons-row>
      <v-ons-row >
        <v-ons-col width="20%" style="text-align:right;line-height: 40px;">库位：&nbsp;</v-ons-col>
        <v-ons-col width="80%">
          <input autocomplete="off" v-model="pageData.LGORT" style="width:55%" placeholder="请输入库位">
          &nbsp;<v-ons-button @click="reset" style="width:60px;padding: 0 3px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.list.length === 0">请输入或扫描条码查询...<br/>{{msg}}</p>

      <v-ons-list>         
          <v-ons-row v-for="(li, $index) in pageData.list" :key="li.id">
            <v-ons-col width="6%" style="padding-top:15px">
              <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="checkDataList" >
              </v-ons-checkbox>
            </v-ons-col>
            <v-ons-col width="94%" >
                <v-ons-list-item expandable >
                  <v-ons-row>
                    <v-ons-col width="60%" style="padding-top:6px" >料号:{{li.MATNR}}</v-ons-col>
                    <v-ons-col width="30%" style="padding-top:6px" >数量:{{li.FULL_BOX_QTY}}/</v-ons-col>
                    <v-ons-col width="10%" ><ons-input id="re_date" readonly="true" :value="li.RECEIPT_QTY" modifier="underbar" placeholder="数量" float></ons-input></v-ons-col>
                  </v-ons-row>
                  <div class="expandable-content">工厂/仓库号：{{li.WERKS}}/{{li.WH_NUMBER}}<br/>物料描述：{{li.MAKTX}}<br/>库位：{{li.LGORT}}<br/>
                  单位：{{li.UNIT}}<br/>箱数：{{li.BOX_COUNT}}<br/><v-ons-button @click="matCheck($index)" style="width:125px;padding: 0 5px;background-color:green" modifier="material"><v-ons-icon icon="fa-barcode"></v-ons-icon>&nbsp;物料点收</v-ons-button></div>
                </v-ons-list-item>
            </v-ons-col>
        </v-ons-row>
      </v-ons-list>

    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-checkbox @click="checkall" v-model="ischeck" style="padding-top:14px">
        </v-ons-checkbox>&nbsp;全选&nbsp;&nbsp;
        <v-ons-button @click="barcodeCheck" style="margin: 6px 0"><v-ons-icon icon="fa-qrcode"></v-ons-icon>&nbsp;条码校验</v-ons-button> &nbsp;&nbsp;
        <v-ons-button @click="confirm" style="margin: 2px 0"><v-ons-icon icon="fa-cogs"></v-ons-icon>&nbsp;确认过帐</v-ons-button>
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
      st_pageData: (state) => state.wms_in.pageData,
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
    }),
    mounted () {
      this.setPage('in_receipt_305Sap')
      if(undefined === this.st_pageData.list){
        this.pageData = { 
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          SAP_MATDOC_NO: '',
          LGORT: '',
          BUSINESS_NAME:'06'
        }
      }else{
        this.pageData = this.st_pageData
      }
    },
    data(){
      return{
        checkDataList :[],
        PZ_YEAR: '2019',
        pageData:{
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          SAP_MATDOC_NO: '',
          LGORT: '',
          BUSINESS_NAME:'06'
        },
        msg: '',
        modalVisible: false,
        ischeck: false
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
        'list303Mat',
      ]),
      ...mapMutations([
        'setPage','setPageData','setCheckDataList'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      confirm(){
        if(0===this.checkDataList.length){
          this.$ons.notification.toast('请选择需要过帐的行项目', {timeout: 2000});
        }else{
          this.setPageData(this.pageData)
          this.setCheckDataList(this.checkDataList)
          this.$emit('gotoPageEvent','in_confirm')
        }
      },
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          this.search()
        }
      },
      search(){
        this.modalVisible = true;
        this.list303Mat({BUSINESS_NAME: this.pageData.BUSINESS_NAME,
        SAP_MATDOC_NO: this.pageData.SAP_MATDOC_NO,
        WH_NUMBER: this.st_whNumber,
        WERKS:this.st_userWerks,
        PZ_YEAR: this.PZ_YEAR}).then(res => {
          if(0 === res.data.code){
            this.pageData.list = []
            if('' != this.pageData.LGORT){
              for(var i = 0;i<res.data.page.list.length;i++){
                if(res.data.page.list[i].LGORT === this.pageData.LGORT){
                  this.pageData.list.push(res.data.page.list[i])
                }
              }
            }else{
              this.pageData.list = res.data.page.list
            }
            this.msg = (this.pageData.list.length === 0)?"当前条件没有查到任何数据！":""
          }else{
            this.pageData.list = [],
            this.msg = res.data.msg,
            this.$ons.notification.toast(res.data.msg, {timeout: 2000});
          }
          this.modalVisible = false;
        })
      },
      barcodeCheck(){
        this.setPageData(this.pageData)
        this.setCheckDataList(this.checkDataList)
        this.$emit('gotoPageEvent','in_barcodeCheck')
      },
      reset(){
        this.pageData = {
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          SAP_MATDOC_NO: '',
          LGORT: '',
          BUSINESS_NAME:'06'
        }
        this.setPageData(this.pageData)
      },
      checkall(){
        if(this.ischeck){
          this.ischeck = false
        }else{
          this.ischeck = true
        }
        let temp = []
        for(var i = 0;i<this.pageData.list.length;i++){
          temp[i] = i + ''
        }
        this.checkDataList = (this.ischeck)?temp:[]
      }
    }
  }
</script>