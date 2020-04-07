<template>
  <v-ons-page>
    <custom-toolbar :title="'上架任务'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-show="modalPage=='index'">
      <v-ons-row >
        <v-ons-col width="100%">
          <label>待上架任务：{{taskList.length}}</label>
        </v-ons-col>
      </v-ons-row>
      <v-ons-list>
        <v-ons-row v-for="(li) in taskList" :key="li.TASK_NUM">
          <v-ons-col width="100%" style="padding-top:6px"><b>单号:</b> {{li.TASK_NUM}}&nbsp;&nbsp;<b>仓库号:</b> {{li.WH_NUMBER}}&nbsp;&nbsp;<b>状态:</b> {{li.WT_STATUS}}</v-ons-col>         
        </v-ons-row>  
      </v-ons-list> 
    </v-ons-card>

    <v-ons-bottom-toolbar class="bottom-toolbar">
        <center v-show="modalPage=='index'">
            <v-ons-button @click="modalPage = 'order'">开始上架</v-ons-button>
        </center> 
    </v-ons-bottom-toolbar>
  </v-ons-page>
</template>

<script>
import customToolbar from '_c/toolbar'
import { mapActions, mapMutations,mapState } from 'vuex'
import {queryWhTasks} from '@/api/in'
export default {
    computed: mapState({
      userWerks: (state) => sessionStorage.getItem('UserWerks'),
      userWhNumber: (state) => sessionStorage.getItem('UserWhNumber'),
      userName: (state) => sessionStorage.getItem('UserName'),
    }),
    created(){
        this.modalPage='index'
        let data={
            WH_NUMBER:this.userWhNumber
        }
        queryWhTasks(data).then(resp => {
            resp = resp.data;
            if(resp.code == '0'){
                if(resp.data.length == 0){
                    this.$ons.notification.toast("没有待上架的任务单",{timeout:1000});
                }
                let taskList = resp.data;
                for(let task of taskList){
                    if(task.WT_STATUS == '00'){
                        task.WT_STATUS = '未上架';
                    }
                    if(task.WT_STATUS == '01'){
                        task.WT_STATUS = '部分上架';
                    }
                }
                this.taskList = taskList;
            } else{
                this.$ons.notification.toast(resp.msg,{timeout:1000});
            }
        })
    },
    props: ['toggleMenu'],
    components: { customToolbar },
    data(){
        return{
            modalPage:'index',
            taskList:[],
        }
    },
    methods: {
      ...mapActions([
        
      ]),
      ...mapMutations([
        
      ]),
    } 
    
}
</script>
