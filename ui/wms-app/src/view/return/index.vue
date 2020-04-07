<!-- 退货模块 首页 -->
<template>
  <v-ons-page>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="48%" >
          <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('return01')">
            <div class="center">
              <span class="list-item__title">收料房退货</span>
              <span class="list-item__subtitle">外购退货</span>
            </div>
          </v-ons-list-item>
          <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('return161Barcode')">
            <div class="center">
              <span class="list-item__title">161退货订单</span>
              <span class="list-item__subtitle">扫描条码退货</span>
            </div>
          </v-ons-list-item>
        </v-ons-col>
        <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
          <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('return02')">
            <div class="center" >
              <span class="list-item__title">收料房退货</span>
              <span class="list-item__subtitle" >STO退货</span>
            </div>
          </v-ons-list-item>

          <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('return122Barcode')">
            <div class="center">
              <span class="list-item__title">122采购订单</span>
              <span class="list-item__subtitle">扫描条码退货</span>
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
    console.log("-->returnPage mounted!")
  },
  methods:{
    ...mapMutations([
      'resetState'
    ]),
    reloadPage(){
      console.log("-->returnPage reloadPage!!!")
    },
    gotoPage(page){
      this.resetState()
      this.$emit('gotoPageEvent',page);
    }
  }
}
</script>
