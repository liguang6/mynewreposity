<!-- 库内 盘点 -->
<template>
  <v-ons-page>
    <custom-toolbar v-on:gohome="gohome" :title="'库存盘点'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
      <v-ons-row>
        <v-ons-col width="15%" style="text-align:right"><ons-icon @click="setFocus('jump',0)" style="color: green;padding-top:5px" size="25px" icon="fa-barcode"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="85%">
          <input @focus="setFocusIndex(0)" v-bind:disabled="pageStoreData.noCheck" autocomplete="off" v-model="pageStoreData.stocktakingNo" style="width:60%" data-index="0" @keypress="ScanStocktakingNo" placeholder="请输入盘点任务号">
          &nbsp;&nbsp;<v-ons-select style="width: 60px" v-model="pageStoreData.type">
              <option v-for="(value, key) in typeList" :value="key" :name="key" v-bind:key="key">
                {{ value }}
              </option>
          </v-ons-select>
        </v-ons-col>

        <v-ons-col width="15%" style="text-align:right"><ons-icon @click="setFocus('jump',1)" style="color: green;padding-top:5px" size="25px" icon="fa-box"></ons-icon>&nbsp;</v-ons-col>
        <v-ons-col width="85%">
          <input @focus="setFocusIndex(1)" autocomplete="off" v-model="pageStoreData.barCode" style="width:60%" data-index="1" @keypress="ScanBarCode" placeholder="请扫描包装箱条码">
          &nbsp;<v-ons-button @click="reset" style="width:60px;padding: 0 3px;background-color:grey" modifier="material"><v-ons-icon icon="fa-refresh"></v-ons-icon>&nbsp;重置</v-ons-button>
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="pageStoreData.list.length === 0">请扫描或输入盘点任务号...<br/>{{msg}}</p>
      <v-ons-list> 
        <v-ons-row v-for="(li, $index) in pageStoreData.list" :key="li.id">
          <v-ons-col width="6%" style="padding-top:15px">
            <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="pageStoreData.checkDataList" >
            </v-ons-checkbox>
          </v-ons-col>
          <v-ons-col width="94%" >
            <v-ons-list-item expandable >
              <v-ons-row>
                <v-ons-col width="5%" style="padding-top:6px" >{{$index+1}}</v-ons-col>
                <v-ons-col width="65%" style="padding-top:6px" >料号:{{li.MATNR}}</v-ons-col>
                <v-ons-col width="20%" style="padding-top:6px" >{{li.STOCK_QTY}}</v-ons-col>
                <v-ons-col width="10%" ><ons-input id="INVENTORY_QTY" name="INVENTORY_QTY" v-model="pageStoreData.list[$index].INVENTORY_QTY" :value="li.INVENTORY_QTY?li.INVENTORY_QTY:0" @change="changeqty($index,$event)" modifier="underbar" float></ons-input></v-ons-col>
              </v-ons-row>
              <div class="expandable-content">
                工厂/仓库号：{{li.WERKS}}/{{li.WH_NUMBER}}<br/>
                物料号：{{li.MATNR}}<br/>
                物料描述：{{li.MAKTX}}<br/>
                供应商：{{li.LIFNR}} {{li.LIKTX}}<br/>
                库位：{{li.LGORT}}<br/>
                单位：{{li.MEINS}}<br/>
                </div>
            </v-ons-list-item>
          </v-ons-col>
        </v-ons-row>
      </v-ons-list>
      
    </v-ons-card>

    <ons-bottom-toolbar>
      
      <center>
        <v-ons-checkbox @click="checkall" v-model="ischeck" style="padding-top:14px;padding-left:14px"></v-ons-checkbox>&nbsp;全选
        <v-ons-button @click="saveStocktakingResult" style="margin: 2px 0"><v-ons-icon icon="fa-check"></v-ons-icon>&nbsp;确认</v-ons-button>
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
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
      st_pageStoreData: (state) => state.pageStoreData,
    }),
    mounted () {
      this.setPage('inwh_stocktaking')      
      if(undefined === this.st_pageStoreData.barCodeInfo){
        this.pageStoreData = {
          stocktakingNo:'',
          barCode:'',
          list: [],
          barCodeInfo:[],
          type:'00',
          noCheck:false,
          checkDataList:[]
        }
      }else{
        this.pageStoreData = this.st_pageStoreData
      }
      this.barCode = this.st_barCode
      
    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        pageStoreData:{
          stocktakingNo:'',
          barCode:'',
          list: [],
          barCodeInfo:[],
          type:'00',
          noCheck:false,
          checkDataList:[]
        },
        msg: '',
        modalVisible: false,
        typeList: {
          '00': '初盘',
          '01': '复盘'
        },
        ischeck: false,
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'getInventoryResult','getLabelInfoByNo','saveResult'
      ]),
      ...mapMutations([
        'setPage','setPageStoreData',
      ]),
      gohome(){
        this.$emit('gotoPageEvent','home')
      },
      ScanStocktakingNo(e){
        if(13 === window.event.keyCode){
          this.modalVisible = true
          if('' === this.pageStoreData.stocktakingNo){
            this.$ons.notification.toast('请扫描或输入盘点任务号！', {timeout: 2000});
            this.modalVisible = false
            return false
          }else{
            this.getInventoryResult({
              WERKS:this.st_userWerks,
              WH_NUMBER:this.st_whNumber,
              INVENTORY_NO:this.pageStoreData.stocktakingNo,
              TYPE:this.pageStoreData.type
              }).then(res => {
              if(0 === res.data.code){
                this.pageStoreData.noCheck = true
                this.pageStoreData.list = res.data.map.list
                for(var i in this.pageStoreData.list){
                  if(this.pageStoreData.list[i].INVENTORY_QTY == null){
                    this.pageStoreData.list[i].INVENTORY_QTY = 0
                  }
                }
              }else{
                this.$ons.notification.toast(res.data.msg, {timeout: 2000});
              }
              this.modalVisible = false
            })
          }
        }
      },
      ScanBarCode(){
        if(13 === window.event.keyCode){
          this.modalVisible = true
          if('' === this.pageStoreData.barCode){
            this.$ons.notification.toast('请扫描条码！', {timeout: 2000});
            this.modalVisible = false
            return false
          }
          if(this.pageStoreData.noCheck == false){
            this.$ons.notification.toast('请先扫描或输入盘点任务号！', {timeout: 2000});
            this.modalVisible = false
            this.pageStoreData.barCode = ''
            this.setFocus('jump',0)
            return false
          }
          let barCodeStr = this.pageStoreData.barCode
          if(barCodeStr.indexOf("PN:")>0){
            this.pageStoreData.barCode = barCodeStr.substring(barCodeStr.indexOf("PN:")).substring(3,barCodeStr.substring(barCodeStr.indexOf("PN:")).indexOf(";"))
          }else if(barCodeStr.indexOf("S:")>0){
            this.pageStoreData.barCode = barCodeStr.substring(barCodeStr.indexOf("S:")).substring(2,barCodeStr.substring(barCodeStr.indexOf("S:")).indexOf(";"))
          }else{
            this.$ons.notification.toast('请扫描正确的条码！', {timeout: 2000});
            this.modalVisible = false
            this.pageStoreData.barCode = ''
            return false
          }
          
          if(this.pageStoreData.barCodeInfo.indexOf(this.pageStoreData.barCode) >= 0){
            this.$ons.notification.toast('不能扫重复的条码，请重新扫描！', {timeout: 2000});
            this.pageStoreData.barCode = ''
            this.modalVisible = false
            return false
          }

          this.getLabelInfoByNo({LABEL_NO:this.pageStoreData.barCode}).then(res => {
            if(0 === res.data.code && res.data.result.length > 0){
              this.pageStoreData.barCodeInfo.push(this.pageStoreData.barCode)
              for(var i in this.pageStoreData.list){
                if(res.data.result[0].MATNR === this.pageStoreData.list[i].MATNR && res.data.result[0].LGORT === this.pageStoreData.list[i].LGORT){
                  this.pageStoreData.list[i].INVENTORY_QTY += res.data.result[0].BOX_QTY
                }
              }
            }else{
              this.$ons.notification.toast('条码查询失败，请重新扫描！', {timeout: 2000});
            }
            this.pageStoreData.barCode = ''
            this.modalVisible = false
          })
        }
      },
      saveStocktakingResult(){
        if(0===this.pageStoreData.checkDataList.length){
            this.$ons.notification.toast('当前没有选择任何行项目！', {timeout: 2000});
          return false;
        }
        let save_data = []
        for(var i in this.pageStoreData.checkDataList){
          save_data.push(this.pageStoreData.list[this.pageStoreData.checkDataList[i]])
        }
        
        console.log('save_data:',save_data)
        this.saveResult({
          SAVE_DATA:JSON.stringify(save_data),
          TYPE:this.pageStoreData.type,
          INVENTORY_NO:this.pageStoreData.stocktakingNo
        }).then(res => {
          if(0 === res.data.code){
            this.$ons.notification.toast('操作成功！', {timeout: 2000});
            this.reset()
          }else{
            this.msg += "失败： " +res.data.msg
          }
        })
      },
      checkall(){
        if(this.ischeck){
          this.ischeck = false
        }else{
          this.ischeck = true
        }
        var temp = []
        for(var i = 0;i<this.pageStoreData.list.length;i++){
          temp[i] = i + ''
        }
        this.pageStoreData.checkDataList = (this.ischeck)?temp:[]
      },
      reset(){
        this.pageStoreData = {
          stocktakingNo:'',
          barCode:'',
          list: [],
          barCodeInfo:[],
          type:'00',
          noCheck:false,
          checkDataList:[]
        }
        this.msg = ''
        this.ischeck = false,
        this.setFocus('jump',0)
        this.setPage('inwh_stocktaking')
      },
      changeqty(index,event){
        console.log(index + '|' + event.currentTarget.value)
        this.pageStoreData.list[index].INVENTORY_QTY = event.currentTarget.value
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
