<!-- 入库模块 条码校验组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'条码校验'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col width="15%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-barcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="85%">
          <input @focus="setFocusIndex(0)" autocomplete="off" v-model="barcode" style="width:65%" data-index="0" @keypress="ScanBarcode" placeholder="请扫条码">
          &nbsp;<v-ons-button @click="reset" style="width:65px;padding: 0 5px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="msg===''">请扫条码后回车...<br/></p>
      <p v-html="msg"></p>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button @click="goback" style="margin: 6px 0"><v-ons-icon icon="fa-reply"></v-ons-icon>&nbsp;返回</v-ons-button> &nbsp;&nbsp;
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
    }),
    mounted () {
      this.modalVisible = true
      this.assno = this.st_assno
      if('in_receipt_scm' === this.page){
        this.getAllMatBarcodeList({ASS_NO:this.assno}).then(res => {
          if(0 === res.data.code){
            this.barcodeList = res.data.data
          }else{
            this.msg = res.data.msg
          }
          this.modalVisible = false
        })
      }if('in_receipt_305Sap' === this.page){
        this.getAllMatBarcodeListBySapMatDocNo({SAP_MATDOC_NO:this.st_pageData.SAP_MATDOC_NO,WH_NUMBER:this.st_pageData.WH_NUMBER}).then(res => {
          if(0 === res.data.code){
            this.barcodeList = res.data.data
          }else{
            this.msg = res.data.msg
          }
          this.modalVisible = false
        })
      }
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        barcode: '',
        barcodeList:[],
        pageData:{},
        msg:'',
        modalVisible: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getAllMatBarcodeList','getAllMatBarcodeListBySapMatDocNo'
      ]),
      ...mapMutations([
        'setPage'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      goback(){
        switch(this.page){
          case 'in_receipt_scm':
            this.$emit('gotoPageEvent','in_receipt_scm');
            break
          case 'in_receipt_stoSend':
            this.$emit('gotoPageEvent','in_receipt_stoSend');
            break
          case 'in_receipt_305Sap':
            this.$emit('gotoPageEvent','in_receipt_305Sap');
            break
        }
      },
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          if('' === this.barcode){
            this.$ons.notification.toast('请输入或扫描SCM条码！', {timeout: 2000});
          }else{
            //console.log(this.barcodeList.findIndex(this.barcode))
            let barCode = this.barcode
            let check = false
            this.barcodeList.find(function(value) { 
              if(value.BARCODE === barCode) { 
                check = true
              }
            })
            if(check){
              this.msg += '条码' + barCode + '包含在数据内; <br/>'
            }else{
              this.msg += '<span style="color:red">条码' + barCode + '不在数据内;</span> <br/>'
            }
            this.barcode = ''
            setFocusIndex(0)
          }
        }
      },
      reset(){
        this.barCode = ''
        this.msg = ''
        this.setFocus('jump',0)
      },
      setFocus(actionType,index) {
        if (actionType === 'jump') {
          this.currentIndex = index
        }
        this.focusCtrl++
        this.actionType = actionType
      },
      /**
       * 元素聚焦时,获取当前聚焦元素的索引
       * @param index {number} 当前聚焦的索引
       **/
      setFocusIndex(index) {
          this.currentIndex = index
      }
    }
  }
</script>