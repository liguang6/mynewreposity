<template>
    <v-ons-page>
        <toolbar :title="'上架'" :action="toggleMenu"></toolbar>
        <data-table :dataTable="dataTable" :columns="columns"  ref="mytable" ></data-table>
        <v-ons-bottom-toolbar class="bottom-toolbar">
            <v-ons-button @click="del">删除</v-ons-button>
            <v-ons-button @click="back">返回</v-ons-button>
        </v-ons-bottom-toolbar>
    </v-ons-page>
</template>

<script>
    import toolbar from '_c/toolbar'
    import DataTable from '_c/DataTable'
    export default {
        props:['toggleMenu'],
        components:{toolbar,DataTable},
        data(){
            return {
                columns:{LABEL_NO:'条形码',BOX_QTY:'数量'}
            }
        },
        computed : {
          dataTable(){
              let l = [];
              for(let i in this.scanner_label_list){
                    l[i] = {"LABEL_NO":this.scanner_label_list[i].LABEL_NO,"BOX_QTY":this.scanner_label_list[i].BOX_QTY};
              }
              return l;
          },
          scanner_label_list : {
              get(){
                  return this.$store.state.wms_in.shelf.scanner_label_list;
              },
              set(n){
                  this.$store.commit("shelf/scanner_label_list",n);
              }
          }
        },
        methods : {
            back(){
                this.$emit('gotoPageEvent','ShelfViewScannerDataTable')
            },
            del(){
                let arr = this.$refs.mytable.selected();//获取选中的行
                this.scanner_label_list = this.scanner_label_list.filter((v,i)=>{
                    for(let a of arr){
                        if(a == i)
                            return false;
                    }
                    return true;
                });
                this.$refs.mytable.clearSelect();
            }
        }
    }
</script>
