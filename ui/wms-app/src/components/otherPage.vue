<template>
  <v-ons-page>
    <p style="text-align: center">
      Other Pagessss
    </p>
  </v-ons-page>
</template>

<script>
  export default {
    mounted () {
      console.log("-->otherPage mounted!")
    },
    methods:{  
      reloadPage(){
        console.log("-->otherPage reloadPage!!!")
      }
    }
  }
</script>
