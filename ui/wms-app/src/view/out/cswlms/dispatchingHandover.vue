<!-- 出库模块 长沙总装需求交接 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'总装需求交接'" :action="toggleMenu"></custom-toolbar>
    
    <v-ons-card v-auto-focus="focusCtrl" :data-current="currentIndex" :data-action="actionType">
      <v-ons-row>
        <v-ons-col width="80%">
          <input @focus="setFocusIndex(0)" autocomplete="off" v-model="barCode" style="width:60%" data-index="0" @keypress="ScanBarcode" placeholder="单号">
          &nbsp;<v-ons-button @click="search" style="width:65px;padding: 0 5px;" modifier="material"><v-ons-icon icon="fa-search"></v-ons-icon>&nbsp;查询</v-ons-button>
        </v-ons-col>
        <v-ons-col width="10%">
        <v-ons-fab style="background-color: #FF8C00;border: 1px solid #FF8C00;border-radius: 4px;color: black">
          <span>{{cntCount}}</span>
        </v-ons-fab>
        </v-ons-col>
      </v-ons-row>
      <v-ons-row>
        <v-ons-col width="10%">
          <v-ons-button @click="checkall">
            全选
          </v-ons-button>
        </v-ons-col>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <v-ons-col width="10%">
          <v-ons-button @click="confirm">
            确认
          </v-ons-button>
        </v-ons-col>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <v-ons-col width="10%">
          <v-ons-button @click="reset">
            重置
          </v-ons-button>
        </v-ons-col>
      </v-ons-row>
      
    </v-ons-card>

    <v-ons-card>
      
      <v-ons-list>         
          <v-ons-row v-for="(li, $index) in pageData.list" :key="li.BARCODE">
            <v-ons-col width="6%" style="padding-top:15px">
              <v-ons-checkbox :input-id="'checkbox-' + $index" :value='$index' v-model="checkDataList" >
              </v-ons-checkbox>
            </v-ons-col>
            <v-ons-col width="94%" >
                
                  <v-ons-row>
                    <v-ons-col width="45%" style="padding-top:6px" >{{li.BARCODE}}</v-ons-col>
                    <v-ons-col width="40%" style="padding-top:6px" >{{li.MATERIAL_CODE}}</v-ons-col>
                    <v-ons-col width="100%" style="padding-top:6px" >{{li.MATERIAL_DESC}}</v-ons-col>
                    <v-ons-col width="15%" style="padding-top:6px" >{{li.QUANTITY}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px" >{{li.UNIT}}</v-ons-col>
                    <v-ons-col width="10%" style="padding-top:6px" >{{li.LINE_CATEGORY}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px" >{{li.LEFT_HANDOVER_TIMES}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px" >{{li.FROM_PLANT_CODE}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px;display:none" >{{li.XJ_QTY}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px;display:none" >{{li.LGORT}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px;display:none" >{{li.DISPATCHING_NO}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px;display:none" >{{li.ITEM_NO}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px;display:none" >{{li.COMPONENT_NO}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px;display:none" >{{li.STATUS}}</v-ons-col>
                    <v-ons-col width="20%" style="padding-top:6px;display:none" >{{li.TYPE}}</v-ons-col>
                  </v-ons-row>
             
                </v-ons-list-item>
            </v-ons-col>
        </v-ons-row>
        </v-ons-list>

    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
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
      page: (state) => state.wms_out.page,
      st_barCode: (state) => state.wms_out.barCode,
      st_pageData: (state) => state.wms_out.pageData,
      st_userWerks: (state) => state.userWerks,
      st_whNumber: (state) => state.userWhNumber,
      st_checkDataList: (state) => state.wms_out.checkDataList
    }),
    mounted () {
      this.setPage('dispatching_handover')
      this.barCode = this.st_barCode
      this.list = this.st_pageDataList
      
      if(undefined === this.st_pageData.list){
        this.pageData = {
          list: []
        }
      }else{
        console.log('-->this.st_pageDataRE : ' ,this.st_pageData.list);
        this.pageData = this.st_pageData
      }
      this.checkDataList = this.st_checkDataList
      console.log('-->this.checkDataList>>> : ' ,this.checkDataList);

    },
    data(){
      return{
        focusCtrl: 0,       // 自动聚焦控制,变动时,执行自动聚焦指令
        currentIndex: 0,    // 当前聚焦元素的索引
        actionType: 'next', // 自动聚焦的行为类型
        checkDataList :[],
        list: [],
        checkData: [],
        barCode :'',
        cntCount:'',
        pageData:{
          list: []
        },
        msg: '',
        modalVisible: false,
        ischeck: false
      }
    },
    
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'DispatchingHandoverList','DispatchingConfirm'
      ]),
      ...mapMutations([
        'setPage','setPageDataList','setCheckDataList','setPageData'
      ]),
      ScanBarcode(e){
        if(13 === window.event.keyCode){
          this.search()
        }
      },
      search(){
          if('' === this.barCode){
            this.$ons.notification.toast('请输入或扫描单号!', {timeout: 2000});
          }else{
            let barCode = this.barCode
            let check = false
            this.pageData.list.find(function(value) { 
              if(value.BARCODE === barCode) { 
                check = true
              }
            })
            
            if(check){
              this.$ons.notification.toast('单号'+barCode+'已扫描，请勿重复扫描！', {timeout: 1500})
              this.modalVisible = false
            }else{
              console.log('-->fromplantcode: ' +this.st_userWerks)
              this.DispatchingHandoverList({barCode:barCode,fromPlantCode:this.st_userWerks}).then(res => {
                if(0 === res.data.code){
                  
                  if(0 === res.data.result.length){
                    this.$ons.notification.toast('没有查询到条码'+barCode+'的信息！', {timeout: 1500})
                  }else{
                   for(var i=0;i<res.data.result.length;i++){
                      this.pageData.list.push(res.data.result[i])
                      console.log('-->data: ' ,this.pageData.list)
                      this.cntCount++
                    }
                  }
                }else{
                  this.msg = res.data.msg
                }
                
              })
            

            }

          }
        
      },
      confirm(){
        
          if(0===this.checkDataList.length){
            this.$ons.notification.toast('请选择需要过帐的行项目', {timeout: 2000});
          }else{
            this.setPageData(this.pageData)
            this.setCheckDataList(this.checkDataList)
            console.log('-->this.checkDataList_confirm : ' ,this.checkDataList);
            this.$emit('gotoPageEvent','dispatching_handover_confirm')
            
          }
        
      },
      reset(){
        this.pageData = {
          list: []
        }
        this.barCode = ''
        this.cntCount=''
        this.setFocus('jump',0)
        this.setPage('dispatchingConfirm')
      },
      
      checkall(){
        this.ischeck = !this.ischeck
        let temp = []
        for(var i = 0;i<this.pageData.list.length;i++){
          temp[i] = i + ''
        }
        this.checkDataList = (this.ischeck)?temp:[]
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