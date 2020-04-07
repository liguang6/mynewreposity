<!-- 入库模块 收料房收货 STO一步联动(根据条码收货) 组件 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'STO一步联动(根据条码收货)'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>
      <v-ons-row>
        <v-ons-col width="85%" ><center><v-ons-input id="barCode" autofocus=“true” name="SCM条码" :class="{'input': true, 'is-danger': errors.has('SCM条码') }" v-model="barCode" width="95%" modifier="underbar" placeholder="SCM条码" float ></v-ons-input></center></v-ons-col>
        <v-ons-col width="15%" ></v-ons-col>
      </v-ons-row>
    </v-ons-card>

    <ons-bottom-toolbar>
      <center>
        <v-ons-button style="margin: 6px 0"><v-ons-icon icon="fa-qrcode"></v-ons-icon>&nbsp;条码校验</v-ons-button> &nbsp;&nbsp;
        <v-ons-button style="margin: 6px 0"><v-ons-icon icon="fa-cogs"></v-ons-icon>&nbsp;确认过帐</v-ons-button>
      </center>
    </ons-bottom-toolbar>

    <v-ons-modal :visible="modalVisible">
      <p style="text-align: center">
          <v-ons-icon icon="fa-spinner" size="2x" spin></v-ons-icon>
          <br><br>
          正在操作...
      </p>
    </v-ons-modal>

  </v-ons-page>
</template>

<script>
  import customToolbar from '_c/toolbar'
  import { mapActions, mapMutations,mapState } from 'vuex'
  export default {
    computed: mapState({
      page: (state) => state.wms_in.page,
    }),
    mounted () {
      this.setPage('in_receipt_stoDelivery')
    },
    data(){
      return{
        barCode :'',
        msg: '',
        modalVisible: false,
      }
    },
    watch:{
      checkDataList(curVal,oldVal){
        this.ischeck = (curVal.length === this.list.length&&this.list.length>0)?true:false
      }
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    methods: {
      ...mapActions([
        'listScmMat',
      ]),
      ...mapMutations([
        'setPage',
      ]),
      search(){

      },
      
    }
  }
</script>