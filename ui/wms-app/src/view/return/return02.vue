<!-- 退货模块 收料房退货 124T STO退货-->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'124T STO退货'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
       <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-qrcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input v-bind:disabled="pageData.assnoCheck" @focus="setFocusIndex(0)" autocomplete="off" v-model="pageData.assno" style="width:60%" data-index="0" @keypress="ScanAssno" placeholder="请扫交货单号条码">
          &nbsp;<v-ons-button @click="reset" style="width:65px;padding: 0 5px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',1)" style="color: green;padding-top:5px" size="25px" icon="fa-cube"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(1)" autocomplete="off" v-model="barCode" style="width:60%" data-index="1" @keypress="ScanBarcode" placeholder="请扫包装箱条码">
          &nbsp;<v-ons-button @click="search" style="width:65px;padding: 0 5px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.barCodeInfo.length === 0">{{msg}}</p>
      <v-ons-list v-if="pageData.barCodeInfo.length > 0">
        <v-ons-list-item >
          <p style="line-height:0px"><b>条码：</b>{{pageData.barCodeInfo[0].LABEL_NO}} </p>
        </v-ons-list-item>
        <v-ons-list-item >
          <p style="line-height:0px"><b>批次：</b>{{pageData.barCodeInfo[0].BATCH}} </p>
        </v-ons-list-item>
        <v-ons-list-item >
          <p><b>物料:</b>{{pageData.barCodeInfo[0].MATNR}} {{pageData.barCodeInfo[0].MAKTX}}</p>
        </v-ons-list-item>
        <v-ons-list-item >
          <p><b>供应商:</b>{{pageData.barCodeInfo[0].LIFNR}} {{pageData.barCodeInfo[0].LIKTX}}</p>
        </v-ons-list-item>

        <v-ons-row >
            <v-ons-col width="48%" >
              <v-ons-list>
                <v-ons-list-item modifier="longdivider">
                  <p style="line-height:0px"><b>数量:</b>{{pageData.barCodeInfo[0].QTY3}} {{pageData.barCodeInfo[0].UNIT}}</p>
                </v-ons-list-item>

                <v-ons-list-item modifier="longdivider">
                  <p style="line-height:0px"><b>类型:</b>{{pageData.barCodeInfo[0].SOBKZ}}</p>
                </v-ons-list-item>
              </v-ons-list>
            </v-ons-col>
            <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list>
                <v-ons-list-item modifier="longdivider">
                  <p style="line-height:0px"><b>收货工厂:</b>{{pageData.barCodeInfo[0].WERKS}} </p>
                </v-ons-list-item>

                <v-ons-list-item modifier="longdivider">
                  <p style="line-height:0px"><b>已扫箱数:</b>{{pageData.barCodeList.length}}</p>
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
      st_pageData: (state) => state.wms_in.pageData,
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
    }),
    mounted () {
      console.log('-->st_userWerks = ' + this.st_userWerks + "|" + sessionStorage.getItem('UserName') + "|" + this.st_whNumber)
      this.setPage('return02')      
      if(undefined === this.st_pageData.barCodeList){
        this.pageData = {
          assno:'',
          assnoCheck:false,
          assnoBarCodeInfo:[],
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'27'
        }
      }else{
        this.pageData = this.st_pageData
      }
      this.barCode = this.st_barCode
      console.log('this.st_pageData.barCodeList : ' , this.st_pageData.barCodeList)
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型        
        barCode: '',
        pageData:{
          assno:'',
          assnoCheck:false,
          assnoBarCodeInfo:[],     //SAP交货单对应的条码集合
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'27'
        },
        msg: '请扫描或输入SAP交货单后回车....',
        modalVisible: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getReturnInfoByBarcode','getReceiveRoomOutData'
      ]),
      ...mapMutations([
        'setPage','setPageData'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      ScanAssno(e){
        if(13 === window.event.keyCode){
          this.modalVisible = true
          this.getReceiveRoomOutData({WERKS:this.st_userWerks,
          WH_NUMBER:this.st_whNumber,
          BUSINESS_NAME:'',
          BUSINESS_CODE:this.pageData.BUSINESS_NAME,
          deliveryNO:this.pageData.assno}).then(res => {
            this.modalVisible = false
            /** if(0 === res.data.code){
              this.pageData.assnoCheck = true
              //封装 assnoBarCodeInfo
            }else{
              this.msg = res.data.msg
              this.$ons.notification.toast(res.data.msg, {timeout: 2000});
            }*/
            this.pageData.assnoCheck = true
            this.msg = '请扫描此交货单对应的条码....'
            this.pageData.assnoBarCodeInfo = ['1BQ0000001242','1BQ0000001243','1BQ0000001244','1BQ0000001245','1BQ0000001246']
            console.log(this.pageData.assnoBarCodeInfo[0])
          })
        }
      },
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          this.search()
        }
      },
      search(){
        this.modalVisible = true
        if('' === this.barCode){
          this.$ons.notification.toast('请输入或扫描条码!', {timeout: 2000});
        }else{
          let barCode = this.barCode
          let check = false //条码是否已经扫描过
          let match = false //条码是否属于此SAP交货单
          this.pageData.barCodeList.find(function(value) { 
            if(value.LABEL_NO === barCode) { 
              check = true
            }
          })

          if(check){
            this.$ons.notification.toast('条码'+barCode+'已扫描，请勿重复扫描！', {timeout: 1500})
            this.modalVisible = false
          }else{
            for(var i in this.pageData.assnoBarCodeInfo){
              if(barCode === this.pageData.assnoBarCodeInfo[i]){
                match = true
              }
            }
            if(match){
              this.getReturnInfoByBarcode({barCode:barCode}).then(res => {
                if(0 === res.data.code){
                  if(0 === res.data.result.length){
                    this.$ons.notification.toast('没有查询到条码'+barCode+'的信息！', {timeout: 1500})
                  }else{
                    this.msg = ''
                    if(this.st_userWerks != res.data.result[0].WERKS || this.st_whNumber != res.data.result[0].WH_NUMBER){
                      this.$ons.notification.toast(barCode + '不是当前仓库号'+this.st_whNumber+'的条码！！', {timeout: 1500})
                    }else if(!('04' === res.data.result[0].LABEL_STATUS || '02' === res.data.result[0].LABEL_STATUS)){
                      this.$ons.notification.toast('只有退货状态的物料才允许退货！', {timeout: 1500})
                    }else if(!('01' === res.data.result[0].BUSINESS_TYPE || '02' === res.data.result[0].BUSINESS_TYPE
                      || '03' === res.data.result[0].BUSINESS_TYPE || '04' === res.data.result[0].BUSINESS_TYPE || '05' === res.data.result[0].BUSINESS_TYPE)){
                      this.$ons.notification.toast('不是外购收货业务的条码不能选择外购退货！', {timeout: 1500})
                    }else{
                      this.pageData.barCodeInfo = res.data.result
                      this.pageData.barCodeList.push(res.data.result[0])
                    }
                  }
                }else{
                  this.msg = res.data.msg
                }
                this.modalVisible = false
              })
            }else{
              this.modalVisible = false
              this.$ons.notification.toast('条码'+barCode+'不属于此SAP交货单！', {timeout: 1500})
            }
            
            
          }
          this.barCode = ''
          this.setFocus('jump',0)
        }
      },
      getDataTable(){
        //this.pageData.list = this.pageData.barCodeList
        this.pageData.list = []
        for(var i in this.pageData.barCodeList){
          console.log('---->RID : ' + this.pageData.barCodeList[i].RID)
          let check = false
          let RID = this.pageData.barCodeList[i].RID
          let LABEL_NO = this.pageData.barCodeList[i].LABEL_NO
          let QTY = parseInt(this.pageData.barCodeList[i].QTY3)
          let listIndex = -1;
          console.log(LABEL_NO);
          for(var j in this.pageData.list){
            if (this.pageData.list[j].RID === RID) {
              check = true
              listIndex = j
              break
            } 
          }
          //this.pageData.barCodeList[i].LABEL_NOS = LABEL_NO
          if(!check){
            this.pageData.barCodeList[i].LABEL_NOS = LABEL_NO
            this.pageData.list.push(this.pageData.barCodeList[i])
          }else{
            this.pageData.list[listIndex].LABEL_NOS += ',' + LABEL_NO
            //this.pageData.list[listIndex].LABEL_NO += ',' + LABEL_NO
            this.pageData.list[listIndex].QTY2 += QTY
            this.pageData.list[listIndex].QTY3 += QTY
          }
        }

        this.setPageData(this.pageData)
        this.$emit('gotoPageEvent','re_matDataTable')
      },
      reset(){
        this.pageData = {
          assno:'',
          assnoCheck:false,
          assnoBarCodeInfo:[],
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'27'
        }
        this.barCode = ''
        this.msg = '请扫描或输入SAP交货单后回车....'
        this.setPage('return02')
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
      },
      
    }
  }
</script>