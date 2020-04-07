<!--
上架首页
Yang Lin
2019-03-29
-->
<template>
    <v-ons-page id="shelf-init">
        <custom-toolbar :title="'创建外购进仓单'" :action="toggleMenu"></custom-toolbar>
       <v-ons-card>
        <v-ons-row class="in-row">
            <v-ons-col width="25%"><span class="red-star">* </span>条码: </v-ons-col>
            <v-ons-col width="55%">
                <v-ons-input placeholder="扫描条码" type="text"  v-model="barcode" name="条码" v-validate="'required'"></v-ons-input>
            </v-ons-col>
            <v-ons-col width = '20%'><v-ons-button @click="scannerBarcode">扫描</v-ons-button></v-ons-col>
        </v-ons-row>

           <v-ons-row class="in-row">
               <v-ons-col width="25%">物流载具ID: </v-ons-col>
               <v-ons-col width="55%">
                   <v-ons-input placeholder="扫描条码" v-model="postVehicleID" type="text"></v-ons-input>
               </v-ons-col>
               <v-ons-col width = '20%'><v-ons-button>扫描</v-ons-button></v-ons-col>
           </v-ons-row>

           <v-ons-row class="in-row">
               <v-ons-col width="25%">储位: </v-ons-col>
               <v-ons-col width="55%">
                   <v-ons-input type="text" v-model="storeArea" placeholder="储位"></v-ons-input>
               </v-ons-col>
               <v-ons-col width = '20%'></v-ons-col>
           </v-ons-row>
       </v-ons-card>

        <data-table :dataTable="list" :columns="cols" :hiddenSelect="'true'"></data-table>


        <v-ons-bottom-toolbar>
            <div class="bottom-toolbar">
                <v-ons-button modifier="cta" @click="toDataTable">数据表</v-ons-button>
                <v-ons-button  @click="back">返回</v-ons-button>
            </div>
        </v-ons-bottom-toolbar>

    </v-ons-page>
</template>

<script>
    import customToolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import {queryInInbound} from '@/api/in.js'

    export default {
        components:{customToolbar,DataTable},
        props : ['toggleMenu'],
        created(){
          this.setBarcodeFlag();
        },
        computed : {
            //条码
            barcode : {
                get(){
                    return this.$store.state.wms_in.shelf.barcode
                },
                set(val){
                    this.$store.commit('shelf/setBarCode',val)
                }
            },
            //物流载具ID
            postVehicleID : {
                get(){
                    return this.$store.state.wms_in.shelf.postVehicleID
                },
                set(val){
                    this.$store.commit('shelf/setPostVehicleID',val)
                }
            },
            //存储区/储位
            storeArea : {
                get(){
                    return this.$store.state.wms_in.shelf.storeArea;
                },
                set(v){
                    this.$store.commit('shelf/setStoreArea',v);
                }
            },
            //标签管理标识
            barcodeFlag(){
                return this.$store.state.wms_in.shelf.barcodeFlag;
            },
            //工厂
            werks(){
                return sessionStorage.getItem("UserWerks");
            },
            //仓库
            whNumber(){
                return sessionStorage.getItem("UserWhNumber");
            },
            //表格字段
            cols(){
                return this.$store.state.wms_in.shelf.initTaskCols;
            },
            //表格数据
            list: {
                get(){
                    return this.$store.state.wms_in.shelf.initTaskTabs;
                },
                set(v){
                    this.$store.commit('shelf/setInitTaskTabs',v);
                }
            }
        },
        methods : {
            toDataTable(){
                if(this.list.length === 0){
                    this.$ons.notification.toast('数据不存在',{timeout:1000})
                }else{
                    this.$emit('gotoPageEvent','ShelfInitTaskDataTable')
                }
            },
            //条码扫描
            scannerBarcode(){
                this.$validator.validateAll().then(result => {
                    if(result){
                        //没有开启标签管理的工厂不能扫描
                        if(this.barcodeFlag !== true){
                            this.$ons.notification.toast("工厂没有开启条码管理，不能扫描标签入库",{timeout:1000});
                            return;
                        }
                        //标签是否已经扫描过了
                        for(let i of this.list){
                            if(i.barcode == this.barcode){
                                this.$ons.notification.toast("标签已扫描",{timeout:1000});
                                return;
                            }
                        }
                        //获取条码信息
                        this.$store.dispatch('shelf/scannerbarcode',this.barcode).then(data => {
                            if(data.code != '0'){
                                this.$ons.notification.toast(data.msg,{timeout:1000});
                            }else {
                                let barcode = data.data[0];
                                if(barcode.WERKS != this.werks || barcode.WH_NUMBER != this.whNumber){
                                    this.$ons.notification.toast("标签不属于"+this.werks+"工厂",{timeout:1000});
                                }
                                else if(barcode.LABEL_STATUS != '02' && barcode.LABEL_STATUS != '03'){
                                    this.$ons.notification.toast("标签状态不是免检或者已质检状态",{timeout:1000});
                                }
                                else{
                                    //查询进仓单是否存在
                                    queryInInbound({"LABEL_NO":this.barcode}).then(d=>d.data).then(d=>{
                                        if(d.code == '0'){
                                            if(d.data.length !== 0){
                                                let inInbound = d.data[0];
                                                //进仓单不是 已关闭状态，且进仓数量不为0
                                                if(inInbound.ITEM_STATUS != '04' && (inInbound.IN_QTY != null && inInbound.IN_QTY != 0 && inInbound.IN_QTY != undefined)){
                                                    this.$ons.notification.toast("标签已经创建了进仓单,不能重复进仓",{timeout:1000});
                                                    return;
                                                }
                                            }
                                        }else {
                                            //TODO: 校验数量是否大于收货单允许的最大进仓数量

                                            let barcodeItem = {barcode:barcode.LABEL_NO,batch:barcode.BATCH,vendor:barcode.LIFNR,vendorName:barcode.LIKTX,qty:barcode.BOX_QTY}
                                            this.list.splice(-1,0,barcodeItem);//TIP: 使用splice方法更新列表，触发vue响应
                                            this.barcode = "";
                                        }
                                    })

                                }
                            }
                        })
                    }else {
                        this.$ons.notification.toast(this.$validator.errors.first('条码'),{timeout:1000})
                    }
                })
            },
            setBarcodeFlag(){
                let WERKS = this.werks;//用户登录时设置
                let WH_NUMBER = this.whNumber;//用户登录时设置
                if(WERKS == null || WERKS == undefined || WERKS == ''){
                    this.$ons.notification.toast("没有设置工厂",{timeout:1000})
                    return ;
                }
                if(WH_NUMBER == null || WH_NUMBER == undefined || WH_NUMBER == ''){
                    this.$ons.notification.toast("没有设置仓库",{timeout:1000})
                    return ;
                }
                this.$store.dispatch("shelf/setBarcodeFlag",{WERKS:WERKS,WH_NUMBER:WH_NUMBER})
            },
            back(){
                this.$emit('gotoPageEvent',"home");
                this.$store.commit('tabbar/set',1);
            }
        }
    }
</script>


<style>
    .red-star { color: red }
    .in-row {margin: 6px 0;}
</style>
