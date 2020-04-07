<!-- 退货模块 首页 -->
<template>
  <v-ons-page>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="48%" >
            <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('freeze')">
            <div class="center">
              <span class="list-item__title">库存冻结</span>
            </div>
          </v-ons-list-item>
        </v-ons-col>
        <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
          <v-ons-list-item tappable modifier="longdivider" @click="gotoPage('unfreeze')">
          <div class="center" >
            <span class="list-item__title">库存解冻</span>
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
