<!-- 入库模块 收料房收货 SCM条码收货 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'SCM条码收货'" :action="toggleMenu"></custom-toolbar>
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
      <p v-if="pageData.barCodeInfo.length === 0">请扫描或输入条码后回车...<br/>{{msg}}</p>
      <v-ons-list v-if="pageData.barCodeInfo.length > 0">
        <v-ons-list-item >
          <span class="list-item__title"><b>条码：</b>{{pageData.barCodeInfo[0].BARCODE}} </span>
        </v-ons-list-item>
        <v-ons-list-item >
          <span class="list-item__title"><b>SCM单号：</b>{{pageData.barCodeInfo[0].ASSNO}} </span>
        </v-ons-list-item>

        <v-ons-row >
            <v-ons-col width="48%" >
              <v-ons-list>
                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>数量:</b>{{pageData.barCodeInfo[0].QUANTITY}} {{pageData.barCodeInfo[0].UNIT}}</span>
                </v-ons-list-item>

                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>箱序:</b>{{pageData.barCodeInfo[0].CONTAINERCODE}}</span>
                </v-ons-list-item>
              </v-ons-list>
            </v-ons-col>
            <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list>
                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>收货工厂:</b>{{pageData.barCodeInfo[0].BYDFACTORY}} </span>
                </v-ons-list-item>

                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>已扫箱数:</b>{{pageData.barCodeList.length}}</span>
                </v-ons-list-item>
              </v-ons-list>
            </v-ons-col>
        </v-ons-row >

        <v-ons-list-item >
          <span class="list-item__title"><b>供应商:</b>{{pageData.barCodeInfo[0].PROVIDERCODE}}</span>
          <span class="list-item__subtitle">{{pageData.barCodeInfo[0].PROVIDERNAME}}</span>
        </v-ons-list-item>
        <v-ons-list-item >
          <span class="list-item__title"><b>物料:</b>{{pageData.barCodeInfo[0].MATERIELNO}}</span>
          <span class="list-item__subtitle">{{pageData.barCodeInfo[0].MATERIELDESCRIPTION}}</span>
        </v-ons-list-item>
        
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
      st_pageData: (state) => state.wms_in.pageData,
    }),
    mounted () {
      this.setPage('in_receipt_scmBarcode')      
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
        this.pageData = this.st_pageData
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
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'01'
        },
        msg: '',
        modalVisible: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getScmBarCodeInfo','listScmMat','listSKInfo'
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
            this.$ons.notification.toast('请输入或扫描条码！', {timeout: 2000});
          }else{
            let barCode = this.barCode
            let check = false
            this.pageData.barCodeList.find(function(value) { 
              if(value.BARCODE === barCode) { 
                check = true
              }
            })

            if(check){
              this.$ons.notification.toast('条码'+barCode+'已扫描，请勿重复扫描！', {timeout: 1500})
              this.modalVisible = false
            }else{
              this.getScmBarCodeInfo({BARCODE:barCode}).then(res => {
                if(0 === res.data.code){
                  if(0 === res.data.data.length){
                    this.$ons.notification.toast('没有查询到条码'+barCode+'的信息！', {timeout: 1500})
                  }else{
                    this.msg = ''
                    if(2 === res.data.data[0].STATE || 8 === res.data.data[0].STATE){
                      if('' != this.assno){
                        console.log('-->this.assno : ' + this.assno)
                        //只能扫描同一个SCM送货单的条码
                        if(this.assno != res.data.data[0].ASSNO){
                          this.$ons.notification.toast('只能扫描同一个SCM送货单的条码！', {timeout: 1500})
                        }else{
                          this.pageData.barCodeInfo = res.data.data
                          this.pageData.barCodeList.push(res.data.data[0])
                        }
                      }else{
                        this.pageData.barCodeInfo = res.data.data
                        this.pageData.barCodeList.push(res.data.data[0])
                      }
                    }else{
                      this.$ons.notification.toast('条码'+barCode+'的状态为'+res.data.data[0].STATE+',不允许收货！', {timeout: 2000})
                    }
                  }
                  console.log('-->barCodeList.length : ' + this.pageData.barCodeList.length)
                  if(1 === this.pageData.barCodeList.length){
                    this.assno = res.data.data[0].ASSNO
                    this.setAssno(this.assno)
                    //查询对应SCM送货单的信息
                    this.listScmMat({assno: this.assno}).then(res => {
                      if(0 === res.data.code){
                        this.pageData.allList = res.data.page.list
                        this.msg = ''
                      }else{
                        this.pageData.allList = [],
                        this.msg = res.data.msg,
                        this.$ons.notification.toast(res.data.msg, {timeout: 2000});
                      }
                      this.modalVisible = false;
                    })
                  
                    console.log('---->listSKInfo assno = ' + this.assno);
                    this.listSKInfo({assno: this.assno}).then(res => {
                      if(0 === res.data.code){
                        this.pageData.skInfoList = res.data.data
                        this.msg = ''
                      }
                    })
                  }
                }else{
                  this.msg = res.data.msg
                }
                this.modalVisible = false
              })
            }
            this.barCode = ''
            this.setFocus('jump',0)
          }
        }
      },
      getDataTable(){
        for(var i in this.pageData.barCodeList){
          let PONO = this.pageData.barCodeList[i].PONO
          let ROWITEM = this.pageData.barCodeList[i].ROWITEM

          //SCM扫描条码查询出的行项目号（ROWITEM）0050，SAP同步的数据（PO_ITEM_NO）为00050。
          if(ROWITEM.length == 4){
            ROWITEM = '0'+ ROWITEM
          }
          this.pageData.barCodeList[i].ROWITEM = ROWITEM

          for(var j in this.pageData.allList){
            if(this.pageData.barCodeList[i].PONO === this.pageData.allList[j].PONO && this.pageData.barCodeList[i].ROWITEM === this.pageData.allList[j].PO_ITEM_NO){
              let check = true
              this.pageData.allList[j].RECEIPT_QTY = 0

              this.pageData.list.find(function(value) { 
                if(value.PONO === PONO && value.PO_ITEM_NO === ROWITEM) { 
                  check = false
                }
              })
              if(check){
                this.pageData.allList[j].RECEIPT_QTY = 0
                this.pageData.list.push(this.pageData.allList[j])
              }
            }
          }
        }

        for(var i in this.pageData.barCodeList){
          let PONO = this.pageData.barCodeList[i].PONO
          let PO_ITEM_NO = this.pageData.barCodeList[i].ROWITEM
          console.log('-->PO_ITEM_NO = ' + PO_ITEM_NO)
          let QUANTITY = this.pageData.barCodeList[i].QUANTITY
          for(var j in this.pageData.list){
            if(PONO === this.pageData.list[j].PONO && PO_ITEM_NO === this.pageData.list[j].PO_ITEM_NO){
              this.pageData.list[j].RECEIPT_QTY += QUANTITY
            }
          }
        }

        this.setPageData(this.pageData)
        this.$emit('gotoPageEvent','in_matDataTable')
      },
      reset(){
        this.pageData = {
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'01'
        }
        this.barCode = ''
        this.setFocus('jump',0)
        this.setPage('in_receipt_scmBarcode')
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
      },
      
    }
  }
</script>
