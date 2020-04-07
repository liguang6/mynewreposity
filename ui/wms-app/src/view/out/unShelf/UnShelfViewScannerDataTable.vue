
<template>
    <v-ons-page>
        <toolbar :title="'拣配下架'" :action="toggleMenu" ></toolbar>

        <v-ons-card>
      
      <v-ons-list>
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">
  <!--          <v-ons-checkbox v-model="ischeck" @click="checkall"></v-ons-checkbox>   -->
          </v-ons-col>        
          <v-ons-col width="70%" style="border-left-style: ridge;border-left-width: 1px;">条形码</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in this.dataTable" :key="$index">
          <v-ons-col width="10%" style="border-left-style: ridge;border-left-width: 1px;">
            <v-ons-checkbox
              :input-id="'checkbox-' + $index"
              :value="$index"
              v-model="checkDataList"
            ></v-ons-checkbox>
          </v-ons-col>
          <v-ons-col
            width="70%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.labelNo}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.QTY}}</v-ons-col>            
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>
        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="deleteRow">删除</v-ons-button>
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
        dataTable: (state) => state.wms_out.dataTable
        }),  
        data() {
          return {
             checkDataList: []
          };
        },   
        methods : {
            back(){
                this.$emit('gotoPageEvent','UnShelfViewRecommendStart')
            },
            deleteRow(){
              let qty = 0
              let totalQty = 0 
              let labelNoArr = JSON.parse(sessionStorage.getItem('labelNoArr')) 
              let labelList = JSON.parse(sessionStorage.getItem('labelList')) 
              this.dataTable.forEach(ele=>{
                  totalQty+=ele.QTY
              }) 
              this.checkDataList.sort(function(a, b) { return b - a})
              this.checkDataList.forEach(ele=>{
                qty+=this.dataTable[ele].QTY
                labelNoArr = labelNoArr.filter(e=>{
                  return e != this.dataTable[ele].labelNo
                })
                labelList = labelList.filter(e=>{
                  return e.labelNo != this.dataTable[ele].labelNo
                })
                this.dataTable.splice(ele, 1)
              })
              sessionStorage.setItem('confirmQty',totalQty-qty)  
              sessionStorage.setItem('labelNoArr',JSON.stringify(labelNoArr)) 
              sessionStorage.setItem('labelList',JSON.stringify(labelList)) 
              this.checkDataList = []
            }          
        }
    }
</script>
