<template>
    <v-ons-page>
        <toobar :title="'上架'" :action="toggleMenu"></toobar>
                <v-ons-list>

                    <v-ons-list-item v-for="(type,$index) in shelfTypes" :key="type.value">
                        <label class="left">
                            <v-ons-radio :input-id="'radio-' + $index" :value="type.value" v-model="shelf">

                            </v-ons-radio>
                        </label>
                        <label :for="'radio-' + $index" class="center">{{type.name}}</label>
                    </v-ons-list-item>

                </v-ons-list>
        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="back">返回</v-ons-button>
            <v-ons-button @click="confirm">确认</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toobar from '_c/toolbar'
    export default {
        components : {toobar},
        props : ['toggleMenu'],
        data(){
            return {
                shelfTypes : [{value:'1',name:'扫描实物条码上架'},{value:'2',name:'根据上架任务单上架'}]
            }
        },
        computed : {
            shelf : {
                get(){
                    return this.$store.state.wms_in.shelf.shelfType
                },
                set(newVal){
                    this.$store.commit('shelf/shelfType',newVal)
                }
            }
        },
        methods : {
            back(){
                this.$emit('gotoPageEvent','home')
                this.$store.commit('tabbar/set',1)
            },
            confirm(){
                if(this.shelf === '2')
                    this.$emit('gotoPageEvent','ShelfViewSelect')
              //  if(this.shelf === '1')
                   // this.$emit('gotoPageEvent','ShelfViewScanner')
            }
        }
    }
</script>
