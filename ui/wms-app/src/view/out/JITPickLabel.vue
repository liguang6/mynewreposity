
<template>
    <v-ons-page>
        <toolbar :title="'条码明细'" :action="toggleMenu" ></toolbar>

        <v-ons-card>
      
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">条码</v-ons-col>        
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">箱序</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">操作</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;"  v-for="(mat, $index) in this.list" :key="$index">
                 
          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.LABEL_NO}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BOX_SN}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BOX_QTY}}</v-ons-col>

          <v-ons-col
            width="20%" @click="deleteRow(mat.LABEL_NO,mat.BOX_QTY,mat.MATNR)"
            style="border-left-style: ridge;border-left-width: 1px;"
          >删除</v-ons-col>
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>


        <v-ons-bottom-toolbar class="bottom-toolbar">
          <!--  <v-ons-button @click="deleteRow">删除</v-ons-button> -->
            <v-ons-button @click="back">返回</v-ons-button>                
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import { mapActions, mapMutations,mapState } from 'vuex' 

    export default {
        props:['toggleMenu'],
        components:{toolbar},
        computed: mapState({       
        pageDataList: (state) => state.wms_out.pageDataList,
        pageCache: (state) => state.wms_out.pageCache, 
        pageData: (state) => state.wms_out.pageData, 
        }),
        mounted(){
          this.pageDataList.forEach(ele=>{
            if(ele.labelArr.length)
              this.list = ele.labelArr
          })
        },  
        data() {
          return {
            msg:'没有相关数据..',
            list: [],
          }
        }, 
        methods : {  
            ...mapMutations([
                'setPageCache','setPageDataList','setPageData'
             ]),         
            back(){
                this.$emit('gotoPageEvent','JITPickData')
            },
            deleteRow(LABEL_NO,BOX_QTY,MATNR){
                this.list = this.list.filter(ele=>{
                    return ele.LABEL_NO!= LABEL_NO
                })
                this.pageDataList.forEach(ele=>{
                  if(ele.labelArr.length)
                    ele.labelArr = this.list
                })
                let pc = this.pageCache
                pc = pc.filter(ele=>{
                  return ele.LABEL_NO != LABEL_NO
                })
                this.setPageCache(pc)

                let pd = this.pageData  
                        
                for(let ele of pd){ 
                  if(ele.MATNR == MATNR)
                    ele.BOX_QTY -= BOX_QTY                  
                }    
                for(let i=pd.length-1;i>=0;i--){
		              if(pd[i].BOX_QTY==0){
			              pd.splice(i,1);
		              }
	              }          
                this.setPageData(pd)
            }         
        }
    }
</script>
