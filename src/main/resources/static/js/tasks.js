/**
 * tasks.js — Handles all task CRUD operations and rendering
 * Author: Satyajeet Sharma
 */

import { API } from './config.js';
import { getToken, clearAuth } from './auth.js';

let allTasks      = [];
let currentFilter = 'ALL';

// ─── LOAD ───────────────────────────────────────────
export async function loadTasks() {
  try {
    const res = await fetch(`${API}/api/tasks`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (res.status === 401) { clearAuth(); location.reload(); return; }
    const data = await res.json();
    allTasks = data.data || [];
    renderTasks();
    updateStats();
  } catch {
    document.getElementById('task-grid').innerHTML =
      '<div class="loading">Could not load tasks. Is the backend running?</div>';
  }
}

// ─── RENDER ─────────────────────────────────────────
export function renderTasks() {
  const grid  = document.getElementById('task-grid');
  const tasks = currentFilter === 'ALL'
    ? allTasks
    : allTasks.filter(t => t.status === currentFilter);

  if (tasks.length === 0) {
    grid.innerHTML = `
      <div class="empty-state">
        <div class="icon">📋</div>
        <p>No tasks here. Add one!</p>
      </div>`;
    return;
  }

  grid.innerHTML = tasks.map(task => `
    <div class="task-card" id="card-${task.id}">
      <div class="task-header">
        <div class="task-title">${escHtml(task.title)}</div>
        <span class="priority-badge priority-${task.priority}">${task.priority}</span>
      </div>
      ${task.description ? `<div class="task-desc">${escHtml(task.description)}</div>` : ''}
      <div class="task-footer">
        <select class="status-select" onchange="window.updateStatus(${task.id}, this.value)">
          <option value="PENDING"     ${task.status === 'PENDING'     ? 'selected' : ''}>⏳ Pending</option>
          <option value="IN_PROGRESS" ${task.status === 'IN_PROGRESS' ? 'selected' : ''}>🔄 In Progress</option>
          <option value="COMPLETED"   ${task.status === 'COMPLETED'   ? 'selected' : ''}>✅ Completed</option>
        </select>
        <button class="btn-delete" onclick="window.deleteTask(${task.id})">🗑 Delete</button>
      </div>
    </div>`).join('');
}

// ─── STATS ──────────────────────────────────────────
export function updateStats() {
  document.getElementById('stat-total').textContent    = allTasks.length;
  document.getElementById('stat-pending').textContent  = allTasks.filter(t => t.status === 'PENDING').length;
  document.getElementById('stat-progress').textContent = allTasks.filter(t => t.status === 'IN_PROGRESS').length;
  document.getElementById('stat-done').textContent     = allTasks.filter(t => t.status === 'COMPLETED').length;
}

// ─── FILTER ─────────────────────────────────────────
export function filterTasks(status, btn) {
  currentFilter = status;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  renderTasks();
}

// ─── CREATE ─────────────────────────────────────────
export async function createTask() {
  const title       = document.getElementById('task-title').value.trim();
  const description = document.getElementById('task-desc').value.trim();
  const priority    = document.getElementById('task-priority').value;
  if (!title) return alert('Title is required');
  try {
    const res  = await fetch(`${API}/api/tasks`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description, priority })
    });
    const data = await res.json();
    if (data.success) { closeModal(); loadTasks(); }
    else alert(data.message);
  } catch { alert('Failed to create task'); }
}

// ─── UPDATE STATUS ──────────────────────────────────
export async function updateStatus(id, status) {
  try {
    await fetch(`${API}/api/tasks/${id}/status`, {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${getToken()}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({ status })
    });
    const task = allTasks.find(t => t.id === id);
    if (task) task.status = status;
    updateStats();
    if (currentFilter !== 'ALL') renderTasks();
  } catch { console.error('Status update failed'); }
}

// ─── DELETE ─────────────────────────────────────────
export async function deleteTask(id) {
  if (!confirm('Delete this task?')) return;
  try {
    await fetch(`${API}/api/tasks/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    allTasks = allTasks.filter(t => t.id !== id);
    renderTasks();
    updateStats();
  } catch { alert('Failed to delete task'); }
}

// ─── MODAL ──────────────────────────────────────────
export function openModal() {
  document.getElementById('task-title').value    = '';
  document.getElementById('task-desc').value     = '';
  document.getElementById('task-priority').value = 'MEDIUM';
  document.getElementById('modal').classList.add('open');
}

export function closeModal() {
  document.getElementById('modal').classList.remove('open');
}

// ─── UTIL ───────────────────────────────────────────
function escHtml(str) {
  return str
    .replace(/&/g,  '&amp;')
    .replace(/</g,  '&lt;')
    .replace(/>/g,  '&gt;')
    .replace(/"/g,  '&quot;');
}
