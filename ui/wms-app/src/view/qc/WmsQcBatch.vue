<!--
入库质检 -> 整批质检
Yang Lin
2019-3-26
-->

<template>
    <v-ons-page>
        <custom-toolbar :title="'批次质检'" :action="toggleMenu"></custom-toolbar>
        <v-ons-card>
            <v-ons-row class="qc-row">
                <v-ons-col width="30%">箱号：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" readonly="readonly" autocomplete="off"  autofocus="true" name="箱号" :class="{'input': true, 'is-danger': errors.has('箱号') }"  v-validate="'required'"  class="qc-input" v-model="boxNum"> </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">送检单号：</v-ons-col>
                <v-ons-col>
                    <v-ons-input @keydown="query($event)" type="text"  placeholder="送检单号" class="qc-input" v-model="inspectionNo"> </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">待检批次：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" readonly="readonly"  class="qc-input" v-model="batch"> </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">待检数量：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" readonly="readonly"  class="qc-input" v-model="qcQty"> </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">质检结果：</v-ons-col>
                <v-ons-col>
                    <v-ons-select style="width: 40%" modifier="material" v-model="qcCode">
                        <option v-for="option in qc_dict" :key="option.CODE" :value="option.CODE">{{option.VALUE}}</option>
                    </v-ons-select>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row" v-if="!valid">
                <v-ons-col width="30%">不良原因：</v-ons-col>
                <v-ons-col>
                    <v-ons-select style="width: 40%" v-model="qcReasonCode">
                        <option v-for="a in qc_reason_list" :key="a.id" :value="a.reasonCode">{{a.reasonDesc}}</option>
                    </v-ons-select>
                </v-ons-col>
            </v-ons-row>

        </v-ons-card>

        <ons-bottom-toolbar>
                <div style="text-align: center;">
                    <v-ons-button style="margin:0 16px" @click="commit" :disabled = 'disable'>提交</v-ons-button>
                    <v-ons-button style="margin:0 16px" modifier="outline" @click="back">返回</v-ons-button>
                </div>
        </ons-bottom-toolbar>
        <v-ons-modal :visible='disable'>
            <p style="text-align: center">
                <v-ons-icon icon="fa-spinner" size="2x" spin></v-ons-icon>
                <br><br>
                正在操作...
            </p>
        </v-ons-modal>
    </v-ons-page>
</template>
<script>
    import customToolbar from '_c/toolbar'
    import {mapActions} from 'vuex'
    import {queryBatchInfo,qcBtachCommit} from '@/api/qc'

    export default {
        props: ['toggleMenu'],
        components: { customToolbar },
        data(){
            return {
                disable : false,
                boxNum:null, //箱号
                qcCode:'02',//质检结果代码
                inspectionNo:'',//送检单号
                batch:'',//批次
                qcQty: null,//质检数量
                valid : true,//是否是合格类型
                qcReasonCode : '',//不良原因代码
            }
        },
        watch: {
            qcCode(val){
                //根据质检结果配置表
                //判断质检结果是否是合格
                for(let cf of this.qc_result_config){
                    if(cf.qcResultCode == val){
                        if((cf.whFlag == '0' || cf.retunFlag == 'X') && cf.qcStatus != '01'){
                            this.valid = false;
                        }else {
                            this.valid = true;
                            this.qcReasonCode = null;
                        }
                    }
                }
            }
        },
        computed : {
            qc_dict(){
                return this.$store.state.qc.qc_dict;
            },
            qc_reason_list(){
                return this.$store.state.qc.qc_reason_list;
            },
            qc_result_config(){
                return this.$store.state.qc.qc_result_config;
            }
        },
        created(){
            this.getQcDict();//初始化质检结果
            this.getQcReasonList();//初始化不良原因
            this.getQcResultConfig();
        },
        methods : {
            ...mapActions(['getQcDict','getQcReasonList','getQcResultConfig']),
            back : function () {
                this.$emit('gotoPageEvent',"home");
                this.$store.commit('tabbar/set',2);
            },
            commit : function () {
                this.$validator.localize('zh_CN');
                this.$validator.validateAll().then((result) => {
                    if(!result){
                        this.$ons.notification.toast("箱号不能为空",{timeout:1000})
                        return false
                    }else {
                        this.disable = true
                        let QC_RESULT = "";
                        for(let f of this.qc_reason_list){
                            if(f.reasonCode == this.qcReasonCode){
                                QC_RESULT = f.reasonDesc
                            }
                        }
                        let QC_RESULT_TEXT = "";
                        for (let f of this.qc_dict){
                            if(f.CODE == this.qcCode){
                                QC_RESULT_TEXT = f.VALUE
                            }
                        }
                        qcBtachCommit({"INSPECTION_NO":this.inspectionNo,"QC_RESULT_CODE":this.qcCode,"QC_RESULT_TEXT":QC_RESULT_TEXT,"QC_RESULT":QC_RESULT}).then(r => {
                            this.disable = false
                            let d = r.data
                            if(d.code != '0'){
                                this.$ons.notification.toast(d.msg,{timeout:1000})
                            }else {
                                this.clear()
                                this.$ons.notification.toast("操作成功",{timeout:1000})
                            }
                        });
                    }
                })

            },
            clear :function () {
                this.boxNum = null;
                this.batch = null;
                this.qcReasonCode = null;
                this.qcQty = null;
            },
            //根据送检单查询数据
            query(event){
                let keyCode = event.keyCode
                if(keyCode == 13){
                    queryBatchInfo({'INSPECTION_NO':this.inspectionNo}).then( d => d.data ).then(d=>{
                        let result = d.data;
                        if(d.code != '0'){
                            this.$ons.notification.toast(d.message,{timeout:1000});
                        }
                        else if(result.length == 0){
                            this.$ons.notification.toast("送检单不存在",{timeout:1000});
                        }
                        else if(result.length == 1){
                            this.qcQty = result[0].SUM_INSPECTION_QTY
                            this.boxNum = result[0].LABELS
                            this.batch = result[0].BATCHS
                        }

                    })
                }

            }
        }
    }
</script>

<style>


</style>
