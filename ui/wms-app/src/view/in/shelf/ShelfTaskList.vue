<template>
  <v-ons-page>
    <custom-toolbar :title="'上架任务'" :action="toggleMenu"></custom-toolbar>
    <v-ons-card v-show="modalPage=='index'">
      <v-ons-row >
        <v-ons-col width="100%;" style="text-align:right">
          <label>待上架任务：{{taskList.length}}</label>
        </v-ons-col>
      </v-ons-row>
      <v-ons-list style="padding-top:10px">
        <v-ons-row v-for="(li) in taskList" :key="li.TASK_NUM">
          <v-ons-col width="100%" style="padding-top:6px"><b>单号:</b> {{li.TASK_NUM}}&nbsp;&nbsp;<b>仓库号:</b> {{li.WH_NUMBER}}&nbsp;&nbsp;<b>状态:</b> {{li.WT_STATUS}}</v-ons-col>         
        </v-ons-row>  
      </v-ons-list> 
    </v-ons-card>

    <v-ons-card v-show="modalPage=='order'">
        <v-ons-row >
            <v-ons-col vertical-align="center" width="100px;" style="text-align:right">
                定位标签：
            </v-ons-col>
            <v-ons-col width="75%" >           
                <input placeholder="请扫储位条码" style="width:90%" v-model="BIN_CODE_SORT" @keyup.enter="orderTask" data-index="0">                 
            </v-ons-col> 
        </v-ons-row> 

    </v-ons-card>
    <v-ons-card v-show="modalPage=='order'">
        <v-ons-list style="padding-top:10px">
        <v-ons-row v-for="(li) in taskList" :key="li.TASK_NUM">
          <v-ons-col width="100%" style="padding-top:6px"><b>序号:</b> {{li.INDEX}}&nbsp;&nbsp;<b>推荐储位:</b> {{li.TO_BIN_CODE}}&nbsp;&nbsp;<b>料号:</b> {{li.MATNR}}&nbsp;&nbsp;
          <b>批次:</b>{{li.BATCH}}&nbsp;&nbsp;<b>数量:</b>{{li.QUANTITY}}
          </v-ons-col>         
        </v-ons-row>  
        </v-ons-list>
    </v-ons-card>

    <v-ons-card v-show="modalPage=='detail'">
        <v-ons-list style="padding-top:10px">
        <v-ons-row >
          <v-ons-col width="100%" style="padding-top:10px"><b>物料号：</b>{{mat_cur.MATNR}}</v-ons-col>
          <v-ons-col width="100%" style="padding-top:10px"><b>物料描述：</b>{{mat_cur.MAKTX}}</v-ons-col> 
          <v-ons-col width="100%" style="padding-top:10px"><b>数量：</b>{{mat_cur.QUANTITY}}</v-ons-col> 
          <v-ons-col width="100%" style="padding-top:10px"><b>批次：</b>{{mat_cur.BATCH}}</v-ons-col> 
          <v-ons-col width="100%" style="padding-top:10px"><b>供应商：</b>{{mat_cur.LIFNR}}</v-ons-col> 
          <v-ons-col width="100%" style="padding-top:10px"><b>推荐储位：</b>{{mat_cur.TO_BIN_CODE}}</v-ons-col> 
          <v-ons-col width="100%" style="padding-top:10px"><b>序号：</b>{{mat_cur.INDEX}}</v-ons-col> 
          <v-ons-col width="100%" style="padding-top:10px;padding-bottom:10px;"><b>实际储位：</b>
            <input style="width:70%;padding:0 0" :value='mat_cur.TO_BIN_CODE'> 
          </v-ons-col>          
        </v-ons-row>  
        </v-ons-list>
    </v-ons-card>        

    <v-ons-bottom-toolbar class="bottom-toolbar">
        <center v-show="modalPage=='index'">
            <v-ons-button @click="modalPage = 'order'">开始上架</v-ons-button>
        </center> 
        <center v-show="modalPage=='order'">
            <v-ons-button @click="modalPage = 'index'" style="background-color:grey;margin: 6px 0" ><v-ons-icon icon="fa-reply"></v-ons-icon> 返回</v-ons-button>
            <v-ons-button @click="showDetail"><v-ons-icon icon="fa-cogs"></v-ons-icon> 上架</v-ons-button>
        </center>
        <center v-show="modalPage=='detail'">
            <v-ons-button @click="modalPage = 'index'" style="background-color:green;margin: 6px 0" ><v-ons-icon icon="fa-refresh"></v-ons-icon> 结束返回</v-ons-button>
            <v-ons-button @click="confirmShelf" ><v-ons-icon icon="fa-cogs"></v-ons-icon> 完成上架</v-ons-button>
            <v-ons-button @click="modalPage = 'order'" style="background-color:grey;" ><v-ons-icon icon="fa-reply"></v-ons-icon> 返回</v-ons-button>
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
            mat_cur:{},
            BIN_CODE_SORT:''
        }
    },
    methods: {
        ...mapActions([
        
        ]),
        ...mapMutations([
        
        ]),
        confirmShelf(){
            let mat=this.mat_cur
            let index=null;
            this.taskList.forEach(function(v,i){
                if(v.INDEX==mat.INDEX){
                    index=i
                }
            })
            if(index!=null){
                this.taskList.splice(index,1)
            }
            

            if(this.taskList.length>0){
                this.modalPage='order'
            }else
                this.modalPage='index'
        },
        showDetail(){
            this.mat_cur=this.taskList[0]
            this.modalPage='detail'
        }    
    }  
}
</script>
<style>

input,
select {
  padding: 0.75em 0.5em;
  border:none;
  font-size: 100%;
  border-bottom: 1px solid #ccc;
  width: 100%;
}
</style>