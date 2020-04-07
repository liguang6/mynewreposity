<template>
    <v-ons-page>
        <toolbar :title="'拣配'" :action="toggleMenu"></toolbar>
        <v-ons-list>
            <v-ons-list-item>
                <div class="left" style="width:30%">
                    需求单号：
                </div>
                <div class="center" >
                    <v-ons-input placeholder="需求单号" v-model="requirementNo"></v-ons-input>
                </div>
                <div class="right" >
            <!--        <v-ons-button @click="" style="margin: 6px 0">扫描</v-ons-button> -->
                </div>
            </v-ons-list-item>
            <v-ons-list-item>
                <div class="left" style="width:30%">
                    定位标签：
                </div>
                <div class="center">
                    <v-ons-input placeholder="定位标签" v-model="positionTag"></v-ons-input>
                </div>
            </v-ons-list-item>
        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="confirm">确认</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>
<script>
    import toolbar from '_c/toolbar'
    import { mapActions, mapMutations,mapState } from 'vuex'
    export default {
        components : {toolbar},
        props : ['toggleMenu'],
        computed: mapState({
        pageDataList: (state) => state.wms_out.pageDataList, 
        }),
        data(){
            return {
                requirementNo:'',
                positionTag: '',
                list: []       
            }
        },
        methods : {
            ...mapActions([
                'recommend'
            ]),
            ...mapMutations([
                 'setPageDataList','setRequirementNo','setPageCache','setSortNo'
             ]),
            confirm(){
                if(this.requirementNo)
                this.recommend({requirementNo:this.requirementNo,WERKS:sessionStorage.getItem('UserWerks'),WH_NUMBER:sessionStorage.getItem('UserWhNumber')}).then(res => {
                    this.list = res.data.page.list
                    this.list = this.list.filter(ele=>{
                                    return Boolean(ele.MSG)==false
                                })
                    for (let i = this.list.length-1; i>-1; i--){
                         let ele = this.list[i]
                            //if(ele.MSG&&ele.MSG.indexOf('未满足数量')!=-1)
                               //this.list.splice(i,1)
                            if (ele.RECOMMEND_QTY==0)
                               this.list.splice(i,1)                                                     
                    }
                    if(this.list.length==0){
                        this.$ons.notification.toast('需求单号不存在或已完成拣配下架或库存为0!', {timeout: 2000});
                        return
                    }else{
                        this.setRequirementNo(this.requirementNo)
                        this.setPageDataList(this.list) 
                        this.setPageCache(this.pageDataList[0])
                        this.$emit('gotoPageEvent','UnShelfViewRecommend')
                        if(this.positionTag)
                        this.setSortNo(this.positionTag)
                    }                                    
                })               
            }
        }
    }
</script>
