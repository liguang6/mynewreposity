<template id="toolbar">
    <v-ons-toolbar>
      <div class="left">
        <v-ons-toolbar-button @click="action">
          <v-ons-icon icon="ion-navicon, material:md-menu"></v-ons-icon>
        </v-ons-toolbar-button>
        <v-ons-toolbar-button v-show=this.showback?false:true @click="gohome">
          <v-ons-icon icon="ion-home, material:md-menu"></v-ons-icon>
        </v-ons-toolbar-button>
      </div>
      <div class="center">{{ title }}</div>
      <div class="right">
        <v-ons-toolbar-button @click="logout"><v-ons-icon icon="ion-log-out"></v-ons-icon></v-ons-toolbar-button>
        </div>
    </v-ons-toolbar>
</template>

<script>
import { mapActions, mapMutations } from 'vuex'
export default {
  props: ['title', 'action','showback'],
  methods:{
    ...mapActions([
      'handleLogOut',
    ]),
    gohome(){
      this.$emit('gohome');
    },
    logout(){
      this.handleLogOut().then(() => {
        this.$router.push({
          name: 'login'
        })
      })
    }
  }
} 
</script>
