const API_BASE = 'http://localhost:5000/api';

// Fallback in-memory state if server is not yet running
let localFiles = [
  {
    id: 'f-1',
    name: 'Quarterly_Report_2026.pdf',
    category: 'documents',
    size: 2450000,
    isStarred: true,
    isShared: true,
    isTrash: false,
    updatedAt: new Date().toLocaleDateString()
  },
  {
    id: 'f-2',
    name: 'Architecture_Diagram.png',
    category: 'images',
    size: 4200000,
    isStarred: false,
    isShared: false,
    isTrash: false,
    updatedAt: new Date().toLocaleDateString()
  },
  {
    id: 'f-3',
    name: 'Intro_Product_Video.mp4',
    category: 'videos',
    size: 38900000,
    isStarred: true,
    isShared: false,
    isTrash: false,
    updatedAt: new Date().toLocaleDateString()
  }
];

let currentTab = 'dashboard';
let currentCategory = 'all';
let searchQuery = '';

// DOM Elements
const filesListEl = document.getElementById('files-list');
const searchInput = document.getElementById('searchInput');
const uploadModal = document.getElementById('uploadModal');
const upgradeModal = document.getElementById('upgradeModal');
const dropzone = document.getElementById('dropzone');
const fileInput = document.getElementById('fileInput');
const btnOpenUpload = document.getElementById('btn-open-upload');
const btnCloseUpload = document.getElementById('close-upload-modal');
const btnCancelUpload = document.getElementById('cancel-upload');
const btnConfirmUpload = document.getElementById('confirm-upload');
const selectedFilesList = document.getElementById('selected-files-list');
const btnUpgrade = document.getElementById('btn-upgrade');
const btnCloseUpgrade = document.getElementById('close-upgrade-modal');

let pendingFiles = [];

// Initialize
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  setupUploadModal();
  setupPricingModal();
  setupSearch();
  loadStorageOverview();
  loadFiles();
});

// Setup Navigation Tabs
function setupNavigation() {
  document.querySelectorAll('.sidebar-nav .nav-item').forEach((item) => {
    item.addEventListener('click', () => {
      document.querySelectorAll('.sidebar-nav .nav-item').forEach((el) => el.classList.remove('active'));
      item.classList.add('active');
      currentTab = item.dataset.tab;
      currentCategory = 'all';
      loadFiles();
    });
  });

  document.querySelectorAll('.category-card').forEach((card) => {
    card.addEventListener('click', () => {
      currentCategory = card.dataset.category;
      loadFiles();
    });
  });
}

// Search Filter
function setupSearch() {
  searchInput.addEventListener('input', (e) => {
    searchQuery = e.target.value.toLowerCase().trim();
    renderFiles();
  });
}

// Fetch Files
async function loadFiles() {
  try {
    let url = `${API_BASE}/files?`;
    if (currentTab === 'trash') url += 'trash=true&';
    if (currentTab === 'starred') url += 'starred=true&';
    if (currentTab === 'shared') url += 'shared=true&';
    if (currentCategory !== 'all') url += `category=${currentCategory}&`;

    const res = await fetch(url);
    if (res.ok) {
      const data = await res.json();
      localFiles = data.files;
    }
  } catch (err) {
    console.log('Using local client state (backend offline)');
  }
  renderFiles();
}

// Render Files to DOM
function renderFiles() {
  filesListEl.innerHTML = '';

  let filtered = localFiles.filter((f) => {
    if (currentTab === 'trash') return f.isTrash;
    if (f.isTrash) return false;
    if (currentTab === 'starred' && !f.isStarred) return false;
    if (currentTab === 'shared' && !f.isShared) return false;
    if (currentCategory !== 'all' && f.category !== currentCategory) return false;
    if (searchQuery && !f.name.toLowerCase().includes(searchQuery)) return false;
    return true;
  });

  // Update category counts on dashboard
  updateCategoryCounts();

  if (filtered.length === 0) {
    filesListEl.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 48px; color: #64748B;">
        <i class="fa-solid fa-folder-open" style="font-size: 3rem; margin-bottom: 12px; opacity: 0.5;"></i>
        <p>No files found in this view.</p>
      </div>
    `;
    return;
  }

  filtered.forEach((file) => {
    const card = document.createElement('div');
    card.className = 'file-card';
    card.innerHTML = `
      <div class="file-preview">
        <i class="${getFileIcon(file.category, file.name)}"></i>
      </div>
      <div class="file-name" title="${file.name}">${file.name}</div>
      <div class="file-meta">
        <span>${formatBytes(file.size)}</span>
        <span>${file.category}</span>
      </div>
      <div class="file-actions">
        ${
          currentTab === 'trash'
            ? `
          <button title="Restore" onclick="restoreFile('${file.id}')"><i class="fa-solid fa-rotate-left"></i></button>
          <button title="Delete Permanently" onclick="deletePermanent('${file.id}')" style="color: #DC2626;"><i class="fa-solid fa-trash"></i></button>
        `
            : `
          <button class="${file.isStarred ? 'starred' : ''}" title="Star" onclick="toggleStar('${file.id}')">
            <i class="fa-${file.isStarred ? 'solid' : 'regular'} fa-star"></i>
          </button>
          <button title="Share" onclick="shareFile('${file.id}')"><i class="fa-solid fa-share-nodes"></i></button>
          <button title="Delete" onclick="deleteFile('${file.id}')"><i class="fa-solid fa-trash-can"></i></button>
        `
        }
      </div>
    `;
    filesListEl.appendChild(card);
  });
}

function getFileIcon(category, filename) {
  if (category === 'images') return 'fa-solid fa-file-image';
  if (category === 'videos') return 'fa-solid fa-file-video';
  if (category === 'audio') return 'fa-solid fa-file-audio';
  if (filename.endsWith('.pdf')) return 'fa-solid fa-file-pdf';
  return 'fa-solid fa-file-lines';
}

function formatBytes(bytes) {
  if (!bytes) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
}

function updateCategoryCounts() {
  const images = localFiles.filter((f) => !f.isTrash && f.category === 'images').length;
  const docs = localFiles.filter((f) => !f.isTrash && f.category === 'documents').length;
  const vids = localFiles.filter((f) => !f.isTrash && f.category === 'videos').length;
  const aud = localFiles.filter((f) => !f.isTrash && f.category === 'audio').length;

  document.getElementById('cat-images-count').innerText = `${images} file${images === 1 ? '' : 's'}`;
  document.getElementById('cat-docs-count').innerText = `${docs} file${docs === 1 ? '' : 's'}`;
  document.getElementById('cat-videos-count').innerText = `${vids} file${vids === 1 ? '' : 's'}`;
  document.getElementById('cat-audio-count').innerText = `${aud} file${aud === 1 ? '' : 's'}`;
}

// Storage Overview
async function loadStorageOverview() {
  try {
    const res = await fetch(`${API_BASE}/storage/overview`);
    if (res.ok) {
      const data = await res.json();
      document.getElementById('storage-percentage').innerText = `${data.percentage}%`;
      document.getElementById('storage-bar').style.width = `${data.percentage}%`;
      document.getElementById('storage-text-details').innerText = `${(data.totalUsed / (1024 ** 3)).toFixed(1)} GB of 15 GB used`;
    }
  } catch (e) {
    // default already rendered in HTML
  }
}

// File Actions
window.toggleStar = async function (id) {
  const file = localFiles.find((f) => f.id === id);
  if (file) {
    file.isStarred = !file.isStarred;
    renderFiles();
    try {
      await fetch(`${API_BASE}/files/${id}/star`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ isStarred: file.isStarred })
      });
    } catch (e) {}
  }
};

window.deleteFile = async function (id) {
  const file = localFiles.find((f) => f.id === id);
  if (file) {
    file.isTrash = true;
    renderFiles();
    try {
      await fetch(`${API_BASE}/files/${id}`, { method: 'DELETE' });
    } catch (e) {}
  }
};

window.restoreFile = async function (id) {
  const file = localFiles.find((f) => f.id === id);
  if (file) {
    file.isTrash = false;
    renderFiles();
    try {
      await fetch(`${API_BASE}/files/${id}/restore`, { method: 'PATCH' });
    } catch (e) {}
  }
};

window.deletePermanent = async function (id) {
  localFiles = localFiles.filter((f) => f.id !== id);
  renderFiles();
  try {
    await fetch(`${API_BASE}/files/${id}/permanent`, { method: 'DELETE' });
  } catch (e) {}
};

window.shareFile = async function (id) {
  const file = localFiles.find((f) => f.id === id);
  if (file) {
    file.isShared = true;
    alert(`Share link generated for: ${file.name}\nLink: http://localhost:5000/share/${file.id}`);
    renderFiles();
    try {
      await fetch(`${API_BASE}/files/${id}/share`, { method: 'POST' });
    } catch (e) {}
  }
};

// Upload Modal & Logic
function setupUploadModal() {
  btnOpenUpload.addEventListener('click', () => uploadModal.classList.add('active'));
  btnCloseUpload.addEventListener('click', () => uploadModal.classList.remove('active'));
  btnCancelUpload.addEventListener('click', () => uploadModal.classList.remove('active'));

  dropzone.addEventListener('click', () => fileInput.click());

  fileInput.addEventListener('change', (e) => {
    handleSelectedFiles(Array.from(e.target.files));
  });

  dropzone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropzone.style.borderColor = '#2563EB';
  });

  dropzone.addEventListener('dragleave', () => {
    dropzone.style.borderColor = '#CBD5E1';
  });

  dropzone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropzone.style.borderColor = '#CBD5E1';
    if (e.dataTransfer.files) {
      handleSelectedFiles(Array.from(e.dataTransfer.files));
    }
  });

  btnConfirmUpload.addEventListener('click', async () => {
    if (pendingFiles.length === 0) return;

    btnConfirmUpload.innerText = 'Uploading...';
    btnConfirmUpload.disabled = true;

    try {
      const formData = new FormData();
      pendingFiles.forEach((f) => formData.append('files', f));

      const res = await fetch(`${API_BASE}/files/upload`, {
        method: 'POST',
        body: formData
      });

      if (res.ok) {
        const data = await res.json();
        data.files.forEach((f) => localFiles.unshift(f));
      } else {
        throw new Error('Upload failed');
      }
    } catch (e) {
      // Simulate successful local upload if server is offline
      pendingFiles.forEach((f) => {
        localFiles.unshift({
          id: 'up-' + Date.now() + Math.random(),
          name: f.name,
          category: f.type.startsWith('image/')
            ? 'images'
            : f.type.startsWith('video/')
            ? 'videos'
            : 'documents',
          size: f.size,
          isStarred: false,
          isTrash: false,
          isShared: false,
          updatedAt: new Date().toLocaleDateString()
        });
      });
    }

    pendingFiles = [];
    selectedFilesList.innerHTML = '';
    btnConfirmUpload.innerText = 'Upload';
    uploadModal.classList.remove('active');
    renderFiles();
  });
}

function handleSelectedFiles(files) {
  pendingFiles = files;
  selectedFilesList.innerHTML = '';

  if (files.length > 0) {
    btnConfirmUpload.disabled = false;
    files.forEach((file) => {
      const item = document.createElement('div');
      item.style.padding = '8px';
      item.style.fontSize = '0.85rem';
      item.style.display = 'flex';
      item.style.justifyContent = 'space-between';
      item.innerHTML = `<span>${file.name}</span> <span style="color:#64748B;">${formatBytes(file.size)}</span>`;
      selectedFilesList.appendChild(item);
    });
  } else {
    btnConfirmUpload.disabled = true;
  }
}

// Pricing Modal Logic
function setupPricingModal() {
  btnUpgrade.addEventListener('click', () => upgradeModal.classList.add('active'));
  btnCloseUpgrade.addEventListener('click', () => upgradeModal.classList.remove('active'));
}
