const express = require('express');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const { v4: uuidv4 } = require('uuid');

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Storage directory setup
const UPLOAD_DIR = path.join(__dirname, 'uploads');
const DATA_FILE = path.join(__dirname, 'data.json');

if (!fs.existsSync(UPLOAD_DIR)) {
  fs.mkdirSync(UPLOAD_DIR, { recursive: true });
}

// Simple JSON persistence helper
function loadData() {
  if (!fs.existsSync(DATA_FILE)) {
    const initialData = {
      users: [
        {
          id: 'user-demo-1',
          name: 'Demo User',
          email: 'user@cloudstorage.com',
          password: 'password123',
          storageUsed: 2684354560, // ~2.5 GB
          storageLimit: 16106127360 // 15 GB
        }
      ],
      files: [
        {
          id: 'file-demo-1',
          userId: 'user-demo-1',
          name: 'Project_Presentation.pdf',
          category: 'documents',
          size: 4520000,
          mimeType: 'application/pdf',
          path: '',
          isStarred: true,
          isTrash: false,
          isShared: true,
          createdAt: new Date(Date.now() - 86400000 * 2).toISOString(),
          updatedAt: new Date(Date.now() - 86400000 * 2).toISOString()
        },
        {
          id: 'file-demo-2',
          userId: 'user-demo-1',
          name: 'Vacation_Photo.png',
          category: 'images',
          size: 2840000,
          mimeType: 'image/png',
          path: '',
          isStarred: false,
          isTrash: false,
          isShared: false,
          createdAt: new Date(Date.now() - 86400000 * 5).toISOString(),
          updatedAt: new Date(Date.now() - 86400000 * 5).toISOString()
        },
        {
          id: 'file-demo-3',
          userId: 'user-demo-1',
          name: 'Product_Demo.mp4',
          category: 'videos',
          size: 45200000,
          mimeType: 'video/mp4',
          path: '',
          isStarred: true,
          isTrash: false,
          isShared: false,
          createdAt: new Date(Date.now() - 86400000 * 8).toISOString(),
          updatedAt: new Date(Date.now() - 86400000 * 8).toISOString()
        }
      ],
      folders: [
        { id: 'folder-1', userId: 'user-demo-1', name: 'Work Documents', createdAt: new Date().toISOString() },
        { id: 'folder-2', userId: 'user-demo-1', name: 'Personal Photos', createdAt: new Date().toISOString() }
      ]
    };
    fs.writeFileSync(DATA_FILE, JSON.stringify(initialData, null, 2));
    return initialData;
  }
  return JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
}

function saveData(data) {
  fs.writeFileSync(DATA_FILE, JSON.stringify(data, null, 2));
}

// Multer Storage Configuration
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, UPLOAD_DIR);
  },
  filename: (req, file, cb) => {
    const ext = path.extname(file.originalname);
    const safeName = `${Date.now()}-${uuidv4()}${ext}`;
    cb(null, safeName);
  }
});
const upload = multer({
  storage,
  limits: { fileSize: 100 * 1024 * 1024 } // 100MB
});

// Category helper
function getCategory(mimeType, filename) {
  if (mimeType.startsWith('image/')) return 'images';
  if (mimeType.startsWith('video/')) return 'videos';
  if (mimeType.startsWith('audio/')) return 'audio';
  if (
    mimeType.includes('pdf') ||
    mimeType.includes('word') ||
    mimeType.includes('text') ||
    mimeType.includes('presentation') ||
    mimeType.includes('spreadsheet') ||
    filename.endsWith('.doc') ||
    filename.endsWith('.docx') ||
    filename.endsWith('.txt') ||
    filename.endsWith('.pdf') ||
    filename.endsWith('.ppt') ||
    filename.endsWith('.pptx') ||
    filename.endsWith('.xls') ||
    filename.endsWith('.xlsx')
  ) {
    return 'documents';
  }
  return 'others';
}

// ========================
// Authentication Routes
// ========================
app.post('/api/auth/register', (req, res) => {
  const { name, email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  const data = loadData();
  const existing = data.users.find((u) => u.email.toLowerCase() === email.toLowerCase());
  if (existing) {
    return res.status(409).json({ error: 'User with this email already exists' });
  }

  const newUser = {
    id: uuidv4(),
    name: name || email.split('@')[0],
    email: email.toLowerCase(),
    password, // For production use bcrypt hash
    storageUsed: 0,
    storageLimit: 16106127360 // 15 GB default
  };

  data.users.push(newUser);
  saveData(data);

  res.status(201).json({
    message: 'User registered successfully',
    user: { id: newUser.id, name: newUser.name, email: newUser.email, storageLimit: newUser.storageLimit },
    token: `token_${newUser.id}`
  });
});

app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  const data = loadData();
  const user = data.users.find(
    (u) => u.email.toLowerCase() === email.toLowerCase() && u.password === password
  );

  if (!user) {
    return res.status(401).json({ error: 'Invalid email or password' });
  }

  res.json({
    message: 'Login successful',
    user: {
      id: user.id,
      name: user.name,
      email: user.email,
      storageUsed: user.storageUsed,
      storageLimit: user.storageLimit
    },
    token: `token_${user.id}`
  });
});

app.get('/api/auth/me', (req, res) => {
  const data = loadData();
  const user = data.users[0]; // Returns current active/demo user
  if (!user) return res.status(404).json({ error: 'User not found' });
  res.json({
    id: user.id,
    name: user.name,
    email: user.email,
    storageUsed: user.storageUsed,
    storageLimit: user.storageLimit
  });
});

// ========================
// Storage Overview Route
// ========================
app.get('/api/storage/overview', (req, res) => {
  const data = loadData();
  const activeFiles = data.files.filter((f) => !f.isTrash);

  let imagesSize = 0;
  let docsSize = 0;
  let videoSize = 0;
  let audioSize = 0;
  let othersSize = 0;

  activeFiles.forEach((file) => {
    const s = file.size || 0;
    if (file.category === 'images') imagesSize += s;
    else if (file.category === 'documents') docsSize += s;
    else if (file.category === 'videos') videoSize += s;
    else if (file.category === 'audio') audioSize += s;
    else othersSize += s;
  });

  const totalUsed = imagesSize + docsSize + videoSize + audioSize + othersSize;
  const totalLimit = 16106127360; // 15 GB

  res.json({
    totalLimit,
    totalUsed,
    percentage: Math.min(100, Math.round((totalUsed / totalLimit) * 100)),
    breakdown: {
      images: imagesSize,
      documents: docsSize,
      videos: videoSize,
      audio: audioSize,
      others: othersSize
    },
    fileCount: activeFiles.length,
    trashCount: data.files.filter((f) => f.isTrash).length
  });
});

// ========================
// File Routes
// ========================
app.get('/api/files', (req, res) => {
  const { category, search, starred, trash, shared, folderId } = req.query;
  const data = loadData();
  let files = data.files;

  if (trash === 'true') {
    files = files.filter((f) => f.isTrash);
  } else {
    files = files.filter((f) => !f.isTrash);
  }

  if (starred === 'true') {
    files = files.filter((f) => f.isStarred);
  }

  if (shared === 'true') {
    files = files.filter((f) => f.isShared);
  }

  if (category && category !== 'all') {
    files = files.filter((f) => f.category === category);
  }

  if (folderId) {
    files = files.filter((f) => f.folderId === folderId);
  }

  if (search) {
    const query = search.toLowerCase();
    files = files.filter((f) => f.name.toLowerCase().includes(query));
  }

  res.json({
    count: files.length,
    files
  });
});

app.post('/api/files/upload', upload.array('files', 10), (req, res) => {
  if (!req.files || req.files.length === 0) {
    return res.status(400).json({ error: 'No files provided' });
  }

  const data = loadData();
  const folderId = req.body.folderId || null;
  const uploadedRecords = [];

  req.files.forEach((file) => {
    const newRecord = {
      id: uuidv4(),
      userId: req.body.userId || 'user-demo-1',
      folderId,
      name: file.originalname,
      serverFilename: file.filename,
      size: file.size,
      mimeType: file.mimetype,
      category: getCategory(file.mimetype, file.originalname),
      path: file.path,
      isStarred: false,
      isTrash: false,
      isShared: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    data.files.unshift(newRecord);
    uploadedRecords.push(newRecord);
  });

  saveData(data);
  res.status(201).json({
    message: `${uploadedRecords.length} file(s) uploaded successfully`,
    files: uploadedRecords
  });
});

app.get('/api/files/:id/download', (req, res) => {
  const data = loadData();
  const file = data.files.find((f) => f.id === req.params.id);

  if (!file) {
    return res.status(404).json({ error: 'File not found' });
  }

  if (!file.path || !fs.existsSync(file.path)) {
    // If it is demo seed data without disk file
    return res.json({
      message: 'Demo file record',
      file
    });
  }

  res.download(file.path, file.name);
});

app.patch('/api/files/:id/star', (req, res) => {
  const data = loadData();
  const file = data.files.find((f) => f.id === req.params.id);

  if (!file) {
    return res.status(404).json({ error: 'File not found' });
  }

  file.isStarred = req.body.isStarred !== undefined ? req.body.isStarred : !file.isStarred;
  file.updatedAt = new Date().toISOString();
  saveData(data);

  res.json({ message: 'File star status updated', file });
});

app.patch('/api/files/:id/rename', (req, res) => {
  const { name } = req.body;
  if (!name) return res.status(400).json({ error: 'New name is required' });

  const data = loadData();
  const file = data.files.find((f) => f.id === req.params.id);

  if (!file) return res.status(404).json({ error: 'File not found' });

  file.name = name;
  file.category = getCategory(file.mimeType, name);
  file.updatedAt = new Date().toISOString();
  saveData(data);

  res.json({ message: 'File renamed successfully', file });
});

app.delete('/api/files/:id', (req, res) => {
  const data = loadData();
  const file = data.files.find((f) => f.id === req.params.id);

  if (!file) {
    return res.status(404).json({ error: 'File not found' });
  }

  file.isTrash = true;
  file.deletedAt = new Date().toISOString();
  saveData(data);

  res.json({ message: 'File moved to recycle bin', file });
});

app.patch('/api/files/:id/restore', (req, res) => {
  const data = loadData();
  const file = data.files.find((f) => f.id === req.params.id);

  if (!file) {
    return res.status(404).json({ error: 'File not found' });
  }

  file.isTrash = false;
  delete file.deletedAt;
  saveData(data);

  res.json({ message: 'File restored from recycle bin', file });
});

app.delete('/api/files/:id/permanent', (req, res) => {
  const data = loadData();
  const index = data.files.findIndex((f) => f.id === req.params.id);

  if (index === -1) {
    return res.status(404).json({ error: 'File not found' });
  }

  const [deletedFile] = data.files.splice(index, 1);
  if (deletedFile.path && fs.existsSync(deletedFile.path)) {
    try {
      fs.unlinkSync(deletedFile.path);
    } catch (e) {
      console.error('Error removing file from disk', e);
    }
  }

  saveData(data);
  res.json({ message: 'File permanently deleted', id: req.params.id });
});

app.post('/api/files/:id/share', (req, res) => {
  const data = loadData();
  const file = data.files.find((f) => f.id === req.params.id);

  if (!file) return res.status(404).json({ error: 'File not found' });

  file.isShared = true;
  const shareLink = `http://localhost:${PORT}/share/${file.id}`;
  saveData(data);

  res.json({ message: 'Share link created', shareLink, file });
});

// ========================
// Folder Routes
// ========================
app.get('/api/folders', (req, res) => {
  const data = loadData();
  res.json({ folders: data.folders });
});

app.post('/api/folders', (req, res) => {
  const { name } = req.body;
  if (!name) return res.status(400).json({ error: 'Folder name is required' });

  const data = loadData();
  const folder = {
    id: uuidv4(),
    userId: 'user-demo-1',
    name,
    createdAt: new Date().toISOString()
  };

  data.folders.push(folder);
  saveData(data);

  res.status(201).json({ message: 'Folder created', folder });
});

// ========================
// Automatic Backup Route
// ========================
app.post('/api/backup/sync', (req, res) => {
  const { items, deviceName } = req.body;
  res.json({
    message: 'Backup synchronized successfully',
    syncedAt: new Date().toISOString(),
    device: deviceName || 'Android Device',
    itemsCount: Array.isArray(items) ? items.length : 0
  });
});

// Root & Health Check
app.get('/api/health', (req, res) => {
  res.json({ status: 'healthy', timestamp: new Date().toISOString() });
});

app.listen(PORT, () => {
  console.log(`CloudStorage backend server running on http://localhost:${PORT}`);
});
