<!--
 入库-扫描实体标签上架 - 数据表
 Yang Lin 2019 4/8
 -->
<template>
    <v-ons-page>
        <toolbar :title="'上架'" :action="toggleMenu" ></toolbar>

        <DataTable :dataTable="dataTable"  ref="mytable" :columns = 'columns' :renders="renders" v-on:table-column-click="handleColumnClick"></DataTable>


        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="back">返回</v-ons-button>
            <v-ons-button @click="shelfAndTransfer">上架</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import {scannerLabelNoShelf} from '@/api/in'

    export default {
        props:['toggleMenu'],
        components:{toolbar,DataTable},
        data(){
            {
                return {
                    columns : {INBOUND_NO:'进仓单号',INBOUND_ITEM_NO:'行号',MATNR:'物料号',BATCH:'批次',LGORT:'库位',BIN_CODE:'储位',BOX_QTY:'总数量',"LABEL_QTY":"箱数"},
                    renders : {
                        LABEL_QTY : function (val) {
                            return `<a  href="javascript:void(1)">${val}</a>`
                        }
                    }
                }
            }
        },
        methods : {
            back(){
                this.$emit('gotoPageEvent','ShelfViewScanner')
            },
            handleColumnClick(obj){
                if(obj.column === 'LABEL_QTY'){
                    this.$emit('gotoPageEvent','ShelfViewScannerDataTableDetail')
                }
            },
            shelfAndTransfer(){
                //this.$store.commit('setPage','ShelfViewScannerDataTable')
                //this.$emit('gotoPageEvent','in_confirm')
                let arr = this.$refs.mytable.selected();
                if(arr.length == 0){
                    this.$ons.notification.toast("没有选择上架数据",{timeout:1000})
                }else {
                    //
                    let data = this.$store.state.wms_in.shelf.scanner_label_list;
                    let labelList = [];
                    for(let i of data){
                        labelList.splice(-1,0,i.LABEL_NO);
                    }
                    scannerLabelNoShelf({"LABEL_LIST":labelList,"WH_NUMBER":this.$store.state.user.userWhNumber,"TO_BIN_CODE":this.$store.state.wms_in.shelf.scanner_bin_code}).then(r => {
                        let d = r.data;
                        if(d.code =='0'){
                            //清除数据
                            //返回到扫描实物上架-首页
                            this.$ons.notification.toast("上架成功",{timeout:1000})
                            this.$store.commit("shelf/scanner_label_list",[]);
                            this.$store.commit("shelf/scanner_barcode",'');
                            this.$store.commit("shelf/scanner_orderItemEnable",[]);
                            this.$store.commit("shelf/scanner_bin_code","");
                            this.$emit("gotoPageEvent","ShelfViewScanner");
                        }else {
                            this.$ons.notification.toast(d.msg,{timeout:1000})
                        }
                    })
                }
            }
        },
        computed : {
            dataTable(){
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
                d.LABEL_QTY = data.length;
                return [d];
            }
        }
    }
</script>
