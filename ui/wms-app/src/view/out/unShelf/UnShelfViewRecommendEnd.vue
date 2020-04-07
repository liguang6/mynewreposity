<template>
    <v-ons-page>
        <toolbar :title="'拣配结果'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        需拣配物料数:
                    </v-ons-col>
                    <v-ons-col>
                        {{this.QTY}}
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            
            <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                       已拣配物料数:
                   </v-ons-col>
                   <v-ons-col>
                       {{this.confirmQty}}
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>

  <!--          <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                       已下架物料数:
                   </v-ons-col>
                   <v-ons-col>
                       {{this.xj}}
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>   -->
            
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        未拣配物料数:
                    </v-ons-col>
                    <v-ons-col>
                        {{this.QTY-this.confirmQty}}
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="back">确认</v-ons-button>
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
        pageDataList: (state) => state.wms_out.pageDataList,
        totalQty: (state) => state.wms_out.totalQty,         
        }),
        mounted(){                               
            this.pageDataList.forEach(ele => {                   
                   // this.QTY+=(Number)(ele.QTY)
                   // this.xj+=(Number)(ele.QTY_XJ)
            })
           // this.confirmQty = this.totalQty
            let arr = []
            for(let i=0;i<this.pageDataList.length;i++){
                if(arr.length==0)
                   arr.push(this.pageDataList[i])
                else{
                   for(let j=0;j<arr.length;j++){
                       let flag = true
                      if(this.pageDataList[i].MATNR==arr[j].MATNR){
                         arr[j].flag = arr[j].flag&&this.pageDataList[i].flag
                         flag = false 
                         break 
                      }  
                      if(flag&&(j==arr.length-1))
                        arr.push(this.pageDataList[i])                   
                   }
                   }                
            }
            this.QTY = arr.length
            arr.forEach(ele=>{
                if(ele.flag)
                    this.confirmQty=(Number)(this.confirmQty)+1
            })
        },
        data(){
            return{
                QTY:0,
                confirmQty:0,
                xj:0
            }
        },
        methods : {
            ...mapMutations([
                 'setFlag','setDataTable','setPageCache','setPageDataList','setTotalQty'
             ]),
            back (){
                //this.setTaskList([])
                this.setPageDataList([])
                this.setDataTable([])
                this.setPageCache('')
                this.setFlag(true)
                //this.setTotalQty(0)
                sessionStorage.removeItem('storageCode')  
                sessionStorage.removeItem('labelNo')  
                sessionStorage.removeItem('batch')  
                sessionStorage.removeItem('confirmQty')  
                sessionStorage.removeItem('labelNoArr')  
                sessionStorage.removeItem('labelList') 
                sessionStorage.removeItem('addList1') 
                sessionStorage.removeItem('modifyList1') 
                this.$emit('gotoPageEvent','UnShelfViewSelect')
            }
        }
    }
</script>
