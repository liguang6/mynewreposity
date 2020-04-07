<template>
    <v-ons-page>
        <custom-toolbar :title="'单箱改判'" :action="toggleMenu"></custom-toolbar>
        <v-ons-card>
            <v-ons-row class="qc-row">
                <v-ons-col width="30%">箱号：</v-ons-col>
                <v-ons-col>
                    <v-ons-input type="text" @keydown="query($event)" v-model="boxNum"  name="箱号" autocomplete="off"  autofocus="true"  modifier="material" class="qc-input" :class="{'input': true, 'is-danger': errors.has('箱号') }"  v-validate="'required'"> </v-ons-input>
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
                <v-ons-col width="30%">改判原因：</v-ons-col>
                <v-ons-col>
                    <v-ons-select style="width: 40%" v-model="qcReasonCode" v-validate="'required'">
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
    import customToolbar from '_c/toolbar';
    import {mapActions} from 'vuex';
    import {single_qc_change_query,single_qc_change_save} from '@/api/qc';

    export default {
        props: ['toggleMenu'],
        components: { customToolbar },

        data(){
            return {
                disable : false,//加载状态标识符
                boxNum:'',//箱号(标签号)
                valid : true,//是否是合格类型
                qcCode:'02',//质检结果代码
                qcCodeText : '合格',
                qcReasonCode:'',//不良原因代码
                qcReasonText :'',//不良原因文本
                qcResultObj:{},//质检结果对象
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
        methods : {
            ...mapActions(['getQcDict','getQcReasonList','getQcResultConfig']),
            back : function () {
                this.$emit('gotoPageEvent',"home");
                this.$store.commit('tabbar/set',2);
            },
            commit : function () {
                if(!this.valid && this.qcReasonCode == null) this.$ons.notification.toast("请选择不良原因",{timeout:1000});
                this.$validator.localize('zh_CN');
                this.$validator.validateAll().then( (result) => {
                    if(!result){
                        this.$ons.notification.toast("箱号不能为空",{timeout:1000});
                        return false;
                    }else {
                        this.disable = true;
                        this.qcResultObj.qcResultCode = this.qcCode;
                        this.qcResultObj.qcResultText = this.qcCodeText;
                        this.qcResultObj.qcResult = this.qcReasonText;
                        this.qcResultObj.pdaLabelNo = this.boxNum;

                        single_qc_change_save(this.qcResultObj).then(d=>{
                            this.disable = false;
                            if(d.code != '0'){
                                this.$ons.notification.toast(d.msg,{timeout:1000});
                            }else {
                                this.$ons.notification.toast("保存成功",{timeout:1000});
                                this.clear();
                            }
                        })
                    }
                })

            },
            clear :function () {
                this.boxNum = null;
                this.qcCode = null;
                this.batch = null;
                this.qcReasonCode = null;
            },
            query : function (event) {
                if(event.keyCode  != 13){
                    return;
                }

                let re = /.*LABEL_NO:(\d|\w+),.*/;
                let r =  re.exec(this.boxNum);
                if(r != null)
                    this.boxNum = r[1];

                single_qc_change_query({'LABEL_NO':this.boxNum}).then(d=> {
                    if(d.code != '0'){
                        this.$ons.notification.toast(d.msg,{timeout:1000});
                    }else {
                        let res = d.data;
                        if(res.length == null ||  res.length == 0 ){
                            this.$ons.notification.toast("没找到已质检的物料信息",{timeout:1000});
                        }else{
                            this.qcResultObj = res[0];
                            this.qcCode = res[0].qcResultCode;
                            let qcReasonText = res[0].qcResult;
                            for(let f of this.qc_reason_list){
                                if(f.reasonDesc == qcReasonText){
                                    this.qcReasonCode = f.reasonCode;
                                }
                            }
                        }
                    }
                })
            }
        },
        created(){
            this.getQcDict();//初始化质检结果
            this.getQcReasonList();//初始化不良原因
            this.getQcResultConfig();//初始化质检结果配置
        },

    }
</script>

<style>

    .qc-row {
        margin: 32px 0;
    }

</style>
