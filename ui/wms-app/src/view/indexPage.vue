<template>
    <v-ons-page>
      <v-ons-card>
        <div class="title">
          
        </div>
        <v-ons-row>
          <v-ons-col width="50%"><center><v-ons-button modifier="material" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 待收货(0)</v-ons-button></center></v-ons-col>
          <v-ons-col><center><v-ons-button modifier="material" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 待质检(0)</v-ons-button></center></v-ons-col>
        </v-ons-row>
        <v-ons-row>
          <v-ons-col><center><v-ons-button modifier="material" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 我的任务</v-ons-button></center></v-ons-col>
          <v-ons-col><center><v-ons-button modifier="material" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 我的任务</v-ons-button></center></v-ons-col>
        </v-ons-row>
        <v-ons-row>
          <v-ons-col><center><v-ons-button modifier="material" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 我的任务</v-ons-button></center></v-ons-col>
          <v-ons-col><center><v-ons-button modifier="material" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 我的任务</v-ons-button></center></v-ons-col>
        </v-ons-row>
        <v-ons-row>
          <v-ons-col><center><v-ons-button modifier="material" @click="in_receipt_scm" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 我的任务</v-ons-button></center></v-ons-col>
          <v-ons-col><center><v-ons-button modifier="material" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 我的任务</v-ons-button></center></v-ons-col>
        </v-ons-row>
        <v-ons-row>

        </v-ons-row>
      </v-ons-card>
      <v-ons-card>
        <v-ons-row>
          <v-ons-col><center><v-ons-button modifier="cta" style="background-color:yellowgreen;margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 我的任务</v-ons-button></center></v-ons-col>
          <v-ons-col><center><v-ons-button @click="showModal" modifier="cta" style="margin: 6px 0"><v-ons-icon icon="fa-tasks"></v-ons-icon> 未绑定的任务</v-ons-button></center></v-ons-col>
        </v-ons-row>
      </v-ons-card>

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
  export default {
    data() {
        return {
        modalVisible: false,
        timeoutID: 0
        };
    },
  methods: {
    showModal() {
      this.modalVisible = true;
      clearTimeout(this.timeoutID);
      this.timeoutID = setTimeout(() => this.modalVisible = false, 2000);
    },
    reloadPage(){
      console.log("-->indexPage reloadPage!!!")
    },
    in_receipt_scm(){
      this.$emit('gotoPageEvent','in_receipt_scm');
    }
  }
  }
</script>