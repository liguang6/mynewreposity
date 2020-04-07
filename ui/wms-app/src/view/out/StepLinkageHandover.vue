
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
        <v-ons-col width="25%" style="padding-top:10px"><center>抬头文本:</center></v-ons-col>
        <v-ons-col width="75%" ><center><v-ons-input id="content" name="抬头文本" v-model="content" style="padding-top:8px;width:95%" modifier="underbar" placeholder="抬头文本" float v-validate="'required'"></v-ons-input></center></v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <v-ons-card>
    </p>{{msg}}</p>
    <v-ons-list v-if="this.code==0">
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        WMS凭证号:{{wmsno}}
                    </v-ons-col>
                    <v-ons-col>
                        
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            
            <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                      SAP凭证号:{{sapdoc}}
                   </v-ons-col>
                   <v-ons-col>
                       
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>
            
            
                <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                      收货单号:{{receiptno}}
                   </v-ons-col>
                   <v-ons-col>
                       
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>

                <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                      质检单号:{{iqcno}}
                   </v-ons-col>
                   <v-ons-col>
                       
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button  @click="goback" style="margin: 6px 0"></v-ons-icon>返回</v-ons-button>&nbsp;&nbsp;
        <v-ons-button  @click="handover" style="margin: 6px 0">过账</v-ons-button>
      </center>
    </ons-bottom-toolbar>
  </v-ons-page>
</template>
<script>
  import customToolbar from '_c/toolbar'
  import Datepicker from "vuejs-datepicker/dist/vuejs-datepicker.esm.js";
  import { mapActions, mapMutations,mapState } from 'vuex'
  export default {
    computed: mapState({
      page: (state) => state.wms_in.page,
      pageDataList: (state) => state.wms_out.pageDataList
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
        content: '',
        format: "yyyy-MM-dd",
        code:-1,
        msg:'过账信息..',
        wmsno:'',
        receiptno:'',
        iqcno:'',
        sapdoc:''
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar,Datepicker },
    methods: {
      ...mapActions([
        'stepLinkageHandover'
      ]),  
      ...mapMutations([
                'setPageDataList','setPageCache'
             ]),
      handover(){
        let arr = [];                
        this.pageDataList.forEach(ele=>{
        ele = JSON.parse(JSON.stringify(ele))
        if(arr.length == 0) {
           ele.label=[ele.LABEL_NO]
           arr.push(ele)
           }
        else{
           let flag = true
           for(let i=0;i<arr.length;i++){
              if(arr[i].MATNR== ele.MATNR&&arr[i].BATCH == ele.BATCH){
                arr[i].boxNo += ele.boxNo
                arr[i].BOX_QTY += ele.BOX_QTY
                arr[i].label.push(ele.LABEL_NO)
                flag = false
                break
              }
            }
              if(flag){
                ele.label=[ele.LABEL_NO]
                arr.push(ele)
              }    
        }  
        })
        let list = []
        arr.forEach(ele=>{
          let obj  = Object({ "LABEL":JSON.stringify(ele.label),"MATNR":ele.MATNR,"BATCH":ele.BATCH,"QTY":ele.BOX_QTY,"UNIT":ele.UNIT })
		      list.push(obj)
        })
        let o = this.pageDataList[this.pageDataList.length-1]  
        let map = {"WH_NUMBER":o.WH_NUMBER,"WERKS_FROM":o.outwerks,"WERKS_TO":o.WERKS,"BINCODE":o.binCode,
            "PZ_DATE":this.pz_data,"JZ_DATE":this.jz_data,"HEADTXT":this.content,"ITEMLIST":JSON.stringify(list)}
        this.stepLinkageHandover({list:map}).then(res=>{
          console.log(res.data)
          this.setPageCache([])
          this.setPageDataList([])
          this.code = res.data.code
          this.msg = res.data.msg
          if(res.data.code==500){
            //this.$ons.notification.toast(res.data.msg, {timeout: 2000})
            return
          }
          if(res.data.code==0){
            let obj =  res.data.data
            this.wmsno = obj.wmsno
            this.receiptno = obj.receiptno
            this.iqcno = obj.iqcno
            this.sapdoc = obj.sapdoc
            //this.$emit('gotoPageEvent','StepLinkageHandoverEnd')
          }        
        })
      },
      goback(){     
        switch(this.page){
          case 'StepLinkage':
            this.$emit('gotoPageEvent','StepLinkage');
            break
          case 'StepLinkageDataTable':
            this.$emit('gotoPageEvent','StepLinkageDataTable');
            break          
        }
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