<!-- 入库模块 物料点收条码扫描组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'物料点收条码扫描'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card style="padding-bottom:0px">
      <v-ons-row v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
        <v-ons-col width="15%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="28px" icon="fa-barcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="85%">
          <input @focus="setFocusIndex(0)" id="barcode" autocomplete="off" v-model="barcode" style="width:75%" type="text" data-index="0" @keypress="ScanBarcode" placeholder="请扫条码">
          <p><b>已点收数量：{{checkQty}}/{{totalQty}}</b></p>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <v-ons-list>
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center">
          <!--<v-ons-col width="15%" >点收</v-ons-col> -->
          <v-ons-col width="50%" style="border-left-style: ridge;border-left-width: 1px;">条码</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">箱序</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
        </v-ons-row>

        <v-ons-row v-for="mat in matList" :key="mat.IDXID" :class="{'is-check': mat.checked }" width="100%" style="border-style: ridge;border-width: 1px;text-align:center;line-height:25px">
          <!--<v-ons-col width="15%" >
            <v-ons-checkbox style="" :value='mat.id' v-model="matListCheck">
            </v-ons-checkbox>
          </v-ons-col>-->
          <v-ons-col width="50%" style="border-left-style: ridge;border-left-width: 1px;">{{mat.BARCODE}}</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">{{mat.CONTAINERCODE}}</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">{{mat.QUANTITY}}</v-ons-col>
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
      this.modalVisible = true
      this.pageData = this.st_pageData
      console.log('---->metCheckId : ' + this.st_metCheckId +'|'+ this.pageData.list[this.st_metCheckId].PO_NO
      +'|'+ this.pageData.list[this.st_metCheckId].PO_ITEM_NO)
      this.matList = []
      this.getMatBarcodeList({ASS_NO:this.pageData.list[this.st_metCheckId].ASNNO,
        MATNR:this.pageData.list[this.st_metCheckId].MATNR,
        PO_NO:this.pageData.list[this.st_metCheckId].PO_NO,
        ROWITEM:this.pageData.list[this.st_metCheckId].PO_ITEM_NO}).then(res => {
        if(0 === res.data.code){
          this.matList = res.data.data
        }
        for(var i in this.matList){
          this.matList[i].sort = 0
          if(this.pageData.barCodeList !== undefined){
            for(var j in this.pageData.barCodeList){
              if(this.matList[i].BARCODE === this.pageData.barCodeList[j].BARCODE){
                this.matList[i].checked = true
                this.checkQty += this.matList[i].QUANTITY
              }
            }
          }
          this.totalQty += this.matList[i].QUANTITY
        }
        this.modalVisible = false

      })
    },
    data(){
      return{
          focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
          currentIndex: 0,    // 当前聚焦元素的索引
          actionType: 'next', // 自动聚焦的行为类型
          barcode: '',
          pageData:{
            list: [],
            skInfoList: []
          },
          matList: [],
          matListCheck: [],
          checkQty: 0,
          totalQty:0,
          modalVisible: false,
          scanCount: 0
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getMatBarcodeList'
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
          this.scanCount++;
          for(var i in this.matList){
            if(this.barcode === this.matList[i].BARCODE){
              match = true
              if(this.matList[i].checked){
                this.$ons.notification.toast('此条码已扫描！', {timeout: 2000});
              }else{
                this.matList[i].checked = true
                this.matList[i].sort = this.scanCount
                this.checkQty += this.matList[i].QUANTITY

                if(this.pageData.barCodeList !== undefined){
                  this.pageData.barCodeList.push(this.matList[i])
                }
              }  
            }
          }
          if(match){
            this.matList.sort(sortMat)
          }else{
            this.$ons.notification.toast('此条码不存在于此批物料数据中！', {timeout: 2000});
          }
          this.barcode = ''
        }
      },
      checkConfirm(){
        this.pageData.list[this.st_metCheckId].RECEIPT_QTY = this.checkQty
        this.setPageData(this.pageData)
        //this.$emit('gotoPageEvent','in_receipt_scm');
        switch(this.page){
          case 'in_receipt_scm':
            this.$emit('gotoPageEvent','in_receipt_scm');
            break
          case 'in_receipt_scmBarcode':
            this.$emit('gotoPageEvent','in_receipt_scmBarcode');
            break
        }
      },
      goback(){
        //this.setPageData(this.pageData)
        //this.$emit('gotoPageEvent','in_receipt_scm');
        console.log('-->当前操作页面：' + this.page)
        switch(this.page){
          case 'in_receipt_scm':
            this.$emit('gotoPageEvent','in_receipt_scm');
            break
          case 'in_receipt_scmBarcode':
            this.$emit('gotoPageEvent','in_receipt_scmBarcode');
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