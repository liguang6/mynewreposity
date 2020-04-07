<!-- 退货模块 数据表 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'数据表'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <p v-if="pageData.list.length === 0">当前没有任何数据</p>
      <v-ons-list>
        
        <v-ons-row v-for="(li, $index) in pageData.list" :key="li.id">
        <v-ons-col width="6%" style="padding-top:15px">
          <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="checkDataList" >
          </v-ons-checkbox>
        </v-ons-col>
        <v-ons-col width="94%" >
            <v-ons-list-item expandable >
              <v-ons-row>
                <v-ons-col width="5%" style="padding-top:6px" >{{$index+1}}</v-ons-col>
                <v-ons-col width="65%" style="padding-top:6px" >料号:{{li.MATNR}}</v-ons-col>
                <v-ons-col width="20%" style="padding-top:6px" >数量:</v-ons-col>
                <v-ons-col width="10%" ><ons-input id="re_date" readonly="true" :value="li.QTY1" modifier="underbar" float></ons-input></v-ons-col>
              </v-ons-row>
              <div class="expandable-content">
                工厂/仓库号：{{li.WERKS}}/{{li.WH_NUMBER}}<br/>
                物料号：{{li.MATNR}}<br/>
                物料描述：{{li.MAKTX}}<br/>
                供应商：{{li.LIFNR}} {{li.LIKTX}}<br/>
                PO：{{li.PO_NO}}<br/>
                库位：{{li.LGORT}}<br/>
                单位：{{li.MEINS}}<br/>
                </div>
            </v-ons-list-item>
        </v-ons-col>
        </v-ons-row>

      </v-ons-list>
    </v-ons-card>
    
    <v-ons-dialog :visible.sync="hasResult">
      <p style="text-align: center">{{msg}}</p>
      <center>
        <v-ons-button @click="goback" style="margin: 2px 0"><v-ons-icon icon="fa-reply"></v-ons-icon>&nbsp;返回</v-ons-button>
      </center>
    </v-ons-dialog>

    <ons-bottom-toolbar>
      <center>
        <v-ons-checkbox @click="checkall" v-model="ischeck" style="padding-top:14px">
        </v-ons-checkbox>&nbsp;全选&nbsp;&nbsp;
        <!--<v-ons-button @click="del" style="margin: 2px 0"><v-ons-icon icon="fa-remove"></v-ons-icon>&nbsp;删除</v-ons-button> &nbsp;-->
        <v-ons-button @click="goback" style="margin: 2px 0"><v-ons-icon icon="fa-reply"></v-ons-icon>&nbsp;返回</v-ons-button> &nbsp;
        <v-ons-button @click="confirm" v-bind:disabled="isConfirm" style="margin: 2px 0"><v-ons-icon icon="fa-cogs"></v-ons-icon>&nbsp;{{confirmTxt}}</v-ons-button>
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
    }),
    mounted () {
      if(undefined === this.st_pageData.barCodeInfo){
        this.pageData = {
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'25'
        }
      }else{
        console.log("-->st_pageData length = ",this.st_pageData.barCodeInfo)
        this.pageData = this.st_pageData
      }
      if(this.page === 'return161Barcode'){
        for(var i in this.pageData.barCodeList){
          let MATNR = this.pageData.barCodeList[i].MATNR
          let LIFNR = this.pageData.barCodeList[i].LIFNR
          let SOBKZ = this.pageData.barCodeList[i].SOBKZ
          let BOX_QTY = this.pageData.barCodeList[i].BOX_QTY
          console.log(MATNR + "|" + LIFNR + "|" + SOBKZ)
          for(var j in this.pageData.list){
            if(MATNR === this.pageData.list[j].MATNR && LIFNR === this.pageData.list[j].LIFNR 
            && SOBKZ === this.pageData.list[j].SOBKZ){
              if(BOX_QTY > 0){
                console.log('FREEZE_QTY = ' + this.pageData.list[j].FREEZE_QTY)
                if(this.pageData.list[j].FREEZE_QTY - this.pageData.list[j].QTY1 >0){
                  if(BOX_QTY > (this.pageData.list[j].FREEZE_QTY - this.pageData.list[j].QTY1)){
                    this.pageData.list[j].QTY1 = this.pageData.list[j].FREEZE_QTY
                    BOX_QTY = BOX_QTY - (this.pageData.list[j].FREEZE_QTY - this.pageData.list[j].QTY1)
                  }else{
                    this.pageData.list[j].QTY1 = BOX_QTY
                    BOX_QTY = 0
                  }
                }
              }
            }
          }
        }
      }else{
        for(var i in this.pageData.list){
          this.pageData.list[i].QTY1 = this.pageData.list[i].QTY2
        }
      }
      console.log('list:',this.pageData.list)

      this.barCode = this.st_barCode
    },
    data(){
      return{
        barCode :'',
        msg: '',
        pageData:{
          list: [],
          barCodeList:[],
          barCodeInfo: [],
          BUSINESS_NAME:'25'
        },
        checkDataList:[],
        isConfirm :false,
        modalVisible: false,
        ischeck: false,
        hasResult:false,
        confirmTxt:'创建'
      }
    },
    watch:{
      checkDataList(curVal,oldVal){
        this.ischeck = (curVal.length === this.pageData.list.length&&this.pageData.list.length>0)?true:false
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([

      ]),
      ...mapMutations([
        'setPage','setPageData','setCheckDataList','setMetCheckId'
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      checkall(){
        if(this.ischeck){
          this.ischeck = false
        }else{
          this.ischeck = true
        }
        var temp = []
        for(var i = 0;i<this.pageData.list.length;i++){
          temp[i] = i + ''
        }
        this.checkDataList = (this.ischeck)?temp:[]
      },
      confirm(){
        if(0===this.checkDataList.length){
            this.$ons.notification.toast('当前没有选择任何行项目！', {timeout: 2000});
          return false;
        }
        switch(this.page){
          case 'return01':
            console.log('-->this.pageData.list : ' ,this.pageData.list)
            this.$emit('gotoPageEvent','re_confirm')
            break
          case 'return161Barcode':
            console.log('-->this.pageData.list : ' ,this.pageData.list)
            this.setPageData(this.pageData)
            this.setCheckDataList(this.checkDataList)
            this.$emit('gotoPageEvent','re_confirm')
            break
          default:
            this.$emit('gotoPageEvent','re_confirm')
        }
        
      },
      matCheck(id){
        this.setPageData(this.pageData)
        this.setMetCheckId(id)
        this.$emit('gotoPageEvent','re_matCheck')
      },
      del(){

      },
      goback(){
        console.log('---->当前操作页面：' + this.page)
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
      
    }
  }
</script>