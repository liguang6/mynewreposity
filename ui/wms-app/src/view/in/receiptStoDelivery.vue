<!-- 入库模块 收料房收货 STO交货单收货 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'STO交货单收货'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-qrcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(0)" autocomplete="off" v-model="assno" style="width:60%" data-index="0" @keypress="ScanBarcode" placeholder="请扫交货单号条码">
          &nbsp;
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',1)" style="color: green;padding-top:5px" size="25px" icon="fa-cube"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(1)" autocomplete="off" v-model="barCode" style="width:60%" data-index="1" @keypress="ScanBarcode" placeholder="请扫包装箱条码">
          &nbsp;<v-ons-button @click="search" style="width:65px;padding: 0 5px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',2)" style="color: green;padding-top:5px" size="25px" icon="fa-object-group"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(2)" autocomplete="off" v-model="area" style="width:60%" data-index="2" @keypress="ScanBarcode" placeholder="请输入收料区">
          &nbsp;<v-ons-button @click="reset" style="width:65px;padding: 0 5px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.barCodeInfo.length === 0">请扫描或输入交货单号/条码后回车...<br/>{{msg}}</p>
      <v-ons-list>
        <v-ons-list-item tappable v-for="li in pageData.list" :key="li.id" @click="getDetail(li.id)">
          <v-ons-col width="45%"><b>物料：</b>{{li.mat}}</v-ons-col>
          <v-ons-col width="30%"><b>数量：</b>{{li.boxQty}}</v-ons-col>
          <v-ons-col width="25%"><b>箱数：</b>{{li.qty}}</v-ons-col>
        </v-ons-list-item>
      </v-ons-list>

    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button @click="getDetail" style="margin: 6px 0"><v-ons-icon icon="fa-table"></v-ons-icon>&nbsp;数据表</v-ons-button>
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
      st_assno: (state) => state.wms_in.assno,
    }),
    mounted () {
      this.setPage('in_receipt_stoDelivery')
      if(undefined === this.st_pageData.barCodeInfo){
        this.pageData = {
          barCodeInfo: [],
          list: []
        }
      }else{
        console.log("-->st_pageData length = "+this.st_pageData.barCodeInfo)
        this.pageData = this.st_pageData
      }
      this.barCode = this.st_barCode
      this.assno = this.st_assno
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        assno: '',
        barCode :'',
        area: '',
        pageData:{
          barCodeInfo: [],
          list: []
        },
        msg: '',
        modalVisible: false,
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
        'setPage','setBarCode','setPageData','setAssno'
      ]),
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          this.search()
        }
      },
      search(){
        if('' === this.barCode && '' === this.assno){
          this.$ons.notification.toast('请输入或扫描交货单号/包装箱号！', {timeout: 2000});
        }else{
          //TODO 查询数据
          this.pageData = {
            barCodeInfo: [{boxNumber: '8888888',factory: 'C161',vendor: 'VBYD123',isTest:'是'}],
            list: [{id: 1,mat:'11-123456',boxQty: 5,qty:10},{id: 2,mat:'11-222333',boxQty: 6,qty:30},
            {id: 3,mat:'11-3333333',boxQty: 15,qty:10},{id: 4,mat:'11-4444444',boxQty: 12,qty:30}]
          }
        }
      },
      getDetail(id){
        console.log('-->getDetail id : ' + id + " barcode : " + this.barCode)
        this.setBarCode(this.barCode)
        this.setPageData(this.pageData)
        this.setAssno(this.assno)
        this.$emit('gotoPageEvent','in_matDataTable')
      },
      reset(){
        this.pageData = {
          barCodeInfo: [],
          list: []
        }
        this.assno = ''
        this.barCode = ''
        this.area = ''
        this.setFocus('jump',0)
        this.setPage('in_receipt_stoDelivery')
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