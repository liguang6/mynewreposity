<!--

-->

<template>

    <v-ons-page>
        <custom-toolbar :title="'单箱复检'" :action="toggleMenu"></custom-toolbar>

        <v-ons-card>

            <v-ons-row class="qc-row">
                <v-ons-col width="30%">
                    箱号：
                </v-ons-col>
                <v-ons-col>
                    <v-ons-input placeholder="箱号" @keydown.enter="parseLabelNo" v-model="boxNum"></v-ons-input>
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

        </v-ons-card>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="commit">提交</v-ons-button>
            <v-ons-button @click="back">返回</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import customToolbar from '_c/toolbar'
    import {mapActions} from 'vuex'
    import {single_qc_rejudge_save} from '@/api/qc'

    export default {
        components : {customToolbar},
        props : ['toggleMenu'],
        data(){
          return {
              boxNum:'',//箱号、标签号
              valid : true,//合格标识
              qcCode :'02',
              qcReasonCode:'',
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
            parseLabelNo:function(){
                let re = /.*LABEL_NO:(\d|\w+),.*/;
                let r =  re.exec(this.boxNum);
                if(r != null)
                    this.boxNum = r[1];
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
                single_qc_rejudge_save({"LABEL_NO":this.boxNum,"QC_RESULT_CODE":this.qcCode,"QC_RESULT_TEXT":QC_RESULT_TEXT,'QC_RESULT':QC_RESULT}).then(d=> {
                    if(d.code != '0'){
                        this.$ons.notification.toast(d.msg,{timeout:1000});
                    }else {
                        this.$ons.notification.toast("操作成功",{timeout:1000});
                        this.clear();
                    }
                })
            },
            clear(){
                this.boxNum = null;
                this.qcCode = null;
                this.qcReasonCode = null;
            }
        }
    }
</script>
