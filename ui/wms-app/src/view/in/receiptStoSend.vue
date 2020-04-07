<!-- 入库模块 收料房收货 STO送货单收货 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'STO送货单收货'" :action="toggleMenu"></custom-toolbar>
    
    <v-ons-card v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-qrcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(0)" autocomplete="off" v-model="barCode" style="width:60%" data-index="0" @keypress="ScanBarcode" placeholder="请扫描送货单条码">
          &nbsp;<v-ons-button @click="search" style="width:65px;padding: 0 5px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',1)" style="color: green;padding-top:5px" size="25px" icon="fa-object-group"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(1)" autocomplete="off" v-model="area" style="width:60%" data-index="1" @keypress="ScanBarcode" placeholder="请输入收料区">
          &nbsp;<v-ons-button @click="reset" style="width:65px;padding: 0 5px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="list.length === 0">请输入或扫描STO送货单查询...<br/>{{msg}}</p>

      <v-ons-list>         
          <v-ons-row v-for="(li, $index) in list" :key="li.id">
            <v-ons-col width="6%" style="padding-top:15px">
              <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="checkDataList" >
              </v-ons-checkbox>
            </v-ons-col>
            <v-ons-col width="94%" >
                <v-ons-list-item expandable >
                  <v-ons-row>
                    <v-ons-col width="10%" style="padding-top:6px" >{{li.id}}</v-ons-col>
                    <v-ons-col width="50%" style="padding-top:6px" >料号:{{li.MATNR}}</v-ons-col>
                    <v-ons-col width="30%" style="padding-top:6px" >数量:{{li.RECEIPT_QTY}}/</v-ons-col>
                    <v-ons-col width="10%" ><ons-input id="re_date" readonly="true" :value="li.QTY" modifier="underbar" placeholder="数量" float></ons-input></v-ons-col>
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
        <v-ons-button style="margin: 6px 0"><v-ons-icon icon="fa-cogs"></v-ons-icon>&nbsp;确认过帐</v-ons-button>
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
      st_pageDataList: (state) => state.wms_in.pageDataList,
      st_checkDataList: (state) => state.wms_in.checkDataList,
    }),
    mounted () {
      this.setPage('in_receipt_stoSend')
      this.barCode = this.st_barCode
      this.list = this.st_pageDataList
      this.checkDataList = this.st_checkDataList
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        barCode :'',
        area: '',
        list: [],
        checkDataList: [],
        msg: '',
        modalVisible: false,
        ischeck: false
      }
    },
    watch:{
      checkDataList(curVal,oldVal){
        this.ischeck = (curVal.length === this.list.length&&this.list.length>0)?true:false
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'listScmMat',
      ]),
      ...mapMutations([
        'setPage','setPageDataList','setCheckDataList','setBarCode',
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
        //TODO 模拟数据
        this.list = [
          {id: 1,MATNR:'11-123456',RECEIPT_QTY: 155,QTY:155},
          {id: 2,MATNR:'11-222333',RECEIPT_QTY: 16,QTY:16},
          {id: 3,MATNR:'11-444444',RECEIPT_QTY: 16,QTY:16},
          {id: 4,MATNR:'11-555555',RECEIPT_QTY: 16,QTY:16},
          {id: 5,MATNR:'11-666666',RECEIPT_QTY: 16,QTY:16}
        ]
      },
      reset(){

      },
      barcodeCheck(){
        this.setBarCode(this.barCode)
        this.setCheckDataList(this.checkDataList)
        this.setPageDataList(this.list)
        this.$emit('gotoPageEvent','in_barcodeCheck')
      },
      checkall(){

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