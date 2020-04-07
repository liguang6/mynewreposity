<template>
    <v-ons-page>
        <toolbar :title="'数据表'" :action="toggleMenu"/>
        <data-table  :dataTable="dataTable" :columns = "columns" :renders="renders" ref="tb" v-on:table-column-click="handleColumnClick"/>
        <v-ons-bottom-toolbar >
            <div class="bottom-toolbar">
                <v-ons-button @click="del">删除</v-ons-button>
                <v-ons-button @click="back">返回</v-ons-button>
                <v-ons-button @click="posting">确认过账</v-ons-button>
            </div>
        </v-ons-bottom-toolbar>
    </v-ons-page>

</template>

<script>
    import toolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'

    export default {
        components:{toolbar,DataTable},
        props:['toggleMenu'],
        data(){
            return {
                columns :{PO_NO:'采购订单',PO_ITEM_NO:"行号",LGORT:'库位',MATNR:'物料号',BOX_QTY:'已扫数量',LABEL_QTY:'已扫箱数'},
                renders : {
                    LABEL_QTY : function (val) {
                        return `<a style="color:red" href="javascript:void(1)">${val}</a>`
                    }
                }
            }
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
            dataTable(){
                if(this.labelList.length == 0)
                    return [];
                let d = new Map();
                for(let i of this.labelList){
                    //根据进仓单合并
                    let key = i.INBOUND_NO+":"+i.INBOUND_ITEM_NO;
                    if(d.has(key)){
                        d.get(key).BOX_QTY = parseInt(d.get(key).BOX_QTY) + parseInt(i.BOX_QTY);
                        d.get(key).LABEL_QTY = parseInt(d.get(key).LABEL_QTY) + 1;
                    }else {
                        let value = {"INBOUND_NO":i.INBOUND_NO,"INBOUND_ITEM_NO":i.INBOUND_ITEM_NO,"PO_NO":i.PO_NO,"PO_ITEM_NO":i.PO_ITEM_NO,"LGORT":i.LGORT,"MATNR":i.MATNR,"BOX_QTY":i.BOX_QTY,"LABEL_QTY":1};
                        d.set(key,value);
                    }
                }
                //返回根据进仓单合并之后的条码数据
                return Array.from(d.values());
            } ,
            labelList : {
                get(){
                    return this.$store.state.wms_in.shelf.ub_label_list;
                },
                set(v){
                    this.$store.commit('shelf/ub_label_list', v);
                }
            }
        },
        methods : {
            back(){
                this.$emit('gotoPageEvent','ShelfUBTransferOrder')
            },
            del(){
                let arr =  this.$refs.tb.selected()
                //删除整个进仓单的标签
                this.labelList = this.labelList.filter(v=> {
                   for(let i of arr){
                       if(this.dataTable[i].INBOUND_NO == v.INBOUND_NO && this.dataTable[i].INBOUND_ITEM_NO == v.INBOUND_ITEM_NO){
                           return false
                       }
                   }
                   return true;
                });
                this.$refs.tb.clearSelect()
            },
            posting(){
                if(this.$refs.tb.selected().length === 0){
                    this.$ons.notification.toast('请选择数据',{timeout:1000})
                    return ;
                }

                //跳转到过账页面
                //TODO: --> 有公用的页面，数据怎么传？
                this.$store.commit("setPage",'ShelfUBTransferOrderDataTable')
                this.$emit("gotoPageEvent","in_confirm");
            },
            handleColumnClick(obj){
                if(obj.column === 'LABEL_QTY'){
                    this.ub_in_inbound_no = this.dataTable[obj.index];
                    this.$emit('gotoPageEvent','ShelfUBTransferOrderBarCode')
                }
            }
        }
    }
</script>

<style>
    .bottom-toolbar {text-align: center}
    .bottom-toolbar ons-button {
        margin-left: 6px;
    }
</style>
