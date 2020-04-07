
<!--
PDA扫描条码出库
QIU JIAMING1
2019-04-15
-->

<template>
    <v-ons-page id="shelf-init-task-data-table">
        <custom-toolbar :title="'数据表'" :action="toggleMenu"></custom-toolbar>
        <data-table :dataTable = "dataTable" :columns="columns" ref="mytable" ></data-table>
        <v-ons-bottom-toolbar>
            <div style="text-align: center;">
                <v-ons-button class="btn" @click="del">删除</v-ons-button>
                <v-ons-button class="btn" @click="back">返回</v-ons-button>
                <v-ons-button class="btn" @click="create">保存</v-ons-button>
            </div>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>

    import customToolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import { mapActions, mapMutations,mapState } from 'vuex'
    import {getDate,getRelativeTime} from '@/libs/tools'
    export default {

        computed: mapState({
            page: (state) => state.wms_in.page,
            st_assno: (state) => state.wms_in.assno,
            st_pageDataList: (state) => state.wms_in.pageDataList,
            st_checkDataList: (state) => state.wms_in.checkDataList,
            st_userName: (state) => state.app.userName,
            }),

        mounted () {
                                
        //this.listData = this.st_pageDataList;
        this.st_pageDataList.filter((v,i)=>{
                var tagList  = [];
                    var dataNew = {"LABEL_NO":v.LABEL_NO,
                    "BOX_SN":v.BOX_SN,"BOX_QTY":v.BOX_QTY};
                    tagList= dataNew;
                v = tagList;
            });
        },
        created(){
                var listData = [];
                var temp = [] ;
                temp = this.st_pageDataList;
                temp.forEach(item => {
                        var tagList  = [];
                        var dataNew = {"LABEL_NO":item.LABEL_NO,
                        "BOX_SN":item.BOX_SN,"BOX_QTY":item.BOX_QTY,"UNIT":item.UNIT};
                        tagList= dataNew;
                        listData.push(tagList);
                    });
    
                this.dataTable = listData;
    
        },
        props:['toggleMenu'],
        components:{DataTable, customToolbar},
        data(){
          return {
              listData:[],
              columns : {BOX_SN:'箱序',LABEL_NO:'条码号',BOX_QTY:'装箱数量',UNIT:"单位"},
              dataTable:[],
          }
        },
        methods : {
             ...mapMutations([
            'setPageDataList'
            ]),
            ...mapActions([
            'listSaveByPdaBarcode'
            ]),
            create(){

                if(this.$refs.mytable.selected().length === 0){
                    this.$ons.notification.toast('请选择数据',{timeout:1000})
                }else{
                    //alert(this.st_userName);
                    let arr = this.$refs.mytable.selected()
                    let lemp = [];
                    var tagList  = [];
                    this.dataTable = this.st_pageDataList.filter((v,i)=>{
                        var dataNew = {"LABEL_NO":v.LABEL_NO,"MAKTX":v.MAKTX,"MATNR":v.MATNR,"WERKS":v.WERKS,
                        "BOX_SN":v.BOX_SN,"BOX_QTY":v.BOX_QTY,"UNIT":v.UNIT,postVehicleID:v.postVehicleID,CREATOR:"admin","BATCH":v.BATCH};
                        tagList= dataNew;
                        

                        for(let a of arr){
                            if(a == i)
                                lemp.push(tagList);
                                //lemp.push(v);
                        }
                        
                    });

                    this.listSaveByPdaBarcode({list:lemp}).then(res => {
                        if(res.data.code === 0){
                            this.$ons.notification.toast('操作成功!',{timeout:1000});
                            this.$emit("gotoPageEvent",'pdaScanner');
                        }else{
                            this.$ons.notification.toast('操作失败!',{timeout:1000});
                            this.$emit("gotoPageEvent",'pdaScanner');
                        }
                        
                    });

                }

            },
            back(){
                this.$emit("gotoPageEvent",'pdaScanner');
            },
            del(){
                let arr = this.$refs.mytable.selected()//获取选中的行
                
                this.dataTable = this.dataTable.filter((v,i)=>{
                    for(let a of arr){
                        if(a == i)
                            return false
                    }
                    return true
                })
                this.$refs.mytable.clearSelect()
            }
        }
    }

</script>

<style>
    .btn  { margin-left: 16px; }
</style>
