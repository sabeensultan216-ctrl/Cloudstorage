# CloudStorage ☁️

A cross-platform Cloud Storage solution featuring:
- **Mobile Client**: Native Android application built with Jetpack Compose & Material 3.
- **Web Client**: Responsive web dashboard with drag-and-drop upload, categorization, and storage management.
- **Backend API**: REST API service for file upload, authentication, storage metrics, and trash management.

---

## 📁 Repository Structure

```
Cloudstorage/
├── app/                  # Native Android application (Jetpack Compose)
│   ├── src/main/java/    # Kotlin UI screens, navigation, and network client
│   └── src/main/res/     # Resources, themes, and multilingual strings
├── frontend/             # Responsive Web Frontend Client
│   ├── index.html        # Web dashboard interface
│   ├── styles.css        # Stylesheet & modern UI theme
│   ├── app.js            # Frontend logic & API integration
│   └── README.md
├── backend/              # REST API & File Storage Server
│   ├── server.js         # Express server with file upload & auth
│   ├── package.json      # Dependencies and scripts
│   ├── .env.example      # Environment variable template
│   └── README.md
├── gradle/               # Gradle wrapper configuration
├── build.gradle.kts      # Android root build file
└── README.md             # Project documentation
```

---

## ✨ Features

- **Multi-Category Storage**: Organize files into Images, Documents, Videos, Audio, and Others.
- **Real-Time Storage Meter**: Visual indicator of total used vs. available storage (15 GB free tier).
- **File Actions**: Preview, Star/Favorite, Download, Share links, and Rename.
- **Recycle Bin**: Move files to trash, restore files, or delete permanently.
- **Automatic Backup**: Device file backup and sync API.
- **Multilingual Support**: Supports English, Arabic, Urdu, Hindi, German, Spanish, French, Chinese.

---

## 🚀 Quick Start Guide

### 1. Backend Server
```bash
cd backend
npm install
npm start
```
*API Server runs at `http://localhost:5000`.*

### 2. Web Frontend
Open `frontend/index.html` in any browser, or run:
```bash
# Using Python
python -m http.server 3000 --directory frontend

# Or using npx
npx serve frontend
```

### 3. Android Mobile App
Open this project in **Android Studio** and run it on an Android Emulator or connected physical device.
*(Note: To connect to local backend from emulator, use `http://10.0.2.2:5000/api`)*

---

## 🔒 License
MIT License
