<!-- 入库模块 云平台收货 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'云平台条码收货'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col width="15%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-barcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="85%">
          <input @focus="setFocusIndex(0)" autocomplete="off" v-model="barCode" style="width:60%" data-index="0" @keypress="ScanbarCode" placeholder="请扫条码">
          &nbsp;<v-ons-button @click="reset" style="width:60px;padding: 0 3px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.barCodeInfo.length === 0">请扫描云平台条码...<br/>{{msg}}</p>
      <v-ons-list v-if="pageData.barCodeInfo.length > 0">
        <v-ons-list-item >
          <span class="list-item__title"><b>条码：</b>{{pageData.barCodeInfo[0].LABEL_NO}} </span>
        </v-ons-list-item>
        <v-ons-list-item >
          <span class="list-item__title"><b>批次：</b>{{pageData.barCodeInfo[0].BATCH}} </span>
        </v-ons-list-item>
        <v-ons-list-item >
          <span class="list-item__title"><b>送货单号：</b>{{pageData.barCodeInfo[0].ASNNO}} </span>
        </v-ons-list-item>
        <v-ons-list-item >
          <span class="list-item__title"><b>物料:</b>{{pageData.barCodeInfo[0].MATNR}}</span>
          <span class="list-item__subtitle">{{pageData.barCodeInfo[0].MAKTX}}</span>
        </v-ons-list-item>
        <v-ons-list-item >
          <span class="list-item__title"><b>供应商:</b>{{pageData.barCodeInfo[0].LIFNR}}</span>
          <span class="list-item__subtitle">{{pageData.barCodeInfo[0].LIKTX}}</span>
        </v-ons-list-item>

        <v-ons-row >
            <v-ons-col width="48%" >
              <v-ons-list>
                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>数量:</b>{{pageData.barCodeInfo[0].BOX_QTY}} {{pageData.barCodeInfo[0].UNIT_NO}}</span>
                </v-ons-list-item>

                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>箱序:</b>{{pageData.barCodeInfo[0].BOX_SN}}</span>
                </v-ons-list-item>
              </v-ons-list>
            </v-ons-col>
            <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list>
                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>收货工厂:</b>{{pageData.barCodeInfo[0].WERKS}} </span>
                </v-ons-list-item>

                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>已扫箱数:</b>{{pageData.barCodeList.length}}</span>
                </v-ons-list-item>
              </v-ons-list>
            </v-ons-col>
        </v-ons-row >
      </v-ons-list>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button @click="getDataTable" style="margin: 2px 0"><v-ons-icon icon="fa-table"></v-ons-icon>&nbsp;数据表</v-ons-button>
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
      st_assno: (state) => state.wms_in.assno,
      st_pageData: (state) => state.wms_in.pageData,
    }),
    mounted () {
      this.setPage('in_receipt_cloudBarcode')      
      if(undefined === this.st_pageData.barCodeInfo){
        this.pageData = {
          list: [],
          skInfoList:[],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'78'
        }
        this.assno = ''
      }else{
        this.pageData = this.st_pageData
        this.assno = this.st_assno
      }
      this.barCode = this.st_barCode
      
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        barCode: '',
        assno:'',
        pageData:{
          list: [],
          skInfoList:[],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'78'
        },
        msg: '',
        modalVisible: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'listSKInfo','listCloudMat','getDeliveryDetailByBarcode'
      ]),
      ...mapMutations([
        'setPage','setPageData','setAssno'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      ScanbarCode(e){
        if(13 === window.event.keyCode){
          this.modalVisible = true
          if('' === this.barCode){
            this.$ons.notification.toast('请扫描条码！', {timeout: 2000});
            this.modalVisible = false
            return false
          }else{
            //通过扫描条码获取送货单号 ID:0001;DN:N190800000000037/00001;S:20190814000001;DD:12345;
            /** let scan_str = this.barCode
            if (scan_str.indexOf("DN:") < 0 || scan_str.indexOf("S:") < 0) {
              this.modalVisible = false
              this.barCode = ''
              this.$ons.notification.toast('请扫描正确的云平台二维码！', {timeout: 2000});
              return false
            } else {
              this.assno = scan_str.substring(scan_str.indexOf("DN:")).substring(3, scan_str.substring(scan_str.indexOf("DN:")).indexOf("/"))
              this.barCode = scan_str.substring(scan_str.indexOf("S:")).substring(2, scan_str.substring(scan_str.indexOf("S:")).indexOf(";"))
            }**/
            let barCode = this.barCode
            if(""===this.assno){
              this.getDeliveryDetailByBarcode({BARCODE:this.barCode}).then(res2 => {
                this.assno = res2.data.data.VBELN
                console.log('this.assno = ' + this.assno)
                this.setAssno(this.assno)
                let check = false
                this.pageData.barCodeList.find(function(value) { 
                  if(value.LABEL_NO === barCode) { 
                    check = true
                  }
                })

                if(check){
                  this.modalVisible = false
                  this.$ons.notification.toast('条码'+barCode+'已扫描，请勿重复扫描！', {timeout: 1500})
                }else{
                  if(this.pageData.skInfoList.length === 0 ){ //扫描第一个条码
                    console.log('扫描第一个条码barCode:' + barCode + "|assno:" + this.assno)
                    this.listSKInfo({assno: this.assno,BUSINESS_NAME:this.pageData.BUSINESS_NAME}).then(res => {
                      console.log('res.data.code' + res.data.code)
                      if(0 === res.data.code){
                        this.pageData.skInfoList = res.data.data
                        for(var i in this.pageData.skInfoList){
                          if(this.pageData.skInfoList[i].LABEL_NO === barCode){
                            this.pageData.barCodeInfo[0] = this.pageData.skInfoList[i]
                            this.pageData.barCodeList.push(this.pageData.skInfoList[i])
                          }
                        }
                        this.msg = ''
                        this.modalVisible = false
                      }else{
                        this.modalVisible = false
                        this.$ons.notification.toast(res.data.msg, {timeout: 1500})
                      }
                    })
                  }
                  this.barCode = ''
                  this.setFocus('jump',0)
                }
              })
              
            }else{
              let labelExist = false
              for(var i in this.pageData.skInfoList){
                if(this.pageData.skInfoList[i].LABEL_NO === barCode){
                  labelExist = true
                  this.pageData.barCodeInfo[0] = this.pageData.skInfoList[i]
                  this.pageData.barCodeList.push(this.pageData.skInfoList[i])
                  this.$ons.notification.toast("扫描成功！", {timeout: 1500})
                }
              }
              if(!labelExist){
                this.$ons.notification.toast("此条码不存在或不属于送货单" + this.assno, {timeout: 1500})
              }
              this.modalVisible = false
              this.barCode = ''
              this.setFocus('jump',0)
            }
          }
        }
      },
      getDataTable(){
        this.modalVisible = true
        this.pageData.list = []
        try{
          this.listCloudMat({assno: this.assno,BUSINESS_NAME:this.pageData.BUSINESS_NAME}).then(res => {
            if(0 != res.data.code){
              this.modalVisible = false
              this.$ons.notification.toast(res.data.msg, {timeout: 1500})
              return false
            }
            for(var i in res.data.page.list){
              let RECEIPT_QTY = 0
              for(var j in this.pageData.barCodeList){
                if(this.pageData.barCodeList[j].ASNNO === res.data.page.list[i].ASNNO && this.pageData.barCodeList[j].ASNITM === res.data.page.list[i].ASNITM){
                  RECEIPT_QTY += this.pageData.barCodeList[j].BOX_QTY
                }
              }
              if(RECEIPT_QTY > 0){
                res.data.page.list[i].RECEIPT_QTY = RECEIPT_QTY
                this.pageData.list.push(res.data.page.list[i])
              }
            }
            this.setPageData(this.pageData)
            this.modalVisible = false
            this.$emit('gotoPageEvent','in_matDataTable')
          }).catch(err => {
            console.log(err)
            this.modalVisible = false
          })
        }catch (error) {
          console.log('系统异常，请联系管理员')
          this.modalVisible = false
        }
      },
      reset(){
        this.pageData = {
          list: [],
          skInfoList:[],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'78'
        }
        this.barCode = ''
        this.assno = ''
        this.setFocus('jump',0)
        this.setPage('in_receipt_cloudBarcode')
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
