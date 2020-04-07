<!-- 入库模块 确认过帐组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'确认过帐'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="28%" style="padding-top:10px"><center>凭证日期:</center></v-ons-col>
        <v-ons-col width="72%" ><center><datepicker id="pz_data" name="凭证日期" placeholder="凭证日期" style="width:95%" v-model="pz_data" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="28%" style="padding-top:10px"><center>记帐日期:</center></v-ons-col>
        <v-ons-col width="72%" ><center><datepicker id="jz_data" name="记帐日期" placeholder="记帐日期" style="width:95%" v-model="jz_data" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      <!--<v-ons-row>
        <v-ons-col width="28%" style="padding-top:10px"><center>抬头文本:</center></v-ons-col>
        <v-ons-col width="72%" ><center><v-ons-input id="h_text" name="抬头文本" v-model="h_text" style="padding-top:8px;width:95%" modifier="underbar" placeholder="  抬头文本" float v-validate="'required'"></v-ons-input></center></v-ons-col>
      </v-ons-row>-->

    </v-ons-card>
    <v-ons-card>
      <p v-if="msg != ''">{{msg}}</p>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button @click="goback" style="margin: 6px 0"><v-ons-icon icon="fa-reply"></v-ons-icon>&nbsp;返回</v-ons-button> &nbsp;&nbsp;
        <v-ons-button v-bind:disabled="confirmed" @click="confirm" style="margin: 6px 0"><v-ons-icon icon="fa-check"></v-ons-icon>&nbsp;确认</v-ons-button>
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
  import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";
  import { mapActions, mapMutations,mapState } from 'vuex'
  export default {
    computed: mapState({
      page: (state) => state.wms_in.page,
      st_pageData: (state) => state.wms_in.pageData,
      st_assno: (state) => state.wms_in.assno,
      st_checkDataList: (state) => state.wms_in.checkDataList
    }),
    mounted () {
      console.log('-->当前过帐页面：' + this.page + ',st_assno : ' + this.st_assno)
      this.pageData = this.st_pageData
      if(this.page === 'in_receipt_scm'){
        this.h_text = this.st_assno
      }
      var date = new Date();
      var seperator1 = "-";
      var year = date.getFullYear();
      var month = date.getMonth() + 1;
      var strDate = date.getDate();
      if (month >= 1 && month <= 9) {
        month = "0" + month;
      }
      if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
      }
      this.pz_data = year + seperator1 + month + seperator1 + strDate;
      this.jz_data = year + seperator1 + month + seperator1 + strDate;
    },
    data(){
      return{
        pageData:{
          list: [],
          skInfoList: []
        },
        pz_data: '',
        jz_data: '',
        h_text: '',
        modalVisible: false,
        msg:'',
        confirmed:false,
        format: "yyyy-MM-dd"
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar,Datepicker },
    methods: {
      ...mapActions([
        'boundIn'
      ]),
      ...mapMutations([
        'setPage'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      goback(){
        //确认过帐为公共组件，根据page返回对应的上一页
        switch(this.page){
          case 'in_receipt_scm':
            this.$emit('gotoPageEvent','in_receipt_scm');
            break
          case 'in_receipt_scmBarcode':
            this.$emit('gotoPageEvent','in_receipt_scmBarcode');
            break
          case 'in_receipt_cloudBarcode':
            this.$emit('gotoPageEvent','in_receipt_cloudBarcode');
            break
          case 'in_receipt_stoDelivery':
            this.$emit('gotoPageEvent','in_receipt_stoDelivery');
            break
          case 'in_receipt_305Barcode':
            this.$emit('gotoPageEvent','in_receipt_305Barcode');
            break
          case 'in_receipt_305Sap' :
            this.$emit('gotoPageEvent','in_receipt_305Sap');
            break;
          case 'ShelfViewScannerDataTable':
            this.$emit('gotoPageEvent','ShelfViewScannerDataTable');
            break;
          case 'ShelfViewRecommendEnd' :
            this.$emit('gotoPageEvent','ShelfViewRecommendEnd');
            break;
          case 'ShelfUBTransferOrderDataTable' :
            this.$emit('gotoPageEvent','ShelfUBTransferOrderDataTable');
            break;
        }

      },
      confirm(){
        console.log('-->page : ' + this.page + ',st_assno : ' + this.st_assno);
        //console.log('-->this.pageData.skInfoList : ' , JSON.stringify(this.pageData.skInfoList));
        this.modalVisible = true
        let matList = []
        let skInfoListScaned = []
        let barCodeListScaned = []
        for(var i = 0;i<this.st_checkDataList.length;i++){
          let PONO = this.pageData.list[this.st_checkDataList[i]].PONO
          let POITM = this.pageData.list[this.st_checkDataList[i]].POITM
          console.log(PONO + '|' + POITM)
          for(var j = 0;j<this.pageData.list.length;j++){
            if(PONO === this.pageData.list[j].PONO && POITM === this.pageData.list[j].ROWITEM){
              barCodeListScaned.push(this.pageData.list[j])
            }
          }
          matList[i] = JSON.stringify(this.pageData.list[this.st_checkDataList[i]])
        }
        console.log('-->barCodeListScaned = ' , barCodeListScaned);
        //SCM条码收货页面 skInfoList 只保留本次扫描的条码
        if('in_receipt_scmBarcode' === this.page){
          for(var i = 0;i<this.pageData.skInfoList.length;i++){
          //for(var i in this.pageData.skInfoList){
            let SKUID = this.pageData.skInfoList[i].SKUID
            //console.log(i+'|SKUID:'+SKUID+'|length=' + this.pageData.skInfoList.length)
            for(var j = 0;j<barCodeListScaned.length;j++){
              if(SKUID === barCodeListScaned[j].BARCODE){
                //console.log(SKUID)
                skInfoListScaned.push(this.pageData.skInfoList[i])
              }
            }
          }
        }
        if('in_receipt_scmBarcode' === this.page){
          this.pageData.skInfoList = skInfoListScaned
        }
        console.log('-->skInfoList = ' , this.pageData.skInfoList);
        
        this.boundIn({
          matList:'[' + matList.join(",") + ']',
          allDataList:this.pageData.list,
          skList:JSON.stringify(this.pageData.skInfoList),
          WERKS:this.pageData.list[0].WERKS,
          BUSINESS_NAME:this.pageData.BUSINESS_NAME,
          ASNNO:this.st_assno,
          PZ_DATE:this.pz_data,
          JZ_DATE:this.jz_data
        }).then(res => {
          this.msg = res.data.msg
          if(0 === res.data.code){
            this.confirmed = true
          }
          this.modalVisible = false
        })
      }
    }
  }
</script>
<style>
.is-danger{
  border-style: dashed;
  border-width: 1px;
  border-color: crimson
}

input,
select {
  padding: 0.75em 0.5em;
  border:none;
  font-size: 100%;
  border-bottom: 1px solid #ccc;
  width: 100%;
}
</style>
