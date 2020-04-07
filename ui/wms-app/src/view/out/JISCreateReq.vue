<template>
    <v-ons-page>
    <toolbar :title="'需求创建'" :action="toggleMenu"></toolbar>
    <v-ons-card>
        <v-ons-list>
            <v-ons-list-item>
                <div class="left" style="width:30%">
                    配送单号：
                </div>
                <div class="center" >
                    <v-ons-input placeholder="配送单号" v-model="DELIVERY_NO"></v-ons-input>
                </div>  
                <div class="right" >
                    <v-ons-button @click="scanDeliveryNo" style="margin: 6px 0">扫描</v-ons-button>
                </div>             
            </v-ons-list-item>           
        </v-ons-list>
    </v-ons-card>

    <v-ons-card>
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">  
          <v-ons-col width="25%" style="border-left-style: ridge;border-left-width: 1px;">排序号</v-ons-col>      
          <v-ons-col width="25%" style="border-left-style: ridge;border-left-width: 1px;">物料号</v-ons-col>
          <v-ons-col width="25%" style="border-left-style: ridge;border-left-width: 1px;">物料描述</v-ons-col>
          <v-ons-col width="25%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in this.list" :key="$index">
          
          <v-ons-col
            width="25%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.DLV_ITEM}}</v-ons-col>

          <v-ons-col
            width="25%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MAKTX}}</v-ons-col>

          <v-ons-col
            width="25%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MAKTX}}</v-ons-col>

          <v-ons-col
            width="25%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.QTY}}</v-ons-col>
             
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="confirm">确认</v-ons-button>
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

        }),       
        data(){
            return {
                DELIVERY_NO:'',
                list:[],
                msg:'请扫描送货单号'                    
            }
        },
        mounted(){

        },
        methods : {
            ...mapActions([
                'JITScanDeliveryNo'
            ]),
            ...mapMutations([
             ]),
            scanDeliveryNo(){
                this.JITScanDeliveryNo({DELIVERY_NO:this.DELIVERY_NO}).then(res=>{
                    let arr = res.data.data
                    if(arr.length)
                        this.list = arr                       
                    else
                        this.$ons.notification.toast('配送单号不存在!', {timeout: 2000})                   
                })
            },
            confirm(){

            }
        }
    }
</script>
