<!-- 入库模块 首页 -->
<template>
    <v-ons-page>
        <v-ons-card>
            <v-ons-row >
            <v-ons-col width="48%" >
              <v-ons-list-item @click="gotoPage('out_reqCreate')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">扫描看板卡</span>
                  <span class="list-item__subtitle">创建需求</span>
                </div>
              </v-ons-list-item>  

               <v-ons-list-item @click="gotoUnShelf('UnShelfViewSelect',true)" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">拣配下架</span>
                  <span class="list-item__subtitle">扫描条码</span>
                </div>
              </v-ons-list-item>  

              <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('pdaScanner')">
              <div class="center" >
                <span class="list-item__title">PDA扫描出库</span>
                <span class="list-item__subtitle" >根据扫描条码</span>
              </div>
              </v-ons-list-item>   

              <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('JISCreateReq')">
              <div class="center" >
                <span class="list-item__title">创建需求</span>
                <span class="list-item__subtitle" >总装需求JIS</span>
              </div>
              </v-ons-list-item>  
              <v-ons-list-item @click="gotoPage('dispatching_handover')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">总装需求交接</span>
                  <span class="list-item__subtitle">总装需求交接</span>
                </div>
              </v-ons-list-item>                              
            </v-ons-col>     

          <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list-item @click="gotoUnShelf('UnShelfViewSelect',false)" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">拣配下架</span>
                  <span class="list-item__subtitle">不扫条码</span>
                </div>
              </v-ons-list-item>   

              <v-ons-list-item @click="gotoPage('handover')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">pda过账</span>
                  <span class="list-item__subtitle">pda过账</span>
                </div>
              </v-ons-list-item>                                  
            
              <v-ons-list-item @click="gotoPage('JITPick')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">总装JIT拣配</span>
                  <span class="list-item__subtitle">总装JIT拣配</span>
                </div>
              </v-ons-list-item>  

              <v-ons-list-item @click="gotoPage('dispatching_confirm')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">总装拣配确认</span>
                  <span class="list-item__subtitle">总装拣配确认</span>
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
    gotoUnShelf(page,scanner){//拣配下架
      if(scanner === undefined) 
        scanner = false
      this.scannerBarcode(scanner)
      this.$emit('gotoPageEvent',page)
    }
  }
}
</script>
