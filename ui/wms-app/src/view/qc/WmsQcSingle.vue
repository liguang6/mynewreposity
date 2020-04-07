<template>
    <v-ons-page>
        <custom-toolbar :title="'单箱质检'" :action="toggleMenu"></custom-toolbar>
        <v-ons-card>
            <v-ons-row class="qc-row">
            <v-ons-col width="30%">箱号：</v-ons-col>
            <v-ons-col>
                <v-ons-input  @keydown.enter="parseLabelNo" type="text" v-model="boxNum" modifier="material" class="qc-input" name="箱号" :class="{'input': true, 'is-danger': errors.has('箱号') }"  v-validate="'required'">  </v-ons-input>
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
    import {boxQcSave} from '@/api/qc'

    export default {
        props: ['toggleMenu'],
        components: { customToolbar },
        data(){
            return {
                disable : false,
                boxNum:'',
                qcCode :'02',
                qcReasonCode:'',
                valid : true,//合格标识
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
        methods:{
            ...mapActions (['getQcDict','getQcReasonList','getQcResultConfig']),
            back:function () {
                this.$emit('gotoPageEvent',"home");
                this.$store.commit('tabbar/set',2);
            },
            parseLabelNo:function(){
                let re = /.*LABEL_NO:(\d|\w+),.*/;
                let r =  re.exec(this.boxNum);
                if(r != null)
                    this.boxNum = r[1];
            },
            commit : function () {
                this.$validator.localize('zh_CN');
                this.$validator.validateAll().then( (result) => {
                    if(!result){
                        this.$ons.notification.toast("箱号不能为空",{timeout:1000})
                        return false
                    }else {
                        this.disable = true
                        //根据code获取text
                        let QC_RESULT_TEXT;
                        let QC_RESULT;
                        for(let v of this.qc_dict){
                            if(v.CODE == this.qcCode){
                                QC_RESULT_TEXT = v.VALUE
                            }
                        }

                        for(let v of this.qc_reason_list){
                            if(v.reasonCode == this.qcReasonCode) {
                                QC_RESULT = v.reasonDesc
                            }
                        }

                        boxQcSave({'LABEL_NO':this.boxNum,'QC_RESULT_CODE':this.qcCode,'QC_RESULT_TEXT':QC_RESULT_TEXT,'QC_RESULT':QC_RESULT}).then(resp => {
                            this.disable = false;
                            if(resp.data.code != '0'){
                                this.$ons.notification.toast("提交失败",{timeout:1000})
                            }else {
                                this.$ons.notification.toast("保存成功",{timeout:1000})
                                this.clear()
                            }
                        })
                    }
                })

            },
            clear :function () {
                this.boxNum = null;
                this.batch = null;
            }
        }
    }
</script>

<style>

    .qc-row {
        margin: 32px 0;
    }


</style>
