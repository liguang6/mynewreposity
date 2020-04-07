<!--

-->

<template>

    <v-ons-page>
        <custom-toolbar :title="'整批复检'" :action="toggleMenu"></custom-toolbar>

        <v-ons-card>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">
                    送检单号：
                </v-ons-col>
                <v-ons-col>
                    <v-ons-input placeholder="送检单号" @keydown="query($event)" v-model="inspectionNo"></v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">
                    待检批次：
                </v-ons-col>
                <v-ons-col>
                    <v-ons-input readonly="readonly" v-model="batch"></v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">
                    待检数量：
                </v-ons-col>
                <v-ons-col>
                    <v-ons-input readonly="readonly" v-model="qty"></v-ons-input>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">
                    质检结果：
                </v-ons-col>
                <v-ons-col>
                    <v-ons-select style="width: 40%" modifier="material" v-model="qcCode">
                        <option v-for="option in qc_dict" :key="option.CODE" :value="option.CODE">{{option.VALUE}}</option>
                    </v-ons-select>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row" v-if="!valid">
                <v-ons-col width="30%">
                    不良原因：
                </v-ons-col>
                <v-ons-col>
                    <v-ons-select style="width: 40%" v-model="qcReasonCode">
                        <option v-for="a in qc_reason_list" :key="a.id" :value="a.reasonCode">{{a.reasonDesc}}</option>
                    </v-ons-select>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">
                    有效日期：
                </v-ons-col>
                <v-ons-col>
                    <datepicker :placeholder="effectDateEditable?'不允许修改有效日期':''" v-model="effectDate"  :disabled="effectDateEditable" :format="'yyyy-MM-dd'" :language="zh" :input-class="'text-input text-input--material'"></datepicker>
                </v-ons-col>
            </v-ons-row>

        </v-ons-card>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="commit">提交</v-ons-button>
            <v-ons-button @click="back">返回</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import customToolbar from '_c/toolbar'
    import {zh} from 'vuejs-datepicker/src/locale'
    import Datepicker from 'vuejs-datepicker'
    import {mapActions} from 'vuex'
    import {batch_qc_rejudge_query,batch_qc_rejudge_save} from '@/api/qc'

    export default {
        components : {customToolbar,Datepicker},
        props : ['toggleMenu'],
        data(){
            return {
                zh : zh,//日期插件，中文
                valid : true,//合格标识
                qcCode :'02',//质检结果
                qcReasonCode:'',//不合格原因
                batch:'',//待检批次
                inspectionNo:'',//送检单号
                qty:'',//待检数量
                effectDate :'',//有效日期
                EXTENDED_EFFECT_DATE:null,//是否可以延长有效日期
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
            },
            //是否可以编辑有效日期
            effectDateEditable(){
                return  this.EXTENDED_EFFECT_DATE == null || this.EXTENDED_EFFECT_DATE !='X';
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
        created(){
            this.getQcDict();//初始化质检结果
            this.getQcReasonList();//初始化不良原因
            this.getQcResultConfig();//质检结果配置
        },
        methods : {
            ...mapActions (['getQcDict','getQcReasonList','getQcResultConfig']),
            back(){
                this.$emit('gotoPageEvent',"home");
                this.$store.commit('tabbar/set',2);
            },
            query(e){
                if(e.keyCode != 13)
                    return ;
                batch_qc_rejudge_query({'INSPECTION_NO':this.inspectionNo}).then(d=>{
                    if(d.code != '0'){
                        this.$ons.notification.toast(d.msg,{timeout:1000})
                    }else {
                        let v = d.data[0];
                        this.batch = v.BATCH;
                        this.qty = v.INSPECTION_QTY;
                        this.effectDate = v.EFFECT_DATE;
                        this.EXTENDED_EFFECT_DATE = v.EXTENDED_EFFECT_DATE;
                    }
                })

            },
            commit(){
                let QC_RESULT_TEXT = null;
                let QC_RESULT = null;
                for(let f of this.qc_dict){
                    if(f.CODE == this.qcCode){
                        QC_RESULT_TEXT = f.VALUE;
                    }
                }

                for(let f of this.qc_reason_list){
                    if(f.reasonCode == this.qcReasonCode){
                        QC_RESULT = f.reasonDesc;
                    }
                }
                batch_qc_rejudge_save({'INSPECTION_NO':this.inspectionNo,"QC_RESULT_CODE":this.qcCode,"EFFECT_DATE":this.effectDate,"QC_RESULT_TEXT":QC_RESULT_TEXT,"QC_RESULT":QC_RESULT}).then(d=>{
                    if(d.code != '0'){
                        this.$ons.notification.toast(d.msg,{timeout:1000});
                    }else {
                        this.$ons.notification.toast('操作成功',{timeout:1000});
                        this.clear();
                    }
                })
            },
            clear(){
                this.inspectionNo = null;
                this.effectDate = null;
                this.qcCode = null;
                this.qcReasonCode = null;
                this.qty = null;
            }
        }
    }
</script>
