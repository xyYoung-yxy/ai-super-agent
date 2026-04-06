import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import LoveAppPage from '../views/LoveAppPage.vue'
import ManusAppPage from '../views/ManusAppPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/love-app',
    name: 'LoveApp',
    component: LoveAppPage
  },
  {
    path: '/manus-app',
    name: 'ManusApp',
    component: ManusAppPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router