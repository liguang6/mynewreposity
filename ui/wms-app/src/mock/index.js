import Mock from 'mockjs'
import {initShelfTask,ubTransfer,queryUbTreansferBarcodeDesc,whTaskList} from './in'

Mock.setup({
    timeout:1000
})
//定义拦截规则，被拦截的URL使用mock 数据
/*
Mock.mock(/\/in\/init_shelf_task_mock/,initShelfTask)
*/
Mock.mock(/\/in\/ub_transfer_init/,ubTransfer)
Mock.mock(/\/in\/ub_transfer_barcode_desc/,queryUbTreansferBarcodeDesc)
Mock.mock(/\/in\/wmsinbound\/pda\/wh_task_list/,whTaskList);

export default Mock
