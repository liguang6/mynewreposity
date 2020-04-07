<!-- 入库模块 收料房收货 305工厂间调拨(根据条码收货) 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'305调拨(根据条码收货)'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>

      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col width="20%" style="text-align:right;line-height: 40px;">条码：&nbsp;</v-ons-col>
        <v-ons-col width="62%">
          <input @focus="setFocusIndex(0)" data-index="0" autocomplete="off" v-model="barCode" style="width:90%" @keypress="ScanBarcode" placeholder="请扫条码">  
        </v-ons-col>
        <v-ons-col width="18%" style="text-align:right;line-height: 40px;">
          <v-ons-button @click="search" style="width:60px;padding: 0 3px;" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;查询</v-ons-button>
         </v-ons-col>
      </v-ons-row>
      <v-ons-row >
        <v-ons-col width="26%" style="text-align:right;line-height: 40px;">储/库位：</v-ons-col>
        <v-ons-col width="56%">
          <input autocomplete="off" v-model="binCode" style="width:40%" placeholder="储位">
          &nbsp;<input autocomplete="off" v-model="lgort" style="width:30%" placeholder="库位">
        </v-ons-col>
        <v-ons-col width="18%" style="text-align:right;line-height: 40px;">
          <v-ons-button @click="reset" style="width:60px;padding: 0 3px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.barCodeInfo.length === 0">请扫描或输入交货单号/条码后回车...<br/>{{msg}}</p>
      <v-ons-list v-if="pageData.barCodeInfo.length > 0">
        <v-ons-list-item >
          <p style="line-height:0px"><b>条码：</b>{{pageData.barCodeInfo[0].LABEL_NO}} </p>
        </v-ons-list-item>
        <v-ons-list-item >
          <p><b>物料:</b>{{pageData.barCodeInfo[0].MATNR}} {{pageData.barCodeInfo[0].MAKTX}}</p>
        </v-ons-list-item>
        <v-ons-list-item >
          <p style="line-height:0px"><b>数量:</b>{{pageData.barCodeInfo[0].BOX_QTY}} {{pageData.barCodeInfo[0].UNIT}}</p>
        </v-ons-list-item>
        <v-ons-list-item >
          <p style="line-height:0px"><b>批次:</b>{{pageData.barCodeInfo[0].BATCH}}</p>
        </v-ons-list-item>
        <v-ons-list-item >
          <p style="line-height:0px"><b>供应商:</b>{{pageData.barCodeInfo[0].LIFNR}}</p>
        </v-ons-list-item>

      <!--
        BUG1093:在扫描页面 增加字段 库位（输入）扫描条码 只获取料号，物料描述，数量，批次，供应商代码数据，不需要校验状态
        -->
      <!--
      <v-ons-row >
        <v-ons-col width="48%" >
          <v-ons-list>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>数量:</b>{{pageData.barCodeInfo[0].BOX_QTY}} {{pageData.barCodeInfo[0].UNIT}}</p>
            </v-ons-list-item>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>储位:</b>{{pageData.barCodeInfo[0].BIN_CODE}}</p>
            </v-ons-list-item>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>供应商:</b>{{pageData.barCodeInfo[0].LIFNR}}</p>
            </v-ons-list-item>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>箱数:</b>{{pageData.barCodeInfo[0].BOX_SN}}</p>
            </v-ons-list-item>
          </v-ons-list>
        </v-ons-col>
        <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
          <v-ons-list>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>收货工厂:</b>{{pageData.barCodeInfo[0].WERKS}} </p>
            </v-ons-list-item>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>库存类型:</b>{{pageData.barCodeInfo[0].SOBKZ}}</p>
            </v-ons-list-item>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>尾箱标识:</b>{{pageData.barCodeInfo[0].END_FLAG}}</p>
            </v-ons-list-item>
            <v-ons-list-item modifier="longdivider">
              <p style="line-height:0px"><b>已扫箱数:</b>{{pageData.list.length}}</p>
            </v-ons-list-item>
          </v-ons-list>
        </v-ons-col>
      </v-ons-row >
      -->
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
      st_pageData: (state) => state.wms_in.pageData
    }),
    mounted () {
      this.setPage('in_receipt_305Barcode')
      if(undefined === this.st_pageData.barCodeInfo){
        this.pageData = {
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'06'
        }
      }else{
        console.log("-->st_pageData length = "+this.st_pageData.barCodeInfo)
        this.pageData = this.st_pageData
      }
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        barCode :'',
        lgort:'',
        binCode :'',
        pageData:{
          list: [],
          allList:[],
          skInfoList: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'06'
        },
        msg: '',
        modalVisible: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getLabelInfo',
      ]),
      ...mapMutations([
        'setPage','setPageData'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          this.search()
        }
      },
      search(){
        if('' === this.barCode){
          this.$ons.notification.toast('请输入或扫描条码！', {timeout: 2000});
        }else{
          let barCode = this.barCode
          let lgort = this.lgort
          let binCode = this.binCode
          let check = false
          this.pageData.list.find(function(value) { 
            if(value.LABEL_NO === barCode) { 
              check = true
            }
          })

          if(check){
            this.$ons.notification.toast('条码'+barCode+'已扫描，请勿重复扫描！', {timeout: 1500})
            this.modalVisible = false
          }else{
            this.getLabelInfo({LabelNo:barCode,LGORT:lgort,BIN_CODE:binCode}).then(res => {
              if(0 === res.data.code){
                if(0 === res.data.data.length){
                  this.$ons.notification.toast('没有查询到条码'+barCode+'的信息！', {timeout: 1500})
                }else{
                  //BUG1093:在扫描页面 增加字段 库位（输入）扫描条码 只获取料号，物料描述，数量，批次，供应商代码数据，不需要校验状态
                  this.msg = ''
                  this.pageData.barCodeInfo = res.data.data
                  this.pageData.list.push(res.data.data[0])
                  /** if(res.data.data[0].LABEL_STATUS === '00'){
                    this.msg = ''
                    this.pageData.barCodeInfo = res.data.data
                    this.pageData.list.push(res.data.data[0])
                  }else{
                    this.$ons.notification.toast('条码'+barCode+'的状态不是已创建状态，不能收货！', {timeout: 1500})
                  }**/
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
      },
      getDataTable(){
        for(var i in this.pageData.list){
          this.pageData.list[i].QTY = this.pageData.list[i].BOX_QTY
          this.pageData.list[i].RECEIPT_QTY = this.pageData.list[i].BOX_QTY
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
        this.setPage('in_receipt_305Barcode')
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