<template>
    <v-ons-page>
    <toolbar :title="'拣配下架'" :action="toggleMenu"></toolbar>
        <v-ons-list>
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        {{index+1}}/{{this.pageDataList.length}}
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>           
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        任务：
                    </v-ons-col>
                    <v-ons-col>{{this.pageCache.TASK_NUM}}</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        物料号：
                    </v-ons-col>
                    <v-ons-col>{{this.pageCache.MATNR}}</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            <v-ons-list-item>
            <v-ons-row>
                <v-ons-col>
                    物料描述：
                </v-ons-col>
                <v-ons-col>{{this.pageCache.MAKTX}}</v-ons-col>
            </v-ons-row>
            </v-ons-list-item>
            <v-ons-list-item>
            <v-ons-row>
                <v-ons-col>
                   推荐储位：               			
				</v-ons-col>         
                <v-ons-col>
					<v-ons-input v-model="this.pageCache.BIN_CODE"  width="95%"  placeholder="推荐储位"></v-ons-input>
                </v-ons-col>                
				<v-ons-col>        
                <v-ons-input v-model="storageCode"  width="95%" modifier="underbar" placeholder="储位条码" @blur="ValaStorage"></v-ons-input>
                </v-ons-col>
            </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item v-if="this.scannerBarcode === true">
                <v-ons-row>
                    <v-ons-col>
                        条码：
                    </v-ons-col>
                    <v-ons-col>
                        <v-ons-input placeholder="条码" v-model="labelNo" modifier="underbar"></v-ons-input>
                    </v-ons-col>
                    <v-ons-col>
                        <v-ons-button @click="scan" >扫描</v-ons-button>
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        需求类型：
                    </v-ons-col>
                    <v-ons-col>*****</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        容器类型：型号
                    </v-ons-col>
                    <v-ons-col>*****</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        批次：
                    </v-ons-col>                   
                    <v-ons-col><v-ons-input v-model="this.pageCache.BATCH"  width="95%"  placeholder="推荐批次"></v-ons-input></v-ons-col>  
                    <v-ons-col>        
                        <v-ons-input v-model="batch"  width="95%" modifier="underbar" placeholder="批次" readonly></v-ons-input>
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>              

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        推荐数量：
                    </v-ons-col>
                    <v-ons-col>{{this.pageCache.RECOMMEND_QTY}}</v-ons-col><!--QUANTITY -->
                    <v-ons-col>
                        实拣数量：
                    </v-ons-col>
                    <v-ons-col><v-ons-input v-model="confirmQty"  width="95%" modifier="underbar" @blur="modifyQty" @focus="focusQty" placeholder="实拣数量"></v-ons-input>
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item v-if="this.scannerBarcode === true">
                <v-ons-row>
                    <v-ons-col>
                        显示例外：
                    </v-ons-col>
                    <v-ons-col>
                        <v-ons-input placeholder="需更改批次或超发时使用" v-model="particular" @keypress="partic" modifier="underbar"></v-ons-input>
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
                      
        </v-ons-list>
    <v-ons-bottom-toolbar class="bottom-toolbar">
        <v-ons-button @click="bindNum">绑定物流器具</v-ons-button>
   <!--     <v-ons-button @click="modify">更改</v-ons-button>  -->
        <v-ons-button @click="next">下一个</v-ons-button>
        <v-ons-button @click="save">下架</v-ons-button>
        <v-ons-button @click="endShelf">结束下架</v-ons-button>
        <v-ons-button @click="gotoDataTable" v-if="this.scannerBarcode === true">数据表</v-ons-button>
    </v-ons-bottom-toolbar>
    <v-ons-dialog :visible.sync="dialogVisible" cancelable>
            <div style="text-align: center">
                <p>请扫描物流器具标签</p>
                <v-ons-input placeholder="标签" v-model="bindNo"> </v-ons-input>
                <v-ons-button @click="saveBindNum">确认</v-ons-button>
            </div>
        </v-ons-dialog>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import { mapActions, mapMutations,mapState } from 'vuex' 
    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        mounted(){
            if(this.scannerBarcode===false){
                this.storageCode = this.pageCache.BIN_CODE
                this.batch = this.pageCache.BATCH
                this.confirmQty = this.pageCache.RECOMMEND_QTY
            }
            if(sessionStorage.getItem('storageCode'))
                this.storageCode = sessionStorage.getItem('storageCode')
            if(sessionStorage.getItem('labelNo'))
                this.labelNo = sessionStorage.getItem('labelNo')
            if(sessionStorage.getItem('batch'))
                this.batch = sessionStorage.getItem('batch')
            if(sessionStorage.getItem('confirmQty'))
                this.confirmQty = sessionStorage.getItem('confirmQty')
            if(sessionStorage.getItem('labelNoArr'))
                this.labelNoArr = JSON.parse(sessionStorage.getItem('labelNoArr'))
            if(sessionStorage.getItem('labelList'))
                this.labelList = JSON.parse(sessionStorage.getItem('labelList'))    
            if(sessionStorage.getItem('addList1'))
                this.addList1 = JSON.parse(sessionStorage.getItem('addList1'))    
            if(sessionStorage.getItem('modifyList1'))
                this.modifyList1 = JSON.parse(sessionStorage.getItem('modifyList1'))    
        },
        computed: mapState({
        requirementNo: (state) => state.wms_out.requirementNo,
        scannerBarcode: (state) => state.wms_out.scannerBarcode,
        pageDataList: (state) => state.wms_out.pageDataList, 
        taskList: (state) => state.wms_out.taskList,
        flag: (state) => state.wms_out.flag,  
        pageCache: (state) => state.wms_out.pageCache,
        dataTable: (state) => state.wms_out.dataTable,
        totalQty: (state) => state.wms_out.totalQty,  
        }),
        data(){
            return {                
                index : 0,
                storageCode: '',
                batch: '',
                confirmQty: '',
                labelNo:'',
                labelList:[],
                WERKS: 'C161',
			    WH_NUMBER: 'C161',
                WT_STATUS: '00',
                labelNoArr: [],
                bindNo: '',
                dialogVisible: false,
                particular: '',
                addList1:[],
                modifyList1:[],
                newLabelNo:'',
                cqty:0,
            }
        },
        watch: {            
            batch: function (newVal, oldVal) {      
                 if(newVal!==''&&this.pageCache.BATCH!=newVal){
                    this.$ons.notification.toast('扫描条码批次与推荐批次不一致', {timeout: 2000})
                    this.batch = ''
                 }     
            }               
        },
        methods : {
             ...mapMutations([
                 'setTaskList','setFlag','setDataTable','setPageCache','setPageCache','setTotalQty'
             ]),
            ...mapActions([
                'selectCoreWHTask','scanLabel','pdaPicking','getLabel'
            ]),
            ValaStorage(){
                 if(this.pageCache.BIN_CODE!=this.storageCode){                    
                     this.$ons.notification.toast('扫描储位与推荐储位不一致', {timeout: 2000})
                     this.storageCode = ''
                 }else
                    sessionStorage.setItem('storageCode',this.storageCode) 
            },
            scan(){ 
                    let num = this.labelNoArr.indexOf(this.labelNo)
                    if(num===-1){
                        this.scanLabel({labelNo:this.labelNo}).then(res=>{
                            if(res.data.data){  
                            if(this.pageCache.BATCH==res.data.data.BATCH){
                                this.labelNoArr.push(this.labelNo)
                                this.confirmQty = (Number)(this.confirmQty)+(Number)(res.data.data.BOX_QTY)
                                this.batch = res.data.data.BATCH
                                sessionStorage.setItem('labelcopy',JSON.stringify(res.data.data)) 
                                sessionStorage.setItem('labelNo',this.labelNo)  
                                sessionStorage.setItem('batch',this.batch)  
                                sessionStorage.setItem('confirmQty',this.confirmQty)  
                                sessionStorage.setItem('labelNoArr',JSON.stringify(this.labelNoArr))  
                                let o = {'labelNo':this.labelNo,'QTY':res.data.data.BOX_QTY}
                                this.labelList.push(o)
                                sessionStorage.setItem('labelList',JSON.stringify(this.labelList))  
                                this.setDataTable(this.labelList)  
                            }else
                                this.$ons.notification.toast('条码批次与推荐批次不一致', {timeout: 2000})    
                            } 
                        })                            
                    }else
                        this.$ons.notification.toast('条码已扫描', {timeout: 2000})                                                                                    
            },
            focusQty(){
                this.cqty = this.confirmQty
                if(!this.newLabelNo)
                this.getLabel({WERKS:sessionStorage.getItem('UserWerks')}).then(res => { 
                        this.newLabelNo = res.data.labelNo
                })
            },
            modifyQty(){
                if(this.scannerBarcode==true&&this.cqty>this.confirmQty&&this.confirmQty==this.pageCache.RECOMMEND_QTY){            
                let cqty = this.cqty
                let qty = (this.dataTable[this.dataTable.length-1]).QTY
                       sessionStorage.setItem('confirmQty',this.confirmQty)  
                       if(cqty-qty==this.confirmQty){
                           this.labelNoArr = this.labelNoArr.filter(e=>{
                               return e != (this.dataTable[this.dataTable.length-1]).labelNo
                           })
                       this.setDataTable(this.dataTable.filter(ele=>{
                           return ele.labelNo != (this.dataTable[this.dataTable.length-1]).labelNo
                       })) 
                       }
                       if(cqty-qty<this.confirmQty){
                           let addl =[]
                           let modifyl= []
                           let obj = JSON.parse(sessionStorage.getItem('labelcopy'));
                           let no = (this.dataTable[this.dataTable.length-1]).labelNo
                           let o = {'labelNo':no,'BOX_QTY':cqty - this.confirmQty} //旧条码剩余数量
                           let insertLabel = {'LABEL_NO':this.newLabelNo,'F_LABEL_NO':no,'BOX_SN':'1/1','QTY':qty - (cqty - this.confirmQty),'BATCH':obj.BATCH,'BIN_CODE':obj.BIN_CODE,
                           'DEL':obj.DEL,'DOSAGE':obj.DOSAGE,'LGORT':obj.LGORT,'LIFNR':obj.LIFNR,'LIKTX':obj.LIKTX,'MAKTX':obj.MAKTX,'FULL_BOX_QTY':qty - (cqty - this.confirmQty),
                           'MATNR':obj.MATNR,'SOBKZ':obj.SOBKZ,'STATION':obj.STATION,'UNIT':obj.UNIT,'WERKS':obj.WERKS,'WH_NUMBER':obj.WH_NUMBER}
                           this.labelList = this.labelList.filter(ele=>{
                              return ele.labelNo != no
                           })
                           this.labelList.push({'labelNo':this.newLabelNo,'QTY':qty - (cqty - this.confirmQty)})
                           this.setDataTable(this.labelList)
                           this.addList1.push(insertLabel)
                           this.modifyList1.push(o)  
                           this.labelNoArr = this.labelNoArr.filter(ele=>{
                              return ele !=this.modifyList1[0].labelNo
                           })
                           this.labelNoArr.push(insertLabel.LABEL_NO) 
                           sessionStorage.setItem('labelNoArr',JSON.stringify(this.labelNoArr))  
                           sessionStorage.setItem('labelList',JSON.stringify(this.labelList)) 
                           sessionStorage.setItem('addList1',JSON.stringify(this.addList1))  
                           sessionStorage.setItem('modifyList1',JSON.stringify(this.modifyList1))   
                        }   
            }                                       
        },
            save(){   
                if(this.confirmQty==0){
                    this.$ons.notification.toast('下架数量不能为0', {timeout: 1000});
                        return
                }                       
                if(this.flag){
                    if(this.scannerBarcode&&this.labelNoArr.length==0){
                        this.$ons.notification.toast('请扫描条码', {timeout: 2000});
                        return
                    }
                    if(this.pageCache.BIN_CODE!==this.storageCode){
                       this.$ons.notification.toast('储位不一致', {timeout: 2000});
                        return
                    }
                    if(!isNaN(this.confirmQty)&&(Number)(this.confirmQty)>(Number)(this.pageCache.RECOMMEND_QTY)&&isNaN(this.labelNo)){
                        this.$ons.notification.toast('条码数量大于推荐数量,请更改条码数量', {timeout: 2000});
                        return
                    }
                    if(this.scannerBarcode&&!isNaN(this.labelNo)){

                    }
                    if(this.confirmQty>this.pageCache.RECOMMEND_QTY){
                        this.$ons.notification.toast('下架数量大于推荐数量', {timeout: 2000})
                        return
                    }                      
                    let ele = this.pageDataList[this.index]
                        ele.LABEL_NO = (JSON.parse(JSON.stringify(this.labelNoArr))).join(',')
                        if(this.addList1.length){
                            this.labelNoArr = this.labelNoArr.filter(e=>{
                            return e !=this.addList1[0].LABEL_NO
                             })
                        }
                         ele.LABEL = JSON.stringify(this.labelNoArr)
                        //ele.LABEL = (JSON.parse(ele.LABEL_NO)).join(',')
                        //ele.QUANTITY = this.confirmQty
                        //ele.CONFIRM_QUANTITY = this.confirmQty
                        if((Number)(this.confirmQty)+(Number)(ele.QTY_XJ)==ele.QTY)
                            ele.flag = true
                        else
                            ele.flag = false
                        ele.RECOMMEND_QTY = this.confirmQty
                        //ele.WT_STATUS = '02'                        
                        //ele.TASK_NUM = this.taskList[this.index].TASK_NUM                       
                        let list = [ele]
                        this.pdaPicking({REQUIREMENT_NO:this.requirementNo,WERKS:sessionStorage.getItem('UserWerks'),WH_NUMBER:sessionStorage.getItem('UserWhNumber'),SAVE_DATA:list,addList:this.addList1,modifyList:this.modifyList1}).then(res => {  
                            if(res.data.code==0) {
                                this.$ons.notification.toast('下架成功', {timeout: 2000});
                                this.setFlag(false)
                                this.setTotalQty((Number)(this.totalQty)+(Number)(this.confirmQty))
                                //this.setDataTable(list)
                            } 
                            if(res.data.code==500) {
                                //this.labelNo = ''  
                                //this.labelNoArr = []   
                                //this.confirmQty = ''  
                                //ele.WT_STATUS = '00'           
                                this.$ons.notification.toast('下架失败', {timeout: 2000});                               
                            }                         
                        })      
                }else{
                     this.$ons.notification.toast('已下架', {timeout: 2000});
                }            
            },        
            gotoDataTable(){                    
                this.$emit('gotoPageEvent','UnShelfViewScannerDataTable')      
            },
            next(){
                if(this.scannerBarcode==true){             
                    this.index  = this.index + 1
                    if(this.index  < this.pageDataList.length){
                        this.setPageCache(this.pageDataList[this.index]) 
                        this.labelNo = ''  
                        this.labelNoArr = []   
                        this.confirmQty = ''  
                        this.storageCode = ''
                        this.batch = ''    
                        this.newLabelNo = ''
                        this.labelList = []
                        this.addList1=[]
                        this.modifyList1=[]
                        this.setDataTable([])
                        this.setFlag(true)      
                    }else{
                        this.index = this.pageDataList.length-1
                        this.setFlag(false)
                        this.$ons.notification.toast('当前页是最后一页', {timeout: 2000});
                    }
                }else{
                    this.index  = this.index + 1
                    if(this.index  < this.pageDataList.length){
                        this.setPageCache(this.pageDataList[this.index])
                        this.storageCode = this.pageCache.BIN_CODE
                        this.batch = this.pageCache.BATCH
                        this.confirmQty = this.pageCache.RECOMMEND_QTY
                        this.setFlag(true)  
                    }else{
                        this.index = this.pageDataList.length-1
                        this.setFlag(false)
                        this.$ons.notification.toast('当前页是最后一页', {timeout: 2000});
                    } 
                }               
            },
            endShelf(){ 
                this.$emit('gotoPageEvent','UnShelfViewRecommendEnd')                  
            },
            bindNum(){
                this.dialogVisible = true  
            },
            saveBindNum(){                             
                let ele = this.pageDataList[this.index]
                ele.MOULD_NO = this.bindNo                       
                this.bindNo = ''
                this.dialogVisible = false
            },
            partic(){
                //例外代码  
                if (13 === window.event.keyCode) {
                    if(this.particular.trim()  === ''){
                        this.$ons.notification.toast('输入不能为空', {timeout: 2000});
                    }else{
                        console.log(this.particular)
                    }
                }
            }
        }
    }
</script>
