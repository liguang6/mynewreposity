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
      <v-ons-row>
        <v-ons-col width="25%" style="padding-top:10px"><center>需求单号:</center></v-ons-col>
        <v-ons-col width="75%" ><center><v-ons-input id="requirementNo" name="需求单号" v-model="requirementNo" style="padding-top:8px;width:95%" modifier="underbar" placeholder="  需求单号" float v-validate="'required'"></v-ons-input></center></v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">行项目</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">料号</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">批次</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in list" :key="$index">
          
          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.REFERENCE_DELIVERY_ITEM}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BATCH}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.QUANTITY}}</v-ons-col>
             
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button  :disabled="dis" @click="confirm" style="margin: 6px 0"><v-ons-icon icon="fa-check"></v-ons-icon>&nbsp;确认</v-ons-button>&nbsp;&nbsp;
        <v-ons-button  @click="gotoDataTable" style="margin: 6px 0">&nbsp;数据表</v-ons-button>
      </center>
    </ons-bottom-toolbar>
        <v-ons-dialog :visible.sync="dialogVisible" cancelable>
            <div style="text-align: center">
                <p>过账成功,WMS、SAP凭证:</p>
              
                
      <v-ons-row>
        <v-ons-col width="30%" style="padding-top:10px"><center>WMS凭证:</center></v-ons-col>
        <v-ons-col width="70%" ><center><v-ons-input id="wmsNo" name="WMS凭证" v-model="wmsNo" style="padding-top:8px;width:95%" placeholder="  WMS凭证" float ></v-ons-input></center></v-ons-col>
      </v-ons-row>

      <v-ons-row>
        <v-ons-col width="30%" style="padding-top:10px"><center>SAP凭证:</center></v-ons-col>
        <v-ons-col width="70%" ><center><v-ons-input id="sapNo" name="SAP凭证" v-model="sapNo" style="padding-top:8px;width:95%" placeholder="  SAP凭证" float ></v-ons-input></center></v-ons-col>
      </v-ons-row>

               <br />
                <v-ons-button @click="closeDialog">确认</v-ons-button>
            </div>
        </v-ons-dialog>
  </v-ons-page>
</template>
<script>
  import customToolbar from '_c/toolbar'
  import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";
  import { mapActions, mapMutations,mapState } from 'vuex'
  export default {
    computed: mapState({
      //requirementNo: (state) => state.wms_out.requirementNo 
    }),
    mounted () {
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
        pz_data: '',
        jz_data: '',
        requirementNo: '',
        format: "yyyy-MM-dd",
        list: [],
        jsonList : [],
        msg: '数据表...',
        dialogVisible:false,
        WERKS: sessionStorage.getItem('UserWerks'),
        WH_NUMBER: sessionStorage.getItem('UserWhNumber'),
        //USERNAME: 'admin',
        wmsNo:'',
        sapNo:'',
        dis:false
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar,Datepicker },
    methods: {
      ...mapActions([
        'handoverList','handoverSave'
      ]),
      ...mapMutations([
        'setRequirementNo'
      ]),
      closeDialog(){                             
          this.dialogVisible = false
      },
      confirm(){
        this.dis = true
        if(this.requirementNo==''){
           this.$ons.notification.toast('需求单号不能为空', {timeout: 2000});
           this.dis = false
           return
       }
        if(this.list.length==0){
            this.handoverList({requirementNo:this.requirementNo,WERKS:this.WERKS,WH_NUMBER:this.WH_NUMBER}).then(res=>{
            //this.requirementNo=''
            //this.list=[]      
            if(res.data.page.list.length===0){
                this.msg = '没有查询到相关数据'
                this.dis = false
                return
            }  
            this.list = res.data.page.list  //this.mergeObject(res.data.page.list)  
          }) 
        }
       
        setTimeout(() => {  
            if(this.list.length===0)
                return
            this.jsonList = JSON.stringify(this.list)
            this.handoverSave({PZDDT:this.pz_data,JZDDT:this.jz_data,ARRLIST:this.jsonList,WERKS:this.WERKS,WH_NUMBER:this.WH_NUMBER,USERNAME:sessionStorage.getItem('UserName')}).then(
            res=>{
                //{msg: " WMS凭证: 1WMS0000000651 SAP凭证: 后台过账任务创建成功！", code: 0}
                if(res.data.code==0){
                    //this.$ons.notification.toast('过账成功', {timeout: 2000});
                    this.wmsNo = res.data.wmsNo
                    if(res.data.sapNo)
                    this.sapNo = res.data.sapNo.replace(/; <br\/>/g,' ')
                    this.dialogVisible = true
                    this.list=[]
                    this.jsonList=[]
                }
                if(res.data.code==500){
                    this.$ons.notification.toast('过账失败', {timeout: 2000});                  
                }
                this.dis = false
                })
                        }, 200);
        
      },
      gotoDataTable(){     
       if(this.requirementNo==''){
           this.$ons.notification.toast('需求单号不能为空', {timeout: 2000});
           return
       }
        //this.setRequirementNo(this.requirementNo) 
        this.handoverList({requirementNo:this.requirementNo,WERKS:this.WERKS,WH_NUMBER:this.WH_NUMBER}).then(res=>{
            //this.requirementNo=''           
            if(res.data.page.list.length===0){
                this.list = []
                this.msg = '没有查询到相关数据'
                return
            }  
            this.list = res.data.page.list  //this.mergeObject(res.data.page.list)               
          })              
        //this.$emit('gotoPageEvent','HandoverDataTable')      
      },
      mergeObject(array) {
                var arrayFilted = [];
                array.forEach(function (value,key) {
                //判断过滤后的数组是否为空
                if ( arrayFilted.length == 0 ) {
                    arrayFilted.push(value);
                }else{
                      arrayFilted.forEach( function (valueIndex,keyIndex) {
                      if (valueIndex.MATNR && valueIndex.MATNR !== value.MATNR) {
                          arrayFilted.push(value);
                      }else if (valueIndex.MATNR && valueIndex.MATNR === value.MATNR) {
                          valueIndex.QUANTITY = valueIndex.QUANTITY + value.QUANTITY;
                      }
                      });
                }
                });
                return arrayFilted;
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