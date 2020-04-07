<!-- 退货模块 物料点收条码扫描组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'物料点收条码扫描'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card style="padding-bottom:0px">
      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col width="15%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="28px" icon="fa-barcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="85%">
          <input @focus="setFocusIndex(0)" id="barcode" autocomplete="off" v-model="barCode" style="width:75%" type="text" data-index="0" @keypress="ScanBarcode" placeholder="请扫条码">
          <p><b>已扫条码数：{{scanCount}}/{{barCodeList.length}}</b></p>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <v-ons-list>
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center">
          <v-ons-col style="border-left-style: ridge;border-left-width: 1px;">条码</v-ons-col>
        </v-ons-row>

        <v-ons-row v-for="mat in barCodeList" :key="mat.code" :class="{'is-check': mat.checked }" width="100%" style="border-style: ridge;border-width: 1px;text-align:center;line-height:25px">
          <v-ons-col style="border-left-style: ridge;border-left-width: 1px;">{{mat.code}}</v-ons-col>
        </v-ons-row>

      </v-ons-list>

    </v-ons-card>


    <ons-bottom-toolbar>
      <center>
        <v-ons-button @click="goback" style="margin: 6px 0"><v-ons-icon icon="fa-reply"></v-ons-icon>&nbsp;返回</v-ons-button> &nbsp;&nbsp;
        <v-ons-button @click="checkConfirm" style="margin: 6px 0"><v-ons-icon icon="fa-check"></v-ons-icon>&nbsp;确认</v-ons-button> &nbsp;&nbsp;
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
      st_pageData: (state) => state.wms_in.pageData,
      st_metCheckId: (state) => state.wms_in.metCheckId
    }),
    mounted () {
      this.pageData = this.st_pageData
      this.scanCount = this.pageData.list[this.st_metCheckId].LABEL_NOS.split(",").length
      var arr = this.pageData.list[this.st_metCheckId].BARCODES.split(",")
      var arr2 = this.pageData.list[this.st_metCheckId].LABEL_NOS.split(",")
      var barCodeList = []
      arr.forEach(function(value,i){
        var checked = false
        arr2.forEach(function(v,j){
          if(value === v) checked = true
        })
        var barcode = {code:value,checked:checked}
        console.log('barcode',barcode)
        barCodeList.push(barcode)
      })
      this.barCodeList = barCodeList
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        barCode: '',
        barCodeList:[],
        pageData:{
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'25'
        },
        checkQty: 0,
        modalVisible: false,
        scanCount: 0
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
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          let match = false
          for(var i in this.barCodeList){
            console.log('-->' + i + ':'+ this.barCode + '|' + this.barCodeList[i].code)
            if(this.barCode === this.barCodeList[i].code){
              match = true
              if(this.barCodeList[i].checked){
                this.$ons.notification.toast('此条码已扫描！', {timeout: 2000});
              }else{
                console.log('---->matched' + i + ':')
                //this.barCodeList[i].checked = true               
                this.modalVisible = true
                this.scanCount++;
                let barCode = this.barCode
                let index = i
                this.getReturnInfoByBarcode({barCode:barCode}).then(res => {
                  if(0 === res.data.code){
                      this.pageData.barCodeInfo = res.data.result
                      this.pageData.barCodeList.push(res.data.result[0])
                      this.barCodeList[index].checked = true
                  }else{
                    this.msg = res.data.msg
                  }
                  this.modalVisible = false
                })
              }  
            }
          }
          if(match){
            //this.matList.sort(sortMat)
          }else{
            this.$ons.notification.toast('此条码不存在于此批物料数据中！', {timeout: 2000});
          }
          this.barCode = ''
        }
      },
      checkConfirm(){
        this.setPageData(this.pageData)
        switch(this.page){
          case 'return01':
            this.$emit('gotoPageEvent','return01');
            break
          case 'return02':
            this.$emit('gotoPageEvent','return02');
            break
        }
      },
      goback(){
        console.log('-->goback 当前操作页面：' + this.page)
        switch(this.page){
          case 'return01':
            this.$emit('gotoPageEvent','return01');
            break
          case 'return02':
            this.$emit('gotoPageEvent','return02');
            break
        }
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
  function sortMat(a, b){
    return b.sort - a.sort
  }
</script>
<style>
.is-check{
  background-color: lightseagreen;
}
</style>