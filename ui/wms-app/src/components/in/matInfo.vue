<!-- 入库模块 物料明细组件 -->
<template>
  <v-ons-page>
    <custom-toolbar :title="'物料明细'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card>

    </v-ons-card>
  </v-ons-page>
</template>

<script>
  import customToolbar from '_c/toolbar'
  export default {
    data(){
      return{
          barcode: '',
          modalVisible: false,
      }
    },
    components: { customToolbar },
  }
</script>