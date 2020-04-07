
<template>
    <v-ons-page>
        <toolbar :title="'数据表'" :action="toggleMenu" ></toolbar>

        <v-ons-card>
      
      <p v-if="this.list.length===0">{{msg}}</p>
      <v-ons-list v-if="this.list.length!==0">
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">配送单号/行号</v-ons-col>        
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">物料号</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">数量/已扫</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">工位</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" @click="gotoItem(mat)" v-for="(mat, $index) in this.list" :key="$index">
                 
          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.DELIVERY_NO}}/{{mat.DLV_ITEM}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.QTY}}/{{mat.reQTY}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.POINT_OF_USE}}</v-ons-col>

       
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>


        <v-ons-bottom-toolbar class="bottom-toolbar">
          <!--  <v-ons-button @click="deleteRow">删除</v-ons-button> -->
            <v-ons-button @click="back">返回</v-ons-button>   
            <v-ons-button @click="confirm">确认拣配</v-ons-button>   
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import { mapActions, mapMutations,mapState } from 'vuex' 

    export default {
        props:['toggleMenu'],
        components:{toolbar},
        computed: mapState({       
        pageDataList: (state) => state.wms_out.pageDataList,
        pageCache: (state) => state.wms_out.pageCache, 
        }),
        mounted(){
          this.pageDataList.forEach(ele=>{
            ele.reQTY = 0
          })
          this.pageDataList.forEach(ele=>{
            ele.label = []
              this.pageCache.forEach(e=>{
                if(ele.MATNR==e.MATNR){
                  ele.label.push(e.LABEL_NO)
                  ele.reQTY += e.BOX_QTY
                }                 
            })
          })       
          this.list = this.pageDataList      
        },  
        data() {
          return {
            list:[],
            msg:'没有相关数据..'           
          }
        }, 
        methods : {           
             ...mapMutations([
                'setPageCache','setPageDataList','setPage','setPageData'
             ]),
             ...mapActions([
                'JITPick'
          ]),
            back(){
                this.$emit('gotoPageEvent','JITPick')
            },
            gotoItem(mat){
              let arr = this.pageCache.filter(ele=>{
                return ele.MATNR == mat.MATNR
              })
              this.pageDataList.forEach(ele=>{
                ele.labelArr = []
                if(ele.MATNR==mat.MATNR)
                  ele.labelArr = arr
              })
              this.$emit('gotoPageEvent','JITPickLabel')
            },
            confirm(){
              if(this.list.length&&(!this.list[0].reQTY)){
                 this.$ons.notification.toast('拣配数量不能为0!', {timeout: 2000})
                 return
              }
             if(this.list.length){
               let o = this.list[0]
               o.labelInfo = this.pageCache
               this.JITPick({SAVE_DATA:this.list}).then(res=>{               
                 this.$emit('gotoPageEvent','JITPick')
                 if(res.data.code==0){
                    this.setPageDataList([])
                    this.setPageCache([])
                    this.setPageData([])
                    this.$ons.notification.toast('拣配成功!', {timeout: 2000})
                 }                   
                 else
                    this.$ons.notification.toast('拣配失败!', {timeout: 2000})
               })
              }
            }         
        }
    }
</script>
