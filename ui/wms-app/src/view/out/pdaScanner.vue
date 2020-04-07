<!--
PDA扫描条码出库
QIU JIAMING1
2019-04-15
-->
<template>
    <v-ons-page id="shelf-init">
        <custom-toolbar :title="'PDA扫描条码出库'" :action="toggleMenu"></custom-toolbar>
       <v-ons-card>
        <v-ons-row class="in-row">
            <v-ons-col width="25%">物流载具ID: </v-ons-col>
               <v-ons-col width="55%">
                   <v-ons-input placeholder="扫描物流载具" v-model="postVehicleID" type="text" modifier="material"></v-ons-input>
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

        <v-ons-row class="in-row">
            <v-ons-col width="25%"><span class="red-star">*</span>业务类型: </v-ons-col>
            <v-ons-col width="55%">
              <v-ons-select style="width: 30%"   id="type" name="业务类型" v-model="type" @change="selectType($event)" modifier="material">
                  <option value="">请选择</option>
                  <option v-for="(item, i) in typeList" :value="item.CODE" :name="item.CODE"  :type="item.CODE" v-bind:key="i">
                {{ item.VALUE }}
              </option>
            </v-ons-select>
            </v-ons-col>
            <v-ons-col width = '20%'></v-ons-col>
        </v-ons-row>

       </v-ons-card>
        <data-table :dataTable="list" :columns="cols" :hiddenSelect="'true'"></data-table>
        <v-ons-bottom-toolbar>
            <div style="text-align: center"><v-ons-button modifier="cta" @click="toDataTable">数据表</v-ons-button></div>
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
            this.getTypeList()
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
            postVehicleID : {
                get(){
                    return this.$store.state.wms_in.shelf.postVehicleID
                },
                set(val){
                    this.$store.commit('shelf/setPostVehicleID',val)
                }
            },
            userName:{
                get(){
                    return this.$store.state.user.userName
                }
            }
        },
        methods : {
        ...mapMutations([
            'setPageDataList'
          ]),
        ...mapActions([
            'listByPdaBarcode','queryBarcodeOnly','getBusinessList'
          ]),
            updateBarcode(e){
                this.$store.commit('setBarCode',e.target.value)
            },
            toDataTable(){
                if(this.list.length === 0){
                    this.$ons.notification.toast('数据不存在',{timeout:1000})
                }else{
                    this.listData.forEach(ele=>{
                        ele.BUSINESS_NAME = this.type
                    })
                    this.setPageDataList(this.listData)
                    this.$emit('gotoPageEvent','pdaScannerDataTable')
                }
            },
            getTypeList(){        
                this.getBusinessList({werks:sessionStorage.getItem('UserWerks'),businessClass:'07'}).then(res =>{
                    if(0 === res.data.code){
                        this.typeList = res.data.data;                          
                    }else
                        this.$ons.notification.toast('系统异常，获取业务类型失败！', {timeout: 1000});                   
                }).catch(error => {
                    console.log(error);
                    this.$ons.notification.toast('获取业务类型失败！', {timeout: 1000});
                })               
            },
            selectType(event){  
                this.type = event.target.options[event.target.options.selectedIndex].getAttribute('type');
            },
            //条码扫描
            scannerBarcode(){
                this.$validator.validateAll().then(result => {
                    if(result){
                            //调用action
                            this.listByPdaBarcode({barcode:this.barcode}).then(res => {
                                if(res.data.length > 0){
                                    this.queryBarcodeOnly({barcode:this.barcode}).then(ress =>{
                                    if(ress.data.length===0){
                                        var tagList  = [];
                                        var tagListToTable  = [];
                                        var dataNew = {"LABEL_NO":res.data[0].LABEL_NO,"WERKS":res.data[0].WERKS,"MAKTX":res.data[0].MAKTX,"MATNR":res.data[0].MATNR,
                                        "BOX_QTY":res.data[0].BOX_QTY,"UNIT":res.data[0].UNIT,"postVehicleID":this.postVehicleID};
                                        tagList= dataNew;
                                        var dataNewToTable = {"LABEL_NO":res.data[0].LABEL_NO,"WERKS":res.data[0].WERKS,"MAKTX":res.data[0].MAKTX,"MATNR":res.data[0].MATNR,
                                        "BOX_QTY":res.data[0].BOX_QTY,"BOX_SN":res.data[0].BOX_SN,"postVehicleID":this.postVehicleID,"UNIT":res.data[0].UNIT,"BATCH":res.data[0].BATCH};
                                        tagListToTable= dataNewToTable;

                                        if(this.list.length>0){
                                            let indexList = [];
                                            this.list.forEach(ele => {
                                            indexList.push(ele.LABEL_NO);
                                            }); 
                                            let i = indexList.indexOf(this.barcode);
                                            if (i == -1) {
                                                this.list.push(tagList);
                                                this.listData.push(tagListToTable);
                                            } else {
                                                this.$ons.notification.toast('请勿重复扫描!', {timeout: 1000});                                            
                                            }
                                        }    
                                        if(0 === this.list.length){
                                            this.list.push(tagList);
                                            this.listData.push(tagListToTable);
                                        } 
                                }else{
                                    this.$ons.notification.toast('条码已经存在缓存表!', {timeout: 1000});
                                }                                                              
                                })
                            }else{
                                this.$ons.notification.toast('条码不存在!', {timeout: 1000});
                                
                            }
                            });                           
                    }else{
                        this.$ons.notification.toast('请扫描或输入条码!',{timeout:1000})
                    }
                })
            }
        },
        data () {
            return {
                cols : {LABEL_NO:'条码号',WERKS:'工厂',MATNR:'料号',MAKTX:'物料描述',BOX_QTY:'装箱数量',UNIT:"单位",postVehicleID:'工装器具'},
                list : [],
                listData:[],
                testCode : '',
                type:'',
                typeList:[]
            }
        }
    }
</script>


<style>
    .red-star { color: red }
    .in-row {margin: 6px 0;}
</style>
