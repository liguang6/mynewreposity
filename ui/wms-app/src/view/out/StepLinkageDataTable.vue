
<template>
    <v-ons-page>
        <toolbar :title="'数据表'" :action="toggleMenu" ></toolbar>

        <v-ons-card>
      
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">             
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">物料号</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">批次</v-ons-col>
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">已扫数量</v-ons-col>
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">已扫箱数</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">操作</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" @click="gotoItem(mat)" v-for="(mat, $index) in list" :key="$index">
          
          

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BATCH}}</v-ons-col>

          <v-ons-col
            width="10%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BOX_QTY}}</v-ons-col>

          <v-ons-col
            width="10%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.boxNo}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
            @click="del(mat)"
          >删除</v-ons-col>   
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>


        <v-ons-bottom-toolbar class="bottom-toolbar">
          <!--  <v-ons-button @click="deleteRow">删除</v-ons-button> -->
            <v-ons-button @click="back">返回</v-ons-button>   
            <v-ons-button @click="confirm">确认过账</v-ons-button>   
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
        pageDataList: (state) => state.wms_out.pageDataList
        }),
        mounted(){
           this.setPage('StepLinkageDataTable')
           this.merge()     
        },  
        data() {
          return {
            msg:'没有相关数据..',
            list: [],
            listTemp: [],
          }
        }, 
        methods : {           
             ...mapMutations([
                'setPageCache','setPageDataList','setPage'
             ]),
            back(){
                this.$emit('gotoPageEvent','StepLinkage')
            },
            gotoItem(mat){
              this.pageDataList.forEach(ele=>{
                if(ele.outwerks==mat.outwerks&&ele.MATNR==mat.MATNR&&ele.WERKS==mat.WERKS)
                  this.listTemp.push(ele)
              })
              this.setPageCache(this.listTemp)
              this.$emit('gotoPageEvent','StepLinkageLabelDataTable')
            },
            confirm(){
              this.$emit('gotoPageEvent','StepLinkageHandover')
            },
            del(mat){
              if(event.stopPropagation)
                event.stopPropagation();
              else 
                event.cancelBubble = true;
  
              let arr = this.pageDataList.filter(ele=>
                 !(mat.MATNR == ele.MATNR&&mat.BATCH == ele.BATCH)
              )
              this.setPageDataList(arr)
              this.merge()
            },
            merge(){
              let arr = [];                
                this.pageDataList.forEach(ele=>{
                  ele = JSON.parse(JSON.stringify(ele))
                  if(arr.length == 0) 
                    arr.push(ele);
                  else{
                  let flag = true
                  for(let i=0;i<arr.length;i++){
                    if(arr[i].MATNR== ele.MATNR&&arr[i].BATCH == ele.BATCH){
                      arr[i].boxNo += ele.boxNo
                      arr[i].BOX_QTY += ele.BOX_QTY
                      flag = false
                      break
                    }
                  }
                  if(flag)
                    arr.push(ele);
                }  
                })
                this.list = arr
            }
        }
    }
</script>
