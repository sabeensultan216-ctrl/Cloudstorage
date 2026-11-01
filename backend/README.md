# CloudStorage Backend API Server

A lightweight, powerful REST API backend for CloudStorage application supporting user authentication, file uploads, storage quota tracking, file categorization, folder management, and trash/recovery.

## Features

- 🔐 **Authentication**: User registration, login, and profile management
- 📁 **File Uploads**: Multi-file upload support using Multer with categorization (Images, Documents, Videos, Audio, Others)
- 📊 **Storage Overview**: Real-time storage consumption analytics and breakdown
- ⭐ **File Management**: Star/favorite, rename, shareable links, recycle bin & permanent delete
- 📂 **Folders**: Organize files into virtual folders
- 🔄 **Automatic Backup API**: Device backup synchronization endpoint

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get profile of authenticated user

### Storage Overview
- `GET /api/storage/overview` - Storage usage breakdown by category and percentage

### Files
- `GET /api/files` - List files (filters: `category`, `search`, `starred`, `trash`, `shared`, `folderId`)
- `POST /api/files/upload` - Upload file(s) via `multipart/form-data`
- `GET /api/files/:id/download` - Download file
- `PATCH /api/files/:id/star` - Star or unstar a file
- `PATCH /api/files/:id/rename` - Rename a file
- `DELETE /api/files/:id` - Move file to recycle bin
- `PATCH /api/files/:id/restore` - Restore file from recycle bin
- `DELETE /api/files/:id/permanent` - Permanently delete file
- `POST /api/files/:id/share` - Generate share link

### Folders & Backup
- `GET /api/folders` - Get folders
- `POST /api/folders` - Create new folder
- `POST /api/backup/sync` - Synchronize device backup

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the server:
   ```bash
   npm start
   ```
   Server will run on `http://localhost:5000`.
