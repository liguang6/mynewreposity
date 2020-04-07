<template>
  <v-ons-page>
    <custom-toolbar :title="'设置'" :action="toggleMenu"></custom-toolbar>
    <p style="text-align: center">
      Change the settings.
    </p>
  </v-ons-page>
</template>

<script>
  import customToolbar from '_c/toolbar'
  export default {
    props: ['toggleMenu'],
    components: { customToolbar }
  }
</script>
