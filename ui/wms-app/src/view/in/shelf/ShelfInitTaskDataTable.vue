<template>
    <v-ons-page id="shelf-init-task-data-table">
        <custom-toolbar :title="'数据表'" :action="toggleMenu"></custom-toolbar>
        <data-table :dataTable = "dataTable" :renders="renders" :columns="columns" ref="mytable" v-on:table-column-click="colClick"></data-table>
        <v-ons-bottom-toolbar>
            <div style="text-align: center;">
                <v-ons-button class="btn" @click="del">删除</v-ons-button>
                <v-ons-button class="btn" @click="back">返回</v-ons-button>
                <v-ons-button class="btn" @click="create">创建</v-ons-button>
            </div>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import customToolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import {newInInbound,relatedAreaNamelist} from '@/api/in'

    export default {
        props:['toggleMenu'],
        components:{DataTable, customToolbar},
        created(){
           //获取仓管员
            relatedAreaNamelist({"WERKS":this.werks,"WH_NUMBER":this.whNumber}).then(d=>{
                if(d.data.code == '0' && d.data.result.length > 0){
                    this.admin = d.data.result[0].MANAGER;
                }
                this.init();
            })
        },
        data(){
          return {
              columns :{batch:'批次',vendor:'供应商',admin:'仓管员',qty:'数量',box:'箱数'},
              dataTable:[],
              renders: {
                  box : function (val) {
                      return '<a href="javascript:void(0);" style="color:red">'+val+'</a>';
                  }
              },
              admin:"",//仓管员
          }
        },
        computed : {
            initTaskTabs(){
                return this.$store.state.wms_in.shelf.initTaskTabs;
            },
            //工厂
            werks(){
                return sessionStorage.getItem("UserWerks");
            },
            //仓库
            whNumber(){
                return sessionStorage.getItem("UserWhNumber");
            },
            binCode(){
                return this.$store.state.wms_in.shelf.storeArea;
            },
            postVehicleID(){
                return this.$store.state.wms_in.shelf.postVehicleID;
            }
        },
        methods : {
            init(){
                //根据批次汇总标签数据
                let map = new Map();
                for(let i of this.initTaskTabs){
                    let o = map.get(i.batch);
                    if(o == null || o == undefined){
                        o = {batch:i.batch,vendor:i.vendor,admin:this.admin,qty:i.qty,box:1};
                    }else {
                        o = {batch:i.batch,vendor:i.vendor,qty:o.qty+i.qty,admin:this.admin,box:o.box+1};
                    }
                    map.set(i.batch,o);
                }
                let unionList = [];
                for(let [key,val] of map){
                    unionList.splice(-1,0,val);
                }
                this.dataTable = unionList;
            },
            create(){
                if(this.$refs.mytable.selected().length === 0){
                    this.$ons.notification.toast('请选择数据',{timeout:1000})
                }else{
                    let batchIndexList =  this.$refs.mytable.selected();//获取选中的批次
                    let labelList = [];//获取创建进仓单的标签
                    for(let b of batchIndexList){
                        let batch = this.dataTable[b].batch;
                        for(let i of this.initTaskTabs){
                            if(i.batch == batch){
                                labelList.splice(-1,0,i.barcode);
                            }
                        }
                    }

                    let post = {'data':labelList,"WERKS":this.werks,"WH_NUMBER":this.whNumber,"BIN_CODE":this.binCode,"LT_WARE":this.postVehicleID};
                    //创建进仓单
                    newInInbound(post).then(d=>{
                        let data = d.data;
                        if(data.code == '0'){
                            this.$ons.notification.alert("189000023467",{buttonLabels:'确定',title:'上架单号'});
                            this.$store.commit("shelf/setInitTaskTabs",[]);//清空条码
                            this.$emit("gotoPageEvent","ShelfInitTask");//跳转到创建进程单页
                        } else {
                            this.$ons.notification.toast("创建失败,"+data.msg,{timeout:1000});
                        }
                    });
                }
            },
            back(){
                this.$emit("gotoPageEvent",'ShelfInitTask')
            },
            del(){
                let arr = this.$refs.mytable.selected();//获取选中的行
                this.dataTable = this.dataTable.filter((v,i)=>{
                    for(let a of arr){
                        if(a == i)
                            return false;
                    }
                    return true;
                })
                this.$refs.mytable.clearSelect();
            },
            colClick(data){
                if(data.column == 'box'){
                    //设置点击的批次
                    this.$store.commit("shelf/initTaskDatatableBatch",this.dataTable[data.index].batch);
                    //跳转到批次标签详情
                    this.$emit("gotoPageEvent",'ShelfInitTaskDataTableDetail');
                }
            }
        }
    }

</script>

<style>
    .btn  { margin-left: 16px; }
</style>
