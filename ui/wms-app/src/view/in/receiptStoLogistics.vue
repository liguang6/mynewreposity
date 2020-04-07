<!-- 入库模块 收料房收货 STO物流发货清单收货 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'STO物流发货清单收货'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-qrcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(0)" autocomplete="off" v-model="assno" style="width:60%" data-index="0" @keypress="ScanBarcode" placeholder="请扫描物流单号">
          &nbsp;
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="10%" style="text-align:right"><ons-icon @click="setFocus('jump',1)" style="color: green;padding-top:5px" size="25px" icon="fa-cube"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="90%">
          <input @focus="setFocusIndex(1)" autocomplete="off" v-model="barCode" style="width:60%" data-index="1" @keypress="ScanBarcode" placeholder="请扫描条码">
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
      <p>请扫描或输入条码后回车...<br/>{{msg}}</p>

    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button style="margin: 6px 0"><v-ons-icon icon="fa-qrcode"></v-ons-icon>&nbsp;条码校验</v-ons-button> &nbsp;&nbsp;
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
    }),
    mounted () {
      this.setPage('in_receipt_stoDelivery')
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        assno: '',
        barCode :'',
        area: '',
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
        'setPage',
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

      },
      reset(){

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