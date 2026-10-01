import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  function readUserInfo() {
    try {
      return JSON.parse(sessionStorage.getItem('userInfo') || 'null')
    } catch {
      sessionStorage.removeItem('userInfo')
      sessionStorage.removeItem('token')
      return null
    }
  }

  const userInfo = ref(readUserInfo())
  const token = ref(sessionStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const roleName = computed(() => (isAdmin.value ? '管理员' : '普通员工'))

  function setLogin(data) {
    userInfo.value = data
    token.value = data.token || 'logged-in'
    sessionStorage.setItem('userInfo', JSON.stringify(data))
    sessionStorage.setItem('token', token.value)
  }

  function logout() {
    userInfo.value = null
    token.value = ''
    sessionStorage.removeItem('userInfo')
    sessionStorage.removeItem('token')
  }

  function updateUserInfo(data) {
    userInfo.value = { ...userInfo.value, ...data }
    sessionStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return { userInfo, token, isLoggedIn, isAdmin, roleName, setLogin, logout, updateUserInfo }
})
