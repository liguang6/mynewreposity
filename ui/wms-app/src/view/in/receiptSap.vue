<!-- 入库模块 收料房收货 SAP采购订单收料组件 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'SAP采购订单收料'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="45%" ><center><v-ons-input id="assno" autofocus=“true” name="SAP采购订单" :class="{'input': true, 'is-danger': errors.has('SAP采购订单') }" v-model="assno" width="95%" modifier="underbar" placeholder="SAP采购订单" float v-validate="'required'"></v-ons-input></center></v-ons-col>
        <v-ons-col width="5%" ></v-ons-col>
        <v-ons-col width="50%" >
          <v-ons-button @click="search" style="width:75px;padding: 0 5px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>&nbsp;
          <v-ons-button @click="reset" style="width:75px;padding: 0 5px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-checkbox @click="checkall" v-model="ischeck" style="padding-top:14px">
        </v-ons-checkbox>&nbsp;全选&nbsp;&nbsp;
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
      st_assno: (state) => state.wms_in.assno,
      st_pageDataList: (state) => state.wms_in.pageDataList,
      st_checkDataList: (state) => state.wms_in.checkDataList,
    }),
    mounted () {
      this.setPage('in_receipt_sap')
      this.assno = this.st_assno
      this.list = this.st_pageDataList
      this.checkDataList = this.st_checkDataList
      console.log('-->mounted page: ' + this.page + ";st_assno:" + this.st_assno)
    },
    data(){
      return{
        checkDataList :[],
        assno :'',
        list: [],
        msg: '',
        isExpanded :false,
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
        'setPage','setAssno','setPageDataList','setCheckDataList','setMetCheckId'
      ]),
      checkall(){
        this.ischeck = !this.ischeck
        let temp = []
        for(var i = 0;i<this.list.length;i++){
          temp[i] = i + ''
        }
        this.checkDataList = (this.ischeck)?temp:[]
      },
      search(){

      },
      reset(){
        this.assno = ''
        this.list = []
        this.checkDataList = []
        this.setAssno(this.assno)
        this.setPageDataList(this.list)
        this.setCheckDataList(this.checkDataList)
      },
      
    }
  }
</script>