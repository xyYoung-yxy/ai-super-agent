<template>
  <div class="home-container">
    <h1>AI智能助手应用平台</h1>
    <p>探索AI的无限可能，体验智能对话的魅力</p>
    
    <!-- 终端窗口 -->
    <div class="terminal-window">
      <div class="terminal-header">
        <div class="terminal-buttons">
          <div class="terminal-button red"></div>
          <div class="terminal-button yellow"></div>
          <div class="terminal-button green"></div>
        </div>
        <div class="terminal-title">yuai-terminal</div>
      </div>
      <div class="terminal-content">
        <p class="terminal-line typing-animation" style="animation-delay: 0.5s;"><span class="terminal-prompt">root@yuai:~$</span> <span class="terminal-command">./launch-ai-platform.sh</span></p>
        <p class="terminal-line typing-animation" style="animation-delay: 1.5s;"><span class="terminal-output">正在初始化 AI 引擎...</span></p>
        <p class="terminal-line typing-animation" style="animation-delay: 2.5s;"><span class="terminal-output">加载智能助手模型...</span></p>
        <p class="terminal-line typing-animation" style="animation-delay: 3.5s;"><span class="terminal-output">连接服务器...</span></p>
        <p class="terminal-line typing-animation" style="animation-delay: 4.5s;"><span class="terminal-success">AI 平台启动成功！选择下方应用开始体验</span></p>
      </div>
    </div>
    
    <div class="app-cards">
      <router-link to="/love-app" class="app-card">
        <div class="app-icon love-icon">❤</div>
        <h2>AI 恋爱大师</h2>
        <p>与AI恋爱大师进行甜蜜对话，获取恋爱建议</p>
      </router-link>
      <router-link to="/manus-app" class="app-card">
        <div class="app-icon brain-icon">🧠</div>
        <h2>AI 超级智能体</h2>
        <p>与超级智能AI进行对话，解决各种问题</p>
      </router-link>
    </div>

    <!-- 底部信息 -->
    <footer class="home-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于我们</h3>
          <p>AI智能助手应用平台致力于为用户提供智能、便捷的AI对话服务，探索人工智能的无限可能。</p>
        </div>
        <div class="footer-section">
          <h3>联系方式</h3>
          <p>邮箱：2272909516@qq.com</p>
          <p>微信：oonelovee</p>
        </div>
        <div class="footer-section">
          <h3>快速链接</h3>
          <ul>
            <li><a href="/" @click.prevent="reloadPage">首页</a></li>
            <li><router-link to="/love-app">AI恋爱大师</router-link></li>
            <li><router-link to="/manus-app">AI超级智能体</router-link></li>
          </ul>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 AI智能助手应用平台. 保留所有权利.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

// 重新加载页面并回到顶部
const reloadPage = () => {
  // 先回到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
  // 然后重新加载页面
  setTimeout(() => {
    window.location.reload()
  }, 500)
}

// 页面加载时回到顶部
onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
})
</script>

<style scoped>
/* 样式已移至全局样式文件 */
</style>