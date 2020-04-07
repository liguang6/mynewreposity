<template>
    <v-ons-splitter>
      <v-ons-splitter-side swipeable width="190px" collapse="" side="left" :open.sync="openSide" >
        <v-ons-page>
          <v-ons-list>
            <v-ons-list-header>菜单..</v-ons-list-header>
            <!--<v-ons-list-item  v-for="page in pages" tappable modifier="chevron" v-bind:key="page.url"
              @click="currentPage = page.url; openSide = false" >
              <div class="center">{{ page.name }}</div>
            </v-ons-list-item>-->
            <v-ons-list-item tappable modifier="chevron" v-bind:key="'home'" @click="currentPage = 'home'; openSide = false">
              <div class="center">首页</div>
            </v-ons-list-item>

            <v-ons-list-item expandable >
              <div class="center">入库</div>
              <div style="" class="expandable-content">
                <v-ons-list-item  v-for="page in pages" tappable modifier="chevron" v-bind:key="page.id" @click="menu_click(page)" >
                  <div class="center">{{ page.name }}</div>
                </v-ons-list-item>
              </div>
            </v-ons-list-item>


            <v-ons-list-item   expandable>
              <div class="center">质检</div>

              <div class="expandable-content">
                <v-ons-list-item v-for = 'page in qc_pages' tappable modifier="chevron" :key = 'page.id' @click="menu_click(page)">
                  <div class="center">{{page.name}}</div>
                </v-ons-list-item>
              </div>
            </v-ons-list-item>

            <v-ons-list-item tappable modifier="chevron" v-bind:key="'menu2'" @click="currentPage = 'settings'; openSide = false">
              <div class="center">出库</div>
            </v-ons-list-item>
            <v-ons-list-item tappable modifier="chevron" v-bind:key="'menu3'" @click="currentPage = 'settings'; openSide = false">
              <div class="center">库内</div>
            </v-ons-list-item>
            <v-ons-list-item tappable modifier="chevron" v-bind:key="'settings'" @click="currentPage = 'settings'; openSide = false">
              <div class="center">设置</div>
            </v-ons-list-item>

          </v-ons-list>
        </v-ons-page>
      </v-ons-splitter-side>

      <v-ons-splitter-content>
        <div v-on:gotoPageEvent='gotoPage' :is="currentPage" :toggle-menu="() => openSide = !openSide"></div>
      </v-ons-splitter-content>

    </v-ons-splitter>


</template>

<script>
  import home from '@/view/homePage'
  import in_receipt_scm from '@/view/in/receiptScm.vue'                     //SCM送货单收货
  import in_receipt_scmBarcode from '@/view/in/receiptScmBarcode.vue'       //SCM扫描条码收货
  import in_receipt_stoDelivery from '@/view/in/receiptStoDelivery.vue'     //STO交货单收货
  import in_receipt_stoSend from '@/view/in/receiptStoSend.vue'             //STO送货单收货
  import in_receipt_stoLogistics from '@/view/in/receiptStoLogistics.vue'  //STO物流发货清单收货
  import in_receipt_305Barcode from '@/view/in/receipt305Barcode.vue'       //305工厂间调拨(根据条码收货)
  import in_receipt_305Sap from '@/view/in/receipt305Sap.vue'               //305工厂间调拨(303SAP凭证收货)
  import in_receipt_305Wms from '@/view/in/receipt305Wms.vue'               //305工厂间调拨(WMS调货单收货)
  import in_receipt_stoWms from '@/view/in/receiptStoWms.vue'               //STO一步联动(WMS调货单收货)
  import in_receipt_stoBarcode from '@/view/in/receiptStoBarcode.vue'       //STO一步联动(根据条码收货)
  import in_confirm from '_c/in/inConfirm.vue'
  import in_barcodeCheck from '_c/in/barcodeCheck.vue'
  import in_matCheck from '_c/in/matCheck.vue'
  import in_matDataTable from '_c/in/matDataTable.vue'
  import settings from '_c/settingsPage'
  import in_receipt_writeOff from '@/view/in/receiptWriteOff.vue'       //收货凭证冲销
  import in_incoming_writeOff from '@/view/in/incomingWriteOff.vue'       //入库凭证冲销
  import in_no_inbound from '@/view/in/inboundByNo.vue'       //扫进仓单入库
  import in_barcode_inbound from '@/view/in/inboundByBarcode.vue'  //扫条码入库
  import in_receipt_cloudBarcode from '@/view/in/receiptCloudBarcode.vue'       //SCM扫描条码收货

  import ShelfInitTask from '@/view/in/shelf/ShelfInitTask.vue'
  import ShelfInitTaskDataTable from '@/view/in/shelf/ShelfInitTaskDataTable'
  import ShelfInitTaskDataTableDetail from '@/view/in/shelf/ShelfInitTaskDataTableDetail'
  import ShelfUBTransferOrder from '@/view/in/shelf/ShelfUBTransferOrder.vue'
  import ShelfUBTransferOrderDataTable from '@/view/in/shelf/ShelfUBTransferOrderDataTable'
  import ShelfUBTransferOrderBarCode from '@/view/in/shelf/ShelfUBTransferOrderBarCode.vue'
  import ShelfUBTransferOrderPosting from '@/view/in/shelf/ShelfUBTransferOrderPosting'
  import ShelfView from '@/view/in/shelf/ShelfView'
  import ShelfViewSelect from '@/view/in/shelf/ShelfViewSelect'
  import ShelfViewRecommend from '@/view/in/shelf/ShelfViewRecommend'
  import ShelfViewRecommendStart from '@/view/in/shelf/ShelfViewRecommendStart.vue'
  import ShelfViewRecommendEnd from '@/view/in/shelf/ShelfViewRecommendEnd.vue'
  import ShelfViewRecommendEndDisplay from '@/view/in/shelf/ShelfViewRecommendEndDisplay'
  import ShelfViewTaskList from '@/view/in/shelf/ShelfViewTaskList'

  import ShelfViewScanner from '@/view/in/shelf/ShelfViewScanner'
  import ShelfViewScannerDataTable from '@/view/in/shelf/ShelfViewScannerDataTable'
  import ShelfViewScannerDataTableDetail from '@/view/in/shelf/ShelfViewScannerDataTableDetail'

  //import ShelfTaskList from '@/view/in/shelf/ShelfTaskList'
  //import ShelfTaskWithBarcode from '@/view/in/shelf/ShelfTaskWithBarcode'

  import WmsQc from '@/view/qc/WmsQc'
  import WmsQcSingle from '@/view/qc/WmsQcSingle'
  import WmsQcBatch from '@/view/qc/WmsQcBatch'
  import WmsQcSingleChange from '@/view/qc/WmsQcSingleChange'
  import WmsQcBatchChange from '@/view/qc/WmsQcBatchChange'
  import WmsQcStockBatch from '@/view/qc/WmsQcStockBatch'
  import WmsQcStockSingle from '@/view/qc/WmsQcStockSingle'

  import return01 from '@/view/return/return01.vue'                   //收料房退货 外购退货
  import return02 from '@/view/return/return02.vue'                   //收料房退货 STO退货
  import return161Barcode from '@/view/return/return161Barcode.vue'   //161退货订单退货 按条码退货
  import return122Barcode from '@/view/return/return122Barcode.vue'   //122采购订单退货 按条码退货
  import re_matDataTable from '_c/return/matDataTable.vue'
  import re_confirm from '_c/return/returnConfirm.vue'
  import re_matCheck from '_c/return/matCheck.vue'

	import stocktaking from '@/view/inwh/stocktaking.vue'               //库内 盘点
  import out_reqCreate from '@/view/out/reqCreate.vue'
  import UnShelfView from '@/view/out/unShelf/UnShelfView'
  import UnShelfViewSelect from '@/view/out/unShelf/UnShelfViewSelect'
  import UnShelfViewRecommend from '@/view/out/unShelf/UnShelfViewRecommend'
  import UnShelfViewRecommendStart from '@/view/out/unShelf/UnShelfViewRecommendStart'

  import pdaScanner from '@/view/out/pdaScanner'
  import pdaScannerDataTable from '@/view/out/pdaScannerDataTable'
  import pdaScannerByHwyd from '@/view/out/pdaScannerByHwyd'   

  import UnShelfViewRecommendEnd from '@/view/out/unShelf/UnShelfViewRecommendEnd'
  import UnShelfViewScannerDataTable from '@/view/out/unShelf/UnShelfViewScannerDataTable'
  import handover from '@/view/out/handover'
  import InventorySearch from '@/view/out/InventorySearch'
  import HandoverDataTable from '@/view/out/HandoverDataTable'

  import StepLinkage from '@/view/out/StepLinkage'
  import StepLinkageDataTable from '@/view/out/StepLinkageDataTable'
  import StepLinkageLabelDataTable from '@/view/out/StepLinkageLabelDataTable'
  import StepLinkageHandover from '@/view/out/StepLinkageHandover'
  import StepLinkageHandoverEnd from '@/view/out/StepLinkageHandoverEnd'
  import JITPick from '@/view/out/JITPick'  
  import JITPickData from '@/view/out/JITPickData' 
  import JITPickLabel from '@/view/out/JITPickLabel'
  import JISCreateReq from '@/view/out/JISCreateReq'
  import dispatching_confirm from '@/view/out/cswlms/dispatchingConfirm.vue'

  import { mapActions, mapMutations,mapState } from 'vuex'
  export default {
    mounted () {
      console.log('-->UserWerks : ' + sessionStorage.getItem('UserWerks'));
      this.setUserName(sessionStorage.getItem('UserName'))
      this.setUserWerks(sessionStorage.getItem('UserWerks'))
      this.setUserWhNumber(sessionStorage.getItem('UserWhNumber'))

      const userAuthUrlSet = JSON.parse(sessionStorage.getItem('UserAuthUrlSet'))
      const userAuthUrlStr = ','+userAuthUrlSet.join(",")+",";
      sessionStorage.setItem('userAuthUrlStr' ,userAuthUrlStr)
    },
    data() {
      return {
        currentPage: 'home',
        pages: [
          {id:1,url: "in_receipt_scm",name: 'SCM送货单收货'},
          {id:2,url: "in_receipt_scmBarcode",name: '扫描条码收货'},
          {id:3,url: "in_receipt_stoDelivery",name: 'STO交货单收货'},
        ],
        qc_pages : [
          {id:1,url:'WmsQcSingle',name: "单箱检验"},
          {id:2,url:'WmsQcBatch',name:'整批质检'},
          {id:3,url:'WmsQcSingleChange',name:'单箱改判'},
          {id:4,url:'WmsQcBatchChange',name:'整批改判'}
        ],
        openSide: false
      };
    },
    methods: {
      ...mapActions([

      ]),
      ...mapMutations([
        'setUserName',
        'setUserWerks',
        'setUserWhNumber',
        'resetState'
      ]),
      menu_click(page){
        this.currentPage = page.url
        this.openSide = false;
        this.resetState()   //从菜单进入页面时初始仳页面数据
      },
      gotoPage(page){
        this.currentPage = page
        /*const userAuthUrlSet = JSON.parse(sessionStorage.getItem('UserAuthUrlSet'))
        var flag = false;
        for(var i=0; i<userAuthUrlSet.length;i++){
          if(page === userAuthUrlSet[i]){
            flag = true;
            break
          }
        }
        if(flag){
            this.currentPage = page
            console.log('-->gotoPage:' + page)
        }else{
            //
            console.log('-->gotoPage:没有权限'+page)
            this.$ons.notification.toast('无权访问'+page, {timeout: 1000});
        } */

      }
    },
    components: {
      home,
      in_receipt_scm,in_receipt_scmBarcode,in_receipt_stoDelivery,in_receipt_stoSend,in_receipt_stoLogistics,
      in_receipt_305Barcode,in_receipt_305Sap,in_receipt_305Wms,in_receipt_stoWms,in_receipt_stoBarcode,WmsQcStockSingle,
      in_confirm,in_barcodeCheck,in_matCheck,in_matDataTable,in_receipt_cloudBarcode,stocktaking,
      in_receipt_writeOff,in_incoming_writeOff,in_no_inbound,in_barcode_inbound,

      settings,
      WmsQc,WmsQcSingle,WmsQcBatch,WmsQcSingleChange,WmsQcBatchChange,WmsQcStockBatch,

      ShelfInitTask,ShelfInitTaskDataTable,ShelfViewTaskList,ShelfInitTaskDataTableDetail,ShelfUBTransferOrder,ShelfUBTransferOrderDataTable,ShelfUBTransferOrderBarCode
      ,ShelfUBTransferOrderPosting,ShelfView,ShelfViewSelect,ShelfViewRecommend,ShelfViewRecommendStart,ShelfViewRecommendEnd

      ,ShelfViewRecommendEndDisplay,ShelfViewScanner,ShelfViewScannerDataTable,ShelfViewScannerDataTableDetail,
      
      out_reqCreate,UnShelfViewSelect,UnShelfViewRecommend,UnShelfViewRecommendStart,UnShelfViewRecommendEnd,UnShelfViewScannerDataTable,
      handover,HandoverDataTable,InventorySearch,StepLinkage,StepLinkageDataTable,StepLinkageLabelDataTable,StepLinkageHandover,StepLinkageHandoverEnd,
      return01,return02,re_matDataTable,re_confirm,re_matCheck,pdaScanner,pdaScannerDataTable,pdaScannerByHwyd,JITPick,JITPickData,JITPickLabel,JISCreateReq,
      dispatching_confirm,
      return161Barcode,return122Barcode
    }
  }
</script>
<style>
.expandable-content{
  padding: 0px;
}
</style>

