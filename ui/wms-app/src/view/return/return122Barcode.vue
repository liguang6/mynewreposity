<!-- 退货模块 122采购订单退货 按条码退货-->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'122采购订单退货'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">      
       <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-qrcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input v-bind:disabled="pageData.assnoCheck" @focus="setFocusIndex(0)" autocomplete="off" v-model="pageData.assno" style="width:60%" data-index="0" @keypress="ScanAssno" placeholder="请扫订单号条码">
          &nbsp;<v-ons-button @click="reset" style="width:65px;padding: 0 5px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-cube"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(0)" autocomplete="off" v-model="barCode" style="width:60%" data-index="0" @keypress="ScanBarcode" placeholder="请扫包装箱条码">
          &nbsp;<v-ons-button @click="search" style="width:65px;padding: 0 5px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.barCodeInfo.length === 0">{{msg}}</p>
      <v-ons-list>
        <v-ons-row v-for="(li, $index) in pageData.barCodeList" :key="li.LID">
          <v-ons-col>
            <v-ons-list-item expandable >
              <v-ons-row>
                <v-ons-col width="60%" style="padding-top:6px" >{{$index+1}}:{{li.LABEL_NO}}</v-ons-col>
                <v-ons-col width="40%" style="padding-top:6px" >数量:{{li.BOX_QTY}}{{li.UNIT}}</v-ons-col>
              </v-ons-row>
              <div class="expandable-content">
                物料：{{li.MATNR}}{{li.MAKTX}}<br/>
                箱序：{{li.BOX_SN}}<br/>
                批次：{{li.BATCH}}<br/>
                库位：{{li.LGORT}}<br/>
                库存类型：{{li.SOBKZ}}<br/>
                供应商：{{li.LIFNR}}{{li.LIKTX}}<br/>
                </div>
            </v-ons-list-item>
          </v-ons-col>
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        累计数量：{{countQty}}
        <v-ons-button @click="getDataTable" style="margin: 2px 0"><v-ons-icon icon="fa-table"></v-ons-icon>&nbsp;数据表</v-ons-button>
        已扫箱数：{{countBox}}
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
      page: (state) => state.wms_return.page,
      st_barCode: (state) => state.wms_return.barCode,
      st_pageData: (state) => state.wms_return.pageData,
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
    }),
    mounted () {
      console.log('-->st_userWerks = ' + this.st_userWerks + "|" + sessionStorage.getItem('UserName') + "|" + this.st_whNumber)
      this.setPage('return122Barcode')      
      if(undefined === this.st_pageData.barCodeList){
        this.pageData = {
          assno:'',
          assnoCheck:false,
          assnoBarCodeInfo:[],
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'28',
          BUSINESS_TYPE:'02'
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
        countQty:0,
        countBox:0,
        pageData:{
          assno:'',
          assnoCheck:false,
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'28',
          BUSINESS_TYPE:'02'
        },
        msg: '请扫描或输入条码后回车....',
        modalVisible: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getWareHouseOutData29','getSapPoBarcodeInfo'
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
          this.getWareHouseOutData29({WERKS:this.st_userWerks,WH_NUMBER:this.st_whNumber,PONO:this.pageData.assno}).then(res => {
            this.modalVisible = false
            if(0 === res.data.code){
              if(res.data.result.length > 0){
                this.pageData.list = res.data.result
                this.pageData.assnoCheck = true
                this.msg = '请扫描此交货单对应的条码....'
                for(var i in this.pageData.list){
                  this.pageData.list[i].QTY1 = 0
                }
              }else{
                this.msg = '没有找到此订单的数据！'
              }
            }else{
              this.msg = res.data.msg
              this.$ons.notification.toast(res.data.msg, {timeout: 2000});
            }
          })
        }
      },
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          if(!this.pageData.assnoCheck){
            this.$ons.notification.toast('请先输入或扫描订单号!', {timeout: 2000});
          }else{
            this.search()
          }
        }
      },
      search(){
        if('' === this.barCode || '' === this.pageData.assno){
          this.$ons.notification.toast('请输入或扫描订单号和条码!', {timeout: 2000});
        }else{
          this.modalVisible = true
          let barCode = this.barCode
          let check = false //条码是否已经扫描过
          this.pageData.barCodeList.find(function(value) { 
            if(value.LABEL_NO === barCode) { 
              check = true
            }
          })

          if(check){
            this.$ons.notification.toast('条码'+barCode+'已扫描，请勿重复扫描！', {timeout: 1500})
            this.modalVisible = false
          }else{
            this.getSapPoBarcodeInfo({PO_NO:this.pageData.assno,Barcode:barCode}).then(res => {
              if(0 === res.data.code){
                if(res.data.result.length > 0){
                  //判断扫描数量是否超出冻结数量
                  let matnr = res.data.result[0].MATNR
                  let freeQty = 0
                  let countQty = 0 
                  for(var i in this.pageData.list){
                    if(matnr === this.pageData.list[i].MATNR){
                      freeQty = freeQty + this.pageData.list[i].FREEZE_QTY
                    }
                  }
                  for(var i in this.pageData.barCodeList){
                    if(matnr === this.pageData.barCodeList[i].MATNR){
                      countQty = countQty + this.pageData.barCodeList[i].BOX_QTY
                    }
                  }
                  console.log(countQty + '|' + freeQty)
                  if(countQty + res.data.result[0].BOX_QTY > freeQty){
                    this.$ons.notification.toast('物料'+matnr+'扫描数量不能超过冻结数量'+freeQty+'!', {timeout: 2000});
                  }else{
                    this.pageData.barCodeInfo = res.data.result[0]
                    this.pageData.barCodeList.unshift(res.data.result[0])
                    this.countQty += res.data.result[0].BOX_QTY
                    this.countBox = this.countBox + 1
                  }
                }else{
                  this.$ons.notification.toast('订单'+this.pageData.assno+'没有此条码的信息!', {timeout: 2000});
                }
              }else{
                this.$ons.notification.toast('订单'+this.pageData.assno+'没有此条码的信息!', {timeout: 2000});
              }
              this.modalVisible = false
            })
          }
          this.barCode = ''
          this.setFocus('jump',0)
        }
      },
      getDataTable(){
        this.setPageData(this.pageData)
        this.$emit('gotoPageEvent','re_matDataTable')
      },
      reset(){
        this.pageData = {
          assno:'',
          assnoCheck:false,
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'28',
          BUSINESS_TYPE:'02'
        }
        this.countQty = 0,
        this.countBox = 0,
        this.barCode = ''
        this.msg = '请扫描或输入条码后回车....'
        this.setPage('return122Barcode')
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