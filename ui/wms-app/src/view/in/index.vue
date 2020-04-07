<!-- 入库模块 首页 -->
<template>
    <v-ons-page>
        <v-ons-card>
            <v-ons-row >
            <v-ons-col width="48%" >
              <v-ons-list-item  :class="{'noAuthClass' :userAuthUrlStr.indexOf(',in_receipt_scm,') <0}" @click="gotoPage('in_receipt_scm')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">扫描送货单</span>
                  <span class="list-item__subtitle">SCM收货单</span>
                </div>
              </v-ons-list-item>
              <!--<v-ons-list-item :class="{'noAuthClass': userAuthUrlStr.indexOf(',in_receipt_stoDelivery,') <0}" @click="gotoPage('in_receipt_stoDelivery')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">STO交货单</span>
                  <span class="list-item__subtitle">SCM未创建条码</span>
                </div>
              </v-ons-list-item>-->
              <v-ons-list-item @click="gotoPage('in_receipt_cloudBarcode')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">云平台收货</span>
                  <span class="list-item__subtitle">扫描条码</span>
                </div>
              </v-ons-list-item>
              <v-ons-list-item :class="{'noAuthClass': userAuthUrlStr.indexOf(',in_receipt_stoLogistics,') <0}"  @click="gotoPage('in_receipt_stoLogistics')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">STO收货</span>
                  <span class="list-item__subtitle">物流发货清单</span>
                </div>
              </v-ons-list-item>
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_305Sap,') <0}" @click="gotoPage('in_receipt_305Sap')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">305调拨</span>
                  <span class="list-item__subtitle">303SAP凭证号</span>
                </div>
              </v-ons-list-item>
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_sto,') <0}" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">STO一步联动</span>
                  <span class="list-item__subtitle">根据WMS调拨单</span>
                </div>
              </v-ons-list-item>
            </v-ons-col>

            <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_scmBarcode,') <0}" @click="gotoPage('in_receipt_scmBarcode')" tappable modifier="longdivider">
                <!-- <div class="left" style="width:26px"><v-ons-icon icon="fa-cube" style="color: green;padding:0 0" class="list-item__icon"></v-ons-icon></div> -->
                <div class="center">
                  <span class="list-item__title">扫描条码</span>
                  <span class="list-item__subtitle">SCM条码</span>
                </div>
              </v-ons-list-item>
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_stoSend,') <0}" @click="gotoPage('in_receipt_stoSend')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">STO送货单</span>
                  <span class="list-item__subtitle">SCM中创建条码</span>
                </div>
              </v-ons-list-item>
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_305Barcode,') <0}" @click="gotoPage('in_receipt_305Barcode')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">305调拨</span>
                  <span class="list-item__subtitle">根据条码收货</span>
                </div>
              </v-ons-list-item>
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_305Wms,') <0}" @click="gotoPage('in_receipt_305Wms')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">305调拨</span>
                  <span class="list-item__subtitle">根据WMS调拨单</span>
                </div>
              </v-ons-list-item>
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',StepLinkage,') <0}" @click="gotoPage('StepLinkage')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">STO一步联动</span>
                  <span class="list-item__subtitle">根据条码</span>
                </div>
              </v-ons-list-item>
            </v-ons-col>
          </v-ons-row>

        </v-ons-card>

        <v-ons-card>
          <v-ons-row>
            <v-ons-col width="48%" >

                <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',ShelfInitTask,') <0}" tappable modifier="longdivider" @click="gotoPage('ShelfInitTask')">
                <div class="center">
                  <span class="list-item__title">上架任务</span>
                  <span class="list-item__subtitle">创建上架任务单</span>
                </div>
              </v-ons-list-item>


              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_sto,') <0}" tappable modifier="longdivider" @click="gotoInShelf('2')">
                <div class="center">
                  <span class="list-item__title">上架</span>
                  <span class="list-item__subtitle">按任务单-不扫条码</span>
                </div>
              </v-ons-list-item>

              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_sto,') <0}" tappable modifier="longdivider" @click="gotoInShelf('2',true)">
                <div class="center">
                  <span class="list-item__title">上架</span>
                  <span class="list-item__subtitle">按任务单-扫条码</span>
                </div>
              </v-ons-list-item>

            </v-ons-col>
            <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',ShelfUBTransferOrder,') <0}" tappable modifier="longdivider" @click="gotoPage('ShelfUBTransferOrder')">
              <div class="center" >
                <span class="list-item__title">UB转储单调拨</span>
                <span class="list-item__subtitle" >101进仓</span>
              </div>
              </v-ons-list-item>

              <v-ons-list-item :class="{'noAuthClass':userAuthUrlStr.indexOf(',in_receipt_sto,') <0}" @click="gotoInShelf('1')" tappable modifier="longdivider">
                <div class="center">
                  <span class="list-item__title">上架</span>
                  <span class="list-item__subtitle">扫描实物条码</span>
                </div>
              </v-ons-list-item>

            </v-ons-col>
          </v-ons-row>
        </v-ons-card>

        <v-ons-card>
          <v-ons-row >
            <v-ons-col width="48%" >
              <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('in_receipt_writeOff')">
              <div class="center" >
                <span class="list-item__title">收货凭证冲销</span>
                <span class="list-item__subtitle" >收货凭证冲销</span>
              </div>
              </v-ons-list-item>
            </v-ons-col>  

            <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('in_incoming_writeOff')">
              <div class="center" >
                <span class="list-item__title">入库凭证冲销</span>
                <span class="list-item__subtitle" >入库凭证冲销</span>
              </div>
              </v-ons-list-item>
            </v-ons-col>  
          </v-ons-row>

        </v-ons-card>  

        <v-ons-card>
          <v-ons-row >
            <v-ons-col width="48%" >
              <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('in_no_inbound')">
              <div class="center" >
                <span class="list-item__title">进仓单入库</span>
                <span class="list-item__subtitle" >扫描进仓单入库</span>
              </div>
              </v-ons-list-item>
            </v-ons-col>  

            <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
              <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('in_barcode_inbound')">
              <div class="center" >
                <span class="list-item__title">条码入库</span>
                <span class="list-item__subtitle" >扫描条码入库</span>
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
    console.log("-->inPage mounted!")
    this.userAuthUrlStr = sessionStorage.getItem('userAuthUrlStr')
    console.log("-->this.userAuthUrlStr : " + this.userAuthUrlStr)
  },
  data(){
    return {
      userAuthUrlStr: '',
    }
  },
  methods:{
    ...mapMutations([
      'resetState'
    ]),
    reloadPage(){
      console.log("-->otherPage reloadPage!!!")
    },
    gotoPage(page){
      this.resetState()
      this.$emit('gotoPageEvent',page);
    },
    gotoInShelf(type,scanner){//入库上架
      if(scanner === undefined) scanner = false
      this.$store.commit('shelf/shelfType',type)
      this.$store.commit('shelf/scannerBarcode',scanner)
      this.$emit('gotoPageEvent','ShelfView')
    }
  }
}
</script>
<style>
.noAuthClass{
  color: grey
}
</style>
