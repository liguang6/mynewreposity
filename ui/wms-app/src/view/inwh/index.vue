<!-- 库内模块 首页 -->
<template>
    <v-ons-page>
        <v-ons-card>
            <v-ons-row >
            <v-ons-col width="48%" > 
              <v-ons-list-item @click="gotoPage('InventorySearch')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">库存查询</span>
                  <span class="list-item__subtitle">库存查询</span>
                </div>
              </v-ons-list-item>  
              <v-ons-list-item @click="gotoPage('stocktaking')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">库存盘点</span>
                  <span class="list-item__subtitle">扫描条码</span>
                </div>
              </v-ons-list-item>                              
            </v-ons-col>     

          <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
            <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('pdaScannerByHwyd')">
              <div class="center" >
                <span class="list-item__title">PDA扫描货物移动</span>
                <span class="list-item__subtitle" >根据扫描条码</span>
              </div>
              </v-ons-list-item>
              <v-ons-list-item tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title"></span>
                  <span class="list-item__subtitle"></span>
                </div>
              </v-ons-list-item>
            </v-ons-col>

             
        
          </v-ons-row>
        </v-ons-card>      
    </v-ons-page>
</template>

<script>
import { mapActions, mapMutations,mapState } from 'vuex'
export default {
  mounted () {
    console.log("-->otherPage mounted!")
  },
  methods:{
    ...mapMutations([
      'resetState','scannerBarcode'
    ]),
    reloadPage(){
      console.log("-->otherPage reloadPage!!!")
    },
    gotoPage(page){
      this.resetState()
      this.$emit('gotoPageEvent',page);
    },
  }
}
</script>
