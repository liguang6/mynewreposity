<!--
上架任务单
Yang Lin
2019 05 08
-->
<template>
    <v-ons-page>
        <toobar :title="'上架任务单'" :action="toggleMenu"></toobar>
        <div style="text-align: center">
            <p>待上架任务单： <span style="font-weight: bold">{{tableData.length}}</span></p>
        </div>
        <data-table :hiddenSelect="'true'" :columns="columns" :dataTable="tableData" :renders="renders" v-on:table-column-click="onTableClick"></data-table>
        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="$emit('gotoPageEvent','ShelfViewSelect')">开始上架</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toobar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    import {queryWhTasks} from '@/api/in'

    export default {
        components: {DataTable, toobar},
        props: ['toggleMenu'],
        computed : {
            whTaskList : {
                get(){
                    return this.$store.wms_in.shelf.whTaskList;
                },
                set(val){
                    this.$store.commit("shelf/whTaskList",val);
                }
            }
        },
        created(){
            //查询上架任务单
            queryWhTasks({'WERKS':null,'WH_NUMBER':null,'MANAGER':null}).then(resp => {
                resp = resp.data;
                if(resp.code == '0'){
                    if(resp.data.length == 0){
                        this.$ons.notification.toast("没有待上架的任务单",{timeout:1000});
                    }
                    let taskList = resp.data;
                    for(let task of taskList){
                        if(task.WT_STATUS == '00'){
                            task.STATUS = '未上架';
                        }
                        if(task.WT_STATUS == '01'){
                            task.STATUS = '部分上架';
                        }
                    }
                    this.tableData = taskList;
                    this.whTaskList = taskList;
                } else{
                    this.$ons.notification.toast(resp.msg,{timeout:1000});
                }
            })
        },
        data() {
            return {
                columns: {TASK_NUM: "单号", WH_NUMBER: "仓库号", STATUS: "状态"},
                tableData: [],
                renders: {
                    //单号显示成超链接
                    TASK_NUM : function(value){
                        return "<a href='javascript:void(0);'>"+value+"</a>";
                    }
                }
            }
        },
        methods : {
            //表格点击事件
            onTableClick(data){
                /*if(data.column == 'TASK_NUM'){
                    //设置上架任务单号
                    this.$store.commit("shelf/inboundTaskNo",data.data.value);
                    //跳转到上架任务单页面
                    this.$emit("gotoPageEvent","ShelfViewSelect");
                }*/
            }
        }
    }

</script>
