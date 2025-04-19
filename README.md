# Servant Lite - Android WebSocket 客户端

[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)

轻量级 Android WebSocket 客户端，支持后台消息监听、分级通知推送和持久化连接。

## ✨ 功能特性

- 🛰️ WebSocket 长连接管理（连接/断开）
- 🔔 智能通知分类（紧急/心跳/普通消息）
- 🔋 电池优化豁免请求（提升后台存活）
- 🛡️ 前台服务保活（API 21+ 兼容）
- 📡 OkHttp 实现的稳定 WebSocket 连接
- 📱 Material Design 3 界面

## 📸 技术栈

- 语言: Kotlin
- 网络: OkHttp 4.11.0
- 异步: Kotlin Coroutines
- 最低 SDK: 21 (Android 5.0)
- 架构: MVC (带前台服务扩展)

## 🚀 快速开始

### 前提条件
- Android Studio Flamingo 2022.2.1+
- 可访问的 WebSocket 服务器（示例：`ws://192.168.137.1:8765`）

### 安装步骤
1. 克隆仓库：
   ```bash
   git clone https://github.com/fengyec2/SeewoServantApp.git
   ```
2. 修改服务器配置：
   ```xml
   <!-- res/values/strings.xml -->
   <string name="default_ip">192.168.137.1</string>
   <string name="default_port">8765</string>
   ```
3. 构建并运行应用

## 📖 使用指南

1. **界面操作**：
   - 输入服务器 IP 和端口
   - 点击「保存并连接」建立 WebSocket 连接
   - 点击「断开连接」终止通信

2. **通知类型**：
   - `[ALERT]`：红色高亮通知（振动+呼吸灯）
   - `[HEARTBEAT]`：静默心跳通知
   - 普通消息：蓝色默认通知

3. **后台模式**：
   - 连接后自动启动前台服务
   - 系统状态栏显示持久化通知
   - 首次运行会自动请求电池优化豁免

## 🔐 权限说明

| 权限 | 用途 |
|------|------|
| `INTERNET` | WebSocket 通信 |
| `FOREGROUND_SERVICE` | 前台服务保活 |
| `POST_NOTIFICATIONS` | 消息通知展示 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 提升后台存活率 |

## 💻 代码结构

```
src/
├── main/
│   ├── java/com/luminary/servantlite/
│   │   ├── MainActivity.kt       # 主界面逻辑
│   │   ├── WebSocketClient.kt   # WebSocket 封装
│   │   ├── ForegroundService.kt # 前台服务实现
│   │   └── NotificationHelper.kt# 通知管理类
│   ├── res/                     # 资源文件
│   └── AndroidManifest.xml      # 权限声明
```

## ⚙️ 开发依赖

```gradle
dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.core:core-ktx:1.12.0")
}
```

## ⚖️ 许可证

本项目采用 **GNU General Public License v3.0** 开源协议，完整条款参见 [LICENSE](LICENSE) 文件。