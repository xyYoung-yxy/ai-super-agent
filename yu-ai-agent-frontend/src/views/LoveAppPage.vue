<template>
  <div class="chat-container love-app">
    <div class="back-home">
      <router-link to="/" class="back-home-btn">
        🏠 回到首页
      </router-link>
    </div>
    <h1>AI 恋爱大师</h1>
    <div ref="chatMessagesRef" class="chat-messages">
      <div v-for="(message, index) in messages" :key="index" :class="['message-wrapper', message.role]">
          <div v-if="message.role === 'ai'" class="avatar ai-love">
            💖
          </div>
          <div :class="['message', message.role]">
            <div class="message-content">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>
      <!-- AI正在思考提示 -->
      <div v-if="isThinking" class="message-wrapper ai thinking-wrapper">
        <div class="avatar ai-love">💖</div>
        <div class="thinking-bubble">
          <span class="thinking-text">AI正在思考中</span>
          <span class="thinking-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </span>
        </div>
      </div>
    </div>
    <div class="chat-input">
      <input type="text" v-model="inputMessage" @keyup.enter="sendMessage" placeholder="请输入消息..." />
      <button v-if="!isReceiving" @click="sendMessage">发送</button>
      <button v-else class="stop-btn" @click="stopAIReply">
        <span class="stop-icon">■</span>
        停止
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import axios from 'axios'

const messages = ref([])
const inputMessage = ref('')
const chatId = ref('')
const isThinking = ref(false)
const isReceiving = ref(false)
const chatMessagesRef = ref(null)
let eventSource = null

// 自动滚动到最新消息
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动
watch(messages, () => {
  scrollToBottom()
}, { deep: true })

// 监听思考状态变化，自动滚动
watch(isThinking, () => {
  scrollToBottom()
})

const generateChatId = () => {
  return 'love_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: inputMessage.value,
    time: new Date().toLocaleTimeString()
  })

  const message = inputMessage.value
  inputMessage.value = ''
  
  // 显示AI正在思考
  isThinking.value = true
  isReceiving.value = true

  // 调用SSE接口
  if (eventSource) {
    eventSource.close()
  }

  eventSource = new EventSource(`/api/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${chatId.value}`)

  eventSource.onmessage = (event) => {
    if (event.data === '[DONE]') {
      eventSource.close()
      isThinking.value = false
      isReceiving.value = false
    } else {
      // 首次收到AI回复时，关闭思考状态
      if (isThinking.value) {
        isThinking.value = false
      }
      // 检查是否已有AI消息，如果有则追加，没有则创建新消息
      const lastMessage = messages.value[messages.value.length - 1]
      if (lastMessage && lastMessage.role === 'ai') {
        lastMessage.content += event.data
      } else {
        messages.value.push({
          role: 'ai',
          content: event.data,
          time: new Date().toLocaleTimeString()
        })
      }
    }
  }

  eventSource.onerror = (error) => {
    console.error('SSE Error:', error)
    eventSource.close()
    isThinking.value = false
    isReceiving.value = false
  }
}

// 停止AI回复
const stopAIReply = async () => {
  try {
    await axios.post(`/api/ai/stop?requestId=${chatId.value}`)
    if (eventSource) {
      eventSource.close()
    }
    isThinking.value = false
    isReceiving.value = false
  } catch (error) {
    console.error('停止AI回复失败:', error)
  }
}

onMounted(() => {
  // 生成聊天室ID
  chatId.value = generateChatId()
  
  // 初始欢迎消息
  messages.value.push({
    role: 'ai',
    content: '你好！我是AI恋爱大师，有什么可以帮助你的吗？',
    time: new Date().toLocaleTimeString()
  })
})

onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
  }
})
</script>

<style scoped>
/* 样式已移至全局样式文件 */
</style>
