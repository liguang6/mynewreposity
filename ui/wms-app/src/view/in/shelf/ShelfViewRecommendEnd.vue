<template>
    <v-ons-page>
        <toolbar :title="'任务明细'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <v-ons-list-item modifier="chevron" tappable @click="next('0')">
                <v-ons-row>
                    <v-ons-col>
                        需求上架物料数：
                    </v-ons-col>
                    <v-ons-col>
                        {{whTaskList.length}}
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            <v-ons-list-item modifier="chevron" tappable @click="next('1')">

               <v-ons-row>
                   <v-ons-col>
                       已上架物料数：
                   </v-ons-col>
                   <v-ons-col>
                       {{hasShelfTasks.length}}
                   </v-ons-col>
               </v-ons-row>

            </v-ons-list-item>
            <v-ons-list-item modifier="chevron" tappable @click="next('2')">
                <v-ons-row>
                    <v-ons-col>
                        未上架物料数：
                    </v-ons-col>
                    <v-ons-col>
                        {{whTaskList.length - hasShelfTasks.length }}
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="back">返回</v-ons-button>
            <v-ons-button @click="shelfAndTransfer">确认</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'

    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        computed : {
            whTaskList(){
                return this.$store.state.wms_in.shelf.whTaskList;
            },
            hasShelfTasks:{
                get(){
                    return this.$store.state.wms_in.shelf.hasShelfTasks;
                },
                set(v){
                    this.$store.commit("shelf/hasShelfTasks",v);
                }
            },
        },
        methods : {
            back (){
                this.$emit('gotoPageEvent','ShelfViewRecommendStart')
            },
            next(type){
                this.$store.commit("shelf/displayWhTaskListType",type);
                this.$emit('gotoPageEvent','ShelfViewRecommendEndDisplay')
            },
            shelfAndTransfer(){
                this.$store.commit("setPage",'ShelfViewRecommendEnd')
                this.$emit('gotoPageEvent','in_confirm')
            }
        }
    }
</script>
