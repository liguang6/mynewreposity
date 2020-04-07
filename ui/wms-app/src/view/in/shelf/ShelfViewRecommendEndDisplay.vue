<template>
    <v-ons-page>
        <toolbar :title="title" :action="toggleMenu"></toolbar>
        <v-ons-list>
            <v-ons-list-item>
                <div style="text-align: center;">
                    {{title}}
                </div>
            </v-ons-list-item>

            <v-ons-list-item >
                <v-ons-row>
                    <v-ons-col v-for="(col,$index) in columes" :key="$index">{{col}}</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item v-for="(data,$index) in taskList" :key="$index">
                <v-ons-row>
                    <v-ons-col v-for="key in data" :key="key">
                        {{key}}
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="back">返回</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'

    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        data () {
            return {
                columes : ['推荐储位','物料号批次','数量']
            }
        },
        computed : {
            title(){
                let titel = "上架清单"
                if(this.displayWhTaskListType == '1')
                    titel =  '已上架清单';
                if(this.displayWhTaskListType == '2')
                    titel =  "未上架清单"
                return titel;
            },
            displayWhTaskListType(){
                return this.$store.state.wms_in.shelf.displayWhTaskListType;
            },
            whTaskList(){
                return this.$store.state.wms_in.shelf.whTaskList;
            },
            hasShelfTasks(){
                return this.$store.state.wms_in.shelf.hasShelfTasks;
            },
            taskList(){
                if(this.displayWhTaskListType=='0'){
                    let f =  this.whTaskList;
                    let is = [];
                    for(let i of f){
                        is.splice(-1,0,{"TO_BIN_CODE":i.TO_BIN_CODE,"BATCH":i.BATCH,"QUANTITY":i.QUANTITY});
                    }
                    return is;
                }
                if(this.displayWhTaskListType=='1'){
                    let f =  this.whTaskList.filter(v=>{
                        for(let i of this.hasShelfTasks){
                            if(v.ID == i){
                                return true;
                            }
                        }
                        return false;
                    });
                    let is = [];
                    for(let i of f){
                        is.splice(-1,0,{"TO_BIN_CODE":i.TO_BIN_CODE,"BATCH":i.BATCH,"QUANTITY":i.QUANTITY});
                    }
                    return is;
                }
                if(this.displayWhTaskListType=='2'){
                    let f =  this.whTaskList.filter(v=>{
                        for(let i of this.hasShelfTasks){
                            if(v.ID == i){
                                return false;
                            }
                        }
                        return true;
                    })
                    let is = [];
                    for(let i of f){
                        is.splice(-1,0,{"TO_BIN_CODE":i.TO_BIN_CODE,"BATCH":i.BATCH,"QUANTITY":i.QUANTITY});
                    }
                    return is;
                }

                return [];
            }
        },
        methods : {
            back(){
                this.$emit('gotoPageEvent','ShelfViewRecommendEnd')
            }
        }
    }
</script>
