<!--
上架- 扫描实物条码
Yang Lin 2019 4/8
-->

<template>
    <v-ons-page>
        <toolbar :title="'上架'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <v-ons-list-item>
                <label class="center" :for="'checkbox-1'">
                    是否对当前订单行项目生效：
                </label>
                <label class="right">
                    <v-ons-checkbox :input-id="'checkbox-1'" :value="'1'" v-model="orderItemEnable"></v-ons-checkbox>
                </label>
            </v-ons-list-item>

            <!--<v-ons-list-item>
                <div class="left" style="width:20%">
                    库位：
                </div>
                <div class="center">
                    <v-ons-input placeholder="库位" v-model="location"></v-ons-input>
                </div>
            </v-ons-list-item>-->

            <v-ons-list-item>
                <div class="left" style="width:20%">
                    条码：
                </div>
                <div class="center">
                    <v-ons-input placeholder="条码" v-model="barcode" @keydown.enter="queryLabel"></v-ons-input>
                </div>
                <div class="right">
                    <v-ons-button @click="queryLabel">扫描</v-ons-button>
                </div>
            </v-ons-list-item>

            <v-ons-list-item>
                <div class="left" style="width:20%">
                </div>
                <div class="center">
                    {{latest_bin_code}}
                </div>
            </v-ons-list-item>

            <v-ons-list-item>
                <div class="left" style="width:20%">
                    储位：
                </div>
                <div class="center">
                    <v-ons-input placeholder="储位" v-model="storageSpace"></v-ons-input>
                </div>
                <div class="right">
                    <v-ons-button>扫描</v-ons-button>
                </div>
            </v-ons-list-item>


        </v-ons-list>

        <v-ons-card>
            <DataTable :dataTable="dataTable" :columns="columes" :hiddenSelect="'hiddenSelect'"></DataTable>
        </v-ons-card>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="dataTableHandler">数据表</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import {queryLabelScannerShelf,queryLatestMvStorageBincode} from '@/api/in'

    export default {
        components: {toolbar,DataTable},
        props:['toggleMenu'],
        data(){
            return {
                columes: {INBOUND_NO:'进仓单号',INBOUND_ITEM_NO:'行号',MATNR:'物料号',BATCH:'批次',LGORT:'库位',BIN_CODE:'储位',BOX_QTY:'数量'},
                latest_bin_code:'',//最后上架储位
            }
        },
        created(){

        },
        computed : {
            storageSpace : {
                get(){
                    return this.$store.state.wms_in.shelf.scanner_bin_code;
                },
                set(n){
                    this.$store.commit("shelf/scanner_bin_code",n);
                }
            },
            dataTable:{
                get(){
                    let data = this.$store.state.wms_in.shelf.scanner_label_list;
                    if(data.length == 0)
                        return data;
                    //汇总数量
                    let quanlity = 0;
                    for(let i of data){
                        quanlity += parseInt(i.BOX_QTY);
                    }
                    let d = {"INBOUND_NO":data[0].INBOUND_NO,
                        "INBOUND_ITEM_NO":data[0].INBOUND_ITEM_NO,
                        "MATNR":data[0].MATNR,
                        "BATCH":data[0].BATCH,
                        "LGORT":data[0].LGORT,
                        "BIN_CODE":data[0].BIN_CODE
                    }
                    d.BOX_QTY = quanlity;
                    return [d];
                },
                set(n){
                    this.$store.commit("shelf/scanner_label_list",n);
                }
            },
            barcode : {
                get(){
                    return this.$store.state.wms_in.shelf.scanner_barcode;
                },
                set(n){
                    this.$store.commit("shelf/scanner_barcode",n);
                }
            },
            orderItemEnable:{
                get(){
                    return this.$store.state.wms_in.shelf.scanner_orderItemEnable;
                },
                set(n){
                    this.$store.commit("shelf/scanner_orderItemEnable",n);
                }
            }
        },
        methods : {
            dataTableHandler(){
                if(this.storageSpace == ''){
                    this.$ons.notification.toast("储位不能为空",{timeout:1000});
                    return;
                }
                this.$emit('gotoPageEvent','ShelfViewScannerDataTable')
            },
            queryLabel(){
                let INBOUND_RELATED = null;
                if(this.orderItemEnable.length > 0){
                    INBOUND_RELATED = "true";
                }
                //查询标签
                queryLabelScannerShelf({"LABEL_NO":this.barcode,"INBOUND_RELATED":INBOUND_RELATED}).then(r => {
                    let response = r.data;
                    if(response.code == '0'){
                        let data = response.data;
                        this.dataTable = data;
                        this.queryLastBinCode();
                    }else {
                        this.$ons.notification.toast(response.msg,{timeout:1000})
                    }
                })
            },
            queryLastBinCode(){
                queryLatestMvStorageBincode({'LABEL_NO':this.barcode}).then(d=> {
                    let resp = d.data;
                    if(resp.code == '0'){
                        this.latest_bin_code = resp.data;
                    }else {
                        this.$ons.notification.toast(resp.msg,{timeout:1000});
                    }
                });
            }
        }
    }
</script>

<style>
</style>
