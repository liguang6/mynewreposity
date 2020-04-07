<!-- 退货模块 收料房退货 外购124退货-->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'外购退货'" :action="toggleMenu"></custom-toolbar>
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
      <p v-if="pageData.barCodeInfo.length === 0">请扫描或输入条码后回车....<br/>{{msg}}</p>
      <v-ons-list v-if="pageData.barCodeInfo.length > 0">
        <v-ons-list-item >
          <span class="list-item__title"><b>条码：</b>{{pageData.barCodeInfo[0].LABEL_NO}}</span>
        </v-ons-list-item>
        <v-ons-list-item >
          <span class="list-item__title"><b>批次：</b>{{pageData.barCodeInfo[0].BATCH}}</span>
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
                  <span class="list-item__title"><b>数量:</b>{{pageData.barCodeInfo[0].QTY3}} {{pageData.barCodeInfo[0].UNIT}}</span>
                </v-ons-list-item>

                <v-ons-list-item modifier="longdivider">
                  <span class="list-item__title"><b>类型:</b>{{pageData.barCodeInfo[0].SOBKZ}}</span>
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
      st_pageData: (state) => state.wms_in.pageData,
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
    }),
    mounted () {
      console.log('-->st_userWerks = ' + this.st_userWerks + "|" + sessionStorage.getItem('UserName') + "|" + this.st_whNumber)
      this.setPage('return01')      
      if(undefined === this.st_pageData.barCodeList){
        this.pageData = {
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'25'
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
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'25'
        },
        msg: '',
        modalVisible: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getReturnInfoByBarcode'
      ]),
      ...mapMutations([
        'setPage','setPageData'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      ScanbarCode(e){
        if(13 === window.event.keyCode){
          this.modalVisible = true
          if('' === this.barCode){
            this.$ons.notification.toast('请输入或扫描条码!', {timeout: 2000});
          }else{
            let barCode = this.barCode
            let check = false
            this.pageData.barCodeList.find(function(value) { 
              if(value.LABEL_NO === barCode) { 
                check = true
              }
            })

            if(check){
              this.$ons.notification.toast('条码'+barCode+'已扫描，请勿重复扫描！', {timeout: 1500})
              this.modalVisible = false
            }else{
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
                      //res.data.result[0].LABEL_NOS = barCode
                      this.pageData.barCodeInfo = res.data.result
                      //console.log('res.data.result[0].LABEL_NO = ' + res.data.result[0].LABEL_NO)
                      this.pageData.barCodeList.push(res.data.result[0])
                    }
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
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'25'
        }
        this.barCode = ''
        this.setFocus('jump',0)
        this.setPage('return01')
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