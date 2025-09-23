import axios from 'axios'

// 创建自定义的 Axios 实例，用于 OpenAPI 请求
export const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 60000,
  withCredentials: true,
})

// 添加请求拦截器
axiosInstance.interceptors.request.use(
  function (config) {
    return config
  },
  function (error) {
    return Promise.reject(error)
  },
)

// 添加响应拦截器
axiosInstance.interceptors.response.use(
  function (response) {
    const { data } = response
    if (data.code === 40100) {
      // 不是获取用户信息的请求，并且用户目前不是已经在用户登录页面，则跳转到登录页面
      if (!response.request.responseURL.includes('/login') && !window.location.pathname.includes('/login')) {
        window.location.href = `/login`
      }
    }
    return response
  },
  function (error) {
    return Promise.reject(error)
  },
)
