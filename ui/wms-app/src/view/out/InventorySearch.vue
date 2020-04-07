<template>
    <v-ons-page>
        <toolbar :title="'库存查询'" :action="toggleMenu"></toolbar>
    <v-ons-card>
        <v-ons-list>
            <v-ons-list-item>
                <div class="left" style="width:30%">
                    条码：
                </div>
                <div class="center" >
                    <v-ons-input placeholder="标签条码" v-model="labelNo" modifier="underbar"></v-ons-input>
                </div>
                <div class="right" >
                    <v-ons-button @click="confirm" style="margin: 6px 0">扫描条码</v-ons-button>
                </div>
            </v-ons-list-item>
        </v-ons-list>
    </v-ons-card>

<v-ons-card>
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">料号</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">批次</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">储位</v-ons-col>
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">供应商代码</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in list" :key="$index">
          
          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BATCH}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BIN_CODE}}</v-ons-col>

          <v-ons-col
            width="10%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.STOCK_QTY}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.LIFNR}}</v-ons-col>
             
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>
    
        <v-ons-bottom-toolbar class="bottom-toolbar">
            
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import { mapActions, mapMutations,mapState } from 'vuex'
    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        computed: mapState({
        pageDataList: (state) => state.wms_out.pageDataList, 
        userWerks: (state) => state.user.userWerks
        }),
        data(){
            return {
                labelNo:'',
                obj: null,
                list: [],
                msg: '请扫条码..',
                WERKS: sessionStorage.getItem('UserWerks'),
                WH_NUMBER: sessionStorage.getItem('UserWhNumber'), 
            }
        },
        methods : {
            ...mapActions([
                'queryMatnr','inventoryList'
            ]),
            ...mapMutations([
                 'setPageDataList'
             ]),
            confirm(){  
                this.list = []         
                this.queryMatnr({labelNo:this.labelNo}).then(res => {
                    if(res.data.data.length===0){
                        this.$ons.notification.toast('没有该条码信息', {timeout: 2000});
                        return   
                    }
                    this.obj = res.data.data[0]
                   
                    if(this.obj.WERKS==sessionStorage.getItem('UserWerks'))       //  this.userWerks 用户登录工厂 'C160'            
                      this.inventoryList({werks:this.obj.WERKS,matnr:this.obj.MATNR}).then(res =>{
                        if(res.data.data.length==0){
                          this.msg='没有找到物料'+this.obj.MATNR
                          return
                        }else
                          this.list = res.data.data
                         //this.setPageDataList(res.data.data) 
                         //this.list = this.pageDataList
                     })    
                     else
                       this.$ons.notification.toast('没有操作权限', {timeout: 2000});        
                })                         
            }
        }
    }
</script>
