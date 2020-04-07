
<template>
    <v-ons-page>
        <toolbar :title="'数据表'" :action="toggleMenu" ></toolbar>

        <v-ons-card>
      
      <v-ons-list>
        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;">   
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">行项目</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">料号</v-ons-col>
          <v-ons-col width="30%" style="border-left-style: ridge;border-left-width: 1px;">批次</v-ons-col>
          <v-ons-col width="20%" style="border-left-style: ridge;border-left-width: 1px;">数量</v-ons-col>
        </v-ons-row>

        <v-ons-row width="100%" style="border-style: ridge;border-width: 1px;text-align:center;" v-for="(mat, $index) in list" :key="$index">
          
          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.REFERENCE_DELIVERY_ITEM}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.MATNR}}</v-ons-col>

          <v-ons-col
            width="30%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.BATCH}}</v-ons-col>

          <v-ons-col
            width="20%"
            style="border-left-style: ridge;border-left-width: 1px;"
          >{{mat.QUANTITY}}</v-ons-col>
             
        </v-ons-row>
      </v-ons-list>
    </v-ons-card>


        <v-ons-bottom-toolbar class="bottom-toolbar">
          <!--  <v-ons-button @click="deleteRow">删除</v-ons-button> -->
            <v-ons-button @click="back">返回</v-ons-button>   
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
        requirementNo: (state) => state.wms_out.requirementNo
        }),
        mounted(){
          this.handoverList({requirementNo:this.requirementNo}).then(res=>{            
            this.list = res.data.page.list  //this.mergeObject(res.data.page.list)
                             
          })
        },  
        data() {
          return {
             list: []
          }
        }, 
        methods : {
            ...mapActions([
                  'handoverList'
            ]),
            back(){
                this.$emit('gotoPageEvent','handover')
            },
            mergeObject(array) {
                var arrayFilted = [];
                array.forEach(function (value,key) {
                //判断过滤后的数组是否为空
                if ( arrayFilted.length == 0 ) {
                    arrayFilted.push(value);
                }else{
                      arrayFilted.forEach( function (valueIndex,keyIndex) {
                      if (valueIndex.MATNR && valueIndex.MATNR !== value.MATNR) {
                          arrayFilted.push(value);
                      }else if (valueIndex.MATNR && valueIndex.MATNR === value.MATNR) {
                          valueIndex.QUANTITY = valueIndex.QUANTITY + value.QUANTITY;
                      }
                      });
                }
                });
                return arrayFilted;
            }
        }
    }
</script>
