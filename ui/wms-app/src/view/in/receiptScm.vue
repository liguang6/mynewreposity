<!-- 入库模块 收料房收货 SCM送货单收料组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'SCM送货单收料'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="45%" ><center><v-ons-input id="assno" autocomplete="off" @keypress="ScanAssno" autofocus=“true” name="SCM送货单" :class="{'input': true, 'is-danger': errors.has('SCM送货单') }" v-model="assno" width="95%" modifier="underbar" placeholder="SCM送货单" float v-validate="'required'"></v-ons-input></center></v-ons-col>
        <v-ons-col width="5%" ></v-ons-col>
        <v-ons-col width="50%" >
          <v-ons-button @click="search" style="width:60px;padding: 0 3px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>&nbsp;
          <v-ons-button @click="reset" style="width:60px;padding: 0 3px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.list.length === 0">请输入SCM送货单查询...<br/>{{msg}}</p>
      <v-ons-list>

        <v-ons-row v-for="(li, $index) in pageData.list" :key="li.ASNNO + '-' + li.ASNITM">
          <v-ons-col width="6%" style="padding-top:20px">
            <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="checkDataList" >
            </v-ons-checkbox>
          </v-ons-col>
          <v-ons-col width="94%" >
              <v-ons-list-item expandable >
                <v-ons-row>
                  <v-ons-col width="50%" style="padding-top:6px" >{{li.MATNR}}</v-ons-col>
                  <v-ons-col width="35%" style="padding-top:6px" >收货数量:{{li.QTY}}/</v-ons-col>
                  <v-ons-col width="15%" ><ons-input id="re_date" readonly="true" :value="li.RECEIPT_QTY" modifier="underbar" placeholder="数量" float></ons-input></v-ons-col>
                </v-ons-row>
                <div class="expandable-content">工厂/仓库号：{{li.WERKS}}/{{li.WH_NUMBER}}<br/>物料描述：{{li.MAKTX}}<br/>库位：{{li.LGORT}}<br/>
                PO：{{li.PO_NO}}<br/>单位：{{li.UNIT}}<br/>箱数：{{li.BOX_COUNT}}<br/><v-ons-button @click="matCheck($index)" style="width:125px;padding: 0 5px;background-color:green" modifier="material"><v-ons-icon icon="fa-barcode"></v-ons-icon>&nbsp;物料点收</v-ons-button></div>
              </v-ons-list-item>
          </v-ons-col>
        </v-ons-row>

      </v-ons-list>

    </v-ons-card>

    <ons-bottom-toolbar>
          
      <center>
        <v-ons-checkbox @click="checkall" v-model="ischeck" style="padding-top:14px">
        </v-ons-checkbox>&nbsp;全选&nbsp;&nbsp;
        <v-ons-button @click="barcodeCheck" style="margin: 2px 0"><v-ons-icon icon="fa-qrcode"></v-ons-icon>&nbsp;条码校验</v-ons-button> &nbsp;&nbsp;
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
      st_assno: (state) => state.wms_in.assno,
      st_pageData: (state) => state.wms_in.pageData,
      st_checkDataList: (state) => state.wms_in.checkDataList,
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
    }),
    mounted () {
      this.setPage('in_receipt_scm')
      this.assno = this.st_assno
      if(undefined === this.st_pageData.skInfoList){
        this.pageData = {
          list: [],
          skInfoList: [],
          BUSINESS_NAME:'01'
        }
      }else{
        this.pageData = this.st_pageData
      }
      //this.list = this.st_pageDataList
      this.checkDataList = this.st_checkDataList
      console.log('-->mounted page: ' + this.page + ";st_assno:" + this.st_assno)
    },
    data(){
      return{
        checkDataList :[],
        assno :'',
        pageData:{
          list: [],
          skInfoList: [],
          BUSINESS_NAME:'01'
        },
        msg: '',
        isExpanded :false,
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
        'listScmMat','listSKInfo'
      ]),
      ...mapMutations([
        'setPage','setAssno','setPageData','setCheckDataList','setMetCheckId'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      search(){
        this.setAssno(this.assno)
        console.log('-->search page: ' + this.page + ";assno:" + this.assno)
        console.log('-->st_userWerks = ' + this.st_userWerks + "|" + sessionStorage.getItem('UserName') + "|" + this.st_whNumber)
        this.$validator.localize('zh_CN');
        this.$validator.validateAll().then(result => {
          if (!result) {
            this.$ons.notification.toast(((this.$validator.errors.has('SCM送货单'))?this.$validator.errors.first('SCM送货单'):""), {timeout: 2000});
            return false;
          }else{
            this.modalVisible = true;
            this.listScmMat({assno: this.assno}).then(res => {
              if(0 === res.data.code){
                if(this.st_userWerks === res.data.page.list[0].WERKS){
                  this.pageData.list = res.data.page.list
                  this.msg = ''
                }else{
                  this.msg = '送货单仓库号和登陆选择的仓库号不一致'
                }
              }else{
                this.pageData.list = [],
                this.msg = res.data.msg,
                this.$ons.notification.toast(res.data.msg, {timeout: 2000});
              }
              this.modalVisible = false;
            })
            
            console.log('---->listSKInfo');
            this.listSKInfo({assno: this.assno}).then(res => {
              if(0 === res.data.code){
                this.pageData.skInfoList = res.data.data
                this.msg = ''
              }
            })
          }
        })
        },
        confirm(){
          if(0===this.checkDataList.length){
            this.$ons.notification.toast('请选择需要过帐的行项目', {timeout: 2000});
          }else{
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

            this.setAssno(this.assno)
            this.setPageData(this.pageData)
            this.setCheckDataList(this.checkDataList)
            this.$emit('gotoPageEvent','in_confirm')
          }
        },
        reset(){
          this.assno = ''
          this.pageData.list = []
          this.pageData.skInfoList = []
          this.checkDataList = []
          this.setAssno(this.assno)
          this.setPageData(this.pageData)
          //this.setPageDataList(this.list)
          this.setCheckDataList(this.checkDataList)
          this.setPage('in_receipt_scm')
        },
        barcodeCheck(){
          this.setAssno(this.assno)
          this.setPageData(this.pageData)
          this.setCheckDataList(this.checkDataList)
          this.$emit('gotoPageEvent','in_barcodeCheck')
        },
        matCheck(id){
          console.log('-->matCheck : id = ' + id)
          this.setPageData(this.pageData)
          this.setMetCheckId(id)
          this.$emit('gotoPageEvent','in_matCheck')
        },
        ScanAssno(){
          if(13 === window.event.keyCode){
            this.search()
          }
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
<style>
.is-danger{
  border-style: dashed;
  border-width: 1px;
  border-color: crimson
}
</style>