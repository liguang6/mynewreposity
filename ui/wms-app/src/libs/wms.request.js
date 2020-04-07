import HttpRequest from '@/libs/axios'
import config from '@/config'
const baseUrl = process.env.NODE_ENV === 'development' ? config.baseUrl.wms_dev : config.baseUrl.wms_pro

const wms_axios = new HttpRequest(baseUrl)
export default wms_axios