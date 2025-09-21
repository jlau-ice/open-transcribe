import axios from 'axios'

// 创建 Axios 实例
const request = axios.create({
  baseURL: 'http://127.0.0.1:8101/',
  timeout: 60000,
  withCredentials: true,
})

// 全局请求拦截器
request.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    return config
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error)
  },
)

// 全局响应拦截器
request.interceptors.response.use(
  function (response) {
    const { data } = response
    // 未登录
    console.log('拦截器 data:', response)
    if (data.code === 40100) {
      // 不是获取用户信息的请求，并且用户目前不是已经在用户登录页面，则跳转到登录页面
      if (!response.request.responseURL.includes('/login') && !window.location.pathname.includes('/login')) {
        // 重定向
        // ?redirect=${window.location.href}
        window.location.href = `/login`
      }
    }
    return response
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error)
  },
)
export default request