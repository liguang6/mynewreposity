<!-- 退货模块 确认过帐组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'确认过帐'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="30%" style="padding-top:10px"><center>凭证日期:</center></v-ons-col>
        <v-ons-col width="70%" ><center><datepicker id="pz_data" name="凭证日期" placeholder="凭证日期" style="width:95%" v-model="pz_data" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="30%" style="padding-top:10px"><center>记帐日期:</center></v-ons-col>
        <v-ons-col width="70%" ><center><datepicker id="jz_data" name="记帐日期" placeholder="记帐日期" style="width:95%" v-model="jz_data" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      <!--<v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>抬头文本:</center></v-ons-col>
        <v-ons-col width="75%" ><center><v-ons-input id="h_text" name="抬头文本" v-model="h_text" style="padding-top:8px;width:95%" modifier="underbar" placeholder="  抬头文本" float v-validate="'required'"></v-ons-input></center></v-ons-col>
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
      page: (state) => state.wms_return.page,
      st_pageData: (state) => state.wms_return.pageData,
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
      st_userName: (state) => state.userName,
      st_checkDataList: (state) => state.wms_return.checkDataList
    }),
    mounted () {
      console.log('-->当前过帐页面：' + this.page + ',st_userWerks : ' + this.st_userWerks + "|" + this.st_whNumber + "|" + this.st_userName)
      this.pageData = this.st_pageData
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
          list: []
        },
        pz_data: '',
        jz_data: '',
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
        'createReceiveRoomOutReturn','confirmReceiveRoomOutReturn','wareHouseOutReturnConfirm'
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
          case 'return01':
            this.$emit('gotoPageEvent','return01');
            break
          case 'return02':
            this.$emit('gotoPageEvent','return02');
            break
          case 'return161Barcode':
            this.$emit('gotoPageEvent','return161Barcode')
            break
          case 'return122Barcode':
            this.$emit('gotoPageEvent','return122Barcode')
            break
        }
      },
      confirmData(outNo){
        console.log('-->confirmData outNo : ' + outNo)
        this.confirmReceiveRoomOutReturn({
          WERKS:this.st_userWerks,
          RETURN_NO:outNo,
          PZ_DATE:this.pz_data,
          JZ_DATE:this.jz_data,
          USERNAME:this.st_userName,
          ARRLIST:JSON.stringify(this.pageData.list)
        }).then(res => {
          if(0 === res.data.code){
            this.msg += "退货成功 " +res.data.result + ";"
          }else{
            this.msg += "退货确认失败： " +res.data.msg
          }
        })
      },
      createReturn(){
        this.createReceiveRoomOutReturn({
          WERKS:this.st_userWerks,
          WH_NUMBER:this.st_whNumber,
          BUSINESS_NAME:this.pageData.BUSINESS_NAME,
          USERNAME:this.st_userName,
          ARRLIST:JSON.stringify(this.pageData.list)
        }).then(res => {
          this.msg = res.data.msg
          if(0 === res.data.code){
            //创建退货单后确认退货 outNo: "1RT0167;"
            this.msg += "创建退货单成功，退货单号：" +res.data.outNo
            var arr = res.data.outNo.split(";")
            var that = this
            arr.forEach(function(value,i){
              console.log('-->outNo : ' + value);
              if('' != value){
                that.confirmData(value)
              }
            })

          }
          this.modalVisible = false
        })
      },
      confirm(){
        this.modalVisible = true
        this.confirmed = true

        let matList = []
        console.log('-->this.st_checkDataList.length : ' + this.st_checkDataList.length);
        for(var i = 0;i<this.st_checkDataList.length;i++){
          matList[i] = JSON.stringify(this.pageData.list[this.st_checkDataList[i]])
        }
        console.log('-->matList = ' + matList.join(","));

        switch(this.page){
          case 'return01':
            this.createReturn()
            break
          case 'return02':
            this.createReturn()
            break
          case 'return161Barcode':
            console.log('return161Barcode BUSINESS_TYPE : ' + this.pageData.BUSINESS_TYPE)
            this.wareHouseOutReturnConfirm({
              WERKS:this.st_userWerks,
              WH_NUMBER:this.st_whNumber,
              BUSINESS_TYPE:this.pageData.BUSINESS_TYPE,
              BUSINESS_NAME:this.pageData.BUSINESS_NAME,
              USERNAME:this.st_userName,
              ARRLIST:'[' + matList.join(",") + ']',
              PZ_DATE:this.pz_data,
              JZ_DATE:this.jz_data,
            }).then(res => {
              if(0 === res.data.code){
                this.msg += res.data.msg
              }else{
                this.msg += res.data.msg
              }
            })
            this.modalVisible = false
            break
        }
        this.modalVisible = false
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