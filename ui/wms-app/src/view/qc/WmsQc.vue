<!--
质检模块入口页面
Yang Lin
2019 03 26
 -->
<template>
   <v-ons-page>

       <custom-toolbar :title="'质检'" :action="toggleMenu"></custom-toolbar>
       <v-ons-card>
          <v-ons-row>

              <v-ons-col width="48%" >
                  <v-ons-list-item @click="toViewSingle" tappable modifier="longdivider" >
                      <div class="center">
                          <span class="list-item__title">单箱质检</span>
                          <span class="list-item__subtitle">质检</span>
                      </div>
                  </v-ons-list-item>

                  <v-ons-list-item @click="toViewSingleChange" tappable modifier="longdivider">
                      <div class="center">
                          <span class="list-item__title">单箱改判</span>
                          <span class="list-item__subtitle">改判</span>
                      </div>
                  </v-ons-list-item>


                  <v-ons-list-item @click="$emit('gotoPageEvent','WmsQcStockSingle')" tappable modifier="longdivider">
                      <div class="center">
                          <span class="list-item__title">单箱复检</span>
                          <span class="list-item__subtitle">复检</span>
                      </div>
                  </v-ons-list-item>

              </v-ons-col>





              <v-ons-col width="52%" style="border-left-width: 1px;border-left-style: solid;border-left-color: lightgray">
                  <v-ons-list-item @click="toViewBatch" tappable modifier="longdivider">
                      <div class="center">
                          <span class="list-item__title">整批质检</span>
                          <span class="list-item__subtitle">质检</span>
                      </div>
                  </v-ons-list-item>

                  <v-ons-list-item @click="toViewBatchChange" tappable modifier="longdivider">
                      <div class="center">
                          <span class="list-item__title">整批改判</span>
                          <span class="list-item__subtitle">改判</span>
                      </div>
                  </v-ons-list-item>

                  <v-ons-list-item @click="$emit('gotoPageEvent','WmsQcStockBatch')" tappable modifier="longdivider">
                      <div class="center">
                          <span class="list-item__title">整批复检</span>
                          <span class="list-item__subtitle">复检</span>
                      </div>
                  </v-ons-list-item>
              </v-ons-col>
          </v-ons-row>
       </v-ons-card>
   </v-ons-page>
</template>

<script>

    export default  {
        data(){
            return {

            }
        },
        methods : {
            toViewSingle : function () {
                this.$emit('gotoPageEvent',"WmsQcSingle");
            },
            toViewSingleChange : function () {
                this.$emit('gotoPageEvent',"WmsQcSingleChange");
            },
            toViewBatchChange : function () {
                this.$emit('gotoPageEvent',"WmsQcBatchChange");
            },
            toViewBatch : function () {
                this.$emit('gotoPageEvent',"WmsQcBatch");
            }
        },
        //components:[customToolbar],
        props: ['toggleMenu'],
    }
</script>

<style>


</style>
