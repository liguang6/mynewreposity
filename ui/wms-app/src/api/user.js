import axios from '@/libs/api.request'

export const getPdaUserAuthWh = ({userName}) => {
  const data = {
    username: userName
  }
  return axios.request({
    url: '/getPdaUserAuthWh',
    params: data,
    method: 'post',
  })

}

export const login = ({ userName, password,whnumber }) => {
  const data = {
    username: userName,
    password: password,
    whnumber: whnumber,
    language: 'zh_CN'
  }
  //return { token: 'admin'}
  return axios.request({
    url: '/pdaLogin',
    params: data,
    method: 'post',
  })
}

export const logout = ({username}) => {
  const data = {
    username: username
  }
  return axios.request({
    url: '/logout',
    params: data,
    method: 'post'
  })
}

export const getUnreadCount = () => {
  return axios.request({
    url: 'message/count',
    method: 'get'
  })
}

export const getMessage = () => {
  return axios.request({
    url: 'message/init',
    method: 'get'
  })
}

export const getContentByMsgId = msg_id => {
  return axios.request({
    url: 'message/content',
    method: 'get',
    params: {
      msg_id
    }
  })
}

export const hasRead = msg_id => {
  return axios.request({
    url: 'message/has_read',
    method: 'post',
    data: {
      msg_id
    }
  })
}

export const removeReaded = msg_id => {
  return axios.request({
    url: 'message/remove_readed',
    method: 'post',
    data: {
      msg_id
    }
  })
}

export const restoreTrash = msg_id => {
  return axios.request({
    url: 'message/restore',
    method: 'post',
    data: {
      msg_id
    }
  })
}
