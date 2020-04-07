<!-- 入库模块 确认过帐组件 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'确认过帐'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>凭证日期:</center></v-ons-col>
        <v-ons-col width="75%" ><center><datepicker id="pz_data" name="凭证日期" placeholder="凭证日期" style="width:95%" v-model="pz_data" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>记帐日期:</center></v-ons-col>
        <v-ons-col width="75%" ><center><datepicker id="jz_data" name="记帐日期" placeholder="记帐日期" style="width:95%" v-model="jz_data" :format="format"></datepicker></center></v-ons-col>
      </v-ons-row>
      

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
      page: (state) => state.wms_out.page,
      st_pageData: (state) => state.wms_out.pageData,
      st_assno: (state) => state.wms_out.assno,
      st_checkDataList: (state) => state.wms_out.checkDataList
    }),
    mounted () {
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
        'dispatchingHandover'
      ]),
      ...mapMutations([
        'setPage'
      ]),
      goback(){
        //确认过帐为公共组件，根据page返回对应的上一页
        this.$emit('gotoPageEvent','dispatching_handover')

      },
      confirm(){
        console.log('-->this.st_checkDataList : ' , this.st_checkDataList);
        this.modalVisible = true
        let arrayList = []
        for(var i = 0;i<this.st_checkDataList.length;i++){
          arrayList[i] = JSON.stringify(this.pageData.list[this.st_checkDataList[i]])
        }
        
        this.dispatchingHandover({
          ARRLIST:arrayList,
          PZDDT:this.pz_data,
          JZDDT:this.jz_data
        }).then(res => {
          this.msg = res.data.msg
          if(0 === res.data.code){
            this.confirmed = true;
            this.msg=this.msg+res.data.jkinfo+res.data.retsap;
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
