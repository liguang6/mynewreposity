<!--
 UB转储单进仓
 -->
<template>
    <v-ons-page>
        <toolbar :title="'UB转储单调拨进仓'" :action="toggleMenu"/>

        <v-ons-list>
            <v-ons-list-item modifier="nodivider">
                <v-ons-row>
                    <v-ons-col width="20%">库位：</v-ons-col>
                    <v-ons-col>
                        <v-ons-select style="width: 60%" v-model="selectedLgort">
                            <option v-for="item in logrtList" :value="item.LGORT">
                                {{item.LGORT}}
                            </option>
                        </v-ons-select>

                    </v-ons-col>
                    <v-ons-col></v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item modifier="nodivider">
                <v-ons-row>
                    <v-ons-col width="20%">条码：</v-ons-col>
                    <v-ons-col>
                        <v-ons-input type="text" modifier="material" placeholder="扫描或输入" v-model="barcode" name="条码" v-validate="'required'" @keydown.enter="scanner"></v-ons-input>
                    </v-ons-col>
                    <v-ons-col style="text-align: center">
                        <v-ons-button @click="scanner" :disabled="loading">扫描</v-ons-button>
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item modifier="nodivider">
                <v-ons-row>
                    <v-ons-col width="20%">储位：</v-ons-col>
                    <v-ons-col>
                        <v-ons-input type="text" modifier="material" v-model="bin_code" name="储位"></v-ons-input>
                    </v-ons-col>
                    <v-ons-col></v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>


        <data-table :dataTable="dataTable" :columns="columns" :hiddenSelect="'true'"></data-table>

        <v-ons-bottom-toolbar>
            <div style="text-align: center">
                <v-ons-button modifier="outline" @click="toNext">数据表</v-ons-button>
            </div>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import {queryLgortFromInbound,ubTransferInit} from '@/api/in'

    export default {
        components: {toolbar, DataTable},
        props: ['toggleMenu'],
        created(){
            this.$validator.localize('zh_CN');
            this.queryLgort();
        },
        computed: {

            selectedLgort :{
                get() {
                    return this.$store.state.wms_in.shelf.ub_lgort
                },
                set(newVal) {
                    this.$store.commit('shelf/ub_lgort', newVal)
                }
            },
            labelList : {
              get(){
                  return this.$store.state.wms_in.shelf.ub_label_list;
              },
                set(v){
                    this.$store.commit('shelf/ub_label_list', v);
                }
            },
            werks: {
                get() {
                    return this.$store.state.wms_in.shelf.ub_werks
                },
                set(newVal) {
                    this.$store.commit('shelf/ub_werks', newVal)
                }
            },
            bin_code: {
                get() {
                    return this.$store.state.wms_in.shelf.ub_bin_code
                },
                set(newVal) {
                    this.$store.commit('shelf/ub_bin_code', newVal)
                }
            },
            barcode : {
                get(){
                    return this.$store.state.wms_in.shelf.ub_barcode
                },
                set(newVal){
                    this.$store.commit('shelf/ub_barcode',newVal)
                }
            },
            dataTable(){
                if(this.labelList.length == 0)
                    return [];
                let d = new Map();
                for(let i of this.labelList){
                    //根据进仓单合并
                    let key = i.INBOUND_NO+":"+i.INBOUND_ITEM_NO;
                    if(d.has(key)){
                        d.get(key).BOX_QTY = parseInt(d.get(key).BOX_QTY) + parseInt(i.BOX_QTY);
                    }else {
                        let value = {"PO_NO":i.PO_NO,"PO_ITEM_NO":i.PO_ITEM_NO,"LGORT":i.LGORT,"MATNR":i.MATNR,"BOX_QTY":i.BOX_QTY};
                        d.set(key,value);
                    }
                }
                //返回根据进仓单合并之后的条码数据
                return Array.from(d.values());
            }
        },
        methods: {
            toNext() {
                if(this.dataTable.length === 0){
                    this.$ons.notification.toast('数据不存在',{timeout:1000})
                }else{
                    this.$emit('gotoPageEvent', 'ShelfUBTransferOrderDataTable')
                }

            },
            scanner() {
                this.$validator.validateAll().then(result => {
                    if(result){
                        this.loading = true
                        ubTransferInit({"LABEL_NO":this.barcode}).then(r=>{
                            this.loading = false;
                            let d = r.data;
                            if(d.code == '0'){
                                let labelList = d.data;
                                //判断标签是否已经存在
                                for(let l of this.labelList){
                                    for(let a of labelList){
                                        if(a.LABEL_NO == l.LABEL_NO){
                                            this.$ons.notification.toast('标签已扫描',{timeout:1000})
                                            return ;
                                        }
                                    }
                                }
                                this.barcode = "";
                                this.labelList = this.labelList.concat(labelList);
                            }else {
                                this.$ons.notification.toast(d.msg,{timeout:1000})
                            }
                        })
                    }else {
                        this.$ons.notification.toast(this.$validator.errors.all().join('</br>'),{timeout:1000})
                    }
                })
            },
            queryLgort(){
                let WERKS = this.$store.state.user.userWerks;
                let WH_NUMBER = this.$store.state.user.userWhNumber;
                queryLgortFromInbound({"WERKS":WERKS,"WH_NUMBER":WH_NUMBER}).then(r => {
                    let d = r.data;
                    if(d.code == '0'){
                        this.logrtList = d.data;
                    }else {
                        this.$ons.notification.toast(d.msg,{timeout:1000});
                    }
                })
            }
        },
        data() {
            return {
                columns: {PO_NO: '采购订号',PO_ITEM_NO:"行号", LGORT: '库位', MATNR: '物料号', BOX_QTY: '数量'},
                loading:false,//在ajax请求期间使按钮不能点击
                logrtList:[],
            }
        }
    }
</script>

<style>
    .shelf-table-item span {
        margin: 0 5px;
    }
</style>
