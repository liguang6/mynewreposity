<!--
表格组件

Yang Lin

2019-04-02
-->

<template>
    <v-ons-list>
        <label :for="'checkbox-all'" v-show="!hidden">
            <v-ons-list-item>
                <v-ons-row>
                    <v-ons-col width="10%">
                        <v-ons-checkbox @click="allSelect" :input-id="'checkbox-all'" v-model="all" :value="'checkbox-all'">

                        </v-ons-checkbox>
                    </v-ons-col>
                    <v-ons-col>全选</v-ons-col>
                </v-ons-row>
            </v-ons-list-item>
        </label>

        <v-ons-list-item v-for="(line,$index) in dataTable" :key="$index">
            <label :for="'checkbox-'+$index" style="width: 100%">
                <v-ons-row v-for="(val,$index_c) in generator(line)" :key="$index_c">
                        <v-ons-col width="10%" v-show="!hidden && val.title !== null">
                            <v-ons-checkbox :checked="'true'" v-if="$index_c == 0" :input-id="'checkbox-'+ $index"
                                            v-model="arr" :value="$index"></v-ons-checkbox>
                        </v-ons-col>

                        <v-ons-col v-show="val.title !== null">
                            <span>
                                <B>{{val.title}}</B>:
                                <data-table-value :val="val.value"
                                                  :valueRender="valueRender(val.name)"
                                                  v-on:value-click="clickCallback(val,val.name,$index)">
                                </data-table-value>
                            </span>
                        </v-ons-col>
                </v-ons-row>
            </label>
        </v-ons-list-item>

    </v-ons-list>
</template>

<script>
    import DataTableValue from '_c/DataTableValue'

    export default {
        props: ['dataTable', 'columns', 'renders','hiddenSelect'],
        data() {
            return {
                all:[],
                arr:[],
                hidden : false
            }
        },
        created(){
            //是否需要隐藏多选框
          if(this.hiddenSelect != null || this.hiddenSelect != undefined){
              this.hidden = true
          }
        },
        components: {DataTableValue},
        methods: {
            //字段渲染函数
            valueRender(column) {
                //获取字段的渲染函数,仅提供显示效果-->TODO:需要改进
                let renderFunc = null
                for (let r in this.renders) {
                    if (r == column) {
                        renderFunc = this.renders[r]
                    }
                }
                if (renderFunc === null) {
                    //没有定义渲染函数的
                    renderFunc = function (val) {
                        return val
                    }
                }
                return renderFunc
            },
            //字段点击事件
            clickCallback(data, value, index) {
                this.$emit('table-column-click', {data: data, column: value, index: index})
            },
            //合并dataTable和columns
            generator(obj) {

                let resultList = [];
                for (let column_key in this.columns) {
                    //遍历对象的属性
                    let value = null;
                    for (let t in obj) {
                        if (t === column_key) {
                            value = obj[t];
                        }
                    }

                    let item = {name: column_key, title:this.columns[column_key] , value: value}
                    resultList.push(item)
                }
                return resultList
            },
            //全选
            allSelect() {
                if(this.all.length != 0){
                    this.arr = []
                }else{
                    //TIP: v-ons-checkbox v-mode 绑定的值都是字符串
                    let arr = this.dataTable.map((v,i)=>""+i)
                    this.arr = arr;
                }
            },
            //获取选中的行的index
            selected(){
                return this.arr
            },
            clearSelect(){
                this.arr = []
            }
        }
    }
</script>
