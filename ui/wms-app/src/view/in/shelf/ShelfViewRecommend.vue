<template>
    <v-ons-page>
        <toolbar :title="'推荐上架顺序'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <v-ons-list-item>
                <div style="text-align: center;">
                    待上架数量 : {{whTaskList.length}}
                </div>
            </v-ons-list-item>

            <data-table :columns = "columns" :dataTable="whTaskList" :hiddenSelect="'true'"></data-table>

        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="planShelfOrder">重新规划</v-ons-button>
            <v-ons-button @click="startShelf">开始上架</v-ons-button>
        </v-ons-bottom-toolbar>

        <v-ons-dialog :visible.sync="dialogVisible" cancelable>
            <div style="text-align: center">
                <p>请扫描定位标签</p>
                <v-ons-input placeholder="标签" v-model="positionBarcode"> </v-ons-input>
            </div>
        </v-ons-dialog>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'

    export default {
        components : {DataTable, toolbar},
        props : ['toggleMenu'],
        computed : {
          whTaskList(){
              return this.$store.state.wms_in.shelf.whTaskList;
          },
        },
        watch : {
            dialogVisible : function (val) {
                if(val == false && this.positionBarcode !== ''){
                    let whNumber = sessionStorage.getItem("UserWerks");
                    if(whNumber == undefined || whNumber == '')
                        this.$ons.notification.toast("仓库号没有设置",{timeout:1000});
                    this.$store.dispatch("shelf/orderInTaskList",whNumber).then(()=>{
                        //更新理序号
                        this.setNO();
                    });
                }
            }
        },
        created(){
            //是否显示理序号
            let displayNo = this.$store.state.wms_in.shelf.displayNo;
            if(displayNo){
                this.columns = {"TO_BIN_CODE":"推荐储位","BATCH":"物料号批次","QUANTITY":"数量","NO":"理序号"};
            }
            this.setNO();
        },
        data(){
            return {
                columns:{"TO_BIN_CODE":"推荐储位","BATCH":"物料号批次","QUANTITY":"数量"},
                dialogVisible:false,
                positionBarcode:"",
            }
        },
        methods : {
            planShelfOrder(){
                this.dialogVisible = true
            },
            startShelf(){
                this.$emit('gotoPageEvent','ShelfViewRecommendStart')
            },
            setNO(){
                //添加理序号
                let taskList = this.whTaskList.map((v,i)=>{
                    v.NO = parseInt(i) + 1;
                    return v;
                });
                this.$store.commit("shelf/whTaskList",taskList);
            }
        }
    }
</script>
