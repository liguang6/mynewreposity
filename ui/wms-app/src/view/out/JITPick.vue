<template>
    <v-ons-page>
    <toolbar :title="'总装JIT拣配'" :action="toggleMenu"></toolbar>
    <v-ons-card>
        <v-ons-list>
            <v-ons-list-item>
                <div class="left" style="width:30%">
                    配送单号：
                </div>
                <div class="center" >
                    <v-ons-input placeholder="配送单号" v-model="DELIVERY_NO"></v-ons-input>
                </div>  
                <div class="right" >
                    <v-ons-button @click="scanDeliveryNo" style="margin: 6px 0">扫描</v-ons-button>
                </div>             
            </v-ons-list-item>
            <v-ons-list-item>
                <div class="left" style="width:30%">
                   条码：
                </div>
                <div class="center">
                    <v-ons-input placeholder="条码" v-model="LABEL_NO"></v-ons-input>
                </div>
                <div class="right" >
                    <v-ons-button @click="scanLabel" style="margin: 6px 0">扫描</v-ons-button>                   
                </div>
            </v-ons-list-item>
        </v-ons-list>
    </v-ons-card>

    <v-ons-card>
      <p v-if="this.list.length===0||this.list.length===undefined">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0&&this.list.length">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">物料号</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">物料描述</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">工位</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in list" :key="$index">
          
          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MAKTX}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BOX_QTY}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.PRO_STATION}}</v-ons-col>
             
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="getData">数据表</v-ons-button>&nbsp;&nbsp;
            <v-ons-button @click="reset" style="margin: 6px 0">重置</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import { mapActions, mapMutations,mapState } from 'vuex'
    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        computed: mapState({
            pageDataList: (state) => state.wms_out.pageDataList, //配送单行项目信息
            pageData: (state) => state.wms_out.pageData,    //条码信息 合并相同物料
            pageCache: (state) => state.wms_out.pageCache, //条码信息 不合并
            deliveryNo: (state) => state.wms_out.deliveryNo
        }),       
        data(){
            return {
                DELIVERY_NO:'',
                LABEL_NO: '',
                list:[],//条码信息 合并相同物料
                list1:[],//条码信息 不合并相同
                msg:'请扫描条码'                    
            }
        },
        mounted(){
            this.list = this.pageData
            this.list1 = this.pageCache          
            this.DELIVERY_NO = this.deliveryNo
        },       
        methods : {
            ...mapActions([
                'JITScanLabel','JITScanDeliveryNo'
            ]),
            ...mapMutations([
                 'setPageDataList','setPageCache','setDeliveryNo','setPageData'
             ]),
            scanDeliveryNo(){
                this.reset()
                this.setDeliveryNo(this.DELIVERY_NO)
                              
                this.JITScanDeliveryNo({DELIVERY_NO:this.DELIVERY_NO}).then(res=>{
                    let arr = res.data.data
                    if(arr.length){
                        this.setPageDataList(arr)
                        this.$ons.notification.toast('配送单号扫描完成,请扫描条码!', {timeout: 2000})
                    }else
                        this.$ons.notification.toast('配送单号不存在!', {timeout: 2000})                   
                })
            },
            scanLabel(){
                this.JITScanLabel({LABEL_NO:this.LABEL_NO,werks:sessionStorage.getItem('UserWerks')}).then(res=>{
                    if(!this.pageDataList.length){
                        this.$ons.notification.toast('未扫描配送单号!', {timeout: 2000})
                        return
                    }
                    let rtnFlag = false
                    this.list1.forEach(ele=>{
                        if(ele.LABEL_NO==this.LABEL_NO){
                            rtnFlag = true
                            this.$ons.notification.toast('条码已扫描!', {timeout: 2000})
                        }
                    })
                    if(rtnFlag)
                        return
                    let arr = res.data.data
                    let matFlag = false
                    let staFlag = false
                    if(!arr.length){
                        this.$ons.notification.toast('工厂不存在条码信息或条码已下架!', {timeout: 2000})
                        return
                    }
                    if(arr.length){
                        this.pageDataList.forEach(ele=>{
                            if(ele.MATNR==arr[0].MATNR){
                                matFlag=true
                                if(arr[0].PRO_STATION&&ele.POINT_OF_USE==arr[0].PRO_STATION){
                                    staFlag = true
                                }
                                 if(!arr[0].PRO_STATION){
                                     arr[0].PRO_STATION = ele.POINT_OF_USE
                                     staFlag = true
                                 }
                            }                          
                        })                       
                    }   
                    if(!matFlag)   
                        this.$ons.notification.toast('条码不存在或配送单号不存在该物料!', {timeout: 2000})
                    if(matFlag==true&&staFlag==false) 
                        this.$ons.notification.toast('条码物料工位与配送单号工位不一致!', {timeout: 2000}) 
                    if(matFlag&&staFlag){
                        let QTY = 0
                        let lQTY = 0
                        let putFlag = true

                        //汇总相同物料需求数量
                        this.pageDataList.map(ele=>{
                            if(ele.MATNR==arr[0].MATNR)
                                QTY +=(+ele.QTY)
                        })
                        this.list1.map(ele=>{
                            if(ele.MATNR==arr[0].MATNR)
                                lQTY +=(+ele.BOX_QTY)
                        })
                        let qty = (Number)(arr[0].BOX_QTY)
                        if(lQTY+qty>QTY){
                            this.$ons.notification.toast('条码数量过大:'+qty+'<br/>'+'已扫:'+lQTY+',需求:'+QTY ,{timeout: 2000}) 
                            return
                        }
                        this.list1.push(JSON.parse(JSON.stringify(arr[0])))
                        
                        if(this.list.length==0)
                            this.list.push(JSON.parse(JSON.stringify(arr[0])))
                        else{
                           this.list.forEach(ele=>{
                                if(ele.MATNR==arr[0].MATNR){
                                    putFlag = false
                                    ele.BOX_QTY+=(+arr[0].BOX_QTY)
                                }                                   
                            })
                            if(putFlag)
                                this.list.push(JSON.parse(JSON.stringify(arr[0])))
                        }                       
                        this.setPageCache(this.list1)
                        this.setPageData(this.list)
                    }                                      
                })
            },
            getData(){
                this.$emit('gotoPageEvent','JITPickData')
            },
            reset(){
                this.setDeliveryNo('')
                this.setPageDataList([])
                this.setPageCache([])
                this.setPageData([])
                this.list = []
                this.list1 = []
            }
        }
    }
</script>
