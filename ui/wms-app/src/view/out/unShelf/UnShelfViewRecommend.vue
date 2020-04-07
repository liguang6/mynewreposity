<template>
    <v-ons-page>
        <toolbar :title="'拣配下架顺序'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <v-ons-list-item>
                <div class="right">
            <!--        待下架数量 : {{this.waitShelfQty}}  -->
                </div>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>单号 <br/> 推荐储位</v-ons-col>
                    <v-ons-col>物料号 <br/> 批次</v-ons-col>
                    <v-ons-col >数量</v-ons-col>
                    <v-ons-col >已下架数量</v-ons-col>
                    <v-ons-col >推荐数量</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item v-for="(item,$index) in pageDataList" :key="$index">
                <v-ons-row>
                    <v-ons-col>{{item.REQUIREMENT_NO}} <br/> {{item.BIN_CODE}}</v-ons-col>                  
                    <v-ons-col>{{item.MATNR}} <br/> {{item.BATCH}}</v-ons-col>
                    <v-ons-col>{{item.QTY}}</v-ons-col>
                    <v-ons-col>{{Number(item.QTY_XJ)}}</v-ons-col>
                <!--    <v-ons-col>{{isNaN(item.XJ) ? item.QTY : (Number)(item.QTY)-(Number)(item.XJ)}}</v-ons-col>  -->
                    <v-ons-col>{{item.RECOMMEND_QTY}} </v-ons-col>               
                </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="planShelfOrder">重新规划</v-ons-button>
            <v-ons-button @click="startShelf">开始下架</v-ons-button>
        </v-ons-bottom-toolbar>

        <v-ons-dialog :visible.sync="dialogVisible" cancelable>
            <div style="text-align: center">
                <p>请扫描定位标签</p>
                <v-ons-input placeholder="标签" v-model="binCode"> </v-ons-input>
                <v-ons-button @click="sortCode">确认</v-ons-button>
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
        computed: mapState({
        requirementNo: (state) => state.wms_out.requirementNo,
        pageDataList: (state) => state.wms_out.pageDataList,
        sortNo: (state) => state.wms_out.sortNo       
        }),
        mounted(){
            let temp = [];
            this.pageDataList.forEach(ele => {
                if(temp.indexOf(ele.MATNR)==-1){
                   temp.push(ele.MATNR)                             //QTY_XJ
                //    this.waitShelfQty = isNaN(ele.XJ) ? (Number)(ele.QTY)+(Number)(this.waitShelfQty) : (Number)(ele.QTY-ele.XJ)+(Number)(this.waitShelfQty)
             
                }   
                
            }) 
             this.pageDataList.forEach(ele=>{
                        this.querySeqNo({binCode:ele.BIN_CODE}).then(res=>{
                        ele.SEQNO = res.data.data.SEQNO  
                        })
                    })

        },
        data(){
            return {
               dialogVisible: false,
               binCode: '',
               WERKS: 'C161',
			   WH_NUMBER: 'C161',
               waitShelfQty: 0           
            }
        },
        methods : {
            ...mapActions([
                'saveWHTask','querySeqNo'
            ]),
             ...mapMutations([
                 'setPageDataList','setPageCache'
             ]),
            compare(property){ 
                return function(a,b){                     
                    var value1 = a[property]; 
                    var value2 = b[property];                    
                    return value1 - value2; 
                    } 
            },
            planShelfOrder(){               
                this.dialogVisible = true                   
            },
            startShelf(){
                if(this.sortNo) {
                    this.binCode = this.sortNo
                    this.sortCode();
                    this.setPageCache(this.pageDataList[0])
                }
                this.$emit('gotoPageEvent','UnShelfViewRecommendStart')            
            },
             sortCode(){
                if(this.binCode){  
                    let indexArr = []
                    let temp = 0 
                    this.pageDataList.sort(this.compare('SEQNO'))            
                this.pageDataList.forEach(ele=>{
                     indexArr.push(ele.SEQNO)
                            if(ele.BIN_CODE===this.binCode){
                                 temp = ele.SEQNO
                             }
                })                                                     
                let index = indexArr.indexOf(temp)
                if(index!=-1){  
                    let mid = Math.ceil((indexArr.length)/2)
                    let arr = []  
                    if(index<mid){
                        if(index==0)
                            arr= this.pageDataList
                        else{
                            arr = (this.pageDataList.splice(0,index+1)).reverse()
                            arr = arr.concat(this.pageDataList)
                        }                 
                   }else{
                        if(index==this.pageDataList.length-1)
                        arr= this.pageDataList.reverse()                    
                        else{
                            let t =  this.pageDataList.splice(0,index)
                            arr = this.pageDataList.concat(t.reverse())
                        } 
                } 
                this.setPageDataList(arr)     
                }               
            }                                      
        }
        }    
    }
</script>
