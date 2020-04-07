<!--
PDA扫描条码货物移动
QIU JIAMING1
2019-05-06
-->
<template>
    <v-ons-page id="shelf-init">
        <custom-toolbar :title="'PDA扫描条码货物移动'" :action="toggleMenu"></custom-toolbar>
       <v-ons-card>
       
       





         <v-ons-row class="in-row">
            <v-ons-col width="25%"><span class="red-star">*</span>目标储位: </v-ons-col>
               <v-ons-col width="55%">
                   <v-ons-input placeholder="扫描储位" v-model="mbBin" type="text" modifier="material" v-validate="'required'"></v-ons-input>
               </v-ons-col>
            <v-ons-col width = '20%'><v-ons-button>扫描</v-ons-button></v-ons-col>
        </v-ons-row>

         <v-ons-row class="in-row">
            <v-ons-col width="25%"><span class="red-star">*</span>条码: </v-ons-col>
            <v-ons-col width="55%">
                <v-ons-input placeholder="扫描条码" type="text" modifier="material" v-model="barcode" name="条码" v-validate="'required'"></v-ons-input>
            </v-ons-col>
            <v-ons-col width = '20%'><v-ons-button @click="scannerBarcode">扫描</v-ons-button></v-ons-col>
        </v-ons-row>


       </v-ons-card>




        <data-table :dataTable="list" :columns="cols" :hiddenSelect="'true'"></data-table>


        <v-ons-bottom-toolbar>
            <div style="text-align: center;">
                <v-ons-button class="btn" @click="create">保存</v-ons-button>
            </div>
        </v-ons-bottom-toolbar>


        

    </v-ons-page>
</template>

<script>
    import customToolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import { mapActions, mapMutations,mapState } from 'vuex'

    export default {
        components:{customToolbar,DataTable},
        props : ['toggleMenu'],
        mounted() {
            
        },
        created(){

        },
        computed : {
            barcode : {
                get(){
                    return this.$store.state.wms_in.shelf.barcode
                },
                set(val){
                    this.$store.commit('shelf/setBarCode',val)
                }
            },
            page : {
                get(){
                    return this.$store.state.wms_in.shelf.page
                },
                set(val){
                    this.$store.commit('shelf/setPage',val)
                }
            },
            assno : {
                get(){
                    return this.$store.state.wms_in.shelf.assno
                },
                set(val){
                    this.$store.commit('shelf/setAssno',val)
                }
            },

        },
        methods : {
        ...mapMutations([
            'setPageDataList'
          ]),
        ...mapActions([ 
            'queryByBarcode','validateBinCode','commitByHwyd',
          ]),
            updateBarcode(e){
                this.$store.commit('setBarCode',e.target.value)
            },
            toDataTable(){
                if(this.list.length === 0){
                    this.$ons.notification.toast('数据不存在',{timeout:1000})
                }else{

                    //
                    this.setPageDataList(this.listData)
                    this.$emit('gotoPageEvent','pdaScannerDataTable')
                }
            },
            //条码扫描
            scannerBarcode(){
                
                 if(null === this.mbBin || '' === this.mbBin){
                        this.$ons.notification.toast('请输入或扫描目标储位！', {timeout: 1000});
                 }else{
                     
                    if(this.list.length === 0){
                               
                                this.$validator.validateAll().then(result => {
                                if(result){
                                
                                //调用action
                                this.queryByBarcode({barcode:this.barcode}).then(res => {
                  
                                    if(res.data.length > 0){
                                            var tagList  = [];
                                            var dataNew = {"BIN_CODE":res.data[0].BIN_CODE,mbBin_Code:this.mbBin,"MATNR":res.data[0].MATNR,"MAKTX":res.data[0].MAKTX,
                                            "BOX_QTY":res.data[0].BOX_QTY,"BATCH":res.data[0].BATCH};
                                            tagList= dataNew;
                                            this.list.push(tagList);
                                            //
                                            var tagList2  = [];
                                            var dataNew2 = {userName:sessionStorage.getItem('UserName'),LIKTX:res.data[0].LIKTX,LIFNR:res.data[0].LIFNR,UNIT:res.data[0].UNIT,LABEL_NO:this.barcode,WERKS:res.data[0].WERKS,WH_NUMBER:res.data[0].WH_NUMBER,"BIN_CODE":res.data[0].BIN_CODE,"MATNR":res.data[0].MATNR,"MAKTX":res.data[0].MAKTX,
                                            "BOX_QTY":res.data[0].BOX_QTY,"BATCH":res.data[0].BATCH,mbBin_Code:this.mbBin};
                                            tagList2= dataNew2;
                                            this.listData.push(tagList2);
            
                                    }else{
                                        this.$ons.notification.toast('条码不存在!', {timeout: 1000});
                                        
                                    }


                                });
                                


                                }else{
                                    this.$ons.notification.toast('请扫描或输入条码!',{timeout:1000})
                                }

                            })
                    }else{


                        }
                    }

                     
                 },
                //提交方法
                create(){
                        if(null === this.mbBin || '' === this.mbBin){
                                this.$ons.notification.toast('请输入或扫描目标储位！', {timeout: 1000});
                        }else if(null === this.barcode || '' === this.barcode){
                                this.$ons.notification.toast('请输入或扫描条码号！', {timeout: 1000});
                        }
                        else{

                            this.commitByHwyd({list:this.listData}).then(res => {
                               
                                if(res.data.code === 0){
                                    this.$ons.notification.toast('操作成功!',{timeout:1000});
                                    this.list = [];
                                    this.listData = [];
                                    this.mbBin = null;
                                    this.barcode = null;
                                }else{
                                    this.$ons.notification.toast('操作失败!'+res.data.msg,{timeout:2000});
                                    this.list = [];
                                    this.listData = [];
                                    this.mbBin = null;
                                    this.barcode = null;
                                }
                            
                            });

                        }

            },
                
        },
        


        data () {
            return {
                cols : {BIN_CODE:'源储位',mbBin_Code:'目标储位',MATNR:'料号',MAKTX:'物料描述',BOX_QTY:'数量',BATCH:'批次'},
                list : [],
                listData:[],
                yBin : '',
                mbBin:'',
            }
        }
    }
</script>


<style>
    .red-star { color: red }
    .in-row {margin: 6px 0;}
</style>
