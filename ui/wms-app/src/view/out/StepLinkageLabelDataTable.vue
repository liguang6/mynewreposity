
<template>
    <v-ons-page>
        <toolbar :title="'条码明细'" :action="toggleMenu" ></toolbar>

        <v-ons-card>
      
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="40%" style="border-left-style: ridge;border-left-width: 1px;">条码</v-ons-col>
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">箱序</v-ons-col>
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>  
          <v-ons-col width="15%" style="border-left-style: ridge;border-left-width: 1px;">操作</v-ons-col>       
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in list" :key="$index">
          
          <v-ons-col
            width="40%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.LABEL_NO}}</v-ons-col>

          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BOX_SN}}</v-ons-col>

          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BOX_QTY}}</v-ons-col>

          <v-ons-col
            width="15%"
            style="border-left-style: ridge;border-left-width: 1px;"
            @click="del(mat)"
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
        pageCache: (state) => state.wms_out.pageCache,
        pageDataList: (state) => state.wms_out.pageDataList
        }),
        mounted(){               
                this.list = this.pageCache
        },  
        data() {
          return {
            msg:'没有相关数据..',
            list: []
          }
        }, 
        methods : {           
             ...mapMutations([
                'setPageCache','setPageDataList'
             ]),
            back(){
                this.$emit('gotoPageEvent','StepLinkageDataTable')
            },
            del(mat){
              let arr = this.pageCache.filter(ele=>
                 ele.LABEL_NO !== mat.LABEL_NO)             
              this.setPageCache(arr)
              this.list = this.pageCache
              let arr1 = this.pageDataList.filter(ele=>
                 ele.LABEL_NO !== mat.LABEL_NO) 
                 this.setPageDataList(arr1)
            }
                  
        }
    }
</script>
