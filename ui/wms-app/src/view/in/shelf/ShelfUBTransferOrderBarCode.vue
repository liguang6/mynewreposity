<template>
    <v-ons-page>
        <toolbar :title="'条码明细'" :action="toggleMenu"></toolbar>
        <data-table :dataTable="data" :columns="columns" ref="table"></data-table>
        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="del">删除</v-ons-button>
            <v-ons-button @click="back">返回</v-ons-button>
            <v-ons-button @click="confirm">确认</v-ons-button>
        </v-ons-bottom-toolbar>

        <v-ons-modal :visible="loading" >
            <p style="text-align: center">
                <v-ons-icon icon="fa-spinner" spin></v-ons-icon>
            </p>
        </v-ons-modal>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'

    export default {
        components:{DataTable, toolbar},
        props:['toggleMenu'],
        created(){

        },
        computed : {
            ub_in_inbound_no : {
                get(){
                    return this.$store.state.wms_in.shelf.ub_in_inbound_no;
                },
                set(v){
                    this.$store.commit('shelf/ub_in_inbound_no', v);
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
            data(){
              let dataTable = [];
              //根据选择的进仓单号过滤结果
                for(let label of this.labelList){
                    if(label.INBOUND_NO == this.ub_in_inbound_no.INBOUND_NO && label.INBOUND_ITEM_NO == this.ub_in_inbound_no.INBOUND_ITEM_NO){
                        let l = {"LABEL_NO":label.LABEL_NO,"BOX_SN":label.BOX_SN,"BOX_QTY":label.BOX_QTY};
                        dataTable.push(l);
                    }
                }
              return dataTable;
            }
        },
        data(){
            return {
                loading:false,
                dataTable:[],
                columns : {LABEL_NO:'条码',BOX_SN:'箱序',BOX_QTY:'数量'}
            }
        },
        methods : {
            del(){
                let indexArr  = this.$refs.table.selected();
                //删除标签
                this.labelList =  this.labelList.filter(v=> {
                    for(let index of indexArr){
                        if(this.data[index].LABEL_NO == v.LABEL_NO){
                            return false
                        }
                    }
                    return true;
                })
                this.$refs.table.clearSelect()
            },
            back(){
                this.$emit('gotoPageEvent','ShelfUBTransferOrderDataTable')
            },
            confirm(){

            }
        }
    }
</script>
