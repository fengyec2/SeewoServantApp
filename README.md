# ServantLite

[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/GPL-3.0)

轻量级UDP消息监听安卓应用，支持自定义IP/端口配置和消息通知提醒

## ✨ 功能特性

- 📡 实时监听UDP数据包
- 🔔 含特定关键字（如"ALERT:"）的消息触发系统通知
- ⚙️ 可配置监听IP（默认0.0.0.0）和端口
- 🌙 后台持续运行（前台服务）
- 🔒 支持Android 5.0+ (API 21)

## 📸 应用截图

<!-- 建议添加实际截图 -->
![配置界面](screenshots/settings.png)
![通知示例](screenshots/notification.png)


## 🚀 快速开始

### 环境要求
- Android Studio Giraffe+
- JDK 17
- Android SDK 34

### 安装步骤
```bash
git clone https://github.com/fengyec2/ServantLite.git
cd ServantLite
# 使用Android Studio打开项目
```

## 📖 使用方法

1. 在主页输入监听IP（默认0.0.0.0）和端口（如8888）
2. 点击「保存设置并启动监听」
3. 使用网络工具发送测试消息：
   ```bash
   echo "ALERT: Server Down!" | nc -u 192.168.1.100 8888
   ```
4. 当收到含"ALERT:"的消息时，系统将显示通知

## ⚖️ 许可证

本项目采用 **GNU General Public License v3.0** 开源协议，完整文本请见 [LICENSE](LICENSE)

## 📞 联系方式

- 问题报告: [GitHub Issues](https://github.com/yourusername/ServantLite/issues)
- 开发者邮箱: your.email@example.com

---