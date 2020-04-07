<template>
    <v-ons-page>
        <toolbar :title="'上架'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <!--<v-ons-list-item>
                <div class="left" style="width:40%">
                    上架任务单号：
                </div>
                <div class="center" >
                    {{inboundTaskNo}}
                </div>
            </v-ons-list-item>-->
            <v-ons-list-item>
                <div class="left" style="width:40%">
                    定位标签：
                </div>
                <div class="center">
                    <v-ons-input placeholder="定位标签" v-model="positionTag"></v-ons-input>
                </div>
                <div class="right">
                    <v-ons-button >扫描</v-ons-button>
                </div>
            </v-ons-list-item>
           <v-ons-list-item>
                <label class="left">
                <v-ons-checkbox :input-id="'checkbox'" :value="'1'" v-model="displayNo">

                </v-ons-checkbox>
                </label>
                <label class="center" :for="'checkbox'">
                    显示理序序号
                </label>
            </v-ons-list-item>
        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="confirm">确认</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        data(){
            return {
                taskNo :'',
                displayNo:[],//是否理序序号
            }
        },
        computed : {
            inboundTaskNo(){
                return this.$store.state.wms_in.shelf.inboundTaskNo;
            },
            //工厂
            werks(){
                return sessionStorage.getItem("UserWerks");
            },
            //仓库
            whNumber(){
                return sessionStorage.getItem("UserWhNumber");
            },
            positionTag : {
                get(){
                    return this.$store.state.wms_in.shelf.positionTag;
                },
                set(val){
                    this.$store.commit("shelf/positionTag",val);
                }
            },
            whTaskList : {
                get(){
                    return this.$store.state.wms_in.shelf.whTaskList;
                },
                set(value){
                    return this.$emit("shelf/whTaskList",value);
                }
            }
        },
        methods : {
            confirm(){
                if(this.displayNo.length > 0 && this.displayNo[0] == "1"){
                    //显示理序号
                    this.$store.commit("shelf/displayNo",true);
                }
                //把仓库任务排序
                this.orderInTaskList().then(()=> {
                    this.$emit('gotoPageEvent','ShelfViewRecommend');
                });
            },
            orderInTaskList(){
               return this.$store.dispatch("shelf/orderInTaskList",this.whNumber);
            }
        }
    }
</script>
