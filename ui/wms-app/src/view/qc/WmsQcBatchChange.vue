<!--
质检改判 -> 整批质检
Yang Lin
2019-3-26
-->
<template>
    <v-ons-page>
        <custom-toolbar :title="'整批改判'" :action="toggleMenu"></custom-toolbar>
        <v-ons-card>
            <v-ons-row class="qc-row">
                <v-ons-col width="30%">箱号：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" readonly="readonly"  class="qc-input" v-model="boxNum" name="箱号"  > </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">送检单号：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" class="qc-input" @keydown="query($event)" placeholder="送检单号"  v-validate="'required'" :class="{'input': true, 'is-danger': errors.has('送检单号') }"  v-model="inspectionNo"> </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">改判批次：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" readonly="readonly"  class="qc-input" v-model="batch"> </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">改判数量：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" readonly="readonly"  class="qc-input" v-model="qty"> </v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">改判结果：</v-ons-col>
                <v-ons-col>
                    <v-ons-select style="width: 40%"  v-model="qcCode">
                        <option v-for="option in qc_dict" :key="option.CODE" :value="option.CODE">{{option.VALUE}}</option>
                    </v-ons-select>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row" v-if="!valid">
                <v-ons-col width="30%">改判原因：</v-ons-col>
                <v-ons-col>
                    <v-ons-select style="width: 40%" v-model="qcReasonCode">
                        <option v-for="a in qc_reason_list" :key="a.id" :value="a.reasonCode">{{a.reasonDesc}}</option>
                    </v-ons-select>
                </v-ons-col>
            </v-ons-row>


        </v-ons-card>

        <ons-bottom-toolbar>
            <div style="text-align: center;">
                <v-ons-button style="margin:0 16px" @click="commit">提交</v-ons-button>
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
    import {batch_qc_change_query,batch_qc_change_save} from '@/api/qc'

    export default {
        props: ['toggleMenu'],
        components: { customToolbar },
        data(){
            return {
                disable : false,
                boxNum:'',//箱号/标签号
                qcCode:'02',//质检结果
                qcCodeText:'',
                qcReasonCode:'',
                qcReasonText:'',
                batch:'',//批次
                qty:null,//改判数量
                qcReason:'外观不合格',
                inspectionNo:'',//送检单号
                valid : true,//是否是合格类型
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

                //更新质检结果文本
                for(let f of this.qc_dict){
                    if(f.CODE == val){
                        this.qcCodeText = f.VALUE;
                    }
                }
            },
            qcReasonCode(val){
                for(let f of this.qc_reason_list){
                    if(val == f.reasonCode){
                        this.qcReasonText = f.reasonDesc;
                    }
                }
            }
        },
        created(){
            this.getQcDict();//初始化质检结果
            this.getQcReasonList();//初始化不良原因
            this.getQcResultConfig();//初始化质检结果配置
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
        methods :{
            ...mapActions(['getQcDict','getQcReasonList','getQcResultConfig']),
            back : function () {
                this.$emit('gotoPageEvent',"home");
                this.$store.commit('tabbar/set',2);
            },
            commit : function () {
                this.$validator.localize('zh_CN');
                this.$validator.validateAll().then( (result) => {
                    if(!result){
                        this.$ons.notification.toast("箱号不能为空",{timeout:1000})
                        return false
                    }else {
                        this.disable = true;
                        batch_qc_change_save({'INSPECTION_NO':this.inspectionNo,'QC_RESULT_CODE':this.qcCode,'QC_RESULT_TEXT':this.qcCodeText,'QC_RESULT':this.qcResult,'LABELS':this.boxNum}).then(d=>{
                            this.disable = false;
                            if(d.code != '0'){
                                this.$ons.notification.toast(d.msg,{timeout:1000});
                            }else {
                                this.$ons.notification.toast('操作成功',{timeout:1000});
                            }
                            this.clear();
                        })
                    }
                })

            },
            clear :function () {
                this.boxNum = null;
                this.qcResult = null;
                this.batch = null;
                this.qcReason = null;
                this.qty = null;
            },
            query(e) {
                if(e.keyCode != 13)
                    return ;
                batch_qc_change_query({'INSPECTION_NO':this.inspectionNo}).then(d=> {
                    if(d.code != '0'){
                        this.$ons.notification.toast(d.msg,{timeout:1000});
                        this.clear();
                    }else if(d.data.length >= 1){
                        let obj = d.data[0];
                        this.boxNum = obj.LABELS;
                        this.batch = obj.BATCHS;
                        this.qcCode = obj.QC_RESULT_CODE;
                        this.qty = obj.SUM_INSPECTION_QTY;
                        //加上不合格原因字段, 数据库保存的是字典的值，需要根据值去匹配代号
                        let QC_RESULT = obj.QC_RESULT;
                        for(let f of this.qc_reason_list){
                            if(QC_RESULT == f.reasonDesc){
                                this.qcReasonCode = f.reasonCode;
                            }
                        }
                    }
                })
            }
        }
    }
</script>

<style>

    .qc-row {
        margin: 32px 0;
    }

</style>
