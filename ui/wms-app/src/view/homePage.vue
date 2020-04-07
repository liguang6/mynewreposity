<!-- PDA 首页 -->
<template>
    <v-ons-page>
      <custom-toolbar :title="'PDA主菜单'" :action="toggleMenu"></custom-toolbar>
      <v-ons-tabbar v-on:gotoPageEvent='gotoPage' ref="ontabbar" swipeable position="bottom" @postchange="changeTab" :tabs="tabs" :visible="true" :index.sync="index">
      </v-ons-tabbar>

    </v-ons-page>
</template>

<script>
  import customToolbar from '_c/toolbar'
  import otherPage from '_c/otherPage'
  import indexPage from '@/view/indexPage'
  import in_index from '@/view/in/index.vue'
  import inwh_index from '@/view/inwh/index.vue'
  import out_index from '@/view/out/index.vue'
  import return_index from '@/view/return/index.vue'
  import WmsQc from '@/view/qc/WmsQc'
  export default {
    data(){
      return {
        tabs: [
          {
            icon: 'ion-filing',
            label: '首页',
            page: indexPage,
            key: "indexPage"
          },
          {
            icon: 'ion-archive',
            label: '入库',
            page: in_index,
            key: "in_index",
            ref: "in_index"
          },
          {
            icon: 'ion-cube',
            label: '质检',
            page: WmsQc,
            key: "WmsQc"
          },
          {
            icon: 'ion-upload',
            label: '出库',
            page: out_index,
            key: "out_index",
            ref: "out_index"
          },
          {
            icon: 'ion-map',
            label: '库内',
            page: inwh_index,
            key: "inwh_index",
            ref: "inwh_index"
          },
          {
            icon: 'ion-reply-all',
            label: '退货',
            page: return_index,
            key: "return_index"
          }
        ]
      };
    },
    props: ['toggleMenu'],
    components: { customToolbar,indexPage,in_index,otherPage,WmsQc},
    methods:{
      changeTab(event){
        //切换页面时刷新数据
        console.log('-->changeTab' )
        ////this.$refs.ontabbar.$refs.otherPage.page.methods.reloadPage()
      },
      gotoPage(page){
        this.$emit('gotoPageEvent',page);
      }
    },
    computed : {
      index : {
          get : function () {
            return this.$store.state.tabbar.index;
          },
          set : function (newValue) {
            return this.$store.commit('tabbar/set',newValue);
          }
      }
    }
  }
</script>
