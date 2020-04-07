<!--
批次 - 条码明细
Yang Lin
2019-05-06
-->
<template>
    <v-ons-page>
        <custom-toolbar :title="'条码明细'" :action="toggleMenu"></custom-toolbar>
        <div style="text-align: right">
            <p style="padding-right: 25px;">合计:   {{currentBatchBarcodeList.length}}</p>
        </div>
        <data-table :dataTable="currentBatchBarcodeList" :columns="columns" :hiddenSelect="'true'"></data-table>
        <v-ons-bottom-toolbar>
            <div style="text-align: center;">
                <v-ons-button class="btn" @click="$emit('gotoPageEvent','ShelfInitTaskDataTable')">返回</v-ons-button>
            </div>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import customToolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'

    export default {
        props: ['toggleMenu'],
        components: {customToolbar, DataTable},
        computed: {
            currentBatch() {
                return this.$store.state.wms_in.shelf.initTaskDatatableBatch;
            },
            list() {
                return this.$store.state.wms_in.shelf.initTaskTabs;
            },

            //储位
            storeArea() {
                return this.$store.state.wms_in.shelf.storeArea;
            },

            //物流载具ID
            postVehicleID() {
                return this.$store.state.wms_in.shelf.postVehicleID;
            },

            currentBatchBarcodeList() {
                let currentBatchBarcodeList = [];//当前选择的批次对应的标签列表
                for (let item of this.list) {
                    if (item.batch == this.currentBatch) {
                        item.storeArea = this.storeArea;
                        item.postVehicleID = this.postVehicleID;
                        currentBatchBarcodeList.splice(-1, 0, item);
                    }
                }
                return currentBatchBarcodeList
            }
        },
        data() {
            return {
                columns: {'storeArea': '储位', 'barcode': '物料条码号', 'qty': '数量', 'postVehicleID': '物流载具'}
            }
        }
    }
</script>
