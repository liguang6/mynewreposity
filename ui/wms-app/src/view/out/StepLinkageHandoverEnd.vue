<template>
    <v-ons-page>
        <toolbar :title="'过账凭证'" :action="toggleMenu"></toolbar>

        <v-ons-list>
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col>
                        WMS凭证号:
                    </v-ons-col>
                    <v-ons-col>
                        
                    </v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
            
            <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                      SAP凭证号:
                   </v-ons-col>
                   <v-ons-col>
                       
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>
            
            
                <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                      收货单号:
                   </v-ons-col>
                   <v-ons-col>
                       
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>

                <v-ons-list-item>
               <v-ons-row>
                   <v-ons-col>
                      质检单号:
                   </v-ons-col>
                   <v-ons-col>
                       
                   </v-ons-col>
               </v-ons-row>
            </v-ons-list-item>
        </v-ons-list>

        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="back">返回</v-ons-button>
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
        pageDataList: (state) => state.wms_out.pageDataList       
        }),
        
        data(){
            return{
               
            }
        },
        methods : { 
            back(){           
                this.$emit('gotoPageEvent','StepLinkage')
            }
        }
    }
</script>
