<!-- 库内模块 冻结、解冻 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'库存解冻'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="45%" ><center><v-ons-input id="assno" autocomplete="off" @keypress="ScanAssno" autofocus=“true” name="标签号" :class="{'input': true, 'is-danger': errors.has('标签号') }" v-model="labelNo" width="95%" modifier="underbar" placeholder="标签号" float v-validate="'required'"></v-ons-input></center></v-ons-col>
        <v-ons-col width="5%" ></v-ons-col>
        <v-ons-col width="50%" >
          <v-ons-button @click="search" style="width:70px;padding: 0 5px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>&nbsp;
          <v-ons-button @click="reset" style="width:70px;padding: 0 5px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageData.list.length === 0">请输入标签号查询...<br/></p>
      <!--v-ons-row>
          <v-ons-col width="6%" style="padding-top:6px" ></v-ons-col>
          <v-ons-col width="50%" style="padding-top:6px" >标签号</v-ons-col>
          <v-ons-col width="38%" style="padding-top:6px" >物料号</v-ons-col>
           <v-ons-col width="6%" style="padding-top:6px" >数量</v-ons-col>
        </v-ons-row-->
      <v-ons-list>
        
        <v-ons-row v-for="(li, $index) in pageData.list" :key="li.LABEL_NO">
          <v-ons-col width="5%" style="padding-top:15px">
            <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="checkDataList" >
            </v-ons-checkbox>
          </v-ons-col>
          <v-ons-col width="95%" >
              <v-ons-list-item expandable >
                <v-ons-row>
                 <v-ons-col width="50%" style="padding-top:6px" >{{li.LABEL_NO}}</v-ons-col>
                  <v-ons-col width="38%" style="padding-top:6px" >{{li.MATNR}}</v-ons-col>
                   <v-ons-col width="7%" style="padding-top:6px;text-align:right;color:red" >{{li.BOX_QTY}}</v-ons-col>
                </v-ons-row>
                <div class="expandable-content">工厂/仓库号:{{li.WERKS}}/{{li.WH_NUMBER}}<br/>物料描述:{{li.MAKTX}} <br/>
                批次:{{li.BATCH}}<br/>库位:{{li.LGORT}}特殊库存类型:{{li.SOBKZ}}<br/>
                储位:{{li.BIN_CODE}}/{{li.BIN_NAME}} ;单位:{{li.MEINS}} <br/>
                供应商:{{li.LIFNR}}/{{li.LIKTX}}  <br/>
                </div>
              </v-ons-list-item>
          </v-ons-col>
        </v-ons-row>

      </v-ons-list>

    </v-ons-card>

    <ons-bottom-toolbar>
          
      <center>
        <v-ons-checkbox @click="checkall" v-model="ischeck" style="padding-top:14px">
        </v-ons-checkbox>&nbsp;全选&nbsp;&nbsp;
        <v-ons-button @click="confirm" style="margin: 2px 0"><v-ons-icon icon="fa-cogs"></v-ons-icon>&nbsp;解冻</v-ons-button>
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
      st_labelNo: (state) => state.wms_kn.labelNo,
      st_pageData: (state) => state.wms_kn.pageData,
      st_checkDataList: (state) => state.wms_kn.checkDataList,
    }),
    mounted () {
      this.setPage('freeze')
      this.labelNo = this.st_labelNo
      if(undefined === this.st_pageData.skInfoList){
        this.pageData = {
          list: [],
        }
      }else{
        this.pageData = this.st_pageData
      }
      //this.list = this.st_pageDataList
      this.checkDataList = this.st_checkDataList
    },
    data(){
      return{
        checkDataList :[],
        labelNo:'',
        pageData:{
          list: [],
        },
        msg: '',
        isExpanded :false,
        modalVisible: false,
        ischeck: false
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
        'getDataByLabelNo','freeze'
      ]),
      ...mapMutations([
        'setPage','setLabelNo','setPageData','setCheckDataList','setMetCheckId','setMsg'
      ]),
      search(){
        this.setLabelNo(this.labelNo)
        console.log('-->search page: ' + this.page + ";labelNo:" + this.labelNo)
        this.$validator.localize('zh_CN');
        this.$validator.validateAll().then(result => {
        console.log('-->result: '  + result)
          if (!result) {
            this.$ons.notification.toast(((this.$validator.errors.has('SCM送货单'))?this.$validator.errors.first('SCM送货单'):""), {timeout: 2000});
            return false;
          }else{
            this.modalVisible = true;
            this.getDataByLabelNo({labelNo: this.labelNo}).then(res => {
            console.log('-->search start: '  + this.labelNo)
              if(0 === res.data.code){
                if(res.data.labelData.LABEL_STATUS=='11'){
                   this.pageData.list.push( res.data.labelData);
                }else{
                   this.$ons.notification.toast('标签状态不是已冻结,不能解冻', {timeout: 2000});
                }
                this.labelNo=''
                this.msg = ''
                
              }else{
                this.pageData.list = [],
                this.msg = res.data.msg,
                this.$ons.notification.toast(res.data.msg, {timeout: 2000});
              }
              this.modalVisible = false;
            })
          }
        })
        },
        confirm(){
          if(0===this.checkDataList.length){
            this.$ons.notification.toast('请选择需要解冻的数据', {timeout: 2000});
          }else{
            console.log('veu checkDataList = ' + this.checkDataList+"-->pageDataList="+this.pageData)
            this.setCheckDataList(this.checkDataList)
            let saveData=[]
            let indexArr=this.checkDataList.toString().split(',')
            for(let i=0;i<indexArr.length;i++){
            saveData[i] =this.pageData.list[indexArr[i]]
            }
             this.freeze({checkDataList:saveData,type:'01'}).then(res => {
            console.log('-->freeze start: '  + saveData)
              if(0 === res.data.code){
                this.msg = res.data.msg
                console.log('this.msg',this.msg);
                 this.reset();
                this.$ons.notification.toast(res.data.msg, {timeout: 2000});
              }else{
                this.msg = res.data.msg,
                this.$ons.notification.toast(res.data.msg, {timeout: 2000});
              }
              this.modalVisible = false;
            })
          }
        },
        reset(){
          this.pageData.list = []
          this.pageData.skInfoList = []
          this.checkDataList = []
          this.setPageData(this.pageData)
          this.setCheckDataList(this.checkDataList)
          this.setPage('in_receipt_scm')
        },
        barcodeCheck(){
          this.setPageData(this.pageData)
          this.setCheckDataList(this.checkDataList)
          this.$emit('gotoPageEvent','in_barcodeCheck')
        },
        matCheck(id){
          console.log('-->matCheck : id = ' + id)
          this.setPageData(this.pageData)
          this.setMetCheckId(id)
          this.$emit('gotoPageEvent','in_matCheck')
        },
        ScanAssno(){
          if(13 === window.event.keyCode){
            this.search()
          }
        },
        checkall(){
          if(this.ischeck){
            this.ischeck = false
          }else{
            this.ischeck = true
          }
          let temp = []
          for(var i = 0;i<this.pageData.list.length;i++){
            temp[i] = i + '';
             //temp[i] =this.pageData.list[i];
          }
          this.checkDataList = (this.ischeck)?temp:[]
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
</style>