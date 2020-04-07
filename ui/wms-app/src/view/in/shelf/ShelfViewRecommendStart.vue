<template>
    <v-ons-page>
    <toolbar :title="'上架信息'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <v-ons-list-item v-if="shelfType === '1'">
                <!-- 扫描实物条码上架显示条码 -->
                <v-ons-row>
                    <v-ons-col>
                        条码：
                    </v-ons-col>
                    <v-ons-col>
                        <v-ons-input placeholder="条码" v-model="LABEL_NO"></v-ons-input>
                    </v-ons-col>
                    <v-ons-col>
                        <v-ons-button @click="validateLabelNo">扫描</v-ons-button>
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        物料号：
                    </v-ons-col>
                    <v-ons-col>{{current.MATNR}}</v-ons-col>
                </v-ons-row>

            </v-ons-list-item>
            <v-ons-list-item>
            <v-ons-row>
                <v-ons-col>
                    物料描述：
                </v-ons-col>
                <v-ons-col>{{current.MAKTX}}</v-ons-col>
            </v-ons-row>
            </v-ons-list-item>
            <v-ons-list-item>
            <v-ons-row>
                <v-ons-col>
                    数量：
                </v-ons-col>
                <v-ons-col>{{current.QUANTITY}}</v-ons-col>
            </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        批次：
                    </v-ons-col>
                    <v-ons-col>{{current.BATCH}}</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        供应商：
                    </v-ons-col>
                    <v-ons-col>{{current.LIFNR}}</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        推荐放置储位：
                    </v-ons-col>
                    <v-ons-col>{{current.TO_BIN_CODE}}</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        物流器具位置号：
                    </v-ons-col>
                    <v-ons-col>{{current.MOULD_NO}}</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>

            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        实际储位：
                    </v-ons-col>
                    <v-ons-col>
                        <v-ons-input placeholder="储位" v-model="real_bin_code"></v-ons-input>
                    </v-ons-col>

                    <v-ons-col v-if="scannerBarcode === true">
                        <v-ons-button>扫描</v-ons-button>
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>
    <v-ons-bottom-toolbar class="bottom-toolbar">
        <v-ons-button @click="endShelf">结束上架</v-ons-button>
        <v-ons-button @click="shelf">完成上架</v-ons-button>
        <v-ons-button @click="$emit('gotoPageEvent','ShelfViewRecommend')">返回</v-ons-button>
    </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import {whTaskShelf,queryBarcode} from '@/api/in'

    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        created(){
        },
        data(){
            return {
                index : 0,//当前处理任务的位置
                real_bin_code : '',//实际储位
                LABEL_NO:'',//标签
            }
        },
        computed : {
            shelfType : {
                get(){
                    return this.$store.state.wms_in.shelf.shelfType
                },
                set(newVal){
                    this.$store.commit('shelf/shelfType',newVal)
                }
            },
            hasShelfTasks:{
                get(){
                    return this.$store.state.wms_in.shelf.hasShelfTasks;
                },
                set(v){
                    this.$store.commit("shelf/hasShelfTasks",v);
                }
            },
            scannerBarcode: {
              get(){
                  return this.$store.state.wms_in.shelf.scannerBarcode
              },
              set(newVal){
                  this.$store.commit('shelf/scannerBarcode',newVal)
              }
          },
            whTaskList(){
              return this.$store.state.wms_in.shelf.whTaskList;
            },
            current(){
                return this.whTaskList[this.index];
            }
        },
        methods : {
            shelf(){

                if(this.real_bin_code !== '' && this.real_bin_code !== this.current.TO_BIN_CODE){
                        this.$ons.notification.confirm('所扫储位代码与系统推荐不符，是否存储当前储位！')
                        .then((response) => {
                           if(response !== 1){
                               this.real_bin_code = '';
                           }
                        });
                }
                //调用后台
               whTaskShelf({"TASK_NUM":this.current.TASK_NUM,"ID":this.current.ID,"REAL_BIN_CODE":this.real_bin_code}).then(resp => {
                   //...
                    let d = resp.data;
                    if(d.code == '0'){
                        this.next();
                        this.hasShelfTasks.splice(-1,0,this.current.ID);//设置为已处理
                    }else {
                        this.$ons.notification.toast(d.msg,{timeout:1000});
                    }
                });

            },
            next(){
                //显示下一项
                if(this.index < this.whTaskList.length-1)
                    this.index = this.index + 1;
                else
                    this.$ons.notification.toast('已经是最后一项',{timeout:1000})
            },
            endShelf(){
                this.$emit('gotoPageEvent','ShelfViewRecommendEnd')
            },
            validateLabelNo(){
                //校验标签信息
                queryBarcode(this.LABEL_NO).then(resp=>{
                    let d = resp.date;
                    if(d.code == '0'){
                        if(d.data.length > 0){
                            let labelObj = d.data[0];
                            //条码是否与上架任务匹配
                            if(this.current.LABEL_NO.indexOf(labelObj.LABEL_NO) == -1){
                                this.$ons.notification.toast("条码与上架任务不匹配",{timeout:1000})
                            }else {
                                //TODO: 带出条码的储位
                            }
                        }
                    }else {
                        this.$ons.notification.toast(d.msg,{timeout:1000})
                    }
                })
            }
        }
    }
</script>
