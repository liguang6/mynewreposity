<template>
    <v-ons-page>
        <toolbar :title="'STO一步联动收货'" :action="toggleMenu"></toolbar>
    <v-ons-card>
      <v-ons-row class="in-row">
        <v-ons-col width="25%"><span class="red-star">*</span>&nbsp;调出工厂:</v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input type="text" modifier="material" v-model="outwerks"></v-ons-input>
        </v-ons-col>
        <v-ons-col width="20%"></v-ons-col>
      </v-ons-row>
       
      <v-ons-row class="in-row">
        <v-ons-col width="25%"><span class="red-star">*</span>&nbsp;调入工厂:</v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input type="text" modifier="material" v-model="inwerks" @blur="setInwerk"></v-ons-input>
        </v-ons-col>
        <v-ons-col width="20%"></v-ons-col>
      </v-ons-row>

      <v-ons-row class="in-row">
        <v-ons-col width="25%">
          &nbsp;&nbsp;&nbsp;储位:
        </v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input type="text" modifier="material" v-model="binCode"></v-ons-input>
        </v-ons-col>
        <v-ons-col width="20%"></v-ons-col>
      </v-ons-row>

      <v-ons-row class="in-row">
        <v-ons-col width="25%"><span class="red-star">*</span>&nbsp;条码:</v-ons-col>
        <v-ons-col width="55%">
          <v-ons-input type="text" modifier="material" v-model="barcode" placeholder="扫描或输入"></v-ons-input>
          &nbsp;&nbsp;&nbsp;<v-ons-button  @click="confirm" >&nbsp;确认</v-ons-button>
        </v-ons-col>
        <v-ons-col width="20%">
        </v-ons-col>
      </v-ons-row>
    </v-ons-card>

<v-ons-card>
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">物料号</v-ons-col>
          <v-ons-col width="45%" style="border-left-style: ridge;border-left-width: 1px;">物料描述</v-ons-col>
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">单位</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in list" :key="$index">
          
          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="45%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MAKTX}}</v-ons-col>

          <v-ons-col
            width="10%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BOX_QTY}}</v-ons-col>

          <v-ons-col
            width="10%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.UNIT}}</v-ons-col>
     
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>
    
        <v-ons-bottom-toolbar class="bottom-toolbar">
           <center>
             <v-ons-button @click="getDataTable" style="margin: 2px 0"><v-ons-icon icon="fa-table"></v-ons-icon>&nbsp;数据表</v-ons-button>
             <v-ons-button @click="confirmHandover">确认过账</v-ons-button>  
        </center> 
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
        inwerks1: (state) => state.wms_out.inwerks1
        }),
        mounted(){
          this.list = this.pageDataList
          this.inwerks = this.inwerks1
          this.setPage('StepLinkage')
        },
        data(){
            return {
                barcode:'',
                inwerks: '',
                outwerks: '',
                binCode: '',
                msg: '请扫条码..',
                list:[],
                listtemp:[]                    
            }
        },
        methods : {
            ...mapActions([
                'scanLabel'
            ]),
            ...mapMutations([
                 'setPageDataList','setInwerks','setPage'
             ]),
            confirm(){            
              for(let i=0;i<this.pageDataList.length;i++){
                if(this.pageDataList[i].LABEL_NO==this.barcode){
                  this.$ons.notification.toast('该条码已扫描', {timeout: 2000})
                  return
                }  
              }
              //this.list=[]  
              if(this.inwerks==''||this.outwerks==''||this.barcode==''){
                this.$ons.notification.toast('必填字段不能为空', {timeout: 2000})
                return
              }
              this.scanLabel({labelNo:this.barcode,werks:this.inwerks}).then(res=>{
                if(this.pageDataList.length>0)
                    //this.listtemp = this.pageDataList
                    this.list = this.pageDataList
                if(res.data.data==null)
                  this.msg='没有该条码信息'
                else{
                  res.data.data.outwerks = this.outwerks
                  res.data.data.binCode = this.binCode
                  res.data.data.boxNo = 1
                  this.list.push(res.data.data)                  
                  //this.listtemp.push(res.data.data) 
                  //this.setPageDataList(this.listtemp)
                  this.setPageDataList(this.list)
                }   
              })                     
            },
            getDataTable(){
              this.$emit('gotoPageEvent','StepLinkageDataTable') 
            },
            setInwerk(){
              this.setInwerks(this.inwerks)
            },
            confirmHandover(){
              this.$emit('gotoPageEvent','StepLinkageHandover')
            }
        }
    }
</script>
