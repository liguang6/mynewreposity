<template>
    <v-ons-page>
        <toolbar :title="'确认过账'" :action="toggleMenu"></toolbar>

        <v-ons-card class="form-row-card">
            <v-ons-row>
                <v-ons-col width="30%">凭证日期：</v-ons-col>
                <v-ons-col>
                    <datepicker v-model="postData" :format="'yyyy-MM-dd'" :language="zh" :input-class="'text-input text-input--material'" :calendar-class="'datepicker-div'"></datepicker>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row>
                <v-ons-col width="30%">过账日期：</v-ons-col>
                <v-ons-col>
                    <datepicker v-model="gzDate" :format="'yyyy-MM-dd'" :language="zh" :input-class="'text-input text-input--material'"></datepicker>
                </v-ons-col>
            </v-ons-row>

            <v-ons-row>
                <v-ons-col width="30%">抬头文本：</v-ons-col>
                <v-ons-col><v-ons-input  type="text" modifier="material"></v-ons-input></v-ons-col>
            </v-ons-row>
        </v-ons-card>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="back">返回</v-ons-button>
            <v-ons-button @click="posting">过账</v-ons-button>
        </v-ons-bottom-toolbar>


        <v-ons-dialog
            cancelable
            :visible.sync="dialogVisible">
            <v-ons-card>
                <h3>过账凭证</h3>
            <v-ons-row>
                <v-ons-col>WMS凭证号：</v-ons-col>
                <v-ons-col>0000007890</v-ons-col>
            </v-ons-row>

            <v-ons-row>
                <v-ons-col>SAP凭证号：</v-ons-col>
                <v-ons-col>53219998761</v-ons-col>
            </v-ons-row>

            <v-ons-row>
                <v-ons-col>收货单号：</v-ons-col>
                <v-ons-col>235678900</v-ons-col>
            </v-ons-row>

            <v-ons-row>
                <v-ons-col>作业时长：</v-ons-col>
                <v-ons-col>20S</v-ons-col>
            </v-ons-row>
            </v-ons-card>
        </v-ons-dialog>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import Datepicker from 'vuejs-datepicker'
    import {zh} from 'vuejs-datepicker/src/locale'

    export default {
        components:{toolbar,Datepicker},
        props:['toggleMenu'],
        data(){
            return {
                dialogVisible:false,
                postData : new Date(),
                gzDate : new Date(),
                zh:zh
            }
        },
        methods : {
            posting(){
                //do post
                this.dialogVisible=!this.dialogVisible
            },
            back(){
                this.$emit('gotoPageEvent','ShelfUBTransferOrderDataTable')
            }
        }

    }
</script>

<style>
    .form-row-card ons-row{
        margin: 6px 0;
    }
    .datepicker-div {

    }
</style>
